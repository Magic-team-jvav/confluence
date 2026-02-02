package org.confluence.terra_curio.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.client.handler.GravitationHandler;

public record BroadcastGravitationRotPacketS2C(int entityId, boolean enabled) implements IPacketS2C {
    public static final Type<BroadcastGravitationRotPacketS2C> TYPE = new Type<>(TerraCurio.asResource("broadcast_gravitation_rot"));
    public static final StreamCodec<ByteBuf, BroadcastGravitationRotPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, BroadcastGravitationRotPacketS2C::entityId,
            ByteBufCodecs.BOOL, BroadcastGravitationRotPacketS2C::enabled,
            BroadcastGravitationRotPacketS2C::new
    );

    @Override
    public Type<BroadcastGravitationRotPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        GravitationHandler.handleRemoteRot(this, player);
    }
}
