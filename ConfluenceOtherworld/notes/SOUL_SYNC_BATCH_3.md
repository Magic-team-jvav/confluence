# 魂师第三轮数值与机制调整（双版本同名同改）

适用：**1.21.1 NeoForge**（`confluence/ConfluenceOtherworld`）与 **1.20.1 Forge**（`confluence-1.20.1/ConfluenceOtherworld`）。
两份源码本轮改动一一对应，差异只有版本 API（`defineSynchedData` 签名、`ModEffects.X.get()`、`PortEventHandler.addListener`、`PortLivingIncomingDamageEvent`）。

数值仍然只写在 `SoulSkillParameters`；物品只声明「带哪条自带效果」（`SoulWeaponItem.Trait`），行为统一在
`PlayerUtils` / `LivingEntityEvents` / `SoulLureProjectile` 三处结算。

---

## 1. 魂引天星：范围内整片坠星，伤害 +5

| 项 | 旧 | 新 |
| --- | --- | --- |
| 坠星伤害 | 6 | **11**（+5） |
| 落点 | 找一个敌怪，往它头上落**一颗** | 半径内**每只敌怪**头顶各一颗 |
| 持续时间 | 40 秒 | 80 秒（强力附魂系列 ×2） |
| 冷却 | 30 秒 | 60 秒 |
| 启动耗魂 | 12 | 15（+3） |
| 每轮耗魂 | 每颗 1 魂 | **0（免费）** |

实现：

- `tickStarCall` 重写为「一轮齐射」：每 `STAR_CALL_INTERVAL_TICKS`（10 刻）在
  `STAR_CALL_AREA_RADIUS`（8 格）覆盖区内落一圈坠星——中心 1 颗 + 等角度环上 8 颗（半径 6 格），
  共 `STAR_CALL_STARS_PER_VOLLEY = 9` 颗；
- **时间差**：第 i 颗用 `setSpawnDelay(i * STAR_CALL_STAGGER_TICKS)`（默认 1 刻）错开出生；
  `SoulLureProjectile` 新增 `spawnDelayTicks`，延迟期间**不移动、不判定、不生成粒子**；
- **额外齐射不消耗灵魂**：灵魂只在施放时按 `STAR_CALL_SOUL_COST`（15）扣一次，
  之后增益期间的每一轮都是免费的（`STAR_CALL_VOLLEY_SOUL_COST = 0`，与附魂激荡的免费自动施放同一口径）；
  `STAR_CALL_REFUND_CHANCE` 只在轮次成本 > 0 时才结算，避免免费齐射变成白嫖灵魂；
- **落点按敌怪**：`starCallTargets` 取 48 格内的敌怪（球形距离、近的优先、每轮最多 4 只），
  `starCallSpawnY` 在每只头顶 8 格找空位生成；**范围内没有敌怪时整轮不落星**；
  坠星 `setBounces(0)`，不参与弹射（详见 11.0）。

> 需要恢复「每轮扣魂」时，把 `STAR_CALL_VOLLEY_SOUL_COST` 调成非 0 即可（按轮计费 + 概率返还自动回来）。

## 2. 血怒

| 项 | 旧 | 新 |
| --- | --- | --- |
| 增伤 | 7% | **+200%**（`ADD_MULTIPLIED_TOTAL`） |
| 持续时间 | 8 秒 | **16 秒** |
| 生命代价 | 10 | **5** |
| 冷却 / 耗魂 | 3 秒 / 0 | 不变 |

`ModEffects.BLOOD_RAGE` 的属性修饰符直接读常量，改数值即生效。

## 3. 劫掠之魂耗魂 → 8

按「5 + 强力附魂系列 +3」结算：`SOUL_PLUNDER_SOUL_COST` 5 → **8**，同时时长 70 → 140 秒、冷却 50 → 100 秒。

> 需求里另有一条「劫掠之魂改为消耗 5 灵魂」与「所有强力附魂系列 +3」冲突，已确认取 **+3 优先**（5→8）。

## 4. 灭却业火

- `KARMA_FLAME_RADIUS` 6 → **16**：处决范围扩大到玩家周围 16 格；
- 新增 `KARMA_FLAME_KILL_SOUL_BONUS = 2`，`KarmaFlameSkill.use` 在 `hurt` 后判定
  `!target.isAlive()` 时 `PlayerUtils.adjustSoul(+2)`；常规收魂仍由死亡事件结算，所以是「额外 +2」而不是替换。

## 5. 强力附魂系列：时长与冷却 ×2、耗魂 +3

| 技能 | 时长 | 冷却 | 耗魂 |
| --- | --- | --- | --- |
| 强力附魂 `enhanced_soul` | 50 → **100 秒** | 30 → **60 秒** | 3 → **6** |
| 魂引天星 `star_call` | 40 → **80 秒** | 30 → **60 秒** | 12 → **15** |
| 灵魂突击 `soul_assault` | 50 → **100 秒** | 30 → **60 秒** | 10 → **13** |
| 掠夺之魂 `soul_plunder` | 70 → **140 秒** | 50 → **100 秒** | 5 → **8** |
| 灵魂透支 `soul_drain` | 40 → **80 秒** | 6 → **12 秒** | 0 → **3** |

- 系列成员仍是 `ModTags.SoulSkills.ENHANCED_SOUL_SERIES` 那 5 个，标签本身没动；
- 灵魂透支的「灵魂枯竭」`SOUL_DRAIN_LOCKOUT_TICKS` 80 → **160 秒**（与免消耗时长同步翻倍）；
- 灵魂透支改为消耗 3 魂后，`ModSoulSkills.SOUL_DRAIN` 不再传字面量 0，改传 `SOUL_DRAIN_SOUL_COST`；
- 另留 `ENHANCED_SOUL_SERIES_EXTRA_SOUL_COST = 3` 作文档留痕（数值已并入各技能自身常量，代码不读它）。

## 6. 饰品收魂 +1、魂刀 / 弓 +2 与命中回魂

| 对象 | 效果 |
| --- | --- |
| 束魂锁链 `soul_binding_chain` | 每次收魂 **+1** |
| 手环系列（灵魂手环 / 灵魂再生手环 / 通灵手环） | 每次收魂 **+1**（三者共用 `band_of_soul` 标记，戴多只也只算一份） |
| 刻骨的记忆 `memory_of_bonebrand` | 每次收魂 **+1** |
| 祈灵之刃（= 魂刀）击杀 | +1 → **+2** |
| 灵魂弓 / 灵魂短弓 击杀 | +1 → **+2** |
| 祈灵之刃 / 灵魂弓 / 灵魂短弓 命中 | **50% 概率 +1 魂**（新增） |

- 前三条挂 `SoulCollectionEvent`（`LivingEntityEvents.soulCollectionAccessoryBonus`），
  用的是饰品自带的 `ValueType.UnitType`（`TCUtils.hasType`），不新增注册项；
- 击杀加成沿用既有两条处理器，只改常量；
- 命中回魂是新处理器 `soulWeaponHitSoul`：只认主手魂刀 / 主副手灵魂弓，且仅灵魂模式下给魂。

## 7. 灵魂激荡：0.5 秒扩散到 6 格的整块判定框

| 项 | 旧 | 新 |
| --- | --- | --- |
| 半径 | 5 | **6** |
| 扩散时长 | 5 秒（100 刻） | **0.5 秒（10 刻）** |

