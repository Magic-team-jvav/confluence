package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.ChunkPos;
import org.confluence.mod.network.s2c.DropletsSyncPacketS2C;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class DropletsHandler {
    private static @Nullable Map<ChunkPos, Map<BlockPos, ParticleOptions>> droplets = null;

    public static void handle(Minecraft minecraft, LocalPlayer player) {
        if (droplets == null || minecraft.isPaused()) return;
        ClientLevel level = player.clientLevel;
        for (Map.Entry<ChunkPos, Map<BlockPos, ParticleOptions>> entry : droplets.entrySet()) {
            for (Map.Entry<BlockPos, ParticleOptions> entry1 : entry.getValue().entrySet()) {
                if (level.random.nextInt(10) == 0) {
                    BlockPos blockpos = entry1.getKey();
                    level.trySpawnDripParticles(blockpos, level.getBlockState(blockpos), entry1.getValue(), true);
                }
            }
        }
    }

    public static void handlePacket(DropletsSyncPacketS2C packet) {
        droplets = packet.data();
    }

    public static void reset() {
        droplets = null;
    }
}
