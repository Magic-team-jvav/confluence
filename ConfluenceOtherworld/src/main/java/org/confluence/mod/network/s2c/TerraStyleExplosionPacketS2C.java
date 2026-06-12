package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.SectionPos;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.TerraStyleExplosion;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record TerraStyleExplosionPacketS2C(double x, double y, double z,
                                           float radius) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("terra_style_explosion");
    public static final PortStreamCodec<ByteBuf, TerraStyleExplosionPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.DOUBLE, TerraStyleExplosionPacketS2C::x,
            PortByteBufCodecs.DOUBLE, TerraStyleExplosionPacketS2C::y,
            PortByteBufCodecs.DOUBLE, TerraStyleExplosionPacketS2C::z,
            PortByteBufCodecs.FLOAT, TerraStyleExplosionPacketS2C::radius,
            TerraStyleExplosionPacketS2C::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        TerraStyleExplosion.handleClientExplode(player.level(), x, y, z, radius);
    }

    public static void send2All(ServerLevel level, double x, double y, double z, float radius) {
        ChunkPos chunkPos = new ChunkPos(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        TerraStyleExplosionPacketS2C packet = new TerraStyleExplosionPacketS2C(x, y, z, radius);
        for (ServerPlayer player : level.players()) {
            if (player.getChunkTrackingView().contains(chunkPos)) {
                Confluence.NETWORK_HANDLER.sendToPlayer(packet);
            }
        }
    }
}
