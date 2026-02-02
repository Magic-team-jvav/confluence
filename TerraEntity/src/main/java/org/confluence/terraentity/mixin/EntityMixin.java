package org.confluence.terraentity.mixin;

import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.confluence.terraentity.api.entity.IExtendedTracking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Entity 的 Mixin 类，用于扩展实体的追踪和移除行为。
 * 主要功能：
 * 1. 强制扩展追踪实体始终保持 tick 状态
 * 2. 在实体被移除时立即向所有玩家广播移除包
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger("TerraEntity-Entity");

    @Shadow
    public abstract int getId();

    @Shadow
    public abstract net.minecraft.world.level.Level level();

    /** 标记是否已发送移除包，防止重复发送 */
    @Unique
    private boolean terraentity$removalPacketSent = false;

    /**
     * 让扩展追踪实体始终保持 tick 状态
     * 这确保了即使玩家远离，实体仍然会被更新
     */
    @Inject(method = "isAlwaysTicking", at = @At("HEAD"), cancellable = true)
    private void forceAlwaysTicking(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;

        if (entity instanceof IExtendedTracking) {
            cir.setReturnValue(true);
        }
    }

    /**
     * 在实体被标记为移除时立即发送移除包
     * 确保客户端能及时收到实体移除的通知
     */
    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void onSetRemoved(Entity.RemovalReason reason, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;

        // 只处理扩展追踪实体
        if (!(entity instanceof IExtendedTracking)) {
            return;
        }

        // 只处理服务端
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        // 防止重复发送
        if (terraentity$removalPacketSent) {
            LOGGER.debug("实体 {} [ID:{}] 的移除包已发送，跳过",
                    entity.getClass().getSimpleName(),
                    entity.getId());
            return;
        }

        terraentity$removalPacketSent = true;

        LOGGER.warn("实体 {} [ID:{}] 正在被移除 (原因: {}), 准备广播移除包",
                entity.getClass().getSimpleName(),
                entity.getId(),
                reason);

        terraentity$broadcastRemovalPacket(entity, serverLevel, reason);
    }


    /**
     * 向所有在线玩家广播实体移除包
     *
     * @param entity 被移除的实体
     * @param serverLevel 服务端世界
     * @param reason 移除原因
     */
    @Unique
    private void terraentity$broadcastRemovalPacket(Entity entity, ServerLevel serverLevel, Entity.RemovalReason reason) {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entity.getId());
        List<ServerPlayer> players = serverLevel.getServer().getPlayerList().getPlayers();

        if (players.isEmpty()) {
            LOGGER.debug("没有在线玩家，跳过发送移除包");
            return;
        }

        int sentCount = 0;
        int failedCount = 0;

        for (ServerPlayer player : players) {
            if (terraentity$sendRemovalPacketToPlayer(player, packet)) {
                sentCount++;
            } else {
                failedCount++;
            }
        }

        LOGGER.info("移除包广播完成: 成功 {} 个, 失败 {} 个, 总计 {} 个玩家",
                sentCount, failedCount, players.size());
    }

    /**
     * 向单个玩家发送实体移除包
     *
     * @param player 目标玩家
     * @param packet 移除包
     * @return 是否发送成功
     */
    @Unique
    private boolean terraentity$sendRemovalPacketToPlayer(ServerPlayer player,
                                                          ClientboundRemoveEntitiesPacket packet) {
        // 检查玩家有效性
        if (!terraentity$isPlayerValid(player)) {
            return false;
        }

        try {
            player.connection.send(packet);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查玩家是否有效且可以接收数据包
     *
     * @param player 要检查的玩家
     * @return 玩家是否有效
     */
    @Unique
    private boolean terraentity$isPlayerValid(ServerPlayer player) {
        if (player == null) {
            return false;
        }

        if (player.isRemoved()) {
            return false;
        }

        return player.connection.getConnection().isConnected();
    }

}
