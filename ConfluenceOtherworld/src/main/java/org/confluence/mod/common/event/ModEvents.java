package org.confluence.mod.common.event;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.data.saved.IGlobalData;
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
import org.confluence.mod.common.entity.RainbowSheep;
import org.confluence.mod.common.entity.animal.*;
import org.confluence.mod.common.entity.boss.*;
import org.confluence.mod.common.entity.monster.*;
import org.confluence.mod.common.entity.monster.humanoid.Zombie;
import org.confluence.mod.common.entity.monster.slime.*;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.storage.StorageCompanionEntity;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModGunProperties;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.entity.*;
import org.confluence.mod.common.init.gun.GunSounds;
import org.confluence.mod.common.init.gun.GunTrailColors;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.DispenserRegistration;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.util.ModUtils;
import org.confluence.terra_curio.api.event.RegisterAccessoriesComponentUnitValueTypeLocalSyncEvent;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTabs;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.PortEventPriority;
import org.mesdag.portlib.event.entity.PortEntityAttributeCreationEvent;
import org.mesdag.portlib.event.entity.PortEntityAttributeModificationEvent;
import org.mesdag.portlib.event.entity.PortRegisterSpawnPlacementsEvent;
import org.mesdag.portlib.event.lifecycle.PortFMLCommonSetupEvent;
import org.mesdag.portlib.event.lifecycle.PortFMLLoadCompleteEvent;
import org.mesdag.portlib.event.other.PortAddPackFindersEvent;
import org.mesdag.portlib.event.other.PortBlockEntityTypeAddBlocksEvent;
import org.mesdag.portlib.event.other.PortBuildCreativeModeTabContentsEvent;

public final class ModEvents {
    public static void init() {
        PortEventHandler.addListener(ModEvents::commonSetup);
        PortEventHandler.addListener(ModEvents::modConfig$Loading);
        PortEventHandler.addListener(ModEvents::modConfig$Reloading);
        PortEventHandler.addListener(ModEvents::loadComplete);
        PortEventHandler.addListener(ModEvents::addPackFinders);
        PortEventHandler.addListener(ModEvents::entityAttributeCreation);
        PortEventHandler.addListener(ModEvents::entityAttributeModification);
        PortEventHandler.addListener(ModEvents::registerAccessoriesComponentUnitValueTypeLocalSync);
        PortEventHandler.addListener(PortEventPriority.LOW, ModEvents::buildCreativeModeTabContents);
        PortEventHandler.addListener(ModEvents::blockEntityTypeAddBlocks);
        PortEventHandler.addListener(ModEvents::registerBestiaryKeys);
        PortEventHandler.addListener(PortEventPriority.LOW, ModEvents::registerSpawnReplacements);
        PortEventHandler.addListener(ModEvents::registerEvilMaterialReplaces);
    }

    private static void commonSetup(PortFMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModGunProperties.init();
            Confluence.registerGameRules();
            ModFluids.registerInteraction();
            ModFluids.registerShimmerTransform();
            //            ModBiomes.registerRegionAndSurface();
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
                if (LibAttributes.getAttackDamage().get() instanceof RangedAttribute rangedAttribute) {
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
//            CompatibilitySyncPacketS2c.sendToAll();
        }
    }

