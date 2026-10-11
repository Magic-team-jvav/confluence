package org.confluence.mod.common.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
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
import org.confluence.mod.common.data.spawner.NPCSpawner;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModGunProperties;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.entity.CreatureSpawnPlacements;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.init.gun.GunSounds;
import org.confluence.mod.common.init.gun.GunTrailColors;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.common.init.item.DispenserRegistration;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.common.entity.TargetingIntegration;
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
        PortEventHandler.addListener(ModEvents::registerAccessoriesComponentUnitValueTypeLocalSync);
        PortEventHandler.addListener(PortEventPriority.LOW, ModEvents::buildCreativeModeTabContents);
        PortEventHandler.addListener(ModEvents::blockEntityTypeAddBlocks);
        PortEventHandler.addListener(ModEvents::registerBestiaryKeys);
        PortEventHandler.addListener(PortEventPriority.LOW, ModEvents::registerSpawnReplacements);
        PortEventHandler.addListener(ModEvents::registerEvilMaterialReplaces);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TargetingIntegration.init();
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

    private static void registerAccessoriesComponentUnitValueTypeLocalSync(RegisterAccessoriesComponentUnitValueTypeLocalSyncEvent event) {
        AccessoryItems.AFK_INDEX = event.register(AccessoryItems.$AFK);
    }

    private static void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == TCTabs.ACCESSORIES.get()) {
            WipNotDisplayOutput output = new WipNotDisplayOutput(event);
            /// 与泰拉饰品的饰品页签共用同一套 Wiki 分类（见 TCTabs），组名为 {@code itemGroup.terra_curio.<组名>}。
            // 生命和魔力
            TCTabs.acceptGroup(output, "health_and_mana",
                    AccessoryItems.NATURES_GIFT, AccessoryItems.MANA_FLOWER, AccessoryItems.CELESTIAL_MAGNET,
                    AccessoryItems.MAGNET_FLOWER, AccessoryItems.ARCANE_FLOWER, AccessoryItems.BAND_OF_STARPOWER,
                    AccessoryItems.MANA_REGENERATION_BAND, AccessoryItems.MAGIC_CUFFS, AccessoryItems.CELESTIAL_CUFFS,
                    AccessoryItems.MANA_CLOAK, AccessoryItems.PHILOSOPHERS_STONE, AccessoryItems.CHARM_OF_MYTHS);
            // 战斗
            TCTabs.acceptGroup(output, "combat",
                    AccessoryItems.CELESTIAL_EMBLEM, AccessoryItems.SUMMONER_EMBLEM, AccessoryItems.APPRENTICES_SCARF,
                    AccessoryItems.SQUIRES_SHIELD, AccessoryItems.HUNTRESSS_BUCKLER, AccessoryItems.MONKS_BELT,
                    AccessoryItems.HERCULES_BEETLE, AccessoryItems.NECROMANTIC_SCROLL, AccessoryItems.PAPYRUS_SCARAB,
                    AccessoryItems.PYGMY_NECKLACE);
            // 减益免疫
            TCTabs.acceptGroup(output, "debuff_immunity",
                    AccessoryItems.ADHESIVE_BANDAGE, AccessoryItems.MEDICATED_BANDAGE, AccessoryItems.POCKET_MIRROR,
                    AccessoryItems.REFLECTIVE_SHADES, AccessoryItems.ARMOR_POLISH, AccessoryItems.ARMOR_BRACING,
                    AccessoryItems.MEGAPHONE, AccessoryItems.NAZAR, AccessoryItems.COUNTERCURSE_MANTRA,
                    AccessoryItems.CHROMATIC_CLOAK);
            // 信息
            TCTabs.acceptGroup(output, "information", AccessoryItems.MECHANICAL_LENS);
            // 建筑
            TCTabs.acceptGroup(output, "construction",
                    AccessoryItems.PAINT_SPRAYER, AccessoryItems.GUIDE_TO_PLANT_FIBER_CORDAGE,
                    AccessoryItems.SPECTRE_GOGGLES);
            // 悠悠球
            TCTabs.acceptGroup(output, "yoyo",
                    AccessoryItems.WHITE_YOYO_STRING, AccessoryItems.RED_YOYO_STRING, AccessoryItems.ORANGE_YOYO_STRING,
                    AccessoryItems.YELLOW_YOYO_STRING, AccessoryItems.LIME_YOYO_STRING, AccessoryItems.GREEN_YOYO_STRING,
                    AccessoryItems.TEAL_YOYO_STRING, AccessoryItems.CYAN_YOYO_STRING, AccessoryItems.SKY_BLUE_YOYO_STRING,
                    AccessoryItems.BLUE_YOYO_STRING, AccessoryItems.PURPLE_YOYO_STRING, AccessoryItems.VIOLET_YOYO_STRING,
                    AccessoryItems.PINK_YOYO_STRING, AccessoryItems.BROWN_YOYO_STRING, AccessoryItems.RAINBOW_YOYO_STRING,
                    AccessoryItems.BLACK_YOYO_STRING, AccessoryItems.YOYO_GLOVE, AccessoryItems.BLACK_COUNTERWEIGHT,
                    AccessoryItems.BLUE_COUNTERWEIGHT, AccessoryItems.GREEN_COUNTERWEIGHT,
                    AccessoryItems.PURPLE_COUNTERWEIGHT, AccessoryItems.RED_COUNTERWEIGHT,
                    AccessoryItems.YELLOW_COUNTERWEIGHT, AccessoryItems.STRUNG_COUNTERWEIGHT, AccessoryItems.YOYO_BAG,
                    AccessoryItems.MAGIC_STRING, AccessoryItems.MAGIC_YOYO_BAG);
            // 其他
            TCTabs.acceptGroup(output, "other",
                    TCItems.EVERLASTING, TCItems.BASE_POINT,
                    AccessoryItems.HIGH_TEST_FISHING_LINE, AccessoryItems.TACKLE_BOX, AccessoryItems.ANGLER_TACKLE_BAG,
                    AccessoryItems.LAVAPROOF_FISHING_HOOK, AccessoryItems.LAVAPROOF_TACKLE_BAG,
                    AccessoryItems.FISHING_BOBBER, AccessoryItems.GLOWING_FISHING_BOBBER,
                    AccessoryItems.LAVA_MOSS_FISHING_BOBBER, AccessoryItems.HELIUM_MOSS_FISHING_BOBBER,
                    AccessoryItems.NEON_MOSS_FISHING_BOBBER, AccessoryItems.ARGON_MOSS_FISHING_BOBBER,
                    AccessoryItems.KRYPTON_MOSS_FISHING_BOBBER, AccessoryItems.XENON_MOSS_FISHING_BOBBER,
                    AccessoryItems.LUCKY_COIN, AccessoryItems.GOLD_RING, AccessoryItems.COIN_RING,
                    AccessoryItems.DISCOUNT_CARD, AccessoryItems.GREEDY_RING, AccessoryItems.RADIO_THING,
                    AccessoryItems.STRESS_BALL, AccessoryItems.CLOTHIER_VOODOO_DOLL, AccessoryItems.GUIDE_VOODOO_DOLL,
                    AccessoryItems.EXPERIENCE_PIPETTE, AccessoryItems.TINSEL, AccessoryItems.SHINY_PIPETTE);
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
        event.register(MonsterEntities.DEMON_EYE.get(), (type, eye) -> type.getDescriptionId() + '.' + eye.getVariant().getSerializedName());
        /// 原版僵尸通过实际穿戴的防寒服或雨衣识别已有图鉴条目。
        event.register(EntityType.ZOMBIE, (type, zombie) -> {
            String key = type.getDescriptionId();
            Item chest = zombie.getItemBySlot(EquipmentSlot.CHEST).getItem();
            if (chest == ArmorItems.RAINCOAT.get()) return key + ".raincoat";
            if (chest == ArmorItems.SNOW_SUITS.get()) return key + ".frozen";
            if (chest == ArmorItems.PINK_SNOW_SUITS.get()) return key + ".frozen.pink";
            return key;
        });
        event.register(EntityType.SKELETON, (type, skeleton) -> {
            if (skeleton.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.MINING_CHESTPLATE.get())) {
                return "entity.confluence.undead_miner";
            }
            return type.getDescriptionId();
        });
    }

    private static void registerEvilMaterialReplaces(RegisterEvilMaterialReplacesEvent event) {
        event.register(MaterialItems.DEMONITE_INGOT, MaterialItems.CRIMTANE_INGOT);
        event.register(NatureBlocks.VILE_MUSHROOM, NatureBlocks.VICIOUS_MUSHROOM);
    }

//    private static void registerConfigurationTasks (PortRegisterConfigurationTasksEvent event){
//        event.register(new AchievementsTask(event.getListener()));
//    }
}
