# 逐种子依赖闭包（`tools/port2native/seed_closures.py` 输出）

- 种子 **20** 个；1.21 侧可扩张类型 778 个
- 口径与 `dep_subset.py` 一致：只往「1.21 侧还没有的类型」里扩张，已满足的边算 0 成本、不继续走
- `--alias` 2 条：`org.confluence.lib.util.LibEntityUtils` → `org.confluence.lib.util.LibUtils`, `org.confluence.mod.common.init.entity.ModEntities` → `org.confluence.mod.common.init.entity.ModEntities`

## 按单独闭包从小到大（轻量 ≤12 个文件：**2** 个种子）

| 种子 | 闭包文件 | 其中新增 | 非空行 | 种子自身行 | 1.21 已有同名 |
| --- | ---: | ---: | ---: | ---: | :--: |
| `WindyBalloon` | 1 | 0 | 88 | 88 |  |
| `Gnome` | 1 | 0 | 37 | 37 |  |

## 重量种子（闭包 >12 个文件：**18** 个）

| 种子 | 闭包文件 | 其中新增 | 非空行 | 种子自身行 | 1.21 已有同名 |
| --- | ---: | ---: | ---: | ---: | :--: |
| `HillHungry` | 189 | 188 | 23291 | 22 |  |
| `HungryMovementAction` | 189 | 188 | 23291 | 95 |  |
| `TheHungry` | 189 | 188 | 23291 | 316 |  |
| `VisualNeuron` | 189 | 188 | 23291 | 348 |  |
| `BaseSlime` | 189 | 188 | 23291 | 420 |  |
| `FleshSlime` | 189 | 188 | 23291 | 47 |  |
| `SweetSlime` | 189 | 188 | 23291 | 64 |  |
| `SpikedSlime` | 190 | 189 | 23449 | 158 |  |
| `DungeonSpirit` | 190 | 189 | 23358 | 67 |  |
| `LavaSlime` | 190 | 189 | 23348 | 57 |  |
| `Slimer` | 190 | 189 | 23345 | 54 |  |
| `CorruptSlime` | 190 | 189 | 23339 | 48 |  |
| `GoldenSlime` | 190 | 189 | 23338 | 47 |  |
| `MotherSlime` | 190 | 189 | 23336 | 45 |  |
| `Werewolf` | 190 | 189 | 23323 | 32 |  |
| `TropicSlime` | 190 | 189 | 23306 | 15 |  |
| `LuminousSlime` | 190 | 189 | 23304 | 13 |  |
| `MonsterAttributeScaling` | 192 | 191 | 23553 | 106 |  |

## 公共底座（出现在 ≥2 个种子的闭包里：**189** 个）

**先搬这些，后面每个种子都会变轻。**

