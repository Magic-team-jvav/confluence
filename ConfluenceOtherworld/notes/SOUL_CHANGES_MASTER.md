# 魂师体系新增内容总表（本轮全部改动）

适用模块：`ConfluenceOtherworld`（1.21.1 / NeoForge 21.1.235 / Java 21）。

范围：本对话中的**全部改动只在 1.21.1 NeoForge 侧**，`confluence-1.20.1` 未动一字节（该版本连 `SoulStorage`/`SoulSkillStorage`、Magic-Lib 的灵魂伤害属性都还没有，后续同步时本文可直接当清单）。

状态：每个批次结束时 `gradlew :ConfluenceOtherworld:compileJava` 均通过；涉及 datagen 的批次另跑过 `:runData`。**所有内容都未做游戏内实测。**

配套文件：`notes/SOUL_LANTERN_AND_BOOK.md` 是前三个批次的详细记录（含代码片段与扩展指南），本文是全量索引与汇总，两者不冲突。

---

## 0. 一句话总览

十一批改动 + 一次收尾补丁，全部围绕既有魂师框架扩展，没有另起并行体系：

| # | 批次 | 新增 | 主要落点 |
| --- | --- | --- | --- |
| 1 | 魂灯 8 件 | `SoulLanternItem` + 8 物品 + 5 配方 | `SoulItems` / `ModTabs` / datagen |
| 2 | 技能获得 | 首得永久解锁 + 手持临时授予 | `SoulItems.FIRST_OBTAIN_SOUL_SKILLS` / `PlayerUtils` |
| 3 | 技能书 | `SoulBooks` 定义表、6 种书皮、`go_over` 效果、引渡者被动、`firmament_reversed` 技能、`soul_lure` 标签、7 处掉落/交易 | `SoulSkillBookItem` / `SoulBooks` / 战利品与交易 |
| 4 | 书与文案修正 | 书改为**可反复阅读**、删通用书皮「灵魂激荡」书、`soul_surge` 改名、求生欲图标、收割者专属文案 | `SoulBooks` / `SoulSkillBookItem` / lang |
| 5 | 副武器「祈灵之刃」 | `SoulKnifeItem` + 短剑归类 + 扁平矩形贯穿 + 额外增伤/收魂 + 配方 | `SoulItems` / `LivingEntityEvents` / `ModItemTagsProvider` / `ModClientEvents` |
| 6 | HUD 与指令 | 灵魂条换 **#abfffe** 淡蓝、图标行分离、`soulSkill reset` / `soulSkill slots`、初始槽位 3 | `TerraStyleManaHud` / `ModCommands` / `SoulSkillStorage` |
| 7 | 六套魂师套装 | 逐件数值 + 套装奖励（隐匿者、逐灵、蓝隐匿者、幻翼、白南瓜、魂舞、灵魂袍） | `ArmorItems` / `ModArmorBonus` / `PlayerUtils` / `LivingEntityEvents` |
| 8 | 套装修正 | 逐灵头盔/靴子的**魔法伤害** → **灵魂伤害** | `ArmorItems` |
| 9 | 五件饰品 | 灵魂手环家族 3 件 + 刻骨的记忆 + 灵魂徽章（配方/掉落/提示） | `AccessoryItems` / 配方 / 战利品 / lang |
| 10 | 饰品分组 | 5 件新饰品归入饰品页签的**战斗**组 | `ModEvents` |
| 11 | 弹幕调参 | 引魂弹幕速度降低 1/3 | `SoulSkillParameters` |
| 12 | 收尾补丁 | 补 `white_pumpkin_set` 套装键；灵魂再生手环接上生命回复 | `ModArmorBonus` / `BandOfSoulRegeneration` |

---

## 1. 魂灯（8 件主武器）

### 1.1 思路

魂灯是"魂师主武器"的第二档产能：不新造武器基类，而是继承既有 `SoulWeaponItem`，只补一件父类没有的事——**把基础暴击率写成主手属性修饰符**，于是 `WeaponTooltip` 会自动多出一行"暴击率 +x%"，与既有无前缀武器（固定 4%）同一口径。

伤害仍走 `SoulWeaponItem#getSoulDamage`（灵魂伤害），不是 `ATTACK_DAMAGE`：实际输出由魂师技能结算，武器只提供基础值与加成。右键施法、手持授予技能全部继承父类。

### 1.2 位置与代码

新增 `common/item/soulcerer/SoulLanternItem.java`（继承 `SoulWeaponItem`）：

```java
private static final ResourceLocation BASE_CRITICAL_CHANCE_ID = Confluence.asResource("base_critical_chance");
addAttributeModifiers(builder -> builder.add(
        LibAttributes.getCriticalChance(),
        new AttributeModifier(BASE_CRITICAL_CHANCE_ID, criticalChance, AttributeModifier.Operation.ADD_VALUE),
        EquipmentSlotGroup.MAINHAND));
```

注册在 `common/init/item/SoulItems.java`，技能 ID 一律 `ModSoulSkills.X.get().getId()`，**不写字符串**。

| 物品 | ID | 灵魂伤害 | 基础暴击 | 稀有度 | 手持临时授予 |
| --- | --- | --- | --- | --- | --- |
| 领魂之灯 | `soul_conduit` | 5 | 4% | WHITE | — |
| 劫掠之火 | `pillaging_flame` | 7 | 4% | BLUE | `soul_plunder` |
| 松露灯 | `truffle_lantern` | 8 | 4% | GREEN | `confuse_spores` |
| 颂魂指引 | `soul_chant_guide` | 9 | **8%** | GREEN | — |
| 生机灿烂 | `life_splendor` | 11 | 4% | ORANGE | `natures_wrath` |
| 星魄 | `star_spirit` | 11 | 4% | ORANGE | `star_call` |
| 无分昼夜 | `day_and_night_alike` | 13 | 4% | LIGHT_RED | `karma_flame` + `profane_soul` |
| 带路的老前辈 | `senior_pathfinder_pro` | 16 | 4% | YELLOW | — |

### 1.3 配套

- 创造栏：`ModTabs.SOUL` 的 `soulcerer_main_weapons` 组按伤害顺序追加（首位仍是既有的 `broken_glimmer`）。
- 模型：`ModItemModelProvider` 逐条 `handheldTextureAlias(...)` → `item/soul_lantern/<id>`。
- 语言：`SoulLanguageSubProvider`。
- 配方（datagen）：

| 物品 | 台子 | 材料 |
| --- | --- | --- |
| 领魂之灯 | 工作台·无序 | 灯笼 + 堕魂星核 |
| 劫掠之火 | 工作台·无序 | 绿宝石 + 翡翠 + 不祥之瓶 + 领魂之灯 |
| 星魄 | 工作台·无序 | 星怒 + 颂魂指引 |
| 颂魂指引 | 重型工作台·有序 | 金锭×8 + 红玉×8 + 领魂之灯 1 |
| 生机灿烂 | 重型工作台·有序 | 毒刺×6 + 丛林孢子×9 + 大自然的恩赐 1 + 颂魂指引 1 |
| 无分昼夜 | 地狱熔炉·无序无燃料 | 下界合金锭 + 狱石锭×10 + 颂魂指引 + 恶魂之泪×2 |
| 带路的老前辈 | 祭坛·无序 | 地牢魔骨×20 + 地牢指南针 + 蜘蛛网×5 |
| 松露灯 | — | **无配方，仅注册**（待定获取） |

`AmountIngredient` 的 count 是**每格**消耗，总消耗 = count × 该字符出现次数——重排图形时容易踩这个坑。

---

## 2. 技能获取方式

### 2.1 手持临时授予

零改动复用：`SoulWeaponItem#getGrantedSoulSkills` → `PlayerUtils.refreshSoulSkills` 读主手 → 并入 `temporarySkills` / `availableSkills` → `SoulSkillSyncPacketS2C` 下发。放下即不可用，**但已装备槽位与冷却保留**。

