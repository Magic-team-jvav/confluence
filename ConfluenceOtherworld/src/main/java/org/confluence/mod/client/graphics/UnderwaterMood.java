package org.confluence.mod.client.graphics;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.util.OverworldUtils;

/// 水下的“水质”随生物群系变化（TerraGraphics 的水下效果用）：光和光晕的颜色、各色光被吸收的快慢、清澈度
/// （能看多远、焦散光纹多清楚），发光蘑菇的水里光纹自己会发光。
/// 在相机周围 5 个点取样，按权重混合；所有参数随时间平滑过渡（约一秒），游过群系边界不会突变。
final class UnderwaterMood {
    // 参数：0..2 光色，3..5 吸收（每格），6 清澈度，7..9 自发光颜色（已乘强度）
    private static final int COUNT = 10;
    private static final float[] current = new float[COUNT];
    private static final float[] target = new float[COUNT];
    private static long lastSample, lastFrame;
    private static boolean initialised;

    private UnderwaterMood() {}

    private static final float[] OCEAN = {0.55F, 0.85F, 1.0F, 0.08F, 0.03F, 0.022F, 1.0F, 0, 0, 0};
    private static final float[] WARM = {0.5F, 0.95F, 0.95F, 0.07F, 0.022F, 0.025F, 1.25F, 0, 0, 0};
    private static final float[] COLD = {0.7F, 0.88F, 1.0F, 0.07F, 0.03F, 0.02F, 1.1F, 0, 0, 0};
    private static final float[] SWAMP = {0.6F, 0.75F, 0.45F, 0.12F, 0.07F, 0.12F, 0.45F, 0, 0, 0};
    private static final float[] JUNGLE = {0.55F, 0.9F, 0.6F, 0.1F, 0.04F, 0.07F, 0.7F, 0, 0, 0};
    private static final float[] RIVER = {0.6F, 0.85F, 0.95F, 0.09F, 0.04F, 0.035F, 0.85F, 0, 0, 0};
    private static final float[] DESERT = {0.7F, 0.95F, 0.9F, 0.07F, 0.03F, 0.03F, 1.15F, 0, 0, 0};
    private static final float[] CORRUPTION = {0.55F, 0.4F, 0.85F, 0.09F, 0.11F, 0.05F, 0.55F, 0, 0, 0};
    private static final float[] CRIMSON = {0.9F, 0.4F, 0.4F, 0.04F, 0.12F, 0.11F, 0.55F, 0, 0, 0};
    private static final float[] HALLOW = {0.95F, 0.75F, 1.0F, 0.05F, 0.03F, 0.02F, 1.3F, 0, 0, 0};
    private static final float[] MUSHROOM = {0.45F, 0.65F, 1.0F, 0.09F, 0.05F, 0.02F, 0.9F, 0.08F, 0.16F, 0.4F};
    private static final float[] DEFAULT = {0.6F, 0.85F, 1.0F, 0.09F, 0.035F, 0.028F, 1.0F, 0, 0, 0};

    static float get(int index) {
        return current[index];
    }

    static void update(ClientLevel level, Camera camera, boolean noSky) {
        long now = System.nanoTime();
        if (!initialised || now - lastSample > 150_000_000L) {
            lastSample = now;
            computeTarget(level, camera.getBlockPosition(), noSky);
            if (!initialised) {
                System.arraycopy(target, 0, current, 0, COUNT);
                initialised = true;
                lastFrame = now;
                return;
            }
        }
        float dt = Math.min((now - lastFrame) / 1.0E9F, 0.25F);
        lastFrame = now;
        float k = 1.0F - (float) Math.exp(-dt / 0.9F);
        for (int i = 0; i < COUNT; i++) current[i] += (target[i] - current[i]) * k;
    }

    static void reset() {
        initialised = false;
    }

    private static void computeTarget(ClientLevel level, BlockPos center, boolean noSky) {
        java.util.Arrays.fill(target, 0.0F);
        float total = 0.0F;
        BlockPos.MutableBlockPos sample = new BlockPos.MutableBlockPos();
        int[][] offsets = {{0, 0}, {6, 0}, {-6, 0}, {0, 6}, {0, -6}};
        for (int[] offset : offsets) {
            float weight = offset[0] == 0 && offset[1] == 0 ? 2.0F : 1.0F;
            float[] mood = classify(level.getBiome(sample.set(center.getX() + offset[0], center.getY(), center.getZ() + offset[1])));
            for (int i = 0; i < COUNT; i++) target[i] += mood[i] * weight;
            total += weight;
        }
        for (int i = 0; i < COUNT; i++) target[i] /= total;
        // 看不到天的水（洞穴、地下湖）更浑
        if (noSky) {
            target[6] *= 0.7F;
            for (int i = 3; i < 6; i++) target[i] *= 1.25F;
        }
    }

    private static float[] classify(Holder<Biome> biome) {
        if (OverworldUtils.isCorruption(biome)) return CORRUPTION;
        if (OverworldUtils.isCrimson(biome)) return CRIMSON;
        if (OverworldUtils.isHallow(biome)) return HALLOW;
        if (OverworldUtils.isMushroom(biome)) return MUSHROOM;
        if (biome.is(Tags.Biomes.IS_SWAMP)) return SWAMP;
        if (OverworldUtils.isJungle(biome)) return JUNGLE;
        if (biome.is(Biomes.WARM_OCEAN) || biome.is(Biomes.LUKEWARM_OCEAN) || biome.is(Biomes.DEEP_LUKEWARM_OCEAN)) return WARM;
        if (OverworldUtils.isSnowy(biome) || biome.is(Biomes.FROZEN_OCEAN) || biome.is(Biomes.DEEP_FROZEN_OCEAN)
                || biome.is(Biomes.COLD_OCEAN) || biome.is(Biomes.DEEP_COLD_OCEAN) || biome.is(Biomes.FROZEN_RIVER)) return COLD;
        if (OverworldUtils.isDesert(biome)) return DESERT;
        if (biome.is(Tags.Biomes.IS_RIVER)) return RIVER;
        if (OverworldUtils.isOcean(biome)) return OCEAN;
        return DEFAULT;
    }
}
