# 批次 8「火星内容批」落地计划（逐文件 / 逐插入点 / 逐适配）

> 只读分析产物。分析期间**未修改任何仓库文件、未 add/commit、未编译、未跑 datagen**。中间产物全部在 `%TEMP%`。
> 基线：SOURCE `D:\Minecraft\1.20forge\confluence` HEAD `07c2ab5b5`；TARGET `D:\Minecraft\1.21neoforge\confluence` HEAD **`67a2fb7b8`**。

---

## 0. 必须先读：基线漂移与两个前提修正

### 0.1 TARGET 在本次分析期间被并行改动了（行号锚点仍有效）

| 时刻 | TARGET HEAD | 内容 |
|---|---|---|
| 我开工时 | `b5dfc0adc` | 第二十七节 |
| 中途 | `85d6d6591` | docs：第二十八节，批次 8 启动 + 裁定 23/24 |
| 中途 | `831c9908b` / `57c50c392` / `67a2fb7b8` | **子批 A 已落地**：18 个火星资源（6 geo + 6 animation + 6 texture） |

**关键复核命令**（已跑，结果 = 0 行）：

```
git -C D:\Minecraft\1.21neoforge\confluence diff --name-only b5dfc0adc 67a2fb7b8 -- "*.java"
```

⇒ 这 3 个提交**只加资源、零 java**，所以**本计划所有 1.21 java 行号锚点在 `67a2fb7b8` 上依然成立**。但**执行时必须重新 `git rev-parse HEAD` 并复核锚点**。

### 0.2 修正 ①：1.20 侧这批代码**从未编译通过**

`MartianEventHelper` **在两个仓库都不存在**（证据链）：

- `git -C 1.20 grep -n "MartianEventHelper" -- "*.java"` ⇒ 只有 4 处**引用**：`MartianOfficer.java:17`（import）、`:80`（调用）、`MartianWalker.java:20`（import）、`:211`（调用）。
- `git ls-files | Select-String MartianEvent` ⇒ 空；全盘 `Get-ChildItem -Recurse -Filter *MartianEvent*` ⇒ 空。
- 1.20 `common/gameevent/` 17 个文件里无 `Martian*.java`。
- 1.20 编译产物 `ConfluenceOtherworld\build\classes\java\main` 递归搜 `*Martian*` ⇒ **只有 `MartianProbe*` 5 个 class**；`*Walker*`、`MonsterLaser`、`TeslaTurret`、`RayGunner`、`Scutlix`、`MartianEngineer`、`ElectrifiedEffect` **一个都没有**。

⇒ 结论：**"非毒"（不引入破坏性改动）≠ "可编译"**。裁定 23/24 里的"整批搬"必须先补 `MartianEventHelper` 的语义（见 §6 裁定项 1）。

### 0.3 修正 ②：裁定 23 里的第二枚射弹名字过期了

裁定 23（`notes/PORT-LANDING-RECORD.md:1019`）写的是 `MartianElectricBolt`/`RayGunnerLaser`。但 1.20 HEAD 上 `1780b9a88` **删除**了 `common/entity/projectile/RayGunnerLaser.java`（stat `61 -`）并**新增** `MonsterLaser.java`（`116 ++`），`ModEntities.java:61` 注册的是 `MONSTER_LASER`。
⇒ 实际要加的两条是 **`MARTIAN_ELECTRIC_BOLT` + `MONSTER_LASER`**。1.21 侧从未有过 `RayGunnerLaser`，无 `-` 行需要处理。

### 0.4 路径搬家的唯一一处（本批）

| 1.20 | 1.21 | 影响 |
|---|---|---|
| `org.confluence.mod.common.init.entity.ModEntities` | **`org.confluence.mod.common.init.entity.ModEntities`** | ← **唯一包搬家**。`MonsterEntities` 两边同包。所以 `TeslaTurret.java:22`、`RayGunner.java:22`、`MartianWalker.java:22` 的 import 必须改写 |

其余全部同路径：`common/entity/monster/**`、`common/entity/projectile/**`、`common/effect/harmful/**`、`network/c2s/**`、`client/entity/model/**`、`client/entity/renderer/**`、`client/renderer/entity/projectile/**` 在 1.21 均**逐一对得上、无需改名**。

---

## 1. 文件三分类

### (a) 纯新增文件（19 个 java，1.21 完全不存在）

全部可整文件搬，最后一列是**必修适配点数**（详见 §3）。

| # | 1.20 源路径（相对 `...\src\main\java\org\confluence\mod\`） | 1.21 目标路径 | 1.20 行数 | 适配点 |
|---|---|---|---|---|
| 1 | `common/entity/monster/MartianEngineer.java` | 同 | 202 | 3（GeckoLib 包 / `isPresent` / `get` 语义） |
| 2 | `common/entity/monster/TeslaTurret.java` | 同 | 191 | 5 |
| 3 | `common/entity/monster/RayGunner.java` | 同 | 266 | 5 |
| 4 | `common/entity/monster/Scutlix.java` | 同 | 251 | 8 |
| 5 | `common/entity/monster/MartianOfficer.java` | 同 | 168 | 6（含 **`MartianEventHelper` 阻塞**） |
| 6 | `common/entity/monster/MartianWalker.java` | 同 | 472 | 11（含 **`MartianEventHelper` 阻塞**） |
| 7 | `common/entity/monster/WalkerWeapon.java` | 同 | 101 | 2 |
| 8 | `common/entity/monster/WalkerGeometry.java` | 同 | 228 | **0（逐字可搬）** |
| 9 | `common/entity/monster/OfficerShieldGeometry.java` | 同 | 47 | **0（逐字可搬）** |
| 10 | `common/entity/projectile/MartianElectricBolt.java` | 同 | 73 | 1 |
| 11 | `common/entity/projectile/MonsterLaser.java` | 同 | 116 | **3（含 `defineSynchedData` 重写）** |
| 12 | `common/effect/harmful/ElectrifiedEffect.java` | 同 | 52 | **2（父类 + `applyEffectTick` 返回 boolean）** |
| 13 | `network/c2s/ElectrifiedInputPacketC2S.java` | 同 | 34 | **全类重写（PortLib 4 个 api）** |
| 14 | `client/entity/model/MartianWalkerModel.java` | 同 | 28 | 1（GeckoLib 包） |
| 15 | `client/entity/renderer/MartianOfficerRenderer.java` | 同 | 196 | 5（Blaze3D 硬断裂） |
| 16 | `client/entity/renderer/MartianWalkerRenderer.java` | 同 | 20 | **0（逐字可搬）** |
| 17 | `client/entity/renderer/ShieldOutlineProjection.java` | 同 | 52 | **0（逐字可搬）** |
| 18 | `client/renderer/entity/projectile/MartianElectricBoltRenderer.java` | 同 | 44 | **0（逐字可搬）** |
| 19 | ~~`common/entity/projectile/RayGunnerLaser.java`~~ | **不搬** | — | 1.20 HEAD 已删除 |