`SpiritSurgeEntity` 本来就是「半径随 `age / duration` 线性增长、每个目标只结算一次」的扩散判定框，
本轮只改两个常量，因此灵魂激荡 / 强力激荡 / 灵魂引发 / 灵魂激荡冲击（含终结爆炸）**全部**同步变成
「从玩家中心 0.5 秒扩到自身半径」。强力激荡仍是 8 格、终结爆炸仍是 8 格，纵向范围不变。

> 引魂激荡（`lure_surge`）是「弹幕命中后原地爆炸」，不属于扩散判定框，未改动。

## 8. 主武器自带效果

新增 `SoulWeaponItem.Trait` 枚举与 `hasTrait`；物品只声明带哪几条，
统一判定入口 `PlayerUtils.holdsSoulWeaponTrait(player, trait)`（看主手，换手即失效）。

| 物品 | Trait | 效果 |
| --- | --- | --- |
| 残破微光 `broken_glimmer` | `HEAL_ON_SOUL_GAIN` | 手持时获得灵魂，20% 概率回复 4 点生命 |
| 星魄 `star_spirit` | `STAR_ON_SKILL_HIT` | 技能命中敌人，30% 概率额外落一颗坠星砸它 |
| 颂魂指引 `soul_chant_guide` | `LURE_PIERCE` | 引魂弹幕额外穿透 2 个敌怪 |
| 带路的老前辈 `senior_pathfinder_pro` | `LURE_HOMING` | 引魂弹幕追踪最近敌怪 |
| 丛林的灯（生机灿烂 `life_splendor`） | `POISON_ON_SKILL_HIT` | 技能命中附带中毒 5 秒 |
| 无分昼夜 `day_and_night_alike` | `SHADOWFLAME_ON_SKILL_HIT` | 技能命中附带暗影焰 5 秒 |

实现落点：

- 「技能命中」= `LivingIncomingDamageEvent` 且 `damageSource.is(LibDamageTypes.SOUL)`，
  由 `LivingEntityEvents.soulWeaponTraitsOnSkillHit` 统一结算，普通近战 / 弓箭不会触发；
- 「获得灵魂」走 `SoulCollectionEvent`（`brokenGlimmerHeal`），灵魂自然再生与灵魂手环的缓慢回复不算；
- 穿透 / 追踪写在 `SoulLureProjectile`：首个服务端 tick 里 `resolveWeaponTraits()` 读一次主手，
  之后换手不影响这一发已经在飞的弹幕。

> 「丛林的灯」按确认解释为现有魂灯 **生机灿烂**（`life_splendor`，毒刺 + 丛林孢子配方、手持授予自然之惩戒）。

## 9. 松露灯：技能冷却 -30%

`PlayerUtils.getSoulCooldownTicks` 在「分魂驾驭延长 → 冰冷魂戒缩短」之后再追加一条：
主手是松露灯时 `ticks *= 0.7`。技能与法器冷却都走这个入口，因此一套通用。

## 10. 魂师技能总览：背景总开关

- `ClientConfigs` 新增 `SOULCERER_BACKGROUND_ENABLED`（`soulcererBackgroundEnabled`，默认 `true`），
  与 `soulcererBackgroundHue / Contrast / Tree / Magic` **同一分组** `GUI → Soulcerer`；
- `SoulOverviewScreen#renderBackground` 开头提前返回，关掉后不画噪点、树、魔法，也不推进滚动；
- 翻译键：`confluence.configuration.soulcererBackgroundEnabled[.tooltip]`（中英各一份）。

## 11. 引渡者（鬼魂掉落的技能书）强化

| 项 | 旧 | 新 |
| --- | --- | --- |
| 引魂系列技能伤害 | +1 | **+2**（`PSYCHOPOMP_SOUL_LURE_DAMAGE_BONUS`） |
| 引魂系列弹幕弹射 | — | **额外 +1 次**（`PSYCHOPOMP_SOUL_LURE_EXTRA_BOUNCE`） |

+SoulProjectileSkill.use 在生成弹幕时，若「已学引渡者 + 技能属于 `confluence:soul_lure` 标签」，
就给每发弹幕 `addBounces(1)`。

## 12. 引魂弹幕：命中后弹射一次

`SoulLureProjectile` 现在按优先级结算命中：**穿透 → 弹射 → 消散**。

| 项 | 值 |
| --- | --- |
| 基础弹射次数 | 1（`SOUL_LURE_BOUNCE_COUNT`） |
| 弹射索敌半径 | 16 格（`SOUL_LURE_BOUNCE_RADIUS`） |
| 无第二个敌怪时 | 随机偏航 + 轻微俯仰的偏移方向射出 |

- 弹射体继承伤害、特效、重力、射程、`aoe`、`enhancedSoulOnHit`、剩余穿透 / 弹射 / 追踪，
  并把**已经打过的敌怪 UUID 一起带走**，不会回头重复打同一只；
- `canHitEntity` 排除已命中集合，因此穿透弹幕对同一目标只结算一次；
- 魂引天星的坠星 `setBounces(0)`，不弹射。

> 注意：弹射加在所有 `SoulLureProjectile` 上（迷魂孢雾、灵魂引发的连锁弹幕同样会弹），
> 若觉得连锁太吵，把 `SOUL_LURE_BOUNCE_COUNT` 调 0 再按技能单独给。

## 13. 堕魂星核解锁：灵魂条 / 魔力条随武器自动互切

- `SoulStorage` 新增 `masterSwitchUnlocked`（NBT `masterSwitchUnlocked`），
  `FallenSoulCoreItem` 每次成功切换后置 `true`（一次性解锁，永久保留）；
- 解锁后：
  - `SoulWeaponItem.use` → `PlayerUtils.ensureResourceModeForSoulWeapon`：灵魂模式没开就切到灵魂条；
  - `PlayerUtils.extractMana`（所有魔法武器都走这里）→ `ensureResourceModeForMagicWeapon`：灵魂模式开着就切回魔力条；
- 没解锁时行为完全不变，仍只能靠堕魂星核手动切换。

> 切换沿用既有 `switchSoulMode` 语义：进入哪种模式就把另一条清零（`targetResource = 0`）。
> 所以「切过去的第一下」通常因为源池为空而打得空一发，属于既有设计，不在本轮改动范围。

---

## 改动文件清单（两版同名同路径）

**参数 / 注册**

- `common/soulcerer/skill/SoulSkillParameters.java`（全部数值 + `STAR_CALL_*` 新常量 + `Trait` 相关数值）
- `common/init/ModSoulSkills.java`（灵魂透支耗魂改读常量）
- `common/init/item/SoulItems.java`（6 件主武器挂 `Trait`）

**逻辑**

- `common/item/soulcerer/SoulWeaponItem.java`（`Trait` 枚举、`hasTrait`、用武器时自动切灵魂条）
- `common/item/soulcerer/SoulLanternItem.java`（构造转发 traits）
- `common/item/soulcerer/FallenSoulCoreItem.java`（解锁标记）
- `common/attachment/SoulStorage.java`（`masterSwitchUnlocked`）
- `common/entity/soulcerer/SoulLureProjectile.java`（生成延迟、弹射、穿透、追踪、命中去重）
- `common/soulcerer/skill/SoulProjectileSkill.java`（引渡者额外弹射）
- `common/soulcerer/skill/KarmaFlameSkill.java`（击杀 +2 魂）
- `util/PlayerUtils.java`（`holdsSoulWeaponTrait`、两个资源模式入口、松露灯冷却、`tickStarCall` 齐射、`extractMana` 钩子）
- `common/event/game/entity/LivingEntityEvents.java`（收魂饰品加成、命中回魂、主武器命中效果、残破微光回血）

