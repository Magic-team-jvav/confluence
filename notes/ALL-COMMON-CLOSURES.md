# 逐种子依赖闭包（`tools/port2native/seed_closures.py` 输出）

- 种子 **425** 个；1.21 侧可扩张类型 704 个
- 口径与 `dep_subset.py` 一致：只往「1.21 侧还没有的类型」里扩张，已满足的边算 0 成本、不继续走
- `--alias` 3 条：`org.confluence.lib.util.LibEntityUtils` → `org.confluence.lib.util.LibUtils`, `org.confluence.mod.common.data.GamePhase` → `org.confluence.mod.common.data.saved.GamePhase`, `org.confluence.mod.common.init.entity.ModEntities` → `org.confluence.mod.common.init.entity.ModEntities`

## 按单独闭包从小到大（轻量 ≤4 个文件：**109** 个种子）

| 种子 | 闭包文件 | 其中新增 | 非空行 | 种子自身行 | 1.21 已有同名 |
| --- | ---: | ---: | ---: | ---: | :--: |
| `AttackEffectsSubProvider` | 1 | 0 | 353 | 353 |  |
| `AccessoriesSubProvider` | 1 | 0 | 347 | 347 |  |
| `AbstractMountEntity` | 1 | 0 | 307 | 307 |  |
| `GuardianFlailEntity` | 1 | 0 | 152 | 152 |  |
| `SpikyBallRuntime` | 1 | 0 | 138 | 138 |  |
| `SpaceSpawner` | 1 | 0 | 135 | 135 |  |
| `SwordProjectileAppearance` | 1 | 0 | 133 | 133 |  |
| `Ellipse` | 1 | 0 | 122 | 122 |  |
| `SyncFieldDispatcher` | 1 | 0 | 114 | 114 |  |
| `BrushData` | 1 | 0 | 111 | 111 |  |
| `SwordDefinition` | 1 | 0 | 107 | 107 |  |
| `FlailAuxiliaryProjectile` | 1 | 0 | 100 | 100 |  |
| `SpiderNestStructure` | 1 | 0 | 93 | 93 |  |
| `BossMultiplayerEnhancement` | 1 | 0 | 92 | 92 |  |
| `LucyTheAxeLanguageSubProvider` | 1 | 0 | 90 | 90 |  |
| `WindyBalloon` | 1 | 0 | 88 | 88 |  |
| `PrefixArgument` | 1 | 0 | 82 | 82 |  |
| `YoyoEquipment` | 1 | 0 | 76 | 76 |  |
| `DriveAwayController` | 1 | 0 | 72 | 72 |  |
| `Team` | 1 | 0 | 71 | 71 |  |
| `WhipCollisionGeometry` | 1 | 0 | 68 | 68 |  |
| `CompostableSubProvider` | 1 | 0 | 66 | 66 |  |
| `OBB` | 1 | 0 | 62 | 62 |  |
| `SpecificMoonVariant` | 1 | 0 | 60 | 60 |  |
| `AntlionEggBlock` | 1 | 0 | 55 | 55 |  |
| `SummonerBatchedParticlesPayload` | 1 | 0 | 55 | 55 |  |
| `StickyBlockPersistence` | 1 | 0 | 54 | 54 |  |
| `TorchBlocks` | 1 | 0 | 54 | 54 |  |
| `GardenGnomeBlock` | 1 | 0 | 51 | 51 |  |
| `SwordProjectileParticleEffect` | 1 | 0 | 50 | 50 |  |
| `GunPropertyComponent` | 1 | 0 | 47 | 47 |  |
| `GunProjectilePattern` | 1 | 0 | 47 | 47 |  |
| `EntityVariantLootItemCondition` | 1 | 0 | 41 | 41 |  |
| `GameEventArgument` | 1 | 0 | 40 | 40 |  |
| `LostPaperSubProvider` | 1 | 0 | 38 | 38 |  |
| `Gnome` | 1 | 0 | 37 | 37 |  |
| `PathNode` | 1 | 0 | 37 | 37 |  |
| `ShimmerDecompositionInputs` | 1 | 0 | 34 | 34 |  |
| `DifficultyChanceLootItemCondition` | 1 | 0 | 33 | 33 |  |
| `SimpleMegaTreeGrower` | 1 | 0 | 32 | 32 |  |
| `ModFluidTagsProvider` | 1 | 0 | 31 | 31 |  |
| `StarPhase` | 1 | 0 | 30 | 30 |  |
| `MultiBoomerangEnchantment` | 1 | 0 | 29 | 29 |  |
| `WhipSweepEnchantment` | 1 | 0 | 29 | 29 |  |
| `DaoOfPowItem` | 1 | 0 | 29 | 29 |  |
| `DateStamp` | 1 | 0 | 28 | 28 |  |
| `LaunchedFlailEntity` | 1 | 0 | 26 | 26 |  |
| `AbstractManaEnchantment` | 1 | 0 | 25 | 25 |  |
| `SummonerPactEnchantment` | 1 | 0 | 25 | 25 |  |
| `LeftClickState` | 1 | 0 | 24 | 24 |  |
| `ModWorldPresetTagsProvider` | 1 | 0 | 24 | 24 |  |
| `SwordProjectileVisualBridge` | 1 | 0 | 24 | 24 |  |
| `IgnitingFlailItem` | 1 | 0 | 24 | 24 |  |
| `SummonerSoundEvents` | 1 | 0 | 23 | 23 |  |
| `CrimsonStormEffect` | 1 | 0 | 21 | 21 |  |
| `ArchaeologySubProvider` | 1 | 0 | 20 | 20 |  |
| `SimpleTreeGrower` | 1 | 0 | 17 | 17 |  |
| `WhipDamageSource` | 1 | 0 | 15 | 15 |  |
| `ShotContext` | 1 | 0 | 14 | 14 |  |
| `IMomentumAttachmentEntity` | 1 | 0 | 12 | 12 |  |
| `SummonerModels` | 1 | 0 | 10 | 10 |  |
| `FireMode` | 1 | 0 | 5 | 5 |  |
| `AmmoStats` | 1 | 0 | 4 | 4 |  |
| `GunStats` | 1 | 0 | 4 | 4 |  |
| `Ballistics` | 1 | 0 | 3 | 3 |  |
| `RideableBeeMountEntity` | 2 | 1 | 487 | 180 |  |
| `RideableSlimeMountEntity` | 2 | 1 | 475 | 168 |  |
| `RideableUnicornMountEntity` | 2 | 1 | 392 | 85 |  |
| `RideableLavaSharkMountEntity` | 2 | 1 | 369 | 62 |  |
| `StorageCompanionEntity` | 2 | 1 | 336 | 217 |  |
| `StorageCompanionItem` | 2 | 1 | 336 | 119 |  |
| `FlaironBubbleProjectile` | 2 | 1 | 196 | 96 |  |
| `VolcanoItem` | 2 | 1 | 172 | 65 |  |
| `GeoSwordProjectile` | 2 | 1 | 164 | 31 |  |
| `DripplerCripplerProjectile` | 2 | 1 | 156 | 56 |  |
| `MomentumSwordItem` | 2 | 1 | 146 | 39 |  |
| `EffectSwordItem` | 2 | 1 | 138 | 31 |  |
| `BreakerBladeItem` | 2 | 1 | 132 | 25 |  |
| `BatBatItem` | 2 | 1 | 127 | 20 |  |
| `UmbrellaSwordItem` | 2 | 1 | 127 | 20 |  |
| `FlowerPowerPetalProjectile` | 2 | 1 | 125 | 25 |  |
| `DriveAwayEffect` | 2 | 1 | 123 | 51 |  |
| `ModOpalDataProvider` | 2 | 1 | 112 | 58 |  |
| `GenericParticleOptions` | 2 | 1 | 99 | 83 |  |
| `SummonerParticleTypes` | 2 | 1 | 99 | 16 |  |
| `PlannedPath` | 2 | 1 | 83 | 46 |  |
| `AnchorFlailEntity` | 2 | 1 | 64 | 38 |  |
| `LyraStreamCodecs` | 2 | 1 | 56 | 19 |  |
| `ManaAffectiveEnchantment` | 2 | 1 | 54 | 29 |  |
| `ManaMendingEnchantment` | 2 | 1 | 43 | 18 |  |
| `MagicAttackEnchantment` | 2 | 1 | 42 | 17 |  |
| `ManaIOEnchantment` | 2 | 1 | 42 | 17 |  |
| `ArcaneProtectionEnchantment` | 2 | 1 | 39 | 14 |  |
| `ManaAttackEnchantment` | 2 | 1 | 38 | 13 |  |
| `MountItem` | 3 | 2 | 448 | 52 |  |
| `MountManager` | 3 | 2 | 448 | 89 |  |
| `FlyingPiggyBankEntity` | 3 | 2 | 398 | 62 |  |
| `ChesterEntity` | 3 | 2 | 376 | 40 |  |
| `FlaironFlailEntity` | 3 | 2 | 262 | 66 |  |
| `DripplerCripplerFlailEntity` | 3 | 2 | 188 | 32 |  |
| `GenericParticleBuilder` | 3 | 2 | 177 | 78 |  |
| `BeeKeeperItem` | 3 | 2 | 176 | 38 |  |
| `FlowerPowerFlailEntity` | 3 | 2 | 173 | 48 |  |
| `YoyoSession` | 4 | 3 | 807 | 131 |  |
| `YoyoEntity` | 4 | 3 | 807 | 468 |  |
| `YoyoItem` | 4 | 3 | 807 | 132 |  |
| `MountItems` | 4 | 3 | 475 | 27 |  |
| `GunDefinition` | 4 | 3 | 150 | 51 |  |
| `BallisticsResolver` | 4 | 3 | 21 | 10 |  |

