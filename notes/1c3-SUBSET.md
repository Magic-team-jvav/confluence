# 最大可编译子集（`tools/port2native/dep_subset.py` 输出）

- 种子 4 个：`AttackEffects`, `CasterCycleAction`, `ShootSpikesAction`, `WormMovementAction`
- 扩张后候选 **10** 个 `org.confluence.*` 类型
- 本批保留 **10** 个（其中 1.21 侧**新增** 10 个），因 `--defer` 剔除 **0** 个

## 本批新增（直接拷贝即可）

- `org.confluence.mod.common.data.map.AttackEffects`
- `org.confluence.mod.common.entity.ai.bt.leaf.CasterCycleAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.ShootSpikesAction`
- `org.confluence.mod.common.entity.ai.bt.leaf.WormMovementAction`
- `org.confluence.mod.common.entity.monster.BaseCasterMonster`
- `org.confluence.mod.common.entity.monster.BaseWormMonster`
- `org.confluence.mod.common.entity.monster.BaseWormPart`
- `org.confluence.mod.common.entity.monster.WormSegment`
- `org.confluence.mod.common.entity.projectile.SlimeSpikeEntity`
- `org.confluence.mod.common.entity.projectile.StraightMonsterProjectile`

## 被 `--defer` 剔除（连依赖链一起还给后续批次）

_无_
