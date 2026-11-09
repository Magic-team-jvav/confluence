package org.confluence.mod.client.graphics;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.biome.Biome;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;
import org.confluence.mod.common.gameevent.*;
import org.confluence.mod.util.OverworldUtils;

/// 泰拉瑞亚式画面效果（Confluence 画质的第二层）：按所在的生物群系、事件和深度给画面调色。
/// 腐化发紫、猩红发红、神圣明亮、发光蘑菇幽蓝、丛林湿绿、沙漠暖黄、雪原清冷；
/// 血月、日食、南瓜月、霜月整片天地变色；沙尘暴和暴风雪是贴近的浓雾；地下越深越灰暗，太空空气稀薄，
/// 地狱和白天的沙漠远处有热浪扭曲。
/// 生物群系在玩家周围一圈采样按距离加权，所以边界上是渐变；所有参数再随时间平滑过渡（约一秒），不会突变。
/// 结果交给 TerraGraphics 的合成着色器（色调、饱和度、曝光、暗角、浓雾、扭曲）。
public final class TerraEffects {
    // 参数下标
    static final int TINT_R = 0, TINT_G = 1, TINT_B = 2, TINT = 3, SATURATION = 4, EXPOSURE = 5, VIGNETTE = 6,
            HAZE_R = 7, HAZE_G = 8, HAZE_B = 9, HAZE = 10, HAZE_DISTANCE = 11, DISTORTION = 12, AERIAL = 13,
            SHADOW_R = 14, SHADOW_G = 15, SHADOW_B = 16, SHADOW = 17, HIGHLIGHT_R = 18, HIGHLIGHT_G = 19, HIGHLIGHT_B = 20, HIGHLIGHT = 21,
            EDGE_R = 22, EDGE_G = 23, EDGE_B = 24, PULSE = 25, COUNT = 26;

    private static final float[] current = neutral();
    private static final float[] target = neutral();
    private static long lastFrame;
    private static long lastSample;
    private static ClientLevel lastLevel;

    private TerraEffects() {}

    private static float[] neutral() {
        float[] values = new float[COUNT];
        values[TINT_R] = values[TINT_G] = values[TINT_B] = 1.0F;
        values[SATURATION] = values[EXPOSURE] = values[AERIAL] = 1.0F;
        values[HAZE_R] = values[HAZE_G] = values[HAZE_B] = 0.5F;
        values[HAZE_DISTANCE] = 48.0F;
        values[SHADOW_R] = values[SHADOW_G] = values[SHADOW_B] = 1.0F;
        values[HIGHLIGHT_R] = values[HIGHLIGHT_G] = values[HIGHLIGHT_B] = 1.0F;
        return values;
    }

    public static float get(int index) {
        return current[index];
    }

    /// 每帧调用（AFTER_LEVEL）：大约每 0.15 秒重新计算一次目标，参数每帧向目标平滑靠拢
    public static void update(Minecraft minecraft, ClientLevel level, Camera camera, float partialTick) {
        long now = System.nanoTime();
        if (level != lastLevel) {
            // 换世界、换维度：直接跳到新目标，不从上一个世界渐变过来
            lastLevel = level;
            computeTarget(level, camera, partialTick);
            System.arraycopy(target, 0, current, 0, COUNT);
            lastSample = lastFrame = now;
            return;
        }
        if (now - lastSample > 150_000_000L) {
            lastSample = now;
            computeTarget(level, camera, partialTick);
        }
        float dt = Math.min((now - lastFrame) / 1.0E9F, 0.25F);
        lastFrame = now;
        float k = 1.0F - (float) Math.exp(-dt / 0.9F);
        for (int i = 0; i < COUNT; i++) current[i] += (target[i] - current[i]) * k;
    }

    /// 合成用的累加器：色调和浓雾按强度加权平均颜色，强度相加；饱和度、曝光、大气相乘；暗角、扭曲相加
    private static final class Acc {
        float tintR, tintG, tintB, tint;
        float saturation = 1.0F, exposure = 1.0F, aerial = 1.0F, vignette, distortion;
        float hazeR, hazeG, hazeB, haze, hazeDistance;
        float shadowR, shadowG, shadowB, shadow, highlightR, highlightG, highlightB, highlight;
        float edgeR, edgeG, edgeB, pulse;

