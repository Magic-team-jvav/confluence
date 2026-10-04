# TE 退役逐条施工依据（te_refs.py 生成）

> 生成命令：`python tools/port2native/te_refs.py --worksheet`

> 1.20 侧 terraentity 引用数 = 0，故每条引用的目标是**同一位置的 1.20 原生写法**。

> difflib 逐行对齐给出「1.20 对侧行」，仅供比对；同名类在 1.20 里的写法即最终形态。

## 一、按 TE 类汇总

| TE 类 | 引用数 | 处理 |
|---|---:|---|
| `TEMonsterEntities` | 567 | 改指 `common/init/entity/MonsterEntities.java` |
| `TEAnimals` | 199 | 改指 `common/init/entity/CritterEntities.java` |
| `TENpcEntities` | 65 | 改指 `common/init/entity/NpcEntities.java` |
| `TEBossEntities` | 60 | 改指 `common/init/entity/BossEntities.java` |
| `TEEffects` | 44 | 改指 `common/init/ModEffects.java` |
| `TEYoyosItems` | 22 | 改指 `common/init/item/YoyoItems.java` |
| `TEBoomerangItems` | 16 | 改指 `common/init/item/BoomerangItems.java` |
| `TETags` | 16 | 改指 `common/init/ModTags.java` |
| `DemonEyeVariant` | 15 | 逐条判定（见 §三） |
| `TESummonItems` | 14 | 改指 `common/init/item/SummonItems.java` |
| `TEUtils` | 14 | 逐条判定（见 §三） |
| `TEWhipItems` | 12 | 改指 `common/init/item/WhipItems.java` |
| `GeoNegativeVolumeRenderer` | 7 | 逐条判定（见 §三） |
| `ITrackType` | 6 | 逐条判定（见 §三） |
| `ITradeLock` | 6 | 逐条判定（见 §三） |
| `TESounds` | 6 | 改指 `common/init/ModSoundEvents.java` |
| `TESpawnEggItems` | 6 | 改指 `common/init/item/SpawnEggItems.java` |
| `AbstractTerraBossBase` | 5 | 逐条判定（见 §三） |
| `TEProjectileEntities` | 5 | 改指 `common/init/ModEntities.java` |
| `TerraEntity` | 5 | 逐条判定（见 §三） |
| `WallOfFlesh` | 5 | 逐条判定（见 §三） |
| `BaseWormPart` | 4 | 逐条判定（见 §三） |
| `IEffectStrategy` | 4 | 逐条判定（见 §三） |
| `IMinion` | 4 | 逐条判定（见 §三） |
| `TEItems` | 4 | 改指 `common/init/item/ModItems.java` |
| `AbstractMonster` | 3 | 逐条判定（见 §三） |
| `CuriosHelper` | 3 | 逐条判定（见 §三） |
| `DeathAnimOptions` | 3 | 逐条判定（见 §三） |
| `DebugBlocksHelper` | 3 | 逐条判定（见 §三） |
| `ITradeHolder` | 3 | 逐条判定（见 §三） |
| `NPCTradeManager` | 3 | 逐条判定（见 §三） |
| `TEFigureBlocks` | 3 | 改指 `common/init/block/FigureBlocks.java` |
| `WoodenMimic` | 3 | 逐条判定（见 §三） |
| `AbstractTerraNPC` | 2 | 逐条判定（见 §三） |
| `AimUtils` | 2 | 逐条判定（见 §三） |
| `BaseWorm` | 2 | 逐条判定（见 §三） |
| `BasisTrack` | 2 | 逐条判定（见 §三） |
| `DemonEye` | 2 | 逐条判定（见 §三） |
| `FlyMonsterPrefab` | 2 | 逐条判定（见 §三） |
| `ILeftClickStateItem` | 2 | 逐条判定（见 §三） |
| `ISummonMob` | 2 | 逐条判定（见 §三） |
| `KeyframeAnimation` | 2 | 逐条判定（见 §三） |
| `NPCMood` | 2 | 逐条判定（见 §三） |
| `RecipeDrawerUtils` | 2 | 逐条判定（见 §三） |
| `SimpleTrack` | 2 | 逐条判定（见 §三） |
| `SimpleVariantAnimal` | 2 | 逐条判定（见 §三） |
| `TEPetItems` | 2 | 改指 `common/init/item/PetItems.java` |
| `TradeParams` | 2 | 逐条判定（见 §三） |
| `renderDebugBlock` | 2 | 逐条判定（见 §三） |
| `AbstractBufferManager` | 1 | 逐条判定（见 §三） |
| `AbstractSummonMob` | 1 | 逐条判定（见 §三） |
| `AnglerNPC` | 1 | 逐条判定（见 §三） |
| `BaseEntityRenderer` | 1 | 逐条判定（见 §三） |
| `BaseSlime` | 1 | 逐条判定（见 §三） |
| `BoneSerpent` | 1 | 逐条判定（见 §三） |
| `BrainOfCthulhu` | 1 | 逐条判定（见 §三） |
| `DungeonGuardian` | 1 | 逐条判定（见 §三） |
| `EaterOfWorlds` | 1 | 逐条判定（见 §三） |
| `GeoNormalRenderer` | 1 | 逐条判定（见 §三） |
| `GeoWormRenderer` | 1 | 逐条判定（见 §三） |
| `GoldenSlime` | 1 | 逐条判定（见 §三） |
| `HillOfFlesh` | 1 | 逐条判定（见 §三） |
| `IAttackableProjectile` | 1 | 逐条判定（见 §三） |
| `ICollisionAttackEntity` | 1 | 逐条判定（见 §三） |
| `IHouseDetector` | 1 | 逐条判定（见 §三） |
| `IPlayer` | 1 | 逐条判定（见 §三） |
| `IVariant` | 1 | 逐条判定（见 §三） |
| `IZombie` | 1 | 逐条判定（见 §三） |
| `LittleHornet` | 1 | 逐条判定（见 §三） |
| `NPCEvent` | 1 | 逐条判定（见 §三） |
| `QueenBee` | 1 | 逐条判定（见 §三） |
| `SpitParticle` | 1 | 逐条判定（见 §三） |
| `SurefaceWorm` | 1 | 逐条判定（见 §三） |
| `TEArmors` | 1 | 改指 `common/init/item/ArmorItems.java` |
| `TEEnchantmentHelper` | 1 | 逐条判定（见 §三） |
| `TEEnchantments` | 1 | 改指 `common/init/ModEnchantments.java` |
| `TEEntities` | 1 | 改指 `common/init/ModEntities.java` |
| `TETradeScreen` | 1 | 逐条判定（见 §三） |
| `UpdateNPCTradePacket` | 1 | 逐条判定（见 §三） |
| `VariantsTextureMaps` | 1 | 逐条判定（见 §三） |
| `WallOfFleshRenderer` | 1 | 逐条判定（见 §三） |
| `WeaponStorage` | 1 | 逐条判定（见 §三） |

## 二、逐文件清单（1.21 行 -> 1.20 对侧行）

### `api/event/CustomMimicSummonKeyEvent.java`

- 1.20 对应：`org/confluence/mod/api/event/CustomMimicSummonKeyEvent.java`
- TE import：`WoodenMimic`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 34 | `public static void summon(WoodenMimic mimic, ChestBlockEntity blockEntity) {` | `public static void summon(BaseMimic mimic, ChestBlockEntity blockEntity) {` |

### `api/event/bestiary/RegisterBestiaryKeyEvent.java`

- 1.20 对应：`org/confluence/mod/api/event/bestiary/RegisterBestiaryKeyEvent.java`
- TE import：`IVariant`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 37 | `public static <V, T extends LivingEntity & IVariant<V>> Factory<T> terraVariant(Function<V, String> toString) {` | `<无>` |

### `api/event/bestiary/RegisterCustomBestiaryEntryRendererEvent.java`

- 1.20 对应：`org/confluence/mod/api/event/bestiary/RegisterCustomBestiaryEntryRendererEvent.java`
- TE import：`BaseWorm`, `BaseWormPart`, `BoneSerpent`, `SurefaceWorm`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 36 | `public void registerBaseWorm(DeferredHolder<EntityType<?>, EntityType<BaseWorm<BaseWormPart>>> holder) {` | `public void registerBaseWorm(RegistryObject<EntityType<SimpleWormMonster>> holder) {` |
| 36 | `public void registerBaseWorm(DeferredHolder<EntityType<?>, EntityType<BaseWorm<BaseWormPart>>> holder) {` | `public void registerBaseWorm(RegistryObject<EntityType<SimpleWormMonster>> holder) {` |
| 39 | `public void registeSurefaceWorm(DeferredHolder<EntityType<?>, EntityType<SurefaceWorm<BaseWormPart>>> holder) {` | `register(holder.get().getDescriptionId(), new GeoWormBestiaryEntryRenderer(context, holder.getId()));` |
| 39 | `public void registeSurefaceWorm(DeferredHolder<EntityType<?>, EntityType<SurefaceWorm<BaseWormPart>>> holder) {` | `register(holder.get().getDescriptionId(), new GeoWormBestiaryEntryRenderer(context, holder.getId()));` |
| 42 | `public void registerBoneSerpent(DeferredHolder<EntityType<?>, EntityType<BoneSerpent<BaseWormPart>>> holder) {` | `register(holder.get().getDescriptionId(), new GeoWormBestiaryEntryRenderer(context, holder.getId()));` |
| 42 | `public void registerBoneSerpent(DeferredHolder<EntityType<?>, EntityType<BoneSerpent<BaseWormPart>>> holder) {` | `register(holder.get().getDescriptionId(), new GeoWormBestiaryEntryRenderer(context, holder.getId()));` |

### `client/ClientConfigs.java`

- 1.20 对应：`org/confluence/mod/client/ClientConfigs.java`
- TE import：`TETradeScreen`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 373 | `return Minecraft.getInstance().screen instanceof TETradeScreen<?>;` | `return Minecraft.getInstance().player != null` |

### `client/effect/SpelunkerHelper.java`

- 1.20 对应：`org/confluence/mod/client/effect/SpelunkerHelper.java`
- TE import：`AbstractBufferManager`, `renderDebugBlock`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 55 | `public class SpelunkerHelper extends AbstractBufferManager {` | `public class SpelunkerHelper extends AbstractBufferManager {` |
| 531 | `renderDebugBlock(buffer, blockPos, size, r, g, b, a, up, down, north, south, east, west);` | `LibRenderUtils.renderDebugBlock(buffer, blockPos, size, r, g, b, a, up, down, north, south, east, west);` |
| 534 | `renderDebugBlock(buffer, blockPos, size, r, g, b, a);` | `LibRenderUtils.renderDebugBlock(buffer, blockPos, size, r, g, b, a);` |

### `client/event/GameClientEvents.java`

