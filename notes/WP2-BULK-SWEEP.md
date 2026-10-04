# WP2 一次性收口：60 个缺失 id 全量落地

按用户要求「一次性把缺的全弄进来」，本批把注册层剩余的全部物种一次搬入，然后逐轮修编译。

## 结果

| 项 | 数值 |
|---|---|
| `MonsterEntities` 成员数 | 153 → **209** |
| 注册层缺口（普查口径 213） | 60 → **4**（仅剩被未移植 Boss 挡住的那 4 个） |
| 本批新增 java | 21 个（渲染器 12 + 模型 9） |
| 本批新增资源 | 见 `git show --stat` |
| 编译门 | **0 错误 / 0 文件** |

## 一次搬入的内容

1. **注册 56 条**（`MonsterEntities`）：由脚本从 1.20 抽取声明块 + 机械替换
   （`RegistryObject<EntityType<X>>` → `DeferredHolder<EntityType<?>, EntityType<X>>`、
   `LibAttributes.getArmorPenetration().get()` → 无 `.get()`），并自动补齐 41 个缺失 import。
   同时补齐 6 个注册辅助：`registerSnatcher` / `registerCharger` / `registerHumanoidLand` /
   `registerJellyFish`（+ 之前的 `registerSkeleton` / `registerJumpingLand` / `registerFlyingFish` /
   `registerAcceleratingLand` / `registerGoblinLand`）。
2. **渲染器 87 条**（`ModClientEvents`）：同样脚本抽取 1.20 行 + 自动补 127 个 import。
3. **客户端类 21 个**：`JellyFishRenderer`/`EntityGlowingGeoLayer`/`SnatcherRenderer`/`PlantHeadRenderer`/
   `ClimbingSpiderRenderer`/`FullbrightGeoRenderer`/`TetheredPlantRenderer`/`DemonRenderer`/`DemonEyeRenderer`/
   `MartianProbeRenderer`/`BloodySporeRenderer`/`OldShakingChestRenderer`/`GeoNegativeVolumeRenderer`（从 TerraEntity 归位）+
   模型 `NymphModel`/`HopliteModel`/`AngryTumblerModel`/`VariantTextureGeoModel`（上一批）/`DemonEyeGeoModel`/
   `MartianProbeModel`/`SpiderSetModel`/`CritterGeoModel`。
4. **放置规则 68 组** + **谓词 39 支**（含私有辅助 `hasConnectedSand`/`hasClearColumn`/`canSpawnTownSlimeRescue`）：
   脚本按 1.20 的组与谓词抽取，`PortSpawnPlacementTypes` → `SpawnPlacementTypes`、`PortTags.Biomes.*` → `Tags.Biomes.*`。
5. **资源**：按 id 派生 + 渲染器里的显式路径从 1.20 拷贝（新增 40 个）。

## 逐轮修掉的编译问题（脚本改不动的部分）

| 类别 | 处数 | 处理 |
|---|---|---|
| geckolib 颜色尾参（`preRender`/`renderRecursively`/`actuallyRender` 的 4 个 float → `int colour`） | 7 个文件 | 脚本批量改写 + 逐个人工核对 |
| `applyRotations` 5 参 → 6 参 | 6 个文件 | 脚本追加 `float scale` 并透传，个别文件的局部变量重名单独改名 |
| `VertexConsumer` 旧 API（`vertex/color/uv/overlayCoords/uv2/normal/endVertex`） | 2 个文件 | 改 `addVertex/setColor/setUv/setOverlay/setLight/setNormal`；`Matrix3f` → `PoseStack.Pose` |
| `defineSynchedData` / `AnimatableManager` 等 | — | 随 4 个被挡住的类一起回退（见下） |
| `EntityGlowingGeoLayer#getRenderType` 被标记 `forRemoval` | 1 | `@SuppressWarnings("removal")` |
| 一次性误删两个文件（脚本读写顺序写反导致截断为 0 字节） | 2 | 从 1.20 重新拷贝并重做替换 |

## 仍缺的 4 个 id（阻塞原因，非遗漏）

`DUNGEON_SPIRIT` / `VISUAL_NEURON` / `THE_HUNGRY` / `HILL_HUNGRY` 需要
`BrainOfCthulhu`（12 处引用）、`WallOfFlesh`（6 处）、`BossEntities.PLANTERA` 等**尚未移植的 Boss**，
以及 `TheHungry`/`VisualNeuron`/`HillHungry`/`DungeonSpirit`/`HungryMovementAction` 五个类
（63 处错误：geckolib 包名 + `defineSynchedData` 新签名 + 上述 Boss 依赖）。
本批把已拷入的这 5 个类**整批撤回**（不留半截编译不通过的代码），随「血肉墙 / 克苏鲁之脑」批次一起做。

## 验证

| 项 | 结果 |
|---|---|
| `build_errors.py --maxerrs 2000` | 0 错误 / 0 文件 |
| `check_duplicates.py`（20 个新 java） | 全部 0 处 |
| `fix_eol.py --check` | 0 候选 |
| `asset_gap.py` | 978 处引用 / 缺口 19（18 处为既有的 B/C 类 + 1 个 geckolib 占位路径 `geo/animal/dummy`，1.20 也没有） |
| 注释口径 | 新代码只保留 1.20 的注释原文 |
