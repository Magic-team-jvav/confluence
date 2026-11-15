/**
 * GeckoLib Multi-Particle Keyframes
 * ---------------------------------------------------------------------------
 * Blockbench plugin for the "GeckoLib Animated Model" format.
 *
 * 让 GeckoLib 动画编辑器可以像基岩版实体动画那样，在“一个关键帧”上放置多个粒子效果
 * （以及多个音效），而不是一个关键帧只能有一个效果。
 *
 * 原理 / How it works
 *  - Blockbench 核心本身支持 effect 通道（particle / sound）的关键帧拥有多个 data point
 *    （`EffectAnimator.channels.particle.max_data_points = 1000`，
 *     `Keyframe#compileBedrockKeyframe()` 在 data_points > 1 时返回数组，否则返回单个对象）。
 *  - 但 GeckoLib 官方插件在每一帧都会隐藏关键帧面板里的 “+” (add data point) 按钮，
 *    于是编辑器里无法为一个关键帧添加第二个粒子效果。本插件只在“效果通道”的关键帧上
 *    恢复该按钮，不动骨骼（rotation/position/scale）关键帧的行为。
 *  - 导出时固定为：多效果关键帧写成数组，单效果关键帧保持原来的对象写法（不做任何时间偏移）。
 *    数组写法需要运行时支持 —— 见 ConfluenceOtherworld 的
 *    `integration.geckolib.KeyFramesAdapterMixin`。
 *
 * @author  DSH
 * @version 1.0.0
 */

