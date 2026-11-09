package org.confluence.mod.client.graphics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.util.OverworldUtils;

/// 环境粒子（Confluence 画质）：玩家周围按所在位置的生物群系、昼夜和光照随机生成一些小光点，
/// 用原版粒子图集里的 glow 贴图上色，不需要新贴图：
/// 森林/丛林夜里的萤火虫（一闪一闪）、丛林白天的花粉、发光蘑菇的蓝色孢子（上升）、地狱的火星（上升）、
/// 神圣之地的彩色闪光、腐化之地的暗紫碎屑、猩红之地的血滴、洞穴里火光附近的灰尘。
/// 每次都按“生成位置”判断生物群系，所以在边界上自然混合。数量受设置和原版“粒子”选项影响。
public final class TerraAmbientParticles {
    private static final int MAX_ACTIVE = 320;
    private static int active;
    private static int ticks;
    private static ClientLevel lastLevel;
    /// 原版粒子图集里的 glow 贴图（不能叫 sprite：Mote 继承的 TextureSheetParticle 有同名字段，会把它遮住）
    private static TextureAtlasSprite glowSprite;

    private TerraAmbientParticles() {}

    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        LocalPlayer player = minecraft.player;
        if (level == null || player == null || minecraft.isPaused() || !ClientConfigs.enhancedGraphics) return;
        // 换世界/维度时粒子引擎直接清空，不会回调 remove：此时把计数归零。
        // 另外极少数被引擎挤掉的粒子也不回调，所以每 1200 tick（比最长寿命 ~10 秒长得多）兜底归零一次
        if (level != lastLevel) {
            lastLevel = level;
            active = 0;
        }
        if (++ticks % 1200 == 0) active = 0;
        float density = ClientConfigs.ambientParticles / 100.0F;
        ParticleStatus status = minecraft.options.particles().get();
        if (status == ParticleStatus.MINIMAL) return;
        if (status == ParticleStatus.DECREASED) density *= 0.5F;
        if (density <= 0.0F || active >= MAX_ACTIVE) return;
        RandomSource random = level.getRandom();
        int attempts = Math.round(density * 8.0F);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < attempts && active < MAX_ACTIVE; i++) {
            double x = player.getX() + (random.nextDouble() - 0.5) * 32.0;
            double y = player.getEyeY() + (random.nextDouble() - 0.4) * 14.0;
            double z = player.getZ() + (random.nextDouble() - 0.5) * 32.0;
            pos.set(x, y, z);
            if (!level.isLoaded(pos) || !level.isEmptyBlock(pos) || !level.getFluidState(pos).isEmpty()) continue;
            trySpawn(level, random, pos, x, y, z);
        }
    }

    private static void trySpawn(ClientLevel level, RandomSource random, BlockPos pos, double x, double y, double z) {
        Holder<Biome> biome = level.getBiome(pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        // 不能用 level.isNight()：客户端的 skyDarken 只在进入世界时算一次，之后一直停在那一刻的昼夜
        boolean night = Mth.cos(level.getTimeOfDay(1.0F) * Mth.TWO_PI) < 0.0F;
        boolean raining = level.isRainingAt(pos);
        if (level.dimension() == Level.NETHER) {
            // 地狱：火星，慢慢往上飘
            if (random.nextFloat() < 0.6F) add(level, x, y, z, rand(random, -0.004, 0.004), rand(random, 0.008, 0.025), rand(random, -0.004, 0.004),
                    pick(random, 0xFF8A2A, 0xFFB040, 0xFF5A1A), rand(random, 0.05, 0.09), 60 + random.nextInt(60), true, 0.95F, 0.0F);
            return;
        }
        if (OverworldUtils.isMushroom(biome)) {
            // 发光蘑菇：蓝色孢子，缓缓上升
            if (random.nextFloat() < 0.7F) add(level, x, y, z, rand(random, -0.003, 0.003), rand(random, 0.002, 0.008), rand(random, -0.003, 0.003),
                    pick(random, 0x4E9CFF, 0x6AB8FF, 0x3A7AE0), rand(random, 0.05, 0.1), 100 + random.nextInt(80), true, 0.85F, 0.0F);
            return;
        }
        if (OverworldUtils.isHallow(biome)) {
            // 神圣之地：彩色闪光（快闪）
            if (random.nextFloat() < 0.5F) add(level, x, y, z, rand(random, -0.005, 0.005), rand(random, -0.002, 0.004), rand(random, -0.005, 0.005),
                    pick(random, 0xFF9AD8, 0x9AE8FF, 0xFFF29A, 0xC8A8FF), rand(random, 0.04, 0.07), 40 + random.nextInt(40), true, 0.9F, 0.45F);
            return;
        }
        if (OverworldUtils.isCorruption(biome)) {
            // 腐化之地：暗紫碎屑，慢慢落下
            if (random.nextFloat() < 0.5F) add(level, x, y, z, rand(random, -0.004, 0.004), rand(random, -0.006, -0.001), rand(random, -0.004, 0.004),
                    pick(random, 0x5A3A8A, 0x45306E, 0x6E4A9E), rand(random, 0.06, 0.1), 80 + random.nextInt(60), false, 0.75F, 0.0F);
            return;
        }
        if (OverworldUtils.isCrimson(biome)) {
            // 猩红之地：血滴，往下掉
            if (random.nextFloat() < 0.45F) add(level, x, y, z, rand(random, -0.002, 0.002), rand(random, -0.02, -0.008), rand(random, -0.002, 0.002),
                    pick(random, 0xA0222A, 0x8A1820, 0xC03038), rand(random, 0.05, 0.08), 50 + random.nextInt(40), false, 0.85F, 0.0F);
            return;
        }
        boolean outdoors = skyLight >= 10;
        if (outdoors && !raining && OverworldUtils.isJungle(biome)) {
            if (night) {
                // 丛林夜里：萤火虫
                if (random.nextFloat() < 0.6F) firefly(level, random, x, y, z);
            } else if (random.nextFloat() < 0.35F) {
                // 丛林白天：花粉
                add(level, x, y, z, rand(random, -0.006, 0.006), rand(random, -0.002, 0.003), rand(random, -0.006, 0.006),
                        pick(random, 0xF2E6A0, 0xE8F0B0, 0xFFF4C8), rand(random, 0.03, 0.05), 100 + random.nextInt(80), false, 0.6F, 0.0F);
            }
            return;
        }
        if (outdoors && !raining && night && isTemperateLand(biome)) {
            // 森林、平原等地表夜里：零星萤火虫
            if (random.nextFloat() < 0.25F) firefly(level, random, x, y, z);
            return;
        }
        if (skyLight < 4 && blockLight >= 7) {
            // 洞穴里火光附近：灰尘
            if (random.nextFloat() < 0.5F) add(level, x, y, z, rand(random, -0.002, 0.002), rand(random, -0.001, 0.002), rand(random, -0.002, 0.002),
                    0xFFE8C0, rand(random, 0.025, 0.045), 80 + random.nextInt(60), false, 0.35F, 0.0F);
        }
    }

    private static boolean isTemperateLand(Holder<Biome> biome) {
        if (OverworldUtils.isDesert(biome) || OverworldUtils.isSnowy(biome) || OverworldUtils.isOcean(biome)) return false;
        float temperature = biome.value().getBaseTemperature();
        return temperature > 0.3F && temperature < 1.5F;
    }

    private static void firefly(ClientLevel level, RandomSource random, double x, double y, double z) {
        add(level, x, y, z, rand(random, -0.01, 0.01), rand(random, -0.004, 0.006), rand(random, -0.01, 0.01),
                pick(random, 0xC8FF5A, 0xE6FF7A, 0xA8F040), rand(random, 0.06, 0.09), 100 + random.nextInt(100), true, 1.0F, 0.12F);
    }

    private static double rand(RandomSource random, double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    private static int pick(RandomSource random, int... colors) {
        return colors[random.nextInt(colors.length)];
    }

    private static void add(ClientLevel level, double x, double y, double z, double vx, double vy, double vz,
                            int rgb, double size, int lifetime, boolean glow, float alpha, float blink) {
        Minecraft minecraft = Minecraft.getInstance();
        if (glowSprite == null) {
            TextureAtlas atlas = (TextureAtlas) minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES);
            glowSprite = atlas.getSprite(ResourceLocation.withDefaultNamespace("glow"));
        }
        minecraft.particleEngine.add(new Mote(level, x, y, z, vx, vy, vz, rgb, (float) size, lifetime, glow, alpha, blink));
    }

    /// 清掉贴图缓存（资源重载后图集会换）
    public static void resetSprite() {
        glowSprite = null;
    }

    private static final class Mote extends TextureSheetParticle {
        private final boolean glow;
        private final float baseAlpha;
        private final float blink;
        private final float phase;

        Mote(ClientLevel level, double x, double y, double z, double vx, double vy, double vz,
             int rgb, float size, int lifetime, boolean glow, float alpha, float blink) {
            super(level, x, y, z);
            setSprite(TerraAmbientParticles.glowSprite);
            this.xd = vx;
            this.yd = vy;
            this.zd = vz;
            this.hasPhysics = false;
            this.gravity = 0.0F;
            this.friction = 0.985F;
            this.quadSize = size;
            this.lifetime = lifetime;
            this.rCol = ((rgb >> 16) & 255) / 255.0F;
            this.gCol = ((rgb >> 8) & 255) / 255.0F;
            this.bCol = (rgb & 255) / 255.0F;
            this.glow = glow;
            this.baseAlpha = alpha;
            this.blink = blink;
            this.phase = random.nextFloat() * Mth.TWO_PI;
            this.alpha = 0.0F;
            active++;
        }

        @Override
        public void tick() {
            super.tick();
            // 轻轻飘动
            xd += (random.nextDouble() - 0.5) * 0.0015;
            yd += (random.nextDouble() - 0.5) * 0.0008;
            zd += (random.nextDouble() - 0.5) * 0.0015;
            float life = age / (float) lifetime;
            float envelope = Math.min(1.0F, Math.min(life / 0.2F, (1.0F - life) / 0.3F));
            float flicker = blink > 0.0F ? 0.35F + 0.65F * Math.max(0.0F, Mth.sin(age * blink + phase)) : 1.0F;
            this.alpha = baseAlpha * Math.max(0.0F, envelope) * flicker;
        }

        @Override
        public void remove() {
            if (!removed) active = Math.max(0, active - 1);
            super.remove();
        }

        @Override
        protected int getLightColor(float partialTick) {
            return glow ? 0xF000F0 : super.getLightColor(partialTick);
        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }
    }
}
