package org.confluence.mod.network.s2c;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.Confluence;
import org.confluence.mod.mixed.IEntity;
import org.slf4j.Logger;

public record DeathMotionPacketS2C(int entityId, float x,float y, float z) implements CustomPacketPayload {
    public static final Type<DeathMotionPacketS2C> TYPE = new Type<>(Confluence.asResource("death_motion"));
    public static final StreamCodec<ByteBuf, DeathMotionPacketS2C> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, p -> p.entityId,
        ByteBufCodecs.FLOAT, p -> p.x,
        ByteBufCodecs.FLOAT, p -> p.y,
        ByteBufCodecs.FLOAT, p -> p.z,
        DeathMotionPacketS2C::new
    );
    private static final Logger LOGGER = LogUtils.getLogger();

    public DeathMotionPacketS2C(int entityId, Vec3 motion){
        this(entityId, (float) motion.x, (float) motion.y, (float) motion.z);
    }

    @Override
    public Type<DeathMotionPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player().isLocalPlayer() && context.player().level().getEntity(entityId) instanceof IEntity entity){
                entity.confluence$deathMotion(new Vec3(x, y, z));
                if(entity instanceof LivingEntity living && living.isDeadOrDying()){
                    LOGGER.warn("Receive death motion packet but entity is dying");
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToAll(LivingEntity living) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            // 死前服务端把死亡时的速度发给客户端
            Vec3 motion = living.getDeltaMovement();
            if (motion.length() == 0) {
                Vec3 pos = living.position();
                motion = new Vec3(pos.x - living.xo, pos.y - living.yo, pos.z - living.zo);
            }
            PacketDistributor.sendToAllPlayers(new DeathMotionPacketS2C(living.getId(), motion));
        }
    }

    public static void sendToClient(ServerPlayer serverPlayer, int entityId, Vec3 motion) {
        PacketDistributor.sendToPlayer(serverPlayer, new DeathMotionPacketS2C(entityId, motion));
    }
}
