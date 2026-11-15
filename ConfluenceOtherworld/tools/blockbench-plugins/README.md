# GeckoLib Multi-Particle Keyframes

让 **GeckoLib Animated Model**（Blockbench）的动画编辑器可以像基岩版实体动画那样，在**一个关键帧**上放
**多个粒子效果**（以及多个音效），而不是一个关键帧只能有一个效果。

包含两部分：

| 部分 | 位置 | 作用 |
| --- | --- | --- |
| Blockbench 插件 | `ConfluenceOtherworld/tools/blockbench-plugins/geckolib_multi_particle.js` | 编辑器侧：恢复“+”按钮、多效果关键帧、导出/导入 |
| Mixin | `ConfluenceOtherworld/src/main/java/org/confluence/mod/mixin/integration/geckolib/KeyFramesAdapterMixin.java` | 运行时侧：让 GeckoLib 原生读取 `particle_effects: [ {...}, {...} ]` 数组写法 |

* Blockbench 版本：5.x（验证于 **5.1.6** Windows 桌面版）
* GeckoLib：**4.8.3**（1.20.1 Forge）为主；已对照 4.8.4（1.21.1 NeoForge）与 main 分支（GeckoLib 5）源码
* 导出规则固定、没有开关：**多效果关键帧 → 数组；单效果关键帧 → 和以前一样的对象写法**。
  数组写法需要第 5 节的 mixin 才能被运行时读取。

---

## 1. 为什么原来做不到

Blockbench 核心其实**已经**支持 effect 通道（`particle` / `sound`）的关键帧带多个数据点：

| 位置 | 内容 |
| --- | --- |
| `timeline_animators.js` → `EffectAnimator.channels` | `particle: { max_data_points: 1000 }`、`sound: { max_data_points: 1000 }` |
| `keyframe.js` → `Keyframe#compileBedrockKeyframe` | `data_points.length > 1` 时返回数组，否则返回单个对象 |
| `keyframe.js` → 关键帧面板模板 | 每个数据点都有独立的 effect / locator / script / bind_to_actor 输入框 |

问题出在 GeckoLib 官方插件每渲染帧都会执行：

```js
// geckolib.js / ts/keyframe.ts
const addPrePostButton = document.querySelector('#keyframe_type_label > div');
if (addPrePostButton) addPrePostButton.hidden = true;
```

`#keyframe_type_label > div` 就是关键帧面板里的 **“+”（add data point）按钮**，于是编辑器里没有办法给
一个粒子关键帧再加第二个效果。

插件因此做三件事：

1. 当**选中的关键帧全部是效果关键帧**（`particle` / `sound`）时恢复 “+” 按钮；骨骼关键帧
   （rotation/position/scale）保持 GeckoLib 原本行为，不做任何改动。
2. 让效果关键帧始终保持 `linear` 插值（避免 GeckoLib 插件的 `updateKeyframe()` 删掉多余数据点）。
3. 保证导出结构：多效果 → 数组，单效果 → 对象，**不改写时间码**。导入端完全不动
   （数组写法本来就是 GeckoLib 的 Blockbench 插件原生支持的）。

另外提供工具栏按钮和关键帧右键菜单的 **“添加粒子效果”**（等价于点 “+”，会复制当前效果作为新条目）。

---

## 2. 安装 / 生效

插件已经放进 Blockbench 插件目录：

```
%APPDATA%\Blockbench\plugins\geckolib_multi_particle.js
```

如果 Blockbench 当时正在运行，需要**重启 Blockbench**（或
`File → Plugins → Load Plugin from File` 重新加载）才会用上新版本。

Mixin 部分需要重新编译 mod 才生效（见第 5 节）。

---

## 3. 使用

