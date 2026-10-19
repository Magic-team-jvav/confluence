# 灵魂弓与六套套装配方（1.21.1 ⇄ 1.20.1 双版本同步，本文为 1.20.1 侧副本）

本轮目标：新增两把魂师弓、一个通用灵魂材料「魂瓶」，并给六套既有魂师装甲补上合成方式。
**两棵树（`confluence` / `confluence-1.20.1`）改动语义完全一致**，仅 API 写法按各自版本落地。

## 0. 验证

| 侧 | compileJava | runData |
| --- | --- | --- |
| 1.21.1 NeoForge | `BUILD SUCCESSFUL` | `BUILD SUCCESSFUL` |
| 1.20.1 Forge | `BUILD SUCCESSFUL` | `BUILD SUCCESSFUL` |

产物（`src/generated`，构建产物不入库）已自动清理旧的 `heavy_work_bench/hermit_*` 与 `blue_hermit_*`。

## 1. 魂瓶（`soul_bottle`）

**定位**：装了灵魂的玻璃瓶，是本轮所有魂师配方的通用灵魂材料，同时也是一个可放置的灯笼方块。

**获取方式**：灵魂条状态下，手持本模组的玻璃瓶（`PotionItems.BOTTLE`，物品名「玻璃瓶」）
右键 → 消耗 **20 灵魂**，玻璃瓶变成魂瓶。

- 数值入口：`SoulSkillParameters.SOUL_BOTTLE_SOUL_COST = 20`。
- 实现入口：`SoulItems.canFillSoulBottle` / `SoulItems.fillSoulBottle`，由 `BottleItem.use` 优先调用
  （因此对着空气和对着方块右键都能用；灵魂不足 20 时行为不变，仍然是取水）。
- 消耗走 `PlayerUtils.consumeSoul`，即与技能消耗同一套规则：创造模式免费、灵魂透支 / 自然法则照常生效、
  蓝隐匿者套装的「累计消耗 10 灵魂」也会统计。
- 方块：`BaseSoulInABottleBlock`，亮度 7，与其余 8 种「XX之魂瓶」共用 `SOUL_BOTTLE_ENTITY`。
- 资源：贴图由「光明之魂瓶」换色而来（饱和洋红液相 → 淡蓝白，保留描边、瓶盖与高光），
  方块模型直接复用 `template_soul_bottle` / `template_soul_bottle_hanging`。
- 已接入：创造栏「灵魂瓶」页签、`mineable/pickaxe`、`confluence:needs_1_level`、`dropSelf` 战利品、
  物品价值 20（与其余魂瓶同档）。

## 2. 灵魂短弓 / 灵魂弓

| 物品 | 基础伤害 | 耐久 | 类 |
| --- | --- | --- | --- |
| 灵魂短弓 `soul_short_bow` | 5.5 | 1024 | `SoulShortBowItem extends ShortBowItem` |
| 灵魂弓 `soul_bow` | 4.0 | 1024 | `SoulBowItem extends BaseTerraBowItem` |

两条共用机制（标记接口 `ISoulBow`，事件在 `LivingEntityEvents`）：

1. **击杀额外收魂**：`soulBowExtraSoul` → `SoulCollectionEvent +1`
   （主手或副手持魂师弓时，`SOUL_BOW_KILL_SOUL_BONUS = 1`）。
2. **灵魂 ≥ 10 时增伤**：`soulBowBonusDamage` → 由这把弓射出的箭矢额外 **+3** 点伤害
   （`SOUL_BOW_SOUL_THRESHOLD = 10`、`SOUL_BOW_DAMAGE_BONUS = 3`，与祈灵之刃同档同阈值）。
   判定条件：伤害来源的直接实体是 `AbstractArrow` 且主人是该玩家，并且玩家手持魂师弓，
   因此不会出现"拿着弓空手打人还增伤"。

**配方**（工作台·有序，两版各一份）：`堕魂星核` 在上、`铁短弓/铅短弓`（或 `铁弓/铅弓`）在下。

