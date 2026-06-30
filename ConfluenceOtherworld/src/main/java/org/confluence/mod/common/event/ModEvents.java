package org.confluence.mod.common.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;
import org.confluence.lib.common.LibAttributes;
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
import org.confluence.mod.common.data.saved.*;
import org.confluence.mod.common.entity.InverseEnderMan;
import org.confluence.mod.common.entity.InverseEntityType;
import org.confluence.mod.common.entity.RainbowSheep;
import org.confluence.mod.common.entity.animal.Bunny;
import org.confluence.mod.common.entity.animal.HostileBunny;
import org.confluence.mod.common.entity.monster.DemonEye;
import org.confluence.mod.common.entity.monster.humanoid.Zombie;
import org.confluence.mod.common.entity.monster.slime.*;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModGunProperties;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.common.init.gun.GunSounds;
import org.confluence.mod.common.init.gun.GunTrailColors;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.common.init.item.DispenserRegistration;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.integration.terra_entity.TEHelper;
import org.confluence.mod.network.s2c.CompatibilitySyncPacketS2c;
import org.confluence.mod.util.DateUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.terra_curio.api.event.RegisterAccessoriesComponentUnitValueTypeLocalSyncEvent;
import org.confluence.terra_curio.api.event.RegisterAccessoriesComponentUpdateEvent;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTabs;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.PortEventPriority;
import org.mesdag.portlib.event.entity.PortEntityAttributeCreationEvent;
import org.mesdag.portlib.event.entity.PortRegisterSpawnPlacementsEvent;
import org.mesdag.portlib.event.lifecycle.PortFMLCommonSetupEvent;
import org.mesdag.portlib.event.lifecycle.PortFMLLoadCompleteEvent;
import org.mesdag.portlib.event.other.PortAddPackFindersEvent;
import org.mesdag.portlib.event.other.PortBlockEntityTypeAddBlocksEvent;
import org.mesdag.portlib.event.other.PortBuildCreativeModeTabContentsEvent;

import java.util.function.Function;

public final class ModEvents {
    public static void init() {
        PortEventHandler.addListener(ModEvents::commonSetup);
        PortEventHandler.addListener(ModEvents::modConfig$Loading);
        PortEventHandler.addListener(ModEvents::modConfig$Reloading);
        PortEventHandler.addListener(ModEvents::loadComplete);
//        PortEventHandler.addListener(ModEvents::registerCauldronFluidContent);
        PortEventHandler.addListener(ModEvents::addPackFinders);
//        PortEventHandler.addListener(ModEvents::registerConfigurationTasks);
        PortEventHandler.addListener(ModEvents::entityAttributeCreation);
        PortEventHandler.addListener(ModEvents::registerUnitType);
        PortEventHandler.addListener(ModEvents::registerOtherType);
        PortEventHandler.addListener(ModEvents::registerAccessoriesComponentUnitValueTypeLocalSync);
        PortEventHandler.addListener(PortEventPriority.LOW, ModEvents::buildCreativeModeTabContents);
        PortEventHandler.addListener(ModEvents::blockEntityTypeAddBlocks);
//        PortEventHandler.addListener(ModEvents::registerCapabilities);
        PortEventHandler.addListener(PortEventPriority.LOW, ModEvents::registerSpawnReplacements);
        PortEventHandler.addListener(ModEvents::registerBestiaryKey);
        PortEventHandler.addListener(ModEvents::registerEvilMaterialReplaces);
    }

    private static void commonSetup(PortFMLCommonSetupEvent event) {
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

    private static void modConfig$Loading(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON && Confluence.MODID.equals(event.getConfig().getModId())) {
            CommonConfigs.onLoad();
        }
    }

    private static void modConfig$Reloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON && Confluence.MODID.equals(event.getConfig().getModId())) {
            CommonConfigs.onLoad();
            CompatibilitySyncPacketS2c.sendToAll();
        }
    }

    private static void loadComplete(PortFMLLoadCompleteEvent event) {
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
            PortEventHandler.postEvent(new RegisterEvilMaterialReplacesEvent());
            DispenserRegistration.boostrap();
        });
    }

