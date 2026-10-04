# TE 退役决策表（施工级）

> 生成方式：`tools/port2native/te_refs.py`（盘点/`--resolve`/`--file`）+ 本次会话对 1.20 真源的定点查证。
> 本文只写**判定**，不重复 `notes/TE-RETIREMENT-WORKSHEET.md` 的逐条明细。
>
> **口径**：以**使用行**计。本次实测快照：**105 文件 / 1198 条使用行 / 80 个 TE 类**
> （会话开始时为 1200 行 / 82 类——并行批次已经清掉了 `TEEnchantments`、`TEEnchantmentHelper` 两族，
> 见 `notes/WORK-QUEUE.md` §六）。1.20 全仓对 `org.confluence.terraentity` 的引用 = **0**，
> 所以每条引用的终点都是「1.20 同位置的原生写法」。
>
> ⚠️ **工作树正在被并行批次改动**（`WhipAttackEntity:114` 在我做分析的十几分钟内就从
> `TEEnchantmentHelper.getEnchantmentLevel(TEEnchantments.WHIP_SWEEP, weapon)` 变成了
> `EnchantmentUtils.getEnchantmentLevel(ModEnchantments.WHIP_SWEEP, weapon)`；
> `SummonItems.java` 已落地，`PetItems.java` 仍未落地）。**落盘前请重跑 `te_refs.py` + `te_repoint.py` dry-run**，
> 本文的成员级判定不受影响（判定依据是 1.20 权威名，不是当前计数）。
>
> 图例：`同名` = 目标注册表已有同名字段，纯改指；`改名 → X` = 1.20 权威名不同；
> `1.21 缺 → 需补` = 1.20 有、主模组 1.21 无，要补出来；`1.21 缺 → 应删` = 1.20 也没有，引用该消失；
> `改指` / `搬` / `删` / `待定` = §三 的 C 类判决。

---

## §一 施工顺序与依赖

### 1.1 分组实测（当前快照）

| 组 | 文件数 | 说明 |
|---|---:|---|
| 只引用 B 类（注册表） | **49** | 可整体机械改指 |
| 含 C 类（无同名对应物） | **56** | 需按 §三 逐类处理 |
| 合计 | **105** | B 类使用行 ≈1044，C 类 ≈155 |

B-only 文件（49，机械批）：`client/renderer/entity/bestiary/SlimeZombieRenderer`、`common/block/common/{BasePotBlock,TombstoneBlock}`、
`common/block/functional/crafting/AltarBlock`、`common/block/natural/{CorrodedWormRootsBlock,CorruptedOvariesBlock}`、
`common/data/gen/{ModChineseProvider,ModDataProvider,ModItemModelProvider}`、
`common/data/gen/data_map/{BlockBreakSpawnsSubProvider,BugNetEntityToItemSubProvider,GamePhase2AttributeModifiersSubProvider,ImmunitySubProvider,LivingInvulnerableEffectsSubProvider,TreasureBagSubProvider}`、
`common/data/gen/loot/{ChestSubProvider,EntitySubProvider}`、`common/data/gen/loot/modifiers/AddEntityLootConfluenceSubProvider`、
`common/data/gen/recipe/{CraftingRecipeProvider,HeavyWorkBenchProvider,ShimmerTransmutationRecipeProvider}`、
`common/data/gen/tag/{ModBlockTagsProvider,ModDamageTypeTagsProvider,ModEntityTypeTagsProvider,ModItemTagsProvider}`、
`common/data/saved/{KillBoard,MeteoriteTracker,SpaceSpawner}`、
`common/entity/projectile/{IceTofuBrickProjectile}`、`common/entity/projectile/boulder/GhoulderEntity`、
`common/entity/projectile/mana/{BallOfFireProjectile,WandOfFrostingProjectile}`、`common/entity/projectile/whip/WhipAttackEntity`、
`common/gameevent/{BloodMoonGameEvent,GoblinArmyGameEvent,SlimeRainGameEvent}`、`common/init/{ModDamageTypes,ModFluids,ModTabs}`、
`common/init/armor/ModArmorBonus`、`common/init/item/BaitItems`、`common/item/common/{ModBoneMealItem,SpikyBallItem,ThrowableDropSelfItem}`、
`common/item/mana/MagicDaggerItem`、`common/recipe/special/BoomBunnyRecipe`、
`integration/mrcrayfish/furniture/MrCrayfishFurnitureHelper`、`integration/sodium/dynamiclights/SodiumDynamicLightsHelper`、
`network/s2c/AvailableHouseSelectPacketS2C`。

### 1.2 子批（顺序即施工顺序，前面的不落地不要开后面的）

| # | 子批 | 文件 | 为什么这一步之后门禁仍绿 |
|---|---|---:|---|
| **SB-0** | 前置核对（不改代码） | — | 等 R3 的 `PetItems.java` 落地 → 重跑 `te_refs.py` + `te_repoint.py` dry-run；确认 §二 里 `需补/应删/待定` 的 17 个成员已分流完毕 |
| **SB-1** | B 类纯改指（49 B-only 文件 + mixed 文件里的 B 引用） | 49+ | 目标注册表成员等价存在（§二 除例外 17 条外全 `同名`）；`te_repoint.py --apply` 已实测 38 文件 / 452 处可机械落盘。例外 17 条随批单独处理（改名立即改，需补/应删的引用先摘掉或补注册） |
| **SB-2** | A 类同名类改指（23 类 / 36 条） | ~20 | 类在 1.21 已存在且同签名（`WallOfFlesh`、`BrainOfCthulhu`、`QueenBee`、`EaterOfWorlds`、`HillOfFlesh`、`DungeonGuardian`、`GoldenSlime`、`BaseSlime`、`LittleHornet`、`WoodenMimic`、`DemonEye`、`BaseWormPart`、`AnglerNPC`、`NPCMood`、`IPlayer`、`IVariant`、`ILeftClickStateItem`、`ITrackType`、`BasisTrack`、`SimpleTrack`、`GeoNormalRenderer`、`GeoNegativeVolumeRenderer`、`WallOfFleshRenderer`）→ 只换 import 的 FQN，编译立刻成立 |
| **SB-3** | 工具/数学改指（`TEUtils` 9 + `AimUtils` 1 + `renderDebugBlock` 1） | 11 | 目标在 1.20 就是 portlib：`org.confluence.lib.util.{LibEntityUtils,LibMathUtils,AimUtils}`、`LibRenderUtils.renderDebugBlock`（1.21 也在用 `LibUtils/LibMathUtils/LibRenderUtils`）→ 逐方法确认存在即可（§三 有方法级清单）；`TEUtils.internalSpawnEntity` 两处直接删（1.20 无此步） |
| **SB-4** | 客户端渲染类（`GeoNegativeVolumeRenderer`/`GeoWormRenderer`/`BaseEntityRenderer`/`AbstractBufferManager`/`SpitParticle`/`DebugBlocksHelper`/`DeathAnimOptions`/`WallOfFleshRenderer`） | 10 | 改指的用主模组同名类；`AbstractBufferManager`(110 行)、`SpitParticle` 按 1.20 文件**搬**（1.20 有同名实现，搬完即编译）；`DebugBlocksHelper`/`DeathAnimOptions` 三处调用在 1.20 是**注释掉的** → 删调用与 import |
| **SB-5** | 实体构造式与变体（`StatueBlocks`、`DecomposeTheSourceExtractBlock`、`JungleHiveBlock`、`CrimsonHeart/Larva/ShadowOrb`、`ModClientBestiaryEntryProvider`、`MeteorShowerGameEvent`、`PlayerEvents`、`api/event/CustomMimicSummonKeyEvent`） | 8 | 1.20 同位置都是 `MonsterEntities.X.get().create(level)` / `DemonEye.Variant.*` / `Worm.Variant.*`，1.21 主模组实体与枚举都已存在 → 编译成立 |
| **SB-6** | 交易 / NPC / 界面（`PlayerSpecialData`、`PrefixUtils`、`ClientConfigs`、`GameClientEvents`、`NPCReforgeScreen`、`ExtraInventory`、`HouseSelectHud`、`mixin/integration/touhoulittlemaid/EntityMaidMixin`） | 8 | **依赖用户对 §三 待定 10 条的表决**；`ITradeHolder`/`ITradeLock`/`NPCTradeManager`/`TradeParams`/`UpdateNPCTradePacket` 目前只有 TE 实现，删或改指决定这一批能不能编译 |
| **SB-7** | mixin 与 `TerraEntity.MODID`（3 个 model mixin + `RegistryDataLoaderMixin` + `ModUtils`） | 5 | 3 个 model mixin 一起改 `Set.of(Confluence.MODID, TerraFurniture.MODID)`；`RegistryDataLoaderMixin` 与 R4 的 1.20 版 `RegistryDataLoader$RegistryDataMixin` **互换**（见 1.3） |
| **SB-8** | 语言与数据（19 个 java 字符串面 + 28 个 resources + 5 个 lang） | 52 | 纯文本替换/重定向，不参与编译；但**必须与 SB-1/SB-5 的注册命名空间同批**（否则玩家侧掉名/掉进度） |
| **SB-9** | 摘除（`settings.gradle`、`.gitmodules` gitlink、`TerraEntity/`、`assets|data/terra_entity`） | 3 处配置 | 前八批把引用清零后才做；做完要 `gradlew compileJava` + 客户端启动冒烟，因为它动的是工程结构 |

