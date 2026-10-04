# WP2 僵尸族收口（3/3）+ `ZombieGeoModel`

> 编译门 `ConfluenceOtherworld` **0 错误 / 0 文件**（1 → 0：`LibDateUtils.isDay` 参数收窄）。
> 注册层第 5 批。上一批（`0b584993e`）只吃了两只孢子僵尸，本批把 `ZOMBIE` 补齐，僵尸族 3/3 齐。

## 一、本批内容（1 新 java + 4 改 + 3 资源）

| 文件 | 改动 |
|---|---|
| `client/entity/model/ZombieGeoModel`（新，32 行） | 1.20 同名文件逐字移植：十种 `Zombie.Variant` 与 geckolib 资源之间的映射，**实体同步变体身份、模型层查变体资源**，以后补独立美术不需要动渲染器或网络协议。1.21 侧**零适配**（基类 `VanillaZombieGeoModel` 上一批刚落地；`Zombie#getVariant` 与 `Variant#{modelPath,texturePath,animationPath}` 在 1.21 已存在，三者当前都指向共享的 `blood_zombie` 资源） |
| `common/init/entity/MonsterEntities` | +`ZOMBIE` 注册（1.20 `:47`，属性逐字：20/3/护甲韧性 1/4/穿甲 2/跟随 16/击退 0.5/抗性 0/移速 0.23） |
| `client/event/ModClientEvents` | +`ZOMBIE` 渲染器（1.20 `:879`：`VanillaHumanoidRenderer` + `ZombieGeoModel`） |
| `common/entity/SpawnPlacementChecks` | +`checkZombieSpawn`（1.20 `:308`：非白天 + 地表高度 + 统一敌怪规则） |
| `common/init/entity/CreatureSpawnPlacements` | +1 条 `group(..., checkZombieSpawn, ZOMBIE)`（1.20 `:95`） |
| 资源 3 | `blood_zombie` 的 geo + animation + texture（变体共享资源），与 1.20 逐字节相同 |

## 二、本批唯一的 API 差异

| 1.20 | 1.21.1 | 处理 |
|---|---|---|
| `LibDateUtils.isDay(LevelAccessor)` | `LibDateUtils.isDay(Level)`（参数**收窄**） | `ServerLevelAccessor` 不再是合法实参 → 改传 `level.getLevel()`（同一个 `ServerLevel`，取值等价）。**这一处只有编译门能发现** |

## 三、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --maxerrs 2000` | **0 错误 / 0 文件**（1 → 0） |
| 渲染器覆盖（僵尸族 3/3） | `ZOMBIE` / `SPORE_ZOMBIE` / `HAT_SPORE_ZOMBIE` 各 1 条 |
| 放置规则覆盖（僵尸族 3/3） | 同上各 1 条 |
| `check_duplicates.py` | 0 处「疑似移动/重复」 |
| 资源 | 3/3 与 1.20 同哈希 |
| `asset_gap.py` 复算 | 919 处引用 / 缺口仍 **18**，未新增 |
| 待游戏内验收 | 僵尸十变体的颜色/缩放差异、血月加速、只在地表夜晚生成 |

## 四、注册层进度

| 批次 | id 数 | `MonsterEntities` 成员数 |
|---|---:|---:|
| 普查起点 | — | 63 |
| 蝙蝠族 `b78cd7d8b` | 9 | 72 |
| 宝箱怪族 `a30777650` | 8 | 80 |
| 骨骼族 `c71cabef3` | 12 | 92 |
| 哥布林 + 孢子僵尸 `0b584993e` | 8 | 100 |
| **僵尸族收口（本批）** | **1** | **101** |
| 缺口 | — | 150 → **112** 待补 |

## 五、下一批候选（按「共享类是否就绪」排序）

1. **水母族**：`JellyFishRenderer` + `EntityGlowingGeoLayer` + `registerJellyFish` + 2 支水谓词（`checkWaterMonsterSpawn`/`checkUndergroundWaterMonsterSpawn`）→ 3 个 id。
2. **`registerAcceleratingLand` / `registerJumpingLand` / `registerCharger` / `registerSnatcher` 对应的族**：
   这四个辅助的宿主类（`BaseWarriorMonster` / `JumpingWarriorMonster` / `ChargingMonster` / `Snatcher`）**都已在 1.21**，
   只是注册语句没落地 —— 属于「零新类」批次，预计一次能吃掉十几个 id。
3. **`GeoNegativeVolumeRenderer` 迁入主模组**（现在只在 TerraEntity 子模块），顺手解开 `CURSED_SKULL` 等 5 处注册。
