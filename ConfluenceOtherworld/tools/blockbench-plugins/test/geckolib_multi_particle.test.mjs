/**
 * GeckoLib Multi-Particle Keyframes — 离线单元测试
 *
 * 在 Node 里用一层最小化的 Blockbench 全局桩（Plugin / Setting / Codecs / Timeline / DOM ...）
 * 载入插件脚本，验证：
 *   1. 插件注册与设置项创建
 *   2. 导出时把“同一关键帧的多个效果”拆成亚刻偏移条目（GeckoLib 运行时兼容）
 *   3. array 模式保持数组并给出一次警告
 *   4. 导入时把亚刻偏移条目合并回一个关键帧的多个 data point
 *   5. 模型层兜底合并（插件加载顺序导致 JSON 预处理没生效的情况）
 *   6. “添加粒子效果”动作
 *   7. 恢复关键帧面板的 “+” 按钮 + 效果关键帧强制线性插值
 *   8. onunload 完整还原
 *
 * 运行： node tools/blockbench-plugins/test/geckolib_multi_particle.test.mjs
 */

import fs from 'node:fs';
import path from 'node:path';
import vm from 'node:vm';
import assert from 'node:assert/strict';
import { fileURLToPath } from 'node:url';

const here = path.dirname(fileURLToPath(import.meta.url));
const pluginPath = path.join(here, '..', 'geckolib_multi_particle.js');
const source = fs.readFileSync(pluginPath, 'utf8');

/* ---------------------------------------------------------------------- *
 * Blockbench 全局桩
 * ---------------------------------------------------------------------- */

function createEnvironment() {
	const env = {
		registered: null,
		listeners: {},
		messages: [],
		quickMessages: [],
		warnings: [],
		errors: [],
		settings: {},
		categories: {},
		removedSettings: [],
		button: { hidden: true },
		button_queries: [],
		panel: { id: 'panel_keyframe' },
		toolbar_children: [],
		toolbar_removals: [],
		undo_edits: [],
		preview_count: 0,
		observed: [],
	};

	// 插件用 console.warn 记录“写出了数组”的提示，这里收集起来断言
	env.console = {
		log() {},
		warn: (...args) => env.warnings.push(args.join(' ')),
		error: (...args) => env.errors.push(args.join(' ')),
	};

	env.Plugin = {
		register(id, data) {
			env.registered = { id, data };
		},
	};

	env.Settings = {
		structure: env.categories,
		addCategory(id, data) {
			env.categories[id] = { id, name: data.name, items: {} };
		},
	};

	env.Setting = class Setting {
		constructor(id, options) {
			this.id = id;
			Object.assign(this, options);
			env.settings[id] = this;
			if (env.categories[options.category]) env.categories[options.category].items[id] = this;
		}
		delete() {
			delete env.settings[this.id];
			// 与 Blockbench 核心一致：Setting#delete() 也会把自己从设置分类里移除
			const category = env.categories[this.category];
			if (category && category.items[this.id]) delete category.items[this.id];
			env.removedSettings.push(this.id);
		}
	};

	env.Blockbench = {
		on(event, callback) {
			(env.listeners[event] = env.listeners[event] || []).push(callback);
		},
		removeListener(event, callback) {
			const list = env.listeners[event] || [];
			const index = list.indexOf(callback);
			if (index !== -1) list.splice(index, 1);
		},
		dispatch(event, ...args) {
			(env.listeners[event] || []).slice().forEach(callback => callback(...args));
		},
		showQuickMessage(message) {
			env.quickMessages.push(message);
		},
	};

	env.Format = { id: 'geckolib_model' };

	env.Timeline = { selected: [], keyframes: [] };

	env.KeyframeDataPoint = class KeyframeDataPoint {
		constructor(keyframe) {
			this.keyframe = keyframe;
		}
		extend(other) {
			for (const key in KeyframeDataPoint.properties) {
				if (other && other[key] !== undefined) this[key] = other[key];
			}
		}
	};
	env.KeyframeDataPoint.properties = {
		effect: {}, locator: {}, script: {}, file: {}, bind_to_actor: {},
	};

	env.Keyframe = class Keyframe {};
	env.Keyframe.prototype.menu = {
		structure: [],
		addAction(item) {
			this.structure.push(item);
		},
	};

	env.Action = class Action {
		constructor(id, data) {
			this.id = id;
			Object.assign(this, data);
		}
		delete() {
			env.action_deleted = true;
		}
	};

	env.Toolbars = {
		keyframe: {
			children: env.toolbar_children,
			add(item) {
				env.toolbar_children.push(item);
			},
			remove(item) {
				const index = env.toolbar_children.indexOf(item);
				if (index !== -1) env.toolbar_children.splice(index, 1);
				env.toolbar_removals.push(item);
			},
		},
	};

	env.Undo = {
		initEdit(entry) { env.undo_edits.push({ type: 'init', entry }); },
		finishEdit(label) { env.undo_edits.push({ type: 'finish', label }); },
	};

	env.Animator = {
		preview() { env.preview_count++; },
	};

	env.document = {
		getElementById(id) {
			if (id === 'keyframe_type_label') {
				return {
					querySelector(selector) {
						env.button_queries.push(selector);
						return env.button;
					},
				};
			}
			if (id === 'panel_keyframe') return env.panel;
			return null;
		},
	};

	env.MutationObserver = class MutationObserver {
		constructor(callback) { this.callback = callback; }
		observe(target, options) { env.observed.push({ target, options }); }
		disconnect() { env.observed_disconnected = true; }
	};

	env.autoParseJSON = content => JSON.parse(content);

	// 动画编解码器（GeckoLib 复用 Bedrock 的 animation codec）
	const original_compile = function () { return env.compile_result; };
	const original_load = function (file) {
		env.load_file_argument = file;
		return env.load_result === undefined ? [] : env.load_result;
	};
	env.original_compile = original_compile;
	env.original_load = original_load;
	env.codec = { compileAnimation: original_compile, loadFile: original_load };
	env.Codecs = { bedrock: { format: { animation_codec: env.codec } } };

	return env;
}