> 依赖已核（1.21 存在，无需新建）：`org.confluence.mod.common.entity.ai.bt.{BTNode,BTStatus}`、`...bt.composite.SelectorNode.of`、`...bt.leaf.VanillaGoalAction`、`common/entity/ai/SweptContactAttack`、`common/entity/PartHitTarget`、`common/entity/EnemyDamageRules`、`common/entity/projectile/StraightMonsterProjectile`、`client/entity/model/ExplicitGeoModel`、`client/entity/renderer/{GeoNormalRenderer,NoopRenderer}`、`client/model/entity/projectile/SpearProjectileModels`、`client/effect/RenderStateShardAccessor`、`org.confluence.lib.common.LibEffects`、`org.confluence.lib.network.IPacketC2S`、`Confluence.createType/asResource/LOGGER`。

### (b) 需要往既有共享文件里插内容

⛔ = 1.21 无合适锚点（见 §6）；✅ = 锚点精确、无同名冲突。

| # | 1.21 目标文件 | 插入锚点（1.21 文件:行） | 要插的 1.20 原文 | 1.20 HEAD 行号 | 同名冲突 |
|---|---|---|---|---|---|
| 1 | `common/init/ModEntities.java` ⚠️**禁改** | `:61` `EMPTY_ENTITY = ENTITIES.register("empty_entity", ...)` **之后** | `MARTIAN_ELECTRIC_BOLT`、`MONSTER_LASER` 两行 | `common/init/entity/ModEntities.java:60-61` | 无（两常量 ABSENT） |
| 2 | `common/init/entity/MonsterEntities.java` ✅ | 注释插在 `:899`（`MARTIAN_PROBE`）**之前**；7 条注册插在 `:900` 之后、`:902`（`// Boss 附属生物：克苏鲁之脑`）之前 | `//火星人事件` + 7 条 `withAttributes(...)`（共 18 行） | `MonsterEntities.java:763`（注释）、`:766-783` | 无（7 常量 ABSENT） |
| 3 | `common/init/ModEffects.java` ✅ | `:165`（`JUNGLES_FURY` 结尾）之后、`:166`（`GARDEN_GNOME_LUCK`）之前 | `ELECTRIFIED` 一行 | `ModEffects.java:38` | 无（1.21 无 `ELECTRIFIED`） |
| 4 | `common/init/item/SpawnEggItems.java` ✅ | ①`:212`（`MARTIAN_PROBE_SPAWN_EGG`）之后 → 4 枚<br>②`:302` 之后（`:304` `public static void init() {}` 之前）→ 2 枚 | ①`MARTIAN_ENGINEER/TESLA_TURRET/RAY_GUNNER/SCUTLIX`<br>②`MARTIAN_OFFICER/MARTIAN_WALKER` | `SpawnEggItems.java:214-217`、`:309-310` | 无（6 常量 ABSENT） |
| 5 | `common/init/ModTabs.java` ⛔ | **1.21 全文无刷怪蛋分节**（`:458` 只有 `.withTabsBefore(CreativeTabs.SPAWN_EGGS)`；grep `egg` 命中 0） | `martian = GroupItem.belongsTo("martian_entity", output)` + 7 条 `accept` | `ModTabs.java:2128-2135` | n/a |
| 6 | `common/event/NetworkEvents.java` ✅ | `:69`（`DyeMixPacketC2S`）之后（1.20 亦在 `DyeMixPacketC2S` 之后） | `ElectrifiedInputPacketC2S` 注册一行 | `NetworkEvents.java:26` | 无 |
| 7 | `common/gameevent/GameEventSystem.java` ✅ | import 区加 `Scutlix`；替换 `:252-258` 的 `if (entity != null) {...}` 块 | import + `instanceof Scutlix ... ensureRider` + `getSelfAndPassengers().forEach(...)` | `:29`（import）、`:259-262`（改）、`:264-268`（加） | 无 |
| 8 | `common/data/gen/loot/EntitySubProvider.java` ✅ | `:857`（`MARTIAN_PROBE` 空表）之后、`:858`（`NpcEntities.MECHANIC`）之前 | `//火星人事件` + 7 条 `add(..., LootTable.lootTable())` | `:841`（注释）、`:843-849` | 无 |
| 9 | `common/data/gen/ModClientBestiaryEntryProvider.java` ⛔ | **1.21 无 `MARTIAN_PROBE` 条目**；占位注释在 `:483-490` | 6 条 `.add(...FilterEntry.MARTIAN_MADNESS)` | `:515-520` | `FilterEntry.MARTIAN_MADNESS` **存在**（`FilterEntry.java:72`） |
| 10 | `common/data/gen/language/BestiaryLanguageSubProvider.java` ⚠️ | EN `:501`（`martian_probe.desc`，**1.21 是注释**）+ `:574-583`（注释占位）；ZH `:1272` + `:1345-1354` | EN `:432,513`；ZH `:1222,1303` | 同左 | **文案与 1.21 注释占位不同** |
| 11 | `common/data/gen/ModChineseProvider.java` ⛔⚠️ | ①`itemGroup.confluence.martian_entity` → `:328`（`misc_entity`）之后（1.21 无 `pirate_entity`）<br>②`ModEntities.MONSTER_LASER` 名 → `:1761` 后（名称段 `:1715-1843`）<br>③`ModEffects.ELECTRIFIED` → `:4899` 后（`addEffect` 段 `:4817-4899`）<br>④**6 个 `entity.confluence.martian_*` → 无段落**<br>⑤刷怪蛋名 → `:3665`（唯一一条）后 | 见 §1(b) 注 | `:361`、`:5084-5093`、`:6279-6280`、`:6288-6291` | 无 |
| 12 | `common/data/gen/ModEnglishProvider.java` ⛔⚠️ | 同 11；效果名在 `:1730` 由 `ModEffects.EFFECTS.getEntries().forEach(...toTitleCase)` **自动生成**，实体名自动段 `:1731` **只覆盖 `ModEntities.ENTITIES`** | 同上 | `:518`、`:1265-1270`、`:1840-1845`、`:2796-2801` | 无 |
| 13 | `client/event/ModClientEvents.java` ⚠️ | ①import：`client.entity.renderer.*` 区（`:184` `MartianProbeRenderer` 附近）+ `client.renderer.entity.projectile.*` 区<br>②射弹渲染器：1.20 锚在 `FROST_BEAM`（**1.21 未注册任何 `LaserProjectileRenderer`**）⇒ 建议放 `:630`（`STORM_SPEAR_SHOT`，同一个 `SpearProjectileModels.STORM`）后<br>③实体渲染器：`:936`（`MARTIAN_PROBE`）之后 | ①2 条 import；②`MARTIAN_ELECTRIC_BOLT`/`MONSTER_LASER`；③20 行火星渲染器块 | `:460`、`:703-722` | 无 |
| 14 | `client/event/GameClientEvents.java` ⚠️ | ①import `MartianOfficerRenderer` → `:80` 附近；`ElectrifiedInputPacketC2S` → `:104` 前<br>②电击输入块 → `:184` 前（`clientTick$Post` 内 `if (player != null)` 里；**1.21 无 `isPaused()` 门**）<br>③`MartianOfficerRenderer.renderShields(event)` → `:396`（`:395` `AFTER_PARTICLES` 分支第一句） | ②7 行；③1 行 | `:187-193`、`:372` | 无 |
| 15 | `client/entity/renderer/LaserProjectileRenderer.java`（**改造既有文件**） | 1.21 现为 83 行（`:19` class、`:27` 三参 ctor、`:41-46` render、`:71-72` `addVertex/setColor`） | 移植 1.20 diff：`import MonsterLaser`、单参 ctor、`render` 内 variant 取色分支 | 1.20:14（import）、`:35-38`（ctor）、`:50-57`（variant） | 无 |

