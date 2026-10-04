# WP2 批次 6 · 小型物种 25 个（+ 5 条新规则）

## 一、边界

先按上一轮的做法**逐个测每个种子的独立闭包**（一次性打包测会互相污染、看不出谁在拖后腿）：
候选 39 个里，闭包 1~4 的有 20 个，闭包 90 的有 6 个（`HillHungry` / `Werewolf` / `FireImp` /
`EaterOfSouls` / `WindyBalloon` / `DungeonSpirit` / `Corruptor` / `DarkCaster` / `BloodTumor` /
`HungryMovementAction`）→ 只取前者。

20 个种子合并后的闭包 = **28 个**（多出 8 个：`GoblinMonster`、`PlantStemPart`、`Snatcher`、
`SnatcherMovementAction` + 4 个弹幕），其中 3 个落地时又被编译器挡回（见第四节），
**最终落地 25 个 / 1953 非空行**：

| 类别 | 文件 |
|---|---|
| 怪物（20） | `Antlion`(116)、`MartianProbe`(241)、`Pixie`(135)、`FrostFighter`(137)、`Demon`(130)、`BloodCrawler`(110)、`SandPoacher`(102)、`Unicorn`(87)、`Derpling`(54)、`GoblinMonster`(53)、`GoblinArcher`(41)、`Paladin`(39)、`AntlionCharger`(34)、`AngryTumbler`(33)、`WoodenMimic`(30)、`SporeZombie`(30)、`EvilPenguin`(29)、`AngerGoblin`(23)、`SnatcherMovementAction`(76)、`PlantStemPart`(71) |
| 连带闭包（5） | `Snatcher`(290)、`MartianProbeEventTrigger`(7)、`AntlionSandBall`(44)、`DesertSpiritCurse`(62)、`HostileDemonScytheProjectile`(47)、`PaladinHammerProjectile`(28) |

（表内数字为非空行；`DesertSpirit`(45) 也在本批。）

外加既有文件改动：`ModEntities` **+6 条弹幕注册**
（`ANTLION_SAND_BALL` / `DESERT_SPIRIT_CURSE` / `HOSTILE_DEMON_SCYTHE` /
`PALADIN_HAMMER_PROJECTILE` / `FROST_BEAM` / `ICEWATER_SPIT`，参数逐条照抄 1.20 的
`common/init/entity/ModEntities.java`；后两条是 `FrostMonsterProjectile` 的另外两个 Kind）。

## 二、本批新增 5 条规则（都是被编译错误逼出来的）

### 1. `SoundEvents` 的 Holder 字段不是 22 个，是 **117** 个（**上一轮的规则是错的**）

`rules/soundevents-holder-1.21.json` 由 **`tools/port2native/gen_sounds_rule.py` 从源码重新生成**。
重新扫描 1.21.1 `SoundEvents.java` 的真实分布：

| 声明类型 | 字段数 |
|---|---|
| `SoundEvent` | 1486 |
| `Holder<SoundEvent>` | 22 |
| **`Holder.Reference<SoundEvent>`** | **95** |
| 其它 | 2 |

上一轮我只扫了 `Holder<SoundEvent>` 与 `SoundEvent` 两种写法，**整批 `Holder.Reference` 漏掉了**
（`GENERIC_EXPLODE` 就在里面），由 `DesertSpiritCurse` 的
「`Reference<SoundEvent>` cannot be converted to `SoundEvent`」暴露。
工具内置自检：必须命中 `GENERIC_EXPLODE` / `TRIDENT_THROW`，且**不得**命中 `DOLPHIN_ATTACK`
与已经带 `.value()` 的写法 —— 防止把 1486 个裸 `SoundEvent` 误伤。

### 2. `ForgeEventFactory.getMobGriefingEvent` → `EventHooks.canEntityGrief`（已固化）

NeoForge 的对应物是 `net.neoforged.neoforge.event.EventHooks.canEntityGrief(Level, Entity)`。
逐个核对 1.21.1 源码：`Entity.java:3718`、`LivingEntity.java:1445`、`Mob.java:577`、
`EatBlockGoal.java:73`、`SnowGolem.java:98`、`WitherBoss.java:328` 等 10+ 处都是这个形式。
规则带 `typeImports` + `addImports:true`（否则调用点缺 import）。

### 3~6. `defineSynchedData` 的四处配套规则（把反复出现的手术自动化）

