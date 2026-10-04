# 批次 25（NPC 框架 + 枪械 G3′ 弹幕层）：**已收口**，逐轮过程记录（交接/复盘用）

> ✅ **状态**：编译门 **0 错误 / 0 文件**（主模组与 Magic-Lib 都是），PortLib 残留 0 处，
> 已按标准循环落地（Magic-Lib 子模块先提交、根仓库单独提交）。
>
> ⚠️ **开工前请先读第十三节 13.1**：前 12 轮一直被 javac 的 `-Xmaxerrs` 默认值（100）误导 ——
> 「100 错误 / 46 文件」是**截断的下界**，真实规模是 **150 错误 / 65 文件**。
> 大批次请用 `build_errors.py --maxerrs 2000`（`tools/port2native/README.md` 过程纪律 3b）。
>
> 下面第一~十二节是**逐轮过程记录**（含当时的错误图与判断），保留原文以便复盘 ——
> 其中的错误数一律是当时工具口径（即可能被截断）。**最终结论、偏差清单与后续影响看第十三节。**
> 各节的暂存命令仍可复现（工作树即使丢失也能从 1.20 侧重做）。


## 一、已经做完的（工作树里）

| 动作 | 内容 | 可复现命令 |
|---|---|---|
| 暂存 54 个 npc 文件 | `common/entity/npc/**` 全部缺失文件（3746 非空行），`uncovered=[]` | `stage_batch.py --name wp25-npc --file <54 个>` |
| 暂存 `NpcEntities` | `common/init/entity/NpcEntities`（228 非空行 / 42 个条目） | `--name wp25-npcents` |
| **NPCSpawner 真迁移** | `git mv data/saved/NPCSpawner.java → data/spawner/NPCSpawner.java`（git 记为 **RM**）+ 用 1.20 内容（967 非空行）覆盖 | `git mv` + `--name wp25-spawner` |
| 17 处引用改指 | 脚本 `%TEMP%\migrate_npcspawner_pkg.py`：所有引用 `data.saved.NPCSpawner` 的文件改指新包（脚本顺带校验「只在 import/全限定用法上命中」） | 见脚本 |
| 暂存 `BaseSlime` + `SweetSlime` | `common/entity/monster/slime/**`（753 + 64 非空行）—— 因为 `TownSlimeNPC` 要 `BaseSlime`，而 1.21 没有这个包 | `--name wp25-bases` / `--name wp25-sweet` |
| 修转换器缺陷（第 4 例） | `NPCMood:78` 的 `unwrapKey()()` → `unwrapKey()`（某条规则二次改写） | 手工 |
| `NPCCombatActions` 改指 | 指向 **1.21 现有的** `projectile.range.arrow.BaseArrowEntity` + `ModEntities.ARROW_PROJECTILE`（不造副本，见第三节） | 手工 |
| `HouseHandler` / `BossDelaySpawner` 补 import | NPCSpawner 搬家后，同包（`data/saved`）的裸引用要显式 import 新包 | 手工 |
| 删除误暂存的重复类 | `common/entity/projectile/arrow/BaseArrowEntity.java`（已删；原因见第三节） | — |

## 二、开工测量（这些结论是准的）

```powershell
python tools/port2native/missing_files.py --src120 <1.20 src/main/java> `
  --root121 ConfluenceOtherworld/src/main/java --sub common/entity/npc      # 54 个缺失文件
python tools/port2native/dep_subset.py … --seed …monster.slime.BaseSlime     # {"candidates":2} = BaseSlime + SweetSlime
# check_duplicates：55 个文件（54 + NPCSpawner）→ 0 处「疑似移动/重复」，
#   12 条 DIFF：11 条是 TerraEntity 同名旧形态（随退役删），1 条是 NPCSpawner 的包移动（已按真迁移处理）
```

**关键好消息**：1.20 的 `NpcEntities`（42 个条目）注册的**全是主模组类**（`SimpleNPC` + 11 个专类），
**没有任何 TerraEntity 引用** → 可以整篇搬，不需要写「增量版」。

## 三、两个新发现的「移动 vs 新增」陷阱（政策要求写明）

1. **`NPCSpawner`**（已正确处理）：1.21 旧版在 `data/saved/`（743 行，被 17 个文件引用），
   1.20 在 `data/spawner/`（1052 行重写版）→ 本批用 `git mv` + 换内容 + 改引用（**真迁移**，git 记为 RM）。
   附带发现：1.21 旧版引用 `TEBossEntities`，换成主模组版后该依赖消失。
2. **箭子树**（本批**不做**，只改指）：1.21 是旧布局 `common/entity/projectile/**range**/arrow/`（4 个类，
   实体 id `arrow_projectile`），1.20 是新布局 `common/entity/projectile/arrow/`（**14 个类**，id 改成 `arrow`），
   且有 **14 个文件**引用旧包 → 这是一次**独立的内容批**（要连 14 处引用一起改）。
   本批**只为 `NPCCombatActions` 改指到既有类**，并把说明写在该文件里；
   等箭那一批落地时把这一处一并改指。⚠️ 我一度把 1.20 的 `BaseArrowEntity` 暂存到新包
   —— 那正好会造出「两个同名类」，已删除（这正是 `check_duplicates.py` 存在的意义）。

## 四、当前错误图（100 / 38 文件）与下一轮的动作

| 文件 | 错误数 | 下一轮做什么 |
|---|---:|---|
| `common/init/entity/NpcEntities` | 41 | 逐个对齐 1.21 的 `register(...)` 辅助签名与属性 API（1.20 用 `register(name, supplier, weapon, attack, settings, w, h)` 私有重载） |
| `common/data/spawner/NPCSpawner` | 19 | ① `PartyGameEvent` 未 import（同类名同包问题）；② `AttributeModifier` 是 record（`amount()/operation()`）；③ 其余成员级差异 |
| `common/entity/npc/BaseNPC` | 3 | `PortDataResultExtension.ifSuccess` → 原生 `DataResult#ifSuccess`（leftovers 里已列出 5 处） |
| `common/entity/npc/ai/NPCHealGoal`、`trade/NPCTradeMenu` | 2+2 | 逐个看 `error_detail.py` |
| 其余 34 文件 | ~40 | 同上，按 `error_detail.py` 顺序推 |

**顺序建议**：先 `NpcEntities`（41 处，是枢纽）→ 再 `NPCSpawner`（19）→ 再 `BaseNPC`（3，leftovers 说明明确）
→ 其余按错误数从多到少推 → 最后做**约 21 处击杀判据改 id 版**（`NPCSpawner` 7 / `NPCCombatProgress` 11 / `NurseNPC` 3，
前置 `5d83b5236` 的 `KillBoard.isDefeated(ResourceLocation)` 已就位）→ 编译到 0。