### (c) 纯资源

**子批 A 已全部落地（3 个提交），本计划无需再动。** 复核结果：

| 项 | 1.20 | 1.21 现状 | 需改名？ |
|---|---|---|---|
| `assets/confluence/geo/entity/{martian_engineer,martian_officer,martian_walker,ray_gunner,scutlix,tesla_turret}.geo.json` | 6 个 | **6 个全在**，MD5 与 1.20 一致 | 否 |
| `assets/confluence/animations/entity/{同名 6}.animation.json` | 6 个 | **6 个全在** | 否 |
| `assets/confluence/textures/entity/{同名 6}.png` | 6 个 | **6 个全在** | 否 |
| 音效 `.ogg`（`martian_drone_*` 5 + `martian_walker_free_*` 4 + `scutlix_*` 3 + `tesla_turret_*` 2） | 在 `sounds/mob/` | 1.21 **已平铺到 `sounds/`**，全部存在 | 否（1.21 已整体迁移为平铺） |
| `assets/confluence/sounds.json` | 1.20 用 `confluence:mob/xxx` | 1.21 用 `confluence:xxx`（`martian_drone_death:2751`、`martian_walker_free:2771`、`scutlix_*:3093-3108`、`tesla_turret_*:3156-3165`、`item_laser_shoot:2312`、`metal_hurt/death:366-375`） | 否 |
| `ModSoundEvents` 常量 | 9 个 | 1.21 全在：`MARTIAN_WALKER_FREE:436`、`SCUTLIX_DEATH/FREE/HURT:485-487`、`TESLA_TURRET_DEATH/HURT:494-495`、`METAL_HURT/DEATH:65-66`、`ITEM_LASER_SHOOT:363` | 否 |
| 粒子 | 无自定义粒子（唯一 `ParticleTypes.EXPLOSION` 是原版） | 无需任何工作；`ModParticleTypes.java` 两仓 diff **为空**（blob 同 hash） | 否 |
| 刷怪蛋模型/贴图 | 1.20 由 datagen 生成 285 个 `*_spawn_egg.json` + `textures/item/egg/*.png` | 1.21 `src/generated/.../models/item` 只有 **1 个**（`rainbow_sheep_spawn_egg.json`），`ModItemModelProvider` 无刷怪蛋逻辑，`textures/item/egg/` 只有 2 个原版 | **不改名；但 1.21 不会有刷怪蛋模型**（见 §5） |

**geo `identifier` 一致性**：6 个新 geo 的 `"identifier"` 都是 `"geometry.unknown"`（各文件第 6 行），而 1.21 既有 `geo/entity/martian_probe.geo.json:6` 是 `"geometry.martian_probe"`。两仓全部 `.java` grep `geometry.unknown`/`"identifier"` ⇒ **0 命中**，渲染器一律走 `ExplicitGeoModel` 显式路径，故**功能上无害**，但风格不统一（→ §6 裁定 7）。

---

## 2. 子批划分与依赖顺序

### 2.1 ⚠️ notes 28.2 的初步顺序（A→B实体→C射弹）**会产生死引用**

依赖已核（1.20 file:line）：

| 实体 | 依赖（编译期） | 证据 |
|---|---|---|
| `RayGunner` | `MonsterLaser` + `MonsterLaser.Variant.RAY_GUNNER` | `RayGunner.java:20,213-216` |
| `TeslaTurret` | `MartianElectricBolt` + `ModEntities.MARTIAN_ELECTRIC_BOLT` | `TeslaTurret.java:19,128-133` |
| `MartianWalker` | `MonsterLaser` + `ModEntities.MONSTER_LASER` | `MartianWalker.java:19,229-232` |
| `Scutlix` | `RayGunner` + `MonsterEntities.RAY_GUNNER` | `Scutlix.java:138-141,169` |
| `MartianEngineer` | `TeslaTurret` + `MonsterEntities.TESLA_TURRET` | `MartianEngineer.java:107-122` |
| `GameEventSystem` | `Scutlix.ensureRider(ServerLevel)` | `GameEventSystem.java:29,259` |
| `LaserProjectileRenderer` | `MonsterLaser.Variant` | 1.20:14, 50-57 |

⇒ **射弹类必须先于实体类存在**。建议把「射弹 + 效果 + 网络包」整体前置。

### 2.2 建议子批序列（拓扑序，每批自洽可编译、无死引用）

| 子批 | 内容（文件） | 为什么这样切 |
|---|---|---|
| **A** ✅**已落地** | 18 个资源（`831c9908b`/`57c50c392`/`67a2fb7b8`） | 零 java 依赖，可独立提交 |
| **B** 射弹 + 效果 + 网络 | ①`common/entity/projectile/MartianElectricBolt.java`（新）②`common/entity/projectile/MonsterLaser.java`（新）③`common/effect/harmful/ElectrifiedEffect.java`（新）④`common/init/ModEntities.java`（+2 行，禁改已解除）⑤`common/init/ModEffects.java`（+1 行）⑥`network/c2s/ElectrifiedInputPacketC2S.java`（新）⑦`common/event/NetworkEvents.java`（+1 行） | ①依赖 `StraightMonsterProjectile`/`EnemyDamageRules`（均已在 1.21）→ 只需 `ModEffects.ELECTRIFIED`（本批）②只依赖 `EnemyDamageRules`。③只依赖原版 `MobEffect`。⑥依赖③+⑤。**闭包完整，可单独编译** |
| **C** 实体 + 注册 + 刷怪蛋 + 掉落 + 事件接线 | ①7 个实体类（`MartianEngineer`/`TeslaTurret`/`RayGunner`/`Scutlix`/`MartianOfficer`/`MartianWalker`/`WalkerWeapon`）②`WalkerGeometry`+`OfficerShieldGeometry`（新，零依赖）③`common/init/entity/MonsterEntities.java`（+18 行）④`common/init/item/SpawnEggItems.java`（+6 行）⑤`common/data/gen/loot/EntitySubProvider.java`（+8 行）⑥`common/gameevent/GameEventSystem.java`（改） | C 只引用 B 的射弹 + 已在 1.21 的 `BaseWarriorMonster`/`BaseMonster`/BT 框架/`SweptContactAttack`/`PartHitTarget`/`ModSoundEvents`。**但 `MartianOfficer`/`MartianWalker` 依赖不存在的 `MartianEventHelper` → 按裁定 1 先删两处调用再落地** |
| **D** 渲染 / 客户端注册 | ①`client/entity/model/MartianWalkerModel.java`（新）②`client/entity/renderer/{MartianOfficerRenderer,MartianWalkerRenderer,ShieldOutlineProjection}.java`（新）③`client/renderer/entity/projectile/MartianElectricBoltRenderer.java`（新）④`client/entity/renderer/LaserProjectileRenderer.java`（**改造**）⑤`client/event/ModClientEvents.java`（+22 行）⑥`client/event/GameClientEvents.java`（+10 行） | D 引用 C 的实体/几何与 B 的射弹/Variant，全部就位后才可编译 |
| **E** lang / bestiary | ①`common/data/gen/ModChineseProvider.java`②`common/data/gen/ModEnglishProvider.java`③`common/data/gen/ModClientBestiaryEntryProvider.java`④`common/data/gen/language/BestiaryLanguageSubProvider.java` | 纯 datagen，不参与编译期依赖，放最后以吸收前面各批的最终命名 |