**客户端 / 配置**

- `client/ClientConfigs.java`（背景总开关）
- `client/gui/container/soulcerer/SoulOverviewScreen.java`（提前返回）

**数据生成**

- `common/data/gen/language/InterfaceLanguageSubProvider.java`（血怒 / 魂引天星 / 灭却业火说明）
- `common/data/gen/language/TooltipLanguageSubProvider.java`（血怒、魂引天星、三手环、束魂锁链、刻骨的记忆）
- `common/data/gen/language/SoulLanguageSubProvider.java`（引渡者说明）
- `common/data/gen/language/ConfigurationLanguageSubProvider.java`（背景总开关）

## 验证

- 两版用 javac 单独编译改动过的源码：均 `exit code 0`（无新增错误）；
- 两版 `:ConfluenceOtherworld:runData`：均 `BUILD SUCCESSFUL`；
- 1.21.1 生成物核对：`assets/confluence/lang/{en_us,zh_cn,en_ud}.json` 已带上
  `confluence.configuration.soulcererBackgroundEnabled[.tooltip]`、新版 `blood_rage / star_call / karma_flame`
  说明、`tooltip.confluence.soulcerer_skill_book.psychopomp`（+2 与弹射）。

## 仍未做 / 已知风险

1. 两版都没有进游戏实测：坠星齐射的帧率与覆盖面（每轮 9 个弹幕实体）、松露灯 -30% 后的手感、弹射在密集敌群里的连锁数量。
2. 弹射是**实体级**行为（所有 `SoulLureProjectile` 共用），灵魂引发的 6 发连锁弹幕也会各自弹一次。
3. 星魄的坠星自循环已修（`isStarProjectile` 跳过），但其余灵魂伤害仍会触发星魄。
4. 血怒 +200% 是 `ADD_MULTIPLIED_TOTAL`，与其它近战 / 远程倍率直接相加，属于极强的常驻增益
   （3 秒冷却 + 16 秒持续，可以一直维持），如需削弱只改 `BLOOD_RAGE_DAMAGE_BONUS`。

---

## 11. 追加修正（同日第二轮）

### 11.0 魂引天星：额外齐射免费 + 改成按敌怪落星

第一版实现是「以玩家为中心铺一圈」，只要增益在身就无条件落星（空场也落）。现在两处一起改：

**a. 额外齐射不消耗灵魂**

- 新常量 `STAR_CALL_VOLLEY_SOUL_COST = 0F`：**每轮齐射的灵魂消耗**，0 表示整轮免费；
- `tickStarCall` 不再按颗扣魂，只在 `volleyCost > 0` 时按轮扣一次，并在这条分支里结算
  `STAR_CALL_REFUND_CHANCE`（免费模式不返还，否则等于白嫖灵魂）；
- `STAR_CALL_SOUL_PER_STAR` 常量删除，`STAR_CALL_SOUL_COST`（施放 15 魂）不变。

**b. 有索敌：只落在敌怪身上，没敌怪就不落**

| 项 | 现在 |
| --- | --- |
| 索敌半径 | `STAR_CALL_TARGET_RADIUS = 48` 格（球形距离，不只是包围盒） |
| 落点 | **每只敌怪头顶各一颗**，按距离由近到远 |
| 每轮上限 | `STAR_CALL_STARS_PER_VOLLEY = 4` 只（近的优先，超出的这轮不落） |
| 范围内无敌怪 | **整轮不落星**（不再空转刷实体） |
| 生成高度 | 目标上方 `STAR_CALL_SPAWN_HEIGHT = 8` 格；被方块占住时向下找第一个空位 |
| 筛选 | 存活 / 非观察者 / 非友军 / `EnemyDamageRules.isEnemy`（与旧索敌一致） |

`starVolleyPositions`（玩家周围的环）删除，替换为 `starCallTargets` + `starCallSpawnY`。

- 技能说明同步：`star_call.desc` →「范围内每只敌怪头顶都会落下坠星；没有敌怪就不落，且施放之后的每轮齐射都不消耗灵魂。」
- 效果说明同步：`tooltip.effect.confluence.star_call.0` →「范围内每只敌怪头顶都会落下坠星」。

### 11.1 主武器自带效果补 tooltip

走既有 **DataTip** 体系（与饰品同一套，不需要写 `appendHoverText`）：

- `ModDataTipProvider.weaponUsage()` 新增 7 条 entry（残破微光 / 星魄 / 颂魂指引 / 带路的老前辈 /
  生机灿烂 / 无分昼夜 / 松露灯）；
- `TooltipLanguageSubProvider` 新增 `tooltip.item.confluence.<id>.0`（中英各一）；
- 生成物：`assets/confluence/datatip/weapon_usage.json`（`runData` 产出，已核对 7 个 id 都在）。

### 11.2 星魄不再由坠星触发（防自循环）

`LivingEntityEvents` 新增 `isStarProjectile(event)`：伤害直接来源是「星怒外观的 `SoulLureProjectile`」时，
`STAR_ON_SKILL_HIT` 整条跳过。其余灵魂伤害（弹幕、激荡、连锁）仍可触发星魄。

### 11.3 背景总开关：关动画但保留底色

`SoulOverviewScreen#renderBackground` 改成「先画静态底色 → 再判开关」：

```
静态底色（色相 / 对比度） → 关闭时直接 return → 噪点 / 树 / 魔法 / 滚动推进全部跳过
```

关掉后界面是一层静态深色底，不再直接透出世界；开关位置与翻译键不变。

### 11.4 `SOUL_TEST_GUIDE.md` 同步更新（两版内容一致）
- 新增「主武器自带效果」表（7 件）；
- 副武器改为「击杀 +2 魂、命中 50% 再 +1 魂」；
- 饰品三手环 / 刻骨的记忆 / 束魂锁链 补「每次收魂 +1」；
- 引渡者改为「伤害 +2 且引魂弹幕额外弹射一次」；
- 数值速查新增收魂加成、魂刀弓命中回魂、松露灯冷却四行；
- 新增**第十节 第三批改动测试清单**（10.1~10.9），含每件主武器的逐条验证方法。

### 11.5 追加轮验证

| 步骤 | 1.21.1 | 1.20.1 |
| --- | --- | --- |
| javac 编译改动源码 | exit 0 | exit 0 |
| `:ConfluenceOtherworld:runData` | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| `datatip/weapon_usage.json` | 7 条新 entry | 7 条新 entry |
| `lang/{en_us,zh_cn,en_ud}.json` | 7 条 `tooltip.item.confluence.<id>.0` | 同左 |

---

## 12. 追加修正（同日第三轮）

### 12.1 无分昼夜：灵魂伤害 13 → 23

`SoulItems.DAY_AND_NIGHT_ALIKE` 的武器基数 13.0F → **23.0F**（+10）。
它同时授予灭却业火 + 亵渎之魂，因此这两个技能的伤害一起变高。

### 12.2 施法伤害快照：换手不再影响已放出去的技能

**问题**：魂引天星的每一轮齐射都会重新读「当前主手武器」的灵魂伤害，
所以用星魄开完增益再换武器/空手，后续坠星会照着新武器结算；
灵魂突击的连射同理。

**修法**：每次施法成功时把「当时的武器灵魂伤害」记进 `SoulStorage`，延时/多段攻击改读这份快照。