(function () {
	'use strict';

	const PLUGIN_ID = 'geckolib_multi_particle';
	const GECKOLIB_FORMAT_ID = 'geckolib_model';
	/** 采用“一个关键帧可以有多个效果”的动画通道 */
	const EFFECT_CHANNELS = ['particle', 'sound'];
	/** 通道 -> 动画 JSON 中的字段名 */
	const EFFECT_JSON_KEYS = { particle: 'particle_effects', sound: 'sound_effects' };
	const CATEGORY_ID = PLUGIN_ID;

	const state = {
		/** 被 patch 的动画编解码器（GeckoLib 复用 Bedrock 的 animation codec） */
		codec: null,
		originalCompileAnimation: null,
		patchedCompile: null,
		observer: null,
		observerTarget: null,
		toolbarItem: null,
		menuItem: null,
		/** 已注册的事件监听：[event, handler]，卸载时逐个移除 */
		listeners: [],
		notifiedArrayExport: false,
	};

	/** Setting 实例，键为短名 */
	const config = {};

	/* ====================================================================== *
	 * 小工具
	 * ====================================================================== */

	function isGeckoLibProject() {
		try {
			return typeof Format !== 'undefined' && !!Format && Format.id === GECKOLIB_FORMAT_ID;
		} catch (error) {
			return false;
		}
	}

	function setting(name, fallback) {
		const item = config[name];
		return item && item.value !== undefined ? item.value : fallback;
	}

	/** 插件是否应当生效（设置已启用 + 当前工程是 GeckoLib 格式） */
	function isActive() {
		return !!setting('enabled', true) && isGeckoLibProject();
	}

	/** 关键帧是否属于“效果”通道（粒子 / 音效），而不是骨骼变换通道 */
	function isEffectKeyframe(keyframe) {
		if (!keyframe || EFFECT_CHANNELS.indexOf(keyframe.channel) === -1) return false;
		const channels = keyframe.animator && keyframe.animator.channels;
		const channel = channels && channels[keyframe.channel];
		if (channel && channel.transform) return false;
		return true;
	}

	function canAddDataPoint(keyframe) {
		if (!isEffectKeyframe(keyframe)) return false;
		const channels = keyframe.animator && keyframe.animator.channels;
		const channel = channels && channels[keyframe.channel];
		const max = channel && channel.max_data_points ? channel.max_data_points : 1;
		return !keyframe.data_points || keyframe.data_points.length < max;
	}

	function selectedKeyframes() {
		try {
			return (typeof Timeline !== 'undefined' && Timeline.selected) ? Timeline.selected : [];
		} catch (error) {
			return [];
		}
	}

	function selectedEffectKeyframes() {
		return selectedKeyframes().filter(isEffectKeyframe);
	}

	function notify(message) {
		try {
			if (typeof Blockbench !== 'undefined' && Blockbench.showQuickMessage) {
				Blockbench.showQuickMessage(message);
				return;
			}
		} catch (error) { /* ignore */ }
		console.log('[GeckoLib Multi-Particle] ' + message);
	}

	/* ====================================================================== *
	 * 导出：多效果关键帧 -> 数组，单效果关键帧 -> 原来的对象写法
	 * ====================================================================== */

	/**
	 * 归一化一个 `xxx_effects` 对象（幂等）：
	 *
	 *   { "0.5": { effect, locator, pre_effect_script } }              只有一个效果 —— 原样保留
	 *   { "0.5": [ { effect, ... }, { effect, ... } ] }               多个效果 —— 数组
	 *
	 * Blockbench 核心的 `Keyframe#compileBedrockKeyframe()` 本来就是这么输出的
	 * （`points.length <= 1 ? points[0] : points`），这里只是再保证一次，
	 * 顺便把“是否真的写出了数组”告诉调用方。
	 *
	 * @returns {{effects: object, hasArray: boolean}}
	 */
	function normalizeEffectsObject(effects) {
		if (!effects || typeof effects !== 'object') return { effects, hasArray: false };

		const result = {};
		let hasArray = false;

		for (const timecode in effects) {
			if (!Object.prototype.hasOwnProperty.call(effects, timecode)) continue;
			const value = effects[timecode];

			if (Array.isArray(value)) {
				const points = value.filter(point => point !== undefined && point !== null && point !== '');
				if (points.length === 0) continue;
				if (points.length === 1) {
					result[timecode] = points[0];
				} else {
					result[timecode] = points;
					hasArray = true;
				}
			} else {
				result[timecode] = value;
			}
		}

		return { effects: result, hasArray };
	}

	/** 处理单个动画的编译结果（`animation_codec.compileAnimation()` 的返回值） */
	function transformCompiledAnimation(animationTag) {
		if (!animationTag || typeof animationTag !== 'object') return animationTag;

		let hasArray = false;

		for (const channel of EFFECT_CHANNELS) {
			const key = EFFECT_JSON_KEYS[channel];
			if (!animationTag[key]) continue;
			const result = normalizeEffectsObject(animationTag[key]);
			animationTag[key] = result.effects;
			hasArray = hasArray || result.hasArray;
		}

		// 数组写法需要运行时支持，只在真的写出数组时提示一次（只写日志，不打扰操作）
		if (hasArray && !state.notifiedArrayExport) {
			state.notifiedArrayExport = true;
			console.warn('[GeckoLib Multi-Particle] 已导出数组写法（同一关键帧多个效果）。'
				+ '读取该文件的 GeckoLib 运行时需要数组支持，例如本仓库的 '
				+ 'integration.geckolib.KeyFramesAdapterMixin。');
		}

		return animationTag;
	}

	/* ====================================================================== *
	 * 编辑器 UI：恢复“+”按钮 + 效果关键帧保持线性插值
	 * ====================================================================== */

	/**
	 * GeckoLib 插件在每一帧都执行 `#keyframe_type_label > div` 的 `hidden = true`，
	 * 也就是把核心的 “add data point” 按钮藏起来。这里只在“选中的关键帧全都是效果关键帧”
	 * 时把它恢复显示；骨骼关键帧（GeckoLib 的 pre/post 按钮）保持原样。
	 *
	 * @returns {boolean|null} 期望的可见性；null 表示不干预
	 */
	function desiredAddButtonVisibility() {
		if (!isActive()) return null;
		const selected = selectedKeyframes();
		if (!selected.length) return null;
		if (!selected.every(isEffectKeyframe)) return null;
		return selected.some(canAddDataPoint);
	}

	function fixAddDataPointButton() {
		const desired = desiredAddButtonVisibility();
		if (desired === null) return;

		const label = document.getElementById('keyframe_type_label');
		if (!label) return;
		const button = label.querySelector(':scope > .in_list_button');
		if (!button) return;
		if (button.hidden === !desired) return;

		button.hidden = !desired;
	}

	/**
	 * 效果关键帧在 GeckoLib 里不需要插值方式（编译时只取 effect/locator/script），
	 * 但 GeckoLib 插件会在 `update_keyframe_selection` 时把“data_points != 1 且
	 * interpolation != 'linear'”的关键帧的多余 data point 删掉，所以这里强制保持线性。
	 */
	function enforceLinearInterpolation() {
		if (!isActive() || !setting('force_linear', true)) return;
		let keyframes;
		try {
			keyframes = Timeline.keyframes;
		} catch (error) {
			return;
		}
		if (!keyframes) return;
		keyframes.forEach(keyframe => {
			if (!isEffectKeyframe(keyframe)) return;
			if (keyframe.interpolation !== 'linear' && keyframe.data_points && keyframe.data_points.length > 1) {
				keyframe.interpolation = 'linear';
			}
		});
	}

	function ensurePanelObserver() {
		const panel = document.getElementById('panel_keyframe');
		if (!panel || typeof MutationObserver === 'undefined') return;
		if (state.observer && state.observerTarget === panel) return;

		if (state.observer) state.observer.disconnect();
		state.observerTarget = panel;
		state.observer = new MutationObserver(() => {
			try {
				fixAddDataPointButton();
			} catch (error) { /* ignore */ }
		});
		state.observer.observe(panel, { attributes: true, attributeFilter: ['hidden'], childList: true, subtree: true });
	}

	/* ====================================================================== *
	 * 手动添加效果
	 * ====================================================================== */

	function addEffectToSelection() {
		const targets = selectedEffectKeyframes().filter(canAddDataPoint);
		if (!targets.length) {
			notify('请先在时间轴上选中至少一个“粒子/音效”关键帧。');
			return;
		}

		const DataPoint = (typeof KeyframeDataPoint !== 'undefined') ? KeyframeDataPoint : null;
		if (!DataPoint) return;

		Undo.initEdit({ keyframes: targets });
		targets.forEach(keyframe => {
			const point = new DataPoint(keyframe);
			if (keyframe.data_points && keyframe.data_points[0]) point.extend(keyframe.data_points[0]);
			keyframe.data_points.push(point);
		});
		try {
			Animator.preview();
		} catch (error) { /* ignore */ }
		Undo.finishEdit('Add particle effect');
		fixAddDataPointButton();
	}

	/* ====================================================================== *
	 * Patch 动画编解码器
	 * ====================================================================== */

	function resolveAnimationCodec() {
		try {
			if (typeof Codecs !== 'undefined' && Codecs.bedrock && Codecs.bedrock.format
				&& Codecs.bedrock.format.animation_codec) {
				return Codecs.bedrock.format.animation_codec;
			}
		} catch (error) { /* ignore */ }
		try {
			if (typeof AnimationCodec !== 'undefined' && AnimationCodec.codecs && AnimationCodec.codecs.bedrock) {
				return AnimationCodec.codecs.bedrock;
			}
		} catch (error) { /* ignore */ }
		return null;
	}

	/**
	 * 给动画编解码器打 patch（只包导出）。
	 *
	 * 每次调用都会检查当前的实现是不是我们自己的包装函数：GeckoLib 插件（或别的插件）
	 * 可能在之后重新应用 monkeypatch 把我们的包装覆盖掉，这时需要重新包装一层。
	 */
	function patchAnimationCodec() {
		const codec = resolveAnimationCodec();
		if (!codec) return;

		// 导出：compileAnimation / compileFile / saveAnimation / exportFile 都会经过这里
		if (typeof codec.compileAnimation === 'function' && codec.compileAnimation !== state.patchedCompile) {
			const previous = codec.compileAnimation;
			state.originalCompileAnimation = previous;
			state.patchedCompile = function () {
				const result = previous.apply(this, arguments);
				try {
					if (isActive()) transformCompiledAnimation(result);
				} catch (error) {
					console.error('[GeckoLib Multi-Particle] export transform failed', error);
				}
				return result;
			};
			codec.compileAnimation = state.patchedCompile;
		}

		state.codec = codec;
	}

	function unpatchAnimationCodec() {
		const codec = state.codec;
		if (codec && state.patchedCompile && codec.compileAnimation === state.patchedCompile
			&& state.originalCompileAnimation) {
			codec.compileAnimation = state.originalCompileAnimation;
		}
		state.codec = null;
		state.patchedCompile = null;
		state.originalCompileAnimation = null;
	}

	/* ====================================================================== *
	 * 插件本体
	 * ====================================================================== */

	Plugin.register(PLUGIN_ID, {
		title: 'GeckoLib Multi-Particle Keyframes',
		author: 'DSH',
		description: '在 GeckoLib 动画编辑器中，允许同一个关键帧包含多个粒子/音效效果（像基岩版实体动画那样）。'
			+ '导出时多效果关键帧写成数组，单效果关键帧保持原来的对象写法。',
		version: '1.0.0',
		variant: 'both',
		min_version: '5.0.0',

		onload() {
			// --- 设置项 -------------------------------------------------------
			try {
				Settings.addCategory(CATEGORY_ID, { name: 'GeckoLib 多粒子关键帧', open: false });
			} catch (error) { /* 分类已存在 */ }

			config.enabled = new Setting(PLUGIN_ID + '_enabled', {
				name: '启用多粒子关键帧',
				description: '允许一个粒子/音效关键帧包含多个效果条目（多个 data point）。',
				category: CATEGORY_ID,
				plugin: PLUGIN_ID,
				type: 'toggle',
				value: true,
			});

			config.force_linear = new Setting(PLUGIN_ID + '_force_linear', {
				name: '效果关键帧强制线性插值',
				description: '效果关键帧的插值方式对 GeckoLib 没有意义，但非线性的插值会让 GeckoLib 插件删掉多余的 data point，因此建议保持开启。',
				category: CATEGORY_ID,
				plugin: PLUGIN_ID,
				type: 'toggle',
				value: true,
			});

			// --- 编解码器 patch ----------------------------------------------
			patchAnimationCodec();

			// --- 事件 ---------------------------------------------------------
			const listen = (event, handler) => {
				Blockbench.on(event, handler);
				state.listeners.push([event, handler]);
			};

			listen('render_frame', () => {
				if (!isGeckoLibProject()) return;
				try {
					fixAddDataPointButton();
					enforceLinearInterpolation();
					ensurePanelObserver();
				} catch (error) {
					console.error('[GeckoLib Multi-Particle] frame update failed', error);
				}
			});

			listen('update_keyframe_selection', () => {
				try {
					fixAddDataPointButton();
				} catch (error) { /* ignore */ }
			});

			listen('select_project', () => {
				state.notifiedArrayExport = false;
				state.observerTarget = null;
				patchAnimationCodec();
			});

			// --- 面板按钮 / 右键菜单 ------------------------------------------
			const condition = () => isActive() && selectedEffectKeyframes().some(canAddDataPoint);

			try {
				state.toolbarItem = new Action(PLUGIN_ID + '_add_effect', {
					name: '添加粒子效果',
					description: '给选中的粒子/音效关键帧再加一个效果条目',
					icon: 'add_circle',
					category: 'animation',
					condition,
					click() {
						addEffectToSelection();
					},
				});
				const toolbar = (typeof Toolbars !== 'undefined' && Toolbars) ? Toolbars.keyframe : null;
				if (toolbar && typeof toolbar.add === 'function') toolbar.add(state.toolbarItem);
			} catch (error) {
				console.error('[GeckoLib Multi-Particle] could not add toolbar item', error);
			}

			try {
				state.menuItem = {
					name: '添加粒子效果',
					icon: 'add_circle',
					condition,
					click() {
						addEffectToSelection();
					},
				};
				if (typeof Keyframe !== 'undefined' && Keyframe.prototype.menu
					&& typeof Keyframe.prototype.menu.addAction === 'function') {
					Keyframe.prototype.menu.addAction(state.menuItem, '#actions');
				}
			} catch (error) {
				console.error('[GeckoLib Multi-Particle] could not add menu item', error);
			}
		},

		onunload() {
			unpatchAnimationCodec();

			state.listeners.forEach(([event, handler]) => {
				try {
					Blockbench.removeListener(event, handler);
				} catch (error) { /* ignore */ }
			});
			state.listeners = [];

			if (state.observer) {
				state.observer.disconnect();
				state.observer = null;
				state.observerTarget = null;
			}

			if (state.menuItem && typeof Keyframe !== 'undefined' && Keyframe.prototype.menu
				&& Array.isArray(Keyframe.prototype.menu.structure)) {
				const index = Keyframe.prototype.menu.structure.indexOf(state.menuItem);
				if (index !== -1) Keyframe.prototype.menu.structure.splice(index, 1);
				state.menuItem = null;
			}

			if (state.toolbarItem) {
				const toolbar = (typeof Toolbars !== 'undefined' && Toolbars) ? Toolbars.keyframe : null;
				if (toolbar && typeof toolbar.remove === 'function') toolbar.remove(state.toolbarItem);
				if (typeof state.toolbarItem.delete === 'function') state.toolbarItem.delete();
				state.toolbarItem = null;
			}

			for (const name in config) {
				if (config[name] && typeof config[name].delete === 'function') config[name].delete();
				delete config[name];
			}

			// 设置分类如果已经空了就一并清掉
			try {
				const category = Settings.structure[CATEGORY_ID];
				if (category && (!category.items || Object.keys(category.items).length === 0)) {
					delete Settings.structure[CATEGORY_ID];
				}
			} catch (error) { /* ignore */ }
		},
	});
})();
