# 1.21 实体掉落表补齐 —— 最终批次计划（按 5 条裁定重排）

**修订锚点**：1.21 = `b1677a5d5b30064d4c0a92b64f4592345f03c177`；1.20 = `07c2ab5b598d13688f50b6f6617937ed99ea3f68`
**数据来源**：`notes/_tmp_lootverify/final2.txt`（逐实体表）与本次核查结论；本文件仅重排/补齐，未重跑核查。
**范围**：94 个 `NO-LOOT`（1.21 已注册、无表）= 56 有内容 + 38 空表；另有 **7 个火星实体**（原 `NOT-APPLICABLE`）按裁定 2 升格为独立实体批。
**已完成、不得重做**：`b1677a5d5` 已补 10 个（史莱姆族 5 + 海盗 5，另 `530ff7116` 补 giant_tortoise）。

---

## 0. 跨批次共用前置（放在第几批 —— 明确答案）

| 前置项 | 内容 | 放在第几批 | 影响范围 / 依据 |
|---|---|---|---|
| **① 共用 import** | 在 `EntitySubProvider.java` import 区加一行：<br>`import org.confluence.mod.common.loot.DifficultyChanceLootItemCondition;` | **批次 3（海洋）** —— 这是**首次需要**它的批次 | 批次 3/4/5/6 共 7 个条目使用：ANGLER_FISH(批3)、GIANT_BAT/ARMORED_SKELETON/BLACK_RECLUSE(批4)、MOSS_HORNET(批5)、WEREWOLF/UNICORN(批6)。批次 1/2 不需要（小动物族与蚂蚁狮族无 `[难度概率]` 条目） |
| **② 唯一类名搬家** | `ModBlocks.CURSED_FLAME` → `MaterialItems.CURSED_FLAME` | **批次 6（腐化/猩红）** —— 这是**唯一需要**它的批次 | 仅 2 个条目用到：CLINGER(1.20 `:552-554`)、WORLD_FEEDER(1.20 `:639-644`)，两者都属腐化族。1.21 现成模板：`EntitySubProvider.java:1141`（`LootItem.lootTableItem(MaterialItems.CURSED_FLAME)` + `SetItemCountFunction`）；1.20/1.21 的 `CursedFlameBlock.java:35/37` 正好印证 `ModBlocks.CURSED_FLAME.asItem()` → `stack.is(MaterialItems.CURSED_FLAME.get())` |

**结论一句话**：① 放 **批次 3** 开头（一次加、后面 4/5/6 直接受益），② 放 **批次 6**（也只有批次 6 用得上）。

**核查已确认、无需再动的写法**（94 个条目全部适用，不必逐条改造）：
- 物品常量**不需要** `.get()`（1.20 裸用写法照抄即可；`MaterialItems.GEL` 与 `SPIDER_FANG` 同为 `DeferredItem<Item>`，GEL 在 1.21 已裸用 `:1304`）；
- `EmptyLootItem` / `LootItem.lootTableItem` / `UniformGenerator` / `SetItemCountFunction` / `SmeltItemFunction` / `shouldSmeltLoot()` / `random0To1` / `random3To4` / `count1To2` / `count2To5` / `emptyWeight98` / `hearts` 全部原样可用（1.21 局部声明 `:59/:60/:61/:68/:69/:71`）；
- `batCommon()`(`:1280`) / `goblinCommon()`(`:1343`) / `slimeCommon()`(`:1292`) / `corruptionSlimeLoot()`(`:1314`) / `pirateCommon()`(`:1333`) 已存在，可直接调用。

---

## 批次 1 —— G 小动物/暴击动物族（15 个：6 有内容 + 9 空表→**移入批次 9**）

- **1.20 行号范围**：内容条目 `:878-892`、`:917-922`、`:935-940`
- **本批条目（6）**：

| 实体 | 1.21 id | 1.20 行 | 摘要 |
|---|---|---|---|
| `CritterEntities.CLOUD_SHEEP` | `cloud_sheep` | 878-880 | 羊肉 x1-2 熟化 |
| `CritterEntities.GLOWING_MOOSHROOM` | `glowing_mooshroom` | 881-884 | 皮革 x0-2 \| 牛肉 x1-3 熟化 |
| `CritterEntities.CLUCKSHROOM` | `cluckshroom` | 885-888 | 羽毛 x0-2 \| 鸡肉 熟化 |
| `CritterEntities.GLOWING_CLUCKSHROOM` | `glowing_cluckshroom` | 889-892 | 羽毛 x0-2 \| 鸡肉 熟化 |
| `CritterEntities.RED_SQUIRREL` | `red_squirrel` | 917-922 | 生松鼠肉 熟化 +looting0-1 |
| `CritterEntities.EXPLOSIVE_BUNNY` | `explosive_bunny` | 935-940 | 兔肉 熟化 +looting0-1 |

- **要照抄的注释**：无
- **适配点**：无（纯 vanilla 掉落 + `SmeltItemFunction`）
- **1.21 落点**：追加在小动物族现有条目附近（同族 `CritterEntities` 段内）
- **说明**：本族 9 个空表（HOSTILE_BUNNY 947 / PENGUIN 948 / MYSTIC_FROG 949 / GOLDFISH 950 / STINKBUG 954 / FIREFLY 955 / TRUFFLE_WORM 956 / BUGGY 957 / LIGHTNING_BUG 958）**按裁定 1 移入批次 9**

