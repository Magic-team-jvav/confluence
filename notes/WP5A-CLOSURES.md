# 逐种子依赖闭包（`tools/port2native/seed_closures.py` 输出）

- 种子 **88** 个；1.21 侧可扩张类型 698 个
- 口径与 `dep_subset.py` 一致：只往「1.21 侧还没有的类型」里扩张，已满足的边算 0 成本、不继续走
- `--alias` 3 条：`org.confluence.lib.util.LibEntityUtils` → `org.confluence.lib.util.LibUtils`, `org.confluence.mod.common.data.GamePhase` → `org.confluence.mod.common.data.saved.GamePhase`, `org.confluence.mod.common.init.entity.ModEntities` → `org.confluence.mod.common.init.entity.ModEntities`

## 按单独闭包从小到大（轻量 ≤12 个文件：**9** 个种子）

| 种子 | 闭包文件 | 其中新增 | 非空行 | 种子自身行 | 1.21 已有同名 |
| --- | ---: | ---: | ---: | ---: | :--: |
| `SyncFieldDispatcher` | 1 | 0 | 114 | 114 |  |
| `SummonerBatchedParticlesPayload` | 1 | 0 | 55 | 55 |  |
| `PlannedPath` | 1 | 0 | 46 | 46 |  |
| `SummonerSoundEvents` | 1 | 0 | 23 | 23 |  |
| `LyraStreamCodecs` | 1 | 0 | 19 | 19 |  |
| `SummonerModels` | 1 | 0 | 10 | 10 |  |
| `GenericParticleOptions` | 2 | 1 | 99 | 83 |  |
| `SummonerParticleTypes` | 2 | 1 | 99 | 16 |  |
| `GenericParticleBuilder` | 3 | 2 | 177 | 78 |  |

## 重量种子（闭包 >12 个文件：**79** 个）

