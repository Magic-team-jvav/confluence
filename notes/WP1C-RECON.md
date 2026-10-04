# WP1 批次 1c 侦察结论（Boss 血条系统 + Boss/Monster 基座）

**状态：未落地**（按「不留半成品」回退到 `c4e808093` 的可编译状态）。本文记录已查清的前沿，供下一次开工直接用。

> **行数口径**：文中所有「N 行」都是**非空行**（`总行数 - 空行数`），不是文件总行数。
> 例：`BaseBoss.java` 总 950 行、空 113 行 → 记 837；`BaseBossPart.java` 332/47 → 285。
> 之前几处对不上号就是因为两个口径混用。

## 一、目标文件（1.20 侧 → 1.21 侧全部缺失）

| 文件 | 行数(非空) | 用途 |
|---|---|---|
| `common/entity/monster/BaseMonster.java` | 427 | 怪物基类（行为树挂载、状态属性、图鉴、音效） |
| `common/entity/boss/BaseBoss.java` | **837** | Boss 基类（遭遇范围、玩家死亡规则、血条同步、秘种） |
| `common/entity/boss/BossOwnedEntity.java` | 9 | 「属于某个 Boss」的标记接口 |
| `common/entity/EnemyTargeting.java` | 76 | 敌怪通用目标徘徊（玩家优先 / 保留或寻找受击反击目标） |
| `network/s2c/BossBarSyncPacketS2C.java` | 41 | 血条数据同步封包 |
| `client/handler/ClientBossBarTracker.java` | 23 | 客户端按 Boss 事件 UUID 记录实体类型与血量 |
| `client/gui/hud/CustomBossBarRenderer.java` | 117 | 自定义 Boss 血条渲染（分层纹理 + `boss_bar_flow` 着色器） |

依赖链：`BaseMonster` → `EnemyTargeting` → `BaseBoss` → `BossBarSyncPacketS2C` → 客户端渲染器。

## 二、试落一轮后的四类残留（已逐条核实，附出处）

把 7 个文件跑一遍转换器（`uncovered 0`）后编译，剩余错误分四类：

### 1. 属性/record 手术（**与批次 1b 完全同类，已有现成做法**）

- `BaseBoss` 与 `BaseMonster` 各有 ~22~29 处 `Holder<Attribute> ↔ Attribute`：套用 1b 的脚本（裸 `Attribute` → `Holder<Attribute>`，注意 `Map<Attribute, X>` 里的那个 `<` 前视不能排除）+ 去掉 `.value()` 拆包
- `AttributeModifier` record 化：`getId()/getName()` → `id()`、`getAmount()` → `amount()`、`getOperation()` → `operation()`，四参构造 `(UUID,String,double,Operation)` → 三参 `(ResourceLocation,double,Operation)`；1b 里 `BaseMonster.stateModifier*` 已写过一份可照抄的改法（用 `stateModifierId()` 从名字派生稳定的 ResourceLocation）
- `Entity#onAddedToWorld()` 在 1.21.1 **改名为 `onAddedToLevel()`**（`Entity.java:3733`，
  调用点 `ServerLevel.java:933/:945`）→ 照旧覆写、保留 `super` 调用即可。
  ⚠️ 本条最初写成「被 1.21 删除 → 挪进已有的 `tick()`」，是**错的**：当时只在
  `Entity.java`/`Level.java` 里 grep 了旧名，没查新名。已固化成规则 `vanilla-onaddedtoworld-rename`。

### 2. 1.21 渲染 API 重写（`CustomBossBarRenderer`，22 处）

- `BufferBuilder`：`Tesselator.getBuilder()` / `begin(Mode, VertexFormat)` / `end()` / `vertex(Matrix4f,int,int,float)` 这套 1.20.1 写法在 1.21.1 已换成别的 API（需要按 1.21 的 `BufferBuilder` + `BufferUploader` 形态重写，不是改名能解决的）
- `RegistryObject` → `DeferredHolder`
- `ClientConfigs.bossBarStyle` / `bossBarNumbersVisible`：1.21 侧配置项不存在，需一并搬

### 3. 注册层（`BossEntities`，9 处引用）

