# 魂师实现说明（给程序）

更新日期：2026-10-22 批次。本文描述**代码里实际是怎么实现的**，面向维护者。
玩家向的介绍与测试清单见 [魂师简介与测试](SOUL_TEST_GUIDE.md)。

本文同时适用于两棵源码树：

| 版本 | 路径 |
|------|------|
| 1.21.1 NeoForge | `汇流来世/confluence/ConfluenceOtherworld` |
| 1.20.1 Forge | `汇流来世/confluence-1.20.1/ConfluenceOtherworld` |

两版的类名、文件名、注册 ID、数值、语言键、贴图名保持一致；差异只在框架 API
（注册方式、事件总线、`SynchedEntityData`、`DeferredHolder` / `RegistryObject`、`Holder` / 直接对象）。
**同步的是规则与数值，不是逐字节一致的源码。**

---

## 1. 代码组织

包段是 `soulcerer`（职业），资源/伤害/书籍等概念沿用 `soul`。灵魂钓手、噬魂怪、亡魂射弹等旧内容不属于本系统。

| 入口（相对 `src/main/java/org/confluence/mod`） | 职责 |
|---|---|
| `common/attachment/SoulStorage` | 当前灵魂、永久成长、恢复比例、资源模式、仪式进度、施法武器快照；NBT 持久化与附件同步 |
| `common/attachment/SoulSkillStorage` | 装备槽、当前索引、永久/临时学习、可用集合、书籍被动、共享冷却、刻骨的记忆；**开局解锁表** |
| `util/PlayerUtils` | 模式互斥、施法入口、支付/代偿、伤害基数、可用性、持续技能推进（`tickSoulSkillStates`） |
| `common/init/ModSoulSkills` | 技能注册、缓存原型、界面分类 |
| `common/soulcerer/skill/` | 技能定义（`SoulSkill`）、实例栈（`SoulSkillStack`）、各技能类、集中参数 `SoulSkillParameters` |
| `common/entity/soulcerer/` | `SoulLureProjectile`（全部弹幕）、`SpiritSurgeEntity`（全部激荡）、`SoulStingProjectile`（毒刺） |
| `common/event/game/entity/LivingEntityEvents` | 技能命中/击杀结算（附魂印记回魂、武器特性、套装） |
| `common/init/ModEffects` | 技能同名增益 + `soul_mark`（敌人身上的 debuff） |
| `common/item/soulcerer/SoulBooks` | 技能书定义表（id → 稀有度 / 解锁技能 / 前置 / 被动说明 / 旧 id 兼容） |
| `client/gui/**/soulcerer`、`client/handler/soulcerer` | HUD、快捷面板、技能总览、技能框 tooltip |
| `common/data/gen/**` | 语言、标签、配方、战利品、模型（`src/generated` 是产物，不入库） |

`ConfluenceMagicLib` / `LibAttributes.getSoulDamage()` / `LibDamageTypes.SOUL` 提供灵魂伤害属性与独立伤害类型。

---

## 2. 数值与伤害口径

三个数不能混用：

- **武器基数**：`SoulWeaponProperties.damage()`，1.21 读 `getBasicSoulDamage(stack)`，1.20 读 `getSoulDamage(stack)`。
- **技能基数**：`SoulSkill.getDamage(stack)`；技能 tooltip 的“伤害”行只显示这个。
- **灵魂属性**：`LibAttributes.getSoulDamage()` 是倍率（1 = 100%，上限 10），不是“额外加成”。

```
基础合计   = 武器基数 + 技能基数
实际伤害   = 基础合计 × 灵魂属性倍率  → 由统一受伤流程再乘一次
```

`PlayerUtils.getSoulSkillDamage` 里结算事件修正（`SoulSkillDamageEvent`）、引渡者 +2、星魂联结、分魂驾驭；
返回值仍**未**乘全局灵魂倍率。持续/多段技能用 `getCastSoulSkillDamage`，读施法时写入 `SoulStorage` 的武器基数快照。