---

## 批次 2 —— A 蚂蚁狮/沙漠族（4 个：4 有内容，0 空表）

- **1.20 行号范围**：`:112-126` + `:1340-1341`
- **本批条目（4）**：

| 实体 | 1.21 id | 1.20 行 | 摘要 |
|---|---|---|---|
| `MonsterEntities.ANTLION` | `antlion` | 112-115 | 蚂蚁狮下颚 x1-2 空w2 \| 香蕉船 w2 + emptyWeight98 |
| `MonsterEntities.ANTLION_LARVA` | `antlion_larva` | 116-118 | 蚂蚁狮下颚 空w5 |
| `MonsterEntities.ANTLION_CHARGER` | `antlion_charger` | 119-126 | 下颚 x1-2 空w2 \| 香蕉船 w2 \| 颚骨刃 w2 |
| `MonsterEntities.BASILISK` | `basilisk` | 1340-1341 | 坚固化石 x1-3 |

- **要照抄的注释**：
  - `1.20:1339` 蛇蜥怪 → 「缺少未加入物品：远古号角(Ancient Horn, 2%)（坚固化石已实现）」
- **适配点**：无（`count1To2`/`emptyWeight98` 均在 1.21 `:68/:71`）
- **1.21 落点**：紧邻 `MonsterEntities.ANTLION_SWARMER`(`:112`) / `GIANT_ANTLION_SWARMER`(`:126`)；BASILISK 同段追加

---

## 批次 3 —— B 海洋/鲨鱼/人鱼族（13 个：9 有内容 + 4 空表→**批次 9**）

- **1.20 行号范围**：`:240-242`、`:435-439`、`:499-505`、`:762-785`、`:839-840`
- **★ 本批开头加共用前置 ① 的 import**（首次需要）
- **本批条目（9）**：

| 实体 | 1.21 id | 1.20 行 | 摘要 |
|---|---|---|---|
| `MonsterEntities.WATER_BOLT_MIMIC` | `water_bolt_mimic` | 240-242 | 水矢 空w39 |
| `MonsterEntities.ICY_MERMAN` | `icy_merman` | 435-439 | 奶昔 +looting0-1 空w74 |
| `MonsterEntities.ZOMBIE_MERMAN` | `zombie_merman` | 499-502 | 吸血鬼青蛙法杖 空w7 \| 鱼饵桶 空w7 \| 血泪 空w24 |
| `MonsterEntities.WANDERING_EYE_FISH` | `wandering_eye_fish` | 503-505 | 吸血鬼青蛙法杖 空w7 |
| `MonsterEntities.ANGLER_FISH` | `angler_fish` | 765-769 | 机器人帽 空w249 \| 粘性绷带 **[难度概率 0.01/0.0199]** |
| `MonsterEntities.SAND_SHARK` | `sand_shark` | 771-773 | 鲨鱼鳍 空w7 \| 玉米片 空w29 |
| `MonsterEntities.BONE_BITER` | `bone_biter` | 774-777 | 鲨鱼鳍 空w7 \| 玉米片 空w29 \| 暗影碎块 空w24 |
| `MonsterEntities.FLESH_REAVER` | `flesh_reaver` | 778-781 | 鲨鱼鳍 空w7 \| 玉米片 空w29 \| 暗影碎块 空w24 |
| `MonsterEntities.CRYSTAL_THRESHER` | `crystal_thresher` | 782-785 | 鲨鱼鳍 空w7 \| 玉米片 空w29 \| 光明碎块 空w24 |

- **要照抄的注释**：
  - `1.20:434` 冰雪鱼人 → 「缺少未加入物品：冰雪镰刀(Ice Sickle, 1%)、寒冰法杖(Frost Staff, 2%)」
  - `1.20:498` 僵尸鱼人 → 「缺少未加入物品：血雨弓(Blood Rain Bow, 12.5%)、钱币槽(Money Trough, 6.67%)、鱼饵桶(Chum Bucket, 50%)」
  - `1.20:764` 琵琶鱼 → 「缺少未加入物品：机器人帽(Robot Hat, 0.4%)（粘性绷带已实现）」
  - `1.20:770` 沙鲨/腐化沙鲨/猩红沙鲨/神圣沙鲨 → 「缺少未加入物品：沙鲨风筝(Sand Shark Kite, 4%)」（**族注释，覆盖 4 个变体**）
- **适配点**：1 处 —— ANGLER_FISH 需前置 ① 的 import（本批开头加一次即可）
- **补充**：`SummonItems.VAMPIRE_FROG_STAFF` 在 1.21 **存在**（`SummonItems.java:109`），ZOMBIE_MERMAN/WANDERING_EYE_FISH 直接照抄
- **1.21 落点**（**已按 2026-10 用户裁定修正**）：本批实际拆成 **4 个锚点**，保持 1.20 的相对顺序与邻接关系 ——
  组1 `WATER_BOLT_MIMIC` → `DARK_CASTER` 之前；组2 `ICY_MERMAN` → `CRIMERA` 之前；
  组3 `ZOMBIE_MERMAN`/`WANDERING_EYE_FISH` → `DEVOURER` 之前；组4（琵琶鱼 + 4 条鲨鱼）→ `SHARK` 之前。
  （原稿此处写「紧邻 `PIRANHA`」；用户裁定「保持现状」，即以 1.20 邻接关系为准，全局行号 240 < 435 < 499 < 765 与 1.20 一致。）

