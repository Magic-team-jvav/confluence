package org.confluence.mod.network.s2c;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;

import java.util.List;


public record SummonerBatchedParticlesS2C(List<Entry> entries) implements IPacketS2C {

    public static final Type<SummonerBatchedParticlesS2C> TYPE = Confluence.createType("summoner_batched_particles");

    public static final StreamCodec<RegistryFriendlyByteBuf, SummonerBatchedParticlesS2C> STREAM_CODEC = StreamCodec.composite(
            Entry.STREAM_CODEC.apply(ByteBufCodecs.list(4096)),
            SummonerBatchedParticlesS2C::entries,
            SummonerBatchedParticlesS2C::new
    );

    @Override
    public void work(Player player) {
        Level level = player.level();
        for (Entry entry : entries) {
            level.addParticle(entry.options(), false, entry.x(), entry.y(), entry.z(), entry.vx(), entry.vy(), entry.vz());
        }
    }

    @Override
    public Type<SummonerBatchedParticlesS2C> type() {
        return TYPE;
    }

    public record Entry(ParticleOptions options, double x, double y, double z, double vx, double vy, double vz) {

        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.ofMember(
                (entry, buffer) -> {
                    ParticleTypes.STREAM_CODEC.encode(buffer, entry.options);
                    buffer.writeDouble(entry.x);
                    buffer.writeDouble(entry.y);
                    buffer.writeDouble(entry.z);
                    buffer.writeDouble(entry.vx);
                    buffer.writeDouble(entry.vy);
                    buffer.writeDouble(entry.vz);
                },
                buffer -> new Entry(
                        ParticleTypes.STREAM_CODEC.decode(buffer),
                        buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                        buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
        );
    }
}
