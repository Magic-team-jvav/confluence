# WP3 客户端族批·**客户端半**：克苏鲁之眼 + 蠕虫 Boss 渲染与改指

> 编译门：`ConfluenceOtherworld` **0 错误 / 0 文件**。规模：**9 新 java + 7 改 + 38 资源**（55 文件 / +13041 −22）。
> **`notes/WP3-CLIENTFAMILY-HANDOFF.md` 的 42 处错误至此全部清零**（服务端半见 `notes/WP3-WORM-SERVER-SUBSET.md`，
> 本批是客户端半）。

## 一、新增（9 java，全部 1.20 同名文件）

| 文件 | 备注 |
|---|---|
| `client/entity/renderer/GeoNormalRenderer` | 共享 geo 渲染基类（1.20 自有，此前 1.21 完全没有） |
| `client/entity/renderer/BossGeoRenderer` | 继承前者，boss 专用（含蠕虫判定） |
| `client/entity/renderer/BossWormPartRenderer` | 体节渲染 |
| `client/entity/renderer/EntityLightSampler` | 光照采样 |
| `client/entity/renderer/EyeOfCthulhuRenderer` | 含残影（afterimage）分支 |
| `client/entity/renderer/MissingModelRenderer` | 「模型待补」占位渲染器 |
| `client/entity/model/GeoNormalModel` | 共享 geo 模型基类 |
| `client/entity/model/WormPartGeoModel` | 体节 geo 模型 |
| `client/entity/model/ExplicitGeoModel` | 显式资源路径模型（**整类缺失**，被探测怪注册用到） |

## 二、1.20 → 1.21 的 API 差异（逐条修完）

| 1.20 写法 | 1.21.1 实际 | 处理 |
|---|---|---|
| `software.bernie.geckolib.core.animatable.model.CoreGeoBone` | **该类在 geckolib 4.8.4 已删除**（与 G5′ 的 `GunRenderer` 同一问题） | `GeoNormalModel`：import 与两处字段/方法签名改 `GeoBone` |
| `preRender(…, float red, green, blue, alpha)` | `preRender(…, int colour)`（4.8.4 把四个颜色分量收成一个 ARGB int；`GeoRenderer` default 方法） | `GeoNormalRenderer` 覆写 + `super` 透传；`EyeOfCthulhuRenderer` 覆写 + **残影分支** `(0,0,0,activeAlpha)` → `FastColor.ARGB32.color(round(activeAlpha*255), 0,0,0)` |
| `applyRotations(…5 参)` | 4.8.4 的 5 参版 **`@Deprecated(forRemoval)`**，而基类渲染路径调用的是 **6 参**版（`javap -c` 确认 `invokevirtual applyRotations:(…FFFF)V`） | `GeoNormalRenderer` **改覆写 6 参版**并透传 `scale`。⚠️ 只加 `@SuppressWarnings("removal")` 会让覆写变成死代码 → 蠕虫的偏航/俯仰修正会静默失效 |
| `renderNameTag(…, int packedLight)` | 多一个 `float partialTick`（`EntityRenderer.java:188`） | `MissingModelRenderer` |

## 三、接线（与渲染器**同批**，避免隐形 Boss）

`ModClientEvents` +7 条（1.20 `ModClientEvents:712/713/714/715/716/723/724` 逐条）：
`EYE_OF_CTHULHU`→`EyeOfCthulhuRenderer`、`SERVANT_OF_CTHULHU`/`EATER_OF_WORLDS`/`THE_DESTROYER_PROBE`→`BossGeoRenderer`
（含 1.20 的 `true, 2.2F, 0.0F` 与 `ExplicitGeoModel` 参数）、`EATER_OF_WORLDS_SEGMENT`→`BossWormPartRenderer`、
`THE_DESTROYER`/`THE_DESTROYER_PART`→`MissingModelRenderer`（1.20 原文即 `todo`，本批照实保留 todo 注释）。

**克苏鲁之眼的 8 处改指**（同批完成，否则 boss 会隐形）：`BasePotBlock` ×1、图鉴 ×2（含 `DEERCLOPS`）、
免疫效果表 ×2、宝藏袋 ×3（含 `DEERCLOPS`）、`BossDelaySpawner` ×4（含 `DEERCLOPS`）、`BloodMoonGameEvent` ×1、
`MoneyTradeHealthFull` ×1；并做 `BossDelaySpawner` 的**类型对齐**：
`AbstractTerraBossBase` → `org.confluence.mod.common.entity.boss.BaseBoss`（1.20 原文即主模组 `BaseBoss`，
队列/迭代器/`pushBoss` 签名 4 处）+ `EyeOfCthulhu` import 换主模组。

## 四、资源（38）

`geo/animations/textures` 中 `eye_of_cthulhu`、`servant_of_cthulhu`、`eater_of_worlds{,_segment,_tail}`、
`the_destroyer{,_probe,_segment,_segment_damage,_tail}`；Boss 血条 ×4、GUI 血条 ×2、眼之悠悠球贴图 ×2、
`the_eye_of_cthulhu` 物品模型、`true_eye_of_cthulhu_free_{0,1}.ogg`。

## 五、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --repo . --maxerrs 2000` | **0 错误 / 0 文件**（三轮：8 → 3 → 0） |
| `check_duplicates.py`（9 新增） | 0 处「疑似移动/重复」；2 处 DIFF 对 **TerraEntity** 同名渲染器/模型（`terraentity.client.entity.*`）—— 既定「先加后删」 |
| 子模块 | 未改动（Lib 已在上批 `a762fe6`） |
| 待游戏内验收 | 克苏鲁之眼（含残影/两阶段）与仆从、世界吞噬者体节连接与尾部、毁灭者（头部暂为占位渲染器）/探测怪激光；西线**秋明**：`BossDelaySpawner` 的夜袭现在召的是主模组眼实体 |

## 六、本批之后仍待做（另立批次）

1. **史莱姆族的特效渲染尾巴**（`GeoSpecialSlimeRenderer` 尖刺×3 + `Slimer`、`TownSlimeRenderer` +
   `DivaSlimeVertexConsumer`）——现在依赖已齐（`GeoNormalRenderer` + `RenderStateShardAccessor` 都在），
   可直接照 `notes/WP2-SLIME-SUBSET.md` 第四节做；
2. `THE_DESTROYER`/`THE_DESTROYER_PART` 的专用模型（1.20 也是 `todo`，资源已导出）
3. 2 处遗留 warning（`getDeathMaxRotation` 相关）+ WP7 datagen（刷怪蛋/掉落表/lang/bestiary）