// todo   private static void registerCauldronFluidContent(RegisterCauldronFluidContentEvent event) {
//        event.register(ModBlocks.HONEY_CAULDRON.get(), ModFluids.HONEY.fluid().get(), FluidType.BUCKET_VOLUME, null);
//        event.register(ModBlocks.AETHERIUM_CAULDRON.get(), ModFluids.SHIMMER.fluid().get(), FluidType.BUCKET_VOLUME, null);
//    }

    private static void addPackFinders(PortAddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            IModFile modFile = ModList.get().getModFileById(Confluence.MODID).getFile();
            event.addRepositorySource(consumer -> {
                Pack pack = Pack.readMetaAndCreate(
                        "confluence:terraria_art",
                        Component.translatable("resourcepack.terraria_art"),
                        false,
                        (String id) -> new PathPackResources(id, true, modFile.findResource("resourcepacks/terraria_art")),
                        PackType.CLIENT_RESOURCES,
                        Pack.Position.TOP,
                        PackSource.BUILT_IN
                );
                if (pack != null) consumer.accept(pack);
            });
            event.addRepositorySource(consumer -> {
                Pack pack = Pack.readMetaAndCreate(
                        "confluence:terraria_armor",
                        Component.translatable("resourcepack.terraria_armor"),
                        false,
                        (String id) -> new PathPackResources(id, true, modFile.findResource("resourcepacks/terraria_armor")),
                        PackType.CLIENT_RESOURCES,
                        Pack.Position.TOP,
                        PackSource.BUILT_IN
                );
                if (pack != null) consumer.accept(pack);
            });
        }
    }

// todo   private static void registerConfigurationTasks(PortRegisterConfigurationTasksEvent event) {
//        event.register(new AchievementsTask(event.getListener()));
//    }

    private static void entityAttributeCreation(PortEntityAttributeCreationEvent event) {
        event.put(ModEntities.BESTIARY_ENTRY_DISPLAY.get(), LivingEntity.createLivingAttributes().build());
        event.put(ModEntities.RAINBOW_SHEEP.get(), RainbowSheep.createAttributes().build());
        event.put(ModEntities.INVERSE_ENDERMAN.get(), InverseEnderMan.createAttributes().build());
        event.put(CritterEntities.BUNNY.get(), Bunny.createAttributes().build());
        event.put(CritterEntities.JEWEL_BUNNY.get(), Bunny.createAttributes().build());
        event.put(CritterEntities.HOSTILE_BUNNY.get(), HostileBunny.createAttributes().build());
        event.put(MonsterEntities.DEMON_EYE.get(), DemonEye.createAttributes().build());
        event.put(MonsterEntities.GREEN_SLIME.get(), BaseSlime.createGreenAttributes().build());
        event.put(MonsterEntities.BLUE_SLIME.get(), BaseSlime.createBlueAttributes().build());
        event.put(MonsterEntities.PINK_SLIME.get(), Pinky.createAttributes().build());
        event.put(MonsterEntities.DUNGEON_SLIME.get(), BaseSlime.createDungeonAttributes().build());
        event.put(MonsterEntities.CORRUPT_SLIME.get(), CorruptSlime.createAttributes().build());
        event.put(MonsterEntities.DESERT_SLIME.get(), BaseSlime.createDesertAttributes().build());
        event.put(MonsterEntities.JUNGLE_SLIME.get(), BaseSlime.createJungleAttributes().build());
        event.put(MonsterEntities.EVIL_SLIME.get(), BaseSlime.createEvilAttributes().build());
        event.put(MonsterEntities.ICE_SLIME.get(), IceSlime.createAttributes().build());
        event.put(MonsterEntities.LAVA_SLIME.get(), LavaSlime.createAttributes().build());
        event.put(MonsterEntities.LUMINOUS_SLIME.get(), LuminousSlime.createAttributes().build());
        event.put(MonsterEntities.CRIMSLIME.get(), Crimslime.createAttributes().build());
        event.put(MonsterEntities.SLIMELING.get(), Slimeling.createAttributes().build());
        event.put(MonsterEntities.PURPLE_SLIME.get(), BaseSlime.createPurpleAttributes().build());
        event.put(MonsterEntities.RED_SLIME.get(), BaseSlime.createRedAttributes().build());
        event.put(MonsterEntities.TROPIC_SLIME.get(), TropicSlime.createAttributes().build());
        event.put(MonsterEntities.YELLOW_SLIME.get(), BaseSlime.createYellowAttributes().build());
        event.put(MonsterEntities.GREEN_DUMPLING_SLIME.get(), BaseSlime.createGreenDumplingAttributes().build());
        event.put(MonsterEntities.SWAMP_SLIME.get(), BaseSlime.createSwampAttributes().build());
        event.put(MonsterEntities.BLACK_SLIME.get(), BlackSlime.createAttributes().build());
        event.put(MonsterEntities.HONEY_SLIME.get(), HoneySlime.createAttributes().build());
        event.put(MonsterEntities.GOLDEN_SLIME.get(), GoldenSlime.createAttributes().build());
        event.put(MonsterEntities.FLESH_SLIME.get(), FleshSlime.createAttributes().build());
        event.put(MonsterEntities.SPIKED_SLIME.get(), SpikedSlime.createAttributes().build());
        event.put(MonsterEntities.SPIKED_JUNGLE_SLIME.get(), SpikedJungleSlime.createAttributes().build());
        event.put(MonsterEntities.SPIKED_ICE_SLIME.get(), SpikedIceSlime.createAttributes().build());
        event.put(MonsterEntities.ZOMBIE.get(), Zombie.createAttributes().build());
        event.put(NpcEntities.GUIDE.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.MERCHANT.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.NURSE.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.DEMOLITIONIST.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.DYE_TRADER.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.PAINTER.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.DRYAD.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.ARMS_DEALER.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.GOBLIN_TINKERER.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.WITCH_DOCTOR.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.CLOTHIER.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.MECHANIC.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.PARTY_GIRL.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.STYLIST.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.TAX_COLLECTOR.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.TRUFFLE.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.WIZARD.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.ZOOLOGIST.get(), BaseNPC.createAttributes().build());
    }

