package org.confluence.mod.common.event;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
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
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.RegisterCauldronFluidContentEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforgespi.locating.IModFile;
import org.confluence.lib.api.event.CustomGroupItemIconEvent;
import org.confluence.lib.api.event.NameFixRegisterEvent;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.util.ConfluenceResources;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.WipNotDisplayOutput;
import org.confluence.mod.Confluence;
import org.confluence.mod.StartupConfigs;
import org.confluence.mod.api.event.RegisterEvilMaterialReplacesEvent;
import org.confluence.mod.api.event.bestiary.RegisterBestiaryKeyEvent;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.block.natural.MagicMailBox;
import org.confluence.mod.common.capability.FluidBottomlessBucketWrapper;
import org.confluence.mod.common.data.saved.*;
import org.confluence.mod.common.entity.InverseEnderMan;
import org.confluence.mod.common.entity.InverseEntityType;
import org.confluence.mod.common.entity.RainbowSheep;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.integration.jei.RecipeTransferPacketC2S;
import org.confluence.mod.integration.terra_entity.TEEvents;
import org.confluence.mod.integration.terra_entity.TEHelper;
import org.confluence.mod.integration.terra_entity.TEItemComponentModify;
import org.confluence.mod.network.AskForSoftcorePacket;
import org.confluence.mod.network.TeamPacket;
import org.confluence.mod.network.c2s.*;
import org.confluence.mod.network.s2c.*;
import org.confluence.mod.network.task.AchievementsTask;
import org.confluence.mod.network.task.ReplyAchievementsPacketC2S;
import org.confluence.mod.network.task.RequestAchievementsPacketS2C;
import org.confluence.mod.util.DateUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.RepeaterContentsComponentHandler;
import org.confluence.terra_curio.api.event.RegisterAccessoriesComponentUnitValueTypeLocalSyncEvent;
import org.confluence.terra_curio.api.event.RegisterAccessoriesComponentUpdateEvent;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTabs;
import org.confluence.terraentity.init.entity.TEAnimals;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.mixed.IZombie;

import java.util.Optional;
import java.util.function.Function;

import static org.confluence.mod.Confluence.MODID;

@EventBusSubscriber(modid = MODID)
public final class ModEvents {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModGunProperties.init();
            Confluence.registerGameRules();
            ModFluids.registerInteraction();
            ModFluids.registerShimmerTransform();
            ModBiomes.registerRegionAndSurface();
            if (StartupConfigs.forceAllowWipItemsDisplayInCreativeModeTab()) {
                WipNotDisplayOutput.forceAllow();
            }

