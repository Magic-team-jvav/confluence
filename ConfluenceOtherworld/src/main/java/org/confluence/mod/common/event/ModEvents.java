package org.confluence.mod.common.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
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
import org.confluence.mod.common.entity.InverseEnderMan;
import org.confluence.mod.common.entity.InverseEntityType;
import org.confluence.mod.common.entity.RainbowSheep;
import org.confluence.mod.common.entity.animal.*;
import org.confluence.mod.common.entity.boss.*;
import org.confluence.mod.common.entity.monster.*;
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
import org.confluence.mod.common.init.entity.*;
import org.confluence.mod.common.init.gun.GunSounds;
import org.confluence.mod.common.init.gun.GunTrailColors;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.DispenserRegistration;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.network.s2c.CompatibilitySyncPacketS2c;
import org.confluence.mod.util.ModUtils;
import org.confluence.terra_curio.api.event.RegisterAccessoriesComponentUnitValueTypeLocalSyncEvent;
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
import org.mesdag.portlib.wrapper.world.entity.PortSpawnPlacementTypes;

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
        PortEventHandler.addListener(ModEvents::registerAccessoriesComponentUnitValueTypeLocalSync);
        PortEventHandler.addListener(PortEventPriority.LOW, ModEvents::buildCreativeModeTabContents);
        PortEventHandler.addListener(ModEvents::blockEntityTypeAddBlocks);
