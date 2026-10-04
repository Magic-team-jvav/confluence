# WP5 批次 B（AttachmentEntity 核心）· **第 1 次尝试：整体推迟**，逐文件错误图已查实

> 目标：把 WP5 的核心 —— `dep_subset` 报的 **28 文件 / 2374 非空行**（`AttachmentEntityData`
> 单点闭包）一次落下去。实际动手后编译门报 **35 处错误、涉及 12 个文件**，其中 **5 个根因需要先补**，
> 且这些文件**互相咬死**（`SummonerAttachmentTypes` 被 7 个文件引用、两个 payload 被 3 个文件引用、
> `SummonerRegistries` 被 `AttachmentEntityData` 引用、`AttachmentEntity` 被一大片引用），
> **切不出一个还能编译的子集**。
> 按纪律（「收不了口的文件从工作树移除并写回滚」「宁可推迟并写清原因」），把这 28 个文件
> **全部从工作树移除**，只把其中**可以安全前置**的两块单独落地（见第四节：Magic-Lib `VEC_3` +
> `Immunity.isActive/apply`），其余留给下一批按本文清单开工。工作树与编译状态回到 0 错误。
>
> **两轮进展**：第 1 轮（`dd890f61c`）= 查实错误图 + 落 `VEC_3`；第 2 轮 = 落 `Immunity.isActive/apply`
> 并把 `ModPrefix.Summon` 从「补个 record」改判为 **WP6 设计任务**（证据见第四节）。
> 现在开工清单里**只剩 3 步**（第五节 2/3/4）。
>
> ✅ **第 3 轮（2026-09-27）：28 个文件已全部落地，提交 `fe98072a3`，编译门 0 错误。**
> 本文第五节那 3 步（注册表改造 / 网络层重写 / 零散 API）都已按本文清单做完；
> 唯一偏离清单的是 `ModPrefix.Summon`：选择了「等价判定顶替」（`WhipMarkTracker.tagDamageOf` 返回 0，
> 与 1.20 那个分支在 Summon 组落地前恒假完全一致），**不是**推迟整个 `WhipMarkTracker`。
> 落地记录、实测的 API 细节与推迟清单见 **`notes/WP5D-SUBSET.md`**。

## 一、开工测量（这部分是准的，下一批直接复用）

```powershell
python tools/port2native/dep_subset.py --seed org.confluence.mod.common.summoner.attachment.AttachmentEntityData ...
# {"candidates": 28, "kept": 28, "new": 28, "removed": 0, "unused_defer": 0}
python tools/port2native/seed_closures.py --seed-dir common/summoner ... --small 12
# 该种子闭包 28 文件 / 2374 非空行（notes/WP5A-CLOSURES.md 的「AttachmentEntity 核心」）
```

28 个文件（FQN 见文末附录），`stage_batch --convert --apply` 一次通过：
`uncovered.json=[]`、`filesChanged=15`、`leftovers.txt` 4 行（全是 PortLib 注册/网络词汇）。

## 二、35 处错误的 5 个根因（下一批的开工清单）

### 根因 1 · 两个网络 payload：`CustomPacketPayload.S2C` 在 1.21 **不存在**

涉及 `network/SummonerBatchedInfoPayload`（7 处）、`network/SummonerBatchedParticlesPayload`（5 处）。

- 1.20 写 `implements IPortPacket.S2C`，转换器把它映射成 `implements CustomPacketPayload.S2C`
  —— 而 1.21.1 的 `CustomPacketPayload`（`CustomPacketPayload.java:13`）**是一个扁平接口**，
  **没有** `S2C`/`C2S` 这两个嵌套接口（方向区分已删）。于是 `@Override identifier()` / `work(Player)`
  全部报「does not override」。
- 1.21 的写法（已核对源码）：
  - `CustomPacketPayload.Type<? extends CustomPacketPayload> type();`（:14）
  - `static <T> Type<T> createType(String id)`（:20，内部走 `ResourceLocation.withDefaultNamespace`，
    所以要传带命名空间的 `"confluence:xxx"`）
  - 客户端处理**不在 payload 里**，而是 `RegisterPayloadHandlersEvent` 里按 `Type` 注册 handler。
- 老的粒子序列化也一起失效：`ParticleOptions#writeToNetwork` / `ParticleType#getDeserializer().fromNetwork`
  在 1.21 都没了（`SummonerBatchedParticlesPayload.java:48/59`）—— 要改成从
  `ParticleType#streamCodec()`（或 1.21 的 `ParticleTypes` 流编解码）走。

