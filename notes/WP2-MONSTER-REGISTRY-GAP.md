# WP2 注册层缺口普查：`MonsterEntities` 还差 90 个 id（含可直接落地的清单）

> 口径：解析两侧 `common/init/entity/{Monster,Boss,Critter,Npc}Entities.java` 的
> `public static final …<…> NAME =` 声明与 `register*("<id>")` 字符串，做**成员级**差集。
> 脚本口径与 `notes/WP2-MONSTER-ATTR-SCALING.md` 第五节的配置成员对账同源。
> 生成时点：`5554dffe9` 之后（狼人 / 大风气球怪 / 愤怒蒲公英 / 敌怪倍率四批已落）。

## 一、总账（**类已就位、注册未落地**是当前最大的结构性缺口）

| 注册类 | 1.20 成员 | 1.21 成员 | 1.21 缺 | 1.21 多余 |
|---|---:|---:|---:|---:|
| `MonsterEntities` | 213 | 63 | **150** | 0 |
| `BossEntities` | 32 | 13 | **19** | 0 |
| `CritterEntities` | 41 | 40 | **1**（`MYSTIC_FROG`） | 0 |
| `NpcEntities` | 36 | 36 | **0** | 0 |

而**类文件**层面 `common/entity/**` 只差 10 个：

```
monster/{HillHungry, DungeonSpirit, HungryMovementAction, TheHungry, VisualNeuron}.java
animal/MysticFrog.java  + 4 个 package-info.java
```

→ 也就是说：**一百多个怪物的类早已移植、能编译、能 `new` 出来，但拿不到 `EntityType`，
在游戏里根本不存在**（`/summon` 也不行）。这是「编译门全绿也看不出来」的最大一类欠账。

## 二、150 个缺失成员按「落地成本」分三类

| 类别 | 数量 | 判据 | 说明 |
|---|---:|---|---|
| **A. 可直接落地** | **123** | 注册语句只引用已存在的类与属性（无 `ModEntities.*` 弹幕依赖） | 本表第三节给出完整名单与 1.20 行号 |
| **B. 需先补 `ModEntities` 弹幕成员** | 25 | 注册语句里有 `.projectile(ModEntities.X, …)` 且 `X` 在 1.21 缺失 | 逐条列出（见第四节） |
| **C. 类本身未移植** | 2 | `DUNGEON_SPIRIT`（等 `BossEntities.PLANTERA`）、`VISUAL_NEURON`（随 `TheHungry` 族） | 见第五节 |

## 三、A 类：123 个「一句话注册」的名单（1.20 行号）

按 1.20 的分段顺序排列（**同一段通常可以一批吃完**）：