        /// 分区调色：暗部和亮部各染一种颜色（腐化、猩红那种“病态”的观感），边缘暗角带颜色，pulse 让暗角缓缓脉动
        void tone(float weight, float sr, float sg, float sb, float shadowStrength, float hr, float hg, float hb, float highlightStrength,
                  float er, float eg, float eb, float pulseStrength) {
            if (weight <= 0.0F) return;
            float s = shadowStrength * weight;
            shadowR += sr * s;
            shadowG += sg * s;
            shadowB += sb * s;
            shadow += s;
            float h = highlightStrength * weight;
            highlightR += hr * h;
            highlightG += hg * h;
            highlightB += hb * h;
            highlight += h;
            edgeR += er * weight;
            edgeG += eg * weight;
            edgeB += eb * weight;
            pulse += pulseStrength * weight;
        }

        void add(float weight, float r, float g, float b, float tintStrength, float sat, float exp, float vig) {
            if (weight <= 0.0F) return;
            float t = tintStrength * weight;
            tintR += r * t;
            tintG += g * t;
            tintB += b * t;
            tint += t;
            saturation *= Mth.lerp(weight, 1.0F, sat);
            exposure *= Mth.lerp(weight, 1.0F, exp);
            vignette += vig * weight;
        }

        void haze(float weight, float r, float g, float b, float amount, float distance) {
            if (weight <= 0.0F) return;
            float h = amount * weight;
            hazeR += r * h;
            hazeG += g * h;
            hazeB += b * h;
            hazeDistance += distance * h;
            haze += h;
        }
    }

    private static void computeTarget(ClientLevel level, Camera camera, float partialTick) {
        float[] out = neutral();
        float strength = ClientConfigs.terrariaEffects / 100.0F;
        net.minecraft.world.level.material.FogType fluid = camera.getFluidInCamera();
        // 水下也保留群系的画面（否则出水时滤镜要从零重新淡入，有明显的延迟，水下反而比水面看得更清楚）；
        // 岩浆、细雪里看不见东西，不加
        boolean underwater = fluid == net.minecraft.world.level.material.FogType.WATER;
        if (strength <= 0.0F || (fluid != net.minecraft.world.level.material.FogType.NONE && !underwater)) {
            keepColors(out);
            System.arraycopy(out, 0, target, 0, COUNT);
            return;
        }
        Acc acc = new Acc();
        BlockPos pos = camera.getBlockPosition();
        double y = camera.getPosition().y;
        float openSky = Mth.clamp(level.getBrightness(LightLayer.SKY, pos) / 15.0F * 1.5F - 0.25F, 0.0F, 1.0F);
        boolean overworld = level.dimension() == OverworldUtils.dimension();
        boolean night = Mth.cos(level.getTimeOfDay(partialTick) * Mth.TWO_PI) < 0.0F;

        if (OverworldUtils.inUnderworld(level.dimension(), y)) {
            // 地狱：橙红、浓烟、热浪
            acc.add(1.0F, 1.0F, 0.6F, 0.35F, 0.28F, 1.1F, 1.0F, 0.12F);
            acc.haze(1.0F, 0.6F, 0.22F, 0.08F, 0.22F, 60.0F);
            acc.distortion += 0.7F;
        } else {
            biomes(level, camera, acc, openSky, night);
            if (overworld) {
                depth(acc, y, openSky);
                events(level, pos, acc, openSky);
            }
        }

        out[TINT] = Math.min(acc.tint, 0.75F) * strength;
        if (acc.tint > 0.0F) {
            out[TINT_R] = acc.tintR / acc.tint;
            out[TINT_G] = acc.tintG / acc.tint;
            out[TINT_B] = acc.tintB / acc.tint;
        }
        out[SATURATION] = Mth.lerp(strength, 1.0F, acc.saturation);
        out[EXPOSURE] = Mth.lerp(strength, 1.0F, acc.exposure);
        out[AERIAL] = Mth.lerp(strength, 1.0F, acc.aerial);
        out[VIGNETTE] = Math.min(acc.vignette, 0.6F) * strength;
        out[SHADOW] = Math.min(acc.shadow, 0.7F) * strength;
        if (acc.shadow > 0.0F) setLumaOne(out, SHADOW_R, acc.shadowR / acc.shadow, acc.shadowG / acc.shadow, acc.shadowB / acc.shadow);
        out[HIGHLIGHT] = Math.min(acc.highlight, 0.5F) * strength;
        if (acc.highlight > 0.0F) setLumaOne(out, HIGHLIGHT_R, acc.highlightR / acc.highlight, acc.highlightG / acc.highlight, acc.highlightB / acc.highlight);
        out[EDGE_R] = Math.min(acc.edgeR, 1.0F) * strength;
        out[EDGE_G] = Math.min(acc.edgeG, 1.0F) * strength;
        out[EDGE_B] = Math.min(acc.edgeB, 1.0F) * strength;
        out[PULSE] = Math.min(acc.pulse, 0.6F) * strength;
        // 水下：没有热浪；群系的雾减半（原版水下雾已经很浓）
        out[DISTORTION] = underwater ? 0.0F : Math.min(acc.distortion, 1.0F) * strength * ClientConfigs.heatDistortion / 100.0F;
        out[HAZE] = Math.min(acc.haze, 0.8F) * strength * (underwater ? 0.5F : 1.0F);
        if (acc.haze > 0.0F) {
            // 露天的雾随天色变暗（夜里的沙尘暴不该发亮）；地下的雾颜色本来就暗
            float daylight = Mth.lerp(openSky, 1.0F, level.getSkyDarken(partialTick));
            out[HAZE_R] = acc.hazeR / acc.haze * daylight;
            out[HAZE_G] = acc.hazeG / acc.haze * daylight;
            out[HAZE_B] = acc.hazeB / acc.haze * daylight;
            out[HAZE_DISTANCE] = acc.hazeDistance / acc.haze;
        }
        keepColors(out);
        System.arraycopy(out, 0, target, 0, COUNT);
    }

