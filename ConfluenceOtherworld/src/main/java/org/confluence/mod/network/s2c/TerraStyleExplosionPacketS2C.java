package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.SectionPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.network.IPacket;
import org.confluence.mod.util.TerraStyleExplosion;

public record TerraStyleExplosionPacketS2C(double x, double y, double z, float radius) implements IPacketS2C {
    public static final Type<TerraStyleExplosionPacketS2C> TYPE = IPacket.createType("terra_style_explosion");
    public static final StreamCodec<ByteBuf, TerraStyleExplosionPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, TerraStyleExplosionPacketS2C::x,
            ByteBufCodecs.DOUBLE, TerraStyleExplosionPacketS2C::y,
            ByteBufCodecs.DOUBLE, TerraStyleExplosionPacketS2C::z,
            ByteBufCodecs.FLOAT, TerraStyleExplosionPacketS2C::radius,
            TerraStyleExplosionPacketS2C::new
    );

    @Override
    public Type<TerraStyleExplosionPacketS2C> type() {
        return TYPE;
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
                PacketDistributor.sendToPlayer(player, packet);
            }
        }
    }
}
