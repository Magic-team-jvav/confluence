# WP2 宝箱怪族（8 个 id）：注册层第二批

> 编译门 `ConfluenceOtherworld` **0 错误 / 0 文件**（一次通过）。
> 承接 `notes/WP2-MONSTER-REGISTRY-GAP.md` 第七节的「候选 1」，是该普查的第二个注册层批次。

## 一、本批内容（2 改 + 24 资源，无新 java）

| 文件 | 改动 |
|---|---|
| `common/init/entity/MonsterEntities` | **+8 条注册**：小型 `WoodenMimic` 四只（`WOODEN_MIMIC`:58 / `GOLDEN_MIMIC`:139 / `ICE_MIMIC`:302 / `SHADOW_MIMIC`:615，260 血）与大型 `BaseMimic` 四只（`JUNGLE_MIMIC`:254 / `CORRUPT_MIMIC`:413 / `CRIMSON_MIMIC`:449 / `HALLOWED_MIMIC`:483，1820 血 + 三段战斗状态 `DEFENDING`/`RISING`/`SLAMMING`），属性与状态参数逐字照 1.20 |
| `client/event/ModClientEvents` | **+8 条渲染器注册**（1.20 `:905-912`）：全部 `GeoNormalRenderer<>(c, id)`，大型四只加 `.withScale(2.0F)` |
| `common/entity/SpawnPlacementChecks` | **+1 支谓词** `checkBelowSurfaceMonsterSpawn`（1.20 `:609`：低于地表 + 不露天 + 基础敌怪规则） |
| `common/init/entity/CreatureSpawnPlacements` | **+4 条 `group(...)`**（1.20 `:220/:233-236/:240`）+ 把 `ICE_MIMIC` 并入已有的「困难模式 + 洞穴层」组（1.20 `:238`，与 `GIANT_BAT` 的 `:223` 谓词相同 → 按谓词合并并注释出处） |
| 资源 24 | 8 × (geo + animation + texture)，与 1.20 逐字节相同 |

## 二、本批没踩坑的两个原因（写下来供后面复用）

1. **类与预算都已备齐**：`WoodenMimic` / `BaseMimic` / `BaseMimic.CombatState{JUMPING,DEFENDING,RISING,SLAMMING}`
   与 `CreatureAttributeBuilder.state(Enum,Consumer)`/`duration(int)`/`chargeSpeed(double)` 在 1.21 都存在
   → 注册语句可以**逐字照搬**（只改 `RegistryObject`→`DeferredHolder`、`LibAttributes.getArmorPenetration().get()`→`getArmorPenetration()`）。
2. **放置谓词只差 1 支**：1.20 宝箱怪用的 `checkGroundSpawn` / `checkCaveMonsterSpawn` / `checkNetherMonsterSpawn`
   在蝙蝠批已经落地，本批只补 `checkBelowSurfaceMonsterSpawn`。

## 三、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --maxerrs 2000` | **0 错误 / 0 文件**（一次通过） |
| 渲染器覆盖 | 8/8（脚本逐条计数 = 1） |
| 放置规则覆盖 | 8/8（脚本逐条计数 = 1） |
| 资源哈希 | 24/24 与 1.20 相同 |
| `asset_gap.py` 复算 | 914 处引用 / 缺口仍 **18**（= B 类 6 + C 类 12），本批未新增 |
| 待游戏内验收 | 地表/地下/冰雪/地狱的木质与黄金宝箱怪；四种大型宝箱怪（腐化/猩红/神圣/丛林）的 2 倍体型与三段攻击；暗影宝箱怪只在困难模式地狱 |

## 四、注册层进度

| 批次 | id 数 | `MonsterEntities` 成员数 |
|---|---:|---:|
| 起始（普查时） | — | 63 |
| 蝙蝠族（`b78cd7d8b`） | 9 | 72 |
| 宝箱怪族（本批） | 8 | **80** |
| 普查缺口 | — | 150 → **133** 待补（其中 A 类「一句话注册」已扣掉 17 个） |

## 五、下一批

`notes/WP2-MONSTER-REGISTRY-GAP.md` 第八节「候选 2 水母族」：3 个 id + 1 个新客户端类 `JellyFishRenderer`
+ `registerJellyFish(...)`/`jellyfishModel(...)` 两个辅助 + 2 支水谓词（`checkWaterMonsterSpawn` /
`checkUndergroundWaterMonsterSpawn`）；资源为**三色共用**一套 `jellyfish.geo/animation` + 3 张贴图。
若想继续保持「零新类」节奏，可改吃**骷髅族**（`ARMORED_SKELETON`/`DECAYEDER`/`DOCTOR_BONES`/`BASE_BONES`/
`ANGER_BONES`/`SHORT_BONES`/`BIG_BONES`/`BIG_ANGER_BONES`/`BIG_MUSCLE_ANGER_BONES`/`BIG_HELMET_ANGER_BONES`/
`CURSED_SKULL`/`BONE_LEE`，12 个 id）—— 但需先核它们用的谓词（多在地牢/洞穴段）。