            if (!LibUtils.isModLoaded("attributefix")) {
                if (Attributes.ARMOR.value() instanceof RangedAttribute rangedAttribute) {
                    rangedAttribute.maxValue = 65536;
                }
                if (Attributes.ARMOR_TOUGHNESS.value() instanceof RangedAttribute rangedAttribute) {
                    rangedAttribute.maxValue = 65536;
                }
                if (Attributes.MAX_HEALTH.value() instanceof RangedAttribute rangedAttribute) {
                    rangedAttribute.maxValue = 65536;
                }
                if (LibAttributes.getAttackDamage().value() instanceof RangedAttribute rangedAttribute) {
                    rangedAttribute.maxValue = 65536;
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
            TEHelper.redirectLootTable();
            MagicMailBox.registerVariants();
            ModArmorBonus.registerArmorSetBonus();
            IGlobalData.registerGlobalData(
                    KillBoard.INSTANCE,
                    HardmodeConvertor.INSTANCE,
                    NPCSpawner.INSTANCE,
                    Bestiary.INSTANCE,
                    GlobalCloakData.INSTANCE,
                    GameEventSystem.INSTANCE
//                    HouseHandler.INSTANCE
            );
            GlobalCloakData.INSTANCE.initialize();
            ModLoader.postEvent(new RegisterEvilMaterialReplacesEvent());
            DispenserRegistration.boostrap();
        });
    }

    @SubscribeEvent
    public static void registerCauldronFluidContent(RegisterCauldronFluidContentEvent event) {
        event.register(ModBlocks.HONEY_CAULDRON.get(), ModFluids.HONEY.fluid().get(), FluidType.BUCKET_VOLUME, null);
        event.register(ModBlocks.AETHERIUM_CAULDRON.get(), ModFluids.SHIMMER.fluid().get(), FluidType.BUCKET_VOLUME, null);
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
        event.registrar("1")
                .playToClient(BrushingColorPacketS2C.TYPE, BrushingColorPacketS2C.STREAM_CODEC, BrushingColorPacketS2C::handle)
                .playToClient(DeathMotionPacketS2C.TYPE, DeathMotionPacketS2C.STREAM_CODEC, DeathMotionPacketS2C::handle)
                .playToClient(VisibilityPacketS2C.TYPE, VisibilityPacketS2C.STREAM_CODEC, VisibilityPacketS2C::handle)
                .playToClient(ExtraInventoryStackPacketS2C.TYPE, ExtraInventoryStackPacketS2C.STREAM_CODEC, ExtraInventoryStackPacketS2C::handle)
                .playToClient(ExtraInventorySyncPacketS2C.TYPE, ExtraInventorySyncPacketS2C.STREAM_CODEC, ExtraInventorySyncPacketS2C::handle)
                .playToClient(FishingPowerInfoPacketS2C.TYPE, FishingPowerInfoPacketS2C.STREAM_CODEC, FishingPowerInfoPacketS2C::handle)
                .playToClient(KillBoardSyncPacketS2C.TYPE, KillBoardSyncPacketS2C.STREAM_CODEC, KillBoardSyncPacketS2C::handle)
                .playToClient(ManaPacketS2C.TYPE, ManaPacketS2C.STREAM_CODEC, ManaPacketS2C::handle)
//                .playToClient(SoulPacketS2C.TYPE, SoulPacketS2C.STREAM_CODEC, SoulPacketS2C::handle)
                .playToClient(MeteoriteLocationPacketS2C.TYPE, MeteoriteLocationPacketS2C.STREAM_CODEC, MeteoriteLocationPacketS2C::handle)
                .playToClient(OpenSelectionsScreenPacketS2C.TYPE, OpenSelectionsScreenPacketS2C.STREAM_CODEC, OpenSelectionsScreenPacketS2C::handle)
                .playToClient(PlayerDeathInfoPacketS2C.TYPE, PlayerDeathInfoPacketS2C.STREAM_CODEC, PlayerDeathInfoPacketS2C::handle)
                .playToClient(SecretFlagSyncPacketS2C.TYPE, SecretFlagSyncPacketS2C.STREAM_CODEC, SecretFlagSyncPacketS2C::handle)
                .playToClient(StarPhasesPacketS2C.TYPE, StarPhasesPacketS2C.STREAM_CODEC, StarPhasesPacketS2C::handle)
                .playToClient(WindSpeedPacketS2C.TYPE, WindSpeedPacketS2C.STREAM_CODEC, WindSpeedPacketS2C::handle)
                .playToClient(AchievementOffsetSyncPacketS2C.TYPE, AchievementOffsetSyncPacketS2C.STREAM_CODEC, AchievementOffsetSyncPacketS2C::handle)
                .playToClient(RepeaterShootingPayloadS2C.TYPE, RepeaterShootingPayloadS2C.STREAM_CODEC, RepeaterShootingPayloadS2C::handle)
                .playToClient(CompatibilitySyncPacketS2c.TYPE, CompatibilitySyncPacketS2c.STREAM_CODEC, CompatibilitySyncPacketS2c::handle)
                .playToClient(PiggyBankTotalMoneyPacket.TYPE, PiggyBankTotalMoneyPacket.STREAM_CODEC, PiggyBankTotalMoneyPacket::handle)
                .playToClient(DropletsSyncPacketS2C.TYPE, DropletsSyncPacketS2C.STREAM_CODEC, DropletsSyncPacketS2C::handle)
                .playToClient(BestiarySyncPacketS2C.TYPE, BestiarySyncPacketS2C.STREAM_CODEC, BestiarySyncPacketS2C::handle)
                .playToClient(AvailableHouseSelectPacketS2C.TYPE, AvailableHouseSelectPacketS2C.STREAM_CODEC, AvailableHouseSelectPacketS2C::handle)
                .playToClient(TerraStyleExplosionPacketS2C.TYPE, TerraStyleExplosionPacketS2C.STREAM_CODEC, TerraStyleExplosionPacketS2C::handle)
                .playToClient(FlushArmorSetBonusPacketS2C.TYPE, FlushArmorSetBonusPacketS2C.STREAM_CODEC, FlushArmorSetBonusPacketS2C::handle)
                .playToClient(GlobalCloakSyncPacketS2C.TYPE, GlobalCloakSyncPacketS2C.STREAM_CODEC, GlobalCloakSyncPacketS2C::handle)
                .playToClient(LucyTheAxeDialogPacketS2C.TYPE, LucyTheAxeDialogPacketS2C.STREAM_CODEC, LucyTheAxeDialogPacketS2C::handle)
                .playToClient(GameEventSyncPacketS2C.TYPE, GameEventSyncPacketS2C.STREAM_CODEC, GameEventSyncPacketS2C::handle)
                .playToClient(GoblinArmyProgressPacketS2C.TYPE, GoblinArmyProgressPacketS2C.STREAM_CODEC, GoblinArmyProgressPacketS2C::handle)
                .playToClient(SyncEnemyBannerEntriesPacketS2C.TYPE, SyncEnemyBannerEntriesPacketS2C.STREAM_CODEC, SyncEnemyBannerEntriesPacketS2C::handle)
                .playToClient(AchievementsDataSyncPacketS2C.TYPE, AchievementsDataSyncPacketS2C.STREAM_CODEC, AchievementsDataSyncPacketS2C::handle)
                .playToClient(DragonChargePlayerConfigPacketS2C.TYPE, DragonChargePlayerConfigPacketS2C.STREAM_CODEC, DragonChargePlayerConfigPacketS2C::handle)

                .playToServer(ApplySelectionPacketC2S.TYPE, ApplySelectionPacketC2S.STREAM_CODEC, ApplySelectionPacketC2S::handle)
                .playToServer(HookThrowingPacketC2S.TYPE, HookThrowingPacketC2S.STREAM_CODEC, HookThrowingPacketC2S::handle)
                .playToServer(KeyRequestPacketC2S.TYPE, KeyRequestPacketC2S.STREAM_CODEC, KeyRequestPacketC2S::handle)
                .playToServer(OpenMenuPacketC2S.TYPE, OpenMenuPacketC2S.STREAM_CODEC, OpenMenuPacketC2S::handle)
                .playToServer(WormholeToPlayerPacketC2S.TYPE, WormholeToPlayerPacketC2S.STREAM_CODEC, WormholeToPlayerPacketC2S::handle)
                .playToServer(SellTradePacketC2S.TYPE, SellTradePacketC2S.STREAM_CODEC, SellTradePacketC2S::handle)
                .playToServer(RecipeTransferPacketC2S.TYPE, RecipeTransferPacketC2S.STREAM_CODEC, RecipeTransferPacketC2S::handle)
                .playToServer(SpearAttackPacketC2S.TYPE, SpearAttackPacketC2S.STREAM_CODEC, SpearAttackPacketC2S::handle)
                .playToServer(SwitchEffectEnabledPackedC2S.TYPE, SwitchEffectEnabledPackedC2S.STREAM_CODEC, SwitchEffectEnabledPackedC2S::handle)
                .playToServer(DyeMixPacketC2S.TYPE, DyeMixPacketC2S.STREAM_CODEC, DyeMixPacketC2S::handle)
                .playToServer(HouseSelectPacketC2S.TYPE, HouseSelectPacketC2S.STREAM_CODEC, HouseSelectPacketC2S::handle)
                .playToServer(EmptyTargetSweepPacketC2S.TYPE, EmptyTargetSweepPacketC2S.STREAM_CODEC, EmptyTargetSweepPacketC2S::handle)
                .playToServer(SwordProjectilePacketC2S.TYPE, SwordProjectilePacketC2S.STREAM_CODEC, SwordProjectilePacketC2S::handle)
                .playToServer(GiveBannerPacketC2S.TYPE, GiveBannerPacketC2S.STREAM_CODEC, GiveBannerPacketC2S::handle)

                .playBidirectional(TeamPacket.TYPE, TeamPacket.STREAM_CODEC, TeamPacket::handle)
                .playBidirectional(AskForSoftcorePacket.TYPE, AskForSoftcorePacket.STREAM_CODEC, AskForSoftcorePacket::handle)

                .configurationToClient(RequestAchievementsPacketS2C.TYPE, RequestAchievementsPacketS2C.STREAM_CODEC, RequestAchievementsPacketS2C::handle)
                .configurationToServer(ReplyAchievementsPacketC2S.TYPE, ReplyAchievementsPacketC2S.STREAM_CODEC, ReplyAchievementsPacketC2S::handle)
        ;
    }

    @SubscribeEvent
    public static void registerConfigurationTasks(RegisterConfigurationTasksEvent event) {
        event.register(new AchievementsTask(event.getListener()));
    }

    @SubscribeEvent
    public static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BESTIARY_ENTRY_DISPLAY.get(), LivingEntity.createLivingAttributes().build());
        event.put(ModEntities.RAINBOW_SHEEP.get(), RainbowSheep.createAttributes().build());
        event.put(ModEntities.INVERSE_ENDERMAN.get(), InverseEnderMan.createAttributes().build());
    }

    @SubscribeEvent
    public static void entityAttributeModification(EntityAttributeModificationEvent event) {
        TEEvents.modifyAttributes(event);
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
        event.register(AccessoryItems.$AFK);
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

    @SubscribeEvent
    public static void registerAccessoriesComponentUnitValueTypeLocalSync(RegisterAccessoriesComponentUnitValueTypeLocalSyncEvent event) {
        AccessoryItems.AFK_INDEX = event.register(AccessoryItems.$AFK);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == TCTabs.ACCESSORIES.get()) {
            WipNotDisplayOutput output = new WipNotDisplayOutput(event);
            output.accept(TCItems.EVERLASTING);
            output.accept(TCItems.BASE_POINT);
            output.acceptAll(AccessoryItems.ITEMS);
        }
    }

    @SubscribeEvent
    public static void customGroupItemIcon(CustomGroupItemIconEvent event) {
        event.register(ModTabs.NATURAL_BLOCKS.getKey(), helper -> {
            helper.register(Confluence.asResource("ebony"), NatureBlocks.EBONY_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("pearl"), NatureBlocks.PEARL_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("shadow"), NatureBlocks.SHADOW_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("palm"), NatureBlocks.PALM_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("baobab"), NatureBlocks.BAOBAB_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("yellow_willow"), NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("spooky"), NatureBlocks.SPOOKY_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("living"), NatureBlocks.LIVING_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("living_mahogany"), NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("ash"), NatureBlocks.ASH_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("void"), NatureBlocks.VOID_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("gaze"), NatureBlocks.GAZE_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("moonglow_willow"), NatureBlocks.MOONGLOW_WILLOW_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("dynasty"), NatureBlocks.DYNASTY_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("pine"), NatureBlocks.PINE_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("fey"), NatureBlocks.FEY_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("stone_tree"), NatureBlocks.STONY_LOG.toStack());
            helper.register(Confluence.asResource("pot"), PotBlocks.FOREST_POT.toStack());

            helper.register(Confluence.asResource("natural_environment"), NatureBlocks.LIFE_CRYSTAL_BLOCK.toStack());
            helper.register(Confluence.asResource("corruption"), NatureBlocks.CORRUPT_GRASS_BLOCK.toStack());
            helper.register(Confluence.asResource("hallow"), NatureBlocks.HALLOW_GRASS_BLOCK.toStack());
            helper.register(Confluence.asResource("crimson"), NatureBlocks.CRIMSON_GRASS_BLOCK.toStack());
            helper.register(Confluence.asResource("mushroom"), NatureBlocks.MUSHROOM_GRASS_BLOCK.toStack());
            helper.register(Confluence.asResource("desert"), NatureBlocks.HARDENED_SAND_BLOCK.toStack());
            helper.register(Confluence.asResource("jungle"), NatureBlocks.JUNGLE_GRASS_BLOCK.toStack());
            helper.register(Confluence.asResource("end"), NatureBlocks.VOID_GRASS_BLOCK.toStack());
            helper.register(Confluence.asResource("nether"), NatureBlocks.ASH_GRASS_BLOCK.toStack());
            helper.register(Confluence.asResource("skyland"), NatureBlocks.CLOUD_BLOCK.toStack());
            helper.register(Confluence.asResource("snow"), NatureBlocks.THIN_ICE_BLOCK.toStack());
            helper.register(Confluence.asResource("ocean"), NatureBlocks.DIATOMACEOUS.toStack());
            helper.register(Confluence.asResource("crops"), NatureBlocks.WHITE_PUMPKIN.toStack());
            helper.register(Confluence.asResource("shimmer"), NatureBlocks.AETHERIUM_BLOCK.toStack());
            helper.register(Confluence.asResource("moss"), NatureBlocks.LAVA_MOSS.toStack());
            helper.register(Confluence.asResource("special_plants"), NatureBlocks.CRIMSON_VENUS_FLYTRAP_BLOCK.toStack());
            helper.register(Confluence.asResource("miscellaneous"), DecorativeBlocks.LOST_PAPER_BLOCK.toStack());
            helper.register(Confluence.asResource("sanctification_ores"), OreBlocks.SANCTIFICATION_DIAMOND_ORE.toStack());
            helper.register(Confluence.asResource("corruption_ores"), OreBlocks.CORRUPTION_DIAMOND_ORE.toStack());
            helper.register(Confluence.asResource("fleshification_ores"), OreBlocks.FLESHIFICATION_DIAMOND_ORE.toStack());
            helper.register(Confluence.asResource("normal_ores"), OreBlocks.PLATINUM_ORE.toStack());
            helper.register(Confluence.asResource("raw_ore_blocks"), OreBlocks.RAW_PLATINUM_BLOCK.toStack());
            helper.register(Confluence.asResource("ore_storage_blocks"), OreBlocks.PLATINUM_BLOCK.toStack());
        });

        event.register(ModTabs.BUILDING_BLOCKS.getKey(), helper -> {
            helper.register(Confluence.asResource("gloom_obsidian_bricks"), DecorativeBlocks.GLOOM_OBSIDIAN_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("blue_ice_bricks"), DecorativeBlocks.BLUE_ICE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("packed_ice_bricks"), DecorativeBlocks.PACKED_ICE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("sandstone_bricks"), DecorativeBlocks.SANDSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("red_sandstone_bricks"), DecorativeBlocks.RED_SANDSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("ebonsandstone_bricks"), DecorativeBlocks.EBONSANDSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("pearlsandstone_bricks"), DecorativeBlocks.PEARLSANDSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("crimsandstone_bricks"), DecorativeBlocks.CRIMSANDSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("snow_bricks"), DecorativeBlocks.SNOW_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("aetherium_bricks"), DecorativeBlocks.AETHERIUM_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("rainbow_bricks"), DecorativeBlocks.RAINBOW_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("copper_bricks"), DecorativeBlocks.COPPER_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("tin_bricks"), DecorativeBlocks.TIN_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("iron_bricks"), DecorativeBlocks.IRON_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("lead_bricks"), DecorativeBlocks.LEAD_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("silver_bricks"), DecorativeBlocks.SILVER_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("tungsten_bricks"), DecorativeBlocks.TUNGSTEN_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("golden_bricks"), DecorativeBlocks.GOLDEN_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("platinum_bricks"), DecorativeBlocks.PLATINUM_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("demonite_ore_bricks"), DecorativeBlocks.DEMONITE_ORE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("ebonstone_bricks"), DecorativeBlocks.EBONSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("meteorite_bricks"), DecorativeBlocks.METEORITE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("crimtane_ore_bricks"), DecorativeBlocks.CRIMTANE_ORE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("crimstone_bricks"), DecorativeBlocks.CRIMSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("pearlstone_bricks"), DecorativeBlocks.PEARLSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("sun_plate"), DecorativeBlocks.SUN_PLATE.FULL.toStack());
            helper.register(Confluence.asResource("disc_block"), DecorativeBlocks.DISC_BLOCK.FULL.toStack());
            helper.register(Confluence.asResource("moon_plate"), DecorativeBlocks.MOON_PLATE.FULL.toStack());
            helper.register(Confluence.asResource("obsidian_bricks"), DecorativeBlocks.OBSIDIAN_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("granite_bricks"), DecorativeBlocks.GRANITE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("marble_bricks"), DecorativeBlocks.MARBLE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("blue_bricks"), DecorativeBlocks.BLUE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("green_bricks"), DecorativeBlocks.GREEN_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("pink_bricks"), DecorativeBlocks.PINK_BRICKS.FULL.toStack());

            helper.register(Confluence.asResource("glass"), DecorativeBlocks.PURE_GLASS.toStack());
            helper.register(Confluence.asResource("special_building"), DecorativeBlocks.SWORD_IN_STONE.toStack());
            helper.register(Confluence.asResource("chains"), DecorativeBlocks.DIAMOND_CHAIN.toStack());
            helper.register(Confluence.asResource("doors"), DecorativeBlocks.TRADITIONAL_DYNASTY_DOOR.toStack());
            helper.register(Confluence.asResource("statue"), StatueBlocks.A_STATUE.toStack());
            helper.register(Confluence.asResource("boss_relics"), DecorativeBlocks.KING_SLIME_RELIC.toStack());
            helper.register(Confluence.asResource("balloons"), DecorativeBlocks.RED_BALLOON.toStack());
            helper.register(Confluence.asResource("gem_blocks"), DecorativeBlocks.RUBY_BLOCK.toStack());
            helper.register(Confluence.asResource("fur_wool"), DecorativeBlocks.RAINBOW_WOOL.toStack());
        });

        event.register(ModTabs.MECHANICAL.getKey(), helper -> {
            helper.register(Confluence.asResource("boulders"), FunctionalBlocks.NORMAL_BOULDER.toStack());
            helper.register(Confluence.asResource("redstone_circuit_traps"), FunctionalBlocks.DART_TRAP.toStack());
            helper.register(Confluence.asResource("trigger"), FunctionalBlocks.SWITCH.toStack());
            helper.register(Confluence.asResource("crafting_stations"), FunctionalBlocks.HEAVY_WORK_BENCH.toStack());
            helper.register(Confluence.asResource("storage"), ChestBlocks.ICE_CHEST.toStack());
            helper.register(Confluence.asResource("souls"), FunctionalBlocks.SOUL_OF_LIGHT_IN_A_BOTTLE.toStack());
            helper.register(Confluence.asResource("misc_functional"), FunctionalBlocks.SPIKE.toStack());
        });

        event.register(ModTabs.MATERIALS.getKey(), helper -> {
            helper.register(Confluence.asResource("metal_materials"), MaterialItems.PLATINUM_INGOT.toStack());
            helper.register(Confluence.asResource("natural_materials"), MaterialItems.RUBY.toStack());
            helper.register(Confluence.asResource("souls_special"), MaterialItems.SOUL_OF_LIGHT.toStack());
            helper.register(Confluence.asResource("monster_drops"), MaterialItems.LENS.toStack());
            helper.register(Confluence.asResource("plants_herbs"), MaterialItems.DAYBLOOM.toStack());
            helper.register(Confluence.asResource("crafting_materials"), MaterialItems.SILK.toStack());
        });

        event.register(ModTabs.MISC.getKey(), helper -> {
            helper.register(Confluence.asResource("treasure_bag"), TreasureBagItems.KING_SLIME_TREASURE_BAG.toStack());
            helper.register(Confluence.asResource("tombstone"), ModBlocks.TOMBSTONE.toStack());
            helper.register(Confluence.asResource("bait"), BaitItems.MASTER_BAIT.toStack());
            helper.register(Confluence.asResource("quested_fish"), QuestedFishes.FALLEN_STARFISH.toStack());
            // 跳过了宝匣和油漆，投掷武器
            helper.register(Confluence.asResource("bombs_explosives"), ConsumableItems.BOMB.toStack());
            // 跳过了boss召唤物
            helper.register(Confluence.asResource("environment_items"), ConsumableItems.PURIFICATION_POWDER.toStack());
            helper.register(Confluence.asResource("gain"), ConsumableItems.LIFE_CRYSTAL.toStack());
            helper.register(Confluence.asResource("loot_gifts"), ConsumableItems.CHRISTMAS_GIFT.toStack());
        });

        event.register(ModTabs.TOOLS.getKey(), helper -> {
            // 跳过了绳子，魔棒，电路工具，钥匙，虫网,园艺剪，船，箱船
            helper.register(Confluence.asResource("buckets_liquids"), ToolItems.BOTTOMLESS_SHIMMER_BUCKET.toStack());
            helper.register(Confluence.asResource("utility_tools"), TCItems.MAGIC_MIRROR.toStack());
            helper.register(Confluence.asResource("axe"), AxeItems.PLATINUM_AXE.toStack());
            helper.register(Confluence.asResource("pickaxe"), PickaxeItems.PLATINUM_PICKAXE.toStack());
            helper.register(Confluence.asResource("pickaxe_axe"), PickaxeAxeItems.PICKAXE_AXE.toStack());
            helper.register(Confluence.asResource("drill"), DrillItems.TITANIUM_DRILL.toStack());
            helper.register(Confluence.asResource("chainsaw"), ChainsawItems.TITANIUM_CHAINSAW.toStack());
            helper.register(Confluence.asResource("hamaxe"), HamaxeItems.METEOR_HAMAXE.toStack());
            helper.register(Confluence.asResource("how_shovel"), HoeShovelItems.METEOR_HOE_SHOVEL.toStack());
            helper.register(Confluence.asResource("hammer"), HammerItems.PLATINUM_HAMMER.toStack());
            helper.register(Confluence.asResource("hook"), HookItems.GRAPPLING_HOOK.toStack());
            helper.register(Confluence.asResource("minecart"), MinecartItems.DIAMOND_MINECART.toStack());
            helper.register(Confluence.asResource("fishing_pole"), FishingPoleItems.GOLDEN_FISHING_ROD.toStack());
            helper.register(Confluence.asResource("hoe"), HoeItems.PLATINUM_HOE.toStack());
            helper.register(Confluence.asResource("shovel"), ShovelItems.PLATINUM_SHOVEL.toStack());
        });
        // 跳过了近战武器
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
        CrossbowItems.ITEMS.getEntries().stream().map(DeferredHolder::get).filter(BaseTerraRepeaterItem.class::isInstance).map(BaseTerraRepeaterItem.class::cast).forEach(item -> event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) ->
                new RepeaterContentsComponentHandler(stack, ModDataComponentTypes.REPEATER_CONTENTS.get(), item.getCapacity()), item
        ));
        event.registerBlock(Capabilities.ItemHandler.BLOCK, (level, pos, state, blockEntity, side) -> {
            if (state.hasProperty(StateProperties.UNLOCKED) && !state.getValue(StateProperties.UNLOCKED)) {
                return null;
            }
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
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FunctionalBlocks.HELLFORGE_ENTITY.get(), SidedInvWrapper::new);

        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBottomlessBucketWrapper(stack),
                ToolItems.BOTTOMLESS_WATER_BUCKET,
                ToolItems.BOTTOMLESS_LAVA_BUCKET,
                ToolItems.BOTTOMLESS_HONEY_BUCKET,
                ToolItems.BOTTOMLESS_SHIMMER_BUCKET
        );
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ToolItems.HONEY_BUCKET);


