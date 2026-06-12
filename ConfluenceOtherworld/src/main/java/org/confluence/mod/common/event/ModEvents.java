package org.confluence.mod.common.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.forgespi.locating.IModFile;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.api.event.NameFixRegisterEvent;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.util.ConfluenceResources;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.WipNotDisplayOutput;
import org.confluence.mod.Confluence;
import org.confluence.mod.StartupConfigs;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.block.natural.MagicMailBox;
import org.confluence.mod.common.data.saved.*;
import org.confluence.mod.common.entity.InverseEnderMan;
import org.confluence.mod.common.entity.RainbowSheep;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.gun.GunSounds;
import org.confluence.mod.common.init.gun.GunTrailColors;
import org.confluence.mod.common.init.item.DispenserRegistration;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.integration.terra_entity.TEHelper;
import org.confluence.mod.integration.terra_entity.TEItemComponentModify;
import org.confluence.mod.network.task.AchievementsTask;
import org.confluence.mod.util.ModUtils;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.entity.PortEntityAttributeCreationEvent;
import org.mesdag.portlib.event.entity.living.PortLivingEquipmentChangeEvent;
import org.mesdag.portlib.event.entity.living.PortLivingIncomingDamageEvent;
import org.mesdag.portlib.event.lifecycle.PortFMLCommonSetupEvent;
import org.mesdag.portlib.event.lifecycle.PortFMLLoadCompleteEvent;
import org.mesdag.portlib.event.other.PortAddPackFindersEvent;
import org.mesdag.portlib.event.other.PortBlockEntityTypeAddBlocksEvent;
import org.mesdag.portlib.event.other.PortModifyDefaultComponentsEvent;

import java.util.Optional;

import static org.confluence.mod.Confluence.MODID;

public final class ModEvents {
    public static void init() {
        PortEventHandler.addListener(ModEvents::commonSetup);
        PortEventHandler.addListener(ModEvents::loadComplete);
        PortEventHandler.addListener(ModEvents::addPackFinders);
        PortEventHandler.addListener(ModEvents::entityAttributeCreation);
        PortEventHandler.addListener(ModEvents::blockEntityTypeAddBlocks);
        PortEventHandler.addListener(ModEvents::modifyDefaultComponents);
        PortEventHandler.addListener(ModEvents::registerConfigurationTasks);
        PortEventHandler.addListener(ModEvents::livingEquipmentChange);
        PortEventHandler.addListener(ModEvents::livingIncomingDamage);
        PortEventHandler.addListener(ModEvents::nameFixRegister);
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

            GunSounds.init();
            GunTrailColors.init();
        });
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
            );
            GlobalCloakData.INSTANCE.initialize();
            DispenserRegistration.boostrap();
        });
    }

    private static void addPackFinders(PortAddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            IModFile modFile = net.minecraftforge.fml.ModList.get().getModFileById(MODID).getFile();
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

    private static void entityAttributeCreation(PortEntityAttributeCreationEvent event) {
        event.put(ModEntities.BESTIARY_ENTRY_DISPLAY.get(), LivingEntity.createLivingAttributes().build());
        event.put(ModEntities.RAINBOW_SHEEP.get(), RainbowSheep.createAttributes().build());
        event.put(ModEntities.INVERSE_ENDERMAN.get(), InverseEnderMan.createAttributes().build());
    }

    private static void blockEntityTypeAddBlocks(PortBlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.BRUSHABLE_BLOCK, OreBlocks.OPAL_ORE.get());
        event.modify(BlockEntityType.SIGN, LogBlockSet.getSignBlocks());
        event.modify(BlockEntityType.HANGING_SIGN, LogBlockSet.getHangingSignBlocks());
        event.modify(BlockEntityType.SCULK_SENSOR, FunctionalBlocks.SCULK_TRAP.get());
        event.modify(BlockEntityType.CAMPFIRE, FunctionalBlocks.LIFE_CAMPFIRE.get());
    }

    private static void modifyDefaultComponents(PortModifyDefaultComponentsEvent event) {
        TEItemComponentModify.modifyDefaultComponents(event);
        event.modify(Items.SNOWBALL, builder -> builder.set(DataComponents.MAX_STACK_SIZE, LibUtils.MAX_STACK_SIZE));
    }

    private static void registerConfigurationTasks(RegisterConfigurationTasksEvent event) {
        event.register(new AchievementsTask(event.getListener()));
    }

    // region 枪械事件（原GunEvents）

    private static void livingEquipmentChange(PortLivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getSlot() == EquipmentSlot.MAINHAND && event.getTo().getItem() instanceof BaseGun baseGun) {
                baseGun.pickAnimator(event.getTo(), player);
            }
        }
    }

    private static void livingIncomingDamage(PortLivingIncomingDamageEvent event) {
        if (event.getSource().is(LibDamageTypes.BULLET_DAMAGE)) {
            if (ConfluenceMagicLib.IS_CONFLUENCE_LOAD) return;
            event.setInvulnerabilityTicks(0);
        }
    }

    private static void nameFixRegister(NameFixRegisterEvent.Item event) {
        event.register("terra_guns:blowpipe", "confluence:blowgun");
    }
}