### 2.2 首次获得永久解锁

用映射表代替 if 分支（`SoulItems`）：

```java
public static final Map<DeferredItem<? extends Item>, List<ResourceLocation>> FIRST_OBTAIN_SOUL_SKILLS = Map.of(
        LIFE_SPLENDOR, List.of(ModSoulSkills.LAW_OF_NATURE.get().getId())
);
```

判定入口 `PlayerUtils.learnSoulSkillsOnFirstObtain(ServerPlayer)`，由 `TickEvents.playerTick$Post` 在 `tickSoulSkills` 之前调用：

1. 遍历整表，技能全学会则跳过；
2. 主背包或副手持有该物品（`PlayerUtils.hasItemInInventory`）；
3. 逐个 `learnSoulSkill`，成功则发动作条 `message.confluence.soulcerer_skill.first_obtain`。

键必须是 `DeferredItem` 而不是 `Item`——注册事件前 `.get()` 会抛异常，不能放进静态 Map。

即「生机灿烂 → 自然法则」永久学会，其余魂灯只给手持临时授予。

---

## 3. 技能书

### 3.1 思路

一套物品承载 N 本书：**组件决定一切**

- 物品仍旧是 `confluence:soulcerer_skill_book`；
- 组件 `confluence:soulcerer_skill`（`ModDataComponentTypes.SOUL_SKILL`）语义升级为**书籍 ID**（旧两本书的 ID 与技能 ID 同名，天然兼容）；
- 新增 `common/item/soulcerer/SoulBooks.java`：`LinkedHashMap<书籍 ID, Definition>`，登记顺序同时决定**模型覆盖序号**与**创造栏顺序**；
- 外观靠客户端 item property `confluence:soul_book` + 模型 `overrides`，序号 0 没有匹配项 → 自动回落到通用书皮。

```java
public record Definition(ResourceLocation id, ModRarity rarity,
                         @Nullable ResourceLocation skill,      // 永久解锁的技能，null = 纯被动
                         @Nullable ResourceLocation required) {} // 使用前置
```

### 3.2 使用逻辑（`SoulSkillBookItem.use`，服务端）

1. `SoulBooks.of(stack)`，未登记 → 失败；
2. 前置技能未永久学会 → 失败，**无消耗无惩罚**；
3. 已生效（技能已学 / 被动已登记）→ `applySoulBookReview` 给「复习」+ 提示；
4. 否则首次生效：技能走 `learnSoulSkill`，被动走 `SoulSkillStorage#learnSoulBook`，发提示（收割者有专属文案）；
5. `awardStat(Stats.ITEM_USED)`——**书不消耗，可反复阅读**。

`getName(stack)` 覆写为 `confluence.soul_book.<id>.name`，未绑定时回落物品名。tooltip 显示：授予的技能名（或引渡者被动说明）+ 使用前置。

### 3.3 书籍总表

| 书 ID | 名称 | 稀有度 | 永久解锁 | 前置 | 获取 |
| --- | --- | --- | --- | --- | --- |
| `psychopomp` | 引渡者 | BLUE | 被动：引魂标签技能伤害 +1 | — | 鬼魂 1% |
| `raiders_path` | 劫掠之道 | BLUE | `boiling_blood` | — | 暂无 |
| `soul_surge` | 灵魂激荡 | GREEN | `soul_surge` | — | 克脑/世吞宝藏袋 100% 池 |
| `harvester` | 收割者 | ORANGE | `surge_blast` | `soul_surge` | 暂无 |
| `firmament_reversed` | 天穹倒转 | GREEN | `firmament_reversed`（原型） | — | 空岛村宝箱 33%；天师 3 级 1 绿宝石币 |
| `blood_world_wrath` | 血与世界之怒 | ORANGE | `blood_rage` | — | 血肉墙/血肉山宝藏袋 100% 池 |
| `survival_instinct` | 求生欲 | WHITE | `survival_instinct` | — | 既有（通用书皮，序号 0） |

### 3.4 「复习」效果 `go_over`

- `ModEffects.GO_OVER = registerEffectWithEnglishName("go_over", "复习", "Go Over", GoOverEffect::new)`；
- `common/effect/soulcerer/GoOverEffect.java`：`BENEFICIAL` + 颜色 `0xC8A165` + `addAttributeModifier(getSoulDamage(), confluence:go_over, 0.05, ADD_MULTIPLIED_TOTAL)`；
- 时长 `SOUL_BOOK_REVIEW_DURATION_TICKS = 12000`（10 分钟），原版 `addEffect` 语义 = 刷新不叠层；
- 说明文本 `tooltip.effect.confluence.go_over.0`。

### 3.5 引渡者被动（非技能）

- `SoulSkillStorage` 新字段 `Set<ResourceLocation> learnedSoulBooks`（NBT `learnedSoulBooks`，随 `copyOnDeath` 存档）；
- 接口 `getLearnedSoulBooks()` / `learnSoulBook(id)` / `PlayerUtils.isSoulBookLearned(player, id)`；
- 伤害入口 `PlayerUtils.getSoulSkillDamage`：先让 `SoulSkillDamageEvent` 结算技能+武器总伤害，若书已学且技能在 `ModTags.SoulSkills.SOUL_LURE` 标签内，再 **+1**，最后才乘灵魂伤害倍率。

```java
float amount = Math.max(0.0F, event.getAmount());
if (isSoulBookLearned(player, SoulBooks.PSYCHOPOMP) && stack.getSoulSkill().is(ModTags.SoulSkills.SOUL_LURE)) {
    amount += SoulSkillParameters.PSYCHOPOMP_SOUL_LURE_DAMAGE_BONUS;
}
```

### 3.6 新标签「引魂」

`ModTags.SoulSkills.SOUL_LURE` → `confluence:soul_lure`，7 个成员：`confuse_spores`、`profane_soul`、`natures_wrath`、`star_call`、`lure_surge`、`enhanced_lure`、`soul_lure`。生成源 `ModSoulSkillTagsProvider` 里显式 `add`，与按分类自动生成的 `soul`/`blood_nature`/`star_lure` 互不影响。

### 3.7 新技能 `firmament_reversed`

`ModSoulSkills` 增加原型条目 `register("firmament_reversed", 0, SkillCategory.STAR_LURE)`：只有注册与解锁，`basicDamage = 0`、无 `use` 实现。名称键补在 `InterfaceLanguageSubProvider`（否则 tooltip 露出原始键）。

### 3.8 获取途径

| 途径 | 落点 |
| --- | --- |
| 鬼魂 1% 引渡者 | `EntitySubProvider` 新建 `entities/ghost` 表 |
| 克脑 3 张宝藏袋 | `GiftSubProvider` 每张表 `.withPool(soulBookLoot(SoulBooks.SOUL_SURGE))` |
| 世吞 3 张宝藏袋 | `eaterOfWorldsTreasureBagCommon()` 内加 |
| 血肉墙/血肉山 6 张 | `wallOfFleshTreasureBagCommon()` 内加 `blood_world_wrath` |
| 空岛村宝箱 33% | `ChestSubProvider` 独立池 + `random_chance 0.33` |
| 天师 3 级交易 | `ModVillagers`：1 绿宝石币 → 天穹倒转书（`maxUses 12 / xp 10 / 0.05`） |
| 骷髅王宝藏袋 66% 刻骨的记忆 | `GiftSubProvider.memoryOfBonebrandLoot()`，3 张表共用 |

「100% 抽取池」= 不带概率条件的独立 `LootPool`（`rolls = 1`，必掉）。稀有度在掉落里用链式两个 `SetComponentsFunction`（书籍 ID + `MOD_RARITY`）。

---

## 4. 书与文案修正