function loadPlugin(env) {
	const context = vm.createContext(env);
	vm.runInContext(source, context, { filename: pluginPath });
	assert.ok(env.registered, 'Plugin.register() 没有被调用');
	assert.equal(env.registered.id, 'geckolib_multi_particle');
	env.registered.data.onload();
	return env.registered.data;
}

/** 造一个 GeckoLib 效果关键帧 */
function effectKeyframe(time, channel = 'particle', points = 1) {
	return {
		channel,
		time,
		interpolation: 'linear',
		data_points: Array.from({ length: points }, (_, index) => ({ effect: `effect_${index}`, locator: 'root' })),
		animator: { channels: { particle: { max_data_points: 1000 }, sound: { max_data_points: 1000 } } },
		remove() { this.removed = true; },
	};
}

/** vm 里创建的对象/数组属于另一个 realm，比较前先转成宿主 realm 的纯数据 */
function plain(value) {
	return JSON.parse(JSON.stringify(value));
}

/* ---------------------------------------------------------------------- *
 * 测试
 * ---------------------------------------------------------------------- */

let failures = 0;
function test(name, callback) {
	try {
		callback();
		console.log(`  ok   ${name}`);
	} catch (error) {
		failures++;
		console.error(`  FAIL ${name}\n       ${error.message}`);
	}
}

console.log('GeckoLib Multi-Particle Keyframes — tests\n');

/* 1. 注册 / 设置 / patch ------------------------------------------------- */
test('注册插件、创建设置项、patch 编解码器', () => {
	const env = createEnvironment();
	loadPlugin(env);

	assert.equal(env.registered.data.variant, 'both');

	const ids = Object.keys(env.settings);
	assert.deepEqual(ids.sort(), [
		'geckolib_multi_particle_enabled',
		'geckolib_multi_particle_force_linear',
	].sort());

	assert.notEqual(env.codec.compileAnimation, env.original_compile, 'compileAnimation 应该被包装');
	assert.equal(env.codec.loadFile, env.original_load, 'loadFile 不应该被包装（插件不碰导入）');
	assert.ok(env.listeners.render_frame && env.listeners.render_frame.length === 1);
	assert.ok(env.listeners.update_keyframe_selection && env.listeners.update_keyframe_selection.length === 1);
	assert.equal(env.toolbar_children.length, 1, '工具栏按钮应该被加入 keyframe 工具栏');
	assert.equal(env.Keyframe.prototype.menu.structure.length, 1, '右键菜单项应该被加入');
});