1. 打开 / 新建 **GeckoLib Animated Model** 工程，进入 `Animate` 模式。
2. 在时间轴 **Effects → particle** 轨道上选中（或新建）一个粒子关键帧。
3. 关键帧面板出现 **“+”** 按钮 → 点一下多出一个效果条目，每个条目可单独填
   `Effect`、`Locator`、`Pre Effect Script`、`Bind to actor`。
   * 也可用工具栏 **添加粒子效果** 或右键关键帧 → **添加粒子效果**。
   * 删除某个效果：点该条目右上角 `✕`。
4. 正常保存 / 导出：`.bbmodel` 里保存的是“一个关键帧 + N 个数据点”（完全保真），
   `.animation.json` 按第 4 节的规则导出。

音效（`sound` 通道）同理。

---

## 4. 导出格式

同一个时间码下：

* **只有 1 个效果** → 保持原来的对象写法（和插件出现之前完全一样）：

```json
"particle_effects": {
  "0.5": { "effect": "minecraft:flame", "locator": "root" }
}
```

* **有 2 个及以上效果** → 数组，所有元素共用同一个时间码，不再伪造时间：

```json
"particle_effects": {
  "0.0": [
    { "effect": "pa", "locator": "la", "pre_effect_script": "sa;" },
    { "effect": "pb", "locator": "lb", "pre_effect_script": "sb;" }
  ]
}
```

补充说明：

* **时间码永远不会被改写**，不会出现 `0.00001` 之类的偏移条目；插件也不对导入做任何加工。
* 官方 GeckoLib 读不了数组（4.8.x 的 `KeyFramesAdapter`、5.x 的 `ActorAnimationParticleEffect` 都是
  `json.getAsJsonObject()`，遇数组抛 `IllegalStateException: Not a JSON Object`），所以需要第 5 节的 mixin。
  插件在真的写出数组时会在**控制台**留一条提示（不弹窗）。
* Blockbench 侧 GeckoLib 插件本身就支持数组（`data_points: particles`），所以导入导出往返无损。
* 早期版本插件（亚刻偏移导出）留下的文件里，`0.50001` 这类条目会被当作**各自独立的关键帧**导入
  （插件不再做自动合并）；如需合成一个关键帧，在时间轴上手动处理即可。

---

## 5. Mixin：让运行时原生支持数组

文件：`ConfluenceOtherworld/src/main/java/org/confluence/mod/mixin/integration/geckolib/KeyFramesAdapterMixin.java`
（已注册进 `ConfluenceOtherworld/src/main/resources/confluence.mixins.json` 的 `mixins` 数组）。

做法：

* `@Mixin(value = KeyFramesAdapter.class, remap = false)` —— GeckoLib 不是 MC 类所以 `remap = false`；
  `@Pseudo` 让 GeckoLib 缺失时优雅跳过。
* 在 `deserialize` 的 `HEAD` 注入并 `cancellable`，**只在检测到数组写法时**自行构建
  `Animation.Keyframes`；否则直接 return，完全走 GeckoLib 原逻辑（timeline、普通对象写法行为不变）。
* 注入点写了完整描述符，避免选中 `JsonDeserializer` 生成的 `Object deserialize(...)` bridge 方法。
* 数组元素逐个生成 `ParticleKeyframeData` / `SoundKeyframeData`，**`startTick` 完全相同、不做任何时间偏移**。
* 由此带来一个 GeckoLib 侧的行为：`AnimationController` 用 `Set#add` 去重，而 `KeyFrameData#equals`
  比较的是 `hashCode`（粒子的 `hashCode` = `hash(startTick, effect, locator, script)`，音效 =
  `hash(startTick, sound)`），所以**同一时间码且内容完全相同**的条目只会触发一次；
  只要 effect / locator / pre_effect_script 任一不同就互不影响，全部正常触发。
  想让同一个粒子在同一刻发射多次，用不同的 `locator` / `pre_effect_script` 区分。

已验证：

* `gradlew :ConfluenceOtherworld:compileJava` **BUILD SUCCESSFUL**（Mixin 注解处理器 0.8.5 通过）。
* `javap` 对照 GeckoLib 4.8.3 运行库，确认 `deserialize(JsonElement, Type, JsonDeserializationContext)`
  返回 `Animation$Keyframes`、`Animation.Keyframes` 是公开 record（构造器签名与 mixin 一致）。