**还差一步接线**：`NpcEntities.ENTITIES.register(eventBus)` 要加进 `Confluence` 构造器
（与 `MonsterEntities`/`CritterEntities`/`BossEntities` 并列）。

**出口**：编译 0 错误后按标准循环收口（notes + `fix_eol` + 单独提交）。这一批落地即解开 WP4 整层，
并连带解开 WP2 剩余 18 怪物 + 40 动物与枪械 G3′（`BaseBulletEntity -> BaseNPC -> NPCSpawner` 那条链）。

---

## 五、goal round 5 进展

### 5.1 已完成

| 项 | 结果 |
|---|---|
| `NpcEntities` 的 41 处 | ✅ **清零**。改法（脚本 `%TEMP%\fix_npcentities.py`）：`RegistryObject<X>` → `DeferredHolder<EntityType<?>, X>`（39 处）、`PortDeferredRegisterExtension.register(ENTITIES, name, fn)` → `ENTITIES.register(name, fn)`、`withAttributes` 宿主包改 `common.init.ModEntities` |
| `PortDataResultExtension.ifSuccess(r, c)` 11 处 | ✅ 改成原生 `r.ifSuccess(c)`。**含一个我自己的错误**：先误改成 `.result().ifPresent(...)`，但 1.21 **确实有** `DataResult#ifSuccess`（本仓库既有文件 `DemonEye`/`Zombie` 的注释早就写明），已改回并还原被误改注释的那两个文件 |
| 暂存 `NPCProjectileEffects` | ✅ 88 非空行（闭包 2、闸门 0 重复） |
| 暂存 `PartyGameEvent` + `NPCServiceMenu` | ✅ 两者闭包都只有自己 |
| `NPCCombatActions` 的箭改指 | ✅ 指 1.21 现有 `range.arrow.BaseArrowEntity` + `ModEntities.ARROW_PROJECTILE` |

错误分布：`NpcEntities` 41→0、`NPCSpawner` 23→19、`BaseNPC` 23→18（总数 100/39 文件，深层文件开始暴露）。

### 5.2 本轮查实的剩余缺口（下一轮处理）

| 缺口 | 事实 |
|---|---|
| `OpenNPCDialogPacketS2C` | 1.21 没有；闭包 **6 文件**（`BaseNPC:419` 要用）→ 整簇暂存 |
| `NPCReforgeMenu` | 1.21 有但**构造器形状不同** → 按 1.20 对齐 |
| `HouseHandler`（1.21 现有） | 它的 API 吃 **`AbstractTerraNPC`**（TE 类），新 NPC 层是 `BaseNPC` → `BaseNPC:275/289`、`NPCSpawner:518` 报 incompatible types → 需搬 1.20 版 `HouseHandler` |
| `AchievementUtils.noHobo` | 同样吃 TE 的 `AbstractTerraNPC` → 按 1.20 对齐 |
| `ModEffects.DRYADS_BLESSING` | 1.21 只有 `DRYADS_BANE` → 按 1.20 补（注意效果层已按 WP6a 搬进 Magic-Lib） |
| `AttributeModifier` | 1.21 是 record（`(ResourceLocation, double, Operation)` + `removeModifier(ResourceLocation)`）；1.20 用 `UUID` + `rl2uuid` → 共 7 处（`BaseNPC:387-401`、`NPCSpawner:978-989`） |
| `NPCCombatActions` 17 处 | 箭那 14 个类的 API（构造器不匹配）→ 下一轮定：并入箭子树批，或在本类改用 1.21 现有构造器 |

### 5.3 ⚠️ 索引陷阱（真实踩到，已修正）

上一轮的 `git mv` 会把改名**直接放进索引**；之后用不带路径的 `git commit` 提交文档就会连带提交那个改名
（HEAD 因此编译不过）。本轮用 `git reset --soft` + `git reset` 修正了。
**规矩：提交文档/笔记时显式 `git add <路径>` + `git commit`，提交前先 `git status` 确认索引为空。**

---

## 六、goal round 6 进展

### 6.1 已完成

| 项 | 结果 |
|---|---|
| **约 25 处击杀判据 id 化** | ✅ 全部完成（路线 B′ 的核心动作）：`NPCSpawner` 8、`NPCCombatProgression` 12、`NurseNPC` 5 → 全部改成 `KillBoard.isDefeated(Confluence.asResource("plantera"))` 形式（脚本 `fix_killgates.py` / `fix_killgates2.py`，**只改这两个方法的实参**，`new Skeletron(BossEntities.SKELETRON.get(), …)` 那种真类型用法原样保留；脚本自带混用校验） |
| **`AttributeModifier` 7 处** | ✅ 改成 1.21 record 形状：`new AttributeModifier(ResourceLocation, amount, Operation)`；`BaseNPC` 的 `removeAttributeModifier` 形参改 `Holder<Attribute>`（`getAttribute` 在 1.21 吃 Holder）、删除 `rl2uuid` |
| 暂存 `OpenNPCDialogPacketS2C` 簇（6 文件） | ✅ 2 个客户端屏（`NPCDialogScreen`/`GoblinTinkererDialogScreen`）+ 3 个 C2S 包 + 1 个 S2C 包 |
| 暂存 `DryadsBlessingEffect` + 注册 `ModEffects.DRYADS_BLESSING` | ✅ 1.21 原本只有 `DRYADS_BANE` |

错误收敛：`NPCSpawner` 19→**≈8**（判据全清 + AttributeModifier 清）；总文件数 45→**44**（上限 100 封顶，
所以看**文件数**更准；深层文件仍在陆续暴露）。

### 6.2 下一轮的顺序建议（按当前错误数）

| 顺序 | 目标 | 备注 |
|---:|---|---|
| 1 | `NPCCombatActions`（17） | 箭 API：决定「并入箭子树批」还是「改用 1.21 现有 `range.arrow` 构造器」；`BoomerangItems` 的 import 路径也要核 |
| 2 | `AnglerNPC`（13） | 含 `getDimensions(Pose)` 覆写冲突（1.21 该方法是 final → 改 `EntityDimensions`/`getDefaultDimensions`？需查 1.21 先例） |
| 3 | `NPCSpawner` 剩余（≈8） | `LibCodecUtils.reference2BooleanMap`、`HouseHandler.INSTANCE.*`（TE 类型）、`ModTags.Items.BULLET/GUN`、`LibDateUtils._12$00` |
| 4 | **`HouseHandler` + `AchievementUtils.noHobo`** | 两者吃 TE 的 `AbstractTerraNPC` → 搬 1.20 版（`HouseHandler` 是既有文件，**必须先 diff 再合并**，不许盲覆盖） |
| 5 | 其余 40 余文件 | 按 `error_detail.py` 逐个推 |
| 6 | 收口 | 接线 `NpcEntities.ENTITIES.register(eventBus)` → 编译 0 → notes + `fix_eol` + 单独提交 |

