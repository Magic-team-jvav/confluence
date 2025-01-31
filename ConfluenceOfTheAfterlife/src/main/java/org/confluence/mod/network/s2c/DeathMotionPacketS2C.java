package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.Confluence;
import org.confluence.mod.mixed.IEntity;
import org.jetbrains.annotations.NotNull;

public record DeathMotionPacketS2C(int entityId, Vec3 motion) implements CustomPacketPayload {
    public static final Type<DeathMotionPacketS2C> TYPE = new Type<>(Confluence.asResource("death_motion"));
    public static final StreamCodec<ByteBuf, DeathMotionPacketS2C> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, p -> p.entityId,
        ByteBufCodecs.fromCodec(Vec3.CODEC), p -> p.motion,
        DeathMotionPacketS2C::new
    );

    @Override
    public @NotNull Type<DeathMotionPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player().isLocalPlayer() && context.player().level().getEntity(entityId) instanceof IEntity entity){
                entity.confluence$deathMotion(motion);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToAll(int entityId, Vec3 motion) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(new DeathMotionPacketS2C(entityId, motion));
        }
    }

    public static void sendToClient(ServerPlayer serverPlayer, int entityId, Vec3 motion) {
        PacketDistributor.sendToPlayer(serverPlayer, new DeathMotionPacketS2C(entityId, motion));
    }
}
