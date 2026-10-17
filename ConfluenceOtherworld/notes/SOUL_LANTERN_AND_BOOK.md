# 魂灯与技能书变更记录

更新日期：2026-10-17。适用模块：`ConfluenceOtherworld`；Minecraft 1.21.1、NeoForge 21.1.235。

本轮**只改 1.21.1 NeoForge**，`confluence-1.20.1` 保持原样。`compileJava` 与 `runData` 均为 BUILD SUCCESSFUL，**未做游戏实测**。

## 0. 摘要

三块内容，全部挂在既有魂师框架上，没有新造并行体系：

| 模块 | 新增 | 复用/扩展的既有入口 |
| --- | --- | --- |
| 魂灯 | `SoulLanternItem`、8 件物品、5 条配方 | `SoulWeaponItem`、`PlayerUtils.refreshSoulSkills`、`SoulSkillDamageEvent` |
| 技能书 | `SoulBooks` 定义表、6 种书皮、`go_over` 效果 | `ModDataComponentTypes.SOUL_SKILL`、`learnSoulSkill`、`ItemProperties` 模型覆盖 |
| 配套 | `firmament_reversed` 技能、`soul_lure` 标签、被动记录 | `ModSoulSkills`、`ModTags.SoulSkills`、`SoulSkillStorage` |

---

## 1. 魂灯

### 1.1 新增类：`SoulLanternItem`

路径 `common/item/soulcerer/SoulLanternItem.java`，继承 `SoulWeaponItem`，只多做一件事：**把基础暴击率写进主手属性修饰符**。

```java
public class SoulLanternItem extends SoulWeaponItem {
    private static final ResourceLocation BASE_CRITICAL_CHANCE_ID = Confluence.asResource("base_critical_chance");
    protected final float criticalChance;

    public SoulLanternItem(Item.Properties properties, ModRarity rarity, float damage, float criticalChance, List<ResourceLocation> grantedSoulSkills) {
        super(properties, rarity, damage, grantedSoulSkills);
        this.criticalChance = criticalChance;
        addAttributeModifiers(builder -> builder.add(
                LibAttributes.getCriticalChance(),
                new AttributeModifier(BASE_CRITICAL_CHANCE_ID, criticalChance, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
        ));
    }
}
```

要点：

- 暴击走 `CRITICAL_CHANCE` 属性的 `ADD_VALUE`，与既有无前缀武器（`ModItems.attributes()` 的固定 4%）同一口径，因此 `WeaponTooltip` 会自动显示"暴击率 +4%/+8%"。
- `addAttributeModifiers` 是 `CustomRarityItem` 提供的入口，`getDefaultAttributeModifiers(ItemStack)` 在物品没有 `ATTRIBUTE_MODIFIERS` 组件时生效（NeoForge `IItemStackExtension#getAttributeModifiers` 的回落路径），钓鱼竿系列用的是同一条路。
- 伤害仍是**灵魂伤害**（`SoulWeaponItem.getSoulDamage`），不是 `ATTACK_DAMAGE`；实际输出由魂师技能结算。
- 右击释放当前灵魂技能、手持授予技能，全部继承父类，无需重写。

### 1.2 属性表

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

注册写在 `common/init/item/SoulItems.java`，形如：

```java
public static final DeferredItem<SoulLanternItem> PILLAGING_FLAME = ITEMS.register("pillaging_flame",
        () -> new SoulLanternItem(new Item.Properties().stacksTo(1), ModRarity.BLUE, 7.0F, 0.04F,
                List.of(ModSoulSkills.SOUL_PLUNDER.get().getId())));
```

技能 ID 一律用 `ModSoulSkills.X.get().getId()` 取，**不写字符串**；`ModSoulSkills` 是缓存原型注册，静态期 `.get()` 安全。

### 1.3 创造栏、模型、语言

- 创造栏：`ModTabs.SOUL` 的 `soulcerer_main_weapons` 组按伤害顺序追加 8 件。
- 模型：`ModItemModelProvider` 里逐条 `handheldTextureAlias(item, Confluence.asResource("item/soul_lantern/<id>"))`，产出 `item/handheld` 父模型 + `item/soul_lantern/<id>` 贴图。
- 语言：`SoulLanguageSubProvider` 用 `language.add(SoulItems.X.get(), english, chinese)`，键与 ID 自动对齐。

### 1.4 配方