### 6.3 工作树规模

当前 WIP：56 个新增文件（npc 54 + NpcEntities + NPCProjectileEffects + 3 个杂项 + 7 个对话簇……）、
1 个改名（NPCSpawner）、20 余个既有文件改动（其中 17 个是 NPCSpawner 包移动的引用改指）。
**这些都不在 HEAD 里**（HEAD 只到 `0754220ec` 的笔记）；续跑清单见本文件第一节与 6.2。

---

## 七、goal round 7 进展

### 7.1 已完成

| 项 | 结果 |
|---|---|
| **弹幕层并入**（`NPCCombatActions` 需要的 4 个类） | ✅ 暂存 `NPCWeaponProjectile` / `CyborgExplosiveProjectile` / `BoomerangProjectile` / `BoomerangItems` / `BoomerangItem`（5 文件 / 517 非空行，闭包自包含）。闸门：`BoomerangProjectile` 判 **DIFF**（TerraEntity 有同名旧形态 `terraentity.entity.proj.BoomerangProjectile`）→ 属既定「TerraEntity 退役」过渡态，**旧实现随退役删除** |
| **补 3 个 `ModEntities` 成员** | ✅ `NPC_WEAPON_PROJECTILE` / `CYBORG_EXPLOSIVE` / `BOOMERANG_PROJECTILE`（1.20 `ModEntities:189/190/196` 逐字）—— 又一次**成员级盲区**（三个类搬进来了，但实体类型成员此前不存在） |
| **4 个网络包原生化** | ✅ 按 WP5 那套改写：`implements IPacketS2C` / `IPacketC2S`（1.21 的 `CustomPacketPayload` **没有 `S2C`/`C2S`**）、`ID` → `TYPE = Confluence.createType(...)`、`identifier()` → `type()`；并在 `NetworkEvents` 里注册 4 条 handler（1 条 `playToClient` + 3 条 `playToServer`） |

收敛：文件数 47 → **43**；`NPCCombatActions`(17) 已从榜首消失、`BaseNPC` 10 → 9。

### 7.2 下一轮顺序（更新）

| 顺序 | 目标 | 备注 |
|---:|---|---|
| 1 | `AnglerNPC`（15） | ① `getDimensions(Pose)` 覆写冲突（1.21 该方法是 final，需查 1.21 先例：`EntityDimensions`/`getDefaultDimensions`）；② `ResourceKey<LootTable>` → `ResourceLocation`（1.21 的战利品表 API 变了） |
| 2 | `BaseNPC`（9） | `NPCReforgeMenu` 构造器形状、`AbstractTerraNPC` 两处（属 HouseHandler/AchievementUtils）、`:592` 的覆写冲突 |
| 3 | `NPCSpawner` 剩余 | `LibCodecUtils.reference2BooleanMap`、`HouseHandler.INSTANCE.*`、`ModTags.Items.BULLET/GUN`、`LibDateUtils._12$00` |
| 4 | **`HouseHandler` + `AchievementUtils.noHobo`** | 搬 1.20 版（`HouseHandler` 是既有文件，**先 diff 再合并**） |
| 5 | 其余 40 文件 | 按 `error_detail.py` 推 |
| 6 | 收口 | 接线 `NpcEntities.ENTITIES.register(eventBus)` → 编译 0 → notes + `fix_eol` + 单独提交 |

---

## 八、goal round 8 进展

### 8.1 已完成

| 项 | 结果 |
|---|---|
| `AnglerNPC` 的维度覆写 | ✅ `getDimensions(Pose)` 在 1.21 是 **final**（`Entity.java:3423`）→ 改覆写 `getDefaultDimensions(Pose)`（protected），写法照本仓库既有先例 `Hoplite.java:46`、`BaseLivingBossPart.java:245-252` |
| `OpenAnglerDialogPacketS2C` | ✅ 暂存 + 原生化（`IPacketS2C` + `TYPE` + `type()`），并在 `NetworkEvents` 注册 `playToClient` |
| **`HouseHandler` 换装新架构** | ✅ 用 **1.20 版**（189 行，形参是主模组 `BaseNPC` + 主模组 `House`）替换 1.21 那份 **TE 时代形态**（91 行，形参是 TE 的 `AbstractTerraNPC` + TE 的 `House`）。差异实测 22→120 行；`stage_batch` 报 OVERWRITE，按纪律**先 diff 再合并**（1.21 调用方 `NPCSpawner`/`BaseNPC` 的方法名一致，故可直接对齐） |
| `AchievementUtils.noHobo` | ✅ 形参 `AbstractTerraNPC` → **`BaseNPC`**（两侧方法体逐字相同，只是 1.21 那时主模组还没有 NPC 层） |

收敛：`BaseNPC` 9 → **5**（`AbstractTerraNPC` 两处已消失）、`NPCCombatActions` 17 → **6**、`AnglerNPC` 已不在榜首。

### 8.2 下一轮顺序

| 顺序 | 目标 | 备注 |
|---:|---|---|
| 1 | `TownSlimeNPC`（9） | `[53-56]` 一组「cannot find symbol」——多半是 `BaseSlime` 的成员/构造器与 1.21 现有 `MonsterEntities` 成员（成员级盲区） |
| 2 | `NPCCombatActions`（6） | 剩下的是 `BaseBulletEntity` 构造器（`GunItems.MUSKET_BULLET` 那条链）→ 建议临时指 TE 的 `BaseBulletEntity`（枪械 G3′ 会带主模组版过来），或并入枪械批 |
| 3 | `BaseNPC`（5） | `NPCReforgeMenu` 构造器形状、`:403`/`:454` 的成员差异 |
| 4 | `NPCSpawner` 剩余 | `LibCodecUtils.reference2BooleanMap`、`ModTags.Items.BULLET/GUN`、`LibDateUtils._12$00` |
| 5 | 其余 40+ 文件 | 按 `error_detail.py` 推 |
| 6 | 收口 | 接线 + 编译 0 + 标准循环 |