* GeckoLib 4.8.3 中 `particle_effects` 只被 `KeyFramesAdapter` 读取，一个注入点即可全覆盖。

注意：

* 运行时补丁需要重新编译 mod；`confluence.mixins.json` 的 `injectors.defaultRequire = 1`，
  将来 GeckoLib 改了方法签名会在启动阶段直接报错（便于发现，而不是静默失效）。
* GeckoLib 5 的动画加载结构不同（`ActorAnimation` + `ActorAnimationParticleEffect`），升级要另写一份。
* `ParticleStorm` 也 patch 了 GeckoLib 粒子关键帧（`ParticleKeyframeDataMixin`），两者不冲突
  （它给 `ParticleKeyframeData` 加访问器，本 mixin 只管反序列化）。

---

## 6. 设置项

`File → Preferences → Settings → GeckoLib 多粒子关键帧`

| 设置 | 默认 | 说明 |
| --- | --- | --- |
| 启用多粒子关键帧 | 开 | 总开关（关掉后不做导出转换，也不恢复 “+” 按钮） |
| 效果关键帧强制线性插值 | 开 | 防止 GeckoLib 插件删除多余数据点（建议保持开启） |

> 导出格式（多效果 → 数组、单效果 → 对象）是固定行为，没有开关；导入端不做任何处理。

---

## 7. 已知限制

* **不要给粒子关键帧选非线性插值**。GeckoLib 插件的 `updateKeyframe()` 在
  `update_keyframe_selection` 时，只要动画里存在“数据点 != 1 且 interpolation != linear”的关键帧
  （例如 catmullrom 骨骼关键帧），就会从**当前选中**的关键帧里删掉多余数据点 —— 这是 GeckoLib
  插件自身的行为，本插件通过“强制效果关键帧为线性”规避。
* **GeckoLib 插件的 pre/post 转换**：把骨骼关键帧设成 `step` 时，GeckoLib 会把它变成“线性 + 两个数据点”，
  若此时**同时选中了粒子关键帧**，它也会顺手给粒子关键帧加一个重复数据点，面板点 `✕` 删掉即可。
* 数组写法下，**同一时间刻 + 内容完全相同**的条目只会触发一次：GeckoLib 的 `AnimationController`
  用 `Set#add` 去重，而 `KeyFrameData#equals` 比的是 `hashCode`（粒子含 `startTick/effect/locator/script`，
  音效含 `startTick/sound`）。内容不同的条目不受影响；想让同一粒子在同一刻发射多次，
  用不同的 `locator` / `pre_effect_script` 区分。
* 数组写法需要 mixin；把这种动画文件给**没有补丁**的 GeckoLib 用时会在加载阶段抛
  `IllegalStateException: Not a JSON Object`。
* 粒子能否显示、`locator` 是否存在，取决于 mod 里的 `ParticleKeyframeHandler` / locator 定义，与本插件无关。
* 插件只影响 `geckolib_model` 格式；基岩版格式完全保持原样。

---

## 8. 测试

插件逻辑有离线单元测试（最小化 Blockbench 全局桩，Node 直跑，无第三方依赖）：

```bash
node ConfluenceOtherworld/tools/blockbench-plugins/test/geckolib_multi_particle.test.mjs
```

覆盖（11 项）：插件注册 / 设置项、**多效果 → 数组**、**单效果 → 对象**、单数据点数组还原成对象、
数组导出只提示一次（控制台）、0.0 关键帧多效果的数组输出、**导入路径不被 patch**、
添加效果动作、恢复 “+” 按钮（粒子显示 / 骨骼不干预 / 超过 `max_data_points` 隐藏）、强制线性插值、卸载还原。

**仍需人工确认**：真实时间轴交互、面板 Vue 重渲染时按钮的观感、以及游戏内实际触发粒子的表现
（含 mixin 生效后数组写法的触发次数）。