- 渲染器用 `BossEntities.<X>.get()` 把 Boss 实体映射到血条样式 —— 而 `BossEntities` 属 1.20 的 `common/init/entity/` 拆分（WP1c 的模块对齐 + 物种注册，**必须与物种同批**）
- 结论：`CustomBossBarRenderer` 不能先于 `BossEntities`/物种落地；建议 1c 拆成两半：
  - **1c-1**：`BaseBoss` + `BossOwnedEntity` + `EnemyTargeting` + `BaseMonster` + 19 个 BT 节点（不含渲染器）→ 编译门只到「基类存在」
  - **1c-2**：血条系统三件套（封包 + tracker + 渲染器）+ `BossEntities` 骨架 → 与物种注册一起

### 4. 封包风格（`BossBarSyncPacketS2C`，3 处）

- 1.20 的 `S2C`（PortLib 的封包基接口）→ 1.21 只用 `CustomPacketPayload`，`@Override` 的方法集不同 → 按 1.21 现有封包（`network/s2c/**`）的形态改写

## 三、顺带落地的一条规则（已提交）

`PortTranslatableEnum`（PortLib 的 7 行小接口）→ **`net.neoforged.neoforge.common.TranslatableEnum`**（方法名同为 `getTranslatedName()`，1.21 现有 8 处在用：`TerraStyle*Hud`、`ClientConfigs`、`AchievementOffset`、`ClientBestiary`）。已写进 `rules/forge-to-neoforge.json`（FQN + 简名两条）。

## 四、可复用的经验

- 这一批的错误构成 = 1b 的三种手术 + **1.21 渲染 API 重写** + **注册层**。前两类有现成做法，第三类必须跟物种同批。
- `CustomBossBarRenderer` 是「1.20 的自定义 HUD 渲染」与「1.21 渲染管线」差异最大的一处，值得单独排期，不要塞进基座批次。

## 五、1c-1 试落一轮后的追加结论（2026-09-27 第二次侦察）

把渲染器剥掉、只留基座（`BaseMonster`/`BaseBoss`/`BossOwnedEntity`/`BossChunkTicket`/`EnemyTargeting`/`ClientBossBarTracker`/`BossBarSyncPacketS2C`）再试一轮：
转换器 `uncovered 0`、`leftovers 0`，三类手术（属性 Holder 化、AttributeModifier record 化、`onAddedToWorld`→`onAddedToLevel` 改名）全部可用脚本完成，
`BossBarSyncPacketS2C` 也已按 1.21 的 `IPacketS2C` 形态重写（对照 `network/s2c/ManaPacketS2C.java`）。编译只剩两类残留：

### 1. 又一个未发现的依赖层：**Boss 部件系统**（1.20 新增，1.21 全缺）

| 缺失类 | 行数 | 被谁用 |
|---|---|---|
| `common/entity/boss/BaseBossPart.java` | 285 | `BaseBoss` 判定「这个实体是不是我这个 Boss 的部件」 |
| `common/entity/boss/BaseLivingBossPart.java` | 214 | 同上（活体部件） |
| `common/entity/boss/BossChildDeathLedger.java` | 71 | `BaseBoss` 清理时登记/释放子部件 |
| `common/entity/boss/MechanicalMayhemTracker.java` | 59 | 秘种机制（`MechanicalMayhem`） |

合计 **629 行**，是「多部件 Boss（如世界吞噬者、克苏鲁之脑）」的骨架。它们本身还会带出自己的依赖，属下一层。
另有 `CommonConfigs.BOSS_CLEAR_WHEN_NO_TARGET` 这个配置项 1.21 侧没有（1.21 的 `CommonConfigs` 只有 `BOSS_RESPAWN_TIME_MIN/MAX`），要补一个 config 定义。

### 2. 一个会咬人的正则细节（已记录，供下次直接避坑）

`BaseMonster` 里 `baseValue` / `setBaseValue` 的参数写的是**全限定名**
`net.minecraft.world.entity.ai.attributes.Attribute`，而手术脚本的 lookbehind `(?<![\w.])`
会把「点号前面」的 `Attribute` 全部放过 → 只剩 4 行被改，其余 15 行报 `Holder<Attribute> cannot be converted to Attribute`。
**下次手术要同时处理简名与全限定名两种写法**（FQN 形式：`net\.minecraft\.world\.entity\.ai\.attributes\.Attribute` → `Holder<net.minecraft.world.entity.ai.attributes.Attribute>`，或直接导入简名）。

### 3. 建议的 1c 拆分（修正版）