| 1.20 段落（推定） | 成员 |
|---|---|
| 地表 | `ZOMBIE`(47) `DEMON_EYE`(49) `POSSESS_ARMOR`(51) `WRAITH`(54) `WOODEN_MIMIC`(58) |
| 蠕虫 | `WYVERN_SEGMENT`(70) `WYVERN`(71) `GIANT_WORM_SEGMENT`(100) `GIANT_WORM`(101) `DIGGER_SEGMENT`(103) `DIGGER`(104) `DEVOURER_SEGMENT`(401) `DEVOURER`(402) `WORLD_FEEDER_SEGMENT`(404) `WORLD_FEEDER`(405) `BONE_SERPENT_SEGMENT`(609) `BONE_SERPENT`(610) `WITHER_BONE_SERPENT_SEGMENT`(612) `WITHER_BONE_SERPENT`(613) `LEECH_SEGMENT`(782) `LEECH`(783) `ARCH_WYVERN_SEGMENT`(768) `ARCH_WYVERN`(769) |
| 骷髅 | `ARMORED_SKELETON`(87) `DECAYEDER`(89) `DOCTOR_BONES`(244) `BASE_BONES`(527) `ANGER_BONES`(529) `SHORT_BONES`(531) `BIG_BONES`(533) `BIG_ANGER_BONES`(535) `BIG_MUSCLE_ANGER_BONES`(537) `BIG_HELMET_ANGER_BONES`(539) `CURSED_SKULL`(561) `BONE_LEE`(571) |
| 蝙蝠 | `CAVE_BAT`(94) `GIANT_BAT`(96) `JUNGLE_BAT`(214) `GIANT_FLYING_FOX`(216) `ICE_BAT`(276) `ILLUMINANT_BAT`(459) `SPORE_BAT`(497) `HELL_BAT`(585) `LAVA_BAT`(587) |
| 沙/雪/丛林地表 | `GIANT_SHELLY`(122) `CRAWDAD`(129) `NYMPH`(133) `SNOW_FLINX`(278) `UNDEAD_VIKING`(270) `ARMORED_VIKING`(272) `ICE_TORTOISE`(280) `SNATCHER`(220) `MAN_EATER`(222) `DERPLING`(232) `GIANT_TORTOISE`(234) `PIRANHA`(248) `ARAPAIMA`(250) |
| 宝箱怪 | `GOLDEN_MIMIC`(139) `JUNGLE_MIMIC`(254) `ICE_MIMIC`(302) `CORRUPT_MIMIC`(413) `CRIMSON_MIMIC`(449) `HALLOWED_MIMIC`(483) `SHADOW_MIMIC`(615) |
| 水母/水怪 | `BLUE_JELLYFISH`(143) `GREEN_JELLYFISH`(148) `ANGLER_FISH`(153) `WALL_CREEPER`(157) `PINK_JELLYFISH`(518) `SHARK`(516) `ZOMBIE_MERMAN`(644) `WANDERING_EYE_FISH`(640) `FLYING_FISH`(665) `SAND_SHARK`(697) |
| 花岗岩 | `GRANITE_ELEMENTAL`(173) `GRANITE_GOLEM`(179) |
| 沙漠 | `ANTLION_LARVA`(318) `ANTLION_CHARGER`(320) `ANTLION_SWARMER`(322) `GIANT_ANTLION_SWARMER`(324) `BASILISK`(328) `SAND_POACHER`(331) |
| 木乃伊/食尸鬼 | `MUMMY`(308) `DARK_LAMIA`(340) `LIGHT_LAMIA`(343) `GHOUL`(346) `TAINTED_GHOUL`(349) `VILE_GHOUL`(352) `DREAMER_GHOUL`(355) `DARK_MUMMY`(360) `BLOOD_MUMMY`(365) `LIGHT_MUMMY`(376) |
| 猩红/腐化 | `HERPLING`(426) `BLOOD_FEEDER`(440) `BLOOD_JELLY`(442) |
| 神圣 | `PIXIE`(461) `UNICORN`(465) `ENCHANTED_SWORD`(476) `CHAOS_ELEMENTAL`(481) |
| 蘑菇 | `SPORE_ZOMBIE`(491) `HAT_SPORE_ZOMBIE`(493) `SPORE_SKELETON`(495) `FUNGI_BULB`(501) `FUNGO_FISH`(509) |
| 陨石/幽灵 | `METEOR_HEAD`(619) `GHOST`(623) |
| 血月 | `BLOOD_ZOMBIE`(627) `DRIPPLER`(630) `THE_GROOM`(634) `THE_BRIDE`(636) |
| 沙尘暴 | `ANGRY_TUMBLER`(691) |
| 沙漠地下 | `BONE_BITER`(699) `FLESH_REAVER`(701) `CRYSTAL_THRESHER`(703) |
| 哥布林入侵 | `GOBLIN_SCOUT`(707) `GOBLIN_PEON`(711) `GOBLIN_WARRIOR`(713) `GOBLIN_THIEF`(715) `GOBLIN_ARCHER`(717) `ANGER_GOBLIN`(722) |
| 火星 | `MARTIAN_PROBE`(764) |
| 肉山前 | `THE_HUNGRY`(777) `HILL_HUNGRY`(785) |

## 四、B 类：25 个需要先补弹幕成员的（1.20 行号 + 依赖）