### 根因 2 · 两个自定义注册表：`PortCustomRegistration` → `DeferredRegister`

涉及 `register/SummonerRegistries`（4 处）、`register/SummonerAttachmentTypes`（6 处）。

- 好消息：**`DeferredRegister#makeRegistry(Consumer<RegistryBuilder<T>>)` 在 NeoForge 21.1.219 仍然存在**
  （`DeferredRegister.java:259`），所以自定义注册表是直接的：
  `DeferredRegister.create(REGISTRY_KEY, Confluence.MODID)` + `makeRegistry(b -> b.sync(true))`。
- `TYPES.registerSimple(name, supplier)` → `TYPES.register(name, supplier)`。
- `PortCustomRegistration#get(ResourceLocation)`（`AttachmentEntityData.java:238`）没有同名对应物，
  要落到 `registryAccess().registryOrThrow(KEY).get(id)`（那里的 `level` 就在手边）。
- 两个注册表都要在 `Confluence` 构造器里 `register(eventBus)`
  （附件类型那一份要和既有的 `ModAttachmentTypes.TYPES` 并列，别另起一套）。

### 根因 3 · 共享类的**成员**缺失（1.21 侧那份更旧/更窄）

| 缺的成员 | 使用点 | 1.21 现状 |
|---|---|---|
| `Immunity.isActive(Immunity, LivingEntity)` / `Immunity.apply(Immunity, DamageSource, LivingEntity)` | `AttachmentEntity.java:102/114` | 1.21 的 `mixed/Immunity.java` **只有** `getCause`/`calculateInvTicks`/`tick`/`Type`——1.20 侧那两个 static（1.20 `mixed/Immunity.java:70/78`）没搬过来。**要么按 1.20 补进 1.21 的 `Immunity`（注意它同时被 1.21 既有代码使用，要先 diff 现有用法），要么把那两个 static 提到 WP5 自己的一侧** |
| `ModPrefix.Summon`（record） | `WhipMarkTracker.java:83` | 1.21 的 `ModPrefix` 里没有 `Summon` 前缀（1.20 有 `record Summon(String name, float attackDamage, float armorPenetration, …)`）。属 WP6「词缀」层，**要先决定它落在哪一批** |
| `LibStreamCodecUtils.VEC_3` | `LyraStreamCodecs.java:15`、`SummonerBatchedInfoPayload.java:47/48/57/58` | ✅ **本批已补**（Magic-Lib `95133c3`，见第四节） |

### 根因 4 · `RegistryObject` 没有对应物（老问题第 N 次）

`AttachmentEntity.java:32/59`、`Minion.java:24` 三处
`RegistryObject<? extends AttachmentEntityType<?>>`。NeoForge 无 `RegistryObject`；按既有手册
（转换器 manual 条目已写明）应改成 `Holder<AttachmentEntityType<?>>` 或 `Supplier<…>`，
**并且与根因 2 的注册表改造一起做**（改造后 `SummonerRegistries` 的条目类型本来就是 `DeferredHolder`）。

### 根因 5 · 其余零散的 1.21 API 差异（都是小改，但必须一起收）

| 文件:行 | 1.20 写法 | 1.21 写法 |
|---|---|---|
| `AttachmentEntityData.java:229` | `new RegistryFriendlyByteBuf(..., PortConnectionType.MODDED)` | `ConnectionType.NEOFORGE`（`ConnectionType` 只有 `NEOFORGE`/`OTHER`） |
| `InfoData.java:86`、`SummonerParticleData.java:30` | `PacketDistributor.sendToPlayersInDimension(level.dimension(), payload)` | 形参是 **`ServerLevel`**（`PacketDistributor.java:57`），要传服务端 level |
| `InfoData.java:39` | `ClientConfigs.healIndicator.isParticle()` | 1.21 的 `ClientConfigs.healIndicator` 是 **boolean**，不能再解引用 |
| `NumberInfo.java:57` | `consumer.vertex(matrix, x, y, z)` | 1.21 是 `addVertex(matrix, x, y, z)` |
| `LyraStreamCodecs.java:21` | `ByteBufCodecs.optional(ByteBufCodecs.UUID)` | 1.21 的 `ByteBufCodecs` 无 `UUID` 常量；用 `LibStreamCodecUtils.UUID`（Magic-Lib 已有）或 `ByteBufCodecs.optional` 配自定义 codec |

## 三、为什么不能「先落一半」

引用关系（`Get-ChildItem -Recurse | Select-String` 实测）：