- **1c-1**：`BaseBossPart` + `BaseLivingBossPart` + `BossChildDeathLedger` + `MechanicalMayhemTracker` + `CommonConfigs.BOSS_CLEAR_WHEN_NO_TARGET`
- **1c-2**：`BaseBoss` + `BossOwnedEntity` + `BossChunkTicket` + `EnemyTargeting` + `BaseMonster` + 19 个 BT 节点（三类手术 + FQN 处理）
- **1c-3**：血条三件套（封包已改写完成、可直接复用）+ `CustomBossBarRenderer`（需 1.21 渲染管线重写）+ `BossEntities` 骨架

三批都以「编译通过」为出口；本轮所有改动已在验证后回退，树保持在 `ad8a9120e` 的可编译状态。

> ⚠️ **上面这条拆分已被实测推翻，实际落地的是另一套切法。**
> `BaseBossPart<T extends BaseBoss>` 的泛型上界要求 `BaseBoss` 存在，`BaseBoss extends BaseMonster`，
> `EnemyTargeting.applies` 里又要 `instanceof BaseBoss` —— 这四层是**不可分割的编译单元**，
> 所以「1c-1 只搬部件 4 文件」过不了编译门；而 1c-c 的血条渲染器依赖 `BossEntities` →
> 全部 Boss 物种，实测闭包 320 文件 / 37342 非空行。
> **实际切法与逐批结论见 `notes/WP1C-CLOSEOUT.md`。**

## 六、1c-a 完成：API 差异表已按正确口径重跑（v2）

`notes/WP1C-API-DIFF.md` 已由 `tools/port2native/entity_api_diff.py` 重新生成，
**83 个覆写点里只有 6 个需要动手**（`OK` 77 / `SIG_CHANGED` 5 / `REMOVED` 1 / `UNRESOLVED` 0）。
旧版判的 44 个「需处理」有 38 个是口径缺陷造成的假阳性（形参名差异、嵌套类限定前缀、
匿名内部类误收、工程/第三方库接口未进闭包）。

**必须动手的 6 处**（其余 77 处签名一致，移植时不需要任何 API 手术）：

| 文件 | 方法 | 1.20 | 1.21 | 做法 |
|---|---|---|---|---|
| `BaseBossPart` | `defineSynchedData` | `()` | `(SynchedEntityData.Builder)` | `entityData.define(...)` → `builder.define(...)` |
| `BaseBossPart` | `getAddEntityPacket` | `()` | `(ServerEntity)` | `new ClientboundAddEntityPacket(this, entity)` |
| `BaseBossPart` | `lerpTo` | 7 参 | **6 参** | **删**掉末尾 `boolean teleport` |
| `BaseLivingBossPart` | `defineSynchedData` | `()` | `(SynchedEntityData.Builder)` | 同 BaseBossPart（并 `super.defineSynchedData(builder)`）|
| `BossChildDeathLedger` | `save` | `(CompoundTag)` | `(CompoundTag, HolderLookup.Provider)` | 加第二个形参 |
| `BaseMonster` | `onAddedToWorld` | `()` | `onAddedToLevel()` | **改名，不是删除**；照旧覆写并保留 `super.onAddedToLevel()`（它会置 `isAddedToLevel`）|

### 两条被推翻的旧结论（记下来，别再按旧结论动手）

1. **`lerpTo` 方向反了。** 不是「1.21 多一个插值参」，而是 **1.20.1 Forge 比 1.21.1 NeoForge 多一个**：
   Forge 47.4.20 给 `Entity` 打了补丁 `lerpTo(double,double,double,float,float,int,boolean teleport)`
   （`forge-1.20.1-47.4.20-sources.jar` → `net/minecraft/world/entity/Entity.java:2115`），
   NeoForge 21.1.219 没带这个补丁，是六参（`neoforge-21.1.219-sources.jar` → 同文件 `:2202`）。
   移植方向是**删**参数。
2. **`tickHeadTurn` 不是差异。** 两侧都是 `(float, float)`，与 `Mob#tickHeadTurn` 一致，
   `BaseBoss` / `BaseLivingBossPart` 两处都不用改。

### 顺带确认的 1.20.1 Forge 与 1.21.1 NeoForge 的同类差异

同一份 1.20.1 Forge 源码里，`Entity#defineSynchedData()` 与 `Entity#getAddEntityPacket()` 都是**无参**，
1.21.1 分别变成 `(SynchedEntityData.Builder)` 与 `(ServerEntity)` —— 这两处是版本演进，
和 `lerpTo` 的「Forge 补丁被 NeoForge 丢掉」要分开记。