1.21.1 `Entity.java:342` 是 `protected abstract void defineSynchedData(SynchedEntityData.Builder builder)`，
1.20.1 同处是无参。这条手术**跨批次已手工做过 16 次**
（`BaseBossPart`/`BaseLivingBossPart`/`BaseWormPart`/`SlimeSpikeEntity`/`StraightMonsterProjectile`/
`BaseMimic`/`Crawdad`/`DemonEye`/`GraniteElemental`/`GraniteGolem`/`Hoplite` + 本批 5 处），
现在拆成 4 条规则：改签名、改 `super.defineSynchedData()` 调用、
`this.entityData.define(` → `builder.define(`、`entityData.define(` → `builder.define(`。
**顺序有讲究**：带 `this.` 的必须排在前面，否则会被后一条切成 `this.builder.define(`。
本批实测：5 处全部自动改完，只剩 1 处人工（`AngryTumbler`）。

### 7. `getAttributeValue(Attribute)` → `(Holder<Attribute>)`（已固化）

同一条手术也做过多次（`BaseWormMonster`/`ZombieMerman`/`AngryTumbler`），固化成规则并补 `Holder` import。
只匹配**方法签名**写法，不影响调用点传参。

## 三、校验

- `:ConfluenceOtherworld:compileJava` 通过（BUILD SUCCESSFUL）
- 转换器 `uncovered 0` / `leftovers 0`
- 25 个文件里无 `org.mesdag.portlib` / `getRandom1211` / `BlockPathTypes` / `.unwrap()`
  / `onAddedToWorld` / `entityData.define(` 残留
- 规则文件 19 条，`json.load` 通过、逐条 `pattern`/`replace` 非空、新规则带正反样本自检

## 四、推迟了什么、为什么

| 推迟项 | 原因 |
|---|---|
| `Nymph`(169) | 用 `LibEffects.CONFUSED`（Magic-Lib 的 `LibEffects` 未移植）+ `getStandingEyeHeight` |
| `SpittingPlant`(44) | 用 `LibEffects.CONFUSED` |
| `Gnome`(37) | 用 `DecorativeBlocks.GARDEN_GNOME`（该方块未移植） |
| 闭包 90 的 10 个 | `HillHungry` / `Werewolf` / `FireImp` / `EaterOfSouls` / `WindyBalloon` / `DungeonSpirit` / `Corruptor` / `DarkCaster` / `BloodTumor` / `HungryMovementAction` —— 都指向同一批未移植的实体/弹幕 |
| `ClimbingSpider` / `SpiderWebSpit` | 仍等 `LibEffects`（上一轮已记录，需先决定它与 `TCEffects.GRAVITATION` 的关系） |

## 五、两处过程失误（都是自己刚立的规矩抓到的）

1. **又把规则文件的 `pattern` 字段删掉了。** 给 `rules/vanilla-api-1.20-to-1.21.json` 追加规则时，
   `old_string` 只写到 `"id": "..."` 那一行，等于把下一条规则的 `pattern` 行吃掉 —— 与上一轮
   一模一样的错，**在同一轮里犯了两次**。两次都是靠「改完规则文件立刻 `json.load` +
   逐条打印 `id/pattern`」发现的。这条校验必须固化成肌肉记忆；已经写进 README 的过程纪律。
2. **PowerShell 内插字符串把正则写坏了。** 用 `@"..."@` 传 python 源码时，
   `'\\\\bSoundEvents\\\\.'` 经过 PowerShell 与 Python 两层转义后，落进 JSON 的是 `\\bSoundEvents\\.`，
   正则于是要求一个字面反斜杠 —— 规则「存在但永不命中」。
   解法是把生成逻辑写成独立工具 `gen_sounds_rule.py`（带自检），**不再把正则塞进 here-string**。

## 六、复现命令

```powershell
# 1) 逐个测种子闭包，挑出自成一类的
python tools/port2native/dep_subset.py --src120 <1.20 src> --root121 ConfluenceOtherworld/src/main/java `
  --root121 Confluence-Magic-Lib/src/main/java `
  --alias "org.confluence.mod.common.init.entity.ModEntities=org.confluence.mod.common.init.entity.ModEntities" `
  --seed org/confluence/mod/common/entity/monster/Antlion.java --defer <各 hub> --out <报告>

# 2) 按报告里的列表暂存/转换/合并（stage_batch.py 接受任意多个 --file）
python tools/port2native/stage_batch.py --name wp2f --src120 <1.20 src> `
  --dest ConfluenceOtherworld/src/main/java --file <...> --convert --apply

# 3) 重生成 SoundEvents 规则（改了依赖版本时要重跑）
python tools/port2native/gen_sounds_rule.py --jar ConfluenceOtherworld/build/moddev/artifacts/neoforge-21.1.219-sources.jar
```