| 项 | 改动 | 位置 |
| --- | --- | --- |
| 书可反复读 | 去掉 `stack.consume`，只记 `Stats.ITEM_USED` | `SoulSkillBookItem` |
| `soul_surge` 中文名 | 「附魂激荡」→「灵魂激荡」 | `InterfaceLanguageSubProvider` |
| 通用书皮「灵魂激荡」书 | 删除 `plain(SPIRIT_SURGE, ...)` 及它专用的 5% 书池（`soulSkillBookLoot()` 与 6 处用法），避免掉出查不到定义的书 | `SoulBooks` / `GiftSubProvider` |
| 求生欲图标 | 构造参数由复用的 `soul_lure` 改为自身 ID，启用 `hud/soulcerer_skill/survival_instinct.png` | `ModSoulSkills` |
| 收割者文案 | 新增 `SoulBooks.USE_MESSAGE_KEYS`，`harvester` → `message.confluence.soul_book.harvester`（「引魂系列技能的威力增强了！」） | `SoulBooks` / lang |
| 技能状态文案 | `confluence.soulcerer_skill.state.learned` =「已永久解锁 / Permanently unlocked」 | `SoulLanguageSubProvider` |

---

## 5. 副武器「祈灵之刃」

### 5.1 命名沿革

实现时叫「灵魂之刃 `soul_knife`」，后按需求改名为**祈灵之刃 `spiritward_blade`**：物品 ID、贴图（`textures/item/sword/spiritward_blade.png`）、语言键、参数名（`SPIRITWARD_BLADE_*`）全部同步；**Java 类名 `SoulKnifeItem` 与事件方法名 `soulKnifeReach/BonusDamage/ExtraSoul` 保留旧名**（纯内部标识，不影响数据与显示）。

### 5.2 属性与流派

| 项 | 值 | 说明 |
| --- | --- | --- |
| 总伤害 | 6 | `ModItems.createAttributes(ModTiers.UNBREAKABLE, 4.0F, -2.4F, ModItems.attributes(0, 0.4F))`，走 `ATTACK_DAMAGE` |
| 总攻速 | 1.6 | 同上（-2.4 修正） |
| 基础暴击 | 4% | `attributes(0, 0.4F)` |
| 击退 | +0.4 | 同上 |
| 稀有度 | BLUE | — |

即**吃近战加成、不吃灵魂伤害加成**，与魂灯形成"战士侧 / 术士侧"对照。

### 5.3 三条行为

1. **扁平矩形贯穿**（`LivingEntityEvents.soulKnifeReach`，监听 `AttackEntityEvent`）：以眼位为起点、沿视线 `REACH = 4.5` 格、半宽/半高均 `0.45` 的 AABB 补打除主目标外的敌怪；需蓄力 ≥ `MIN_CHARGE = 0.9`（防连点刷伤害）；伤害 `attackValue * (0.2 + s² * 0.8)`，与原版蓄力公式一致，主目标仍由原版结算，不会双打。
2. **额外增伤**（`soulKnifeBonusDamage`，监听 `LivingIncomingDamageEvent`）：主手持刃且当前灵魂 > 10 → **+3**（在护甲减伤之前，与"额外攻击力"同口径）。
3. **额外收魂**（`soulKnifeExtraSoul`，监听 `SoulCollectionEvent`）：主手持刃击杀 → **+1 灵魂**（Boss 本体直接回满的路径不受影响）。

### 5.4 归类与资源

- 短剑：`ModItemTagsProvider` 把 `spiritward_blade` 加入 `confluence:short_sword` 标签，并注册 `ShortSwordInHandRenderer` 使手持姿态与短剑一致；
- 模型 `handheldTextureAlias(... item/sword/spiritward_blade)`；
- 配方（工作台·无序）：`confluence:fallen_soul_core` + （`iron_short_sword` 或 `lead_short_sword`）；
- 创造栏：魂师页签 `soulcerer_secondary_weapons` 组。

---

## 6. HUD、指令与槽位

### 6.1 灵魂条配色与图标行（`client/gui/hud/TerraStyleManaHud`）

- 灵魂条目标色 **#abfffe**（`SOUL_TINT_* = 0xAB/255, 1.0, 0xFE/255`），调色板 `SOUL / SOUL_LOW / SOUL_HIGH` 改为中性亮度，最终呈淡蓝而非紫；LEGACY 与 OVERLAY 两种样式共用同一套配色。
- 图标行分离：魔力 `OVERLAY_DEFAULT_UV_Y = 10`，灵魂 `OVERLAY_SOUL_UV_Y = 60`（`hud/overlay.png` 最下面一行），原来两者共用 10。

### 6.2 指令（`ModCommands`，权限 2）

| 指令 | 作用 |
| --- | --- |
| `/confluence soulSkill reset` | 清空全部装备槽、索引归零、清空技能冷却并重算可用技能；**不动**永久学习与技能书记录 |
| `/confluence soulSkill slots <3..6>` | 设置可装备槽数（参数被 `IntegerArgumentType.integer(3, 6)` 限制），改完立即同步槽位、索引与技能数据 |

落地实现仍是静态工具：`PlayerUtils.resetSoulSkillEquip` / `PlayerUtils.setSoulSkillSlotCount`，指令只做校验与消息。文档已写入 `assets/confluence/ageratum/zh_cn/detailed_information/command/command_list.md`。

### 6.3 初始槽位

`SoulSkillStorage.DEFAULT_EQUIPPED_SOUL_SKILL_COUNT` 由 **6 改为 3**，新玩家默认 3 槽，上限仍可扩到 6。

---

## 7. 六套魂师套装

### 7.1 设计

- **逐件数值**写在 `ArmorItems`（`BaseArmorItem.Builder` 的 `.attribute(...)` / `.criticalChance(...)` / `.rangedDamage(...)` / `.armorBonus(PrimitiveValueComponent.of(...))`）；
- **套装奖励**写在 `ModArmorBonus.register("<id>_set", tooltipCount, 头, 胸, 腿, 脚, key -> {...})`；
- 新增通用值类型 `SOUL$DROP = ValueType.FloatType.of(confluence:soul_drop, ADDITION, 0)`：收魂时额外获得的灵魂，**逐件与套装共用同一个值**，由 `LivingEntityEvents.soulDropBonus` 一次性汇总（`ModArmorBonus.getValue(player, SOUL$DROP)`）；
- 需要"计时/累计"的套装（魂舞、蓝隐匿者）不在 `ModArmorBonus` 里做运算，只在 `SoulStorage` 存状态，由 `PlayerUtils` 结算——保持"数据存附件、逻辑在工具类"的既有分工。

### 7.2 逐件数值（本轮写入的值）

| 套装 | 头盔 | 胸甲 | 护腿 | 靴子 |
| --- | --- | --- | --- | --- |
| 隐匿者 `hermit` | 灵魂伤害 +3% | 暴击 +8%（既有） | 收魂 +1 | 灵魂伤害 +3% |
| 逐灵 `seeker` | 灵魂伤害 +5% | 暴击 +7% | 收魂 +2 | 灵魂伤害 +5% |
| 蓝隐匿者 `blue_hermit` | 四职业伤害 +4%（既有） | 暴击 +3%（既有） | 四职业伤害 +4%（既有） | 收魂 +1 |
| 幻翼 `phantom` | 灵魂伤害 +3% | 暴击 8% → **3%** | 远程伤害 +3% | 远程伤害 +3% |
| 白南瓜 `white_pumpkin` | 灵魂伤害 +5% | 暴击 +5%（既有） | 近战 +4%（既有） | 近战 +4%（既有） |
| 魂舞 `souldancer` | 灵魂伤害 +9%、收魂 +1 | 暴击 +8%、收魂 +1 | —（两件套） | — |
| 灵魂头套 / 长袍（无套装） | 收魂 +1 | 灵魂伤害 +5% | — | — |

### 7.3 套装奖励