| 类型 | 被几个种子需要 | 非空行 | 自己单独搬时的闭包 |
| --- | ---: | ---: | ---: |
| `api.IGeneration` | 18 | 19 | （不是种子） |
| `api.ITrackType` | 18 | 22 | （不是种子） |
| `api.event.BulletEvent` | 18 | 125 | （不是种子） |
| `api.item.ILeftClickStateItem` | 18 | 12 | （不是种子） |
| `client.gui.screen.AnglerDialogScreen` | 18 | 100 | （不是种子） |
| `client.gui.screen.GoblinTinkererDialogScreen` | 18 | 20 | （不是种子） |
| `client.gui.screen.NPCDialogScreen` | 18 | 124 | （不是种子） |
| `common.combat.gun.HomingController` | 18 | 34 | （不是种子） |
| `common.component.BulletPropertyComponent` | 18 | 48 | （不是种子） |
| `common.data.AnglerQuestLoader` | 18 | 133 | （不是种子） |
| `common.data.GamePhase` | 18 | 60 | （不是种子） |
| `common.data.MoonPhase` | 18 | 38 | （不是种子） |
| `common.data.saved.AnglerData` | 18 | 83 | （不是种子） |
| `common.data.spawner.NPCSpawner` | 18 | 967 | （不是种子） |
| `common.effect.harmful.HorrifiedEffect` | 18 | 65 | （不是种子） |
| `common.entity.boss.AbstractTwinEye` | 18 | 250 | （不是种子） |
| `common.entity.boss.BaseWormBoss` | 18 | 319 | （不是种子） |
| `common.entity.boss.BossMultiplayerEnhancement` | 18 | 92 | （不是种子） |
| `common.entity.boss.BossWormPart` | 18 | 409 | （不是种子） |
| `common.entity.boss.BrainFake` | 18 | 180 | （不是种子） |
| `common.entity.boss.BrainOfCthulhu` | 18 | 690 | （不是种子） |
| `common.entity.boss.DeerClops` | 18 | 491 | （不是种子） |
| `common.entity.boss.DungeonGuardian` | 18 | 130 | （不是种子） |
| `common.entity.boss.EaterOfWorlds` | 18 | 956 | （不是种子） |
| `common.entity.boss.EyeOfCthulhu` | 18 | 486 | （不是种子） |
| `common.entity.boss.HillOfFlesh` | 18 | 651 | （不是种子） |
| `common.entity.boss.HillOfFleshEye` | 18 | 46 | （不是种子） |
| `common.entity.boss.HillOfFleshMouth` | 18 | 86 | （不是种子） |
| `common.entity.boss.KingSlime` | 18 | 493 | （不是种子） |
| `common.entity.boss.LunaticCultist` | 18 | 276 | （不是种子） |
| `common.entity.boss.LunaticCultistClone` | 18 | 146 | （不是种子） |
| `common.entity.boss.PhantasmDragon` | 18 | 129 | （不是种子） |
| `common.entity.boss.Plantera` | 18 | 471 | （不是种子） |
| `common.entity.boss.PlanteraHook` | 18 | 140 | （不是种子） |
| `common.entity.boss.PlanteraTentacle` | 18 | 156 | （不是种子） |
| `common.entity.boss.PrimeEnderDragon` | 18 | 537 | （不是种子） |
| `common.entity.boss.PrimeEnderDragonPart` | 18 | 71 | （不是种子） |
| `common.entity.boss.Retinazer` | 18 | 228 | （不是种子） |
| `common.entity.boss.ServantOfCthulhu` | 18 | 146 | （不是种子） |
| `common.entity.boss.Skeletron` | 18 | 519 | （不是种子） |
| `common.entity.boss.SkeletronArmPose` | 18 | 60 | （不是种子） |
| `common.entity.boss.SkeletronHand` | 18 | 350 | （不是种子） |
| `common.entity.boss.SkeletronPrime` | 18 | 321 | （不是种子） |
| `common.entity.boss.SkeletronPrimeArm` | 18 | 323 | （不是种子） |
| `common.entity.boss.Spazmatism` | 18 | 220 | （不是种子） |
| `common.entity.boss.TheDestroyer` | 18 | 493 | （不是种子） |
| `common.entity.boss.TheDestroyerProbe` | 18 | 144 | （不是种子） |
| `common.entity.boss.TheTwins` | 18 | 341 | （不是种子） |
| `common.entity.boss.WallOfFlesh` | 18 | 836 | （不是种子） |
| `common.entity.boss.WallOfFleshEye` | 18 | 61 | （不是种子） |
| `common.entity.boss.WallOfFleshMouth` | 18 | 74 | （不是种子） |
| `common.entity.boss.WallOfFleshPart` | 18 | 101 | （不是种子） |
| `common.entity.model.CrownOfKingSlimeModelEntity` | 18 | 95 | （不是种子） |
| `common.entity.mount.AbstractMountEntity` | 18 | 307 | （不是种子） |
| `common.entity.npc.AnglerNPC` | 18 | 228 | （不是种子） |
| `common.entity.npc.BaseNPC` | 18 | 618 | （不是种子） |
| `common.entity.npc.BurstGunNPC` | 18 | 36 | （不是种子） |
| `common.entity.npc.DryadNPC` | 18 | 95 | （不是种子） |
| `common.entity.npc.GolferNPC` | 18 | 36 | （不是种子） |
| `common.entity.npc.MechanicNPC` | 18 | 22 | （不是种子） |
| `common.entity.npc.NPCAttackBlacklist` | 18 | 96 | （不是种子） |
| `common.entity.npc.NPCNames` | 18 | 74 | （不是种子） |
| `common.entity.npc.NPCTradeGoal` | 18 | 34 | （不是种子） |
| `common.entity.npc.NurseNPC` | 18 | 62 | （不是种子） |
| `common.entity.npc.OldManNPC` | 18 | 103 | （不是种子） |
| `common.entity.npc.SimpleNPC` | 18 | 10 | （不是种子） |
| `common.entity.npc.SkeletonMerchantNPC` | 18 | 57 | （不是种子） |
| `common.entity.npc.TownSlimeNPC` | 18 | 94 | （不是种子） |
| `common.entity.npc.TravelingMerchantNPC` | 18 | 84 | （不是种子） |
| `common.entity.npc.ai.NPCCombatActions` | 18 | 167 | （不是种子） |
| `common.entity.npc.ai.NPCCombatProfile` | 18 | 192 | （不是种子） |
| `common.entity.npc.ai.NPCCombatProgression` | 18 | 55 | （不是种子） |
| `common.entity.npc.ai.NPCDefenseGoal` | 18 | 126 | （不是种子） |
| `common.entity.npc.ai.NPCHealGoal` | 18 | 126 | （不是种子） |
| `common.entity.npc.ai.NPCHurtRetreatGoal` | 18 | 77 | （不是种子） |
| `common.entity.npc.ai.NPCReturnHomeGoal` | 18 | 35 | （不是种子） |
| `common.entity.npc.chat.ChatLine` | 18 | 19 | （不是种子） |
| `common.entity.npc.chat.ChatManager` | 18 | 80 | （不是种子） |
| `common.entity.npc.chat.NPCChat` | 18 | 19 | （不是种子） |
| `common.entity.npc.dialog.NPCDialog` | 18 | 21 | （不是种子） |
| `common.entity.npc.dialog.NPCDialogLoader` | 18 | 54 | （不是种子） |
| `common.entity.npc.house.House` | 18 | 29 | （不是种子） |
| `common.entity.npc.house.HouseValidater` | 18 | 110 | （不是种子） |
| `common.entity.npc.mood.Mood` | 18 | 16 | （不是种子） |
| `common.entity.npc.mood.MoodData` | 18 | 90 | （不是种子） |
| `common.entity.npc.mood.MoodEnvironment` | 18 | 46 | （不是种子） |
| `common.entity.npc.mood.NPCMood` | 18 | 127 | （不是种子） |
| `common.entity.npc.trade.NPCTradeList` | 18 | 76 | （不是种子） |
| `common.entity.npc.trade.NPCTradeMenu` | 18 | 471 | （不是种子） |
| `common.entity.npc.trade.NPCTradeOffer` | 18 | 43 | （不是种子） |
| `common.entity.npc.trade.TradeCondition` | 18 | 28 | （不是种子） |
| `common.entity.npc.trade.conditions.AlwaysTrueCondition` | 18 | 18 | （不是种子） |
| `common.entity.npc.trade.conditions.AndCondition` | 18 | 17 | （不是种子） |
| `common.entity.npc.trade.conditions.AnyBossDefeatedCondition` | 18 | 15 | （不是种子） |
| `common.entity.npc.trade.conditions.ArtisanLoafUnusedCondition` | 18 | 19 | （不是种子） |
| `common.entity.npc.trade.conditions.AttackTargetCondition` | 18 | 19 | （不是种子） |
| `common.entity.npc.trade.conditions.BestiaryCondition` | 18 | 15 | （不是种子） |
| `common.entity.npc.trade.conditions.BiomeCondition` | 18 | 31 | （不是种子） |
| `common.entity.npc.trade.conditions.BossDefeatedCondition` | 18 | 19 | （不是种子） |
| `common.entity.npc.trade.conditions.DateCondition` | 18 | 26 | （不是种子） |
| `common.entity.npc.trade.conditions.DimensionCondition` | 18 | 23 | （不是种子） |
| `common.entity.npc.trade.conditions.FluidCondition` | 18 | 28 | （不是种子） |
| `common.entity.npc.trade.conditions.GameEventCondition` | 18 | 23 | （不是种子） |
| `common.entity.npc.trade.conditions.GraveyardCondition` | 18 | 21 | （不是种子） |
| `common.entity.npc.trade.conditions.HardmodeCondition` | 18 | 15 | （不是种子） |
| `common.entity.npc.trade.conditions.KillEntityCondition` | 18 | 20 | （不是种子） |
| `common.entity.npc.trade.conditions.MoodCondition` | 18 | 19 | （不是种子） |
| `common.entity.npc.trade.conditions.MoonPhaseCondition` | 18 | 25 | （不是种子） |
| `common.entity.npc.trade.conditions.NPCItemInHandCondition` | 18 | 21 | （不是种子） |
| `common.entity.npc.trade.conditions.NPCNearbyCondition` | 18 | 21 | （不是种子） |
| `common.entity.npc.trade.conditions.NotCondition` | 18 | 16 | （不是种子） |
| `common.entity.npc.trade.conditions.OrCondition` | 18 | 17 | （不是种子） |
| `common.entity.npc.trade.conditions.PositionHeightCondition` | 18 | 29 | （不是种子） |
| `common.entity.npc.trade.conditions.TimeCondition` | 18 | 23 | （不是种子） |
| `common.entity.npc.trade.conditions.WeatherCondition` | 18 | 22 | （不是种子） |
| `common.entity.npc.trade.conditions.WorldFlagCondition` | 18 | 20 | （不是种子） |
| `common.entity.projectile.AncientLightProjectile` | 18 | 113 | （不是种子） |
| `common.entity.projectile.BaseBulletEntity` | 18 | 435 | （不是种子） |
| `common.entity.projectile.BoomerangProjectile` | 18 | 177 | （不是种子） |
| `common.entity.projectile.CultistProjectile` | 18 | 64 | （不是种子） |
| `common.entity.projectile.CustomBulletEntity` | 18 | 51 | （不是种子） |
| `common.entity.projectile.CyborgExplosiveProjectile` | 18 | 88 | （不是种子） |
| `common.entity.projectile.DeerclopsIcePillarProjectile` | 18 | 110 | （不是种子） |
| `common.entity.projectile.DeerclopsShadowHandProjectile` | 18 | 71 | （不是种子） |
| `common.entity.projectile.DeerclopsThrownIceProjectile` | 18 | 69 | （不是种子） |
| `common.entity.projectile.DestroyerLaserProjectile` | 18 | 19 | （不是种子） |
| `common.entity.projectile.HillLavaPillarProjectile` | 18 | 66 | （不是种子） |
| `common.entity.projectile.NPCProjectileEffects` | 18 | 88 | （不是种子） |
| `common.entity.projectile.NPCWeaponProjectile` | 18 | 121 | （不是种子） |
| `common.entity.projectile.PlanteraProjectile` | 18 | 108 | （不是种子） |
| `common.entity.projectile.PrimeCannonballProjectile` | 18 | 87 | （不是种子） |
| `common.entity.projectile.PrimeLaserProjectile` | 18 | 23 | （不是种子） |
| `common.entity.projectile.SkeletronSkullProjectile` | 18 | 52 | （不是种子） |
| `common.entity.projectile.TwinEyeProjectile` | 18 | 42 | （不是种子） |
| `common.entity.projectile.arrow.BaseArrowEntity` | 18 | 333 | （不是种子） |
| `common.entity.projectile.mana.NPCShadowflameSkullProjectile` | 18 | 26 | （不是种子） |
| `common.gameevent.PartyGameEvent` | 18 | 118 | （不是种子） |
| `common.gameevent.SolarEclipseGameEvent` | 18 | 54 | （不是种子） |
| `common.init.ModCustomRegistries` | 18 | 34 | （不是种子） |
| `common.init.ModGenerationProviderTypes` | 18 | 20 | （不是种子） |
| `common.init.ModTrackTypeProviderTypes` | 18 | 18 | （不是种子） |
| `common.init.ModTradeConditions` | 18 | 35 | （不是种子） |
| `common.init.entity.BossEntities` | 18 | 166 | （不是种子） |
| `common.init.entity.NpcEntities` | 18 | 228 | （不是种子） |
| `common.init.item.BoomerangItems` | 18 | 23 | （不是种子） |
| `common.init.item.MountItems` | 18 | 27 | （不是种子） |
| `common.item.BaseBullet` | 18 | 66 | （不是种子） |
| `common.item.boomerang.BoomerangItem` | 18 | 108 | （不是种子） |
| `common.item.gun.definition.BulletBehavior` | 18 | 28 | （不是种子） |
| `common.item.gun.definition.BulletDefinition` | 18 | 41 | （不是种子） |
| `common.item.gun.definition.BulletImpactEffect` | 18 | 22 | （不是种子） |
| `common.item.gun.definition.behavior.AbstractBulletBehavior` | 18 | 12 | （不是种子） |
| `common.item.gun.definition.behavior.BulletBehaviorSupport` | 18 | 48 | （不是种子） |
| `common.item.gun.definition.behavior.ChlorophyteHomingBehavior` | 18 | 58 | （不是种子） |
| `common.item.gun.definition.behavior.CrystalSplitBehavior` | 18 | 40 | （不是种子） |
| `common.item.gun.definition.behavior.CursedDebuffBehavior` | 18 | 14 | （不是种子） |
| `common.item.gun.definition.behavior.ExplosiveBulletBehavior` | 18 | 19 | （不是种子） |
| `common.item.gun.definition.behavior.HighVelocityDamageDecayBehavior` | 18 | 14 | （不是种子） |
| `common.item.gun.definition.behavior.IchorDebuffBehavior` | 18 | 14 | （不是种子） |
| `common.item.gun.definition.behavior.LuminiteDamageDecayBehavior` | 18 | 14 | （不是种子） |
| `common.item.gun.definition.behavior.MeteorRicochetBehavior` | 18 | 37 | （不是种子） |
| `common.item.gun.definition.behavior.NanoRicochetBehavior` | 18 | 28 | （不是种子） |
| `common.item.gun.definition.behavior.NormalBulletBehavior` | 18 | 7 | （不是种子） |
| `common.item.gun.definition.behavior.PartyBulletBehavior` | 18 | 7 | （不是种子） |
| `common.item.gun.definition.behavior.SilverBulletBehavior` | 18 | 7 | （不是种子） |
| `common.item.gun.definition.behavior.VenomDebuffBehavior` | 18 | 14 | （不是种子） |
| `common.item.mount.MountItem` | 18 | 52 | （不是种子） |
| `common.menu.NPCServiceMenu` | 18 | 7 | （不是种子） |
| `common.mount.MountManager` | 18 | 89 | （不是种子） |
| `common.world.IncrementalCylinderDestruction` | 18 | 100 | （不是种子） |
| `network.c2s.NPCDialogSessionPacketC2S` | 18 | 29 | （不是种子） |
| `network.c2s.OpenNPCServicePacketC2S` | 18 | 32 | （不是种子） |
| `network.c2s.SummonSkeletronPacketC2S` | 18 | 26 | （不是种子） |
| `network.s2c.OpenAnglerDialogPacketS2C` | 18 | 46 | （不是种子） |
| `network.s2c.OpenNPCDialogPacketS2C` | 18 | 28 | （不是种子） |
| `util.generation.GenerationProvider` | 18 | 7 | （不是种子） |
| `util.generation.variant.AboveFallenGeneration` | 18 | 85 | （不是种子） |
| `util.generation.variant.ForwardGeneration` | 18 | 35 | （不是种子） |
| `util.generation.variant.StillGeneration` | 18 | 38 | （不是种子） |
| `util.track.TrackTypeProvider` | 18 | 7 | （不是种子） |
| `util.track.variant.BasisTrack` | 18 | 34 | （不是种子） |
| `util.track.variant.SimpleTrack` | 18 | 48 | （不是种子） |
| `common.entity.monster.HillHungry` | 17 | 22 | 189 |
| `common.entity.monster.HungryMovementAction` | 17 | 95 | 189 |
| `common.entity.monster.TheHungry` | 17 | 316 | 189 |
| `common.entity.monster.VisualNeuron` | 17 | 348 | 189 |
| `common.entity.monster.slime.BaseSlime` | 17 | 420 | 189 |
| `common.entity.monster.slime.FleshSlime` | 17 | 47 | 189 |
| `common.entity.monster.slime.SweetSlime` | 17 | 64 | 189 |