## 重量种子（闭包 >4 个文件：**316** 个）

| 种子 | 闭包文件 | 其中新增 | 非空行 | 种子自身行 | 1.21 已有同名 |
| --- | ---: | ---: | ---: | ---: | :--: |
| `BaseYoyoProjectile` | 5 | 4 | 947 | 140 |  |
| `TerrarianProjectile` | 5 | 4 | 924 | 117 |  |
| `HiveFiveYoyoItem` | 5 | 4 | 838 | 31 |  |
| `AmarokYoyoItem` | 5 | 4 | 835 | 28 |  |
| `HelFireYoyoItem` | 5 | 4 | 832 | 25 |  |
| `YeletsYoyoItem` | 5 | 4 | 828 | 21 |  |
| `FormatCYoyoItem` | 5 | 4 | 825 | 18 |  |
| `PhasebladeProjectile` | 5 | 4 | 814 | 415 |  |
| `BasePhasebladeItem` | 5 | 4 | 814 | 191 |  |
| `Phasesaber` | 5 | 4 | 814 | 23 |  |
| `PetItems` | 5 | 4 | 454 | 16 |  |
| `BaseGun` | 5 | 4 | 315 | 165 |  |
| `CthulhuEyeProjectile` | 6 | 5 | 979 | 32 |  |
| `CascadeFireProjectile` | 6 | 5 | 974 | 27 |  |
| `ChikCrystalProjectile` | 6 | 5 | 969 | 22 |  |
| `TerrarianYoyoItem` | 6 | 5 | 967 | 43 |  |
| `KrakenWaveProjectile` | 6 | 5 | 963 | 16 |  |
| `ThrownPhasebladeProjectile` | 6 | 5 | 826 | 12 |  |
| `ThrownPhasesaberProjectile` | 6 | 5 | 826 | 12 |  |
| `GunSounds` | 6 | 5 | 358 | 43 |  |
| `KrakenYoyoItem` | 7 | 6 | 1024 | 61 |  |
| `CascadeYoyoItem` | 7 | 6 | 1019 | 45 |  |
| `ChikYoyoItem` | 7 | 6 | 1015 | 46 |  |
| `EyeOfCthulhuYoyoItem` | 7 | 6 | 1014 | 35 |  |
| `YoyoItems` | 21 | 20 | 1552 | 38 |  |
| `AttachmentEntityData` | 31 | 30 | 2485 | 285 |  |
| `InfoData` | 31 | 30 | 2485 | 86 |  |
| `TargetCache` | 31 | 30 | 2485 | 291 |  |
| `WhipMarkTracker` | 31 | 30 | 2485 | 114 |  |
| `AttachmentEntity` | 31 | 30 | 2485 | 365 |  |
| `AttachmentEntityDamageSource` | 31 | 30 | 2485 | 30 |  |
| `AttachmentEntityGoal` | 31 | 30 | 2485 | 17 |  |
| `AttachmentEntityGoalSelector` | 31 | 30 | 2485 | 50 |  |
| `AttachmentEntityType` | 31 | 30 | 2485 | 20 |  |
| `IBlockCollision` | 31 | 30 | 2485 | 74 |  |
| `IEntityCollision` | 31 | 30 | 2485 | 201 |  |
| `ICarryMinion` | 31 | 30 | 2485 | 28 |  |
| `Minion` | 31 | 30 | 2485 | 117 |  |
| `MinionSlotType` | 31 | 30 | 2485 | 10 |  |
| `SummonerBatchedInfoPayload` | 31 | 30 | 2485 | 51 |  |
| `SummonerParticleData` | 31 | 30 | 2485 | 35 |  |
| `SummonerAttachmentTypes` | 31 | 30 | 2485 | 23 |  |
| `SummonerRegistries` | 31 | 30 | 2485 | 17 |  |
| `SummonMarkInstance` | 31 | 30 | 2485 | 25 |  |
| `SummonMarkType` | 31 | 30 | 2485 | 69 |  |
| `MomentumMinion` | 32 | 31 | 2598 | 113 |  |
| `MomentumAttachmentEntity` | 32 | 31 | 2552 | 67 |  |
| `SummonerHelper` | 32 | 31 | 2543 | 58 |  |
| `SummonerWeaponItem` | 33 | 32 | 2744 | 201 |  |
| `GroundMinion` | 33 | 32 | 2735 | 137 |  |
| `SummonerEvents` | 33 | 32 | 2654 | 111 |  |
| `Projectile` | 33 | 32 | 2592 | 40 |  |
| `ParticleHelper` | 35 | 34 | 2807 | 145 |  |
| `WhipAttackEntity` | 45 | 44 | 3589 | 470 |  |
| `BaseWhipItem` | 45 | 44 | 3589 | 119 |  |
| `WhipSession` | 45 | 44 | 3589 | 70 |  |
| `SnapthornItem` | 46 | 45 | 3613 | 24 |  |
| `SwampWhipItem` | 46 | 45 | 3605 | 16 |  |
| `LeatherWhipItem` | 46 | 45 | 3602 | 13 |  |
| `FirecrackerItem` | 47 | 46 | 3709 | 105 |  |
| `SummonerSummonMarks` | 48 | 47 | 3751 | 42 |  |
| `WhipItems` | 52 | 51 | 3834 | 30 |  |
| `DeadlySphereMinion` | 92 | 91 | 6183 | 74 |  |
| `DesertTigerMinion` | 92 | 91 | 6183 | 76 |  |
| `EyeLaserTurretMinion` | 92 | 91 | 6183 | 112 |  |
| `FinchMinion` | 92 | 91 | 6183 | 99 |  |
| `HornetMinion` | 92 | 91 | 6183 | 49 |  |
| `ImpMinion` | 92 | 91 | 6183 | 57 |  |
| `IronGolemMinion` | 92 | 91 | 6183 | 49 |  |
| `RuinRelicMinion` | 92 | 91 | 6183 | 157 |  |
| `SanguineBatMinion` | 92 | 91 | 6183 | 91 |  |
| `SculkWispMinion` | 92 | 91 | 6183 | 46 |  |
| `SlimeMinion` | 92 | 91 | 6183 | 71 |  |
| `SnowFlinxMinion` | 92 | 91 | 6183 | 50 |  |
| `SpiderMinion` | 92 | 91 | 6183 | 55 |  |
| `TerraprismaMinion` | 92 | 91 | 6183 | 121 |  |
| `VampireFrogMinion` | 92 | 91 | 6183 | 57 |  |
| `DeadlySphereAttackGoal` | 92 | 91 | 6183 | 36 |  |
| `DeadlySphereIdleGoal` | 92 | 91 | 6183 | 36 |  |
| `DesertTigerAttackGoal` | 92 | 91 | 6183 | 75 |  |
| `DesertTigerIdleGoal` | 92 | 91 | 6183 | 38 |  |
| `EyeLaserTurretAttackGoal` | 92 | 91 | 6183 | 22 |  |
| `EyeLaserTurretIdleGoal` | 92 | 91 | 6183 | 21 |  |
| `FinchAttackGoal` | 92 | 91 | 6183 | 37 |  |
| `FinchIdleGoal` | 92 | 91 | 6183 | 26 |  |
| `HornetAttackGoal` | 92 | 91 | 6183 | 66 |  |
| `HornetIdleGoal` | 92 | 91 | 6183 | 38 |  |
| `ImpAttackGoal` | 92 | 91 | 6183 | 50 |  |
| `ImpIdleGoal` | 92 | 91 | 6183 | 37 |  |
| `IronGolemAttackGoal` | 92 | 91 | 6183 | 48 |  |
| `IronGolemIdleGoal` | 92 | 91 | 6183 | 40 |  |
| `RuinRelicAttackGoal` | 92 | 91 | 6183 | 26 |  |
| `RuinRelicIdleGoal` | 92 | 91 | 6183 | 20 |  |
| `SanguineBatAttackGoal` | 92 | 91 | 6183 | 43 |  |
| `SanguineBatIdleGoal` | 92 | 91 | 6183 | 25 |  |
| `SculkWispAttackGoal` | 92 | 91 | 6183 | 79 |  |
| `SculkWispIdleGoal` | 92 | 91 | 6183 | 38 |  |
| `SlimeAttackGoal` | 92 | 91 | 6183 | 21 |  |
| `SlimeIdleGoal` | 92 | 91 | 6183 | 40 |  |
| `SnowFlinxAttackGoal` | 92 | 91 | 6183 | 28 |  |
| `SnowFlinxIdleGoal` | 92 | 91 | 6183 | 40 |  |
| `SpiderAttackGoal` | 92 | 91 | 6183 | 80 |  |
| `SpiderIdleGoal` | 92 | 91 | 6183 | 38 |  |
| `TerraprismAttackGoal` | 92 | 91 | 6183 | 192 |  |
| `TerraprismIdleGoal` | 92 | 91 | 6183 | 21 |  |
| `TerraprismPrepGoal` | 92 | 91 | 6183 | 34 |  |
| `VampireFrogAttackGoal` | 92 | 91 | 6183 | 35 |  |
| `VampireFrogIdleGoal` | 92 | 91 | 6183 | 40 |  |
| `EyeFireball` | 92 | 91 | 6183 | 77 |  |
| `ForbiddenOrb` | 92 | 91 | 6183 | 97 |  |
| `HornetStinger` | 92 | 91 | 6183 | 43 |  |
| `ImpFireball` | 92 | 91 | 6183 | 46 |  |
| `SummonerAttachmentEntityTypes` | 92 | 91 | 6183 | 42 |  |
| `SummonItems` | 95 | 94 | 6664 | 257 |  |
| `NPCSpawner` | 156 | 155 | 20867 | 967 |  |
| `HorrifiedEffect` | 156 | 155 | 20867 | 65 |  |
| `AbstractTwinEye` | 156 | 155 | 20867 | 250 |  |
| `BaseWormBoss` | 156 | 155 | 20867 | 319 |  |
| `BossWormPart` | 156 | 155 | 20867 | 409 |  |
| `BrainFake` | 156 | 155 | 20867 | 180 |  |
| `BrainOfCthulhu` | 156 | 155 | 20867 | 690 |  |
| `EaterOfWorlds` | 156 | 155 | 20867 | 956 |  |
| `EyeOfCthulhu` | 156 | 155 | 20867 | 486 |  |
| `HillOfFlesh` | 156 | 155 | 20867 | 651 |  |
| `HillOfFleshEye` | 156 | 155 | 20867 | 46 |  |
| `HillOfFleshMouth` | 156 | 155 | 20867 | 86 |  |
| `KingSlime` | 156 | 155 | 20867 | 493 |  |
| `LunaticCultist` | 156 | 155 | 20867 | 276 |  |
| `LunaticCultistClone` | 156 | 155 | 20867 | 146 |  |
| `PhantasmDragon` | 156 | 155 | 20867 | 129 |  |
| `Plantera` | 156 | 155 | 20867 | 471 |  |
| `PlanteraHook` | 156 | 155 | 20867 | 140 |  |
| `PlanteraTentacle` | 156 | 155 | 20867 | 156 |  |
| `PrimeEnderDragon` | 156 | 155 | 20867 | 537 |  |
| `PrimeEnderDragonPart` | 156 | 155 | 20867 | 71 |  |
| `Retinazer` | 156 | 155 | 20867 | 228 |  |
| `ServantOfCthulhu` | 156 | 155 | 20867 | 146 |  |
| `Skeletron` | 156 | 155 | 20867 | 519 |  |
| `SkeletronHand` | 156 | 155 | 20867 | 350 |  |
| `SkeletronPrime` | 156 | 155 | 20867 | 321 |  |
| `SkeletronPrimeArm` | 156 | 155 | 20867 | 323 |  |
| `Spazmatism` | 156 | 155 | 20867 | 220 |  |
| `TheDestroyer` | 156 | 155 | 20867 | 493 |  |
| `TheDestroyerProbe` | 156 | 155 | 20867 | 144 |  |
| `TheTwins` | 156 | 155 | 20867 | 341 |  |
| `WallOfFlesh` | 156 | 155 | 20867 | 836 |  |
| `WallOfFleshEye` | 156 | 155 | 20867 | 61 |  |
| `WallOfFleshMouth` | 156 | 155 | 20867 | 74 |  |
| `WallOfFleshPart` | 156 | 155 | 20867 | 101 |  |
| `CrownOfKingSlimeModelEntity` | 156 | 155 | 20867 | 95 |  |
| `HillHungry` | 156 | 155 | 20867 | 22 |  |
| `HungryMovementAction` | 156 | 155 | 20867 | 95 |  |
| `TheHungry` | 156 | 155 | 20867 | 316 |  |
| `VisualNeuron` | 156 | 155 | 20867 | 348 |  |
| `BaseSlime` | 156 | 155 | 20867 | 420 |  |
| `FleshSlime` | 156 | 155 | 20867 | 47 |  |
| `SweetSlime` | 156 | 155 | 20867 | 64 |  |
| `AnglerNPC` | 156 | 155 | 20867 | 228 |  |
| `BaseNPC` | 156 | 155 | 20867 | 618 |  |
| `BurstGunNPC` | 156 | 155 | 20867 | 36 |  |
| `DryadNPC` | 156 | 155 | 20867 | 95 |  |
| `GolferNPC` | 156 | 155 | 20867 | 36 |  |
| `MechanicNPC` | 156 | 155 | 20867 | 22 |  |
| `NPCTradeGoal` | 156 | 155 | 20867 | 34 |  |
| `NurseNPC` | 156 | 155 | 20867 | 62 |  |
| `OldManNPC` | 156 | 155 | 20867 | 103 |  |
| `SimpleNPC` | 156 | 155 | 20867 | 10 |  |
| `SkeletonMerchantNPC` | 156 | 155 | 20867 | 57 |  |
| `TownSlimeNPC` | 156 | 155 | 20867 | 94 |  |
| `TravelingMerchantNPC` | 156 | 155 | 20867 | 84 |  |
| `NPCCombatActions` | 156 | 155 | 20867 | 167 |  |
| `NPCCombatProfile` | 156 | 155 | 20867 | 192 |  |
| `NPCCombatProgression` | 156 | 155 | 20867 | 55 |  |
| `NPCDefenseGoal` | 156 | 155 | 20867 | 126 |  |
| `NPCHealGoal` | 156 | 155 | 20867 | 126 |  |
| `NPCHurtRetreatGoal` | 156 | 155 | 20867 | 77 |  |
| `NPCReturnHomeGoal` | 156 | 155 | 20867 | 35 |  |
| `ChatLine` | 156 | 155 | 20867 | 19 |  |
| `ChatManager` | 156 | 155 | 20867 | 80 |  |
| `MoodEnvironment` | 156 | 155 | 20867 | 46 |  |
| `NPCMood` | 156 | 155 | 20867 | 127 |  |
| `NPCTradeList` | 156 | 155 | 20867 | 76 |  |
| `NPCTradeMenu` | 156 | 155 | 20867 | 471 |  |
| `NPCTradeOffer` | 156 | 155 | 20867 | 43 |  |
| `TradeCondition` | 156 | 155 | 20867 | 28 |  |
| `AlwaysTrueCondition` | 156 | 155 | 20867 | 18 |  |
| `AndCondition` | 156 | 155 | 20867 | 17 |  |
| `AnyBossDefeatedCondition` | 156 | 155 | 20867 | 15 |  |
| `ArtisanLoafUnusedCondition` | 156 | 155 | 20867 | 19 |  |
| `AttackTargetCondition` | 156 | 155 | 20867 | 19 |  |
| `BestiaryCondition` | 156 | 155 | 20867 | 15 |  |
| `BiomeCondition` | 156 | 155 | 20867 | 31 |  |
| `BossDefeatedCondition` | 156 | 155 | 20867 | 19 |  |
| `DateCondition` | 156 | 155 | 20867 | 26 |  |
| `DimensionCondition` | 156 | 155 | 20867 | 23 |  |
| `FluidCondition` | 156 | 155 | 20867 | 28 |  |
| `GameEventCondition` | 156 | 155 | 20867 | 23 |  |
| `GraveyardCondition` | 156 | 155 | 20867 | 21 |  |
| `HardmodeCondition` | 156 | 155 | 20867 | 15 |  |
| `KillEntityCondition` | 156 | 155 | 20867 | 20 |  |
| `MoodCondition` | 156 | 155 | 20867 | 19 |  |
| `MoonPhaseCondition` | 156 | 155 | 20867 | 25 |  |
| `NPCItemInHandCondition` | 156 | 155 | 20867 | 21 |  |
| `NPCNearbyCondition` | 156 | 155 | 20867 | 21 |  |
| `NotCondition` | 156 | 155 | 20867 | 16 |  |
| `OrCondition` | 156 | 155 | 20867 | 17 |  |
| `PositionHeightCondition` | 156 | 155 | 20867 | 29 |  |
| `TimeCondition` | 156 | 155 | 20867 | 23 |  |
| `WeatherCondition` | 156 | 155 | 20867 | 22 |  |
| `WorldFlagCondition` | 156 | 155 | 20867 | 20 |  |
| `AncientLightProjectile` | 156 | 155 | 20867 | 113 |  |
| `BaseBulletEntity` | 156 | 155 | 20867 | 435 |  |
| `BoomerangProjectile` | 156 | 155 | 20867 | 177 |  |
| `CustomBulletEntity` | 156 | 155 | 20867 | 51 |  |
| `CyborgExplosiveProjectile` | 156 | 155 | 20867 | 88 |  |
| `NPCProjectileEffects` | 156 | 155 | 20867 | 88 |  |
| `NPCWeaponProjectile` | 156 | 155 | 20867 | 121 |  |
| `PrimeCannonballProjectile` | 156 | 155 | 20867 | 87 |  |
| `BaseArrowEntity` | 156 | 155 | 20867 | 333 |  |
| `PartyGameEvent` | 156 | 155 | 20867 | 118 |  |
| `ModCustomRegistries` | 156 | 155 | 20867 | 34 |  |
| `ModGenerationProviderTypes` | 156 | 155 | 20867 | 20 |  |
| `ModTrackTypeProviderTypes` | 156 | 155 | 20867 | 18 |  |
| `ModTradeConditions` | 156 | 155 | 20867 | 35 |  |
| `BossEntities` | 156 | 155 | 20867 | 166 |  |
| `NpcEntities` | 156 | 155 | 20867 | 228 |  |
| `BoomerangItems` | 156 | 155 | 20867 | 23 |  |
| `BaseBullet` | 156 | 155 | 20867 | 66 |  |
| `BoomerangItem` | 156 | 155 | 20867 | 108 |  |
| `BulletBehavior` | 156 | 155 | 20867 | 28 |  |
| `BulletDefinition` | 156 | 155 | 20867 | 41 |  |
| `AbstractBulletBehavior` | 156 | 155 | 20867 | 12 |  |
| `BulletBehaviorSupport` | 156 | 155 | 20867 | 48 |  |
| `ChlorophyteHomingBehavior` | 156 | 155 | 20867 | 58 |  |
| `CrystalSplitBehavior` | 156 | 155 | 20867 | 40 |  |
| `CursedDebuffBehavior` | 156 | 155 | 20867 | 14 |  |
| `ExplosiveBulletBehavior` | 156 | 155 | 20867 | 19 |  |
| `HighVelocityDamageDecayBehavior` | 156 | 155 | 20867 | 14 |  |
| `IchorDebuffBehavior` | 156 | 155 | 20867 | 14 |  |
| `LuminiteDamageDecayBehavior` | 156 | 155 | 20867 | 14 |  |
| `MeteorRicochetBehavior` | 156 | 155 | 20867 | 37 |  |
| `NanoRicochetBehavior` | 156 | 155 | 20867 | 28 |  |
| `NormalBulletBehavior` | 156 | 155 | 20867 | 7 |  |
| `PartyBulletBehavior` | 156 | 155 | 20867 | 7 |  |
| `SilverBulletBehavior` | 156 | 155 | 20867 | 7 |  |
| `VenomDebuffBehavior` | 156 | 155 | 20867 | 14 |  |
| `NPCServiceMenu` | 156 | 155 | 20867 | 7 |  |
| `NPCNameProvider` | 157 | 156 | 21373 | 506 |  |
| `NPCDialogProvider` | 157 | 156 | 21236 | 369 |  |
| `SpawnEggItems` | 157 | 156 | 21176 | 309 |  |
| `SpikedSlime` | 157 | 156 | 21025 | 158 |  |
| `SpearProjectileComponent` | 157 | 156 | 21022 | 155 |  |
| `BossDelaySpawner` | 157 | 156 | 21019 | 152 |  |
| `AnglerQuestProvider` | 157 | 156 | 21005 | 138 |  |
| `MonsterAttributeScaling` | 157 | 156 | 20973 | 106 |  |
| `NPCMoodProvider` | 157 | 156 | 20969 | 102 |  |
| `TheTongueEffect` | 157 | 156 | 20950 | 83 |  |
| `TownSlimeRescue` | 157 | 156 | 20944 | 77 |  |
| `DungeonSpirit` | 157 | 156 | 20934 | 67 |  |
| `GunTrailColors` | 157 | 156 | 20933 | 66 |  |
| `MechanicalMayhemTracker` | 157 | 156 | 20926 | 59 |  |
| `SparkleSlimeBalloonProjectile` | 157 | 156 | 20925 | 58 |  |
| `HellBatArrowEntity` | 157 | 156 | 20925 | 58 |  |
| `LavaSlime` | 157 | 156 | 20924 | 57 |  |
| `BeeArrowEntity` | 157 | 156 | 20923 | 56 |  |
| `Slimer` | 157 | 156 | 20921 | 54 |  |
| `MysticFrog` | 157 | 156 | 20919 | 52 |  |
| `StarArrowEntity` | 157 | 156 | 20918 | 51 |  |
| `NPCChatProvider` | 157 | 156 | 20916 | 49 |  |
| `CorruptSlime` | 157 | 156 | 20915 | 48 |  |
| `GoldenSlime` | 157 | 156 | 20914 | 47 |  |
| `DryadsBlessingEffect` | 157 | 156 | 20912 | 45 |  |
| `MotherSlime` | 157 | 156 | 20912 | 45 |  |
| `DeveloperArrowEntity` | 157 | 156 | 20904 | 37 |  |
| `FlyFishArrowEntity` | 157 | 156 | 20904 | 37 |  |
| `HellfireArrowEntity` | 157 | 156 | 20904 | 37 |  |
| `FrostburnArrowEntity` | 157 | 156 | 20903 | 36 |  |
| `FlamingArrowEntity` | 157 | 156 | 20901 | 34 |  |
| `ShimmerArrowEntity` | 157 | 156 | 20900 | 33 |  |
| `Werewolf` | 157 | 156 | 20899 | 32 |  |
| `UnholyArrowEntity` | 157 | 156 | 20896 | 29 |  |
| `BoneArrowEntity` | 157 | 156 | 20892 | 25 |  |
| `FossilArrowEntity` | 157 | 156 | 20892 | 25 |  |
| `DemonBow` | 157 | 156 | 20891 | 24 |  |
| `TendonBow` | 157 | 156 | 20889 | 22 |  |
| `FossilBow` | 157 | 156 | 20883 | 16 |  |
| `MoltenFury` | 157 | 156 | 20883 | 16 |  |
| `TropicSlime` | 157 | 156 | 20882 | 15 |  |
| `BoneArrowItem` | 157 | 156 | 20882 | 15 |  |
| `FlamingArrowItem` | 157 | 156 | 20882 | 15 |  |
| `FlyFishArrowItem` | 157 | 156 | 20882 | 15 |  |
| `FossilArrowItem` | 157 | 156 | 20882 | 15 |  |
| `FrostburnArrowItem` | 157 | 156 | 20882 | 15 |  |
| `HellfireArrowItem` | 157 | 156 | 20882 | 15 |  |
| `ShimmerArrowItem` | 157 | 156 | 20882 | 15 |  |
| `StarArrowItem` | 157 | 156 | 20882 | 15 |  |
| `UnholyArrowItem` | 157 | 156 | 20882 | 15 |  |
| `HuntingBow` | 157 | 156 | 20882 | 15 |  |
| `LuminousSlime` | 157 | 156 | 20880 | 13 |  |
| `ModBlockCounters` | 158 | 157 | 21038 | 80 |  |
| `ModMiniBiomes` | 158 | 157 | 21038 | 91 |  |
| `DriveAwayArrowEntity` | 158 | 157 | 21030 | 91 |  |
| `TheBeesKnees` | 158 | 157 | 20952 | 29 |  |
| `HellwingBow` | 158 | 157 | 20944 | 19 |  |
| `DeveloperBow` | 158 | 157 | 20921 | 17 |  |
| `SpawnPlacementChecks` | 159 | 158 | 21741 | 703 |  |
| `ModDynamicBiomes` | 159 | 158 | 21189 | 151 |  |
| `LightsBaneItem` | 159 | 158 | 21142 | 35 |  |
| `MeteoriteSpawner` | 159 | 158 | 21130 | 92 |  |
| `Scarebow` | 159 | 158 | 21047 | 17 |  |
| `CreatureSpawnPlacements` | 160 | 159 | 21994 | 253 |  |
| `BladeOfGrassItem` | 160 | 159 | 21194 | 37 |  |
| `GunProjectileFactory` | 164 | 163 | 21499 | 69 |  |
| `GunEvents` | 165 | 164 | 21517 | 41 |  |
| `GunFiringService` | 169 | 168 | 21563 | 43 |  |
| `ShootingService` | 171 | 170 | 21664 | 41 |  |