配方全部由 datagen 产出（`src/generated` 被 `.gitignore` 忽略，属构建产物）。

| 物品 | 台子 | 类型 | 材料 |
| --- | --- | --- | --- |
| 领魂之灯 | 工作台 | 无序 | 灯笼 + 堕魂星核 |
| 劫掠之火 | 工作台 | 无序 | 绿宝石 + 翡翠 + 不祥之瓶 + 领魂之灯 |
| 颂魂指引 | 重型工作台 | 有序 | 见下 |
| 松露灯 | — | — | 暂无获取，仅注册 |
| 生机灿烂 | 重型工作台 | 有序 | 见下 |
| 星魄 | 工作台 | 无序 | 星怒 + 颂魂指引 |
| 无分昼夜 | 地狱熔炉 | 无序·无燃料 | 下界合金锭 + 狱石锭×10 + 颂魂指引 + 恶魂之泪×2 |
| 带路的老前辈 | 祭坛 | 无序 | 地牢魔骨×20 + 地牢指南针×1 + 蜘蛛网×5 |

重型工作台图形（`AmountIngredient` 的 count 是**每格**消耗，总消耗 = count × 该字符出现次数）：

```
颂魂指引：b=金锭(每格2)  c=红玉(每格4)  a=领魂之灯
b b b
c a c
  b
→ 金锭 8、红玉 8、领魂之灯 1

生机灿烂：b=毒刺(每格3)  c=大自然的恩赐(1)  d=丛林孢子(每格3)  a=颂魂指引
d c d
b a b
  d
→ 毒刺 6、丛林孢子 9、大自然恩赐 1、颂魂指引 1
```

- 大自然的恩赐取 `AccessoryItems.NATURES_GIFT`（`confluence:natures_gift`）；方块版 `NatureBlocks.NATURES_GIFT` 是 `registerWithoutItem`，没有物品形态，不能当原料。
- 地狱熔炉沿用 `hellforge(..., 0.5F, 200, false, ...)`，`requiresFuel=false` 即"不需要燃料"。
- 祭坛 `AltarRecipe` 上限 5 个原料，本配方 3 个。

对应文件：`CraftingRecipeProvider`（3 条无序）、`HeavyWorkBenchProvider`（2 条有序）、`ModRecipeProvider`（地狱熔炉 + 祭坛各 1 条）。

---

## 2. 魂灯带来的技能解锁

### 2.1 手持临时授予

完全复用既有链路：`SoulWeaponItem#getGrantedSoulSkills` → `PlayerUtils.refreshSoulSkills` 读主手 → 并入 `temporarySkills` / `availableSkills` → `SoulSkillSyncPacketS2C` 下发。

因此放进 `SoulLanternItem` 构造参数的技能 ID 列表 = 手持临时可用；**放下即失效，但已装备槽位与冷却保留**（符合 `SOUL_DEVELOPMENT.md` 的"来源失效保留装备与冷却"）。

### 2.2 首次获得永久解锁（生机灿烂 → 自然法则）

用映射表而非 if 分支，写在 `SoulItems`：

```java
/// 首次获得即永久学会的技能：物品 → 技能 ID。新增首得解锁只在此登记。
public static final Map<DeferredItem<? extends Item>, List<ResourceLocation>> FIRST_OBTAIN_SOUL_SKILLS = Map.of(
        LIFE_SPLENDOR, List.of(ModSoulSkills.LAW_OF_NATURE.get().getId())
);
```

判定入口 `PlayerUtils.learnSoulSkillsOnFirstObtain(ServerPlayer)`，由 `TickEvents.playerTick$Post` 在 `tickSoulSkills` 之前调用：

1. 整表遍历，技能全学会则跳过；
2. 主背包或副手持有该物品（`hasItemInInventory`）；
3. 逐个 `learnSoulSkill`，返回 true 时发动作条提示 `message.confluence.soulcerer_skill.first_obtain`。

键必须是 `DeferredItem` 而不是 `Item`——`DeferredItem.get()` 在注册事件前会抛异常，不能放进静态 Map。

### 2.3 新增标签「引魂」

- `ModTags.SoulSkills.SOUL_LURE` → 标签 ID `confluence:soul_lure`（与技能 `soul_lure` 同名，但属不同注册表，不冲突）。
- 成员 7 个：`confuse_spores`、`profane_soul`、`natures_wrath`、`star_call`、`lure_surge`、`enhanced_lure`、`soul_lure`。
- 生成源 `ModSoulSkillTagsProvider` 里显式 `add(key(...))`，与按分类自动生成的 `soul` / `blood_nature` / `star_lure` 三个标签互不影响；后续增删只改这里。