/* 2. 导出：多效果 -> 数组，单效果 -> 对象 --------------------------------- */
test('导出：多效果关键帧写成数组，单效果关键帧保持对象写法', () => {
	const env = createEnvironment();
	loadPlugin(env);

	const p1 = { effect: 'minecraft:flame' };
	const p2 = { effect: 'minecraft:smoke' };
	const p3 = { effect: 'minecraft:heart' };
	const p4 = { effect: 'minecraft:crit' };
	const single = { effect: 'minecraft:single', locator: 'root' };
	const s1 = { effect: 'minecraft:block.note_block.bell' };
	const s2 = { effect: 'minecraft:block.note_block.hat' };
	env.compile_result = {
		particle_effects: { '10.0': [p1, p2], '0.1': [p3, p4], '2.0': single },
		sound_effects: { '0.5': [s1, s2] },
	};

	const result = env.codec.compileAnimation({ name: 'animation.test' });

	// 时间码不被改写，一个时间码仍然只有一个条目
	assert.deepEqual(Object.keys(result.particle_effects), ['10.0', '0.1', '2.0']);
	assert.deepEqual(plain(result.particle_effects['10.0']), [p1, p2]);
	assert.deepEqual(plain(result.particle_effects['0.1']), [p3, p4]);
	assert.deepEqual(plain(result.particle_effects['2.0']), single, '单效果必须还是普通对象');
	assert.deepEqual(plain(result.sound_effects['0.5']), [s1, s2]);
});

test('导出：1 个数据点的数组会被还原成对象写法', () => {
	const env = createEnvironment();
	loadPlugin(env);

	// 例如关键帧有两个 data point，但其中一个 effect 为空（核心只返回一个点）
	const only = { effect: 'minecraft:flame' };
	env.compile_result = { particle_effects: { '0.5': [only] } };

	const result = env.codec.compileAnimation({ name: 'animation.test' });
	assert.deepEqual(plain(result.particle_effects['0.5']), only);
	assert.equal(env.warnings.length, 0, '没有写出数组就不该提示');
});

test('导出数组时只在控制台提示一次', () => {
	const env = createEnvironment();
	loadPlugin(env);

	const points = [{ effect: 'a' }, { effect: 'b' }];
	env.compile_result = { particle_effects: { '0.5': points } };
	const first = env.codec.compileAnimation({ name: 'animation.test' });
	assert.deepEqual(plain(first.particle_effects['0.5']), points);
	assert.equal(env.warnings.length, 1, '应该给出一次控制台提示');
	assert.equal(env.quickMessages.length, 0, '不应该弹窗打扰');

	env.compile_result = { particle_effects: { '0.5': points } };
	env.codec.compileAnimation({ name: 'animation.test' });
	assert.equal(env.warnings.length, 1, '提示不应重复');
});

test('默认导出：0.0 关键帧上的两个效果写成数组（复现用户场景）', () => {
	const env = createEnvironment();
	loadPlugin(env);

	const pa = { effect: 'pa', locator: 'la', pre_effect_script: 'sa;' };
	const pb = { effect: 'pb', locator: 'lb', pre_effect_script: 'sb;' };
	env.compile_result = { particle_effects: { '0.0': [pa, pb] } };

	const result = env.codec.compileAnimation({ name: 'animation.test' });
	assert.deepEqual(plain(result.particle_effects), { '0.0': [pa, pb] });
});

/* 4. 导入：插件完全不碰导入路径 ------------------------------------------ */
test('导入路径不被 patch（数组写法由 GeckoLib 插件自己解析）', () => {
	const env = createEnvironment();
	loadPlugin(env);

	assert.equal(env.codec.loadFile, env.original_load, 'loadFile 不应该被包装');

	const file = {
		content: JSON.stringify({
			animations: {
				'animation.test': {
					particle_effects: { '0.5': [{ effect: 'a' }, { effect: 'b' }] },
				},
			},
		}),
	};
	env.codec.loadFile(file);

	assert.equal(env.load_file_argument, file, '文件对象应原样传给 GeckoLib 插件');
	assert.equal(file.json, undefined, '插件不应改写导入内容（不做亚刻合并）');
});

