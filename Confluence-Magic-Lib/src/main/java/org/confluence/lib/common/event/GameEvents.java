package org.confluence.lib.common.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.StartupConfig;
import org.confluence.lib.common.data.IdFixer;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.common.item.IFunctionCouldEnable;
import org.confluence.lib.event.SwitchItemFunctionEvent;
import org.confluence.lib.mixed.IExtraSyncedData;
import org.confluence.lib.network.AttackDamagePacketS2C;
import org.confluence.lib.network.SetEntityDataPacketS2C;
import org.confluence.lib.util.DelayTaskHolder;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.NaturalSpawnerUtil;

@EventBusSubscriber(modid = ConfluenceMagicLib.LIB_ID)
public final class GameEvents {
    @SubscribeEvent
    public static void entityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER);
        event.add(EntityType.PLAYER, ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER);
    }

    @SubscribeEvent
    public static void spawnClusterSize(SpawnClusterSizeEvent event) {
        Mob mob = event.getEntity();
        NaturalSpawnerUtil.ChunkSpawnData data = NaturalSpawnerUtil.getChunkSpawnData(mob.level().dimension(), mob.chunkPosition());
        if (data != NaturalSpawnerUtil.ChunkSpawnData.DEFAULT) {
            event.setSize(data.getCount(event.getSize()));
        }
    }

    @SubscribeEvent
    public static void livingDrops(LivingDropsEvent event) {
        if (event.getEntity().getTags().contains(LibUtils.NO_DROPS_TAG)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void livingHeal(LivingHealEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getEntity().getActiveEffects().stream().anyMatch(instance -> instance.getCures().contains(LibUtils.DENY_HEAL))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void player$StartTracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && event.getTarget() instanceof IExtraSyncedData<?> extraSyncedData) {
            PacketDistributor.sendToPlayer(serverPlayer, new SetEntityDataPacketS2C(
                    extraSyncedData.confluence$self().getId(),
                    extraSyncedData.confluence$getAllEntries()
            ));
        }
    }

    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent event) {
        NaturalSpawnerUtil.init(event.getServer());
    }

    @SubscribeEvent
    public static void serverTick$Post(ServerTickEvent.Post event) {
        NaturalSpawnerUtil.update(event.getServer());
    }

    @SubscribeEvent
    public static void serverStopped(ServerStoppedEvent event) {
        NaturalSpawnerUtil.clear();
        IGlobalData.clearAll();
    }

    @SubscribeEvent
    public static void player$PlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        IdFixer.fixPersistentData(event.getEntity());
        StartupConfig.checkIfSomeoneHasViolatedEULA(event.getEntity());
    }

    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        DelayTaskHolder delayTaskHolder = livingEntity.getExistingDataOrNull(ConfluenceMagicLib.DELAY_TASK_HOLDER);
        if (delayTaskHolder != null) {
            livingEntity.removeData(ConfluenceMagicLib.DELAY_TASK_HOLDER);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public static void itemStackedOnOther(ItemStackedOnOtherEvent event) {
        if (event.getClickAction() != ClickAction.SECONDARY) return;
        ItemStack carried = event.getCarriedItem();
        ItemStack onSlot = event.getStackedOnItem();
        // 需要注意创造模式物品栏是仅客户端的，所以创造模式无法正常使用
        if (carried.isEmpty() && onSlot.getItem() instanceof IFunctionCouldEnable couldEnable) {
            Player player = event.getPlayer();
            if (!NeoForge.EVENT_BUS.post(new SwitchItemFunctionEvent.Pre(player, onSlot)).isCanceled()) {
                couldEnable.cycleEnable(onSlot);
                NeoForge.EVENT_BUS.post(new SwitchItemFunctionEvent.Post(player, onSlot, couldEnable.isEnabled(onSlot)));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void entityTick$Pre(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.isAlive()) {
                DelayTaskHolder delayTaskHolder = livingEntity.getExistingDataOrNull(ConfluenceMagicLib.DELAY_TASK_HOLDER);
                if (delayTaskHolder != null) {
                    delayTaskHolder.tick();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void livingEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity livingEntity = event.getEntity();
        EquipmentSlot slot = event.getSlot();
        if (livingEntity.isAlive()) {
            DelayTaskHolder delayTaskHolder = livingEntity.getExistingDataOrNull(ConfluenceMagicLib.DELAY_TASK_HOLDER);
            if (delayTaskHolder != null && !event.getFrom().getItem().shouldCauseBlockBreakReset(event.getFrom(), event.getTo())) {
                delayTaskHolder.removeTask(slot);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void livingSwapItems$Hands(LivingSwapItemsEvent.Hands event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity.isAlive()) {
            DelayTaskHolder delayTaskHolder = livingEntity.getExistingDataOrNull(ConfluenceMagicLib.DELAY_TASK_HOLDER);
            if (delayTaskHolder != null) {
                ItemStack itemSwappedToMainHand = event.getItemSwappedToMainHand();
                ItemStack itemSwappedToOffHand = event.getItemSwappedToOffHand();
                if (!itemSwappedToMainHand.getItem().shouldCauseBlockBreakReset(itemSwappedToMainHand, itemSwappedToOffHand)) {
                    delayTaskHolder.removeTask(InteractionHand.MAIN_HAND);
                }
                if (!itemSwappedToOffHand.getItem().shouldCauseBlockBreakReset(itemSwappedToOffHand, itemSwappedToMainHand)) {
                    delayTaskHolder.removeTask(InteractionHand.OFF_HAND);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void livingDamage$Post(LivingDamageEvent.Post event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            AttackDamagePacketS2C.sendToClient(player, event.getNewDamage());
        }
    }
}