---

## 3. 技能书

### 3.1 数据模型：一个物品 + NBT 决定外观与效果

- 物品仍是 `SoulItems.SOUL_SKILL_BOOK`（`confluence:soulcerer_skill_book`）。
- 组件 `confluence:soulcerer_skill`（`ModDataComponentTypes.SOUL_SKILL`）语义升级为**书籍 ID**（旧的两本书 ID 与技能 ID 同名，天然兼容）。
- 新增 `SoulBooks`（`common/item/soulcerer/SoulBooks.java`）：`LinkedHashMap<书籍 ID, Definition>`，登记顺序同时决定模型覆盖序号与创造栏顺序。

```java
public record Definition(ResourceLocation id, ModRarity rarity,
                         @Nullable ResourceLocation skill,      // 永久解锁的技能，null = 纯被动
                         @Nullable ResourceLocation required) {} // 使用前置
```

对外接口：`get(id)`、`all()`、`styled()`、`rarity(id)`、`styleIndex(id)`、`styleIndex(stack)`、`of(stack)`、`nameKey(id)`。

### 3.2 外观：模型覆盖 + 自定义 item property

1. 客户端注册属性（`ModClientSetups.registerItemProperties`）：

```java
ItemProperties.register(SoulItems.SOUL_SKILL_BOOK.get(), Confluence.asResource("soul_book"),
        (stack, level, entity, seed) -> SoulBooks.styleIndex(stack));
```

2. datagen 生成基础模型 + 覆盖表（`ModItemModelProvider`）：

```java
ItemModelBuilder skillBookModel = withExistingParent("soulcerer_skill_book", "item/generated")
        .texture("layer0", ResourceLocation.withDefaultNamespace("item/book"));
for (SoulBooks.Definition book : SoulBooks.styled()) {
    String path = "soulcerer_skill_book/" + book.id().getPath();
    ModelFile styledBook = withExistingParent(path, "item/generated")
            .texture("layer0", Confluence.asResource("item/skill_book/" + book.id().getPath()));
    skillBookModel.override().predicate(Confluence.asResource("soul_book"), SoulBooks.styleIndex(book.id())).model(styledBook).end();
}
```

3. 产物：`models/item/soulcerer_skill_book.json`（含 6 条 `overrides`，序号 1~6）＋ `models/soulcerer_skill_book/<id>.json`（各指向 `item/skill_book/<id>` 贴图）。序号 0 没有匹配项，自动回落到基础书皮（原版书）。

### 3.3 使用逻辑

`SoulSkillBookItem.use`（服务端）：

1. 读组件 → `SoulBooks.of(stack)`，未登记直接失败；
2. 前置技能未永久学会（`isSoulSkillLearned`）→ 失败，**无惩罚、无消耗**；
3. 效果已生效（技能已学 / 被动已登记）→ 给予「复习」+ 提示；
4. 否则 → 首次生效：技能走 `learnSoulSkill`，被动走 `SoulSkillStorage#learnSoulBook`，并发提示；
5. **书籍不消耗**，只记 `Stats.ITEM_USED`——一本书可以反复读。

`getName(ItemStack)` 覆写为书籍名称键 `confluence.soul_book.<id>.name`，未绑定组件时回落到"灵魂技能书"。

tooltip 显示：技能名（或引渡者的被动说明）、使用前置（`需要先永久学会%s`）。

### 3.4 稀有度

- 写在物品栈的 `confluence_magic_lib:mod_rarity` 组件上；`ModRarity.getRarity(stack)` 读栈组件，所以同一物品可按 NBT 变色。
- 三条产出路径都带：`SoulItems.soulBook(id)`（创造栏、交易）、战利品 `SetComponentsFunction`（掉落）。
- 战利品里是两条 `set_components`（书籍 ID + 稀有度），因为 `SetComponentsFunction` 一次只能设一个组件，链式 `.apply()` 两次即可。
- `ModRarity` 的 `equals/hashCode` 基于 `name + color`，JSON 反序列化出来的实例与常量等价。

### 3.5 书籍总表

