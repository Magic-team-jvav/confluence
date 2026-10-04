# WP2 批次 10 · 注册层第二片 + 施法者群与 `HostileParticleProjectile` 枢纽

> 对象：1.20.1 的 `common/entity/projectile/HostileParticleProjectile`（306 行）与 6 个使用者
> `DarkCaster`(94) `FireImp`(45) `Gastropod`(183) `GoblinWarlock`(130) `WaterBoltMimic`(145) `Corruptor`(55)。

## 一、为什么先打这个枢纽

`notes/WP2I-CLOSURES.md` 的公共底座表里 `common.entity.projectile.HostileParticleProjectile`
被 **24 个种子**需要，是待移植物种里最大的单点。它落地后这 6 个施法者会一起放行。

开工命令（把注册层当已满足，只看还剩多少）：

```powershell
python tools/port2native/dep_subset.py --src120 <1.20 src/main/java> `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --alias "org.confluence.mod.common.init.entity.ModEntities=org.confluence.mod.common.init.entity.ModEntities" `
  --alias "org.confluence.lib.util.LibEntityUtils=org.confluence.lib.util.LibUtils" `
  --defer org.confluence.mod.common.init.entity.MonsterEntities `
  --seed …（7 个）… --out <报告>
# 结果：candidates 7 / kept 7 / new 7 / removed 0
```

### ⚠️ `--defer` 报「多余」是本批最值得记的一件事

那次运行 `unused_defer: 1` —— 工具说 `MonsterEntities` 根本没被引用。原因是**批次 9 刚把它建出来了**：
闭包判据是「这个类型在 1.21 侧存不存在」，类一存在，整条边就被当成已满足。
**但成员级需求一分没少**：这 7 个文件实际用到 `ModEntities` 的 9 个成员
（`RUNE_BLAST` `DARK_CASTER_PROJECTILE` `CHAOS_BALL_PROJECTILE` `SHADOW_BEAM_PROJECTILE`
`INFERNO_BOLT_PROJECTILE` `LOST_SOUL_PROJECTILE` `VILE_SPIT_PROJECTILE` `FIRE_IMP_PROJECTILE`
`GASTROPOD_PROJECTILE`）与 `MonsterEntities.SHADOWFLAME_APPARITION`，而当时一个都没有。
**「把类建出来」会让工具从「算多了」翻成「算少了」** —— 这是成员级盲区的第 5 例、也是方向相反的一例。
以后新增注册类时必须同时把**成员**列出来核对，不能只看闭包。
（实测：只暂存枢纽那一个文件时编译器立刻报 `Corruptor` / `WaterBoltMimic` 找不到 —— 枢纽反向引用了两个使用者，
所以这 7 个文件必须同批落地。）

## 二、本批落地

| 项 | 内容 |
|---|---|
| 新增物种/射弹 7 个 | `HostileParticleProjectile`(306) `DarkCaster`(94) `FireImp`(45) `Gastropod`(183) `GoblinWarlock`(130) `WaterBoltMimic`(145) `Corruptor`(55) = **958 非空行** |
| `ModEntities` | +9 条怪物射弹注册（上列成员，尺寸/`noSave`/`updateInterval` 全照 1.20；`RUNE_BLAST` 在 1.20 侧没有 `.noSave()`，故只有它不一样） |
| `MonsterEntities` | +`registerCaster` 辅助 + 7 条注册（6 个物种 + `SHADOWFLAME_APPARITION`） |
| `DevelopmentSpawnPolicy` | 新增（`common/init/entity/`，1.20 的 21 行版），1.21 侧把 `RegistryObject` 换成 `DeferredHolder`（两者都有 `getId()`） |
| `Confluence` | +`UNRELEASED_SPAWNS = LibUtils.isDev()`（1.20 `Confluence.java:55` 同名字段；`DevelopmentSpawnPolicy.allowsAutomaticSpawn` 读它） |
| 粒子 | `ModParticleTypes` +`SPIT_GLOW`（`VILE_SPIT` 用），并从 1.20 复制 `particles/spit_glow.json` + `textures/particle/spit/spit_0..5.png`（6 张，逐字节复制） |

`SHADOWFLAME_APPARITION` 是补注册：类在批次 7 就已落地，`GoblinWarlock` 引用
`MonsterEntities.SHADOWFLAME_APPARITION`（召唤物），不补编不过。

## 三、本批的 API 差异：`finalizeSpawn` 第 5 形参（第 3 例，最简形态）

1.20.1 `Mob.java:1097` 有 `@Nullable CompoundTag tag`、1.21.1 `Mob.java:1192` 没有。
前两例（`DemonEye`、`humanoid/Zombie`）都用 `tag` 判断「本次生成是否已带变体 NBT」，因此要补标记位；
本批 `WaterBoltMimic` 的 `tag` **只被透传给 super**、正文没用到，所以**去掉形参即可**。
两种处理都不进规则（是语义判断，不是机械替换），只记在这里以便下次少走弯路。

## 四、推迟了什么、为什么

| 推迟项 | 原因 |
|---|---|
| slime 全族 + `Werewolf` / `VisualNeuron` / `TheHungry` 族 | 仍卡在 `TownSlimeNPC` → NPC 层（WP4），见 `notes/WP2I-SUBSET.md` 第五节 |
| `WindyBalloon` | 同上（引用 `MonsterEntities.BLUE_SLIME` → `BaseSlime`） |
| `Gnome` | 卡方块层的 `DecorativeBlocks.GARDEN_GNOME` |
| `LittleHornet`(191+2) `SimpleWormMonster`(220+2) `PirateRangedMonster`(156+2) | 闭包只剩 2 个新增，是下一批的候选；`SimpleWormMonster` 还要 `registerWorm` 系列辅助与 `BaseWormPart` 段体 |
| `DungeonSpirit` / `HillHungry` / `HungryMovementAction` | 闭包 199~200，与 slime 同因 |

## 五、验证

```powershell
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 总错误数: 0，涉及 0 个文件；[build] exit=0
```

转换器报告：两次都是 `uncovered.json` = `[]`、`leftovers.txt` 空。
残留自查：本批 9 个 java 文件里 `portlib|PortLib|Port[A-Z]\w+` 命中 **2** 处，
都是 `MonsterEntities` 里我自己写的说明注释（"逐条照 1.20 搬，只去掉 PortLib"），不是代码残留。
跨模块核对：`HostileParticleProjectile` 反向引用的 `Corruptor` / `WaterBoltMimic` 与本批同时落地，
没有留下悬空引用。