| 套装 ID | 说明行数 | 效果 | 结算位置 |
| --- | --- | --- | --- |
| `hermit_set` | 1 | 灵魂上限 **+20**（`ADDITIONAL_SOUL` ADD_VALUE，`SoulStorage` 实时读取） | 属性系统 |
| `seeker_set` | 2 | 灵魂上限 **+40**；灵魂 > 20 时伤害 **×1.07** | 属性 + `seekerSetDamage` |
| `phantom_set` | 1 | 收魂 **+1**；夜间再 **+2** | `soulDropBonus` |
| `blue_hermit_set` | 1 | 每累计消耗 **10** 灵魂，耐药性剩余时长 **-3 秒** | `SoulStorage.consumedSoul` + `PlayerUtils.onSoulSpent` |
| `souldancer_robe_set` | 2 | 收魂后 **10 秒**：暴击 +8%（ADD_VALUE）、移速 +7%、受伤 -7% | `SoulStorage.souldancerBuffEnd` + `tickSoulArmorSets` / `souldancerDamageReduction` |
| `white_pumpkin_set` | 1 | 近战暴击回复 **3** 点灵魂 | `processCriticalDamage` + `isWearingWhitePumpkinSet` |

要点：

- 蓝隐匿者的"累计消耗"存**余量**（`consumedSoul` 只保留不足 10 的部分），跨刻累加不丢；
- 魂舞增益用**游戏刻结束时间**判定，`tickSoulArmorSets` 每刻 `addOrUpdateTransientModifier`，到期 `removeModifier`，不写永久属性；
- 白南瓜的套装键 `white_pumpkin_set` 与 `blue_hermit_set` / `souldancer_robe_set` 一样**只用于套装识别与说明行**（`register` 的 consumer 为空），实际回魂在 `processCriticalDamage` 里按四件判定结算。原先漏了这条注册，说明行不会显示，已补（见 §14.1）。

---

## 8. 逐灵套装修正（魔法伤害 → 灵魂伤害）

发现逐灵头盔/靴子被写成了 `.magicDamage(0.05)`，按需求改回灵魂伤害：

```java
.attribute(LibAttributes.getSoulDamage(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
```

即**整套魂师套装只用灵魂伤害属性**，不与四职业伤害混用（蓝隐匿者沿用既有的四职业 +4%，属特例）。

---

## 9. 五件饰品

### 9.1 位置与注册

全部挂在 `common/init/item/AccessoryItems.java`，用 `registerCurio(id, 中文名, builder -> ...)`（TerraCurio 饰品框架），新增两个单位值类型：

```java
public static final ValueType.UnitType BAND$OF$SOUL = ValueType.ofUnit("band_of_soul");        // 三手环共用
public static final ValueType.UnitType MEMORY$OF$BONEBRAND = ValueType.ofUnit("memory_of_bonebrand");
```

| 物品 | ID | 稀有度 | 属性/标记 |
| --- | --- | --- | --- |
| 灵魂手环 | `band_of_soul` | BLUE | `BAND$OF$SOUL`（每 3 秒 +1 灵魂） |
| 灵魂再生手环 | `band_of_soul_regeneration` | GREEN | `BAND$OF$SOUL` + 每秒 0.2 生命（`BandOfSoulRegeneration`） |
| 通灵手环 | `band_of_mediumship` | GREEN | `ADDITIONAL_SOUL +20`（ADD_VALUE）+ `BAND$OF$SOUL` |
| 刻骨的记忆 | `memory_of_bonebrand` | PURPLE | `MEMORY$OF$BONEBRAND`（无武器也能施法） |
| 灵魂徽章 | `soulcerer_emblem` | LIGHT_RED | `getSoulDamage() +12%`（ADD_MULTIPLIED_TOTAL），`noTooltip` |

`BAND$OF$SOUL` 是**一个标记打三个物品**：灵魂回复只在 `PlayerUtils.regenerateSoul` 判一次 `TCUtils.hasType(player, BAND$OF$SOUL)`，三件手环都吃到，不必逐个判断。

### 9.2 灵魂回复逻辑（`PlayerUtils.regenerateSoul`）

```java
// 灵魂手环：每 3 秒回复 1 点灵魂
if (TCUtils.hasType(player, AccessoryItems.BAND$OF$SOUL) && gameTime % 60 == 0 && (gameTime / 60) % 3 == 0) {
    storage.receive(SoulSkillParameters.BAND_OF_SOUL_RECOVERY);
}
```

挂在原有的"每秒一次"入口里，用 `(gameTime / 60) % 3` 做 3 秒节拍；数值 `BAND_OF_SOUL_RECOVERY = 1.0F`。

### 9.2b 生命回复逻辑（`BandOfSoulRegeneration`）

灵魂再生手环的名字里有"再生"，因此额外带一份缓慢回血，做法与泰拉瑞亚再生手环完全一致——**在物品的 `curioTick` 里调 MagicLib 的每秒节拍**，不自己计时：

```java
public class BandOfSoulRegeneration extends BaseCurioItem {
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
        if (slotContext.entity() instanceof ServerPlayer player) {
            LibEffects.healPerSecond(player, SoulSkillParameters.BAND_OF_SOUL_REGENERATION_HEAL_PER_SECOND);
        }
    }
}
```

- 注册从 `registerCurio` 改为 `registerDirectly(... new BandOfSoulRegeneration(BaseCurioItem.builder(name)...))`，物品 ID、稀有度、`BAND$OF$SOUL` 标记、语言产出全部不变；
- 速率 `BAND_OF_SOUL_REGENERATION_HEAL_PER_SECOND = 0.2F`（每秒 0.2 点 = 每 5 秒 1 点），与 `TCItems.BAND_OF_REGENERATION` 同档；
- 只在服务端结算（`ServerPlayer` 判定），且与灵魂模式无关——不开灵魂模式也照常回血。

### 9.3 刻骨的记忆

只改一处闸门（`PlayerUtils.tryUseSoulSkill`）：

```java
if (!Confluence.SOUL_SKILLS || !isSoulMode(player)
        || (!isHoldingSoulWeapon(player) && !TCUtils.hasType(player, AccessoryItems.MEMORY$OF$BONEBRAND))) {
    return false;
}
```

即"手持主武器"这一前置被饰品豁免，其余（灵魂模式、消耗、冷却、结算）完全不变。

### 9.4 配方与掉落

| 物品 | 途径 |
| --- | --- |
| 灵魂手环 | 工作台·无序：星力手环 + 堕魂星核；另有**反向**配方拆回星力手环 |
| 灵魂再生手环 | 灵雾工作台：灵魂手环 + 再生手环（`TCItems.BAND_OF_REGENERATION`） |
| 通灵手环 | 灵雾工作台：灵魂手环 + 星力手环 |
| 灵魂徽章 | 灵雾工作台：战士 / 射手 / 法师徽章（TerraCurio）或召唤师徽章（本模组）**任一**徽章 |
| 刻骨的记忆 | 骷髅王宝藏袋通用池 66%（`memoryOfBonebrandLoot()`，经典/专家/大师三张表共用） |

### 9.5 提示文本

`TooltipLanguageSubProvider` 五条 `addTooltip`（灵魂回复、灵魂+生命、灵魂上限 +20、无武器施法、灵魂伤害 +12%），`go_over` 的说明也在此文件。

---

## 10. 饰品创造栏分组

### 10.1 结构

饰品页签本体在 **TerraCurio** 的 `TCTabs.ACCESSORIES`（`creativetab.terra_curio`），分类沿用泰拉 Wiki：移动 / 信息 / 生命和魔力 / 战斗 / 减益免疫 / 建筑 / 悠悠球 / 其他 / 专家模式独有。

本模组的饰品**不在** `ModTabs` 里登记，而是在 `ModEvents.buildCreativeModeTabContents`（`EventPriority.LOW`）里监听该页签，用 `TCTabs.acceptGroup(output, "<组名>", ...)` 追加——组名对应 `itemGroup.terra_curio.<组名>`，`GroupItem.belongsTo` 负责把物品写进组。

