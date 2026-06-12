package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.mixed.IClientLivingEntity;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record DeathMotionPacketS2C(int entityId, float x, float y,
                                   float z) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("death_motion");
    public static final PortStreamCodec<ByteBuf, DeathMotionPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VAR_INT, DeathMotionPacketS2C::entityId,
            PortByteBufCodecs.FLOAT, DeathMotionPacketS2C::x,
            PortByteBufCodecs.FLOAT, DeathMotionPacketS2C::y,
            PortByteBufCodecs.FLOAT, DeathMotionPacketS2C::z,
            DeathMotionPacketS2C::new
    );

    public DeathMotionPacketS2C(int entityId, Vec3 motion) {
        this(entityId, (float) motion.x, (float) motion.y, (float) motion.z);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        if (player.level().getEntity(entityId) instanceof IClientLivingEntity entity) {
            entity.confluence$deathMotion(new Vec3(x, y, z));
            if (entity instanceof LivingEntity living && living.isDeadOrDying()) {
                Confluence.LOGGER.warn("Receive death motion packet but entity is dying");
            }
        }
    }

    public static void sendToAll(LivingEntity living) {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            // 死前服务端把死亡时的速度发给客户端
            Vec3 motion = living.getDeltaMovement();
            if (motion.length() == 0) {
                Vec3 pos = living.position();
                motion = new Vec3(pos.x - living.xo, pos.y - living.yo, pos.z - living.zo);
            }
            Confluence.NETWORK_HANDLER.sendToAllPlayers(new DeathMotionPacketS2C(living.getId(), motion));
        }
    }

    public static void sendToClient(ServerPlayer serverPlayer, int entityId, Vec3 motion) {
        Confluence.NETWORK_HANDLER.sendToPlayer(serverPlayer, new DeathMotionPacketS2C(entityId, motion));
    }
}