| 位置 | 说明 |
| --- | --- |
| `SoulStorage.castWeaponDamage` | `Map<技能 id, 武器灵魂伤害>`，NBT 键 `castWeaponDamage` |
| `PlayerUtils.castSoulSkill` | 施法成功后 `setCastWeaponDamage(skill.getId(), getWeaponSoulDamage(player))` |
| `PlayerUtils.getWeaponSoulDamage` | 主手是灵魂武器时取其 `getBasicSoulDamage`，否则 0 |
| `PlayerUtils.getCastSoulSkillDamage(player, skill)` | 用快照拼装：`getSoulSkillDamage(player, stack, 快照值)` |
| `PlayerUtils.getSoulSkillDamage(player, stack, weaponDamage)` | 新增重载，武器部分由调用方给定 |
| `PlayerUtils.tickStarCall` | 改用 `getCastSoulSkillDamage(player, STAR_CALL)` |
| `SoulAssaultSkill.fireBurst` | 改用 `getCastSoulSkillDamage(player, SOUL_ASSAULT)` |

**注意边界**：快照只锁「武器基数」。技能基础伤害、引渡者 +2、星魂联结、
分魂驾驭、灵魂伤害属性与其它实时增益/减益仍然每轮实时结算，
所以「施法武器的伤害不变、增益减益照常吃」这两条同时成立。

> 该快照按技能 id 分开存，所以「用星魄开魂引天星 → 换武器 → 再放别的技能」不会污染魂引天星的伤害。
> 同一技能重新施放会用新武器覆盖快照。

### 12.3 两处检查（结论：本来就是对的，未改代码）

| 检查项 | 结论 |
| --- | --- |
| 带路的老前辈的追踪是否对**弹射后**的弹幕生效 | **生效**。`spawnBounce` 里 `bounced.homing = homing;` 且 `bounced.weaponTraitsResolved = true`，弹射体直接继承追踪标记，不会再重新读主手 |
| 穿透 + 弹射同时存在时是否「先穿透耗尽再弹射」 | **是**。`tick()` 里先判 `pierceRemaining > 0`（扣一次穿透并继续飞），只有穿透耗尽才走 `bouncesRemaining > 0` 生成弹射体 |
| 魂引天星「一轮只有一个敌怪时会不会落 4 颗」 | **不会**。`starCallTargets` 来自 `getEntitiesOfClass`，每只实体只出现一次，再按距离截断到 4 只；1 只敌怪 = 1 颗星 |

> 唯一例外：多部件 Boss 的各个部件是独立实体，会各自算一个目标；如需按 Boss 去重可改成按 `LibEntityUtils.encounterOwner` 合并。

### 12.4 第三轮验证

| 步骤 | 1.21.1 | 1.20.1 |
| --- | --- | --- |
| javac 编译改动源码 | exit 0 | exit 0 |
| `:ConfluenceOtherworld:compileJava` | BUILD SUCCESSFUL | BUILD SUCCESSFUL |

（本轮无数值表以外的资源/语言变更，因此没有重跑 `runData`。）

---

## 13. 追加修正（同日第四轮）

### 13.1 强力引魂：额外弹射 +1

- `SoulProjectileSkill.Builder` 新增 `bounces(int extraBounces)`，技能自己声明「在本体自带 1 次弹射之上再弹几次」；
- 常量 `ENHANCED_LURE_EXTRA_BOUNCE = 1`，`ModSoulSkills.ENHANCED_LURE` 加 `.bounces(...)`；
- 实际弹射次数 = 1（本体）+ 1（强力引魂）+ 1（引渡者书，若已学）；
- 说明文案：`enhanced_lure.desc` →「强化版引魂，弹幕基础威力大幅提升，且额外弹射一次。」

### 13.2 灵魂长袍套装效果：灵魂伤害 +5%

灵魂头套 `soul_hood` + 灵魂长袍 `soul_robe` 同时穿戴时：

- `PlayerUtils.isWearingSoulRobeSet(player)` 判定；
- `PlayerUtils.tickSoulArmorSets` 每刻同步一个临时属性修饰符
  `confluence:soul_robe_set_soul_damage`（`ADD_MULTIPLIED_TOTAL`，`SOUL_ROBE_SET_SOUL_DAMAGE_BONUS = 0.05`）；
- 与长袍单件自带的 +5% **叠加**（即两件套共 +10% 灵魂伤害）；脱下立即移除。

### 13.3 迷魂孢雾崩溃修复

崩溃报告：`run/crash-reports/crash-2026-10-21_22.56.41-client.txt`

```
NullPointerException: Cannot invoke "IntArrayList.getInt(int)" because "this.wrapped" is null
  at MolangParticleEngine.tick(MolangParticleEngine.java:121)
```

**根因**：`MolangParticleEngine.tick()` 用 fastutil 的**复用**迭代器
（`emitters.int2ObjectEntrySet().fastIterator()`）遍历 `emitters` 表；而旧实现里
`SoulLureProjectile` 的客户端 tick 写成「`emitter == null || emitter.isRemoved()` 就重建 + `addEmitter`」——
迷魂孢雾一次放 8 发、每发都短命，于是每刻都往同一张表里 `put`。
引擎正在遍历这张表时被结构性修改，复用迭代器状态被破坏，就抛出了上面那个 NPE。
另外 `SPORE_PARTICLE` 原本借用 `confluence:spore_cloud`，这个 id 同时是 `ModParticleTypes`
注册的原版粒子类型，同名会让发射器解析路径变得不可预期。

**修法**（两处一起）：

1. `SoulLureProjectile` 的客户端发射器改成**每个弹幕只登记一次**（条件由
   `emitter == null || emitter.isRemoved()` 改为 `emitter == null`），不再每刻重建；
2. 迷魂孢雾改用**专用粒子 id** `confluence:confuse_spore_projectile`，与 `spore_cloud` 原版粒子类型彻底分离。

### 13.4 新粒子：迷魂孢雾射弹（7 帧序列帧）

| 项 | 内容 |
| --- | --- |
| 粒子 id | `confluence:confuse_spore_projectile` |
| 定义文件 | `assets/confluence/particle_definitions/confuse_spore_projectile.particle.json` |
| 贴图 | `assets/confluence/textures/particle/confuse_spore_projectile.png`（原 `菇菇嘎嘎.png`，8×56 = 7 帧 8×8，已改名） |
| 渲染材质 | `particles_blend`（孢子是柔和半透明，不用叠加混合） |
| 序列帧 | `texture_width 8 / texture_height 56`，`size_UV [8,8]`，`step_UV [0,8]`，`max_frame 7`，14 fps 循环 |
| 发射器 | `emitter_lifetime_looping(active_time 1)` + `emitter_local_space(position)`，跟着弹幕走 |
| 粒子 | `spawn_rate 30 / max_particles 60`，寿命 0.6 秒，初速 0.35、向上加速度 0.6、阻尼 3.5 |
| 外观 | 首帧 0.08 缩放、之后 0.22；颜色淡绿 `[0.85, 1.0, 0.7]`，随寿命线性淡出 |
| 碰撞 | `particle_motion_collision(collision_drag 0.4)` |

旧的 `spore_cloud` 定义**保留不动**——`SporeCloudProjectile`、`NorthPoleSubProjectile`、
`PlantSpit` 等还在用它。

### 13.5 第四轮验证

| 步骤 | 1.21.1 | 1.20.1 |
| --- | --- | --- |
| javac 编译改动源码 | exit 0 | exit 0 |
| `:ConfluenceOtherworld:runData` | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| 新粒子 JSON 合法性 | 通过（identifier/texture 均为 `confluence:confuse_spore_projectile`） | 同左 |
| 贴图存在 | 是（8×56） | 是（8×56） |
| 旧 `SPORE_PARTICLE` 常量残留 | 0 | 0 |

