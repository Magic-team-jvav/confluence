# WP2 批次 1 · 飞行/水生怪物基类层（9 文件）

> 本文是策展版说明。原始输出由 `tools/port2native/dep_subset.py` 生成（命令见文末）；
> 由于文件已落进 1.21 树，现在重跑该命令会显示「新增 0 / 已存在 9」，属正常。

## 一、边界

从 9 个种子出发算出的 `org.confluence.*` 闭包**恰好是这 9 个**（候选 9、无需 `--defer`）：

| 文件 | 非空行 | 直接依赖 |
|---|---|---|
| `BaseFlyingMonster` | 65 | `BaseMonster`（1c-1 已落地） |
| `BaseAquaticMonster` | 107 | `BaseMonster` |
| `ReboundingFlyingMonster` | 33 | `BaseFlyingMonster` + `EntityAccessor`（mixin） |
| `RangedFlyingMonster` | 52 | `BaseFlyingMonster` + BT 节点（1a/1c-2 已落地） |
| `SimpleFlyMonster` | 101 | `BaseFlyingMonster` + BT |
| `FlyingFishMonster` | 80 | `BaseFlyingMonster` + BT |
| `PhasingChargeMonster` | 16 | `SimpleFlyMonster` |
| `PirateFlyingMonster` | 36 | `BaseFlyingMonster` + BT |
| `humanoid/BaseHumanoidMonster` | 11 | `BaseMonster`（11 行的薄壳，人形系中间基类） |

合计 **501 非空行**。另改既有文件 1 个：`mixin/world/entity/EntityAccessor`（补一个 `@Invoker`，见下）。

这条切法能这么干净，是因为 1a/1b/1c 已经把 BT 节点、`BaseMonster`、`CreatureDefinition`
全部铺好 —— 这批只是把它们组装成「飞行系」的中间基类，不引入任何新子系统。

## 二、两处 API 手术

### 1. `BaseAquaticMonster`：两个 1.21 删掉 / 封死的钩子

**(a) 寻路钩子被删。** 1.20 覆写的是
`SwimNodeEvaluator#getBlockPathType(BlockGetter, int,int,int, Mob)`（1.20.1 `NodeEvaluator` 有它），
1.21.1 的 `NodeEvaluator` 只剩 `getPathTypeOfMob` / `getPathType`，只给坐标 + `PathfindingContext`。
改法与 1b 的 `EnemyWalkNodeEvaluator` 同构：改覆写 `getPathTypeOfMob`，
方块视图从 `PathfindingContext#level()` 取 —— 它**直接**返回 `CollisionGetter`
（`PathfindingContext.java:37`），因此不再需要 1.20 那句 `blocks instanceof CollisionGetter` 判断。

**(b) `canBreatheUnderwater()` 变成 final。** 1.21.1 的
`LivingEntity#canBreatheUnderwater()` 是 `public final`（`LivingEntity.java:382`），本体改为查
`EntityTypeTags.CAN_BREATHE_UNDER_WATER`，并且它自带
`@Deprecated //FORGE: Use canDrownInFluidType instead`。NeoForge 给的替代钩子是
`ILivingEntityExtension#canDrownInFluidType(FluidType)`（`ILivingEntityExtension.java:55`），
其水类型的默认实现恰好就是 `!self().canBreatheUnderwater()`。
**因此改覆写 `canDrownInFluidType` 并在水类型上返回 false**，语义与 1.20 的
`canBreatheUnderwater() { return true; }` 等价 —— 这是 NeoForge 官方指明的做法，不是绕路。

（另一条路是把水生生物类型加进 `EntityTypeTags.CAN_BREATHE_UNDER_WATER`；但那要等物种注册层落地，
现在没有可加的 `EntityType`，所以走前者。）

### 2. `EntityAccessor`：1.21 侧漏了一条 invoker

`ReboundingFlyingMonster` 调 `((EntityAccessor) this).callCollide(movement)` 来用 `Entity` 的私有
`collide(Vec3)`（1.21.1 `Entity.java:901`）。1.20 侧 `EntityAccessor` 本来就有这条（注释写着
`todo AT`），1.21 侧那份只有 `callReadAdditionalSaveData`，于是**补齐 `@Invoker Vec3 callCollide(Vec3 motion);`**。

Mixin 目标已用 `tools/port2native/check_mixin_targets.py` 审计：`TARGET-OK`。
**注意该工具的 `--reference` 必须是解包后的源码目录**，给 jar 会一律报 `TARGET-MISSING`（本轮踩到）。

## 三、推迟了什么、为什么

| 推迟项 | 非空行 | 直接原因 |
|---|---|---|
| `BaseMimic` | 334 | 需要 `ModSoundEvents.SOUL_DEATH` / `METAL_HURT` |
| `BaseWarriorMonster` | 274 | 需要 `ModSoundEvents.TR_ZOMBIE_FREE` / `TR_ZOMBIE_DEATH` / `FACE_HOOT` / `METAL_HURT` / `SOUL_DEATH` |
| 连带推迟：`ChargingMonster` / `RangedMonster` / `HumanoidWarriorMonster` / `JumpingWarriorMonster` | 230 | 都继承 `BaseWarriorMonster` |

**根因可量化**：1.21 侧 `ModSoundEvents` 只有 **31** 条，1.20 侧有 **501** 条 ——
缺 469 条，属 WP6/WP7（音效注册 + `sounds.json` + `.ogg` + 字幕）的欠账。
本批需要的 5 条（`soul_death` / `metal_hurt` / `tr_zombie_free` / `face_hoot` / `tr_zombie_death`）
在 1.21 侧 **ogg 与 sounds.json 条目都不存在**（1.20 侧 ogg 齐全），
所以顺序只能是「先做音效搬迁批，再做这些物种基类」，不能在这里顺手塞。

其余推迟项（后续批次）：
`slime/BaseSlime`（需 `MonsterEntities` / `ModTags` / `SweetSlime` / `TownSlimeNPC`）、
`SimpleWormMonster`（需 `MonsterEntities` / `SandstormGameEvent` / `OverworldUtils`）、
`MonsterAttributeScaling`（需 `BaseCritter` / `BaseNPC` / `SweetSlime`）、
`PirateRangedMonster`（需 `MonsterEntities` / `PirateInvasionGameEvent` / `PirateShot`），
以及 108 个 `common/entity/monster/**` 缺文件里剩下的具体物种（共 11399 非空行）。

## 四、复现命令

```powershell
python tools/port2native/dep_subset.py `
  --src120  D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --alias   "org.confluence.mod.common.init.entity.ModEntities=org.confluence.mod.common.init.entity.ModEntities" `
  --seed    org/confluence/mod/common/entity/monster/BaseFlyingMonster.java `
  --seed    org/confluence/mod/common/entity/monster/BaseAquaticMonster.java `
  --seed    org/confluence/mod/common/entity/monster/ReboundingFlyingMonster.java `
  --seed    org/confluence/mod/common/entity/monster/RangedFlyingMonster.java `
  --seed    org/confluence/mod/common/entity/monster/SimpleFlyMonster.java `
  --seed    org/confluence/mod/common/entity/monster/FlyingFishMonster.java `
  --seed    org/confluence/mod/common/entity/monster/PhasingChargeMonster.java `
  --seed    org/confluence/mod/common/entity/monster/PirateFlyingMonster.java `
  --seed    org/confluence/mod/common/entity/monster/humanoid/BaseHumanoidMonster.java `
  --out     notes/WP2A-SUBSET.md
```
