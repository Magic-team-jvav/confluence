package org.confluence.mod.common.summoner.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.summoner.network.SummonerBatchedParticlesPayload;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.mesdag.portlib.event.tick.PortLevelTickEvent;
import org.mesdag.portlib.network.PortPacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端粒子累积器，在 Level tick 结束时统一发送本 tick 产生的粒子。
 */
public final class SummonerParticleData {

    private final List<SummonerBatchedParticlesPayload.Entry> entries = new ArrayList<>();

    /**
     * 将本 tick 累积的粒子打包发送给同维度玩家。
     */
    public static void tick(PortLevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (!level.isClientSide()) {
            SummonerParticleData data = level.getData(SummonerAttachmentTypes.BATCHED_PARTICLES);
            if (!data.entries.isEmpty()) {
                List<SummonerBatchedParticlesPayload.Entry> snapshot = new ArrayList<>(data.entries);
                data.entries.clear();
                PortPacketDistributor.sendToPlayersInDimension(level.dimension(), new SummonerBatchedParticlesPayload(snapshot));
            }
        }
    }

    /**
     * 累积一条粒子记录。
     */
    public void add(ParticleOptions options, double x, double y, double z, double vx, double vy, double vz) {
        entries.add(new SummonerBatchedParticlesPayload.Entry(options, x, y, z, vx, vy, vz));
    }
}