### 1.3 必须「同一改动内」完成的事项（拆开就红）

| 事项 | 拆开的后果 |
|---|---|
| 删 `mixin/integration/touhoulittlemaid/EntityMaidMixin` ⇄ 删 `confluence.mixins.json:26` 的 `integration.touhoulittlemaid.EntityMaidMixin` | mixin 配置指向不存在的类 → **启动期崩**（不是静默） |
| 换 `mixin/resources/RegistryDataLoaderMixin` ⇄ 改 `confluence.mixins.json:32-33` 两行（`resources.RegistryDataLoaderMixin` / `...$RegistryDataMixin`） | 同上 |
| 3 个 model mixin 的 `TerraEntity.MODID` → `TerraFurniture.MODID` ⇄ `ModUtils.CONFLUENCE_NAMESPACES` | 三处语义相同（「哪些命名空间算自家」），只改一处会让模型加载豁免面与命名空间判定不一致 |
| 实体/物品 id 命名空间改写 ⇄ `Confluence.registerEntityAliases()` 增补 ⇄ `data/confluence/**` 里的 `terra_entity:` id ⇄ 5 个 lang 的键族 | 存档中已存在的 TE 实体 id 会解析失败（旧世界实体消失/刷怪静默失效） |
| `TEFigureBlocks.FIGURE*` / `TEItems.DEBUG_ITEM` / `TEItems.HOUSE_DETECTOR` / `TEEffects.SUMMON_FOCUS` / `TEMonsterEntities.HONEY_SLIME` / `TEPetItems.WALLET` 的引用 ⇄ 它们所在的 tag/配方/战利品/语言条目 | 只删引用会留下悬空 tag 条目/配方产物 |

### 1.4 纪律（沿用 `notes/WP6-WP7-PLAN.md` §四）

1. 替换只作用于**本批列出的文件**，绝不按目录扫；2. 注册层插入点在第一个方法声明之前；
3. import 块不重排；4. PowerShell 读中文一律假阴性——中文本地化核查用 Python（`encoding="utf-8"`）；
5. 注意 Windows 大小写改名污染 git 索引（`core.ignorecase=true`）；6. 每批出口：`build_errors.py` 0 错误 → `check_duplicates.py` 无重复。

---

## §二 B 类成员级映射表

> 「1.20 权威」= 1.20 同名注册表里的字段名（`--resolve` + 定点查证）。`同名` 即直接 `Simple → Target.Simple`。
> **`TESummonItems.*` / `TEPetItems.*` 已由并行 R3 批次落地**（`common/init/item/SummonItems.java` 已存在；
> `PetItems.java` 正在落地）→ 这两族落地后是**纯机械改指**，本表中已按「可机械」标注。

### 2.1 `TEMonsterEntities` → `MonsterEntities`（567 行；113 同名 + 2 例外）

**同名（113）**：ANGER_BONES, ANGER_GOBLIN, ANTLION_SWARMER, ARAPAIMA, BIG_ANGER_BONES, BIG_BONES, BIG_HELMET_ANGER_BONES,
BIG_MUSCLE_ANGER_BONES, BLACK_SLIME, BLOODY_SPORE, BLOOD_CRAWLER, BLOOD_MUMMY, BLOOD_ZOMBIE, BLUE_JELLYFISH, BLUE_SLIME,
BONE_SERPENT, CAVE_BAT, CORRUPT_MIMIC, CORRUPT_SLIME, CRAWDAD, CRIMERA, CRIMSLIME, CRIMSON_MIMIC, CURSED_SKULL, DARK_CASTER,
DARK_LAMIA, DARK_MUMMY, DECAYEDER, DEMON, DEMON_EYE, DERPLING, DESERT_SLIME, DEVOURER, DREAMER_GHOUL, DRIPPLER,
DUNGEON_SLIME, EATER_OF_SOULS, FACE_MONSTER, FIRE_IMP, FLYING_FISH, GHOST, GHOUL, GIANT_ANTLION_SWARMER, GIANT_SHELLY,
GIANT_WORM, GOBLIN_ARCHER, GOBLIN_PEON, GOBLIN_SCOUT, GOBLIN_SORCERER, GOBLIN_THIEF, GOBLIN_WARRIOR, GOLDEN_MIMIC,
GOLDEN_SLIME, GRANITE_ELEMENTAL, GREEN_DUMPLING_SLIME, GREEN_JELLYFISH, GREEN_SLIME, HALLOWED_MIMIC, HARPY,
HAT_SPORE_ZOMBIE, HELL_BAT, HERPLING, HORNET, ICE_BAT, ICE_MIMIC, ICE_SLIME, JUNGLE_BAT, JUNGLE_MIMIC, JUNGLE_SLIME,
LAVA_SLIME, LEECH, LIGHT_LAMIA, LIGHT_MUMMY, LITTLE_HORNET, LUMINOUS_SLIME, MAN_EATER, METEOR_HEAD, MUMMY, NYMPH,
PINK_JELLYFISH, PINK_SLIME, PIRANHA, PIXIE, POSSESS_ARMOR, PURPLE_SLIME, RED_SLIME, SAND_POACHER, SHADOW_MIMIC, SHARK,
SHORT_BONES, SNATCHER, SNOW_FLINX, SPIKED_ICE_SLIME, SPIKED_JUNGLE_SLIME, SPIKED_SLIME, SPORE_BAT, SPORE_SKELETON,
SPORE_ZOMBIE, SWAMP_SLIME, TAINTED_GHOUL, THE_HUNGRY, TOMB_CRAWLER, TROPIC_SLIME, UNDEAD_VIKING, VILE_GHOUL,
VISUAL_NEURON, VOODOO_DEMON, WANDERING_EYE_FISH, WITHER_BONE_SERPENT, WOODEN_MIMIC, WRAITH, WYVERN, YELLOW_SLIME

| 成员 | 判决 | 证据 / 目标 |
|---|---|---|
| `HONEY_SLIME`（2 处：bestiary 187、loot 437） | **1.21 缺 → 待定（倾向删）** | 1.20 `MonsterEntities` **无** `HONEY_SLIME`；TE 侧 `registerSlime("honey_slime")` + `HoneySlime` 类是 TE 独有。引用还有 lang（`bestiary.entity.terra_entity.honey_slime.desc` 中英各 1 条）、loot 路径 `entities/terra_entity/honey_slime`、`data/terra_curio/tags/entity_type/slime.json` 一条 |
| `SERVANT_OF_CTHULHU`（1 处：`LivingInvulnerableEffectsSubProvider:144`） | **改名 → `BossEntities.SERVANT_OF_CTHULHU`** | 1.20 `BossEntities.java:40` `registerEntity("servant_of_cthulhu", …)`；1.21 同名成员在 `BossEntities.java:108`；1.20 同位置写的是 `.add(BossEntities.SERVANT_OF_CTHULHU, ModEffects.SHIMMER, LibEffects.CONFUSED)` |

### 2.2 `TEAnimals` → `CritterEntities`（199 行；26 同名）

BIRD, BLUE_JAY, BUNNY, BUTTERFLY, CARDINAL, CRAB, DRAGONFLY, DUCK, EXPLOSIVE_BUNNY, FAIRY, FEALING, GLOWING_SNAIL,
GRASSHOPPER, GRUBBY, HELL_BUTTERFLY, JEWEL_BUNNY, JEWEL_SQUIRREL, LADYBUG, MAGGOT, MAGMA_SNAIL, PRISMATIC_LACEWING,
SCORPION, SLUGGY, SNAIL, SQUIRREL, WORM —— **全部同名，纯改指**（`1.20 CritterEntities` 逐条同名同 id）。

### 2.3 `TENpcEntities` → `NpcEntities`（65 行；21 同名）

ANGLER, ARMS_DEALER, CLOTHIER, DEMOLITIONIST, DRYAD, DYE_TRADER, **ENTITIES**（注册器本体，`ModFluids` 在用）, FEMALE_ANGLER,
GOBLIN_TINKERER, GUIDE, MECHANIC, MERCHANT, NURSE, OLD_MAN, PAINTER, PARTY_GIRL, TRAVELING_MERCHANT, TRUFFLE,
WITCH_DOCTOR, WIZARD, ZOOLOGIST —— 全部同名。

### 2.4 `TEBossEntities` → `BossEntities`（60 行；18 同名）

BRAIN_OF_CTHULHU, DUNGEON_GUARDIAN, EATER_OF_WORLDS, EATER_OF_WORLDS_SEGMENT, HILL_OF_FLESH, PLANTERA, PLANTERA_HOOK,
PLANTERA_TENTACLE, QUEEN_BEE, RETINAZER, SKELETRON, SKELETRON_HAND, SKELETRON_PRIME, SKELETRON_PRIME_PART, SPAZMATISM,
THE_DESTROYER, THE_TWINS, WALL_OF_FLESH —— 全部同名（`EATER_OF_WORLDS_SEGMENT` 的 1.20 id 是 `boss_worm_segment`，成员名一致）。