    /// 调色用的颜色归一到亮度为 1，着色器里乘上像素亮度就只改色相、不改明暗
    private static void setLumaOne(float[] out, int index, float r, float g, float b) {
        float luma = Math.max(0.2126F * r + 0.7152F * g + 0.0722F * b, 0.05F);
        out[index] = r / luma;
        out[index + 1] = g / luma;
        out[index + 2] = b / luma;
    }

    /// 强度为 0 的颜色没有意义：沿用当前颜色，免得淡出时颜色先跳到白色/灰色
    private static void keepColors(float[] out) {
        if (out[SHADOW] <= 0.0F) System.arraycopy(current, SHADOW_R, out, SHADOW_R, 3);
        if (out[HIGHLIGHT] <= 0.0F) System.arraycopy(current, HIGHLIGHT_R, out, HIGHLIGHT_R, 3);
        if (out[TINT] <= 0.0F) {
            out[TINT_R] = current[TINT_R];
            out[TINT_G] = current[TINT_G];
            out[TINT_B] = current[TINT_B];
        }
        if (out[HAZE] <= 0.0F) {
            out[HAZE_R] = current[HAZE_R];
            out[HAZE_G] = current[HAZE_G];
            out[HAZE_B] = current[HAZE_B];
            out[HAZE_DISTANCE] = current[HAZE_DISTANCE];
        }
    }