### 10.2 本次改动

把 5 件新饰品放进 `combat` 组：

```java
TCTabs.acceptGroup(output, "combat",
        AccessoryItems.CELESTIAL_EMBLEM, AccessoryItems.SUMMONER_EMBLEM, AccessoryItems.SOULCERER_EMBLEM,
        ...,
        // 魂师：灵魂手环与刻骨的记忆
        AccessoryItems.BAND_OF_SOUL, AccessoryItems.BAND_OF_SOUL_REGENERATION,
        AccessoryItems.BAND_OF_MEDIUMSHIP, AccessoryItems.MEMORY_OF_BONEBRAND);
```

灵魂徽章紧挨其它徽章；三手环 + 记忆排在战斗组末尾。因为事件优先级 LOW，它们会排在泰拉原生战斗饰品之后。**没有新建组，也没有改 TerraCurio。**

---

## 11. 引魂弹幕实装与调参

### 11.1 实装

`common/entity/soulcerer/SoulLureProjectile.java` 改为法杖式直线弹幕：

- 飞行参数集中在 `SoulSkillParameters`：`SOUL_LURE_SPEED` / `SOUL_LURE_MAX_DISTANCE = 40` / `SOUL_LURE_MAX_AGE_TICKS = 120`；
- 视觉改用**粒子风暴**：`PARTICLE = confluence:soul_projectile`，客户端 `ParticleEmitter` + `attachEntity(this)` 挂到弹幕上，拖尾与收尾全由该特效负责；
- 原 `client/renderer/entity/soulcerer/SoulLureRenderer.java` **删除**，`ModClientEvents` 改为 `NoopRenderer`；
- 粒子定义 `assets/confluence/particle_definitions/soul_projectile.particle.json`：`texture` 修正为 `confluence:soul_projectile`（贴图 `textures/particle/soul_projectile.png`，16×112 = 7 帧），收尾由火焰的 `flamelash_expiration*` 换成新增的 `confluence:soul_expiration`（克隆蓝玉法杖收尾贴图，tint 固定淡蓝 `[0.671, 1.0, 0.996, 0.8]`，去掉原来的第二个球状收尾事件）。

### 11.2 速度调整（最后一批）

`SOUL_LURE_SPEED` 由 `1.6F` 改为：

```java
/// 基础速度 1.6 格/刻，再降低三分之一（×2/3）→ 约 1.0667 格/刻。
public static final float SOUL_LURE_SPEED = 1.6F * 2.0F / 3.0F;
```

该值经 `shootFromRotation(..., SoulSkillParameters.SOUL_LURE_SPEED, 0)` 直接作为初速度，因此速度降 33.3%；**射程（40 格）与存活（120 刻）不变**——满射程耗时由 25 刻变 37.5 刻，远小于存活上限，不会提前消失。`SOUL_LURE_SPEED` 全工程仅此一处引用。

---

## 12. 数值总表（`SoulSkillParameters`）

单一调参入口，全部为 `public static final`：

| 字段 | 值 | 用途 |
| --- | --- | --- |
| `SOUL_LURE_SPEED` | `1.6 × 2/3 ≈ 1.0667` | 引魂弹幕速度（格/刻） |
| `SOUL_LURE_MAX_DISTANCE` | 40.0 | 引魂弹幕射程 |
| `SOUL_LURE_MAX_AGE_TICKS` | 120 | 引魂弹幕存活上限 |
| `PSYCHOPOMP_SOUL_LURE_DAMAGE_BONUS` | 1.0 | 引渡者 +1 基础伤害 |
| `SOUL_BOOK_REVIEW_DURATION_TICKS` | 12000 | 复习时长（10 分钟） |
| `GO_OVER_SOUL_DAMAGE_BONUS` | 0.05 | 复习期间灵魂伤害 +5% |
| `SPIRITWARD_BLADE_SOUL_THRESHOLD` | 10.0 | 刃增伤所需灵魂 |
| `SPIRITWARD_BLADE_DAMAGE_BONUS` | 3.0 | 刃额外近战伤害 |
| `SPIRITWARD_BLADE_KILL_SOUL_BONUS` | 1.0 | 刃击杀额外收魂 |
| `SPIRITWARD_BLADE_REACH` | 4.5 | 贯穿长度 |
| `SPIRITWARD_BLADE_HALF_WIDTH / HALF_HEIGHT` | 0.45 / 0.45 | 贯穿半宽/半高 |
| `SPIRITWARD_BLADE_MIN_CHARGE` | 0.9 | 贯穿最低蓄力 |
| `BLUE_HERMIT_SOUL_PER_REDUCTION` | 10.0 | 每 10 灵魂 |
| `BLUE_HERMIT_SICKNESS_REDUCTION_TICKS` | 60 | 每次削减 3 秒耐药性 |
| `PHANTOM_NIGHT_SOUL_DROP_BONUS` | 2.0 | 幻翼夜间额外收魂 |
| `WHITE_PUMPKIN_CRIT_SOUL` | 3.0 | 白南瓜近战暴击回魂 |
| `SEEKER_DAMAGE_SOUL_THRESHOLD / BONUS` | 20.0 / 0.07 | 逐灵增伤阈值与幅度 |
| `SOULDANCER_BUFF_DURATION_TICKS` | 200 | 魂舞增益时长 |
| `SOULDANCER_CRIT_BONUS / SPEED_BONUS / DAMAGE_REDUCTION` | 0.08 / 0.07 / 0.07 | 魂舞三效果 |
| `BAND_OF_SOUL_RECOVERY` | 1.0 | 手环每次回复量（每 3 秒） |
| `BAND_OF_SOUL_REGENERATION_HEAL_PER_SECOND` | 0.2 | 灵魂再生手环每秒回血 |

---

## 13. 后续扩展怎么做（速查）

| 目标 | 只改这里 |
| --- | --- |
| 新增魂灯 | 贴图 → `SoulItems` 注册 → `ModTabs` 主武器组 → `ModItemModelProvider` → 语言 → 配方 |
| 新增技能书 | 贴图 `item/skill_book/<id>.png` → `SoulBooks` 静态块一行 → 语言 `confluence.soul_book.<id>.name`；外观/覆盖序号/创造栏/掉落绑定全自动跟随 |
| 新增纯被动书 | 参照引渡者：`SoulSkillStorage` 记录 + `PlayerUtils` 查记录 + 语言一条 tooltip |
| 新增首得解锁 | `SoulItems.FIRST_OBTAIN_SOUL_SKILLS` 加一条映射 |
| 新增套装 | `ModArmorBonus.register("<id>_set", 说明行数, 头/胸/腿/脚, key -> {...})` + `ArmorBonusLanguageSubProvider` 写 `armor_set_bonus.confluence.<id>.<n>` |
| 新增收魂加成件 | `ArmorItems` 里 `.armorBonus(PrimitiveValueComponent.of(ModArmorBonus.SOUL$DROP, n))`，汇总已统一 |
| 新增魂师饰品 | `AccessoryItems` 注册（需要多件共用一个标记就 `ValueType.ofUnit(...)`）+ 放进 `ModEvents` 的某个组 + `TooltipLanguageSubProvider` |
| 调弹幕/刃/套装数值 | `SoulSkillParameters` |
| 调灵魂条颜色 | `TerraStyleManaHud.SOUL_TINT_*` 与 `SOUL*` 调色板 |

工程约定（本轮一直遵守）：

- 魂师通用操作一律静态放 `PlayerUtils`，不新建 Handler；
- 属性/数值走 `SoulSkillParameters` 与 `ModArmorBonus`，不散落在事件里；
- `SoulSkillStack` 不允许 null，空栈用 `SoulSkillStack.Empty`；
- `common` 不引用客户端类；注册表 ID 一律用 `ModSoulSkills.X.get().getId()`；
- 文件保持 **CRLF + UTF-8 无 BOM**；`src/generated` 是构建产物（gitignore）。