### 2.5 `TEEffects` → `ModEffects`（44 行）

| 成员 | 判决 | 证据 / 目标 |
|---|---|---|
| CRIMSON_STORM, DEMONIC_THOUGHTS, FROST_BURN, HELLFIRE, HORRIFIED, SCARED, THE_TONGUE（7 个） | **同名** | 1.20 `ModEffects` 逐条同名同 id |
| `SUMMON_FOCUS` | **1.21 缺 → 应删** | TE 侧注册 id 是 **`summon_mark`**（`TEEffects:15`），类 `SummonFocusEffect`；1.20 `ModEffects` **没有**该条目，1.20 java 全仓无 `summon_mark`/`SUMMON_FOCUS`，只有 lang 遗留键 `effect.confluence.summon_mark` + `tooltip.effect.confluence.summon_mark.0`。1.21 里唯一用法是两条 `addEffect(...)` 语言条目（中文 "狩猎"/"召唤物额外造成伤害"）；「召唤印记」机制两边都由附件 `SummonerAttachmentTypes.SUMMON_MARK_DATA` 承担 → 该 effect 是 TE 时代的并行实现 |

### 2.6 `TETags` → `ModTags`（16 行）

| 成员 | 判决 | 证据 / 目标 |
|---|---|---|
| `Blocks`, `Items`, `EntityTypes`（嵌套类） | **同名** | 1.20 `ModTags` 有同名嵌套类（`Blocks`/`Items`/`Biomes`/`Fluids`/`EntityTypes`/`RecipeSerializers`） |
| `DamageTypes`（5 处：`ModDamageTypeTagsProvider:29/39`、`WhipAttackEntity:409/465`、`ModDamageTypes:42`） | **改名 → `LibDamageTypes`（库）** | 注意：TE 的 `TETags.DamageTypes` 其实是**伤害类型注册表**，不是标签。1.20 的权威是 portlib `org.confluence.lib.common.LibDamageTypes` |

`TETags.DamageTypes` 的成员级判决（1.20 权威 = `LibDamageTypes`）：

| TE 成员/用法 | 判决 | 证据 |
|---|---|---|
| `SUMMON` | **改名 → `LibDamageTypes.SUMMON`，但 1.21 库缺 → 需补** | 1.20 库有 `SUMMON = register("summon")`；1.20 `WhipAttackEntity:406/463` 用 `LibDamageTypes.of(level(), LibDamageTypes.SUMMON, this, owner)`；**1.21 的 LibDamageTypes 只有 `SUMMONER`**（注释自认「补齐 1.20 的 13 个伤害类型是独立 WP」） |
| `FROST_BURN` | **改名 → `LibDamageTypes.FROST_BURN`**（已存在） | 1.21 库 `LibDamageTypes:21`；1.20 `ModDamageTypeTagsProvider:21-22` 用 `.addOptional(LibDamageTypes.FROST_BURN.location())` |
| `PASS_ARMOR`（`ModDamageTypeTagsProvider:39`） | **1.21 缺 → 应删**（该 tag 行整条删） | 1.20 java 全仓无 `PASS_ARMOR`；1.20 的同类标签只有 `tag(TCTags.HARMFUL_EFFECT).addOptional(...)` 三条 |
| `.of(level,key,…)` 静态入口 | **改名 → `LibDamageTypes.of(...)`** | 1.20 全仓用 `LibDamageTypes.of(...)` |
| `.createDamageTypes(context)`（`ModDamageTypes:42`） | **1.21 缺 → 应删** | 1.20 **没有** `ModDamageTypes.java`（无 `common/init/ModDamageTypes`、无 `common/data/gen/ModDamageTypes`），自定义伤害类型全在库侧 bootstrap |

### 2.7 `TESounds` → `ModSoundEvents`（6 行；4 同名）

ROAR, SOUL_DEATH, WALL_OF_FLESH_ROAR, WAVING —— 全部同名（1.20 `ModSoundEvents` 逐条同名同 id）。

### 2.8 `TEBoomerangItems` → `BoomerangItems`（16 行）

| 成员 | 判决 |
|---|---|
| ITEMS, COMBAT_WRENCH, DEVELOPER_BOOMERANG, ENCHANTED_BOOMERANG, FLAMARANG, ICE_BOOMERANG, SHROOMERANG, TRIMARANG, WOOD_BOOMERANG | **同名** |
| `BeiDou_BOOMERANG`（`ModTabs:2029`） | **改名 → `BEIDOU_BOOMERANG`**（大小写差异！1.21 `BoomerangItems.java:25` 已是 `BEIDOU_BOOMERANG = register("beidou_boomerang", …)`；1.20 `ModTabs:2242` 也写 `BoomerangItems.BEIDOU_BOOMERANG`） |

### 2.9 `TEYoyosItems` → `YoyoItems`（22 行；9 同名）

AMAZON, ARTERY, CASCADE, CODE_1, HIVE_FIVE, MALAISE, RALLY, VALOR, WOODEN_YOYO —— 全部同名（WP6-A 已落地）。

### 2.10 `TEWhipItems` → `WhipItems`（12 行；11 + ITEMS 同名）

AMBER_WHIP, AMETHYST_WHIP, DIAMOND_WHIP, JADE_WHIP, RUBY_WHIP, SAPPHIRE_WHIP, SLUB_WHIP, SNAPTHORN, SPINAL_TAP,
SWAMP_WHIP, TOPAZ_WHIP, ITEMS —— 全部同名。

### 2.11 `TEItems` → `ModItems`（4 行；0 同名 —— 全族都是 TE 独有）

| 成员 | 判决 | 证据 |
|---|---|---|
| `DEBUG_ITEM`（`ModItemTagsProvider:1406`） | **1.21 缺 → 应删** | 1.20 java 全仓无 `DEBUG_ITEM`。所在 tag 列表（`…STATIC_HOOK, DEBUG_ITEM, CHLOROPHYTE_DRILL…`）在 1.20 的同名 tag 里没有这一项 |
| `HOUSE_DETECTOR`（`CraftingRecipeProvider:146`「房屋探测器」配方产物） | **1.21 缺 → 待定（倾向删）** | 1.20 java **无**该物品（`house_detector` 全仓零命中；`Detector` 只命中原版 `DETECTOR_RAIL`）；但 1.20 的 lang 里有遗留键 `item.confluence.house_detector = 探室械`。1.20 的 `no_hobo` 成就是 `AchievementUtils.awardAchievement(player,"no_hobo")` 直接发，不依赖物品 |
| `NEO_TERRA`（`ModTabs:2014/2039`） | **1.21 缺 → 应删（改写 tab 顺序）** | 它不是物品，是 **TE 的创造标签页**（`withTabsAfter/withTabsBefore(TEItems.NEO_TERRA.getId())`）。1.20 的 `SUMMONERS` 写 `.withTabsBefore(MAGES.getId())`、`DEVELOPER` 写 `.withTabsBefore(ENTITY.getId())` → 退役后按 1.20 改写；⚠️ 1.21 目前**没有** `ENTITY` 标签页（WP7 datagen 范围） |

### 2.12 `TEArmors` → `ArmorItems`（1 行）

| 成员 | 判决 | 证据 |
|---|---|---|
| `POSSESSED_ARMOR`（`ModClientBestiaryEntryProvider:221` 的 `.mobArmorItems(TEMonsterEntities.POSSESS_ARMOR, "", List.of(TEArmors.POSSESSED_ARMOR.boots…))`） | **1.21 缺 → 应删** | 1.20 `ArmorItems` 无 `POSSESSED_ARMOR`（既非成员也非 `$` 集合）；1.20 同位置的写法是纯 `.add(MonsterEntities.POSSESS_ARMOR, builder -> builder.order(15600)…)`（`:239`），没有盔甲预览。`BaseWarriorMonster.LandSoundProfile.POSSESSED_ARMOR` 是**另一个东西**（音效档位），两边都在 |

### 2.13 `TEProjectileEntities` / `TEEntities` → `ModEntities`（5 + 1 行）

| 成员 | 判决 | 证据 |
|---|---|---|
| BOOMERANG_PROJECTILE, ICE_PILLAR, SLIME_SPIKE | **同名** | 1.20 `ModEntities` 同名同 id（`ice_pillar`、`slime_spike`） |
| `FIRE_IMP_PROJ`（`ImmunitySubProvider:25`） | **改名 → `FIRE_IMP_PROJECTILE`** | 1.20 `ModEntities:363` `register("fire_imp_projectile", … HostileParticleProjectile.Variant.FIRE_IMP)`；1.21 `ModEntities:593` 同名（这就是用户给的样例形态） |
| `SUMMON_BEE_STICK_PROJ`（`ImmunitySubProvider:26`） | **1.21 缺 → 应删（或随 WP5 召唤弹幕命名另定）** | 1.20 java 全仓无 `BEE_STICK`/`BeeStick`/`bee_stick`；1.21 lang 里有 `entity.terra_entity.summon_bee_stick_proj`（TE 独有） |
| `TEEntities.getEntities`（`EntitySubProvider:1297`） | **1.21 缺 → 需补** | 1.20 `ModEntities:731` 有 `public static List<DeferredRegister<EntityType<?>>> getEntities()`；1.21 `ModEntities` 没有 → 按 1.20 补一个（或直接用 `ModEntities.ENTITIES`） |