| 种子 | 闭包文件 | 其中新增 | 非空行 | 种子自身行 | 1.21 已有同名 |
| --- | ---: | ---: | ---: | ---: | :--: |
| `AttachmentEntityData` | 28 | 27 | 2374 | 285 |  |
| `InfoData` | 28 | 27 | 2374 | 86 |  |
| `TargetCache` | 28 | 27 | 2374 | 291 |  |
| `WhipMarkTracker` | 28 | 27 | 2374 | 114 |  |
| `AttachmentEntity` | 28 | 27 | 2374 | 365 |  |
| `AttachmentEntityDamageSource` | 28 | 27 | 2374 | 30 |  |
| `AttachmentEntityGoal` | 28 | 27 | 2374 | 17 |  |
| `AttachmentEntityGoalSelector` | 28 | 27 | 2374 | 50 |  |
| `AttachmentEntityType` | 28 | 27 | 2374 | 20 |  |
| `IBlockCollision` | 28 | 27 | 2374 | 74 |  |
| `IEntityCollision` | 28 | 27 | 2374 | 201 |  |
| `ICarryMinion` | 28 | 27 | 2374 | 28 |  |
| `Minion` | 28 | 27 | 2374 | 117 |  |
| `MinionSlotType` | 28 | 27 | 2374 | 10 |  |
| `SummonerBatchedInfoPayload` | 28 | 27 | 2374 | 51 |  |
| `SummonerParticleData` | 28 | 27 | 2374 | 35 |  |
| `SummonerAttachmentTypes` | 28 | 27 | 2374 | 23 |  |
| `SummonerRegistries` | 28 | 27 | 2374 | 17 |  |
| `SummonMarkInstance` | 28 | 27 | 2374 | 25 |  |
| `SummonMarkType` | 28 | 27 | 2374 | 69 |  |
| `MomentumMinion` | 29 | 28 | 2487 | 113 |  |
| `MomentumAttachmentEntity` | 29 | 28 | 2441 | 67 |  |
| `SummonerHelper` | 29 | 28 | 2432 | 58 |  |
| `GroundMinion` | 30 | 29 | 2624 | 137 |  |
| `SummonerEvents` | 30 | 29 | 2543 | 111 |  |
| `Projectile` | 30 | 29 | 2481 | 40 |  |
| `ParticleHelper` | 32 | 31 | 2696 | 145 |  |
| `SummonerSummonMarks` | 45 | 44 | 3640 | 42 |  |
| `DeadlySphereMinion` | 88 | 87 | 5950 | 74 |  |
| `DesertTigerMinion` | 88 | 87 | 5950 | 76 |  |
| `EyeLaserTurretMinion` | 88 | 87 | 5950 | 112 |  |
| `FinchMinion` | 88 | 87 | 5950 | 99 |  |
| `HornetMinion` | 88 | 87 | 5950 | 49 |  |
| `ImpMinion` | 88 | 87 | 5950 | 57 |  |
| `IronGolemMinion` | 88 | 87 | 5950 | 49 |  |
| `RuinRelicMinion` | 88 | 87 | 5950 | 157 |  |
| `SanguineBatMinion` | 88 | 87 | 5950 | 91 |  |
| `SculkWispMinion` | 88 | 87 | 5950 | 46 |  |
| `SlimeMinion` | 88 | 87 | 5950 | 71 |  |
| `SnowFlinxMinion` | 88 | 87 | 5950 | 50 |  |
| `SpiderMinion` | 88 | 87 | 5950 | 55 |  |
| `TerraprismaMinion` | 88 | 87 | 5950 | 121 |  |
| `VampireFrogMinion` | 88 | 87 | 5950 | 57 |  |
| `DeadlySphereAttackGoal` | 88 | 87 | 5950 | 36 |  |
| `DeadlySphereIdleGoal` | 88 | 87 | 5950 | 36 |  |
| `DesertTigerAttackGoal` | 88 | 87 | 5950 | 75 |  |
| `DesertTigerIdleGoal` | 88 | 87 | 5950 | 38 |  |
| `EyeLaserTurretAttackGoal` | 88 | 87 | 5950 | 22 |  |
| `EyeLaserTurretIdleGoal` | 88 | 87 | 5950 | 21 |  |
| `FinchAttackGoal` | 88 | 87 | 5950 | 37 |  |
| `FinchIdleGoal` | 88 | 87 | 5950 | 26 |  |
| `HornetAttackGoal` | 88 | 87 | 5950 | 66 |  |
| `HornetIdleGoal` | 88 | 87 | 5950 | 38 |  |
| `ImpAttackGoal` | 88 | 87 | 5950 | 50 |  |
| `ImpIdleGoal` | 88 | 87 | 5950 | 37 |  |
| `IronGolemAttackGoal` | 88 | 87 | 5950 | 48 |  |
| `IronGolemIdleGoal` | 88 | 87 | 5950 | 40 |  |
| `RuinRelicAttackGoal` | 88 | 87 | 5950 | 26 |  |
| `RuinRelicIdleGoal` | 88 | 87 | 5950 | 20 |  |
| `SanguineBatAttackGoal` | 88 | 87 | 5950 | 43 |  |
| `SanguineBatIdleGoal` | 88 | 87 | 5950 | 25 |  |
| `SculkWispAttackGoal` | 88 | 87 | 5950 | 79 |  |
| `SculkWispIdleGoal` | 88 | 87 | 5950 | 38 |  |
| `SlimeAttackGoal` | 88 | 87 | 5950 | 21 |  |
| `SlimeIdleGoal` | 88 | 87 | 5950 | 40 |  |
| `SnowFlinxAttackGoal` | 88 | 87 | 5950 | 28 |  |
| `SnowFlinxIdleGoal` | 88 | 87 | 5950 | 40 |  |
| `SpiderAttackGoal` | 88 | 87 | 5950 | 80 |  |
| `SpiderIdleGoal` | 88 | 87 | 5950 | 38 |  |
| `TerraprismAttackGoal` | 88 | 87 | 5950 | 192 |  |
| `TerraprismIdleGoal` | 88 | 87 | 5950 | 21 |  |
| `TerraprismPrepGoal` | 88 | 87 | 5950 | 34 |  |
| `VampireFrogAttackGoal` | 88 | 87 | 5950 | 35 |  |
| `VampireFrogIdleGoal` | 88 | 87 | 5950 | 40 |  |
| `EyeFireball` | 88 | 87 | 5950 | 77 |  |
| `ForbiddenOrb` | 88 | 87 | 5950 | 97 |  |
| `HornetStinger` | 88 | 87 | 5950 | 43 |  |
| `ImpFireball` | 88 | 87 | 5950 | 46 |  |
| `SummonerAttachmentEntityTypes` | 88 | 87 | 5950 | 42 |  |

## 公共底座（出现在 ≥2 个种子的闭包里：**88** 个）

**先搬这些，后面每个种子都会变轻。**