> 若要保留 notes 的 A–E 字母，把上表 **B 与 C 互换**即可，但**必须**保持「射弹/效果/网络」在「实体」之前。

---

## 3. 平台适配清单（1.20 写法 → 1.21 写法 + 依据）

### 3.1 PortLib → 原生（PortLib 只存在于 1.20 仓，1.21 无此库）

| 1.20（PortLib / Forge） | 1.21 对应物 | 依据 |
|---|---|---|
| `org.mesdag.portlib.wrapper.world.effect.PortMobEffect` | `net.minecraft.world.effect.MobEffect`，且 **`applyEffectTick` 返回 `boolean`**（1.20 是 `void`） | `ElectrifiedEffect.java:6,13`；`HorrifiedEffect.java:22-24`；`MobEffect.java:74`（1.21 反编译源） |
| `IPortPacket.C2S` / `PortStreamCodec` / `PortByteBufCodecs.BOOL` / `PortPacketDistributor.sendToServer` | `IPacketC2S` / `StreamCodec` / `ByteBufCodecs.BOOL` / `PacketDistributor.sendToServer` | `IPacketC2S.java:6-19`；`MountInputPacketC2S.java:4-7,17-41`（最贴近的样板：`record ... implements IPacketC2S` + `Confluence.createType` + `StreamCodec.composite(ByteBufCodecs.BOOL, ...)`） |
| `PortDeferredItem<ForgeSpawnEggItem>` | `DeferredItem<DeferredSpawnEggItem>` | `SpawnEggItems.java:210,306-312` |
| `PortDeferredRegisterExtension.register(ENTITIES, name, id -> builder.build(...))` | `ENTITIES.register(name, id -> builder.build(id.toString()))` | `MonsterEntities.java:827-828`(1.20) → `MonsterEntities.java:1035-1037`(1.21) |
| `RegistryObject<T>` | `DeferredHolder<R, T>`；**`.isPresent()` → `.isBound()`**；**`.get()` 现在抛 NPE 而非返回 null** | 1.21 全仓 116 处 `.isBound()`；`DeferredHolder` 无 `isPresent()` |
| `IPortProjectileExtension` | **不实现**（`Projectile` 原生已含） | `StraightMonsterProjectile.java:26-29` |
| `net.minecraftforge.entity.PartEntity` | `net.neoforged.neoforge.entity.PartEntity` | `PlantStemPart.java:10` |
| `net.minecraftforge.client.event.RenderLevelStageEvent` | `net.neoforged.neoforge.client.event.RenderLevelStageEvent` | `GameClientEvents.java:44`（通配导入） |
| `PortEffectCure` | `net.neoforged.neoforge.common.EffectCure` | `ModEffects.java:14,35-36` |
| PortLib `getRandom1211()` | `getRandom()` | `AnglerDialogScreen.java:25`（仓库内既有迁移注释） |

### 3.2 1.21 实体注册助手 —— **存在且同形** ✅

| 助手 | 1.21 位置与签名 | 与 1.20 同形？ |
|---|---|---|
| `registerEntity` | `MonsterEntities.java:1035` `private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder)` | ✅ 同形（1.20:827 返回 `RegistryObject<EntityType<T>>`） |
| `withAttributes`（MonsterEntities 内） | `MonsterEntities.java:1040` `private static <T extends LivingEntity> DeferredHolder<EntityType<?>, EntityType<T>> withAttributes(DeferredHolder<...> type, Supplier<CreatureAttributeBuilder.Definition> attributes)` → 委托 `ModEntities.withAttributes`（`ModEntities.java:359` **public**） | ✅ 同形 |
| `DevelopmentSpawnPolicy.developmentOnly` | `common/init/entity/DevelopmentSpawnPolicy.java:19` `public static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> developmentOnly(DeferredHolder<EntityType<?>, EntityType<T>> registration)` | ✅ 同形（入参/出参类型改了） |
| `CreatureAttributeBuilder.state(Enum<?>, Consumer<StateBuilder>)` | `CreatureAttributeBuilder.java:44` | ✅（`RAY_GUNNER`/`SCUTLIX` 的 `.state(CombatState.MOUNTED, ...)` 需要） |
| `MonsterEntities.ENTITIES` 是一个**独立** DeferredRegister | `MonsterEntities.java:136`；由 `ModEntities.java:380` `MonsterEntities.ENTITIES.register(eventBus)` 挂上总线 | ✅ |

⇒ **§1(b)#2 的 18 行注册块只需把 `RegistryObject<EntityType<X>>` 改成 `DeferredHolder<EntityType<?>, EntityType<X>>`，其余逐字照搬。**

### 3.3 GeckoLib 4.8.4：1.21 侧**已无 `core.` 包**

两仓 `gradle.properties` 都写 `geckolib_version=4.8.4`，但 1.21 的 `geckolib-neoforge-1.21.1-4.8.4` 里 `software/bernie/geckolib/core/**` **条目数为 0**。实测两仓 import 并集对照：

| 1.20 import | 1.21 import | 本批用到 |
|---|---|---|
| `software.bernie.geckolib.core.animation.AnimatableManager` | `software.bernie.geckolib.animation.AnimatableManager` | `MartianEngineer:23`、`TeslaTurret:24`、`RayGunner:24`、`Scutlix:18`、`MartianOfficer:18`、`MartianWalker:29` |
| `...core.animation.AnimationController` | `software.bernie.geckolib.animation.AnimationController` | 同上 |
| `...core.animation.RawAnimation` | `software.bernie.geckolib.animation.RawAnimation` | 同上 |
| `...core.animation.AnimationState` | `software.bernie.geckolib.animation.AnimationState` | `MartianWalkerModel:6` |
| `...core.object.PlayState` | `software.bernie.geckolib.animation.PlayState` | `Scutlix:21` |
| `...core.object.Color` | `software.bernie.geckolib.util.Color` | — |
| `...core.state.BoneSnapshot` | `software.bernie.geckolib.animation.state.BoneSnapshot` | — |
| `...core.animatable.GeoAnimatable` | `software.bernie.geckolib.animatable.GeoAnimatable` | — |
| `...core.animatable.instance.AnimatableInstanceCache` | `software.bernie.geckolib.animatable.instance.AnimatableInstanceCache` | — |
| `...core.keyframe.AnimationPoint` | `software.bernie.geckolib.animation.keyframe.AnimationPoint` | — |
| `...core.keyframe.event.data.*` | `software.bernie.geckolib.animation.keyframe.event.data.*` | — |
| `...core.keyframe.Keyframe` | `software.bernie.geckolib.animation.keyframe.Keyframe` | — |
| `...renderer.DyeableGeoArmorRenderer` | `software.bernie.geckolib.renderer.specialty.DyeableGeoArmorRenderer` | — |
| `...util.RenderUtils` | `software.bernie.geckolib.util.RenderUtil` | — |
| `...animatable.GeoEntity` / `...model.GeoModel` / `...renderer.GeoEntityRenderer` / `...cache.GeckoLibCache` / `...cache.object.*` / `...constant.*` / `...animatable.client.GeoRenderProvider` | **同名不变** | `LaserProjectileRenderer:17-18` 等 |