### 2.14 `TESpawnEggItems` → `SpawnEggItems`（6 行；6 同名）

PLANTERA_SPAWN_EGG, RETINAZER_SPAWN_EGG, SKELETRON_PRIME_SPAWN_EGG, SPAZMATISM_SPAWN_EGG, THE_DESTROYER_SPAWN_EGG,
THE_TWINS_SPAWN_EGG —— 全部同名（WP6-C 已落地 `SpawnEggItems`）。

### 2.15 `TEEnchantments` → `ModEnchantments`（1 行 —— 已被并行批次清掉）

| 成员 | 判决 |
|---|---|
| `WHIP_SWEEP` | **同名**（1.20 `ModEnchantments:35`；1.21 已是 `ResourceKey<Enchantment>` 数据驱动形态 `ModEnchantments:34`）。1.20 的取值写法是 `EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.WHIP_SWEEP.get(), weapon)`，1.21 对应 `EnchantmentUtils.getEnchantmentLevel(ModEnchantments.WHIP_SWEEP, weapon)` —— 该处已在会话中被改好 |

### 2.16 `TEFigureBlocks` → `FigureBlocks`（3 行）

| 成员 | 判决 | 证据 |
|---|---|---|
| `FIGURE`, `FIGURE2`, `FIGURE3`（`ModItemTagsProvider:1413-1415` 的 `.asItem()`） | **1.21 缺 → 待定（倾向删条目）** | TE 独有装饰方块（`FigureBlock`，53 行 + 方块实体 + 资源）。1.20 java 全仓无 `Figure`/`FIGURE`；1.20 lang 里也**没有**任何 `figure` 键 → 1.20 从未有过这套内容 |

### 2.17 `TEPetItems` → `PetItems`（2 行）—— **R3 落地后即可机械改指**

| 成员 | 判决 | 证据 |
|---|---|---|
| `WALLET`（`EntitySubProvider:283/297`、`ValueSubProvider:694`） | **改名 → `MONEY_TROUGH`** | 1.20 `PetItems:19` `MONEY_TROUGH = ITEMS.register("money_trough", () -> new StorageCompanionItem<>(…FlyingPiggyBankEntity))`；1.20 同位置正是 `.add(LootItem.lootTableItem(PetItems.MONEY_TROUGH.get())…)`（`EntitySubProvider:295/321`）。⚠️ **id 也变**（`wallet` → `money_trough`），且依赖「存储伙伴是否移植」的既有裁决（WP6-C 曾判不移植） |
| `CHESTER_STAFF` | 未被主模组引用（无需处理） | — |

### 2.18 `TESummonItems` → `SummonItems`（14 行）—— **R3 已落地，纯机械**

| 成员 | 判决 | 证据 |
|---|---|---|
| ITEMS, FINCH_STAFF, IMP_STAFF, IRON_GOLEM_STAFF, SCULK_WISP_STAFF, SLIME_STAFF, SNOW_FLINX_STAFF | **同名** | 1.20 `SummonItems` 逐条同名（`finch_staff`/`imp_staff`/`iron_golem_staff`/`sculk_wisp_staff`/`slime_staff`/`snow_flinx_staff`）；1.21 `SummonItems.java` 已存在 |
| `HORNET_STAFF`（`HeavyWorkBenchProvider:1036`） | **改名 → `NEW_HORNET_STAFF`** | 1.20 里没有 `HORNET_STAFF`，只有 `NEW_HORNET_STAFF`（`:54` `ITEMS.register("new_hornet_staff", …)`）→ **id 也变**，涉及配方输出 |

### 2.19 §二 汇总

| 判决 | 成员数 | 清单 |
|---|---:|---|
| 同名（纯改指） | ~1043 行 | 见上各表 |
| 改名 | 6 | `BeiDou_BOOMERANG`→`BEIDOU_BOOMERANG`、`HORNET_STAFF`→`NEW_HORNET_STAFF`、`WALLET`→`MONEY_TROUGH`、`FIRE_IMP_PROJ`→`FIRE_IMP_PROJECTILE`、`SERVANT_OF_CTHULHU`→`BossEntities.SERVANT_OF_CTHULHU`、`TETags.DamageTypes.{SUMMON,FROST_BURN}`→`LibDamageTypes.*` |
| 需补 | 2 | `LibDamageTypes.SUMMON`（库侧缺）、`ModEntities.getEntities()` |
| 应删 | 7 | `TEEffects.SUMMON_FOCUS`、`TETags.DamageTypes.PASS_ARMOR`、`TETags.DamageTypes.createDamageTypes`、`TEItems.DEBUG_ITEM`、`TEItems.NEO_TERRA`、`TEArmors.POSSESSED_ARMOR`、`TEProjectileEntities.SUMMON_BEE_STICK_PROJ` |
| 待定 | 3 | `TEMonsterEntities.HONEY_SLIME`、`TEItems.HOUSE_DETECTOR`、`TEFigureBlocks.FIGURE{1,2,3}` |

---

## §三 C 类逐类判定（63 类 / ≈155 行）

### 3.1 `改指`（41 类）—— 目标类已核实存在于 1.21 树内