```
a          a = 堕魂星核
b          b = 铁短弓 或 铅短弓（同一 ingredient）
```

- 配方 ID：`soul_short_bow_from_iron_or_lead_short_bow`、`soul_bow_from_iron_or_lead_bow`
  （一个配方同时接受铁/铅两种底弓，与祈灵之刃的写法一致）。
- 贴图由铁短弓/铁弓的灰阶映射到「深靛 → 淡青白」渐变得到，模型由 `ModItemModelProvider` 按
  `item/bow/<name>.png` + `_pulling_0/1/2` 自动生成。

## 3. 六套既有套装的合成方式

配方数值只按需求表实现，**逐件/套装数值一律未改动**。

### 3.1 灵魂头套 / 灵魂长袍（工作台·有序）

| 产物 | 图案 | 材料 |
| --- | --- | --- |
| 灵魂头套 | `bbb` / `bab` | a = 皮革帽子，b = 魂瓶（5 个） |
| 灵魂长袍 | `bcb` | b = 魂瓶（2 个），c = 长袍（`VanityArmorItems.ROBE`） |

### 3.2 幻翼盔甲（邪恶祭坛·无序）

| 产物 | 材料 |
| --- | --- |
| 幻翼头盔 | 幻翼膜 + 银头盔 |
| 幻翼胸甲 | 幻翼膜 + 银胸甲 |
| 幻翼护腿 | 幻翼膜 + 银护腿 |
| 幻翼靴子 | 幻翼膜 + 银靴子 |

幻翼膜用原版 `minecraft:phantom_membrane`。

### 3.3 白南瓜盔甲（重型工作台·有序）

| 产物 | 图案 | 材料 |
| --- | --- | --- |
| 白南瓜头盔 | `aba` / `aca` | a = 白南瓜，b = 灵魂头套，c = 雕刻白南瓜 |
| 白南瓜胸甲 | `aaa` / ` b ` | a = 白南瓜，b = 灵魂长袍 |
| 白南瓜护腿 | `aaa` / `a a` / `b b` | a = 白南瓜，b = 魂瓶 |
| 白南瓜靴子 | `a a` / `b b` | a = 白南瓜，b = 魂瓶 |

### 3.4 隐匿者盔甲（工作台·有序）

| 产物 | 图案 | 材料 |
| --- | --- | --- |
| 隐匿者头盔 | `aba` / `c c` | a = 黑橡木原木，b = 紫水晶，c = 绿宝石 |
| 隐匿者胸甲 | `a a` / `aba` / `cac` | 同上 |
| 隐匿者护腿 | `aaa` / `b b` / `a a` | a = 黑橡木原木，b = 紫水晶 |
| 隐匿者靴子 | `b b` / `a a` | 同上 |

### 3.5 蓝隐匿者盔甲（工作台·有序）

图案与隐匿者完全一致，只把 `a` 从黑橡木原木换成**发光蘑菇**（`MaterialItems.GLOWING_MUSHROOM`）。

### 3.6 逐灵盔甲（重型工作台·有序）

`a` = 地牢魔骨，`b` = 蛛网；数字是**每个格子的堆叠数**（沿用死灵套装的 `AmountIngredient` 写法）。

| 产物 | 图案 | a | b |
| --- | --- | --- | --- |
| 逐灵头盔 | `bab` / `a a` | 10 | 4 |
| 逐灵胸甲 | `a a` / `bab` / `bab` | 8 | 2 |
| 逐灵护腿 | `bab` / `b b` / `a a` | 10 | 2 |
| 逐灵靴子 | `a a` / `b b` | 10 | 4 |

## 4. 落地时的判断与偏离（需确认项）

1. **「紫水晶」按原版紫水晶碎片 `minecraft:amethyst_shard` 实现**（与「绿宝石 = 原版绿宝石」同级），
   没有用模组的「紫晶」`MaterialItems.AMETHYST`。若要改成紫晶，只需改四行 `Ingredient.of`。
