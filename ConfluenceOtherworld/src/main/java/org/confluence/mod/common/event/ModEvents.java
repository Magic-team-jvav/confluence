package org.confluence.mod.common.event;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
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
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforgespi.locating.IModFile;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.event.NameFixRegisterEvent;
import org.confluence.lib.util.ConfluenceResources;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.block.natural.ChlorophyteOreBlock;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.block.natural.MagicMailBox;
import org.confluence.mod.common.block.natural.StepRevealingBlock;
import org.confluence.mod.common.capability.FluidBottomlessBucketWrapper;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.data.saved.HardmodeConvertor;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.entity.TargetDummyEntity;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.ChestBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.integration.jei.RecipeTransferPacketC2S;
import org.confluence.mod.integration.terra_entity.TEItemComponentModify;
import org.confluence.mod.integration.terra_entity.TERemoval;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import org.confluence.mod.network.c2s.*;
import org.confluence.mod.network.s2c.*;
import org.confluence.mod.util.DateUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.phase_journey.api.PhaseJourneyEvent;
import org.confluence.terra_curio.api.event.RegisterAccessoriesComponentUpdateEvent;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTabs;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.Optional;

import static org.confluence.mod.Confluence.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModEvents {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
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
    public static void modConfig$Loading(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON && MODID.equals(event.getConfig().getModId())) {
            CommonConfigs.onLoad();
        }
    }

    @SubscribeEvent
    public static void modConfig$Reloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON && MODID.equals(event.getConfig().getModId())) {
            CommonConfigs.onLoad();
            CompatibilitySyncPacketS2c.sendToAll();
        }
    }

    @SubscribeEvent
    public static void loadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            LogBlockSet.wrapStrip();
            LogBlockSet.setFlammable();
            ModRecipes.Brewing.initialize();
            ModUtils.registerCauldronInteractions();
            TERemoval.redirectLootTable();
            MagicMailBox.registerVariants();
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
        registrar.playToClient(KillBoardSyncPacketS2C.TYPE, KillBoardSyncPacketS2C.STREAM_CODEC, KillBoardSyncPacketS2C::handle);
        registrar.playToClient(ManaPacketS2C.TYPE, ManaPacketS2C.STREAM_CODEC, ManaPacketS2C::handle);
        registrar.playToClient(MeteoriteLocationPacketS2C.TYPE, MeteoriteLocationPacketS2C.STREAM_CODEC, MeteoriteLocationPacketS2C::handle);
        registrar.playToClient(OpenSelectionsScreenPacketS2C.TYPE, OpenSelectionsScreenPacketS2C.STREAM_CODEC, OpenSelectionsScreenPacketS2C::handle);
        registrar.playToClient(PlayerDeathInfoPacketS2C.TYPE, PlayerDeathInfoPacketS2C.STREAM_CODEC, PlayerDeathInfoPacketS2C::handle);
        registrar.playToClient(SecretFlagSyncPacketS2C.TYPE, SecretFlagSyncPacketS2C.STREAM_CODEC, SecretFlagSyncPacketS2C::handle);
        registrar.playToClient(StarPhasesPacketS2C.TYPE, StarPhasesPacketS2C.STREAM_CODEC, StarPhasesPacketS2C::handle);
        registrar.playToClient(WindSpeedPacketS2C.TYPE, WindSpeedPacketS2C.STREAM_CODEC, WindSpeedPacketS2C::handle);
        registrar.playToClient(AchievementOffsetSyncPacketS2C.TYPE, AchievementOffsetSyncPacketS2C.STREAM_CODEC, AchievementOffsetSyncPacketS2C::handle);
        registrar.playToClient(CompatibilitySyncPacketS2c.TYPE, CompatibilitySyncPacketS2c.STREAM_CODEC, CompatibilitySyncPacketS2c::handle);
        registrar.playToClient(PiggyBankTotalMoneyPacket.TYPE, PiggyBankTotalMoneyPacket.STREAM_CODEC, PiggyBankTotalMoneyPacket::handle);
        registrar.playToClient(DropletsSyncPacketS2C.TYPE, DropletsSyncPacketS2C.STREAM_CODEC, DropletsSyncPacketS2C::handle);

        registrar.playToServer(ApplySelectionPacketC2S.TYPE, ApplySelectionPacketC2S.STREAM_CODEC, ApplySelectionPacketC2S::handle);
        registrar.playToServer(HookThrowingPacketC2S.TYPE, HookThrowingPacketC2S.STREAM_CODEC, HookThrowingPacketC2S::handle);
        registrar.playToServer(KeyRequestPacketC2S.TYPE, KeyRequestPacketC2S.STREAM_CODEC, KeyRequestPacketC2S::handle);
        registrar.playToServer(OpenMenuPacketC2S.TYPE, OpenMenuPacketC2S.STREAM_CODEC, OpenMenuPacketC2S::handle);
        registrar.playToServer(SwordShootingPacketC2S.TYPE, SwordShootingPacketC2S.STREAM_CODEC, SwordShootingPacketC2S::handle);
        registrar.playToServer(WormholeToPlayerPacketC2S.TYPE, WormholeToPlayerPacketC2S.STREAM_CODEC, WormholeToPlayerPacketC2S::handle);
        registrar.playToServer(SellTradePacketC2S.TYPE, SellTradePacketC2S.STREAM_CODEC, SellTradePacketC2S::handle);
        registrar.playToServer(RecipeTransferPacketC2S.TYPE, RecipeTransferPacketC2S.STREAM_CODEC, RecipeTransferPacketC2S::handle);
        registrar.playToServer(LanceAttackPacketC2S.TYPE, LanceAttackPacketC2S.STREAM_CODEC, LanceAttackPacketC2S::handle);
        registrar.playToServer(SwitchEffectEnabledPackedC2S.TYPE, SwitchEffectEnabledPackedC2S.STREAM_CODEC, SwitchEffectEnabledPackedC2S::handle);
        registrar.playToServer(DyeMixPacketC2S.TYPE, DyeMixPacketC2S.STREAM_CODEC, DyeMixPacketC2S::handle);
        WaystonesHelper.registerPayload(registrar);
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
        event.register(AccessoryItems.CLOTHIER$KILLER);
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
        } else if (event.getTab() == ModTabs.MISC.get()) {
            ItemStack clothierVoodooDollStack = AccessoryItems.CLOTHIER_VOODOO_DOLL.toStack();
            event.insertAfter(ConsumableItems.ABEEMINATION.toStack(), clothierVoodooDollStack, visibility);
            event.insertAfter(clothierVoodooDollStack, AccessoryItems.GUIDE_VOODOO_DOLL.toStack(), visibility);
        }
    }

    /**
     * @see ConfluenceData#increaseRevealStep
     */
    @SubscribeEvent
    public static void phaseJourney$Register(PhaseJourneyEvent.Register event) {
        BlockState deepslate = Blocks.DEEPSLATE.defaultBlockState();
        int step = 0;
        for (int state = 0; state < 3; state++) {
            int finalState = state;
            event.phaseRegister(Confluence.asResource("reveal_step_" + (step++)), context -> {
                context.blockReplacement(OreBlocks.DEEPSLATE_COBALT_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), deepslate);
                context.blockReplacement(OreBlocks.DEEPSLATE_PALLADIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), deepslate);
            });
            event.phaseRegister(Confluence.asResource("reveal_step_" + (step++)), context -> {
                context.blockReplacement(OreBlocks.DEEPSLATE_MYTHRIL_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), deepslate);
                context.blockReplacement(OreBlocks.DEEPSLATE_ORICHALCUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), deepslate);
            });
            event.phaseRegister(Confluence.asResource("reveal_step_" + (step++)), context -> {
                context.blockReplacement(OreBlocks.DEEPSLATE_ADAMANTITE_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), deepslate);
                context.blockReplacement(OreBlocks.DEEPSLATE_TITANIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), deepslate);
            });
        }

        event.phaseRegister(ChlorophyteOreBlock.PHASE, context -> context.blockReplacement(OreBlocks.CHLOROPHYTE_ORE.get(), Blocks.MUD));
    }

    @SubscribeEvent
    public static void blockEntityTypeAddBlocks(BlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.BRUSHABLE_BLOCK, OreBlocks.OPAL_ORE.get());
        event.modify(BlockEntityType.SIGN, LogBlockSet.getSignBlocks());
        event.modify(BlockEntityType.HANGING_SIGN, LogBlockSet.getHangingSignBlocks());
        event.modify(BlockEntityType.SCULK_SENSOR, FunctionalBlocks.SCULK_TRAP.get());
        event.modify(BlockEntityType.CAMPFIRE, FunctionalBlocks.LIFE_CAMPFIRE.get());
    }

    @SubscribeEvent
    public static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        TEItemComponentModify.modifyDefaultComponents(event);
        event.modify(Items.SNOWBALL, builder -> builder.set(DataComponents.MAX_STACK_SIZE, LibUtils.MAX_STACK_SIZE));
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(Capabilities.ItemHandler.BLOCK, (level, pos, state, blockEntity, side) -> {
            if (state.hasProperty(StateProperties.UNLOCKED) && !state.getValue(StateProperties.UNLOCKED)) return null;
            Container container = ChestBlock.getContainer((ChestBlock) state.getBlock(), state, level, pos, true);
            return container == null ? null : new InvWrapper(container);
        }, ChestBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get).toArray(Block[]::new));