---

## 批次 4 —— C 骷髅/地牢族（19 个：12 有内容 + 7 空表→**批次 9**）

- **1.20 行号范围**：`:99-110`、`:340-362`、`:524-526`、`:646-653`、`:1432-1444`
- **本批条目（12）**：

| 实体 | 1.21 id | 1.20 行 | 摘要 |
|---|---|---|---|
| `MonsterEntities.WALL_CREEPER` | `wall_creeper` | 102-104 | 煎蛋 空w29 |
| `MonsterEntities.BLACK_RECLUSE` | `black_recluse` | 106-110 | 蜘蛛牙 x1-3 **[难度概率 0.5/0.9]** \| 煎蛋 空w29 |
| `MonsterEntities.GIANT_BAT` | `giant_bat` | 340-342 | helper `batCommon()` + 三折地图 **[难度概率 0.01/0.0199]** |
| `MonsterEntities.ARMORED_SKELETON` | `armored_skeleton` | 345-347 | 护甲抛光剂 **[难度概率 0.01/0.0199]** |
| `MonsterEntities.ROCK_GOLEM` | `rock_golem` | 349-350 | `Items.STONE` x10-20 |
| `MonsterEntities.ILLUMINANT_BAT` | `illuminant_bat` | 357-359 | 苹果派 空w149 \| 蝙蝠棍 空w299 |
| `MonsterEntities.LAVA_BAT` | `lava_bat` | 360-362 | 岩浆石 空w49 \| 蝙蝠棍 空w299 |
| `MonsterEntities.DUNGEON_SPIRIT` | `dungeon_spirit` | 524-526 | 灵质 x1-2 |
| `MonsterEntities.TIM` | `tim` | 648-650 | 巫师帽 |
| `MonsterEntities.DOCTOR_BONES` | `doctor_bones` | 651-653 | 考古学家帽 |
| `MonsterEntities.PALADIN` | `paladin` | 1433-1436 | 圣骑士盾 空w14 |
| `MonsterEntities.BONE_LEE` | `bone_lee` | 1437-1440 | 黑带 空w11 |

- **要照抄的注释**：
  - `1.20:105` 黑隐士 → 「缺少未加入物品：毒刺法杖(Poison Staff, 2.5%)」
  - `1.20:343` 巨型蝙蝠 → 「缺少未加入物品：深度计(Depth Meter, 1%)（三折地图已实现）」
  - `1.20:344` 装甲骷髅 → 「缺少未加入物品：光束剑(Beam Sword, 0.67%)」
  - `1.20:348` 岩石巨人 → 「缺少未加入物品：岩石巨人头(Rock Golem Head, 33.33%)」
  - `1.20:356` 夜明蝙蝠 → 「缺少未加入物品：结晶(Crystallize, 0.67%)（苹果派、蝙蝠棍已实现）」
- **适配点**：3 处需要 `[难度概率]` ⇒ 依赖批次 3 已加的 import；本批无需再加
- **本批移出到批次 9 的空表（7）**：JUNGLE_CREEPER 99、DESERT_SPIRIT 101、RUNE_WIZARD 646、ENCHANTED_SWORD 1432、NECROMANCER 1442、DIABOLIST 1443、RAGGED_CASTER 1444
  - ⚠️ `ENCHANTED_SWORD` 按裁定 4 落 `confluence:entities/enchanted_sword_monster.json`（1.21 注册 id 带 `_monster` 后缀，**不回头改 id**）
- **1.21 落点**：蝙蝠族紧邻 `CAVE_BAT:307` / `SPORE_BAT:309` / `HELL_BAT:536` / `ICE_BAT:588` / `JUNGLE_BAT:603`（GIANT_BAT 模板 = `:588-593`）；其余同段追加

---

## 批次 5 —— F 雪原/杂项怪物族（19 个：13 有内容 + 6 空表→**批次 9**）

- **1.20 行号范围**：`:389-393`、`:425-450`、`:632-696`、`:721-724`、`:827-830`、`:959-1000`
- **本批条目（13）**：