### 8.3 备注：`PlayerSpecialData` 的缺口（本轮查实，供后续批次）

`AnglerNPC` 用到的 `hasCompletedAnglerQuestToday` / `markAnglerQuestCompleted` / `getAnglerQuestCount`
**不在** `AnglerData`（两侧都没有），而在 **`PlayerSpecialData`**（1.21 版缺这三个方法）→
属「1.21 那份更窄」的既有形态，下一轮随 `BaseNPC`/`AnglerNPC` 一起补（既有文件，先 diff 再合并）。

---

## 九、goal round 9 进展

### 9.1 已完成

| 项 | 结果 |
|---|---|
| `TownSlimeNPC`（9） | ✅ 清零。它的 9 处是 `MonsterEntities.{BLUE,GREEN,RED,PURPLE,YELLOW,ICE,JUNGLE,DESERT,PINK}_SLIME` 比对，而 1.21 的 `MonsterEntities` **一个史莱姆成员都没有**（整族属 WP2 史莱姆批，26 个成员要连 `registerSlime(...)` 辅助一起搬）→ 本批改用**与 Boss 判据同源的按注册 id 判等**（`isStandardSlime(...)` + `STANDARD_SLIME_PATHS`）：语义等价（`type.getKey()` 就是 `confluence:blue_slime` 这些 id；史莱姆族落地前这些 id 不存在，与「成员不存在」等价；落地后自动生效），**⚠️ 史莱姆批落地时改回成员比对**（注释与本节都写明） |
| `BaseNPC` 的两处 | ✅ ① 补 `net.minecraft.core.Holder` import（`removeAttributeModifier(Holder<Attribute>, ResourceLocation)` 要用）；② `NetworkHooks.openScreen(...)` → 原生 `player.openMenu(provider, buf)`（NeoForge 21.1 已删 `NetworkHooks.openScreen`） |

收敛：`TownSlimeNPC` 9 → 0、`BaseNPC` 退出榜首。当前榜首：`NPCCombatActions`(6)、`AnglerNPC`(5)、
`BoomerangProjectile`(4)。

### 9.2 下一轮顺序

| 顺序 | 目标 | 备注 |
|---:|---|---|
| 1 | `NPCCombatActions`（6） | `BaseBulletEntity` 构造器（`GunItems.MUSKET_BULLET` 链）+ `projectile.setDamage` + `ModEntities.NPC_SHADOWFLAME_SKULL`（成员级）→ 建议临时指 TE 的 `BaseBulletEntity`（枪械 G3′ 带主模组版过来后回填），或用同源 id/成员策略 |
| 2 | `AnglerNPC`（5） | `ResourceKey<LootTable>` ↔ `ResourceLocation`（1.21 战利品表 API）+ `PlayerSpecialData` 的三个方法（见 8.3） |
| 3 | `BoomerangProjectile`（4） | `:59/136/186` cannot find symbol + `CompoundTag` → `Provider`（1.21 `valueInput`/`HolderLookup.Provider`） |
| 4 | `NPCSpawner` 剩余 | `LibCodecUtils.reference2BooleanMap`、`ModTags.Items.BULLET/GUN`、`LibDateUtils._12$00` |
| 5 | 其余 40+ 文件 | 按 `error_detail.py` 推 |
| 6 | 收口 | 接线 `NpcEntities.ENTITIES.register(eventBus)` + 编译 0 + 标准循环 |

---

## 十、goal round 10 进展：**枪械 G3′ 主体（弹幕/子弹层 21 文件）并入本批**

### 10.1 为什么并入

`NPCCombatActions` 的 6 处错误里有 4 处指向 `BaseBulletEntity`（1.20 主模组版）—— 而那一层
（`BaseBulletEntity` + `CustomBulletEntity` + `BaseBullet` + `BulletEvent` + `BulletBehavior` + 15 个 behavior
+ `BulletDefinition`）正是**枪械内联迁移 G3′ 的主体**：

```powershell
python tools/port2native/dep_subset.py … --seed …projectile.BaseBulletEntity --seed …item.BaseBullet
# {"candidates": 21, "kept": 21, "new": 21, "removed": 0}
```

`BaseBulletEntity` 的链里有 `BaseNPC`（本批已有）→ **两处阻塞一次解开**（NPC 层 + 枪械 G3′）。

### 10.2 闸门结论（政策要求逐条写明）

`check_duplicates.py` 对这 21 个文件的判定：**NEAR 12 / MOVE 2 / DIFF 7 → 14 处「疑似移动/重复」**。
原因是 **TerraGuns 子模块里有同名旧实现**（`terra_guns.common.{definition,combat,component,entity.bullet}` 等）。
处置口径（与坐骑/骷髅王/NPC 三批一致）：

- 这是**用户已裁决的「枪械内联迁移」**（`notes/GUNS-INLINE-MIGRATION.md`），属既定的**先加后删**：
  **旧实现在 TerraGuns 子模块，随 G6′（gitlink 移除）一并删除**；
- **不许**说成「1.21 侧此前不存在」；
- 同一批里主模组对 `terra_guns.*` 的引用**仍未改指**（`ItemEvents`/`ModGunProperties`/`ManaGunItem`/`StarCannonItem` 等），
  这是 G2′/G3′ 的收尾项（`ItemEvents` 的事件形状要按 1.20 重写、`ModGunProperties` 绑 TE 的 `TGGunSounds`），
  **下一轮或枪械专用批处理**。

### 10.3 本轮其他动作与收敛

| 项 | 结果 |
|---|---|
| 暂存 21 文件 | ✅ 1079 非空行 |
| 补 2 个 `ModEntities` 成员 | ✅ `BASE_BULLET_ENTITY`（`base_bullet`）/ `GRAVITY_BULLET_ENTITY`（`gravity_bullet`），1.20 `ModEntities:656/657` 逐字；**无 id 冲突**（实测 1.21 侧没有这两个 id） |
| `NPCCombatActions` | ✅ 已退出榜首（`BaseBulletEntity` 类就位） |
| `BaseBulletEntity` 自身 | 14 → 12 处（`CompoundTag` → `HolderLookup.Provider`、若干成员差异）待下一轮 |

### 10.4 下一轮顺序

1. `BaseBulletEntity`（12）—— `:435` `CompoundTag` → `HolderLookup.Provider`、`:450` 覆写冲突、`:381-409` 的成员差异
2. `AnglerNPC`（5）—— 战利品表 API + `PlayerSpecialData` 三个方法（8.3）
3. `NPCSpawner` 剩余 —— `LibCodecUtils.reference2BooleanMap`、`ModTags.Items.BULLET/GUN`、`LibDateUtils._12$00`
4. 其余 45+ 文件 → 接线 → 编译 0 → 收口