- 1.20 对应：`org/confluence/mod/client/event/GameClientEvents.java`
- TE import：`NPCEvent`, `TENpcEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 505 | `public static void npc$Dialog(NPCEvent.NPCDialogEvent event) {` | `//        LocalPlayer player = Minecraft.getInstance().player;` |
| 509 | `if (!ModClientSetups.guideCheckedJEI && type == TENpcEntities.GUIDE.get()) {` | `//            event.setNeoDialog(Component.translatable("dialogs.confluence.guide.jei_check"));` |
| 512 | `} else if (type == TENpcEntities.NURSE.get() && event.getNPC().getRandom().nextInt(25) == 0) {` | `//            StatsCounter stats = player.getStats();` |

### `client/event/ModClientEvents.java`

- 1.20 对应：`org/confluence/mod/client/event/ModClientEvents.java`
- TE import：`GeoNegativeVolumeRenderer`, `SpitParticle`, `TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 582 | `event.registerEntityRenderer(HELL_BAT_ARROW.get(), context -> new GeoArrowRenderer(context, TEMonsterEntities.HELL_BAT.getId(), 0.5f, 0));` | `event.registerEntityRenderer(HELL_BAT_ARROW.get(), context -> new GeoArrowRenderer(context, MonsterEntities.HELL_BAT.getId()));` |
| 634 | `event.registerEntityRenderer(BLOOD_CLOUD_PROJECTILE.get(), context -> new GeoNegativeVolumeRenderer<>(context, new BloodCloudProjectileModel(), false, 2, -0.2F));` | `event.registerEntityRenderer(BLOOD_CLOUD.get(), context -> new GeoNegativeVolumeRenderer<>(context, new BloodCloudProjectileModel(), false, 2.0F, -0.2F));` |
| 636 | `event.registerEntityRenderer(RAIN_CLOUD_PROJECTILE.get(), context -> new GeoNegativeVolumeRenderer<>(context, new RainCloudProjectileModel(), false, 2, -0.2F));` | `event.registerEntityRenderer(RAIN_CLOUD.get(), context -> new GeoNegativeVolumeRenderer<>(context, new RainCloudProjectileModel(), false, 2.0F, -0.2F));` |
| 878 | `event.registerEntityRenderer(MonsterEntities.CURSED_SKULL.get(), c -> new GeoNegativeVolumeRenderer<>(c, new GeoNormalModel<>(MonsterEntities.CURSED_SKULL.getId()), true, 1.0F, 0.0F).addBoneToGlow("outline"));` | `// Critter renderers — Bunny 保留自定义模型，其余用 CritterRenderer` |
| 888 | `event.registerEntityRenderer(MonsterEntities.GRANITE_ELEMENTAL.get(), c -> new GeoNegativeVolumeRenderer<>(c, new GeoNormalModel<>(MonsterEntities.GRANITE_ELEMENTAL.getId()), true, 1.0F, 0.0F).addBoneToGlow("Core"));` | `// Critter renderers — Bunny 保留自定义模型，其余用 CritterRenderer` |
| 889 | `event.registerEntityRenderer(MonsterEntities.ANGLER_FISH.get(), c -> new GeoNegativeVolumeRenderer<>(c, new GeoNormalModel<>(MonsterEntities.ANGLER_FISH.getId(), false), true, 1.0F, -0.1875F).addBoneToGlow("light"));` | `// Critter renderers — Bunny 保留自定义模型，其余用 CritterRenderer` |
| 1216 | `event.registerSpriteSet(ModParticleTypes.SPIT_GLOW.get(), SpitParticle.EmissiveProvider::new);` | `<无>` |
| 1330 | `event.registeSurefaceWorm(TEMonsterEntities.DEVOURER);` | `event.registeSurefaceWorm(MonsterEntities.DEVOURER);` |
| 1331 | `event.registerBaseWorm(TEMonsterEntities.TOMB_CRAWLER);` | `event.registeSurefaceWorm(MonsterEntities.WORLD_FEEDER);` |
| 1332 | `event.registerBaseWorm(TEMonsterEntities.GIANT_WORM);` | `event.registerBaseWorm(MonsterEntities.TOMB_CRAWLER);` |
| 1333 | `event.registerBaseWorm(TEMonsterEntities.LEECH);` | `event.registerBaseWorm(MonsterEntities.GIANT_WORM);` |
| 1334 | `event.registerBoneSerpent(TEMonsterEntities.BONE_SERPENT);` | `event.registerBaseWorm(MonsterEntities.DIGGER);` |
| 1335 | `event.registerBoneSerpent(TEMonsterEntities.WITHER_BONE_SERPENT);` | `event.registerBaseWorm(MonsterEntities.LEECH);` |

### `client/gui/container/NPCReforgeScreen.java`

- 1.20 对应：`org/confluence/mod/client/gui/container/npc_screen/NPCReforgeScreen.java`
- TE import：`KeyframeAnimation`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 30 | `KeyframeAnimation interpolator;` | `<无>` |
| 69 | `this.interpolator = KeyframeAnimation.builder()` | `<无>` |

### `client/gui/hud/HouseSelectHud.java`

- 1.20 对应：`org/confluence/mod/client/gui/hud/HouseSelectHud.java`
- TE import：`DebugBlocksHelper`, `IHouseDetector`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 150 | `IHouseDetector detect = IHouseDetector.detect(pos, player.level());` | `HouseValidater.Result detect = HouseValidater.scan(player.level(), pos);` |
| 153 | `DebugBlocksHelper.Singleton().addDebugBlock(blockPos, new DebugBlocksHelper.DebugInfo(255, 255, 30, 100));` | `//                DebugBlocksHelper.Singleton().addDebugBlock(blockPos, new DebugBlocksHelper.DebugInfo(255, 255, 30, 100));` |
| 155 | `DebugBlocksHelper.Singleton().addDebugBlock(pos, new DebugBlocksHelper.DebugInfo(255, 0, 120, 120));` | `//            DebugBlocksHelper.Singleton().addDebugBlock(pos, new DebugBlocksHelper.DebugInfo(255, 0, 120, 120));` |

### `client/renderer/entity/bestiary/GeoWormBestiaryEntryRenderer.java`

- 1.20 对应：`org/confluence/mod/client/renderer/entity/bestiary/GeoWormBestiaryEntryRenderer.java`
- TE import：`GeoWormRenderer`, `BaseWorm`, `BaseWormPart`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 11 | `public class GeoWormBestiaryEntryRenderer<T extends BaseWorm<S>, S extends BaseWormPart> extends GeoWormRenderer<T, S> {` | `public class GeoWormBestiaryEntryRenderer extends GeoNormalRenderer<BaseWormMonster> {` |
| 11 | `public class GeoWormBestiaryEntryRenderer<T extends BaseWorm<S>, S extends BaseWormPart> extends GeoWormRenderer<T, S> {` | `public class GeoWormBestiaryEntryRenderer extends GeoNormalRenderer<BaseWormMonster> {` |
| 11 | `public class GeoWormBestiaryEntryRenderer<T extends BaseWorm<S>, S extends BaseWormPart> extends GeoWormRenderer<T, S> {` | `public class GeoWormBestiaryEntryRenderer extends GeoNormalRenderer<BaseWormMonster> {` |

### `client/renderer/entity/bestiary/SlimeZombieRenderer.java`

- 1.20 对应：`org/confluence/mod/client/renderer/entity/bestiary/SlimeZombieRenderer.java`
- TE import：`TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 22 | `this.slime = TEMonsterEntities.BLUE_SLIME.get().create(zombie.level());` | `this.slime = MonsterEntities.BLUE_SLIME.get().create(zombie.level());` |

### `client/renderer/entity/projectile/CrystalVileShardProjectileRenderer.java`

- 1.20 对应：`org/confluence/mod/client/renderer/entity/projectile/CrystalVileShardProjectileRenderer.java`
- TE import：`GeoNegativeVolumeRenderer`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 17 | `public class CrystalVileShardProjectileRenderer extends GeoNegativeVolumeRenderer<CrystalVileShardProjectile> {` | `public class CrystalVileShardProjectileRenderer extends GeoNegativeVolumeRenderer<CrystalVileShardProjectile> {` |

### `client/renderer/entity/projectile/GeoArrowRenderer.java`

- 1.20 对应：`org/confluence/mod/client/renderer/entity/projectile/GeoArrowRenderer.java`
- TE import：`GeoNormalRenderer`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 12 | `public class GeoArrowRenderer extends GeoNormalRenderer<HellBatArrowEntity> {` | `public class GeoArrowRenderer extends GeoNormalRenderer<HellBatArrowEntity> {` |

### `client/renderer/entity/projectile/MagicDaggerRenderer.java`

- 1.20 对应：`org/confluence/mod/client/renderer/entity/projectile/MagicDaggerRenderer.java`
- TE import：`GeoNegativeVolumeRenderer`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 12 | `public class MagicDaggerRenderer extends GeoNegativeVolumeRenderer<MagicDaggerProjectile> {` | `public class MagicDaggerRenderer extends GeoNegativeVolumeRenderer<MagicDaggerProjectile> {` |

### `client/renderer/entity/projectile/sword/ForwardProjRenderer.java`

- 1.20 对应：`<无同名文件>`
- TE import：`BaseEntityRenderer`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 13 | `public class ForwardProjRenderer<T extends Entity, S extends Entity, M extends EntityModel<S>> extends BaseEntityRenderer<T, S, M> {` | `<无>` |

### `common/attachment/ExtraInventory.java`

- 1.20 对应：`org/confluence/mod/common/attachment/ExtraInventory.java`
- TE import：`CuriosHelper`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 272 | `handler.getStacksHandler(CuriosHelper.PET_KEY).ifPresent(t -> function.accept(PET_INDEX, t));` | `<无>` |
| 273 | `handler.getStacksHandler(CuriosHelper.LIGHT_PET_KEY).ifPresent(t -> function.accept(LIGHT_PET_INDEX, t));` | `<无>` |
| 274 | `handler.getStacksHandler(CuriosHelper.MOUNT_KEY).ifPresent(t -> function.accept(MOUNT_INDEX, t));` | `<无>` |

### `common/attachment/PlayerSpecialData.java`

- 1.20 对应：`org/confluence/mod/common/attachment/PlayerSpecialData.java`
- TE import：`ITradeHolder`, `ITradeLock`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 52 | `private ITradeLock currentQuestedFishCondition;` | `<无>` |
| 73 | `this.currentQuestedFishCondition = ITradeLock.alwaysTrue();` | `<无>` |
| 106 | `public void setCurrentQuestedFish(ItemStack cost, ITradeLock lock) {` | `<无>` |
| 112 | `setCurrentQuestedFish(ItemStack.EMPTY, ITradeLock.alwaysTrue());` | `<无>` |
| 116 | `if (currentQuestedFishCondition.canTrade(player, ITradeHolder.dummy(player), 0)) {` | `<无>` |
| 293 | `ITradeLock.TYPED_CODEC.encodeStart(ops, currentQuestedFishCondition).ifSuccess(nbt -> tag.put("CurrentQuestedFishCondition", nbt));` | `PortDataResultExtension.ifSuccess(ArmorSetBonusKey.CODEC.encodeStart(ops, armorSetBonusKey), nbt -> tag.put("ArmorBonusKey", nbt));` |
| 312 | `this.currentQuestedFishCondition = ITradeLock.TYPED_CODEC.parse(ops, nbt.get("CurrentQuestedFishCondition")).result().orElse(ITradeLock.alwaysTrue());` | `<无>` |

### `common/block/common/BasePotBlock.java`

- 1.20 对应：`org/confluence/mod/common/block/common/BasePotBlock.java`
- TE import：`TEBossEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 417 | `TEBossEntities.EATER_OF_WORLDS.get(),` | `BossEntities.EATER_OF_WORLDS.get(),` |
| 418 | `TEBossEntities.BRAIN_OF_CTHULHU.get(),` | `BossEntities.BRAIN_OF_CTHULHU.get(),` |
| 419 | `TEBossEntities.QUEEN_BEE.get(),` | `BossEntities.QUEEN_BEE.get(),` |
| 420 | `TEBossEntities.SKELETRON.get(),` | `BossEntities.SKELETRON.get(),` |
| 421 | `TEBossEntities.THE_TWINS.get(),` | `BossEntities.THE_TWINS.get(),` |
| 422 | `TEBossEntities.PLANTERA.get()` | `BossEntities.THE_DESTROYER.get(),` |

### `common/block/common/TombstoneBlock.java`

- 1.20 对应：`org/confluence/mod/common/block/common/TombstoneBlock.java`
- TE import：`TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 68 | `TEMonsterEntities.GHOST.get().spawn((ServerLevel) level, pos.offset(` | `MonsterEntities.GHOST.get().spawn((ServerLevel) level, pos.offset(` |

### `common/block/functional/crafting/AltarBlock.java`

- 1.20 对应：`org/confluence/mod/common/block/functional/crafting/AltarBlock.java`
- TE import：`TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 154 | `TEMonsterEntities.WRAITH.get().spawn(serverLevel, pos.offset(` | `MonsterEntities.WRAITH.get().spawn(serverLevel, pos.offset(` |

### `common/block/natural/CorrodedWormRootsBlock.java`

- 1.20 对应：`org/confluence/mod/common/block/natural/CorrodedWormRootsBlock.java`
- TE import：`TETags`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 99 | `for (Mob mob : level.getEntitiesOfClass(Mob.class, new AABB(pos).inflate(radius), e -> e.getType().is(TETags.EntityTypes.CORRUPT))) {` | `for (Mob mob : level.getEntitiesOfClass(Mob.class, new AABB(pos).inflate(radius), e -> e.getType().is(ModTags.EntityTypes.CORRUPT))) {` |

### `common/block/natural/CorruptedOvariesBlock.java`

- 1.20 对应：`org/confluence/mod/common/block/natural/CorruptedOvariesBlock.java`
- TE import：`TEEffects`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 38 | `living.addEffect(new MobEffectInstance(TEEffects.DEMONIC_THOUGHTS, 200));` | `living.addEffect(new MobEffectInstance(ModEffects.DEMONIC_THOUGHTS.get(), 200));` |

### `common/block/natural/CrimsonHeartBlock.java`

- 1.20 对应：`org/confluence/mod/common/block/natural/CrimsonHeartBlock.java`
- TE import：`BrainOfCthulhu`, `TEBossEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 83 | `ModUtils.summonBoss(serverLevel, pos, new BrainOfCthulhu(TEBossEntities.BRAIN_OF_CTHULHU.get(), level), false);` | `ModUtils.summonBoss(serverLevel, pos, new BrainOfCthulhu(BossEntities.BRAIN_OF_CTHULHU.get(), level), false);` |
| 83 | `ModUtils.summonBoss(serverLevel, pos, new BrainOfCthulhu(TEBossEntities.BRAIN_OF_CTHULHU.get(), level), false);` | `ModUtils.summonBoss(serverLevel, pos, new BrainOfCthulhu(BossEntities.BRAIN_OF_CTHULHU.get(), level), false);` |

### `common/block/natural/DecomposeTheSourceExtractBlock.java`

- 1.20 对应：`org/confluence/mod/common/block/natural/DecomposeTheSourceExtractBlock.java`
- TE import：`AbstractMonster`, `TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 108 | `AbstractMonster entity = TEMonsterEntities.EATER_OF_SOULS.get().create(level);` | `EaterOfSouls entity = MonsterEntities.EATER_OF_SOULS.get().create(level);` |
| 108 | `AbstractMonster entity = TEMonsterEntities.EATER_OF_SOULS.get().create(level);` | `EaterOfSouls entity = MonsterEntities.EATER_OF_SOULS.get().create(level);` |

### `common/block/natural/JungleHiveBlock.java`

- 1.20 对应：`org/confluence/mod/common/block/natural/JungleHiveBlock.java`
- TE import：`LittleHornet`, `TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 40 | `LittleHornet BeeEntity = TEMonsterEntities.LITTLE_HORNET.get().create(level);` | `Hornet BeeEntity = MonsterEntities.LITTLE_HORNET.get().create(level);` |
| 40 | `LittleHornet BeeEntity = TEMonsterEntities.LITTLE_HORNET.get().create(level);` | `Hornet BeeEntity = MonsterEntities.LITTLE_HORNET.get().create(level);` |

### `common/block/natural/LarvaBlock.java`

- 1.20 对应：`org/confluence/mod/common/block/natural/LarvaBlock.java`
- TE import：`QueenBee`, `TEBossEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 60 | `ModUtils.summonBoss(serverLevel, pos, new QueenBee(TEBossEntities.QUEEN_BEE.get(), level), false);` | `ModUtils.summonBoss(serverLevel, pos, new QueenBee(BossEntities.QUEEN_BEE.get(), level), false);` |
| 60 | `ModUtils.summonBoss(serverLevel, pos, new QueenBee(TEBossEntities.QUEEN_BEE.get(), level), false);` | `ModUtils.summonBoss(serverLevel, pos, new QueenBee(BossEntities.QUEEN_BEE.get(), level), false);` |

### `common/block/natural/ShadowOrbBlock.java`

- 1.20 对应：`org/confluence/mod/common/block/natural/ShadowOrbBlock.java`
- TE import：`EaterOfWorlds`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 82 | `ModUtils.summonBoss(serverLevel, pos, new EaterOfWorlds(level, true), false);` | `ModUtils.summonBoss(serverLevel, pos, new EaterOfWorlds(BossEntities.EATER_OF_WORLDS.get(), level), false);` |

### `common/data/gen/ModChineseProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/ModChineseProvider.java`
- TE import：`TEEffects`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 4861 | `addEffect(TEEffects.DEMONIC_THOUGHTS.get(), "邪念", "再次被赋予邪念时会生成噬魂怪");` | `private void addMaidIntegrationTranslations() {` |
| 4862 | `addEffect(TEEffects.SUMMON_FOCUS.get(), "狩猎", "召唤物额外造成伤害");` | `private void addMaidIntegrationTranslations() {` |
| 4863 | `addEffect(TEEffects.HELLFIRE.get(), "狱炎", "持续损失生命值");` | `private void addMaidIntegrationTranslations() {` |
| 4864 | `addEffect(TEEffects.FROST_BURN.get(), "霜冻", "缓慢损失生命值，无法再生生命");` | `private void addMaidIntegrationTranslations() {` |
| 4865 | `addEffect(TEEffects.CRIMSON_STORM.get(), "猩红风暴", "你已陷入风暴，无可逃脱。");` | `private void addMaidIntegrationTranslations() {` |
| 4866 | `addEffect(TEEffects.HORRIFIED.get(), "惊恐", "你已看到污秽之物，无可逃脱。");` | `private void addMaidIntegrationTranslations() {` |
| 4867 | `addEffect(TEEffects.THE_TONGUE.get(), "狂卷之舌", "你被吸入嘴中");` | `private void addMaidIntegrationTranslations() {` |
| 4868 | `addEffect(TEEffects.SCARED.get(), "惊慌", "如惊弓之鸟，四处逃串");` | `private void addMaidIntegrationTranslations() {` |

### `common/data/gen/ModClientBestiaryEntryProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/ModClientBestiaryEntryProvider.java`
- TE import：`DemonEye`, `DemonEyeVariant`, `AnglerNPC`, `TEAnimals`, `TEBossEntities`, `TEMonsterEntities`, `TENpcEntities`, `TEArmors`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 50 | `.add(TENpcEntities.GUIDE, builder -> builder.order(100).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))` | `.add(NpcEntities.GUIDE, builder -> builder.order(100).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))` |
| 51 | `.add(TENpcEntities.MERCHANT, builder -> builder.order(200).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))` | `.add(NpcEntities.MERCHANT, builder -> builder.order(200).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))` |
| 52 | `.add(TENpcEntities.NURSE, builder -> builder.order(300).rarity(1).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))` | `.add(NpcEntities.NURSE, builder -> builder.order(300).rarity(1).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))` |
| 53 | `.add(TENpcEntities.DEMOLITIONIST, builder -> builder.order(400).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))` | `.add(NpcEntities.DEMOLITIONIST, builder -> builder.order(400).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))` |
| 54 | `.add(TENpcEntities.ANGLER, builder -> builder.order(500).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN).entityNbt(nbt -> nbt.putBoolean(AnglerNPC.WAKE_UP_KEY, true)))` | `.add(NpcEntities.ANGLER, builder -> builder.order(500).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN).entityNbt(tag -> tag.putBoolean(AnglerNPC.WAKE_UP_KEY, true)))` |
| 54 | `.add(TENpcEntities.ANGLER, builder -> builder.order(500).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN).entityNbt(nbt -> nbt.putBoolean(AnglerNPC.WAKE_UP_KEY, true)))` | `.add(NpcEntities.ANGLER, builder -> builder.order(500).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN).entityNbt(tag -> tag.putBoolean(AnglerNPC.WAKE_UP_KEY, true)))` |
| 55 | `.add(TENpcEntities.DRYAD, builder -> builder.order(600).rarity(3).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))` | `.add(NpcEntities.DRYAD, builder -> builder.order(600).rarity(3).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))` |
| 56 | `.add(TENpcEntities.ARMS_DEALER, builder -> builder.order(700).rarity(1).background(DESERT).filters(FilterEntry.DESERT))` | `.add(NpcEntities.ARMS_DEALER, builder -> builder.order(700).rarity(1).background(DESERT).filters(FilterEntry.DESERT))` |
| 57 | `.add(TENpcEntities.DYE_TRADER, builder -> builder.order(800).rarity(2).background(DESERT).filters(FilterEntry.DESERT))` | `.add(NpcEntities.DYE_TRADER, builder -> builder.order(800).rarity(2).background(DESERT).filters(FilterEntry.DESERT))` |
| 58 | `.add(TENpcEntities.PAINTER, builder -> builder.order(900).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))` | `.add(NpcEntities.PAINTER, builder -> builder.order(900).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))` |
| 60 | `.add(TENpcEntities.ZOOLOGIST, builder -> builder.order(1100).rarity(5).background(SURFACE).filters(FilterEntry.SURFACE))` | `.add(NpcEntities.ZOOLOGIST, builder -> builder.order(1100).rarity(5).background(SURFACE).filters(FilterEntry.SURFACE))` |
| 63 | `.add(TENpcEntities.GOBLIN_TINKERER, builder -> builder.order(1400).rarity(3).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))` | `.add(NpcEntities.GOBLIN_TINKERER, builder -> builder.order(1400).rarity(3).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))` |
| 64 | `.add(TENpcEntities.WITCH_DOCTOR, builder -> builder.order(1500).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))` | `.add(NpcEntities.WITCH_DOCTOR, builder -> builder.order(1500).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))` |
| 65 | `.add(TENpcEntities.MECHANIC, builder -> builder.order(1600).rarity(2).background(SNOW).filters(FilterEntry.SNOW))` | `.add(NpcEntities.MECHANIC, builder -> builder.order(1600).rarity(2).background(SNOW).filters(FilterEntry.SNOW))` |
| 66 | `.add(TENpcEntities.CLOTHIER, builder -> builder.order(1700).rarity(2).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))` | `.add(NpcEntities.CLOTHIER, builder -> builder.order(1700).rarity(2).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))` |
| 67 | `.add(TENpcEntities.WIZARD, builder -> builder.order(1800).rarity(3).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))` | `.add(NpcEntities.WIZARD, builder -> builder.order(1800).rarity(3).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))` |
| 70 | `.add(TENpcEntities.TRUFFLE, builder -> builder.order(2100).rarity(5).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))` | `.add(NpcEntities.TRUFFLE, builder -> builder.order(2100).rarity(5).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))` |
| 73 | `.add(TENpcEntities.PARTY_GIRL, builder -> builder.order(2400).rarity(4).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))` | `.add(NpcEntities.PARTY_GIRL, builder -> builder.order(2400).rarity(4).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))` |
| 77 | `.add(TENpcEntities.TRAVELING_MERCHANT, builder -> builder.order(3800).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))` | `// 城镇狗狗.add(NpcEntities.TOWN_DOG, builder -> builder.order(2800).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))` |
| 79 | `.add(TENpcEntities.OLD_MAN, builder -> builder.order(4000).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` | `.add(NpcEntities.NERDY_SLIME, builder -> builder.order(3000).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))` |
| 81 | `.add(TEAnimals.BUNNY, builder -> builder.order(4200).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.add(NpcEntities.ELDER_SLIME, builder -> builder.order(3200).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))` |
| 84 | `.add(TEAnimals.EXPLOSIVE_BUNNY, builder -> builder.order(4250).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.variant(CritterEntities.BUNNY, Bunny.Variant.PARTY, builder -> builder.order(4300).rarity(2).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.PARTY, FilterEntry.DAYTIME))` |
| 87 | `.addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.GOLDEN_ID, builder -> builder.order(4700).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))` | `.variant(CritterEntities.BUNNY, Bunny.Variant.XMAS, builder -> builder.order(4600).rarity(4).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.CHRISTMAS, FilterEntry.DAYTIME))` |
| 88 | `.add(TEAnimals.BIRD, builder -> builder.order(4800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.JEWEL_BUNNY, 4, Bunny.Variant.GOLD, builder -> builder.order(4700).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 89 | `.add(TEAnimals.BLUE_JAY, builder -> builder.order(4900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.add(CritterEntities.BIRD, builder -> builder.order(4800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 90 | `.add(TEAnimals.CARDINAL, builder -> builder.order(5000).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))` | `.add(CritterEntities.BLUE_JAY, builder -> builder.order(4900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 99 | `.addIntVariant(TEAnimals.SQUIRREL, Squirrel.VARIANT_KEY, Squirrel.COMMON_ID, builder -> builder.order(5900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `// 金金鱼.add(CritterEntities.GOLD_GOLDFISH, builder -> builder.order(5800).rarity(5).background(SURFACE).filters(FilterEntry.SURFACE))` |
| 100 | `.addIntVariant(TEAnimals.SQUIRREL, Squirrel.VARIANT_KEY, Squirrel.RED_ID, builder -> builder.order(6000).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.SQUIRREL, 0, Squirrel.Variant.NORMAL, builder -> builder.order(5900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 101 | `.addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.GOLDEN_ID, builder -> builder.order(6100).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.RED_SQUIRREL, "entity.confluence.squirrel", 1, Squirrel.Variant.RED, builder -> builder.order(6000).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 106 | `.addIntVariant(TEAnimals.GRASSHOPPER, JumpableVariantAnimal.VARIANT_KEY, VariantsTextureMaps.COMMON_GRASSHOPPER_ID, builder -> builder.order(6600).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))` | `.numberedVariant(CritterEntities.GRASSHOPPER, 1, Grasshopper.Variant.GREEN, builder -> builder.order(6600).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))` |
| 107 | `.addIntVariant(TEAnimals.GRASSHOPPER, JumpableVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_GRASSHOPPER_ID, builder -> builder.order(6700).rarity(5).background(SURFACE).filters(FilterEntry.SURFACE))` | `.numberedVariant(CritterEntities.GRASSHOPPER, 0, Grasshopper.Variant.GOLD, builder -> builder.order(6700).rarity(5).background(SURFACE).filters(FilterEntry.SURFACE))` |
| 108 | `.addIntVariant(TEAnimals.BUTTERFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.JULIA_BUTTERFLY_ID, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.BUTTERFLY, 1, Butterfly.Variant.JULIA, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 109 | `.addIntVariant(TEAnimals.BUTTERFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.MONARCH_BUTTERFLY_ID, builder -> builder.order(6801).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.BUTTERFLY, 2, Butterfly.Variant.MONARCH, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 110 | `.addIntVariant(TEAnimals.BUTTERFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.PURPLE_EMPEROR_BUTTERFLY_ID, builder -> builder.order(6802).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.BUTTERFLY, 3, Butterfly.Variant.PURPLE_EMPEROR, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 111 | `.addIntVariant(TEAnimals.BUTTERFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.RED_ADMIRAL_BUTTERFLY_ID, builder -> builder.order(6803).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.BUTTERFLY, 4, Butterfly.Variant.RED_ADMIRAL, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 112 | `.addIntVariant(TEAnimals.BUTTERFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.SULPHUR_BUTTERFLY_ID, builder -> builder.order(6804).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.BUTTERFLY, 5, Butterfly.Variant.SULPHUR, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 113 | `.addIntVariant(TEAnimals.BUTTERFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.TREE_NYMPH_BUTTERFLY_ID, builder -> builder.order(6805).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.BUTTERFLY, 6, Butterfly.Variant.TREE_NYMPH, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 114 | `.addIntVariant(TEAnimals.BUTTERFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.ULYSSES_BUTTERFLY_ID, builder -> builder.order(6806).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.BUTTERFLY, 7, Butterfly.Variant.ULYSSES, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 115 | `.addIntVariant(TEAnimals.BUTTERFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.ZEBRA_SWALLOWTAIL_BUTTERFLY_ID, builder -> builder.order(6807).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.BUTTERFLY, 8, Butterfly.Variant.ZEBRA_SWALLOWTAIL, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 116 | `.addIntVariant(TEAnimals.BUTTERFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_BUTTERFLY_ID, builder -> builder.order(6900).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.BUTTERFLY, 0, Butterfly.Variant.GOLD, builder -> builder.order(6900).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 117 | `.addIntVariant(TEAnimals.WORM, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.COMMON_WORM_ID, builder -> builder.order(7000).rarity(1).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))` | `.numberedVariant(CritterEntities.WORM, 2, Worm.Variant.NORMAL, builder -> builder.order(7000).rarity(1).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))` |
| 118 | `.addIntVariant(TEAnimals.WORM, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_WORM_ID, builder -> builder.order(7100).rarity(5).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))` | `.numberedVariant(CritterEntities.WORM, 1, Worm.Variant.GOLD, builder -> builder.order(7100).rarity(5).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))` |
| 119 | `.addIntVariant(TEAnimals.DRAGONFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.BLACK_DRAGONFLY_ID, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.DRAGONFLY, 0, Dragonfly.Variant.BLACK, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 120 | `.addIntVariant(TEAnimals.DRAGONFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.BLUE_DRAGONFLY_ID, builder -> builder.order(7201).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.DRAGONFLY, 1, Dragonfly.Variant.BLUE, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 121 | `.addIntVariant(TEAnimals.DRAGONFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GREEN_DRAGONFLY_ID, builder -> builder.order(7202).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.DRAGONFLY, 3, Dragonfly.Variant.GREEN, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 122 | `.addIntVariant(TEAnimals.DRAGONFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.ORANGE_DRAGONFLY_ID, builder -> builder.order(7203).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.DRAGONFLY, 4, Dragonfly.Variant.ORANGE, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 123 | `.addIntVariant(TEAnimals.DRAGONFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.RED_DRAGONFLY_ID, builder -> builder.order(7204).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.DRAGONFLY, 5, Dragonfly.Variant.RED, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 124 | `.addIntVariant(TEAnimals.DRAGONFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.YELLOW_DRAGONFLY_ID, builder -> builder.order(7205).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.DRAGONFLY, 6, Dragonfly.Variant.YELLOW, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 125 | `.addIntVariant(TEAnimals.DRAGONFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_DRAGONFLY_ID, builder -> builder.order(7300).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.DRAGONFLY, 2, Dragonfly.Variant.GOLD, builder -> builder.order(7300).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 130 | `.addIntVariant(TEAnimals.LADYBUG, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.COMMON_LADYBUG_ID, builder -> builder.order(7800).rarity(3).background(SURFACE).filters(FilterEntry.WINDY_DAY))` | `.numberedVariant(CritterEntities.LADYBUG, 1, Ladybug.Variant.RED, builder -> builder.order(7800).rarity(3).background(SURFACE).filters(FilterEntry.WINDY_DAY))` |
| 131 | `.addIntVariant(TEAnimals.LADYBUG, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_LADYBUG_ID, builder -> builder.order(7900).rarity(5).background(SURFACE).filters(FilterEntry.WINDY_DAY))` | `.numberedVariant(CritterEntities.LADYBUG, 0, Ladybug.Variant.GOLD, builder -> builder.order(7900).rarity(5).background(SURFACE).filters(FilterEntry.WINDY_DAY))` |
| 133 | `.addIntVariant(TEAnimals.FEALING, Fairy.VARIANT_KEY, VariantsTextureMaps.COMMON_FEALING_ID, builder -> builder.order(8100).rarity(5).background(CAVE).filters(FilterEntry.CAVE))` | `.add(CritterEntities.FEALING, builder -> builder.order(8100).rarity(5).background(CAVE).filters(FilterEntry.CAVE))` |
| 134 | `.addIntVariant(TEAnimals.DUCK, Duck.VARIANT_KEY, Duck.MALLARD_ID, builder -> builder.order(8200).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.DUCK, 0, Duck.Variant.MALLARD, builder -> builder.order(8200).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 135 | `.addIntVariant(TEAnimals.DUCK, Duck.VARIANT_KEY, Duck.COMMON_ID, builder -> builder.order(8300).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))` | `.numberedVariant(CritterEntities.DUCK, 1, Duck.Variant.COMMON, builder -> builder.order(8300).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 139 | `.addIntVariant(TEAnimals.WORM, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.ENCHANTED_NIGHTCRAWLER_ID, builder -> builder.order(8700).rarity(5).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))` | `.numberedVariant(CritterEntities.WORM, 0, Worm.Variant.NIGHTCRAWLER, builder -> builder.order(8700).rarity(5).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 140 | `.addIntVariant(TEAnimals.FAIRY, Fairy.VARIANT_KEY, VariantsTextureMaps.PINK_FAIRY_ID, builder -> builder.order(8800).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))` | `.numberedVariant(CritterEntities.FAIRY, 0, Fairy.Variant.PINK, builder -> builder.order(8800).rarity(4).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 141 | `.addIntVariant(TEAnimals.FAIRY, Fairy.VARIANT_KEY, VariantsTextureMaps.GREEN_FAIRY_ID, builder -> builder.order(8900).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))` | `.numberedVariant(CritterEntities.FAIRY, 1, Fairy.Variant.GREEN, builder -> builder.order(8900).rarity(4).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 142 | `.addIntVariant(TEAnimals.FAIRY, Fairy.VARIANT_KEY, VariantsTextureMaps.BLUE_FAIRY_ID, builder -> builder.order(9000).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))` | `.numberedVariant(CritterEntities.FAIRY, 2, Fairy.Variant.BLUE, builder -> builder.order(9000).rarity(4).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 144 | `.add(TEAnimals.MAGGOT, builder -> builder.order(9200).rarity(1).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))` | `.add(CritterEntities.MAGGOT, builder -> builder.order(9200).rarity(1).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))` |
| 145 | `.addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.AMETHYST_ID, builder -> builder.order(9300).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 2, Squirrel.Variant.AMETHYST, builder -> builder.order(9300).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 146 | `.addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.TOPAZ_ID, builder -> builder.order(9400).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 7, Squirrel.Variant.TOPAZ, builder -> builder.order(9400).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 147 | `.addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.SAPPHIRE_ID, builder -> builder.order(9500).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 6, Squirrel.Variant.SAPPHIRE, builder -> builder.order(9500).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 148 | `.addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.EMERALD_ID, builder -> builder.order(9600).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 4, Squirrel.Variant.EMERALD, builder -> builder.order(9600).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 149 | `.addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.RUBY_ID, builder -> builder.order(9700).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 5, Squirrel.Variant.RUBY, builder -> builder.order(9700).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 150 | `.addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.DIAMOND_ID, builder -> builder.order(9800).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 3, Squirrel.Variant.DIAMOND, builder -> builder.order(9800).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 151 | `.addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.AMBER_ID, builder -> builder.order(9900).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 0, Squirrel.Variant.AMBER, builder -> builder.order(9900).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 152 | `.addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.AMETHYST_ID, builder -> builder.order(10000).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.numberedVariant(CritterEntities.JEWEL_BUNNY, 1, Bunny.Variant.AMETHYST, builder -> builder.order(10000).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 153 | `.addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.TOPAZ_ID, builder -> builder.order(10100).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.numberedVariant(CritterEntities.JEWEL_BUNNY, 7, Bunny.Variant.TOPAZ, builder -> builder.order(10100).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 154 | `.addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.SAPPHIRE_ID, builder -> builder.order(10200).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.numberedVariant(CritterEntities.JEWEL_BUNNY, 6, Bunny.Variant.SAPPHIRE, builder -> builder.order(10200).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 155 | `.addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.EMERALD_ID, builder -> builder.order(10300).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.numberedVariant(CritterEntities.JEWEL_BUNNY, 3, Bunny.Variant.EMERALD, builder -> builder.order(10300).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 156 | `.addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.RUBY_ID, builder -> builder.order(10400).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.numberedVariant(CritterEntities.JEWEL_BUNNY, 5, Bunny.Variant.RUBY, builder -> builder.order(10400).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 157 | `.addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.DIAMOND_ID, builder -> builder.order(10500).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.numberedVariant(CritterEntities.JEWEL_BUNNY, 2, Bunny.Variant.DIAMOND, builder -> builder.order(10500).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 158 | `.addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.AMBER_ID, builder -> builder.order(10600).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.numberedVariant(CritterEntities.JEWEL_BUNNY, 0, Bunny.Variant.AMBER, builder -> builder.order(10600).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 159 | `.add(TEAnimals.SNAIL, builder -> builder.order(10700).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` | `.add(CritterEntities.SNAIL, builder -> builder.order(10700).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 163 | `.addIntVariant(TEAnimals.SCORPION, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.SCORPION_ID, builder -> builder.order(11100).rarity(1).background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME))` | `.numberedVariant(CritterEntities.SCORPION, 1, Scorpion.Variant.NORMAL, builder -> builder.order(11100).rarity(1).background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME))` |
| 164 | `.addIntVariant(TEAnimals.SCORPION, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.BLACK_SCORPION_ID, builder -> builder.order(11200).rarity(2).background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME))` | `.numberedVariant(CritterEntities.SCORPION, 0, Scorpion.Variant.BLACK, builder -> builder.order(11200).rarity(2).background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME))` |
| 171 | `.add(TEAnimals.GRUBBY, builder -> builder.order(11900).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))` | `// 丛林龟.add(CritterEntities.JUNGLE_TURTLE, builder -> builder.order(11900).rarity(1).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME))` |
| 172 | `.add(TEAnimals.SLUGGY, builder -> builder.order(12000).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))` | `.add(CritterEntities.GRUBBY, builder -> builder.order(12000).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))` |
| 175 | `.add(TEAnimals.HELL_BUTTERFLY, builder -> builder.order(12300).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` | `// 熔岩萤火虫.add(CritterEntities.LAVAFLY, builder -> builder.order(12300).rarity(3).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` |
| 176 | `.add(TEAnimals.MAGMA_SNAIL, builder -> builder.order(12400).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` | `.add(CritterEntities.HELL_BUTTERFLY, builder -> builder.order(12400).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` |
| 178 | `.add(TEAnimals.PRISMATIC_LACEWING, builder -> builder.order(12600).rarity(3).background(THE_HALLOW_MOON).filters(FilterEntry.THE_HALLOW, FilterEntry.NIGHTTIME)) //神圣夜晚` | `.add(CritterEntities.LIGHTNING_BUG, builder -> builder.order(12600).rarity(3).background(THE_HALLOW_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.THE_HALLOW))` |
| 179 | `.add(TEAnimals.GLOWING_SNAIL, builder -> builder.order(12700).rarity(3).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))` | `.add(CritterEntities.PRISMATIC_LACEWING, builder -> builder.order(12700).rarity(3).background(THE_HALLOW_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.THE_HALLOW)) //神圣夜晚` |
| 181 | `.add(TEMonsterEntities.GOBLIN_SCOUT, builder -> builder.order(12900).rarity(3).background(SURFACE).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE))` | `.add(MonsterEntities.GNOME, builder -> builder.order(12900).rarity(3).background(SURFACE).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE))` |
| 182 | `.add(TEMonsterEntities.GREEN_SLIME, builder -> builder.order(13000).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.add(MonsterEntities.GOBLIN_SCOUT, builder -> builder.order(13000).rarity(3).background(SURFACE).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE))` |
| 183 | `.add(TEMonsterEntities.BLUE_SLIME, builder -> builder.order(13100).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.add(MonsterEntities.GREEN_SLIME, builder -> builder.order(13100).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 184 | `.add(TEMonsterEntities.PURPLE_SLIME, builder -> builder.order(13200).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` | `.add(MonsterEntities.BLUE_SLIME, builder -> builder.order(13200).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 185 | `.add(TEMonsterEntities.PINK_SLIME, builder -> builder.order(13300).rarity(4).background(SURFACE_SUN).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE, FilterEntry.DAYTIME))` | `.add(MonsterEntities.PURPLE_SLIME, builder -> builder.order(13300).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 186 | `.add(TEMonsterEntities.GOLDEN_SLIME, builder -> builder.order(13301).rarity(5).background(SURFACE_SUN).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE, FilterEntry.DAYTIME))` | `.add(MonsterEntities.PINK_SLIME, builder -> builder.order(13400).rarity(4).background(SURFACE_SUN).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE, FilterEntry.DAYTIME))` |
| 187 | `.add(TEMonsterEntities.HONEY_SLIME, builder -> builder.order(13302).rarity(3).background(THE_JUNGLE_SUN).filters(FilterEntry.UNDERGROUND_JUNGLE))` | `.add(MonsterEntities.GOLDEN_SLIME, builder -> builder.order(13401).rarity(5).background(SURFACE_SUN).filters(FilterEntry.RARE_CREATURE, surfaceDaytime[0], surfaceDaytime[1]))` |
| 188 | `.add(TEMonsterEntities.SWAMP_SLIME, builder -> builder.order(13303).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))` | `.add(MonsterEntities.SWEET_SLIME, builder -> builder.order(13402).rarity(3).background(THE_JUNGLE_SUN).filters(FilterEntry.UNDERGROUND_JUNGLE))` |
| 189 | `.add(TEMonsterEntities.GREEN_DUMPLING_SLIME, builder -> builder.order(13304).rarity(4).background(SURFACE_SUN).filters(surfaceDaytime))` | `.add(MonsterEntities.SWAMP_SLIME, builder -> builder.order(13403).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 190 | `.add(TEMonsterEntities.SPIKED_SLIME, builder -> builder.order(13305).rarity(4).background(SURFACE_SUN).filters(surfaceDaytime))` | `.add(MonsterEntities.GREEN_DUMPLING_SLIME, builder -> builder.order(13404).rarity(4).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 191 | `.add(TEMonsterEntities.TROPIC_SLIME, builder -> builder.order(13306).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN,FilterEntry.DAYTIME))` | `.add(MonsterEntities.SPIKED_SLIME, builder -> builder.order(13405).rarity(4).background(SURFACE_SUN).filters(surfaceDaytime))` |
| 195 | `.add(TEMonsterEntities.FLYING_FISH, builder -> builder.order(13700).rarity(2).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))` | `// 雨伞史莱姆.add(MonsterEntities.UMBRELLA_SLIME, builder -> builder.order(13700).rarity(2).background(SURFACE_RAIN).filters(FilterEntry.RAIN, FilterEntry.SURFACE))` |
| 197 | `.demonEyeVariant(DemonEyeVariant.DILATED, builder -> builder.order(13900).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.add(MonsterEntities.ANGRY_NIMBUS, builder -> builder.order(13900).rarity(3).background(SURFACE_RAIN).filters(FilterEntry.RAIN, FilterEntry.SURFACE))` |
| 198 | `.demonEyeVariant(DemonEyeVariant.DILATED_SMALL, builder -> builder.order(13910).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.DILATED, builder -> builder.order(14000).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 199 | `.demonEyeVariant(DemonEyeVariant.SLEEPY, builder -> builder.order(14000).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.DILATED_SMALL, builder -> builder.order(14000).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 200 | `.demonEyeVariant(DemonEyeVariant.SLEEPY_BIG, builder -> builder.order(14010).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.SLEEPY, builder -> builder.order(14100).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 201 | `.demonEyeVariant(DemonEyeVariant.PURPLE, builder -> builder.order(14100).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.SLEEPY_BIG, builder -> builder.order(14100).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 202 | `.demonEyeVariant(DemonEyeVariant.PURPLE_BIG, builder -> builder.order(14110).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.PURPLE, builder -> builder.order(14200).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 203 | `.demonEyeVariant(DemonEyeVariant.NORMAL, builder -> builder.order(14200).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.PURPLE_BIG, builder -> builder.order(14200).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 204 | `.demonEyeVariant(DemonEyeVariant.NORMAL_BIG, builder -> builder.order(14210).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.NORMAL, builder -> builder.order(14300).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 205 | `.demonEyeVariant(DemonEyeVariant.GREEN, builder -> builder.order(14300).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.NORMAL_BIG, builder -> builder.order(14300).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 206 | `.demonEyeVariant(DemonEyeVariant.GREEN_SMALL, builder -> builder.order(14310).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.GREEN, builder -> builder.order(14400).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 207 | `.demonEyeVariant(DemonEyeVariant.CATARACT, builder -> builder.order(14400).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.GREEN_SMALL, builder -> builder.order(14400).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 208 | `.demonEyeVariant(DemonEyeVariant.CATARACT_BIG, builder -> builder.order(14410).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.CATARACT, builder -> builder.order(14500).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 209 | `.demonEyeVariant(DemonEyeVariant.SPACESHIP, builder -> builder.order(14411).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.CATARACT_BIG, builder -> builder.order(14500).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 210 | `.demonEyeVariant(DemonEyeVariant.OWL, builder -> builder.order(14412).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.SPACESHIP, builder -> builder.order(47000).rarity(3).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.HALLOWEEN))` |
| 221 | `.mobArmorItems(TEMonsterEntities.POSSESS_ARMOR, "", List.of(TEArmors.POSSESSED_ARMOR.boots.toStack(), TEArmors.POSSESSED_ARMOR.leggings.toStack(), TEArmors.POSSESSED_ARMOR.chestplate.toStack(), TEArmors.POSSESSED_ARMOR.helmet.toStack()), provider, builder -> builder.order(15500).rarity(2).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "raincoat", builder -> builder.order(15500).rarity(2).background(SURFACE_MOON).filters(FilterEntry.RAIN, FilterEntry.NIGHTTIME).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.RAINCOAT.getSerializedName())))` |
| 221 | `.mobArmorItems(TEMonsterEntities.POSSESS_ARMOR, "", List.of(TEArmors.POSSESSED_ARMOR.boots.toStack(), TEArmors.POSSESSED_ARMOR.leggings.toStack(), TEArmors.POSSESSED_ARMOR.chestplate.toStack(), TEArmors.POSSESSED_ARMOR.helmet.toStack()), provider, builder -> builder.order(15500).rarity(2).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "raincoat", builder -> builder.order(15500).rarity(2).background(SURFACE_MOON).filters(FilterEntry.RAIN, FilterEntry.NIGHTTIME).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.RAINCOAT.getSerializedName())))` |
| 223 | `.add(TEMonsterEntities.WRAITH, builder -> builder.order(15700).rarity(2).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))` | `.add(MonsterEntities.WEREWOLF, builder -> builder.order(15700).rarity(2).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 228 | `.add(TEMonsterEntities.BLOOD_ZOMBIE, builder -> builder.order(16200).rarity(1).background(BLOOD_MOON).filters(FilterEntry.SURFACE, FilterEntry.BLOOD_MOON, FilterEntry.NIGHTTIME))` | `.add(MonsterEntities.VICIOUS_PENGUIN, builder -> builder.order(16200).rarity(3).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))` |
| 237 | `.add(TEMonsterEntities.DRIPPLER, builder -> builder.order(17100).rarity(2).background(BLOOD_MOON).filters(FilterEntry.SURFACE, FilterEntry.BLOOD_MOON, FilterEntry.NIGHTTIME))` | `.add(MonsterEntities.VICIOUS_GOLDFISH, builder -> builder.order(17100).rarity(3).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))` |
| 239 | `.add(TEMonsterEntities.WANDERING_EYE_FISH, builder -> builder.order(17300).rarity(4).background(BLOOD_MOON).filters(FilterEntry.SURFACE, FilterEntry.BLOOD_MOON, FilterEntry.NIGHTTIME))` | `// 嗒嗒牙齿炸弹.add(MonsterEntities.CHATTERING_TEETH_BOMB, builder -> builder.order(17300).rarity(2).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))` |
| 245 | `.add(TEMonsterEntities.GHOST, builder -> builder.order(17900).rarity(3).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))` | `// 苔藓僵尸.add(MonsterEntities.MOSS_ZOMBIE, builder -> builder.order(17900).rarity(4).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))` |
| 246 | `.add(TEMonsterEntities.RED_SLIME, builder -> builder.order(18000).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))` | `// 乌鸦.add(MonsterEntities.RAVEN, builder -> builder.order(18000).rarity(2).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))` |
| 247 | `.add(TEMonsterEntities.YELLOW_SLIME, builder -> builder.order(18100).rarity(3).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))` | `.add(MonsterEntities.GHOST, builder -> builder.order(18100).rarity(3).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))` |
| 249 | `.add(TEMonsterEntities.GIANT_WORM, builder -> builder.order(18300).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))` | `.add(MonsterEntities.YELLOW_SLIME, builder -> builder.order(18400).rarity(3).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))` |
| 251 | `.add(TEMonsterEntities.BLACK_SLIME, "entity.terra_entity.baby_slime", "", builder -> cave(builder, 18500, 1))` | `// 毒泥.add(MonsterEntities.TOXIC_SLUDGE, builder -> builder.order(18500).rarity(2).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))` |
| 252 | `.add(TEMonsterEntities.BLACK_SLIME, builder -> cave(builder, 18600, 1))` | `.add(MonsterEntities.GIANT_WORM, builder -> builder.order(18600).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))` |
| 254 | `.add(TEMonsterEntities.BLACK_SLIME, "entity.terra_entity.mother_slime", "", builder -> cave(builder, 18800, 2))` | `.add(MonsterEntities.BABY_SLIME, builder -> builder.order(18800).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 260 | `.add(TEMonsterEntities.CRAWDAD, builder -> builder.order(19500).rarity(2).background(CAVE).filters(FilterEntry.CAVE))` | `.add(EntityType.SKELETON, builder -> builder.order(19400).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 263 | `.add(TEMonsterEntities.NYMPH, builder -> builder.order(19800).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))` | `// 骷髅 （无裤）.add(MonsterEntities.SKELETON_PANTLESS, builder -> builder.order(19700).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 268 | `.add(TEMonsterEntities.CAVE_BAT, builder -> cave(builder, 20300, 1))` | `.add(MonsterEntities.ARMORED_SKELETON, builder -> builder.order(20200).rarity(2).background(CAVE).filters(FilterEntry.CAVE))` |
| 270 | `.add(TEMonsterEntities.BLUE_JELLYFISH, builder -> cave(builder, 20500, 1))` | `.add(MonsterEntities.TIM, builder -> builder.order(20400).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))` |
| 271 | `.add(TEMonsterEntities.GREEN_JELLYFISH, builder -> cave(builder, 20600, 2))` | `.add(MonsterEntities.RUNE_WIZARD, builder -> builder.order(20500).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))` |
| 272 | `.add(TEMonsterEntities.WOODEN_MIMIC, builder -> builder.order(20700).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))` | `.add(MonsterEntities.CAVE_BAT, builder -> builder.order(20600).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 273 | `.add(TEMonsterEntities.GOLDEN_MIMIC, builder -> builder.order(20701).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))` | `.add(MonsterEntities.GIANT_BAT, builder -> builder.order(20700).rarity(2).background(CAVE).filters(FilterEntry.CAVE))` |
| 274 | `.add(TEMonsterEntities.SHADOW_MIMIC, builder -> builder.order(20701).rarity(5).background(THE_NETHER).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_NETHER))` | `.add(MonsterEntities.BLUE_JELLYFISH, builder -> builder.order(20800).rarity(1).background(CAVE).filters(FilterEntry.CAVE))` |
| 275 | `.add(TEMonsterEntities.GIANT_SHELLY, builder -> cave(builder, 20800, 2))` | `.add(MonsterEntities.GREEN_JELLYFISH, builder -> builder.order(20900).rarity(2).background(CAVE).filters(FilterEntry.CAVE))` |
| 278 | `.add(TEMonsterEntities.GRANITE_ELEMENTAL, builder -> builder.order(21100).rarity(2).background(GRANITE).filters(FilterEntry.GRANITE))` | `.add(MonsterEntities.SHADOW_MIMIC, builder -> builder.order(21002).rarity(5).background(THE_NETHER).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_NETHER))` |
| 281 | `.add(TEMonsterEntities.SPORE_SKELETON, builder -> builder.order(21400).rarity(1).background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM))` | `.add(MonsterEntities.GRANITE_GOLEM, builder -> builder.order(21300).rarity(2).background(GRANITE).filters(FilterEntry.GRANITE))` |
| 282 | `.add(TEMonsterEntities.SPORE_BAT, builder -> builder.order(21500).rarity(1).background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM))` | `.add(MonsterEntities.GRANITE_ELEMENTAL, builder -> builder.order(21400).rarity(2).background(GRANITE).filters(FilterEntry.GRANITE))` |
| 285 | `.add(TEMonsterEntities.ICE_SLIME, builder -> builder.order(21800).rarity(1).background(SNOW).filters(FilterEntry.SNOW, FilterEntry.DAYTIME))` | `.add(MonsterEntities.SPORE_SKELETON, builder -> builder.order(21700).rarity(1).background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM))` |
| 290 | `.add(TEMonsterEntities.SPIKED_ICE_SLIME, builder -> builder.order(22200).rarity(2).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))` | `.add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "frozen", builder -> builder.order(22200).rarity(2).background(SNOW_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.SNOW).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.ESKIMO.getSerializedName())))` |
| 292 | `.add(TEMonsterEntities.UNDEAD_VIKING, builder -> builder.order(22400).rarity(2).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))` | `// 狼.add(MonsterEntities.WOLF, builder -> builder.order(22400).rarity(2).background(SNOW_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.SNOW))` |
| 293 | `.add(TEMonsterEntities.SNOW_FLINX, builder -> builder.order(22500).rarity(3).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))` | `.add(MonsterEntities.SPIKED_ICE_SLIME, builder -> builder.order(22500).rarity(2).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))` |
| 296 | `.add(TEMonsterEntities.ICE_BAT, builder -> builder.order(22800).rarity(1).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))` | `.add(MonsterEntities.SNOW_FLINX, builder -> builder.order(22800).rarity(3).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))` |
| 298 | `.add(TEMonsterEntities.ICE_MIMIC, builder -> builder.order(23000).rarity(5).background(UNDERGROUND_SNOW).filters(FilterEntry.RARE_CREATURE, FilterEntry.ICE))` | `.add(MonsterEntities.ICY_MERMAN, builder -> builder.order(23000).rarity(3).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))` |
| 301 | `.add(TEMonsterEntities.DESERT_SLIME, builder -> builder.order(23300).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))` | `.add(MonsterEntities.ICE_MIMIC, builder -> builder.order(23300).rarity(5).background(UNDERGROUND_SNOW).filters(FilterEntry.RARE_CREATURE, FilterEntry.ICE))` |
| 304 | `.add(TEMonsterEntities.MUMMY, builder -> builder.order(23600).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))` | `// 巨型蚁狮马.add(MonsterEntities.GIANT_ANTLION_CHARGER, builder -> builder.order(23800).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))` |
| 305 | `.add(TEMonsterEntities.GHOUL, builder -> builder.order(23700).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))` | `.add(MonsterEntities.MUMMY, builder -> builder.order(23900).rarity(2).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.UNDERGROUND_DESERT))` |
| 307 | `.add(TEMonsterEntities.TOMB_CRAWLER, builder -> builder.order(23900).rarity(1).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))` | `.add(MonsterEntities.BASILISK, builder -> builder.order(24100).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))` |
| 309 | `.add(TEMonsterEntities.SAND_POACHER, builder -> builder.order(24100).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))` | `.add(MonsterEntities.ANTLION, builder -> builder.order(24300).rarity(1).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.UNDERGROUND_DESERT))` |
| 310 | `.add(TEMonsterEntities.GIANT_ANTLION_SWARMER, builder -> builder.order(24200).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))` | `.add(MonsterEntities.SAND_POACHER, builder -> builder.order(24400).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))` |
| 314 | `.add(TEMonsterEntities.ANTLION_SWARMER, builder -> builder.order(24600).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))` | `.add(MonsterEntities.ANGRY_TUMBLER, builder -> builder.order(24800).rarity(2).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.SANDSTORM))` |
| 317 | `.add(TEAnimals.CRAB, builder -> builder.order(24900).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN))` | `.add(MonsterEntities.SAND_SHARK, builder -> builder.order(25100).rarity(2).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.SANDSTORM))` |
| 319 | `.add(TEMonsterEntities.SHARK, builder -> builder.order(25100).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))` | `// 海蜗牛.add(MonsterEntities.SEA_SNAIL, builder -> builder.order(25300).rarity(4).background(OCEAN).filters(FilterEntry.RARE_CREATURE, FilterEntry.OCEAN))` |
| 321 | `.add(TEMonsterEntities.PINK_JELLYFISH, builder -> builder.order(25300).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))` | `// 虎鲸.add(MonsterEntities.ORCA, builder -> builder.order(25500).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))` |
| 322 | `.add(TEMonsterEntities.JUNGLE_SLIME, builder -> builder.order(25400).rarity(1).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME))` | `// 乌贼.add(MonsterEntities.SQUID, builder -> builder.order(25600).rarity(3).background(OCEAN).filters(FilterEntry.RARE_CREATURE, FilterEntry.OCEAN))` |
| 323 | `.add(TEMonsterEntities.SNATCHER, builder -> builder.order(25500).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))` | `.add(MonsterEntities.PINK_JELLYFISH, builder -> builder.order(25700).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))` |
| 325 | `.add(TEMonsterEntities.DERPLING, builder -> builder.order(25700).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))` | `.add(MonsterEntities.GIANT_FLYING_FOX, builder -> builder.order(26000).rarity(2).background(THE_JUNGLE_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.THE_JUNGLE))` |
| 326 | `.add(TEMonsterEntities.SPIKED_JUNGLE_SLIME, builder -> builder.order(25800).rarity(2).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))` | `.add(MonsterEntities.DERPLING, builder -> builder.order(26100).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))` |
| 333 | `.add(TEMonsterEntities.HORNET, builder -> builder.order(26500).rarity(1).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))` | `// 黄蜂 （尖刺）.add(MonsterEntities.HORNET_SPIKEY, builder -> builder.order(26800).rarity(1).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))` |
| 336 | `.add(TEMonsterEntities.MAN_EATER, builder -> builder.order(27100).rarity(2).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))` | `// 黄蜂（蜂蜜）.add(MonsterEntities.HORNET_HONEY, builder -> builder.order(27100).rarity(1).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))` |
| 338 | `.add(TEMonsterEntities.JUNGLE_BAT, builder -> builder.order(27300).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))` | `.add(MonsterEntities.MOSS_HORNET, builder -> builder.order(27300).rarity(2).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))` |
| 339 | `.add(TEMonsterEntities.PIRANHA, builder -> builder.order(27400).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND, FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))` | `// 蛾.add(MonsterEntities.MOTH, builder -> builder.order(27400).rarity(4).background(UNDERGROUND_JUNGLE).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_JUNGLE))` |
| 341 | `.add(TEMonsterEntities.ARAPAIMA, builder -> builder.order(27600).rarity(2).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND, FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))` | `// 愤怒捕手.add(MonsterEntities.ANGRY_TRAPPER, builder -> builder.order(27600).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))` |
| 342 | `.add(TEMonsterEntities.JUNGLE_MIMIC, builder -> builder.order(27601).rarity(5).background(UNDERGROUND).filters(FilterEntry.RARE_CREATURE,  FilterEntry.THE_JUNGLE,FilterEntry.UNDERGROUND_JUNGLE))` | `.add(MonsterEntities.JUNGLE_BAT, builder -> builder.order(27700).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))` |
| 346 | `.add(TEMonsterEntities.METEOR_HEAD, builder -> builder.order(27900).rarity(2).background(METEOR).filters(FilterEntry.METEOR))` | `.add(MonsterEntities.METEOR_HEAD, builder -> builder.order(28300).rarity(2).background(METEOR).filters(FilterEntry.METEOR))` |
| 347 | `.add(TEMonsterEntities.DUNGEON_SLIME, builder -> builder.order(28000).rarity(4).background(THE_DUNGEON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_DUNGEON))` | `.add(MonsterEntities.DUNGEON_SLIME, builder -> builder.order(28400).rarity(4).background(THE_DUNGEON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_DUNGEON))` |
| 348 | `.add(TEMonsterEntities.ANGER_BONES, builder -> builder.order(28100).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` | `.add(MonsterEntities.ANGER_BONES, builder -> builder.order(28500).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` |
| 349 | `.add(TEMonsterEntities.SHORT_BONES, builder -> builder.order(28101).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` | `.add(MonsterEntities.SHORT_BONES, builder -> builder.order(28501).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` |
| 350 | `.add(TEMonsterEntities.BIG_BONES, builder -> builder.order(28102).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` | `.add(MonsterEntities.BIG_BONES, builder -> builder.order(28600).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` |
| 351 | `.add(TEMonsterEntities.BIG_ANGER_BONES, builder -> builder.order(28200).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` | `.add(MonsterEntities.BIG_ANGER_BONES, builder -> builder.order(28601).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` |
| 352 | `.add(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES, builder -> builder.order(28300).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` | `.add(MonsterEntities.BIG_MUSCLE_ANGER_BONES, builder -> builder.order(28700).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` |
| 353 | `.add(TEMonsterEntities.BIG_HELMET_ANGER_BONES, builder -> builder.order(28400).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` | `.add(MonsterEntities.BIG_HELMET_ANGER_BONES, builder -> builder.order(28800).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` |
| 371 | `.add(TEMonsterEntities.DARK_CASTER, builder -> builder.order(30200).rarity(1).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` | `.add(MonsterEntities.DARK_CASTER, builder -> builder.order(30600).rarity(1).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` |
| 378 | `.add(TEMonsterEntities.CURSED_SKULL, builder -> builder.order(30900).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` | `// 褴褛邪教徒法师 （敞开外衣）.add(MonsterEntities.RAGGED_CASTER_OPEN_COAT, builder -> builder.order(31300).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` |
| 380 | `.add(TEBossEntities.DUNGEON_GUARDIAN, builder -> builder.order(31100).rarity(4).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` | `// 巨型诅咒骷髅头.add(MonsterEntities.GIANT_CURSED_SKULL, builder -> builder.order(31600).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` |
| 382 | `.add(TEMonsterEntities.LAVA_SLIME, builder -> builder.order(31300).rarity(1).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` | `.add(MonsterEntities.DUNGEON_SPIRIT, builder -> builder.order(31800).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))` |
| 384 | `.add(TEMonsterEntities.BONE_SERPENT, builder -> builder.order(31500).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` | `// 痛苦亡魂.add(MonsterEntities.TORTURED_SOUL, builder -> builder.order(32000).rarity(5).background(THE_NETHER).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_NETHER))` |
| 385 | `.add(TEMonsterEntities.FIRE_IMP, builder -> builder.order(31600).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` | `.add(MonsterEntities.BONE_SERPENT, builder -> builder.order(32100).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` |
| 386 | `.add(TEMonsterEntities.HELL_BAT, builder -> builder.order(31700).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` | `.add(MonsterEntities.FIRE_IMP, builder -> builder.order(32200).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` |
| 387 | `.add(TEMonsterEntities.DEMON, builder -> builder.order(31800).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` | `.add(MonsterEntities.HELL_BAT, builder -> builder.order(32300).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` |
| 388 | `.add(TEMonsterEntities.VOODOO_DEMON, builder -> builder.order(31900).rarity(3).background(THE_NETHER).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_NETHER))` | `.add(MonsterEntities.DEMON, builder -> builder.order(32400).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` |
| 391 | `.add(TEMonsterEntities.WYVERN, builder -> builder.order(32200).rarity(3).background(SKY).filters(FilterEntry.SKY))` | `.add(MonsterEntities.RED_DEVIL, builder -> builder.order(32700).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` |
| 392 | `.add(TEMonsterEntities.HARPY, builder -> builder.order(32300).rarity(2).background(SKY).filters(FilterEntry.SKY))` | `.add(MonsterEntities.WYVERN, builder -> builder.order(32800).rarity(3).background(SKY).filters(FilterEntry.SKY))` |
| 395 | `.add(TEMonsterEntities.CORRUPT_SLIME, builder -> builder.order(32600).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))` | `// 小史莱姆.add(MonsterEntities.SLIMELING, builder -> builder.order(33100).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))` |
| 396 | `.add(TEMonsterEntities.EATER_OF_SOULS, builder -> builder.order(32700).rarity(1).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))` | `.add(MonsterEntities.CORRUPT_SLIME, builder -> builder.order(33200).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))` |
| 398 | `.add(TEMonsterEntities.DEVOURER, builder -> builder.order(32900).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))` | `// 腐化者.add(MonsterEntities.CORRUPTOR, builder -> builder.order(33400).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))` |
| 403 | `.add(TEMonsterEntities.CORRUPT_MIMIC, builder -> builder.order(33400).rarity(5).background(UNDERGROUND_CORRUPTION).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_CORRUPTION))` | `// 诅咒锤.add(MonsterEntities.CURSED_HAMMER, builder -> builder.order(33900).rarity(2).background(UNDERGROUND_CORRUPTION).filters(FilterEntry.UNDERGROUND_CORRUPTION))` |
| 406 | `.add(TEMonsterEntities.DARK_MUMMY, builder -> builder.order(33700).rarity(2).background(CORRUPT_DESERT).filters(FilterEntry.CORRUPT_DESERT))` | `.add(MonsterEntities.BONE_BITER, builder -> builder.order(34200).rarity(2).background(CORRUPT_DESERT).filters(FilterEntry.CORRUPT_DESERT, FilterEntry.SANDSTORM))` |
| 407 | `.add(TEMonsterEntities.VILE_GHOUL, builder -> builder.order(33800).rarity(2).background(CORRUPT_CAVE_DESERT).filters(FilterEntry.CAVE, FilterEntry.CORRUPT_DESERT))` | `.add(MonsterEntities.DARK_MUMMY, builder -> builder.order(34300).rarity(2).background(CORRUPT_DESERT).filters(FilterEntry.CORRUPT_DESERT, FilterEntry.CORRUPT_CAVE_DESERT))` |
| 408 | `.add(TEMonsterEntities.CRIMSLIME, builder -> builder.order(33900).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))` | `.add(MonsterEntities.VILE_GHOUL, builder -> builder.order(34400).rarity(2).background(CORRUPT_CAVE_DESERT).filters(FilterEntry.CORRUPT_CAVE_DESERT))` |
| 409 | `.add(TEMonsterEntities.FACE_MONSTER, builder -> builder.order(34000).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))` | `.add(MonsterEntities.CRIMSLIME, builder -> builder.order(34500).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))` |
| 410 | `.add(TEMonsterEntities.CRIMERA, builder -> builder.order(34100).rarity(1).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))` | `.add(MonsterEntities.FACE_MONSTER, builder -> builder.order(34600).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))` |
| 416 | `.add(TEMonsterEntities.BLOOD_CRAWLER, builder -> builder.order(34700).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))` | `// 猩红斧.add(MonsterEntities.CRIMSON_AXE, builder -> builder.order(35200).rarity(2).background(UNDERGROUND_CRIMSON).filters(FilterEntry.UNDERGROUND_CRIMSON))` |
| 417 | `.add(TEMonsterEntities.HERPLING, builder -> builder.order(34800).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))` | `.add(MonsterEntities.BLOOD_CRAWLER, builder -> builder.order(35300).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))` |
| 418 | `.add(TEMonsterEntities.CRIMSON_MIMIC, builder -> builder.order(34900).rarity(5).background(UNDERGROUND_CRIMSON).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_CRIMSON))` | `.add(MonsterEntities.HERPLING, builder -> builder.order(35400).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))` |
| 421 | `.add(TEMonsterEntities.BLOOD_MUMMY, builder -> builder.order(35200).rarity(2).background(CRIMSON_DESERT).filters(FilterEntry.CRIMSON_DESERT))` | `.add(MonsterEntities.FLESH_REAVER, builder -> builder.order(35700).rarity(2).background(CRIMSON_DESERT).filters(FilterEntry.CRIMSON_DESERT, FilterEntry.SANDSTORM))` |
| 422 | `.add(TEMonsterEntities.TAINTED_GHOUL, builder -> builder.order(35300).rarity(2).background(CRIMSON_CAVE_DESERT).filters(FilterEntry.CAVE, FilterEntry.CRIMSON_DESERT))` | `.add(MonsterEntities.BLOOD_MUMMY, builder -> builder.order(35800).rarity(2).background(CRIMSON_DESERT).filters(FilterEntry.CRIMSON_DESERT, FilterEntry.CRIMSON_CAVE_DESERT))` |
| 423 | `.add(TEMonsterEntities.DARK_LAMIA, builder -> builder.order(35400).rarity(2).background(CORRUPT_CAVE_DESERT).filters(FilterEntry.CORRUPT_CAVE_DESERT, FilterEntry.CRIMSON_CAVE_DESERT))` | `.add(MonsterEntities.TAINTED_GHOUL, builder -> builder.order(35900).rarity(2).background(CRIMSON_CAVE_DESERT).filters(FilterEntry.CRIMSON_CAVE_DESERT))` |
| 426 | `.add(TEMonsterEntities.PIXIE, builder -> builder.order(35700).rarity(2).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))` | `// 彩虹史莱姆.add(MonsterEntities.RAINBOW_SLIME, builder -> builder.order(36200).rarity(4).background(THE_HALLOW_RAIN).filters(FilterEntry.RARE_CREATURE, FilterEntry.RAIN, FilterEntry.THE_HALLOW))` |
| 429 | `.add(TEMonsterEntities.LUMINOUS_SLIME, builder -> builder.order(36000).rarity(2).background(UNDERGROUND_HALLOW).filters(FilterEntry.UNDERGROUND_HALLOW))` | `.add(MonsterEntities.UNICORN, builder -> builder.order(36500).rarity(2).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))` |
| 433 | `.add(TEMonsterEntities.HALLOWED_MIMIC, builder -> builder.order(36400).rarity(5).background(UNDERGROUND_HALLOW).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_HALLOW))` | `.add(MonsterEntities.ENCHANTED_SWORD, builder -> builder.order(36900).rarity(2).background(UNDERGROUND_HALLOW).filters(FilterEntry.UNDERGROUND_HALLOW))` |
| 436 | `.add(TEMonsterEntities.LIGHT_MUMMY, builder -> builder.order(36700).rarity(2).background(HALLOW_DESERT).filters(FilterEntry.RARE_CREATURE, FilterEntry.HALLOW_DESERT))` | `.add(MonsterEntities.CRYSTAL_THRESHER, builder -> builder.order(37200).rarity(2).background(HALLOW_DESERT).filters(FilterEntry.HALLOW_DESERT, FilterEntry.SANDSTORM))` |
| 437 | `.add(TEMonsterEntities.DREAMER_GHOUL, builder -> builder.order(36800).rarity(2).background(HALLOW_CAVE_DESERT).filters(FilterEntry.CAVE, FilterEntry.HALLOW_DESERT))` | `.add(MonsterEntities.LIGHT_MUMMY, builder -> builder.order(37300).rarity(2).background(HALLOW_DESERT).filters(FilterEntry.HALLOW_DESERT, FilterEntry.HALLOW_CAVE_DESERT))` |
| 438 | `.add(TEMonsterEntities.LIGHT_LAMIA, builder -> builder.order(36900).rarity(2).background(HALLOW_CAVE_DESERT).filters(FilterEntry.CAVE, FilterEntry.HALLOW_DESERT))` | `.add(MonsterEntities.DREAMER_GHOUL, builder -> builder.order(37400).rarity(2).background(HALLOW_CAVE_DESERT).filters(FilterEntry.HALLOW_CAVE_DESERT))` |
| 439 | `.add(TEMonsterEntities.SPORE_ZOMBIE, builder -> builder.order(37000).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))` | `.add(MonsterEntities.LIGHT_LAMIA, builder -> builder.order(37500).rarity(2).background(HALLOW_CAVE_DESERT).filters(FilterEntry.UNDERGROUND_DESERT, FilterEntry.HALLOW_CAVE_DESERT))` |
| 440 | `.add(TEMonsterEntities.HAT_SPORE_ZOMBIE, builder -> builder.order(37100).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))` | `.add(MonsterEntities.SPORE_ZOMBIE, builder -> builder.order(37600).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))` |
| 448 | `.add(TEMonsterEntities.GOBLIN_PEON, builder -> builder.order(37900).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))` | `// 飞蛇.add(MonsterEntities.FLYING_SNAKE, builder -> builder.order(38400).rarity(2).background(THE_TEMPLE).filters(FilterEntry.THE_TEMPLE))` |
| 449 | `.add(TEMonsterEntities.GOBLIN_THIEF, builder -> builder.order(38000).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))` | `.add(MonsterEntities.GOBLIN_PEON, builder -> builder.order(38500).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))` |
| 450 | `.add(TEMonsterEntities.GOBLIN_ARCHER, builder -> builder.order(38100).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))` | `.add(MonsterEntities.GOBLIN_THIEF, builder -> builder.order(38600).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))` |
| 451 | `.add(TEMonsterEntities.GOBLIN_WARRIOR, builder -> builder.order(38200).rarity(2).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))` | `.add(MonsterEntities.GOBLIN_ARCHER, builder -> builder.order(38700).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))` |
| 452 | `.add(TEMonsterEntities.GOBLIN_SORCERER, builder -> builder.order(38300).rarity(2).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))` | `.add(MonsterEntities.GOBLIN_WARRIOR, builder -> builder.order(38800).rarity(2).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))` |
| 453 | `.add(TEMonsterEntities.ANGER_GOBLIN, builder -> builder.order(38301).rarity(3).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))` | `.add(MonsterEntities.GOBLIN_SORCERER, builder -> builder.order(39000).rarity(2).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))` |
| 575 | `.add(TEMonsterEntities.DEMON_EYE, "minion", builder -> builder.order(50500).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` | `.add(BossEntities.SERVANT_OF_CTHULHU, "entity.confluence.demon_eye", "minion", builder -> builder.order(51100).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 577 | `.add(TEBossEntities.EATER_OF_WORLDS, builder -> builder.order(50700).rarity(3).background(THE_CORRUPTION).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_CORRUPTION))` | `.add(BossEntities.EATER_OF_WORLDS, builder -> builder.order(51300).rarity(3).background(THE_CORRUPTION).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_CORRUPTION))` |
| 578 | `.add(TEBossEntities.BRAIN_OF_CTHULHU, builder -> builder.order(50800).rarity(3).background(THE_CRIMSON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_CRIMSON))` | `.add(BossEntities.BRAIN_OF_CTHULHU, builder -> builder.order(51400).rarity(3).background(THE_CRIMSON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_CRIMSON))` |
| 579 | `.add(TEMonsterEntities.VISUAL_NEURON, builder -> builder.order(50900).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON))` | `.add(MonsterEntities.VISUAL_NEURON, builder -> builder.order(51500).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON))` |
| 581 | `.add(TEBossEntities.SKELETRON, builder -> builder.order(51100).rarity(3).background(THE_DUNGEON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_DUNGEON))` | `.add(BossEntities.SKELETRON, builder -> builder.order(51700).rarity(3).background(THE_DUNGEON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_DUNGEON))` |
| 582 | `.add(TEBossEntities.QUEEN_BEE, builder -> builder.order(51200).rarity(3).background(UNDERGROUND_JUNGLE).filters(FilterEntry.BOSS_ENEMY, FilterEntry.UNDERGROUND_JUNGLE))` | `.add(BossEntities.QUEEN_BEE, builder -> builder.order(51800).rarity(3).background(UNDERGROUND_JUNGLE).filters(FilterEntry.BOSS_ENEMY, FilterEntry.UNDERGROUND_JUNGLE))` |
| 583 | `.add(TEBossEntities.WALL_OF_FLESH, builder -> builder.order(51300).rarity(4).background(THE_NETHER).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_NETHER))` | `.add(BossEntities.WALL_OF_FLESH, builder -> builder.order(51900).rarity(4).background(THE_NETHER).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_NETHER))` |
| 584 | `.add(TEMonsterEntities.LEECH, builder -> builder.order(51400).rarity(1).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` | `.add(MonsterEntities.LEECH, builder -> builder.order(52000).rarity(1).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` |
| 585 | `.add(TEMonsterEntities.THE_HUNGRY, builder -> builder.order(51500).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` | `.add(MonsterEntities.THE_HUNGRY, builder -> builder.order(52100).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` |
| 591 | `.add(TEBossEntities.RETINAZER, builder -> builder.order(52100).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))` | `// 飞翔史莱姆.add(BossEntities.HEAVENLY_SLIME, builder -> builder.order(52600).rarity(2).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))` |
| 592 | `.add(TEBossEntities.SPAZMATISM, builder -> builder.order(52200).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))` | `.add(BossEntities.RETINAZER, builder -> builder.order(52700).rarity(4).background(SURFACE_MOON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))` |
| 596 | `.add(TEBossEntities.SKELETRON_PRIME, builder -> builder.order(52500).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))` | `.add(BossEntities.THE_DESTROYER_PROBE, builder -> builder.order(53000).rarity(2).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))` |
| 597 | `.add(TEBossEntities.PLANTERA, builder -> builder.order(52600).rarity(4).background(UNDERGROUND_JUNGLE).filters(FilterEntry.BOSS_ENEMY, FilterEntry.UNDERGROUND_JUNGLE))` | `.add(BossEntities.SKELETRON_PRIME, builder -> builder.order(53100).rarity(4).background(SURFACE_MOON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))` |
| 690 | `.add(TEMonsterEntities.DECAYEDER, builder -> builder.order(70000).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.SURFACE,FilterEntry.DAYTIME,FilterEntry.THE_CORRUPTION))` | `.add(MonsterEntities.DECAYEDER, builder -> builder.order(70000).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME, FilterEntry.THE_CORRUPTION))` |
| 691 | `.add(TEMonsterEntities.BLOODY_SPORE, builder -> builder.order(70100).rarity(2).background(THE_CRIMSON).filters(FilterEntry.SURFACE,FilterEntry.DAYTIME,FilterEntry.THE_CRIMSON))` | `.add(MonsterEntities.BLOODY_SPORE, builder -> builder.order(70100).rarity(2).background(THE_CRIMSON).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME, FilterEntry.THE_CRIMSON))` |
| 692 | `.add(TEBossEntities.HILL_OF_FLESH, builder -> builder.order(70200).rarity(4).background(THE_NETHER).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_NETHER))` | `.add(BossEntities.HILL_OF_FLESH, builder -> builder.order(70200).rarity(4).background(THE_NETHER).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_NETHER))` |
| 693 | `.add(TEMonsterEntities.WITHER_BONE_SERPENT, builder -> builder.order(70300).rarity(3).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` | `.add(MonsterEntities.WITHER_BONE_SERPENT, builder -> builder.order(70300).rarity(3).background(THE_NETHER).filters(FilterEntry.THE_NETHER))` |
| 742 | `public Builder demonEyeVariant(DemonEyeVariant variant, Consumer<ClientBestiaryEntry.Builder> consumer) {` | `public <E extends Enum<E> & IVariant> Builder numberedVariant(Supplier<? extends EntityType<?>> type, int displayVariant, E entityVariant, Consumer<ClientBestiary.Entry.Builder> consumer) {` |
| 743 | `return add(TEMonsterEntities.DEMON_EYE, variant.getSerializedName(), consumer.andThen(builder -> builder.entityNbt(nbt -> nbt.putInt(DemonEye.VARIANT_KEY, variant.getId()))));` | `return numberedVariant(type, type.get().getDescriptionId(), displayVariant, entityVariant, consumer);` |
| 743 | `return add(TEMonsterEntities.DEMON_EYE, variant.getSerializedName(), consumer.andThen(builder -> builder.entityNbt(nbt -> nbt.putInt(DemonEye.VARIANT_KEY, variant.getId()))));` | `return numberedVariant(type, type.get().getDescriptionId(), displayVariant, entityVariant, consumer);` |

### `common/data/gen/ModDataProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/ModDataProvider.java`
- TE import：`TEAnimals`, `TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 1382 | `new MobSpawnSettings.SpawnerData(TEAnimals.CRAB.get(), 7, 1, 1),` | `new MobSpawnSettings.SpawnerData(CritterEntities.CRAB.get(), 7, 1, 1),` |
| 1383 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.TROPIC_SLIME.get(), 7, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.TROPIC_SLIME.get(), 7, 1, 2),` |
| 1384 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_SCOUT.get(), 15, 1, 1)` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GOBLIN_SCOUT.get(), 15, 1, 1)` |
| 1390 | `new MobSpawnSettings.SpawnerData(TEAnimals.SCORPION.get(), 15, 1, 1),` | `new MobSpawnSettings.SpawnerData(CritterEntities.SCORPION.get(), 15, 1, 1),` |
| 1391 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.DESERT_SLIME.get(), 15, 1, 2),` | `new MobSpawnSettings.SpawnerData(CritterEntities.DRAGONFLY.get(), 5, 1, 2),` |
| 1392 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.TOMB_CRAWLER.get(), 180, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.DESERT_SLIME.get(), 23, 1, 2),` |
| 1393 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.ANTLION_SWARMER.get(), 500, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.TOMB_CRAWLER.get(), 270, 1, 1),` |
| 1394 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GIANT_ANTLION_SWARMER.get(), 200, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.ANTLION_SWARMER.get(), 750, 1, 1),` |
| 1395 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.WOODEN_MIMIC.get(), 2, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GIANT_ANTLION_SWARMER.get(), 300, 1, 1),` |
| 1396 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOLDEN_MIMIC.get(), 2, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.WOODEN_MIMIC.get(), 3, 1, 1),` |
| 1397 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.MUMMY.get(), 35, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GOLDEN_MIMIC.get(), 3, 1, 1),` |
| 1398 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.LIGHT_LAMIA.get(), 45, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.MUMMY.get(), 53, 1, 2),` |
| 1399 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GHOUL.get(), 35, 2, 3),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.LIGHT_LAMIA.get(), 68, 1, 1),` |
| 1400 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.SAND_POACHER.get(), 45, 1, 1)` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GHOUL.get(), 53, 2, 3),` |
| 1406 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.ICE_BAT.get(), 140, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.ICE_BAT.get(), 210, 1, 2),` |
| 1407 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.UNDEAD_VIKING.get(), 140, 1, 2),` | `new MobSpawnSettings.SpawnerData(CritterEntities.PENGUIN.get(), 10, 1, 3),` |
| 1408 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.SNOW_FLINX.get(), 130, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.UNDEAD_VIKING.get(), 210, 1, 2),` |
| 1409 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPIKED_ICE_SLIME.get(), 130, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.ICE_TORTOISE.get(), 45, 1, 1),` |
| 1410 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.ICE_SLIME.get(), 15, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.ARMORED_VIKING.get(), 150, 1, 2),` |
| 1411 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.WOODEN_MIMIC.get(), 2, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.ICE_ELEMENTAL.get(), 90, 1, 1),` |
| 1412 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.ICE_MIMIC.get(), 6, 1, 1)` | `new MobSpawnSettings.SpawnerData(MonsterEntities.ICY_MERMAN.get(), 90, 1, 1),` |
| 1418 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.HORNET.get(), 170, 1, 2),` | `new MobSpawnSettings.SpawnerData(CritterEntities.MYSTIC_FROG.get(), 10, 1, 1),` |
| 1419 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.JUNGLE_BAT.get(), 40, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.HORNET.get(), 255, 1, 2),` |
| 1420 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.JUNGLE_SLIME.get(), 40, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.MOSS_HORNET.get(), 135, 1, 2),` |
| 1421 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPIKED_JUNGLE_SLIME.get(), 100, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.JUNGLE_CREEPER.get(), 60, 1, 2),` |
| 1422 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.MAN_EATER.get(), 150, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.JUNGLE_BAT.get(), 60, 1, 2),` |
| 1423 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.SNATCHER.get(), 50, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GIANT_FLYING_FOX.get(), 60, 1, 1),` |
| 1424 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.PIRANHA.get(), 40, 2, 3),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GIANT_TORTOISE.get(), 60, 1, 1),` |
| 1425 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.ARAPAIMA.get(), 40, 2, 3),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.JUNGLE_SLIME.get(), 60, 1, 2),` |
| 1426 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.DERPLING.get(), 80, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.SPIKED_JUNGLE_SLIME.get(), 150, 1, 2),` |
| 1427 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.WOODEN_MIMIC.get(), 2, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.MAN_EATER.get(), 225, 1, 1),` |
| 1428 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOLDEN_MIMIC.get(), 2, 1, 1)` | `new MobSpawnSettings.SpawnerData(MonsterEntities.SNATCHER.get(), 75, 1, 1),` |
| 1429 | `// todo 仅 Celebrationmk10 和 Get fixed boi 世界生成 new MobSpawnSettings.SpawnerData(TEMonsterEntities.JUNGLE_MIMIC.get(), 3, 1, 1)` | `new MobSpawnSettings.SpawnerData(MonsterEntities.PIRANHA.get(), 40, 2, 3),` |
| 1436 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.NYMPH.get(), 3, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.NYMPH.get(), 3, 1, 1),` |
| 1437 | `new MobSpawnSettings.SpawnerData(TEAnimals.FAIRY.get(), 3, 1, 1)` | `new MobSpawnSettings.SpawnerData(MonsterEntities.ARMORED_SKELETON.get(), 40, 1, 2),` |
| 1443 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.SWAMP_SLIME.get(), 90, 1, 3)` | `new MobSpawnSettings.SpawnerData(MonsterEntities.SWAMP_SLIME.get(), 90, 1, 3)` |
| 1449 | `new MobSpawnSettings.SpawnerData(TEAnimals.BUNNY.get(), 10, 1, 2),` | `new MobSpawnSettings.SpawnerData(CritterEntities.BUNNY.get(), 10, 1, 2),` |
| 1450 | `new MobSpawnSettings.SpawnerData(TEAnimals.SQUIRREL.get(), 10, 1, 2),` | `new MobSpawnSettings.SpawnerData(CritterEntities.SQUIRREL.get(), 10, 1, 2),` |
| 1451 | `new MobSpawnSettings.SpawnerData(TEAnimals.DUCK.get(), 10, 1, 2),` | `new MobSpawnSettings.SpawnerData(CritterEntities.CLOUD_SHEEP.get(), 2, 2, 3),` |
| 1452 | `new MobSpawnSettings.SpawnerData(TEAnimals.BIRD.get(), 10, 1, 2),` | `new MobSpawnSettings.SpawnerData(CritterEntities.RED_SQUIRREL.get(), 10, 1, 2),` |
| 1453 | `new MobSpawnSettings.SpawnerData(TEAnimals.BLUE_JAY.get(), 10, 1, 2),` | `new MobSpawnSettings.SpawnerData(CritterEntities.DUCK.get(), 10, 1, 2),` |
| 1454 | `new MobSpawnSettings.SpawnerData(TEAnimals.CARDINAL.get(), 10, 1, 2)` | `new MobSpawnSettings.SpawnerData(CritterEntities.BIRD.get(), 10, 1, 2),` |
| 1460 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLACK_SLIME.get(), 60, 1, 3),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.BLACK_SLIME.get(), 24, 1, 3),` |
| 1461 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLUE_SLIME.get(), 30, 2, 4),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.MOTHER_SLIME.get(), 36, 1, 3),` |
| 1462 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.CAVE_BAT.get(), 145, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.BLUE_SLIME.get(), 30, 2, 4),` |
| 1463 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GIANT_SHELLY.get(), 90, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.CAVE_BAT.get(), 145, 1, 2),` |
| 1464 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRAWDAD.get(), 90, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GIANT_SHELLY.get(), 90, 1, 1),` |
| 1465 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GIANT_WORM.get(), 60, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.CRAWDAD.get(), 90, 1, 1),` |
| 1466 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GREEN_DUMPLING_SLIME.get(), 30, 1, 3),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GIANT_WORM.get(), 60, 1, 1),` |
| 1467 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GREEN_SLIME.get(), 45, 3, 3),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.DIGGER.get(), 30, 1, 1),` |
| 1468 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.PINK_SLIME.get(), 2, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.TIM.get(), 5, 1, 1),` |
| 1469 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.PURPLE_SLIME.get(), 15, 1, 3),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.RUNE_WIZARD.get(), 1, 1, 1),` |
| 1470 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.RED_SLIME.get(), 45, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.DOCTOR_BONES.get(), 1, 1, 1),` |
| 1471 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.YELLOW_SLIME.get(), 45, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.THE_GROOM.get(), 2, 1, 1),` |
| 1472 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEMON_EYE.get(), 65, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.THE_BRIDE.get(), 2, 1, 1),` |
| 1473 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.POSSESS_ARMOR.get(), 65, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.ANGRY_DANDELION.get(), 10, 1, 2),` |
| 1474 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.WRAITH.get(), 65, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.WINDY_BALLOON.get(), 10, 1, 1),` |
| 1475 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.FLYING_FISH.get(), 60, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GNOME.get(), 10, 1, 1),` |
| 1476 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.WOODEN_MIMIC.get(), 2, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.ANGRY_NIMBUS.get(), 10, 1, 1),` |
| 1477 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOLDEN_MIMIC.get(), 2, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GREEN_DUMPLING_SLIME.get(), 30, 1, 3),` |
| 1478 | `new MobSpawnSettings.SpawnerData(TEAnimals.BUNNY.get(), 10, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GREEN_SLIME.get(), 45, 3, 3),` |
| 1479 | `new MobSpawnSettings.SpawnerData(TEAnimals.SQUIRREL.get(), 10, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.PINK_SLIME.get(), 2, 1, 1),` |
| 1480 | `new MobSpawnSettings.SpawnerData(TEAnimals.DUCK.get(), 10, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.PURPLE_SLIME.get(), 15, 1, 3),` |
| 1481 | `new MobSpawnSettings.SpawnerData(TEAnimals.BIRD.get(), 10, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.RED_SLIME.get(), 45, 1, 2),` |
| 1482 | `new MobSpawnSettings.SpawnerData(TEAnimals.BLUE_JAY.get(), 10, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.YELLOW_SLIME.get(), 45, 1, 2),` |
| 1483 | `new MobSpawnSettings.SpawnerData(TEAnimals.CARDINAL.get(), 10, 1, 2),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.DEMON_EYE.get(), 65, 1, 2),` |
| 1484 | `new MobSpawnSettings.SpawnerData(TEAnimals.SNAIL.get(), 10, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.POSSESS_ARMOR.get(), 65, 1, 2),` |
| 1485 | `new MobSpawnSettings.SpawnerData(TEAnimals.WORM.get(), 15, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.WRAITH.get(), 65, 1, 2),` |
| 1486 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLUE_JELLYFISH.get(), 5, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.FLYING_FISH.get(), 60, 1, 2),` |
| 1487 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GREEN_JELLYFISH.get(), 5, 1, 1)` | `new MobSpawnSettings.SpawnerData(MonsterEntities.WOODEN_MIMIC.get(), 2, 1, 1),` |
| 1493 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.LAVA_SLIME.get(), 25, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.LAVA_SLIME.get(), 25, 1, 1),` |
| 1494 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.HELL_BAT.get(), 20, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.HELL_BAT.get(), 20, 1, 1),` |
| 1495 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.FIRE_IMP.get(), 13, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.LAVA_BAT.get(), 20, 1, 1),` |
| 1496 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.BONE_SERPENT.get(), 1, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.RED_DEVIL.get(), 10, 1, 1),` |
| 1497 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.SHADOW_MIMIC.get(), 1, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.FIRE_IMP.get(), 13, 1, 1),` |
| 1498 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEMON.get(), 7, 1, 1)` | `new MobSpawnSettings.SpawnerData(MonsterEntities.BONE_SERPENT.get(), 1, 1, 1),` |
| 1504 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.PINK_JELLYFISH.get(), 1, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.PINK_JELLYFISH.get(), 1, 1, 1),` |
| 1505 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.SHARK.get(), 1, 1, 1)` | `new MobSpawnSettings.SpawnerData(MonsterEntities.SHARK.get(), 1, 1, 1)` |
| 1511 | `new MobSpawnSettings.SpawnerData(TEAnimals.BUTTERFLY.get(), 30, 1, 3)` | `new MobSpawnSettings.SpawnerData(CritterEntities.BUTTERFLY.get(), 30, 1, 3)` |
| 1543 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DECAYEDER.get(), 30, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.DECAYEDER.get(), 35, 1, 1))` |
| 1544 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEVOURER.get(), 3, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.DEVOURER.get(), 3, 1, 1))` |
| 1545 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.WORLD_FEEDER.get(), 9, 1, 1))` |
| 1546 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CORRUPT_SLIME.get(), 35, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2))` |
| 1547 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CORRUPT_MIMIC.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CORRUPTOR.get(), 65, 1, 2))` |
| 1564 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DECAYEDER.get(), 22, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.DECAYEDER.get(), 22, 1, 1))` |
| 1565 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEVOURER.get(), 3, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.DEVOURER.get(), 3, 1, 1))` |
| 1566 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.WORLD_FEEDER.get(), 9, 1, 1))` |
| 1567 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CORRUPT_SLIME.get(), 35, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2))` |
| 1568 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CORRUPT_MIMIC.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CORRUPTOR.get(), 65, 1, 2))` |
| 1569 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DARK_MUMMY.get(), 35, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CORRUPT_SLIME.get(), 35, 1, 1))` |
| 1570 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DARK_LAMIA.get(), 45, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.SLIMER.get(), 35, 1, 1))` |
| 1571 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.VILE_GHOUL.get(), 35, 2, 3))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CORRUPT_MIMIC.get(), 1, 1, 1))` |
| 1579 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DECAYEDER.get(), 22, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.DECAYEDER.get(), 22, 1, 1))` |
| 1580 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEVOURER.get(), 3, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.DEVOURER.get(), 3, 1, 1))` |
| 1581 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.WORLD_FEEDER.get(), 9, 1, 1))` |
| 1582 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CORRUPT_SLIME.get(), 35, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2))` |
| 1583 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CORRUPT_MIMIC.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CORRUPTOR.get(), 65, 1, 2))` |
| 1592 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1))` |
| 1593 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOODY_SPORE.get(), 30, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.BLOODY_SPORE.get(), 30, 1, 1))` |
| 1594 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMERA.get(), 60, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CRIMERA.get(), 60, 1, 1))` |
| 1595 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FACE_MONSTER.get(), 60, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.FACE_MONSTER.get(), 60, 1, 1))` |
| 1596 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSLIME.get(), 35, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CRIMSLIME.get(), 35, 1, 1))` |
| 1597 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSON_MIMIC.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CRIMSON_MIMIC.get(), 1, 1, 1))` |
| 1598 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HERPLING.get(), 60, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.HERPLING.get(), 60, 1, 1))` |
| 1615 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1))` |
| 1616 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOODY_SPORE.get(), 30, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.BLOODY_SPORE.get(), 30, 1, 1))` |
| 1617 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMERA.get(), 60, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CRIMERA.get(), 60, 1, 1))` |
| 1618 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FACE_MONSTER.get(), 60, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.FACE_MONSTER.get(), 60, 1, 1))` |
| 1619 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSLIME.get(), 35, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CRIMSLIME.get(), 35, 1, 1))` |
| 1620 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSON_MIMIC.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CRIMSON_MIMIC.get(), 1, 1, 1))` |
| 1621 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_MUMMY.get(), 35, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.BLOOD_MUMMY.get(), 35, 1, 2))` |
| 1622 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DARK_LAMIA.get(), 45, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.DARK_LAMIA.get(), 45, 1, 1))` |
| 1623 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HERPLING.get(), 60, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.HERPLING.get(), 60, 1, 1))` |
| 1624 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.TAINTED_GHOUL.get(), 35, 2, 3))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.TAINTED_GHOUL.get(), 35, 2, 3))` |
| 1632 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1))` |
| 1633 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOODY_SPORE.get(), 30, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.BLOODY_SPORE.get(), 30, 1, 1))` |
| 1634 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMERA.get(), 60, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CRIMERA.get(), 60, 1, 1))` |
| 1635 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FACE_MONSTER.get(), 60, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.FACE_MONSTER.get(), 60, 1, 1))` |
| 1636 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSLIME.get(), 35, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CRIMSLIME.get(), 35, 1, 1))` |
| 1637 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSON_MIMIC.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CRIMSON_MIMIC.get(), 1, 1, 1))` |
| 1638 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HERPLING.get(), 60, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.HERPLING.get(), 60, 1, 1))` |
| 1646 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.PIXIE.get(), 60, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.PIXIE.get(), 60, 1, 2))` |
| 1647 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.LUMINOUS_SLIME.get(), 35, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.UNICORN.get(), 35, 1, 2))` |
| 1648 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HALLOWED_MIMIC.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.GASTROPOD.get(), 15, 1, 1))` |
| 1656 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.PIXIE.get(), 60, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.PIXIE.get(), 60, 1, 2))` |
| 1657 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.LUMINOUS_SLIME.get(), 35, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.UNICORN.get(), 35, 1, 2))` |
| 1658 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HALLOWED_MIMIC.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.GASTROPOD.get(), 15, 1, 1))` |
| 1659 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.LIGHT_MUMMY.get(), 35, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.CHAOS_ELEMENTAL.get(), 45, 1, 2))` |
| 1660 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.LIGHT_LAMIA.get(), 45, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.LUMINOUS_SLIME.get(), 35, 1, 1))` |
| 1661 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DREAMER_GHOUL.get(), 35, 2, 3))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.HALLOWED_MIMIC.get(), 1, 1, 1))` |
| 1669 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.PIXIE.get(), 60, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.PIXIE.get(), 60, 1, 2))` |
| 1670 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.LUMINOUS_SLIME.get(), 35, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.UNICORN.get(), 35, 1, 2))` |
| 1671 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HALLOWED_MIMIC.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.GASTROPOD.get(), 15, 1, 1))` |
| 1679 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEMON.get(), 10, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.DEMON.get(), 10, 1, 1))` |
| 1680 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.VOODOO_DEMON.get(), 4, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.VOODOO_DEMON.get(), 2, 1, 1))` |
| 1681 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FIRE_IMP.get(), 25, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.FIRE_IMP.get(), 25, 1, 1))` |
| 1682 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BONE_SERPENT.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.BONE_SERPENT.get(), 1, 1, 1))` |
| 1683 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HELL_BAT.get(), 60, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.HELL_BAT.get(), 60, 1, 2))` |
| 1684 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.LAVA_SLIME.get(), 80, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.LAVA_SLIME.get(), 80, 1, 1))` |
| 1685 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SHADOW_MIMIC.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.SHADOW_MIMIC.get(), 1, 1, 1))` |
| 1686 | `// .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEAnimals.MAGMA_SNAIL.get(), 20, 1, 2))  //todo 地狱中小动物生成` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.SHADOW_MIMIC.get(), 1, 1, 1))` |
| 1687 | `// .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEAnimals.HELL_BUTTERFLY.get(), 20, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.SHADOW_MIMIC.get(), 1, 1, 1))` |
| 1699 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEMON.get(), 15, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.DEMON.get(), 15, 1, 1))` |
| 1700 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.VOODOO_DEMON.get(), 5, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.VOODOO_DEMON.get(), 4, 1, 1))` |
| 1701 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FIRE_IMP.get(), 20, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.FIRE_IMP.get(), 20, 1, 1))` |
| 1702 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BONE_SERPENT.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.BONE_SERPENT.get(), 1, 1, 1))` |
| 1703 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HELL_BAT.get(), 40, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.HELL_BAT.get(), 40, 1, 2))` |
| 1704 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.LAVA_SLIME.get(), 40, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.LAVA_SLIME.get(), 40, 1, 1))` |
| 1705 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SHADOW_MIMIC.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.SHADOW_MIMIC.get(), 1, 1, 1))` |
| 1706 | `// .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEAnimals.MAGMA_SNAIL.get(), 20, 1, 2)) //todo 地狱中小动物生成` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.SHADOW_MIMIC.get(), 1, 1, 1))` |
| 1707 | `// .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEAnimals.HELL_BUTTERFLY.get(), 20, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.SHADOW_MIMIC.get(), 1, 1, 1))` |
| 1717 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPORE_BAT.get(), 60, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.SPORE_BAT.get(), 60, 1, 2))` |
| 1718 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPORE_SKELETON.get(), 60, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.SPORE_SKELETON.get(), 60, 1, 2))` |
| 1719 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPORE_ZOMBIE.get(), 45, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.SPORE_ZOMBIE.get(), 45, 1, 2))` |
| 1720 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HAT_SPORE_ZOMBIE.get(), 15, 1, 2))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.HAT_SPORE_ZOMBIE.get(), 15, 1, 2))` |
| 1721 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.WOODEN_MIMIC.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.WOODEN_MIMIC.get(), 1, 1, 1))` |
| 1722 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOLDEN_MIMIC.get(), 1, 1, 1))` | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(MonsterEntities.GOLDEN_MIMIC.get(), 1, 1, 1))` |
| 1723 | `.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEAnimals.GLOWING_SNAIL.get(), 10, 1, 2))` | `.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(CritterEntities.GLOWING_SNAIL.get(), 10, 1, 2))` |
| 2132 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GRANITE_ELEMENTAL.get(), 30, 1, 1)` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GRANITE_ELEMENTAL.get(), 30, 1, 1),` |
| 2151 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.ANGER_BONES.get(), 240, 8, 9),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.ANGER_BONES.get(), 240, 8, 9),` |
| 2152 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_ANGER_BONES.get(), 240, 8, 9),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.BIG_ANGER_BONES.get(), 240, 8, 9),` |
| 2153 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_BONES.get(), 240, 8, 9),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.BIG_BONES.get(), 240, 8, 9),` |
| 2154 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_HELMET_ANGER_BONES.get(), 240, 8, 9),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.BIG_HELMET_ANGER_BONES.get(), 240, 8, 9),` |
| 2155 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), 240, 8, 9),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), 240, 8, 9),` |
| 2156 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.SHORT_BONES.get(), 240, 8, 9),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.SHORT_BONES.get(), 240, 8, 9),` |
| 2157 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.DARK_CASTER.get(), 240, 2, 3),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.DARK_CASTER.get(), 240, 2, 3),` |
| 2158 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.CURSED_SKULL.get(), 200, 3, 4),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.WATER_BOLT_MIMIC.get(), 15, 1, 1),` |
| 2159 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.DUNGEON_SLIME.get(), 120, 1, 2)` | `new MobSpawnSettings.SpawnerData(MonsterEntities.CURSED_SKULL.get(), 200, 3, 4),` |
| 2190 | `new MobSpawnSettings.SpawnerData(TEAnimals.FEALING.get(), 30, 1, 2)` | `new MobSpawnSettings.SpawnerData(CritterEntities.FEALING.get(), 30, 2, 5)` |