| 实体 | 1.21 id | 1.20 行 | 摘要 |
|---|---|---|---|
| `MonsterEntities.ZOMBIE` | `zombie` | 389-393 | 腐肉 x1-2 +looting0-1 |
| `MonsterEntities.ICE_GOLEM` | `ice_golem` | 425-427 | 霜核 |
| `MonsterEntities.ARMORED_VIKING` | `armored_viking` | 429-433 | 罗盘 空w99 |
| `MonsterEntities.ICE_TORTOISE` | `ice_tortoise` | 443-450 | 冰冻陆龟壳 w2 空w98 \| 奶昔 +looting0-1 空w74 |
| `MonsterEntities.DIGGER` | `digger` | 632-637 | 放屁坐垫 w2 + emptyWeight98 |
| `MonsterEntities.ANGRY_TUMBLER` | `angry_tumbler` | 689-690 | 玉米片 空w29 |
| `MonsterEntities.RED_DEVIL` | `red_devil` | 695-696 | 热狗 空w29 |
| `MonsterEntities.MOSS_HORNET` | `moss_hornet` | 721-724 | 蜂刺 空w5 \| 牛黄 **[难度概率 0.01/0.0199]** |
| `MonsterEntities.GOBLIN_WARLOCK` | `goblin_warlock` | 827 | helper `goblinCommon()` |
| `MonsterEntities.ANGRY_NIMBUS` | `angry_nimbus` | 959-961 | 雨云魔杖 空w14 |
| `MonsterEntities.ANGRY_DANDELION` | `angry_dandelion` | 963-968 | 日光花 x1-2 空 |
| `MonsterEntities.GRANITE_GOLEM` | `granite_golem` | 970-977 | 花岗岩 x5-10 \| 意大利面 w2 + emptyWeight98 |
| `MonsterEntities.HOPLITE` | `hoplite` | 988-1000 | 披萨 w2 \| 标枪 x40-80 \| 角斗士四件套 空w18 \| 短剑 w5/空w95 \| 钩爪 w4/空w96 |

- **要照抄的注释**：
  - `1.20:424` 冰雪巨人 → 「缺少未加入物品：冰雪羽(Ice Feather, 33.3%)」
  - `1.20:428` 装甲维京海盗 → 「缺少未加入物品：冰雪镰刀(Ice Sickle, 1%)（罗盘已实现）」
  - `1.20:442` 冰雪陆龟 → 「缺少未加入物品：冰雪镰刀(Ice Sickle, 1%)（龟壳、奶昔已实现）」
  - `1.20:631` 挖掘怪 → 「缺少未加入物品：怪物肉(Monster Meat, 0.07%)、可疑苹果(Suspicious Looking Apple, 40%)」
  - `1.20:694` 红魔鬼 → 「缺少未加入物品：火焰羽(Fire Feather, 2%)、烈火之花(Flower of Fire, 3.33%)（热狗、邪恶三叉戟已实现）」
  - `1.20:720` 青苔黄蜂 → 「缺少未加入物品：破碎蜂翼(Tattered Bee Wing, 1%)（蜂刺、牛黄已实现）」
  - `1.20:826` 哥布林术士 → 「缺少未加入物品：暗影焰弓(Shadowflame Bow, 33.3%)、暗影焰巫术娃娃(Shadowflame Hex Doll, 33.3%)、暗影焰刀(Shadowflame Knife, 33.3%)」
  - `1.20:969` 花岗岩巨人 → 「缺少未加入物品：晶洞(Geode, 5%)、夜视头盔(Night Vision Helmet, 3.3%)、响石(Snapping Stone, 1.25%)（花岗岩、意大利面已实现）」
  - `1.20:987` 装甲步兵 → 「缺少未加入物品：压力球(Stress Ball, 1%)、角斗士胸甲(Gladiator Breastplate, 4.76%)（标枪、角斗士头盔/护腿、短剑、钩爪、披萨已实现）」
- **适配点**：1 处需要 `[难度概率]`（MOSS_HORNET）⇒ 依赖批次 3 已加的 import；`GOBLIN_WARLOCK` 直接用现成 `goblinCommon()`（1.21 `:1343`，已有 6 个使用者 `:651-661`）
- **本批移出到批次 9 的空表（6）**：ICE_ELEMENTAL 441、WINDY_BALLOON 691、OLD_SHAKING_CHEST 692、CLUMSY_BALLOON_SLIME 693、SHADOWFLAME_APPARITION 828、GNOME 830
- **1.21 落点**：哥布林族紧邻 `:651-661`；僵尸 `TCItems.COMPASS` 池可对照 `:596-598`

---

## 批次 6 —— E 腐化/猩红/肉山后族（20 个：11 有内容 + 9 空表→**批次 9**）

- **1.20 行号范围**：`:300-310`、`:352-355`、`:527-531`、`:549-554`、`:639-644`、`:952-953`、`:1157-1159`、`:1397-1445`
- **★ 本批落唯一类名搬家 ②**（`ModBlocks.CURSED_FLAME` → `MaterialItems.CURSED_FLAME`）
- **本批条目（11）**：