## 公共底座（出现在 ≥2 个种子的闭包里：**299** 个）

**先搬这些，后面每个种子都会变轻。**

| 类型 | 被几个种子需要 | 非空行 | 自己单独搬时的闭包 |
| --- | ---: | ---: | ---: |
| `common.entity.mount.AbstractMountEntity` | 209 | 307 | 1 |
| `common.item.mount.MountItem` | 204 | 52 | 3 |
| `common.mount.MountManager` | 204 | 89 | 3 |
| `api.IGeneration` | 202 | 19 | （不是种子） |
| `api.ITrackType` | 202 | 22 | （不是种子） |
| `api.event.BulletEvent` | 202 | 125 | （不是种子） |
| `client.gui.screen.AnglerDialogScreen` | 202 | 100 | （不是种子） |
| `client.gui.screen.GoblinTinkererDialogScreen` | 202 | 20 | （不是种子） |
| `client.gui.screen.NPCDialogScreen` | 202 | 124 | （不是种子） |
| `common.entity.boss.BossMultiplayerEnhancement` | 202 | 92 | 1 |
| `common.init.item.MountItems` | 202 | 27 | 4 |
| `network.c2s.NPCDialogSessionPacketC2S` | 202 | 29 | （不是种子） |
| `network.c2s.OpenNPCServicePacketC2S` | 202 | 32 | （不是种子） |
| `network.c2s.SummonSkeletronPacketC2S` | 202 | 26 | （不是种子） |
| `network.s2c.OpenAnglerDialogPacketS2C` | 202 | 46 | （不是种子） |
| `network.s2c.OpenNPCDialogPacketS2C` | 202 | 28 | （不是种子） |
| `util.generation.GenerationProvider` | 202 | 7 | （不是种子） |
| `util.generation.variant.AboveFallenGeneration` | 202 | 85 | （不是种子） |
| `util.generation.variant.ForwardGeneration` | 202 | 35 | （不是种子） |
| `util.generation.variant.StillGeneration` | 202 | 38 | （不是种子） |
| `util.track.TrackTypeProvider` | 202 | 7 | （不是种子） |
| `util.track.variant.BasisTrack` | 202 | 34 | （不是种子） |
| `util.track.variant.SimpleTrack` | 202 | 48 | （不是种子） |
| `common.data.spawner.NPCSpawner` | 201 | 967 | 156 |
| `common.effect.harmful.HorrifiedEffect` | 201 | 65 | 156 |
| `common.entity.boss.AbstractTwinEye` | 201 | 250 | 156 |
| `common.entity.boss.BaseWormBoss` | 201 | 319 | 156 |
| `common.entity.boss.BossWormPart` | 201 | 409 | 156 |
| `common.entity.boss.BrainFake` | 201 | 180 | 156 |
| `common.entity.boss.BrainOfCthulhu` | 201 | 690 | 156 |
| `common.entity.boss.EaterOfWorlds` | 201 | 956 | 156 |
| `common.entity.boss.EyeOfCthulhu` | 201 | 486 | 156 |
| `common.entity.boss.HillOfFlesh` | 201 | 651 | 156 |
| `common.entity.boss.HillOfFleshEye` | 201 | 46 | 156 |
| `common.entity.boss.HillOfFleshMouth` | 201 | 86 | 156 |
| `common.entity.boss.KingSlime` | 201 | 493 | 156 |
| `common.entity.boss.LunaticCultist` | 201 | 276 | 156 |
| `common.entity.boss.LunaticCultistClone` | 201 | 146 | 156 |
| `common.entity.boss.PhantasmDragon` | 201 | 129 | 156 |
| `common.entity.boss.Plantera` | 201 | 471 | 156 |
| `common.entity.boss.PlanteraHook` | 201 | 140 | 156 |
| `common.entity.boss.PlanteraTentacle` | 201 | 156 | 156 |
| `common.entity.boss.PrimeEnderDragon` | 201 | 537 | 156 |
| `common.entity.boss.PrimeEnderDragonPart` | 201 | 71 | 156 |
| `common.entity.boss.Retinazer` | 201 | 228 | 156 |
| `common.entity.boss.ServantOfCthulhu` | 201 | 146 | 156 |
| `common.entity.boss.Skeletron` | 201 | 519 | 156 |
| `common.entity.boss.SkeletronHand` | 201 | 350 | 156 |
| `common.entity.boss.SkeletronPrime` | 201 | 321 | 156 |
| `common.entity.boss.SkeletronPrimeArm` | 201 | 323 | 156 |
| `common.entity.boss.Spazmatism` | 201 | 220 | 156 |
| `common.entity.boss.TheDestroyer` | 201 | 493 | 156 |
| `common.entity.boss.TheDestroyerProbe` | 201 | 144 | 156 |
| `common.entity.boss.TheTwins` | 201 | 341 | 156 |
| `common.entity.boss.WallOfFlesh` | 201 | 836 | 156 |
| `common.entity.boss.WallOfFleshEye` | 201 | 61 | 156 |
| `common.entity.boss.WallOfFleshMouth` | 201 | 74 | 156 |
| `common.entity.boss.WallOfFleshPart` | 201 | 101 | 156 |
| `common.entity.model.CrownOfKingSlimeModelEntity` | 201 | 95 | 156 |
| `common.entity.monster.HillHungry` | 201 | 22 | 156 |
| `common.entity.monster.HungryMovementAction` | 201 | 95 | 156 |
| `common.entity.monster.TheHungry` | 201 | 316 | 156 |
| `common.entity.monster.VisualNeuron` | 201 | 348 | 156 |
| `common.entity.monster.slime.BaseSlime` | 201 | 420 | 156 |
| `common.entity.monster.slime.FleshSlime` | 201 | 47 | 156 |
| `common.entity.monster.slime.SweetSlime` | 201 | 64 | 156 |
| `common.entity.npc.AnglerNPC` | 201 | 228 | 156 |
| `common.entity.npc.BaseNPC` | 201 | 618 | 156 |
| `common.entity.npc.BurstGunNPC` | 201 | 36 | 156 |
| `common.entity.npc.DryadNPC` | 201 | 95 | 156 |
| `common.entity.npc.GolferNPC` | 201 | 36 | 156 |
| `common.entity.npc.MechanicNPC` | 201 | 22 | 156 |
| `common.entity.npc.NPCTradeGoal` | 201 | 34 | 156 |
| `common.entity.npc.NurseNPC` | 201 | 62 | 156 |
| `common.entity.npc.OldManNPC` | 201 | 103 | 156 |
| `common.entity.npc.SimpleNPC` | 201 | 10 | 156 |
| `common.entity.npc.SkeletonMerchantNPC` | 201 | 57 | 156 |
| `common.entity.npc.TownSlimeNPC` | 201 | 94 | 156 |
| `common.entity.npc.TravelingMerchantNPC` | 201 | 84 | 156 |
| `common.entity.npc.ai.NPCCombatActions` | 201 | 167 | 156 |
| `common.entity.npc.ai.NPCCombatProfile` | 201 | 192 | 156 |
| `common.entity.npc.ai.NPCCombatProgression` | 201 | 55 | 156 |
| `common.entity.npc.ai.NPCDefenseGoal` | 201 | 126 | 156 |
| `common.entity.npc.ai.NPCHealGoal` | 201 | 126 | 156 |
| `common.entity.npc.ai.NPCHurtRetreatGoal` | 201 | 77 | 156 |
| `common.entity.npc.ai.NPCReturnHomeGoal` | 201 | 35 | 156 |
| `common.entity.npc.chat.ChatLine` | 201 | 19 | 156 |
| `common.entity.npc.chat.ChatManager` | 201 | 80 | 156 |
| `common.entity.npc.mood.MoodEnvironment` | 201 | 46 | 156 |
| `common.entity.npc.mood.NPCMood` | 201 | 127 | 156 |
| `common.entity.npc.trade.NPCTradeList` | 201 | 76 | 156 |
| `common.entity.npc.trade.NPCTradeMenu` | 201 | 471 | 156 |
| `common.entity.npc.trade.NPCTradeOffer` | 201 | 43 | 156 |
| `common.entity.npc.trade.TradeCondition` | 201 | 28 | 156 |
| `common.entity.npc.trade.conditions.AlwaysTrueCondition` | 201 | 18 | 156 |
| `common.entity.npc.trade.conditions.AndCondition` | 201 | 17 | 156 |
| `common.entity.npc.trade.conditions.AnyBossDefeatedCondition` | 201 | 15 | 156 |
| `common.entity.npc.trade.conditions.ArtisanLoafUnusedCondition` | 201 | 19 | 156 |
| `common.entity.npc.trade.conditions.AttackTargetCondition` | 201 | 19 | 156 |
| `common.entity.npc.trade.conditions.BestiaryCondition` | 201 | 15 | 156 |
| `common.entity.npc.trade.conditions.BiomeCondition` | 201 | 31 | 156 |
| `common.entity.npc.trade.conditions.BossDefeatedCondition` | 201 | 19 | 156 |
| `common.entity.npc.trade.conditions.DateCondition` | 201 | 26 | 156 |
| `common.entity.npc.trade.conditions.DimensionCondition` | 201 | 23 | 156 |
| `common.entity.npc.trade.conditions.FluidCondition` | 201 | 28 | 156 |
| `common.entity.npc.trade.conditions.GameEventCondition` | 201 | 23 | 156 |
| `common.entity.npc.trade.conditions.GraveyardCondition` | 201 | 21 | 156 |
| `common.entity.npc.trade.conditions.HardmodeCondition` | 201 | 15 | 156 |
| `common.entity.npc.trade.conditions.KillEntityCondition` | 201 | 20 | 156 |
| `common.entity.npc.trade.conditions.MoodCondition` | 201 | 19 | 156 |
| `common.entity.npc.trade.conditions.MoonPhaseCondition` | 201 | 25 | 156 |
| `common.entity.npc.trade.conditions.NPCItemInHandCondition` | 201 | 21 | 156 |
| `common.entity.npc.trade.conditions.NPCNearbyCondition` | 201 | 21 | 156 |
| `common.entity.npc.trade.conditions.NotCondition` | 201 | 16 | 156 |
| `common.entity.npc.trade.conditions.OrCondition` | 201 | 17 | 156 |
| `common.entity.npc.trade.conditions.PositionHeightCondition` | 201 | 29 | 156 |
| `common.entity.npc.trade.conditions.TimeCondition` | 201 | 23 | 156 |
| `common.entity.npc.trade.conditions.WeatherCondition` | 201 | 22 | 156 |
| `common.entity.npc.trade.conditions.WorldFlagCondition` | 201 | 20 | 156 |
| `common.entity.projectile.AncientLightProjectile` | 201 | 113 | 156 |
| `common.entity.projectile.BaseBulletEntity` | 201 | 435 | 156 |
| `common.entity.projectile.BoomerangProjectile` | 201 | 177 | 156 |
| `common.entity.projectile.CustomBulletEntity` | 201 | 51 | 156 |
| `common.entity.projectile.CyborgExplosiveProjectile` | 201 | 88 | 156 |
| `common.entity.projectile.NPCProjectileEffects` | 201 | 88 | 156 |
| `common.entity.projectile.NPCWeaponProjectile` | 201 | 121 | 156 |
| `common.entity.projectile.PrimeCannonballProjectile` | 201 | 87 | 156 |
| `common.entity.projectile.arrow.BaseArrowEntity` | 201 | 333 | 156 |
| `common.gameevent.PartyGameEvent` | 201 | 118 | 156 |
| `common.init.ModCustomRegistries` | 201 | 34 | 156 |
| `common.init.ModGenerationProviderTypes` | 201 | 20 | 156 |
| `common.init.ModTrackTypeProviderTypes` | 201 | 18 | 156 |
| `common.init.ModTradeConditions` | 201 | 35 | 156 |
| `common.init.entity.BossEntities` | 201 | 166 | 156 |
| `common.init.entity.NpcEntities` | 201 | 228 | 156 |
| `common.init.item.BoomerangItems` | 201 | 23 | 156 |
| `common.item.BaseBullet` | 201 | 66 | 156 |
| `common.item.boomerang.BoomerangItem` | 201 | 108 | 156 |
| `common.item.gun.definition.BulletBehavior` | 201 | 28 | 156 |
| `common.item.gun.definition.BulletDefinition` | 201 | 41 | 156 |
| `common.item.gun.definition.behavior.AbstractBulletBehavior` | 201 | 12 | 156 |
| `common.item.gun.definition.behavior.BulletBehaviorSupport` | 201 | 48 | 156 |
| `common.item.gun.definition.behavior.ChlorophyteHomingBehavior` | 201 | 58 | 156 |
| `common.item.gun.definition.behavior.CrystalSplitBehavior` | 201 | 40 | 156 |
| `common.item.gun.definition.behavior.CursedDebuffBehavior` | 201 | 14 | 156 |
| `common.item.gun.definition.behavior.ExplosiveBulletBehavior` | 201 | 19 | 156 |
| `common.item.gun.definition.behavior.HighVelocityDamageDecayBehavior` | 201 | 14 | 156 |
| `common.item.gun.definition.behavior.IchorDebuffBehavior` | 201 | 14 | 156 |
| `common.item.gun.definition.behavior.LuminiteDamageDecayBehavior` | 201 | 14 | 156 |
| `common.item.gun.definition.behavior.MeteorRicochetBehavior` | 201 | 37 | 156 |
| `common.item.gun.definition.behavior.NanoRicochetBehavior` | 201 | 28 | 156 |
| `common.item.gun.definition.behavior.NormalBulletBehavior` | 201 | 7 | 156 |
| `common.item.gun.definition.behavior.PartyBulletBehavior` | 201 | 7 | 156 |
| `common.item.gun.definition.behavior.SilverBulletBehavior` | 201 | 7 | 156 |
| `common.item.gun.definition.behavior.VenomDebuffBehavior` | 201 | 14 | 156 |
| `common.menu.NPCServiceMenu` | 201 | 7 | 156 |
| `common.summoner.attachmentEntity.PathNode` | 91 | 37 | 1 |
| `client.summoner.LyraRenderTypes` | 89 | 71 | （不是种子） |
| `client.summoner.info.Info` | 89 | 85 | （不是种子） |
| `client.summoner.info.NumberInfo` | 89 | 51 | （不是种子） |
| `client.summoner.info.TextInfo` | 89 | 25 | （不是种子） |
| `common.summoner.LyraStreamCodecs` | 89 | 19 | 2 |
| `common.summoner.attachmentEntity.IMomentumAttachmentEntity` | 89 | 12 | 1 |
| `common.summoner.attachmentEntity.OBB` | 89 | 62 | 1 |
| `common.summoner.attachmentEntity.PlannedPath` | 89 | 46 | 2 |
| `common.summoner.attachmentEntity.SyncFieldDispatcher` | 89 | 114 | 1 |
| `common.summoner.network.SummonerBatchedParticlesPayload` | 89 | 55 | 1 |
| `common.summoner.attachment.AttachmentEntityData` | 88 | 285 | 31 |
| `common.summoner.attachment.InfoData` | 88 | 86 | 31 |
| `common.summoner.attachment.TargetCache` | 88 | 291 | 31 |
| `common.summoner.attachment.WhipMarkTracker` | 88 | 114 | 31 |
| `common.summoner.attachmentEntity.AttachmentEntity` | 88 | 365 | 31 |
| `common.summoner.attachmentEntity.AttachmentEntityDamageSource` | 88 | 30 | 31 |
| `common.summoner.attachmentEntity.AttachmentEntityGoal` | 88 | 17 | 31 |
| `common.summoner.attachmentEntity.AttachmentEntityGoalSelector` | 88 | 50 | 31 |
| `common.summoner.attachmentEntity.AttachmentEntityType` | 88 | 20 | 31 |
| `common.summoner.attachmentEntity.IBlockCollision` | 88 | 74 | 31 |
| `common.summoner.attachmentEntity.IEntityCollision` | 88 | 201 | 31 |
| `common.summoner.minion.ICarryMinion` | 88 | 28 | 31 |
| `common.summoner.minion.Minion` | 88 | 117 | 31 |
| `common.summoner.minion.MinionSlotType` | 88 | 10 | 31 |
| `common.summoner.network.SummonerBatchedInfoPayload` | 88 | 51 | 31 |
| `common.summoner.particle.SummonerParticleData` | 88 | 35 | 31 |
| `common.summoner.register.SummonerAttachmentTypes` | 88 | 23 | 31 |
| `common.summoner.register.SummonerRegistries` | 88 | 17 | 31 |
| `common.summoner.summonMark.SummonMarkInstance` | 88 | 25 | 31 |
| `common.summoner.summonMark.SummonMarkType` | 88 | 69 | 31 |
| `common.summoner.particle.GenericParticleOptions` | 55 | 83 | 2 |
| `common.summoner.register.SummonerParticleTypes` | 55 | 16 | 2 |
| `common.summoner.SummonerHelper` | 54 | 58 | 32 |
| `common.summoner.attachmentEntity.MomentumAttachmentEntity` | 53 | 67 | 32 |
| `common.summoner.minion.MomentumMinion` | 53 | 113 | 32 |
| `common.summoner.particle.GenericParticleBuilder` | 53 | 78 | 3 |
| `common.summoner.attachmentEntity.Ellipse` | 52 | 122 | 1 |
| `common.summoner.minion.GroundMinion` | 52 | 137 | 33 |
| `common.summoner.particle.ParticleHelper` | 52 | 145 | 35 |
| `common.summoner.projectile.Projectile` | 52 | 40 | 33 |
| `common.summoner.minion.DeadlySphereMinion` | 51 | 74 | 92 |
| `common.summoner.minion.DesertTigerMinion` | 51 | 76 | 92 |
| `common.summoner.minion.EyeLaserTurretMinion` | 51 | 112 | 92 |
| `common.summoner.minion.FinchMinion` | 51 | 99 | 92 |
| `common.summoner.minion.HornetMinion` | 51 | 49 | 92 |
| `common.summoner.minion.ImpMinion` | 51 | 57 | 92 |
| `common.summoner.minion.IronGolemMinion` | 51 | 49 | 92 |
| `common.summoner.minion.RuinRelicMinion` | 51 | 157 | 92 |
| `common.summoner.minion.SanguineBatMinion` | 51 | 91 | 92 |
| `common.summoner.minion.SculkWispMinion` | 51 | 46 | 92 |
| `common.summoner.minion.SlimeMinion` | 51 | 71 | 92 |
| `common.summoner.minion.SnowFlinxMinion` | 51 | 50 | 92 |
| `common.summoner.minion.SpiderMinion` | 51 | 55 | 92 |
| `common.summoner.minion.TerraprismaMinion` | 51 | 121 | 92 |
| `common.summoner.minion.VampireFrogMinion` | 51 | 57 | 92 |
| `common.summoner.minion.goal.deadly_sphere.DeadlySphereAttackGoal` | 51 | 36 | 92 |
| `common.summoner.minion.goal.deadly_sphere.DeadlySphereIdleGoal` | 51 | 36 | 92 |
| `common.summoner.minion.goal.desert_tiger.DesertTigerAttackGoal` | 51 | 75 | 92 |
| `common.summoner.minion.goal.desert_tiger.DesertTigerIdleGoal` | 51 | 38 | 92 |
| `common.summoner.minion.goal.eye_laser_turret.EyeLaserTurretAttackGoal` | 51 | 22 | 92 |
| `common.summoner.minion.goal.eye_laser_turret.EyeLaserTurretIdleGoal` | 51 | 21 | 92 |
| `common.summoner.minion.goal.finch.FinchAttackGoal` | 51 | 37 | 92 |
| `common.summoner.minion.goal.finch.FinchIdleGoal` | 51 | 26 | 92 |
| `common.summoner.minion.goal.hornet.HornetAttackGoal` | 51 | 66 | 92 |
| `common.summoner.minion.goal.hornet.HornetIdleGoal` | 51 | 38 | 92 |
| `common.summoner.minion.goal.imp.ImpAttackGoal` | 51 | 50 | 92 |
| `common.summoner.minion.goal.imp.ImpIdleGoal` | 51 | 37 | 92 |
| `common.summoner.minion.goal.iron_golem.IronGolemAttackGoal` | 51 | 48 | 92 |
| `common.summoner.minion.goal.iron_golem.IronGolemIdleGoal` | 51 | 40 | 92 |
| `common.summoner.minion.goal.ruin_relic.RuinRelicAttackGoal` | 51 | 26 | 92 |
| `common.summoner.minion.goal.ruin_relic.RuinRelicIdleGoal` | 51 | 20 | 92 |
| `common.summoner.minion.goal.sanguine_bat.SanguineBatAttackGoal` | 51 | 43 | 92 |
| `common.summoner.minion.goal.sanguine_bat.SanguineBatIdleGoal` | 51 | 25 | 92 |
| `common.summoner.minion.goal.sculk_wisp.SculkWispAttackGoal` | 51 | 79 | 92 |
| `common.summoner.minion.goal.sculk_wisp.SculkWispIdleGoal` | 51 | 38 | 92 |
| `common.summoner.minion.goal.slime.SlimeAttackGoal` | 51 | 21 | 92 |
| `common.summoner.minion.goal.slime.SlimeIdleGoal` | 51 | 40 | 92 |
| `common.summoner.minion.goal.snow_flinx.SnowFlinxAttackGoal` | 51 | 28 | 92 |
| `common.summoner.minion.goal.snow_flinx.SnowFlinxIdleGoal` | 51 | 40 | 92 |
| `common.summoner.minion.goal.spider.SpiderAttackGoal` | 51 | 80 | 92 |
| `common.summoner.minion.goal.spider.SpiderIdleGoal` | 51 | 38 | 92 |
| `common.summoner.minion.goal.terraprisma.TerraprismAttackGoal` | 51 | 192 | 92 |
| `common.summoner.minion.goal.terraprisma.TerraprismIdleGoal` | 51 | 21 | 92 |
| `common.summoner.minion.goal.terraprisma.TerraprismPrepGoal` | 51 | 34 | 92 |
| `common.summoner.minion.goal.vampire_frog.VampireFrogAttackGoal` | 51 | 35 | 92 |
| `common.summoner.minion.goal.vampire_frog.VampireFrogIdleGoal` | 51 | 40 | 92 |
| `common.summoner.projectile.EyeFireball` | 51 | 77 | 92 |
| `common.summoner.projectile.ForbiddenOrb` | 51 | 97 | 92 |
| `common.summoner.projectile.HornetStinger` | 51 | 43 | 92 |
| `common.summoner.projectile.ImpFireball` | 51 | 46 | 92 |
| `common.summoner.register.SummonerAttachmentEntityTypes` | 51 | 42 | 92 |
| `common.item.yoyo.YoyoEquipment` | 20 | 76 | 1 |
| `common.entity.yoyo.YoyoEntity` | 19 | 468 | 4 |
| `common.item.yoyo.YoyoItem` | 19 | 132 | 4 |
| `common.attachment.YoyoSession` | 19 | 131 | 4 |
| `common.item.sword.SwordDefinition` | 14 | 107 | 1 |
| `api.whip.WhipAppearance` | 9 | 22 | （不是种子） |
| `api.whip.WhipDirectHitContext` | 9 | 14 | （不是种子） |
| `api.whip.WhipFriendlyHitContext` | 9 | 11 | （不是种子） |
| `api.whip.WhipSegment` | 9 | 29 | （不是种子） |
| `api.whip.curve.KeyframedWhipCurve` | 9 | 62 | （不是种子） |
| `api.whip.curve.RetractingWhipCurve` | 9 | 45 | （不是种子） |
| `api.whip.curve.WhipCurve` | 9 | 8 | （不是种子） |
| `api.whip.curve.WhipCurveSampler` | 9 | 131 | （不是种子） |
| `api.whip.curve.WhipCurves` | 9 | 34 | （不是种子） |
| `api.whip.curve.WhipFrame` | 9 | 21 | （不是种子） |
| `common.entity.projectile.whip.WhipCollisionGeometry` | 9 | 68 | 1 |
| `common.entity.yoyo.BaseYoyoProjectile` | 9 | 140 | 5 |
| `common.item.whip.BaseWhipItem` | 8 | 119 | 45 |
| `common.item.whip.WhipSession` | 8 | 70 | 45 |
| `common.entity.projectile.whip.WhipAttackEntity` | 8 | 470 | 45 |
| `common.component.GunPropertyComponent` | 7 | 47 | 1 |
| `common.item.gun.definition.FireMode` | 7 | 5 | 1 |
| `common.item.gun.definition.GunProjectilePattern` | 7 | 47 | 1 |
| `common.item.gun.definition.GunDefinition` | 6 | 51 | 4 |
| `common.enchantment.AbstractManaEnchantment` | 6 | 25 | 1 |
| `common.entity.projectile.flail.FlailAuxiliaryProjectile` | 6 | 100 | 1 |
| `common.item.gun.BaseGun` | 5 | 165 | 5 |
| `common.init.ModBlockCounters` | 5 | 80 | 158 |
| `common.init.ModMiniBiomes` | 5 | 91 | 158 |
| `client.particle.BladeTrailEmitter` | 5 | 78 | （不是种子） |
| `api.event.GunEvent` | 4 | 234 | （不是种子） |
| `common.combat.gun.ShotContext` | 4 | 14 | 1 |
| `common.item.sword.BasePhasebladeItem` | 4 | 191 | 5 |
| `common.item.sword.Phasesaber` | 4 | 23 | 5 |
| `common.entity.projectile.sword.PhasebladeProjectile` | 4 | 415 | 5 |
| `common.entity.storage.StorageCompanionEntity` | 4 | 217 | 2 |
| `common.item.storage.StorageCompanionItem` | 4 | 119 | 2 |
| `common.combat.gun.AmmoStats` | 3 | 4 | 1 |
| `common.combat.gun.Ballistics` | 3 | 3 | 1 |
| `common.combat.gun.GunStats` | 3 | 4 | 1 |
| `common.effect.harmful.DriveAwayController` | 3 | 72 | 1 |
| `common.component.SwordProjectileAppearance` | 3 | 133 | 1 |
| `common.item.whip.WhipDamageSource` | 3 | 15 | 1 |
| `common.combat.gun.BallisticsResolver` | 2 | 10 | 4 |
| `common.combat.gun.GunProjectileFactory` | 2 | 69 | 164 |
| `common.item.whip.FirecrackerItem` | 2 | 105 | 47 |
| `common.entity.yoyo.CascadeFireProjectile` | 2 | 27 | 6 |
| `common.entity.yoyo.ChikCrystalProjectile` | 2 | 22 | 6 |
| `common.entity.yoyo.CthulhuEyeProjectile` | 2 | 32 | 6 |
| `common.entity.yoyo.KrakenWaveProjectile` | 2 | 16 | 6 |
| `common.entity.yoyo.TerrarianProjectile` | 2 | 117 | 5 |
