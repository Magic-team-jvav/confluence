package org.confluence.mod.common.summoner.network;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.List;
import java.util.Objects;

public record SummonerBatchedParticlesPayload(List<Entry> entries) implements IPortPacket.S2C {

    public static final ResourceLocation ID = Confluence.asResource("summoner_batched_particles");

    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, SummonerBatchedParticlesPayload> STREAM_CODEC = PortStreamCodec.composite(
            Entry.STREAM_CODEC.apply(PortByteBufCodecs.list(4096)),
            SummonerBatchedParticlesPayload::entries,
            SummonerBatchedParticlesPayload::new
    );

    @Override
    public void work(Player player) {
        Level level = player.level();
        for (Entry entry : entries) {
            level.addParticle(entry.options(), false, entry.x(), entry.y(), entry.z(), entry.vx(), entry.vy(), entry.vz());
        }
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    public record Entry(ParticleOptions options, double x, double y, double z, double vx, double vy, double vz) {

        @SuppressWarnings("unchecked")
        public static final PortStreamCodec<PortRegistryFriendlyByteBuf, Entry> STREAM_CODEC = PortStreamCodec.ofMember(
                (entry, buffer) -> {
                    ParticleType<?> type = entry.options.getType();
                    buffer.writeResourceLocation(BuiltInRegistries.PARTICLE_TYPE.getKey(type));
                    entry.options.writeToNetwork(buffer);
                    buffer.writeDouble(entry.x);
                    buffer.writeDouble(entry.y);
                    buffer.writeDouble(entry.z);
                    buffer.writeDouble(entry.vx);
                    buffer.writeDouble(entry.vy);
                    buffer.writeDouble(entry.vz);
                },
                buffer -> {
                    ResourceLocation id = buffer.readResourceLocation();
                    ParticleType<?> type = Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.get(id), "Unknown particle type: " + id);
                    ParticleOptions options = ((ParticleType<ParticleOptions>) type).getDeserializer().fromNetwork((ParticleType<ParticleOptions>) type, buffer);
                    return new Entry(options, buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
                }
        );
    }
}