| 实体 | 1.21 id | 1.20 行 | 摘要 |
|---|---|---|---|
| `MonsterEntities.THE_BRIDE` | `the_bride` | 300-302 | 血泪 空w4 |
| `MonsterEntities.THE_GROOM` | `the_groom` | 304-310 | 高顶礼帽 \| 血泪 空w4 |
| `MonsterEntities.WEREWOLF` | `werewolf` | 352-355 | 月之护符 空w59 \| 粘性绷带 **[难度概率 0.01/0.0199]** |
| `MonsterEntities.SWEET_SLIME` | `sweet_slime` | 527-531 | 蜂蜜软糖 x5-7 +looting3-4 |
| `MonsterEntities.CLINGER` | `clinger` | 552-554 | **诅咒焰 x2-5（②类名搬家）** \| 绞肉机 空w199 |
| `MonsterEntities.WORLD_FEEDER` | `world_feeder` | 639-644 | **诅咒焰 x2-5（②类名搬家）** |
| `MonsterEntities.GIANT_FLYING_FOX` | `giant_flying_fox` | 1397-1401 | helper `batCommon()` + 葡萄 空w39 |
| `MonsterEntities.CORRUPTOR` | `corruptor` | 1402-1411 | 腐肉块 w33 q1 +looting0-1 空w67 \| 维生素 w2 空w98 |
| `MonsterEntities.UNICORN` | `unicorn` | 1415-1418 | 独角兽角 \| 祝福苹果 **[难度概率 1/40, 1/30]** |
| `MonsterEntities.GASTROPOD` | `gastropod` | 1419-1423 | 凝胶 x5-10 +looting0-1（**无颜色组件**，不涉及 `setGelColor` 改造） |
| `MonsterEntities.CHAOS_ELEMENTAL` | `chaos_elemental` | 1425-1431 | 苹果派 w67 空w9933 +looting0-1 |

- **要照抄的注释**：
  - `1.20:299` 僵尸新娘 → 「缺少未加入物品：婚纱(Wedding Dress, 100%)」
  - `1.20:303` 僵尸新郎 → 「缺少未加入物品：大脑(Brain, 75%)（高顶礼帽、血泪已实现）」
  - `1.20:351` 狼人 → 「缺少未加入物品：狼牙(Wolf Fang, 1.5%)」
  - `1.20:551` 爬藤怪 → 「缺少未加入物品：怪物肉(Monster Meat, 0.07%)、生命吞噬者(Eater Of Life, 0.67%)（诅咒焰已实现，但缺专家掉率提升）」
  - `1.20:638` 吞世怪 → 「缺少未加入物品：怪物肉(Monster Meat, 0.07%)、生命吞噬者(Eater Of Life, 0.67%)、吞世怪风筝(World Feeder Kite, 4%)、可疑苹果(Suspicious Looking Apple, 45%)」
  - `1.20:1424` 混沌精 → 「缺少未加入物品：混乱之杖(Rod of Discord, 0.25%)（苹果派已实现，需要补 0.67% 概率）」
  - `1.20:1426` **行内注释** `// 混沌传送杖`（写在 `add(...)` 与 `.withPool(...)` 之间，位置固定，照抄）
- **适配点**：2 处 ② 类名搬家（CLINGER / WORLD_FEEDER）+ 2 处依赖批次 3 的 import（WEREWOLF / UNICORN）
- **本批移出到批次 9 的空表（9）**：FUNGI_BULB 549、GIANT_FUNGI_BULB 550、CORRUPT_PENGUIN 952、VICIOUS_PENGUIN 953、BLOOD_JELLY 1157、FUNGO_FISH 1159、SLIMER 1412、BLOOD_FEEDER 1414、ARCH_WYVERN 1445
- **1.21 落点**：`ghoulCommon()`(`:1207`) / `mummyCommon()`(`:1215`) 段落附近；诅咒焰模板 `:1141`

---

## 批次 7 —— H Boss/NPC 零头（3 个：1 有内容 + 2 空表→**批次 9**）

- **1.20 行号范围**：`:856-861`（空表 `:80-81` 移入批次 9）
- **本批条目（1）**：

| 实体 | 1.21 id | 1.20 行 | 摘要 |
|---|---|---|---|
| `NpcEntities.STYLIST` | `stylist` | 856-861 | 时尚剪刀 空w7 |

- **要照抄的注释**：无
- **适配点**：无
- **本批移出到批次 9 的空表（2）**：THE_DESTROYER_PROBE 80、LUNATIC_CULTIST_CLONE 81

---

## 批次 8 —— 火星内容批（**裁定 2：实体待移植，整批搬**）

- **性质**：本批 = **实体 + 资源 + 掉落表一起落**，不再是"只补掉落表"。原 `NOT-APPLICABLE` 7 个按裁定 2 升格为独立批次。
- **1.20 掉落表行号范围**：`:842-849`（连续 8 行，`MARTIAN_PROBE:842` 之后 7 行）
- **本批条目（7 个实体 + 1 个已存在实体）**：

| 实体 | 1.20 注册 id | 1.20 行 | 1.20 掉落表 | 备注 |
|---|---|---|---|---|
| `MonsterEntities.MARTIAN_PROBE` | `martian_probe` | 842 | 空表 | **1.21 已注册该实体**（掉落表属批次 9 的空表清单） |
| `MonsterEntities.MARTIAN_ENGINEER` | `martian_engineer` | 843 | 空表 | 1.20 `MonsterEntities.java:766` 标 `DevelopmentSpawnPolicy.developmentOnly` |
| `MonsterEntities.MARTIAN_OFFICER` | `martian_officer` | 844 | 空表 | `MonsterEntities.java:778` |
| `MonsterEntities.MARTIAN_WALKER` | `martian_walker` | 845 | 空表 | `MonsterEntities.java:780` |
| `MonsterEntities.WALKER_WEAPON` | `martian_walker_weapon` ⚠️id 带 `martian_` 前缀 | 846 | 空表 | `MonsterEntities.java:782`，标 `developmentOnly` |
| `MonsterEntities.TESLA_TURRET` | `tesla_turret` | 847 | 空表 | `MonsterEntities.java:768`，标 `developmentOnly` |
| `MonsterEntities.RAY_GUNNER` | `ray_gunner` | 848 | 空表 | `MonsterEntities.java:770` |
| `MonsterEntities.SCUTLIX` | `scutlix` | 849 | 空表 | `MonsterEntities.java:774` |

