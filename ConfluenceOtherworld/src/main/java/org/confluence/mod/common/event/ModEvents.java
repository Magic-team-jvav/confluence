package org.confluence.mod.common.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
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
import org.confluence.mod.common.entity.monster.CreatureAttributeBuilder;
import org.confluence.mod.common.entity.monster.DemonEye;
import org.confluence.mod.common.entity.monster.VisualNeuron;
import org.confluence.mod.common.entity.monster.humanoid.Zombie;
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
import org.mesdag.portlib.event.entity.PortRegisterSpawnPlacementsEvent;
import org.mesdag.portlib.event.other.PortBlockEntityTypeAddBlocksEvent;

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

    private static void commonSetup(FMLCommonSetupEvent event) {
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

    private static void loadComplete(FMLLoadCompleteEvent event) {
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

    private static void addPackFinders(AddPackFindersEvent event) {
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

    private static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        // 本体实体与伙伴
        event.put(ModEntities.BESTIARY_ENTRY_DISPLAY.get(), CreatureAttributeBuilder.living().build());
        event.put(ModEntities.RAINBOW_SHEEP.get(), CreatureAttributeBuilder.critter().maxHealth(8).movementSpeed(0.23).build());
        var storageCompanionAttributes = CreatureAttributeBuilder.critter().maxHealth(20).movementSpeed(0.35).flyingSpeed(0.45).followRange(32).build();
        event.put(ModEntities.CHESTER.get(), storageCompanionAttributes);
        event.put(ModEntities.FLYING_PIGGY_BANK.get(), storageCompanionAttributes);
        event.put(ModEntities.SUMMONED_IRON_GOLEM.get(), IronGolem.createAttributes().build());

        // 小动物
        event.put(CritterEntities.BUNNY.get(), CreatureAttributeBuilder.rabbit().jumpStrength(0.6).safeFallDistance(6).build());
        event.put(CritterEntities.PENGUIN.get(), CreatureAttributeBuilder.critter().maxHealth(5).movementSpeed(0.2).build());
        event.put(CritterEntities.MYSTIC_FROG.get(), Frog.createAttributes().add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 5).build());
        event.put(CritterEntities.GOLDFISH.get(), CreatureAttributeBuilder.critter().maxHealth(5).movementSpeed(0.2).build());
        event.put(CritterEntities.JEWEL_BUNNY.get(), CreatureAttributeBuilder.rabbit().jumpStrength(0.6).safeFallDistance(6).build());
        event.put(CritterEntities.EXPLOSIVE_BUNNY.get(), CreatureAttributeBuilder.rabbit().jumpStrength(0.6).safeFallDistance(6).build());
        event.put(CritterEntities.HOSTILE_BUNNY.get(), CreatureAttributeBuilder.rabbit().maxHealth(70).armor(4).jumpStrength(0.6).safeFallDistance(6).attackDamage(20).followRange(16).build());
        event.put(CritterEntities.BIRD.get(), CreatureAttributeBuilder.critter().maxHealth(6).movementSpeed(0.2).flyingSpeed(0.4).magicLibAttackDamage(3).fallDamageMultiplier(0).build());
        event.put(CritterEntities.BLUE_JAY.get(), CreatureAttributeBuilder.critter().maxHealth(6).movementSpeed(0.2).flyingSpeed(0.4).magicLibAttackDamage(3).fallDamageMultiplier(0).build());
        event.put(CritterEntities.CARDINAL.get(), CreatureAttributeBuilder.critter().maxHealth(6).movementSpeed(0.2).flyingSpeed(0.4).magicLibAttackDamage(3).fallDamageMultiplier(0).build());
        event.put(CritterEntities.SQUIRREL.get(), CreatureAttributeBuilder.critter().maxHealth(10).movementSpeed(0.2).safeFallDistance(6).build());
        event.put(CritterEntities.RED_SQUIRREL.get(), CreatureAttributeBuilder.critter().maxHealth(10).movementSpeed(0.2).safeFallDistance(6).build());
        event.put(CritterEntities.JEWEL_SQUIRREL.get(), CreatureAttributeBuilder.critter().maxHealth(10).movementSpeed(0.2).safeFallDistance(6).build());
        event.put(CritterEntities.WORM.get(), CreatureAttributeBuilder.insect().build());
        event.put(CritterEntities.DUCK.get(), CreatureAttributeBuilder.critter().maxHealth(4).movementSpeed(0.25).flyingSpeed(0.35).waterMovementEfficiency(1).fallDamageMultiplier(0).build());
        event.put(CritterEntities.CRAB.get(), CreatureAttributeBuilder.critter().maxHealth(21).armor(5).attackDamage(10).movementSpeed(0.2).followRange(20).knockbackResistance(0.25).build());
        event.put(CritterEntities.BUTTERFLY.get(), CreatureAttributeBuilder.flyingCritter().build());
        event.put(CritterEntities.FAIRY.get(), CreatureAttributeBuilder.flyingCritter().build());
        event.put(CritterEntities.FEALING.get(), CreatureAttributeBuilder.flyingCritter().build());
        event.put(CritterEntities.GLOWING_SNAIL.get(), CreatureAttributeBuilder.insect().build());
        event.put(CritterEntities.GRUBBY.get(), CreatureAttributeBuilder.insect().build());
        event.put(CritterEntities.GLOWING_MOOSHROOM.get(), Cow.createAttributes().build());
        event.put(CritterEntities.CLOUD_SHEEP.get(), Sheep.createAttributes().build());
        event.put(CritterEntities.CLUCKSHROOM.get(), Chicken.createAttributes().build());
        event.put(CritterEntities.GLOWING_CLUCKSHROOM.get(), Chicken.createAttributes().build());
        event.put(CritterEntities.FIREFLY.get(), CreatureAttributeBuilder.flyingCritter().build());
        event.put(CritterEntities.LIGHTNING_BUG.get(), CreatureAttributeBuilder.flyingCritter().build());
        event.put(CritterEntities.TRUFFLE_WORM.get(), CreatureAttributeBuilder.insect().build());
        event.put(CritterEntities.MAGGOT.get(), CreatureAttributeBuilder.insect().build());
        event.put(CritterEntities.MAGMA_SNAIL.get(), CreatureAttributeBuilder.insect().build());
        event.put(CritterEntities.SLUGGY.get(), CreatureAttributeBuilder.insect().build());
        event.put(CritterEntities.BUGGY.get(), CreatureAttributeBuilder.insect().maxHealth(5).build());
        event.put(CritterEntities.SNAIL.get(), CreatureAttributeBuilder.insect().build());
        event.put(CritterEntities.SCORPION.get(), CreatureAttributeBuilder.insect().build());
        event.put(CritterEntities.HELL_BUTTERFLY.get(), CreatureAttributeBuilder.flyingCritter().build());
        event.put(CritterEntities.PRISMATIC_LACEWING.get(), CreatureAttributeBuilder.flyingCritter().build());
        event.put(CritterEntities.DRAGONFLY.get(), CreatureAttributeBuilder.flyingCritter().build());
        event.put(CritterEntities.GRASSHOPPER.get(), CreatureAttributeBuilder.insect().build());
        event.put(CritterEntities.STINKBUG.get(), CreatureAttributeBuilder.flyingCritter().maxHealth(5).build());
        event.put(CritterEntities.LADYBUG.get(), CreatureAttributeBuilder.critter().maxHealth(3).movementSpeed(0.18).flyingSpeed(0.25).build());

        // 敌怪
        DemonEye.registerVariantStats(DemonEye.Variant.NORMAL, 15, 3.5, 1, 0.2);
        DemonEye.registerVariantStats(DemonEye.Variant.NORMAL_BIG, 12, 4, 2, 0.1);
        DemonEye.registerVariantStats(DemonEye.Variant.CATARACT, 11.5, 3.5, 2, 0.2);
        DemonEye.registerVariantStats(DemonEye.Variant.CATARACT_BIG, 14, 4, 2, 0.1);
        DemonEye.registerVariantStats(DemonEye.Variant.SLEEPY, 15, 3, 1, 0.2);
        DemonEye.registerVariantStats(DemonEye.Variant.SLEEPY_BIG, 16, 3.5, 1, 0.1);
        DemonEye.registerVariantStats(DemonEye.Variant.DILATED, 12, 3.5, 1, 0.1);
        DemonEye.registerVariantStats(DemonEye.Variant.DILATED_SMALL, 11.5, 3, 0, 0.2);
        DemonEye.registerVariantStats(DemonEye.Variant.GREEN, 15, 4, 0, 0.1);
        DemonEye.registerVariantStats(DemonEye.Variant.GREEN_SMALL, 12.5, 3, 0, 0.2);
        DemonEye.registerVariantStats(DemonEye.Variant.PURPLE, 15, 3, 2, 0.2);
        DemonEye.registerVariantStats(DemonEye.Variant.PURPLE_BIG, 16, 3, 2, 0.1);
        DemonEye.registerVariantStats(DemonEye.Variant.OWL, 18.5, 3, 3, 0.2);
        DemonEye.registerVariantStats(DemonEye.Variant.SPACESHIP, 15, 3, 2, 0.2);
        event.put(MonsterEntities.DEMON_EYE.get(), CreatureAttributeBuilder.creature().maxHealth(15).armor(1).attackDamage(3.5).followRange(40).attackKnockback(0).knockbackResistance(0).movementSpeed(0.2).flyingSpeed(0.6).build());
        event.put(MonsterEntities.HARPY.get(), CreatureAttributeBuilder.creature().maxHealth(41).armor(8).attackDamage(13).build());
        event.put(MonsterEntities.PIXIE.get(), CreatureAttributeBuilder.creature().maxHealth(78).armor(20).attackDamage(28).followRange(16).attackKnockback(1).knockbackResistance(0.46).build());
        event.put(MonsterEntities.EATER_OF_SOULS.get(), CreatureAttributeBuilder.creature().maxHealth(20).armor(6).attackDamage(11).followRange(30).attackKnockback(0.5).knockbackResistance(0.1).build());
        event.put(MonsterEntities.CRIMERA.get(), CreatureAttributeBuilder.creature().maxHealth(20).armor(6).attackDamage(11).followRange(30).attackKnockback(0.5).knockbackResistance(0.1).build());
        event.put(MonsterEntities.CURSED_SKULL.get(), CreatureAttributeBuilder.creature().maxHealth(21).armor(6).attackDamage(18).followRange(32).attackKnockback(1).knockbackResistance(0.82).build());
        event.put(MonsterEntities.CORRUPTOR.get(), CreatureAttributeBuilder.creature().maxHealth(120).armor(32).attackDamage(32).followRange(48).attackKnockback(1).knockbackResistance(0.51).build());
        event.put(MonsterEntities.SLIMER.get(), CreatureAttributeBuilder.creature().maxHealth(31).armor(20).attackDamage(23).followRange(48).attackKnockback(1).knockbackResistance(0.28).build());
        event.put(MonsterEntities.WINGLESS_SLIMER.get(), CreatureAttributeBuilder.slime().maxHealth(46).armor(20).attackDamage(23).followRange(48).attackKnockback(1).knockbackResistance(0.3).build());
        event.put(MonsterEntities.ENCHANTED_SWORD.get(), CreatureAttributeBuilder.creature().maxHealth(208).armor(20).attackDamage(41).followRange(48).attackKnockback(1).knockbackResistance(0.82).build());
        event.put(MonsterEntities.GREEN_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(9).armor(0).attackDamage(3).build());
        event.put(MonsterEntities.BLUE_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(16).armor(2).attackDamage(4).build());
        event.put(MonsterEntities.PINK_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(97).armor(2).attackDamage(2).build());
        event.put(MonsterEntities.DUNGEON_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(78).armor(2).attackDamage(15.6).build());
        event.put(MonsterEntities.CORRUPT_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(88).armor(20).attackDamage(28).build());
        event.put(MonsterEntities.DESERT_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(21).armor(5).attackDamage(6).build());
        event.put(MonsterEntities.JUNGLE_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(46).armor(6).attackDamage(12).build());
        event.put(MonsterEntities.ICE_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(13).armor(4).attackDamage(5).build());
        event.put(MonsterEntities.LAVA_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(30).armor(10).attackDamage(10).build());
        event.put(MonsterEntities.LUMINOUS_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(93).armor(30).attackDamage(36.4).build());
        event.put(MonsterEntities.CRIMSLIME.get(), CreatureAttributeBuilder.slime().maxHealth(104).armor(26).attackDamage(31.2).build());
        event.put(MonsterEntities.SLIMELING.get(), CreatureAttributeBuilder.slime().maxHealth(51).armor(10).attackDamage(20).build());
        event.put(MonsterEntities.PURPLE_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(25).armor(6).attackDamage(5).build());
        event.put(MonsterEntities.RED_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(25).armor(4).attackDamage(5).build());
        event.put(MonsterEntities.TROPIC_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(13).armor(1).attackDamage(5).build());
        event.put(MonsterEntities.YELLOW_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(25).armor(7).attackDamage(6).build());
        event.put(MonsterEntities.GREEN_DUMPLING_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(25).armor(0).attackDamage(5).build());
        event.put(MonsterEntities.SWAMP_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(25).armor(1).attackDamage(5).build());
        event.put(MonsterEntities.BLACK_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(25).armor(4).attackDamage(6).build());
        event.put(MonsterEntities.MOTHER_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(58).armor(7).attackDamage(10).build());
        event.put(MonsterEntities.BABY_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(25).armor(4).attackDamage(6).build());
        event.put(MonsterEntities.SWEET_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(16).armor(0).attackDamage(0).build());
        event.put(MonsterEntities.GOLDEN_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(97).armor(2).attackDamage(5).build());
        event.put(MonsterEntities.FLESH_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(50).armor(6).attackDamage(14).build());
        event.put(MonsterEntities.SPIKED_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(26).armor(5).attackDamage(7).build());
        event.put(MonsterEntities.SPIKED_JUNGLE_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(33).armor(8).attackDamage(15).build());
        event.put(MonsterEntities.SPIKED_ICE_SLIME.get(), CreatureAttributeBuilder.slime().maxHealth(31).armor(8).attackDamage(6).build());
        event.put(MonsterEntities.SNATCHER.get(), CreatureAttributeBuilder.creature().maxHealth(31).armor(10).attackDamage(13).followRange(20).attackKnockback(1).knockbackResistance(1).build());
        event.put(MonsterEntities.MAN_EATER.get(), CreatureAttributeBuilder.creature().maxHealth(57).armor(10).attackDamage(15).followRange(20).attackKnockback(1).knockbackResistance(1).build());
        event.put(MonsterEntities.FUNGI_BULB.get(), CreatureAttributeBuilder.creature().maxHealth(90).armor(4).attackDamage(24).followRange(20).knockbackResistance(1).build());
        event.put(MonsterEntities.GIANT_FUNGI_BULB.get(), CreatureAttributeBuilder.creature().maxHealth(300).armor(20).attackDamage(70).followRange(40).knockbackResistance(1).build());
        event.put(MonsterEntities.CLINGER.get(), CreatureAttributeBuilder.creature().maxHealth(320).armor(30).attackDamage(70).followRange(32).knockbackResistance(0.8).build());
        event.put(MonsterEntities.SPORE_SKELETON.get(), CreatureAttributeBuilder.creature().maxHealth(31).armor(8).attackDamage(11).followRange(60).attackKnockback(0.5).knockbackResistance(0.28).build());
        event.put(MonsterEntities.BASE_BONES.get(), CreatureAttributeBuilder.creature().maxHealth(41).armor(2).attackDamage(13).followRange(20).attackKnockback(1).knockbackResistance(0.28).movementSpeed(0.3).build());
        event.put(MonsterEntities.ANGER_BONES.get(), CreatureAttributeBuilder.creature().maxHealth(41).armor(8).attackDamage(13).build());
        event.put(MonsterEntities.SHORT_BONES.get(), CreatureAttributeBuilder.creature().maxHealth(37).armor(7).attackDamage(12).build());
        event.put(MonsterEntities.BIG_BONES.get(), CreatureAttributeBuilder.creature().maxHealth(52).armor(9).attackDamage(17).build());
        event.put(MonsterEntities.BIG_ANGER_BONES.get(), CreatureAttributeBuilder.creature().maxHealth(36).armor(6).attackDamage(17).build());
        event.put(MonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), CreatureAttributeBuilder.creature().maxHealth(36).armor(12).attackDamage(14).build());
        event.put(MonsterEntities.BIG_HELMET_ANGER_BONES.get(), CreatureAttributeBuilder.creature().maxHealth(62).armor(14).attackDamage(12).build());
        event.put(MonsterEntities.UNDEAD_VIKING.get(), CreatureAttributeBuilder.creature().maxHealth(36).armor(10).attackDamage(12).build());
        event.put(MonsterEntities.GIANT_TORTOISE.get(), CreatureAttributeBuilder.creature().maxHealth(244).armor(30).attackDamage(41).followRange(48).attackKnockback(1).knockbackResistance(0.73).movementSpeed(0.2).build());
        event.put(MonsterEntities.ARMORED_VIKING.get(), CreatureAttributeBuilder.creature().maxHealth(280).armor(28).attackDamage(50).knockbackResistance(0.55).movementSpeed(0.23).followRange(32).build());
        event.put(MonsterEntities.ARMORED_SKELETON.get(), CreatureAttributeBuilder.creature().maxHealth(260).armor(28).attackDamage(40).knockbackResistance(0.6).movementSpeed(0.23).followRange(32).build());
        event.put(MonsterEntities.ICE_ELEMENTAL.get(), CreatureAttributeBuilder.creature().maxHealth(200).armor(20).attackDamage(55).knockbackResistance(0.4).movementSpeed(0.18).followRange(40).build());
        event.put(MonsterEntities.ICY_MERMAN.get(), CreatureAttributeBuilder.creature().maxHealth(280).armor(30).attackDamage(60).knockbackResistance(0.5).movementSpeed(0.23).followRange(32).build());
        event.put(MonsterEntities.ICE_GOLEM.get(), CreatureAttributeBuilder.creature().maxHealth(4000).armor(32).attackDamage(60).knockbackResistance(0.95).movementSpeed(0.2).followRange(48).build());
        event.put(MonsterEntities.ROCK_GOLEM.get(), CreatureAttributeBuilder.creature().maxHealth(1000).armor(35).attackDamage(85).knockbackResistance(0.9).movementSpeed(0.2).followRange(32).build());
        event.put(MonsterEntities.ICE_TORTOISE.get(), CreatureAttributeBuilder.creature().maxHealth(400).armor(28).attackDamage(55).followRange(48).attackKnockback(1).knockbackResistance(0.7).movementSpeed(0.2).build());
        event.put(MonsterEntities.UNICORN.get(), CreatureAttributeBuilder.creature().maxHealth(416).armor(30).attackDamage(65).followRange(64).attackKnockback(1).knockbackResistance(0.82).movementSpeed(0.35).build());
        event.put(MonsterEntities.GASTROPOD.get(), CreatureAttributeBuilder.creature().maxHealth(143).armor(20).attackDamage(40).followRange(48).attackKnockback(1).knockbackResistance(0.64).build());
        event.put(MonsterEntities.WYVERN.get(), CreatureAttributeBuilder.creature().maxHealth(2080).armor(10).attackDamage(41).followRange(50).attackKnockback(1).knockbackResistance(0.28).build());
        event.put(MonsterEntities.ARCH_WYVERN.get(), CreatureAttributeBuilder.creature().maxHealth(3120).armor(18).attackDamage(52).followRange(64).attackKnockback(1).knockbackResistance(0.37).build());
        event.put(MonsterEntities.DEVOURER.get(), CreatureAttributeBuilder.creature().maxHealth(52).armor(2).attackDamage(8).build());
        event.put(MonsterEntities.WORLD_FEEDER.get(), CreatureAttributeBuilder.creature().maxHealth(500).armor(36).attackDamage(70).knockbackResistance(1.0).followRange(32).build());
        event.put(MonsterEntities.TOMB_CRAWLER.get(), CreatureAttributeBuilder.creature().maxHealth(16).armor(2).attackDamage(4).build());
        event.put(MonsterEntities.GIANT_WORM.get(), CreatureAttributeBuilder.creature().maxHealth(31).armor(3).attackDamage(9).build());
        event.put(MonsterEntities.DIGGER.get(), CreatureAttributeBuilder.creature().maxHealth(200).armor(10).attackDamage(45).knockbackResistance(1.0).followRange(32).build());
        event.put(MonsterEntities.LEECH.get(), CreatureAttributeBuilder.creature().maxHealth(36).armor(4).attackDamage(10).movementSpeed(0.145).build());
        event.put(MonsterEntities.BONE_SERPENT.get(), CreatureAttributeBuilder.creature().maxHealth(156).armor(12).attackDamage(18).build());
        event.put(MonsterEntities.WITHER_BONE_SERPENT.get(), CreatureAttributeBuilder.creature().maxHealth(186).armor(15).attackDamage(22).build());
        event.put(MonsterEntities.DARK_CASTER.get(), CreatureAttributeBuilder.creature().maxHealth(26).armor(2).attackDamage(10).followRange(20).attackKnockback(1).knockbackResistance(0.82).build());
        event.put(MonsterEntities.WATER_BOLT_MIMIC.get(), CreatureAttributeBuilder.creature().maxHealth(60).armor(4).attackDamage(20).followRange(32).knockbackResistance(0.8).build());
        event.put(MonsterEntities.TIM.get(), CreatureAttributeBuilder.creature().maxHealth(200).armor(4).attackDamage(20).knockbackResistance(0.4).followRange(32).build());
        event.put(MonsterEntities.RUNE_WIZARD.get(), CreatureAttributeBuilder.creature().maxHealth(600).armor(30).attackDamage(200).knockbackResistance(0.7).followRange(32).build());
        event.put(MonsterEntities.DOCTOR_BONES.get(), CreatureAttributeBuilder.creature().maxHealth(500).armor(10).attackDamage(20).knockbackResistance(0.5).followRange(32).build());
        event.put(MonsterEntities.THE_GROOM.get(), CreatureAttributeBuilder.creature().maxHealth(200).armor(8).attackDamage(14).knockbackResistance(0.5).followRange(32).build());
        event.put(MonsterEntities.THE_BRIDE.get(), CreatureAttributeBuilder.creature().maxHealth(200).armor(8).attackDamage(14).knockbackResistance(0.5).followRange(32).build());
        event.put(MonsterEntities.GOBLIN_SORCERER.get(), CreatureAttributeBuilder.creature().maxHealth(20).armor(2).attackDamage(10).followRange(32).attackKnockback(1).knockbackResistance(0.46).build());
        event.put(MonsterEntities.GOBLIN_WARLOCK.get(), CreatureAttributeBuilder.creature().maxHealth(2000).armor(26).attackDamage(80).movementSpeed(0.3).followRange(48).knockbackResistance(0.85).build());
        event.put(MonsterEntities.PIRATE_DECKHAND.get(), CreatureAttributeBuilder.creature().maxHealth(300).armor(17).attackDamage(35).movementSpeed(0.3).followRange(40).knockbackResistance(0.6).build());
        event.put(MonsterEntities.PIRATE_DEADEYE.get(), CreatureAttributeBuilder.creature().maxHealth(225).armor(14).attackDamage(30).movementSpeed(0.3).followRange(40).knockbackResistance(0.7).build());
        event.put(MonsterEntities.PIRATE_CROSSBOWER.get(), CreatureAttributeBuilder.creature().maxHealth(350).armor(20).attackDamage(35).movementSpeed(0.3).followRange(40).knockbackResistance(0.65).build());
        event.put(MonsterEntities.PIRATE_CORSAIR.get(), CreatureAttributeBuilder.creature().maxHealth(450).armor(22).attackDamage(50).movementSpeed(0.3).followRange(40).knockbackResistance(0.8).build());
        event.put(MonsterEntities.PIRATE_CAPTAIN.get(), CreatureAttributeBuilder.creature().maxHealth(3000).armor(30).attackDamage(70).movementSpeed(0.15).followRange(40).knockbackResistance(1).build());
        event.put(MonsterEntities.PIRATE_PARROT.get(), CreatureAttributeBuilder.creature().maxHealth(100).armor(12).attackDamage(80).movementSpeed(0.3).followRange(40).knockbackResistance(0.3).build());
        event.put(MonsterEntities.PIRATES_CURSE.get(), CreatureAttributeBuilder.creature().maxHealth(500).armor(0).attackDamage(75).movementSpeed(0.3).followRange(40).knockbackResistance(1).build());
        event.put(MonsterEntities.SHADOWFLAME_APPARITION.get(), CreatureAttributeBuilder.creature().maxHealth(180).armor(18).attackDamage(50).followRange(48).knockbackResistance(1).build());
        event.put(MonsterEntities.CHAOS_ELEMENTAL.get(), CreatureAttributeBuilder.creature().maxHealth(192).armor(30).attackDamage(21).followRange(48).attackKnockback(1).knockbackResistance(0.64).build());
        event.put(MonsterEntities.NECROMANCER.get(), CreatureAttributeBuilder.creature().maxHealth(260).armor(24).attackDamage(46).followRange(48).attackKnockback(1).knockbackResistance(0.73).build());
        event.put(MonsterEntities.DIABOLIST.get(), CreatureAttributeBuilder.creature().maxHealth(260).armor(28).attackDamage(52).followRange(48).attackKnockback(1).knockbackResistance(0.73).build());
        event.put(MonsterEntities.RAGGED_CASTER.get(), CreatureAttributeBuilder.creature().maxHealth(260).armor(22).attackDamage(44).followRange(48).attackKnockback(1).knockbackResistance(0.73).build());
        Zombie.registerVariantStats(Zombie.Variant.NORMAL, 20, 4, 2);
        Zombie.registerVariantStats(Zombie.Variant.ARMED, 24, 6, 3);
        Zombie.registerVariantStats(Zombie.Variant.SLIMED, 18, 3.5, 2);
        Zombie.registerVariantStats(Zombie.Variant.PINCUSHION, 22, 5, 3);
        Zombie.registerVariantStats(Zombie.Variant.TWIGGY, 20, 5, 1);
        Zombie.registerVariantStats(Zombie.Variant.SWAMP, 20, 3.5, 3);
        Zombie.registerVariantStats(Zombie.Variant.RAINCOAT, 22, 4.5, 2);
        Zombie.registerVariantStats(Zombie.Variant.BLOOD, 28, 6, 3);
        Zombie.registerVariantStats(Zombie.Variant.ESKIMO, 24, 5, 4);
        Zombie.registerVariantStats(Zombie.Variant.BALD, 18, 4.5, 1);
        event.put(MonsterEntities.ZOMBIE.get(), CreatureAttributeBuilder.creature().maxHealth(20).armor(2).attackDamage(4).followRange(16).attackKnockback(0.5).knockbackResistance(0.0).movementSpeed(0.23).build());
        // 蝙蝠
        event.put(MonsterEntities.CAVE_BAT.get(), CreatureAttributeBuilder.creature().maxHealth(8).armor(1).attackDamage(4).followRange(16).attackKnockback(0.2).knockbackResistance(0.5).build());
        event.put(MonsterEntities.JUNGLE_BAT.get(), CreatureAttributeBuilder.creature().maxHealth(17).armor(1).attackDamage(8).followRange(16).attackKnockback(0.2).knockbackResistance(0.5).build());
        event.put(MonsterEntities.ICE_BAT.get(), CreatureAttributeBuilder.creature().maxHealth(15).armor(2).attackDamage(7).followRange(16).attackKnockback(0.2).knockbackResistance(0.5).build());
        event.put(MonsterEntities.GIANT_BAT.get(), CreatureAttributeBuilder.creature().maxHealth(166).armor(16).attackDamage(34).followRange(32).attackKnockback(0.2).knockbackResistance(0.55).build());
        event.put(MonsterEntities.HELL_BAT.get(), CreatureAttributeBuilder.creature().maxHealth(23).armor(2).attackDamage(15).followRange(16).attackKnockback(0.2).knockbackResistance(0.5).build());
        event.put(MonsterEntities.LAVA_BAT.get(), CreatureAttributeBuilder.creature().maxHealth(160).armor(16).attackDamage(50).followRange(32).attackKnockback(0.2).knockbackResistance(0.4).build());
        event.put(MonsterEntities.RED_DEVIL.get(), CreatureAttributeBuilder.creature().maxHealth(600).armor(40).attackDamage(50).followRange(32).knockbackResistance(0.5).build());
        event.put(MonsterEntities.ILLUMINANT_BAT.get(), CreatureAttributeBuilder.creature().maxHealth(200).armor(30).attackDamage(75).followRange(32).attackKnockback(0.2).knockbackResistance(0.25).build());
        event.put(MonsterEntities.SPORE_BAT.get(), CreatureAttributeBuilder.creature().maxHealth(15).armor(2).attackDamage(7).followRange(16).attackKnockback(0.2).knockbackResistance(0.5).build());
        event.put(MonsterEntities.GIANT_FLYING_FOX.get(), CreatureAttributeBuilder.creature().maxHealth(221).armor(18).attackDamage(38).followRange(48).attackKnockback(0.5).knockbackResistance(0.64).build());
        // 飞行怪
        event.put(MonsterEntities.DRIPPLER.get(), CreatureAttributeBuilder.creature().maxHealth(26).armor(7).attackDamage(14).followRange(64).attackKnockback(0.5).knockbackResistance(0.2).build());
        event.put(MonsterEntities.FLYING_FISH.get(), CreatureAttributeBuilder.creature().maxHealth(10).armor(1).attackDamage(2).followRange(30).attackKnockback(0.5).knockbackResistance(0.3).build());
        event.put(MonsterEntities.WANDERING_EYE_FISH.get(), CreatureAttributeBuilder.creature().maxHealth(156).armor(18).attackDamage(15).followRange(60).attackKnockback(1).knockbackResistance(1).movementSpeed(2.2).build());
        event.put(MonsterEntities.ZOMBIE_MERMAN.get(), CreatureAttributeBuilder.creature().maxHealth(400).armor(20).attackDamage(40).followRange(48).knockbackResistance(1).movementSpeed(0.2).build());
        event.put(MonsterEntities.VISUAL_NEURON.get(), CreatureAttributeBuilder.creature().maxHealth(VisualNeuron.BASE_MAX_HEALTH).armor(10).attackDamage(9).followRange(0).attackKnockback(0).knockbackResistance(0.1).build());
        event.put(MonsterEntities.DEMON.get(), CreatureAttributeBuilder.creature().maxHealth(62).armor(8).attackDamage(20).followRange(16).attackKnockback(1).knockbackResistance(0.28).build());
        event.put(MonsterEntities.VOODOO_DEMON.get(), CreatureAttributeBuilder.creature().maxHealth(62).armor(8).attackDamage(20).followRange(16).attackKnockback(1).knockbackResistance(0.28).build());
        event.put(MonsterEntities.HORNET.get(), CreatureAttributeBuilder.creature().maxHealth(32).armor(6).attackDamage(13).followRange(32).attackKnockback(0).knockbackResistance(0.55).movementSpeed(0.5).build());
        event.put(MonsterEntities.MOSS_HORNET.get(), CreatureAttributeBuilder.creature().maxHealth(220).armor(22).attackDamage(70).followRange(32).attackKnockback(0).knockbackResistance(0.5).movementSpeed(0.5).build());
        event.put(MonsterEntities.JUNGLE_CREEPER.get(), CreatureAttributeBuilder.creature().maxHealth(400).armor(28).attackDamage(100).followRange(32).knockbackResistance(0.75).movementSpeed(0.28).build());
        event.put(MonsterEntities.WALL_CREEPER.get(), CreatureAttributeBuilder.creature().maxHealth(80).armor(10).attackDamage(30).followRange(32).knockbackResistance(0.75).movementSpeed(0.23).build());
        event.put(MonsterEntities.BLACK_RECLUSE.get(), CreatureAttributeBuilder.creature().maxHealth(350).armor(40).attackDamage(90).followRange(32).knockbackResistance(0.75).movementSpeed(0.28).build());
        event.put(MonsterEntities.LITTLE_HORNET.get(), CreatureAttributeBuilder.creature().maxHealth(3).armor(1).attackDamage(3).followRange(20).attackKnockback(0).knockbackResistance(0.2).build());
        event.put(MonsterEntities.FIRE_IMP.get(), CreatureAttributeBuilder.creature().maxHealth(36).armor(16).attackDamage(15).followRange(20).attackKnockback(1).knockbackResistance(0.55).build());
        event.put(MonsterEntities.DECAYEDER.get(), CreatureAttributeBuilder.creature().maxHealth(10).armor(6).attackDamage(6).build());
        event.put(MonsterEntities.GHOST.get(), CreatureAttributeBuilder.creature().maxHealth(26).armor(4).attackDamage(8).followRange(16).attackKnockback(0).knockbackResistance(0.55).gravity(0).build());
        event.put(MonsterEntities.DUNGEON_SPIRIT.get(), CreatureAttributeBuilder.creature().maxHealth(200).armor(30).attackDamage(70).movementSpeed(0.35).followRange(32).knockbackResistance(0.8).gravity(0).build());
        event.put(MonsterEntities.DERPLING.get(), CreatureAttributeBuilder.creature().maxHealth(156).armor(26).attackDamage(41).followRange(48).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.HERPLING.get(), CreatureAttributeBuilder.creature().maxHealth(114).armor(26).attackDamage(33).followRange(48).attackKnockback(1).knockbackResistance(0.73).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.METEOR_HEAD.get(), CreatureAttributeBuilder.creature().maxHealth(13).armor(6).attackDamage(21).followRange(32).attackKnockback(1).knockbackResistance(0.64).build());
        event.put(MonsterEntities.GRANITE_ELEMENTAL.get(), CreatureAttributeBuilder.creature().maxHealth(46).armor(8).attackDamage(17).followRange(32).attackKnockback(1).knockbackResistance(0.73).build());
        event.put(MonsterEntities.GRANITE_GOLEM.get(), CreatureAttributeBuilder.creature().maxHealth(110).armor(18).attackDamage(30).knockbackResistance(0.65).movementSpeed(0.2).build());
        event.put(MonsterEntities.HOPLITE.get(), CreatureAttributeBuilder.creature().maxHealth(70).armor(10).attackDamage(22).knockbackResistance(0.6).movementSpeed(0.23).followRange(32).build());
        event.put(MonsterEntities.ANGRY_DANDELION.get(), CreatureAttributeBuilder.creature().maxHealth(50).armor(0).attackDamage(15).knockbackResistance(1.0).movementSpeed(0.0).followRange(32).build());
        event.put(MonsterEntities.WINDY_BALLOON.get(), CreatureAttributeBuilder.creature().maxHealth(1).armor(0).attackDamage(0).knockbackResistance(0.7).followRange(16).build());
        event.put(MonsterEntities.OLD_SHAKING_CHEST.get(), CreatureAttributeBuilder.creature().maxHealth(250).armor(10).attackDamage(0).movementSpeed(0.2).knockbackResistance(0.3).build());
        event.put(MonsterEntities.CLUMSY_BALLOON_SLIME.get(), CreatureAttributeBuilder.creature().maxHealth(1).attackDamage(0).movementSpeed(0.0).build());
        event.put(MonsterEntities.ANGRY_TUMBLER.get(), CreatureAttributeBuilder.creature().maxHealth(50).armor(6).attackDamage(30).knockbackResistance(0.2).movementSpeed(0.25).followRange(32).build());
        event.put(MonsterEntities.SAND_SHARK.get(), CreatureAttributeBuilder.creature().maxHealth(360).armor(20).attackDamage(50).knockbackResistance(0.1).followRange(40).build());
        event.put(MonsterEntities.BONE_BITER.get(), CreatureAttributeBuilder.creature().maxHealth(380).armor(24).attackDamage(60).knockbackResistance(0.2).followRange(40).build());
        event.put(MonsterEntities.FLESH_REAVER.get(), CreatureAttributeBuilder.creature().maxHealth(400).armor(22).attackDamage(64).knockbackResistance(0.2).followRange(40).build());
        event.put(MonsterEntities.CRYSTAL_THRESHER.get(), CreatureAttributeBuilder.creature().maxHealth(450).armor(26).attackDamage(54).knockbackResistance(0.3).followRange(40).build());
        event.put(MonsterEntities.ANGRY_NIMBUS.get(), CreatureAttributeBuilder.creature().maxHealth(300).armor(24).attackDamage(50).knockbackResistance(0.7).movementSpeed(0.3).flyingSpeed(0.5).followRange(48).build());
        event.put(MonsterEntities.ANTLION_SWARMER.get(), CreatureAttributeBuilder.creature().maxHealth(31).armor(8).attackDamage(15).followRange(32).attackKnockback(1).knockbackResistance(0.55).build());
        event.put(MonsterEntities.ANTLION_CHARGER.get(), CreatureAttributeBuilder.creature().maxHealth(80).armor(10).attackDamage(25).movementSpeed(0.25).knockbackResistance(0.5).followRange(32).build());
        event.put(MonsterEntities.ANTLION.get(), CreatureAttributeBuilder.creature().maxHealth(45).armor(6).attackDamage(10).movementSpeed(0.0).knockbackResistance(1.0).followRange(24).build());
        event.put(MonsterEntities.ANTLION_LARVA.get(), CreatureAttributeBuilder.creature().maxHealth(30).armor(2).attackDamage(10).movementSpeed(0.3).knockbackResistance(0.25).followRange(24).build());
        event.put(MonsterEntities.GNOME.get(), CreatureAttributeBuilder.creature().maxHealth(25).armor(0).attackDamage(10).movementSpeed(0.3).followRange(24).build());
        event.put(MonsterEntities.DESERT_SPIRIT.get(), CreatureAttributeBuilder.creature().maxHealth(220).armor(20).attackDamage(40).knockbackResistance(1.0).followRange(32).build());
        event.put(MonsterEntities.GIANT_ANTLION_SWARMER.get(), CreatureAttributeBuilder.creature().maxHealth(46).armor(12).attackDamage(17).followRange(32).attackKnockback(1).knockbackResistance(0.73).build());
        event.put(MonsterEntities.THE_HUNGRY.get(), CreatureAttributeBuilder.creature().maxHealth(87).armor(16).attackDamage(15).followRange(32).attackKnockback(0.75).knockbackResistance(1).build());
        event.put(MonsterEntities.HILL_HUNGRY.get(), CreatureAttributeBuilder.creature().maxHealth(87).armor(16).attackDamage(15).followRange(32).attackKnockback(0.75).knockbackResistance(1).build());
        event.put(MonsterEntities.BLOOD_ZOMBIE.get(), CreatureAttributeBuilder.creature().maxHealth(39).armor(8).attackDamage(10).followRange(60).attackKnockback(0.5).knockbackResistance(0.1).movementSpeed(0.15).build());
        event.put(MonsterEntities.SNOW_FLINX.get(), CreatureAttributeBuilder.creature().maxHealth(36).armor(12).attackDamage(13).followRange(60).attackKnockback(0.1).knockbackResistance(0.1).build());
        event.put(MonsterEntities.FACE_MONSTER.get(), CreatureAttributeBuilder.creature().maxHealth(36).armor(10).attackDamage(13).stepHeight(3.2).jumpStrength(0.8).build());
        event.put(MonsterEntities.BLOOD_TUMORS.get(), CreatureAttributeBuilder.creature().maxHealth(5).armor(2).attackDamage(0).followRange(0).attackKnockback(0).knockbackResistance(0).movementSpeed(0).safeFallDistance(100).build());
        event.put(MonsterEntities.POSSESS_ARMOR.get(), CreatureAttributeBuilder.creature().maxHealth(135).armor(10).attackDamage(28).followRange(32).attackKnockback(1).knockbackResistance(0.64).build());
        event.put(MonsterEntities.WEREWOLF.get(), CreatureAttributeBuilder.creature().maxHealth(350).armor(38).attackDamage(70).followRange(32).attackKnockback(1).knockbackResistance(0.6).movementSpeed(0.3).build());
        event.put(MonsterEntities.CORRUPT_PENGUIN.get(), CreatureAttributeBuilder.creature().maxHealth(70).armor(4).attackDamage(20).followRange(32).attackKnockback(1).knockbackResistance(0).movementSpeed(0.25).build());
        event.put(MonsterEntities.VICIOUS_PENGUIN.get(), CreatureAttributeBuilder.creature().maxHealth(75).armor(5).attackDamage(21).followRange(32).attackKnockback(1).knockbackResistance(0).movementSpeed(0.25).build());
        event.put(MonsterEntities.MUMMY.get(), CreatureAttributeBuilder.creature().maxHealth(67).armor(16).attackDamage(26).followRange(48).attackKnockback(1).knockbackResistance(0.46).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.DARK_MUMMY.get(), CreatureAttributeBuilder.creature().maxHealth(93).armor(18).attackDamage(32).followRange(48).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.BLOOD_MUMMY.get(), CreatureAttributeBuilder.creature().maxHealth(93).armor(18).attackDamage(32).followRange(48).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.LIGHT_MUMMY.get(), CreatureAttributeBuilder.creature().maxHealth(104).armor(18).attackDamage(28).followRange(48).attackKnockback(1).knockbackResistance(0.51).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.DARK_LAMIA.get(), CreatureAttributeBuilder.creature().maxHealth(182).armor(28).attackDamage(27).followRange(48).attackKnockback(1).knockbackResistance(0.69).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.LIGHT_LAMIA.get(), CreatureAttributeBuilder.creature().maxHealth(182).armor(28).attackDamage(27).followRange(48).attackKnockback(1).knockbackResistance(0.69).stepHeight(3.2).jumpStrength(0.5).build());
        event.put(MonsterEntities.GHOUL.get(), CreatureAttributeBuilder.creature().maxHealth(93).armor(26).attackDamage(26).followRange(64).attackKnockback(1).knockbackResistance(0.46).stepHeight(3.2).jumpStrength(0.7).build());
        event.put(MonsterEntities.BASILISK.get(), CreatureAttributeBuilder.creature().maxHealth(270).armor(34).attackDamage(65).followRange(32).movementSpeed(0.35).attackKnockback(1).knockbackResistance(0.7).build());
        event.put(MonsterEntities.TAINTED_GHOUL.get(), CreatureAttributeBuilder.creature().maxHealth(114).armor(32).attackDamage(33).followRange(64).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.7).build());
        event.put(MonsterEntities.VILE_GHOUL.get(), CreatureAttributeBuilder.creature().maxHealth(130).armor(30).attackDamage(31).followRange(64).attackKnockback(1).knockbackResistance(0.64).stepHeight(3.2).jumpStrength(0.7).build());
        event.put(MonsterEntities.DREAMER_GHOUL.get(), CreatureAttributeBuilder.creature().maxHealth(156).armor(32).attackDamage(28).followRange(64).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.7).build());
        event.put(MonsterEntities.PALADIN.get(), CreatureAttributeBuilder.creature().maxHealth(520).armor(52).attackDamage(52).followRange(64).attackKnockback(1).knockbackResistance(1).build());
        event.put(MonsterEntities.BONE_LEE.get(), CreatureAttributeBuilder.creature().maxHealth(520).armor(34).attackDamage(48).followRange(48).attackKnockback(1).knockbackResistance(0.95).movementSpeed(0.38).build());
        event.put(MonsterEntities.GOBLIN_ARCHER.get(), CreatureAttributeBuilder.creature().maxHealth(41).armor(6).attackDamage(11).followRange(32).attackKnockback(1).knockbackResistance(0.37).build());
        event.put(MonsterEntities.GOBLIN_PEON.get(), CreatureAttributeBuilder.creature().maxHealth(31).armor(4).attackDamage(6).followRange(32).attackKnockback(1).knockbackResistance(0.2).build());
        event.put(MonsterEntities.GOBLIN_WARRIOR.get(), CreatureAttributeBuilder.creature().maxHealth(57).armor(8).attackDamage(13).followRange(32).attackKnockback(1).knockbackResistance(0.6).build());
        event.put(MonsterEntities.GOBLIN_THIEF.get(), CreatureAttributeBuilder.creature().maxHealth(41).armor(6).attackDamage(10).followRange(32).attackKnockback(1).knockbackResistance(0.37).build());
        event.put(MonsterEntities.GOBLIN_SCOUT.get(), CreatureAttributeBuilder.creature().maxHealth(41).armor(6).attackDamage(10).followRange(32).attackKnockback(1).knockbackResistance(0.37).build());
        event.put(MonsterEntities.ANGER_GOBLIN.get(), CreatureAttributeBuilder.creature().maxHealth(220).armor(0).attackDamage(15).followRange(32).attackKnockback(1).knockbackResistance(0.88).build());
        // 陆行怪
        event.put(MonsterEntities.BLOODY_SPORE.get(), CreatureAttributeBuilder.creature().maxHealth(100).armor(6).attackDamage(0).followRange(32).attackKnockback(0).knockbackResistance(0.8).spawnReinforcementsChance(0.01).build());
        event.put(MonsterEntities.BLOOD_CRAWLER.get(), CreatureAttributeBuilder.creature().maxHealth(31).armor(8).attackDamage(15).followRange(32).attackKnockback(1).knockbackResistance(0.8)
                .movementSpeed(0.38).spawnReinforcementsChance(0.01).build());
        event.put(MonsterEntities.SPORE_ZOMBIE.get(), CreatureAttributeBuilder.creature().maxHealth(93).armor(10).attackDamage(20).followRange(60).attackKnockback(0.6).knockbackResistance(0.1).movementSpeed(0.08).build());
        event.put(MonsterEntities.HAT_SPORE_ZOMBIE.get(), CreatureAttributeBuilder.creature().maxHealth(114).armor(16).attackDamage(19).followRange(60).attackKnockback(0.6).knockbackResistance(0.72).movementSpeed(0.08).build());
        event.put(MonsterEntities.NYMPH.get(), CreatureAttributeBuilder.creature().maxHealth(156).armor(16).attackDamage(15).followRange(15).attackKnockback(1).knockbackResistance(0.5).build());
        event.put(MonsterEntities.SAND_POACHER.get(), CreatureAttributeBuilder.creature().maxHealth(166).armor(24).attackDamage(34).followRange(64).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.5).build());
        // 水怪
        event.put(MonsterEntities.PIRANHA.get(), CreatureAttributeBuilder.aquatic().maxHealth(15).armor(2).attackDamage(13).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1).build());
        event.put(MonsterEntities.BLOOD_FEEDER.get(), CreatureAttributeBuilder.creature().maxHealth(78).armor(20).attackDamage(26).followRange(32).attackKnockback(0.5).knockbackResistance(0.10).build());
        event.put(MonsterEntities.CORRUPT_GOLDFISH.get(), CreatureAttributeBuilder.aquatic().maxHealth(100).armor(6).attackDamage(30).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0).build());
        event.put(MonsterEntities.VICIOUS_GOLDFISH.get(), CreatureAttributeBuilder.aquatic().maxHealth(110).armor(7).attackDamage(31).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0).build());
        event.put(MonsterEntities.ANGLER_FISH.get(), CreatureAttributeBuilder.aquatic().maxHealth(90).armor(22).attackDamage(80).followRange(24).movementSpeed(1.5).attackKnockback(0.5).knockbackResistance(0).build());
        event.put(MonsterEntities.ARAPAIMA.get(), CreatureAttributeBuilder.aquatic().maxHealth(104).armor(30).attackDamage(39).followRange(32).movementSpeed(1.2).attackKnockback(0.1).knockbackResistance(0.1).build());
        event.put(MonsterEntities.BLUE_JELLYFISH.get(), CreatureAttributeBuilder.aquatic().maxHealth(17).armor(4).attackDamage(13).followRange(16).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1).build());
        event.put(MonsterEntities.PINK_JELLYFISH.get(), CreatureAttributeBuilder.aquatic().maxHealth(36).armor(6).attackDamage(15).followRange(16).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1).build());
        event.put(MonsterEntities.GREEN_JELLYFISH.get(), CreatureAttributeBuilder.aquatic().maxHealth(62).armor(18).attackDamage(41).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1).build());
        event.put(MonsterEntities.BLOOD_JELLY.get(), CreatureAttributeBuilder.aquatic().maxHealth(150).armor(20).attackDamage(75).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0).build());
        event.put(MonsterEntities.FUNGO_FISH.get(), CreatureAttributeBuilder.aquatic().maxHealth(140).armor(20).attackDamage(90).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0).build());
        event.put(MonsterEntities.SHARK.get(), CreatureAttributeBuilder.aquatic().maxHealth(156).armor(2).attackDamage(20).followRange(48).movementSpeed(1.2).attackKnockback(0.37).knockbackResistance(0.1).build());
        // 卷壳怪
        event.put(MonsterEntities.GIANT_SHELLY.get(), CreatureAttributeBuilder.creature().maxHealth(26).armor(12).attackDamage(9).followRange(20).attackKnockback(0).knockbackResistance(0.4).movementSpeed(0.1).build());
        event.put(MonsterEntities.CRAWDAD.get(), CreatureAttributeBuilder.creature().maxHealth(26).armor(6).attackDamage(15).followRange(25).attackKnockback(0).knockbackResistance(0.1).jumpStrength(0.8).build());
        // 幻灵与宝箱怪
        event.put(MonsterEntities.WRAITH.get(), CreatureAttributeBuilder.creature().maxHealth(83).armor(0).attackDamage(33).followRange(32).attackKnockback(1).knockbackResistance(0.37).gravity(0).build());
        event.put(MonsterEntities.WOODEN_MIMIC.get(), CreatureAttributeBuilder.creature().maxHealth(260).armor(30).attackDamage(42).followRange(32).attackKnockback(1).knockbackResistance(0.73).build());
        event.put(MonsterEntities.GOLDEN_MIMIC.get(), CreatureAttributeBuilder.creature().maxHealth(260).armor(30).attackDamage(42).followRange(32).attackKnockback(1).knockbackResistance(0.73).build());
        event.put(MonsterEntities.ICE_MIMIC.get(), CreatureAttributeBuilder.creature().maxHealth(260).armor(30).attackDamage(42).followRange(32).attackKnockback(1).knockbackResistance(0.73).build());
        event.put(MonsterEntities.SHADOW_MIMIC.get(), CreatureAttributeBuilder.creature().maxHealth(260).armor(30).attackDamage(42).followRange(32).attackKnockback(1).knockbackResistance(0.73).build());
        event.put(MonsterEntities.CRIMSON_MIMIC.get(), CreatureAttributeBuilder.creature().maxHealth(1820).armor(34).attackDamage(47).followRange(32).attackKnockback(1).knockbackResistance(0.9).build());
        event.put(MonsterEntities.CORRUPT_MIMIC.get(), CreatureAttributeBuilder.creature().maxHealth(1820).armor(34).attackDamage(47).followRange(32).attackKnockback(1).knockbackResistance(0.9).build());
        event.put(MonsterEntities.HALLOWED_MIMIC.get(), CreatureAttributeBuilder.creature().maxHealth(1820).armor(34).attackDamage(47).followRange(32).attackKnockback(1).knockbackResistance(0.9).build());
        event.put(MonsterEntities.JUNGLE_MIMIC.get(), CreatureAttributeBuilder.creature().maxHealth(1820).armor(34).attackDamage(47).followRange(32).attackKnockback(1).knockbackResistance(0.9).build());
        // 城镇 NPC
        event.put(NpcEntities.ANGLER.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.FEMALE_ANGLER.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.TRAVELING_MERCHANT.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.OLD_MAN.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.GUIDE.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.MERCHANT.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.NURSE.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.DEMOLITIONIST.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.DYE_TRADER.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.PAINTER.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.DRYAD.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.ARMS_DEALER.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.GOBLIN_TINKERER.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.WITCH_DOCTOR.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.CLOTHIER.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.MECHANIC.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.PARTY_GIRL.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.STYLIST.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.TAX_COLLECTOR.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.TRUFFLE.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.WIZARD.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.ZOOLOGIST.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.STEAMPUNKER.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.CYBORG.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.GOLFER.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.NERDY_SLIME.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.COOL_SLIME.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.ELDER_SLIME.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.CLUMSY_SLIME.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.DIVA_SLIME.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.SURLY_SLIME.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.MYSTIC_SLIME.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.SQUIRE_SLIME.get(), CreatureAttributeBuilder.npc().build());
        event.put(NpcEntities.SKELETON_MERCHANT.get(), CreatureAttributeBuilder.npc().build());

        // 困难模式前 Boss 与独立战斗部件
        event.put(BossEntities.KING_SLIME.get(), CreatureAttributeBuilder.boss().maxHealth(728).armor(10).attackDamage(16.5).followRange(100).attackKnockback(2.2).knockbackResistance(1).build());
        event.put(BossEntities.EYE_OF_CTHULHU.get(), CreatureAttributeBuilder.boss().maxHealth(728).armor(12).attackDamage(4).followRange(300).attackKnockback(2).knockbackResistance(1).build());
        event.put(BossEntities.SERVANT_OF_CTHULHU.get(), CreatureAttributeBuilder.boss().maxHealth(10).armor(1).attackDamage(3).followRange(30).attackKnockback(0.5).knockbackResistance(0.3).movementSpeed(0.25).build());
        event.put(BossEntities.EATER_OF_WORLDS.get(), CreatureAttributeBuilder.boss().maxHealth(54).armor(4).attackDamage(11.5).followRange(300).knockbackResistance(1).build());
        event.put(BossEntities.EATER_OF_WORLDS_SEGMENT.get(), CreatureAttributeBuilder.boss().maxHealth(50).armor(6).attackDamage(4).followRange(300).knockbackResistance(1).build());
        event.put(BossEntities.QUEEN_BEE.get(), CreatureAttributeBuilder.boss().maxHealth(1237).armor(8).attackDamage(14).followRange(300).attackKnockback(2).knockbackResistance(1).build());
        event.put(BossEntities.BRAIN_OF_CTHULHU.get(), CreatureAttributeBuilder.boss().maxHealth(552).armor(14).attackDamage(14).followRange(300).attackKnockback(2.5).knockbackResistance(0.5).build());
        event.put(BossEntities.SKELETRON.get(), CreatureAttributeBuilder.boss().maxHealth(2288).armor(10).attackDamage(18.2).followRange(300).knockbackResistance(1).build());
        event.put(BossEntities.SKELETRON_HAND.get(), CreatureAttributeBuilder.boss().maxHealth(405).armor(4).attackDamage(10).followRange(300).knockbackResistance(1).build());
        event.put(BossEntities.DUNGEON_GUARDIAN.get(), CreatureAttributeBuilder.boss().maxHealth(9999).armor(9999).attackDamage(9999).followRange(100).knockbackResistance(1).build());
        event.put(BossEntities.DEERCLOPS.get(), CreatureAttributeBuilder.boss().maxHealth(3094).armor(10).attackDamage(10.4).followRange(300).knockbackResistance(1).movementSpeed(0.4).build());
        event.put(BossEntities.WALL_OF_FLESH.get(), CreatureAttributeBuilder.boss().maxHealth(3096).armor(6).attackDamage(39).followRange(120).knockbackResistance(1).movementSpeed(0.125).build());

        // 机械 Boss 与独立战斗部件
        event.put(BossEntities.THE_TWINS.get(), CreatureAttributeBuilder.boss().maxHealth(1).armor(10).attackDamage(15).followRange(0).knockbackResistance(1).build());
        event.put(BossEntities.RETINAZER.get(), CreatureAttributeBuilder.boss().maxHealth(7800).armor(10).attackDamage(19).followRange(300).knockbackResistance(0.8).movementSpeed(0.3).flyingSpeed(0.6).build());
        event.put(BossEntities.SPAZMATISM.get(), CreatureAttributeBuilder.boss().maxHealth(8970).armor(10).attackDamage(22).followRange(300).knockbackResistance(0.8).movementSpeed(0.3).flyingSpeed(0.6).build());
        event.put(BossEntities.THE_DESTROYER.get(), CreatureAttributeBuilder.boss().maxHealth(23333).armor(2).attackDamage(35).followRange(300).knockbackResistance(1).build());
        event.put(BossEntities.THE_DESTROYER_PART.get(), CreatureAttributeBuilder.boss().maxHealth(23333).armor(2).attackDamage(66).followRange(96).knockbackResistance(1).build());
        event.put(BossEntities.THE_DESTROYER_PROBE.get(), CreatureAttributeBuilder.boss().maxHealth(100).armor(10).attackDamage(12).followRange(64).knockbackResistance(1).build());
        event.put(BossEntities.SKELETRON_PRIME.get(), CreatureAttributeBuilder.boss().maxHealth(10920).armor(6).attackDamage(21).followRange(300).knockbackResistance(1).build());
        event.put(BossEntities.SKELETRON_PRIME_PART.get(), CreatureAttributeBuilder.boss().maxHealth(2080).armor(26).attackDamage(8).followRange(64).knockbackResistance(1).build());

        // 困难模式后期与扩展 Boss
        event.put(BossEntities.PLANTERA.get(), CreatureAttributeBuilder.boss().maxHealth(10920).armor(36).attackDamage(26).followRange(300).knockbackResistance(1).build());
        event.put(BossEntities.PLANTERA_HOOK.get(), CreatureAttributeBuilder.boss().maxHealth(1040).armor(24).attackDamage(15.6).followRange(64).knockbackResistance(1).build());
        event.put(BossEntities.PLANTERA_TENTACLE.get(), CreatureAttributeBuilder.boss().maxHealth(260).armor(20).attackDamage(15.6).followRange(64).knockbackResistance(1).build());
        event.put(BossEntities.LUNATIC_CULTIST.get(), CreatureAttributeBuilder.boss().maxHealth(700).armor(8).attackDamage(20).followRange(64).knockbackResistance(0.8).build());
        event.put(BossEntities.LUNATIC_CULTIST_CLONE.get(), CreatureAttributeBuilder.boss().maxHealth(1).armor(0).attackDamage(8).followRange(64).knockbackResistance(1).movementSpeed(0.3).flyingSpeed(0.6).build());
        event.put(BossEntities.PHANTASM_DRAGON.get(), CreatureAttributeBuilder.boss().maxHealth(150).armor(4).attackDamage(12).followRange(48).knockbackResistance(0.5).movementSpeed(0.3).flyingSpeed(0.6).build());
        event.put(BossEntities.HILL_OF_FLESH.get(), CreatureAttributeBuilder.boss().maxHealth(3824).armor(6).attackDamage(1).followRange(75).knockbackResistance(1).build());
        event.put(BossEntities.PRIME_ENDER_DRAGON.get(), CreatureAttributeBuilder.boss().maxHealth(4624).armor(20).attackDamage(32).followRange(300).knockbackResistance(1).movementSpeed(1).build());
    }

    private static void entityAttributeModification(EntityAttributeModificationEvent event) {
        Attribute armorPenetration = LibAttributes.getArmorPenetration().get();
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

        Attribute armorToughness = Attributes.ARMOR_TOUGHNESS;
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

    private static void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
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
        event.register(CritterEntities.JEWEL_BUNNY.get(), (type, bunny) -> type.getDescriptionId() + '.' + bunny.getBunnyVariant().getSerializedName());
        // todo 改成注册单独的史莱姆之母和宝宝
        event.register(MonsterEntities.BLACK_SLIME.get(), (type, slime) -> {
            if (slime.getSlimeSize() == 1) return "entity.confluence.baby_slime";
            if (slime.getSlimeSize() == 4) return "entity.confluence.mother_slime";
            return type.getDescriptionId();
        });
    }

    private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {
        event.register(MaterialItems.DEMONITE_INGOT, MaterialItems.CRIMTANE_INGOT);
        event.register(NatureBlocks.VILE_MUSHROOM, NatureBlocks.VICIOUS_MUSHROOM);
    }
}
