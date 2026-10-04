package org.confluence.mod.common.summoner.particle;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.common.summoner.network.SummonerBatchedParticlesPayload;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;


/**
 * 服务端粒子累积器，在 Level tick 结束时统一发送本 tick 产生的粒子。
 */
public final class SummonerParticleData {

    private final List<SummonerBatchedParticlesPayload.Entry> entries = new ArrayList<>();

    /**
     * 将本 tick 累积的粒子打包发送给同维度玩家。
     */
    public static void tick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (!level.isClientSide()) {
            SummonerParticleData data = level.getData(SummonerAttachmentTypes.BATCHED_PARTICLES);
            if (!data.entries.isEmpty()) {
                List<SummonerBatchedParticlesPayload.Entry> snapshot = new ArrayList<>(data.entries);
                data.entries.clear();
                if (level instanceof ServerLevel serverLevel) {
                    PacketDistributor.sendToPlayersInDimension(serverLevel, new SummonerBatchedParticlesPayload(snapshot));
                }
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