```
HARPY(66) HARPY_FEATHER          TIM(108) CHAOS_BALL_PROJECTILE*      RUNE_WIZARD(112) RUNE_BLAST
ROCK_GOLEM(118) THROWN_ROCK      BLACK_RECLUSE(161) SPIDER_WEB_SPIT   HOPLITE(188) HOPLITE_JAVELIN
HORNET(202) / MOSS_HORNET(206) HORNET_STINGER                          JUNGLE_CREEPER(227) SPIDER_WEB_SPIT
ICE_ELEMENTAL(290) FROST_BLAST   ICY_MERMAN(294) ICEWATER_SPIT         ANTLION(315) ANTLION_SAND_BALL
DESERT_SPIRIT(370) DESERT_SPIRIT_CURSE                                 CLINGER(409) CLINGER_FLAME
GIANT_FUNGI_BULB(503) FUNGI_SPORE                                      NECROMANCER(547) SHADOW_BEAM_PROJECTILE
DIABOLIST(551) INFERNO_BOLT_PROJECTILE                                 RAGGED_CASTER(555) LOST_SOUL_PROJECTILE
PALADIN(567) PALADIN_HAMMER_PROJECTILE                                 DEMON(591) / VOODOO_DEMON(595) HOSTILE_DEMON_SCYTHE
RED_DEVIL(599) UNHOLY_TRIDENT    ANGRY_NIMBUS(669) NIMBUS_RAIN         ICE_GOLEM(675) FROST_BEAM
GOBLIN_SORCERER(726) CHAOS_BALL_PROJECTILE*
```

\* `CHAOS_BALL_PROJECTILE` 在 1.21 **已存在**（`MonsterEntities` 现有条目在用），落地前逐个核对即可，
实际缺口应小于 25。

## 五、C 类：2 个被类挡住

| 成员 | 1.20 | 阻塞点 |
|---|---|---|
| `DUNGEON_SPIRIT` | `:563` | 类与渲染器都引用 `BossEntities.PLANTERA`（WP3 未落地） |
| `VISUAL_NEURON` | `:773` | 与 `TheHungry`/`HillHungry`/`HungryMovementAction` 同属「血肉墙」15 文件并集 |

## 六、建议的批次切法（下一批直接照做）

**推荐下一批 = 蝙蝠族（9 个 id）**，因为它是「同一段 + 同一渲染器家族 + 资源齐备」的最小完整体：

| 前置 | 内容 | 状态 |
|---|---|---|
| 1 | `MonsterEntities` 9 条注册（`CAVE_BAT`/`GIANT_BAT`/`JUNGLE_BAT`/`GIANT_FLYING_FOX`/`ICE_BAT`/`ILLUMINANT_BAT`/`SPORE_BAT`/`HELL_BAT`/`LAVA_BAT`），属性逐字照 1.20 `:94-588` | 类 `CaveBat`（含 `Variant{ROUTINE,ICE,ILLUMINANT,LAVA,HELL}`）**已在 1.21** |
| 2 | 客户端 `client/entity/renderer/BatRenderer`（1.20 仅 27 行：`GeoNormalRenderer<CaveBat>` + 覆盖 `getAnimationResource` + `getBlockLightLevel`） | 1.21 缺该类；依赖 `GeoNormalRenderer`/`GeoNormalModel` **都已在 1.21** |
| 3 | 9 条渲染器注册（1.20 `ModClientEvents:784-792`，其中 `LAVA_BAT`/`ILLUMINANT_BAT` 用 `BatRenderer`，其余 `GeoNormalRenderer<CaveBat>`） | 1 条带显式 `GeoNormalModel` |
| 4 | 放置谓词 5 支：`checkUndergroundMonsterSpawn`(1.20 `:476`)、`checkCaveMonsterSpawn`、`checkNetherMonsterSpawn`(`:648`)、`checkPostMechanicalNetherSpawn`、`checkSurfaceNightMonsterSpawn` | 都只需 `checkMonsterSpawnRules`（本会话已落地）+ `OverworldUtils`；**同批补进 `SpawnPlacementChecks`** |
| 5 | 放置登记 7 条 `group(...)`（1.20 `:136-148 / 149 / 176 / 180 / 190 / 223`） | 与谓词同批 |
| 6 | 资源：`geo/entity/{cave_bat,giant_bat,jungle_bat,ice_bat,illuminant_bat,hell_bat,lava_bat,spore_bat}.geo.json` + `animations/entity/{cave_bat,giant_bat,jungle_bat,ice_bat,hell_bat,spore_bat}.animation.json` + 同名 8 张 `textures/entity/*.png` | 1.20 磁盘全有（**GAP → 可逐字节拷贝**）；`textures/item/egg/*_spawn_egg.png` 属物品批，不在本批 |

