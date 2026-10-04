# WP5 批次 D（AttachmentEntity 核心 28 文件）· **已落地**

> 前情：`notes/WP5B-SUBSET.md`（第 1 次尝试 = 查实错误图后整体推迟；第 2 轮 = 落 `VEC_3` 与
> `Immunity.isActive/apply`，并把 `ModPrefix.Summon` 改判为 WP6 任务）。
> **本文是第 3 轮：5 个根因全部解决，28 个文件一次落地，编译门 0 错误。**
>
> 本批是 WP5（召唤与 AttachmentEntity 体系）的**核心**：`AttachmentEntityData` 单点闭包
> = 28 文件 / 2374 非空行。它之后，WP5 剩余的是「物种 + 注册条目 + 客户端渲染器 + tick 接线」。

## 一、开工三件事（都按纪律做完了）

| 步骤 | 命令 | 结果 |
|---|---|---|
| 1. 移动 vs 新增审计 | `check_duplicates.py --file …`（28 个文件全跑） | **28 个待查文件，0 处「疑似移动/重复」** |
| 2. 闭包重算 | `dep_subset.py --seed org.confluence.mod.common.summoner.attachment.AttachmentEntityData` | `{"candidates": 28, "kept": 28, "new": 28, "removed": 0, "already": 0, "unused_defer": 0}` —— 与 WP5B 完全一致 |
| 3. 暂存 + 转换 | `stage_batch.py --name wp5core … --convert --apply` | 28 个文件全部 `add`、**覆盖 0**；`uncovered.json=[]`；`filesWithPortLib=14`、`filesChanged=15` |

> ⚠️ **工具用法坑（本次踩到）**：`dep_subset.py` 的 `--seed` 是 **FQN**（`…attachment.AttachmentEntityData`）、
> `--root121` 是 **`ConfluenceOtherworld/src/main/java` 这样的源码根**。
> 我第一次按 `--seed <路径>` + `--root121 .` 跑，得到 **2147 个候选**（因为 `.` 不是源码根，
> 所有类型都被判成「1.21 缺失」，闭包退化成了整棵 1.20 依赖树）。两个参数的语义容易搞反，记在这里。

`leftovers.txt` 4 行（全是 PortLib 注册/网络词汇），**本批全部处置**：

| 位置 | 处置 |
|---|---|
| `SummonerRegistries:14/18` `PortRegisterHandler.custom` ×2 | `DeferredRegister.create(KEY, MODID)` + `makeRegistry(b -> b.sync(true))` |
| `SummonerAttachmentTypes:12` `PortRegisterHandler.attachment` | `DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID)` + `registerSimple → register` |
| `AttachmentEntityData:229` `PortConnectionType.MODDED` | `ConnectionType.NEOFORGE` |

## 二、五个根因的落地形态（WP5B 第二节清单的收口）

### 根因 1 · 网络层 → 仓库既有的 `IPacketS2C` 形态（不是裸 `CustomPacketPayload`）

WP5B 只写到「两个 payload 改 `CustomPacketPayload`」。实际做的时候发现 1.21 侧**已经有更贴合的原生形态**：
`org.confluence.lib.network.IPacketS2C`（Magic-Lib）—— `type()` + `work(Player)` + `handle(IPayloadContext)`，
正是 1.20 `IPortPacket.S2C` 的 1:1 对应物（`identifier()` → `type()`、`work(Player)` 原样保留）。

| 文件 | 落地 |
|---|---|
| `SummonerBatchedInfoPayload` | `implements IPacketS2C`；`ID: ResourceLocation` + `identifier()` → `TYPE = Confluence.createType("summoner_batched_info")` + `type()` |
| `SummonerBatchedParticlesPayload` | 同上（`confluence:summoner_batched_particles`）；粒子序列化见下 |
| `NetworkEvents.registerPayloadHandlers` | 两条 `.playToClient(TYPE, STREAM_CODEC, X::handle)` |

**粒子序列化**：1.20 手写 `options.writeToNetwork(buf)` + `type.getDeserializer().fromNetwork(type, buf)`；
1.21 原生是 `ParticleTypes.STREAM_CODEC`（`ParticleTypes.java:144`，
`ByteBufCodecs.registry(PARTICLE_TYPE).dispatch(ParticleOptions::getType, ParticleType::streamCodec)`）——
按注册名 dispatch 到 `ParticleType#streamCodec()`（`ParticleType.java:20`），**对模组粒子类型同样有效**
（`SummonerParticleTypes` 那套仍走它）。

> 附带修正：`ModEntities.java:45` 的 Fast Link 注释把发包指向 `ModEvents#registerPayloadHandlers`，
> 实际那个类不存在，真正的位置是 `NetworkEvents#registerPayloadHandlers` —— 本批一并改掉。

### 根因 2 + 4 · 自定义注册表与 `Holder`（一起做的）