// todo   private static void entityAttributeModification(PortEntityAttributeModificationEvent event) {
//        new AttributeRegistration(event)
//                .set(LibAttributes.getArmorPenetration())
//                .register(TEBossEntities.QUEEN_BEE.get(), 2)
//                .register(TEBossEntities.SKELETRON.get(), 4)
//                .register(TEBossEntities.SKELETRON_HAND.get(), 4)
//                .register(TEBossEntities.HILL_OF_FLESH.get(), 4)
//                .register(TEBossEntities.WALL_OF_FLESH.get(), 6)
//                // 肉后
//                .register(TEMonsterEntities.PIXIE.get(), 8)
//                .register(TEMonsterEntities.WYVERN.get(), 8)
//                .register(TEMonsterEntities.WRAITH.get(), 8)
//                .register(TEMonsterEntities.POSSESS_ARMOR.get(), 8)
//                .register(TEMonsterEntities.CORRUPT_SLIME.get(), 8)
//                .register(TEMonsterEntities.LUMINOUS_SLIME.get(), 8)
//                .register(TEMonsterEntities.CRIMSLIME.get(), 8)
//                .register(TEMonsterEntities.WOODEN_MIMIC.get(), 8)
//                .register(TEMonsterEntities.GOLDEN_MIMIC.get(), 8)
//                .register(TEMonsterEntities.SHADOW_MIMIC.get(), 8)
//                .register(TEMonsterEntities.ICE_MIMIC.get(), 8)
//                .register(TEMonsterEntities.CRIMSON_MIMIC.get(), 8)
//                .register(TEMonsterEntities.CORRUPT_MIMIC.get(), 8)
//                .register(TEMonsterEntities.HALLOWED_MIMIC.get(), 8)
//                .register(TEMonsterEntities.JUNGLE_MIMIC.get(), 8)
//
//                .register(TEMonsterEntities.MUMMY.get(), 8)
//                .register(TEMonsterEntities.DARK_MUMMY.get(), 8)
//                .register(TEMonsterEntities.BLOOD_MUMMY.get(), 8)
//                .register(TEMonsterEntities.LIGHT_MUMMY.get(), 8)
//                .register(TEMonsterEntities.DARK_LAMIA.get(), 8)
//                .register(TEMonsterEntities.LIGHT_LAMIA.get(), 8)
//                .register(TEMonsterEntities.DERPLING.get(), 8)
//                .register(TEMonsterEntities.HERPLING.get(), 8)
//                .register(TEMonsterEntities.GHOUL.get(), 8)
//                .register(TEMonsterEntities.VILE_GHOUL.get(), 8)
//                .register(TEMonsterEntities.TAINTED_GHOUL.get(), 8)
//                .register(TEMonsterEntities.DREAMER_GHOUL.get(), 8)
//                .register(TEMonsterEntities.SAND_POACHER.get(), 8)
//
//                .register(TEBossEntities.RETINAZER.get(), 8)
//                .register(TEBossEntities.SPAZMATISM.get(), 8)
//                .register(TEBossEntities.PLANTERA.get(), 8)
//                .register(TEBossEntities.PLANTERA_TENTACLE.get(), 8)
//                .register(TEBossEntities.PLANTERA_HOOK.get(), 8)
//
//
//                .set(Attributes.ARMOR_TOUGHNESS)
//                .register(TEMonsterEntities.PIXIE.get(), 2)
//                .register(TEMonsterEntities.WYVERN.get(), 2)
//                .register(TEMonsterEntities.CORRUPT_SLIME.get(), 2)
//                .register(TEMonsterEntities.LUMINOUS_SLIME.get(), 2)
//                .register(TEMonsterEntities.CRIMSLIME.get(), 2)
//                .register(TEMonsterEntities.WOODEN_MIMIC.get(), 2)
//                .register(TEMonsterEntities.GOLDEN_MIMIC.get(), 2)
//                .register(TEMonsterEntities.SHADOW_MIMIC.get(), 2)
//                .register(TEMonsterEntities.ICE_MIMIC.get(), 2)
//                .register(TEMonsterEntities.CRIMSON_MIMIC.get(), 2)
//                .register(TEMonsterEntities.CORRUPT_MIMIC.get(), 2)
//                .register(TEMonsterEntities.HALLOWED_MIMIC.get(), 2)
//                .register(TEMonsterEntities.JUNGLE_MIMIC.get(), 2)
//
//                .register(TEMonsterEntities.MUMMY.get(), 2)
//                .register(TEMonsterEntities.DARK_MUMMY.get(), 2)
//                .register(TEMonsterEntities.BLOOD_MUMMY.get(), 2)
//                .register(TEMonsterEntities.LIGHT_MUMMY.get(), 2)
//                .register(TEMonsterEntities.DARK_LAMIA.get(), 2)
//                .register(TEMonsterEntities.LIGHT_LAMIA.get(), 2)
//                .register(TEMonsterEntities.DERPLING.get(), 2)
//                .register(TEMonsterEntities.HERPLING.get(), 2)
//                .register(TEMonsterEntities.GHOUL.get(), 2)
//                .register(TEMonsterEntities.VILE_GHOUL.get(), 2)
//                .register(TEMonsterEntities.TAINTED_GHOUL.get(), 2)
//                .register(TEMonsterEntities.DREAMER_GHOUL.get(), 2)
//                .register(TEMonsterEntities.SAND_POACHER.get(), 2)
//
//                .register(TEBossEntities.RETINAZER.get(), 2)
//                .register(TEBossEntities.SPAZMATISM.get(), 2)
//                .register(TEBossEntities.PLANTERA.get(), 2)
//                .register(TEBossEntities.PLANTERA_TENTACLE.get(), 2)
//                .register(TEBossEntities.PLANTERA_HOOK.get(), 2)
//        ;
//    }

    private static void registerUnitType(RegisterAccessoriesComponentUpdateEvent.UnitType event) {
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

    private static void registerOtherType(RegisterAccessoriesComponentUpdateEvent.OtherType event) {
        event.register(AccessoryItems.ADDITIONAL$MANA);
        event.register(AccessoryItems.MANA$USE$REDUCE);
        event.register(AccessoryItems.MANA$PICKUP$RANGE);
        event.register(AccessoryItems.COIN$PICKUP$RANGE);
        event.register(AccessoryItems.REDUCE$HEALING$COOLDOWN);
        event.register(AccessoryItems.FISHING$POWER);
        event.register(AccessoryItems.SPECIAL$PRICE);
    }

    private static void registerAccessoriesComponentUnitValueTypeLocalSync(RegisterAccessoriesComponentUnitValueTypeLocalSyncEvent event) {
        AccessoryItems.AFK_INDEX = event.register(AccessoryItems.$AFK);
    }

    private static void buildCreativeModeTabContents(PortBuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == TCTabs.ACCESSORIES.get()) {
            WipNotDisplayOutput output = new WipNotDisplayOutput(event);
            output.accept(TCItems.EVERLASTING);
            output.accept(TCItems.BASE_POINT);
            output.acceptAll(AccessoryItems.ITEMS);
        }
    }

    private static void blockEntityTypeAddBlocks(PortBlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.BRUSHABLE_BLOCK, OreBlocks.OPAL_ORE.get());
        event.modify(BlockEntityType.SIGN, LogBlockSet.getSignBlocks());
        event.modify(BlockEntityType.HANGING_SIGN, LogBlockSet.getHangingSignBlocks());
        event.modify(BlockEntityType.SCULK_SENSOR, FunctionalBlocks.SCULK_TRAP.get());
        event.modify(BlockEntityType.CAMPFIRE, FunctionalBlocks.LIFE_CAMPFIRE.get());
    }