| 书 ID | 名称 | 稀有度 | 永久解锁 | 使用前置 | 获取 |
| --- | --- | --- | --- | --- | --- |
| `psychopomp` | 引渡者 | BLUE | 被动：引魂标签技能伤害 +1 | — | 鬼魂 1% |
| `raiders_path` | 劫掠之道 | BLUE | `boiling_blood` 血液沸腾 | — | 暂无 |
| `soul_surge` | 灵魂激荡 | GREEN | `soul_surge` | — | 克苏鲁之脑 / 世界吞噬怪 宝藏袋 100% 池 |
| `harvester` | 收割者 | ORANGE | `surge_blast` 灵魂激荡冲击 | `soul_surge` | 暂无 |
| `firmament_reversed` | 天穹倒转 | GREEN | `firmament_reversed`（新注册，原型） | — | 空岛村宝箱 33%；天师 3 级 1 绿宝石币 |
| `blood_world_wrath` | 血与世界之怒 | ORANGE | `blood_rage` 血怒 | — | 血肉墙 / 血肉山 宝藏袋 100% 池 |
| `survival_instinct` | 求生欲 | WHITE | `survival_instinct` | — | 既有（通用书皮） |

创造栏顺序＝`SoulBooks.all()` 登记顺序：未绑定书 → 6 本特殊书 → 求生欲。

> 批注（第二批）：原来由 `plain(...)` 登记的通用书皮「灵魂激荡」书（`spirit_surge`）已删除，连同它专用的 5% 独立宝箱书池一并移除；`spirit_surge` 技能本身保留。详见第 12 节。

---

## 4. 「复习」效果

- 注册：`ModEffects.GO_OVER = registerEffectWithEnglishName("go_over", "复习", "Go Over", GoOverEffect::new)`。
- 实现在 `common/effect/soulcerer/GoOverEffect.java`：

```java
super(MobEffectCategory.BENEFICIAL, 0xC8A165);
addAttributeModifier(LibAttributes.getSoulDamage(), Confluence.asResource("go_over"),
        SoulSkillParameters.GO_OVER_SOUL_DAMAGE_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
```

- 数值集中在 `SoulSkillParameters`：`SOUL_BOOK_REVIEW_DURATION_TICKS = 10 * 60 * 20`、`GO_OVER_SOUL_DAMAGE_BONUS = 0.05F`。
- 叠加规则：复用原版 `addEffect`，同效果取"最长时长 + 最高等级"，即**刷新为 10 分钟、不叠层**。
- 语言：`effect.confluence.go_over`（名称）、`tooltip.effect.confluence.go_over.0`（说明）由 datagen 生成。

---

## 5. 「引渡者」被动

不是技能，而是**书籍被动**，记录在附件里：

- `SoulSkillStorage` 新增 `Set<ResourceLocation> learnedSoulBooks`，NBT 键 `learnedSoulBooks`，随 `copyOnDeath` 附件存档。
- 接口：`getLearnedSoulBooks()`、`learnSoulBook(id)`；`PlayerUtils.isSoulBookLearned(player, id)`。
- 伤害入口（`PlayerUtils.getSoulSkillDamage`）：

```java
float amount = Math.max(0.0F, event.getAmount());
// 引渡者：引魂标签内的技能基础伤害固定 +1，不看技能分类
if (isSoulBookLearned(player, SoulBooks.PSYCHOPOMP) && stack.getSoulSkill().is(ModTags.SoulSkills.SOUL_LURE)) {
    amount += SoulSkillParameters.PSYCHOPOMP_SOUL_LURE_DAMAGE_BONUS;
}
return amount;
```

即：先让 `SoulSkillDamageEvent` 结算技能+武器总伤害，再追加固定 +1，最后才乘灵魂伤害属性倍率（在 MagicLib 的伤害分支里）。

---

## 6. 新技能 `firmament_reversed`

`ModSoulSkills` 增加一个原型条目（无 `use` 实现、`basicDamage = 0`）：

```java
public static final Supplier<SoulSkill> FIRMAMENT_REVERSED = register("firmament_reversed", 0, SkillCategory.STAR_LURE);
```

名称键 `confluence.soulcerer_skill.firmament_reversed.name` 补在 `InterfaceLanguageSubProvider`（与其余 22 个技能名同处），否则技能书 tooltip 会显示原始键。

---

## 7. 获取途径