```java
public static final DeferredRegister<AttachmentEntityType<? extends AttachmentEntity>> ATTACHMENT_ENTITY_TYPES
        = DeferredRegister.create(ATTACHMENT_ENTITY_TYPE_KEY, Confluence.MODID);
public static void register(IEventBus eventBus) {
    ATTACHMENT_ENTITY_TYPES.makeRegistry(builder -> builder.sync(true));   // 1.20: maker.sync(true)
    SUMMON_MARK_TYPES.makeRegistry(builder -> builder.sync(true));
    ATTACHMENT_ENTITY_TYPES.register(eventBus);
    SUMMON_MARK_TYPES.register(eventBus);
}
```

- `makeRegistry(Consumer<RegistryBuilder<T>>)` 在 NeoForge 21.1.219 仍在（`DeferredRegister.java:259`）；
  `sync(boolean)` 只表示「数值 id 同步给客户端」（`RegistryBuilder.java:92/122`），**不需要 codec**，与 1.20 语义一致。
- 接线在 `Confluence` 构造器（紧随 `ModAttachmentTypes.TYPES.register(eventBus)`），
  `SummonerAttachmentTypes.register(eventBus)` 与 `SummonerRegistries.register(eventBus)` 各一行。
  → **这是本批唯一的行为变化：两个自定义注册表从此刻起真的存在了**。
- `AttachmentEntity` / `Minion` 的字段与构造器参数：`RegistryObject<? extends AttachmentEntityType<?>>`
  → **`Holder<AttachmentEntityType<? extends AttachmentEntity>>`**（与 `DeferredRegister` 的 `R` 参数一致），
  取用处 `type.get()` → `type.value()`（`Holder` 没有 `get()`）。
- `PortCustomRegistration#get(ResourceLocation)`（`AttachmentEntityData:238`）没有对应物 →
  `level.registryAccess().registryOrThrow(SummonerRegistries.ATTACHMENT_ENTITY_TYPE_KEY).get(id)`。
- `PortAttachmentRegistration#registerSimple` → `register`，且链尾补 `.build()`
  （1.21 的 `AttachmentType.builder(...)` 返回 `Builder<T>`，`AttachmentType.java:294` 才是成品）。

### 根因 3 · 共享类成员

`VEC_3`（子模块 `95133c3`）与 `Immunity.isActive/apply`（`e3820ac55`）已在上一轮补好，本批直接消费。

**`ModPrefix.Summon` 的处置（二选一里选了「等价判定顶替」，理由如下）**：
`WhipMarkTracker:83` 是**本批唯一**的 `ModPrefix.Summon` 引用点。1.21 的 `ModPrefix` 没有 Summon 组
（只有 Accessory/Universal/Common/Melee/Ranged/Magic），因此在 Summon 组落地之前，
`prefix instanceof ModPrefix.Summon` 这个分支**永远不可能命中**。于是：

- **不**把 `WhipMarkTracker` 连同 `AttachmentEntity:105`、`TargetCache:159`、`SummonMarkType` 的引用一起推迟
  （那等于把「鞭痕标记 → 伤害加成」这条链从核心类里挖掉，留一个更难合并的洞）；
- 改为在 `WhipMarkTracker` 内加一个**行为等价**的私有方法，并在 javadoc 里写清替换条件：

```java
/// 1.20 侧这里写的是 `prefix instanceof ModPrefix.Summon summon -> summon.tagDamage()`。
/// 1.21 的 `ModPrefix` 没有 Summon 词缀组……在 Summon 组落地之前任何前缀都不可能命中那个分支，
/// 因此先返回 0（与「instanceof 恒假」在行为上完全一致）。WP6 词缀批次要把 Summon 组与
/// tagDamage 一起搬过来时，这个方法要改回真实实现。
private static float tagDamageOf(ModPrefix prefix) { return 0.0F; }
```

→ **WP6 的待办因此多一条明确清单项**：`ModPrefix` 的 Summon 组 + `PrefixComponent` 形状裁决 +
`WhipMarkTracker.tagDamageOf` 回填（详见 `notes/WP5B-SUBSET.md` 第四节）。

### 根因 5 · 零散 API（两处比 WP5B 记的更宽）

| 位置 | 1.20 | 1.21 实测 |
|---|---|---|
| `InfoData:86`、`SummonerParticleData:30` | `sendToPlayersInDimension(level.dimension(), payload)` | 形参是 **`ServerLevel`**（`PacketDistributor.java:57`）→ `if (level instanceof ServerLevel serverLevel)` 分支内发送 |
| `InfoData:39` | `(…? healIndicator : damageIndicator).isParticle()` | 1.21 两个配置都是 **boolean**（`ClientConfigs.java:64-65`，true = 显示数值）→ `PARTICLE` 等价于「开关关闭」，即 `if (!enabled) continue;`（注释已写在代码里） |
| `LyraStreamCodecs:21` | `ByteBufCodecs.optional(ByteBufCodecs.UUID)` | `ByteBufCodecs` 无 `UUID` → `LibStreamCodecUtils.UUID`（`LibStreamCodecUtils.java:51`） |
| `NumberInfo:57` | `consumer.vertex(matrix, x, y, z).color(…).uv(…).uv2(…).endVertex()` | **不只是 `vertex → addVertex`**：1.21 是 `addVertex(...)` + `setColor(int)` + `setUv(f,f)` + `setLight(int)`（`VertexConsumer.java:159/53/20/63`），`color/uv/uv2` 已改名、**`endVertex()` 已删除** |