/* 5. 手动添加效果 -------------------------------------------------------- */
test('“添加粒子效果”动作会复制现有效果为新条目', () => {
	const env = createEnvironment();
	loadPlugin(env);

	const keyframe = effectKeyframe(0.5);
	keyframe.data_points[0].effect = 'minecraft:flame';
	env.Timeline.selected = [keyframe];

	const item = env.toolbar_children[0];
	assert.equal(item.condition(), true, '选中粒子关键帧时动作可用');
	item.click();

	assert.equal(keyframe.data_points.length, 2);
	assert.equal(keyframe.data_points[1].effect, 'minecraft:flame');
	assert.equal(env.undo_edits.filter(entry => entry.type === 'init').length, 1);
	assert.equal(env.preview_count, 1);
});

test('没有选中效果关键帧时动作不可用且给出提示', () => {
	const env = createEnvironment();
	loadPlugin(env);

	env.Timeline.selected = [{ channel: 'rotation', animator: { channels: { rotation: { transform: true } } }, data_points: [{}] }];
	const item = env.toolbar_children[0];
	assert.equal(item.condition(), false);
	item.click();
	assert.equal(env.Timeline.selected[0].data_points.length, 1);
	assert.equal(env.quickMessages.length, 1);
});

/* 7. 面板 UI ------------------------------------------------------------- */
test('恢复 “+” 按钮：选中效果关键帧时可见，骨骼关键帧时保持隐藏', () => {
	const env = createEnvironment();
	loadPlugin(env);

	const particle = effectKeyframe(0.5, 'particle', 2);
	particle.interpolation = 'catmullrom';
	env.Timeline.selected = [particle];
	env.Timeline.keyframes = [particle];
	env.button.hidden = true; // GeckoLib 插件每帧都这样设置

	env.Blockbench.dispatch('render_frame');

	assert.deepEqual(env.button_queries, [':scope > .in_list_button']);
	assert.equal(env.button.hidden, false, '效果关键帧应显示 “+” 按钮');
	assert.equal(particle.interpolation, 'linear', '效果关键帧应被强制为线性插值');

	// 骨骼关键帧：不干预 GeckoLib 的行为
	env.Timeline.selected = [{ channel: 'rotation', animator: { channels: { rotation: { transform: true, max_data_points: 2 } } }, data_points: [{}] }];
	env.button.hidden = true;
	env.Blockbench.dispatch('render_frame');
	assert.equal(env.button.hidden, true, '骨骼关键帧不应显示 “+” 按钮');
});

test('达到 max_data_points 时不再显示 “+” 按钮', () => {
	const env = createEnvironment();
	loadPlugin(env);

	const keyframe = effectKeyframe(0.5);
	keyframe.animator.channels.particle.max_data_points = 1;
	env.Timeline.selected = [keyframe];
	env.Blockbench.dispatch('render_frame');
	assert.equal(env.button.hidden, true);
});

/* 8. 卸载 ---------------------------------------------------------------- */
test('onunload 完整还原', () => {
	const env = createEnvironment();
	const plugin = loadPlugin(env);

	// 触发一次渲染帧，让 MutationObserver 被挂上
	env.Timeline.selected = [effectKeyframe(0.5)];
	env.Blockbench.dispatch('render_frame');
	assert.ok(env.observed.length > 0, 'MutationObserver 应该被挂到关键帧面板上');

	plugin.onunload();

	assert.equal(env.codec.compileAnimation, env.original_compile);
	assert.equal(env.codec.loadFile, env.original_load);
	assert.equal(env.toolbar_children.length, 0);
	assert.equal(env.Keyframe.prototype.menu.structure.length, 0);
	assert.equal(Object.keys(env.settings).length, 0);
	assert.equal(env.removedSettings.length, 2);
	assert.equal(env.observed_disconnected, true);
	assert.equal((env.listeners.render_frame || []).length, 0, 'render_frame 监听应被移除');
	assert.equal((env.listeners.update_keyframe_selection || []).length, 0, 'update_keyframe_selection 监听应被移除');
	assert.equal((env.listeners.select_project || []).length, 0, 'select_project 监听应被移除');
	assert.equal(Object.keys(env.categories).length, 0, '空的设置分类应被清理');
});

console.log(`\n${failures === 0 ? '全部通过' : failures + ' 个测试失败'}`);
process.exit(failures === 0 ? 0 : 1);
