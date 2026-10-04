# 逐种子依赖闭包（`tools/port2native/seed_closures.py` 输出）

- 种子 **49** 个；1.21 侧可扩张类型 821 个
- 口径与 `dep_subset.py` 一致：只往「1.21 侧还没有的类型」里扩张，已满足的边算 0 成本、不继续走
- `--alias` 2 条：`org.confluence.lib.util.LibEntityUtils` → `org.confluence.lib.util.LibUtils`, `org.confluence.mod.common.init.entity.ModEntities` → `org.confluence.mod.common.init.entity.ModEntities`

## 按单独闭包从小到大（轻量 ≤12 个文件：**18** 个种子）

| 种子 | 闭包文件 | 其中新增 | 非空行 | 种子自身行 | 1.21 已有同名 |
| --- | ---: | ---: | ---: | ---: | :--: |
| `JellyFish` | 1 | 0 | 323 | 323 |  |
| `GiantShelly` | 1 | 0 | 305 | 305 |  |
| `GiantTortoise` | 1 | 0 | 283 | 283 |  |
| `Zombie` | 1 | 0 | 226 | 226 |  |
| `Nymph` | 1 | 0 | 169 | 169 |  |
| `Unicorn` | 1 | 0 | 87 | 87 |  |
| `AntlionSwarmer` | 1 | 0 | 64 | 64 |  |
| `Drippler` | 1 | 0 | 59 | 59 |  |
| `Derpling` | 1 | 0 | 54 | 54 |  |
| `Wyvern` | 1 | 0 | 53 | 53 |  |
| `ShadowflameApparition` | 1 | 0 | 48 | 48 |  |
| `SpittingPlant` | 1 | 0 | 44 | 44 |  |
| `Gnome` | 1 | 0 | 37 | 37 |  |
| `Hornet` | 2 | 1 | 258 | 202 |  |
| `ClimbingSpider` | 2 | 1 | 158 | 130 |  |
| `Harpy` | 2 | 1 | 92 | 71 |  |
| `AngryNimbus` | 2 | 1 | 83 | 70 |  |
| `RedDevil` | 2 | 1 | 81 | 73 |  |

## 重量种子（闭包 >12 个文件：**31** 个）

| 种子 | 闭包文件 | 其中新增 | 非空行 | 种子自身行 | 1.21 已有同名 |
| --- | ---: | ---: | ---: | ---: | :--: |
| `BloodTumor` | 244 | 243 | 29940 | 71 |  |
| `BloodySpore` | 244 | 243 | 29940 | 200 |  |
| `Corruptor` | 244 | 243 | 29940 | 55 |  |
| `DarkCaster` | 244 | 243 | 29940 | 94 |  |
| `DungeonSpirit` | 244 | 243 | 29940 | 67 |  |
| `EaterOfSouls` | 244 | 243 | 29940 | 50 |  |
| `FireImp` | 244 | 243 | 29940 | 45 |  |
| `Gastropod` | 244 | 243 | 29940 | 183 |  |
| `GoblinWarlock` | 244 | 243 | 29940 | 130 |  |
| `HillHungry` | 244 | 243 | 29940 | 22 |  |
| `HungryMovementAction` | 244 | 243 | 29940 | 95 |  |
| `LittleHornet` | 244 | 243 | 29940 | 191 |  |
| `PirateRangedMonster` | 244 | 243 | 29940 | 156 |  |
| `SimpleWormMonster` | 244 | 243 | 29940 | 220 |  |
| `Slimer` | 244 | 243 | 29940 | 54 |  |
| `TheHungry` | 244 | 243 | 29940 | 316 |  |
| `VisualNeuron` | 244 | 243 | 29940 | 348 |  |
| `WaterBoltMimic` | 244 | 243 | 29940 | 145 |  |
| `Werewolf` | 244 | 243 | 29940 | 32 |  |
| `WindyBalloon` | 244 | 243 | 29940 | 88 |  |
| `BaseSlime` | 244 | 243 | 29940 | 420 |  |
| `CorruptSlime` | 244 | 243 | 29940 | 48 |  |
| `FleshSlime` | 244 | 243 | 29940 | 47 |  |
| `GoldenSlime` | 244 | 243 | 29940 | 47 |  |
| `LavaSlime` | 244 | 243 | 29940 | 57 |  |
| `LuminousSlime` | 244 | 243 | 29940 | 13 |  |
| `MotherSlime` | 244 | 243 | 29940 | 45 |  |
| `SpikedSlime` | 244 | 243 | 29940 | 158 |  |
| `SweetSlime` | 244 | 243 | 29940 | 64 |  |
| `TropicSlime` | 244 | 243 | 29940 | 15 |  |
| `MonsterAttributeScaling` | 247 | 246 | 30202 | 106 |  |