| TE 类 | 次数 | 目标（1.21 已验证存在） | 1.20 同位置证据 |
|---|---:|---|---|
| `DemonEyeVariant` | 15 | `common/entity/monster/DemonEye.Variant`（1.21 `DemonEye` 有嵌套 `Variant` + `VARIANT_KEY="Variant"`；`DemonEyeGeoModel:10` 已在用） | 1.20 写 `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.DILATED, …)`（14 个变体：DILATED…OWL） |
| `TEUtils`（方法族） | 14 | `org.confluence.lib.util.LibEntityUtils` / `LibMathUtils`（1.20 就是它） | 逐方法：`getAABBAngleTarget`（`LibEntityUtils`）、`angleBetween`/`interpolateSimple`（`LibMathUtils`）、`getPlayerHandPos`（`LibEntityUtils`）、`projectileCanHurtEntityTest` → `LibEntityUtils.canHitEntity(e, owner)`；`internalSpawnEntity` → **1.20 无此步，删**（`BossSummoningItem`/`ModUtils` 各一处） |
| `GeoNegativeVolumeRenderer` | 7 | `org.confluence.mod.client.entity.renderer.GeoNegativeVolumeRenderer`（1.21/1.20 均有同名类） | 1.20 同位置同名（`client/event/ModClientEvents`、两个 projectile renderer） |
| `ITrackType` | 6 | `org.confluence.mod.api.ITrackType`（两边同名接口） | 1.20 同位置同名 |
| `AbstractTerraBossBase` | 5 | `common/entity/boss/BaseBoss`（`BaseBoss:56 extends BaseMonster implements Boss`） | 1.20 `ModUtils.summonBoss(…, BaseBoss boss, …)`；`DeadBodyPartEntity:64` 1.20 只判 `instanceof Boss`。→ **删 1.21 里的 TE 重载**（`ModUtils:129/142`），只留 `BaseBoss` 两条 |
| `WallOfFlesh` | 5 | `common/entity/boss/WallOfFlesh`（两边同名） | 1.20 同位置同名 |
| `BaseWormPart` | 4 | `common/entity/monster/BaseWormPart`（两边同名） | 1.20 `registerBaseWorm(RegistryObject<EntityType<SimpleWormMonster>>)` —— 泛型参数形状不同，需按 1.20 改签名 |
| `IMinion` | 4 | `org.confluence.mod.api.summon.OwnedSummon`（两边同名接口） | 1.20 `LivingEntityEvents:153/169` 用 `!(victim instanceof OwnedSummon)` |
| `WoodenMimic` | 3 | `common/entity/monster/WoodenMimic`（两边同名）；但 1.20 变量类型写 `BaseMimic` | 1.20 `CustomMimicSummonKeyEvent.summon(BaseMimic mimic, …)`、`PlayerEvents:433` `BaseMimic mimic = MonsterEntities.HALLOWED_MIMIC…` |
| `DemonEye` | 2 | `common/entity/monster/DemonEye`（两边同名） | 1.20 `StatueBlocks:138` 用 `.create(level)` 而非 `new DemonEye(...)` |
| `AbstractTerraNPC` | 2 | `common/entity/npc/BaseNPC`（两边同名） | 1.20 `if (entity instanceof BaseNPC)`（`BossDelaySpawner:141`、`TombstoneBoulderEntity:97`） |
| `BasisTrack` / `SimpleTrack` | 2 + 2 | `util/track/variant/{BasisTrack,SimpleTrack}`（两边同名 record） | 1.20 同位置同名 |
| `SimpleVariantAnimal` | 2 | `common/entity/animal/Worm`（4 处 1.21 写 `EntityType<SimpleVariantAnimal> type = TEAnimals.WORM.get()`） | 1.20 `EntityType<Worm> type = CritterEntities.WORM.get()` |
| `ILeftClickStateItem` | 2 | `api/item/ILeftClickStateItem`（两边同名接口） | 1.20 同位置同名 |
| `ISummonMob` | 2 | 删（无对应）；同位置的 `IMinion` 部分改 `OwnedSummon` | 1.20 **没有**这两个 mixin 文件（`mixin/client/MinecraftMixin`、`mixin/world/entity/LocalEntityMixin` 都是 1.21 独有） |
| `NPCMood` | 2 | `common/entity/npc/mood/NPCMood`（两边同名类） | 1.20 同名类存在；但使用它的 `EntityMaidMixin` 是 1.21 独有文件 → 见 3.3 |
| `AimUtils` | 2 | `org.confluence.lib.util.AimUtils`（**1.20 的 import 就是 `org.confluence.lib.util.AimUtils`**） | 1.20 `AboveFallenGeneration:13/66/69` 逐行同名同调用 |
| `IVariant` | 1 | `common/entity/IVariant`（两边同名接口） | 1.20 同名（`RegisterBestiaryKeyEvent` 的 `IVariant<V>` 泛型用法在 1.20 是另一形态，需按 1.20 重写该 helper） |
| `BoneSerpent` / `SurefaceWorm` | 1 + 1 | `common/entity/monster/SimpleWormMonster`（bestiary 渲染器泛型） | 1.20 `RegisterCustomBestiaryEntryRendererEvent.registerBaseWorm(RegistryObject<EntityType<SimpleWormMonster>>)` |
| `TETradeScreen` | 1 | `common/entity/npc/trade/NPCTradeMenu`（1.21 已存在） | 1.20 `ClientConfigs.TRADE_SCREEN.test()` = `Minecraft.getInstance().player != null && player.containerMenu instanceof NPCTradeMenu` |
| `IHouseDetector` | 1 | `common/entity/npc/house/HouseValidater`（1.20/1.21 同名） | 1.20 `HouseValidater.Result detect = HouseValidater.scan(player.level(), pos)` |
| `GeoWormRenderer` | 1 | `GeoNormalRenderer<SimpleWormMonster>` | 1.20 的 `GeoWormBestiaryEntryRenderer extends GeoNormalRenderer<BaseWormMonster>`（无泛型蠕虫渲染器） |
| `GeoNormalRenderer` | 1 | `client/entity/renderer/GeoNormalRenderer`（两边同名） | 1.20 同位置同名 |
| `BrainOfCthulhu`/`QueenBee`/`EaterOfWorlds`/`DungeonGuardian`/`HillOfFlesh`/`LittleHornet`/`GoldenSlime`/`AnglerNPC` | 各 1 | 主模组对应 `common/entity/{boss,monster,npc}` 同名类 | 1.20 同位置：`ModUtils.summonBoss(…, new BrainOfCthulhu(BossEntities.BRAIN_OF_CTHULHU.get(), level), false)`；`EaterOfWorlds` 两边构造式不同（1.21 `new EaterOfWorlds(level, true)` → 1.20 `new EaterOfWorlds(BossEntities.EATER_OF_WORLDS.get(), level)`） |
| `AbstractSummonMob` | 1 | `api/summon/OwnedSummon` | 1.20 `LivingEntityEvents:588` 附近写的是 `OwnedSummon` 判定 |
| `WeaponStorage` | 1 | `common/attachment/LeftClickState`（两边同名，`LeftClickState.of(owner).isPressed(stack)`） | 1.20 `BaseLanceItem:110` |
| `WallOfFleshRenderer` | 1 | `client/entity/renderer/WallOfFleshRenderer`（两边同名） | 1.20 同位置是**注释掉**的一行（`DeathAnimUtils:233`）→ 见 3.3/3.4 |
| `IPlayer` | 1 | `org.confluence.mod.mixed.IPlayer`（两边同名接口）；**但 `terra_entity$getTradeHolder()` 1.21 的 IPlayer 没有** | 1.20 该行本身是 `// todo trade` 注释 → 这条引用应随 3.4 一起删 |
| `renderDebugBlock`（static import） | 2 | `LibRenderUtils.renderDebugBlock(...)` | 1.20 `SpelunkerHelper:531/534` 逐行 `LibRenderUtils.renderDebugBlock(...)` |
| `VariantsTextureMaps` | 1 | `Worm.Variant`（1.21 `Worm.java:101 enum Variant`） | 1.20 `worm.setVariant(Worm.Variant.NIGHTCRAWLER)` |

> `TEUtils` 的方法级还有 3 处需要**逐个方法查库**：`interpolateSimple`（`util/track/variant/SimpleTrack` 两处）、
> `getPlayerHandPos`（`StillGeneration:34`）、`angleBetween`（`BeeArrowEntity:46`、`BaseDraggingProjectile:52`、`BeeGunBullet:38`、`SwordProjectile:191`）。
> 1.20 对侧全部是 `LibMathUtils.*` / `LibEntityUtils.*`，但 1.21 的库不一定逐方法齐全（
> `LibDamageTypes` 就已经缺 `SUMMON`）→ **开工前用 1.20 方法名在 1.21 库源码里核一遍**。

### 3.2 `搬`（2 类）—— 1.21 没有、1.20 有现成实现

| TE 类 | 次数 | 1.20 载体 | 行数 | 主模组自有 WP 是否已覆盖 |
|---|---:|---|---:|---|
| `AbstractBufferManager` | 1 | `client/effect/AbstractBufferManager.java` | 110 | ❌ 无（1.21 的 `SpelunkerHelper` 直接 `extends` TE 的版本）→ 照 1.20 搬 |
| `SpitParticle` | 1 | `client/particle/SpitParticle.java` | 76 | ❌ 无（1.21 `ModClientEvents:1216` 注册 `SpitParticle.EmissiveProvider`）→ 照 1.20 搬 |

### 3.3 `待定`（10 类）—— 每条一句问题 + 我的建议

| TE 类 | 次数 | 问题（需你定） | 我的建议 |
|---|---:|---|---|
| `ITradeLock` | 6 | `PlayerSpecialData` 的「任务鱼条件」（`currentQuestedFishCondition`）在 1.20 **根本不存在**（1.20 只有 `completedAnglerQuestDay`/`anglerQuestCount`）；是保留这个 1.21 独有特性（改指原生 `TradeCondition`）还是按 1.20 删？ | **删**该字段与 3 处用法（对齐 1.20）；若要保留，另开「1.21 独有特性」条目，用原生 `TradeCondition` + `AlwaysTrueCondition` |
| `ITradeHolder` | 3 | 同上；`PrefixUtils:474` 在 1.20 是 `// todo trade …confluence$getTradeHolder()` 注释、`IPlayer` 1.21 也没有该 mixed 方法 | **删** `PrefixUtils:474-478` 的价格心情修正（退化回 `price/3`，与 1.20 一致）；`EntityMaidMixin` 见下一行 |
| `NPCTradeManager` / `TradeParams` / `UpdateNPCTradePacket` + `ITradeHolder`（同属 `mixin/integration/touhoulittlemaid/EntityMaidMixin`，87 行） | 3+2+1+1 | 1.20 **完全没有**女仆集成（`mixin/integration/touhoulittlemaid/*` 两个文件都是 1.21 独有；1.20 只有 lang 键 `title.confluence.touhoulittlemaid`）。删掉整个 mixin（并同步 `confluence.mixins.json:26`）还是自研一套原生交易持有者抽象？ | **删 mixin + 同步 mixins.json**（保住 `MaidFishingHookMixin`）。自研原生 `ITradeHolder` 等价物 ≈ 611 行（174+295+142），属独立项目 |
| `CuriosHelper` | 3 | 1.21 `ExtraInventory.initialize()` 用 TE 的 `PET_KEY/LIGHT_PET_KEY/MOUNT_KEY` 把三个 curios 槽接进 `equipment`；1.20 的 `initialize()` **没有**这段。删掉会不会让宠物/坐骑槽显示空？ | **先按 1.20 删这 3 行**，然后实测宠物/坐骑/矿车槽位；若真的空，就只把 3 个字符串常量（`"pet"`/`"light_pet"`/`"mount"`）本地化到主模组常量类，**不搬 TE 类** |
| `IEffectStrategy` | 4 | 1.21 `SpearProjectile` 的 `Config.hitEffect`（策略注册表）在 1.20 是 `protected void applyHitEffect(LivingEntity owner, LivingEntity target) {}` + 子类覆写 | **删策略**，按 1.20 落成可覆写空方法（与 WP6-B 的「剑层/箭层替换」同批，因为都在 `SpearProjectile`） |
| `KeyframeAnimation` | 2 | 151 行的关键帧插值器，1.20 的 `NPCReforgeScreen`（在 `client/gui/container/npc_screen/`）没有它 | 先看 1.20 重铸界面怎么做的前缀过渡；若只是 `EvictingQueue` 文本滚动就**删**，若确需插值则**搬** 151 行 |
| `BaseEntityRenderer` | 1 | 用它的 `client/renderer/entity/projectile/sword/ForwardProjRenderer.java` 是 **1.21 独有文件**（1.20 无对侧），随 WP6-B 剑层替换而定 | 随 WP6-B：若剑弹幕渲染器按 1.20 那代重建，则整文件退役（判**删**）；否则搬 59 行 |
| `IZombie` | 1 | 1.21 `ModEvents:324` 用 `IZombie.of(zombie).terra_entity$isSlimeZombie()` 判「史莱姆僵尸」图鉴变体；1.20 无 `IZombie`，但 1.20 的图鉴条目是 `.add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "slime", …)`（`:227`），即用**另一条判定链** | 按 1.20 改判：删 `IZombie` 用法，改由 1.20 的 zombie 变体判定（`ZombieGeoModel`/`IS_SLIME_ZOMBIE` 数据）驱动；这属于图鉴批 |
| `TEEffects.SUMMON_FOCUS`（§二 已列）与 `TEPetItems.WALLET`、`TEMonsterEntities.HONEY_SLIME`、`TEItems.HOUSE_DETECTOR`、`TEFigureBlocks.FIGURE*` | — | **内容级**决定：这些是 TE 独有内容（蜜史莱姆、钱包=钱币槽、房屋探测器、摆件方块），退役=它们从游戏里消失（`/give`、成就、掉落、图鉴一并失效） | 建议统一「**先删引用，内容另立项目从 TE 移植**」；其中 `WALLET` 已有 1.20 等价物（`PetItems.MONEY_TROUGH`）→ 直接改名，不属于此列 |