灵魂不足时先扣剩余灵魂，缺口按 **1 魂 = 4 生命** 代偿（`SoulOverdraftEvent`，独立伤害类型 `SOUL_OVERDRAFT`，可致死）。
法器、装备等其它入口不改这套规则。

**全部可调数值只在 `SoulSkillParameters` 一处**；技能类只读参数，不要在技能类里硬编码数字。

---

## 3. 学习、解锁与生命周期

- 三种状态分开：**永久学会**（`unlockedSkills`）、**临时可用**（武器授予，`temporarySkills`）、**当前可用**（`availableSkills`，由 `refreshSoulSkills` 汇总）。临时来源消失只关闭“可用”，不删永久记录、不清槽位与冷却。
- **开局即永久解锁**（`SoulSkillStorage.DEFAULT_UNLOCKED_SKILLS`）：`soul_lure` 引魂、`enhanced_soul` 强力附魂、`spirit_surge` 灵魂激荡。
  该表在**构造与反序列化后**都会补一次（`ensureDefaultUnlocked`），因此把技能改成“开局解锁”时老存档同样生效。
  初始只解锁、不自动装备。
- 槽位初始 3，上限 6；第 1/3/5 次仪式各 +1。
- 被动技能只有**装备且在可用集合里**才结算（`isPassiveSoulSkillActive`）；书籍被动（引渡者、灵魂回响）用 `isSoulBookLearned(bookId)`，不占槽。
- 冷却按技能 ID 共享，装备栈里的 CD 只是镜像；换槽/卸装不清冷却。

---

## 4. 施法与同步

主武器右键走原版交互到服务端 → `PlayerUtils.tryUseSoulSkill`：检查模式、可用、冷却、`canUse` → 支付 →
写冷却与武器快照 → `skill.use(player, stack)` → 同步给本人与追踪者。

`use` 在两端都会执行，必须按侧分支：**服务端**生成实体/伤害/效果，**客户端**只做本地表现。
已随实体追踪同步的攻击不要在客户端再造一份。

| 通道 | 内容 |
|---|---|
| `SoulStorage` 附件同步 | 成长、当前灵魂、恢复比例、模式、仪式 |
| `SoulSkillSyncPacketS2C` | 本人技能快照（不含当前索引） |
| `SoulSkillIndexRequestPacketC2S` / `SoulSkillIndexSyncPacketS2C` | 当前槽索引 |
| `SoulSkillUsePacketC2S` / `SoulSkillUsePacketS2C` | 施法请求 / 本次施法者与技能 ID（只重建这一次表现） |

---

## 5. 标签（`ModSoulSkillTagsProvider`）

| 标签 | 成员 | 用途 |
|---|---|---|
| `soul_lure` | 引魂、强力引魂、引魂激荡、迷魂孢雾、亵渎之魂、自然之惩戒、魂引天星 | 引渡者加成与“引魂弹幕”判定 |
| `soul_attach` | 强力附魂、附魂印记、附魂激荡、血怒、掠夺之魂、灵魂透支、魂引天星、灵魂突击、血液沸腾、生命归还、灵魂缓冲 | 束魂锁链联动名单 |
| `major_active` | 灵魂透支、灭却业火、血怒 | 只能手动选，自动连锁/自动选槽都跳过 |
| `passive` | 自然法则、星魂逆转、星魂联结、灵魂收割、噬魂借力、反噬抵抗、分魂驾驭、魂术轮转 | 可装备不可主动释放；魂术轮转跳过 |

改技能时记得同时检查：分类（`SkillCategory`）→ 分类标签 → 上面四个标签 → 总览节点 → 图标 → 语言。

---

## 6. 技能实现速查

参数见 `SoulSkillParameters`，下表只写“怎么实现、改动时注意什么”。

