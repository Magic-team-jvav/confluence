package org.confluence.mod.network.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.MeteorLandingHandler;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record MeteoriteLocationPacketS2C(
        BlockPos location,
        int tickUntilLanding
) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("meteorite_location");
    public static final PortStreamCodec<FriendlyByteBuf, MeteoriteLocationPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.BLOCK_POS, MeteoriteLocationPacketS2C::location,
            PortByteBufCodecs.VAR_INT, MeteoriteLocationPacketS2C::tickUntilLanding,
            MeteoriteLocationPacketS2C::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void handle(IPortPacket.Context context) {
        Player player = context.player();
        if (player != null) context.enqueueWork(() -> work(player));
    }

    @Override
    public void work(Player player) {
        MeteorLandingHandler.handlePacket(this, player);
    }

    /// @param location         陨石坐标，等于[0, 0, 0]是无效的
    /// @param tickUntilLanding 落地时间，小于等于0将只刷新指南针
    public static void sendToAll(BlockPos location, int tickUntilLanding) {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null && !BlockPos.ZERO.equals(location)) {
            Confluence.NETWORK_HANDLER.sendToAllPlayers(new MeteoriteLocationPacketS2C(location, tickUntilLanding));
        }
    }

    public static void sendToClient(ServerPlayer player, BlockPos location, int tickUntilLanding) {
        if (BlockPos.ZERO.equals(location)) return;
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new MeteoriteLocationPacketS2C(location, tickUntilLanding));
    }
}
