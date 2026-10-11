package org.confluence.mod.common.wind;

import it.unimi.dsi.fastutil.longs.Long2DoubleLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.CommonConfigs;
import org.joml.Vector2f;

/// 空间方向场：**风速全局一致，但风向随位置变化**。
///
/// 方向偏差由**世界生成温度噪声的梯度**给出——温度路由 `overworld/temperature` 是
/// `DensityFunctions.shiftedNoise2d(...)`，**只与 (x, z) 有关**，所以它的梯度是一个良定义的 2D 向量场，
/// 且随世界种子变化（不同存档的方向场不同）。
///
/// 求值分两级缓存：64 格晶格上的温度 → 晶格点的梯度单位向量 → 位置处双线性插值。
///
/// **只能在服务端调用**：{@code ClientLevel} 拿不到 {@code RandomState}（客户端没有 worldSeed），
/// 因此客户端一律使用服务端下发的局地风向。
public final class WindField {
    /// 风晶格边长（方块）。温度场的相关长度在数百方块量级，64 足够平滑。
    public static final int CELL = 64;
    /// 采样高度。温度路由与 y 无关，这里只是占位。
    private static final int SAMPLING_Y = 64;
    /// 缓存上限，超过即整体清空（温度场平滑，短时间窗口内的命中率才是关键）。
    private static final int CACHE_LIMIT = 4096;
    private static final Vector2f NO_DIRECTION = new Vector2f();

    private static final Long2DoubleLinkedOpenHashMap TEMPERATURES = new Long2DoubleLinkedOpenHashMap();
    private static final Long2ObjectLinkedOpenHashMap<Vector2f> GRADIENTS = new Long2ObjectLinkedOpenHashMap();

    private static ServerLevel cachedLevel;
    private static DensityFunction cachedTemperature;
    /// 温度路由不可用时置位，整个空间方向场退化为「全局恒向」。
    private static boolean unavailable;

    private WindField() {}

    /// 该位置的风向量，单位：方块/tick。已含阵风与曝露度。
    public static Vec3 windAt(ServerLevel level, Vec3 pos) {
        WindState state = WindState.INSTANCE;
        float speed = state.instantSpeed(level.getGameTime());
        if (speed < WindMath.MIN_EFFECT_SPEED) return Vec3.ZERO;

        float exposure = WindMath.exposure(level, BlockPos.containing(pos));
        if (exposure <= 0.0F) return Vec3.ZERO;

        Vector2f dir = direction(level, state, pos.x, pos.z);
        return new Vec3(dir.x, 0.0, dir.y).scale(WindMath.toBlocksPerTick(speed * exposure));
    }

    /// 该位置的**单位**风向（x, z）。全局风速与位置无关，所以只要方向。
    public static Vector2f direction(ServerLevel level, WindState state, double x, double z) {
        float globalX = state.dirX();
        float globalZ = state.dirZ();
        float blend = state.directionBlend();
        if (blend <= 0.0F || unavailable || !CommonConfigs.WIND_DIRECTION_NOISE.get()) {
            return new Vector2f(globalX, globalZ);
        }

        float amplitude = (float) Math.toRadians((double) CommonConfigs.WIND_DIRECTION_AMPLITUDE.get());
        if (amplitude <= 0.0F) return new Vector2f(globalX, globalZ);

        Vector2f gradient = gradientDirection(level, x, z);
        // cross(a, b) = a.x*b.z - a.z*b.x = sin(θ_b - θ_a)
        // 用叉积而不是角度相减：天然连续、有界于 [-1, 1]，不会在 ±π 处出现风向撕裂线。
        float cross = gradient.x * globalZ - gradient.y * globalX;
        float theta = state.angle() + amplitude * cross * blend;
        return new Vector2f(Mth.cos(theta), Mth.sin(theta));
    }