---

## 14. 未完成与风险

### 14.1 已补齐的两处缺失

1. **白南瓜套装说明行**：`ModArmorBonus` 补上 `register("white_pumpkin_set", 1, WHITE_PUMPKIN_HELMET, …, key -> {})`。与 `blue_hermit_set` / `souldancer_robe_set` 同款——consumer 为空，只做套装识别 + 占位说明行；效果本身仍在 `LivingEntityEvents.processCriticalDamage` 按四件判定。补完后 `armor_set_bonus.confluence.white_pumpkin_set.0`（「近战暴击会回复3点灵魂值」）能正常显示。
2. **灵魂再生手环的回血**：新增 `common/item/accessory/BandOfSoulRegeneration`，在 `curioTick` 里调 `LibEffects.healPerSecond(player, 0.2F)`；注册从 `registerCurio` 换成 `registerDirectly`（物品 ID / 稀有度 / `BAND$OF$SOUL` 标记 / 语言产出全部不变），数值进 `SoulSkillParameters.BAND_OF_SOUL_REGENERATION_HEAL_PER_SECOND`。详见 §9.2b。

两处补完后 `compileJava` 与 `runData` 均 BUILD SUCCESSFUL。

3. **`soul_sting` 渲染器**：`SoulStingProjectileRenderer`（自然之惩戒毒刺，复用黄蜂毒刺模型）此前**只建了类、从未注册**，导致毒刺不可见。已在 `ModClientEvents` 补 `event.registerEntityRenderer(SOUL_STING.get(), SoulStingProjectileRenderer::new)`，1.20.1 侧同步补齐。

4. **1.20.1 移植完成**：1.20.1 侧的本批内容（魂灯、书、祈灵之刃、6 套套装、5 件饰品、指令、创造栏、数据生成、客户端）已全部补齐并 `compileJava` + `runData` 双绿，细节见 `confluence-1.20.1/ConfluenceOtherworld/notes/SOUL_PORT_HANDOVER.md`。1.20.1 用 `BindSoulSkillFunction` 代替 1.20.1 不存在的 `SetComponentsFunction`，书池写入的是**书籍 ID**（此前写成技能 ID，掉出的是查不到定义的坏书，已修）。

### 14.2 仍未完成与风险

| # | 项 | 说明 |
| --- | --- | --- |
| 1 | `go_over` 图标是占位 | `textures/mob_effect/go_over.png` 为 `enhanced_soul.png` 的字节复制，待正式美术替换 |
| 2 | `firmament_reversed` 是原型 | 只注册 + 解锁，没有 `use` 效果 |
| 3 | 中文名撞车 | `soulcerer_skill.soul_surge` 与 `soulcerer_skill.spirit_surge` 中文都是「灵魂激荡」（ID 不同，无功能冲突） |
| 4 | 松露灯无获取途径 | 仅注册 + 创造栏 |
| 5 | 未游戏实测 | 施法、书皮切换、复习刷新、贯穿手感、套装触发、饰品分组排序、弹幕速度均为逻辑层完成 |
| 6 | 旧记录未同步 | `notes/SOUL_DEVELOPMENT.md`、`notes/SOUL_PLAN.md` 未包含本批内容；`i18n/{zh_cn,en_us}.json` 是更早的对照快照，仍写「白南瓜：回复 1 点灵魂」等旧值 |
| 9 | 1.20.1 完全未改 | 该版本缺 `SoulStorage`/`SoulSkillStorage`、6 个灵魂事件、6 个灵魂包、全部灵魂物品/实体/效果，以及 Magic-Lib 的 `SOUL_DAMAGE` / `ADDITIONAL_SOUL` / `soul_overdraft` 伤害类型；同步时按本文逐项移植 |

---

## 15. 文件索引

路径相对 `ConfluenceOtherworld/src/main/java/org/confluence/mod`。

### 15.1 新增（Java）

- `common/effect/soulcerer/GoOverEffect.java`
- `common/item/accessory/BandOfSoulRegeneration.java`（灵魂再生手环的回血实现）
- `common/item/soulcerer/SoulBooks.java`
- `common/item/soulcerer/SoulLanternItem.java`
- `common/item/soulcerer/SoulKnifeItem.java`（祈灵之刃，类名沿用旧称）

### 15.2 删除（Java）

- `client/renderer/entity/soulcerer/SoulLureRenderer.java`（改由粒子风暴渲染）

### 15.3 修改（Java，35 个文件）

本轮 `git diff` 共 37 个文件：Java 修改 35、Java 删除 1（见 15.2）、文档 1（见 15.4）。

| 文件 | 涉及批次 |
| --- | --- |
| `client/event/ModClientEvents.java` | 5（短剑姿态、弹幕渲染器） |
| `client/event/ModClientSetups.java` | 3（书皮 property） |
| `client/gui/hud/TerraStyleManaHud.java` | 6（配色、图标行） |
| `common/attachment/SoulStorage.java` | 7（`consumedSoul` / `souldancerBuffEnd`） |
| `common/attachment/SoulSkillStorage.java` | 3/6（书籍记录、默认 3 槽） |
| `common/data/gen/ModItemModelProvider.java` | 1/3/5（魂灯、书皮、刃） |
| `common/data/gen/language/ArmorBonusLanguageSubProvider.java` | 7（逐灵 7%、白南瓜 3 点） |
| `common/data/gen/language/InterfaceLanguageSubProvider.java` | 3/4（技能名修正、`firmament_reversed`） |
| `common/data/gen/language/SoulLanguageSubProvider.java` | 1~5（物品名、书名、消息、状态文案） |
| `common/data/gen/language/TooltipLanguageSubProvider.java` | 3/9（`go_over`、5 条饰品） |
| `common/data/gen/loot/ChestSubProvider.java` | 3（空岛村 33%） |
| `common/data/gen/loot/EntitySubProvider.java` | 3（鬼魂 1%） |
| `common/data/gen/loot/GiftSubProvider.java` | 3/4/9（书池、删旧 5% 池、记忆 66%） |
| `common/data/gen/recipe/CraftingRecipeProvider.java` | 1/5/9 |
| `common/data/gen/recipe/HeavyWorkBenchProvider.java` | 1 |
| `common/data/gen/recipe/ModRecipeProvider.java` | 1/9（地狱熔炉、祭坛、灵雾工作台×3） |
| `common/data/gen/tag/ModItemTagsProvider.java` | 5（`short_sword`） |
| `common/data/gen/tag/ModSoulSkillTagsProvider.java` | 3（`soul_lure` 7 员） |
| `common/entity/soulcerer/SoulLureProjectile.java` | 11（粒子风暴、速度） |
| `common/event/ModEvents.java` | 10（饰品战斗组） |
| `common/event/game/TickEvents.java` | 2/7（首得解锁、套装 tick） |
| `common/event/game/entity/LivingEntityEvents.java` | 5/7（贯穿、增伤、收魂、逐灵、魂舞、白南瓜） |
| `common/init/ModCommands.java` | 6（两条子指令） |
| `common/init/ModEffects.java` | 3（`go_over`） |
| `common/init/ModSoulSkills.java` | 3/4（`firmament_reversed`、求生欲图标） |
| `common/init/ModTabs.java` | 1/3/5（主武器、副武器、技能书组） |
| `common/init/ModTags.java` | 3（`SOUL_LURE` 标签键） |
| `common/init/ModVillagers.java` | 3（天师 3 级交易） |
| `common/init/armor/ModArmorBonus.java` | 7/14（`SOUL$DROP`、6 个套装键含白南瓜） |
| `common/init/item/AccessoryItems.java` | 9/14（2 个值类型 + 5 件饰品，再生手环改 `registerDirectly`） |
| `common/init/item/ArmorItems.java` | 7/8（7 套逐件数值） |
| `common/init/item/SoulItems.java` | 1/5（8 魂灯、祈灵之刃、首得映射、`soulBook`） |
| `common/item/soulcerer/SoulSkillBookItem.java` | 3/4（使用逻辑、名称、tooltip） |
| `common/soulcerer/skill/SoulSkillParameters.java` | 3/5/7/11（全部新数值） |
| `util/PlayerUtils.java` | 2/3/5/6/7/9（首得解锁、书籍被动、刃、指令落地、套装、手环、记忆豁免） |