### 3.4 `删`（10 类）—— 引用本身该消失

| TE 类 | 次数 | 代码 / 它在做什么 |
|---|---:|---|
| `TerraEntity` | 5 | `TerraEntity.MODID`：3 个 model mixin 的 `confluence$skipSet` + `RegistryDataLoaderMixin` 的 biome modifier 过滤 + `ModUtils.CONFLUENCE_NAMESPACES`。1.20 对应写法是 `Set.of(Confluence.MODID, TerraFurniture.MODID)`（**不是** TE）→ 3 个 mixin 与 `ModUtils` 按 1.20 改；`RegistryDataLoaderMixin` 整个文件删（见下） |
| `DebugBlocksHelper` | 3 | `DebugBlocksHelper.Singleton().addDebugBlock(pos, new DebugInfo(r,g,b,a))`（`HouseSelectHud:153/155`、`RainbowBoulderEntity:206`）。**1.20 这三行全是注释**（`//  DebugBlocksHelper.Singleton()...`）→ 删调用与 import |
| `AbstractMonster` | 3 | `new AbstractMonster(TEMonsterEntities.X.get(), level, FlyMonsterPrefab.Y.get())`（`StatueBlocks:136/138`）+ `DecomposeTheSourceExtractBlock:108` 的 `AbstractMonster entity = …create(level)`。1.20 写 `MonsterEntities.X.get().create(level)` → 用 `Entity`/`Mob` 类型 + `create(level)` |
| `DeathAnimOptions` | 3 | `DeathAnimUtils` 的 `Map<EntityType<?>, DeathAnimOptions> options` + `getDeathAnimOptions`。1.20 三行全注释（`// todo blood`），且 1.20 全仓无该类型声明 → 删 map + 方法 |
| `RecipeDrawerUtils` | 2 | `ModEnglishProvider.formatLocation/formatString` 调用 TE 的字符串格式化。**1.20 这两个私有方法本身就是注释掉的死代码** → 删 |
| `FlyMonsterPrefab` | 2 | `FlyMonsterPrefab.EATER_OF_SOULS_BUILDER.get()` / `DRIPPLER_BUILDER.get()`（雕像召唤）。1.20 不需要 prefab（实体自带属性）→ 删 |
| `NPCEvent` | 1 | `GameClientEvents.npc$Dialog(NPCEvent.NPCDialogEvent event)`（269 行的 TE 事件）。1.20 整个方法体是注释（`//  todo  private static void npc$Dialog(NPCEvent.NPCDialogEvent event)`）→ 删方法。注意它带的两个语言键 `dialogs.terra_entity.guide.jei_check` / `nurse.player_killed_by` 属 §四 键族重定向 |
| `IAttackableProjectile` / `ICollisionAttackEntity` | 1 + 1 | `SpearProjectile implements ICollisionAttackEntity` / `IAttackableProjectile.tryHit(target, damageSource)`。1.20 同位置是 `ProjectileHitRules.canHit(getOwner(), target)`（主模组类，两边都有）+ 直接 `hurt` → 删这两个接口用法 |
| `BaseSlime` | 1 | `new BaseSlime(TEMonsterEntities.BLUE_SLIME.get(), level, 0x73BCF4, 2)`（`StatueBlocks:156`）。1.20 写 `MonsterEntities.BLUE_SLIME.get().create(level)` → 用 `create` |

### 3.5 §三 汇总

| 判决 | 类数 | 使用行数 |
|---|---:|---:|
| 改指 | 41 | ≈109 |
| 搬 | 2 | 2 |
| 删 | 10 | 22 |
| 待定 | 10 | 22 |
| 合计 | 63 | ≈155 |

---

## §四 TE 命名空间资源面

> 「类之外」的依赖面：字符串、翻译键、data/assets、工程结构。**这一节的东西不会让编译变红**，但会让游戏静默变样。

### 4.1 java 里的 `terra_entity` 字符串面（19 文件 / ≈1528 行）

| 文件 | 行数 | 内容 |
|---|---:|---|
| `common/data/gen/language/BestiaryLanguageSubProvider.java` | **1119** | `bestiary.entity.terra_entity.<id>.desc` 全套图鉴描述（1.20 是 `bestiary.entity.confluence.<id>.desc`） |
| `common/data/gen/loot/EntitySubProvider.java` | **118** | 战利品表显式 key `entities/terra_entity/<id>`（1.20 **不用**显式 key，见 §六） |
| `common/data/gen/ModChineseProvider.java` / `ModEnglishProvider.java` | 95 + 95 | `terra_entity.subtitle.*` 字幕键（1.20 是 `confluence.subtitle.*`） |
| `common/data/gen/language/DialogsLanguageSubProvider.java` | 86 | `dialogs.terra_entity.*`（1.20 是 `dialogs.confluence.*`，190 键一一对应） |
| `common/event/ModEvents.java` | 3 | 注释/常量 |
| `mixin/client/resources/model/{BlockStateModelLoader,ModelBakery,ModelManager}Mixin` | 2×3 | `TerraEntity.MODID` 进 `confluence$skipSet` |
| `mixin/resources/RegistryDataLoaderMixin` | 2 | `reader.location().getNamespace().equals(TerraEntity.MODID)` 过滤 biome modifier |
| `common/entity/boss/EaterOfWorlds.java` / `common/init/ModCustomRegistries.java` / `common/init/ModTradeConditions.java` / `common/recipe/special/BoomBunnyRecipe.java` / `util/PrefixUtils.java` | 2/1/1/1/1 | 命名空间常量、注册表 id、配方/工具 |
| `client/event/GameClientEvents.java` | 2 | `dialogs.terra_entity.guide.jei_check`、`dialogs.terra_entity.nurse.player_killed_by` |
| `client/gui/screen/AnglerDialogScreen.java` | 1 | 注释里指向 TE 的 `AnglerDialogScreen` |
| `util/ModUtils.java` | 2 | `CONFLUENCE_NAMESPACES` 含 `TerraEntity.MODID` |

### 4.2 `src/main/resources` 里的 `terra_entity` 引用（28 文件）

| 文件 | 数量 | id / 键 |
|---|---:|---|
| `data/confluence/advancement/achievements/*.json` | 11 文件 | `terra_entity:deerclops`（an_eye_for_an_eye）、`skeletron`（boned）、`nymph`（deceiver_of_fools）、`eye_of_cthulhu`（eye_on_you）、`brain_of_cthulhu`（mastermind）、`house_detector`（no_hobo）、`pink_slime`（pretty_in_pink）、`king_slime`（slippery_shinobi）、`queen_bee`（sting_operation）、`wooden_yoyo`（throwing_lines）、`slime_staff`（you_and_what_army）。**1.20 同文件用 `confluence:<同一 id>`**（实测 `an_eye_for_an_eye`、`you_and_what_army`） |
| `data/confluence/neoforge/biome_modifier/common_{basalt_deltas,soul_sand_valley}_spawns.json` | 2 文件 | `terra_entity:{bone_serpent,demon,hell_bat,lava_slime,wither_bone_serpent}` |
| `data/confluence/npc/dialogs.json` | 1 文件 / 18 id | 18 个 NPC：angler, arms_dealer, clothier, demolitionist, dryad, dye_trader, female_angler, goblin_tinkerer, guide, mechanic, merchant, nurse, old_man, painter, party_girl, traveling_merchant, truffle, witch_doctor。**1.20 该文件不入库**（由 `NPCDialogProvider`(387 行) datagen 产出到 `confluence/npc/dialogs.json`） |
| `data/terra_curio/tags/entity_type/slime.json` | 1 文件 / 17 id | black/blue/corrupt/**crimson**/desert/**evil**/green/**honey**/ice/jungle/lava/luminous/pink/purple/red/tropic/yellow_slime。⚠️ 主模组注册表里叫 `crimslime`，`evil_slime`/`honey_slime` 不存在（TE 独有）；1.20 无此文件（datagen 产物） |
| `assets/confluence/ageratum/zh_cn/**` | 8 文件 | 属性文档/改名表引用 `terra_entity:player.minion_capacity` 等 6 个属性 + `terra_entity:summoner`、`terra_entity:jade_whip`；`hot_issues/index.md` 含命名空间链接 |
| `assets/confluence/lang/*.json`（5 个已入库语区） | de 293 / es 1145 / lzh 1018 / pt 1064 / ru 14 | 见 4.3 |