| 途径 | 实现位置 | 细节 |
| --- | --- | --- |
| 鬼魂 1% | `EntitySubProvider` | 为 `MonsterEntities.GHOST` 新建掉落表，1 个池 + `random_chance 0.01` |
| 克脑 / 世吞宝藏袋 | `GiftSubProvider` | 克脑 3 张表各 `.withPool(soulBookLoot(SOUL_SURGE))`；世吞在 `eaterOfWorldsTreasureBagCommon()` 内加，覆盖 3 张表 |
| 血肉墙 / 血肉山宝藏袋 | `GiftSubProvider` | 在 `wallOfFleshTreasureBagCommon()` 内加，覆盖 6 张表 |
| 空岛村宝箱 33% | `ChestSubProvider` | `chests/sky_village_chests` 追加独立池 + `random_chance 0.33` |
| 天师 3 级交易 | `ModVillagers.villagerTrades` | `SKY_MILLER` 的 `tier3`：1 绿宝石币（`ModItems.EMERALD_COIN`）→ 天穹倒转书，`maxUses 12 / xp 10 / 0.05` |

"100% 抽取池"＝不带概率条件的独立 `LootPool`（`rolls = 1`，必掉）。原有的 5% 独立池（绑定 `spirit_surge` 的旧技能书）保持不动，两者并存。

---

## 8. 后续扩展怎么做

### 8.1 新增一件魂灯

1. 贴图放 `assets/confluence/textures/item/soul_lantern/<id>.png`（动画另加 `.mcmeta`）。
2. `SoulItems` 注册：`new SoulLanternItem(props.stacksTo(1), 稀有度, 伤害, 暴击率[, 手持技能 ID 列表])`。
3. `ModTabs.SOUL` 的主武器组加一行。
4. `ModItemModelProvider` 加一条 `handheldTextureAlias`。
5. `SoulLanguageSubProvider` 加中英文名。
6. 需要配方就往三个 Provider 里加对应台子的一行；需要首得解锁就往 `FIRST_OBTAIN_SOUL_SKILLS` 加一条映射。

### 8.2 新增一本技能书

1. 贴图放 `assets/confluence/textures/item/skill_book/<id>.png`。
2. `SoulBooks` 静态块加一行 `styled(Confluence.asResource("<id>"), 稀有度, 技能ID或null, 前置ID或null)`——**外观、覆盖序号、创造栏、掉落绑定全部自动跟随**。
3. `SoulLanguageSubProvider` 加 `confluence.soul_book.<id>.name`。
4. 只有"纯被动"书才需要额外代码（参考引渡者：在 `SoulSkillStorage` 记录 + 在 `PlayerUtils` 里查这个记录）。
5. 掉落/交易用 `SoulItems.soulBook(SoulBooks.<常量>)` 或 `soulBookLoot(id)`。

### 8.3 调整数值

- 魂灯伤害/暴击/稀有度：`SoulItems` 对应行。
- 技能书稀有度：`SoulBooks` 静态块第二个参数。
- 复习时长与加成：`SoulSkillParameters.SOUL_BOOK_REVIEW_DURATION_TICKS` / `GO_OVER_SOUL_DAMAGE_BONUS`。
- 引渡者加成：`SoulSkillParameters.PSYCHOPOMP_SOUL_LURE_DAMAGE_BONUS`。
- 引魂标签成员：`ModSoulSkillTagsProvider`。

### 8.4 工程约定（本轮遵守的）

- 魂师通用操作一律静态放 `PlayerUtils`，不新建 Handler；
- `SoulSkillStack` 不允许 null，空栈用 `SoulSkillStack.Empty`；
- 客户端类只在 `client` 侧调用，`common` 不引用 `Minecraft`/HUD；
- 保留 CRLF、局部补丁、不整文件覆盖（新建文件除外）；
- 数据生成产物（`src/generated`）是构建产物，不入库。

---

## 9. 验证与生成物

| 步骤 | 结果 |
| --- | --- |
| `gradlew :ConfluenceOtherworld:compileJava` | BUILD SUCCESSFUL |
| `gradlew :ConfluenceOtherworld:runData` | BUILD SUCCESSFUL（沿用既有的 `minecraft:entities/slime` 校验警告，非本轮引入） |

本轮生成物（`src/generated/resources`）：