> 注意 `sounds/mob/bat_death.ogg` 在 1.20、而 1.21 的声音走 `sounds/*.ogg` 扁平路径
> （狼人批已核对过同现象），落地时按 1.21 现有布局核对，不要照抄 `mob/` 子目录。

其余段落（骷髅族、宝箱怪族、木乃伊/食尸鬼族…）可以按上表逐段吃完，每段都是「注册 + 渲染器 + 放置 + 资源」
四件套；B 类那 25 个建议单独成批（先补 `ModEntities` 弹幕成员）。

---

## 七、蝙蝠族已按本表落地（`b78cd7d8b`，`notes/WP2-BAT-FAMILY.md`）

9 个 id 一次通过编译门（0/0），证明**纯注册批可以放心放大**。落地后发现两条通用经验：

1. **资源清点必须用代码里的 `asResource` 路径做基准**：`giant_flying_fox` 不含「bat」子串，
   按名字模糊过滤会漏掉它的 geo/动画/贴图三件套。
2. **放置规则按 1.20 的组合还原，但只登记已注册成员**：1.20 同组里未注册的物种要在注释中写明「随各自批次补」，
   否则会引用不存在的成员而编译失败；等那些物种落地时**要把它们合并回 1.20 的组合**。

## 八、下一批实测前置（本轮已逐个核过，可直接开工）

### 候选 1：宝箱怪族（8 个 id，最省事）

| 项 | 实测结果 |
|---|---|
| 注册成员 | `WOODEN_MIMIC`(1.20 `:58`) `GOLDEN_MIMIC`(139) `JUNGLE_MIMIC`(254) `ICE_MIMIC`(302) `CORRUPT_MIMIC`(413) `CRIMSON_MIMIC`(449) `HALLOWED_MIMIC`(483) `SHADOW_MIMIC`(615) |
| 类 | `WoodenMimic` / `BaseMimic` **1.21 都在** ✓ |
| 渲染器 | 1.20 `ModClientEvents:905-912`：全部 `GeoNormalRenderer<>(c, id)`，后四个加 `.withScale(2.0F)` —— 1.21 的 `GeoNormalRenderer:214` **有 `withScale`** ✓，无需新类 |
| 放置规则 | 1.20 `CreatureSpawnPlacements:220/234-236/239/240`（洞穴/地下 + `hardmode(checkNetherMonsterSpawn)`）→ 需核一下这几组用的谓词（多半只是 `checkCaveMonsterSpawn`/`checkUndergroundMonsterSpawn`，**本会话已落地**）+ `checkRoutineMonsterSpawn`(1.20 `:760`，3 行，随批补) |
| 资源 | 8 × (geo + animation + texture) = **24 个**，1.20 磁盘全有（`*_spawn_egg.png` 属物品批，不带） |
| 属性 | `WoodenMimic` 系列 260/14/42/穿甲 12/32/1/0.73/6；`BaseMimic` 系列 1820/16/47/…/0.9/6（含战斗状态机，见 1.20 原行） |

### 候选 2：水母族（3 个 id，多一个客户端类）