//    private static void registerCapabilities(PortRegisterCapabilitiesEvent event) {
//        event.registerBlock(ForgeCapabilities.ITEM_HANDLER, (level, pos, state, blockEntity, side) -> {
//            if (state.hasProperty(StateProperties.UNLOCKED) && !state.getValue(StateProperties.UNLOCKED)) {
//                return null;
//            }
//            Container container = ChestBlock.getContainer((ChestBlock) state.getBlock(), state, level, pos, true);
//            return container == null ? null : new InvWrapper(container);
//        }, ChestBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get).toArray(Block[]::new));
//
//        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FunctionalBlocks.HELLFORGE_ENTITY.get(), SidedInvWrapper::new);
//
//        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBottomlessBucketWrapper(stack),
//                ToolItems.BOTTOMLESS_WATER_BUCKET,
//                ToolItems.BOTTOMLESS_LAVA_BUCKET,
//                ToolItems.BOTTOMLESS_HONEY_BUCKET,
//                ToolItems.BOTTOMLESS_SHIMMER_BUCKET
//        );
//        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ToolItems.HONEY_BUCKET);
//    }

    private static void registerSpawnReplacements(PortRegisterSpawnPlacementsEvent event) {
        event.register(TEMonsterEntities.GREEN_DUMPLING_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, serverLevel, spawnType, pos, random) -> {
            if (DateUtils.isQingMing(DateUtils.getLunar()) && serverLevel instanceof Level level) {
                int y = pos.getY();
                return y > 30 && y < 260 && LibDateUtils.isDay(level) && serverLevel.canSeeSky(pos);
            }
            return false;
        }, PortRegisterSpawnPlacementsEvent.PortOperation.REPLACE);
        event.register(ModEntities.INVERSE_ENDERMAN.get(), InverseEntityType.ON_CEIL, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, InverseEnderMan::checkInverseEnderManSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CritterEntities.BUNNY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        event.register(CritterEntities.JEWEL_BUNNY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);

        // DemonEye: 夜晚地表飞行怪
        event.register(MonsterEntities.DEMON_EYE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> {
            if (level.isDay() || pos.getY() < 60) return false;
            return level.canSeeSky(pos) && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
        });

        // GreenSlime: 白天森林地表
        event.register(MonsterEntities.GREEN_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> {
            if (!level.isDay() || pos.getY() < 40) return false;
            return level.canSeeSky(pos) && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
        });
        // BlueSlime: 白天森林/地下
        event.register(MonsterEntities.BLUE_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> {
            if (!level.isDay() || pos.getY() < 0) return false;
            return Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
        });
        // Zombie: 夜晚地表
        event.register(MonsterEntities.ZOMBIE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> {
            if (level.isDay() || pos.getY() < 40) return false;
            return Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
        });

        PortEventHandler.postEvent(new RegisterBestiaryKeyEvent()); // 这个时期正好处于实体类型注册完的阶段，且datagen也会调用这个事件
    }

    private static void registerBestiaryKey(RegisterBestiaryKeyEvent event) {
        Function<Integer, String> i2s = i -> Integer.toString(i);
        event.register(TEAnimals.JEWEL_BUNNY.get(), RegisterBestiaryKeyEvent.vanillaVariant(i2s));
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
        event.register(MonsterEntities.DEMON_EYE.get(), (type, eye) -> {
            String key = type.getDescriptionId() + '.';
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

    private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {
        event.register(MaterialItems.DEMONITE_INGOT, MaterialItems.CRIMTANE_INGOT);
        event.register(NatureBlocks.VILE_MUSHROOM, NatureBlocks.VICIOUS_MUSHROOM);
    }
}