//        List<BlockEntityType<? extends BaseContainerBlockEntity>> invBlockEntities = List.of(
//                FunctionalBlocks.ALTAR_BLOCK_ENTITY.get(),
//                FunctionalBlocks.CAULDRON_ENTITY.get(),
//                FunctionalBlocks.TREE_HOLES_ENTITY.get()
//        );
//        for (BlockEntityType<? extends BaseContainerBlockEntity> type : invBlockEntities) {
//            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type, (container, side) -> new InvWrapper(container));
//        }
//        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FunctionalBlocks.HELLFORGE_ENTITY.get(), SidedInvWrapper::new);

        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBottomlessBucketWrapper(stack),
                ToolItems.BOTTOMLESS_WATER_BUCKET,
                ToolItems.BOTTOMLESS_LAVA_BUCKET,
                ToolItems.BOTTOMLESS_HONEY_BUCKET,
                ToolItems.BOTTOMLESS_SHIMMER_BUCKET
        );
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ToolItems.HONEY_BUCKET);

//        event.registerEntity(Capabilities.ItemHandler.ENTITY, EntityType.PLAYER, (player, context) -> player.getData(ModAttachmentTypes.EXTRA_INVENTORY));
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

    @SubscribeEvent
    public static void blockWithItemNameFixRegister(NameFixRegisterEvent.BlockWithItem event) {
        event
                // 1.1.2 -> 1.1.3
                .register("confluence:freeze_crate", "confluence:frozen_crate")
                .register("confluence:ebony_stone", "confluence:ebonstone")
                .register("confluence:pearl_stone", "confluence:pearlstone")
                .register("confluence:tr_crimson_stone", "confluence:crimstone")
                .register("confluence:ebony_cobblestone", "confluence:cobbled_ebonstone")
                .register("confluence:pearl_cobblestone", "confluence:cobbled_pearlstone")
                .register("confluence:tr_crimson_cobblestone", "confluence:cobbled_crimstone")
                .register("confluence:ebony_sandstone", "confluence:ebonsandstone")
                .register("confluence:tr_crimson_sandstone", "confluence:crimsandstone")
                .register("confluence:pearl_sandstone", "confluence:pearlsandstone")
                .register("confluence:ebony_sand", "confluence:ebonsand")
                .register("confluence:pearl_sand", "confluence:pearlsand")
                .register("confluence:crimson_sand", "confluence:crimsand")
                .register("confluence:ebony_sand_layer_block", "confluence:ebonsand_layer_block")
                .register("confluence:tr_crimson_sand_layer_block", "confluence:crimsand_layer_block")
                .register("confluence:pearl_sand_layer_block", "confluence:pearlsand_layer_block")
                .register("confluence:red_hardened_sand_block", "confluence:hardened_red_sand_block")
                .register("confluence:ebony_hardened_sand_block", "confluence:hardened_ebonsand_block")
                .register("confluence:pearl_hardened_sand_block", "confluence:hardened_pearlsand_block")
                .register("confluence:tr_crimson_hardened_sand_block", "confluence:hardened_crimsand_block")
                .register("confluence:ebony_moist_sand_block", "confluence:moistened_ebonsand_block")
                .register("confluence:pearl_moist_sand_block", "confluence:moistened_pearlsand_block")
                .register("confluence:tr_crimson_moist_sand_block", "confluence:moistened_crimsand_block")
                .register("confluence:moist_sand_block", "confluence:moistened_sand_block")
                .register("confluence:red_moist_sand_block", "confluence:moistened_red_sand_block")
                .register("confluence:tr_lava_bricks", "confluence:hellstone_bricks")
                .register("confluence:tr_amethyst_ore", "confluence:amethyst_ore")
                .register("confluence:deepslate_tr_amethyst_ore", "confluence:deepslate_amethyst_ore")
                .register("confluence:sanctification_tr_amethyst_ore", "confluence:sanctification_amethyst_ore")
                .register("confluence:corruption_tr_amethyst_ore", "confluence:corruption_amethyst_ore")
                .register("confluence:fleshification_tr_amethyst_ore", "confluence:fleshification_amethyst_ore")
                .register("confluence:tr_crimson_ore", "confluence:crimtane_ore")
                .register("confluence:deepslate_tr_crimson_ore", "confluence:deepslate_crimtane_ore")
                .register("confluence:sanctification_tr_crimson_ore", "confluence:sanctification_crimtane_ore")
                .register("confluence:corruption_tr_crimson_ore", "confluence:corruption_crimtane_ore")
                .register("confluence:fleshification_tr_crimson_ore", "confluence:fleshification_crimtane_ore")
                .register("confluence:raw_tr_crimson_block", "confluence:raw_crimtane_block")
                .register("confluence:tr_crimson_block", "confluence:crimtane_block")
                .register("confluence:tr_crimson_grass_block", "confluence:crimson_grass_block")
                .register("confluence:tr_crimson_jungle_grass_block", "confluence:crimson_jungle_grass_block")
                .register("confluence:tr_crimson_drooping_vine", "confluence:crimson_drooping_vine")
                .register("confluence:tr_crimson_grass", "confluence:crimson_grass")
                .register("confluence:tr_crimson_cattails_body", "confluence:crimson_cattails_body")
                .register("confluence:tr_crimson_cattails_head", "confluence:crimson_cattails_head")
                .register("confluence:tr_crimson_pot", "confluence:crimson_pot")
                .register("confluence:tr_crimson_crate", "confluence:crimson_crate")
                .register("confluence:tr_crimson_cattails", "confluence:crimson_cattails")
                .register("confluence:tr_crimson_ore_bricks", "confluence:crimtane_ore_bricks")
                .register("confluence:tr_crimson_rock_bricks", "confluence:crimstone_bricks")
                .register("confluence:tr_amethyst_branches", "confluence:amethyst_branches")
                .register("confluence:tr_amethyst_sapling", "confluence:amethyst_sapling")
                .register("confluence:tr_amethyst_block", "confluence:amethyst_block")
                .register("confluence:tr_polished_granite", "confluence:polished_granite")
                .register("confluence:tr_copper_bricks", "confluence:copper_bricks")
                .register("confluence:tr_gold_bricks", "confluence:golden_bricks")
                .register("confluence:tr_iron_bricks", "confluence:iron_bricks")
                .register("confluence:ebony_rock_bricks", "confluence:ebonstone_bricks")
                .register("confluence:pearl_rock_bricks", "confluence:pearlstone_bricks")
                .register("confluence:tr_obsidian_bricks", "confluence:obsidian_bricks")
                .register("confluence:tr_obsidian_small_bricks", "confluence:obsidian_small_bricks")
                .register("confluence:tr_smooth_obsidian", "confluence:smooth_obsidian")
                .register("confluence:tr_oak_planks", "confluence:chiseled_oak_planks")
                .register("confluence:tr_northland_planks", "confluence:chiseled_spruce_planks")
                .register("confluence:tr_granite_column", "confluence:granite_column")
                .register("confluence:tr_emerald_ore", "confluence:jade_ore")
                .register("confluence:deepslate_tr_emerald_ore", "confluence:deepslate_jade_ore")
                .register("confluence:sanctification_tr_emerald_ore", "confluence:sanctification_jade_ore")
                .register("confluence:corruption_tr_emerald_ore", "confluence:corruption_jade_ore")
                .register("confluence:fleshification_tr_emerald_ore", "confluence:fleshification_jade_ore")
                .register("confluence:tr_emerald_block", "confluence:jade_block")
                .register("confluence:emerald_branches", "confluence:jade_branches")
                .register("confluence:emerald_sapling", "confluence:jade_sapling")
                .register("confluence:emerald_chain", "confluence:jade_chain")
                // 1.1.4 -> 1.1.5
                .register("confluence:golden_coin", "confluence:gold_coin");

    }

    @SubscribeEvent
    public static void blockNameFixRegister(NameFixRegisterEvent.Block event) {
        event
                // 1.1.2 -> 1.1.3
                .register("confluence:copper_coin_pile", "confluence:copper_coin")
                .register("confluence:silver_coin_pile", "confluence:silver_coin")
                .register("confluence:golden_coin_pile", "confluence:golden_coin")
                .register("confluence:platinum_coin_pile", "confluence:platinum_coin")
                .register("confluence:emerald_coin_pile", "confluence:emerald_coin");
    }

    @SubscribeEvent
    public static void itemNameFixRegister(NameFixRegisterEvent.Item event) {
        event
                // 1.1.2 -> 1.1.3
                .register("confluence:copper_board_sword", "confluence:copper_broadsword")
                .register("confluence:tin_board_sword", "confluence:tin_broadsword")
                .register("confluence:lead_board_sword", "confluence:lead_broadsword")
                .register("confluence:silver_board_sword", "confluence:silver_broadsword")
                .register("confluence:tungsten_board_sword", "confluence:tungsten_broadsword")
                .register("confluence:golden_board_sword", "confluence:golden_broadsword")
                .register("confluence:platinum_board_sword", "confluence:platinum_broadsword")
                .register("confluence:tr_crimson_ingot", "confluence:crimtane_ingot")
                .register("confluence:raw_tr_crimson", "confluence:raw_crimtane")
                .register("confluence:tr_emerald", "confluence:jade")
                .register("confluence:emerald_minecart", "confluence:jade_minecart")
                .register("confluence:emerald_hook", "confluence:jade_hook")
                .register("confluence:emerald_staff", "confluence:jade_staff")
                .register("confluence:tr_amethyst", "confluence:amethyst")
                .register("confluence:tr_crimson_seed", "confluence:crimson_seed")
                .register("confluence:tr_clownfish", "confluence:clownfish")
                .register("confluence:tr_salmon", "confluence:salmon")
                .register("confluence:red_light_saber", "confluence:red_phaseblade")
                .register("confluence:orange_light_saber", "confluence:orange_phaseblade")
                .register("confluence:yellow_light_saber", "confluence:yellow_phaseblade")
                .register("confluence:green_light_saber", "confluence:green_phaseblade")
                .register("confluence:blue_light_saber", "confluence:blue_phaseblade")
                .register("confluence:purple_light_saber", "confluence:purple_phaseblade")
                .register("confluence:white_light_saber", "confluence:white_phaseblade")
                .register("confluence:demon_ocnch", "confluence:demon_conch")
                // 1.1.3 -> 1.1.4
                .register("confluence:night_edge", "confluence:nights_edge")
                // 1.1.4 -> 1.1.5
                .register("confluence:crystal_shards_item", "confluence:crystal_shards")
                .register("confluence:throwing_knives", "confluence:throwing_knive")
                // 1.1.5 -> 1.2.0
                .register("confluence:cap_tunabeard", "confluence:capn_tunabeard");
    }

    @SubscribeEvent
    public static void biomeNameFixRegister(NameFixRegisterEvent.Biome event) {
        event
                // 1.1.2 -> 1.1.3
                .register("confluence:tr_crimson", "confluence:the_crimson")
                .register("confluence:tr_crimson_desert", "confluence:the_crimson_desert")
                .register("confluence:tr_crimson_tundra", "confluence:the_crimson_tundra");
    }
}