### 4.3 语言键族（已入库 5 个语区；`zh_cn`/`en_us` 由 datagen 生成、不在库里）

以 `lzh.json` 为样本（1.21 → 1.20 键族对照，**同键数 = 可机械重定向**）：

| 1.21 键族 | 1.20 键族 | 1.21 有 | 1.20 有 | 同键 | 1.21 独有（= TE 独有内容） |
|---|---|---:|---:|---:|---|
| `entity.terra_entity.` | `entity.confluence.` | 187 | 293 | **180** | 7（`honey_slime` + `summon_{wooden,stone,iron,golden,diamond,netherite}_sword`） |
| `item.terra_entity.` | `item.confluence.` | 191 | 1550 | **183** | 8（`honey_slime_spawn_egg`、`wallet`、`summon_*_staff` 6 个） |
| `dialogs.terra_entity.` | `dialogs.confluence.` | 190 | 190 | **190** | 0 |
| `bestiary.entity.terra_entity.` | `bestiary.entity.confluence.` | 221 | 221 | **221** | 0 |
| `terra_entity.subtitle.` | `confluence.subtitle.` | 72 | 99 | **72** | 0 |
| `mood.terra_entity.` | `mood.confluence.` | 51 | 51 | **51** | 0 |
| `tooltip.terra_entity.` | `tooltip.confluence.` | 32 | 44 | **32** | 0 |
| `terra_entity.configuration.` | `confluence.configuration.` | 21 | 203 | **21** | 0 |
| `terra_entity.effect.` | `confluence.effect.` | 18 | 18 | **18** | 0 |
| `effect.terra_entity.` | `effect.confluence.` | 7 | 66 | **7** | 0 |
| `enchantment.terra_entity.` | `enchantment.confluence.` | 6 | 22 | **6** | 0 |
| `block.terra_entity.` | `block.confluence.` | 0 | 958 | 0 | — |

**关键事实**：1.21 的 187 个 `entity.terra_entity.<id>` 键里有 **150 个 id 已经在主模组的注册表里**
（`MonsterEntities` 201 / `BossEntities` 31 / `CritterEntities` 39 / `NpcEntities` 34）→ 这些键**当前就是死的**
（真实键由 datagen 生成为 `entity.confluence.<id>`）。因此 4.3 的整表重定向不是可选优化，而是**修 5 个语区的显示回归**。

### 4.4 移除面（工程结构）

| 项 | 现值 | 备注 |
|---|---|---|
| `settings.gradle` | `:14-19` `projectName = ["Confluence-Magic-Lib","TerraEntity","TerraCurio","TerraFurniture"]`；`:22` `forEach { include ":" + it }` | 删数组里的 `"TerraEntity"` 一行 |
| `.gitmodules` | `[submodule "TerraEntity"] path=TerraEntity url=…/TerraEntity` | 需同步删段 |
| gitlink | `160000 62984cf35a4f964df0174f7c0557b4a5feac22de 0 TerraEntity` | 用 `git rm --cached TerraEntity`（本任务不做 git 操作，仅记录） |
| `TerraEntity/` | **5368 文件 / 118.72 MB**（含 build 产物）；源码 860 java / 83682 行；`src/main/resources` 1103 文件 / 7.66 MB | 其中 `assets/terra_entity` **1020 文件 / 7.41 MB**（geo/动画/贴图/音效），`data/terra_entity` **8 文件 / 0.19 MB** |
| `confluence.mixins.json` | **无任何 terraentity 条目**（包名 `org.confluence.mod.mixin`；TE 自己有 `terra_entity.mixins.json`，随子模块消失） | 唯一要注意的是 `:26 integration.touhoulittlemaid.EntityMaidMixin`、`:32-33 resources.RegistryDataLoaderMixin{, $RegistryDataMixin}` 三条**因退役而变**（见 §一 1.3） |
| `build.gradle` / `gradle.properties` | 无 `terraentity` 硬引用（`gradle.properties:18` 的 `terrablender_version` 是误命中） | — |

### 4.5 会**静默**坏掉的（不报错、不崩，只是行为变样）

1. **只改注册不改 data json**：11 个成就 + 2 个刷怪 biome modifier + `npc/dialogs.json` 指向不存在的 `terra_entity:*` → 成就拿不到、刷怪不生成、NPC 对话空。
2. **只改注册不改 5 个 lang**：150 个实体的本地化名在 de/es/lzh/pt 里找不到键（回退 en_us），字幕/图鉴键同理。
3. **`EntitySubProvider` 的 118 处显式 loot key**：`confluence:entities/terra_entity/<id>` 与实体默认表 id（1.20 的隐式派生 = `confluence:entities/<id>`）不一致 → 退役后若只换 id 不换前缀，怪物掉落可能集体为空（见 §六 第 4 条）。
4. **删 `EntityMaidMixin` 不同步 `mixins.json`** / 换 `RegistryDataLoaderMixin` 不同步 `:32-33` → 启动期崩（这是**硬**失败，列在此处是为了提醒必须同批）。
5. **`settings.gradle` 留 `"TerraEntity"` 而目录已删** → Gradle 配置期报「project directory does not exist」（硬失败）。

---

## §五 风险清单

| # | 风险 | 机制 / 证据 | 后果 | 缓解 |
|---|---|---|---|---|
| 1 | **存档里的 TE 实体 id** | 实体类型 `terra_entity:<id>` 现在真实存在于世界中（TE 与主模组两套注册表并存）；旧存档的实体、`BossDelaySpawner` 里持久化的实体引用、刷怪器 NBT 都记着 `terra_entity:*` | 退役后旧存档里这些实体解析失败（消失/变成空气） | 复用已有机制：`Confluence.registerEntityAliases()` 的 `BuiltinRegistries.ENTITY_TYPE.addAlias(from,to)`，把 105 个实体 id 全部 `terra_entity:X → confluence:X` 加进去（**与 id 改写同批**） |
| 2 | **图鉴进度键** | 图鉴条目键 = `type.getDescriptionId() + '.' + variant`（`RegisterBestiaryKeyEvent`），TE 实体的 description id 是 `entity.terra_entity.*`，主模组是 `entity.confluence.*` | 玩家已有图鉴进度按旧键保存 → 改名后进度看起来清零 | 同批加键别名/迁移（或在图上只认 id 的 path 部分）；**风险未验证：图鉴进度存在哪里需实测**（本次没能定位持久化点） |
| 3 | **翻译键命名空间** | §4.3：150 个已注册实体的键现在是 `entity.terra_entity.*`（死键） | de/es/lzh/pt 的实体名已在回退 en_us（**现状即坏**），退役时若不重定向会继续坏 | SB-8 按 4.3 表机械重定向全部 12 个键族 |
| 4 | **战利品表路径前缀** | §4.1/§六-4：`EntitySubProvider` 118 处显式 key `entities/terra_entity/<id>`，1.20 不写显式 key | 掉落表 id 与实体默认表不一致 | SB-1 里把 118 处改成 1.20 形态（`add(type, builder)`），并实测几种怪物掉落 |
| 5 | **音效/效果 id** | `TESounds`→`ModSoundEvents` 的 4 个成员同名，但音效**注册 id** 由字段决定；`TEEffects` 里 `SUMMON_FOCUS` 的 id 是 `summon_mark`（与字段名不一致，是 TE 的坑） | 效果/字幕 key 与实际注册 id 错位 | 只按 §二 表改名；`SUMMON_FOCUS` 直接删（1.20 无） |
| 6 | **标签 id** | `data/terra_curio/tags/entity_type/slime.json` 17 条里 `crimson_slime`/`evil_slime`/`honey_slime` 在主模组**没有对应实体**（主模组是 `crimslime`） | 标签条目失效（连带 TC 饰品的史莱姆判定失效） | 该文件按主模组实体重写（`crimslime`），删 `evil_slime`/`honey_slime`（或随内容决策） |
| 7 | **`CONFLUENCE_NAMESPACES` 语义漂移** | 1.21 = `{Confluence, TerraCurio, TerraEntity}`；1.20 = `{Confluence, TerraCurio, TerraFurniture}` | 现在 `terra_furniture` 的资源**不算自家**（配置界面/工具判定），退役时若照抄 1.20 会顺带改掉对 TerraFurniture 的判定 | 这是**真行为变更**，需你确认（见 §六-2） |
| 8 | **3 个 model mixin 的跳过集合** | 1.21 用 `TerraEntity.MODID`，1.20 用 `TerraFurniture.MODID` | 同上：模型加载豁免面变化 | 同批一起改，并确认 TerraFurniture 是否需要豁免 |
| 9 | **`mixin/**` 的 14 文件差集与退役交叉** | `resources/RegistryDataLoaderMixin`（1.21 独有）↔ 1.20 的 `resources/RegistryDataLoader$RegistryDataMixin`（R4 清单）；前者唯一作用就是**过滤 TE 命名空间的 biome modifier** | 直接删前者会丢掉 1.20 的「THE_END_BIOMES 维度类型改写」钩子；直接搬后者会与前者冲突 | 两者**互换**并同改 `mixins.json:32-33`（§一 1.3） |
| 10 | **TE 内容消失** | §二/§三 里 5 条「TE 独有」内容：`honey_slime`、`wallet`（有 1.20 等价物）、`house_detector`、`debug_item`、`figure{1,2,3}`；以及 `ISummonMob`/`IMinion` 体系外的 `summon_*_sword` 语言键 | 已发布的 1.21 版本玩家可能已拥有这些物品 → 删注册会让存档里的物品变成「空气」/丢失 | 逐项按 §三 3.3 的表决：能改名的改名（wallet），其余要么先保留 TE 注册（延后），要么接受破坏（需你明确） |
| 11 | **`SUMMON` 伤害类型** | 1.21 库的 `LibDamageTypes` 缺 `SUMMON`（只有 `SUMMONER`） | 若直接写 `LibDamageTypes.SUMMON` 会**编译失败**；若改用 `SUMMONER` 则是**行为改变**（不同伤害类型，影响 `ModDamageTypeTagsProvider` 的 `IS_MAGIC`/`AS_MELEE_ATTACK` 标签） | 需你裁决：改库/在 `ModDamageTypes` 自注册 `summon`/改用 `SUMMONER` 三选一 |
| 12 | **工作树正被并行批次改动** | 会话内 `WhipAttackEntity`、`ModChineseProvider`、`ModItemTagsProvider`、`ModEnchantments`、`SummonItems` 都被改过 | 按旧计数施工会漏/重 | 每批开工前重跑 `te_refs.py` + `te_repoint.py` dry-run |