### 3.4 网络包注册入口 = `common/event/NetworkEvents.java`

1.21 入口：`NetworkEvents.java:20` `@SubscribeEvent public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event)`，链式 `event.registrar("1")`，**C2S 用 `.playToServer(TYPE, STREAM_CODEC, Cls::handle)`**（`:62-87`）。1.20 是 `handler.registerInGameC2S(Class, ResourceLocation, Codec)`。

`ElectrifiedInputPacketC2S` 重写（依据 `MountInputPacketC2S.java:17-41`，形状几乎一致）：

```java
public record ElectrifiedInputPacketC2S(boolean moving) implements IPacketC2S {
    public static final Type<ElectrifiedInputPacketC2S> TYPE = Confluence.createType("electrified_input");
    public static final StreamCodec<ByteBuf, ElectrifiedInputPacketC2S> STREAM_CODEC =
            ByteBufCodecs.BOOL.map(ElectrifiedInputPacketC2S::new, ElectrifiedInputPacketC2S::moving);
    @Override public Type<ElectrifiedInputPacketC2S> type() { return TYPE; }
    @Override public void work(ServerPlayer player) {
        if (player.hasEffect(ModEffects.ELECTRIFIED)) ElectrifiedEffect.recordHorizontalInput(player, moving);
    }
    public static void send(boolean moving) { PacketDistributor.sendToServer(new ElectrifiedInputPacketC2S(moving)); }
}
```

（`Confluence.createType` 在 `Confluence.java:154`；1.21 风格省略 `.get()`：`GameClientEvents.java:235` `player.hasEffect(ModEffects.CURSED)`）
注册行插在 `NetworkEvents.java:69`（`DyeMixPacketC2S`）之后。

### 3.5 `ModEffects` / `ModTabs` / `SpawnEggItems`

- **`ModEffects`**：插 `ModEffects.java:166` 前，类型用 `DeferredHolder<MobEffect, MobEffect>`（`:38` 起全是这个形状）。`ElectrifiedEffect` 不需要新 import —— `ModEffects.java:25` 已是 `import org.confluence.mod.common.effect.harmful.*;`。
  `ElectrifiedEffect` 本体两处必改：`extends PortMobEffect` → `extends MobEffect`；`public void applyEffectTick(...)` → `public boolean applyEffectTick(...)` + `return true;`（`MobEffect.java:74`；`shouldApplyEffectTickThisTick` 在 `MobEffect.java:82` 仍存在，无需改）；`living.hurt(living.damageSources().magic(), ...)` 在 1.21 仍合法（`LivingEntity.java:1142`）。
- **`ModTabs`**：**1.21 全文无刷怪蛋分节**（`grep egg` 0 命中；`:458` 只是 `.withTabsBefore(CreativeTabs.SPAWN_EGGS)`）。既有 284 个刷怪蛋同样不在任何 tab 里。⇒ **本项在 1.21 无对应物**，`itemGroup.confluence.martian_entity` 也会变成死键。→ 见 §6 裁定 3（定论：跳过）。
- **`SpawnEggItems`**：1.21 用 `DeferredItem<DeferredSpawnEggItem>` + `ITEMS.register(type.getId().getPath() + "_spawn_egg", ...)`（`:306-312`），**id 自动派生**，无需手写纹理/模型。两处锚点见 §1(b)#4。**不动禁改的 `ModItems.java`** —— `SpawnEggItems.ITEMS` 早在 `ModItems.java:199` 挂上总线。
- **不动 `ModItems` 的验证**：8 条掉落表**全是空表** `LootTable.lootTable()`（1.20 `EntitySubProvider.java:843-849`），**不引用任何 `ModItems` 常量** ⇒ "1.20 引用的物品在 1.21 是否存在" 这一项**无缺项**。

### 3.6 音效 / 粒子 —— **零工作量**

`ModSoundEvents.java` 与 `ModParticleTypes.java` 在 `ce6daf602^..1780b9a88` 的 diff **均为空**（两端 blob hash 相同）。1.20 引用的 9 个音效常量 + `sounds.json` 条目 + `.ogg` 在 1.21 **全部已存在**。唯一新增粒子引用是原版 `ParticleTypes.EXPLOSION`（`WalkerWeapon.java:76`）。

### 3.7 其它 1.21 API 硬断裂（逐条 + 依据）

| 1.20 写法（file:line） | 1.21 写法 | 依据 |
|---|---|---|
| `protected void defineSynchedData()` 无参覆写 + `entityData.define(K,V)` | `protected void defineSynchedData(SynchedEntityData.Builder builder)` + `builder.define(K,V)`（或 `super.defineSynchedData(builder.define(...))`） | `Entity.java:342`（abstract，1.21 反编译源）；仓库样板 `ThrowableDropSelfProjectile.java:32-33`、`AbstractMountEntity.java:96-97`（**仓库自带该迁移说明**） |
| 命中点 | `MonsterLaser.java:80-82`、`TeslaTurret.java:71-74`、`RayGunner.java:63-66`、`MartianOfficer.java:44-48`、`WalkerWeapon.java:34-36`、`MartianWalker.java:114-119,418,463` | |
| `MultiBufferSource.immediate(new BufferBuilder(65536))` | `minecraft.renderBuffers().bufferSource()`（1.21 无 `new BufferBuilder(int)`，`immediate` 改收 `ByteBufferBuilder`） | `BufferBuilder.java:30`；仓库 0 处 `immediate`，样板 `WallOfFleshRenderer.java:82` |
| `consumer.vertex(pose, x,y,z).color(r,g,b,a).endVertex()` | `consumer.addVertex(pose, x,y,z).setColor(r,g,b,a)` | 1.21 `VertexConsumer` 无 `vertex(`/`endVertex()`；`LaserProjectileRenderer.java:71-72` 已是新写法；`WallOfFleshRenderer.java:183-185` |
| `event.getPartialTick()` 当 `float` 用 | `event.getPartialTick().getGameTimeDeltaPartialTick(false)` | 1.21 `RenderLevelStageEvent.getPartialTick()` 返回 `DeltaTracker`；样板 `WallOfFleshRenderer.java:89` |
| `Mob.finalizeSpawn(..., @Nullable CompoundTag)`（5 参） | 4 参版本 | `Mob.java:1192`；样板 `WaterBoltMimic.java:125`。命中 `Scutlix.java:49,50,145` |
| `getPassengersRidingOffset()` 覆写 | `getPassengerAttachmentPoint(Entity, EntityDimensions, float)`（1.21 已删前者） | `Entity.java:2085`；样板 `AbstractMountEntity.java:308`。命中 `Scutlix.java:144,204-206` |
| `setMaxUpStep(1.0F)` | `Attributes.STEP_HEIGHT` 属性 | 1.21 `Entity` 只有 `maxUpStep()` getter（`Entity.java:3624`）；仓库备注 `DeerClops.java:83-87`。命中 `MartianWalker.java:55` |
| `MeleeAttackGoal.checkAndPerformAttack(LivingEntity, double)` | `checkAndPerformAttack(LivingEntity)` | 命中 `MartianOfficer.java:93` |
| `.isPresent()` on registry holder | `.isBound()` | `DeferredHolder` 无 `isPresent()`；命中 `MartianEngineer.java:107`、`TeslaTurret.java:128`、`RayGunner.java:213`、`Scutlix.java:138` |
| `.get()` 当可空用 | 会抛 NPE ⇒ 紧随其后的 `if (x == null)` 变死代码 | `DeferredHolder.get()`；命中 `MartianEngineer.java:111,112`、`TeslaTurret.java:129,130`、`RayGunner.java:214`、`Scutlix.java:139` |
| `net.minecraftforge.*` | `net.neoforged.neoforge.*` | `PartEntity`(`PlantStemPart.java:10`)、`RenderLevelStageEvent`(`GameClientEvents.java:44`)、`EntityAttributeCreationEvent`(`ModEntities.java:9`) |