### 15.4 文档

- `assets/confluence/ageratum/zh_cn/detailed_information/command/command_list.md`（两条指令）
- `notes/SOUL_LANTERN_AND_BOOK.md`（前三批详细记录）
- `notes/SOUL_CHANGES_MASTER.md`（本文）

### 15.5 资源（`src/main/resources/assets/confluence`）

新增：

- `textures/item/sword/spiritward_blade.png`
- `textures/mob_effect/go_over.png`（占位）
- `textures/item/skill_book/firmament_reversed.png`
- `textures/item/accessory/{band_of_soul,band_of_soul_regeneration,band_of_mediumship,memory_of_bonebrand(+.mcmeta),soulcerer_emblem}.png`
- `textures/gui/sprites/hud/soulcerer_skill/survival_instinct.png`
- `textures/particle/soul_projectile.png`
- `particle_definitions/soul_projectile.particle.json`、`particle_definitions/soul_expiration.particle.json`
- `textures/item/soul_lantern/*.mcmeta` × 7（动画帧配置；`soul_conduit` 为静态图）

既有引用（未改）：`textures/item/soul_lantern/*.png`、`textures/item/skill_book/*.png`（除新加的 `firmament_reversed`）。

---

## 16. 验证记录

| 批次 | 编译 | datagen |
| --- | --- | --- |
| 1~3 魂灯/技能书 | BUILD SUCCESSFUL | BUILD SUCCESSFUL（沿用既有 `minecraft:entities/slime` 校验警告） |
| 4 文案修正 | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| 5 祈灵之刃 | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| 6 HUD/指令 | BUILD SUCCESSFUL | — |
| 7/8 套装 | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| 9 饰品 | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| 10 饰品分组 | BUILD SUCCESSFUL（仅代码） | — |
| 11 弹幕速度 | BUILD SUCCESSFUL（仅代码） | — |
| 14 收尾补丁（白南瓜套装键 + 手环回血） | BUILD SUCCESSFUL | BUILD SUCCESSFUL |

注：PowerShell 调用 `gradlew` 时 stderr 的“过时 API”提示会让包装进程回传 exit 1，判定以输出中的 `BUILD SUCCESSFUL` 为准。
---

## 17. 全技能实装与仪式系统（本次批次）

### 17.1 技能总表（31 项，含空技能）

| 中文 | ID | 类型 | 机制要点 | 解锁 |
| --- | --- | --- | --- | --- |
| 引魂 | `soul_lure` | 主动 | 单体灵魂弹幕（粒子风暴 `confluence:soul_projectile`） | 初始 |
| 强力附魂 | `enhanced_soul` | 附魂 | 同名增益，灵魂伤害 ×5，5 秒 | 初始 |
| 灵魂激荡 | `spirit_surge` | 主动 | 以自身为中心的扩散冲击波 | 技能书【灵魂激荡】 |
| 灵魂激荡冲击 | `surge_blast` | 主动 | 每 3 次激荡追加半径 8 的终极爆炸 | 技能书【收割者】（需先学灵魂激荡） |
| 血怒 | `blood_rage` | 附魂 | 扣 20% 生命换近战/远程 +40%，期间击杀 +1 魂 | 技能书【血与世界之怒】 |
| 强力引魂 | `enhanced_lure` | 主动 | 引魂强化弹幕，基础伤害 8 | 仪式 1 |
| 附魂印记 | `soul_mark` | 主动 | 弹幕 + 同步获得强力附魂同时长增益 | 仪式 1 |
| 附魂激荡 | `soul_surge` | 被动 | 强力附魂期间每 3 秒自动释放一次灵魂激荡 | 仪式 1（需先学灵魂激荡） |
| 引魂激荡 | `lure_surge` | 主动 | 弹幕命中引发半径 3 爆炸 | 仪式 1（需先学灵魂激荡） |
| 强力激荡 | `empowered_surge` | 主动 | 与灵魂激荡同机制，半径 8、伤害 12 | 仪式 1（需先学灵魂激荡） |
| 灵魂引发 | `spirit_trigger` | 主动 | 激荡命中后从目标位置射 6 道连锁弹幕 | 仪式 1（需先学灵魂激荡） |
| 灵魂透支 | `soul_drain` | 附魂 | 8 秒不耗魂，结束后 15 秒无法使用主武器 | 仪式 1 |
| 星魂逆转 | `star_reversal` | 被动 | 魔力/灵魂转化不再清空双方（转化入口待实现） | 仪式 2 |
| 星魂联结 | `star_link` | 被动 | 灵魂伤害共用 50% 魔法伤害加成 | 仪式 2 |
| 代行收割 | `soul_harvest` | 被动 | 召唤物命中 5% 概率回 1 魂 | 仪式 2 |
| 灵魂枪手 | `soul_gunner` | 主动 | 灵魂 1:1 换子弹（上限 10），命中 20% 回魂 | 仪式 3 |
| 噬魂提气 | `soul_devour` | 被动 | 召唤物攻击耗 1 魂，该次伤害 +50% | 仪式 3 |
| 诅咒抵抗 | `curse_resistance` | 被动 | 生命代偿伤害 -60% | 仪式 4 |
| 分流管控 | `flow_control` | 被动 | 技能伤害 ×2，冷却 ×1.5 | 仪式 4 |
| 术式轮转 | `spell_rotation` | 被动 | 施法后自动切到下一个可主动释放的技能 | 仪式 4 |
| 生命归还 | `life_return` | 附魂 | 记录期间失去的生命，结束按 70% 转灵魂 | 仪式 4 |
| 魂引天星 | `star_call` | 附魂 | 持续 8 秒，每 0.5 秒 1 魂换一颗坠星，20% 回魂 | 手持【星魄】 |
| 迷魂孢雾 | `confuse_spores` | 主动 | 8 枚小型孢子弹幕，从上方飘落 | 手持【松露灯】 |
| 自然法则 | `law_of_nature` | 被动 | 20% 概率不消耗灵魂 | 首次获得【生机灿烂】 |
| 自然之惩戒 | `natures_wrath` | 主动 | 5 枚扇形毒刺，命中中毒 | 手持【生机灿烂】 |
| 灭却业火 | `karma_flame` | 主动 | 消耗全部灵魂，处决生命低于消耗量的目标 | 手持【无分昼夜】 |
| 亵渎之魂 | `profane_soul` | 主动 | 从地面钻出火球，命中半径 2.5 爆炸 | 手持【无分昼夜】 |
| 掠夺之魂 | `soul_plunder` | 附魂 | 近战/远程 +25%，15 秒 | 手持【劫掠之火】 |
| 血液沸腾 | `boiling_blood` | 附魂 | 耗 80% 灵魂，下一次近战 ×5 后移除增益 | 技能书【劫掠之道】 |
| 天穹倒转 | `firmament_reversed` | 附魂 | 10 秒内摔落伤害改为等量回复 | 技能书【天穹倒转】 |
| 求生欲 | `survival_instinct` | 主动 | 血量 <40 且耐药性时，耗 10 魂把时长减半 | 手持【残破微光】 |

七个新英文 ID（原先只有中文名的技能）：`soul_harvest` 代行收割、`soul_gunner` 灵魂枪手、`soul_devour` 噬魂提气、`curse_resistance` 诅咒抵抗、`flow_control` 分流管控、`spell_rotation` 术式轮转、`life_return` 生命归还。