---

## 十一、goal round 11 进展

### 11.1 已完成

| 项 | 结果 |
|---|---|
| `BulletEvent` 的 2 处 `@Cancelable` | ✅ → `implements ICancellableEvent`（`HitEvent` / `DamageEntityEvent`；与 `GunEvent` 同一修法，注解在 NeoForge 21.1 已删） |
| `BaseBulletEntity` 的 4 处 `PortEventHandler.postEvent(...)` | ✅ → `NeoForge.EVENT_BUS.post(...)`（PortLib → 原生事件总线） |
| `BaseBulletEntity` 的序列化 | ✅ `ItemStack.of(CompoundTag)`（已删）→ `ItemStack.parseOptional(level().registryAccess(), tag)`；`save(new CompoundTag())` → `save(level().registryAccess(), new CompoundTag())`（1.21 的 `save` 要 `HolderLookup.Provider`） |

收敛：`BaseBulletEntity` 12 → **3**；总文件数 48 → 47。当前榜首：`AnglerNPC`(5)、`BoomerangProjectile`(4)、
`NPCSpawner`(4)。

### 11.2 下一轮（最后一轮 goal round）建议

`BaseBulletEntity` 剩 3 处都是**成员级**：
- `LibUtils.knockBackA2B(...)`（Magic-Lib 缺该成员，或 1.21 换了名字）
- `GunItems.DUMMY_BULLET`（1.20 `GunItems:119` 的 `new BaseBullet.Dummy(...)`；`BaseBullet.Dummy` 已在本批落地，**补成员即可**）
- `:451` 的覆写冲突（看是哪个父类方法被收窄）

然后按 `AnglerNPC`(5) → `BoomerangProjectile`(4) → `NPCSpawner`(4) → 其余 40+ 文件的顺序继续。
**注意**：批次 25 是一个 100+ 文件的大批（含枪械 G3′ 主体），预计还需要多轮才能编译到 0；
每轮的进度都可在本文件对应小节查到，工作树 WIP 不在 HEAD 里。

---

## 十二、goal round 12 进展（本批最后一次 goal 轮）

### 12.1 已完成并**已提交**（子模块 `b69ba36`）

| 项 | 结果 |
|---|---|
| Magic-Lib 补 `LibUtils.knockBackA2B` + `LibMathUtils.getVectorA2B` | ✅ 子模块提交 **`b69ba36`**（根仓库 gitlink 随本批提交更新）。1.20 侧分别在 `LibEntityUtils:166`（别名表把 `LibEntityUtils` 等价到 `LibUtils`）与 `LibMathUtils:502`，逐字搬运；这是**唯一**挡住 `BaseBulletEntity` 的成员 |
| 编译门 | ✅ `--module Confluence-Magic-Lib`：**0 错误**（首轮 7 处缺 import 已补）；主模组 `BaseBulletEntity` 随之清零 |

### 12.2 工作树里本轮另外完成的（未提交）

| 项 | 结果 |
|---|---|
| `BaseBulletEntity` 的 `getAddEntityPacket()` | ✅ → `getAddEntityPacket(ServerEntity)`（1.21 多一个形参）+ 补 `ServerEntity` import |
| `GunItems.DUMMY_BULLET` | ✅ 补成员（1.20 `GunItems:119` 逐字，`BaseBullet.Dummy` 已在本批落地）；并注明 **1.21 的 `GunItems` 目前只有 `STAR_CANNON`**，整层属**枪械 G4′（注册层）** |

细化：`BaseBulletEntity` **12 → 1 → 0**（最后一次 goal 轮内清完）。

### 12.3 交接：批次 25 的剩余工作（下一批从这里继续）

当前编译门：**100 错误封顶 / 46 文件**（错误数被工具的输出上限截断，看**文件数**更准）。

| 顺序 | 目标 | 备注 |
|---:|---|---|
| 1 | `AnglerNPC`(5) | ① `ResourceKey<LootTable>` ↔ `ResourceLocation`（1.21 战利品表 API）；② `PlayerSpecialData` 的 `hasCompletedAnglerQuestToday`/`markAnglerQuestCompleted`/`getAnglerQuestCount`（1.21 版缺，既有文件先 diff） |
| 2 | `BoomerangProjectile`(4) | `:174` `CompoundTag` → `HolderLookup.Provider`（同 `BaseBulletEntity` 的改法）、`:59/136/186` 成员差异 |
| 3 | `NPCSpawner`(4) | `LibCodecUtils.reference2BooleanMap`、`ModTags.Items.BULLET/GUN`、`LibDateUtils._12$00` |
| 4 | 其余 40+ 文件 | 按 `error_detail.py` 逐个推 |
| 5 | 收口 | 接线 `NpcEntities.ENTITIES.register(eventBus)` → 编译 0 → `fix_eol` → 单独提交（**不要**把本批 WIP 与枪械内联的其他批次混提交） |

### 12.4 本批（批次 25）全貌回顾（12 轮）

- **范围**：WP4 NPC 框架（54 个 npc 文件 + `NpcEntities` + `NPCSpawner` 真迁移）+ 弹幕/子弹层（枪械 G3′ 主体 21 文件）
  + 关联的缺件（`NPCProjectileEffects`、`DryadsBlessingEffect`、`PartyGameEvent`、`NPCServiceMenu`、对话/服务/召唤包 7 个、
  `BoomerangProjectile`/`NPCWeaponProjectile`/`CyborgExplosiveProjectile`/`BoomerangItems`/`BoomerangItem`、`BaseSlime`/`SweetSlime`）
  ≈ **100+ 新文件**
- **路线 B′ 落地**：25 处击杀判据按注册 id 判等（语义无损，前置 `5d83b5236`）
- **补的成员级盲区**：`ModEntities.{SKELETRON_SKULL, NPC_WEAPON_PROJECTILE, CYBORG_EXPLOSIVE, BOOMERANG_PROJECTILE,
  BASE_BULLET_ENTITY, GRAVITY_BULLET_ENTITY}`、`GunItems.DUMMY_BULLET`、`ModEffects.DRYADS_BLESSING`、
  `ExtraInventory.getMount`（上一批）、`LibUtils.knockBackA2B`（本批子模块）