---

## 14. 追加修正（同日第五轮）：灵魂激荡地面光环粒子

### 14.1 `spirit_surge` 粒子定义重写

原先 `particle_definitions/spirit_surge.particle.json` 只是草稿（无命名空间、UV 按 16×16 但贴图是 32×32、
球体发射 + 初速 3、`rotate_xyz` 朝向相机、没有透明度动画）。现在按需求重做：

| 项 | 值 |
| --- | --- |
| identifier / texture | `confluence:spirit_surge` / `confluence:spirit_surge`（均为 32×32 贴图） |
| 朝向 | `facing_camera_mode: emitter_transform_xz` → 面片躺在 **XZ 平面**（水平环） |
| 发射 | `emitter_rate_instant(1)` + `emitter_lifetime_once(0.5)` + `emitter_shape_point`，初速 0 |
| 寿命 | 粒子 0.5 秒 = **10 刻**，与 `SPIRIT_SURGE_DURATION_TICKS` 一致（扩散到极限范围的时间） |
| 尺寸 | `variable.radius * 2.0 * (age / lifetime)` → 从 0 长到「直径 = 判定框半径 × 2」 |
| 旋转 | `particle_initial_spin.rotation: math.random(0,360)`、`rotation_rate: math.random(-18,18)`（缓慢随机自转） |
| 透明度 | 曲线 `variable.alpha = [0, 1, 1, 0]`（线性）：0 → 不透明 → 保持 → 末尾回 0 |
| 材质 | `particles_blend`（柔和发光，不叠加过曝） |

`variable.radius` 由实体在创建发射器时注入（`new MolangExp("variable.radius", RADIUS)`），
所以灵魂激荡 6 格 → 直径 12；强力激荡 / 终结爆炸 8 格 → 直径 16，不用另写定义。

### 14.2 由 `SpiritSurgeEntity` 在施法点（玩家脚底）发出

```java
MolangParticleEngine.INSTANCE.addEmitter(new ParticleEmitter(level(), position(), SURGE_PARTICLE,
        new MolangExp("variable.radius", entityData.get(RADIUS))));
```

- 只在**客户端**、且每次激荡**只放一次**（`surgeRingSpawned` 标记）；
- 位置就是实体生成点 = `owner.position()` = 施法瞬间的脚底；
- 原来的 5 个原版 `minecraft:soul` 环粒子保留（用来标出当前判定半径），两者互补。

### 14.3 引魂弹幕粒子：透明度淡入

`soul_projectile.particle.json` 的 `particle_appearance_tinting.color`
由写死的 `0.8` 改为 `variable.alpha`，并新增曲线 `variable.alpha = [0, 1, 1, 1]`：
粒子出生时全透明，在寿命前 1/3 内淡入到不透明，之后保持。观感上弹幕是「浮出来」而不是硬闪一下。

### 14.4 第五轮验证

| 步骤 | 1.21.1 | 1.20.1 |
| --- | --- | --- |
| javac 编译改动源码 | exit 0 | exit 0 |
| `:ConfluenceOtherworld:compileJava` | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| `spirit_surge.particle.json` 合法 | 是（id/texture 均 `confluence:spirit_surge`） | 是 |
| `soul_projectile.particle.json` 合法 | 是（alpha=`variable.alpha`） | 是 |

本轮只有客户端资源与渲染侧改动，没有语言/数据生成产物，未跑 `runData`。

---

## 15. 追加修正（同日第六轮）：粒子 id 没同步 → 孢雾特效不生效

### 15.1 现象与根因

迷魂孢雾放出后仍然是**引魂的蓝色拖尾**，新的 7 帧孢子特效完全不出现。

根因：`SoulLureProjectile.particle` 只是一个**普通字段**，而弹幕是**服务端**构造的。
客户端通过 `EntityType` 的裸构造创建实体时，这个字段永远是默认值
`confluence:soul_projectile`——所以客户端只会画引魂粒子，
`ModSoulSkills.CONFUSE_SPORES` 里传的 `CONFUSE_SPORE_PARTICLE` 从来没生效过
（这也意味着以前的 `spore_cloud` 同样从来没被这个弹幕用过）。

> 同类问题在本批次早先已经踩过一次：魂引天星的星怒面片之所以用
> `DATA_STAR_APPEARANCE` 同步，就是因为「引魂弹幕本身没有 spawn 同步数据」。

### 15.2 修法

- 新增 `EntityDataAccessor<String> DATA_PARTICLE`（`EntityDataSerializers.STRING`）；
- `defineSynchedData` 里默认值写 `PARTICLE.toString()`；
- 构造弹幕时 `entityData.set(DATA_PARTICLE, particle.toString())`（服务端写入 → 随生成包同步到客户端）；
- 客户端画特效改读 `syncedParticle()`：解析同步字符串，失败才回落默认；
- `readAdditionalSaveData` 读档时同步写回 `DATA_PARTICLE`。

这样各技能的自定义粒子第一次真正生效：

| 技能 | 粒子 |
| --- | --- |
| 迷魂孢雾 | `confluence:confuse_spore_projectile`（新的 7 帧孢子） |
| 亵渎之魂 | `confluence:ball_of_fire` |
| 引魂 / 强力引魂 / 附魂印记 / 引魂激荡 / 灵魂引发 | `confluence:soul_projectile` |
| 魂引天星 | 星怒面片（`DATA_STAR_APPEARANCE`，不建发射器） |

迷魂孢雾因此**不再有任何引魂弹幕粒子**，只有它自己的孢子序列帧。

### 15.3 第六轮验证

| 步骤 | 1.21.1 | 1.20.1 |
| --- | --- | --- |
| javac 编译改动源码 | exit 0 | exit 0 |
| `DATA_PARTICLE` 同步通道 | 已加（define + 构造写入 + 读档写回） | 同左 |
| 客户端发射器读取 | `syncedParticle()` | 同左 |

---

## 16. 追加修正（同日第七轮）：激荡只留新粒子 + 范围 +20%

### 16.1 去掉原版 `minecraft:soul` 环粒子

`SpiritSurgeEntity` 客户端 tick 里原来每刻沿半径撒 5 颗原版灵魂粒子，现在整段删除，
只保留 `spawnSurgeRing()`（`confluence:spirit_surge` 贴地光环）。

- `SoulSkillParameters.SPIRIT_SURGE_PARTICLE_COUNT` 常量已无引用，一并删除；
- `SpiritSurgeEntity` 的 `ParticleTypes` 导入随之移除。

现在灵魂激荡的视觉只有那张从脚底扩散、0.5 秒长满判定框的光环。

### 16.2 伤害范围 +20%

| 项 | 旧 | 新 |
| --- | --- | --- |
| `SPIRIT_SURGE_RADIUS`（灵魂激荡 / 灵魂引发） | 6.0 | **7.2** |
| `EMPOWERED_SURGE_RADIUS`（强力激荡） | 8.0 | **9.6** |
| `SURGE_BLAST_FINISHER_RADIUS`（终结爆炸） | 8.0 | **9.6** |
| 纵向范围 | 5.0 / 6.0 | 不变（横扩不竖扩，保持"贴地扩散"手感） |

光环粒子读的是实体同步的 `RADIUS`，所以直径自动跟着变成 14.4 / 19.2，不需要改粒子定义。

### 16.3 第七轮验证

