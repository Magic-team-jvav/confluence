package org.confluence.terra_curio.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.client.handler.PlayerJumpHandler;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;

public record InfiniteFlightPacketS2C(boolean enable) implements IPacketS2C {
    public static final Type<InfiniteFlightPacketS2C> TYPE = new Type<>(TerraCurio.asResource("infinite_flight"));
    public static final StreamCodec<ByteBuf, InfiniteFlightPacketS2C> STREAM_CODEC = ByteBufCodecs.BOOL.map(InfiniteFlightPacketS2C::new, InfiniteFlightPacketS2C::enable);

    @Override
    public Type<InfiniteFlightPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        PlayerJumpHandler.handleInfiniteFlight(enable);
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        boolean enable = TCUtils.hasType(serverPlayer, TCItems.INFINITE$FLIGHT);
        PacketDistributor.sendToPlayer(serverPlayer, new InfiniteFlightPacketS2C(enable));
    }
}