//        PortEventHandler.addListener(ModEvents::registerCapabilities);
        PortEventHandler.addListener(PortEventPriority.LOW, ModEvents::registerSpawnReplacements);
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
        event.put(CritterEntities.BIRD.get(), Bird.createAttributes().build());
        event.put(CritterEntities.BLUE_JAY.get(), BlueJay.createAttributes().build());
        event.put(CritterEntities.CARDINAL.get(), Cardinal.createAttributes().build());
        event.put(CritterEntities.SQUIRREL.get(), Squirrel.createAttributes().build());
        event.put(CritterEntities.RED_SQUIRREL.get(), Squirrel.createAttributes().build());
        event.put(CritterEntities.JEWEL_SQUIRREL.get(), Squirrel.createAttributes().build());
        event.put(CritterEntities.WORM.get(), Worm.createAttributes().build());
        event.put(CritterEntities.DUCK.get(), Duck.createAttributes().build());
        event.put(CritterEntities.CRAB.get(), Crab.createAttributes().build());
        event.put(CritterEntities.JEWEL_SQUIRREL.get(), Squirrel.createAttributes().build());
        event.put(CritterEntities.BUTTERFLY.get(), Butterfly.createAttributes().build());
        event.put(CritterEntities.FAIRY.get(), Fairy.createAttributes().build());
        event.put(CritterEntities.GLOWING_SNAIL.get(), SimpleCritter.createAttributes().build());
        event.put(CritterEntities.GRUBBY.get(), SimpleCritter.createAttributes().build());
        event.put(CritterEntities.MAGGOT.get(), SimpleCritter.createAttributes().build());
        event.put(CritterEntities.SCORPION.get(), SimpleCritter.createAttributes().build());
        event.put(CritterEntities.HELL_BUTTERFLY.get(), HellButterfly.createAttributes().build());
        event.put(CritterEntities.PRISMATIC_LACEWING.get(), PrismaticLacewing.createAttributes().build());
        event.put(CritterEntities.DRAGONFLY.get(), Dragonfly.createAttributes().build());
        event.put(CritterEntities.GRASSHOPPER.get(), Grasshopper.createAttributes().build());
        event.put(CritterEntities.LADYBUG.get(), Ladybug.createAttributes().build());
        event.put(MonsterEntities.DEMON_EYE.get(), DemonEye.createAttributes().build());
        event.put(MonsterEntities.HARPY.get(), Harpy.createAttributes().build());
        event.put(MonsterEntities.PIXIE.get(), Pixie.createAttributes().build());
        event.put(MonsterEntities.EATER_OF_SOULS.get(), EaterOfSouls.createAttributes().build());
        event.put(MonsterEntities.CRIMERA.get(), EaterOfSouls.createAttributes().build());
        event.put(MonsterEntities.CURSED_SKULL.get(), CursedSkull.createAttributes().build());
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
        event.put(MonsterEntities.SNATCHER.get(), Snatcher.createAttributes().build());
        event.put(MonsterEntities.MAN_EATER.get(), Snatcher.createAttributes().build());
        event.put(MonsterEntities.SPORE_SKELETON.get(), MeleeSkeleton.createAttributes().build());
        event.put(MonsterEntities.BASE_BONES.get(), MeleeSkeleton.createAttributes().build());
        event.put(MonsterEntities.ANGER_BONES.get(), MeleeSkeleton.createAttributes().build());
        event.put(MonsterEntities.SHORT_BONES.get(), MeleeSkeleton.createAttributes().build());
        event.put(MonsterEntities.BIG_BONES.get(), MeleeSkeleton.createAttributes().build());
        event.put(MonsterEntities.BIG_ANGER_BONES.get(), MeleeSkeleton.createAttributes().build());
        event.put(MonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), MeleeSkeleton.createAttributes().build());
        event.put(MonsterEntities.BIG_HELMET_ANGER_BONES.get(), MeleeSkeleton.createAttributes().build());
        event.put(MonsterEntities.UNDEAD_VIKING.get(), MeleeSkeleton.createAttributes().build());
        event.put(MonsterEntities.WYVERN.get(), Wyvern.createAttributes().build());
        event.put(MonsterEntities.DEVOURER.get(), BaseWormMonster.createWormAttributes().build());
        event.put(MonsterEntities.TOMB_CRAWLER.get(), BaseWormMonster.createWormAttributes().build());
        event.put(MonsterEntities.GIANT_WORM.get(), BaseWormMonster.createWormAttributes().build());
        event.put(MonsterEntities.LEECH.get(), BaseWormMonster.createWormAttributes().build());
        event.put(MonsterEntities.BONE_SERPENT.get(), BaseWormMonster.createWormAttributes().build());
        event.put(MonsterEntities.WITHER_BONE_SERPENT.get(), BaseWormMonster.createWormAttributes().build());
        event.put(MonsterEntities.DARK_CASTER.get(), DarkCaster.createAttributes().build());
        event.put(MonsterEntities.GOBLIN_SORCERER.get(), DarkCaster.createAttributes().build());
        event.put(MonsterEntities.ZOMBIE.get(), Zombie.createAttributes().build());
        // 蝙蝠
        event.put(MonsterEntities.CAVE_BAT.get(), CaveBat.createAttributes().build());
        event.put(MonsterEntities.JUNGLE_BAT.get(), CaveBat.createAttributes().build());
        event.put(MonsterEntities.ICE_BAT.get(), CaveBat.createAttributes().build());
        event.put(MonsterEntities.GIANT_BAT.get(), CaveBat.createAttributes().build());
        event.put(MonsterEntities.HELL_BAT.get(), CaveBat.createAttributes().build());
        event.put(MonsterEntities.SPORE_BAT.get(), CaveBat.createAttributes().build());
        // 飞行怪
        event.put(MonsterEntities.DRIPPLER.get(), BaseFlyingMonster.createFlyingAttributes().build());
        event.put(MonsterEntities.FLYING_FISH.get(), BaseFlyingMonster.createFlyingAttributes().build());
        event.put(MonsterEntities.WANDERING_EYE_FISH.get(), BaseFlyingMonster.createFlyingAttributes().build());
        event.put(MonsterEntities.DEMON.get(), Demon.createAttributes().build());
        event.put(MonsterEntities.VOODOO_DEMON.get(), Demon.createAttributes().build());
        event.put(MonsterEntities.HORNET.get(), Hornet.createAttributes().build());
        event.put(MonsterEntities.LITTLE_HORNET.get(), Hornet.createAttributes().build());
        event.put(MonsterEntities.FIRE_IMP.get(), FireImp.createAttributes().build());
        event.put(MonsterEntities.DECAYEDER.get(), Decayeder.createAttributes().build());
        event.put(MonsterEntities.GHOST.get(), Ghost.createAttributes().build());
        event.put(MonsterEntities.DERPLING.get(), Derpling.createAttributes().build());
        event.put(MonsterEntities.HERPLING.get(), Derpling.createAttributes().build());
        event.put(MonsterEntities.METEOR_HEAD.get(), MeteorHead.createAttributes().build());
        event.put(MonsterEntities.GRANITE_ELEMENTAL.get(), GraniteElemental.createAttributes().build());
        event.put(MonsterEntities.ANTLION_SWARMER.get(), AntlionSwarmer.createAttributes().build());
        event.put(MonsterEntities.GIANT_ANTLION_SWARMER.get(), AntlionSwarmer.createAttributes().build());
        event.put(MonsterEntities.THE_HUNGRY.get(), TheHungry.createAttributes().build());
        event.put(MonsterEntities.BLOOD_ZOMBIE.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.SNOW_FLINX.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.FACE_MONSTER.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.BLOOD_TUMORS.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.POSSESS_ARMOR.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.POSSESS_ARMOR_VOID_VESSEL.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.MUMMY.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.DARK_MUMMY.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.BLOOD_MUMMY.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.LIGHT_MUMMY.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.DARK_LAMIA.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.LIGHT_LAMIA.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.GHOUL.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.TAINTED_GHOUL.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.VILE_GHOUL.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.DREAMER_GHOUL.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.GOBLIN_ARCHER.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.GOBLIN_PEON.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.GOBLIN_WARRIOR.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.GOBLIN_THIEF.get(), BaseWarriorMonster.createAttributes().build());
        event.put(MonsterEntities.GOBLIN_SCOUT.get(), BaseWarriorMonster.createAttributes().build());
        // 陆行怪
        event.put(MonsterEntities.BLOODY_SPORE.get(), BloodySpore.createAttributes().build());
        event.put(MonsterEntities.BLOOD_CRAWLER.get(), BloodCrawler.createAttributes().build());
        event.put(MonsterEntities.NYMPH.get(), Nymph.createAttributes().build());
        event.put(MonsterEntities.SAND_POACHER.get(), SandPoacher.createAttributes().build());
        // 水怪
        event.put(MonsterEntities.PIRANHA.get(), Piranha.createAttributes().build());
        event.put(MonsterEntities.ARAPAIMA.get(), Piranha.createAttributes().build());
        event.put(MonsterEntities.BLUE_JELLYFISH.get(), JellyFish.createAttributes().build());
        event.put(MonsterEntities.PINK_JELLYFISH.get(), JellyFish.createAttributes().build());
        event.put(MonsterEntities.GREEN_JELLYFISH.get(), JellyFish.createAttributes().build());
        event.put(MonsterEntities.SHARK.get(), Shark.createAttributes().build());
        // 卷壳怪
        event.put(MonsterEntities.GIANT_SHELLY.get(), GiantShelly.createAttributes().build());
        event.put(MonsterEntities.CRAWDAD.get(), Crawdad.createAttributes().build());
        // Wraith + Mimics
        event.put(MonsterEntities.WRAITH.get(), Wraith.createAttributes().build());
        event.put(MonsterEntities.WOODEN_MIMIC.get(), WoodenMimic.createAttributes().build());
        event.put(MonsterEntities.GOLDEN_MIMIC.get(), WoodenMimic.createAttributes().build());
        event.put(MonsterEntities.ICE_MIMIC.get(), WoodenMimic.createAttributes().build());
        event.put(MonsterEntities.SHADOW_MIMIC.get(), WoodenMimic.createAttributes().build());
        event.put(MonsterEntities.CRIMSON_MIMIC.get(), BaseMimic.createAttributes().build());
        event.put(MonsterEntities.CORRUPT_MIMIC.get(), BaseMimic.createAttributes().build());
        event.put(MonsterEntities.HALLOWED_MIMIC.get(), BaseMimic.createAttributes().build());
        event.put(MonsterEntities.JUNGLE_MIMIC.get(), BaseMimic.createAttributes().build());
        event.put(NpcEntities.ANGLER.get(), BaseNPC.createAttributes().build());
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
        event.put(BossEntities.EATER_OF_WORLDS.get(), EaterOfWorlds.createAttributes().build());
        event.put(BossEntities.QUEEN_BEE.get(), QueenBee.createAttributes().build());
        event.put(BossEntities.BRAIN_OF_CTHULHU.get(), BrainOfCthulhu.createAttributes().build());
        event.put(BossEntities.CREEPER_OF_CTHULHU.get(), CreeperOfCthulhu.createAttributes().build());
        event.put(BossEntities.SKELETRON.get(), Skeletron.createAttributes().build());
        event.put(BossEntities.DUNGEON_GUARDIAN.get(), DungeonGuardian.createAttributes().build());
        event.put(BossEntities.THE_DESTROYER.get(), TheDestroyer.createAttributes().build());
        event.put(BossEntities.THE_TWINS.get(), TheTwins.createAttributes().build());
        event.put(BossEntities.RETINAZER.get(), Retinazer.createAttributes().build());
        event.put(BossEntities.SPAZMATISM.get(), Spazmatism.createAttributes().build());
        event.put(BossEntities.SKELETRON_PRIME.get(), SkeletronPrime.createAttributes().build());
        event.put(BossEntities.WALL_OF_FLESH.get(), WallOfFlesh.createAttributes().build());
        event.put(BossEntities.HUNGRY.get(), Hungry.createAttributes().build());
        event.put(BossEntities.PLANTERA.get(), Plantera.createAttributes().build());
        event.put(BossEntities.LUNATIC_CULTIST.get(), LunaticCultist.createAttributes().build());
        event.put(BossEntities.PHANTASM_DRAGON.get(), PhantasmDragon.createAttributes().build());
        event.put(BossEntities.HILL_OF_FLESH.get(), HillOfFlesh.createAttributes().build());
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

// todo  private static void registerCapabilities(PortRegisterCapabilitiesEvent event) {
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
        event.register(MonsterEntities.GREEN_DUMPLING_SLIME.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkGreenDumplingSlimeSpawnRules, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.INVERSE_ENDERMAN.get(), InverseEntityType.ON_CEIL, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, InverseEnderMan::checkInverseEnderManSpawnRules, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CritterEntities.BUNNY.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CritterEntities.JEWEL_BUNNY.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(MonsterEntities.DEMON_EYE.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DemonEye::checkDemonEyeSpawnRules, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(MonsterEntities.GREEN_SLIME.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkGreenSlimeSpawnRules, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(MonsterEntities.BLUE_SLIME.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkBlueSlimeSpawnRules, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(MonsterEntities.ZOMBIE.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Zombie::checkZombieSpawnRules, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);

        PortEventHandler.postEvent(new RegisterBestiaryKeyEvent()); // 这个时期正好处于实体类型注册完的阶段，且datagen也会调用这个事件
    }

    private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {
        event.register(MaterialItems.DEMONITE_INGOT, MaterialItems.CRIMTANE_INGOT);
        event.register(NatureBlocks.VILE_MUSHROOM, NatureBlocks.VICIOUS_MUSHROOM);
    }
}
