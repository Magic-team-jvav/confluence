package org.confluence.mod.common.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.forgespi.locating.IModFile;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.lib.common.data.saved.IGlobalData;
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
import org.confluence.mod.common.init.gun.GunSounds;
import org.confluence.mod.common.init.gun.GunTrailColors;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.integration.terra_entity.TEEvents;
import org.confluence.mod.integration.terra_entity.TEHelper;
import org.confluence.mod.integration.terra_entity.TEItemComponentModify;
import org.confluence.mod.network.s2c.CompatibilitySyncPacketS2c;
import org.confluence.mod.network.task.AchievementsTask;
import org.confluence.mod.util.DateUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.RepeaterContentsComponentHandler;
import org.confluence.terra_curio.api.event.RegisterAccessoriesComponentUnitValueTypeLocalSyncEvent;
import org.confluence.terra_curio.api.event.RegisterAccessoriesComponentUpdateEvent;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTabs;
import org.mesdag.portlib.event.entity.PortEntityAttributeCreationEvent;
import org.mesdag.portlib.event.lifecycle.PortFMLCommonSetupEvent;
import org.mesdag.portlib.event.lifecycle.PortFMLLoadCompleteEvent;
import org.mesdag.portlib.event.other.PortAddPackFindersEvent;
import org.mesdag.portlib.event.other.PortBlockEntityTypeAddBlocksEvent;
import org.mesdag.portlib.event.other.PortModifyDefaultComponentsEvent;

import java.util.Optional;
import java.util.function.Function;

import static org.confluence.mod.Confluence.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public final class ModEvents {
    @SubscribeEvent
    public static void commonSetup(PortFMLCommonSetupEvent event) {
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
                if (Attributes.ARMOR instanceof RangedAttribute rangedAttribute) {
                    rangedAttribute.maxValue = 65536;
                }
                if (Attributes.ARMOR_TOUGHNESS instanceof RangedAttribute rangedAttribute) {
                    rangedAttribute.maxValue = 65536;
                }
                if (Attributes.MAX_HEALTH instanceof RangedAttribute rangedAttribute) {
                    rangedAttribute.maxValue = 65536;
                }
                if (LibAttributes.getAttackDamage().value() instanceof RangedAttribute rangedAttribute) {
                    rangedAttribute.maxValue = 65536;
                }
            }

            // 枪械初始化
            GunSounds.init();
            GunTrailColors.init();
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
    public static void loadComplete(PortFMLLoadCompleteEvent event) {
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
    public static void addPackFinders(PortAddPackFindersEvent event) {
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
    public static void registerConfigurationTasks(RegisterConfigurationTasksEvent event) {
        event.register(new AchievementsTask(event.getListener()));
    }

    @SubscribeEvent
    public static void entityAttributeCreation(PortEntityAttributeCreationEvent event) {
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
    public static void blockEntityTypeAddBlocks(PortBlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.BRUSHABLE_BLOCK, OreBlocks.OPAL_ORE.get());
        event.modify(BlockEntityType.SIGN, LogBlockSet.getSignBlocks());
        event.modify(BlockEntityType.HANGING_SIGN, LogBlockSet.getHangingSignBlocks());
        event.modify(BlockEntityType.SCULK_SENSOR, FunctionalBlocks.SCULK_TRAP.get());
        event.modify(BlockEntityType.CAMPFIRE, FunctionalBlocks.LIFE_CAMPFIRE.get());
    }

    @SubscribeEvent
    public static void modifyDefaultComponents(PortModifyDefaultComponentsEvent event) {
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