## 公共底座（出现在 ≥2 个种子的闭包里：**244** 个）

**先搬这些，后面每个种子都会变轻。**

| 类型 | 被几个种子需要 | 非空行 | 自己单独搬时的闭包 |
| --- | ---: | ---: | ---: |
| `common.entity.projectile.NimbusRain` | 32 | 13 | （不是种子） |
| `common.entity.projectile.HarpyFeatherProjectile` | 32 | 21 | （不是种子） |
| `common.entity.projectile.HornetStingerProjectile` | 32 | 56 | （不是种子） |
| `common.entity.projectile.SpiderWebSpit` | 32 | 28 | （不是种子） |
| `common.entity.projectile.UnholyTridentProjectile` | 32 | 8 | （不是种子） |
| `api.IGeneration` | 31 | 19 | （不是种子） |
| `api.ITrackType` | 31 | 22 | （不是种子） |
| `api.event.BulletEvent` | 31 | 125 | （不是种子） |
| `api.item.ILeftClickStateItem` | 31 | 12 | （不是种子） |
| `client.gui.screen.AnglerDialogScreen` | 31 | 100 | （不是种子） |
| `client.gui.screen.GoblinTinkererDialogScreen` | 31 | 20 | （不是种子） |
| `client.gui.screen.NPCDialogScreen` | 31 | 124 | （不是种子） |
| `common.combat.gun.HomingController` | 31 | 34 | （不是种子） |
| `common.component.BulletPropertyComponent` | 31 | 48 | （不是种子） |
| `common.data.AnglerQuestLoader` | 31 | 133 | （不是种子） |
| `common.data.GamePhase` | 31 | 60 | （不是种子） |
| `common.data.MoonPhase` | 31 | 38 | （不是种子） |
| `common.data.saved.AnglerData` | 31 | 83 | （不是种子） |
| `common.data.spawner.NPCSpawner` | 31 | 967 | （不是种子） |
| `common.effect.harmful.HorrifiedEffect` | 31 | 65 | （不是种子） |
| `common.entity.boss.AbstractTwinEye` | 31 | 250 | （不是种子） |
| `common.entity.boss.BaseWormBoss` | 31 | 319 | （不是种子） |
| `common.entity.boss.BossMultiplayerEnhancement` | 31 | 92 | （不是种子） |
| `common.entity.boss.BossOwnerTracker` | 31 | 127 | （不是种子） |
| `common.entity.boss.BossWormPart` | 31 | 409 | （不是种子） |
| `common.entity.boss.BrainFake` | 31 | 180 | （不是种子） |
| `common.entity.boss.BrainOfCthulhu` | 31 | 690 | （不是种子） |
| `common.entity.boss.DeerClops` | 31 | 491 | （不是种子） |
| `common.entity.boss.DungeonGuardian` | 31 | 130 | （不是种子） |
| `common.entity.boss.EaterOfWorlds` | 31 | 956 | （不是种子） |
| `common.entity.boss.EyeOfCthulhu` | 31 | 486 | （不是种子） |
| `common.entity.boss.HillOfFlesh` | 31 | 651 | （不是种子） |
| `common.entity.boss.HillOfFleshEye` | 31 | 46 | （不是种子） |
| `common.entity.boss.HillOfFleshMouth` | 31 | 86 | （不是种子） |
| `common.entity.boss.KingSlime` | 31 | 493 | （不是种子） |
| `common.entity.boss.LunaticCultist` | 31 | 276 | （不是种子） |
| `common.entity.boss.LunaticCultistClone` | 31 | 146 | （不是种子） |
| `common.entity.boss.PhantasmDragon` | 31 | 129 | （不是种子） |
| `common.entity.boss.Plantera` | 31 | 471 | （不是种子） |
| `common.entity.boss.PlanteraHook` | 31 | 140 | （不是种子） |
| `common.entity.boss.PlanteraTentacle` | 31 | 156 | （不是种子） |
| `common.entity.boss.PrimeEnderDragon` | 31 | 537 | （不是种子） |
| `common.entity.boss.PrimeEnderDragonPart` | 31 | 71 | （不是种子） |
| `common.entity.boss.QueenBee` | 31 | 407 | （不是种子） |
| `common.entity.boss.Retinazer` | 31 | 228 | （不是种子） |
| `common.entity.boss.ServantOfCthulhu` | 31 | 146 | （不是种子） |
| `common.entity.boss.Skeletron` | 31 | 519 | （不是种子） |
| `common.entity.boss.SkeletronArmPose` | 31 | 60 | （不是种子） |
| `common.entity.boss.SkeletronHand` | 31 | 350 | （不是种子） |
| `common.entity.boss.SkeletronPrime` | 31 | 321 | （不是种子） |
| `common.entity.boss.SkeletronPrimeArm` | 31 | 323 | （不是种子） |
| `common.entity.boss.Spazmatism` | 31 | 220 | （不是种子） |
| `common.entity.boss.TheDestroyer` | 31 | 493 | （不是种子） |
| `common.entity.boss.TheDestroyerProbe` | 31 | 144 | （不是种子） |
| `common.entity.boss.TheTwins` | 31 | 341 | （不是种子） |
| `common.entity.boss.WallOfFlesh` | 31 | 836 | （不是种子） |
| `common.entity.boss.WallOfFleshEye` | 31 | 61 | （不是种子） |
| `common.entity.boss.WallOfFleshMouth` | 31 | 74 | （不是种子） |
| `common.entity.boss.WallOfFleshPart` | 31 | 101 | （不是种子） |
| `common.entity.model.CrownOfKingSlimeModelEntity` | 31 | 95 | （不是种子） |
| `common.entity.monster.AngryNimbus` | 31 | 70 | 2 |
| `common.entity.monster.AntlionSwarmer` | 31 | 64 | 1 |
| `common.entity.monster.ClimbingSpider` | 31 | 130 | 2 |
| `common.entity.monster.Derpling` | 31 | 54 | 1 |
| `common.entity.monster.Drippler` | 31 | 59 | 1 |
| `common.entity.monster.GiantShelly` | 31 | 305 | 1 |
| `common.entity.monster.GiantTortoise` | 31 | 283 | 1 |
| `common.entity.monster.Gnome` | 31 | 37 | 1 |
| `common.entity.monster.Harpy` | 31 | 71 | 2 |
| `common.entity.monster.Hornet` | 31 | 202 | 2 |
| `common.entity.monster.JellyFish` | 31 | 323 | 1 |
| `common.entity.monster.Nymph` | 31 | 169 | 1 |
| `common.entity.monster.RedDevil` | 31 | 73 | 2 |
| `common.entity.monster.ShadowflameApparition` | 31 | 48 | 1 |
| `common.entity.monster.SpittingPlant` | 31 | 44 | 1 |
| `common.entity.monster.Unicorn` | 31 | 87 | 1 |
| `common.entity.monster.Wyvern` | 31 | 53 | 1 |
| `common.entity.monster.humanoid.Zombie` | 31 | 226 | 1 |
| `common.entity.mount.AbstractMountEntity` | 31 | 307 | （不是种子） |
| `common.entity.npc.AnglerNPC` | 31 | 228 | （不是种子） |
| `common.entity.npc.BaseNPC` | 31 | 618 | （不是种子） |
| `common.entity.npc.BurstGunNPC` | 31 | 36 | （不是种子） |
| `common.entity.npc.DryadNPC` | 31 | 95 | （不是种子） |
| `common.entity.npc.GolferNPC` | 31 | 36 | （不是种子） |
| `common.entity.npc.MechanicNPC` | 31 | 22 | （不是种子） |
| `common.entity.npc.NPCAttackBlacklist` | 31 | 96 | （不是种子） |
| `common.entity.npc.NPCNames` | 31 | 74 | （不是种子） |
| `common.entity.npc.NPCTradeGoal` | 31 | 34 | （不是种子） |
| `common.entity.npc.NurseNPC` | 31 | 62 | （不是种子） |
| `common.entity.npc.OldManNPC` | 31 | 103 | （不是种子） |
| `common.entity.npc.SimpleNPC` | 31 | 10 | （不是种子） |
| `common.entity.npc.SkeletonMerchantNPC` | 31 | 57 | （不是种子） |
| `common.entity.npc.TownSlimeNPC` | 31 | 94 | （不是种子） |
| `common.entity.npc.TownSlimeRescue` | 31 | 77 | （不是种子） |
| `common.entity.npc.TravelingMerchantNPC` | 31 | 84 | （不是种子） |
| `common.entity.npc.ai.NPCCombatActions` | 31 | 167 | （不是种子） |
| `common.entity.npc.ai.NPCCombatProfile` | 31 | 192 | （不是种子） |
| `common.entity.npc.ai.NPCCombatProgression` | 31 | 55 | （不是种子） |
| `common.entity.npc.ai.NPCDefenseGoal` | 31 | 126 | （不是种子） |
| `common.entity.npc.ai.NPCHealGoal` | 31 | 126 | （不是种子） |
| `common.entity.npc.ai.NPCHurtRetreatGoal` | 31 | 77 | （不是种子） |
| `common.entity.npc.ai.NPCReturnHomeGoal` | 31 | 35 | （不是种子） |
| `common.entity.npc.chat.ChatLine` | 31 | 19 | （不是种子） |
| `common.entity.npc.chat.ChatManager` | 31 | 80 | （不是种子） |
| `common.entity.npc.chat.NPCChat` | 31 | 19 | （不是种子） |
| `common.entity.npc.dialog.NPCDialog` | 31 | 21 | （不是种子） |
| `common.entity.npc.dialog.NPCDialogLoader` | 31 | 54 | （不是种子） |
| `common.entity.npc.house.House` | 31 | 29 | （不是种子） |
| `common.entity.npc.house.HouseValidater` | 31 | 110 | （不是种子） |
| `common.entity.npc.mood.Mood` | 31 | 16 | （不是种子） |
| `common.entity.npc.mood.MoodData` | 31 | 90 | （不是种子） |
| `common.entity.npc.mood.MoodEnvironment` | 31 | 46 | （不是种子） |
| `common.entity.npc.mood.NPCMood` | 31 | 127 | （不是种子） |
| `common.entity.npc.trade.NPCTradeList` | 31 | 76 | （不是种子） |
| `common.entity.npc.trade.NPCTradeMenu` | 31 | 471 | （不是种子） |
| `common.entity.npc.trade.NPCTradeOffer` | 31 | 43 | （不是种子） |
| `common.entity.npc.trade.TradeCondition` | 31 | 28 | （不是种子） |
| `common.entity.npc.trade.conditions.AlwaysTrueCondition` | 31 | 18 | （不是种子） |
| `common.entity.npc.trade.conditions.AndCondition` | 31 | 17 | （不是种子） |
| `common.entity.npc.trade.conditions.AnyBossDefeatedCondition` | 31 | 15 | （不是种子） |
| `common.entity.npc.trade.conditions.ArtisanLoafUnusedCondition` | 31 | 19 | （不是种子） |
| `common.entity.npc.trade.conditions.AttackTargetCondition` | 31 | 19 | （不是种子） |
| `common.entity.npc.trade.conditions.BestiaryCondition` | 31 | 15 | （不是种子） |
| `common.entity.npc.trade.conditions.BiomeCondition` | 31 | 31 | （不是种子） |
| `common.entity.npc.trade.conditions.BossDefeatedCondition` | 31 | 19 | （不是种子） |
| `common.entity.npc.trade.conditions.DateCondition` | 31 | 26 | （不是种子） |
| `common.entity.npc.trade.conditions.DimensionCondition` | 31 | 23 | （不是种子） |
| `common.entity.npc.trade.conditions.FluidCondition` | 31 | 28 | （不是种子） |
| `common.entity.npc.trade.conditions.GameEventCondition` | 31 | 23 | （不是种子） |
| `common.entity.npc.trade.conditions.GraveyardCondition` | 31 | 21 | （不是种子） |
| `common.entity.npc.trade.conditions.HardmodeCondition` | 31 | 15 | （不是种子） |
| `common.entity.npc.trade.conditions.KillEntityCondition` | 31 | 20 | （不是种子） |
| `common.entity.npc.trade.conditions.MoodCondition` | 31 | 19 | （不是种子） |
| `common.entity.npc.trade.conditions.MoonPhaseCondition` | 31 | 25 | （不是种子） |
| `common.entity.npc.trade.conditions.NPCItemInHandCondition` | 31 | 21 | （不是种子） |
| `common.entity.npc.trade.conditions.NPCNearbyCondition` | 31 | 21 | （不是种子） |
| `common.entity.npc.trade.conditions.NotCondition` | 31 | 16 | （不是种子） |
| `common.entity.npc.trade.conditions.OrCondition` | 31 | 17 | （不是种子） |
| `common.entity.npc.trade.conditions.PositionHeightCondition` | 31 | 29 | （不是种子） |
| `common.entity.npc.trade.conditions.TimeCondition` | 31 | 23 | （不是种子） |
| `common.entity.npc.trade.conditions.WeatherCondition` | 31 | 22 | （不是种子） |
| `common.entity.npc.trade.conditions.WorldFlagCondition` | 31 | 20 | （不是种子） |
| `common.entity.projectile.AncientLightProjectile` | 31 | 113 | （不是种子） |
| `common.entity.projectile.BaseBulletEntity` | 31 | 435 | （不是种子） |
| `common.entity.projectile.BoomerangProjectile` | 31 | 177 | （不是种子） |
| `common.entity.projectile.CultistProjectile` | 31 | 64 | （不是种子） |
| `common.entity.projectile.CustomBulletEntity` | 31 | 51 | （不是种子） |
| `common.entity.projectile.CyborgExplosiveProjectile` | 31 | 88 | （不是种子） |
| `common.entity.projectile.DeerclopsIcePillarProjectile` | 31 | 110 | （不是种子） |
| `common.entity.projectile.DeerclopsShadowHandProjectile` | 31 | 71 | （不是种子） |
| `common.entity.projectile.DeerclopsThrownIceProjectile` | 31 | 69 | （不是种子） |
| `common.entity.projectile.DestroyerLaserProjectile` | 31 | 19 | （不是种子） |
| `common.entity.projectile.HillLavaPillarProjectile` | 31 | 66 | （不是种子） |
| `common.entity.projectile.HostileParticleProjectile` | 31 | 306 | （不是种子） |
| `common.entity.projectile.NPCProjectileEffects` | 31 | 88 | （不是种子） |
| `common.entity.projectile.NPCWeaponProjectile` | 31 | 121 | （不是种子） |
| `common.entity.projectile.PirateShot` | 31 | 64 | （不是种子） |
| `common.entity.projectile.PlanteraProjectile` | 31 | 108 | （不是种子） |
| `common.entity.projectile.PrimeCannonballProjectile` | 31 | 87 | （不是种子） |
| `common.entity.projectile.PrimeLaserProjectile` | 31 | 23 | （不是种子） |
| `common.entity.projectile.SkeletronSkullProjectile` | 31 | 52 | （不是种子） |
| `common.entity.projectile.TwinEyeProjectile` | 31 | 42 | （不是种子） |
| `common.entity.projectile.arrow.BaseArrowEntity` | 31 | 333 | （不是种子） |
| `common.entity.projectile.mana.NPCShadowflameSkullProjectile` | 31 | 26 | （不是种子） |
| `common.gameevent.PartyGameEvent` | 31 | 118 | （不是种子） |
| `common.gameevent.PirateInvasionGameEvent` | 31 | 147 | （不是种子） |
| `common.gameevent.SandstormGameEvent` | 31 | 86 | （不是种子） |
| `common.gameevent.SolarEclipseGameEvent` | 31 | 54 | （不是种子） |
| `common.init.ModCustomRegistries` | 31 | 34 | （不是种子） |
| `common.init.ModGenerationProviderTypes` | 31 | 20 | （不是种子） |
| `common.init.ModTrackTypeProviderTypes` | 31 | 18 | （不是种子） |
| `common.init.ModTradeConditions` | 31 | 35 | （不是种子） |
| `common.init.entity.BossEntities` | 31 | 166 | （不是种子） |
| `common.init.entity.DevelopmentSpawnPolicy` | 31 | 21 | （不是种子） |
| `common.init.entity.MonsterEntities` | 31 | 826 | （不是种子） |
| `common.init.entity.NpcEntities` | 31 | 228 | （不是种子） |
| `common.init.item.BoomerangItems` | 31 | 23 | （不是种子） |
| `common.init.item.MountItems` | 31 | 27 | （不是种子） |
| `common.item.BaseBullet` | 31 | 66 | （不是种子） |
| `common.item.boomerang.BoomerangItem` | 31 | 108 | （不是种子） |
| `common.item.gun.definition.BulletBehavior` | 31 | 28 | （不是种子） |
| `common.item.gun.definition.BulletDefinition` | 31 | 41 | （不是种子） |
| `common.item.gun.definition.BulletImpactEffect` | 31 | 22 | （不是种子） |
| `common.item.gun.definition.behavior.AbstractBulletBehavior` | 31 | 12 | （不是种子） |
| `common.item.gun.definition.behavior.BulletBehaviorSupport` | 31 | 48 | （不是种子） |
| `common.item.gun.definition.behavior.ChlorophyteHomingBehavior` | 31 | 58 | （不是种子） |
| `common.item.gun.definition.behavior.CrystalSplitBehavior` | 31 | 40 | （不是种子） |
| `common.item.gun.definition.behavior.CursedDebuffBehavior` | 31 | 14 | （不是种子） |
| `common.item.gun.definition.behavior.ExplosiveBulletBehavior` | 31 | 19 | （不是种子） |
| `common.item.gun.definition.behavior.HighVelocityDamageDecayBehavior` | 31 | 14 | （不是种子） |
| `common.item.gun.definition.behavior.IchorDebuffBehavior` | 31 | 14 | （不是种子） |
| `common.item.gun.definition.behavior.LuminiteDamageDecayBehavior` | 31 | 14 | （不是种子） |
| `common.item.gun.definition.behavior.MeteorRicochetBehavior` | 31 | 37 | （不是种子） |
| `common.item.gun.definition.behavior.NanoRicochetBehavior` | 31 | 28 | （不是种子） |
| `common.item.gun.definition.behavior.NormalBulletBehavior` | 31 | 7 | （不是种子） |
| `common.item.gun.definition.behavior.PartyBulletBehavior` | 31 | 7 | （不是种子） |
| `common.item.gun.definition.behavior.SilverBulletBehavior` | 31 | 7 | （不是种子） |
| `common.item.gun.definition.behavior.VenomDebuffBehavior` | 31 | 14 | （不是种子） |
| `common.item.mount.MountItem` | 31 | 52 | （不是种子） |
| `common.menu.NPCServiceMenu` | 31 | 7 | （不是种子） |
| `common.mount.MountManager` | 31 | 89 | （不是种子） |
| `common.world.IncrementalCylinderDestruction` | 31 | 100 | （不是种子） |
| `network.c2s.NPCDialogSessionPacketC2S` | 31 | 29 | （不是种子） |
| `network.c2s.OpenNPCServicePacketC2S` | 31 | 32 | （不是种子） |
| `network.c2s.SummonSkeletronPacketC2S` | 31 | 26 | （不是种子） |
| `network.s2c.OpenAnglerDialogPacketS2C` | 31 | 46 | （不是种子） |
| `network.s2c.OpenNPCDialogPacketS2C` | 31 | 28 | （不是种子） |
| `util.generation.GenerationProvider` | 31 | 7 | （不是种子） |
| `util.generation.variant.AboveFallenGeneration` | 31 | 85 | （不是种子） |
| `util.generation.variant.ForwardGeneration` | 31 | 35 | （不是种子） |
| `util.generation.variant.StillGeneration` | 31 | 38 | （不是种子） |
| `util.track.TrackTypeProvider` | 31 | 7 | （不是种子） |
| `util.track.variant.BasisTrack` | 31 | 34 | （不是种子） |
| `util.track.variant.SimpleTrack` | 31 | 48 | （不是种子） |
| `common.entity.monster.BloodySpore` | 30 | 200 | 244 |
| `common.entity.monster.Corruptor` | 30 | 55 | 244 |
| `common.entity.monster.DarkCaster` | 30 | 94 | 244 |
| `common.entity.monster.DungeonSpirit` | 30 | 67 | 244 |
| `common.entity.monster.EaterOfSouls` | 30 | 50 | 244 |
| `common.entity.monster.FireImp` | 30 | 45 | 244 |
| `common.entity.monster.Gastropod` | 30 | 183 | 244 |
| `common.entity.monster.GoblinWarlock` | 30 | 130 | 244 |
| `common.entity.monster.HillHungry` | 30 | 22 | 244 |
| `common.entity.monster.HungryMovementAction` | 30 | 95 | 244 |
| `common.entity.monster.LittleHornet` | 30 | 191 | 244 |
| `common.entity.monster.PirateRangedMonster` | 30 | 156 | 244 |
| `common.entity.monster.SimpleWormMonster` | 30 | 220 | 244 |
| `common.entity.monster.Slimer` | 30 | 54 | 244 |
| `common.entity.monster.TheHungry` | 30 | 316 | 244 |
| `common.entity.monster.VisualNeuron` | 30 | 348 | 244 |
| `common.entity.monster.WaterBoltMimic` | 30 | 145 | 244 |
| `common.entity.monster.Werewolf` | 30 | 32 | 244 |
| `common.entity.monster.WindyBalloon` | 30 | 88 | 244 |
| `common.entity.monster.slime.BaseSlime` | 30 | 420 | 244 |
| `common.entity.monster.slime.CorruptSlime` | 30 | 48 | 244 |
| `common.entity.monster.slime.FleshSlime` | 30 | 47 | 244 |
| `common.entity.monster.slime.GoldenSlime` | 30 | 47 | 244 |
| `common.entity.monster.slime.LavaSlime` | 30 | 57 | 244 |
| `common.entity.monster.slime.LuminousSlime` | 30 | 13 | 244 |
| `common.entity.monster.slime.MotherSlime` | 30 | 45 | 244 |
| `common.entity.monster.slime.SpikedSlime` | 30 | 158 | 244 |
| `common.entity.monster.slime.SweetSlime` | 30 | 64 | 244 |
| `common.entity.monster.slime.TropicSlime` | 30 | 15 | 244 |
| `common.entity.monster.BloodTumor` | 30 | 71 | 244 |