| 步骤 | 1.21.1 | 1.20.1 |
| --- | --- | --- |
| javac 编译改动源码 | exit 0 | exit 0 |
| `:ConfluenceOtherworld:compileJava` | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| 实体里 `ParticleTypes` / `addParticle` 残留 | 0 | 0 |
| 三个半径常量 | 7.2 / 9.6 / 9.6 | 7.2 / 9.6 / 9.6 |

---

## 17. 追加修正（同日第八轮）：灵魂突击枪械联动 + 魂引天星降频

### 17.1 灵魂突击的子弹不再有粒子

- `SoulLureProjectile` 新增哨兵常量 `NO_PARTICLE = confluence:none`；客户端建发射器时若同步过来的粒子 id
  是它，就直接跳过（不创建任何 `ParticleEmitter`）；
- `SoulAssaultSkill.fireBurst` 生成子弹时改传 `NO_PARTICLE`。

因此灵魂突击的子弹是纯判定体，不带拖尾。

### 17.2 灵魂突击增益期间：开枪强化子弹

| 项 | 值 |
| --- | --- |
| 触发 | 拥有 `soul_assault` 增益 + 灵魂 ≥ 1 |
| 消耗 | **1 魂 / 每开一枪** |
| 强化 | 这一发的子弹伤害 **+5**（霰弹枪的所有 pellet 共用同一发强化） |
| 返还 | **50%** 概率返还 **1 魂** |
| 灵魂不足 | 不强化、不扣血代偿，普通子弹照常打出 |

实现：

- 常量集中在 `SoulSkillParameters.SOUL_ASSAULT_GUN_*`（伤害/耗魂/返还率/返还量）；
- `PlayerUtils.applySoulAssaultGunBonus(player, damage)` 统一结算；
- 挂点在 `GunFiringService.fire(...)`：在 `criticalDamageTotal` 之后、`GunProjectileFactory.spawn` 之前，
  因此强化对这次开火产生的**所有**射弹生效；
- 说明文案 `soul_assault.desc` 同步。

> 按"每开一枪 1 魂"计（不是每颗弹丸 1 魂），霰弹枪一发多 pellet 不会被重复扣魂。

### 17.3 魂引天星：坠星频率 -60%

`STAR_CALL_INTERVAL_TICKS` 10 → **25 刻**（间隔 ×2.5，频率降到原来的 40%）。
每轮仍然是「最近的最多 4 只敌怪各一颗」，只是轮与轮之间从 0.5 秒拉长到 1.25 秒。

### 17.4 第八轮验证

| 步骤 | 1.21.1 | 1.20.1 |
| --- | --- | --- |
| javac 编译改动源码 | exit 0 | exit 0 |
| `:ConfluenceOtherworld:runData` | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| `NO_PARTICLE` 生效点 | `SoulLureProjectile` 客户端判定 + `SoulAssaultSkill` | 同左 |
| 枪械挂点 | `GunFiringService.fire` | 同左（`PortEventHandler` 版本） |
| `STAR_CALL_INTERVAL_TICKS` | 25 | 25 |

---

## 18. 追加修正（同日第九轮）：总览界面左下角补背景开关按钮

之前背景总开关只能去 `选项 → 配置 → GUI → Soulcerer` 里改；总览界面左下角只有
⚙（展开色相 / 明度滑块），没有这个开关。现在补上：

| 项 | 内容 |
| --- | --- |
| 位置 | `(29, height - 25)`，紧挨 ⚙ 右侧，和色相 / 明度滑块同属左下角那一组 |
| 大小 | 20×20 |
| 文案 | 开 = `☑`，关 = `☐`（点击后立刻刷新） |
| 行为 | 直接写 `ClientConfigs.SOULCERER_BACKGROUND_ENABLED` + 同步静态字段，配置项与界面开关是同一份数据 |
| 悬停提示 | `confluence.screen.soulcerer_overview.background_toggle`（中英） |
| 关掉后 | `renderBackground` 只铺静态底色，噪点 / 树 / 魔法 / 滚动全部停画，只留底色与技能控件 |

实现上用的是 `addRenderableWidget(Button...)`，点击由 `super.mouseClicked` 处理，
不需要往自定义的 `mouseClicked` 里加分支（已有的滑块仍在自定义分支里）。

### 18.1 第九轮验证

| 步骤 | 1.21.1 | 1.20.1 |
| --- | --- | --- |
| javac 编译改动源码 | exit 0 | exit 0 |
| `:ConfluenceOtherworld:runData` | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| 新语言键 | `confluence.screen.soulcerer_overview.background_toggle` 已落盘 | 同左 |

---

## 19. 追加修正（同日第十轮）：魂引天星节奏与每轮数量

| 项 | 旧 | 新 |
| --- | --- | --- |
| `STAR_CALL_INTERVAL_TICKS` | 25 刻（1.25 秒一轮） | **50 刻（2.5 秒一轮）** |
| `STAR_CALL_STARS_PER_VOLLEY` | 4 颗/轮 | **2 颗/轮** |

节奏口径按需求取中：**4 轮 / 8 秒 ~ 4 轮 / 12 秒**（一轮 2~3 秒），实装 2.5 秒一轮。

- 满增益 80 秒内：32 轮 × 2 颗 = **64 颗**（上一版 64 轮 × 4 = 256 颗）；
- 索敌半径 48 格、每轮取最近 2 只、无敌人不落、每轮 0 魂 —— 都没有变化；
- 只有数值改动，没有语言/资源产物，未跑 `runData`。

### 19.1 第十轮验证

| 步骤 | 1.21.1 | 1.20.1 |
| --- | --- | --- |
| javac 编译改动源码 | exit 0 | exit 0 |
| `:ConfluenceOtherworld:compileJava` | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| `STAR_CALL_INTERVAL_TICKS` / `STARS_PER_VOLLEY` | 50 / 2 | 50 / 2 |

---

## 20. 追加修正（同日第十一轮）：切换灵魂技能按键

### 20.1 问题

按键列表里有两条都默认 `左 Shift` 的绑定：

| 常量 | 显示名 | 是否被读取 |
| --- | --- | --- |
| `MAGIC_QUICK_SKILL_SWITCHING` | 灵魂快捷技能切换 | **是**（`ClientSoulHandler` 用它开面板） |
| `MAGIC_SKILL_RELEASE` | 释放灵魂技能 | **否**（全树没有任何引用） |

所以「改释放灵魂技能键位没有任何效果，按 Shift 才会弹面板」——**键位失效**是这条死绑定造成的。

### 20.2 修法

合并成一条绑定：

```java
/// 切换灵魂技能：默认左 Alt，按住弹面板、滚轮切换、松开确认。
public static final Lazy<KeyMapping> MAGIC_SKILL_SWITCHING = ... "key.confluence.soulcerer.skill_switching" ...
        GLFW.GLFW_KEY_LEFT_ALT ...
```

- 删除 `MAGIC_QUICK_SKILL_SWITCHING` 与 `MAGIC_SKILL_RELEASE`；
- `ClientSoulHandler.getSoulSkillKeyMapping()` 改读新绑定，并做空判断（`SOUL_SKILLS` 关闭时为 null）；
- `handleSoulSkills` / `handleSoulSkillWheelRelease` 改为传入同一个 `KeyMapping`，松开判定与开面板用的是同一份绑定；
- 语言键：删除 `key.confluence.soulcerer.quick_skill_switching`、
  `key.confluence.magic.skill_release`，新增 `key.confluence.soulcerer.skill_switching`
  = 「切换灵魂技能 / Switch Soul Skill」。