| 被引用的文件 | 被本批几个文件引用 |
|---|---:|
| `register/SummonerAttachmentTypes` | 7（`AttachmentEntityData`、`InfoData`、`TargetCache`、`WhipMarkTracker`、`AttachmentEntity`、`ICarryMinion`、`SummonerParticleData`） |
| `network/SummonerBatchedParticlesPayload` | 3（`InfoData`、`SummonerParticleData`、自身） |
| `network/SummonerBatchedInfoPayload` | 2（`InfoData`、自身） |
| `register/SummonerRegistries` | 1（`AttachmentEntityData`） |
| `attachmentEntity/AttachmentEntity` | 一大片（`AttachmentEntityGoal`/`GoalSelector`/`IEntityCollision`/`TargetCache`/`Minion`/`WhipMarkTracker`/`AttachmentEntityDamageSource`…） |

12 个报错文件里 5 个是枢纽，**抽掉任何一个都会让依赖它的文件跟着报错**，所以没有「干净的轻量子集」
可留 —— 本轮只能整体推迟。

## 四、实际落地的两块前置补件（都不含半成品）

| 前置件 | 提交 | 内容 | 验证 |
|---|---|---|---|
| Magic-Lib `VEC_3` | 子模块 `95133c3`（根仓库 `dd890f61c` 记 gitlink） | `LibStreamCodecUtils` 补 `public static final StreamCodec<ByteBuf, Vec3> VEC_3`（写法与紧邻的 `VEC_2` 一致：`StreamCodec.composite(ByteBufCodecs.DOUBLE ×3, Vec3::new)`），对齐 1.20 分支同名常量（1.20 `LibStreamCodecUtils.java:40-44`） | `--module Confluence-Magic-Lib` 0 错误；主模块 0 错误 |
| `Immunity.isActive` / `Immunity.apply` | 根仓库（本批） | 按 1.20 原文补进 1.21 的 `mixed/Immunity`（1.20 侧 `mixed/Immunity.java:70/78`）。1.21 侧此前只有 `calculateInvTicks`/`tick` 那条路径，**没有**这两个 static，而 `AttachmentEntity.java:102/114` 正是调它们 | `--module ConfluenceOtherworld` 0 错误；**纯新增，不改既有 `calculateInvTicks` 行为** |

两处都属于本项目既有的「前置补件」做法（同 `LibDamageTypes.SUMMONER`：子模块/共享类先补，
根仓库提交信息里注明 hash）；都**没有消费点**，随 WP5 核心批次一起接入 —— 因此不算半成品。

### ⚠️ `ModPrefix.Summon` 已查实：**不是「补一条 record」，而是 WP6 的设计任务**

`WhipMarkTracker.java:83` 用了 `ModPrefix.Summon`，本以为是「1.21 少搬一个 record」，实测两侧
`ModPrefix` **API 形状不一样**：

| | 1.20 | 1.21 |
|---|---|---|
| record 方法 | `createComponent(PrefixType type, **ItemStack stack**)`（1.20 `ModPrefix.java:450-472`） | `createComponent(PrefixType prefixType)`（省略 stack） |
| 前缀组件构造 | `new PrefixComponent(type, name, modifiers, 0, 0)`（**5 参**） | `new PrefixComponent(prefixType, name, modifiers, manaCost, 0, **tier**, **value**)`（**7 参**） |
| 专属成员 | `getModifierId()` / `canBeMercy()` / `canBeMercy` 判据 `tier < 0` | 无 |
| Summon 组 | 有：`record Summon(String name, float attackDamage, float armorPenetration, float tagDamage, float knockBack, int tier, float value)` + `VALUES`/`ID` + **13 条**（FABLED/LOYAL/WORTHY/FOCUSED/EAGER/BALLISTIC/SCRAGGLING/PATIENT/RABID/ILL_TEMPERED/PETTY/FEEBLE/SKITTISH），属性打在 `LibAttributes.getSummonDamage()` 上 | **完全没有 Summon 组**（只有 Accessory/Universal/Common/Melee/Ranged/Magic） |

也就是说：要把 Summon 组搬进 1.21，先得**裁决 1.21 的 `ModPrefix`/`PrefixComponent` 用哪一版 API**，
再顺带处理 `ItemStack` 参数、`tier`/`value` 字段、13 条词缀的 lang 与数据 —— **属 WP6（词缀层）**，
不该塞进 WP5 的批次。**结论：`WhipMarkTracker` 随 WP6 的词缀批次一起落**（它只有 114 行，
且它依赖的 `SummonMarkInstance`/`SummonMarkType` 在 WP5 核心那 28 个文件里，到时一起）。

