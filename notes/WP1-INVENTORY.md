# WP1 · 生物架构基座 —— 待搬文件清单（可直接开工）

来源：`notes/WORK-QUEUE.md` 的 WP1；数据取自 `notes/FILE-LAG.md`（逐文件 overlap）。
`M` = 1.21 完全没有这个文件；`L` = 1.21 有同名文件但内容落后（数字为重叠率）。
行数取自 1.20 HEAD 的工作树。

## 一、基类与工具

| | 行数/重叠 | 文件 |
|---|---|---|
| M | 82 | `common/entity/EnemyTargeting.java` |
| M | 30 | `common/entity/EnemyDamageRules.java` |
| M | 19 | `common/entity/PartHitTarget.java` |
| M | 8 | `common/entity/MoneyDropSource.java` |
| L | 1% | `common/entity/SpawnPlacementChecks.java` |
| L | 0% | `common/entity/IVariant.java` |
| L | 45% | `common/entity/SpearEntity.java` |

**基点：9 个文件，其中 MISSING 139 行。**

## 二、行为树 AI（`common/entity/ai/**`，57 个文件 / MISSING 3517 行）

### 框架核心（**这些是 LAGGING，必须先精读 1.21 现有版本再改，不能当新文件覆盖**）

| 重叠 | 文件 |
|---|---|
| 8% | `ai/bt/Blackboard.java` |
| 33% | `ai/bt/BTNode.java` |
| 39% | `ai/bt/BTRoot.java` |
| 0% | `ai/bt/condition/Condition.java` |
| 19% | `ai/bt/composite/SelectorNode.java` |
| 29% | `ai/bt/composite/SequenceNode.java` |
| 10% | `ai/bt/leaf/JumpAttackAction.java` |
| 11% | `ai/bt/leaf/MoveToTargetAction.java` |
| 23% | `ai/bt/leaf/DashAction.java` |
| 27% | `ai/bt/leaf/RandomStrollAction.java` |
| 42% | `ai/bt/leaf/WaitAction.java` |

> ⚠️ 1.21 已经有一套行为树（`BTNode`/`BTRoot`/`Blackboard`/`SequenceNode`/`SelectorNode`），
> 但只有 1.20 的三分之一。**这是一种"演进"而不是"搬迁"**：先做 `git diff 1.20 与 1.21 的同名文件`，
> 判断是纯功能增补（直接搬）还是结构变化（需要人工重写）。这是 WP1 的第一个必做动作。

### 新增节点（MISSING，46 个）

- **组合/条件**：`ConditionalSwitchNode`(64)、`RoundRobinSelectorNode`(74)、`HasTargetCondition`(12)、
  `PlayerCloseCondition`(24)、`TargetWithinRangeCondition`(23)、`BTStatus`(7)
- **战斗类 leaf**：`BowCombatAction`(194)、`CasterCycleAction`(211)、`ChargeAttackAction`(86)、
  `MeleeAttackAction`(45)、`RangedWindupAction`(40)、`SpawnArrowAction`(98)、`SpawnProjectileAction`(48)、
  `ShootSpikesAction`(45)、`CircleAroundTargetAction`(51)、`MaintainRangedDistanceAction`(80)、
  `SweptContactAttack`(48)
- **移动类 leaf**：`SteeringDashAction`(196)、`WanderDashCycleAction`(106)、`JumpingMonsterCycleAction`(132)、
  `JumpOverBlockAction`(52)、`SlimeHopAction`(82)、`RandomSwimAction`(72)、`FlyWanderAction`(49)、
  `LookForwardWanderFlyAction`(91)、`PhasedFlyingPursuitAction`(114)、`FlyingPursuitAction`(57)、
  `StraightFlyingPursuitAction`(53)、`DirectFloatingPursuitAction`(101)、`TeleportNearTargetAction`(89)
- **恶魔眼专用**：`DemonEyeLeaveAction`(42)、`DemonEyeSurroundAction`(145)、`DemonEyeWanderAction`(73)
- **蠕虫/多体节**：`WormMovementAction`(246)、`WormChainTrail`(158)、`BossMinionCoordinator`(79)
- **工具/杂项**：`GameTickCooldown`(24)、`PanicFleeAction`(64)、`VanillaGoalAction`(60)、
  `EnemyWalkNodeEvaluator`(19)、`SelectiveBlockCollision`(47)
- **传统 goal（新版共存）**：`ai/goal/AcceleratingMeleeAttackGoal`(44)、`AquaticRandomSwimmingGoal`(25)、
  `EnemyBreakDoorGoal`(14)、`EnemyOpenDoorGoal`(19)

## 三、生物数据（`common/data/map/**`，MISSING 475 行）