因为是新语言键，旧配置里那两条的旧值不会再被读取，所有人默认就是**左 Alt**（可改）。

### 20.3 其他样式排查结果

| 样式 | 打开方式 | 滚轮切换 |
| --- | --- | --- |
| 小轮盘 `ROULETTE_WHEEL_SMALL` | 同一条绑定 | ✅ |
| 横向卡片（左/右）`CARD_HORIZONTAL_L/R` | 同一条绑定 | ✅ |
| 大轮盘 `ROULETTE_WHEEL_BIG` | 同一条绑定 | ❌ 设计如此：它靠**鼠标指向**选（`mouseHandler.releaseMouse()`），
`scrollSoulSkills` 对它直接返回 false |

四种样式都由 `openSoulSkillHuds()` 一次性打开，没有各自的硬编码按键
（全树搜 `GLFW_KEY_LEFT_SHIFT` 只剩原来的两处绑定，现已删除）。

### 20.4 第十一轮验证

| 步骤 | 1.21.1 | 1.20.1 |
| --- | --- | --- |
| javac 编译改动源码 | exit 0 | exit 0 |
| `:ConfluenceOtherworld:runData` | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| 旧两条语言键 | 已从生成物移除 | 同左 |
| 新键 `key.confluence.soulcerer.skill_switching` | 已落盘 | 同左 |

---

## 21. 追加修正（同日第十二轮）：新标签「强主动技能」

### 21.1 为什么需要它

灵魂透支原本属于「强力附魂系列」(`enhanced_soul_series`)，同时也在「附魂」(`soul_attach`) 里，因此：

- **束魂锁链**会顺手把它带出来（它结束时会锁主武器 160 秒，被自动带出很坑）；
- **魂术轮转**在连锁该系列时也会把它放出去；
- 轮转挑下一个可施放技能时还会停在它上面。

这类「有副作用 / 资源类大招」需要**只能手动施放**的出口，于是新增标签：

```java
/// 强主动技能：必须手动施放的技能——不被束魂锁链联动、不被魂术轮转连锁或自动选中。
public static final TagKey<SoulSkill> MAJOR_ACTIVE = register("major_active");
```

### 21.2 标签成员

| 技能 | 理由 |
| --- | --- |
| 灵魂透支 `soul_drain` | 结束锁主武器 160 秒 |
| 灭却业火 `karma_flame` | 清空全部灵魂的处决技 |
| 血怒 `blood_rage` | 需要主动扣血，且是常驻增益 |

`ModSoulSkillTagsProvider` 里显式登记，生成
`data/confluence/tags/confluence/soulcerer_skill/major_active.json`。

### 21.3 生效点（都在 `PlayerUtils`）

| 位置 | 行为 |
| --- | --- |
| `applySoulSkillSynergy` 束魂锁链分支 | 跳过 `MAJOR_ACTIVE`：不自动施放 |
| `chainCastEnhancedSoulSeries` 魂术轮转连锁 | 跳过 `MAJOR_ACTIVE`：灵魂透支不再被成组带出 |
| `findNextReadySoulSkillIndex` 轮转选槽 | 跳过 `MAJOR_ACTIVE`：轮转不会停在这些技能上 |
| `findNextCastableSoulSkillIndex` 空槽时的下一槽 | 同样跳过，避免按一下施法键被顺手带出 |

手动选中后按施法键照常释放 —— 只是不再被**自动**放出/选中。

### 21.4 第十二轮验证

| 步骤 | 1.21.1 | 1.20.1 |
| --- | --- | --- |
| javac 编译改动源码 | exit 0 | exit 0 |
| `:ConfluenceOtherworld:runData` | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| 生成 `major_active.json` | 3 个成员 | 3 个成员 |

---

## 22. 追加修正（同日第十三轮）：背景开关修好 + 四个技能降 CD + 亵渎之魂索敌

### 22.1 背景开关"不能自由开关"

原因：界面按钮只写**配置值 + 缓存字段**，而 `renderBackground` 读的是缓存字段。
一旦缓存与配置不同步（配置页改动、配置重载时机差异），就会出现"点了没反应 / 关得掉开不回来"。

修法：把读取收敛到两个入口，界面与配置共用同一份"实时值"。

```java
/// 优先读实时配置，配置未加载时才回落到缓存字段。
public static boolean isSoulcererBackgroundEnabled();
/// 同时写配置项与缓存字段。
public static void setSoulcererBackgroundEnabled(boolean enabled);
```

- `SoulOverviewScreen#renderBackground`、按钮文案、按钮点击全部改走这两个方法；
- 现在点击 ☑/☐ 与配置页开关效果一致，可以反复自由切换。

### 22.2 四个技能冷却下调到 0.8 秒、自然之惩戒到 1 秒

| 技能 | 常量 | 旧 | 新 |
| --- | --- | --- | --- |
| 引魂 | 新增 `SOUL_LURE_COOLDOWN_TICKS` | 20（借用 `ACTIVE_COOLDOWN_TICKS`） | **16** |
| 强力引魂 | `ENHANCED_LURE_COOLDOWN_TICKS` | 16 | 16（已是 0.8 秒，未变） |
| 附魂印记 | `SOUL_MARK_COOLDOWN_TICKS` | 20 | **16** |
| 亵渎之魂 | `PROFANE_SOUL_COOLDOWN_TICKS` | 20 | **16** |
| 自然之惩戒 | `NATURES_WRATH_COOLDOWN_TICKS` | 30 | **20（1 秒）** |

> 引魂原来是直接借用通用的 `ACTIVE_COOLDOWN_TICKS`（还被灵魂激荡、灵魂激荡冲击、
> 附魂激荡间隔共用），所以**没有**改那个常量，而是给引魂单独开了 `SOUL_LURE_COOLDOWN_TICKS`。

### 22.3 亵渎之魂：自动索敌 + 从敌怪脚下向上打

| 项 | 旧 | 新 |
| --- | --- | --- |
| 索敌筛选 | 存活 / 非观察者 / 非友军 | 追加 `EnemyDamageRules.isEnemy`（不再盯上动物/村民/NPC） |
| 半径 | `SPAWN_DISTANCE × 2` = 12 格 | 不变 |
| 目标选择 | 最近 | 最近（逻辑收敛到 `findTarget`） |
| 出生点 | 敌怪 `getY() + 0.2`（脚内） | **敌怪脚下 `PROFANE_SOUL_SPAWN_OFFSET = 0.8` 格** |
| 方向 | 固定 `(0, 1, 0)` | **朝敌怪碰撞箱中心**（`center - origin`），所以是"从下往上怼它" |
| 无目标 | 身前地面向上 | 不变 |

火球的 AoE（半径 2.5）不变，所以即使出生点在脚下被地面挡住，也会在原地爆开命中敌人。

### 22.4 第十三轮验证

| 步骤 | 1.21.1 | 1.20.1 |
| --- | --- | --- |
| javac 编译改动源码 | exit 0 | exit 0 |
| `:ConfluenceOtherworld:compileJava` | BUILD SUCCESSFUL | BUILD SUCCESSFUL |


---

## 23. 追加修正（同日第十四轮）：合并「附魂」与「强力附魂系列」标签

两者本来就指同一类东西（施放后给自己挂同名增益），只是历史上拆成了两份名单。
本轮合并成一份，只保留 `SOUL_ATTACH`（`confluence:soul_attach`）。

### 23.1 标签