---

## 4. 禁区标注

### 4.1 10 个禁改文件的碰触情况

| 禁改文件 | 本批是否要改 | 证据 / 绕开方案 |
|---|---|---|
| `Confluence.java` | ❌ **碰不到** | 只**调用** `Confluence.createType`（`:154`，已存在）、`Confluence.asResource`（`:134`）、`Confluence.LOGGER`（`:49`） |
| `common/init/ModEntities.java` | ⚠️ **必须改 2 行** | 1.20 把两枚射弹注册在 `common/init/entity/ModEntities.java:60-61`（紧接 `EMPTY_ENTITY`）。1.21 同名位点是 `common/init/ModEntities.java:61`。**裁定 23 已解除**（`notes/PORT-LANDING-RECORD.md:1019`），限"只加这两条"。**绕开方案（若不解除）**：注册进 `MonsterEntities.ENTITIES`（`MonsterEntities.java:136`，已由 `ModEntities.java:380` 挂总线，且 `MonsterEntities` 已经在 `:1136` 用 `MobCategory.MISC` 注册 `BaseWormPart`）——**技术上完全可行**，代价是偏离 1.20 邻接 |
| `common/init/item/ModItems.java` | ❌ **碰不到** | 刷怪蛋走 `SpawnEggItems.ITEMS`，`ModItems.java:199` 已挂总线；本批无新物品 |
| `common/init/ModDataComponentTypes.java` | ❌ | 无组件 |
| `common/component/GunPropertyComponent.java` | ❌ | 无枪械 |
| `common/component/BulletPropertyComponent.java` | ❌ | 无子弹 |
| `common/item/fishing/BaitItem.java` | ❌ | 无钓鱼 |
| `common/block/functional/network/PathService.java` | ❌ | 无寻路网络 |
| `common/event/game/ServerEvents.java` | ❌ | 1.20 三个提交未改它（`git diff` 无此路径） |
| `common/init/item/SummonItems.java` | ❌ | 召唤物不属本批（392/393 明确排除） |

**净结论：9 个禁改文件中，只有 `common/init/ModEntities.java` 需要动，且已在裁定 23 下解除；其余 8 个本批完全碰不到。**

### 4.2 两枚射弹是否只能进 `ModEntities`？

不是。1.21 有两个可用的 `EntityType` 注册器：

| 方案 | 位置 | 证据 | 取舍 |
|---|---|---|---|
| **① 1.20 faithful（推荐）** | `common/init/ModEntities.java`，插在 `:61` 后 | `ModEntities.java:56` `ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Confluence.MODID)`；该文件是 1.21 射弹的规范归属地（`:313-357` 全是 `MobCategory.MISC` 射弹） | 需用禁改文件（已解除）。1.21 **无**私有 `register(String, Function)` 助手（只有 `registerBomb`/`registerHook`/`registerMinecart`/`registerStorageCompanion`），须写成 `ENTITIES.register("martian_electric_bolt", id -> EntityType.Builder.of(MartianElectricBolt::new, MobCategory.MISC).sized(0.25F,0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()))` |
| ② 绕开 | `MonsterEntities.ENTITIES`（`MonsterEntities.java:136`） | 同一 `Registries.ENTITY_TYPE`、同一 `Confluence.MODID`；`:1135-1137` 已有 `MobCategory.MISC` 非怪物先例；由 `ModEntities.java:380` 挂总线 | 零禁改风险；但语义上"怪物注册器里塞射弹"，且 `MonsterEntities` 已 1165 行、是热点文件 |

**建议 ①**（定论：采纳 ①，且不加任何助手——见 §6 裁定 6）。

---

## 5. 风险与坑