| 项 | 实测结果 |
|---|---|
| 注册成员 | `BLUE_JELLYFISH`(143) `GREEN_JELLYFISH`(148) `PINK_JELLYFISH`(518)，用 `registerJellyFish("<id>", JellyFish.Profile.ROUTINE)` 辅助 + `.state(...)` 战斗状态 |
| 类 | `JellyFish` **1.21 在** ✓；**`client/entity/renderer/JellyFishRenderer` 1.21 缺**（1.20 有，需一并移植） |
| 渲染器 | 1.20 `ModClientEvents:894-896`：`new JellyFishRenderer(c, jellyfishModel("blue"/"pink"/"green"))` —— 还要搬 `jellyfishModel(...)` 这个局部辅助 |
| 放置规则 | 1.20 `:167/168/169/246`：`checkWaterMonsterSpawn`(需补)、`checkSurfaceWaterMonsterSpawn`(✓ 已有)、`checkUndergroundWaterMonsterSpawn`(需补) |
| 资源 | `geo/entity/jellyfish.geo.json` + `animations/entity/jellyfish.animation.json` + **3 张** `textures/entity/{blue,green,pink}_jellyfish.png`（三色共用一套模型/动画） |
| 声音 | `sounds/mob/jellyfish_{death,free,free_0,hurt}.ogg` → 1.21 声音路径是**扁平** `sounds/*.ogg`（狼人/蝙蝠批已两次核对），按现有布局落 |

→ **顺序建议**：先宝箱怪族（无新类、8 个 id），再水母族（1 个新客户端类 + 3 个 id）。

> ✅ 宝箱怪族已于 `a30777650` 落地（`notes/WP2-MIMIC-FAMILY.md`），一次通过；注册层缺口 150 → 133。

## 九、**最高杠杆下一步：共享「原版人形」客户端三件套**（实测数据）

第三批做完后量了一次「共享渲染器/模型的复用度」（统计 1.20 `ModClientEvents` 里 `registerEntityRenderer` 行）：

| 1.20 共享类 | 1.21 是否存在 | 1.20 复用（渲染器注册数） | 行数 |
|---|---|---:|---:|
| `GeoNormalRenderer` | ✅ 已有 | **114** | — |
| `MissingModelRenderer` | ✅ 已有 | 15 | — |
| **`VanillaHumanoidRenderer`** | ❌ **缺** | **20** | 83 |
| **`VanillaSkeletonGeoModel`** | ❌ **缺** | **10** | 84 |
| **`VanillaGoblinGeoModel`** | ❌ **缺** | 6 | 70 |
| **`VanillaZombieGeoModel`** | ❌ **缺** | 3 | 74 |
| `EntityGlowingGeoLayer`（被图层用，不在注册行里） | ❌ 缺 | — | 31 |
| `GeoNegativeVolumeRenderer` | ⚠️ 只在 **TerraEntity 子模块**里（`terraentity.client.entity.renderer.mob`），主模组 5 处注册在用它 | 5 | — |

结论：**下一批应该做「`VanillaHumanoidRenderer` + `VanillaSkeletonGeoModel`（可选带 Zombie/Goblin 两个模型）」**，
它一次解锁「骨骼族 + 僵尸族 + 哥布林族」约 **17~20 个 id** 的渲染器，而且这些类的 API 适配模式**已经全部踩过**：

- `software.bernie.geckolib.core.animatable.model.CoreGeoBone` → `cache.object.GeoBone`（狼人批已处理同类）
- `core.animation.AnimationState` → `animation.AnimationState`
- `software.bernie.geckolib.util.RenderUtils` → `util.RenderUtil`
- `VanillaHumanoidGeoModel`（基类）**已在 1.21**（狼人批 `2e801ef09` 落地）—— 这三个模型应与它同源
- ⚠️ `preRender/actuallyRender` 家族在 1.21 把 4 个颜色 float 收成 `int colour`（史莱姆尾巴批踩过）→
  `VanillaHumanoidRenderer` 若有颜色相关覆写要按新签名改
- ⚠️ `GeoNegativeVolumeRenderer` 目前来自 **TerraEntity**（该子模块在退役清单上）→ 用到它的 5 处注册
  （`BLOOD_CLOUD_PROJECTILE`/`RAIN_CLOUD_PROJECTILE`/`CURSED_SKULL` 等）**迟早要把这个类搬进主模组**，
  建议与「骨骼族」批一起考虑

配套还缺 9 个 `MonsterEntities` 注册辅助（1.20 有、1.21 无，直接影响能照搬多少注册语句）：