### 17.2 弹幕与外观复用

| 技能族 | 实现 | 外观来源 |
| --- | --- | --- |
| 引魂 / 强力引魂 / 附魂印记 / 引魂激荡 / 灵魂枪手 / 灵魂引发连锁 | `SoulLureProjectile`（新增可配置构造：特效、速度、重力、射程、命中半径、范围伤害） | 粒子风暴 `confluence:soul_projectile` |
| 迷魂孢雾 | 同上（重力 + 上方生成） | 粒子风暴 `confluence:spore_cloud` |
| 魂引天星 | 同上（向下发射） | 粒子风暴 `confluence:falling_star` |
| 亵渎之魂 | 同上（从地面向上发射 + 命中爆炸） | 粒子风暴 `confluence:ball_of_fire`（火焰花同款） |
| 自然之惩戒 | 新实体 `SoulStingProjectile` | **复用黄蜂毒刺模型**（同一份 `createBodyLayer` 与 `stinger.png`） |
| 灵魂激荡系列 | `SpiritSurgeEntity`（半径/时长/连锁参数化） | 原版灵魂粒子环 |

### 17.3 附魂标签与同名药水效果

- 新标签：`confluence:soul_attach`（附魂，10 员）与 `confluence:passive`（被动，9 员），生成源 `ModSoulSkillTagsProvider`。
- 新效果（与技能同名、图标同为技能图标）：`blood_rage`、`soul_plunder`、`soul_drain`、`soul_drain_lock`、`star_call`、`life_return`、`firmament_reversed`、`boiling_blood`。
- 属性的部分写在效果里（`addAttributeModifier`），需要跨刻数据的存在 `SoulStorage`（`surgeBlastCharge` / `lifeReturnLost` / `soulDrainActive` / `lifeReturnActive`）。

### 17.4 仪式系统

| 项 | 实现 |
| --- | --- |
| 数据 | `SoulStorage.ritualCount`（NBT + 同步），`getMaxSoul()` 追加 `ritualCount × 30` |
| 上限 | `SoulSkillParameters.SOUL_PER_RITUAL = 30`、`MAX_RITUAL_COUNT = 6` |
| 指令 | `/confluence soulSkill ritual <0..6>`，降低次数会同步压低灵魂上限并收拢当前值 |
| 自动解锁 | `PlayerUtils.RITUAL_UNLOCKS` 表：`(仪式次数, 前置技能, 目标技能)`，每次变化后重跑 |
| 文案 | 提升时动作条 `message.confluence.soul_ritual.activate`（「你唤醒了世界上的古老之魂...」），解锁时 `message.confluence.soul_ritual.unlock` |
| 触发入口 | **本轮未接**（按需求只做数据层与指令），后续挂方块或 Boss 首杀时调 `PlayerUtils.setSoulRitualCount` 即可 |

### 17.5 图标

- 8 个新技能的图标暂用问号占位，文件名仍是技能 ID（`hud/soulcerer_skill/<id>.png`），美术直接覆盖同名文件即可：`firmament_reversed`、`soul_harvest`、`soul_gunner`、`soul_devour`、`curse_resistance`、`flow_control`、`spell_rotation`、`life_return`。
- 8 个药水效果图标由技能图标复制（`textures/mob_effect/<id>.png`），换成正式图时两处同步。

### 17.6 本轮仍缺的实现

1. `star_reversal`：`convertManaToSoul` / `convertSoulToMana` 仍是空实现（「兑换比例尚未定义」），被动已注册但暂时没有可作用的对象。
2. `star_link` 只实现灵魂侧（共用 50% 魔法加成）；魔法伤害侧需要 MagicLib 的伤害分支，未接线。
3. 仪式触发入口未接（见 17.4）。
4. 技能书映射调整：书 `soul_surge`（名称「灵魂激荡」）现在解锁 `spirit_surge`，收割者前置改为 `spirit_surge`，与技能表一致。
5. 全部技能仍未做游戏内实测。
### 17.7 创造模式豁免

创造模式（以及任何带无限材料的玩家）使用灵魂技能**不消耗灵魂**：

- 判定统一放在 `PlayerUtils.prepareSoulConsumption`：事件 `SoulConsumeEvent` 仍照常发出，之后若 `player.isCreative() || player.hasInfiniteMaterials()` 直接返回 0（既不扣魂，也不结算生命代偿）。
- 技能、法器、套装被动里“把全部/部分灵魂当消耗”的技能（灭却业火、灵魂枪手、血液沸腾、魂引天星逐颗扣魂）都走这一个入口，因此一次性全部豁免。
- 灭却业火 / 灵魂枪手 / 血液沸腾的 `canUse` 额外放行创造模式，避免灵魂为 0 时连施法都被拦。
- 冷却与施法流程不变：创造模式仍然要走冷却与前端判定，只是不扣资源。
### 17.8 附魂统一时长与强力附魂数值调整

| 项 | 调整 |
| --- | --- |
| 附魂系列增益时长 | 全部统一为 **15 秒**（`SOUL_BUFF_DURATION_TICKS`） |
| 附魂系列冷却 | 全部统一为 **15 秒**（`SOUL_BUFF_COOLDOWN_TICKS`） |
| 强力附魂效果 | 由「灵魂伤害 ×5（+400%）」改为 **灵魂伤害 +12%**（`ENHANCED_SOUL_DAMAGE_BONUS = 0.12F`，`ADD_MULTIPLIED_TOTAL`） |

受影响的技能（时长与冷却现在都指向同一对常量，后续调整只改这两行）：

| 技能 | 旧时长 / 旧冷却 | 新时长 / 新冷却 |
| --- | --- | --- |
| 强力附魂 `enhanced_soul` | 5s / 1s | 15s / 15s |
| 附魂印记 `soul_mark`（附带强力附魂） | 5s / 2s | 15s / 15s |
| 血怒 `blood_rage` | 20s / 30s | 15s / 15s |
| 掠夺之魂 `soul_plunder` | 15s / 20s | 15s / 15s |
| 灵魂透支 `soul_drain` | 8s / 60s | 15s / 15s |
| 魂引天星 `star_call` | 8s / 30s | 15s / 15s |
| 血液沸腾 `boiling_blood` | 10s / 20s | 15s / 15s |
| 生命归还 `life_return` | 10s / 40s | 15s / 15s |
| 天穹倒转 `firmament_reversed` | 10s / 30s | 15s / 15s |

附魂激荡 `soul_surge` 是被动自动触发，没有自身增益与冷却，不受影响。

文案同步：技能说明改为「灵魂伤害提高12%，持续15秒。」，并新增效果说明 `tooltip.effect.confluence.enhanced_soul.0`（「灵魂伤害提高12%」）。
### 17.9 仪式解锁技能栏位

第 1 / 3 / 5 次仪式各解锁一个技能栏位，栏位数 3 → 4 → 5 → 6：

- 派生规则：`PlayerUtils.getRitualSlotCount(ritualCount)`（阈值表 `RITUAL_SLOT_UNLOCKS = {1, 3, 5}`，上限 `SoulSkillStorage.MAX_EQUIPPED_SOUL_SKILL_COUNT = 6`）。
- 生效时机：`PlayerUtils.setSoulRitualCount` 里每次仪式次数变化都重算一次栏位；次数提高且超过 3 时动作条提示「古老之力拓宽了你的灵魂栏位：%s」。
- 次数的提高与回退是对称的：`/confluence soulSkill ritual 0` 会把栏位退回 3（多出的栏位内容被裁掉，原逻辑与 `slots` 指令共用 `setSoulSkillSlotCount`）。
- `/confluence soulSkill slots <3..6>` 仍然保留，作为手动覆盖；下一次仪式次数变化会按仪式进度重算。