| 类型 | 被几个种子需要 | 非空行 | 自己单独搬时的闭包 |
| --- | ---: | ---: | ---: |
| `client.summoner.LyraRenderTypes` | 79 | 71 | （不是种子） |
| `client.summoner.info.Info` | 79 | 85 | （不是种子） |
| `client.summoner.info.NumberInfo` | 79 | 51 | （不是种子） |
| `client.summoner.info.TextInfo` | 79 | 25 | （不是种子） |
| `common.summoner.LyraStreamCodecs` | 79 | 19 | 1 |
| `common.summoner.attachmentEntity.PlannedPath` | 79 | 46 | 1 |
| `common.summoner.attachmentEntity.SyncFieldDispatcher` | 79 | 114 | 1 |
| `common.summoner.network.SummonerBatchedParticlesPayload` | 79 | 55 | 1 |
| `common.summoner.attachment.AttachmentEntityData` | 78 | 285 | 28 |
| `common.summoner.attachment.InfoData` | 78 | 86 | 28 |
| `common.summoner.attachment.TargetCache` | 78 | 291 | 28 |
| `common.summoner.attachment.WhipMarkTracker` | 78 | 114 | 28 |
| `common.summoner.attachmentEntity.AttachmentEntity` | 78 | 365 | 28 |
| `common.summoner.attachmentEntity.AttachmentEntityDamageSource` | 78 | 30 | 28 |
| `common.summoner.attachmentEntity.AttachmentEntityGoal` | 78 | 17 | 28 |
| `common.summoner.attachmentEntity.AttachmentEntityGoalSelector` | 78 | 50 | 28 |
| `common.summoner.attachmentEntity.AttachmentEntityType` | 78 | 20 | 28 |
| `common.summoner.attachmentEntity.IBlockCollision` | 78 | 74 | 28 |
| `common.summoner.attachmentEntity.IEntityCollision` | 78 | 201 | 28 |
| `common.summoner.minion.ICarryMinion` | 78 | 28 | 28 |
| `common.summoner.minion.Minion` | 78 | 117 | 28 |
| `common.summoner.minion.MinionSlotType` | 78 | 10 | 28 |
| `common.summoner.network.SummonerBatchedInfoPayload` | 78 | 51 | 28 |
| `common.summoner.particle.SummonerParticleData` | 78 | 35 | 28 |
| `common.summoner.register.SummonerAttachmentTypes` | 78 | 23 | 28 |
| `common.summoner.register.SummonerRegistries` | 78 | 17 | 28 |
| `common.summoner.summonMark.SummonMarkInstance` | 78 | 25 | 28 |
| `common.summoner.summonMark.SummonMarkType` | 78 | 69 | 28 |
| `common.summoner.particle.GenericParticleOptions` | 54 | 83 | 2 |
| `common.summoner.register.SummonerParticleTypes` | 54 | 16 | 2 |
| `common.summoner.SummonerHelper` | 52 | 58 | 29 |
| `common.summoner.attachmentEntity.MomentumAttachmentEntity` | 52 | 67 | 29 |
| `common.summoner.minion.MomentumMinion` | 52 | 113 | 29 |
| `common.summoner.particle.GenericParticleBuilder` | 52 | 78 | 3 |
| `common.summoner.minion.GroundMinion` | 51 | 137 | 30 |
| `common.summoner.particle.ParticleHelper` | 51 | 145 | 32 |
| `common.summoner.projectile.Projectile` | 51 | 40 | 30 |
| `common.summoner.minion.DesertTigerMinion` | 50 | 76 | 88 |
| `common.summoner.minion.EyeLaserTurretMinion` | 50 | 112 | 88 |
| `common.summoner.minion.FinchMinion` | 50 | 99 | 88 |
| `common.summoner.minion.HornetMinion` | 50 | 49 | 88 |
| `common.summoner.minion.ImpMinion` | 50 | 57 | 88 |
| `common.summoner.minion.IronGolemMinion` | 50 | 49 | 88 |
| `common.summoner.minion.RuinRelicMinion` | 50 | 157 | 88 |
| `common.summoner.minion.SanguineBatMinion` | 50 | 91 | 88 |
| `common.summoner.minion.SculkWispMinion` | 50 | 46 | 88 |
| `common.summoner.minion.SlimeMinion` | 50 | 71 | 88 |
| `common.summoner.minion.SnowFlinxMinion` | 50 | 50 | 88 |
| `common.summoner.minion.SpiderMinion` | 50 | 55 | 88 |
| `common.summoner.minion.TerraprismaMinion` | 50 | 121 | 88 |
| `common.summoner.minion.VampireFrogMinion` | 50 | 57 | 88 |
| `common.summoner.minion.goal.deadly_sphere.DeadlySphereAttackGoal` | 50 | 36 | 88 |
| `common.summoner.minion.goal.deadly_sphere.DeadlySphereIdleGoal` | 50 | 36 | 88 |
| `common.summoner.minion.goal.desert_tiger.DesertTigerAttackGoal` | 50 | 75 | 88 |
| `common.summoner.minion.goal.desert_tiger.DesertTigerIdleGoal` | 50 | 38 | 88 |
| `common.summoner.minion.goal.eye_laser_turret.EyeLaserTurretAttackGoal` | 50 | 22 | 88 |
| `common.summoner.minion.goal.eye_laser_turret.EyeLaserTurretIdleGoal` | 50 | 21 | 88 |
| `common.summoner.minion.goal.finch.FinchAttackGoal` | 50 | 37 | 88 |
| `common.summoner.minion.goal.finch.FinchIdleGoal` | 50 | 26 | 88 |
| `common.summoner.minion.goal.hornet.HornetAttackGoal` | 50 | 66 | 88 |
| `common.summoner.minion.goal.hornet.HornetIdleGoal` | 50 | 38 | 88 |
| `common.summoner.minion.goal.imp.ImpAttackGoal` | 50 | 50 | 88 |
| `common.summoner.minion.goal.imp.ImpIdleGoal` | 50 | 37 | 88 |
| `common.summoner.minion.goal.iron_golem.IronGolemAttackGoal` | 50 | 48 | 88 |
| `common.summoner.minion.goal.iron_golem.IronGolemIdleGoal` | 50 | 40 | 88 |
| `common.summoner.minion.goal.ruin_relic.RuinRelicAttackGoal` | 50 | 26 | 88 |
| `common.summoner.minion.goal.ruin_relic.RuinRelicIdleGoal` | 50 | 20 | 88 |
| `common.summoner.minion.goal.sanguine_bat.SanguineBatAttackGoal` | 50 | 43 | 88 |
| `common.summoner.minion.goal.sanguine_bat.SanguineBatIdleGoal` | 50 | 25 | 88 |
| `common.summoner.minion.goal.sculk_wisp.SculkWispAttackGoal` | 50 | 79 | 88 |
| `common.summoner.minion.goal.sculk_wisp.SculkWispIdleGoal` | 50 | 38 | 88 |
| `common.summoner.minion.goal.slime.SlimeAttackGoal` | 50 | 21 | 88 |
| `common.summoner.minion.goal.slime.SlimeIdleGoal` | 50 | 40 | 88 |
| `common.summoner.minion.goal.snow_flinx.SnowFlinxAttackGoal` | 50 | 28 | 88 |
| `common.summoner.minion.goal.snow_flinx.SnowFlinxIdleGoal` | 50 | 40 | 88 |
| `common.summoner.minion.goal.spider.SpiderAttackGoal` | 50 | 80 | 88 |
| `common.summoner.minion.goal.spider.SpiderIdleGoal` | 50 | 38 | 88 |
| `common.summoner.minion.goal.terraprisma.TerraprismAttackGoal` | 50 | 192 | 88 |
| `common.summoner.minion.goal.terraprisma.TerraprismIdleGoal` | 50 | 21 | 88 |
| `common.summoner.minion.goal.terraprisma.TerraprismPrepGoal` | 50 | 34 | 88 |
| `common.summoner.minion.goal.vampire_frog.VampireFrogAttackGoal` | 50 | 35 | 88 |
| `common.summoner.minion.goal.vampire_frog.VampireFrogIdleGoal` | 50 | 40 | 88 |
| `common.summoner.projectile.EyeFireball` | 50 | 77 | 88 |
| `common.summoner.projectile.ForbiddenOrb` | 50 | 97 | 88 |
| `common.summoner.projectile.HornetStinger` | 50 | 43 | 88 |
| `common.summoner.projectile.ImpFireball` | 50 | 46 | 88 |
| `common.summoner.register.SummonerAttachmentEntityTypes` | 50 | 42 | 88 |
| `common.summoner.minion.DeadlySphereMinion` | 50 | 74 | 88 |
