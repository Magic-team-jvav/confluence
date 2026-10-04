# 批次 22 · 动物刷怪放置层（`CreatureSpawnPlacements` 的小动物半边 + `SpawnPlacementChecks` 子集）

> 承接批次 21（`Gnome`）。这一批做的是任务书第五节 B：
> **`CreatureSpawnPlacements` 只做动物那一半**，口径按 `notes/WP5C-SUBSET.md` 第三节的方案 (1)。

## 一、为什么是「子集」而不是 `stage_batch` 整篇搬

1.20 的 `SpawnPlacementChecks` 是 **814 行 / 107 个方法**，它的单点闭包
**159 个文件**（`dep_subset` 实测，见 `notes/WP5C-SUBSET.md` 第三节）：因为文件里引用了
`MonsterEntities.*` 的一大批**成员**，而 1.21 的 `MonsterEntities` 还在增量生长。
`CreatureSpawnPlacements`（263 行）同理 —— 它的 `registerSlimes`/`registerPreHardmodeMonsters`/
`registerHardmodeMonsters` 三组全是怪物。

因此本批**按需摘取**（这正是 WP5C 记的方案 (1)），摘取范围写死为：

| 1.21 新文件 | 摘取的 1.20 部分 | 规模 |
|---|---|---|
| `common/entity/SpawnPlacementChecks` | `registerCritters` 直接用到的那几支谓词 + 它们的私有辅助 | 22 个方法（1.20 是 107 个） |
| `common/init/entity/CreatureSpawnPlacements` | 只有 `registerCritters(...)`（**12 处 `group` + 6 处直接 `event.register`**） | 1.20 的 5 个 register 方法里只落 1 个 |
| `common/event/ModEvents`（既有文件） | +2 行：`CreatureSpawnPlacements.register(event);` 接进既有的 `RegisterSpawnPlacementsEvent` 处理器 | — |

> 工具链说明：本批**没有**走 `stage_batch.py`，因为整篇转换会带进 159 文件闭包；
> 两个文件是「1.20 的子集」，所以用人工摘取 + 机械对照校验（见第三节）。
> `check_duplicates.py` 仍然照跑（过程纪律 5）。

## 二、落地清单

| 文件 | 非空行 | 说明 |
|---|---:|---|
| `common/entity/SpawnPlacementChecks`（新） | ≈190 | 小动物谓词子集；方法与 1.20 逐字一致，只有墓地判定换成 1.21 的 section 级实现 |
| `common/init/entity/CreatureSpawnPlacements`（新） | ≈85 | 只 `registerCritters`；`group(...)` 辅助改为原生 `SpawnPlacementType` + `DeferredHolder` 可变参数 |
| `common/event/ModEvents`（改） | +2 | 在 `registerSpawnReplacements` 里调用新注册中心（紧跟既有的 2 条 `event.register`） |

覆盖的小动物（38 个，全部来自 `CritterEntities`，1.21 侧已全部注册）：
金鱼、企鹅、发光蘑菇牛、发光咯咯菇/咯咯菇、萤火虫、闪电萤火虫、松露虫、云羊、兔子、
爆炸兔/敌对兔、鸟/蓝松鸦/红衣主教、松鼠/红松鼠、鸭子、发光蜗牛/蛆/蛆虫/蛞蝓、蜗牛、蝎子、
蚱蜢、螃蟹、宝石兔/宝石松鼠、蜻蜓、蝴蝶、臭虫、仙灵、Fealing、地狱蝴蝶/岩浆蜗牛、
瓢虫、七彩草蛉、虫子。

### 谓词清单（`SpawnPlacementChecks`）

`checkMobSpawnRules`、`checkAnimalSpawnRules`、`checkFairySpawn`、`checkSurfacePenguinSpawn`、
`checkWormSpawn`、`checkCavernCritterSpawn`、`checkSurfaceDayCritterSpawn`、`checkNetherDayCritterSpawn`、
`checkLadybugSpawn`、`isStinkbugDay`、`checkCalmDayCritterSpawn`、`checkButterflySpawn`、
`checkStinkbugSpawn`、`checkFireflySpawn`、`checkLightningBugSpawn`、`checkGlowBugEnvironment`、
`checkTruffleWormSpawn`、`checkPrismaticLacewingSpawn`、`checkSurfaceWaterMonsterSpawn`、
`hardmode`、`hasDeepWater`、`isGraveyard`。

## 三、校验结果（编译门是唯一权威，但对照校验也不能省）

### 3.1 移动 vs 新增审计（过程纪律 5）

```
python tools/port2native/check_duplicates.py --src120 <1.20> --ref . \
    --file org/confluence/mod/common/entity/SpawnPlacementChecks.java \
    --file org/confluence/mod/common/init/entity/CreatureSpawnPlacements.java
# 共 2 个待查文件，发现 0 处「疑似移动/重复」
```

但输出里有一条 **`DIFF`**，必须写明（政策要求）：

| 1.20 位置 | 1.21 侧同名 | 性质 |
|---|---|---|
| `org.confluence.mod.common.entity.SpawnPlacementChecks` | **TerraEntity** `org.confluence.terraentity.entity.util.SpawnPlacementChecks` | 不同 FQN、不同实现（TE 那份只有 `checkTEMonsterWithConfig`，吃 `ServerConfig.SPAWN_WITHOUT_LIGHT`，被 `TEMonsterEntities` 与 `mixin/integration/terraentity/SpawnPlacementChecksMixin` 使用）→ 属既定的「**TerraEntity 退役、实体内联进主模组**」过渡态（先加后删）。**主模组这一份是 1.20 新架构的对应物，不是 TE 那份的副本**；TE 那份随退役批次一起删 |