- `assets/confluence/models/item/<魂灯>.json` × 8（`item/handheld` 父模型）
- `assets/confluence/models/item/soulcerer_skill_book.json` + `models/soulcerer_skill_book/<id>.json` × 6
- `assets/confluence/lang/{en_us,zh_cn,en_ud}.json`（物品名、书名、提示、效果、技能名）
- `data/confluence/tags/confluence/soulcerer_skill/soul_lure.json`（引魂 7 员）
- `data/confluence/recipe/{soul_conduit,pillaging_flame,star_spirit}.json`、`recipe/heavy_work_bench/{soul_chant_guide,life_splendor}.json`、`recipe/hellforge/day_and_night_alike.json`、`recipe/altar/senior_pathfinder_pro.json`
- `data/confluence/loot_table/entities/ghost.json`、`treasure_bag/{brain_of_cthulhu,eater_of_worlds,wall_of_flesh,hill_of_flesh}/*.json`、`chests/sky_village_chests.json`
- 三条配方进度（`advancement/recipes/...`）

---

## 10. 已知占位与待确认

1. **`go_over` 图标是占位**：`textures/mob_effect/go_over.png` 由 `enhanced_soul.png` 原字节复制，待正式美术替换同名文件。
2. **收割者前置**取 `soul_surge`（按给出的 ID），解锁 `surge_blast`（按中文名"灵魂激荡冲击"）。若要改成前置 `spirit_surge`，改 `SoulBooks` 里 `HARVESTER` 那行第四个参数即可。
3. **中文名撞车**：`soul_surge` 技能官方中文是"附魂激荡"，但灵魂激荡这本书按需求命名为"灵魂激荡"，与 `spirit_surge`（官方中文"灵魂激荡"）同中文不同 ID。
4. **`firmament_reversed` 是原型**：只注册与解锁，没有 `use` 效果。
5. 未做游戏实测：施法、书皮切换、复习 buff 刷新、掉落概率、交易等级均为**逻辑层完成**。
6. `notes/SOUL_DEVELOPMENT.md`、`notes/SOUL_PLAN.md` 尚未同步本批内容。
7. 1.20.1 侧未做任何改动，后续同步时本文件可作为清单。

---

## 11. 文件索引

源码（相对 `ConfluenceOtherworld/src/main/java/org/confluence/mod`）：

新增：

- `common/effect/soulcerer/GoOverEffect.java`
- `common/item/soulcerer/SoulBooks.java`
- `common/item/soulcerer/SoulLanternItem.java`

修改：

- `client/event/ModClientSetups.java`（技能书 item property）
- `common/attachment/SoulSkillStorage.java`（books 记录）
- `common/event/game/TickEvents.java`（首得解锁调用）
- `common/init/ModEffects.java`（`go_over`）
- `common/init/ModSoulSkills.java`（`firmament_reversed`）
- `common/init/ModTags.java`（`SOUL_LURE` 标签键）
- `common/init/ModTabs.java`（主武器组、技能书组）
- `common/init/ModVillagers.java`（天师 3 级交易）
- `common/init/item/SoulItems.java`（8 魂灯、技能书堆叠构造、首得映射）
- `common/item/soulcerer/SoulSkillBookItem.java`（使用逻辑、名称、tooltip）
- `common/soulcerer/skill/SoulSkillParameters.java`（复习与引渡者数值）
- `util/PlayerUtils.java`（首得解锁、书籍被动、引渡者加成）
- `common/data/gen/ModItemModelProvider.java`
- `common/data/gen/language/{InterfaceLanguageSubProvider,SoulLanguageSubProvider,TooltipLanguageSubProvider}.java`
- `common/data/gen/loot/{ChestSubProvider,EntitySubProvider,GiftSubProvider}.java`
- `common/data/gen/recipe/{CraftingRecipeProvider,HeavyWorkBenchProvider,ModRecipeProvider}.java`
- `common/data/gen/tag/ModSoulSkillTagsProvider.java`

资源：

- `assets/confluence/textures/mob_effect/go_over.png`（占位）

既有资源（本轮未改，仅被引用）：`textures/item/soul_lantern/*.png`、`textures/item/skill_book/*.png`。

---

## 12. 第二批增补（同日）

### 12.1 名称与书皮调整

