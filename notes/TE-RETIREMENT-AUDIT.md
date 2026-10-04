# TerraEntity 退役可行性盘点（2026-09-29 第 22 轮实测）

> 触发问题：「R1–R4 会删掉 TerraEntity 子模块吗？」——**不会**，R1–R4 只补主模组
> `org/confluence/mod/**` 下还缺的 mod 侧文件，提示词里明确禁止触碰任何子模块目录。
> 本文是把「TE 退役」这件事的账算清楚，供后续单独立项。
>
> **执行顺序（用户 2026-09-29 定）**：R1–R4 收尾 → **TE 退役** → 冒烟验收。

## 零、逐条施工单（81 个被引用的 TE 类三分）

实测：主模组共引用 **81 种** TE 类。按「改指难度」分成三档：

### A. 主模组已有**同名**类——纯 import 改指（23 种 / 36 条引用）

`GeoNegativeVolumeRenderer`(3)、`ITrackType`(3)、`WallOfFlesh`(3)、`WoodenMimic`(2)、
`BaseWormPart`(2)、`DemonEye`(2)、`BasisTrack`(2)、`ILeftClickStateItem`(2)、`IVariant`(1)、
`GeoNormalRenderer`(1)、`BrainOfCthulhu`(1)、`LittleHornet`(1)、`QueenBee`(1)、`EaterOfWorlds`(1)、
`AnglerNPC`(1)、`SimpleTrack`(1)、`GoldenSlime`(1)、`BaseSlime`(1)、`HillOfFlesh`(1)、
`DungeonGuardian`(1)、`NPCMood`(1)、`WallOfFleshRenderer`(1)、`IPlayer`(1)

### B. **改名**改指（TE 注册表 → 主模组注册表，16 种 / 107 条引用）

| TE 类 | 次数 | 主模组目标 |
|---|---:|---|
| `TEMonsterEntities` | 22 | `MonsterEntities` |
| `TEBossEntities` | 13 | `BossEntities` |
| `TEAnimals` | 13 | `CritterEntities` |
| `TETags` | 9 | `ModTags`（成员级需逐条核对） |
| `TEEffects` | 8 | `ModEffects`（成员级需逐条核对） |
| `TENpcEntities` | 7 | `NpcEntities` |
| `TEBoomerangItems` | 7 | `BoomerangItems` |
| `TEYoyosItems` | 6 | `YoyoItems` |
| `TESounds` | 6 | `ModSoundEvents`（成员级需逐条核对） |
| `TEItems` | 3 | `ModItems` |
| `TEProjectileEntities` | 2 | `ModEntities` |
| `TEWhipItems` | 2 | `WhipItems` |
| `TEArmors` | 1 | `ArmorItems` |
| `TEEntities` | 1 | `ModEntities` |
| `TESpawnEggItems` | 1 | `SpawnEggItems` |
| `TEEnchantments` | 1 | `ModEnchantments` |

A + B 合计 **39 种 / 143 条引用（约 70%）** 属于机械改指。

### C. 主模组无对应物（42 种 / 63 条引用，TE 源码 ≈ 6500 行）

其中**多数只是"名字不同"**（主模组已有等价物，需按语义改指）：
`AbstractTerraNPC`(845 行) → `BaseNPC`、`AbstractMonster`(245) → `BaseMonster`、
`AbstractTerraBossBase`(601) → `BaseBoss`、`IMinion`/`ISummonMob`/`AbstractSummonMob` →
WP5 召唤体系自研基类、`ITradeHolder`/`ITradeLock` → 原生交易层、
`IHouseDetector` → `HouseValidater`、`IPlayer`/`IZombie` → 主模组 mixed 接口、
`SimpleVariantAnimal` → 主模组动物体系、`TEFigureBlocks`/`TEPetItems`/`TESummonItems` → 对应原生注册表。

**真正需要新搬/新写的**（TE 独有）：
`TEUtils`(1097 行，9 处引用；其中不少是 `LibEntityUtils`/`LibMathUtils` 的包装，需逐方法判定)、
`DebugBlocksHelper`(118)、`AbstractBufferManager`(110)、`GeoWormRenderer`(133)、
`BaseEntityRenderer`(59)、`TETradeScreen`(377；1.21 已有自有 NPC 交易界面，需确认是否还要)、
`NPCEvent`(269)、`KeyframeAnimation`(151；`NPCReforgeScreen` 在用)、`CuriosHelper`(58)、
`RecipeDrawerUtils`(17)、`TerraEntity`(61，只有 MODID 常量)、`DemonEyeVariant`(76)、
`IEffectStrategy`(94，1.21 的效果策略体系已改由主模组 effect 组件承担)、
`IAttackableProjectile`/`ICollisionAttackEntity`(41/113)、`TEEnchantmentHelper`(41) 等。