### 3.2 与 1.20 的机械对照（手写子集必须做）

`compare_checks.py`（临时脚本，口径：剥注释 → 取方法体 → 规范化空白 → 逐方法比）：

| 判定 | 数量 | 明细 |
|---|---:|---|
| **逐字一致** | 19 | 见第二节谓词清单去掉 `isGraveyard` 后的全部 |
| 只差一处（已声明的适配） | 2 | `checkCalmDayCritterSpawn`、`checkGlowBugEnvironment`：唯一差异是 `ModBlockCounters.isGraveyard(level, pos)` → `isGraveyard(level, pos)` |
| 1.20 没有（1.21 新增辅助） | 1 | `isGraveyard`（见下） |

`compare_placements.py`（同口径比 `registerCritters` 方法体，替换表只有
`PortRegisterSpawnPlacementsEvent→RegisterSpawnPlacementsEvent`、`PortSpawnPlacementTypes→SpawnPlacementTypes`）：
**归一化后逐字相同 = True**，`group(...)` 12 处、`event.register(...)` 6 处两侧一致。

### 3.3 编译门

```
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 总错误数: 0，涉及 0 个文件；[build] exit=0（本批一次过）
```

已核对 `.class` 真的落盘：`common/entity/SpawnPlacementChecks.class`、
`common/init/entity/CreatureSpawnPlacements.class`。

## 四、挖到的 API 差异 / 判定（都实测过）

1. **事件层**：1.20 的 `PortRegisterSpawnPlacementsEvent` + `PortSpawnPlacementTypes` →
   原生 `net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent` +
   `net.minecraft.world.entity.SpawnPlacementTypes`。**1.21 侧本来就有现成写法**
   （`ModEvents#registerSpawnReplacements` 里两条 `event.register(...)`），本批直接沿用同一处理器，
   没有另起一套事件监听。
2. **`SpawnPredicate` 签名两侧相同**：`boolean test(EntityType<T>, ServerLevelAccessor, MobSpawnType, BlockPos, RandomSource)`
   （`SpawnPlacements.java:181-182`）—— 所以 1.20 的方法签名可以逐字保留。
3. **`group(...)` 泛型**：1.20 用原始 `RegistryObject...`；1.21 对应物是
   `DeferredHolder<EntityType<?>, ? extends EntityType<? extends Mob>>...`（`@SafeVarargs`）。
   保留 1.20 的「只在统一注册边界用通配、调用点仍持有具体类型」的做法，注释里写明了理由。
4. **⚠️ 墓地判定必须换实现**（本批唯一的语义映射决策）：
   - 1.20：`ModBlockCounters.isGraveyard(level, pos)` —— magiclib `BlockCounters` +
     `MiniBiome.windowCounts` 的**窗口统计**，阈值 `GRAVEYARD_THRESHOLD = 7`；
   - 1.21：没有这套计数器，取而代之的是**区块 section 级** `ILevelChunkSection#confluence$isGraveyard()`
     （`util/BlockCounts.java:19`：`tomb - sunflower >= 7`，**阈值同值**），取 section 走
     `DynamicBiomeUtils.getISection(level, pos)`（写法照 `LivingEntityEvents.java:512`）；
   - 差异只在**粒度**（section vs 窗口），方向一致。这是 1.21 分支自己选定的架构，
     本批按「1.21 原生形态」落地并在代码注释里留痕。
5. 其余依赖（`OverworldUtils.getSurfaceY/getUndergroundY/getSpaceY/isHallow`、`ConfluenceData.getWindSpeedX/Z`、
   `KillBoard.INSTANCE.getGamePhase()` + `GamePhase.PLANTERA`、`LibDateUtils._19$30/_00$00/isWithinDayTime`、
   `ModBiomes.GLOWING_MUSHROOM`、`IMinecraftServer.isHardmode`、`ModUtils.isRainingAt`、`Worm`）
   **1.21 侧全部已存在**，直接引用，无新增前置。

## 五、推迟了什么、为什么

| 推迟项 | 原因 |
|---|---|
| `CreatureSpawnPlacements` 的 `registerSlimes` / `registerPreHardmodeMonsters` / `registerHardmodeMonsters` | 引用 `MonsterEntities.*` 的成员，随怪物批次（也是 159 文件闭包的主体） |
| `SpawnPlacementChecks` 的怪物谓词（史莱姆、地表/地下怪物、沙漠、地牢、水中敌怪、困难模式…，约 85 个方法） | 同上；且其中一些依赖 `ModBlockCounters` 的其它计数器（花岗岩洞/大理石洞/蜘蛛巢/陨石/微光…），1.21 用别的机制表达，需要单独一批裁决 |
| `checkMysticFrogSpawn` | 依赖 `NpcEntities.MYSTIC_SLIME` + `NPCSpawner`（1.21 侧尚无 `NPCSpawner`）→ 随 WP4 NPC 基座 |
| 小动物的**第三方生成修饰器**（生物群系权重 JSON） | 属 WP7 数据生成批次；本批只保证「放置规则」这一层 |

**行为变化**：这批让 38 个小动物在服务端装上了真实的放置规则（此前它们要么吃原版默认规则、
要么完全不受模组条件约束）——即 `Operation.REPLACE` 明确覆盖默认规则。