| 项 | 改动 |
| --- | --- |
| `soul_surge` 技能中文名 | 「附魂激荡」→「灵魂激荡」（`InterfaceLanguageSubProvider`），技能书 tooltip 因此显示为灵魂激荡 |
| 通用书皮「灵魂激荡」书 | 删除：`SoulBooks` 去掉 `plain(SPIRIT_SURGE, ...)`；`GiftSubProvider` 删除 `soulSkillBookLoot()` 及其 6 处用法（克脑 3 张 + 世吞 3 张），避免掉落一本查不到定义的死书 |
| `survival_instinct` 技能图标 | `ModSoulSkills` 的构造参数由复用的 `soul_lure` 改为自身 ID，启用新补的 `hud/soulcerer_skill/survival_instinct.png` |
| 收割者学成文案 | 新增 `SoulBooks.USE_MESSAGE_KEYS` 映射，`harvester` 使用 `message.confluence.soul_book.harvester`（「引魂系列技能的威力增强了！」），其余书仍是通用文案 |

### 12.2 副武器「灵魂之刃」`soul_knife`

- 新增 `common/item/soulcerer/SoulKnifeItem.java`：`extends SoulSecondaryWeaponItem implements IWeaponTooltip`（只继承排版工具，没有额外行为）。
- 属性由组件给出：`ModItems.createAttributes(ModTiers.UNBREAKABLE, 4.0F, -2.4F, ModItems.attributes(0, 0.4F))` → **总伤害 6、总攻速 1.6、基础暴击 4%、击退 +0.4**。伤害走 `LibAttributes.getAttackDamage()`，即**吃近战加成、不吃灵魂伤害加成**。
- 附条件增伤：`LivingEntityEvents.soulKnifeBonusDamage` 监听 `LivingIncomingDamageEvent`，主手持刃且当前灵魂 > 10 时 `+3`（在护甲等减伤之前，与"额外攻击力"同口径）。
- 额外收魂：`LivingEntityEvents.soulKnifeExtraSoul` 监听 `SoulCollectionEvent`，主手持刃时 `+1`（复用框架事件，Boss 本体直接回满的路径不受影响）。
- 数值集中在 `SoulSkillParameters`：`SOUL_KNIFE_SOUL_THRESHOLD = 10`、`SOUL_KNIFE_DAMAGE_BONUS = 3`、`SOUL_KNIFE_KILL_SOUL_BONUS = 1`。
- 创造栏挂在「副武器」组；模型 `handheldTextureAlias(..., item/sword/soul_knife)`。

### 12.3 灵魂条 HUD 图标行

`TerraStyleManaHud.OVERLAY` 原来对魔力与灵魂都用 `uvY = 10`；现在灵魂模式改用 `OVERLAY_SOUL_UV_Y = 60`，即 `hud/overlay.png` 图集最下面一行的图例，魔力保持 `OVERLAY_DEFAULT_UV_Y = 10`。

### 12.4 指令：重置灵魂技能装备

| 指令 | 作用 |
| --- | --- |
| `/confluence soulSkill reset` | 清空全部装备槽、当前索引归零、清空技能冷却，并重算可用技能；**不动**永久学习与技能书记录 |
| `/confluence soulSkill slots <count>` | 设置可装备槽数，参数被限制为 `3..6`；改完立即同步槽位、索引与技能数据 |

- 权限沿用 `/confluence` 根的 `hasPermission(2)`。
- 落地：`PlayerUtils.resetSoulSkillEquip` / `PlayerUtils.setSoulSkillSlotCount`，指令只做参数校验与消息。
- 文档：已写入 `assets/confluence/ageratum/zh_cn/detailed_information/command/command_list.md`（含格式、用途、举例）。

### 12.5 初始槽位

`SoulSkillStorage.DEFAULT_EQUIPPED_SOUL_SKILL_COUNT` 由 6 改为 **3**，新玩家与新建附件默认 3 个装备槽；上限仍通过指令或后续机制扩展到 6。

### 12.6 第二批新增/修改文件

新增：

- `common/item/soulcerer/SoulKnifeItem.java`

修改：

- `common/init/ModSoulSkills.java`（求生欲图标）
- `common/attachment/SoulSkillStorage.java`（默认槽位 3）
- `common/client` 侧：`client/gui/hud/TerraStyleManaHud.java`（灵魂条图标行）
- `common/item/soulcerer/{SoulBooks,SoulSkillBookItem}.java`（删除通用书、harvester 文案）
- `common/init/item/SoulItems.java`（灵魂之刃注册）
- `common/init/{ModTabs,ModCommands}.java`（副武器分组、两条指令）
- `common/event/game/entity/LivingEntityEvents.java`（+3 伤害、+1 收魂）
- `common/soulcerer/skill/SoulSkillParameters.java`（灵魂之刃数值）
- `common/data/gen/loot/GiftSubProvider.java`（删除旧 5% 书池）
- `common/data/gen/{ModItemModelProvider,SoulLanguageSubProvider,InterfaceLanguageSubProvider}.java`
- `assets/confluence/ageratum/zh_cn/detailed_information/command/command_list.md`（指令文档）