    /// 位置处双线性插值后的温度梯度方向（单位向量）。退化时返回零向量，调用方应退回全局风向。
    private static Vector2f gradientDirection(ServerLevel level, double x, double z) {
        double cellX = x / CELL;
        double cellZ = z / CELL;
        int i = Mth.floor(cellX);
        int j = Mth.floor(cellZ);
        float fx = (float) (cellX - i);
        float fz = (float) (cellZ - j);

        Vector2f g00 = gradientAt(level, i, j);
        Vector2f g10 = gradientAt(level, i + 1, j);
        Vector2f g01 = gradientAt(level, i, j + 1);
        Vector2f g11 = gradientAt(level, i + 1, j + 1);

        float x0 = Mth.lerp(fx, g00.x, g10.x);
        float x1 = Mth.lerp(fx, g01.x, g11.x);
        float z0 = Mth.lerp(fx, g00.y, g10.y);
        float z1 = Mth.lerp(fx, g01.y, g11.y);
        float gx = Mth.lerp(fz, x0, x1);
        float gz = Mth.lerp(fz, z0, z1);

        float length = Mth.sqrt(gx * gx + gz * gz);
        if (length < 1.0E-6F) return NO_DIRECTION;
        return new Vector2f(gx / length, gz / length);
    }

    /// 晶格点 `(i, j)` 处的温度梯度方向（单位向量）。
    private static Vector2f gradientAt(ServerLevel level, int i, int j) {
        long key = key(i, j);
        Vector2f cached = GRADIENTS.get(key);
        if (cached != null) return cached;

        // 中心差分，步长就是一个晶格
        double dx = temperatureAt(level, i + 1, j) - temperatureAt(level, i - 1, j);
        double dz = temperatureAt(level, i, j + 1) - temperatureAt(level, i, j - 1);
        double length = Math.sqrt(dx * dx + dz * dz);

        Vector2f result;
        if (length < 1.0E-9) {
            result = NO_DIRECTION; // 温度场退化（超平坦 / 自定义维度）：不加偏差
        } else {
            result = new Vector2f((float) (dx / length), (float) (dz / length));
        }
        putGradient(key, result);
        return result;
    }

    private static double temperatureAt(ServerLevel level, int i, int j) {
        long key = key(i, j);
        if (TEMPERATURES.containsKey(key)) return TEMPERATURES.get(key);

        DensityFunction temperature = temperature(level);
        double value = temperature == null
                ? 0.0
                : temperature.compute(new DensityFunction.SinglePointContext(i * CELL, SAMPLING_Y, j * CELL));
        if (TEMPERATURES.size() >= CACHE_LIMIT) TEMPERATURES.clear();
        TEMPERATURES.put(key, value);
        return value;
    }

    /// 取得（并缓存）本世界的温度路由。不可用时永久退化为全局恒向。
    private static DensityFunction temperature(ServerLevel level) {
        if (cachedLevel != level) {
            cachedLevel = level;
            cachedTemperature = null;
            unavailable = false;
            TEMPERATURES.clear();
            GRADIENTS.clear();
        }
        if (unavailable) return null;
        if (cachedTemperature == null) {
            try {
                cachedTemperature = level.getChunkSource().randomState().sampler().temperature();
            } catch (Exception e) {
                unavailable = true;
                return null;
            }
        }
        return cachedTemperature;
    }

    private static void putGradient(long key, Vector2f value) {
        if (GRADIENTS.size() >= CACHE_LIMIT) GRADIENTS.clear();
        GRADIENTS.put(key, value);
    }

    private static long key(int i, int j) {
        return ((long) i << 32) ^ (j & 0xFFFFFFFFL);
    }

    /// 该位置所属的风晶格编号，用于「玩家跨越晶格时下发新的局地风向」。
    public static long cellKey(double x, double z) {
        long i = Mth.floor(x / CELL);
        long j = Mth.floor(z / CELL);
        return (i << 32) ^ (j & 0xFFFFFFFFL);
    }

    /// 世界卸载 / 数据包重载时清空缓存。
    public static void reset() {
        cachedLevel = null;
        cachedTemperature = null;
        unavailable = false;
        TEMPERATURES.clear();
        GRADIENTS.clear();
    }
}
