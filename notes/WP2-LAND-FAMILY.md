# WP2 陆行族收口（8 个 id）：零新客户端类批次

> 编译门 `ConfluenceOtherworld` **0 错误 / 0 文件**（2 → 0：`NatureBlocks` 的包名是 `common.init.block`）。
> 注册层第 6 批。按上一轮笔记的「下一批候选 ②」执行：**宿主类是档案参数化的 `BaseWarriorMonster` / `FlyingFishMonster`，
> 没有专属实体类**，因此整批**不需要任何新 java 类**（只要补两个注册辅助 + 三支谓词）。

## 一、本批内容（4 改 + 21 资源，**无新 java**）

| 文件 | 改动 |
|---|---|
| `common/init/entity/MonsterEntities` | **+8 条注册** + **`registerFlyingFish`（1.20 `:850`）+ `registerAcceleratingLand`（`:904/908`）两个辅助**；属性/档案/尺寸/`developmentOnly` 全逐字照 1.20 |
| `client/event/ModClientEvents` | **+8 条渲染器注册**（1.20 `:690/691/692/794/829/848/862-865`），全部使用已在 1.21 的 `GeoNormalRenderer` / `VanillaHumanoidRenderer` |
| `common/entity/SpawnPlacementChecks` | **+5 支谓词**：`checkGhostSpawn`(`:683`)、`checkDoctorBonesSpawn`(`:365`)、`checkWeddingZombieSpawn`(`:688`)、`checkFlyingFishSpawn`(`:657`) + 复用已有的 `checkSurfaceMobSpawn` |
| `common/init/entity/CreatureSpawnPlacements` | +3 条 `group(...)`，另把 `BLOOD_ZOMBIE` 并入既有 `checkGroundSpawn` 组、`SNOW_FLINX` 并入困难模式洞穴组、`BASILISK` 新建「困难模式地下」组（1.20 `:109/110/150/128/138/177`） |
| 资源 21 | 7 个 id × (geo + animation + texture)，与 1.20 逐字节相同（`BLOOD_ZOMBIE` 复用僵尸批已拷的 `blood_zombie` 三件套） |

### 8 个 id 与它们的 1.20 出处

| 成员 | 1.20 | 档案 / 备注 |
|---|---|---|
| `DOCTOR_BONES` | `:244` | `developmentOnly`，夜晚 + 丛林草（`checkDoctorBonesSpawn`） |
| `SNOW_FLINX` | `:278` | 困难模式冰雪洞穴层 |
| `BASILISK` | `:328` | `developmentOnly`，困难模式地下（1.20 与 `MOSS_HORNET`/`JUNGLE_CREEPER` 同组，后两者未注册） |
| `BLOOD_ZOMBIE` | `:627` | 加速度型（`registerAcceleratingLand`），资源与 `Zombie` 变体共享 `blood_zombie` |
| `THE_GROOM` / `THE_BRIDE` | `:634/636` | `developmentOnly`，血月地表或 1/5 概率墓地 |
| `FLYING_FISH` | `:665` | 只在雨天生成（`registerFlyingFish` + `PursuitProfile`） |
| `ANTLION_LARVA` | `:318` | `developmentOnly`；**1.20 侧没有放置规则**（由蚁狮召唤）→ 照实只注册不虚构规则 |

## 二、本批纠正的一个判断错误（值得记录）

我最初按名字判断 `SnowFlinx`/`Basilisk`/`TheGroom`/`TheBride`/`BloodZombie` 是「类缺失」，
核对注册行才发现**这些只是注册项名**：1.20 用 `registerLand(...)` / `registerAcceleratingLand(...)`
配置出 `BaseWarriorMonster`，**类文件本来就不存在**。
→ **教训：判「类是否缺失」必须看注册表达式里的构造器（`X::new` / `new X(...)`），不能看注册项名。**

## 三、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --maxerrs 2000` | **0 错误 / 0 文件**（2 → 0） |
| 渲染器覆盖 | **8/8** |
| 放置规则覆盖 | **7/8**（`ANTLION_LARVA` 按 1.20 本来就没有，已在代码注释与本节写明） |
| 资源 | 21/21 与 1.20 同哈希 |
| `asset_gap.py` 复算 | 919 处引用 / 缺口仍 **18**，未新增 |
| 待游戏内验收 | 骷髅博士的丛林草判定、血月婚礼组、飞鱼只在雨天、血僵尸的加速追猎、蛇蜥的穿甲与击退抗性 |

## 四、注册层进度

| 批次 | id 数 | `MonsterEntities` 成员数 |
|---|---:|---:|
| 普查起点 | — | 63 |
| 蝙蝠 / 宝箱怪 / 骨骼 / 哥布林+孢子僵尸 / 僵尸族收口 | 38 | 101 |
| **陆行族收口（本批）** | **8** | **109** |
| 缺口 | — | 150 → **104** 待补 |

## 五、下一批候选（按剩余成本排序）

1. **`ContactHumanoidGeoModel` 族（10 个 id）**：`DARK_LAMIA`/`LIGHT_LAMIA`/`GHOUL`/`TAINTED_GHOUL`/`VILE_GHOUL`/
   `DREAMER_GHOUL`/`MUMMY`/`DARK_MUMMY`/`BLOOD_MUMMY`/`LIGHT_MUMMY` —— 只差**一个模型类**
   （`ContactHumanoidGeoModel`）+ `registerJumpingLand` 辅助 + 4~5 支木乃伊/食尸鬼谓词，属于「一个类换 10 个 id」。
2. **水母族（3 个 id）**：`JellyFishRenderer` + `EntityGlowingGeoLayer` + `registerJellyFish` + 2 支水谓词。
3. **`GeoNegativeVolumeRenderer` 迁入主模组**（现只在 TerraEntity），可解开 `CURSED_SKULL` 等 5 处注册。
4. **食人花族（3 个 id）**：`SnatcherRenderer` 一个类即可（`SNATCHER`/`MAN_EATER` 共用）。