### `common/data/gen/ModEnglishProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/ModEnglishProvider.java`
- TE import：`TEEffects`, `RecipeDrawerUtils`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 1840 | `addEffect(TEEffects.DEMONIC_THOUGHTS.get(), "Being inflicted with Demonic Thoughts again spawns Eater of Souls");` | `addEffect(ModEffects.CRIMSON_STORM.get(), "You are trapped in the storm, there is no escape.");` |
| 1841 | `addEffect(TEEffects.SUMMON_FOCUS.get(), "Minions deal additional damage");` | `addEffect(ModEffects.HORRIFIED.get(), "You have seen something nasty, there is no escape.");` |
| 1842 | `addEffect(TEEffects.HELLFIRE.get(), "Losing life");` | `addEffect(ModEffects.THE_TONGUE.get(), "You are being sucked into the mouth");` |
| 1843 | `addEffect(TEEffects.FROST_BURN.get(), "Losing life; Cannot regenerate life");` | `addEffect(ModEffects.SCARED.get(), "Like a bird startled by the sound of a bow, fleeing in all directions");` |
| 1844 | `addEffect(TEEffects.CRIMSON_STORM.get(), "You are trapped in the storm, there is no escape.");` | `addEffect(ModEffects.SCARED.get(), "Like a bird startled by the sound of a bow, fleeing in all directions");` |
| 1845 | `addEffect(TEEffects.HORRIFIED.get(), "You have seen something nasty, there is no escape.");` | `addEffect(ModEffects.SCARED.get(), "Like a bird startled by the sound of a bow, fleeing in all directions");` |
| 1846 | `addEffect(TEEffects.THE_TONGUE.get(), "You are being sucked into the mouth");` | `addEffect(ModEffects.SCARED.get(), "Like a bird startled by the sound of a bow, fleeing in all directions");` |
| 1847 | `addEffect(TEEffects.SCARED.get(), "Like a bird startled by the sound of a bow, fleeing in all directions");` | `addEffect(ModEffects.SCARED.get(), "Like a bird startled by the sound of a bow, fleeing in all directions");` |
| 1887 | `return RecipeDrawerUtils.formatLocationPath(location);` | `add("tooltip.confluence.boomerang.fly_speed", "Fly Speed");` |
| 1891 | `return RecipeDrawerUtils.formatString(name);` | `addWhipTranslation(WhipItems.LEATHER_WHIP.get(), "Leather Whip");` |

