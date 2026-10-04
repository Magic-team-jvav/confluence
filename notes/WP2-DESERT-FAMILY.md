# WP2 地下沙漠族（10 个 id）：木乃伊 + 食尸鬼 + 拉米亚

> 编译门 `ConfluenceOtherworld` **0 错误 / 0 文件**（一次通过）。
> 注册层第 7 批，按 `notes/WP2-MONSTER-REGISTRY-GAP.md` 第十节的预案执行 —— **一个模型类换 10 个 id**。

## 一、本批内容（1 新 java + 4 改 + 30 资源）

| 文件 | 改动 |
|---|---|
| `client/entity/model/ContactHumanoidGeoModel`（新，43 行） | 1.20 同名文件：为**只有移动资源、没有 `attack.strike`** 的接触型人形敌怪补短促挥击 —— 原动画仍负责身体/腿/附加部件，仅在实体已有 swing 进度时叠加双臂旋转，不改变行为、命中时机与空闲姿势。适配：`CoreGeoBone`→`GeoBone`、`AnimationState` 换包（并把 1.20 里为绕开泛型而写的临时变量 `arm` 直接内联） |
| `common/init/entity/MonsterEntities` | **+10 条注册** + **`registerJumpingLand` 五个重载**（1.20 `:912/916/925/929/933`，逐字移植；木乃伊/食尸鬼用最后一个：`JumpProfile` 传 `null` + `mummy` 标记） |
| `client/event/ModClientEvents` | **+10 条渲染器注册**（1.20 `:858-864/867-869`）。三个族的**左右手骨骼名不同**：木乃伊 `RightArm/LeftArm`（+`withCutout()`）、拉米亚 `right_arm/left_arm`、食尸鬼 `hand_right/hand_left`；三只跳扑食尸鬼另挂 geckolib 的 `withAltAnimations(asResource("ghoul"))` |
| `common/entity/SpawnPlacementChecks` | **+11 支谓词**：`checkDesertUndergroundMonsterSpawn`(`:484`) + 六个变体（纯净/纯净或神圣/邪恶/腐化/猩红/神圣）+ 四支木乃伊谓词（`:530/535/540/545`）+ 私有 `checkMummyOnSand`(`:550`) |
| `common/init/entity/CreatureSpawnPlacements` | **+10 条 `group(...)`**（1.20 `:205-217` 六组 + `:241-244` 木乃伊四组，全部 `hardmode(...)`） |
| 资源 30 | 10 个 id × (geo + animation + texture)，与 1.20 逐字节相同 |

## 二、本批按上轮预案「先核 API 再动手」，因此一次通过

开工前已核掉的三个不确定点（上轮笔记第十节）：

| 待核项 | 结论 |
|---|---|
| `GeoNormalRenderer#withCutout()` | 1.21 **已有**（`GeoNormalRenderer.java:77`） |
| `.withAltAnimations(ResourceLocation)` | **不是本仓库的方法**，来自 geckolib `DefaultedEntityGeoModel`；已 `javap` 核 `geckolib-neoforge-1.21.1-4.8.2.jar`：`withAltModel`/`withAltAnimations`/`withAltTexture` 都在 → 三行 ghoul 渲染器逐字照搬 |
| `JumpingWarriorMonster` 构造器 / `CombatState.WOUNDED` / `StateBuilder#multiply` | 全部命中（含 7 参 `@Nullable JumpProfile` 那个，木乃伊要传 `null`） |

## 三、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --maxerrs 2000` | **0 错误 / 0 文件**（一次通过） |
| 渲染器覆盖 | **10/10** |
| 放置规则覆盖 | **10/10**（全部 `hardmode(...)`，与 1.20 一致） |
| `check_duplicates.py`（`ContactHumanoidGeoModel`） | 0 处「疑似移动/重复」 |
| 资源 | 30/30 与 1.20 同哈希 |
| `asset_gap.py` 复算 | 922 处引用 / 缺口仍 **18**，未新增 |
| 待游戏内验收 | 四种木乃伊按脚下沙块分型生成、三只食尸鬼的 alt 动画与跳扑、拉米亚的接触挥击补丁、困难模式门槛 |

## 四、注册层进度

| 批次 | id 数 | `MonsterEntities` 成员数 |
|---|---:|---:|
| 普查起点 | — | 63 |
| 蝙蝠 / 宝箱怪 / 骨骼 / 哥布林+孢子僵尸 / 僵尸族 / 陆行族 | 46 | 109 |
| **地下沙漠族（本批）** | **10** | **119** |
| 缺口 | — | 150 → **94** 待补（**已跌破 100**） |

## 五、下一批候选

1. **食人花族（3 个 id）**：`SNATCHER`/`MAN_EATER` 共用一个 `SnatcherRenderer`（1.20 `client/entity/renderer/SnatcherRenderer.java`），
   `FUNGI_BULB` 需要 `TetheredPlantRenderer` → 「两个类换 3 个 id」，成本中等。
2. **水母族（3 个 id）**：`JellyFishRenderer` + `EntityGlowingGeoLayer` + `registerJellyFish` + 2 支水谓词。
3. **`GeoNegativeVolumeRenderer` 迁入主模组**（现只在 TerraEntity 子模块），可解开 `CURSED_SKULL` 等 5 处注册。
4. **纯 `registerEntity` 批次**：剩余 85 个里有一批（`WYVERN`/`GIANT_WORM`/`DIGGER`/`DEVOURER`/`WORLD_FEEDER`/`BONE_SERPENT` 等
   蠕虫族）用的是已存在的 `registerWorm`/`registerWormSegment` 辅助，属「零新类」路线。