```
registerAcceleratingLand  registerCharger  registerFlyingFish  registerGoblinLand
registerHumanoidLand      registerJellyFish  registerJumpingLand  registerSkeleton  registerSnatcher
```

这些辅助各自对应一族怪物的「尺寸 + 行为档案」包装（例如 `registerSkeleton(id, w, h, BehaviorProfile)`），
**建议与对应种族批一起补**；`registerSkeleton` 随骨骼族、`registerJellyFish` 随水母族、
`registerFlyingFish` 随雨天族（`FLYING_FISH`）、`registerCharger` 随 `BONE_LEE`/`SNAPPER` 一族。

## 十、下一批预案：地下沙漠族（木乃伊 + 食尸鬼 + 拉米亚，10 个 id）—— 已逐个摸清

**一个模型类换 10 个 id**（`ContactHumanoidGeoModel`，1.20 44 行，1.21 缺）。

| 成员 | 1.20 注册 | 辅助 | 渲染器（1.20 行） | 放置谓词 |
|---|---|---|---|---|
| `MUMMY` | `:308` | `registerJumpingLand` | `:858` `ContactHumanoidGeoModel<>(id, "RightArm", "LeftArm")` + `.withCutout()` | `checkSandMummySpawn`(`:241`) |
| `DARK_MUMMY` | `:360` | `registerJumpingLand` | `:859` 同上 | `checkEbonsandMummySpawn`(`:242`) |
| `BLOOD_MUMMY` | `:365` | `registerJumpingLand` | `:860` 同上 | `checkCrimsandMummySpawn`(`:243`) |
| `LIGHT_MUMMY` | `:376` | `registerJumpingLand` | `:861` 同上 | `checkPearlsandMummySpawn`(`:244`) |
| `DARK_LAMIA` | `:340` | `registerLand` | `:862` `<>(id, "right_arm", "left_arm")`（无 cutout） | `hardmode(checkEvilDesertUndergroundSpawn)`(`:210`) |
| `LIGHT_LAMIA` | `:343` | `registerLand` | `:863` 同上 | `hardmode(checkPureOrHallowDesertUndergroundSpawn)`(`:207`) |
| `GHOUL` | `:346` | `registerLand` | `:864` `<>(id, "hand_right", "hand_left")`（无 cutout） | `hardmode(checkPureDesertUndergroundSpawn)`(`:205`) |
| `TAINTED_GHOUL` | `:349` | `registerJumpingLand` | `:867` `<>(id, "hand_right", "hand_left").withAltAnimations(asResource("ghoul"))` | `hardmode(checkCrimsonDesertUndergroundSpawn)`(`:214`) |
| `VILE_GHOUL` | `:352` | `registerJumpingLand` | `:868` 同上 | `hardmode(checkCorruptDesertUndergroundSpawn)`(`:212`) |
| `DREAMER_GHOUL` | `:355` | `registerJumpingLand` | `:869` 同上 | `hardmode(checkHallowDesertUndergroundSpawn)`(`:216`) |

**需一并落地的支撑件（开工前先核对，都在 1.20 有现成原文）**：

1. `client/entity/model/ContactHumanoidGeoModel`（44 行，构造参数 = `ResourceLocation` + 左右手骨骼名，
   内部只做接触攻击的手臂旋转）。**API 已核（本轮做完陆地批后顺手验的）**：
   - `GeoNormalRenderer#withCutout()` **1.21 已有**（`GeoNormalRenderer.java:77`）✓
   - `.withAltAnimations(ResourceLocation)` **来自 geckolib 的 `DefaultedEntityGeoModel`**（不是本仓库的方法），
     已 `javap` 核过 `geckolib-neoforge-1.21.1-4.8.2.jar`：`withAltModel`/`withAltAnimations`/`withAltTexture` 三个都在 ✓
     → 那三行 ghoul 渲染器可以**逐字照搬**