| 旧 | 新 |
| --- | --- |
| `SOUL_ATTACH`（10 项） | **`SOUL_ATTACH`（11 项，并入灵魂突击）** |
| `ENHANCED_SOUL_SERIES`（5 项） | **删除** |

合并后成员：`enhanced_soul / soul_mark / soul_surge / blood_rage / soul_plunder / soul_drain /
star_call / soul_assault / boiling_blood / life_return / firmament_reversed`。

### 23.2 引用

| 位置 | 改动 |
| --- | --- |
| `ModTags` | 删除 `ENHANCED_SOUL_SERIES`；`SOUL_ATTACH` 注释注明合并来源 |
| `ModSoulSkillTagsProvider` | 单标签写入 11 项，删掉系列标签块 |
| `PlayerUtils.applySoulSkillSynergy` | 魂术轮转分支改判 `SOUL_ATTACH` |
| `PlayerUtils.chainCastEnhancedSoulSeries` → **`chainCastSoulAttachSkills`** | 过滤条件改用 `SOUL_ATTACH` |
| `SoulSkillParameters.ENHANCED_SOUL_SERIES_EXTRA_SOUL_COST` → `SOUL_ATTACH_EXTRA_SOUL_COST` | 文档留痕常量改名 |

生成物：`soul_attach.json` 变 11 项，`enhanced_soul_series.json` 被 datagen 清掉。

### 23.3 行为变化（合并的自然结果）

1. **束魂锁链**：名单里多了**灵魂突击**，所以放任意附魂技能时它也会被一起带出
   （灵魂透支 / 血怒 / 灭却业火仍被 `MAJOR_ACTIVE` 排除）。
2. **魂术轮转**：现在对**所有附魂技能**触发连锁（以前只有那 5 个「强力附魂系列」），
   连锁范围同样是整份 `SOUL_ATTACH`；非附魂技能仍只做「切到下一个可释放技能」。

> 如果之后想把连锁范围收窄回「强力」那一组，只需新建一个子标签并改
> `chainCastSoulAttachSkills` 的过滤条件，不必恢复两份平行名单。

### 23.4 第十四轮验证

| 步骤 | 1.21.1 | 1.20.1 |
| --- | --- | --- |
| javac 编译改动源码 | exit 0 | exit 0 |
| `:ConfluenceOtherworld:runData` | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| `soul_attach.json` 成员数 | 11 | 11 |
| `enhanced_soul_series.json` | 已删除 | 已删除 |

---

## 24. 追加修正（同日第十五轮）：引魂激荡命中补激荡 + 激荡冲击终结爆炸击退

### 24.1 引魂激荡：命中位置补一发免费灵魂激荡

| 项 | 旧 | 新 |
| --- | --- | --- |
| 技能定义 | `.aoe(LURE_SURGE_RADIUS, LURE_SURGE_AOE_DAMAGE)` | **`.surgeOnHit(LURE_SURGE_RADIUS)`** |
| 命中效果 | 弹幕位置一次性的 3 格范围伤害（无粒子、无后续） | 在**被打中的敌怪位置**生成一发 `SpiritSurgeEntity`（半径 3、0.5 秒扩散、带地面光环粒子） |
| 弹射 | 弹射后不再有激荡 | **弹射弹幕同样携带激荡**（`spawnBounce` 里 `setSurgeOnHit` 一并继承） |
| 耗魂 | 施放扣 3 魂 | 施放扣 3 魂，**激荡本身不扣魂** |

实现落点：

| 位置 | 改动 |
| --- | --- |
| `SoulSkillParameters` | 删除 `LURE_SURGE_AOE_DAMAGE`；`LURE_SURGE_RADIUS` 保留（3.0）当激荡半径 |
| `SoulProjectileSkill` | 新增 `surgeOnHitRadius` 字段 + `Builder.surgeOnHit(double)`；生成弹幕时 `setSurgeOnHit(...)` |
| `SoulLureProjectile` | 新增 `surgeOnHitRadius` + `setSurgeOnHit(double)` + `spawnSurgeAt(Entity)`；命中分支调用；`spawnBounce` 复制给弹射弹 |
| `ModSoulSkills.LURE_SURGE` | `.aoe(...)` → `.surgeOnHit(SoulSkillParameters.LURE_SURGE_RADIUS)` |

`spawnSurgeAt` 用的是同一发弹幕的**当前伤害**（也就是施法武器快照伤害），
范围取 `LURE_SURGE_RADIUS`，纵向与时长沿用 `SPIRIT_SURGE_VERTICAL_RANGE` / `SPIRIT_SURGE_DURATION_TICKS`，
所以外观与普通灵魂激荡完全一致 —— 只是中心不在玩家身上，而在敌人身上。

### 24.2 为什么衍生攻击一律不扣魂

灵魂的扣费**只在施法那一刻发生**：

```
castSoulSkill(player, skillStack)
  └─ prepareSoulConsumption(player, skill.getSoulCost(skillStack))   // 唯一扣费点
  └─ skill.use(player, skillStack)                                   // 技能内部只生成实体
```

所以技能内部生成的：引魂弹幕的弹射、引魂激荡的激荡、灵魂引发的连锁弹幕、
魂引天星的坠星、灵魂突击的子弹，都不会再走一次扣费。本轮没有改这条链路，
只是把「引魂激荡的激荡」也归进这一类，因此**不需要额外写耗魂判断**。

> 全树 `consumeSoul(...)` 只剩 6 处，全部在"施法 / 开枪 / 装瓶"这类主动入口上
> （`SoulAssaultSkill` 按子弹数、`KarmaFlameSkill` 按处决数、`BoilingBloodSkill`、
> `GunFiringService`、`SoulItems` 装瓶、灵魂吞噬击杀）。衍生实体一律没有引用。

### 24.3 灵魂激荡冲击：终结爆炸带击退，但不推自己

| 项 | 旧 | 新 |
| --- | --- | --- |
| 终结爆炸 | 半径 9.6、伤害 24 的普通激荡 | 同尺寸，**额外带 1.2 的击退** |
| 中心 | 玩家脚下（`SpiritSurgeEntity` 构造时 `setPos(owner.position())`） | 不变 |
| 施法者自身 | —— | **永远不会被推走**（`applyKnockback` 首行 `target == owner` 直接返回） |

实现：`SpiritSurgeEntity` 新增 `knockback` 字段 + `withKnockback(double)`，
命中结算时调用 `applyKnockback(target, owner)`：

```java
Vec3 direction = horizontal.normalize().scale(knockback);
target.push(direction.x, 0.35 * Math.min(1.0, knockback), direction.z);
target.hurtMarked = true;   // 位移同步到客户端
```

- 水平方向以激荡中心为原点向外推；
- 竖直分量固定 `0.35 × min(1, 击退)`，不会把人掀飞；
- 只有终结爆炸调用 `withKnockback`，普通激荡 / 强力激荡 / 引魂激荡都不击退。

### 24.4 第十五轮验证

| 步骤 | 1.21.1 | 1.20.1 |
| --- | --- | --- |
| `:ConfluenceOtherworld:compileJava` | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| `:ConfluenceOtherworld:runData` | BUILD SUCCESSFUL | BUILD SUCCESSFUL |
| `surgeOnHit` 写入点（定义 / 弹幕 / 弹射） | 3 | 3 |
| `withKnockback` 调用点 | 1（仅终结爆炸） | 1（仅终结爆炸） |
| `LURE_SURGE_AOE_DAMAGE` 残留引用 | 0 | 0 |

> 本轮没有新增语言键、标签或物品，`runData` 产物与第十四轮一致（已复跑确认）。