1. **并发改动（已实际发生）**：TARGET 在分析期间前进了 3 个提交。热点大文件：`ModTabs.java`(2067)、`ModChineseProvider.java`(4938)、`ModEnglishProvider.java`(1932)、`BestiaryLanguageSubProvider.java`(1567)、`ModClientEvents.java`(1359)、`MonsterEntities.java`(1165)、`ModClientBestiaryEntryProvider.java`(783)、`ModItemTagsProvider.java`(1609)、`GameClientEvents.java`(569)。⇒ 子批串行 + 限路径提交 + 每批开写前重取 HEAD 复核锚点。
2. **工作树有他人暂存的 3 个 license 文件**：`assets/confluence/LICENSE-CC-BY-NC-SA-4.0.txt`、`assets/confluence/license.bin`、`assets/confluence/textures/vfx/licenses/kenney-particle-pack.txt`（`git status` 显示 `A `）。另有未跟踪的 `textures/item/egg/demon_eye_spawn_egg.png`。⇒ 不得提交/修改。
3. **重名冲突：逐个核过，全部无同名成员。** 唯一"同名不同物"是 `JellyFish` 内部的 `ELECTRIFIED`（`JellyFish.java:52,122,210`，`EntityDataAccessor<Boolean>` + 私有 `CombatState`），与 `ModEffects.ELECTRIFIED` 不同命名空间，**不冲突**，但 grep/评审时极易误命中。
4. **`MartianEventHelper` 双仓缺失（最高风险）**：见 §0.2。这直接决定 C 批能否整体落地。按裁定 1 处理（删两处调用 + 留 TODO）。
5. **`RayGunnerLaser` 已不存在**：见 §0.3。若照裁定 23 原文去找 `RayGunnerLaser`，会找不到；裁定 23 已更正为 `MONSTER_LASER`。
6. **`ModTabs` 无锚点**：1.21 的 284 个刷怪蛋都不在创造标签页里，且 1.21 **没有** 1.20 的 `addSpawnEggTranslations`/刷怪蛋模型 datagen。⇒ 照搬 1.20 会产出（a）一个无 tab 引用的死 lang 键 `itemGroup.confluence.martian_entity`，（b）无 item model 的刷怪蛋。**这是现状的延续，不是本批引入的回归**，本批按裁定 3 跳过。
7. **`BestiaryLanguageSubProvider` 已有注释占位且文案不同**：1.21 `:501`（EN）/`:1272`（ZH）的 `martian_probe.desc` 是**注释掉**的（1.20 HEAD 是启用的）；`:574-583`/`:1345-1354` 已存在 `ray_gunner`/`martian_officer`/`scutlix`/`martian_walker` 的**注释占位**，且 EN 文案与 1.20 新增文案**不一致**（例：1.21:574 "The elite specialists in the Martian Invasion Force…" vs 1.20:513 "An elite Martian marksman that commonly rides a Scutlix…"）。盲目照搬会产生**重复插入**。按裁定 9 取 1.20 HEAD 版；`martian_probe.desc` 本批不动。
8. **`ModClientBestiaryEntryProvider` 无 `MARTIAN_PROBE` 锚点**：1.21 该文件里 `MARTIAN_PROBE` 0 命中，火星占位在 `:483-490`，`PIRATE_*` 也 0 命中。1.20 的 6 条真实条目是"贴在 `MARTIAN_PROBE` 之后"的连续块（`:515-520`），1.21 无此锚。`FilterEntry.MARTIAN_MADNESS` 本身**存在**（`FilterEntry.java:72`）。按裁定 4 原地替换 `:483-490`。
9. **`ModClientEvents` 的射弹渲染器邻接缺失**：1.20 锚在 `FROST_BEAM` 后（`:459`），但 1.21 的 `ModClientEvents` **完全没有注册 `LaserProjectileRenderer`**（`GASTROPOD_PROJECTILE`/`FROST_BEAM`/`FROST_BLAST` 在 1.21 用 `NoopRenderer` 或未注册），只有 `PrimeEnderDragonRenderer.java:75` 静态调 `LaserProjectileRenderer.renderBeam`。⇒ 不能按 1.20 邻接找位置，须自选（建议 `:630` `STORM_SPEAR_SHOT` 之后，复用 `SpearProjectileModels.STORM`）。
10. **`LaserProjectileRenderer` 的变种取色必须回移**：1.21 现有版本是 **pre-1780b9a88 的 83 行版**（无 `MonsterLaser` import、无单参 ctor、`render` 里直接 `renderBeam(..., innerColor, middleColor, outerColor)`）。`EntityRenderersEvent.RegisterRenderers#registerEntityRenderer` 每种实体只能注册一个渲染器，而 `MonsterLaser` 有两个变种配色（RAY_GUNNER `0xFFFFFF/0xFF5B4D/0xB50000`、MARTIAN_WALKER `0xFFFFFF/0x73DFFF/0x0877FF`）⇒ **必须把 1.20 的 variant 分支搬进 1.21 文件**，否则两条激光全白。
11. **`GameClientEvents` 缺 `isPaused()` 门**：1.20 的电击输入块在 `if (!minecraft.isPaused()) {` 内（1.20:186），1.21 `clientTick$Post`（`:173-197`）**没有这个门**（grep `isPaused` 0 命中）。⇒ 锚点改为 `:184` 前（`if (player != null)` 内），并注意 `MeteorLandingHandler.handle` 在 1.21 是 **2 参** `(minecraft, player)`，不能拿该行文本做锚。
12. **geo `identifier` 不统一**：6 个新文件是 `geometry.unknown`，既有 `martian_probe.geo.json` 是 `geometry.martian_probe`。功能无害（无一处 java 读它）。按裁定 7 **保持逐字节 `geometry.unknown`，不统一**。
13. **1.20 侧引用的物品/常量核对结果：无缺项。** 8 条掉落表全空；9 个音效常量全在；`LibEffects.CONFUSED`（`Confluence-Magic-Lib`）在；`ModEffects.{SHIMMER,ACID_VENOM,BLEEDING,BLOOD_BUTCHERED}` 全在。**唯一缺的是 `MartianEventHelper`（类是缺失的，不是常量）。**
14. **未跟踪/暂存污染**：`git status` 里 3 个 `A ` license 文件 + 1 个 `??` png。提交时务必 `git commit -- <限路径>`。

---

## 6. 需要用户裁定（已全部定论）

### 裁定 1 — `MartianEventHelper` 不存在（双仓都没有）

- **议题**：`MartianOfficer.java:80` / `MartianWalker.java:211` 调用 `MartianEventHelper.removeIfEventInactive(this)`，而该类在两仓均不存在。三选项：**(a)** 新建最小 `common/gameevent/MartianEventHelper` + 火星事件骨架（超出这 3 个提交范围）；**(b)** 先删掉这两处调用落地两个实体（行为差异：火星事件结束后走妖/军官不会自动消失）；**(c)** 把 `MartianOfficer`/`MartianWalker` 拆成 C2 子批推迟。
- **我的建议**：**(b)** 先行落地 + 留 TODO，并单列"火星事件本体"为后续批次。理由：本批定义是"这 3 个提交"，事件本体不在其中；且这两处调用是唯一的洞。
- **依据**：§0.2 完整证据链。

→ 定论（2026-10）: **先删掉 `MartianOfficer.java:80` / `MartianWalker.java:211` 两处调用并留 TODO**，实体照常落地；火星事件本体另立批次。**行为差异（须在文件里写明）**：火星事件结束后走妖/军官不会自动消失。

### 裁定 2 — 子批顺序

- **议题**：notes 28.2 的初步顺序是 A 资源 → B 实体+注册 → C 射弹。但实体**编译期**引用射弹（`RayGunner.java:20,213-216`、`TeslaTurret.java:19,128-133`、`MartianWalker.java:19,229-232`）。
- **我的建议**：改为 A → B(射弹+效果+网络) → C(实体+注册) → D(渲染) → E(lang)。
- **依据**：§2.1 依赖表。

→ 定论（2026-10）: **改为 A(已落地) → B 射弹+效果+网络 → C 实体+注册+刷怪蛋+掉落+事件接线 → D 渲染/客户端 → E lang/bestiary**（采纳依赖分析）。

### 裁定 3 — `ModTabs` 无锚点

- **议题**：1.21 既无刷怪蛋分节，也没有刷怪蛋模型 datagen。是否本批顺带补 1.21 的刷怪蛋分组与模型生成？
- **我的建议**：本批跳过 `ModTabs`（与 1.21 现有 284 个刷怪蛋的现状一致），把"刷怪蛋创造页 + 模型 datagen"单列新工单；`itemGroup.confluence.martian_entity` 亦不写。
- **依据**：`ModTabs.java:458` 附近 grep `egg`=0；`src/generated/.../models/item` 只有 1 个 `*_spawn_egg.json`。

→ 定论（2026-10）: **本批跳过**（与 1.21 现有 284 个刷怪蛋的现状一致）；刷怪蛋创造页 + 模型 datagen 另立工单；`itemGroup.confluence.martian_entity` **不写**。

### 裁定 4 — bestiary 6 条落点

- **议题**：1.21 无 `MARTIAN_PROBE` 锚点（`:857` 只在 loot 里），火星占位在 `ModClientBestiaryEntryProvider.java:483-490`。
- **我的建议**：原地替换 `:483-490` 中被实现的 6 个占位为真实 `.add(...)`，并把 1.20 用的 order（41300/41400/41500/41700/41900/42000）与 `FilterEntry.MARTIAN_MADNESS` 抄进来；不新增段落。
- **依据**：`.add(MonsterEntities.X, ...)` 只能在链式 builder 里，锚点必须落在既有链内。