    /// 玩家周围 5×5 个点（间隔 6 格）采样生物群系，近的权重大；没有特殊画面的群系也占权重，所以边界上逐渐变淡
    private static void biomes(ClientLevel level, Camera camera, Acc acc, float openSky, boolean night) {
        BlockPos.MutableBlockPos sample = new BlockPos.MutableBlockPos();
        BlockPos center = camera.getBlockPosition();
        float total = 0.0F;
        float[] found = new float[NONE];
        int seaLevel = level.getSeaLevel();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                float weight = 1.0F - Mth.sqrt(dx * dx + dz * dz) / 3.2F;
                int x = center.getX() + dx * 6, z = center.getZ() + dz * 6;
                total += weight;
                // 原版群系是三维的：同一列里离地面高一点、或者山顶上，可能已经是别的群系了。
                // 所以除了相机高度，在地面上/下和原始地表高度也各取一次，取这一列里最“特殊”的那个（腐化优先）
                int kind = classify(level.getBiome(sample.set(x, center.getY(), z)));
                int ground = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
                if (ground > level.getMinBuildHeight() && center.getY() >= ground - 6) {
                    kind = Math.min(kind, classify(level.getBiome(sample.set(x, ground, z))));
                    kind = Math.min(kind, classify(level.getBiome(sample.set(x, ground - 6, z))));
                    if (ground > seaLevel + 8) kind = Math.min(kind, classify(level.getBiome(sample.set(x, seaLevel + 4, z))));
                }
                if (kind < NONE) found[kind] += weight;
            }
        }
        float corruption = found[CORRUPTION], crimson = found[CRIMSON], hallow = found[HALLOW], mushroom = found[MUSHROOM],
                jungle = found[JUNGLE], desert = found[DESERT], snow = found[SNOW], ocean = found[OCEAN];
        if (total <= 0.0F) return;
        corruption /= total;
        crimson /= total;
        hallow /= total;
        mushroom /= total;
        jungle /= total;
        desert /= total;
        snow /= total;
        ocean /= total;
        // 腐化：腐烂的紫黑色、发灰发暗，亮处泛病态的黄绿，近处就是浑浊的紫雾，四周压暗成深紫并缓慢“呼吸”
        acc.add(corruption, 0.58F, 0.45F, 1.0F, 0.62F, 0.5F, 0.76F, 0.38F);
        acc.haze(corruption, 0.13F, 0.08F, 0.2F, 0.52F, 30.0F);
        acc.tone(corruption, 0.42F, 0.2F, 0.9F, 0.68F, 0.75F, 1.0F, 0.35F, 0.38F, 0.14F, 0.03F, 0.24F, 0.12F);
        // 猩红：暗部发血红，亮处是苍白的肉色，远处血雾，四周暗红并像心跳一样缓缓脉动
        acc.add(crimson, 1.0F, 0.38F, 0.35F, 0.5F, 0.72F, 0.84F, 0.32F);
        acc.haze(crimson, 0.3F, 0.06F, 0.06F, 0.42F, 38.0F);
        acc.tone(crimson, 0.8F, 0.12F, 0.15F, 0.55F, 1.0F, 0.85F, 0.55F, 0.25F, 0.25F, 0.02F, 0.02F, 0.35F);
        acc.add(hallow, 1.0F, 0.82F, 1.0F, 0.22F, 1.15F, 1.04F, 0.0F);
        acc.haze(hallow, 0.95F, 0.8F, 1.0F, 0.08F, 90.0F);
        acc.add(mushroom, 0.55F, 0.72F, 1.0F, 0.4F, 1.1F, 0.95F, 0.1F);
        acc.haze(mushroom, 0.1F, 0.2F, 0.55F, 0.2F, 60.0F);
        acc.add(jungle, 0.85F, 1.0F, 0.75F, 0.14F, 1.08F, 1.0F, 0.0F);
        acc.haze(jungle, 0.55F, 0.7F, 0.45F, 0.1F, 80.0F);
        acc.add(desert, 1.0F, 0.9F, 0.72F, 0.14F, 1.0F, 1.03F, 0.0F);
        // 白天露天的沙漠：远处热浪
        if (!night) acc.distortion += desert * openSky * 0.3F;
        acc.add(snow, 0.82F, 0.92F, 1.0F, 0.14F, 0.92F, 1.02F, 0.0F);
        acc.add(ocean, 0.8F, 0.95F, 1.0F, 0.08F, 1.0F, 1.0F, 0.0F);
    }

    private static final int CORRUPTION = 0, CRIMSON = 1, HALLOW = 2, MUSHROOM = 3, JUNGLE = 4, DESERT = 5, SNOW = 6, OCEAN = 7, NONE = 8;

    /// 群系分类，数字越小越优先
    private static int classify(Holder<Biome> biome) {
        if (OverworldUtils.isCorruption(biome)) return CORRUPTION;
        if (OverworldUtils.isCrimson(biome)) return CRIMSON;
        if (OverworldUtils.isHallow(biome)) return HALLOW;
        if (OverworldUtils.isMushroom(biome)) return MUSHROOM;
        if (OverworldUtils.isJungle(biome)) return JUNGLE;
        if (OverworldUtils.isDesert(biome)) return DESERT;
        if (OverworldUtils.isSnowy(biome)) return SNOW;
        if (OverworldUtils.isOcean(biome)) return OCEAN;
        return NONE;
    }

    /// 深度：地下偏土色、更灰；洞穴层偏冷、更暗；太空空气稀薄，远处不再发白
    private static void depth(Acc acc, double y, float openSky) {
        float underground = (1.0F - openSky) * smooth((float) (OverworldUtils.getSurfaceY() + 8 - y) / 16.0F);
        float caverns = (1.0F - openSky) * smooth((float) (OverworldUtils.getUndergroundY() + 8 - y) / 24.0F);
        acc.add(underground * (1.0F - caverns), 0.95F, 0.88F, 0.8F, 0.08F, 0.92F, 1.0F, 0.08F);
        acc.add(caverns, 0.82F, 0.88F, 1.0F, 0.1F, 0.86F, 0.97F, 0.15F);
        float space = smooth((float) (y - (OverworldUtils.getSpaceY() - 40)) / 40.0F);
        if (space > 0.0F) {
            acc.add(space, 0.8F, 0.85F, 1.0F, 0.12F, 1.05F, 1.0F, 0.1F);
            acc.aerial *= Mth.lerp(space, 1.0F, 0.2F);
        }
    }

    /// 事件：主要作用在露天，进了屋或地下会淡一些
    private static void events(ClientLevel level, BlockPos pos, Acc acc, float openSky) {
        float outside = Mth.lerp(openSky, 0.35F, 1.0F);
        if (ClientGameEventSystem.isEventStarted(SolarEclipseGameEvent.KEY)) {
            acc.add(outside, 1.0F, 0.7F, 0.45F, 0.35F, 0.7F, 0.72F, 0.3F);
            acc.haze(outside, 0.2F, 0.12F, 0.08F, 0.2F, 60.0F);
        }
        if (ClientGameEventSystem.isEventStarted(BloodMoonGameEvent.KEY)) {
            acc.add(outside, 1.0F, 0.35F, 0.35F, 0.45F, 0.85F, 0.95F, 0.2F);
            acc.haze(outside, 0.4F, 0.05F, 0.05F, 0.15F, 60.0F);
        }
        if (ClientGameEventSystem.isEventStarted(PumpkinMoonGameEvent.KEY)) {
            acc.add(outside, 1.0F, 0.6F, 0.25F, 0.35F, 0.9F, 0.92F, 0.15F);
        }
        if (ClientGameEventSystem.isEventStarted(FrostMoonGameEvent.KEY)) {
            acc.add(outside, 0.6F, 0.8F, 1.0F, 0.35F, 0.85F, 0.95F, 0.15F);
            acc.haze(outside, 0.6F, 0.7F, 0.85F, 0.15F, 60.0F);
        }
        if (openSky > 0.0F && ClientGameEventSystem.isEventStarted(SandstormGameEvent.KEY)
                && SandstormGameEvent.INSTANCE.inWindZone(level, pos)) {
            acc.add(openSky, 1.0F, 0.85F, 0.6F, 0.25F, 0.9F, 0.97F, 0.1F);
            acc.haze(openSky, 0.8F, 0.66F, 0.42F, 0.65F, 22.0F);
        }
        if (openSky > 0.0F && level.isRaining() && ClientGameEventSystem.isEventStarted(BlizzardGameEvent.KEY)
                && BlizzardGameEvent.INSTANCE.inWindZone(level, pos)) {
            acc.add(openSky, 0.85F, 0.92F, 1.0F, 0.2F, 0.85F, 1.0F, 0.1F);
            acc.haze(openSky, 0.82F, 0.87F, 0.95F, 0.6F, 22.0F);
        }
    }

    private static float smooth(float x) {
        x = Mth.clamp(x, 0.0F, 1.0F);
        return x * x * (3.0F - 2.0F * x);
    }

    public static void reset() {
        lastLevel = null;
    }
}