- **要照抄的注释**：无（1.20 `:842-849` 整段无注释）
- **适配点**：不能只搬掉落表——须先落实体注册 + 资源（8 张贴图 + 7 geo + 7 animation + 音效 + lang，对应台账新行 **387/391/394** 与 **393 的 `ParticleAccessor`**，已由主代理核实齐备）
- **掉落表落点说明**：这 8 条在 1.20 **全部是空表**，所以本批的掉落表成本为 0，但**不应遗漏**：
  - `MARTIAN_PROBE`（1.21 已有实体）→ 归 **批次 9** 空表清单；
  - 其余 7 个 → **建议随实体一起落空表**（避免出现"实体已存在但无表"的中间态）；若为保持批次一致性而推迟到批次 9，**必须在批次 9 清单里显式列出这 7 个**。
- **位置**：排在批次 7 之后、批次 9 之前（唯一符合裁定 5"空表最后"的插入点）

---

## 批次 9 —— 38 个 1.20 空表（**裁定 1：排在所有有内容批次之后**）

- **性质**：全部是 `LootTable.lootTable()`（无 pool）。补齐**不改变掉落行为**，作用是让 `confluence:entities/<id>` 表存在（`getLootTable()` 不为 null、EMI/REI 可显示、与 1.20 语义一致）。
- **1.20 行号**：见下表（分散，不连续）
- **本批条目（38）**：

| 族 | 实体与 1.20 行号 |
|---|---|
| A 蚂蚁狮/沙漠 | （无） |
| B 海洋 | `CORRUPT_GOLDFISH` 762、`VICIOUS_GOLDFISH` 763、`PIRATE_PARROT` 839、`PIRATES_CURSE` 840（4） |
| C 骷髅/地牢 | `JUNGLE_CREEPER` 99、`DESERT_SPIRIT` 101、`RUNE_WIZARD` 646、`ENCHANTED_SWORD` 1432、`NECROMANCER` 1442、`DIABOLIST` 1443、`RAGGED_CASTER` 1444（7） |
| D 火星 | `MARTIAN_PROBE` 842（1） |
| E 腐化/猩红 | `FUNGI_BULB` 549、`GIANT_FUNGI_BULB` 550、`CORRUPT_PENGUIN` 952、`VICIOUS_PENGUIN` 953、`BLOOD_JELLY` 1157、`FUNGO_FISH` 1159、`SLIMER` 1412、`BLOOD_FEEDER` 1414、`ARCH_WYVERN` 1445（9） |
| F 雪原/杂项 | `ICE_ELEMENTAL` 441、`WINDY_BALLOON` 691、`OLD_SHAKING_CHEST` 692、`CLUMSY_BALLOON_SLIME` 693、`SHADOWFLAME_APPARITION` 828、`GNOME` 830（6） |
| G 小动物 | `HOSTILE_BUNNY` 947、`PENGUIN` 948、`MYSTIC_FROG` 949、`GOLDFISH` 950、`STINKBUG` 954、`FIREFLY` 955、`TRUFFLE_WORM` 956、`BUGGY` 957、`LIGHTNING_BUG` 958（9） |
| H Boss/NPC | `THE_DESTROYER_PROBE` 80、`LUNATIC_CULTIST_CLONE` 81（2） |
| **合计** | **38** |