| 技能(id) | 实现要点 |
|---|---|
| 引魂 `soul_lure` | `SoulLureSkill` → `SoulLureProjectile`，命中后弹射 1 次；**弱追踪**（见 §7） |
| 强力引魂 `enhanced_lure` | `SoulProjectileSkill` + `bounces(+1)`；引渡者再叠 1 次 |
| 附魂印记 `soul_mark` | `SoulMarkSkill` 只覆写 `modifyProjectile` → `setSoulMarkOnHit(10s)`；命中给敌人挂 `soul_mark` debuff，**不再给自身强力附魂** |
| 引魂激荡 `lure_surge` | `SoulProjectileSkill` + `surgeOnHit(3.0)` → 命中点补一发免费激荡（不扣魂） |
| 迷魂孢雾 `confuse_spores` | `SoulProjectileSkill` + `upwardVolley(0.5, 0.35rad)` + `gravity(0.03)` + `spawnAbove(3)` + `weakHoming(false)` + `weaponShare(0.2)`；每轮掷骰决定弹道 |
| 自然之惩戒 `natures_wrath` | `NaturesWrathSkill` 生成 5 枚 `SoulStingProjectile`（扇形 0.12 弧度步进），命中加中毒 |
| 亵渎之魂 `profane_soul` | 锁定最近敌怪，火球从其**脚下**向上飞，命中补小范围伤害 |
| 灭却业火 `karma_flame` | 消耗全部灵魂，直接击杀半径 16 格内生命值低于消耗数的目标；击杀额外 +2 魂 |
| 灵魂突击 `soul_assault` | 增益期间每 10 刻把**至多 10 点当前灵魂**换成等量无粒子 `SoulLureProjectile`（每发 +4，每发 20% 返 1 魂）；枪械强化在开枪流程里每枪结算一次 |
| 灵魂激荡 `spirit_surge` | `SpiritSurgeSkill` 继承 `SoulSurgeSkill`，默认半径/时长；**开局解锁**，并接灵魂回响（§7） |
| 强力激荡 `empowered_surge` | `SoulSurgeSkill`，半径 9.6、伤害 8 |
| 灵魂激荡冲击 `surge_blast` | 每次放一圈激荡并累计层数；攒满 3 次追加终结爆炸 + 击退（不推自己） |
| 灵魂引发 `spirit_trigger` | `SoulSurgeSkill` + 连锁参数：命中目标处向随机方向射 6 发小弹幕 |
| 附魂激荡 `soul_surge` | `StatefulSoulSkill` + `PlayerUtils.tickSoulSkillStates` 每 3 秒自动放一次免费灵魂激荡（**也掷灵魂回响**） |
| 魂引天星 `star_call` | `StatefulSoulSkill`；`tickStarCall` 每 50 刻给 48 格内最近 2 只敌怪落 1 颗星（星怒面片外观，不弹射） |
| 血怒 `blood_rage` | `SoulBuffSkill`（扣 5 生命），效果带近战/远程 +200% 修饰符；击杀 +1 魂 |
| 掠夺之魂 `soul_plunder` | `SoulBuffSkill`，近战/远程 +25%，140 秒 |
| 灵魂透支 `soul_drain` | `StatefulSoulSkill`；效果期间灵魂消耗为 0，结束时挂 `soul_drain_lock` → **无法释放灵魂技能**（不是主武器） |
| 生命归还 `life_return` | `StatefulSoulSkill`；受伤时记录，结束时按 70% 转成灵魂 |
| 灵魂缓冲 `soul_buffer` | `SoulBuffSkill` + `LivingEntityEvents#soulSkillOnHurt`：摔落伤害转为治疗 |
| 血液沸腾 `boiling_blood` | 消耗 80% 灵魂；下一次近战 **×5**（`+400%`）并消耗增益 |
| 求生欲 `survival_instinct` | 血量 < 40 时消耗 10 魂，耐药性时长减半 |
| 被动 | 自然法则 20% 概率免魂、星魂联结 50% 共享魔力加成、灵魂收割 5% 回 1 魂、噬魂借力 1 魂 +50%、反噬抵抗 -60%、分魂驾驭 ×2 伤害 / ×1.5 冷却、魂术轮转自动跳技能 |

