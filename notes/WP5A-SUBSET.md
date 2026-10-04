# WP5 召唤体系（AttachmentEntity）批次 A · 几何/数据基座 4 个纯类型

> WP5 是 1.20 分叉后**最完整、而 1.21 零基础**的新架构（`common/summoner/**` 92 个 + `client/summoner/**` 49 个）。
> 本批只做它的**无依赖几何/数据基座**：4 个纯类型、零 PortLib、零注册接线，因此一次编译通过。
> 其余 5 个「看起来同样自包含」的叶子各有各的接线问题，逐个查实后**本批不落**（见第三节）。

## 一、本批落地（4 个新文件 / 233 非空行）

| 文件 | 非空行 | 内容 | 依赖 |
|---|---:|---|---|
| `common/summoner/attachmentEntity/OBB` | 62 | 有向包围盒（AABB + 四元数朝向） | 仅 `AABB`/`Vec3`/`org.joml` |
| `common/summoner/attachmentEntity/Ellipse` | 122 | 椭圆/摆动轨迹采样 | 仅 `Mth`/`RandomSource`/`Vec3`/`org.joml` |
| `common/summoner/attachmentEntity/PathNode` | 37 | `record PathNode(Vec3 pos, float yaw, float pitch, float roll)` | 仅 `Mth`/`Vec3` |
| `common/summoner/attachmentEntity/IMomentumAttachmentEntity` | 12 | 动量型附着体的速度/阻力/重力接口 | 仅 `Vec3` |

```powershell
python tools/port2native/stage_batch.py --name wp5a ... --file org/confluence/mod/common/summoner/attachmentEntity/{OBB,Ellipse,PathNode,IMomentumAttachmentEntity}.java --convert --apply
# 暂存 4 个文件 / 233 非空行；uncovered.json = []；leftovers.txt 0 行；filesChanged = 0（无 PortLib 词汇）
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 总错误数: 0，涉及 0 个文件；[build] exit=0
```

`notes/ALL-COMMON-CLOSURES.md` 的轻量名单里这 4 个都是「闭包 1 / 0 新增」。

## 二、为什么「闭包 1」的 9 个叶子只落了 4 个

开工时按轻量名单挑了 `common/summoner/**` 下 9 个闭包 1~3 的叶子，**逐个读源码后**发现它们分成三类：

| 叶子 | 非空行 | 判定 | 原因 |
|---|---:|---|---|
| `OBB` / `Ellipse` / `PathNode` / `IMomentumAttachmentEntity` | 233 | ✅ **本批落地** | 纯类型，零 PortLib、零注册 |
| `SyncFieldDispatcher` | 114 | ⛔ 推迟 | 依赖 PortLib 网络（`PortRegistryFriendlyByteBuf` / `PortStreamCodec`），要按原生 `StreamCodec` 重写；它服务于附着体的字段同步，属「网络批」 |
| `SummonerBatchedParticlesPayload` | 55 | ⛔ 推迟 | 同上（`IPortPacket.S2C` + `PortByteBufCodecs`），且要在 `ModEvents.registerPayloadHandlers` 里接线才有效 |
| `SummonerParticleTypes` (+`GenericParticleOptions`) | 16 (+83) | ⛔ 推迟 | 用 PortLib 的 `PortParticleTypeRegistration`/`PortRegisterHandler`/`PortRegistryEntry`，须改写成 1.21 原生 `DeferredRegister`；而 `GenericParticleOptions` 又带 `GenericParticleBuilder`（→3 文件） |
| `SummonerModels` | 10 | ⛔ 推迟 | 客户端：`Software ModelEvent.RegisterAdditional` + `new ModelResourceLocation(id, "inventory")`，1.21 的注册事件与 `ModelResourceLocation` 形态都变了，属客户端批 |
| `SummonerSoundEvents` | 23 | ⛔ 推迟（**且不该照搬形态**） | 见下面的实测 |

### ⚠️ `SummonerSoundEvents` 的实测：1.21 已经有这两个音效的**资产**，但没有 Java 常量

```powershell
# 1.21：sounds.json 里两条都在（音效层批次落的）
#   ConfluenceOtherworld/src/main/resources/assets/confluence/sounds.json:197 "use_minion_weapon"
#   ...:203 "use_terraprism"（各带 subtitle confluence.subtitle.use_*）
# 但 ModSoundEvents.java 里搜 minion / terraprism -> 0 命中
```

也就是说：1.21 侧的约定是**单一 `ModSoundEvents.EVENTS`**，而 1.20 的 `SummonerSoundEvents` 是**另一个
`DeferredRegister`**（`SOUNDS`）。照搬会在 1.21 造出第二套音效注册表 → 与既有约定不符；
正确做法是等召唤内容批次时把 `USE_MINION_WEAPON` / `USE_TERRAPRISM` **加进 `ModSoundEvents`**
（加完就与已存在的 sounds.json 条目对上）。本批只记录，不动手。

## 三、WP5 剩余工作的形状（`notes/WP5A-CLOSURES.md`，本批新测）

```powershell
python tools/port2native/seed_closures.py --seed-dir common/summoner ... --small 12
# {"seeds": 88, "light": 9, "heavy": 79, "hubs": 88, "min_new": 0, "max_new": 87}
```

| 层 | 闭包 | 说明 |
|---|---:|---|
| **AttachmentEntity 核心** | **28 文件 / 2374 非空行** | `AttachmentEntityData`、`InfoData`、`TargetCache`、`AttachmentEntity`、`Minion`、`IEntityCollision`/`IBlockCollision`、`SummonMarkType`/`SummonMarkInstance`、`SummonerAttachmentTypes`/`SummonerRegistries`、`SummonerBatchedInfoPayload`、`WhipMarkTracker` … **整个核心一次可落**（比 NPC 那团 156 文件好得多） |
| 动量/地面仆从层 | 29~30 文件 | `MomentumMinion`、`MomentumAttachmentEntity`、`GroundMinion`、`SummonerHelper`、`SummonerEvents`、`Projectile` |
| 召唤印记 / 粒子 | 32~45 文件 | `ParticleHelper`、`SummonerSummonMarks` |
| 各个仆从 | 88 文件 | `FinchMinion`/`HornetMinion`/`ImpMinion` … 共 14 个，闭包同源，随核心落地后一起变轻（现在都是 87 新增） |

**下一批建议**：直接吃 **AttachmentEntity 核心那 28 个文件**（`dep_subset --seed ...AttachmentEntityData`），
一次把 WP5 的地基打完；网络 payload 与粒子/音效在那批里顺势接线
（`ModEvents.registerPayloadHandlers`、`ModParticleTypes`、`ModSoundEvents`）。

## 四、验证

- 编译门：`总错误数: 0，涉及 0 个文件；[build] exit=0`（本批只加 4 个纯类型，无注册改动）
- 转换器：`uncovered.json` = `[]`、`leftovers.txt` 0 行、`manual-todo` 0 处
- 工作树与子模块干净；本批不含任何 assets/lang 改动