---

## §六 与 1.20 的语义差异（决定「纯改名」还是「真变更」）

| # | 位置 | 1.21 现状（TE 支撑） | 1.20 形态 | 性质 |
|---|---|---|---|---|
| 1 | `BossSummoningItem` / `ModUtils.summonBoss` | `TEUtils.internalSpawnEntity(mob, level)` 是 TE Boss 的**额外初始化**，成功后才 `addFreshEntityWithPassengers` | 直接 `bindSummoner(player, mob)` + `addFreshEntityWithPassengers`，无 internalSpawnEntity | **行为差异**（TE 侧多一步初始化）。按 1.20 删掉后，TE 实体相关的召唤初始化丢失——若那些实体已换成主模组实现则无影响，**建议逐 Boss 实测** |
| 2 | `ModUtils.CONFLUENCE_NAMESPACES` | `{Confluence, TerraCurio, TerraEntity}` | `{Confluence, TerraCurio, TerraFurniture}` | **行为差异**（TerraFurniture 资源从「自家」变「别家」；同时 TE 退出）。`ClientConfigs:339` 依赖它 |
| 3 | `ExtraInventory.initialize()` | 额外把 `pet`/`light_pet`/`mount` 三个 curios 槽接进 `equipment`（TE `CuriosHelper` 的键） | 只做 `updateAccessorySize`，没有这段 | **行为差异**：删掉后宠物/坐骑槽可能不再随 curios 同步（需实测；见 §三 3.3） |
| 4 | `EntitySubProvider` 战利品表 | 显式 key `confluence:entities/terra_entity/<id>`（118 处） | **不写显式 key**，由框架从实体类型派生（等价 `confluence:entities/<id>`） | **行为差异/潜在既有 bug**：两套 id 不一致 → 退役时必须改，改完要实测掉落 |
| 5 | `AbstractTerraNPC` vs `BaseNPC` | TE 的 NPC 基类 | 主模组 `BaseNPC`（两边同名类都存在，但 TE 的世界实体是另一套注册） | 纯改名（`instanceof` 判定对象变了 → 退役后只剩一种 NPC，语义反而更正确） |
| 6 | `PrefixUtils.getReforgeCost` | 按 `holder.getMood().getValue()` 调整价格（TE 交易持有者） | 该段是 `// todo trade` 注释 → 无心情修正 | **行为差异**：重铸价格回到 1.20 的 `price/3` |
| 7 | `PlayerSpecialData` 任务鱼条件 | `ITradeLock` 条件 + `ITradeHolder.dummy(player)` | 1.20 无此字段（只有 `completedAnglerQuestDay`/`anglerQuestCount`） | **1.21 独有特性**：按 1.20 删 = 功能消失（渔夫任务鱼的条件判定） |
| 8 | 效果 `SUMMON_FOCUS`（id `summon_mark`） | TE 注册的「狩猎」效果 | 无（「召唤印记」由附件 `SummonerAttachmentTypes.SUMMON_MARK_DATA` 承担，两边一致） | 纯删除（1.20 只有 lang 遗留键） |
| 9 | `DemonEyeVariant` vs `DemonEye.Variant` | TE 维护 15 个变体常量 + 自己的 helper `demonEyeVariant(...)` | 原生 `DemonEye.Variant`（14 个）+ `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.X, …)` | **近似改名**：变体名一致，但**编号/顺序不同**（1.21 从 13900 起递推、1.20 从 14000 且成对同号）→ 图鉴 `order` 会变，属可接受的外观差异 |
| 10 | `EaterOfWorlds` 构造 | `new EaterOfWorlds(level, true)` | `new EaterOfWorlds(BossEntities.EATER_OF_WORLDS.get(), level)` | 构造签名差异（TE 版多一个 bool），改指时要改实参 |
| 11 | `HORNET_STAFF` / `WALLET` / `SUMMON_BEE_STICK_PROJ` | TE 物品/弹幕 | `NEW_HORNET_STAFF`（id `new_hornet_staff`）/ `MONEY_TROUGH`（id `money_trough`）/ 不存在 | **真变更**：物品 id 变化 → 已有存档里的旧物品失效，需别名或接受破坏 |
| 12 | `AbstractBufferManager` / `SpitParticle` | 用 TE 的实现 | 主模组自己的同名实现（110 / 76 行） | 纯搬运（两代设计相同：都是 `abstract class … { public AbstractBufferManager(int refreshTime) }` 形态） |

---

## 附：待你决策的清单（短版）

| # | 一句话问题 | 我的建议 |
|---|---|---|
| A1 | `LibDamageTypes` 缺 `SUMMON`（1.20 有、1.21 库只有 `SUMMONER`）：改库补 / 在 `ModDamageTypes` 自注册 `summon` / 改用 `SUMMONER`？ | 在 `ModDamageTypes` 自注册 `summon`（不动另一个子模块），伤害类型标签同批补 |
| A2 | `mixin/integration/touhoulittlemaid/EntityMaidMixin`（女仆商店，1.20 无此集成）删掉还是自研原生交易持有者？ | 删 mixin + 同步 `mixins.json:26`；自研另立项目 |
| A3 | `PlayerSpecialData` 的「任务鱼条件」（`ITradeLock`，1.20 无）保留还是删？ | 删（对齐 1.20），要保留就用原生 `TradeCondition` 另立条目 |
| A4 | `PrefixUtils` 的「按 NPC 心情调重铸价」（1.20 是 `todo` 注释）保留还是删？ | 删（回到 `price/3`） |
| A5 | `ExtraInventory.initialize()` 的 3 行 curios 槽同步（1.20 没有）删掉后会不会让宠物/坐骑槽变空？ | 先删再实测；若真空，只本地化 3 个字符串常量 |
| A6 | 5 条 TE 独有内容（`honey_slime` / `house_detector` / `debug_item` / `figure{1,2,3}` / `summon_*_sword` 语言键）——删引用（内容消失）还是先从 TE 移植？ | 删引用 + 另立「TE 内容移植」项目；`wallet` 不在此列（已有 1.20 等价物 `MONEY_TROUGH`） |
| A7 | `ModUtils.CONFLUENCE_NAMESPACES` 与 3 个 model mixin 的跳过集合：照 1.20 把 `TerraEntity` 换成 `TerraFurniture`？ | 是（两处同批），并确认 TerraFurniture 是否真的需要豁免 |
| A8 | `IEFfectStrategy`/`ICollisionAttackEntity`/`IAttackableProjectile` 三件套随 WP6-B 剑/矛层替换一起做，还是单独一批？ | 跟 WP6-B 同批（都在 `SpearProjectile`，拆开会来回改同一文件） |
| A9 | 图鉴进度（`bestiary.entity.terra_entity.*` 键）在存档里怎么存？要不要做键迁移？ | 需要你/我实测一次「图鉴进度持久化点」再定；这是 §五-2 唯一未验证项 |
| A10 | `EntitySubProvider` 118 处显式 loot key 前缀 `entities/terra_entity/`（1.20 无显式 key）——直接对齐 1.20 并实测掉落？ | 是，SB-1 内做，出口加一条「击杀 N 种怪验证掉落」 |
