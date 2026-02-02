package org.confluence.terraentity.mixin;

import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.confluence.terraentity.api.entity.IExtendedTracking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * ServerEntity 的 Mixin 类，用于提供扩展实体追踪功能。
 * 防止实现了 IExtendedTracking 接口的实体被过早取消追踪。
 */
@Mixin(ServerEntity.class)
public class ServerEntityMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger("TerraEntity-Server");

    @Shadow @Final private Entity entity;
    @Shadow @Final private Consumer<net.minecraft.network.protocol.Packet<?>> broadcast;

    /** 当前正在追踪此实体的玩家集合 */
    @Unique
    private final Set<ServerPlayer> terraentity$trackedPlayers = new HashSet<>();

    /** 防止递归调用的标志 */
    @Unique
    private boolean terraentity$isRemovingPairing = false;

    /**
     * 阻止扩展追踪实体的追踪被移除（除非满足特定条件）
     */
    @Inject(method = "removePairing", at = @At("HEAD"), cancellable = true)
    private void preventExtendedEntityUntrack(ServerPlayer player, CallbackInfo ci) {
        if (!(entity instanceof IExtendedTracking tracker)) {
            return;
        }

        // 防止无限递归
        if (terraentity$isRemovingPairing) {
            return;
        }

        // 检查是否应该允许停止追踪
        String untrackReason = terraentity$getUntrackReason(player, tracker);

        if (untrackReason == null) {
            // 在范围内，阻止停止追踪
            double distance = IExtendedTracking.calculateHorizontalDistance(entity, player);
            LOGGER.debug("阻止停止追踪 {} [ID:{}], 水平距离: {}, 玩家: {}",
                    entity.getClass().getSimpleName(),
                    entity.getId(),
                    String.format("%.2f", distance),
                    player.getName().getString());
            ci.cancel();
            return;
        }

        // 允许停止追踪
        LOGGER.warn("允许停止追踪 {} [ID:{}], 原因: {}, 玩家: {}",
                entity.getClass().getSimpleName(),
                entity.getId(),
                untrackReason,
                player.getName().getString());

        terraentity$sendRemovalPacket(player, untrackReason);
    }

    /**
     * 在 sendChanges 中检测并移除不同维度的玩家
     */
    @Inject(method = "sendChanges", at = @At("HEAD"))
    private void checkAndRemoveCrossDimensionPlayers(CallbackInfo ci) {
        if (!(entity instanceof IExtendedTracking) || !entity.isAlive() || entity.isRemoved()) {
            return;
        }

        Iterator<ServerPlayer> iterator = terraentity$trackedPlayers.iterator();
        while (iterator.hasNext()) {
            ServerPlayer player = iterator.next();

            if (terraentity$shouldRemovePlayer(player)) {
                iterator.remove();
                if (player != null && !player.isRemoved()) {
                    terraentity$sendRemovalPacket(player, "维度切换");
                }
            }
        }
    }

    /**
     * 防止重复添加同一玩家的追踪
     */
    @Inject(method = "addPairing", at = @At("HEAD"), cancellable = true)
    private void preventDuplicateTracking(ServerPlayer player, CallbackInfo ci) {
        if (!(entity instanceof IExtendedTracking)) {
            return;
        }

        if (!entity.isAlive() || entity.isRemoved()) {
            LOGGER.warn("实体 {} [ID:{}] 已死亡或被移除，跳过添加追踪",
                    entity.getClass().getSimpleName(),
                    entity.getId());
            ci.cancel();
            return;
        }

        if (terraentity$trackedPlayers.contains(player)) {
            LOGGER.info("阻止重复添加追踪 {} [ID:{}], 玩家: {}",
                    entity.getClass().getSimpleName(),
                    entity.getId(),
                    player.getName().getString());
            ci.cancel();
        }
    }

    /**
     * 记录成功添加玩家追踪
     */
    @Inject(method = "addPairing", at = @At("RETURN"))
    private void onAddPairingSuccess(ServerPlayer player, CallbackInfo ci) {
        if (entity instanceof IExtendedTracking && terraentity$trackedPlayers.add(player)) {
            LOGGER.info("添加玩家 {} 到 {} [ID:{}] 的追踪列表, 当前玩家数: {}",
                    player.getName().getString(),
                    entity.getClass().getSimpleName(),
                    entity.getId(),
                    terraentity$trackedPlayers.size());
        }
    }

    /**
     * 记录成功移除玩家追踪
     */
    @Inject(method = "removePairing", at = @At("RETURN"))
    private void onRemovePairingSuccess(ServerPlayer player, CallbackInfo ci) {
        if (entity instanceof IExtendedTracking && terraentity$trackedPlayers.remove(player)) {
            LOGGER.info("从 {} [ID:{}] 的追踪列表中移除玩家 {}, 剩余玩家数: {}",
                    entity.getClass().getSimpleName(),
                    entity.getId(),
                    player.getName().getString(),
                    terraentity$trackedPlayers.size());
        }
    }

    /**
     * 强制同步扩展追踪实体的位置和数据
     * 在实体死亡或玩家超出常规渲染距离时触发
     */
    @Inject(method = "sendChanges", at = @At("RETURN"))
    private void forceSyncExtendedEntity(CallbackInfo ci) {
        if (!(entity instanceof IExtendedTracking) || entity.isRemoved() || terraentity$trackedPlayers.isEmpty()) {
            return;
        }

        int viewDistance = entity.level().getServer().getPlayerList().getViewDistance();
        double renderDistance = viewDistance * 16.0;
        boolean isDying = !entity.isAlive();

        ClientboundTeleportEntityPacket teleportPacket = null;

        for (ServerPlayer player : terraentity$trackedPlayers) {
            if (player == null || player.isRemoved()) {
                continue;
            }

            double distance = IExtendedTracking.calculateHorizontalDistance(entity, player);

            // 如果实体正在死亡，或者玩家超出渲染距离，强制同步
            if (isDying || distance > renderDistance) {
                if (teleportPacket == null) {
                    teleportPacket = new ClientboundTeleportEntityPacket(entity);
                }
                player.connection.send(teleportPacket);
                terraentity$syncEntityData(player);
            }
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取停止追踪的原因
     * @return 停止追踪的原因，如果不应该停止追踪则返回 null
     */
    @Unique
    private String terraentity$getUntrackReason(ServerPlayer player, IExtendedTracking tracker) {
        if (!entity.isAlive()) {
            return "实体已死亡";
        }
        if (entity.isRemoved()) {
            return "实体已被移除";
        }
        if (!entity.level().dimension().equals(player.level().dimension())) {
            return "不在同一维度";
        }
        if (player.isRemoved()) {
            return "玩家已被移除";
        }

        // 使用接口提供的静态方法检查距离
        if (!IExtendedTracking.isPlayerInTrackingRange(entity, player)) {
            double distance = IExtendedTracking.calculateHorizontalDistance(entity, player);
            double range = tracker.getExtendedTrackingRange();
            return String.format("超出追踪范围 (距离: %.2f, 最大: %.2f)", distance, range);
        }

        return null; // 不应该停止追踪
    }

    /**
     * 检查是否应该从追踪列表中移除玩家
     */
    @Unique
    private boolean terraentity$shouldRemovePlayer(ServerPlayer player) {
        if (player == null || player.isRemoved()) {
            LOGGER.debug("移除无效玩家引用");
            return true;
        }

        if (!entity.level().dimension().equals(player.level().dimension())) {
            LOGGER.warn("检测到玩家 {} 在不同维度, 从追踪列表中移除",
                    player.getName().getString());
            return true;
        }

        return false;
    }

    /**
     * 向玩家发送实体移除数据包
     */
    @Unique
    private void terraentity$sendRemovalPacket(ServerPlayer player, String reason) {
        if (player.isRemoved()) {
            return;
        }

        try {
            terraentity$isRemovingPairing = true;
            ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entity.getId());
            player.connection.send(packet);
            LOGGER.info("发送移除包给玩家 {} (原因: {})",
                    player.getName().getString(), reason);
        } catch (Exception e) {
            LOGGER.error("发送移除包失败", e);
        } finally {
            terraentity$isRemovingPairing = false;
        }
    }

    /**
     * 同步实体数据到指定玩家
     */
    @Unique
    private void terraentity$syncEntityData(ServerPlayer player) {
        try {
            SynchedEntityData entityData = entity.getEntityData();
            List<SynchedEntityData.DataValue<?>> allValues = entityData.getNonDefaultValues();

            if (allValues != null && !allValues.isEmpty()) {
                ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(
                        entity.getId(),
                        allValues
                );
                player.connection.send(dataPacket);
            }
        } catch (Exception e) {
            LOGGER.error("同步实体数据失败", e);
        }
    }
}
