# WP5 批次 C · 召唤体系粒子层（通用粒子 3 个）+ 「CreatureSpawnPlacements 被 159 文件闭包挡住」的实测

## 一、本批落地（3 个新文件 / 180 非空行 + 1 处接线）

| 文件 | 非空行 | 说明 |
|---|---:|---|
| `common/summoner/particle/GenericParticleOptions` | 102 | **手写**：1.20 `extends PortParticleOptions` → 1.21 原生「实现 `ParticleOptions` + `getType()`」，字段/CODEC/STREAM_CODEC/equals/hashCode 逐字照 1.20 |
| `common/summoner/particle/GenericParticleBuilder` | 78 | 转换器直出（纯 Java，只依赖 `java.util.Random`，`filesChanged=0`） |
| `common/summoner/register/SummonerParticleTypes` | 32 | **手写**：`PortRegisterHandler.particleType(...)` + `PortRegistryEntry` → `DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MODID)` + `ParticleType` 匿名子类 |
| `Confluence`（既有文件） | +2 | `SummonerParticleTypes.TYPES.register(eventBus);`（紧挨 `ModParticleTypes`） |

```powershell
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 总错误数: 0，涉及 0 个文件；[build] exit=0（本批一次过，无中间错误）
```

### 两处 API 改写的依据（都不是猜的）

1. **`PortParticleOptions` → 实现 `ParticleOptions`**：1.20 的 PortLib 父类把 `type`/`codec`/`streamCodec`
   塞进父类构造器；1.21.1 的原生形态是**自己实现 `ParticleOptions` 并覆写 `getType()`**。
   1.21 侧**已有同构先例**：`common/particle/DamageIndicatorOptions`（record + `getType()` 指回
   `ModParticleTypes`，同一份 `CODEC`/`STREAM_CODEC` 写法）。
2. **粒子类型注册**：1.21 的 `ParticleType` 是抽象类，`codec()`/`streamCodec()` 必须逐实例覆写；
   本类直接沿用既有 `common/init/ModParticleTypes` 的 `register(id, overrideLimiter, mapCodec, streamCodec)`
   辅助方法形状（逐字同构，便于以后合并或迁移）。

本批卸掉了 WP5 核心 5 个根因里的一个（`SummonerParticleTypes`/`GenericParticleOptions` 那一条），
`notes/WP5B-SUBSET.md` 第二节根因表里的该行现在可以划掉。

## 二、推迟（明确写清）

| 推迟项 | 原因 |
|---|---|
| 粒子的客户端渲染器（`ParticleProvider`）与 `particles/generic.json` 定义、贴图 | 属客户端批，与动物渲染器/方块模型同一条既有约定；本批只保证**类型注册与网络编解码**正确（缺 provider 时客户端不生成该粒子，不崩） |
| 其余召唤粒子 | 随召唤内容批次（WP5 核心 28 文件之后）一起补；本批只落 `generic` 一种，与 1.20 该批次的 `SummonerParticleTypes` 内容一一对应 |

## 三、⚠️ 实测结论：`CreatureSpawnPlacements`（目标里的第 2 步）**现在做不了**

```powershell
python tools/port2native/dep_subset.py --seed org.confluence.mod.common.entity.SpawnPlacementChecks ...
# {"candidates": 159, "kept": 159, "new": 159, "removed": 0, "unused_defer": 0}
```

- 1.20 的 `common/entity/SpawnPlacementChecks`（703 行）单点闭包就是 **159 个文件**
  —— 它引用 `MonsterEntities.*` 的**成员**（`BLUE_SLIME`、`DEMON_EYE`、`ZOMBIE`、各史莱姆…），
  而 1.21 侧这些成员大多还不存在（`MonsterEntities` 是增量长的），于是整条 156 文件大团被拉进来。
  这与 `notes/WP2S-MONSTER-CLOSURES.md` 量到的「怪物侧 18/20 是 156~157 文件闭包」是同一件事：
  **`CreatureSpawnPlacements` 与那些怪物共用同一个前置，不是独立批次**。
- 1.21 侧**有** `RegisterSpawnPlacementsEvent` 的现成写法（`ModEvents.java:282-290`：
  `event.register(type, SpawnPlacementType, Heightmap.Types, predicate, Operation.REPLACE)`），
  所以「放置层」本身没有 API 障碍，**纯粹被动物的缺失挡住**。
- **可行的两个口径**（下一批二选一，都要在批次笔记里写明）：
  1. **只做动物那一半**：把 1.20 `SpawnPlacementChecks` 里**动物用到的十几支谓词**
     （`checkAnimalSpawnRules`/`checkButterflySpawn`/`checkFireflySpawn`/…）按需摘出来，
     写进 1.21 的增量版，配 `CreatureSpawnPlacements` 的动物条目（`CritterEntities.*` 已全部就位）；
     这是「1.20 源码的子集」，要在笔记里逐条标注摘取范围。
  2. **等怪物批次**：先把 156 文件大团（WP4 NPC 基座 + 史莱姆簇 + boss 簇）推进，再整篇搬
     `SpawnPlacementChecks`。稳妥但慢。
  → 建议先做 (1)：动物的刷怪规则同理可先行，且 1.21 的动物实体已全部注册。

## 四、下一步（顺序不变，第 2 步的口径已澄清）

1. **WP5 核心 28 文件**（`notes/WP5B-SUBSET.md` 第五节）：前置已就位三块 —— `VEC_3`（子模块 `95133c3`）、
   `Immunity.isActive/apply`（`e3820ac55`）、**粒子层（本批）**；开工清单剩 3 步：
   ① 注册表改造（`DeferredRegister.create(key, MODID)` + `makeRegistry`，已核实 `DeferredRegister.java:259`）
   + `RegistryObject→Holder` + `Confluence` 接线；② 两个 payload 改 `CustomPacketPayload`（`createType` +
   `StreamCodec` + handler 注册）；③ 零散 API 五条（`ConnectionType.NEOFORGE`、`PacketDistributor` 要 `ServerLevel`、
   `ClientConfigs.healIndicator` 是 boolean、`vertex→addVertex`、`ByteBufCodecs.UUID` 不存在）。
   `WhipMarkTracker` 的 `ModPrefix.Summon` 依赖已改判给 WP6 词缀批次。
2. **`CreatureSpawnPlacements`**：按第三节口径 (1) 做动物半边。
3. **WP4 NPC 基座**（156 文件大团的核心）。
4. 枪械功能批次（消费 `notes/WP6G-SUBSET.md` 的词汇层）。