//        event.registerEntity(Capabilities.ItemHandler.ENTITY, EntityType.PLAYER, (player, context) -> player.getData(ModAttachmentTypes.EXTRA_INVENTORY));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerSpawnReplacements(RegisterSpawnPlacementsEvent event) {
        event.register(TEMonsterEntities.GREEN_DUMPLING_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, serverLevel, spawnType, pos, random) -> {
            if (DateUtils.isQingMing(DateUtils.getLunar()) && serverLevel instanceof Level level) {
                int y = pos.getY();
                return y > 30 && y < 260 && LibDateUtils.isDay(level) && serverLevel.canSeeSky(pos);
            }
            return false;
        }, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.INVERSE_ENDERMAN.get(), InverseEntityType.ON_CEIL, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, InverseEnderMan::checkInverseEnderManSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        ModLoader.postEvent(new RegisterBestiaryKeyEvent()); // 这个时期正好处于实体类型注册完的阶段，且datagen也会调用这个事件
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
                .register("confluence:golden_coin", "confluence:gold_coin")
                // 1.1.5 -> 1.2.0
                .register("confluence:cattails_head", "confluence:cattail_block")
                .register("confluence:cattails_body", "confluence:cattail_block")
                .register("confluence:jungle_cattails_head", "confluence:jungle_cattail_block")
                .register("confluence:jungle_cattails_body", "confluence:jungle_cattail_block")
                .register("confluence:glowing_mushroom_cattais_head", "confluence:glowing_mushroom_cattail_block")
                .register("confluence:glowing_mushroom_cattais_body", "confluence:glowing_mushroom_cattail_block")
                .register("confluence:hallow_cattails_head", "confluence:hallow_cattail_block")
                .register("confluence:hallow_cattails_body", "confluence:hallow_cattail_block")
                .register("confluence:ebony_cattails_head", "confluence:ebony_cattail_block")
                .register("confluence:ebony_cattails_body", "confluence:ebony_cattail_block")
                .register("confluence:crimson_cattails_head", "confluence:crimson_cattail_block")
                .register("confluence:crimson_cattails_body", "confluence:crimson_cattail_block");
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
                .register("confluence:cap_tunabeard", "confluence:capn_tunabeard")
                .register("confluence:obsidian_fish", "confluence:obsidifish")
                .register("terra_moment:slime_rain", "confluence:slime_rain")
                .register("terra_moment:blood_tear", "confluence:blood_tear")
                .register("terra_moment:goblin_battle_standard", "confluence:goblin_battle_standard")
                .register("confluence:cattails", "confluence:cattail")
                .register("confluence:jungle_cattails", "confluence:jungle_cattail")
                .register("confluence:glowing_mushroom_cattails", "confluence:glowing_mushroom_cattail")
                .register("confluence:hallow_cattails", "confluence:hallow_cattail")
                .register("confluence:ebony_cattails", "confluence:ebony_cattail")
                .register("confluence:crimson_cattails", "confluence:crimson_cattail")
                // 1.2.4 -> 1.3.0
                .register("confluence:blue_brick_slab", "confluence:blue_bricks_slab")
                .register("confluence:blue_brick_stairs", "confluence:blue_bricks_stairs")
        ;
    }

    @SubscribeEvent
    public static void biomeNameFixRegister(NameFixRegisterEvent.Biome event) {
        event
                // 1.1.2 -> 1.1.3
                .register("confluence:tr_crimson", "confluence:the_crimson")
                .register("confluence:tr_crimson_desert", "confluence:the_crimson_desert")
                .register("confluence:tr_crimson_tundra", "confluence:the_crimson_tundra");
    }

    @SubscribeEvent
    public static void registerBestiaryKey(RegisterBestiaryKeyEvent event) {
        Function<Integer, String> i2s = i -> Integer.toString(i);
        event.register(TEAnimals.JEWEL_BUNNY.get(), RegisterBestiaryKeyEvent.terraVariant(i2s));
        event.register(TEAnimals.SQUIRREL.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));
        event.register(TEAnimals.JEWEL_SQUIRREL.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));
        event.register(TEAnimals.GRASSHOPPER.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));
        event.register(TEAnimals.BUTTERFLY.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));
        event.register(TEAnimals.WORM.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));
        event.register(TEAnimals.DRAGONFLY.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));
        event.register(TEAnimals.LADYBUG.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));
        event.register(TEAnimals.FEALING.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));
        event.register(TEAnimals.DUCK.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));
        event.register(TEAnimals.FAIRY.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));
        event.register(TEAnimals.SCORPION.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));
        event.register(TEMonsterEntities.DEMON_EYE.get(), (type, eye) -> {
            String key = type.getDescriptionId() + '.';
            if (eye.minion_getOwnerUUID() != null) {
                return key + "minion";
            }
            return key + eye.getVariant().getSerializedName();
        });
        event.register(EntityType.ZOMBIE, ((type, zombie) -> {
            String key = type.getDescriptionId();
            if (IZombie.of(zombie).terra_entity$isSlimeZombie()) {
                return key + ".slime";
            }
            Item chest = zombie.getItemBySlot(EquipmentSlot.CHEST).getItem();
            if (chest == ArmorItems.RAINCOAT.get()) {
                return key + ".raincoat";
            } else if (chest == ArmorItems.SNOW_SUITS.get()) {
                return key + ".frozen";
            } else if (chest == ArmorItems.PINK_SNOW_SUITS.get()) {
                return key + ".frozen.pink";
            }
            return key;
        }));
        event.register(TEMonsterEntities.BLACK_SLIME.get(), (type, slime) -> {
            int size = slime.getSize();
            if (size == 1) return "entity.terra_entity.baby_slime";
            if (size == 4) return "entity.terra_entity.mother_slime";
            return type.getDescriptionId();
        });
        event.register(EntityType.SKELETON, (type, skeleton) -> {
            if (skeleton.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.MINING_CHESTPLATE)) {
                return "entity.confluence.undead_miner";
            }
            return type.getDescriptionId();
        });
    }

    @SubscribeEvent
    public static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {
        event.register(MaterialItems.DEMONITE_INGOT, MaterialItems.CRIMTANE_INGOT);
        event.register(NatureBlocks.VILE_MUSHROOM, NatureBlocks.VICIOUS_MUSHROOM);
    }
}