2. `MonsterEntities.registerJumpingLand`（1.20 `:912/916/925/929` 四个重载）+ `JumpingWarriorMonster`
   的 `CombatState.WOUNDED` ✓（1.21 `JumpingWarriorMonster.java:57`）与
   `.state(..., state -> state.multiply(Attributes.MOVEMENT_SPEED, 2))` ✓
   （`CreatureAttributeBuilder.StateBuilder#multiply(Holder<Attribute>, double)` 在 1.21 `:362`）

3. 10 支沙漠谓词：`checkDesertUndergroundMonsterSpawn`(基) + `checkPureDesertUndergroundSpawn` /
   `checkPureOrHallowDesertUndergroundSpawn` / `checkEvilDesertUndergroundSpawn` /
   `checkCorruptDesertUndergroundSpawn` / `checkCrimsonDesertUndergroundSpawn` /
   `checkHallowDesertUndergroundSpawn` + 四支木乃伊谓词（后四者还需 `hasConnectedSand` 之类的私有辅助，
   1.20 `:540-554` 一带）。
4. 资源：10 个 id × (geo + animation + texture) ≈ 30 个 —— **注意 `GHOUL` 系列的 alt 动画指向
   `animations/entity/ghoul.animation.json`**（共享），先把 1.20 磁盘清单核一遍再拷。

> ✅ 该批已于 `e3d1440b7` 落地（`notes/WP2-DESERT-FAMILY.md`），一次通过；注册层缺口 150 → 94。

## 十一、再下一批预案：蠕虫族（9 组 18 个 id）—— 摸底结论

成员（1.20 `MonsterEntities` 行号）：`WYVERN`(71)/`WYVERN_SEGMENT`(70)、`GIANT_WORM`(101)/`_SEGMENT`(100)、
`DIGGER`(104)/`_SEGMENT`(103)、`DEVOURER`(402)/`_SEGMENT`(401)、`WORLD_FEEDER`(405)/`_SEGMENT`(404)、
`BONE_SERPENT`(610)/`_SEGMENT`(609)、`WITHER_BONE_SERPENT`(613)/`_SEGMENT`(612)、`ARCH_WYVERN`(769)/`_SEGMENT`(768)、
`LEECH`(783)/`_SEGMENT`(782)。

**注册侧已就绪（已核）**：

- `registerWormSegment(String)`（1.21 `MonsterEntities:734`）✓
- `registerWorm(name, w, h, Role, Anatomy, Supplier<BaseWormPart>)`（1.21 `:743`）✓ —— **连 `Anatomy` 参数都在**
- `SimpleWormMonster.Role`：`UNDERGROUND`/`UNDERGROUND_DESERT`/`CORRUPTION`/`UNDERWORLD`/`BONE_SERPENT`/`FLYING` 全在 ✓
- 渲染器用的 `WormHeadRenderer`/`WormPartRenderer` 依赖的 `BaseWormPart` 也已在 1.21（`common/entity/monster/BaseWormPart.java`）

**真正的成本 = 3 个客户端渲染器类**（1.21 只有 Boss 版 `BossWormPartRenderer`）：

| 类 | 1.20 行数 | 说明 |
|---|---:|---|
| `WormPartRenderer` | 124 | 体节渲染；`(context, Supplier<EntityType<?>> headType, float scale, boolean wyvern)` 形态 |
| `WyvernRenderer` | 70 | 飞龙（`WYVERN`/`ARCH_WYVERN`），`(context, float scale)` |
| `WormHeadRenderer` | 39 | 头部（`GIANT_WORM`/`DIGGER`/`DEVOURER`/`WORLD_FEEDER`/`LEECH`/`BONE_SERPENT`/`WITHER_BONE_SERPENT`） |

**还需 5 支谓词**（1.20）：`checkCorruptionWormSpawn`(`:104` 处用到)、`checkGiantWormSpawn`(`:145`)、
`checkDiggerSpawn`(`:173`)、`checkHighLevelMonsterSpawn`(`:184`)、`checkArchWyvernSpawn`(`:185`)；
放置登记 7 条（1.20 `:104/145/149/173/174/184/185`，其中 `:149` 的 `BONE_SERPENT`/`WITHER_BONE_SERPENT`
与已落地的 `HELL_BAT` 同组）。




