# 最大可编译子集（`tools/port2native/dep_subset.py` 输出）

- 种子 19 个：`BowCombatAction`, `ChargeAttackAction`, `CircleAroundTargetAction`, `DashAction`, `DemonEyeLeaveAction`, `DemonEyeSurroundAction`, `DemonEyeWanderAction`, `DirectFloatingPursuitAction`, `FlyingPursuitAction`, `FlyingVolleyCombatAction`, `JumpAttackAction`, `JumpingMonsterCycleAction`, `LookForwardWanderFlyAction`, `MaintainRangedDistanceAction`, `PhasedFlyingPursuitAction`, `RangedWindupAction`, `SteeringDashAction`, `StraightFlyingPursuitAction`, `WanderDashCycleAction`
- 扩张后候选 **20** 个 `org.confluence.*` 类型
- 本批保留 **20** 个（其中 1.21 侧**新增** 20 个），因 `--defer` 剔除 **0** 个

## 本批新增（直接拷贝即可）

- `org.confluence.mod.common.entity.ai.bt.leaf.BowCombatAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.ChargeAttackAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.CircleAroundTargetAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.DashAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.DemonEyeLeaveAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.DemonEyeSurroundAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.DemonEyeWanderAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.DirectFloatingPursuitAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.FlyingPursuitAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.FlyingVolleyCombatAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.JumpAttackAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.JumpingMonsterCycleAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.LookForwardWanderFlyAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.MaintainRangedDistanceAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.PhasedFlyingPursuitAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.RangedWindupAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.SpawnArrowAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.SteeringDashAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.StraightFlyingPursuitAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.WanderDashCycleAction`

## 被 `--defer` 剔除（连依赖链一起还给后续批次）

_无_