> `soul_harvest` / `soul_devour` 的钩子（`soulSkillSummonDamage`）只判断“伤害的 owner 是玩家、直接来源不是玩家本人”，
> 所以**召唤物与弹幕（含魂师技能弹幕）都会触发**，不是字面意义上的“只有召唤物”。描述已按实际行为写成“召唤物与弹幕”。

> 星魂逆转 `star_reversal` **尚未实装**：`convertManaToSoul` / `convertSoulToMana` 还是占位 `return false`，
> 描述已改成“被动（预留）”。接实现时把两个入口写完再改描述。

---

## 7. 本轮（2026-10-22 批次）的机制改动

### 7.1 引魂弹幕弱追踪

`SoulLureProjectile` 同时支持两种转向，统一走 `steerTowardsTarget(strength, radius, chaseBack)`：

| 来源 | 强度 | 索敌半径 | 追身后 | 打开方式 |
|---|---|---|---|---|
| 弱追踪（新） | `SOUL_LURE_WEAK_HOMING_STRENGTH` = 0.15 | 20 格 | 否（点积 ≤ 0 直接跳过） | `setWeakHoming(true)` |
| 带路的老前辈（原有词条） | 0.5 | 16 格 | 是 | `resolveWeaponTraits` 读武器词条 |

- 转向时把速度**归一化回原速度**，只改方向不改快慢。
- `SoulProjectileSkill.Builder.weakHoming` 默认 **true**，所以引魂/强力引魂/附魂印记/引魂激荡都自带弱追踪；
  **迷魂孢雾显式关闭**（会破坏锥形散布），星魄坠星与灵魂引发的连锁弹幕不启用。
- 弱追踪标记存 NBT `WeakHoming`，弹射会继承。

### 7.2 迷魂孢雾的双弹道与伤害

`SoulProjectileSkill.use` 每次施放先掷一次骰（`upwardChance`）：

- **斜上方抛射（50%）**：生成点 = 眼睛 + 视线×2 + 头顶 `spawnAbove` 格，方向 = 视线抬高 `upwardPitch`（≈20°），带 `gravity`。
- **直线平射（50%）**：生成点 = 眼睛，方向 = 视线，**重力强制 0**（“直线”就是直线）。
- 两种模式都保留原来的锥形散布（`spread` 0.35、8 枚）。

伤害公式（本轮改动）：

```text
迷魂孢雾单发 = (武器伤害 × 20% + 技能自身伤害 1.5) × (1 + 灵魂伤害增伤%)
```

- 实现方式是 `SoulProjectileSkill.Builder.weaponShare(0.2)`：`use()` 里把
  `PlayerUtils.getWeaponSoulDamage(player) * weaponShare` 作为“武器部分”传给 `getSoulSkillDamage`，
  其余技能默认 `weaponShare = 1.0`（全额武器伤害）。
- 公式里的 `(1 + 灵魂增伤%)` 仍然由统一伤害流程 `LibAttributes.applySoulDamage` 乘**一次**，
  不要在技能里再乘一次。
- 引渡者（+2）、星魂联结、分魂驾驭这些既有修正仍按原规则叠加，不在这条公式的描述范围内。

### 7.3 附魂印记 → debuff

- 新效果 `ModEffects.SOUL_MARK`（`HARMFUL`，id `soul_mark`，贴图 `mob_effect/soul_mark.png` 沿用技能图标）。
- `SoulMarkSkill` 只做 `projectile.setSoulMarkOnHit(SOUL_MARK_DURATION_TICKS)`；弹幕命中时
  `SoulLureProjectile#applySoulMark` 给**敌人**挂 10 秒印记，**不再给玩家强力附魂**。
- 回魂两处结算（都是“额外一份”，不动原有收魂）：
  - `LivingEntityEvents#soulWeaponTraitsOnSkillHit`：命中带印记的敌人 → `adjustSoul(+1)`。该钩子只认
    `LibDamageTypes.SOUL`，而魂师所有技能伤害最终都走 `SoulLureProjectile` / `SpiritSurgeEntity` / `SoulStingProjectile` /
    灭却业火，因此“所有技能”= 灵魂伤害来源天然覆盖。
  - `LivingEntityEvents#livingDeathSoul`（1.20 是 `deathSoul`）：带印记的敌人死亡且击杀者是玩家 → `adjustSoul(+2)`。
