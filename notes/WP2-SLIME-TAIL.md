# WP2 史莱姆尾巴（特效/城镇史莱姆渲染层）：落地记录

> 代码提交 `5a4d8259f`（40 文件 / +11030）。编译门 `ConfluenceOtherworld` **0 错误 / 0 文件**。
> 本文件是那次提交的**补录记录**（提交时未随附笔记，随台账重算批 2 一并补上）。

## 一、本批内容（3 新 java + 1 改 + 36 资源）

| 文件 | 来源（1.20） |
|---|---|
| `client/entity/renderer/GeoSpecialSlimeRenderer` | 同名（shell/内核两遍渲染） |
| `client/entity/renderer/TownSlimeRenderer` | 同名（城镇史莱姆 + diva 彩虹溶解） |
| `client/effect/DivaSlimeVertexConsumer` | 同名（顶点色装饰器 + 明度贴图生成） |
| `client/event/ModClientEvents` | +13 条渲染器注册（1.20 `:635/636/637/645` 尖刺×3+Slimer、`:774-781` 城镇×8、`:835` CLUMSY_BALLOON_SLIME 占位）+ `:1103` 的 `DivaSlimeVertexConsumer` 贴图清理 registerReloadListener |

资源 36：`spiked_{,jungle_,ice_}slime` 与 `slimer` 的 geo/动画/贴图 + 8 个城镇史莱姆的 geo/动画/贴图。

**闭包为什么只剩 3 个文件**：`notes/WP2-SLIME-SUBSET.md` 当初测出的是 59/61 文件闭包，
那时缺 `GeoNormalRenderer` 与 `client/effect/RenderStateShardAccessor`；两者随后各自落地
（整类批 `57f0fb465`、WP3 客户端族批 `439f42ae5`）→ 闭包自然收敛到自身。

## 二、1.20 → 1.21 的 API 差异（均已写进代码注释）

| 1.20 | 1.21.1 | 处理 |
|---|---|---|
| `VertexConsumer.vertex/color/uv/overlayCoords/uv2/normal` | `addVertex/setColor/setUv/setUv1/setUv2/setNormal` | `DivaSlimeVertexConsumer` 是装饰器 → 19 处逐条改写 |
| `endVertex()` / `defaultColor(...)` / `unsetDefaultColor()` | **三个方法已从 `VertexConsumer` 接口删除** | 三个覆写整体删除 |
| geckolib `actuallyRender/renderCubesOfBone/renderCube(…, float r,g,b,a)` | 尾参收成 `int colour` | `GeoSpecialSlimeRenderer` 5 处 + `TownSlimeRenderer` 3 处 |

## 三、验证与结果

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --repo . --maxerrs 2000` | **0 错误 / 0 文件**（26 → 4 → 0） |
| `check_duplicates.py` | 0 处「疑似移动/重复」；1 处 DIFF 对 TerraEntity 的 `GeoSpecialSlimeRenderer`（先加后删） |
| **WP2 史莱姆族的可见性** | **30/30 全部有渲染器**（此前 25 个普通史莱姆可见；尖刺×3 + Slimer + 城镇×8 不可见） |
| 待游戏内验收 | 三生态尖刺史莱姆与地牢 Slimer 的两遍渲染、8 个城镇史莱姆外观、diva 的彩虹溶解 |
| 遗留 | `CLUMSY_BALLOON_SLIME` 专用模型（1.20 亦为 todo，现用 `MissingModelRenderer` 占位）；刷怪蛋/掉落表/lang（WP7 datagen） |
