package org.confluence.mod.client.handler;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public final class ParticleHandler {
    private static LocalPlayer player;
    private static ClientLevel level;
    private static Vec3 position;
    private static Holder<Biome> biome;

    public static void handle(LocalPlayer localPlayer) {
        player = localPlayer;
        level = player.clientLevel;
        long gameTime = level.getGameTime();
        position = player.position();
        biome = level.getBiome(player.blockPosition());

        if (gameTime % 20 == 0) {

        }
    }

    public static void addEmitter(double x, double y, double z, String path) {
        addEmitter(new Vec3(x, y, z), path);
    }

    public static void addEmitter(Vec3 pos, String path) {
        ParticleEmitter emitter = new ParticleEmitter(level, pos, Confluence.asResource(path));
        emitter.hideOutline = true;
        MolangParticleEngine.INSTANCE.addEmitter(emitter);
    }

    public static int getHeight(BlockPos.MutableBlockPos mutable, int bx, int bz) {
        mutable.setX(bx).setZ(bz);
        for (int y = Mth.floor(player.getY()) + 16; y > level.getMinBuildHeight(); y--) {
            if (level.getBlockState(mutable.setY(y)).canOcclude()) {
                return y;
            }
        }
        return level.getMinBuildHeight();
    }

    public static double[] getCirclePos(RandomSource random, double r) {
        double rot = random.nextDouble() * Math.TAU;
        return new double[]{
                Math.fma(Math.sin(rot), r, position.x),
                Math.fma(Math.cos(rot), r, position.z)
        };
    }

    public static boolean chance(RandomSource random, int denominator) {
        return random.nextInt(denominator) == 0;
    }

    public static boolean withinExclusive(double min, double value, double max) {
        return value > min && value < max;
    }

    public static boolean withinInclusive(double min, double value, double max) {
        return value >= min && value <= max;
    }

    public static int nextBetweenInclusive(RandomSource random, int min, int max) {
        return random.nextIntBetweenInclusive(min, max);
    }

    public static int nextScale(RandomSource random, int scale) {
        return random.nextIntBetweenInclusive(-scale, scale);
    }

    public static double nextBetweenInclusive(RandomSource random, double min, double max) {
        return random.nextDouble() * (max - min) + min;
    }

    public static double nextScale(RandomSource random, double scale) {
        return (random.nextDouble() - 0.5) * (scale + scale);
    }
}