- 施加印记的那一发本身不加魂（先 `hurt` 再挂印记）。
- `SoulMarkSkill#getBuffDurations` 改为显示印记时长；tooltip 行仍由 `SoulSkillStack` 统一拼装。

### 7.4 灵魂回响（原“灵魂激荡”技能书）

- 书籍 id `soul_surge` → **`soul_echo`**，名称“灵魂回响 / Soul Echo”，`SoulBooks` 里 `skill = null`（纯被动书），
  通过 `learnedSoulBooks` 记录，与引渡者同一套机制。
- 书籍 tooltip 不再硬编码引渡者文案：`SoulBooks.passiveTooltipKey(id)` 按 id 取
  `tooltip.confluence.soulcerer_skill_book.{psychopomp,soul_echo}`。
- 效果在 `SpiritSurgeSkill.rollSoulEcho(player, damage)`：学会该书后每次**灵魂激荡释放**都有 20% 概率
  原地再生成一发同参数激荡，**不扣魂、不再连锁**。
  调用点有两处：`SpiritSurgeSkill#use`（主动施放）与 `PlayerUtils.tickSoulSkillStates`（附魂激荡的自动释放）。
- 收割者书原本要求“先永久学会灵魂激荡”，现在灵魂激荡是开局技能，前置已改为 `null`。

### 7.5 天穹倒转 → 灵魂缓冲

改名同时改 id（技能、效果、书籍、参数常量、贴图、语言键一起改）：

| 旧 | 新 |
|---|---|
| 技能 `firmament_reversed` | 技能 `soul_buffer` |
| 效果 `firmament_reversed` | 效果 `soul_buffer` |
| 书 `firmament_reversed` | 书 `soul_buffer` |
| `FIRMAMENT_REVERSED_*` 常量 | `SOUL_BUFFER_*` |
| `textures/…/firmament_reversed.png`（技能/书/效果） | `soul_buffer.png`（技能与效果用新贴图 `灵魂缓冲.png`，书沿用原书皮图） |

旧存档兼容：`SoulBooks.LEGACY_IDS` 把 `firmament_reversed` → `soul_buffer`、`soul_surge` → `soul_echo` 映射到新书籍，
背包里的旧书继续可读；`SoulSkillStorage.LEGACY_SKILL_IDS` 把已学会的旧技能 id 换成新 id（目前只有 `firmament_reversed` → `soul_buffer`）。
再改 id 时请顺手补这两张表，否则旧存档里的技能/书会被静默丢弃。

### 7.7 总览技能树排布（写死在代码表里）

坐标与连线在 `SoulOverviewScreen#initSkillsFromRegistry` 的 `List<NodeSpec> tree` 里，一行一个节点：

```java
n(x, y, ModSoulSkills.SKILL, ModSoulSkills.TARGET1, ...)
```

- `x / y` 是节点**中心**相对屏幕中心的未缩放像素（+x 右、+y 下），一格 40。
- 可变参数是**递进关系**（前置 → 衍生）的连线目标；并列技能不写。
- 记录类型是 `NodeSpec`，辅助方法 `n(...)`；加技能就在对应位置补一行。

> 早期做过"外置 txt + 运行时读取"的方案，因为文件容易被清空/优先级（外部覆盖 vs 包内默认）难排查，
> 已整体移除：**布局只有代码这一处真值**，不存在读不到文件的问题。

排布规则（改表时保持）：

| 规则 | 说明 |
|---|---|
| 左增益、右伤害 | 负 x 放附魂增益与被动，正 x 放伤害类主动技能 |
| 中间 = 开局 | ±40 两列放开局技能（强力附魂 / 灵魂激荡 + 引魂） |
| 同列 = 并列 | 竖直排列，间距 40 |
| 递进才连线 | 连线方向一律由内向外（或同列自上而下） |