## 五、下一批开工顺序（照此做即可）

1. ~~补共享类成员~~ —— `Immunity.isActive/apply` **已完成**（见第四节）；
   `ModPrefix.Summon` **改判为 WP6 任务**（见上），因此本批落地时要**先解决 `WhipMarkTracker` 的去留**
   （建议：把 `WhipMarkTracker` 与 `TargetCache`/`AttachmentEntity` 里那 2 处引用一起留到词缀批次，
   或先用 `ModPrefix` 里已有的等价判定顶替 —— 二选一要在批次笔记里写明）。
2. **注册表改造**：`SummonerRegistries`（两个 `DeferredRegister.create(key, MODID)` + `makeRegistry(sync)`）、
   `SummonerAttachmentTypes`（`registerSimple` → `register`），并在 `Confluence` 构造器接线；
   同时把 `AttachmentEntity`/`Minion` 的 `RegistryObject` 改成 `Holder`（根因 4 与 2 一起做）。
3. **网络层重写**：两个 payload 改 `CustomPacketPayload`（`Type` + `StreamCodec`），
   客户端逻辑挪到 payload handler，并在 `ModEvents.registerPayloadHandlers` 注册；
   粒子序列化改用 `ParticleType#streamCodec()`。
4. **零散 API**：根因 5 那张表逐条改（都是一两行）。
5. 最后再跑编译门；此时 `stage_batch` 的 28 个文件应能一次过（`VEC_3` 与 `Immunity.*` 都已在）。


---

# 附 A：`dep_subset.py` 报告（28 个候选 = 本批目标集）

- 种子 1 个：`AttachmentEntityData`；扩张后候选 **28**（28 新增，0 被 `--defer` 剔除）
- `--alias` 3 条：`LibEntityUtils→LibUtils`、`common.data.GamePhase→common.data.saved.GamePhase`、`init.entity.ModEntities→init.ModEntities`

```
org.confluence.mod.client.summoner.LyraRenderTypes
org.confluence.mod.client.summoner.info.Info
org.confluence.mod.client.summoner.info.NumberInfo
org.confluence.mod.client.summoner.info.TextInfo
org.confluence.mod.common.summoner.LyraStreamCodecs
org.confluence.mod.common.summoner.attachment.AttachmentEntityData
org.confluence.mod.common.summoner.attachment.InfoData
org.confluence.mod.common.summoner.attachment.TargetCache
org.confluence.mod.common.summoner.attachment.WhipMarkTracker
org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity
org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityDamageSource
org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal
org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector
org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType
org.confluence.mod.common.summoner.attachmentEntity.IBlockCollision
org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision
org.confluence.mod.common.summoner.attachmentEntity.PlannedPath
org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher
org.confluence.mod.common.summoner.minion.ICarryMinion
org.confluence.mod.common.summoner.minion.Minion
org.confluence.mod.common.summoner.minion.MinionSlotType
org.confluence.mod.common.summoner.network.SummonerBatchedInfoPayload
org.confluence.mod.common.summoner.network.SummonerBatchedParticlesPayload
org.confluence.mod.common.summoner.particle.SummonerParticleData
org.confluence.mod.common.summoner.register.SummonerAttachmentTypes
org.confluence.mod.common.summoner.register.SummonerRegistries
org.confluence.mod.common.summoner.summonMark.SummonMarkInstance
org.confluence.mod.common.summoner.summonMark.SummonMarkType
```

# 附 B：第一轮编译门的 12 个报错文件（按错误数）

| 文件 | 错误数 | 主要根因 |
|---|---:|---|
| `network/SummonerBatchedInfoPayload` | 7 | 1、5 |
| `register/SummonerAttachmentTypes` | 6 | 2 |
| `network/SummonerBatchedParticlesPayload` | 5 | 1 |
| `attachmentEntity/AttachmentEntity` | 4 | 3、4 |
| `register/SummonerRegistries` | 4 | 2 |
| `attachment/InfoData` | 2 | 5 |
| `common/summoner/LyraStreamCodecs` | 2 | 3（VEC_3，已补）、5 |
| `minion/Minion` | 1 | 4 |
| `client/summoner/info/NumberInfo` | 1 | 5 |
| `attachment/AttachmentEntityData` | 1 | 5（ConnectionType） |
| `attachment/WhipMarkTracker` | 1 | 3（ModPrefix.Summon） |
| `particle/SummonerParticleData` | 1 | 5（PacketDistributor） |