## 三、验证（编译门是唯一权威）

| 轮次 | 结果 |
|---|---|
| 第 1 次 | **1 处错误**：`NumberInfo.java:58 cannot find symbol: method color(int)`（根因 2/4/5 与网络层在编译门之前已按清单手工改完，所以只剩这一处——不是「一次过」） |
| 修 `NumberInfo` 改用 `setColor/setUv/setLight` 后 | **总错误数 0**（`build_errors.py --module ConfluenceOtherworld --repo .`，exit=0） |

落盘核对（纪律：任何「编译通过」都必须先确认文件真的在树里）：
`git status --short` 显示 28 个新增 + 3 个既有文件改动；
`build/classes/java/main` 下 `AttachmentEntity`/`Minion`/`WhipMarkTracker`/`SummonerRegistries`/
`SummonerAttachmentTypes`/`SummonerBatchedInfoPayload`/`SummonerBatchedParticlesPayload`/`NumberInfo`
的 `.class` **各 1 个**。

**转换忠实度抽查**（`git diff --no-index --ignore-all-space`，1.20 ↔ 1.21）：
14 个逻辑最重的文件里 **11 个逐字相同**（`AttachmentEntityGoalSelector`/`SummonMarkType`/`PlannedPath`/
`IEntityCollision`/`AttachmentEntityDamageSource`/`AttachmentEntityGoal`/`SummonMarkInstance`/`ICarryMinion`/
`IBlockCollision`/`Info`/`TextInfo`），3 个只有 API 改名与 import 归位
（`SyncFieldDispatcher` 17 行：`PortRegistryFriendlyByteBuf → RegistryFriendlyByteBuf`、`PortStreamCodec → StreamCodec`；
`MinionSlotType` 2 行、`LyraRenderTypes` 2 行：同一类改名）→ **无语义漂移**。

## 四、来源提交（1.20 侧，按「最后改过该文件的提交」统计）

28 个文件对应 **11 个 1.20 提交**：

| 文件数 | 1.20 提交 | 说明 |
|---:|---|---|
| 5 | `116bef809` | 添加哨兵携带接口 |
| 5 | `936551676` | 完成新的伤害信息 |
| 4 | `f4ffadaa7` | **AttachmentEntity 体系迁移**（本子系统的来源提交，72 个新文件 / +10081 行） |
| 3 | `c0e8c4c75` | 添加多召唤标记叠加支持 |
| 3 | `e1cd9c8cb` | 添加配置文件，召唤词缀复制 |
| 2 | `64b7bbe9e` | 完成吸血鬼青蛙，史莱姆，小雪怪，铁傀儡 |
| 2 | `87b93ede2` | 合并 stream codec |
| 1 | `641467c87` | 迁移黄蜂召唤物至 AttachmentEntity |
| 1 | `af1426bed` | 完成召唤词缀 |
| 1 | `7572f2ebd` | 添加三个饰品 |
| 1 | `27ee0313c` | 无敌帧体系接入 |

（逐提交判定仍以 `notes/COMMIT-LAG.md` 为准；本表只说明**本批的代码事实来源**。）

## 五、本批**没有**落的部分（都属于 WP5 后续批次，不是半成品）

28 个文件本身是完整可编译的一层：它们定义了「附件实体基类 + 目标缓存 + 鞭痕标记 + 同步字段分发 +
召唤标记类型 + 两个 payload + 信息/粒子数据附件 + 自定义注册表」。
下列成员**不在**本闭包内，因此本批不含任何半成品（各自单独成批）：

1. **注册条目**：`SummonerAttachmentEntityTypes`（真正 `register` 附件实体类型的那些条目）、
   `SummonerSummonMarks`、`SummonerSoundEvents`；
2. **tick 接线**：`SummonerEvents`、`SummonerHelper`（`InfoData.tick` / `SummonerParticleData.tick` /
   `WhipMarkTracker.tick` 目前**无人调用**——与上一批的 `SummonerParticleTypes` 一样，是「先落基座、后接消费点」）；
3. **物种**：22 个 minion 实现 + 各自 goal（`minion/**`、`minion/goal/**`）、`MomentumAttachmentEntity`、
   4 个召唤 projectile、`ParticleHelper`；
4. **客户端渲染**：`client/summoner/**` 的渲染器与 trail 层；
5. **词缀**：`ModPrefix.Summon` 组与 `tagDamageOf` 回填（WP6）。