**可视化预览**：仓库根目录的 `soul_tree_layout_editor.html`（双击用浏览器打开）内置了当前布局，
可以按游戏网格拖节点、拖 A→B 加/删连线、删线、撤销，实时校验重复/重叠/悬空/未放置，
并把结果导出成 **Java 表**（直接替换 `List.of(...)` 的内容）或布局文本（给人看/存档）。
它不参与构建、不影响游戏，也用来对照截图反推布局。

当前落位（x 为屏幕中心偏移，同列从上到下）：

| x | 左臂（增益/被动） | 右臂（伤害） |
|---|---|---|
| -200 | 生命归还、分魂驾驭 | — |
| -160 | 反噬抵抗、灵魂突击、噬魂借力、血怒 | — |
| -120 | 星魂逆转 → 星魂联结、灵魂收割 | — |
| -80 | 灵魂透支、附魂激荡、魂术轮转、自然法则、求生欲、掠夺之魂、血液沸腾、魂引天星、灵魂缓冲 | — |
| -40 | 强力附魂（开局） | — |
| +40 | — | 灵魂激荡、引魂（开局） |
| +80 | — | 灵魂激荡冲击、引魂激荡、强力激荡、附魂印记、强力引魂、自然之惩戒、迷魂孢雾 |
| +120 | — | 灵魂引发 |
| +160 | — | 亵渎之魂、灭却业火 |

连线（11 条，全部由内向外）：
强力附魂 → 附魂激荡 / 强力激荡；灵魂激荡 → 引魂激荡 / 强力激荡 / 灵魂激荡冲击 / 灵魂引发；
引魂 → 强力引魂 / 附魂印记；星魂逆转 → 星魂联结；自然法则 → 自然之惩戒；亵渎之魂 → 灭却业火。

加新技能时在表里补一行（记得同时补图标与语言）。

### 7.8 总览 tooltip 的层次

技能图标走的是**不带深度**的 gui 批次，`poseStack.translate(0,0,1000)` 不能改变谁盖住谁，真正决定层次的是绘制顺序。
所以 `OverviewNode#renderWidget` 不再画 tooltip，改由 `SoulOverviewScreen#renderDeferredTooltip` 在
`super.render` + 滑块全部画完之后，对**当前悬停节点**调用一次 `renderTooltip`。

---

## 8. 弹幕与粒子

- `SoulLureProjectile` 是**所有**灵魂弹幕的唯一实体：外观/速度/重力/射程/命中范围/弹射/穿透/AOE 全靠构造参数，
  不为单个技能注册新实体。加新弹幕技能优先加参数，不要新建类。
- 粒子 id 通过同步字段 `DATA_PARTICLE` 下发，只在服务端构造时写；客户端解析失败才回落到 `soul_projectile`。
- 每个弹幕**只登记一次** `ParticleEmitter`（`emitter == null` 判断）。旧实现在 `isRemoved()` 时重建，
  会与 `MolangParticleEngine` 的复用迭代器冲突抛 `this.wrapped is null`，别改回去。
- 迷魂孢雾用专用 `confuse_spore_projectile`，**不能**叫 `spore_cloud`（那是原版粒子类型 id）。
- 激荡光环由 `SpiritSurgeEntity` 一次性注入 `variable.radius`；星魄坠星用 `DATA_STAR_APPEARANCE` + 星怒面片，不挂拖尾。

---

## 9. 物品、技能书、datagen

- 书籍：`SoulBooks` 一张表决定 id、稀有度、解锁技能、前置、被动说明、旧 id 兼容；登记顺序 = 书皮覆盖序号 = 创造栏顺序，
  改顺序会改变模型 `custom_model_data`，别随便调。
