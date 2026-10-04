# WP2 哥布林入侵族（6 个 id）+ 孢子僵尸（2 个 id）

> 编译门 `ConfluenceOtherworld` **0 错误 / 0 文件**（一次 import 遗漏 → 2 错 → 0）。
> 与骨骼批（`c71cabef3`）同属「先落共享客户端类、再吃整族」的注册层节奏；这是第 4 批。

## 一、本批内容（2 新 java 客户端 + 4 改 + 24 资源）

| 文件 | 改动 |
|---|---|
| `client/entity/model/VanillaGoblinGeoModel`（新，70 行） | 1.20 同名文件；哥布林动画族的人形动作桥接（普通兵复用行走/转头/挥臂，弓箭手复用拉弓姿势）。适配：`CoreGeoBone`→`GeoBone`、`RenderUtils`→`RenderUtil`、`AnimationState` 换包；`applyBowPose` 复用同包已落地实现 |
| `client/entity/model/VanillaZombieGeoModel`（新，74 行） | 1.20 同名文件；复用原版 `HumanoidModel(ZOMBIE)` 的行走/转头/前伸手臂，并按 Bedrock 手臂 X 轴反向逐项写骨骼旋转（因此**不需要** `RenderUtil` 改名） |
| `common/init/entity/MonsterEntities` | **+8 条注册** + **`registerGoblinLand` 两个重载**（1.20 `:892/896`）；`GOBLIN_WARRIOR` 默认手持石剑、`GOBLIN_ARCHER` 覆盖箭速 1.6，均逐字照 1.20 |
| `client/event/ModClientEvents` | **+8 条渲染器注册**（1.20 `:872-877/882-883`） |
| `common/entity/SpawnPlacementChecks` | **+3 支谓词**：`checkSurfaceMobSpawn`(`:754`)、`checkSurfaceDayMobSpawn`(`:749`)、`checkGoblinScoutSpawn`(`:743`) |
| `common/init/entity/CreatureSpawnPlacements` | +2 条 `group(...)`（1.20 `:128-134` 的 `checkGroundSpawn` 组、`:135` 的侦察兵组） |
| 资源 24 | 哥布林 6 个 id × (geo + animation + texture)（在 **`goblin/` 子目录**）+ 孢子僵尸 2 个 id × 3 |

## 二、本批两个容易踩的点（都已按 1.20 原样处理）

1. **哥布林的资源在子目录**：1.20 渲染器写的是
   `new VanillaGoblinGeoModel<>(c, MonsterEntities.GOBLIN_X.getId().withPrefix("goblin/"))`，
   即路径是 `geo/entity/goblin/<id>.geo.json`、`textures/entity/goblin/<id>.png`；
   而 `ANGER_GOBLIN` 用的是显式 `Confluence.asResource("goblin/anger_goblin")`。**按实体 id 直接推路径会全部落空。**
2. **僵尸族只吃了两只**：`ZOMBIE`（1.20 `:879`）用的是另一个类 `ZombieGeoModel`（无泛型参数），
   1.21 侧**还没有**；而 `SPORE_ZOMBIE`/`HAT_SPORE_ZOMBIE` 用的是本批新落的 `VanillaZombieGeoModel`。
   → `ZOMBIE` 与 `checkZombieSpawn` 一起留给下一批，避免为一只僵尸单独扩两处依赖。

## 三、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --maxerrs 2000` | **0 错误 / 0 文件**（2 → 0，缺 `GoblinArcher` import，已补） |
| 渲染器覆盖 | **8/8**（脚本逐条计数） |
| 放置规则覆盖 | **8/8** |
| `check_duplicates.py`（2 个新模型类） | 0 处「疑似移动/重复」 |
| 资源 | 24/24 与 1.20 同哈希 |
| `asset_gap.py` 复算 | 919 处引用 / 缺口仍 **18**（= B 类 6 + C 类 12），未新增 |
| 待游戏内验收 | 侦察兵只在白天地表；苦工/盗贼破门、战士/侦察兵开门；弓箭手射箭；愤怒哥布林的击退抗性；孢子僵尸的前伸手臂朝向 |

## 四、注册层进度

| 批次 | id 数 | `MonsterEntities` 成员数 |
|---|---:|---:|
| 普查起点 | — | 63 |
| 蝙蝠族 `b78cd7d8b` | 9 | 72 |
| 宝箱怪族 `a30777650` | 8 | 80 |
| 骨骼族 `c71cabef3` | 12 | 92 |
| **哥布林 + 孢子僵尸（本批）** | **8** | **100** |
| 缺口 | — | 150 → **113** 待补（跨过 100 成员线） |

## 五、下一批

1. **僵尸族收口**：移植 `ZombieGeoModel`（1.20 `client/entity/model/ZombieGeoModel.java`）+ `checkZombieSpawn`（1.20 `:308`）+ `ZOMBIE` 注册（`:47`）→ 僵尸族 3/3 齐。
2. **水母族**：`JellyFishRenderer` + `EntityGlowingGeoLayer` + `registerJellyFish` + 2 支水谓词（3 个 id）。
3. **`registerAcceleratingLand`/`registerJumpingLand`/`registerCharger`/`registerSnatcher`** 四个注册辅助对应的族
   （`BaseWarriorMonster`/`JumpingWarriorMonster`/`ChargingMonster`/`Snatcher` 类都已在 1.21）。