### `common/data/gen/ModItemModelProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/ModItemModelProvider.java`
- TE import：`TEBoomerangItems`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 179 | `handheld.add(createDir(TEBoomerangItems.ITEMS, "boomerang/"));` | `handheld.add(createDir(BoomerangItems.ITEMS, "boomerang/"));` |

### `common/data/gen/data_map/BlockBreakSpawnsSubProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/data_map/BlockBreakSpawnsSubProvider.java`
- TE import：`TEAnimals`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 33 | `.expand(TEAnimals.GRASSHOPPER.get(), 0.01F)` | `.expand(CritterEntities.GRASSHOPPER.get(), 0.01F)` |
| 34 | `.expand(TEAnimals.WORM.get(), 0.0025F)` | `.expand(CritterEntities.WORM.get(), 0.0025F)` |
| 37 | `.expand(TEAnimals.WORM.get(), 0.0025F)` | `.expand(CritterEntities.WORM.get(), 0.0025F)` |
| 40 | `.expand(TEAnimals.GRASSHOPPER.get(), 0.01F)` | `.expand(CritterEntities.GRASSHOPPER.get(), 0.01F)` |
| 41 | `.expand(TEAnimals.WORM.get(), 0.0025F)` | `.expand(CritterEntities.WORM.get(), 0.0025F)` |
| 44 | `.expand(TEAnimals.GRASSHOPPER.get(), 0.01F)` | `.expand(CritterEntities.GRASSHOPPER.get(), 0.01F)` |
| 45 | `.expand(TEAnimals.WORM.get(), 0.0025F)` | `.expand(CritterEntities.WORM.get(), 0.0025F)` |
| 48 | `.expand(TEAnimals.GRASSHOPPER.get(), 0.02F)` | `.expand(CritterEntities.GRASSHOPPER.get(), 0.02F)` |
| 49 | `.expand(TEAnimals.WORM.get(), 0.005F)` | `.expand(CritterEntities.WORM.get(), 0.005F)` |
| 52 | `.expand(TEAnimals.HELL_BUTTERFLY.get(), 0.07F)` | `.expand(CritterEntities.HELL_BUTTERFLY.get(), 0.07F)` |
| 53 | `.expand(TEAnimals.MAGMA_SNAIL.get(), 0.07F)` | `.expand(CritterEntities.MAGMA_SNAIL.get(), 0.07F)` |
| 56 | `.expand(TEAnimals.HELL_BUTTERFLY.get(), 0.01F)` | `.expand(CritterEntities.HELL_BUTTERFLY.get(), 0.01F)` |
| 57 | `.expand(TEAnimals.MAGMA_SNAIL.get(), 0.01F)` | `.expand(CritterEntities.MAGMA_SNAIL.get(), 0.01F)` |
| 60 | `.expand(TEAnimals.HELL_BUTTERFLY.get(), 0.01F)` | `.expand(CritterEntities.HELL_BUTTERFLY.get(), 0.01F)` |
| 61 | `.expand(TEAnimals.MAGMA_SNAIL.get(), 0.01F)` | `.expand(CritterEntities.MAGMA_SNAIL.get(), 0.01F)` |
| 66 | `.add(TEAnimals.GRUBBY.get(), 8)` | `.add(CritterEntities.GRUBBY.get(), 8)` |
| 67 | `.add(TEAnimals.SLUGGY.get(), 3)` | `.add(CritterEntities.SLUGGY.get(), 3)` |
| 68 | `.add(TEAnimals.GRUBBY.get(), 1) // todo 改成蚜虫` | `.add(CritterEntities.BUGGY.get(), 1)` |

### `common/data/gen/data_map/BugNetEntityToItemSubProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/data_map/BugNetEntityToItemSubProvider.java`
- TE import：`TEAnimals`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 24 | `.add(TEAnimals.GLOWING_SNAIL, BaitItems.GLOWING_SNAIL)` | `.add(CritterEntities.GLOWING_SNAIL, BaitItems.GLOWING_SNAIL)` |
| 25 | `.add(TEAnimals.GRUBBY, BaitItems.GRUBBY)` | `.add(CritterEntities.GRUBBY, BaitItems.GRUBBY)` |
| 26 | `.add(TEAnimals.MAGGOT, BaitItems.MAGGOT)` | `.add(CritterEntities.MAGGOT, BaitItems.MAGGOT)` |
| 27 | `.add(TEAnimals.MAGMA_SNAIL, BaitItems.MAGMA_SNAIL)` | `.add(CritterEntities.MAGMA_SNAIL, BaitItems.MAGMA_SNAIL)` |
| 28 | `.add(TEAnimals.HELL_BUTTERFLY, BaitItems.HELL_BUTTERFLY)` | `.add(CritterEntities.HELL_BUTTERFLY, BaitItems.HELL_BUTTERFLY)` |
| 29 | `.add(TEAnimals.PRISMATIC_LACEWING, BaitItems.PRISMATIC_LACEWING)` | `.add(CritterEntities.PRISMATIC_LACEWING, BaitItems.PRISMATIC_LACEWING)` |
| 30 | `.add(TEAnimals.SLUGGY, BaitItems.SLUGGY)` | `.add(CritterEntities.SLUGGY, BaitItems.SLUGGY)` |
| 31 | `.add(TEAnimals.SNAIL, BaitItems.SNAIL)` | `.add(CritterEntities.BUGGY, BaitItems.BUGGY)` |
| 32 | `.add(TEAnimals.BUTTERFLY, List.of(` | `.add(CritterEntities.STINKBUG, BaitItems.STINKBUG)` |
| 43 | `.add(TEAnimals.DRAGONFLY, List.of(` | `.add(CritterEntities.DRAGONFLY, List.of(` |
| 52 | `.add(TEAnimals.LADYBUG, List.of(` | `.add(CritterEntities.LADYBUG, List.of(` |
| 56 | `.add(TEAnimals.WORM, List.of(` | `.add(CritterEntities.WORM, List.of(` |
| 61 | `.add(TEAnimals.SCORPION, List.of(` | `.add(CritterEntities.SCORPION, List.of(` |
| 65 | `.add(TEAnimals.GRASSHOPPER, List.of(` | `.add(CritterEntities.GRASSHOPPER, List.of(` |