- 书的使用规则：前置未满足 → 失败；已学会 → 复习（10 分钟 `go_over`，灵魂伤害 +5%）；首次 → 永久解锁/记录书籍；书不消耗。
- 掉落/交易入口：`GiftSubProvider`（宝藏袋）、`ChestSubProvider`（空岛村箱子 33%）、`ModVillagers`（1.20 在 `GameEvents`，天师 3 级）。
- 语言：技能名/描述、书、界面、消息都在 `InterfaceLanguageSubProvider`；效果/书籍提示在
  `TooltipLanguageSubProvider`。**加技能必须同时补三处**：`.name`、`.desc`（`confluence.soulcerer_skill.<id>.…`）、
  书 `.name`（`confluence.soul_book.<bookId>.name`）。
- `src/generated` 是 datagen 产物（`.gitignore` 内）。改语言/标签/掉落/模型后要跑：

```bash
# 1.21.1
cd 汇流来世/confluence && ./gradlew :ConfluenceOtherworld:runData
# 1.20.1
cd 汇流来世/confluence-1.20.1 && ./gradlew :ConfluenceOtherworld:runData
```

---

## 10. 双版本差异（改代码时最常踩的）

| 1.21.1 NeoForge | 1.20.1 Forge |
|---|---|
| `DeferredRegister` + `DeferredHolder` | PortLib `PortCustomRegistration` / `RegistryObject` |
| `Holder<MobEffect>` / `DeferredHolder` 可直接传给 `hasEffect`、`MobEffectInstance` | 需要 `.get()` |
| `SynchedEntityData.Builder` 版 `defineSynchedData` | 无参 `defineSynchedData` + `entityData.define` |
| `@SubscribeEvent` + `NeoForge.EVENT_BUS` | `PortEventHandler.addListener(...)` 显式登记 |
| 语言/掉落用新路径（`loot_table`、`tags/item`、`ItemModel`） | 旧路径（`loot_tables`、`tags/items`、`recipes`） |
| Java 21，可用 `Math.clamp` | Java 17，用 `Math.max/min` 自行夹取 |
| `GuiGraphics.blitSprite(sprite,x,y,w,h)` | 同签名由 PortLib 的 `IPortGuiGraphicsExtension` 提供；**没有 7 参重载**，别写 `(sprite,x,y,w,h,w,h)` |

---

## 11. 验证状态（本轮）

| 项 | 1.21.1 | 1.20.1 |
|---|---|---|
| `:ConfluenceOtherworld:compileJava` | 通过 | 通过（顺带修了 `SoulSkillBox` 里 7 参 `blitSprite` 的旧报错） |
| `:ConfluenceOtherworld:runData` | 见本轮记录 | 见本轮记录 |
| 游戏内实机回归 | **未做**，按测试文档执行 | **未做** |

本轮没有做游戏内验证，也没有跑专用服务器数值脚本；`附魂印记`的回魂频率、`迷魂孢雾`的直线弹道手感、
`灵魂回响`的实际期望收益都需要实机按 [测试文档](SOUL_TEST_GUIDE.md) 确认。

---

## 12. 加一个新技能（固定流程）

1. 数值写进 `SoulSkillParameters`（一段注释 + 中文名）。
2. 选实现：能复用就在 `SoulProjectileSkill` / `SoulSurgeSkill` / `SoulBuffSkill` / `StatefulSoulSkill` / `PassiveSoulSkill`
   里加参数，确实不行为才写新类。
3. `ModSoulSkills` 注册（id = 贴图名 = 语言键后缀）。
4. 分类标签 + 需要的 `soul_lure` / `soul_attach` / `major_active` / `passive` 成员。
5. 解锁途径：开局表 / 仪式表（`PlayerUtils.RITUAL_UNLOCKS`）/ 技能书 / 武器临时授予。
6. 资源：`textures/gui/sprites/hud/soulcerer_skill/<id>.png`（同名效果再加 `mob_effect/<id>.png`、书籍加 `item/skill_book/<bookId>.png`）。
7. 语言：名称、描述（对齐真实效果）、书籍名/被动说明。
8. 总览节点：在 `SoulOverviewScreen#initSkillsFromRegistry` 的 `n(x, y, 技能, 目标…)` 表里补一行（见 §7.7）。
9. 两个版本各跑一次 `runData`，再各编译一次。
10. 按测试文档追加对应勾选项。