- **包移动**：`NPCSpawner`（`data/saved` → `data/spawner`，17 处引用改指，git 记为 RM）
- **记录在案的偏差（需回填）**：`TownSlimeNPC` 的史莱姆判据暂用 id 判等（史莱姆批落地后改回成员比对）
- **一次索引事故**（已修正并固化成规矩）：`git mv` 会把改名留在索引，提交文档必须显式指定路径

---

## 十三、goal round 13（收口轮）：**编译门 0 错误 / 0 文件，批次 25 落地**

### 13.1 ⚠️ 开工第一件事就推翻了前 4 轮的前提：错误清单被 javac 截断了

前 12 轮一直按 `build_errors.py` 的「**100 错误 / 46 文件**」排优先级，并把它写进了交接笔记。
本轮实测发现那是**假象**：`javac` 的 `-Xmaxerrs` **默认 100**，超出部分**既不打印也不计数**——
所以「100」是**截断的下界**，不是总数。加 `-Xmaxerrs 2000` 重测（临时 Gradle init 脚本，不进仓库）：

| 口径 | 错误数 | 文件数 |
|---|---:|---:|
| `build_errors.py`（默认 100 上限） | 100 | 46 |
| **同一个工作树 + `-Xmaxerrs 2000`** | **150** | **65** |

多出来的 19 个文件里有**整个贸易注册簇**（`NPCTradeMenu` 21 处，是当时真正的榜首）、
`NpcEntities`、`DryadsBlessingEffect`，以及 TE 桥接层与 mixin 的一整组 —— 即「以为快收口了，
其实还有一半没看见」。
**已固化为工具能力与纪律**：`build_errors.py --maxerrs 2000`（见 `tools/port2native/README.md` 过程纪律 3b）；
判定办法是「总错误数是不是恰好 100」。**这是本轮最有价值的一条产出。**

### 13.2 ⚠️ 第二类盲区：不只是「成员级」，还有**整类缺失**没被闭包工具看见

`TradeCondition.java:11/15` 报的 `cannot find symbol: class ModCustomRegistries` 牵出一个事实：
**`common/init/ModCustomRegistries` 与 `common/init/ModTradeConditions` 在 1.21 侧整个不存在**
（1.20 分别在 `ModCustomRegistries.java` / `ModTradeConditions.java`），
而它们不在本批暂存清单里 —— 尽管 `stage_batch` 报 `uncovered=[]`、`dep_subset` 的闭包也从未点名。

后果：18 个 `trade/conditions/**` 各 2 处（`cannot find symbol` + `package ModTradeConditions does not exist`）+
`TradeCondition` 2 处 = **38 处**，一次全在「注册表根本不存在」这一个根因上。

1.20 那两份是 PortLib 形态（`PortCustomRegistration` / `PortRegisterHandler.custom`），
1.21 侧按**仓库内既有的原生等价写法**建（`TerraEntity/.../registries/TERegistries.java:58-60` 与 `:83-99`）：

| 新文件 | 内容 | 关键决定 |
|---|---|---|
| `common/init/ModCustomRegistries` | `new RegistryBuilder<>(key).create()` + `NewRegistryEvent` 注册 + `Keys.TRADE_CONDITIONS`；`register(IEventBus)` 里挂 `NewRegistryEvent` 监听并把 `ModTradeConditions.TYPES` 挂上模组总线 | **增量**：1.20 那份还建 `TRACK_TYPE_PROVIDERS` / `GENERATION_PROVIDERS`，但它们对应的 `TrackTypeProvider`/`GenerationProvider`/`IGeneration`/`ITrackType` 在 1.21 **都还不存在**（WP7）→ 本批只落 `TRADE_CONDITIONS`，不留死代码。**不做 `sync(true)`**：1.20 传的是 `maker -> {}`（consumer 为空 = 不同步） |
| `common/init/ModTradeConditions` | 25 个条件编码器的 `DeferredRegister`（1.20 的 25 个条目逐条对齐） | `DeferredRegister.create(Registry, modid)` 重载 + `DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<X>>`（与 `NpcEntities` 同一写法，替代没有对应物的 `RegistryObject`） |

**一条 1.21 DFU 差异**（本轮实测，值得记进 API 事实表）：`Codec#dispatch` 的**第二个形参换了类型** ——
```java
<E> Codec<E> dispatch(Function<? super E,? extends A> type,
                      Function<? super A,? extends MapCodec<? extends E>> codec)   // 1.21
```
1.20 写的是 `dispatch(TradeCondition::codec, MapCodec::codec)`（那时第二参要的是「怎么变成 `Codec`」）；
1.21 里注册表的元素**本身就是 `MapCodec`**，所以第二参取恒等函数：`dispatch(TradeCondition::codec, mapCodec -> mapCodec)`。
（不改就会得到 `Codec<CAP#1> cannot be converted to MapCodec<? extends E>`。）

### 13.3 修掉的全部缺口（按根因归类，含新增的成员级盲区）

**新建文件 4 个**：`ModCustomRegistries`、`ModTradeConditions`、`integration/terra_entity/TENpcCompat`、
`client/gui/screen/AnglerDialogScreen`（后两个见 13.4 / 13.5）。

**成员级盲区（1.21 里类在、成员不在）—— 本轮补了 16 处**：

| 归属文件 | 补的成员 | 依据 |
|---|---|---|
| `ModTags` | `Blocks.HONEY`、`Items.GUN`、`Items.BULLET` | 1.20 `ModTags:19/452/457` |
| `ModEntities` | `NPC_SHADOWFLAME_SKULL` | 1.20 `ModEntities:141`（类 `NPCShadowflameSkullProjectile` 1.21 早已存在） |
| `MonsterEntities` | `SWEET_SLIME`（含 `SweetSlime` import） | 1.20 `MonsterEntities` 同名条目 |
| `GunItems` | `MINISHARK`/`FLINTLOCK_PISTOL`/`BLOWGUN`/`MUSKET_BULLET` + `registerGun`/`registerBullet` 助手 | 1.20 `GunItems:37/40/43/69/121/127` |
| `ModEnchantments` | `MULTI_BOOMERANG`（**`ResourceKey`**，见 13.6） | 1.20 是 Java 附魔类 |
| `PlayerUtils` | `debit`/`purchase`/`credit`/`creditFromInventory`/`giveCoins` | 1.20 `PlayerUtils:329/334/343/350/362` |
| `ValueComponent` | `getValueLong(ItemStack,int[,boolean])` | 1.20 `ValueComponent:38/52` |
| `ClientUtils` | `formatPrice(long)`（原 `int` 版改为转发） | 1.20 `ClientUtils` 同形 |
| `ModMenuTypes` | `NPC_TRADE` | 1.20 `ModMenuTypes:34` |
| `SpikyBallProjectile` | `damage` 字段 + `setDamage(float)` + NBT 往返 | 1.20 `SpikyBallProjectile:26/83-85/96/103` |
| `PrefixType` | `randomPrefix(RandomSource, ItemStack)` | 1.20 `PrefixType:79`（**降级重载**，见 13.7） |
| `ModUtils` | `summonBoss(ServerLevel, BlockPos, BaseBoss[, boolean])` | 1.20 `ModUtils:119-132` |
| Magic-Lib `LibCodecUtils` | `reference2BooleanMap` | 1.20 子模块同名方法 |
| Magic-Lib `LibDateUtils` | `_12$00` | 1.20 子模块同名常量 |