2. **隐匿者 / 蓝隐匿者的旧配方已删除**：两版原先都有一份重型工作台的占位配方
   （几乎只用黑橡木原木，胸甲才带紫晶/翡翠），与本需求的新图案冲突且更便宜，
   因此改为「删除旧的、按新表生成工作台有序配方」，而不是并存两套。
3. **魂瓶是新建物品**，两版此前都不存在；名称、来源、贴图按本轮对话确定。
4. 逐灵表里的数字按"每格数量"理解（与既有死灵套装同写法），不是"整件总消耗"。
5. 白南瓜头/身的图案里 `a` 出现 4 次 / 3 次，即实际消耗 4 / 3 个白南瓜方块。
6. 本轮**没有改动任何逐件属性与套装奖励**（需求表里的数值仅作描述），
   也没有回到旧需求里"魔法伤害 vs 灵魂伤害"的历史口径上（整套仍统一用灵魂伤害，蓝隐匿者除外）。

## 5. 文件索引

### 1.21.1（`confluence/ConfluenceOtherworld`）

新增：

```
common/api/item/ISoulBow.java
common/item/bow/SoulShortBowItem.java
common/item/bow/SoulBowItem.java
resources: blockstates/soul_bottle.json
           models/block/soul_bottle.json, soul_bottle_hanging.json
           models/item/soul_bottle.json
           textures/block/soul_bottle/soul_bottle.png
           textures/block/soul_bottle_item.png (+ .mcmeta)
           textures/item/bow/soul_short_bow.png, _pulling_0
           textures/item/bow/soul_bow.png, _pulling_0/1/2
```

修改：

```
common/init/block/FunctionalBlocks.java        魂瓶方块 + 注册进 SOUL_BOTTLE_ENTITY
common/init/item/SoulItems.java                装魂入口 canFillSoulBottle / fillSoulBottle
common/item/potion/BottleItem.java             优先尝试装魂
common/init/item/BowItems.java                 两把魂师弓
common/soulcerer/skill/SoulSkillParameters.java 魂瓶与魂师弓的数值
common/event/game/entity/LivingEntityEvents.java soulBowExtraSoul / soulBowBonusDamage
common/init/ModTabs.java                       创造栏：灵魂瓶 + 短弓 + 弓
common/data/gen/recipe/CraftingRecipeProvider.java  6 条工作台配方（弓 2、灵魂 2、隐匿者 8）
common/data/gen/recipe/HeavyWorkBenchProvider.java  删除旧隐匿者配方，新增白南瓜 4 + 逐灵 4
common/data/gen/recipe/ModRecipeProvider.java       幻翼 4 条祭坛配方
common/data/gen/loot/BlockSubProvider.java     魂瓶 dropSelf
common/data/gen/tag/ModBlockTagsProvider.java  魂瓶进 pickaxe / needs_1_level
common/data/gen/data_map/ValueSubProvider.java 魂瓶价值 20
```

### 1.20.1（`confluence-1.20.1/ConfluenceOtherworld`）

与上表一一对应，差异只有版本写法：

- `RecipeOutput` → `Consumer<FinishedRecipe> writer`；`ShapedRecipePattern.of` → `PortShapedRecipePattern.of`；
- `DeferredBlock/DeferredItem` → `PortDeferredBlock/PortDeferredItem`；
- 事件用 `PortLivingIncomingDamageEvent`，并在 `LivingEntityEvents` 的静态注册段补
  `PortEventHandler.addListener(LivingEntityEvents::soulBowBonusDamage / ::soulBowExtraSoul)`。
- 资源文件（贴图/模型/blockstate）与 1.21.1 完全相同，已直接拷贝比对。

## 6. 仍未做

1. 两版都**没有进游戏实测**：装魂交互、两把弓的收魂与增伤、六套配方的实际可合成性与 JEI 展示。
2. 弓与魂瓶的贴图是本轮程序化换色产物，如需美术资源可原样替换同名 png。
3. `i18n/` 目录下的 `zh_cn.json` / `en_us.json` 是本仓库的历史导出快照，本轮没有回头同步；
   正式语言文件由 `runData` 生成。