## 三、结论与建议路径


## 一、现状（数字）

| 项 | 数值 |
|---|---:|
| 主模组引用 `org.confluence.terraentity.*` 的文件 | **105**（主模组共 2563 个 java，约 4%） |
| 引用语句总数 | **206** |
| TerraEntity 子模块自身规模 | **860 个 java / 83682 行** |
| 1.20 侧 | **完全没有 TerraEntity 子模块**（mod 全原生）——所以「对齐 1.20」的终点就是它退场 |

## 二、引用集中在哪（Top）

| 引用 | 次数 | 主模组是否已有原生替代 |
|---|---:|---|
| `TEMonsterEntities` | 22 | ✅ `MonsterEntities`（213 条，已全量落地） |
| `TEBossEntities` | 13 | ✅ `BossEntities`（31 条） |
| `TEAnimals` | 13 | ✅ `CritterEntities`（41 条） |
| `TETags` | 9 | ⚠️ 部分（`ModTags` 已有对应项，需逐条核对） |
| `TEUtils` | 9 | ⚠️ 需把工具方法搬进主模组（`LibEntityUtils`/`TEUtils` 一族） |
| `TEEffects` | 8 | ⚠️ 部分（`ModEffects` 已补 `SCARED/THE_TONGUE/HORRIFIED/CRIMSON_STORM`，其余待核） |
| `TENpcEntities` | 7 | ✅ `NpcEntities`（36 条） |
| `TEBoomerangItems` | 7 | ✅ `BoomerangItems` |
| `TESummonItems` | 7 | 🔄 R3 会补 `SummonItems` |
| `TEYoyosItems` | 6 | ✅ `YoyoItems`（WP6-A 已落地） |
| `TESounds` | 6 | ❌ 需把音效条目搬进 `ModSoundEvents` |
| `GeoNegativeVolumeRenderer` | 3 | ❌ 渲染器类，需主模组自带一份 |
| `ITradeHolder` / `ITradeLock` | 3+ | ✅ 原生交易层已有对应物 |
| `IMinion` / `ISummonMob` | 3+ | ✅ 主模组召唤体系已落地（WP5） |
| `TEDataComponentTypes` | 2+ | ⚠️ 需把数据组件搬进 `ModDataComponentTypes` |
| `WallOfFlesh`/`WoodenMimic`/`DemonEye`/`BaseWorm`/`AbstractMonster` 等 TE 实体基类 | 各 1~3 | ⚠️ 主模组已有自研对应物（WP2/WP3 落地），需逐条改指 |

按 TE 包聚合：`init.entity` 57、`init` 27、`init.item` 25、`entity.monster` 11、`api.entity` 11、
`utils` 11，其余零散。

引用最密集的 5 个主模组文件：`common/data/gen/loot/EntitySubProvider`（9）、
`common/data/gen/ModClientBestiaryEntryProvider`（8）、`common/event/game/entity/LivingEntityEvents`（7）、
`common/data/gen/tag/ModItemTagsProvider`（6）、`common/init/ModTabs`（5）——集中在 datagen 与事件层。

## 三、结论与建议路径

1. **本会话已经把 TE 大面积"掏空"**：Boss（19 个）、武器三层（箭/剑/弓弩/连枷）、鞭子与悠悠球、
   召唤体系、刷怪蛋、NPC 商店数据、交易层、区域/房屋——都已换成主模组原生实现。
   因此剩余 206 条引用里，**多数是「改指」而不是「补实现」**。
2. **真正的硬骨头**只有四类：`TESounds`/`TEEffects` 的剩余条目、`TEUtils` 一族工具方法、
   `GeoNegativeVolumeRenderer` 之类 TE 独有渲染器、以及 TE 实体基类（`AbstractMonster`/`BaseWorm` 等）
   的逐条改指（主模组已有自研基类）。
3. **建议**：等 R1–R4 与冒烟验收之后再单独立项，按「先改指可替代的（约占 70%）→ 再补四类硬骨头
   → 最后摘 gitlink + `settings.gradle` + 资源引用」三步走。放在冒烟之前做的话，会把冒烟清单
   整体作废一次（105 个文件的引用面变了），不划算。
4. 摘 gitlink 的直接影响面：`settings.gradle`/`gradle.properties`、主模组 105 个文件、
   子模块 860 文件退场、以及 `assets/terra_entity/**`、`data/terra_entity/**`、
   各语言文件里的 `terra_entity.*` 键（`ModChineseProvider`/`ModEnglishProvider` 里有大量）。