    private static void loadComplete(PortFMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            LogBlockSet.wrapStrip();
            LogBlockSet.setFlammable();
            ModRecipes.Brewing.initialize();
            ModUtils.registerCauldronInteractions();
            MagicMailBox.registerVariants();
            ModArmorBonus.registerArmorSetBonus();
            IGlobalData.registerGlobalData(
                    KillBoard.INSTANCE,
                    HardmodeConvertor.INSTANCE,
                    NPCSpawner.INSTANCE,
                    Bestiary.INSTANCE,
                    GlobalCloakData.INSTANCE,
                    GameEventSystem.INSTANCE,
                    HouseHandler.INSTANCE,
                    AnglerData.INSTANCE
            );
            GlobalCloakData.INSTANCE.initialize();
            PortEventHandler.postEvent(new RegisterEvilMaterialReplacesEvent());
            DispenserRegistration.boostrap();
        });
    }

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

    private static void entityAttributeCreation(PortEntityAttributeCreationEvent event) {
        event.put(ModEntities.BESTIARY_ENTRY_DISPLAY.get(), LivingEntity.createLivingAttributes().build());
        event.put(ModEntities.RAINBOW_SHEEP.get(), RainbowSheep.createAttributes().build());
        var storageCompanionAttributes = StorageCompanionEntity.createAttributes().build();
        event.put(ModEntities.CHESTER.get(), storageCompanionAttributes);
        event.put(ModEntities.FLYING_PIGGY_BANK.get(), storageCompanionAttributes);
//        event.put(ModEntities.INVERSE_ENDERMAN.get(), InverseEnderMan.createAttributes().build());
        event.put(CritterEntities.BUNNY.get(), Bunny.createAttributes().build());
        event.put(CritterEntities.JEWEL_BUNNY.get(), Bunny.createAttributes().build());
        event.put(CritterEntities.EXPLOSIVE_BUNNY.get(), Bunny.createAttributes().build());
        event.put(CritterEntities.HOSTILE_BUNNY.get(), HostileBunny.createAttributes().build());
        event.put(CritterEntities.BIRD.get(), Bird.createAttributes().build());
        event.put(CritterEntities.BLUE_JAY.get(), BlueJay.createAttributes().build());
        event.put(CritterEntities.CARDINAL.get(), Cardinal.createAttributes().build());
        event.put(CritterEntities.SQUIRREL.get(), Squirrel.createAttributes().build());
        event.put(CritterEntities.RED_SQUIRREL.get(), Squirrel.createAttributes().build());
        event.put(CritterEntities.JEWEL_SQUIRREL.get(), Squirrel.createAttributes().build());
        event.put(CritterEntities.WORM.get(), Worm.createAttributes().build());
        event.put(CritterEntities.DUCK.get(), Duck.createAttributes().build());
        event.put(CritterEntities.CRAB.get(), Crab.createAttributes().build());
        event.put(CritterEntities.BUTTERFLY.get(), Butterfly.createAttributes().build());
        event.put(CritterEntities.FAIRY.get(), Fairy.createAttributes().build());
        event.put(CritterEntities.FEALING.get(), Fairy.createAttributes().build());
        event.put(CritterEntities.GLOWING_SNAIL.get(), SimpleCritter.createAttributes().build());
        event.put(CritterEntities.GRUBBY.get(), SimpleCritter.createAttributes().build());
        event.put(CritterEntities.MAGGOT.get(), SimpleCritter.createAttributes().build());
        event.put(CritterEntities.MAGMA_SNAIL.get(), SimpleCritter.createAttributes().build());
        event.put(CritterEntities.SLUGGY.get(), SimpleCritter.createAttributes().build());
        event.put(CritterEntities.SNAIL.get(), SimpleCritter.createAttributes().build());
        event.put(CritterEntities.SCORPION.get(), Scorpion.createAttributes().build());
        event.put(CritterEntities.HELL_BUTTERFLY.get(), HellButterfly.createAttributes().build());
        event.put(CritterEntities.PRISMATIC_LACEWING.get(), PrismaticLacewing.createAttributes().build());
        event.put(CritterEntities.DRAGONFLY.get(), Dragonfly.createAttributes().build());
        event.put(CritterEntities.GRASSHOPPER.get(), Grasshopper.createAttributes().build());
        event.put(CritterEntities.LADYBUG.get(), Ladybug.createAttributes().build());
        event.put(MonsterEntities.DEMON_EYE.get(), DemonEye.createAttributes().build());
        event.put(MonsterEntities.HARPY.get(), CreatureAttributeBuilder.creature(41, 8, 13).flying().build());
        event.put(MonsterEntities.PIXIE.get(), CreatureAttributeBuilder.creature(78, 20, 28, 16, 1, 0.46).build());
        event.put(MonsterEntities.EATER_OF_SOULS.get(), CreatureAttributeBuilder.creature(20, 6, 11, 30, 0.5, 0.1).flying().build());
        event.put(MonsterEntities.CRIMERA.get(), CreatureAttributeBuilder.creature(20, 6, 11, 30, 0.5, 0.1).flying().build());
        event.put(MonsterEntities.CURSED_SKULL.get(), CreatureAttributeBuilder.creature(21, 6, 18, 32, 1, 0.82).build());
        event.put(MonsterEntities.CORRUPTOR.get(), CreatureAttributeBuilder.creature(156, 18, 32, 48, 1, 0.73).flying().build());
        event.put(MonsterEntities.SLIMER.get(), CreatureAttributeBuilder.creature(156, 20, 45, 48, 1, 0.73).flying().build());
        event.put(MonsterEntities.ENCHANTED_SWORD.get(), CreatureAttributeBuilder.creature(208, 20, 41, 48, 1, 0.82).flying().build());
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
        event.put(MonsterEntities.SNATCHER.get(), CreatureAttributeBuilder.creature(31, 10, 13, 20, 1, 1).build());
        event.put(MonsterEntities.MAN_EATER.get(), CreatureAttributeBuilder.creature(57, 10, 15, 20, 1, 1).build());
        event.put(MonsterEntities.SPORE_SKELETON.get(), CreatureAttributeBuilder.creature(31, 8, 11, 60, 0.5, 0.28).build());
        event.put(MonsterEntities.BASE_BONES.get(), CreatureAttributeBuilder.creature(41, 2, 13, 20, 1, 0.28).movementSpeed(0.3).build());
        event.put(MonsterEntities.ANGER_BONES.get(), CreatureAttributeBuilder.creature(41, 8, 13).build());
        event.put(MonsterEntities.SHORT_BONES.get(), CreatureAttributeBuilder.creature(37, 7, 12).build());
        event.put(MonsterEntities.BIG_BONES.get(), CreatureAttributeBuilder.creature(52, 9, 17).build());
        event.put(MonsterEntities.BIG_ANGER_BONES.get(), CreatureAttributeBuilder.creature(36, 6, 17).build());
        event.put(MonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), CreatureAttributeBuilder.creature(36, 12, 14).build());
        event.put(MonsterEntities.BIG_HELMET_ANGER_BONES.get(), CreatureAttributeBuilder.creature(62, 14, 12).build());
        event.put(MonsterEntities.UNDEAD_VIKING.get(), CreatureAttributeBuilder.creature(36, 10, 12).build());
        event.put(MonsterEntities.GIANT_TORTOISE.get(), CreatureAttributeBuilder.creature(366, 30, 55, 48, 1, 0.82).movementSpeed(0.2).build());
        event.put(MonsterEntities.UNICORN.get(), CreatureAttributeBuilder.creature(416, 30, 65, 64, 1, 0.82).movementSpeed(0.35).build());
        event.put(MonsterEntities.GASTROPOD.get(), CreatureAttributeBuilder.creature(143, 20, 40, 48, 1, 0.64).build());
        event.put(MonsterEntities.WYVERN.get(), CreatureAttributeBuilder.creature(2080, 10, 41, 50, 1, 0.28).build());
        event.put(MonsterEntities.ARCH_WYVERN.get(), CreatureAttributeBuilder.creature(3120, 18, 52, 64, 1, 0.37).flying().build());
        event.put(MonsterEntities.DEVOURER.get(), CreatureAttributeBuilder.creature(52, 2, 8).build());
        event.put(MonsterEntities.TOMB_CRAWLER.get(), CreatureAttributeBuilder.creature(16, 2, 4).build());
        event.put(MonsterEntities.GIANT_WORM.get(), CreatureAttributeBuilder.creature(31, 3, 9).build());
        event.put(MonsterEntities.LEECH.get(), CreatureAttributeBuilder.creature(36, 4, 10).movementSpeed(0.145).build());
        event.put(MonsterEntities.BONE_SERPENT.get(), CreatureAttributeBuilder.creature(156, 12, 18).build());
        event.put(MonsterEntities.WITHER_BONE_SERPENT.get(), CreatureAttributeBuilder.creature(186, 15, 22).build());
        event.put(MonsterEntities.DARK_CASTER.get(), CreatureAttributeBuilder.creature(26, 2, 10, 20, 1, 0.82).build());
        event.put(MonsterEntities.GOBLIN_SORCERER.get(), CreatureAttributeBuilder.creature(20, 2, 10, 32, 1, 0.46).build());
        event.put(MonsterEntities.CHAOS_ELEMENTAL.get(), CreatureAttributeBuilder.creature(312, 24, 41, 48, 1, 0.82).build());
        event.put(MonsterEntities.NECROMANCER.get(), CreatureAttributeBuilder.creature(260, 24, 46, 48, 1, 0.73).build());
        event.put(MonsterEntities.DIABOLIST.get(), CreatureAttributeBuilder.creature(260, 28, 52, 48, 1, 0.73).build());
        event.put(MonsterEntities.RAGGED_CASTER.get(), CreatureAttributeBuilder.creature(260, 22, 44, 48, 1, 0.73).build());
        event.put(MonsterEntities.ZOMBIE.get(), Zombie.createAttributes().build());
        // 蝙蝠
        event.put(MonsterEntities.CAVE_BAT.get(), CreatureAttributeBuilder.creature(8, 1, 4, 16, 0.2, 0.5).flying().build());
        event.put(MonsterEntities.JUNGLE_BAT.get(), CreatureAttributeBuilder.creature(17, 1, 8, 16, 0.2, 0.5).flying().build());
        event.put(MonsterEntities.ICE_BAT.get(), CreatureAttributeBuilder.creature(15, 2, 7, 16, 0.2, 0.5).flying().build());
        event.put(MonsterEntities.GIANT_BAT.get(), CreatureAttributeBuilder.creature(166, 16, 34, 32, 0.2, 0.55).flying().build());
        event.put(MonsterEntities.HELL_BAT.get(), CreatureAttributeBuilder.creature(23, 2, 15, 16, 0.2, 0.5).flying().build());
        event.put(MonsterEntities.SPORE_BAT.get(), CreatureAttributeBuilder.creature(15, 2, 7, 16, 0.2, 0.5).flying().build());
        event.put(MonsterEntities.GIANT_FLYING_FOX.get(), CreatureAttributeBuilder.creature(221, 18, 38, 48, 0.5, 0.64).flying().build());
        // 飞行怪
        event.put(MonsterEntities.DRIPPLER.get(), CreatureAttributeBuilder.creature(26, 7, 14, 64, 0.5, 0.2).flying().build());
        event.put(MonsterEntities.FLYING_FISH.get(), CreatureAttributeBuilder.creature(10, 1, 2, 30, 0.5, 0.3).flying().build());
        event.put(MonsterEntities.WANDERING_EYE_FISH.get(), CreatureAttributeBuilder.creature(156, 18, 15, 60, 1, 1).movementSpeed(2.2).flying().build());
        event.put(MonsterEntities.VISUAL_NEURON.get(), VisualNeuron.createAttributes().build());
        event.put(MonsterEntities.BLAZING_WHEEL.get(), CreatureAttributeBuilder.creature(260, 30, 42, 48, 1, 1).flying().build());
        event.put(MonsterEntities.SPIKE_BALL.get(), CreatureAttributeBuilder.creature(208, 25, 36, 48, 1, 1).flying().build());
        event.put(MonsterEntities.DEMON.get(), CreatureAttributeBuilder.creature(62, 8, 20, 16, 1, 0.28).flying().build());
        event.put(MonsterEntities.VOODOO_DEMON.get(), CreatureAttributeBuilder.creature(62, 8, 20, 16, 1, 0.28).flying().build());
        event.put(MonsterEntities.HORNET.get(), CreatureAttributeBuilder.creature(32, 6, 13, 32, 0, 0.55).movementSpeed(0.5).flying().build());
        event.put(MonsterEntities.LITTLE_HORNET.get(), CreatureAttributeBuilder.creature(3, 1, 3, 20, 0, 0.2).flying().build());
        event.put(MonsterEntities.FIRE_IMP.get(), CreatureAttributeBuilder.creature(36, 16, 15, 20, 1, 0.55).build());
        event.put(MonsterEntities.DECAYEDER.get(), CreatureAttributeBuilder.creature(10, 6, 6).build());
        event.put(MonsterEntities.GHOST.get(), CreatureAttributeBuilder.creature(26, 4, 8, 16, 0, 0.55).gravity(0).build());
        event.put(MonsterEntities.DERPLING.get(), CreatureAttributeBuilder.creature(156, 26, 41, 48, 1, 0.55).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.HERPLING.get(), CreatureAttributeBuilder.creature(114, 26, 33, 48, 1, 0.73).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.METEOR_HEAD.get(), CreatureAttributeBuilder.creature(13, 6, 21, 32, 1, 0.64).flying().build());
        event.put(MonsterEntities.GRANITE_ELEMENTAL.get(), CreatureAttributeBuilder.creature(46, 8, 17, 32, 1, 0.73).flying().build());
        event.put(MonsterEntities.ANTLION_SWARMER.get(), CreatureAttributeBuilder.creature(31, 8, 15, 32, 1, 0.55).flying().build());
        event.put(MonsterEntities.GIANT_ANTLION_SWARMER.get(), CreatureAttributeBuilder.creature(46, 12, 17, 32, 1, 0.73).flying().build());
        event.put(MonsterEntities.THE_HUNGRY.get(), CreatureAttributeBuilder.creature(87, 16, 15, 32, 0.75, 1).build());
        event.put(MonsterEntities.HILL_HUNGRY.get(), CreatureAttributeBuilder.creature(87, 16, 15, 32, 0.75, 1).build());
        event.put(MonsterEntities.BLOOD_ZOMBIE.get(), CreatureAttributeBuilder.creature(39, 8, 10, 60, 0.5, 0.1).movementSpeed(0.15).build());
        event.put(MonsterEntities.SNOW_FLINX.get(), CreatureAttributeBuilder.creature(36, 12, 13, 60, 0.1, 0.1).build());
        event.put(MonsterEntities.FACE_MONSTER.get(), CreatureAttributeBuilder.creature(36, 10, 13).stepHeight(3.2).jumpStrength(0.8).build());
        event.put(MonsterEntities.BLOOD_TUMORS.get(), CreatureAttributeBuilder.creature(5, 2, 0, 0, 0, 0).movementSpeed(0).safeFallDistance(100).build());
        event.put(MonsterEntities.POSSESS_ARMOR.get(), CreatureAttributeBuilder.creature(135, 10, 28, 32, 1, 0.64).build());
        event.put(MonsterEntities.POSSESS_ARMOR_VOID_VESSEL.get(), CreatureAttributeBuilder.creature(1, 0, 28, 32, 1, 0.64).build());
        event.put(MonsterEntities.MUMMY.get(), CreatureAttributeBuilder.creature(67, 16, 26, 48, 1, 0.46).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.DARK_MUMMY.get(), CreatureAttributeBuilder.creature(93, 18, 32, 48, 1, 0.55).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.BLOOD_MUMMY.get(), CreatureAttributeBuilder.creature(93, 18, 32, 48, 1, 0.55).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.LIGHT_MUMMY.get(), CreatureAttributeBuilder.creature(104, 18, 28, 48, 1, 0.51).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.DARK_LAMIA.get(), CreatureAttributeBuilder.creature(182, 28, 27, 48, 1, 0.69).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.LIGHT_LAMIA.get(), CreatureAttributeBuilder.creature(182, 28, 27, 48, 1, 0.69).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.GHOUL.get(), CreatureAttributeBuilder.creature(93, 26, 26, 64, 1, 0.46).stepHeight(3.2).jumpStrength(0.7).build());
        event.put(MonsterEntities.TAINTED_GHOUL.get(), CreatureAttributeBuilder.creature(114, 32, 33, 64, 1, 0.55).stepHeight(3.2).jumpStrength(0.7).build());
        event.put(MonsterEntities.VILE_GHOUL.get(), CreatureAttributeBuilder.creature(130, 30, 31, 64, 1, 0.64).stepHeight(3.2).jumpStrength(0.7).build());
        event.put(MonsterEntities.DREAMER_GHOUL.get(), CreatureAttributeBuilder.creature(156, 32, 28, 64, 1, 0.55).stepHeight(3.2).jumpStrength(0.7).build());
        event.put(MonsterEntities.PALADIN.get(), CreatureAttributeBuilder.creature(520, 52, 52, 64, 1, 0.9).build());
        event.put(MonsterEntities.BONE_LEE.get(), CreatureAttributeBuilder.creature(520, 34, 48, 48, 1, 0.95).movementSpeed(0.38).build());
        event.put(MonsterEntities.GOBLIN_ARCHER.get(), CreatureAttributeBuilder.creature(41, 6, 11, 32, 1, 0.37).build());
        event.put(MonsterEntities.GOBLIN_PEON.get(), CreatureAttributeBuilder.creature(31, 4, 6, 32, 1, 0.2).build());
        event.put(MonsterEntities.GOBLIN_WARRIOR.get(), CreatureAttributeBuilder.creature(57, 8, 13, 32, 1, 0.6).build());
        event.put(MonsterEntities.GOBLIN_THIEF.get(), CreatureAttributeBuilder.creature(41, 6, 10, 32, 1, 0.37).build());
        event.put(MonsterEntities.GOBLIN_SCOUT.get(), CreatureAttributeBuilder.creature(41, 6, 10, 32, 1, 0.37).build());
        event.put(MonsterEntities.ANGER_GOBLIN.get(), CreatureAttributeBuilder.creature(220, 0, 15, 32, 1, 0.88).build());
        // 陆行怪
        event.put(MonsterEntities.BLOODY_SPORE.get(), CreatureAttributeBuilder.creature(100, 6, 0, 32, 0, 0.8).spawnReinforcementsChance(0.01).build());
        event.put(MonsterEntities.BLOOD_CRAWLER.get(), CreatureAttributeBuilder.creature(31, 8, 15, 32, 1, 0.8)
                .movementSpeed(0.38).spawnReinforcementsChance(0.01).build());
        event.put(MonsterEntities.SPORE_ZOMBIE.get(), CreatureAttributeBuilder.creature(93, 10, 20, 60, 0.6, 0.1).movementSpeed(0.08).build());
        event.put(MonsterEntities.HAT_SPORE_ZOMBIE.get(), CreatureAttributeBuilder.creature(114, 16, 19, 60, 0.6, 0.72).movementSpeed(0.08).build());
        event.put(MonsterEntities.NYMPH.get(), CreatureAttributeBuilder.creature(156, 16, 15, 15, 1, 0.5).build());
        event.put(MonsterEntities.SAND_POACHER.get(), CreatureAttributeBuilder.creature(166, 24, 34, 64, 1, 0.55).stepHeight(3.2).jumpStrength(0.5).build());
        // 水怪
        event.put(MonsterEntities.PIRANHA.get(), Piranha.createAttributes().build());
        event.put(MonsterEntities.BLOOD_FEEDER.get(), CreatureAttributeBuilder.creature(130, 12, 30, 32, 0.5, 0.55).build());
        event.put(MonsterEntities.ARAPAIMA.get(), Piranha.createArapaimaAttributes().build());
        event.put(MonsterEntities.BLUE_JELLYFISH.get(), JellyFish.createAttributes().build());
        event.put(MonsterEntities.PINK_JELLYFISH.get(), JellyFish.createPinkAttributes().build());
        event.put(MonsterEntities.GREEN_JELLYFISH.get(), JellyFish.createGreenAttributes().build());
        event.put(MonsterEntities.SHARK.get(), Shark.createAttributes().build());
        // 卷壳怪
        event.put(MonsterEntities.GIANT_SHELLY.get(), CreatureAttributeBuilder.creature(26, 12, 9, 20, 0, 0.4).movementSpeed(0.1).build());
        event.put(MonsterEntities.CRAWDAD.get(), CreatureAttributeBuilder.creature(26, 6, 15, 25, 0, 0.1).jumpStrength(0.8).build());
        // Wraith + Mimics
        event.put(MonsterEntities.WRAITH.get(), CreatureAttributeBuilder.creature(83, 0, 33, 32, 1, 0.37).gravity(0).build());
        event.put(MonsterEntities.WOODEN_MIMIC.get(), CreatureAttributeBuilder.creature(260, 30, 42, 32, 1, 0.73).build());
        event.put(MonsterEntities.GOLDEN_MIMIC.get(), CreatureAttributeBuilder.creature(260, 30, 42, 32, 1, 0.73).build());
        event.put(MonsterEntities.ICE_MIMIC.get(), CreatureAttributeBuilder.creature(260, 30, 42, 32, 1, 0.73).build());
        event.put(MonsterEntities.SHADOW_MIMIC.get(), CreatureAttributeBuilder.creature(260, 30, 42, 32, 1, 0.73).build());
        event.put(MonsterEntities.CRIMSON_MIMIC.get(), CreatureAttributeBuilder.creature(1820, 34, 47, 32, 1, 0.9).build());
        event.put(MonsterEntities.CORRUPT_MIMIC.get(), CreatureAttributeBuilder.creature(1820, 34, 47, 32, 1, 0.9).build());
        event.put(MonsterEntities.HALLOWED_MIMIC.get(), CreatureAttributeBuilder.creature(1820, 34, 47, 32, 1, 0.9).build());
        event.put(MonsterEntities.JUNGLE_MIMIC.get(), CreatureAttributeBuilder.creature(1820, 34, 47, 32, 1, 0.9).build());
        event.put(NpcEntities.ANGLER.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.FEMALE_ANGLER.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.TRAVELING_MERCHANT.get(), BaseNPC.createAttributes().build());
        event.put(NpcEntities.OLD_MAN.get(), BaseNPC.createAttributes().build());
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
        event.put(BossEntities.KING_SLIME.get(), KingSlime.createAttributes().build());
        event.put(BossEntities.EYE_OF_CTHULHU.get(), EyeOfCthulhu.createAttributes().build());
        event.put(BossEntities.SERVANT_OF_CTHULHU.get(), ServantOfCthulhu.createAttributes().build());
        // 每个世界吞噬怪体节独立持有生命值，主头部只负责把分裂后的全部链条汇总到同一 Boss 栏。
        event.put(BossEntities.EATER_OF_WORLDS.get(), EaterOfWorlds.createAttributes().build());
        event.put(BossEntities.QUEEN_BEE.get(), QueenBee.createAttributes().build());
        event.put(BossEntities.BRAIN_OF_CTHULHU.get(), BrainOfCthulhu.createAttributes().build());
        event.put(BossEntities.SKELETRON.get(), Skeletron.createAttributes().build());
        event.put(BossEntities.DUNGEON_GUARDIAN.get(), DungeonGuardian.createAttributes().build());
        event.put(BossEntities.THE_DESTROYER.get(), TheDestroyer.createAttributes().build());
        event.put(BossEntities.THE_DESTROYER_PROBE.get(), CreatureAttributeBuilder.boss(12, 100, 10).build());
        event.put(BossEntities.THE_TWINS.get(), TheTwins.createAttributes().build());
        event.put(BossEntities.RETINAZER.get(), CreatureAttributeBuilder.boss(19, 7800, 10).build());
        event.put(BossEntities.SPAZMATISM.get(), CreatureAttributeBuilder.boss(22, 8970, 10).build());
        event.put(BossEntities.SKELETRON_PRIME.get(), SkeletronPrime.createAttributes().build());
        event.put(BossEntities.WALL_OF_FLESH.get(), WallOfFlesh.createAttributes().build());
        event.put(BossEntities.PLANTERA.get(), Plantera.createAttributes().build());
        event.put(BossEntities.LUNATIC_CULTIST.get(), LunaticCultist.createAttributes().build());
        event.put(BossEntities.LUNATIC_CULTIST_CLONE.get(), LunaticCultistClone.createAttributes().build());
        event.put(BossEntities.PHANTASM_DRAGON.get(), PhantasmDragon.createAttributes().build());
        event.put(BossEntities.HILL_OF_FLESH.get(), HillOfFlesh.createAttributes().build());
        event.put(BossEntities.DEERCLOPS.get(), DeerClops.createAttributes().build());
        event.put(BossEntities.PRIME_ENDER_DRAGON.get(), PrimeEnderDragon.createAttributes().build());
    }

    private static void entityAttributeModification(PortEntityAttributeModificationEvent event) {
        Holder<Attribute> armorPenetration = LibAttributes.getArmorPenetration();
        event.add(BossEntities.QUEEN_BEE.get(), armorPenetration, 2);
        event.add(BossEntities.SKELETRON.get(), armorPenetration, 4);
        event.add(BossEntities.HILL_OF_FLESH.get(), armorPenetration, 4);
        event.add(BossEntities.WALL_OF_FLESH.get(), armorPenetration, 6);

        event.add(MonsterEntities.PIXIE.get(), armorPenetration, 8);
        event.add(MonsterEntities.WYVERN.get(), armorPenetration, 8);
        event.add(MonsterEntities.WRAITH.get(), armorPenetration, 8);
        event.add(MonsterEntities.POSSESS_ARMOR.get(), armorPenetration, 8);
        event.add(MonsterEntities.CORRUPT_SLIME.get(), armorPenetration, 8);
        event.add(MonsterEntities.LUMINOUS_SLIME.get(), armorPenetration, 8);
        event.add(MonsterEntities.CRIMSLIME.get(), armorPenetration, 8);
        event.add(MonsterEntities.WOODEN_MIMIC.get(), armorPenetration, 8);
        event.add(MonsterEntities.GOLDEN_MIMIC.get(), armorPenetration, 8);
        event.add(MonsterEntities.SHADOW_MIMIC.get(), armorPenetration, 8);
        event.add(MonsterEntities.ICE_MIMIC.get(), armorPenetration, 8);
        event.add(MonsterEntities.CRIMSON_MIMIC.get(), armorPenetration, 8);
        event.add(MonsterEntities.CORRUPT_MIMIC.get(), armorPenetration, 8);
        event.add(MonsterEntities.HALLOWED_MIMIC.get(), armorPenetration, 8);
        event.add(MonsterEntities.JUNGLE_MIMIC.get(), armorPenetration, 8);
        event.add(MonsterEntities.MUMMY.get(), armorPenetration, 8);
        event.add(MonsterEntities.DARK_MUMMY.get(), armorPenetration, 8);
        event.add(MonsterEntities.BLOOD_MUMMY.get(), armorPenetration, 8);
        event.add(MonsterEntities.LIGHT_MUMMY.get(), armorPenetration, 8);
        event.add(MonsterEntities.DARK_LAMIA.get(), armorPenetration, 8);
        event.add(MonsterEntities.LIGHT_LAMIA.get(), armorPenetration, 8);
        event.add(MonsterEntities.DERPLING.get(), armorPenetration, 8);
        event.add(MonsterEntities.HERPLING.get(), armorPenetration, 8);
        event.add(MonsterEntities.GHOUL.get(), armorPenetration, 8);
        event.add(MonsterEntities.VILE_GHOUL.get(), armorPenetration, 8);
        event.add(MonsterEntities.TAINTED_GHOUL.get(), armorPenetration, 8);
        event.add(MonsterEntities.DREAMER_GHOUL.get(), armorPenetration, 8);
        event.add(MonsterEntities.SAND_POACHER.get(), armorPenetration, 8);
        event.add(BossEntities.RETINAZER.get(), armorPenetration, 8);
        event.add(BossEntities.SPAZMATISM.get(), armorPenetration, 8);
        event.add(BossEntities.PLANTERA.get(), armorPenetration, 8);

        Holder<Attribute> armorToughness = Holder.direct(Attributes.ARMOR_TOUGHNESS);
        event.add(MonsterEntities.PIXIE.get(), armorToughness, 2);
        event.add(MonsterEntities.WYVERN.get(), armorToughness, 2);
        event.add(MonsterEntities.CORRUPT_SLIME.get(), armorToughness, 2);
        event.add(MonsterEntities.LUMINOUS_SLIME.get(), armorToughness, 2);
        event.add(MonsterEntities.CRIMSLIME.get(), armorToughness, 2);
        event.add(MonsterEntities.WOODEN_MIMIC.get(), armorToughness, 2);
        event.add(MonsterEntities.GOLDEN_MIMIC.get(), armorToughness, 2);
        event.add(MonsterEntities.SHADOW_MIMIC.get(), armorToughness, 2);
        event.add(MonsterEntities.ICE_MIMIC.get(), armorToughness, 2);
        event.add(MonsterEntities.CRIMSON_MIMIC.get(), armorToughness, 2);
        event.add(MonsterEntities.CORRUPT_MIMIC.get(), armorToughness, 2);
        event.add(MonsterEntities.HALLOWED_MIMIC.get(), armorToughness, 2);
        event.add(MonsterEntities.JUNGLE_MIMIC.get(), armorToughness, 2);
        event.add(MonsterEntities.MUMMY.get(), armorToughness, 2);
        event.add(MonsterEntities.DARK_MUMMY.get(), armorToughness, 2);
        event.add(MonsterEntities.BLOOD_MUMMY.get(), armorToughness, 2);
        event.add(MonsterEntities.LIGHT_MUMMY.get(), armorToughness, 2);
        event.add(MonsterEntities.DARK_LAMIA.get(), armorToughness, 2);
        event.add(MonsterEntities.LIGHT_LAMIA.get(), armorToughness, 2);
        event.add(MonsterEntities.DERPLING.get(), armorToughness, 2);
        event.add(MonsterEntities.HERPLING.get(), armorToughness, 2);
        event.add(MonsterEntities.GHOUL.get(), armorToughness, 2);
        event.add(MonsterEntities.VILE_GHOUL.get(), armorToughness, 2);
        event.add(MonsterEntities.TAINTED_GHOUL.get(), armorToughness, 2);
        event.add(MonsterEntities.DREAMER_GHOUL.get(), armorToughness, 2);
        event.add(MonsterEntities.SAND_POACHER.get(), armorToughness, 2);
        event.add(BossEntities.RETINAZER.get(), armorToughness, 2);
        event.add(BossEntities.SPAZMATISM.get(), armorToughness, 2);
        event.add(BossEntities.PLANTERA.get(), armorToughness, 2);
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

    private static void registerSpawnReplacements(PortRegisterSpawnPlacementsEvent event) {
        CreatureSpawnPlacements.register(event);
//        event.register(ModEntities.INVERSE_ENDERMAN.get(), InverseEntityType.ON_CEIL, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, InverseEnderMan::checkInverseEnderManSpawnRules, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 此时实体类型已经完成注册，可以安全建立实体变种与图鉴条目键的对应关系。
        PortEventHandler.postEvent(new RegisterBestiaryKeyEvent());
    }

    private static void registerBestiaryKeys(RegisterBestiaryKeyEvent event) {
        // 1.21 的图鉴使用数字后缀；1.20 的实体仍保存枚举名称，因此这里只转换图鉴键，
        // 不改变实体 NBT、变种初始化或渲染行为。
        event.register(CritterEntities.JEWEL_BUNNY.get(), (type, bunny) -> type.getDescriptionId() + '.' + switch (bunny.getVariant()) {
            case AMBER -> 0;
            case AMETHYST -> 1;
            case DIAMOND -> 2;
            case EMERALD -> 3;
            case GOLD -> 4;
            case RUBY -> 5;
            case SAPPHIRE -> 6;
            case TOPAZ -> 7;
            default -> 0;
        });
        event.register(CritterEntities.SQUIRREL.get(), (type, squirrel) -> type.getDescriptionId() + ".0");
        event.register(CritterEntities.RED_SQUIRREL.get(), (type, squirrel) -> "entity.confluence.squirrel.1");
        event.register(CritterEntities.JEWEL_SQUIRREL.get(), (type, squirrel) -> type.getDescriptionId() + '.' + switch (squirrel.getVariant()) {
            case AMBER -> 0;
            case GOLD -> 1;
            case AMETHYST -> 2;
            case DIAMOND -> 3;
            case EMERALD -> 4;
            case RUBY -> 5;
            case SAPPHIRE -> 6;
            case TOPAZ -> 7;
            default -> 0;
        });
        event.register(CritterEntities.GRASSHOPPER.get(), (type, grasshopper) -> type.getDescriptionId() + '.'
                + (grasshopper.getVariant() == Grasshopper.Variant.GOLD ? 0 : 1));
        event.register(CritterEntities.BUTTERFLY.get(), (type, butterfly) -> type.getDescriptionId() + '.' + switch (butterfly.getVariant()) {
            case GOLD -> 0;
            case JULIA -> 1;
            case MONARCH -> 2;
            case PURPLE_EMPEROR -> 3;
            case RED_ADMIRAL -> 4;
            case SULPHUR -> 5;
            case TREE_NYMPH -> 6;
            case ULYSSES -> 7;
            case ZEBRA_SWALLOWTAIL -> 8;
        });
        event.register(CritterEntities.WORM.get(), (type, worm) -> type.getDescriptionId() + '.' + switch (worm.getVariant()) {
            case NIGHTCRAWLER -> 0;
            case GOLD -> 1;
            case NORMAL -> 2;
        });
        event.register(CritterEntities.DRAGONFLY.get(), (type, dragonfly) -> type.getDescriptionId() + '.' + switch (dragonfly.getVariant()) {
            case BLACK -> 0;
            case BLUE -> 1;
            case GOLD -> 2;
            case GREEN -> 3;
            case ORANGE -> 4;
            case RED -> 5;
            case YELLOW -> 6;
        });
        event.register(CritterEntities.LADYBUG.get(), (type, ladybug) -> type.getDescriptionId() + '.'
                + (ladybug.getVariant() == Ladybug.Variant.GOLD ? 0 : 1));
        event.register(CritterEntities.FEALING.get(), (type, fealing) -> type.getDescriptionId() + ".0");
        event.register(CritterEntities.DUCK.get(), (type, duck) -> type.getDescriptionId() + '.'
                + (duck.getVariant() == Duck.Variant.MALLARD ? 0 : 1));
        event.register(CritterEntities.FAIRY.get(), (type, fairy) -> type.getDescriptionId() + '.' + switch (fairy.getVariant()) {
            case PINK -> 0;
            case GREEN -> 1;
            case BLUE -> 2;
        });
        event.register(CritterEntities.SCORPION.get(), (type, scorpion) -> type.getDescriptionId() + '.'
                + (scorpion.getVariant() == Scorpion.Variant.BLACK ? 0 : 1));
        event.register(MonsterEntities.DEMON_EYE.get(), (type, eye) -> type.getDescriptionId() + '.' + eye.getVariant().getSerializedName());
        event.register(BossEntities.SERVANT_OF_CTHULHU.get(), (type, servant) -> "entity.confluence.demon_eye.minion");
        event.register(MonsterEntities.BLACK_SLIME.get(), (type, slime) -> {
            if (slime.getSlimeSize() == 1) return "entity.confluence.baby_slime";
            if (slime.getSlimeSize() == 4) return "entity.confluence.mother_slime";
            return type.getDescriptionId();
        });
        event.register(MonsterEntities.ZOMBIE.get(), (type, zombie) -> switch (zombie.getVariant()) {
            case SLIMED -> "entity.minecraft.zombie.slime";
            case RAINCOAT -> "entity.minecraft.zombie.raincoat";
            case ESKIMO -> "entity.minecraft.zombie.frozen";
            default -> type.getDescriptionId();
        });
    }

    private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {
        event.register(MaterialItems.DEMONITE_INGOT, MaterialItems.CRIMTANE_INGOT);
        event.register(NatureBlocks.VILE_MUSHROOM, NatureBlocks.VICIOUS_MUSHROOM);
    }
}
