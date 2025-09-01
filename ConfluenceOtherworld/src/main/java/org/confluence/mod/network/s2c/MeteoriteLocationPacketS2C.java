package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.client.handler.MeteorLandingHandler;
import org.confluence.mod.network.IPacket;

public record MeteoriteLocationPacketS2C(BlockPos location, int tickUntilLanding) implements IPacketS2C {
    public static final Type<MeteoriteLocationPacketS2C> TYPE = IPacket.createType("meteorite_location");
    public static final StreamCodec<ByteBuf, MeteoriteLocationPacketS2C> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, MeteoriteLocationPacketS2C::location,
            ByteBufCodecs.VAR_INT, MeteoriteLocationPacketS2C::tickUntilLanding,
            MeteoriteLocationPacketS2C::new
    );

    @Override
    public Type<MeteoriteLocationPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        MeteorLandingHandler.handlePacket(this, player);
    }

    /**
     * @param location         陨石坐标，等于[0, 0, 0]是无效的
     * @param tickUntilLanding 落地时间，小于等于0将只刷新指南针
     */
    public static void sendToAll(BlockPos location, int tickUntilLanding) {
        if (ServerLifecycleHooks.getCurrentServer() != null && !BlockPos.ZERO.equals(location)) {
            PacketDistributor.sendToAllPlayers(new MeteoriteLocationPacketS2C(location, tickUntilLanding));
        }
    }
}