| | 行数 | 文件 |
|---|---|---|
| M | 321 | `CreatureDefinition.java`（生物的声明式定义：属性/行为/掉落/生成） |
| M | 154 | `AttackEffects.java` |
| P | — | 4 个 PARTIAL（`CreatureAttributeBuilder` 等，需 diff） |

## 四、实体注册（`common/init/entity/**`，MISSING 1858 行）

| | 行数 | 文件 |
|---|---|---|
| M | 945 | `MonsterEntities.java`（**怪物全家注册，最大单文件**） |
| M | 263 | `CreatureSpawnPlacements.java` |
| M | 259 | `NpcEntities.java` |
| M | 187 | `BossEntities.java` |
| M | 177 | `CritterEntities.java` |
| M | 27 | `DevelopmentSpawnPolicy.java` |
| L | 0% | `ModEntities.java`（1.21 是 TerraEntity 时代形态，需重写） |

**注意**：注册文件引用全部物种类 → **注册与物种必须同批落地才编得过**（见下「批次」）。

## 五、相关但可稍后

- `common/component/**`：13 个（MISSING `SpearProjectileComponent` 170、`SwordProjectileAppearance` 160、
  `SwordProjectileParticleEffect` 59；LAGGING `FlailComponent` 7%、`GunPropertyComponent` 45%、`BulletPropertyComponent` 48% 等）
- `common/event/**`：10 个（MISSING `game/GunEvents` 46；LAGGING `NetworkEvents` 2%、`game/GameEvents` 9%）

## 六、批次与编译门

**WP1 不能一次编过**：基类/注册必须与物种同批出现。建议批次（每批一个提交序列、每批末尾必须过编译）：

| 批 | 内容 | 出口 |
|---|---|---|
| **1a** | `common/entity` 根 7 个 + `data/map` 2 个 + `ai/bt` 框架 11 个 LAGGING 的 diff 与增补 | `:ConfluenceOtherworld:compileJava` 过（此时新基类已存在，但没人用） |
| **1b** | `ai/**` 46 个新节点落地 | 编译过 |
| **1c** | `init/entity` 注册体系（`ModEntities` 重写 + 新注册类）**+ 首批物种**（挑一类：如 cave 系怪物） | 编译过 + `runClient` 能进世界看到该物种 |
| **1d** | 逐族补齐物种（接 WP2） | 每族一次编译 |

**开工前必做的一件事 —— 已经查清了（2026-09-27）**：

1.21 的 `ConfluenceOtherworld` 里**根本没有** `common/entity/ai/` 这个目录。`FILE-LAG.md` 里那些 `L` 判定是
**跨模块同名匹配**（`file_lag.py` 的兜底规则）算出来的，1.21 的行为树实际住在：

```
TerraEntity/src/main/java/org/confluence/terraentity/entity/ai/goal/behavior/
    BTNode.java  BTRoot.java  blackboard/Blackboard.java
    condition/Condition.java  composite/{SelectorNode,SequenceNode}.java  leaf/{WaitAction,RandomStrollAction,JumpAttackAction,...}.java
```

也就是说：**1.20 把 TerraEntity 的这套东西内联进了主模组、并且重写了一版**（1.20 的 `Blackboard` 与 1.21 的只有 8% 重叠、
`BTNode` 33%、`SelectorNode` 19%），路径也从 `terraentity.entity.ai.goal.behavior.**` 变成了 `mod.common.entity.ai.bt.**`。

**结论（修正 WP1 的性质）**：

- **不要**拿 1.20 的 `ai/bt/**` 去和 TerraEntity 的同名文件做 diff —— 那是在跟一个**即将退役**的模块比对（Q3/Q4：1.21 向 1.20 对齐，
  TerraEntity/TerraGuns 退役，其独有内容不收割）。按**新代码**搬：1.20 的 `mod.common.entity.ai.**` 直接作为新包落地。
- **顺序必须是「先加后删」**：TerraEntity 现有生物仍在引用旧框架，所以
  1a→1b→1c 期间**不要动 TerraEntity**（新增包与旧包并存，只是暂时重复）；
  等 1.20 的物种与注册体系补齐（WP2 完毕后），再一次性把 TerraEntity/TerraGuns 的生物删掉、
  把 `mods.toml`/`build.gradle`/`settings.gradle` 的模块依赖摘掉。**避免大爆炸式中断**。
- **`TerraEntity` 里的非生物设施**（若有 1.20 侧没有的通用能力）按 Q4/Q9 **不收割**，随模块一起退役；
  若退役时发现主模组确实缺这个能力，再单独评估（记进 `notes/PORT-RESIDUALS.md`）。