---

## 13. 第三批增补

| 项 | 改动 |
| --- | --- |
| 技能状态文案 | `confluence.soulcerer_skill.state.learned` 改为「已永久解锁 / Permanently unlocked」，全局技能 tooltip 统一使用该键 |
| 灵魂之刃归入短剑 | ① `ModItemTagsProvider` 把 `soul_knife` 加入 `confluence:short_sword` 标签；② `ModClientEvents` 为该物品注册 `ShortSwordInHandRenderer`，手持姿态与短剑一致 |
| 灵魂之刃贯穿 | 新增 `LivingEntityEvents.soulKnifeReach`（监听 `AttackEntityEvent`）：以眼位为起点、沿视线 `SOUL_KNIFE_REACH = 4.5` 格、半宽 **0.45** / 半高 0.45 的**窄扁矩形**判定，补打主目标之外的敌怪；需蓄力 ≥ `SOUL_KNIFE_MIN_CHARGE = 0.9`；伤害公式与原版一致，主目标仍由原版结算，不会双打 |
| 灵魂之刃配方 | 工作台无序：`confluence:fallen_soul_core` + （`confluence:iron_short_sword` 或 `confluence:lead_short_sword`） |
| 灵魂条染色 | `TerraStyleManaHud`：`SOUL_TINT_*` 改为 **#abfffe**（0.6706 / 1.0 / 0.9961），`SOUL / SOUL_LOW / SOUL_HIGH` 调色板改为中性亮度，最终显示为淡蓝色而非紫色（LEGACY 与 OVERLAY 两种样式共用同一套配色） |
| 引魂技能实装 | `SoulLureProjectile` 改为法杖式直线弹幕：飞行参数集中到 `SoulSkillParameters`（`SOUL_LURE_SPEED = 1.6`、`SOUL_LURE_MAX_DISTANCE = 40`、`SOUL_LURE_MAX_AGE_TICKS = 120`）；视觉改用**粒子风暴**的 `confluence:soul_projectile` 特效（`SoulLureProjectile.PARTICLE`），客户端按 `ParticleEmitter` + `attachEntity` 挂到弹幕上；右键主武器且当前技能为引魂时照旧由 `PlayerUtils.tryUseSoulSkill` 触发 |
| 灵魂弹幕贴图与收尾 | `soul_projectile.particle.json` 的 `texture` 由未加命名空间的 `soul_projectile` 修正为 `confluence:soul_projectile`（实际贴图 `textures/particle/soul_projectile.png`，16×112 = 7 帧）；收尾由火焰的 `flamelash_expiration*` 换成新增的 `confluence:soul_expiration`——克隆蓝玉法杖的 `base_mana_staff_projectile_expiration`（同一套 7 帧收尾贴图），把 tinting 固定为淡蓝 `[0.671, 1.0, 0.996, 0.8]`，并去掉原来的第二个球状收尾事件 |

调参入口一览：

- 引魂弹幕：`SoulSkillParameters.SOUL_LURE_SPEED / SOUL_LURE_MAX_DISTANCE / SOUL_LURE_MAX_AGE_TICKS`（伤害仍为 `ACTIVE_DAMAGE` + 主武器灵魂伤害）；粒子密度与爆散改 `assets/confluence/particle_definitions/soul_projectile.particle.json` 的 `emitter_rate_steady.spawn_rate` 等字段
- 灵魂之刃贯穿：`SOUL_KNIFE_REACH / SOUL_KNIFE_HALF_WIDTH / SOUL_KNIFE_HALF_HEIGHT / SOUL_KNIFE_MIN_CHARGE`
- 灵魂条配色：`TerraStyleManaHud.SOUL_TINT_*` 与 `SOUL*` 调色板

第三批修改文件：`common/soulcerer/skill/SoulSkillParameters.java`、`common/event/game/entity/LivingEntityEvents.java`、`common/entity/soulcerer/SoulLureProjectile.java`、`client/gui/hud/TerraStyleManaHud.java`、`client/event/ModClientEvents.java`、`common/data/gen/tag/ModItemTagsProvider.java`、`common/data/gen/language/SoulLanguageSubProvider.java`。