→ 定论（2026-10）: **原地替换 `:483-490` 占位**，order/`FilterEntry.MARTIAN_MADNESS` 照 1.20 抄，**不新增段落**。

### 裁定 5 — lang 范围

- **议题**：1.21 完全缺 `entity.confluence.*` 怪物名段落（`ModChineseProvider`/`ModEnglishProvider` 里 0 处 `add(MonsterEntities...)`；`ModEnglishProvider.java:1731` 的自动命名只覆盖 `ModEntities.ENTITIES`）。是否本批新建该段落（会一次引入 7 个新 lang 键，且是"补历史欠账"而非本批内容）？
- **我的建议**：本批只加 7 条 `add(MonsterEntities.X.get(), "…")`（火星族）+ 2 条 `add(ModEntities.MONSTER_LASER/…)` + 1 条 `addEffect(ModEffects.ELECTRIFIED, …)`，不扩为全族回填；全族回填另立工单。
- **依据**：`ModEnglishProvider.java:1730-1731`；生成产物 `zh_cn.json` grep `entity.confluence.{green_slime,blood_crawler,martian_probe}` **全部 NOT FOUND**。

→ 定论（2026-10）: **只加火星族 10 条**（7 条实体名 + 2 条射弹名 + 1 条 `addEffect(ModEffects.ELECTRIFIED, …)`），**不做全族回填**。

### 裁定 6 — `ModEntities` 禁改解除的边界

- **议题**：裁定 23 只允许"加这两条射弹注册"。是否也允许加一个 `private static <E extends Entity> DeferredHolder<...> registerProjectile(String, EntityType.Builder<E>)` 小助手？
- **我的建议**：允许或不允许都可（两条 `ENTITIES.register(name, id -> ...build(id.toString()))` 各自展开成超长行，但 `ModEntities.java:313-357` 现有风格也全是内联写法，故不加助手同样自洽）。
- **依据**：`ModEntities.java` 无 `register(String, Function)`；`:290-311` 只有 4 个专用助手。

→ 定论（2026-10）: **不加任何助手**，两条注册按该文件既有内联风格写（严守裁定 23 的"只加这两条"）。

### 裁定 7 — geo `identifier` 不统一

- **议题**：6 个新 geo 是 `geometry.unknown`，既有 `martian_probe.geo.json` 是 `geometry.martian_probe`。
- **我的建议**：统一改为 `geometry.<name>`（第 6 行），纯风格、零风险；或明确"保持 unknown"。
- **依据**：两仓 java grep `"identifier"`/`geometry.unknown` = 0；渲染器走 `ExplicitGeoModel` 显式路径。

→ 定论（2026-10）: **保持逐字节 `geometry.unknown`**（用户裁定，不统一）。

### 裁定 8 — 裁定 23 措辞过期

- **议题**：写的是 `RayGunnerLaser`，实际是 `MonsterLaser`。是否把裁定原文更正？
- **我的建议**：更正文书，避免后续工单按 `RayGunnerLaser` 找不到东西。
- **依据**：§0.3。

→ 定论（2026-10）: **已更正**为 `MARTIAN_ELECTRIC_BOLT` + `MONSTER_LASER`（原文的 `RayGunnerLaser` 作废）。

### 裁定 9 — `BestiaryLanguageSubProvider` 文案冲突

- **议题**：1.21 已有 4 条注释占位（EN/ZH 各 4），EN 文案与 1.20 新增文案**不同**。取哪版？
- **我的建议**：取 1.20 HEAD 版文案（本批目标是"搬 1.20 这批"），启用为正式条目；同时决定 `martian_probe.desc`（1.21 注释掉）是否一并启用。
- **依据**：`BestiaryLanguageSubProvider.java:501,574-583,1272,1345-1354` vs 1.20 `:432,513,1222,1303`。

→ 定论（2026-10）: **取 1.20 HEAD 版文案**；`martian_probe.desc`（1.21 里被注释）**本批不动**。

---

## 附：执行前自检清单（每条都可直接跑）

```powershell
# 0) 基线
git -C D:\Minecraft\1.21neoforge\confluence rev-parse HEAD          # 期望 67a2fb7b8 或更新
git -C D:\Minecraft\1.21neoforge\confluence diff --name-only <base> HEAD -- "*.java"
git -C D:\Minecraft\1.21neoforge\confluence status --porcelain      # 确认 3 个 license 仍在暂存区

# 1) 禁区是否被误碰（每次提交前）
git -C D:\Minecraft\1.21neoforge\confluence show --name-only --format="" HEAD

# 2) 锚点复核（逐个 grep，任一偏移即重定位）
#    ModEntities:61  MonsterEntities:899/900/902  ModEffects:165  SpawnEggItems:212/302/304
#    NetworkEvents:69  GameEventSystem:252-258  EntitySubProvider:857
#    ModClientEvents:184/630/936  GameClientEvents:80/104/184/395/396
#    ModChineseProvider:328/1761/3665/4899  ModEnglishProvider:1730-1731
#    ModClientBestiaryEntryProvider:483-490

# 3) 残留检查（子批落地后）
git -C D:\Minecraft\1.21neoforge\confluence grep -n "MartianEventHelper\|RayGunnerLaser\|isPresent()\|geckolib\.core\." -- "ConfluenceOtherworld/src/main/java"
```


---

## §7 更正（2026-10，主代理追加）

**§5 风险清单里关于"8 个 geo JSON 带 UTF-8 BOM / 单行 minified JSON"的描述有误，已作废。**

实测（清理工单写手逐字节核过，主代理复核）：
- 这 8 个文件（`geo/entity/animal/{bunny,explosive_bunny}.geo.json`、`geo/entity/boss/{eater_of_worlds,eater_of_worlds_segment,eater_of_worlds_tail,hill_of_flesh,queen_bee,skeletron}.geo.json`）
  **没有 BOM**、**不是 JSON**（`json.loads` ×8 失败）；
- 它们是 **Huffman + Vigenère 加密的模型载荷**（`confluence.mixins.json:151` → `FileLoaderMixin` → `ModClientSetups`
  （`!result.startsWith("{")` ⇒ 加密）→ `SecurityFace.S3`，密钥来自 `license.bin`）；载荷格式 `<哈夫曼树>|<位串>`，
  树叶 token 为 `L<字符>,<频次>,`；
- 文件里那唯一的裸 CR / 裸 LF **就是哈夫曼树叶上的 CR/LF 字符**（频次恒成对相等 = 原文 JSON 的 CRLF 计数），
  文件本身没有任何行尾；git 因裸 CR 判其为二进制；
- **对它做任何 EOL/BOM/格式化处理都会破坏解密**（+3 字节、移位 Vigenère 密钥流、触发"位串须停在叶边界"校验）。
- 全仓唯一真正带 BOM 的是 `tools/port2native/rules/{callsites,event-bus}.json`（`gen-rules.ps1` 生成、读取方用 `utf-8-sig`）⇒ **不要动**。

**执行口径**：`assets/confluence/geo/**/*.geo.json` 中首字节非 `{` 的加密文件，一律不做 EOL/BOM/格式化处理；
`fix_eol.py` 自动跳过它们是正确行为，不加 `.gitattributes`、不改工具。