**换算/改名类（1.21 API 事实，已实测）**：

| 位置 | 1.20 | 1.21 |
|---|---|---|
| `NPCTradeMenu` | `ItemStack#isSameItemSameTags` | `isSameItemSameComponents`（13 处） |
| `NPCTradeMenu.withTradeDetails` | 裸 NBT `display` 标签 + `Component.Serializer.toJson(...)` 手写 Lore | `DataComponents.LORE` + `ItemLore#withLineAdded`（**不再需要 JSON 序列化**）+ 购买价走 `DataComponents.CUSTOM_DATA` |
| `ValueComponent.addTooltip` | 从 NBT 读购买价 | 从 `CUSTOM_DATA` 读（同一把键 `BUY_PRICE_TAG`，两侧语义对齐） |
| `NPCTradeOffer` | `PortCodecExtension.lenientOptionalFieldOf(codec, name, default)` | `codec.lenientOptionalFieldOf(name, default)`（先例 `ExtractinatorData:76`） |
| `ModMenuTypes.NPC_TRADE` | `IForgeMenuType.create(NPCTradeMenu::fromNetwork)` | `IMenuTypeExtension.create(...)`（规则表 `forge-to-neoforge.json:438` 已核实）；工厂缓冲区 `FriendlyByteBuf` → `RegistryFriendlyByteBuf` |
| `DryadsBlessingEffect` | `isDurationEffectTick` + `void applyEffectTick` | `shouldApplyEffectTickThisTick` + **`boolean`** `applyEffectTick`；`ADD_VALUE` 从 `AttributeModifier.Operation` 静态导入 |
| `BaseNPC` | `canBeLeashed(Player)` | `canBeLeashed()`（去形参） |
| `OldManNPC` | `finalizeSpawn(..., dataTag)` 5 参 | 4 参（返回 `SpawnGroupData`） |
| `AnglerNPC` | `ResourceLocation` 战利品表 + `server.getLootData()` | `ResourceKey<LootTable>` + `server.reloadableRegistries().getLootTable(...)` |
| `ChatManager` / `NPCTradeList` | `ForgeRegistries.ENTITY_TYPES.getValue` | `BuiltInRegistries.ENTITY_TYPE.get` |
| `NPCMood` | `Component.Serializer.toJson/fromJson(String)` | `ComponentSerialization.FLAT_CODEC` + `NbtOps`（provider-free，标签形状不变） |
| `HouseHandler` | `PortDataResultExtension.ifSuccess(x, c)` | `x.ifSuccess(c)` |
| `BoomerangProjectile` | `setSecondsOnFire(int)` / `ProjectileImpactEvent.onProjectileImpact(...)` / `ItemStack.of`+`save(tag)` | `igniteForSeconds(float)` / `NeoForge.EVENT_BUS.post(...).isCanceled()` / `parseOptional(level().registryAccess(), ...)`+`save(level().registryAccess(), ...)` |
| `BoomerangItem` / `BaseBullet` | `appendHoverText(..., Level, ...)` | 第 2 参 `Item.TooltipContext` |
| `BaseBullet.setup` | `properties.maxStackSize == 99`（字段直读） | 读 `DataComponents.MAX_STACK_SIZE` 组件（1.21 无读回 stackSize 的公开 API） |
| `BaseSlime` | `getDimensions(Pose)` | `getDefaultDimensions(Pose)`，**并去掉自身的 `getScale()`**（父类 `LivingEntity:3423/3427` 会再乘一次，否则重复缩放） |
| `NPCDialogScreen.render` | 显式 `renderBackground(guiGraphics)` | **删掉该调用**：1.21 起 `Screen#render` 本体已经画背景（`Screen.java:131-138`），补成 4 形参会让背景画两遍 |
| `SpikyBallProjectile.canHitEntity` | 多一段 `BaseNPC.canAttack` 守卫 | 按 1.20 `:76-80` 补回（NPC 层本批才落地，此前不需要） |
| `SpikyBallProjectile` | （1.21 硬编码伤害 `3.2F`） | 按 1.20 恢复 `damage` 字段 + `setDamage` + NBT，并把 `NPCCombatActions` 的调用**接回** |

**接线缺口（编译期看不出来、运行时才崩）**：`GunItems.ITEMS` 与 `BoomerangItems.ITEMS`
**此前一直没在 `Confluence` 构造器里 `.register(eventBus)`** —— 而 `BaseBulletEntity#getDefaultItem`
用 `GunItems.DUMMY_BULLET`、`NpcEntities` 用军火商/巫医的枪、`NPCCombatActions` 用
`BoomerangItems.COMBAT_WRENCH`，全是运行时取 `DeferredItem`。本轮一并接上（与 `MountItems.ITEMS` 并列）。

### 13.4 TE 桥接层：新增 `TENpcCompat`（过渡桥，随 TE 退役删除）

新 `NPCSpawner` 的宿主类型是主模组 `BaseNPC`，而 `mixin/integration/terraentity/**` +
`integration/terra_entity/**` 这套桥接层只认 TE 的 `AbstractTerraNPC` → 9 个文件 15 处 `incompatible types`。