### `common/data/gen/data_map/GamePhase2AttributeModifiersSubProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/data_map/GamePhase2AttributeModifiersSubProvider.java`
- TE import：`TEAnimals`, `TEMonsterEntities`, `TENpcEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 58 | `.add(TEMonsterEntities.BLOOD_ZOMBIE, Map.of(` | `.add(MonsterEntities.BLOOD_ZOMBIE, Map.of(` |
| 62 | `.add(TEMonsterEntities.ANGER_BONES, Map.of(` | `.add(MonsterEntities.ANGER_BONES, Map.of(` |
| 65 | `.add(TEMonsterEntities.SHORT_BONES, Map.of(` | `.add(MonsterEntities.SHORT_BONES, Map.of(` |
| 68 | `.add(TEMonsterEntities.BIG_BONES, Map.of(` | `.add(MonsterEntities.BIG_BONES, Map.of(` |
| 71 | `.add(TEMonsterEntities.BIG_ANGER_BONES, Map.of(` | `.add(MonsterEntities.BIG_ANGER_BONES, Map.of(` |
| 74 | `.add(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES, Map.of(` | `.add(MonsterEntities.BIG_MUSCLE_ANGER_BONES, Map.of(` |
| 77 | `.add(TEMonsterEntities.BIG_HELMET_ANGER_BONES, Map.of(` | `.add(MonsterEntities.BIG_HELMET_ANGER_BONES, Map.of(` |
| 80 | `.add(TEMonsterEntities.ANTLION_SWARMER, Map.of(` | `.add(MonsterEntities.ANTLION_SWARMER, Map.of(` |
| 83 | `.add(TEMonsterEntities.GIANT_ANTLION_SWARMER, Map.of(` | `.add(MonsterEntities.GIANT_ANTLION_SWARMER, Map.of(` |
| 86 | `.add(TEMonsterEntities.LITTLE_HORNET, Map.of(` | `.add(MonsterEntities.LITTLE_HORNET, Map.of(` |
| 90 | `.add(TEMonsterEntities.BLACK_SLIME, Map.of(` | `.add(MonsterEntities.BLACK_SLIME, Map.of(` |
| 94 | `.add(TEMonsterEntities.BLUE_SLIME, Map.of(` | `.add(MonsterEntities.MOTHER_SLIME, Map.of(` |
| 106 | `.add(TEMonsterEntities.GREEN_SLIME, Map.of(` | `.add(MonsterEntities.MOTHER_SLIME, Map.of(` |
| 116 | `.add(TEMonsterEntities.ICE_SLIME, Map.of(` | `.add(MonsterEntities.MOTHER_SLIME, Map.of(` |
| 128 | `.add(TEMonsterEntities.PURPLE_SLIME, Map.of(` | `.add(MonsterEntities.MOTHER_SLIME, Map.of(` |
| 132 | `.add(TEMonsterEntities.RED_SLIME, Map.of(` | `.add(MonsterEntities.BABY_SLIME, Map.of(` |
| 136 | `.add(TEMonsterEntities.YELLOW_SLIME, Map.of(` | `.add(MonsterEntities.SLIMER, Map.of(` |
| 140 | `.add(TEMonsterEntities.DESERT_SLIME, Map.of(` | `.add(MonsterEntities.RED_SLIME, Map.of(` |
| 144 | `.add(TEMonsterEntities.BLOOD_CRAWLER, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 147 | `.add(TEMonsterEntities.PINK_JELLYFISH, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 150 | `.add(TEMonsterEntities.BLUE_JELLYFISH, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 153 | `.add(TEMonsterEntities.CAVE_BAT, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 165 | `.add(TEAnimals.CRAB, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 168 | `.add(TEMonsterEntities.PIRANHA, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 171 | `.add(TEMonsterEntities.CRIMERA, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 174 | `.add(TEMonsterEntities.CURSED_SKULL, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 177 | `.add(TEMonsterEntities.EATER_OF_SOULS, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 180 | `.add(TEMonsterEntities.DARK_CASTER, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 187 | `.add(TEMonsterEntities.DEMON, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 190 | `.add(TEMonsterEntities.DEMON_EYE, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 193 | `.add(TEMonsterEntities.DEVOURER, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 196 | `.add(TEMonsterEntities.DUNGEON_SLIME, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 199 | `.add(TEMonsterEntities.FACE_MONSTER, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 202 | `.add(TEMonsterEntities.FIRE_IMP, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 205 | `.add(TEMonsterEntities.GHOST, Map.of(` | `.add(MonsterEntities.YELLOW_SLIME, Map.of(` |
| 209 | `.add(TEMonsterEntities.CRAWDAD, Map.of(` | `.add(MonsterEntities.DESERT_SLIME, Map.of(` |
| 213 | `.add(TEMonsterEntities.GIANT_SHELLY, Map.of(` | `.add(MonsterEntities.GIANT_SHELLY, Map.of(` |
| 216 | `.add(TEMonsterEntities.GIANT_WORM, Map.of(` | `.add(MonsterEntities.GIANT_WORM, Map.of(` |
| 228 | `.add(TEMonsterEntities.GOBLIN_SCOUT, Map.of(` | `.add(MonsterEntities.GOBLIN_SCOUT, Map.of(` |
| 232 | `.add(TEMonsterEntities.GRANITE_ELEMENTAL, Map.of(` | `.add(MonsterEntities.GRANITE_ELEMENTAL, Map.of(` |
| 236 | `/*.add(TEMonsterEntities.METEOR_HEAD, Map.of(` | `/*.add(MonsterEntities.METEOR_HEAD, Map.of(` |
| 239 | `.add(TEMonsterEntities.HARPY, Map.of(` | `.add(MonsterEntities.HARPY, Map.of(` |
| 242 | `.add(TEMonsterEntities.HELL_BAT, Map.of(` | `.add(MonsterEntities.HELL_BAT, Map.of(` |
| 245 | `.add(TEMonsterEntities.HORNET, Map.of(` | `.add(MonsterEntities.HORNET, Map.of(` |
| 249 | `.add(TEMonsterEntities.ICE_BAT, Map.of(` | `.add(MonsterEntities.ICE_BAT, Map.of(` |
| 253 | `.add(TEMonsterEntities.JUNGLE_BAT, Map.of(` | `.add(MonsterEntities.JUNGLE_BAT, Map.of(` |
| 257 | `.add(TEMonsterEntities.JUNGLE_SLIME, Map.of(` | `.add(MonsterEntities.JUNGLE_SLIME, Map.of(` |
| 260 | `.add(TEMonsterEntities.LAVA_SLIME, Map.of(` | `.add(MonsterEntities.LAVA_SLIME, Map.of(` |
| 263 | `.add(TEMonsterEntities.SNATCHER, Map.of(` | `.add(MonsterEntities.SNATCHER, Map.of(` |
| 266 | `.add(TEMonsterEntities.SNATCHER, Map.of(` | `.add(MonsterEntities.SNATCHER, Map.of(` |
| 270 | `//                .add(TEMonsterEntities., Map.of(` | `//                .add(MonsterEntities., Map.of(` |
| 274 | `.add(TEMonsterEntities.PINK_SLIME, Map.of(` | `.add(MonsterEntities.PINK_SLIME, Map.of(` |
| 278 | `.add(TEMonsterEntities.SPORE_BAT, Map.of(` | `.add(MonsterEntities.SPORE_BAT, Map.of(` |
| 290 | `.add(TEMonsterEntities.SPORE_SKELETON, Map.of(` | `.add(MonsterEntities.SPORE_SKELETON, Map.of(` |
| 294 | `.add(TEMonsterEntities.SPORE_ZOMBIE, Map.of(` | `//                .add(MonsterEntities.SPORE_ZOMBIE, Map.of(` |
| 297 | `.add(TEMonsterEntities.TOMB_CRAWLER, Map.of(` | `.add(MonsterEntities.TOMB_CRAWLER, Map.of(` |
| 301 | `.add(TEMonsterEntities.UNDEAD_VIKING, Map.of(` | `.add(MonsterEntities.UNDEAD_VIKING, Map.of(` |
| 304 | `.add(TEMonsterEntities.VOODOO_DEMON, Map.of(` | `.add(MonsterEntities.VOODOO_DEMON, Map.of(` |
| 307 | `.add(TEMonsterEntities.DRIPPLER, Map.of(` | `.add(MonsterEntities.DRIPPLER, Map.of(` |
| 310 | `.add(TEMonsterEntities.FLYING_FISH, Map.of(` | `.add(MonsterEntities.FLYING_FISH, Map.of(` |
| 322 | `.add(TEMonsterEntities.GOBLIN_ARCHER, Map.of(` | `.add(MonsterEntities.GOBLIN_ARCHER, Map.of(` |
| 326 | `.add(TEMonsterEntities.GOBLIN_PEON, Map.of(` | `.add(MonsterEntities.GOBLIN_PEON, Map.of(` |
| 330 | `.add(TEMonsterEntities.GOBLIN_SORCERER, Map.of(` | `.add(MonsterEntities.GOBLIN_SORCERER, Map.of(` |
| 334 | `.add(TEMonsterEntities.GOBLIN_THIEF, Map.of(` | `.add(MonsterEntities.GOBLIN_THIEF, Map.of(` |
| 337 | `)).add(TEMonsterEntities.GOBLIN_WARRIOR, Map.of(` | `)).add(MonsterEntities.GOBLIN_WARRIOR, Map.of(` |
| 341 | `.add(TEMonsterEntities.MUMMY, Map.of(` | `.add(MonsterEntities.MUMMY, Map.of(` |
| 344 | `.add(TEMonsterEntities.DARK_MUMMY, Map.of(` | `.add(MonsterEntities.DARK_MUMMY, Map.of(` |
| 347 | `.add(TEMonsterEntities.BLOOD_MUMMY, Map.of(` | `.add(MonsterEntities.BLOOD_MUMMY, Map.of(` |
| 350 | `.add(TEMonsterEntities.LIGHT_MUMMY, Map.of(` | `.add(MonsterEntities.LIGHT_MUMMY, Map.of(` |
| 513 | `.add(TENpcEntities.GUIDE, Map.of(` | `.add(NpcEntities.GUIDE, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 516 | `.add(TENpcEntities.DEMOLITIONIST, Map.of(` | `.add(NpcEntities.ARMS_DEALER, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 519 | `.add(TENpcEntities.GOBLIN_TINKERER, Map.of(` | `.add(NpcEntities.PAINTER, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 522 | `.add(TENpcEntities.ARMS_DEALER, Map.of(` | `.add(NpcEntities.DRYAD, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 525 | `.add(TENpcEntities.NURSE, Map.of(` | `.add(NpcEntities.MECHANIC, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 528 | `.add(TENpcEntities.MERCHANT, Map.of(` | `.add(NpcEntities.PARTY_GIRL, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 531 | `.add(TENpcEntities.PAINTER, Map.of(` | `.add(NpcEntities.TRUFFLE, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 534 | `.add(TENpcEntities.ANGLER, Map.of(` | `.add(NpcEntities.WIZARD, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 537 | `.add(TENpcEntities.FEMALE_ANGLER, Map.of(` | `.add(NpcEntities.WIZARD, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 540 | `.add(TENpcEntities.DRYAD, Map.of(` | `.add(NpcEntities.WIZARD, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 543 | `.add(TENpcEntities.DYE_TRADER, Map.of(` | `.add(NpcEntities.WIZARD, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 546 | `.add(TENpcEntities.OLD_MAN, Map.of(` | `.add(NpcEntities.WIZARD, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 549 | `.add(TENpcEntities.MECHANIC, Map.of(` | `.add(NpcEntities.WIZARD, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 552 | `.add(TENpcEntities.TRAVELING_MERCHANT, Map.of(` | `.add(NpcEntities.WIZARD, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 555 | `.add(TENpcEntities.WITCH_DOCTOR, Map.of(` | `.add(NpcEntities.WIZARD, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 558 | `.add(TENpcEntities.PARTY_GIRL, Map.of(` | `.add(NpcEntities.WIZARD, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 561 | `.add(TENpcEntities.CLOTHIER, Map.of(` | `.add(NpcEntities.WIZARD, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 564 | `.add(TENpcEntities.ZOOLOGIST, Map.of(` | `.add(NpcEntities.WIZARD, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 567 | `.add(TENpcEntities.TRUFFLE, Map.of(` | `.add(NpcEntities.WIZARD, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |
| 570 | `.add(TENpcEntities.WIZARD, Map.of(` | `.add(NpcEntities.WIZARD, Map.of(GamePhase.WALL_OF_FLESH, INCREASE_FRIENDLY_CREATURE_HEALTH))` |

### `common/data/gen/data_map/ImmunitySubProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/data_map/ImmunitySubProvider.java`
- TE import：`TEProjectileEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 23 | `.add(TEProjectileEntities.ICE_PILLAR, new ImmunityDataMap(Immunity.Type.STATIC, 20), false)` | `.add(ModEntities.ICE_PILLAR, new ImmunityDataMap(Immunity.Type.STATIC, 20), false)` |
| 24 | `.add(TEProjectileEntities.SLIME_SPIKE, new ImmunityDataMap(Immunity.Type.STATIC, 5), false)` | `.add(ModEntities.SLIME_SPIKE, new ImmunityDataMap(Immunity.Type.STATIC, 5), false)` |
| 25 | `.add(TEProjectileEntities.FIRE_IMP_PROJ, new ImmunityDataMap(Immunity.Type.LOCAL, 1), false)` | `.add(ModEntities.FIRE_IMP_PROJECTILE, new ImmunityDataMap(Immunity.Type.LOCAL, 1), false)` |
| 26 | `.add(TEProjectileEntities.SUMMON_BEE_STICK_PROJ, new ImmunityDataMap(Immunity.Type.LOCAL, 1), false)` | `.add(ModEntities.MAGIC_MISSILE, new ImmunityDataMap(Immunity.Type.STATIC, 7), false)` |

### `common/data/gen/data_map/LivingInvulnerableEffectsSubProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/data_map/LivingInvulnerableEffectsSubProvider.java`
- TE import：`TEEffects`, `TEAnimals`, `TEBossEntities`, `TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 30 | `.add(TEMonsterEntities.ANGER_BONES, MobEffects.POISON)` | `.add(MonsterEntities.ANGER_BONES, MobEffects.POISON)` |
| 31 | `.add(TEMonsterEntities.SHORT_BONES, MobEffects.POISON)` | `.add(MonsterEntities.SHORT_BONES, MobEffects.POISON)` |
| 32 | `.add(TEMonsterEntities.BIG_BONES, MobEffects.POISON)` | `.add(MonsterEntities.BIG_BONES, MobEffects.POISON)` |
| 33 | `.add(TEMonsterEntities.BIG_ANGER_BONES, MobEffects.POISON)` | `.add(MonsterEntities.BIG_ANGER_BONES, MobEffects.POISON)` |
| 34 | `.add(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES, MobEffects.POISON)` | `.add(MonsterEntities.BIG_MUSCLE_ANGER_BONES, MobEffects.POISON)` |
| 35 | `.add(TEMonsterEntities.BIG_HELMET_ANGER_BONES, MobEffects.POISON)` | `.add(MonsterEntities.BIG_HELMET_ANGER_BONES, MobEffects.POISON)` |
| 37 | `.add(TEMonsterEntities.LITTLE_HORNET, MobEffects.POISON, LibEffects.CONFUSED)` | `.add(MonsterEntities.LITTLE_HORNET, MobEffects.POISON, LibEffects.CONFUSED.get())` |
| 38 | `.add(TEMonsterEntities.BLACK_SLIME, MobEffects.POISON)` | `.add(MonsterEntities.BLACK_SLIME, MobEffects.POISON)` |
| 39 | `.add(TEMonsterEntities.BLOOD_CRAWLER, MobEffects.POISON)` | `.add(MonsterEntities.MOTHER_SLIME, MobEffects.POISON)` |
| 40 | `.add(TEMonsterEntities.BLUE_JELLYFISH, LibEffects.CONFUSED)` | `.add(MonsterEntities.BABY_SLIME, MobEffects.POISON)` |
| 41 | `.add(TEMonsterEntities.PINK_JELLYFISH, LibEffects.CONFUSED)` | `.add(MonsterEntities.BLOOD_CRAWLER, MobEffects.POISON)` |
| 42 | `.add(TEMonsterEntities.GREEN_JELLYFISH, LibEffects.CONFUSED)` | `.add(MonsterEntities.BLUE_JELLYFISH, LibEffects.CONFUSED)` |
| 44 | `.add(TEMonsterEntities.BLUE_SLIME, MobEffects.POISON)` | `.add(MonsterEntities.BLUE_SLIME, MobEffects.POISON)` |
| 45 | `.add(TEMonsterEntities.BONE_SERPENT, MobEffects.POISON, LibEffects.CONFUSED, TEEffects.HELLFIRE)` | `.add(MonsterEntities.BONE_SERPENT, MobEffects.POISON, LibEffects.CONFUSED.get(), ModEffects.HELLFIRE.get())` |
| 45 | `.add(TEMonsterEntities.BONE_SERPENT, MobEffects.POISON, LibEffects.CONFUSED, TEEffects.HELLFIRE)` | `.add(MonsterEntities.BONE_SERPENT, MobEffects.POISON, LibEffects.CONFUSED.get(), ModEffects.HELLFIRE.get())` |
| 46 | `.add(TEMonsterEntities.WITHER_BONE_SERPENT, MobEffects.POISON, MobEffects.WITHER, LibEffects.CONFUSED, TEEffects.HELLFIRE)` | `.add(MonsterEntities.WITHER_BONE_SERPENT, MobEffects.POISON, MobEffects.WITHER, LibEffects.CONFUSED.get(), ModEffects.HELLFIRE.get())` |
| 46 | `.add(TEMonsterEntities.WITHER_BONE_SERPENT, MobEffects.POISON, MobEffects.WITHER, LibEffects.CONFUSED, TEEffects.HELLFIRE)` | `.add(MonsterEntities.WITHER_BONE_SERPENT, MobEffects.POISON, MobEffects.WITHER, LibEffects.CONFUSED.get(), ModEffects.HELLFIRE.get())` |
| 48 | `.add(TEMonsterEntities.CRIMERA, LibEffects.CONFUSED)` | `.add(MonsterEntities.CRIMERA, LibEffects.CONFUSED)` |
| 49 | `.add(TEMonsterEntities.CRAWDAD, LibEffects.CONFUSED)` | `.add(MonsterEntities.CRAWDAD, LibEffects.CONFUSED)` |
| 50 | `.add(TEAnimals.CRAB, LibEffects.CONFUSED)` | `.add(CritterEntities.CRAB, LibEffects.CONFUSED)` |
| 51 | `.add(TEMonsterEntities.CURSED_SKULL, MobEffects.POISON, LibEffects.CONFUSED)` | `.add(MonsterEntities.CURSED_SKULL, MobEffects.POISON, LibEffects.CONFUSED.get())` |
| 52 | `.add(TEBossEntities.DUNGEON_GUARDIAN, new AnyHolderSet<>(provider.lookupOrThrow(Registries.MOB_EFFECT)), LivingInvulnerableEffects.Category.HARMFUL)` | `.add(BossEntities.DUNGEON_GUARDIAN, new AnyHolderSet<>(provider.lookupOrThrow(Registries.MOB_EFFECT)), LivingInvulnerableEffects.Category.HARMFUL_EXCEPT_WHIP_TAG)` |
| 53 | `.add(TEMonsterEntities.DUNGEON_SLIME, MobEffects.POISON)` | `.add(MonsterEntities.DUNGEON_SPIRIT, new AnyHolderSet<>(provider.lookupOrThrow(Registries.MOB_EFFECT)), LivingInvulnerableEffects.Category.HARMFUL_EXCEPT_WHIP_TAG)` |
| 54 | `.add(TEMonsterEntities.EATER_OF_SOULS, LibEffects.CONFUSED)` | `.add(MonsterEntities.DUNGEON_SLIME, MobEffects.POISON)` |
| 55 | `.add(TEMonsterEntities.FACE_MONSTER, MobEffects.POISON)` | `.add(MonsterEntities.EATER_OF_SOULS, LibEffects.CONFUSED)` |
| 56 | `.add(TEMonsterEntities.FIRE_IMP, LibEffects.CONFUSED, TEEffects.HELLFIRE)` | `.add(MonsterEntities.FACE_MONSTER, MobEffects.POISON)` |
| 56 | `.add(TEMonsterEntities.FIRE_IMP, LibEffects.CONFUSED, TEEffects.HELLFIRE)` | `.add(MonsterEntities.FACE_MONSTER, MobEffects.POISON)` |
| 58 | `.add(TEMonsterEntities.GHOST, ModEffects.ACID_VENOM, ModEffects.FROSTBITE, ModEffects.SHADOWFLAME, LibEffects.CONFUSED, ModEffects.ICHOR, MobEffects.POISON, TEEffects.FROST_BURN, TEEffects.HELLFIRE) //TODO 涂油 破晓` | `.add(MonsterEntities.GHOST, ModEffects.ACID_VENOM, ModEffects.FROSTBITE, ModEffects.SHADOWFLAME, LibEffects.CONFUSED, ModEffects.ICHOR, poison, ModEffects.FROST_BURN, ModEffects.HELLFIRE) //TODO 涂油 破晓` |
| 58 | `.add(TEMonsterEntities.GHOST, ModEffects.ACID_VENOM, ModEffects.FROSTBITE, ModEffects.SHADOWFLAME, LibEffects.CONFUSED, ModEffects.ICHOR, MobEffects.POISON, TEEffects.FROST_BURN, TEEffects.HELLFIRE) //TODO 涂油 破晓` | `.add(MonsterEntities.GHOST, ModEffects.ACID_VENOM, ModEffects.FROSTBITE, ModEffects.SHADOWFLAME, LibEffects.CONFUSED, ModEffects.ICHOR, poison, ModEffects.FROST_BURN, ModEffects.HELLFIRE) //TODO 涂油 破晓` |
| 59 | `.add(TEMonsterEntities.GIANT_SHELLY, LibEffects.CONFUSED)` | `.add(MonsterEntities.WRAITH, new AnyHolderSet<>(provider.lookupOrThrow(Registries.MOB_EFFECT)), LivingInvulnerableEffects.Category.HARMFUL_EXCEPT_WHIP_TAG)` |
| 60 | `.add(TEMonsterEntities.GIANT_WORM, LibEffects.CONFUSED)` | `.add(MonsterEntities.GIANT_SHELLY, LibEffects.CONFUSED)` |
| 62 | `.add(TEMonsterEntities.GRANITE_ELEMENTAL, MobEffects.POISON, LibEffects.CONFUSED, TEEffects.HELLFIRE)` | `.add(MonsterEntities.GRANITE_ELEMENTAL, MobEffects.POISON, LibEffects.CONFUSED.get(), ModEffects.HELLFIRE.get())` |
| 62 | `.add(TEMonsterEntities.GRANITE_ELEMENTAL, MobEffects.POISON, LibEffects.CONFUSED, TEEffects.HELLFIRE)` | `.add(MonsterEntities.GRANITE_ELEMENTAL, MobEffects.POISON, LibEffects.CONFUSED.get(), ModEffects.HELLFIRE.get())` |
| 64 | `.add(TEMonsterEntities.GREEN_SLIME, MobEffects.POISON)` | `.add(MonsterEntities.GREEN_SLIME, MobEffects.POISON)` |
| 65 | `.add(TEMonsterEntities.HARPY, MobEffects.POISON)` | `.add(MonsterEntities.HARPY, MobEffects.POISON)` |
| 66 | `.add(TEMonsterEntities.HELL_BAT, TEEffects.HELLFIRE)` | `.add(MonsterEntities.HELL_BAT, ModEffects.HELLFIRE)` |
| 66 | `.add(TEMonsterEntities.HELL_BAT, TEEffects.HELLFIRE)` | `.add(MonsterEntities.HELL_BAT, ModEffects.HELLFIRE)` |
| 68 | `.add(TEMonsterEntities.HORNET, MobEffects.POISON, LibEffects.CONFUSED)` | `.add(MonsterEntities.HORNET, MobEffects.POISON, LibEffects.CONFUSED.get())` |
| 69 | `.add(TEMonsterEntities.ICE_BAT, TEEffects.FROST_BURN, ModEffects.FROSTBITE)` | `.add(MonsterEntities.ICE_BAT, ModEffects.FROST_BURN, ModEffects.FROSTBITE)` |
| 69 | `.add(TEMonsterEntities.ICE_BAT, TEEffects.FROST_BURN, ModEffects.FROSTBITE)` | `.add(MonsterEntities.ICE_BAT, ModEffects.FROST_BURN, ModEffects.FROSTBITE)` |
| 70 | `.add(TEMonsterEntities.ICE_SLIME, TEEffects.FROST_BURN, ModEffects.FROSTBITE, MobEffects.POISON)` | `.add(MonsterEntities.ICE_SLIME, ModEffects.FROST_BURN.get(), ModEffects.FROSTBITE.get(), MobEffects.POISON)` |
| 70 | `.add(TEMonsterEntities.ICE_SLIME, TEEffects.FROST_BURN, ModEffects.FROSTBITE, MobEffects.POISON)` | `.add(MonsterEntities.ICE_SLIME, ModEffects.FROST_BURN.get(), ModEffects.FROSTBITE.get(), MobEffects.POISON)` |
| 71 | `.add(TEMonsterEntities.JUNGLE_SLIME, MobEffects.POISON)` | `.add(MonsterEntities.JUNGLE_SLIME, MobEffects.POISON)` |
| 73 | `.add(TEMonsterEntities.LAVA_SLIME, MobEffects.POISON)` | `.add(MonsterEntities.LAVA_SLIME, MobEffects.POISON)` |
| 74 | `.add(TEMonsterEntities.MAN_EATER, MobEffects.POISON, LibEffects.CONFUSED)` | `.add(MonsterEntities.MAN_EATER, MobEffects.POISON, LibEffects.CONFUSED.get())` |
| 75 | `.add(TEMonsterEntities.METEOR_HEAD, MobEffects.POISON, LibEffects.CONFUSED, TEEffects.HELLFIRE)` | `.add(MonsterEntities.METEOR_HEAD, MobEffects.POISON, LibEffects.CONFUSED.get(), ModEffects.HELLFIRE.get())` |
| 75 | `.add(TEMonsterEntities.METEOR_HEAD, MobEffects.POISON, LibEffects.CONFUSED, TEEffects.HELLFIRE)` | `.add(MonsterEntities.METEOR_HEAD, MobEffects.POISON, LibEffects.CONFUSED.get(), ModEffects.HELLFIRE.get())` |
| 76 | `.add(TEMonsterEntities.PINK_SLIME, MobEffects.POISON)` | `.add(MonsterEntities.PINK_SLIME, MobEffects.POISON)` |
| 77 | `.add(TEMonsterEntities.PIRANHA, LibEffects.CONFUSED)` | `.add(MonsterEntities.PALADIN, LibEffects.CONFUSED)` |
| 78 | `.add(TEMonsterEntities.PURPLE_SLIME, MobEffects.POISON)` | `.add(MonsterEntities.PIRANHA, LibEffects.CONFUSED)` |
| 80 | `.add(TEMonsterEntities.DESERT_SLIME, MobEffects.POISON)` | `.add(MonsterEntities.DESERT_SLIME, MobEffects.POISON)` |
| 81 | `.add(TEMonsterEntities.SHARK, LibEffects.CONFUSED)` | `.add(MonsterEntities.SHARK, LibEffects.CONFUSED)` |
| 83 | `.add(TEMonsterEntities.SNATCHER, LibEffects.CONFUSED)` | `.add(MonsterEntities.SNATCHER, LibEffects.CONFUSED)` |
| 84 | `.add(TEMonsterEntities.SNOW_FLINX, TEEffects.FROST_BURN, ModEffects.FROSTBITE)` | `.add(MonsterEntities.SNOW_FLINX, ModEffects.FROST_BURN, ModEffects.FROSTBITE)` |
| 84 | `.add(TEMonsterEntities.SNOW_FLINX, TEEffects.FROST_BURN, ModEffects.FROSTBITE)` | `.add(MonsterEntities.SNOW_FLINX, ModEffects.FROST_BURN, ModEffects.FROSTBITE)` |
| 85 | `.add(TEMonsterEntities.SPIKED_SLIME, MobEffects.POISON, ModEffects.SHIMMER)` | `.add(MonsterEntities.SPIKED_SLIME, MobEffects.POISON, ModEffects.SHIMMER.get())` |
| 86 | `.add(TEMonsterEntities.SPIKED_JUNGLE_SLIME, MobEffects.POISON)` | `.add(MonsterEntities.SPIKED_JUNGLE_SLIME, MobEffects.POISON)` |
| 87 | `.add(TEMonsterEntities.SPIKED_ICE_SLIME, MobEffects.POISON,TEEffects.FROST_BURN, ModEffects.FROSTBITE)` | `.add(MonsterEntities.SPIKED_ICE_SLIME, MobEffects.POISON, ModEffects.FROST_BURN.get(), ModEffects.FROSTBITE.get())` |
| 87 | `.add(TEMonsterEntities.SPIKED_ICE_SLIME, MobEffects.POISON,TEEffects.FROST_BURN, ModEffects.FROSTBITE)` | `.add(MonsterEntities.SPIKED_ICE_SLIME, MobEffects.POISON, ModEffects.FROST_BURN.get(), ModEffects.FROSTBITE.get())` |
| 88 | `.add(TEMonsterEntities.SPORE_SKELETON, MobEffects.POISON)` | `.add(MonsterEntities.SPORE_SKELETON, MobEffects.POISON)` |
| 90 | `.add(TEMonsterEntities.TOMB_CRAWLER, LibEffects.CONFUSED)` | `.add(MonsterEntities.TOMB_CRAWLER, LibEffects.CONFUSED)` |
| 92 | `.add(TEMonsterEntities.UNDEAD_VIKING, TEEffects.FROST_BURN, ModEffects.FROSTBITE, MobEffects.POISON)` | `.add(MonsterEntities.UNDEAD_VIKING, ModEffects.FROST_BURN.get(), ModEffects.FROSTBITE.get(), MobEffects.POISON)` |
| 92 | `.add(TEMonsterEntities.UNDEAD_VIKING, TEEffects.FROST_BURN, ModEffects.FROSTBITE, MobEffects.POISON)` | `.add(MonsterEntities.UNDEAD_VIKING, ModEffects.FROST_BURN.get(), ModEffects.FROSTBITE.get(), MobEffects.POISON)` |
| 93 | `.add(TEMonsterEntities.VOODOO_DEMON, LibEffects.CONFUSED, ModEffects.SHADOWFLAME, TEEffects.HELLFIRE)` | `.add(MonsterEntities.ARMORED_VIKING, ModEffects.FROST_BURN.get(), ModEffects.FROSTBITE.get(), MobEffects.POISON)` |
| 93 | `.add(TEMonsterEntities.VOODOO_DEMON, LibEffects.CONFUSED, ModEffects.SHADOWFLAME, TEEffects.HELLFIRE)` | `.add(MonsterEntities.ARMORED_VIKING, ModEffects.FROST_BURN.get(), ModEffects.FROSTBITE.get(), MobEffects.POISON)` |
| 95 | `.add(TEMonsterEntities.YELLOW_SLIME, MobEffects.POISON)` | `.add(MonsterEntities.YELLOW_SLIME, MobEffects.POISON)` |
| 97 | `.add(TEMonsterEntities.WOODEN_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE)` | `.add(MonsterEntities.WOODEN_MIMIC, poison_confused_hellfire)` |
| 97 | `.add(TEMonsterEntities.WOODEN_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE)` | `.add(MonsterEntities.WOODEN_MIMIC, poison_confused_hellfire)` |
| 98 | `.add(TEMonsterEntities.GOLDEN_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE)` | `.add(MonsterEntities.GOLDEN_MIMIC, poison_confused_hellfire)` |
| 98 | `.add(TEMonsterEntities.GOLDEN_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE)` | `.add(MonsterEntities.GOLDEN_MIMIC, poison_confused_hellfire)` |
| 99 | `.add(TEMonsterEntities.SHADOW_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE)` | `.add(MonsterEntities.SHADOW_MIMIC, poison_confused_hellfire)` |
| 99 | `.add(TEMonsterEntities.SHADOW_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE)` | `.add(MonsterEntities.SHADOW_MIMIC, poison_confused_hellfire)` |
| 100 | `.add(TEMonsterEntities.CORRUPT_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE)` | `.add(MonsterEntities.CORRUPT_MIMIC, poison_confused_hellfire)` |
| 100 | `.add(TEMonsterEntities.CORRUPT_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE)` | `.add(MonsterEntities.CORRUPT_MIMIC, poison_confused_hellfire)` |
| 101 | `.add(TEMonsterEntities.JUNGLE_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE)` | `.add(MonsterEntities.JUNGLE_MIMIC, poison_confused_hellfire)` |
| 101 | `.add(TEMonsterEntities.JUNGLE_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE)` | `.add(MonsterEntities.JUNGLE_MIMIC, poison_confused_hellfire)` |
| 102 | `.add(TEMonsterEntities.CRIMSON_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE)` | `.add(MonsterEntities.CRIMSON_MIMIC, poison_confused_hellfire)` |
| 102 | `.add(TEMonsterEntities.CRIMSON_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE)` | `.add(MonsterEntities.CRIMSON_MIMIC, poison_confused_hellfire)` |
| 103 | `.add(TEMonsterEntities.HALLOWED_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE)` | `.add(MonsterEntities.HALLOWED_MIMIC, poison_confused_hellfire)` |
| 103 | `.add(TEMonsterEntities.HALLOWED_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE)` | `.add(MonsterEntities.HALLOWED_MIMIC, poison_confused_hellfire)` |
| 104 | `.add(TEMonsterEntities.ICE_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE, TEEffects.FROST_BURN, ModEffects.FROSTBITE)` | `.add(MonsterEntities.ICE_MIMIC, poison, LibEffects.CONFUSED, ModEffects.HELLFIRE, ModEffects.FROST_BURN, ModEffects.FROSTBITE)` |
| 104 | `.add(TEMonsterEntities.ICE_MIMIC, MobEffects.POISON,LibEffects.CONFUSED,TEEffects.HELLFIRE, TEEffects.FROST_BURN, ModEffects.FROSTBITE)` | `.add(MonsterEntities.ICE_MIMIC, poison, LibEffects.CONFUSED, ModEffects.HELLFIRE, ModEffects.FROST_BURN, ModEffects.FROSTBITE)` |
| 105 | `.add(TEMonsterEntities.CORRUPT_SLIME, MobEffects.POISON)` | `.add(MonsterEntities.CORRUPT_SLIME, MobEffects.POISON)` |
| 106 | `.add(TEMonsterEntities.ARAPAIMA, LibEffects.CONFUSED)` | `.add(MonsterEntities.ARAPAIMA, LibEffects.CONFUSED)` |
| 108 | `.add(TEMonsterEntities.CRIMSLIME, MobEffects.POISON)` | `.add(MonsterEntities.CRIMSLIME, MobEffects.POISON)` |
| 115 | `.add(TEMonsterEntities.WYVERN, LibEffects.CONFUSED)` | `.add(MonsterEntities.WYVERN, LibEffects.CONFUSED)` |
| 116 | `.add(TEMonsterEntities.GREEN_DUMPLING_SLIME, MobEffects.POISON)` | `.add(MonsterEntities.GREEN_DUMPLING_SLIME, MobEffects.POISON)` |
| 117 | `.add(TEMonsterEntities.GOLDEN_SLIME, ModEffects.SHIMMER)` | `.add(MonsterEntities.GOLDEN_SLIME, ModEffects.SHIMMER)` |
| 118 | `.add(TEMonsterEntities.HERPLING, LibEffects.CONFUSED)` | `.add(MonsterEntities.GASTROPOD, LibEffects.CONFUSED, poison, ModEffects.BLEEDING)` |
| 119 | `.add(TEMonsterEntities.DERPLING, LibEffects.CONFUSED)` | `.add(MonsterEntities.HERPLING, LibEffects.CONFUSED)` |
| 120 | `.add(TEMonsterEntities.SAND_POACHER, MobEffects.POISON)` | `.add(MonsterEntities.DERPLING, LibEffects.CONFUSED)` |
| 122 | `.add(TEBossEntities.BRAIN_OF_CTHULHU, LibEffects.CONFUSED)` | `.add(BossEntities.BRAIN_OF_CTHULHU, LibEffects.CONFUSED)` |
| 123 | `.add(TEBossEntities.EATER_OF_WORLDS, LibEffects.CONFUSED)` | `.add(BossEntities.EATER_OF_WORLDS, LibEffects.CONFUSED)` |
| 124 | `.add(TEBossEntities.EATER_OF_WORLDS_SEGMENT, LibEffects.CONFUSED)` | `//                .add(BossEntities.EATER_OF_WORLDS_SEGMENT, LibEffects.CONFUSED)` |
| 127 | `.add(TEBossEntities.QUEEN_BEE, MobEffects.POISON, LibEffects.CONFUSED)` | `.add(BossEntities.QUEEN_BEE, MobEffects.POISON, LibEffects.CONFUSED.get())` |
| 129 | `.add(TEBossEntities.HILL_OF_FLESH, LibEffects.CONFUSED, TEEffects.HELLFIRE)` | `.add(BossEntities.HILL_OF_FLESH, LibEffects.CONFUSED, ModEffects.HELLFIRE)` |
| 129 | `.add(TEBossEntities.HILL_OF_FLESH, LibEffects.CONFUSED, TEEffects.HELLFIRE)` | `.add(BossEntities.HILL_OF_FLESH, LibEffects.CONFUSED, ModEffects.HELLFIRE)` |
| 130 | `.add(TEBossEntities.WALL_OF_FLESH, LibEffects.CONFUSED, TEEffects.HELLFIRE)` | `.add(BossEntities.WALL_OF_FLESH, LibEffects.CONFUSED, ModEffects.HELLFIRE)` |
| 130 | `.add(TEBossEntities.WALL_OF_FLESH, LibEffects.CONFUSED, TEEffects.HELLFIRE)` | `.add(BossEntities.WALL_OF_FLESH, LibEffects.CONFUSED, ModEffects.HELLFIRE)` |
| 131 | `.add(TEBossEntities.THE_TWINS, LibEffects.CONFUSED, MobEffects.POISON,ModEffects.BLEEDING,ModEffects.BLOOD_BUTCHERED,ModEffects.TENTACLE_SPIKES)` | `.add(BossEntities.THE_TWINS, LibEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)` |
| 132 | `.add(TEBossEntities.RETINAZER, LibEffects.CONFUSED, MobEffects.POISON,ModEffects.BLEEDING,ModEffects.BLOOD_BUTCHERED,ModEffects.TENTACLE_SPIKES)` | `.add(BossEntities.RETINAZER, LibEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)` |
| 133 | `.add(TEBossEntities.SPAZMATISM, LibEffects.CONFUSED, MobEffects.POISON,ModEffects.BLEEDING,ModEffects.BLOOD_BUTCHERED,ModEffects.TENTACLE_SPIKES)` | `.add(BossEntities.SPAZMATISM, LibEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)` |
| 134 | `.add(TEBossEntities.THE_DESTROYER, LibEffects.CONFUSED, MobEffects.POISON,ModEffects.BLEEDING,ModEffects.BLOOD_BUTCHERED,ModEffects.TENTACLE_SPIKES)` | `.add(BossEntities.THE_DESTROYER, LibEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES, ModEffects.DRYADS_BANE)` |
| 136 | `.add(TEBossEntities.SKELETRON_PRIME, LibEffects.CONFUSED, MobEffects.POISON,ModEffects.BLEEDING,ModEffects.BLOOD_BUTCHERED,ModEffects.TENTACLE_SPIKES)` | `.add(BossEntities.SKELETRON_PRIME, LibEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)` |
| 137 | `.add(TEBossEntities.SKELETRON_PRIME_PART, LibEffects.CONFUSED, MobEffects.POISON,ModEffects.BLEEDING,ModEffects.BLOOD_BUTCHERED,ModEffects.TENTACLE_SPIKES)` | `.add(BossEntities.SKELETRON_PRIME_PART, LibEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)` |
| 138 | `.add(TEBossEntities.PLANTERA, LibEffects.CONFUSED, MobEffects.POISON,ModEffects.BLEEDING,ModEffects.BLOOD_BUTCHERED,ModEffects.TENTACLE_SPIKES)` | `.add(BossEntities.PLANTERA, LibEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)` |
| 139 | `.add(TEBossEntities.PLANTERA_HOOK, LibEffects.CONFUSED, MobEffects.POISON,ModEffects.BLEEDING,ModEffects.BLOOD_BUTCHERED,ModEffects.TENTACLE_SPIKES)` | `.add(BossEntities.PLANTERA_HOOK, LibEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)` |
| 140 | `.add(TEBossEntities.PLANTERA_TENTACLE, LibEffects.CONFUSED, MobEffects.POISON,ModEffects.BLEEDING,ModEffects.BLOOD_BUTCHERED,ModEffects.TENTACLE_SPIKES)` | `.add(BossEntities.PLANTERA_TENTACLE, LibEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)` |
| 142 | `.add(TEMonsterEntities.VISUAL_NEURON, LibEffects.CONFUSED)` | `.add(MonsterEntities.VISUAL_NEURON, LibEffects.CONFUSED)` |
| 143 | `.add(TEMonsterEntities.LEECH, LibEffects.CONFUSED)` | `.add(MonsterEntities.LEECH, LibEffects.CONFUSED)` |
| 144 | `.add(TEMonsterEntities.SERVANT_OF_CTHULHU, ModEffects.SHIMMER, LibEffects.CONFUSED)` | `.add(BossEntities.SERVANT_OF_CTHULHU, ModEffects.SHIMMER, LibEffects.CONFUSED)` |
| 145 | `.add(TEMonsterEntities.THE_HUNGRY, ModEffects.SHIMMER, LibEffects.CONFUSED)` | `.add(MonsterEntities.THE_HUNGRY, ModEffects.SHIMMER, LibEffects.CONFUSED)` |

### `common/data/gen/data_map/TreasureBagSubProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/data_map/TreasureBagSubProvider.java`
- TE import：`TEBossEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 16 | `.add(TEBossEntities.EATER_OF_WORLDS, new TreasureBagDrop(TreasureBagItems.EATER_OF_WORLDS_TREASURE_BAG.get()), false)` | `.add(BossEntities.EATER_OF_WORLDS, new TreasureBagDrop(TreasureBagItems.EATER_OF_WORLDS_TREASURE_BAG.get()), false)` |
| 17 | `.add(TEBossEntities.BRAIN_OF_CTHULHU, new TreasureBagDrop(TreasureBagItems.BRAIN_OF_CTHULHU_TREASURE_BAG.get()), false)` | `.add(BossEntities.BRAIN_OF_CTHULHU, new TreasureBagDrop(TreasureBagItems.BRAIN_OF_CTHULHU_TREASURE_BAG.get()), false)` |
| 18 | `.add(TEBossEntities.QUEEN_BEE, new TreasureBagDrop(TreasureBagItems.QUEEN_BEE_TREASURE_BAG.get()), false)` | `.add(BossEntities.QUEEN_BEE, new TreasureBagDrop(TreasureBagItems.QUEEN_BEE_TREASURE_BAG.get()), false)` |
| 20 | `.add(TEBossEntities.SKELETRON, new TreasureBagDrop(TreasureBagItems.SKELETRON_TREASURE_BAG.get()), false)` | `.add(BossEntities.SKELETRON, new TreasureBagDrop(TreasureBagItems.SKELETRON_TREASURE_BAG.get()), false)` |
| 22 | `.add(TEBossEntities.WALL_OF_FLESH, new TreasureBagDrop(TreasureBagItems.WALL_OF_FLESH_TREASURE_BAG.get()), false)` | `.add(BossEntities.HILL_OF_FLESH, new TreasureBagDrop(TreasureBagItems.HILL_OF_FLESH_TREASURE_BAG.get()), false)` |
| 23 | `.add(TEBossEntities.HILL_OF_FLESH, new TreasureBagDrop(TreasureBagItems.HILL_OF_FLESH_TREASURE_BAG.get()), false)` | `.add(BossEntities.THE_TWINS, new TreasureBagDrop(TreasureBagItems.THE_TWINS_TREASURE_BAG.get()), false)` |
| 24 | `.add(TEBossEntities.THE_TWINS, new TreasureBagDrop(TreasureBagItems.THE_TWINS_TREASURE_BAG.get()), false)` | `.add(BossEntities.SKELETRON_PRIME, new TreasureBagDrop(TreasureBagItems.SKELETRON_PRIME_TREASURE_BAG.get()), false)` |
| 25 | `.add(TEBossEntities.SKELETRON_PRIME, new TreasureBagDrop(TreasureBagItems.SKELETRON_PRIME_TREASURE_BAG.get()), false);` | `.add(BossEntities.THE_DESTROYER, new TreasureBagDrop(TreasureBagItems.THE_DESTROYER_TREASURE_BAG.get()), false)` |

### `common/data/gen/loot/ChestSubProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/loot/ChestSubProvider.java`
- TE import：`TEBoomerangItems`, `TESummonItems`, `TEYoyosItems`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 58 | `.add(LootItem.lootTableItem(TEBoomerangItems.WOOD_BOOMERANG))` | `.add(LootItem.lootTableItem(BoomerangItems.WOOD_BOOMERANG))` |
| 72 | `.add(LootItem.lootTableItem(TEBoomerangItems.WOOD_BOOMERANG))` | `.add(LootItem.lootTableItem(BoomerangItems.WOOD_BOOMERANG))` |
| 86 | `.add(LootItem.lootTableItem(TESummonItems.FINCH_STAFF))` | `.add(LootItem.lootTableItem(SummonItems.FINCH_STAFF))` |
| 126 | `.add(LootItem.lootTableItem(TEBoomerangItems.ICE_BOOMERANG).setWeight(15))` | `.add(LootItem.lootTableItem(BoomerangItems.ICE_BOOMERANG).setWeight(15))` |
| 142 | `.add(LootItem.lootTableItem(TEBoomerangItems.ICE_BOOMERANG).setWeight(15))` | `.add(LootItem.lootTableItem(BoomerangItems.ICE_BOOMERANG).setWeight(15))` |
| 396 | `.add(LootItem.lootTableItem(TEYoyosItems.VALOR))` | `.add(LootItem.lootTableItem(YoyoItems.VALOR))` |

### `common/data/gen/loot/EntitySubProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/loot/EntitySubProvider.java`
- TE import：`TEEntities`, `TEAnimals`, `TEBossEntities`, `TEMonsterEntities`, `TENpcEntities`, `TEBoomerangItems`, `TEPetItems`, `TESummonItems`, `TEYoyosItems`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 81 | `add(TEBossEntities.EATER_OF_WORLDS.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/eater_of_worlds"), LootTable.lootTable()` | `add(BossEntities.EATER_OF_WORLDS.get(), LootTable.lootTable()` |
| 92 | `add(TEBossEntities.EATER_OF_WORLDS_SEGMENT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/eater_of_worlds_segment"), LootTable.lootTable()` | `add(BossEntities.THE_DESTROYER_PROBE.get(), LootTable.lootTable());` |
| 103 | `add(TEMonsterEntities.VISUAL_NEURON.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/visual_neuron"), LootTable.lootTable()` | `add(MonsterEntities.VISUAL_NEURON.get(), LootTable.lootTable()` |
| 114 | `add(TEMonsterEntities.GOBLIN_SCOUT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/goblin_scout"), LootTable.lootTable()` | `add(MonsterEntities.GOBLIN_SCOUT.get(), LootTable.lootTable()` |
| 119 | `add(TEMonsterEntities.ANTLION_SWARMER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/antlion_swarmer"), LootTable.lootTable()` | `// 丛林蜘蛛 无掉落物（仅钱币），掉落表已齐全` |
| 133 | `add(TEMonsterEntities.GIANT_ANTLION_SWARMER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/giant_antlion_swarmer"), LootTable.lootTable()` | `add(MonsterEntities.GIANT_ANTLION_SWARMER.get(), LootTable.lootTable()` |
| 147 | `add(TEMonsterEntities.ANGER_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/anger_bones"), LootTable.lootTable()` | `add(MonsterEntities.ANGER_BONES.get(), LootTable.lootTable()` |
| 161 | `add(TEMonsterEntities.BIG_ANGER_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/big_anger_bones"), LootTable.lootTable()` | `add(MonsterEntities.BIG_ANGER_BONES.get(), LootTable.lootTable()` |
| 175 | `add(TEMonsterEntities.BIG_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/big_bones"), LootTable.lootTable()` | `add(MonsterEntities.BIG_BONES.get(), LootTable.lootTable()` |
| 189 | `add(TEMonsterEntities.BIG_HELMET_ANGER_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/big_helmet_anger_bones"), LootTable.lootTable()` | `add(MonsterEntities.BIG_HELMET_ANGER_BONES.get(), LootTable.lootTable()` |
| 203 | `add(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/big_muscle_anger_bones"), LootTable.lootTable()` | `add(MonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), LootTable.lootTable()` |
| 217 | `add(TEMonsterEntities.SHORT_BONES.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/short_bones"), LootTable.lootTable()` | `add(MonsterEntities.SHORT_BONES.get(), LootTable.lootTable()` |
| 231 | `add(TEMonsterEntities.DARK_CASTER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/dark_caster"), LootTable.lootTable()` | `// 水矢怪 掉落物已齐全（水矢）` |
| 245 | `add(TEMonsterEntities.CURSED_SKULL.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/cursed_skull"), LootTable.lootTable()` | `add(MonsterEntities.CURSED_SKULL.get(), LootTable.lootTable()` |
| 263 | `add(TEMonsterEntities.BLOOD_CRAWLER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/blood_crawler"), LootTable.lootTable()` | `add(MonsterEntities.BLOOD_CRAWLER.get(), LootTable.lootTable()` |
| 273 | `add(TEMonsterEntities.BLOOD_ZOMBIE.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/blood_zombie"), LootTable.lootTable()` | `add(MonsterEntities.BLOOD_ZOMBIE.get(), LootTable.lootTable()` |
| 283 | `.add(LootItem.lootTableItem(TEPetItems.WALLET.get()).setWeight(5))` | `.add(LootItem.lootTableItem(PetItems.MONEY_TROUGH.get()).setWeight(5))` |
| 287 | `add(TEMonsterEntities.DRIPPLER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/drippler"), LootTable.lootTable()` | `// 僵尸新娘 缺少未加入物品：婚纱(Wedding Dress, 100%)` |
| 297 | `.add(LootItem.lootTableItem(TEPetItems.WALLET.get()).setWeight(5))` | `.add(LootItem.lootTableItem(PetItems.MONEY_TROUGH.get()).setWeight(5))` |
| 301 | `add(TEMonsterEntities.BLOODY_SPORE.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/bloody_spore"), LootTable.lootTable()` | `add(MonsterEntities.BLOODY_SPORE.get(), LootTable.lootTable()` |
| 314 | `add(TEMonsterEntities.CAVE_BAT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/cave_bat"), batCommon()` | `add(MonsterEntities.CAVE_BAT.get(), batCommon()` |
| 316 | `add(TEMonsterEntities.SPORE_BAT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/spore_bat"), batCommon()` | `add(MonsterEntities.GIANT_BAT.get(), batCommon()` |
| 318 | `.add(LootItem.lootTableItem(TEBoomerangItems.SHROOMERANG).setWeight(5).setQuality(1))` | `.add(LootItem.lootTableItem(BoomerangItems.SHROOMERANG).setWeight(5).setQuality(1))` |
| 322 | `add(TEMonsterEntities.SPORE_ZOMBIE.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/spore_zombie"), LootTable.lootTable()` | `add(MonsterEntities.SPORE_ZOMBIE.get(), LootTable.lootTable()` |
| 332 | `add(TEMonsterEntities.HAT_SPORE_ZOMBIE.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/hat_spore_zombie"), LootTable.lootTable()` | `add(MonsterEntities.HAT_SPORE_ZOMBIE.get(), LootTable.lootTable()` |
| 342 | `add(TEMonsterEntities.SPORE_SKELETON.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/spore_skeleton"), LootTable.lootTable()` | `add(MonsterEntities.ZOMBIE.get(), LootTable.lootTable()` |
| 355 | `add(TEMonsterEntities.UNDEAD_VIKING.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/undead_viking"), LootTable.lootTable()` | `add(MonsterEntities.UNDEAD_VIKING.get(), LootTable.lootTable()` |
| 372 | `add(TEMonsterEntities.CRIMERA.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/crimera"), LootTable.lootTable()` | `// 冰雪巨人 缺少未加入物品：冰雪羽(Ice Feather, 33.3%)` |
| 386 | `add(TEMonsterEntities.FACE_MONSTER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/face_monster"), LootTable.lootTable()` | `add(MonsterEntities.FACE_MONSTER.get(), LootTable.lootTable()` |
| 396 | `add(TEMonsterEntities.DECAYEDER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/decayeder"), LootTable.lootTable()` | `add(MonsterEntities.DECAYEDER.get(), LootTable.lootTable()` |
| 409 | `add(TEMonsterEntities.DEMON_EYE.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/demon_eye"), LootTable.lootTable()` | `add(MonsterEntities.DEMON_EYE.get(), LootTable.lootTable()` |
| 419 | `add(TEMonsterEntities.DEVOURER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/devourer"), LootTable.lootTable()` | `// 僵尸鱼人 缺少未加入物品：血雨弓(Blood Rain Bow, 12.5%)、钱币槽(Money Trough, 6.67%)、鱼饵桶(Chum Bucket, 50%)` |
| 432 | `add(TEMonsterEntities.DUNGEON_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/dungeon_slime"), LootTable.lootTable()` | `add(MonsterEntities.DUNGEON_SLIME.get(), LootTable.lootTable()` |
| 437 | `add(TEMonsterEntities.HONEY_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/honey_slime"), LootTable.lootTable()` | `add(MonsterEntities.DUNGEON_SPIRIT.get(), LootTable.lootTable()` |
| 442 | `add(TEMonsterEntities.GOLDEN_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/golden_slime"), LootTable.lootTable()` | `add(MonsterEntities.GOLDEN_SLIME.get(), LootTable.lootTable()` |
| 447 | `add(TEMonsterEntities.NYMPH.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/nymph"), LootTable.lootTable()` | `add(MonsterEntities.NYMPH.get(), LootTable.lootTable()` |
| 452 | `add(TEMonsterEntities.SNATCHER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/snatcher"), LootTable.lootTable()` | `add(MonsterEntities.SNATCHER.get(), LootTable.lootTable()` |
| 458 | `add(TEMonsterEntities.MAN_EATER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/man_eater"), LootTable.lootTable()` | `// 真菌球怪 / 巨型真菌球怪 无掉落物（仅钱币），掉落表已齐全` |
| 468 | `add(TEMonsterEntities.FLYING_FISH.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/flying_fish"), LootTable.lootTable()` | `add(MonsterEntities.FLYING_FISH.get(), LootTable.lootTable()` |
| 474 | `add(TEMonsterEntities.EATER_OF_SOULS.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/eater_of_souls"), LootTable.lootTable()` | `add(MonsterEntities.EATER_OF_SOULS.get(), LootTable.lootTable()` |
| 492 | `add(TEMonsterEntities.GIANT_SHELLY.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/giant_shelly"), LootTable.lootTable()` | `add(MonsterEntities.GIANT_SHELLY.get(), LootTable.lootTable()` |
| 506 | `.add(LootItem.lootTableItem(TEYoyosItems.RALLY).setWeight(667))` | `.add(LootItem.lootTableItem(YoyoItems.RALLY).setWeight(667))` |
| 510 | `add(TEMonsterEntities.CRAWDAD.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/crawdad"), LootTable.lootTable()` | `add(MonsterEntities.CRAWDAD.get(), LootTable.lootTable()` |
| 524 | `.add(LootItem.lootTableItem(TEYoyosItems.RALLY).setWeight(667))` | `.add(LootItem.lootTableItem(YoyoItems.RALLY).setWeight(667))` |
| 528 | `add(TEMonsterEntities.GIANT_WORM.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/giant_worm"), LootTable.lootTable()` | `add(MonsterEntities.GIANT_WORM.get(), LootTable.lootTable()` |
| 534 | `add(TEMonsterEntities.HARPY.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/harpy"), LootTable.lootTable()` | `// 挖掘怪 缺少未加入物品：怪物肉(Monster Meat, 0.07%)、可疑苹果(Suspicious Looking Apple, 40%)` |
| 548 | `add(TEMonsterEntities.HELL_BAT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/hell_bat"), batCommon()` | `add(MonsterEntities.HELL_BAT.get(), batCommon()` |
| 554 | `.add(LootItem.lootTableItem(TEYoyosItems.CASCADE))` | `.add(LootItem.lootTableItem(YoyoItems.CASCADE))` |
| 558 | `add(TEMonsterEntities.FIRE_IMP.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/fire_imp"), LootTable.lootTable()` | `add(MonsterEntities.FIRE_IMP.get(), LootTable.lootTable()` |
| 564 | `.add(LootItem.lootTableItem(TEYoyosItems.CASCADE))` | `.add(LootItem.lootTableItem(YoyoItems.CASCADE))` |
| 568 | `add(TEMonsterEntities.DEMON.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/demon"), LootTable.lootTable()` | `// 愤怒翻滚怪 掉落物已齐全（玉米片）` |
| 570 | `.add(LootItem.lootTableItem(TEYoyosItems.CASCADE))` | `.add(LootItem.lootTableItem(YoyoItems.CASCADE))` |
| 578 | `add(TEMonsterEntities.VOODOO_DEMON.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/voodoo_demon"), LootTable.lootTable()` | `add(MonsterEntities.VOODOO_DEMON.get(), LootTable.lootTable()` |
| 583 | `.add(LootItem.lootTableItem(TEYoyosItems.CASCADE))` | `.add(LootItem.lootTableItem(YoyoItems.CASCADE))` |
| 591 | `add(TEMonsterEntities.HORNET.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/hornet"), LootTable.lootTable()` | `// 青苔黄蜂 缺少未加入物品：破碎蜂翼(Tattered Bee Wing, 1%)（蜂刺、牛黄已实现）` |
| 600 | `add(TEMonsterEntities.ICE_BAT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/ice_bat"), batCommon()` | `add(MonsterEntities.ICE_BAT.get(), batCommon()` |
| 606 | `add(TEMonsterEntities.SNOW_FLINX.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/snow_flinx"), LootTable.lootTable()` | `add(MonsterEntities.SNOW_FLINX.get(), LootTable.lootTable()` |
| 615 | `add(TEMonsterEntities.JUNGLE_BAT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/jungle_bat"), batCommon()` | `add(MonsterEntities.JUNGLE_BAT.get(), batCommon()` |
| 617 | `add(TEMonsterEntities.PIRANHA.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/piranha"), LootTable.lootTable()` | `add(MonsterEntities.PIRANHA.get(), LootTable.lootTable()` |
| 627 | `add(TEMonsterEntities.SHARK.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/shark"), LootTable.lootTable()` | `// 腐化金鱼 / 毒金鱼 无掉落物（仅钱币），掉落表已齐全` |
| 637 | `add(TEMonsterEntities.TOMB_CRAWLER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/tomb_crawler"), LootTable.lootTable()` | `add(MonsterEntities.TOMB_CRAWLER.get(), LootTable.lootTable()` |
| 642 | `add(TEMonsterEntities.BONE_SERPENT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/bone_serpent"), LootTable.lootTable()` | `add(MonsterEntities.BONE_SERPENT.get(), LootTable.lootTable()` |
| 651 | `add(TEMonsterEntities.WITHER_BONE_SERPENT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/wither_bone_serpent"), LootTable.lootTable()` | `add(MonsterEntities.WITHER_BONE_SERPENT.get(), LootTable.lootTable()` |
| 663 | `add(TEMonsterEntities.ANGER_GOBLIN.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/anger_goblin"), goblinCommon()` | `add(MonsterEntities.ANGER_GOBLIN.get(), goblinCommon());` |
| 665 | `add(TEMonsterEntities.GOBLIN_ARCHER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/goblin_archer"), goblinCommon()` | `add(MonsterEntities.GOBLIN_PEON.get(), goblinCommon());` |
| 667 | `add(TEMonsterEntities.GOBLIN_PEON.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/goblin_peon"), goblinCommon()` | `// 哥布林术士 缺少未加入物品：暗影焰弓(Shadowflame Bow, 33.3%)、暗影焰巫术娃娃(Shadowflame Hex Doll, 33.3%)、暗影焰刀(Shadowflame Knife, 33.3%)` |
| 669 | `add(TEMonsterEntities.GOBLIN_SORCERER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/goblin_sorcerer"), goblinCommon()` | `add(MonsterEntities.SHADOWFLAME_APPARITION.get(), LootTable.lootTable());` |
| 671 | `add(TEMonsterEntities.GOBLIN_THIEF.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/goblin_thief"), goblinCommon()` | `add(MonsterEntities.GNOME.get(), LootTable.lootTable());` |
| 673 | `add(TEMonsterEntities.GOBLIN_WARRIOR.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/goblin_warrior"), goblinCommon()` | `add(MonsterEntities.GOBLIN_WARRIOR.get(), goblinCommon());` |
| 675 | `add(TENpcEntities.MECHANIC.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/mechanic"), LootTable.lootTable()` | `add(MonsterEntities.PIRATE_DECKHAND.get(), pirateCommon(1));` |
| 677 | `.add(LootItem.lootTableItem(TEBoomerangItems.COMBAT_WRENCH))` | `.add(LootItem.lootTableItem(BoomerangItems.COMBAT_WRENCH))` |
| 681 | `add(TENpcEntities.DYE_TRADER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/dye_trader"), LootTable.lootTable()` | `add(NpcEntities.STYLIST.get(), LootTable.lootTable()` |
| 687 | `add(TENpcEntities.TRAVELING_MERCHANT.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/traveling_merchant"), LootTable.lootTable()` | `add(NpcEntities.TRAVELING_MERCHANT.get(), LootTable.lootTable()` |
| 692 | `add(TENpcEntities.CLOTHIER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/clothier"), LootTable.lootTable()` | `add(NpcEntities.CLOTHIER.get(), LootTable.lootTable()` |
| 697 | `add(TEAnimals.DUCK.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/duck"), LootTable.lootTable()` | `add(CritterEntities.CLOUD_SHEEP.get(), LootTable.lootTable()` |
| 703 | `add(TEAnimals.BIRD.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/bird"), LootTable.lootTable()` | `add(CritterEntities.BIRD.get(), LootTable.lootTable()` |
| 709 | `add(TEAnimals.BLUE_JAY.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/blue_jay"), LootTable.lootTable()` | `add(CritterEntities.BLUE_JAY.get(), LootTable.lootTable()` |
| 715 | `add(TEAnimals.SQUIRREL.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/squirrel"), LootTable.lootTable()` | `add(CritterEntities.SQUIRREL.get(), LootTable.lootTable()` |
| 721 | `add(TEAnimals.CARDINAL.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/cardinal"), LootTable.lootTable()` | `add(CritterEntities.RED_SQUIRREL.get(), LootTable.lootTable()` |
| 727 | `add(TEAnimals.BUNNY.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/bunny"), LootTable.lootTable()` | `add(CritterEntities.BUNNY.get(), LootTable.lootTable()` |
| 733 | `add(TEAnimals.CRAB.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/crab"), LootTable.lootTable()` | `add(CritterEntities.EXPLOSIVE_BUNNY.get(), LootTable.lootTable()` |
| 739 | `add(TEMonsterEntities.GRANITE_ELEMENTAL.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/granite_elemental"), LootTable.lootTable()` | `add(CritterEntities.HOSTILE_BUNNY.get(), LootTable.lootTable());` |
| 748 | `add(TEMonsterEntities.METEOR_HEAD.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/meteor_head"), LootTable.lootTable()` | `// 装甲步兵 缺少未加入物品：压力球(Stress Ball, 1%)、角斗士胸甲(Gladiator Breastplate, 4.76%)（标枪、角斗士头盔/护腿、短剑、钩爪、披萨已实现）` |
| 754 | `add(TEMonsterEntities.BLUE_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/blue_slime"), slimeCommon(-10644993));` | `add(MonsterEntities.BLUE_SLIME.get(), slimeCommon(-10644993));` |
| 755 | `add(TEMonsterEntities.DESERT_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/desert_slime"), slimeCommon(-2727));` | `add(MonsterEntities.DESERT_SLIME.get(), slimeCommon(-2727));` |
| 756 | `add(TEMonsterEntities.GREEN_DUMPLING_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/green_dumpling_slime"), slimeCommon(-8470674)` | `add(MonsterEntities.GREEN_DUMPLING_SLIME.get(), slimeCommon(-8470674)` |
| 763 | `add(TEMonsterEntities.GREEN_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/green_slime"), slimeCommon(-8470674));` | `add(MonsterEntities.GREEN_SLIME.get(), slimeCommon(-8470674));` |
| 764 | `add(TEMonsterEntities.PURPLE_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/purple_slime"), slimeCommon(-6326333));` | `add(MonsterEntities.PURPLE_SLIME.get(), slimeCommon(-6326333));` |
| 765 | `add(TEMonsterEntities.RED_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/red_slime"), slimeCommon(-1079407));` | `add(MonsterEntities.RED_SLIME.get(), slimeCommon(-1079407));` |
| 766 | `add(TEMonsterEntities.YELLOW_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/yellow_slime"), slimeCommon(-871089));` | `add(MonsterEntities.YELLOW_SLIME.get(), slimeCommon(-871089));` |
| 767 | `add(TEMonsterEntities.JUNGLE_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/jungle_slime"), slimeCommon(-6570130));` | `add(MonsterEntities.SLIMELING.get(), corruptionSlimeLoot(-6522185));` |
| 768 | `add(TEMonsterEntities.ICE_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/ice_slime"), slimeCommon(-10628609)` | `add(MonsterEntities.JUNGLE_SLIME.get(), slimeCommon(-6570130));` |
| 802 | `add(TEMonsterEntities.BLACK_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/black_slime"), slimeCommon(-7697782)` | `add(MonsterEntities.LAVA_SLIME.get(), lavaSlimeLoot());` |
| 808 | `add(TEMonsterEntities.TROPIC_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/tropic_slime"), slimeCommon(-10644993)` | `add(MonsterEntities.TROPIC_SLIME.get(), slimeCommon(-10644993)` |
| 814 | `add(TEMonsterEntities.PINK_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/pink_slime"), LootTable.lootTable()` | `add(MonsterEntities.PINK_SLIME.get(), LootTable.lootTable()` |
| 820 | `.add(LootItem.lootTableItem(TESummonItems.SLIME_STAFF).setQuality(1))` | `.add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).setQuality(1))` |
| 832 | `add(TEMonsterEntities.SWAMP_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/swamp_slime"), LootTable.lootTable()` | `add(MonsterEntities.SWAMP_SLIME.get(), LootTable.lootTable()` |
| 838 | `.add(LootItem.lootTableItem(TESummonItems.SLIME_STAFF).setQuality(1))` | `.add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).setQuality(1))` |
| 851 | `add(TEMonsterEntities.SPIKED_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/spiked_slime"), slimeCommon(-10644993)` | `add(MonsterEntities.SPIKED_SLIME.get(), slimeCommon(-10644993)` |
| 853 | `add(TEMonsterEntities.SPIKED_ICE_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/spiked_ice_slime"), slimeCommon(-10628609)` | `add(MonsterEntities.SPIKED_ICE_SLIME.get(), slimeCommon(-10628609)` |
| 860 | `add(TEMonsterEntities.SPIKED_JUNGLE_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/spiked_jungle_slime"), slimeCommon(-6570130)` | `add(MonsterEntities.SPIKED_JUNGLE_SLIME.get(), slimeCommon(-6570130)` |
| 867 | `add(TEMonsterEntities.BLUE_JELLYFISH.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/blue_jellyfish"), LootTable.lootTable()` | `add(MonsterEntities.BLUE_JELLYFISH.get(), LootTable.lootTable()` |
| 873 | `add(TEMonsterEntities.PINK_JELLYFISH.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/pink_jellyfish"), LootTable.lootTable()` | `add(MonsterEntities.PINK_JELLYFISH.get(), LootTable.lootTable()` |
| 880 | `add(TEMonsterEntities.WYVERN.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/wyvern"), LootTable.lootTable()` | `add(MonsterEntities.WYVERN.get(), LootTable.lootTable()` |
| 888 | `add(TEMonsterEntities.PIXIE.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/pixie"), LootTable.lootTable()` | `add(MonsterEntities.PIXIE.get(), LootTable.lootTable()` |
| 901 | `add(TEMonsterEntities.WRAITH.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/wraith"), LootTable.lootTable()` | `add(MonsterEntities.WRAITH.get(), LootTable.lootTable()` |
| 907 | `add(TEMonsterEntities.GREEN_JELLYFISH.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/green_jellyfish"), LootTable.lootTable()` | `// 血水母 缺少未加入物品：怪物肉(Monster Meat, 0.07%)` |
| 917 | `add(TEMonsterEntities.LUMINOUS_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/luminous_slime"), LootTable.lootTable()` | `add(MonsterEntities.LUMINOUS_SLIME.get(), LootTable.lootTable()` |
| 919 | `.add(LootItem.lootTableItem(TESummonItems.SLIME_STAFF).setQuality(14))` | `.add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).setQuality(14))` |
| 933 | `add(TEMonsterEntities.CRIMSLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/crimslime"), LootTable.lootTable()` | `add(MonsterEntities.CRIMSLIME.get(), corruptionSlimeLoot(-3386287));` |
| 945 | `add(TEMonsterEntities.CORRUPT_SLIME.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/corrupt_slime"), LootTable.lootTable()` | `add(MonsterEntities.GOLDEN_MIMIC.get(), mimicCommon()` |
| 958 | `add(TEMonsterEntities.WOODEN_MIMIC.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/wooden_mimic"), mimicCommon()` | `add(MonsterEntities.SHADOW_MIMIC.get(), mimicCommon()` |
| 960 | `add(TEMonsterEntities.GOLDEN_MIMIC.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/golden_mimic"), mimicCommon()` | `add(MonsterEntities.ICE_MIMIC.get(), LootTable.lootTable()` |
| 962 | `add(TEMonsterEntities.SHADOW_MIMIC.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/shadow_mimic"), mimicCommon()` | `add(MonsterEntities.ICE_MIMIC.get(), LootTable.lootTable()` |
| 964 | `add(TEMonsterEntities.ICE_MIMIC.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/ice_mimic"), LootTable.lootTable()` | `add(MonsterEntities.ICE_MIMIC.get(), LootTable.lootTable()` |
| 975 | `add(TEMonsterEntities.ICE_MIMIC.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/ice_mimic"),LootTable.lootTable()` | `add(MonsterEntities.ICE_MIMIC.get(),LootTable.lootTable()` |
| 981 | `.add(LootItem.lootTableItem(TEBoomerangItems.ICE_BOOMERANG))` | `.add(LootItem.lootTableItem(BoomerangItems.ICE_BOOMERANG))` |
| 990 | `add(TEMonsterEntities.CRIMSON_MIMIC.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/crimson_mimic"), bigMimicCommon()` | `add(MonsterEntities.CRIMSON_MIMIC.get(), bigMimicCommon()` |
| 999 | `add(TEMonsterEntities.CORRUPT_MIMIC.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/corrupt_mimic"), bigMimicCommon()` | `add(MonsterEntities.CORRUPT_MIMIC.get(), bigMimicCommon()` |
| 1008 | `add(TEMonsterEntities.HALLOWED_MIMIC.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/hallowed_mimic"), bigMimicCommon()` | `add(MonsterEntities.HALLOWED_MIMIC.get(), bigMimicCommon()` |
| 1016 | `add(TEMonsterEntities.JUNGLE_MIMIC.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/jungle_mimic"), LootTable.lootTable()` | `add(MonsterEntities.JUNGLE_MIMIC.get(), LootTable.lootTable()` |
| 1054 | `add(TEMonsterEntities.MUMMY.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/mummy"), mummyCommon()` | `add(MonsterEntities.MUMMY.get(), mummyCommon()` |
| 1060 | `add(TEMonsterEntities.DARK_MUMMY.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/dark_mummy"), mummyCommon()` | `add(MonsterEntities.DARK_MUMMY.get(), mummyCommon()` |
| 1074 | `add(TEMonsterEntities.BLOOD_MUMMY.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/blood_mummy"), mummyCommon()` | `add(MonsterEntities.BLOOD_MUMMY.get(), mummyCommon()` |
| 1088 | `add(TEMonsterEntities.LIGHT_MUMMY.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/light_mummy"), mummyCommon()` | `add(MonsterEntities.LIGHT_MUMMY.get(), mummyCommon()` |
| 1098 | `add(TEMonsterEntities.DERPLING.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/derpling"), LootTable.lootTable()` | `add(MonsterEntities.DERPLING.get(), LootTable.lootTable()` |
| 1105 | `add(TEMonsterEntities.GHOUL.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/ghoul"), ghoulCommon()` | `// 蛇蜥怪 缺少未加入物品：远古号角(Ancient Horn, 2%)（坚固化石已实现）` |
| 1107 | `add(TEMonsterEntities.VILE_GHOUL.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/vile_ghoul"), ghoulCommon()` | `add(MonsterEntities.VILE_GHOUL.get(), ghoulCommon()` |
| 1122 | `add(TEMonsterEntities.TAINTED_GHOUL.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/tainted_ghoul"), ghoulCommon()` | `add(MonsterEntities.TAINTED_GHOUL.get(), ghoulCommon()` |
| 1137 | `add(TEMonsterEntities.DREAMER_GHOUL.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/dreamer_ghoul"), ghoulCommon()` | `add(MonsterEntities.DREAMER_GHOUL.get(), ghoulCommon()` |
| 1148 | `add(TEMonsterEntities.DREAMER_GHOUL.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/dreamer_ghoul"), ghoulCommon()` | `add(MonsterEntities.SAND_POACHER.get(), LootTable.lootTable()` |
| 1159 | `add(TEMonsterEntities.SAND_POACHER.get(), Confluence.asResourceKey(Registries.LOOT_TABLE, "entities/terra_entity/sand_poacher"), LootTable.lootTable()` | `add(MonsterEntities.SAND_POACHER.get(), LootTable.lootTable()` |
| 1271 | `.add(LootItem.lootTableItem(TESummonItems.SLIME_STAFF).setQuality(1))` | `.add(LootItem.lootTableItem(SummonItems.SLIME_STAFF).setQuality(1))` |
| 1297 | `TEEntities.getEntities().map(DeferredRegister::getEntries).flatMap(Collection::stream)` | `return ModEntities.getEntities().stream().map(DeferredRegister::getEntries).flatMap(Collection::stream).map(RegistryObject::get);` |

### `common/data/gen/loot/modifiers/AddEntityLootConfluenceSubProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/loot/modifiers/AddEntityLootConfluenceSubProvider.java`
- TE import：`TESummonItems`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 164 | `.add(LootItem.lootTableItem(TESummonItems.SLIME_STAFF).setWeight(1))` | `.add(EmptyLootItem.emptyItem())` |
| 171 | `.add(LootItem.lootTableItem(TESummonItems.SCULK_WISP_STAFF).setWeight(1))` | `.add(EmptyLootItem.emptyItem())` |

### `common/data/gen/recipe/CraftingRecipeProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/recipe/CraftingRecipeProvider.java`
- TE import：`TEItems`, `TEBoomerangItems`, `TEYoyosItems`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 146 | `)), TEItems.HOUSE_DETECTOR.toStack());` | `<无>` |
| 410 | `)), TEYoyosItems.WOODEN_YOYO.toStack());` | `)), YoyoItems.WOODEN_YOYO.toStack());` |
| 615 | `shapeless(output, TEBoomerangItems.TRIMARANG.toStack(), Ingredient.of(TEBoomerangItems.ENCHANTED_BOOMERANG), Ingredient.of(TEBoomerangItems.ICE_BOOMERANG), Ingredient.of(TEBoomerangItems.SHROOMERANG));` | `shapeless(writer, BoomerangItems.TRIMARANG.toStack(), Ingredient.of(BoomerangItems.ENCHANTED_BOOMERANG), Ingredient.of(BoomerangItems.ICE_BOOMERANG), Ingredient.of(BoomerangItems.SHROOMERANG));` |
| 616 | `shapeless(output, TEBoomerangItems.ENCHANTED_BOOMERANG.toStack(), Ingredient.of(TEBoomerangItems.WOOD_BOOMERANG), Ingredient.of(MaterialItems.FALLING_STAR));` | `shapeless(writer, BoomerangItems.ENCHANTED_BOOMERANG.toStack(), Ingredient.of(BoomerangItems.WOOD_BOOMERANG), Ingredient.of(MaterialItems.FALLING_STAR));` |

### `common/data/gen/recipe/HeavyWorkBenchProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/recipe/HeavyWorkBenchProvider.java`
- TE import：`TEBoomerangItems`, `TESummonItems`, `TEWhipItems`, `TEYoyosItems`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 91 | `baseWhip(recipeOutput, AmountIngredient.of(4, Items.BAMBOO), Ingredient.of(Items.BAMBOO_FENCE), TEWhipItems.SLUB_WHIP.toStack());` | `baseWhip(writer, AmountIngredient.of(4, Items.BAMBOO), Ingredient.of(Items.BAMBOO_FENCE), WhipItems.SLUB_WHIP.toStack());` |
| 92 | `baseWhip(recipeOutput, AmountIngredient.of(4, Tags.Items.INGOTS_GOLD), Ingredient.of(DecorativeBlocks.RUBY_CHAIN), TEWhipItems.RUBY_WHIP.toStack());` | `baseWhip(writer, AmountIngredient.of(4, Tags.Items.INGOTS_GOLD), Ingredient.of(DecorativeBlocks.RUBY_CHAIN), WhipItems.RUBY_WHIP.toStack());` |
| 93 | `baseWhip(recipeOutput, AmountIngredient.of(4, MaterialItems.STURDY_FOSSIL), Ingredient.of(DecorativeBlocks.AMBER_CHAIN), TEWhipItems.AMBER_WHIP.toStack());` | `baseWhip(writer, AmountIngredient.of(4, MaterialItems.STURDY_FOSSIL), Ingredient.of(DecorativeBlocks.AMBER_CHAIN), WhipItems.AMBER_WHIP.toStack());` |
| 94 | `baseWhip(recipeOutput, AmountIngredient.of(4, ModTags.Items.INGOTS_TIN), Ingredient.of(DecorativeBlocks.TOPAZ_CHAIN), TEWhipItems.TOPAZ_WHIP.toStack());` | `baseWhip(writer, AmountIngredient.of(4, ModTags.Items.INGOTS_TIN), Ingredient.of(DecorativeBlocks.TOPAZ_CHAIN), WhipItems.TOPAZ_WHIP.toStack());` |
| 95 | `baseWhip(recipeOutput, AmountIngredient.of(4, ModTags.Items.INGOTS_TUNGSTEN), Ingredient.of(DecorativeBlocks.JADE_CHAIN), TEWhipItems.JADE_WHIP.toStack());` | `baseWhip(writer, AmountIngredient.of(4, ModTags.Items.INGOTS_TUNGSTEN), Ingredient.of(DecorativeBlocks.JADE_CHAIN), WhipItems.JADE_WHIP.toStack());` |
| 96 | `baseWhip(recipeOutput, AmountIngredient.of(4, ModTags.Items.INGOTS_PLATINUM), Ingredient.of(DecorativeBlocks.DIAMOND_CHAIN), TEWhipItems.DIAMOND_WHIP.toStack());` | `baseWhip(writer, AmountIngredient.of(4, ModTags.Items.INGOTS_PLATINUM), Ingredient.of(DecorativeBlocks.DIAMOND_CHAIN), WhipItems.DIAMOND_WHIP.toStack());` |
| 97 | `baseWhip(recipeOutput, AmountIngredient.of(4, ModTags.Items.INGOTS_SILVER), Ingredient.of(DecorativeBlocks.SAPPHIRE_CHAIN), TEWhipItems.SAPPHIRE_WHIP.toStack());` | `baseWhip(writer, AmountIngredient.of(4, ModTags.Items.INGOTS_SILVER), Ingredient.of(DecorativeBlocks.SAPPHIRE_CHAIN), WhipItems.SAPPHIRE_WHIP.toStack());` |
| 98 | `baseWhip(recipeOutput, AmountIngredient.of(4, Tags.Items.INGOTS_COPPER), Ingredient.of(DecorativeBlocks.AMETHYST_CHAIN), TEWhipItems.AMETHYST_WHIP.toStack());` | `baseWhip(writer, AmountIngredient.of(4, Tags.Items.INGOTS_COPPER), Ingredient.of(DecorativeBlocks.AMETHYST_CHAIN), WhipItems.AMETHYST_WHIP.toStack());` |
| 99 | `baseWhip(recipeOutput, AmountIngredient.of(4, MaterialItems.SPORE_ROOT), Ingredient.of(ModTags.Items.RAW_MATERIALS_GELSTONE), TEWhipItems.SWAMP_WHIP.toStack());` | `baseWhip(writer, AmountIngredient.of(4, MaterialItems.SPORE_ROOT), Ingredient.of(ModTags.Items.RAW_MATERIALS_GELSTONE), WhipItems.SWAMP_WHIP.toStack());` |
| 355 | `)), TESummonItems.IRON_GOLEM_STAFF.toStack());` | `)), SummonItems.IRON_GOLEM_STAFF.toStack());` |
| 365 | `)), TESummonItems.SNOW_FLINX_STAFF.toStack());` | `)), SummonItems.SNOW_FLINX_STAFF.toStack());` |
| 416 | `)), TESummonItems.IMP_STAFF.toStack());` | `)), SummonItems.IMP_STAFF.toStack());` |
| 429 | `)), TEYoyosItems.MALAISE.toStack());` | `)), YoyoItems.MALAISE.toStack());` |
| 440 | `)), TEYoyosItems.ARTERY.toStack());` | `)), YoyoItems.ARTERY.toStack());` |
| 450 | `)), TEYoyosItems.HIVE_FIVE.toStack());` | `)), YoyoItems.HIVE_FIVE.toStack());` |
| 462 | `)), TEYoyosItems.AMAZON.toStack());` | `)), YoyoItems.AMAZON.toStack());` |
| 814 | `'c', Ingredient.of(TEBoomerangItems.ENCHANTED_BOOMERANG)` | `'c', Ingredient.of(BoomerangItems.ENCHANTED_BOOMERANG)` |
| 820 | `)), TEBoomerangItems.FLAMARANG.toStack());` | `)), BoomerangItems.FLAMARANG.toStack());` |
| 1036 | `)), TESummonItems.HORNET_STAFF.toStack());` | `)), SummonItems.NEW_HORNET_STAFF.toStack());` |
| 1286 | `)), TEWhipItems.SNAPTHORN.toStack());` | `)), WhipItems.SNAPTHORN.toStack());` |
| 1296 | `)), TEWhipItems.SPINAL_TAP.toStack());` | `)), WhipItems.SPINAL_TAP.toStack());` |

### `common/data/gen/recipe/ShimmerTransmutationRecipeProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/recipe/ShimmerTransmutationRecipeProvider.java`
- TE import：`TESummonItems`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 280 | `item(recipeOutput, TESummonItems.FINCH_STAFF, ModItems.LIVING_WOOD_WAND);` | `item(writer, SummonItems.FINCH_STAFF, ModItems.LIVING_WOOD_WAND);` |

### `common/data/gen/tag/ModBlockTagsProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/tag/ModBlockTagsProvider.java`
- TE import：`TETags`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 191 | `tag(TETags.Blocks.HONEY).add(HONEY.get());` | `<无>` |
| 1977 | `tag(TETags.Blocks.NPC_HOUSE_CONSTITUTE).add(` | `tag(ModTags.Blocks.NPC_HOUSE_CONSTITUTE).add(` |

### `common/data/gen/tag/ModDamageTypeTagsProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/tag/ModDamageTypeTagsProvider.java`
- TE import：`TETags`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 29 | `TETags.DamageTypes.FROST_BURN` | `.addOptional(LibDamageTypes.CURSED_INFERNO.location()).addOptional(LibDamageTypes.FROST_BURN.location());` |
| 39 | `tag(DamageTypeTags.BYPASSES_ARMOR).add(TETags.DamageTypes.PASS_ARMOR);` | `.addOptional(LibDamageTypes.CURSED_INFERNO.location()).addOptional(LibDamageTypes.FROST_BURN.location());` |

### `common/data/gen/tag/ModEntityTypeTagsProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/tag/ModEntityTypeTagsProvider.java`
- TE import：`TETags`, `TEAnimals`, `TEMonsterEntities`, `TENpcEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 29 | `TEMonsterEntities.ANGER_BONES.get(),` | `MonsterEntities.ANGER_BONES.get(),` |
| 30 | `TEMonsterEntities.SHORT_BONES.get(),` | `MonsterEntities.SHORT_BONES.get(),` |
| 31 | `TEMonsterEntities.BIG_BONES.get(),` | `MonsterEntities.BIG_BONES.get(),` |
| 32 | `TEMonsterEntities.BIG_ANGER_BONES.get(),` | `MonsterEntities.BIG_ANGER_BONES.get(),` |
| 33 | `TEMonsterEntities.BIG_MUSCLE_ANGER_BONES.get(),` | `MonsterEntities.BIG_MUSCLE_ANGER_BONES.get(),` |
| 34 | `TEMonsterEntities.BIG_HELMET_ANGER_BONES.get()` | `MonsterEntities.BIG_HELMET_ANGER_BONES.get()` |
| 38 | `TEAnimals.MAGMA_SNAIL.get(),` | `CritterEntities.MAGMA_SNAIL.get(),` |
| 39 | `TEAnimals.HELL_BUTTERFLY.get()` | `CritterEntities.HELL_BUTTERFLY.get()` |
| 50 | `TEAnimals.EXPLOSIVE_BUNNY.get(),` | `CritterEntities.EXPLOSIVE_BUNNY.get(),` |
| 51 | `TEAnimals.BUNNY.get(),` | `CritterEntities.BUNNY.get(),` |
| 52 | `TEAnimals.BIRD.get(),` | `CritterEntities.BIRD.get(),` |
| 53 | `TEAnimals.BLUE_JAY.get(),` | `CritterEntities.BLUE_JAY.get(),` |
| 54 | `TEAnimals.CARDINAL.get(),` | `CritterEntities.CARDINAL.get(),` |
| 55 | `TEAnimals.DUCK.get(),` | `CritterEntities.DUCK.get(),` |
| 56 | `TEAnimals.SQUIRREL.get(),` | `CritterEntities.SQUIRREL.get(),` |
| 57 | `TEAnimals.HELL_BUTTERFLY.get(),` | `CritterEntities.HELL_BUTTERFLY.get(),` |
| 58 | `TEAnimals.MAGMA_SNAIL.get(),` | `CritterEntities.MAGMA_SNAIL.get(),` |
| 59 | `TEAnimals.WORM.get(),` | `CritterEntities.WORM.get(),` |
| 60 | `TEAnimals.DRAGONFLY.get(),` | `CritterEntities.DRAGONFLY.get(),` |
| 61 | `TEAnimals.BUTTERFLY.get(),` | `CritterEntities.BUTTERFLY.get(),` |
| 62 | `TEAnimals.GRASSHOPPER.get(),` | `CritterEntities.GRASSHOPPER.get(),` |
| 63 | `TEAnimals.SCORPION.get(),` | `CritterEntities.SCORPION.get(),` |
| 64 | `TEAnimals.SLUGGY.get(),` | `CritterEntities.SLUGGY.get(),` |
| 65 | `TEAnimals.SNAIL.get(),` | `CritterEntities.SNAIL.get(),` |
| 66 | `TEAnimals.GLOWING_SNAIL.get(),` | `CritterEntities.GLOWING_SNAIL.get(),` |
| 67 | `TEAnimals.MAGGOT.get(),` | `CritterEntities.MAGGOT.get(),` |
| 68 | `TEAnimals.PRISMATIC_LACEWING.get(),` | `CritterEntities.PRISMATIC_LACEWING.get(),` |
| 69 | `TEAnimals.FAIRY.get()` | `CritterEntities.FAIRY.get()` |
| 73 | `//.add(TEMonsterEntities.DEMON_EYE.get()); fixme 恶魔之眼白天会飞走` | `.add(MonsterEntities.GHOST.get());` |
| 77 | `TEMonsterEntities.BLUE_SLIME.get(),` | `MonsterEntities.BLUE_SLIME.get(),` |
| 78 | `TEMonsterEntities.GREEN_SLIME.get(),` | `MonsterEntities.GREEN_SLIME.get(),` |
| 79 | `TEMonsterEntities.PINK_SLIME.get(),` | `MonsterEntities.PINK_SLIME.get(),` |
| 80 | `TEMonsterEntities.BLACK_SLIME.get(),` | `MonsterEntities.BLACK_SLIME.get(),` |
| 81 | `TEMonsterEntities.PURPLE_SLIME.get(),` | `MonsterEntities.MOTHER_SLIME.get(),` |
| 82 | `TEMonsterEntities.RED_SLIME.get(),` | `MonsterEntities.BABY_SLIME.get(),` |
| 83 | `TEMonsterEntities.YELLOW_SLIME.get(),` | `MonsterEntities.PURPLE_SLIME.get(),` |
| 84 | `TEMonsterEntities.JUNGLE_SLIME.get(),` | `MonsterEntities.RED_SLIME.get(),` |
| 85 | `TEMonsterEntities.SPIKED_ICE_SLIME.get(),` | `MonsterEntities.YELLOW_SLIME.get(),` |
| 86 | `TEMonsterEntities.SPIKED_JUNGLE_SLIME.get(),` | `MonsterEntities.JUNGLE_SLIME.get(),` |
| 87 | `TEMonsterEntities.SPIKED_SLIME.get()` | `MonsterEntities.SPIKED_ICE_SLIME.get(),` |
| 94 | `TEAnimals.CRAB.get(),` | `CritterEntities.CRAB.get(),` |
| 95 | `TEMonsterEntities.PIRANHA.get()` | `MonsterEntities.PIRANHA.get()` |
| 100 | `.addOptionalTag(TETags.EntityTypes.SLIME);` | `.addTag(LibTags.EntityTypes.SLIME);` |
| 102 | `for (DeferredHolder<EntityType<?>, ? extends EntityType<?>> npc : TENpcEntities.ENTITIES.getEntries()) {` | `for (RegistryObject<? extends EntityType<?>> npc : NpcEntities.ENTITIES.getEntries()) {` |

### `common/data/gen/tag/ModItemTagsProvider.java`

- 1.20 对应：`org/confluence/mod/common/data/gen/tag/ModItemTagsProvider.java`
- TE import：`TEItems`, `TETags`, `TEFigureBlocks`, `TEBoomerangItems`, `TESpawnEggItems`, `TESummonItems`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 92 | `tag(ModTags.Items.MOUNT).addOptionalTag(TETags.Items.CURIOS_MOUNT);` | `tag(ModTags.Items.TOOLS_KNIFE).add(` |
| 93 | `tag(ModTags.Items.PET).addOptionalTag(TETags.Items.CURIOS_PET);` | `SwordItems.COPPER_SHORT_SWORD.get(),` |
| 94 | `tag(ModTags.Items.LIGHT_PET).addOptionalTag(TETags.Items.CURIOS_LIGHT_PET);` | `SwordItems.TIN_SHORT_SWORD.get(),` |
| 95 | `LightPetItems.ITEMS.getEntries().forEach(item -> tag(TETags.Items.CURIOS_LIGHT_PET).add(item.get()));` | `SwordItems.IRON_SHORT_SWORD.get(),` |
| 517 | `.add(TEBoomerangItems.ITEMS.getEntries().stream().map(DeferredHolder::get).toArray(Item[]::new));` | `<无>` |
| 698 | `TESummonItems.ITEMS.getEntries().forEach(item -> tag(ModTags.Items.SUMMONER_WEAPON).add(item.get()));` | `<无>` |
| 1400 | `TEItems.DEBUG_ITEM.get(),` | `<无>` |
| 1407 | `TEFigureBlocks.FIGURE.asItem(),` | `<无>` |
| 1408 | `TEFigureBlocks.FIGURE2.asItem(),` | `<无>` |
| 1409 | `TEFigureBlocks.FIGURE3.asItem(),` | `<无>` |
| 1416 | `TESpawnEggItems.RETINAZER_SPAWN_EGG.get(),` | `FunctionalBlocks.RAINBOW_BOULDER.asItem(),` |
| 1417 | `TESpawnEggItems.SPAZMATISM_SPAWN_EGG.get(),` | `FunctionalBlocks.SPIDER_BOULDER.asItem(),` |
| 1418 | `TESpawnEggItems.THE_DESTROYER_SPAWN_EGG.get(),` | `SpawnEggItems.RETINAZER_SPAWN_EGG.get(),` |
| 1419 | `TESpawnEggItems.THE_TWINS_SPAWN_EGG.get(),` | `SpawnEggItems.SPAZMATISM_SPAWN_EGG.get(),` |
| 1420 | `TESpawnEggItems.SKELETRON_PRIME_SPAWN_EGG.get(),` | `SpawnEggItems.THE_DESTROYER_SPAWN_EGG.get(),` |
| 1421 | `TESpawnEggItems.PLANTERA_SPAWN_EGG.get(),` | `SpawnEggItems.THE_TWINS_SPAWN_EGG.get(),` |

### `common/data/saved/BossDelaySpawner.java`

- 1.20 对应：`org/confluence/mod/common/data/spawner/BossDelaySpawner.java`
- TE import：`AbstractTerraNPC`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 141 | `if (entity instanceof AbstractTerraNPC) {` | `if (entity instanceof BaseNPC) {` |

### `common/data/saved/KillBoard.java`

- 1.20 对应：`org/confluence/mod/common/data/saved/KillBoard.java`
- TE import：`TEBossEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 83 | `return isDefeated(TEBossEntities.THE_TWINS.get()) \|\|` | `return isDefeated(BossEntities.THE_TWINS.get()) \|\|` |
| 84 | `isDefeated(TEBossEntities.THE_DESTROYER.get()) \|\|` | `isDefeated(BossEntities.THE_DESTROYER.get()) \|\|` |
| 85 | `isDefeated(TEBossEntities.SKELETRON_PRIME.get());` | `isDefeated(BossEntities.SKELETRON_PRIME.get());` |
| 118 | `if (entityType == TEBossEntities.SKELETRON.get()) {` | `if (entityType == BossEntities.SKELETRON.get()) {` |
| 120 | `} else if (entityType == TEBossEntities.WALL_OF_FLESH.get() \|\| entityType == TEBossEntities.HILL_OF_FLESH.get()) {` | `} else if (entityType == BossEntities.WALL_OF_FLESH.get() \|\| entityType == BossEntities.HILL_OF_FLESH.get()) {` |

### `common/data/saved/MeteoriteTracker.java`

- 1.20 对应：`org/confluence/mod/common/data/saved/MeteoriteTracker.java`
- TE import：`TEBossEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 163 | `if (KillBoard.INSTANCE.isAnyDefeated(TEBossEntities.EATER_OF_WORLDS.get(), TEBossEntities.BRAIN_OF_CTHULHU.get()) && level.random.nextFloat() < 0.02F) {` | `if (KillBoard.INSTANCE.isAnyDefeated(BossEntities.EATER_OF_WORLDS.get(), BossEntities.BRAIN_OF_CTHULHU.get()) && level.random.nextFloat() < 0.02F) {` |

### `common/data/saved/SpaceSpawner.java`

- 1.20 对应：`org/confluence/mod/common/data/spawner/SpaceSpawner.java`
- TE import：`TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 30 | `new Pair(new MobSpawnSettings.SpawnerData(TEMonsterEntities.HARPY.get(), 3, 1, 1), (level, pos, csd, sd) -> {` | `new Pair(new MobSpawnSettings.SpawnerData(MonsterEntities.HARPY.get(), 3, 1, 1), (level, pos, csd, sd) -> {` |
| 66 | `new Pair(new MobSpawnSettings.SpawnerData(TEMonsterEntities.WYVERN.get(), 2, 1, 1), (level, pos, csd, sd) -> {` | `new Pair(new MobSpawnSettings.SpawnerData(MonsterEntities.WYVERN.get(), 2, 1, 1), (level, pos, csd, sd) -> {` |

### `common/entity/DeadBodyPartEntity.java`

- 1.20 对应：`org/confluence/mod/common/entity/DeadBodyPartEntity.java`
- TE import：`AbstractTerraBossBase`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 64 | `if (dyingEntity instanceof AbstractTerraBossBase \|\| dyingEntity instanceof Boss) {` | `if (dyingEntity instanceof Boss) {` |

### `common/entity/projectile/IceTofuBrickProjectile.java`

- 1.20 对应：`org/confluence/mod/common/entity/projectile/IceTofuBrickProjectile.java`
- TE import：`TEEffects`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 48 | `living.addEffect(new MobEffectInstance(TEEffects.FROST_BURN.getDelegate(), 200, 0));` | `living.addEffect(new MobEffectInstance(ModEffects.FROST_BURN.get(), 200, 0));` |

### `common/entity/projectile/arrow/BeeArrowEntity.java`

- 1.20 对应：`org/confluence/mod/common/entity/projectile/arrow/BeeArrowEntity.java`
- TE import：`TEUtils`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 42 | `LivingEntity target = TEUtils.getAABBAngleTarget(position(), position().add(getDeltaMovement().normalize().scale(10)), level(), this, 20, 30, this::canHitEntity);` | `LivingEntity target = LibEntityUtils.getAABBAngleTarget(position(), position().add(getDeltaMovement().normalize().scale(10)), level(), this, 20, 30, this::canHitEntity);` |
| 46 | `double angle = TEUtils.angleBetween(motion, dir);` | `double angle = LibMathUtils.angleBetween(motion, dir);` |

### `common/entity/projectile/boulder/GhoulderEntity.java`

- 1.20 对应：`org/confluence/mod/common/entity/projectile/boulder/GhoulderEntity.java`
- TE import：`TESounds`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 35 | `serverLevel.playSound(null, pos, TESounds.SOUL_DEATH.get(), SoundSource.BLOCKS, 5.0F, 1.0F);` | `serverLevel.playSound(null, pos, ModSoundEvents.SOUL_DEATH.get(), SoundSource.BLOCKS, 5.0F, 1.0F);` |

### `common/entity/projectile/boulder/RainbowBoulderEntity.java`

- 1.20 对应：`org/confluence/mod/common/entity/projectile/boulder/RainbowBoulderEntity.java`
- TE import：`DebugBlocksHelper`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 206 | `DebugBlocksHelper.Singleton().addDebugBlock(targetPos, new DebugBlocksHelper.DebugInfo(rgb.red(), rgb.green(), rgb.blue(), 200));` | `//                DebugBlocksHelper.Singleton().addDebugBlock(targetPos, new DebugBlocksHelper.DebugInfo(rgb.red(), rgb.green(), rgb.blue(), 200));` |

### `common/entity/projectile/boulder/TombstoneBoulderEntity.java`

- 1.20 对应：`org/confluence/mod/common/entity/projectile/boulder/TombstoneBoulderEntity.java`
- TE import：`AbstractTerraNPC`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 97 | `} else if (living instanceof AbstractTerraNPC && living.level().getLevelData().isHardcore()) {` | `} else if (living instanceof BaseNPC && living.level().getLevelData().isHardcore()) {` |

### `common/entity/projectile/mana/BallOfFireProjectile.java`

- 1.20 对应：`org/confluence/mod/common/entity/projectile/mana/BallOfFireProjectile.java`
- TE import：`TEEffects`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 41 | `living.addEffect(new MobEffectInstance(TEEffects.HELLFIRE, 100));` | `living.addEffect(new MobEffectInstance(ModEffects.HELLFIRE.get(), 100));` |

### `common/entity/projectile/mana/BaseDraggingProjectile.java`

- 1.20 对应：`org/confluence/mod/common/entity/projectile/mana/BaseDraggingProjectile.java`
- TE import：`ITrackType`, `BasisTrack`, `TEUtils`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 20 | `protected ITrackType trackType = new BasisTrack(90, 0.4F);` | `protected ITrackType trackType = new BasisTrack(90, 0.4F);` |
| 20 | `protected ITrackType trackType = new BasisTrack(90, 0.4F);` | `protected ITrackType trackType = new BasisTrack(90, 0.4F);` |
| 48 | `LivingEntity target = TEUtils.getAABBAngleTarget(position(), position().add(getDeltaMovement().normalize().scale(10)), level(), this, getTrackingRange(), 30, this::canHitEntity);` | `LivingEntity target = LibEntityUtils.getAABBAngleTarget(position(), position().add(getDeltaMovement().normalize().scale(10)), level(), this, getTrackingRange(), 30, this::canHitEntity);` |
| 52 | `double angle = TEUtils.angleBetween(motion, dir);` | `double angle = LibMathUtils.angleBetween(motion, dir);` |

### `common/entity/projectile/mana/BeeGunBullet.java`

- 1.20 对应：`org/confluence/mod/common/entity/projectile/mana/BeeGunBullet.java`
- TE import：`ITrackType`, `BasisTrack`, `SimpleTrack`, `TEUtils`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 20 | `private ITrackType trackType = new BasisTrack(90, 0.3);` | `private ITrackType trackType = new BasisTrack(90, 0.3);` |
| 20 | `private ITrackType trackType = new BasisTrack(90, 0.3);` | `private ITrackType trackType = new BasisTrack(90, 0.3);` |
| 32 | `LivingEntity target = TEUtils.getAABBAngleTarget(position(), position().add(getDeltaMovement().normalize()), level(), getOwner(), 10, 180, this::canHitEntity);` | `LivingEntity target = LibEntityUtils.getAABBAngleTarget(position(), position().add(getDeltaMovement().normalize()), level(), getOwner(), 10, 180, candidate -> candidate instanceof Enemy && canHitEntity(candidate));` |
| 38 | `double angle = TEUtils.angleBetween(motion, dir);` | `double angle = LibMathUtils.angleBetween(motion, dir);` |
| 39 | `if (angle < 90 && !(trackType instanceof SimpleTrack)) {` | `if (angle < 90 && !(trackType instanceof SimpleTrack)) {` |
| 40 | `this.trackType = new SimpleTrack(90, 0.5, isGiant() ? 0.5 : 0.25, Optional.of(0.5), 0.5);` | `this.trackType = new SimpleTrack(90, 0.5, isGiant() ? 0.5 : 0.25, Optional.of(0.5), 0.5);` |

### `common/entity/projectile/mana/WandOfFrostingProjectile.java`

- 1.20 对应：`org/confluence/mod/common/entity/projectile/mana/WandOfFrostingProjectile.java`
- TE import：`TEEffects`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 17 | `living.addEffect(new MobEffectInstance(TEEffects.FROST_BURN, duration));` | `living.addEffect(new MobEffectInstance(ModEffects.FROST_BURN.get(), duration));` |

### `common/entity/projectile/spear/SpearProjectile.java`

- 1.20 对应：`org/confluence/mod/common/entity/projectile/spear/SpearProjectile.java`
- TE import：`IAttackableProjectile`, `ICollisionAttackEntity`, `ITrackType`, `IEffectStrategy`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 40 | `public abstract class SpearProjectile extends AbstractHurtingProjectile implements ICollisionAttackEntity {` | `public int pierceRemaining = 1;` |
| 115 | `protected Optional<ITrackType> getTrackType() { return config.trackType; }` | `applyComponent(projComponent);` |
| 117 | `protected IEffectStrategy getHitEffect() { return config.hitEffect; }` | `applyComponent(projComponent);` |
| 131 | `Optional<ITrackType> trackType = Optional.empty();` | `applyComponent(projComponent);` |
| 132 | `IEffectStrategy hitEffect;` | `applyComponent(projComponent);` |
| 140 | `public Config trackType(ITrackType v) { this.trackType = Optional.ofNullable(v); return this; }` | `applyComponent(projComponent);` |
| 141 | `public Config hitEffect(IEffectStrategy v) { this.hitEffect = v; return this; }` | `applyComponent(projComponent);` |
| 199 | `Optional<ITrackType> track = getTrackType();` | `if (ticksAlive++ >= (projComponent != null ? projComponent.existTicks() : lifetime)) {` |
| 251 | `IEffectStrategy effect = getHitEffect();` | `protected void applyHitEffect(LivingEntity owner, LivingEntity target) {}` |
| 268 | `if (IAttackableProjectile.tryHit(target, damageSource)) {` | `DamageSource source = damageSource();` |

### `common/entity/projectile/sword/SwordProjectile.java`

- 1.20 对应：`org/confluence/mod/common/entity/projectile/sword/SwordProjectile.java`
- TE import：`TEUtils`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 191 | `double angle = TEUtils.angleBetween(motion, targetDirection);` | `double angle = LibMathUtils.angleBetween(motion, targetDirection);` |

### `common/entity/projectile/whip/WhipAttackEntity.java`

- 1.20 对应：`org/confluence/mod/common/entity/projectile/whip/WhipAttackEntity.java`
- TE import：`TEEnchantmentHelper`, `TEEnchantments`, `TETags`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 114 | `int enchantmentLevel = TEEnchantmentHelper.getEnchantmentLevel(TEEnchantments.WHIP_SWEEP, weapon);` | `int enchantmentLevel = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.WHIP_SWEEP.get(), weapon);` |
| 114 | `int enchantmentLevel = TEEnchantmentHelper.getEnchantmentLevel(TEEnchantments.WHIP_SWEEP, weapon);` | `int enchantmentLevel = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.WHIP_SWEEP.get(), weapon);` |
| 409 | `TETags.DamageTypes.of(level(), TETags.DamageTypes.SUMMON, this, owner), damage))) {` | `LibDamageTypes.of(level(), LibDamageTypes.SUMMON, this, owner), damage))) {` |
| 465 | `Immunity.withCause(this, () -> target.hurt(TETags.DamageTypes.of(level(), TETags.DamageTypes.SUMMON, this, owner), baseDamage * 0.2F));` | `Immunity.withCause(this, () -> LibDamageTypes.hurtWithoutKnockback(target, LibDamageTypes.of(level(), LibDamageTypes.SUMMON, this, owner), baseDamage * 0.2F));` |

### `common/event/ModEvents.java`

- 1.20 对应：`org/confluence/mod/common/event/ModEvents.java`
- TE import：`TEAnimals`, `TEMonsterEntities`, `IZombie`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 286 | `event.register(TEMonsterEntities.GREEN_DUMPLING_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, serverLevel, spawnType, pos, random) -> {` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 303 | `event.register(TEAnimals.JEWEL_BUNNY.get(), RegisterBestiaryKeyEvent.terraVariant(i2s));` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 304 | `event.register(TEAnimals.SQUIRREL.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 305 | `event.register(TEAnimals.JEWEL_SQUIRREL.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 306 | `event.register(TEAnimals.GRASSHOPPER.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 307 | `event.register(TEAnimals.BUTTERFLY.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 308 | `event.register(TEAnimals.WORM.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 309 | `event.register(TEAnimals.DRAGONFLY.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 310 | `event.register(TEAnimals.LADYBUG.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 311 | `event.register(TEAnimals.FEALING.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 312 | `event.register(TEAnimals.DUCK.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 313 | `event.register(TEAnimals.FAIRY.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 314 | `event.register(TEAnimals.SCORPION.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 315 | `event.register(TEMonsterEntities.DEMON_EYE.get(), (type, eye) -> {` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 324 | `if (IZombie.of(zombie).terra_entity$isSlimeZombie()) {` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |
| 337 | `event.register(TEMonsterEntities.BLACK_SLIME.get(), (type, slime) -> {` | `private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {` |

### `common/event/game/entity/LivingEntityEvents.java`

- 1.20 对应：`org/confluence/mod/common/event/game/entity/LivingEntityEvents.java`
- TE import：`IMinion`, `GoldenSlime`, `AbstractSummonMob`, `TETags`, `TEMonsterEntities`, `TEBossEntities`, `TEYoyosItems`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 124 | `(!(victim instanceof IMinion minion) \|\| minion.minion_getOwnerUUID() == null)` | `!(victim instanceof OwnedSummon)` |
| 128 | `if (attacker != null && attacker.getType().is(TETags.EntityTypes.CORRUPT)) {` | `if (attacker != null && attacker.getType().is(ModTags.EntityTypes.CORRUPT)) {` |
| 359 | `(!(living instanceof IMinion minion) \|\| minion.minion_getOwnerUUID() == null) &&` | `<无>` |
| 376 | `drops.add(new ItemEntity(level, x, y, z, TEYoyosItems.CASCADE.toStack()));` | `drops.add(new ItemEntity(level, x, y, z, YoyoItems.CASCADE.toStack()));` |
| 456 | `GoldenSlime goldenSlime = TEMonsterEntities.GOLDEN_SLIME.get().create(level);` | `GoldenSlime goldenSlime = MonsterEntities.GOLDEN_SLIME.get().create(level);` |
| 456 | `GoldenSlime goldenSlime = TEMonsterEntities.GOLDEN_SLIME.get().create(level);` | `GoldenSlime goldenSlime = MonsterEntities.GOLDEN_SLIME.get().create(level);` |
| 538 | `//            if (entityType == TEMonsterEntities.GHOST.get()) {` | `<无>` |
| 588 | `if (living instanceof AbstractSummonMob) {` | `EntityType<?> type = living.getType();` |
| 594 | `} else if (type == TEBossEntities.SKELETRON_HAND.get()) {` | `<无>` |

### `common/event/game/entity/PlayerEvents.java`

- 1.20 对应：`org/confluence/mod/common/event/game/entity/PlayerEvents.java`
- TE import：`WoodenMimic`, `TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 433 | `WoodenMimic mimic = TEMonsterEntities.HALLOWED_MIMIC.get().create(level);` | `BaseMimic mimic = MonsterEntities.HALLOWED_MIMIC.get().create(level);` |
| 433 | `WoodenMimic mimic = TEMonsterEntities.HALLOWED_MIMIC.get().create(level);` | `BaseMimic mimic = MonsterEntities.HALLOWED_MIMIC.get().create(level);` |
| 444 | `WoodenMimic mimic;` | `BaseMimic mimic;` |
| 446 | `mimic = TEMonsterEntities.CORRUPT_MIMIC.get().create(level);` | `mimic = MonsterEntities.CORRUPT_MIMIC.get().create(level);` |
| 448 | `mimic = TEMonsterEntities.CRIMSON_MIMIC.get().create(level);` | `mimic = MonsterEntities.CRIMSON_MIMIC.get().create(level);` |

### `common/gameevent/BloodMoonGameEvent.java`

- 1.20 对应：`org/confluence/mod/common/gameevent/BloodMoonGameEvent.java`
- TE import：`TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 56 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.DRIPPLER.get(), 150, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.DRIPPLER.get(), 150, 1, 1),` |
| 57 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_ZOMBIE.get(), 420, 1, 1)` | `new MobSpawnSettings.SpawnerData(MonsterEntities.BLOOD_ZOMBIE.get(), 420, 1, 1)` |

### `common/gameevent/GoblinArmyGameEvent.java`

- 1.20 对应：`org/confluence/mod/common/gameevent/GoblinArmyGameEvent.java`
- TE import：`TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 61 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_ARCHER.get(), 360, 2, 4),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GOBLIN_ARCHER.get(), 360, 2, 4),` |
| 62 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_PEON.get(), 480, 2, 3),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GOBLIN_PEON.get(), 480, 2, 3),` |
| 63 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_WARRIOR.get(), 360, 2, 3),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GOBLIN_WARRIOR.get(), 360, 2, 3),` |
| 64 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_SORCERER.get(), 240, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GOBLIN_SORCERER.get(), 240, 1, 1),` |
| 65 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_THIEF.get(), 480, 2, 4),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GOBLIN_THIEF.get(), 480, 2, 4)` |
| 66 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.ANGER_GOBLIN.get(), 240, 1, 2)` | `));` |

### `common/gameevent/MeteorShowerGameEvent.java`

- 1.20 对应：`org/confluence/mod/common/gameevent/MeteorShowerGameEvent.java`
- TE import：`SimpleVariantAnimal`, `VariantsTextureMaps`, `TEAnimals`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 76 | `EntityType<SimpleVariantAnimal> type = TEAnimals.WORM.get();` | `EntityType<Worm> type = CritterEntities.WORM.get();` |
| 76 | `EntityType<SimpleVariantAnimal> type = TEAnimals.WORM.get();` | `EntityType<Worm> type = CritterEntities.WORM.get();` |
| 78 | `SimpleVariantAnimal worm = type.spawn(level, pos, MobSpawnType.EVENT);` | `Worm worm = type.spawn(level, pos, MobSpawnType.EVENT);` |
| 80 | `worm.setVariant(VariantsTextureMaps.ENCHANTED_NIGHTCRAWLER_ID);` | `worm.setVariant(Worm.Variant.NIGHTCRAWLER);` |

### `common/gameevent/SlimeRainGameEvent.java`

- 1.20 对应：`org/confluence/mod/common/gameevent/SlimeRainGameEvent.java`
- TE import：`TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 72 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLUE_SLIME.get(), 200, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.BLUE_SLIME.get(), 200, 1, 1),` |
| 73 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.GREEN_SLIME.get(), 300, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.GREEN_SLIME.get(), 300, 1, 1),` |
| 74 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.PURPLE_SLIME.get(), 100, 1, 1),` | `new MobSpawnSettings.SpawnerData(MonsterEntities.PURPLE_SLIME.get(), 100, 1, 1),` |
| 75 | `new MobSpawnSettings.SpawnerData(TEMonsterEntities.PINK_SLIME.get(), 1, 1, 1)` | `new MobSpawnSettings.SpawnerData(MonsterEntities.PINK_SLIME.get(), 1, 1, 1)` |

### `common/init/ModDamageTypes.java`

- 1.20 对应：`<无同名文件>`
- TE import：`TETags`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 42 | `TETags.DamageTypes.createDamageTypes(context);` | `<无>` |

### `common/init/ModFluids.java`

- 1.20 对应：`org/confluence/mod/common/init/ModFluids.java`
- TE import：`TEAnimals`, `TENpcEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 131 | `addEntity(TENpcEntities.ANGLER.get(), TENpcEntities.FEMALE_ANGLER.get());` | `addEntity(NpcEntities.ANGLER.get(), NpcEntities.FEMALE_ANGLER.get());` |
| 132 | `addEntity(TENpcEntities.FEMALE_ANGLER.get(), TENpcEntities.ANGLER.get());` | `addEntity(NpcEntities.FEMALE_ANGLER.get(), NpcEntities.ANGLER.get());` |
| 134 | `addEntity(ModTags.EntityTypes.FEALING_TRANSMUTATION, TEAnimals.FEALING.get());` | `addEntity(ModTags.EntityTypes.FEALING_TRANSMUTATION, CritterEntities.FEALING.get());` |

### `common/init/ModTabs.java`

- 1.20 对应：`org/confluence/mod/common/init/ModTabs.java`
- TE import：`TEItems`, `TEBoomerangItems`, `TESummonItems`, `TEWhipItems`, `TEYoyosItems`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 1903 | `yoyo.accept(TEYoyosItems.AMAZON.get());` | `yoyo.accept(YoyoItems.AMAZON.get());` |
| 1904 | `yoyo.accept(TEYoyosItems.ARTERY.get());` | `yoyo.accept(YoyoItems.ARTERY.get());` |
| 1905 | `yoyo.accept(TEYoyosItems.CASCADE.get());` | `yoyo.accept(YoyoItems.CASCADE.get());` |
| 1906 | `yoyo.accept(TEYoyosItems.CODE_1.get());` | `yoyo.accept(YoyoItems.CODE_1.get());` |
| 1907 | `yoyo.accept(TEYoyosItems.HIVE_FIVE.get());` | `yoyo.accept(YoyoItems.HIVE_FIVE.get());` |
| 1908 | `yoyo.accept(TEYoyosItems.MALAISE.get());` | `yoyo.accept(YoyoItems.MALAISE.get());` |
| 1909 | `yoyo.accept(TEYoyosItems.RALLY.get());` | `yoyo.accept(YoyoItems.RALLY.get());` |
| 1910 | `yoyo.accept(TEYoyosItems.VALOR.get());` | `yoyo.accept(YoyoItems.VALOR.get());` |
| 1911 | `yoyo.accept(TEYoyosItems.WOODEN_YOYO.get());` | `yoyo.accept(YoyoItems.WOODEN_YOYO.get());` |
| 1912 | `acceptAll(TEBoomerangItems.ITEMS, output, "boomerang");` | `yoyo.accept(YoyoItems.CHIK.get());` |
| 2010 | `acceptAll(TESummonItems.ITEMS, output);` | `acceptAll(SummonItems.ITEMS, output);` |
| 2011 | `acceptAll(TEWhipItems.ITEMS, output);` | `acceptAll(WhipItems.ITEMS, output);` |
| 2014 | `.withTabsAfter(TEItems.NEO_TERRA.getId())` | `<无>` |
| 2029 | `output.accept(TEBoomerangItems.BeiDou_BOOMERANG);` | `output.accept(BoomerangItems.BEIDOU_BOOMERANG);` |
| 2032 | `output.accept(TEBoomerangItems.DEVELOPER_BOOMERANG);` | `output.accept(BoomerangItems.DEVELOPER_BOOMERANG);` |
| 2039 | `.withTabsBefore(TEItems.NEO_TERRA.getId())` | `.withTabsBefore(ENTITY.getId())` |

### `common/init/armor/ModArmorBonus.java`

- 1.20 对应：`org/confluence/mod/common/init/armor/ModArmorBonus.java`
- TE import：`TEEffects`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 463 | `victim.addEffect(new MobEffectInstance(TEEffects.FROST_BURN, 100));` | `victim.addEffect(new MobEffectInstance(ModEffects.FROST_BURN.get(), 100));` |

### `common/init/block/StatueBlocks.java`

- 1.20 对应：`org/confluence/mod/common/init/block/StatueBlocks.java`
- TE import：`AbstractMonster`, `DemonEye`, `FlyMonsterPrefab`, `BaseSlime`, `TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 136 | `public static final DeferredBlock<BehaviourStatueBlock> CORRUPT_STATUE = registerSimpleSummon("corrupt_statue", true, level -> new AbstractMonster(TEMonsterEntities.EATER_OF_SOULS.get(), level, FlyMonsterPrefab.EATER_OF_SOULS_BUILDER.get()));` | `public static final PortDeferredBlock<BehaviourStatueBlock> CORRUPT_STATUE = registerSimpleSummon("corrupt_statue", true, level -> MonsterEntities.EATER_OF_SOULS.get().create(level));` |
| 136 | `public static final DeferredBlock<BehaviourStatueBlock> CORRUPT_STATUE = registerSimpleSummon("corrupt_statue", true, level -> new AbstractMonster(TEMonsterEntities.EATER_OF_SOULS.get(), level, FlyMonsterPrefab.EATER_OF_SOULS_BUILDER.get()));` | `public static final PortDeferredBlock<BehaviourStatueBlock> CORRUPT_STATUE = registerSimpleSummon("corrupt_statue", true, level -> MonsterEntities.EATER_OF_SOULS.get().create(level));` |
| 136 | `public static final DeferredBlock<BehaviourStatueBlock> CORRUPT_STATUE = registerSimpleSummon("corrupt_statue", true, level -> new AbstractMonster(TEMonsterEntities.EATER_OF_SOULS.get(), level, FlyMonsterPrefab.EATER_OF_SOULS_BUILDER.get()));` | `public static final PortDeferredBlock<BehaviourStatueBlock> CORRUPT_STATUE = registerSimpleSummon("corrupt_statue", true, level -> MonsterEntities.EATER_OF_SOULS.get().create(level));` |
| 138 | `public static final DeferredBlock<BehaviourStatueBlock> DRIPPLER_STATUE = registerSimpleSummon("drippler_statue", true, level -> new AbstractMonster(TEMonsterEntities.DRIPPLER.get(), level, FlyMonsterPrefab.DRIPPLER_BUILDER.get()));` | `public static final PortDeferredBlock<BehaviourStatueBlock> DRIPPLER_STATUE = registerSimpleSummon("drippler_statue", true, level -> MonsterEntities.DRIPPLER.get().create(level));` |
| 138 | `public static final DeferredBlock<BehaviourStatueBlock> DRIPPLER_STATUE = registerSimpleSummon("drippler_statue", true, level -> new AbstractMonster(TEMonsterEntities.DRIPPLER.get(), level, FlyMonsterPrefab.DRIPPLER_BUILDER.get()));` | `public static final PortDeferredBlock<BehaviourStatueBlock> DRIPPLER_STATUE = registerSimpleSummon("drippler_statue", true, level -> MonsterEntities.DRIPPLER.get().create(level));` |
| 138 | `public static final DeferredBlock<BehaviourStatueBlock> DRIPPLER_STATUE = registerSimpleSummon("drippler_statue", true, level -> new AbstractMonster(TEMonsterEntities.DRIPPLER.get(), level, FlyMonsterPrefab.DRIPPLER_BUILDER.get()));` | `public static final PortDeferredBlock<BehaviourStatueBlock> DRIPPLER_STATUE = registerSimpleSummon("drippler_statue", true, level -> MonsterEntities.DRIPPLER.get().create(level));` |
| 139 | `public static final DeferredBlock<BehaviourStatueBlock> EYEBALL_STATUE = registerSimpleSummon("eyeball_statue", true, level -> new DemonEye(TEMonsterEntities.DEMON_EYE.get(), level));` | `public static final PortDeferredBlock<BehaviourStatueBlock> EYEBALL_STATUE = registerSimpleSummon("eyeball_statue", true, level -> MonsterEntities.DEMON_EYE.get().create(level));` |
| 139 | `public static final DeferredBlock<BehaviourStatueBlock> EYEBALL_STATUE = registerSimpleSummon("eyeball_statue", true, level -> new DemonEye(TEMonsterEntities.DEMON_EYE.get(), level));` | `public static final PortDeferredBlock<BehaviourStatueBlock> EYEBALL_STATUE = registerSimpleSummon("eyeball_statue", true, level -> MonsterEntities.DEMON_EYE.get().create(level));` |
| 156 | `public static final DeferredBlock<BehaviourStatueBlock> SLIME_STATUE = registerSimpleSummon("slime_statue", false, level -> new BaseSlime(TEMonsterEntities.BLUE_SLIME.get(), level, 0x73BCF4, 2));` | `public static final PortDeferredBlock<BehaviourStatueBlock> SLIME_STATUE = registerSimpleSummon("slime_statue", false, level -> MonsterEntities.BLUE_SLIME.get().create(level));` |
| 156 | `public static final DeferredBlock<BehaviourStatueBlock> SLIME_STATUE = registerSimpleSummon("slime_statue", false, level -> new BaseSlime(TEMonsterEntities.BLUE_SLIME.get(), level, 0x73BCF4, 2));` | `public static final PortDeferredBlock<BehaviourStatueBlock> SLIME_STATUE = registerSimpleSummon("slime_statue", false, level -> MonsterEntities.BLUE_SLIME.get().create(level));` |

### `common/init/item/BaitItems.java`

- 1.20 对应：`org/confluence/mod/common/init/item/BaitItems.java`
- TE import：`TEAnimals`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 23 | `BLACK_DRAGONFLY = register("black_dragonfly", BLUE, 0.2F, TEAnimals.DRAGONFLY, entity -> entity.setVariant(0)),` | `BLACK_DRAGONFLY = register("black_dragonfly", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.BLACK)),` |
| 24 | `BLACK_SCORPION = register("black_scorpion", BLUE, 0.15F, TEAnimals.SCORPION, entity -> entity.setVariant(0)),` | `BLACK_SCORPION = register("black_scorpion", BLUE, 0.15F, CritterEntities.SCORPION, entity -> entity.setVariant(Scorpion.Variant.BLACK)),` |
| 25 | `BLUE_DRAGONFLY = register("blue_dragonfly", BLUE, 0.2F, TEAnimals.DRAGONFLY, entity -> entity.setVariant(1)),` | `BLUE_DRAGONFLY = register("blue_dragonfly", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.BLUE)),` |
| 28 | `ENCHANTED_NIGHTCRAWLER = register("enchanted_nightcrawler", GREEN, 0.35F, TEAnimals.WORM, entity -> entity.setVariant(0)),` | `ENCHANTED_NIGHTCRAWLER = register("enchanted_nightcrawler", GREEN, 0.35F, CritterEntities.WORM, entity -> entity.setVariant(Worm.Variant.NIGHTCRAWLER)),` |
| 30 | `GLOWING_SNAIL = register("glowing_snail", BLUE, 0.15F, TEAnimals.GLOWING_SNAIL),` | `GLOWING_SNAIL = register("glowing_snail", BLUE, 0.15F, CritterEntities.GLOWING_SNAIL),` |
| 31 | `GOLD_BUTTERFLY = register("gold_butterfly", ORANGE, 0.5F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(0)),` | `GOLD_BUTTERFLY = register("gold_butterfly", ORANGE, 0.5F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.GOLD)),` |
| 32 | `GOLD_DRAGONFLY = register("gold_dragonfly", ORANGE, 0.5F, TEAnimals.DRAGONFLY, entity -> entity.setVariant(2)),` | `GOLD_DRAGONFLY = register("gold_dragonfly", ORANGE, 0.5F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.GOLD)),` |
| 33 | `GOLD_GRASSHOPPER = register("gold_grasshopper", ORANGE, 0.5F, TEAnimals.GRASSHOPPER, entity -> entity.setVariant(0)),` | `GOLD_GRASSHOPPER = register("gold_grasshopper", ORANGE, 0.5F, CritterEntities.GRASSHOPPER, entity -> entity.setVariant(Grasshopper.Variant.GOLD)),` |
| 34 | `GOLD_LADYBUG = register("gold_ladybug", ORANGE, 0.5F, TEAnimals.LADYBUG, entity -> entity.setVariant(0)),` | `GOLD_LADYBUG = register("gold_ladybug", ORANGE, 0.5F, CritterEntities.LADYBUG, entity -> entity.setVariant(Ladybug.Variant.GOLD)),` |
| 36 | `GOLD_WORM = register("gold_warm", ORANGE, 0.5F, TEAnimals.WORM, entity -> entity.setVariant(1)),` | `GOLD_WORM = register("gold_warm", ORANGE, 0.5F, CritterEntities.WORM, entity -> entity.setVariant(Worm.Variant.GOLD)),` |
| 37 | `GRASSHOPPER = register("grasshopper", WHITE, 0.1F, TEAnimals.GRASSHOPPER, entity -> entity.setVariant(1)),` | `GRASSHOPPER = register("grasshopper", WHITE, 0.1F, CritterEntities.GRASSHOPPER, entity -> entity.setVariant(Grasshopper.Variant.GREEN)),` |
| 38 | `GREEN_DRAGONFLY = register("green_dragonfly", BLUE, 0.2F, TEAnimals.DRAGONFLY, entity -> entity.setVariant(3)),` | `GREEN_DRAGONFLY = register("green_dragonfly", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.GREEN)),` |
| 40 | `GRUBBY = register("grubby", BLUE, 0.15F, TEAnimals.GRUBBY),` | `GRUBBY = register("grubby", BLUE, 0.15F, CritterEntities.GRUBBY),` |
| 41 | `HELL_BUTTERFLY = register("hell_butterfly", BLUE, 0.15F, TEAnimals.HELL_BUTTERFLY),` | `HELL_BUTTERFLY = register("hell_butterfly", BLUE, 0.15F, CritterEntities.HELL_BUTTERFLY),` |
| 42 | `JULIA_BUTTERFLY = register("julia_butterfly", BLUE, 0.25F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(1)),` | `JULIA_BUTTERFLY = register("julia_butterfly", BLUE, 0.25F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.JULIA)),` |
| 43 | `LADYBUG = register("ladybug", BLUE, 0.17F, TEAnimals.LADYBUG, entity -> entity.setVariant(1)),` | `LADYBUG = register("ladybug", BLUE, 0.17F, CritterEntities.LADYBUG, entity -> entity.setVariant(Ladybug.Variant.RED)),` |
| 46 | `MAGGOT = register("maggot", BLUE, 0.22F, TEAnimals.MAGGOT),` | `MAGGOT = register("maggot", BLUE, 0.22F, CritterEntities.MAGGOT),` |
| 47 | `MAGMA_SNAIL = register("magma_snail", GREEN, 0.35F, TEAnimals.MAGMA_SNAIL),` | `MAGMA_SNAIL = register("magma_snail", GREEN, 0.35F, CritterEntities.MAGMA_SNAIL),` |
| 48 | `MONARCH_BUTTERFLY = register("monarch_butterfly", WHITE, 0.05F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(2)),` | `MONARCH_BUTTERFLY = register("monarch_butterfly", WHITE, 0.05F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.MONARCH)),` |
| 49 | `ORANGE_DRAGONFLY = register("orange_dragonfly", BLUE, 0.2F, TEAnimals.DRAGONFLY, entity -> entity.setVariant(4)),` | `ORANGE_DRAGONFLY = register("orange_dragonfly", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.ORANGE)),` |
| 51 | `PURPLE_EMPEROR_BUTTERFLY = register("purple_emperor_butterfly", GREEN, 0.35F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(3)),` | `PURPLE_EMPEROR_BUTTERFLY = register("purple_emperor_butterfly", GREEN, 0.35F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.PURPLE_EMPEROR)),` |
| 52 | `RED_ADMIRAL_BUTTERFLY = register("red_admiral_butterfly", GREEN, 0.3F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(4)),` | `RED_ADMIRAL_BUTTERFLY = register("red_admiral_butterfly", GREEN, 0.3F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.RED_ADMIRAL)),` |
| 53 | `RED_DRAGONFLY = register("red_dragonfly", BLUE, 0.2F, TEAnimals.DRAGONFLY, entity -> entity.setVariant(5)),` | `RED_DRAGONFLY = register("red_dragonfly", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.RED)),` |
| 54 | `SCORPION = register("scorpion", WHITE, 0.1F, TEAnimals.SCORPION, entity -> entity.setVariant(1)),` | `SCORPION = register("scorpion", WHITE, 0.1F, CritterEntities.SCORPION, entity -> entity.setVariant(Scorpion.Variant.NORMAL)),` |
| 55 | `SLUGGY = register("sluggy", BLUE, 0.25F, TEAnimals.SLUGGY),` | `SLUGGY = register("sluggy", BLUE, 0.25F, CritterEntities.SLUGGY),` |
| 56 | `SNAIL = register("snail", WHITE, 0.1F, TEAnimals.SNAIL),` | `SNAIL = register("snail", WHITE, 0.1F, CritterEntities.SNAIL),` |
| 58 | `SULPHUR_BUTTERFLY = register("sulphur_butter", WHITE, 0.1F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(5)),` | `SULPHUR_BUTTERFLY = register("sulphur_butter", WHITE, 0.1F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.SULPHUR)),` |
| 59 | `TREE_NYMPH_BUTTERFLY = register("tree_numph_butterfly", ORANGE, 0.5F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(6)),` | `TREE_NYMPH_BUTTERFLY = register("tree_numph_butterfly", ORANGE, 0.5F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.TREE_NYMPH)),` |
| 61 | `PRISMATIC_LACEWING = register("prismatic_lacewing", ORANGE, 0F, TEAnimals.PRISMATIC_LACEWING),` | `PRISMATIC_LACEWING = register("prismatic_lacewing", ORANGE, 0F, CritterEntities.PRISMATIC_LACEWING),` |
| 62 | `ULYSSES_BUTTERFLY = register("ulysses_butterfly", BLUE, 0.2F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(7)),` | `ULYSSES_BUTTERFLY = register("ulysses_butterfly", BLUE, 0.2F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.ULYSSES)),` |
| 64 | `WORM = register("worm", BLUE, 0.25F, TEAnimals.WORM, entity -> entity.setVariant(2)),` | `WORM = register("worm", BLUE, 0.25F, CritterEntities.WORM, entity -> entity.setVariant(Worm.Variant.NORMAL)),` |
| 65 | `YELLOW_DRAGONFLY = register("yellow_dragonfly", BLUE, 0.2F, TEAnimals.DRAGONFLY, entity -> entity.setVariant(6)),` | `YELLOW_DRAGONFLY = register("yellow_dragonfly", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.YELLOW)),` |
| 66 | `ZEBRA_SWALLOWTAIL_BUTTERFLY = register("zebra_swallowtail_butterfly", BLUE, 0.15F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(8));` | `ZEBRA_SWALLOWTAIL_BUTTERFLY = register("zebra_swallowtail_butterfly", BLUE, 0.15F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.ZEBRA_SWALLOWTAIL));` |

### `common/item/accessory/GuideVooDooDollItem.java`

- 1.20 对应：`org/confluence/mod/common/item/accessory/GuideVooDooDollItem.java`
- TE import：`HillOfFlesh`, `WallOfFlesh`, `TESounds`, `TEBossEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 120 | `EntityType<WallOfFlesh> wof = TEBossEntities.WALL_OF_FLESH.get();` | `if (level.dimension() != OverworldUtils.underworld() && !CommonConfigs.ALLOW_FLESH_BOSSES_OUTSIDE_UNDERWORLD.get()) {` |
| 120 | `EntityType<WallOfFlesh> wof = TEBossEntities.WALL_OF_FLESH.get();` | `if (level.dimension() != OverworldUtils.underworld() && !CommonConfigs.ALLOW_FLESH_BOSSES_OUTSIDE_UNDERWORLD.get()) {` |
| 121 | `EntityType<HillOfFlesh> hof = TEBossEntities.HILL_OF_FLESH.get();` | `if (entity instanceof ItemEntity) {` |
| 121 | `EntityType<HillOfFlesh> hof = TEBossEntities.HILL_OF_FLESH.get();` | `if (entity instanceof ItemEntity) {` |
| 151 | `WallOfFlesh wallOfFlesh = wof.spawn(level, blockPos.relative(direction, 64), MobSpawnType.MOB_SUMMONED);` | `WallOfFlesh wallOfFlesh = wof.spawn(level, blockPos, MobSpawnType.MOB_SUMMONED);` |
| 159 | `player.connection.send(new ClientboundSoundPacket(TESounds.WALL_OF_FLESH_ROAR, SoundSource.HOSTILE, player.getX(), player.getY(), player.getZ(), 1, 1, 0));` | `player.connection.send(new ClientboundSoundPacket(ModSoundEvents.WALL_OF_FLESH_ROAR.getHolder().orElseThrow(), SoundSource.HOSTILE, player.getX(), player.getY(), player.getZ(), 1, 1, 0));` |

### `common/item/common/BaseLanceItem.java`

- 1.20 对应：`org/confluence/mod/common/item/common/BaseLanceItem.java`
- TE import：`ILeftClickStateItem`, `WeaponStorage`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 53 | `public class BaseLanceItem extends CustomRarityItem implements ILeftClickStateItem, GeoItem {` | `public class BaseLanceItem extends CustomRarityItem implements ILeftClickStateItem, GeoItem {` |
| 103 | `WeaponStorage.of(owner).leftClicking &&` | `LeftClickState.of(owner).isPressed(stack) &&` |

### `common/item/common/BossSummoningItem.java`

- 1.20 对应：`org/confluence/mod/common/item/common/BossSummoningItem.java`
- TE import：`TEUtils`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 48 | `if (TEUtils.internalSpawnEntity(mob, serverLevel)) {` | `bindSummoner(player, mob);` |

### `common/item/common/ModBoneMealItem.java`

- 1.20 对应：`org/confluence/mod/common/item/common/ModBoneMealItem.java`
- TE import：`TEMonsterEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 43 | `newEntity = TEMonsterEntities.BLOODY_SPORE.value().create(level);` | `newEntity = MonsterEntities.BLOODY_SPORE.get().create(level);` |
| 46 | `newEntity = TEMonsterEntities.DECAYEDER.value().create(level);` | `newEntity = MonsterEntities.DECAYEDER.get().create(level);` |

### `common/item/common/SpikyBallItem.java`

- 1.20 对应：`org/confluence/mod/common/item/common/SpikyBallItem.java`
- TE import：`TESounds`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 25 | `level.playSound(null, player.getX(), player.getY(), player.getZ(), TESounds.WAVING.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 0.8F));` | `level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.WAVING.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 0.8F));` |

### `common/item/common/ThrowableDropSelfItem.java`

- 1.20 对应：`org/confluence/mod/common/item/common/ThrowableDropSelfItem.java`
- TE import：`TESounds`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 40 | `pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), TESounds.WAVING.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));` | `level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.WAVING.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 0.8F));` |

### `common/item/crossbow/BaseTerraRepeaterItem.java`

- 1.20 对应：`org/confluence/mod/common/item/crossbow/BaseTerraRepeaterItem.java`
- TE import：`ILeftClickStateItem`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 61 | `public class BaseTerraRepeaterItem extends CrossbowItem implements ILeftClickStateItem {` | `public class BaseTerraRepeaterItem extends CrossbowItem implements ILeftClickStateItem {` |

### `common/item/mana/MagicDaggerItem.java`

- 1.20 对应：`org/confluence/mod/common/item/mana/MagicDaggerItem.java`
- TE import：`TESounds`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 25 | `return TESounds.WAVING.get();` | `return ModSoundEvents.WAVING.get();` |

### `common/particle/DamageIndicatorOptions.java`

- 1.20 对应：`org/confluence/mod/common/particle/DamageIndicatorOptions.java`
- TE import：`WallOfFlesh`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 60 | `if (victim instanceof WallOfFlesh) {` | `if (victim instanceof WallOfFlesh) {` |

### `common/recipe/special/BoomBunnyRecipe.java`

- 1.20 对应：`org/confluence/mod/common/recipe/special/BoomBunnyRecipe.java`
- TE import：`TEAnimals`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 71 | `tag.putString(Entity.ID_TAG, BuiltInRegistries.ENTITY_TYPE.getKey(TEAnimals.EXPLOSIVE_BUNNY.get()).toString());` | `tag.putString(Entity.ID_TAG, CritterEntities.EXPLOSIVE_BUNNY.getId().toString());` |

### `common/worldgen/structure/DungeonStructure.java`

- 1.20 对应：`org/confluence/mod/common/worldgen/structure/DungeonStructure.java`
- TE import：`DungeonGuardian`, `TESounds`, `TEBossEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 560 | `!KillBoard.INSTANCE.isDefeated(TEBossEntities.SKELETRON.get())` | `!KillBoard.INSTANCE.isDefeated(BossEntities.SKELETRON.get())` |
| 566 | `level.playSound(null, player.blockPosition(), TESounds.ROAR.get(), SoundSource.HOSTILE);` | `level.playSound(null, player.blockPosition(), ModSoundEvents.ROAR.get(), SoundSource.HOSTILE);` |
| 572 | `ModUtils.summonBoss(level, player.blockPosition(), new DungeonGuardian(TEBossEntities.DUNGEON_GUARDIAN.get(), level));` | `ModUtils.summonBoss(level, player.blockPosition(), BossEntities.DUNGEON_GUARDIAN.get().create(level));` |
| 572 | `ModUtils.summonBoss(level, player.blockPosition(), new DungeonGuardian(TEBossEntities.DUNGEON_GUARDIAN.get(), level));` | `ModUtils.summonBoss(level, player.blockPosition(), BossEntities.DUNGEON_GUARDIAN.get().create(level));` |

### `integration/mrcrayfish/furniture/MrCrayfishFurnitureHelper.java`

- 1.20 对应：`<无同名文件>`
- TE import：`TETags`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 17 | `consumer.apply(TETags.Blocks.NPC_HOUSE_CHAIR)` | `<无>` |
| 65 | `consumer.apply(TETags.Blocks.NPC_HOUSE_TABLE)` | `<无>` |

### `integration/sodium/dynamiclights/SodiumDynamicLightsHelper.java`

- 1.20 对应：`<无同名文件>`
- TE import：`TEProjectileEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 24 | `DynamicLightHandlers.registerDynamicLightHandler(TEProjectileEntities.BOOMERANG_PROJECTILE.get(), entity -> entity.getModifier().luminance);` | `<无>` |

### `mixin/client/MinecraftMixin.java`

- 1.20 对应：`org/confluence/mod/mixin/client/MinecraftMixin.java`
- TE import：`IMinion`, `ISummonMob`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 59 | `if (entity instanceof IMinion \|\| entity instanceof ISummonMob) return;` | `<无>` |
| 59 | `if (entity instanceof IMinion \|\| entity instanceof ISummonMob) return;` | `<无>` |

### `mixin/client/resources/model/BlockStateModelLoaderMixin.java`

- 1.20 对应：`<无同名文件>`
- TE import：`TerraEntity`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 19 | `private static final Set<String> confluence$skipSet = Set.of(Confluence.MODID, TerraFurniture.MODID, TerraEntity.MODID);` | `<无>` |

### `mixin/client/resources/model/ModelBakeryMixin.java`

- 1.20 对应：`org/confluence/mod/mixin/client/resources/model/ModelBakeryMixin.java`
- TE import：`TerraEntity`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 21 | `private static final Set<String> confluence$skipSet = Set.of(Confluence.MODID, TerraFurniture.MODID, TerraEntity.MODID);` | `private static final Set<String> confluence$skipSet = Set.of(Confluence.MODID, TerraFurniture.MODID);` |

### `mixin/client/resources/model/ModelManagerMixin.java`

- 1.20 对应：`org/confluence/mod/mixin/client/resources/model/ModelManagerMixin.java`
- TE import：`TerraEntity`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 20 | `private static final Set<String> confluence$skipSet = Set.of(Confluence.MODID, TerraFurniture.MODID, TerraEntity.MODID);` | `private static final Set<String> confluence$skipSet = Set.of(Confluence.MODID, TerraFurniture.MODID);` |

### `mixin/integration/touhoulittlemaid/EntityMaidMixin.java`

- 1.20 对应：`<无同名文件>`
- TE import：`ITradeHolder`, `NPCMood`, `NPCTradeManager`, `TradeParams`, `UpdateNPCTradePacket`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 24 | `public abstract class EntityMaidMixin implements ITradeHolder, SelfGetter<EntityMaid> {` | `<无>` |
| 27 | `private NPCTradeManager trades;` | `<无>` |
| 28 | `private NPCMood mood = new NPCMood();` | `<无>` |
| 29 | `TradeParams tradeParams = TradeParams.create(); // 在没有使用发包同步参数之前，暂时使用默认参数` | `<无>` |
| 32 | `public NPCTradeManager getTradeManager() {` | `<无>` |
| 37 | `public TradeParams getTradeParams() {` | `<无>` |
| 43 | `UpdateNPCTradePacket.syncNpcTrade(index, confluence$self().getUUID(), this);` | `<无>` |
| 52 | `public @Nullable NPCMood getMood() {` | `<无>` |
| 63 | `trades = NPCTradeManager.getCopy(Keys.MAID_SHOP,ops);` | `<无>` |

### `mixin/resources/RegistryDataLoaderMixin.java`

- 1.20 对应：`<无同名文件>`
- TE import：`TerraEntity`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 41 | `return isNotBiomeModifier.get() \|\| !reader.location().getNamespace().equals(TerraEntity.MODID);` | `<无>` |

### `mixin/world/entity/LocalEntityMixin.java`

- 1.20 对应：`org/confluence/mod/mixin/world/entity/LocalEntityMixin.java`
- TE import：`IMinion`, `ISummonMob`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 25 | `if (thiz instanceof IMinion \|\| thiz instanceof ISummonMob) return;` | `<无>` |
| 25 | `if (thiz instanceof IMinion \|\| thiz instanceof ISummonMob) return;` | `<无>` |

### `network/s2c/AvailableHouseSelectPacketS2C.java`

- 1.20 对应：`org/confluence/mod/network/s2c/AvailableHouseSelectPacketS2C.java`
- TE import：`TENpcEntities`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 39 | `TENpcEntities.GUIDE.get(),` | `NpcEntities.GUIDE.get(),` |
| 40 | `TENpcEntities.MERCHANT.get(),` | `NpcEntities.MERCHANT.get(),` |
| 41 | `TENpcEntities.NURSE.get(),` | `NpcEntities.NURSE.get(),` |
| 42 | `TENpcEntities.DEMOLITIONIST.get(),` | `NpcEntities.DEMOLITIONIST.get(),` |
| 43 | `TENpcEntities.DRYAD.get(),` | `NpcEntities.DRYAD.get(),` |
| 44 | `TENpcEntities.ARMS_DEALER.get(),` | `NpcEntities.ARMS_DEALER.get(),` |
| 45 | `TENpcEntities.CLOTHIER.get(),` | `NpcEntities.CLOTHIER.get(),` |
| 46 | `TENpcEntities.MECHANIC.get(),` | `NpcEntities.MECHANIC.get(),` |
| 47 | `TENpcEntities.GOBLIN_TINKERER.get(),` | `NpcEntities.GOBLIN_TINKERER.get(),` |
| 48 | `TENpcEntities.WIZARD.get(),` | `NpcEntities.WIZARD.get(),` |
| 50 | `TENpcEntities.TRUFFLE.get(),` | `NpcEntities.TRUFFLE.get(),` |
| 52 | `TENpcEntities.PARTY_GIRL.get(),` | `NpcEntities.PARTY_GIRL.get(),` |
| 54 | `TENpcEntities.PAINTER.get(),` | `NpcEntities.PAINTER.get(),` |
| 55 | `TENpcEntities.WITCH_DOCTOR.get(),` | `NpcEntities.WITCH_DOCTOR.get(),` |
| 59 | `TENpcEntities.TRAVELING_MERCHANT.get(),` | `NpcEntities.TRAVELING_MERCHANT.get(),` |
| 60 | `TENpcEntities.ANGLER.get(),` | `NpcEntities.ANGLER.get(),` |
| 63 | `TENpcEntities.ZOOLOGIST.get()` | `NpcEntities.ZOOLOGIST.get()` |

### `util/DeathAnimUtils.java`

- 1.20 对应：`org/confluence/mod/util/DeathAnimUtils.java`
- TE import：`WallOfFleshRenderer`, `WallOfFlesh`, `DeathAnimOptions`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 52 | `public static final Map<EntityType<? extends LivingEntity>, DeathAnimOptions> options = new HashMap<>();` | `// todo blood   public static final Map<EntityType<? extends LivingEntity>, DeathAnimOptions> options = new HashMap<>();` |
| 100 | `public static DeathAnimOptions getDeathAnimOptions(Entity entity) {` | `//    public static DeathAnimOptions getDeathAnimOptions(Entity entity) {` |
| 101 | `return entity instanceof DeathAnimOptions r ? r : entity == null ? null : options.get(entity.getType());` | `//        return entity instanceof DeathAnimOptions r ? r : entity == null ? null : options.get(entity.getType());` |
| 233 | `if (living instanceof WallOfFlesh && geoRenderer instanceof WallOfFleshRenderer wofRenderer) {` | `//            if (living instanceof WallOfFlesh && geoRenderer instanceof WallOfFleshRenderer wofRenderer) {` |
| 233 | `if (living instanceof WallOfFlesh && geoRenderer instanceof WallOfFleshRenderer wofRenderer) {` | `//            if (living instanceof WallOfFlesh && geoRenderer instanceof WallOfFleshRenderer wofRenderer) {` |
| 252 | `if (living instanceof WallOfFlesh && level.random.nextInt(25) != 0)` | `if (living instanceof WallOfFlesh && level.random.nextInt(25) != 0)` |

### `util/ModUtils.java`

- 1.20 对应：`org/confluence/mod/util/ModUtils.java`
- TE import：`TerraEntity`, `AbstractTerraBossBase`, `TEBossEntities`, `TEMonsterEntities`, `TEUtils`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 96 | `public static final Set<String> CONFLUENCE_NAMESPACES = Set.of(Confluence.MODID, TerraCurio.MODID, TerraEntity.MODID);` | `public static final Set<String> CONFLUENCE_NAMESPACES = Set.of(Confluence.MODID, TerraCurio.MODID, TerraFurniture.MODID);` |
| 129 | `public static void summonBoss(ServerLevel level, BlockPos pos, AbstractTerraBossBase boss, boolean onSurface) {` | `<无>` |
| 137 | `if (TEUtils.internalSpawnEntity(boss, level)) {` | `<无>` |
| 142 | `public static void summonBoss(ServerLevel level, BlockPos pos, AbstractTerraBossBase boss) {` | `<无>` |
| 149 | `/// 1.21 上面那两个重载是 TE 时代形态（收 `AbstractTerraBossBase`），主模组的` | `<无>` |
| 151 | `/// **不继承** `AbstractTerraBossBase`，所以 `Skeletron`（1.21 `common/entity/boss/Skeletron.java:37`` | `<无>` |
| 156 | `/// `TEUtils.internalSpawnEntity`，是 TE Boss 专有的初始化，主模组 Boss 不需要）。` | `<无>` |
| 189 | `boolean isEaterOfWorlds = type == TEBossEntities.EATER_OF_WORLDS.get();` | `KillBoard.INSTANCE.defeat(level.getServer(), type);` |
| 190 | `if (isEaterOfWorlds \|\| type == TEBossEntities.BRAIN_OF_CTHULHU.get()) {` | `if (type == BossEntities.EATER_OF_WORLDS.get() \|\| type == BossEntities.BRAIN_OF_CTHULHU.get()) {` |
| 198 | `boolean is$WallOrHill$OfFlesh = type == TEBossEntities.WALL_OF_FLESH.get() \|\| type == TEBossEntities.HILL_OF_FLESH.get();` | `TreasureBagItem.createItemEntity(boss, player);` |
| 243 | `if (type == TEMonsterEntities.VISUAL_NEURON.get() \|\| (type == TEBossEntities.BRAIN_OF_CTHULHU.get() && attacker.getRandom().nextFloat() < 0.3333F)) {` | `if (type == MonsterEntities.VISUAL_NEURON.get() \|\| (type == BossEntities.BRAIN_OF_CTHULHU.get() && attacker.getRandom1211().nextFloat() < 0.3333F)) {` |
| 243 | `if (type == TEMonsterEntities.VISUAL_NEURON.get() \|\| (type == TEBossEntities.BRAIN_OF_CTHULHU.get() && attacker.getRandom().nextFloat() < 0.3333F)) {` | `if (type == MonsterEntities.VISUAL_NEURON.get() \|\| (type == BossEntities.BRAIN_OF_CTHULHU.get() && attacker.getRandom1211().nextFloat() < 0.3333F)) {` |
| 282 | `if (attacker != null && attacker.getType() == TEMonsterEntities.CURSED_SKULL.get() && attacker.getRandom().nextFloat() < 0.33F) {` | `if (attacker != null && attacker.getType() == MonsterEntities.CURSED_SKULL.get() && attacker.getRandom1211().nextFloat() < 0.33F) {` |

### `util/PrefixUtils.java`

- 1.20 对应：`org/confluence/mod/util/PrefixUtils.java`
- TE import：`ITradeHolder`, `IPlayer`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 474 | `ITradeHolder holder = ((IPlayer) player).terra_entity$getTradeHolder();` | `// todo trade       ITradeHolder holder = ((IPlayer) player).confluence$getTradeHolder();` |
| 474 | `ITradeHolder holder = ((IPlayer) player).terra_entity$getTradeHolder();` | `// todo trade       ITradeHolder holder = ((IPlayer) player).confluence$getTradeHolder();` |

### `util/generation/variant/AboveFallenGeneration.java`

- 1.20 对应：`org/confluence/mod/util/generation/variant/AboveFallenGeneration.java`
- TE import：`AimUtils`, `TEUtils`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 52 | `LivingEntity target = TEUtils.getAABBAngleTarget(eye, eye.add(owner.getForward().normalize().scale(range)), owner.level(), owner, range, maxAngle, e -> TEUtils.projectileCanHurtEntityTest.test(projectile, e));` | `LivingEntity target = LibEntityUtils.getAABBAngleTarget(eye, eye.add(owner.getForward().normalize().scale(range)), owner.level(), owner, range, maxAngle, e -> LibEntityUtils.canHitEntity(e, projectile.getOwner()));` |
| 66 | `AimUtils.AimHelperOptions aimHelperOptions = new AimUtils.AimHelperOptions(projectile)` | `AimUtils.AimHelperOptions aimHelperOptions = new AimUtils.AimHelperOptions(projectile)` |
| 69 | `projVel = AimUtils.helperAimEntity(firePos, target, aimHelperOptions);` | `projVel = AimUtils.helperAimEntity(firePos, target, aimHelperOptions);` |

### `util/generation/variant/StillGeneration.java`

- 1.20 对应：`org/confluence/mod/util/generation/variant/StillGeneration.java`
- TE import：`TEUtils`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 34 | `pos = pos.add(TEUtils.getPlayerHandPos(player));` | `pos = pos.add(LibEntityUtils.getPlayerHandPos(player));` |

### `util/track/variant/SimpleTrack.java`

- 1.20 对应：`org/confluence/mod/util/track/variant/SimpleTrack.java`
- TE import：`TEUtils`

| 行 | TE 代码 | 1.20 对侧 |
|---:|---|---|
| 39 | `return maxSpeed.map(speed -> TEUtils.interpolateSimple(currentDir, targetDir, currDirScaleFactor, homingPower, speed, minSpeed, currentDir))` | `return maxSpeed.map(speed -> LibMathUtils.interpolateSimple(currentDir, targetDir, currDirScaleFactor, homingPower, speed, minSpeed, currentDir))` |
| 40 | `.orElseGet(() -> TEUtils.interpolateSimple(currentDir, targetDir, currDirScaleFactor, homingPower, currentDir.length(), minSpeed, currentDir));` | `.orElseGet(() -> LibMathUtils.interpolateSimple(currentDir, targetDir, currDirScaleFactor, homingPower, currentDir.length(), minSpeed, currentDir));` |