- **要照抄的注释（属空表实体自身，务必随本批搬运，否则欠账丢失）**：
  - `1.20:100` 沙漠幽魂 → 「缺少未加入物品：沙漠幽魂灯(Desert Spirit Lamp, 2.5%)、神灯诅咒(Djinn's Curse, 3.25%)、生命吞噬者(Eater Of Life, 0.67%)」
  - `1.20:440` 冰雪精 → 「缺少未加入物品：冰雪镰刀(Ice Sickle, 1%)、寒冰法杖(Frost Staff, 2%)」
  - `1.20:645` 符文法师 → 「缺少未加入物品：符文帽(Rune Hat, 100%)、符文长袍(Rune Robe, 100%)」
  - `1.20:951` 腐化企鹅/猩红企鹅 → 「缺少未加入物品：Pedguin 套装（兜帽/夹克/裤子，各 0.67%）」
  - `1.20:1156` 血水母 → 「缺少未加入物品：怪物肉(Monster Meat, 0.07%)」
  - `1.20:1441` 死灵法师 → 「缺少未加入物品：暗影束法杖(Shadowbeam Staff, 9.75%)」
- **适配点**：无（空表不引用任何物品/条件，**不需要**批次 3 的 import，也**不需要**批次 6 的类名搬家）
- **⚠️ 需显式列表**：若批次 8 的 7 个火星实体掉落表推迟到本批，本批条目数将变为 **45**（38 + 7）。建议不推迟（见批次 8）。
- **1.21 落点**：按族追加到各段末尾即可（批量一次性提交，可整批一 commit）

---

## 汇总表

| 批次 | 族 | 1.20 行号范围 | 条目数 | 有内容 | 空表(留在本批) | 注释条数 | 适配点 |
|---|---|---|---|---|---|---|---|
| 1 | G 小动物/暴击动物 | `:878-892, 917-922, 935-940` | 15 | 6 | 0 | 0 | 无 |
| 2 | A 蚂蚁狮/沙漠 | `:112-126, 1340-1341` | 4 | 4 | 0 | 1 | 无 |
| 3 | B 海洋/鲨鱼/人鱼 | `:240-242, 435-439, 499-505, 762-785, 839-840` | 13 | 9 | 0 | 4 | **① import（首次）** |
| 4 | C 骷髅/地牢 | `:99-110, 340-362, 524-526, 646-653, 1432-1444` | 19 | 12 | 0 | 5 | 依赖批 3 import ×3 |
| 5 | F 雪原/杂项怪物 | `:389-393, 425-450, 632-696, 721-724, 827-830, 959-1000` | 19 | 13 | 0 | 9 | 依赖批 3 import ×1 |
| 6 | E 腐化/猩红/肉山后 | `:300-310, 352-355, 527-531, 549-554, 639-644, 952-953, 1157-1159, 1397-1445` | 20 | 11 | 0 | 6(+1 行内) | **② 类名搬家 ×2** + 依赖批 3 import ×2 |
| 7 | H Boss/NPC 零头 | `:856-861` | 3 | 1 | 0 | 0 | 无 |
| 8 | **火星内容批（实体+资源+表）** | 掉落表 `:842-849` | 8（7 新实体 + PROBE） | 0（全空表） | 见备注 | 0 | 实体注册+资源（台账 387/391/394 + 393 `ParticleAccessor`） |
| 9 | **38 个 1.20 空表** | 见批次 9 表 | **38** | 0 | 38 | 6 | 无 |
| — | 史莱姆/海盗族 | — | 0 | — | — | — | **已完成 `b1677a5d5`，勿重做** |

**总计待做**：94 个掉落表条目（56 有内容 + 38 空表）+ 7 个火星实体移植；注释共 **32 条**（31 条块注释 + 1 条行内注释 `1.20:1426 // 混沌传送杖`）。
注释按批分布：批2 = 1、批3 = 4、批4 = 5、批5 = 9、批6 = 6(+1 行内)、批9 = 6，合计 31+1 = 32。

**批次间依赖**：1 → 2 → 3（**加 import**）→ 4 → 5 → 6（**类名搬家**）→ 7 → 8（实体批）→ 9（空表批）。
注意批次 4/5/6 的 `[难度概率]` 条目**编译上依赖批次 3**，因此批次 3 不可后置。

## 落地后必做
1. 重新执行 datagen，让 `src/generated/resources/data/confluence/loot_table/entities/` 出现新表（当前产物停留在 HEAD−2 commits，已缺那 11 个文件）。
2. 落地前**重跑一次集合差**（两树都在动：本会话内 1.21 前进 `7b7eaae6c → b1677a5d5`、1.20 前进 `b657b999e → 07c2ab5b5`），避免重复劳动或漏项。


---

## 进度与已定案（2026-10 追加）

### 已落地
| 批次 | 内容 | 提交 | 规模 |
|---|---|---|---|
| — | A2：史莱姆族 5 + 海盗 5（本计划的先导子集） | `b1677a5d5` | +68 |
| 批次 1 | 小动物族 6 条 | `597e57702` | +27 |
| 批次 2 | 蚂蚁狮/沙漠族 4 条（含 `:1339` 欠账注释） | `05543e3f1` | +18 |
| 批次 3 | 海洋/鲨鱼/人鱼族 9 条 + **共用 import**（`DifficultyChanceLootItemCondition`） | `1384f96ef` | +40 |
| 收口 | `CRIMSLIME`/`CORRUPT_SLIME` 补回「史莱姆法杖 + 空 6999」池 | `cb5543c52` | +8 |
| 收口 | `DREAMER_GHOUL` 重复登记去重（两段 SHA-256 相同） | `2ceb32a6b` | −11 |
| 批次 4 | 骷髅/地牢族 12 条（含 5 条欠账注释） | `fbb7b4055` | +44 |
| 批次 5 | 雪原/杂项怪物族 13 条（含 9 条欠账注释，零适配） | `0ed38808c` | +75 |
| 批次 6 | 腐化/猩红/肉山后族 11 条（含唯一类名搬家 2 处 + 1 行 import） | `f78ae75e6` | +66 |
| 批次 7 | Boss/NPC 零头 1 条 | 05cbac11 | +6 |
| 批次 9 | 38 张 1.20 空表（+6 条欠账注释） | 7136a8b8 | +44 |

### 用户裁定（补充到本计划的执行口径）
1. **批次 3 落点保持现状**（4 个锚点、按 1.20 相对顺序），不按原稿的「紧邻 `PIRANHA`」重排。
2. **`CRIMSLIME`/`CORRUPT_SLIME` 补池后的池序** `[法杖, 盲罩, 凝胶]` 与 1.20 的 `[法杖, 凝胶, 盲罩]` 不同 ⇒ **不动**（三池独立 roll、无行为差异），登记为排版差异。
3. **1.20 的 javadoc 不补**（`BaseMinecartItem.createMinecart` 上方那行）；口径仍为「只搬欠账类注释、不新增说明性注释」。
4. `ICE_MIMIC`（1.21 `:1030`/`:1041` 各一条 `add`）**先核对再处理**：同 key 且逐字节相同才算重复；若为 2 参 + 3 参两张不同表（如 `RAINBOW_SHEEP` 的 `rainbow_sheep` / `sheep_rainbow_wool`）则属误报、不动。

### 落地后必做（未做，等用户决定）
- **重跑 datagen**（`runData`）让 `src/generated/resources/data/confluence/loot_table/entities/` 生成新表；本次会话按用户禁令**未编译、未跑 datagen**，故磁盘产物仍是「HEAD 减去最近若干提交」的状态。

### 批次 4 后续裁定与备查（2026-10）
5. **批次 4 的 `ARMORED_SKELETON`/`ROCK_GOLEM` 落在蝙蝠段**（`CAVE_BAT` 与 `SPORE_BAT` 之间）⇒ 用户裁定**保持现状**（1.20 里二者紧邻 `GIANT_BAT`，与批次 3 同口径：以 1.20 邻接关系为准）。
6. **`ICE_MIMIC` 判"非重复"**：`:1125`（当日行号）那条位于 `/* … */` 块注释内（**死代码**），`:1114` 才是活体登记；1.20 结构完全相同（活体 `:1195` + 注释段 `:1206`）。误报根因 = 判据只数 `add(` 文本、对块注释盲 ⇒ **以后判重必须做注释感知扫描**。
7. **给后续批次的预留空位（勿打乱）**：
   - 批次 6 的 `WEREWOLF` → `ROCK_GOLEM` 之后、夜明蝙蝠那条注释之前；
   - 批次 9 的 `RUNE_WIZARD` → `GIANT_WORM` 与 `TIM` 之间；
   - 批次 9 的 `ENCHANTED_SWORD`/`NECROMANCER`/`DIABOLIST`/`RAGGED_CASTER` → `CHAOS_ELEMENTAL` 与 `PALADIN` 之间；
   - 批次 9 的 `JUNGLE_CREEPER`/`DESERT_SPIRIT` → `GOBLIN_SCOUT` 与 `WALL_CREEPER` 之间。

### 批次 5 后续裁定（2026-10）
8. `GOBLIN_WARLOCK` 的单行写法（与 1.21 邻居换行风格不一致）⇒ **保持逐字**。
9. `ARMORED_VIKING` 罗盘池未写 `.setWeight(1)`（相邻 `UNDEAD_VIKING` 显式写了）⇒ **保持逐字**（权重默认即 1，语义等价）。
> 格式类口径已稳定：**除编译必需/语义必需的适配外一律以 1.20 逐字为准**，1.21 侧既有写法差异只登记、不回改。

### 批次 6 后续裁定（2026-10）
10. 批次 6 为 `CLINGER` 的 `FunctionalBlocks.MEAT_GRINDER` 加回的那 1 行 `import …init.block.FunctionalBlocks;` ⇒ **准，保留**（编译必需适配）。
11. 批次 9 落点：**只按 1.20 邻接插入新增空表**；已在 1.21 落地的实体（如 `WINGLESS_SLIMER`、`SLIMELING`）**不动**，不为邻接去移动已提交的行。
12. 复核口径：缺口数以 `python tools/port2native/loot_coverage.py` 为准（当前：1.20 引用 227 / 1.21 引用 187 / 无表 **39** + 实体不存在 7）。

### 批次 7/9 后续裁定（2026-10）
13. 1.20 :841 的段头注释 //火星人事件 ⇒ **随批次 8 一起搬**（插在 MARTIAN_PROBE 之前）。
14. ① 5 条「无掉落物（仅钱币）…已齐全」解释性注释不搬；② NECROMANCER/DIABOLIST/RAGGED_CASTER 按 1.20 实际邻接（BONE_LEE 之后）；③ WINGLESS_SLIMER 不为邻接移动 ⇒ **都按既定口径不改**。
15. 进度：批 1–7、9 全部落地 ⇒ **1.21 无掉落表归 0**；仅剩**批次 8（7 个待移植火星实体 + 其掉落表 + 裁定 13 的注释）**。

### 批次 8 启动（2026-10）
16. 用户裁定：**解除 common/init/ModEntities.java 的禁改**（只允许加两枚新射弹注册 MartianElectricBolt/RayGunnerLaser）。
17. 用户裁定：批次 8 **拆子批自动推进**（A 资源 → B 实体+注册 → C 射弹/网络/事件 → D 渲染 → E lang/掉落/bestiary）。