**处置**：不去污染 `NPCSpawner`（它必须与 1.20 逐字一致），而是把 **HEAD 旧版** `data/saved/NPCSpawner`
里那几个 **TE 形参**的方法（`git show HEAD:` 可得）原样搬进新类 `integration/terra_entity/TENpcCompat`：
`moveNPCToAnotherRegion` / `applyBenedictions` / `applyAdvancedCombatTechniques` / `onNPCRemoved` /
`broadcastMessageToRegion` / `noHobo`。注意 **旧版数值与新版不同**（TE 版 armor +3 / attackDamage +0.2；
`BaseNPC` 版 maxHealth +250 / armor +8 / attackDamage +0.25），搬的时候保持旧版。
调用点 13 处改指（4 个 mixin + `TEGameEvents` + `LivingEntityEvents` + `PlayerEvents` +
`HouseSelectPacketC2S` + 2 个先进战斗技术物品）。
`AdvancedCombatTechniquesItem`/`VolumeTwoItem` 反过来**按 1.20 对齐**（`AbstractTerraNPC` → `BaseNPC`）。
**唯一有意的行为差异**：`broadcastMessageToRegion` 补上了新版的前置守卫
`level.isClientSide || !CommonConfigs.BROADCAST_NPC_MSG.get()`（旧版漏了它，导致 `broadcastNpcMsg=false`
时 TE 的「已到达」讯息照样广播，而主模组那条路径是听这个开关的）。

### 13.5 新屏 `AnglerDialogScreen` + 闸门

`OpenAnglerDialogPacketS2C` 的 7 处错误全因 1.21 缺 `client/gui/screen/AnglerDialogScreen`。
按 1.20 同 FQN 搬入，先跑 `check_duplicates.py`：**0 处「疑似移动/重复」**
（唯一同名的 `org.confluence.terraentity.client.gui.container.AnglerDialogScreen` 是 TE 时代的
另一套实现，判 `DIFF`，随 TE 退役删除）。1.21 原生改写只有两处：`npc.getRandom1211()` → `getRandom()`、
`init()` 补 `minecraft == null || minecraft.level == null` 空检查。

### 13.6 一处记录在案的**推迟**（不是缺陷）

`multi_boomerang` 附魔：1.20 是 Java 类 `MultiBoomerangEnchantment extends Enchantment`，
**1.21 的附魔是数据驱动的**（`Enchantment` record 由 datagen 的 `ModDataProvider` 用
`Enchantment.enchantment(...)` 产出）→ 本批只落 `ModEnchantments.MULTI_BOOMERANG`（`ResourceKey`），
datagen 条目 + `BOOMERANG` 附魔标签**属 WP7**。
`BoomerangItem` 因此改用仓库自带的 `EnchantmentUtils.getEnchantmentLevel(ResourceKey, ItemStack)`
（查不到返回 0），而**不是** `registryAccess().getHolderOrThrow(key)` —— 后者在 JSON 还没生成时会抛异常。

### 13.7 收口校验与偏差清单

| 项 | 结果 |
|---|---|
| `ConfluenceOtherworld` 编译门 | **0 错误 / 0 文件**（`build_errors.py --maxerrs 2000`） |
| `Confluence-Magic-Lib` 编译门 | **0 错误 / 0 文件** |
| PortLib 残留（`Port*`/`IPort*`/`org.mesdag.portlib`，含注释外全树扫描） | **0 处** |
| 工作树规模 | 45（`??`，含 54 个 npc 文件 + 本批新类）/ 39（`M`）/ 1（`D`，NPCSpawner 旧路径） |

**需回填的偏差（逐条写明归属批次）**：

1. **`TownSlimeNPC` 的「9 个标准史莱姆」判据仍用注册 id 判等**（第 9 轮记录）：1.21 的 `MonsterEntities`
   一个史莱姆成员都没有 → **WP2 史莱姆批落地后改回成员比对**；本轮只增量补了 `SWEET_SLIME` 一个成员
   （因为 `BaseSlime` 的蜂蜜转化要用它），史莱姆族其余 25 个 `registerSlime` 成员仍待补。
2. **`PrefixType.randomPrefix(RandomSource, ItemStack)` 是个「忽略物品」的重载**（本轮新补）：
   1.20 那份是**物品感知**的（悠悠球专属传说词条、无击退武器过滤），1.21 的 `PrefixType` 整体落后约 105 行、
   `PrefixUtils:83/134` 两个消费点走的还是 1 参版 → 属**前缀系统对齐**的独立批次。
   补重载只是让 1.20 写的调用点（`NPCReforgeMenu:86`）逐字保留，回填时把过滤逻辑补进 `PrefixType` 即可，调用点无需再动。
3. **`GunItems` 仍只有 7 个成员**（`STAR_CANNON`/`DUMMY_BULLET` + 本轮 4 个 + 助手）；整层属**枪械 G4′**。
   1.20 的 `GUN_ITEMS`/`BULLET_ITEMS` 两个列表**有意没搬**（1.21 零消费点 = 死代码），G4′ 时一并加。
4. **`multi_boomerang` 的 datagen 条目 + 附魔标签**（→ WP7）。
5. **新 NPC 屏的语言键缺口**（非编译问题）：`gui.confluence.quest`、`dialogs.confluence.angler.completed`/
   `.no_quest`/`.quest_fish`、`dialogs.confluence.female_angler.wakeup.N`、`gui.confluence.dialog`
   在 1.21 的 datagen provider（`ModEnglishProvider`/`ModChineseProvider`）里目前没有 → **随 WP7 lang 那一批补**。
6. **`NPCReforgeMenu` 的背景渲染**：1.21 交给 `Screen#render` 内建调用（不回补显式调用，否则画两遍）。
7. **回旋镖「不可损坏」用 `DataComponents.UNBREAKABLE = Unbreakable(true)`**：tooltip 会多一行
   「Unbreakable」，1.20 的 `.unbreakable()` 不显示 —— 为与仓库其它不可损坏物品一致而有意选取。
8. **`TENpcCompat` 及全部调用点**：**删 TE gitlink 的那一批**要一起删（搜 `TENpcCompat` 可列全）。
9. **`SpikyBallProjectile` 的 `canHitEntity` NPC 守卫**与恢复的 `setDamage` 需**实机验收**（行为面，编译门看不出来）。

### 13.8 本批对后续的影响

- **WP4 整层解开**：NPC 框架（含 42 条目注册层、贸易/对话/服务屏、NPC 战斗与生成）已可用；
  连带解开 WP2 剩余怪物与动物（它们的闭包入口就是 `BaseNPC`）与**枪械 G3′**（`BaseBulletEntity -> BaseNPC` 那条链已断）。
- **下一步**：枪械 **G4′**（`GunItems` 整层 + `TGTags`/`TGSoundEvents`/`TGTrailColors`/`TGGunSounds`）
  → G5′（客户端 + `TerraGuns` 主类）→ G6′（删 gitlink 并清理）。注意 `ItemEvents` 仍绑 TE 的 `GunEvent`
  （7 个处理器要按 1.20 事件形状重写）、`ModGunProperties` 仍绑 TE 的 `TGGunSounds`。

