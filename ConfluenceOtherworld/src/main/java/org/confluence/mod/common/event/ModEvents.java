package org.confluence.mod.common.event;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.RegisterCauldronFluidContentEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforgespi.locating.IModFile;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.util.ConfluenceResources;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.block.common.AetheriumCauldronBlock;
import org.confluence.mod.common.block.common.BaseChestBlock;
import org.confluence.mod.common.block.common.HoneyCauldronBlock;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.block.natural.StepRevealingBlock;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.data.saved.HardmodeConvertor;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.entity.TargetDummyEntity;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.integration.jei.RecipeTransferPacketC2S;
import org.confluence.mod.integration.terra_entity.TEItemComponentModify;
import org.confluence.mod.integration.terra_entity.TERemoval;
import org.confluence.mod.network.c2s.*;
import org.confluence.mod.network.s2c.*;
import org.confluence.mod.util.DateUtils;
import org.confluence.phase_journey.api.PhaseJourneyEvent;
import org.confluence.terra_curio.api.event.RegisterAccessoriesComponentUpdateEvent;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTabs;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.Map;
import java.util.Optional;

import static org.confluence.mod.Confluence.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModEvents {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CommonConfigs.onLoad();
            ModGunProperties.init();
            Confluence.registerGameRules();
            ModFluids.registerInteraction();
            ModFluids.registerShimmerTransform();
            ModBiomes.registerRegionAndSurface();

            if (!ModList.get().isLoaded("attributefix")) {
                if (Attributes.ARMOR.value() instanceof RangedAttribute rangedAttribute) {
                    rangedAttribute.maxValue = 32768;
                }
                if (Attributes.ARMOR_TOUGHNESS.value() instanceof RangedAttribute rangedAttribute) {
                    rangedAttribute.maxValue = 32768;
                }
                if (Attributes.MAX_HEALTH.value() instanceof RangedAttribute rangedAttribute) {
                    rangedAttribute.maxValue = 32768;
                }
                if (Attributes.ATTACK_DAMAGE.value() instanceof RangedAttribute rangedAttribute) {
                    rangedAttribute.maxValue = 32768;
                }
            }
        });
    }

    @SubscribeEvent
    public static void loadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            LogBlockSet.wrapStrip();
            LogBlockSet.setFlammable();
            ISpreadable.Type.buildMap();
            ModRecipes.Brewing.initialize();
            CauldronInteraction.INTERACTIONS.values().forEach(map -> {
                Map<Item, CauldronInteraction> interactionMap = map.map();
                interactionMap.put(ToolItems.BOTTOMLESS_WATER_BUCKET.get(), CauldronInteraction.FILL_WATER);
                interactionMap.put(ToolItems.BOTTOMLESS_LAVA_BUCKET.get(), CauldronInteraction.FILL_LAVA);
                interactionMap.put(ToolItems.BOTTOMLESS_HONEY_BUCKET.get(), HoneyCauldronBlock.FILL_HONEY);
                interactionMap.put(ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get(), AetheriumCauldronBlock.FILL_AETHERIUM);
                interactionMap.put(ToolItems.HONEY_BUCKET.get(), HoneyCauldronBlock.FILL_HONEY);
                interactionMap.put(NatureBlocks.AETHERIUM_BLOCK.asItem(), AetheriumCauldronBlock.FILL_AETHERIUM);
            });
            TERemoval.redirectLootTable();
            IGlobalData.registerGlobalData(KillBoard.INSTANCE, HardmodeConvertor.INSTANCE, NPCSpawner.INSTANCE);
        });
    }

    @SubscribeEvent
    public static void registerCauldronFluidContent(RegisterCauldronFluidContentEvent event) {
        event.register(ModBlocks.HONEY_CAULDRON.get(), ModFluids.HONEY.fluid().get(), FluidType.BUCKET_VOLUME, null);
    }

    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            IModFile modFile = ModList.get().getModFileById(MODID).getFile();
            event.addRepositorySource(consumer -> {
                Pack pack = Pack.readMetaAndCreate(
                        new PackLocationInfo("confluence:terraria_art", Component.translatable("resourcepack.terraria_art"), PackSource.BUILT_IN, Optional.empty()),
                        new ConfluenceResources(modFile, "resourcepacks/terraria_art"),
                        PackType.CLIENT_RESOURCES,
                        new PackSelectionConfig(false, Pack.Position.TOP, false)
                );
                if (pack != null) consumer.accept(pack);
            });
            event.addRepositorySource(consumer -> {
                Pack pack = Pack.readMetaAndCreate(
                        new PackLocationInfo("confluence:terraria_armor", Component.translatable("resourcepack.terraria_armor"), PackSource.BUILT_IN, Optional.empty()),
                        new ConfluenceResources(modFile, "resourcepacks/terraria_armor"),
                        PackType.CLIENT_RESOURCES,
                        new PackSelectionConfig(false, Pack.Position.TOP, false)
                );
                if (pack != null) consumer.accept(pack);
            });
            event.addRepositorySource(consumer -> {
                Pack pack = Pack.readMetaAndCreate(
                        new PackLocationInfo("confluence:otherworldly_music", Component.translatable("resourcepack.otherworldly_music"), PackSource.BUILT_IN, Optional.empty()),
                        new ConfluenceResources(modFile, "resourcepacks/otherworldly_music"),
                        PackType.CLIENT_RESOURCES,
                        new PackSelectionConfig(false, Pack.Position.TOP, false)
                );
                if (pack != null) consumer.accept(pack);
            });
        }
    }

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(BrushingColorPacketS2C.TYPE, BrushingColorPacketS2C.STREAM_CODEC, BrushingColorPacketS2C::handle);
        registrar.playToClient(DeathMotionPacketS2C.TYPE, DeathMotionPacketS2C.STREAM_CODEC, DeathMotionPacketS2C::handle);
        registrar.playToClient(VisibilityPacketS2C.TYPE, VisibilityPacketS2C.STREAM_CODEC, VisibilityPacketS2C::handle);
        registrar.playToClient(ExtraInventoryStackPacketS2C.TYPE, ExtraInventoryStackPacketS2C.STREAM_CODEC, ExtraInventoryStackPacketS2C::handle);
        registrar.playToClient(ExtraInventorySyncPacketS2C.TYPE, ExtraInventorySyncPacketS2C.STREAM_CODEC, ExtraInventorySyncPacketS2C::handle);
        registrar.playToClient(FishingPowerInfoPacketS2C.TYPE, FishingPowerInfoPacketS2C.STREAM_CODEC, FishingPowerInfoPacketS2C::handle);
        registrar.playToClient(GamePhasePacketS2C.TYPE, GamePhasePacketS2C.STREAM_CODEC, GamePhasePacketS2C::handle);
        registrar.playToClient(ManaPacketS2C.TYPE, ManaPacketS2C.STREAM_CODEC, ManaPacketS2C::handle);
        registrar.playToClient(MeteoriteLocationPacketS2C.TYPE, MeteoriteLocationPacketS2C.STREAM_CODEC, MeteoriteLocationPacketS2C::handle);
        registrar.playToClient(OpenSelectionsScreenPacketS2C.TYPE, OpenSelectionsScreenPacketS2C.STREAM_CODEC, OpenSelectionsScreenPacketS2C::handle);
        registrar.playToClient(PlayerDeathInfoPacketS2C.TYPE, PlayerDeathInfoPacketS2C.STREAM_CODEC, PlayerDeathInfoPacketS2C::handle);
        registrar.playToClient(SecretFlagSyncPacketS2C.TYPE, SecretFlagSyncPacketS2C.STREAM_CODEC, SecretFlagSyncPacketS2C::handle);
        registrar.playToClient(StarPhasesPacketS2C.TYPE, StarPhasesPacketS2C.STREAM_CODEC, StarPhasesPacketS2C::handle);
        registrar.playToClient(WindSpeedPacketS2C.TYPE, WindSpeedPacketS2C.STREAM_CODEC, WindSpeedPacketS2C::handle);
        registrar.playToClient(AchievementOffsetSyncPacketS2C.TYPE, AchievementOffsetSyncPacketS2C.STREAM_CODEC, AchievementOffsetSyncPacketS2C::handle);

        registrar.playToServer(ApplySelectionPacketC2S.TYPE, ApplySelectionPacketC2S.STREAM_CODEC, ApplySelectionPacketC2S::handle);
        registrar.playToServer(HookThrowingPacketC2S.TYPE, HookThrowingPacketC2S.STREAM_CODEC, HookThrowingPacketC2S::handle);
        registrar.playToServer(KeyRequestPacketC2S.TYPE, KeyRequestPacketC2S.STREAM_CODEC, KeyRequestPacketC2S::handle);
        registrar.playToServer(OpenMenuPacketC2S.TYPE, OpenMenuPacketC2S.STREAM_CODEC, OpenMenuPacketC2S::handle);
        registrar.playToServer(SwordShootingPacketC2S.TYPE, SwordShootingPacketC2S.STREAM_CODEC, SwordShootingPacketC2S::handle);
        registrar.playToServer(WormholeToPlayerPacketC2S.TYPE, WormholeToPlayerPacketC2S.STREAM_CODEC, WormholeToPlayerPacketC2S::handle);
        registrar.playToServer(SellTradePacketC2S.TYPE, SellTradePacketC2S.STREAM_CODEC, SellTradePacketC2S::handle);
        registrar.playToServer(RecipeTransferPacketC2S.TYPE, RecipeTransferPacketC2S.STREAM_CODEC, RecipeTransferPacketC2S::handle);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.TARGET_DUMMY.get(), TargetDummyEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerUnitType(RegisterAccessoriesComponentUpdateEvent.UnitType event) {
        event.register(AccessoryItems.LUCKY$COIN);
        event.register(AccessoryItems.VINE$ROPE);
        event.register(AccessoryItems.AUTO$GET$MANA);
        event.register(AccessoryItems.HURT$GET$MANA);
        event.register(AccessoryItems.FAST$MANA$GENERATION);
        event.register(AccessoryItems.HIGH$TEST$FISHING$LINE);
        event.register(AccessoryItems.TACKLE$BOX);
        event.register(AccessoryItems.LAVAPROOF$FISHING$HOOK);
        event.register(AccessoryItems.SPECTRE$GOGGLES);
        event.register(AccessoryItems.PAINT$SPRAYER);
    }

    @SubscribeEvent
    public static void registerOtherType(RegisterAccessoriesComponentUpdateEvent.OtherType event) {
        event.register(AccessoryItems.ADDITIONAL$MANA);
        event.register(AccessoryItems.MANA$USE$REDUCE);
        event.register(AccessoryItems.MANA$PICKUP$RANGE);
        event.register(AccessoryItems.COIN$PICKUP$RANGE);
        event.register(AccessoryItems.REDUCE$HEALING$COOLDOWN);
        event.register(AccessoryItems.FISHING$POWER);
        event.register(AccessoryItems.SPECIAL$PRICE);
    }

    @SuppressWarnings("all")
    @SubscribeEvent
    public static void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        CreativeModeTab.TabVisibility visibility = CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
        if (event.getTab() == TCTabs.ACCESSORIES.get()) {
            event.accept(TCItems.EVERLASTING.get().getDefaultInstance(), visibility);
            event.accept(TCItems.BASE_POINT.get().getDefaultInstance(), visibility);
            AccessoryItems.ITEMS.getEntries().forEach(item -> event.accept(item.get()));
        }
    }

    /**
     * @see ConfluenceData#increaseRevealStep
     */
    @SubscribeEvent
    public static void phaseJourney$Register(PhaseJourneyEvent.Register event) {
        BlockState target = Blocks.DEEPSLATE.defaultBlockState();
        int step = 0;
        for (int state = 0; state < 3; state++) {
            int finalState = state;
            event.phaseRegister(Confluence.asResource("reveal_step_" + (step++)), context -> {
                context.blockReplacement(OreBlocks.DEEPSLATE_COBALT_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), target);
                context.blockReplacement(OreBlocks.DEEPSLATE_PALLADIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), target);
            });
            event.phaseRegister(Confluence.asResource("reveal_step_" + (step++)), context -> {
                context.blockReplacement(OreBlocks.DEEPSLATE_MYTHRIL_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), target);
                context.blockReplacement(OreBlocks.DEEPSLATE_ORICHALCUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), target);
            });
            event.phaseRegister(Confluence.asResource("reveal_step_" + (step++)), context -> {
                context.blockReplacement(OreBlocks.DEEPSLATE_ADAMANTITE_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), target);
                context.blockReplacement(OreBlocks.DEEPSLATE_TITANIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), target);
            });
        }
    }

    @SubscribeEvent
    public static void blockEntityTypeAddBlocks(BlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.BRUSHABLE_BLOCK, OreBlocks.OPAL_ORE.get());
        event.modify(BlockEntityType.SIGN, LogBlockSet.getSignBlocks());
        event.modify(BlockEntityType.SCULK_SENSOR, FunctionalBlocks.SCULK_TRAP.get());
        event.modify(BlockEntityType.CAMPFIRE, FunctionalBlocks.LIFE_CAMPFIRE.get());
    }

    @SubscribeEvent
    public static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        TEItemComponentModify.modifyDefaultComponents(event);
        event.modify(Items.SNOWBALL, builder -> builder.set(DataComponents.MAX_STACK_SIZE, 9999));
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(Capabilities.ItemHandler.BLOCK, (level, pos, state, blockEntity, side) -> {
            if (!state.getValue(BaseChestBlock.UNLOCKED)) return null;
            Container container = ChestBlock.getContainer((ChestBlock) state.getBlock(), state, level, pos, true);
            if (container == null) return null;
            return new InvWrapper(container);
        }, ChestBlocks.GOLDEN_CHEST.get(), ChestBlocks.DEATH_GOLDEN_CHEST.get());
        event.registerBlock(Capabilities.ItemHandler.BLOCK, (level, pos, state, blockEntity, context) -> {
            if (blockEntity instanceof AltarBlock.Entity entity) {
                return new InvWrapper(entity);
            }
            return null;
        }, FunctionalBlocks.DEMON_ALTAR.get(), FunctionalBlocks.CRIMSON_ALTAR.get());
    }

    @SubscribeEvent
    public static void registerSpawnReplacements(RegisterSpawnPlacementsEvent event) {
        event.register(TEMonsterEntities.GREEN_DUMPLING_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, serverLevel, spawnType, pos, random) -> {
            if (DateUtils.isQingMing(DateUtils.getLunar()) && serverLevel instanceof Level level) {
                int y = pos.getY();
                return y > 30 && y < 260 && level.isDay() && serverLevel.canSeeSky(pos);
            }
            return false;
        }, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
