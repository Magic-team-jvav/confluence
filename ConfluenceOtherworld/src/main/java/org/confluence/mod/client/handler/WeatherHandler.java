package org.confluence.mod.client.handler;

import com.mojang.datafixers.util.Function3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.init.ModParticleTypes;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.wind.WindMath;
import org.confluence.mod.common.wind.WindState;
import org.confluence.mod.common.wind.WindTier;
import org.confluence.mod.network.s2c.WindSpeedPacketS2C;
import org.confluence.mod.util.OverworldUtils;
import org.joml.Vector2f;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;

/// 客户端风系统的唯一入口。
///
/// **风速全局一致，风向随位置变化**：风速由服务端权威演化（{@link WindState}），逐玩家下发；
/// 风向是服务端按接收者位置求出的局地风向（温度噪声梯度，见 {@code WindField}）。
/// 客户端只负责**平滑**：按固定速率向目标收敛，并在两次收包之间用确定性阵风自行插值，
/// 因此风向变化是连续的，不会出现收包瞬间的跳变。
///
/// 单位一律是 **m/s**（1 m/s = 0.05 方块/tick）。需要旧的归一化量（美术系数用）时走
/// {@link #getLegacyWindSpeedX()} / {@link #getLegacyWindSpeedZ()}，它们把风速除以 30 mph 的基准。
public final class WeatherHandler {
    private static final Map<ResourceLocation, Map<Block, Context>> BLOCK_PARTICLES = new Hashtable<>();
    private static final Map<ResourceLocation, Map<FluidType, ParticleOptions>> FLUID_PARTICLES = new Hashtable<>();

    /// 局地风向量（m/s），已含阵风与玩家处曝露度。**类内状态，不对外暴露**——
    /// 外部要局地风请用 {@link #windVec3()}（方块/tick）或 {@link #windExposedSpeed()}（m/s）。
    private static final Vector2f WIND_SPEED = new Vector2f();
    /// 局地风向，已取整到水平 4 向。**粒子速度用 {@link #windAngle()}**，这里只有方块级遮挡判定会用到。
    public static Direction windDirection = Direction.NORTH;

    /// 客户端角度跟随比例：每 tick 消除剩余角差的 15%，并按上下限夹取。
    private static final float ANGLE_FOLLOW = 0.15F;
    private static final float MIN_ANGLE_RATE = 0.0035F; // 与服务端全局转向速率一致
    private static final float MAX_ANGLE_RATE = 0.12F;
    /// 速度跟随比例与上下限（下限与服务端收敛速率一致）。
    private static final float SPEED_FOLLOW = 0.15F;
    private static final float MAX_SPEED_RATE = 0.5F;
    /// 粒子初速 = 风向量 × 该系数，并夹取到 {@link #MAX_PARTICLE_SPEED}。
    private static final float PARTICLE_COUPLING = 0.25F;
    private static final float MAX_PARTICLE_SPEED = 0.2F;

    // ===== 服务端下发的目标态 =====
    private static float targetSpeed = 0.0F;
    private static float targetAngle = 0.0F;
    private static float gustPhase = 0.0F;

    // ===== 客户端插值后的当前态 =====
    private static float speed = 0.0F;
    private static float angle = 0.0F;
    private static boolean initialized = false;
    /// 云层 X 漂移用的「虚拟 tick」。见 {@link #cloudTicks()}。
    private static float cloudTicks = 0.0F;
    /// 云层 Z 漂移量（方块）。见 {@link #cloudDriftZ()}。
    private static float cloudDriftZ = 0.0F;

    public static void handleWindSpeed(WindSpeedPacketS2C packet) {
        targetSpeed = packet.speed();
        targetAngle = packet.angle();
        gustPhase = packet.gustPhase();
        if (!initialized) {
            // 登录首包：直接落到目标值，避免从 0 慢慢爬起来
            initialized = true;
            speed = targetSpeed;
            angle = targetAngle;
        }
        // 注意：**不**把当前值重置为新值，否则每次收包都会跳变
    }

    /// 每客户端 tick 调用一次（见 {@code GameClientEvents}）。
    public static void handle() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        Player player = minecraft.player;
        if (level == null || player == null) return;

        speed = follow(speed, targetSpeed, SPEED_FOLLOW, WindState.SPEED_RATE, MAX_SPEED_RATE);
        float delta = WindMath.wrapRadians(targetAngle - angle);
        float step = Mth.clamp(Mth.abs(delta) * ANGLE_FOLLOW, MIN_ANGLE_RATE, MAX_ANGLE_RATE);
        angle = WindMath.wrapRadians(angle + Mth.clamp(delta, -step, step));

        float instant = instantSpeed(level);
        float exposure = WindMath.exposure(level, player.blockPosition());
        WIND_SPEED.set(Mth.cos(angle) * instant * exposure, Mth.sin(angle) * instant * exposure);
        windDirection = horizontalDirection();
        tickCloudDrift();
    }

    private static float follow(float current, float target, float follow, float minRate, float maxRate) {
        float delta = target - current;
        if (Mth.abs(delta) < 1.0E-4F) return target;
        float step = Mth.clamp(Mth.abs(delta) * follow, minRate, maxRate);
        return current + Mth.clamp(delta, -step, step);
    }

    // ===================== 查询 =====================

    /// 全局风速（m/s），**含阵风**、不含曝露度。物理与视觉（云速 / 雨倾斜）用这个。
    public static float windSpeed() {
        ClientLevel level = Minecraft.getInstance().level;
        return level == null ? speed : instantSpeed(level);
    }

    /// 全局**持续风速**（m/s），不含阵风。
    ///
    /// 气象台/命令等**读数**用这个：阵风是 ±25% 的物理起伏（周期 2~6 秒），
    /// 如果直接显示瞬时值，读数会一直跳、而且会高过自然风速上限（30 mph），看起来像不合理的大风。
    /// 气象学上"风速"本来也是指持续风速，阵风另计。
    public static float sustainedWindSpeed() {
        return speed;
    }

    /// 玩家处的有效风速（m/s），含阵风与曝露度。风粒子用这个。
    public static float windExposedSpeed() {
        return WIND_SPEED.length();
    }

    /// 局地风向量（m/s），含阵风与曝露度。
    public static Vector2f windVector() {
        return new Vector2f(WIND_SPEED);
    }

    /// 局地风向量换算成方块/tick（Vec3 形式），已含阵风与曝露度。
    public static Vec3 windVec3() {
        return new Vec3(WIND_SPEED.x, 0.0, WIND_SPEED.y).scale(WindMath.BLOCKS_PER_TICK);
    }

    /// 不含曝露度的风向量（方块/tick），用于雨倾斜与云速——雨云在露天渲染，玩家站在屋檐下时雨照样该被吹斜。
    /// 配置关闭时返回零向量（雨不斜、云不动）。
    private static float[] windBlocksPerTick() {
        if (!ClientConfigs.windSlantsRainClouds) return new float[]{0.0F, 0.0F};
        float bpt = WindMath.toBlocksPerTick(windSpeed());
        return new float[]{Mth.cos(angle) * bpt, Mth.sin(angle) * bpt};
    }

    /// 雨雪倾斜用的剪切系数：`offset = (y - camY) * slant`。
    ///
    /// 以相机高度为基准做剪切，可以让所有雨条获得**统一倾角**（剪切后线段方向与位置无关）。
    /// 上限按方向守恒的方式夹取，避免超大风速把雨拉成横线。
    public static float[] precipitationSlant() {
        float[] wind = windBlocksPerTick();
        float vx = wind[0] * RAIN_SLANT_GAIN;
        float vz = wind[1] * RAIN_SLANT_GAIN;
        float length = Mth.sqrt(vx * vx + vz * vz);
        if (length > MAX_SLANT) {
            float scale = MAX_SLANT / length;
            vx *= scale;
            vz *= scale;
        }
        return new float[]{vx, vz};
    }

    /// 局地风向（弧度）。
    public static float windAngle() {
        return angle;
    }

    /// 是否达到大风天阈值（20 mph）。这是本系统唯一的 gameplay 阈值，服务端用同一条件开启大风天事件。
    public static boolean isWindyDay() {
        return windSpeed() >= WindMath.WINDY_DAY_MIN;
    }

    /// 阵风是否明显强于目标值，用于装饰性抖动。
    public static boolean isGusting() {
        return windSpeed() > speed * 1.15F;
    }

    /// 玩家处曝露度。
    public static float exposure() {
        ClientLevel level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if (level == null || player == null) return 0.0F;
        return WindMath.exposure(level, player.blockPosition());
    }

    /// 气象台用：`"8.9 m/s"`。**显示持续风速**（不含阵风），见 {@link #sustainedWindSpeed()}。
    public static Component windSpeedInfo() {
        return Component.literal("%.1f m/s".formatted(sustainedWindSpeed()));
    }

    /// 气象台用：`"东南风"`。
    public static Component windCompassInfo() {
        return Component.translatable(WindMath.compassKey(angle));
    }

    // ===================== 兼容层 =====================

    /// 旧归一化单位（满值 1.0 = 30 mph）。风车转速、灯笼夜着色器等既有美术系数走这里，系数不需要改。
    public static float getLegacyWindSpeedX() {
        return WIND_SPEED.x / WindMath.REF_SPEED;
    }

    public static float getLegacyWindSpeedZ() {
        return WIND_SPEED.y / WindMath.REF_SPEED;
    }

    /// 云层的「虚拟 tick」：替换原版 `renderClouds` 里的 `ticks`，驱动 **X 方向** 漂移。
    ///
    /// 推导：原版云层特征的世界坐标是 `3072u - d1`，其中 `d1 = (ticks + partialTick) * 0.03`，
    /// 所以 `dX/dt = -0.03 · d(ticks)/dt`。要让云以 `vx` 顺风漂移，就需要 `d(ticks)/dt = -vx / 0.03`。
    /// 速率换算见 {@link #CLOUD_REFERENCE_SPEED}。
    public static float cloudTicks() {
        return cloudTicks;
    }

    /// 云层叠加到 `camZ` 上的漂移量（方块），驱动 **Z 方向** 漂移。
    ///
    /// 原版 `d4 = camZ / 12 + 0.33` 没有时间项，所以云在南北风下完全不动。同样由
    /// 特征世界坐标 `3072u - 12·d4 + camZ = 3072u - driftZ - 常量` 可得 `dZ/dt = -d(driftZ)/dt`。
    public static float cloudDriftZ() {
        return cloudDriftZ;
    }

    /// 云层漂移是否应该在本维度生效。
    ///
    /// 注入点改的是 `renderClouds` 的 `camZ` **参数**，而同一次调用里 camZ 也会被传给
    /// `level.effects().renderClouds(...)`，所以只对主世界生效：其它维度可能有模组自己的实现，
    /// 不该拿到被我们偏移过的相机坐标。
    public static boolean cloudDriftApplies() {
        if (!ClientConfigs.windSlantsRainClouds) return false;
        ClientLevel level = Minecraft.getInstance().level;
        return level != null && level.dimension() == OverworldUtils.dimension();
    }

    /// 推进云层漂移。**必须在每 tick 的客户端 tick 里调用**（`renderClouds` 是每帧调用，
    /// 放在渲染里会让云速随帧率变化）。
    private static void tickCloudDrift() {
        if (!ClientConfigs.windSlantsRainClouds) {
            // 配置关闭 = 完全回退原版：虚拟 tick 与真实 tick 同步，云回到原版方向与速度，且不注入 Z 漂移
            cloudTicks = wrapFloat(cloudTicks + 1.0F, CLOUD_TICK_WRAP);
            return;
        }
        if (!cloudDriftApplies()) return;
        // windSpeed() 含阵风，所以云速也会跟着阵风轻微起伏
        float factor = Mth.clamp(windSpeed() / CLOUD_REFERENCE_SPEED, 0.0F, MAX_CLOUD_FACTOR);
        float cos = Mth.cos(angle);
        float sin = Mth.sin(angle);
        cloudTicks = wrapFloat(cloudTicks - factor * cos, CLOUD_TICK_WRAP);
        cloudDriftZ = wrapFloat(cloudDriftZ - VANILLA_CLOUD_SPEED * factor * sin, CLOUD_DRIFT_WRAP);
    }

    /// 云速基准风速（m/s）：**该风速下云速等于原版**（{@link #VANILLA_CLOUD_SPEED}）。
    ///
    /// ⚠️ 这里**不能**拿大风天阈值（8.94 m/s）当基准。重掷分布下风速的**中位数只有约 1.6 m/s、
    /// 均值约 3.6 m/s**（65% 的样本落在 0 ~ 4.47 m/s 这个"偏静风"分支里），
    /// 按 8.94 归一化会让**常见情况只有原版云速的 0.4 倍** —— 原版云速本身就慢（0.6 方块/秒），
    /// 再打四折观感就是"云几乎不动"。
    ///
    /// 取 2.0 后的倍数：静风上界(1.34) ≈ 0.7×、中位(1.6) ≈ 0.8×、均值(3.6) ≈ 1.8×、
    /// 微风上界(4.47) ≈ 2.2×、**大风天(8.94) ≈ 4.5×**、强风及以上封顶 6×。
    /// 觉得整体还慢/还快，改这一个数即可。
    private static final float CLOUD_REFERENCE_SPEED = 2.0F;
    /// 云速倍数上限（`0.03 * 6 = 0.18` 方块/tick ≈ 3.6 方块/秒），避免烈风或事件把云拉成残影。
    private static final float MAX_CLOUD_FACTOR = 6.0F;

    /// 按周期对称取模。取模周期都对应原版自己的 2048 取模，所以是无缝的；
    /// 同时把累加值限制在远小于 2^24 的范围，避免长时间运行后 float 精度丢失导致云停住。
    private static float wrapFloat(float value, float period) {
        if (value >= period) return value % period;
        if (value <= -period) return value % -period;
        return value;
    }

    /// 原版云层速度：0.03 方块/tick。
    private static final float VANILLA_CLOUD_SPEED = 0.03F;
    /// 虚拟 tick 的取模周期：原版 `d2 -= floor(d2 / 2048) * 2048` 对应 `2048 * 12 / 0.03`。
    private static final float CLOUD_TICK_WRAP = 819200.0F;
    /// Z 漂移量（方块）的取模周期：`2048 * 12`。
    private static final float CLOUD_DRIFT_WRAP = 24576.0F;
    /// 雨雪倾斜的剪切增益（每 1 方块/tick 的风产生多少斜率）。大风天(8.94 m/s)约 31 度。
    private static final float RAIN_SLANT_GAIN = 1.35F;
    /// 剪切斜率上限（约 42 度），按方向守恒夹取。
    private static final float MAX_SLANT = 0.9F;

    private static float instantSpeed(Level level) {
        return speed * WindMath.gust(level.getGameTime(), gustPhase);
    }

    private static Direction horizontalDirection() {
        float x = Mth.cos(angle);
        float z = Mth.sin(angle);
        if (Mth.abs(x) >= Mth.abs(z)) return x >= 0.0F ? Direction.EAST : Direction.WEST;
        return z >= 0.0F ? Direction.SOUTH : Direction.NORTH;
    }

    // ===================== 风粒子 =====================

    public static void handleBlock(ClientLevel level, RandomSource random, BlockState blockState, BlockPos.MutableBlockPos blockPos, Map<Block, Context> data) {
        Vec3 wind = windAt(level, blockPos);
        if (wind == Vec3.ZERO) return;

        Context context = data.get(blockState.getBlock());
        if (context != null && context.facing.isAvailable(level, random, blockPos, blockState, context)) {
            context.facing.apply(level, random, blockPos, blockState, context, wind);
        }
    }

    public static void handleFluid(ClientLevel level, RandomSource random, FluidState fluidState, BlockPos.MutableBlockPos blockPos, Map<FluidType, ParticleOptions> data) {
        Vec3 wind = windAt(level, blockPos);
        if (wind == Vec3.ZERO) return;

        ParticleOptions particleOptions = data.get(fluidState.getType().getFluidType());
        if (particleOptions == null) return;

        BlockPos relative = blockPos.above();
        BlockState aboveState = level.getBlockState(relative);
        if (!aboveState.isAir() && aboveState.getFluidState().isEmpty() && aboveState.isSuffocating(level, relative)) return;

        Vec3 vec3 = Vec3.atCenterOf(blockPos);
        double x = vec3.x + Mth.nextDouble(random, -0.5, 0.5);
        double y = vec3.y + 0.57;
        double z = vec3.z + Mth.nextDouble(random, -0.5, 0.5);
        spawnParticle(particleOptions, x, y, z, random, wind);
    }

    /// 该方块位置的风向量（方块/tick）。方向取玩家处的局地风向（近似），强度按该位置的曝露度衰减。
    private static Vec3 windAt(Level level, BlockPos pos) {
        float instant = instantSpeed(level);
        if (instant < WindMath.MIN_EFFECT_SPEED) return Vec3.ZERO;
        float exposure = WindMath.exposure(level, pos);
        if (exposure <= 0.0F) return Vec3.ZERO;
        float mps = WindMath.toBlocksPerTick(instant * exposure);
        return new Vec3(Mth.cos(angle) * mps, 0.0, Mth.sin(angle) * mps);
    }

    private static void spawnParticle(ParticleOptions particleOptions, double x, double y, double z, RandomSource random, Vec3 wind) {
        Particle particle = Minecraft.getInstance().particleEngine.createParticle(particleOptions, x, y, z, 0.0, 0.0, 0.0);
        if (particle == null) return;
        double scale = /*PARTICLE_COUPLING * */Mth.nextDouble(random, 0.4, 1.0);
        particle.setParticleSpeed(
                Mth.clamp(wind.x * scale, -MAX_PARTICLE_SPEED, MAX_PARTICLE_SPEED),
                0.0,
                Mth.clamp(wind.z * scale, -MAX_PARTICLE_SPEED, MAX_PARTICLE_SPEED)
        );
        float roll = Mth.nextFloat(random, -Mth.HALF_PI, Mth.HALF_PI);
        particle.oRoll = roll;
        particle.roll = roll;
    }

    public static void initialize(Player player) {
        if (!BLOCK_PARTICLES.isEmpty()) return;
        for (Holder<Biome> biome : player.registryAccess().registryOrThrow(Registries.BIOME).asHolderIdMap()) {
            ResourceKey<Biome> key = biome.getKey();
            if (key == null || !biome.is(BiomeTags.IS_OVERWORLD)) continue;
            // 按群系分流：雪原不该飘沙粒，沙漠不该飘雪
            registerBlockParticle(key, map -> {
                if (biome.is(BiomeTags.IS_FOREST) || biome.is(BiomeTags.IS_JUNGLE) || biome.is(BiomeTags.IS_TAIGA)
                        || biome.is(BiomeTags.IS_SAVANNA) || OverworldUtils.isJungle(biome)) {
                    leavesParticles(map);
                }
                if (OverworldUtils.isDesert(biome) || biome.is(BiomeTags.IS_BEACH) || biome.is(BiomeTags.IS_BADLANDS)) {
                    sandParticles(map);
                }
                if (OverworldUtils.isSnowy(biome) || biome.is(BiomeTags.IS_MOUNTAIN)) {
                    snowParticles(map);
                }
            });
        }
    }

    public static void reset() {
        BLOCK_PARTICLES.clear();
        FLUID_PARTICLES.clear();
        WIND_SPEED.set(0.0F, 0.0F);
        windDirection = Direction.NORTH;
        speed = 0.0F;
        angle = 0.0F;
        targetSpeed = 0.0F;
        targetAngle = 0.0F;
        gustPhase = 0.0F;
        cloudTicks = 0.0F;
        cloudDriftZ = 0.0F;
        initialized = false;
    }

    public static void leavesParticles(Map<Block, Context> map) {
        map.put(Blocks.OAK_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.SPRUCE_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.BIRCH_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.JUNGLE_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.ACACIA_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.CHERRY_LEAVES, new Context(Facing.HORIZONTAL, ParticleTypes.CHERRY_LEAVES, Context.FULL));
        map.put(Blocks.DARK_OAK_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.MANGROVE_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.AZALEA_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.FLOWERING_AZALEA_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.LEAVES.get(), new Context(Facing.HORIZONTAL, ModParticleTypes.YELLOW_WILLOW.get(), Context.FULL));
    }

    public static void sandParticles(Map<Block, Context> map) {
        map.put(Blocks.RED_SAND, new Context(Facing.POSITIVE_Y, ModParticleTypes.RED_SAND.get(), Context.FULL));
        map.put(NatureBlocks.RED_SAND_LAYER_BLOCK.get(), new Context(Facing.POSITIVE_Y, ModParticleTypes.RED_SAND.get(), Context.SUIT_YP));
        map.put(Blocks.SAND, new Context(Facing.POSITIVE_Y, ModParticleTypes.SAND.get(), Context.FULL));
        map.put(NatureBlocks.SAND_LAYER_BLOCK.get(), new Context(Facing.POSITIVE_Y, ModParticleTypes.SAND.get(), Context.SUIT_YP));
    }

    public static void snowParticles(Map<Block, Context> map) {
        map.put(Blocks.SNOW_BLOCK, new Context(Facing.POSITIVE_Y, ModParticleTypes.SNOW.get(), Context.FULL));
        map.put(Blocks.SNOW, new Context(Facing.POSITIVE_Y, ModParticleTypes.SNOW.get(), Context.SUIT_YP));
        map.put(Blocks.POWDER_SNOW, new Context(Facing.POSITIVE_Y, ModParticleTypes.SNOW.get(), Context.FULL));
    }

    private static void registerBlockParticle(ResourceKey<Biome> biome, Consumer<Map<Block, Context>> consumer) {
        consumer.accept(BLOCK_PARTICLES.computeIfAbsent(biome.location(), location -> new Hashtable<>()));
    }

    public static Map<Block, Context> getBlockParticles(Holder<Biome> biome) {
        ResourceKey<Biome> key = biome.getKey();
        return key == null ? null : BLOCK_PARTICLES.get(key.location());
    }

    public static Map<FluidType, ParticleOptions> getFluidParticles(Holder<Biome> biome) {
        ResourceKey<Biome> key = biome.getKey();
        return key == null ? null : FLUID_PARTICLES.get(key.location());
    }

    /// 风粒子密度系数（0 ~ 1），随有效风速线性增长，供 {@code ClientLevelMixin} 调整生成概率。
    private static float particleDensity() {
        return WindTier.intensity(windExposedSpeed());
    }

    /// `ClientConfigs.showWindParticles`（0 ~ 100 的"可见比例"）与风速档位合成的随机门限。
    public static int particleThreshold() {
        int ratio = ClientConfigs.showWindParticles;
        if (ratio <= 0) return 0;
        float density = particleDensity();
        return Math.max(1, Mth.floor((100 - ratio) * (1.0F - density * 0.7F)) + 1);
    }

    public record Context(Facing facing, ParticleOptions options, Function3<Level, BlockPos, BlockState, Vec3> step) {
        private static final Vec3 DOT_57 = new Vec3(0.57, 0.57, 0.57);
        public static final Function3<Level, BlockPos, BlockState, Vec3> FULL = (level, blockPos, blockState) -> DOT_57;
        public static final Function3<Level, BlockPos, BlockState, Vec3> SUIT_YP = (level, blockPos, blockState) -> {
            VoxelShape shape = blockState.getCollisionShape(level, blockPos);
            return new Vec3(0.0, shape.max(Direction.Axis.Y) - 0.23, 0.0);
        };
    }

    public static abstract class Facing {
        public static final Facing POSITIVE_Y = new Facing() {
            @Override
            public boolean isAvailable(Level level, RandomSource random, BlockPos blockPos, BlockState blockState, Context context) {
                BlockPos relative = blockPos.relative(windDirection.getOpposite()).above();
                BlockState relativeState = level.getBlockState(relative);
                return relativeState.isAir() || !relativeState.isSuffocating(level, relative);
            }

            @Override
            public void apply(Level level, RandomSource random, BlockPos blockPos, BlockState blockState, Context context, Vec3 wind) {
                Vec3 vec3 = Vec3.atCenterOf(blockPos);
                double x = vec3.x + Mth.nextDouble(random, -0.5, 0.5);
                double y = vec3.y + context.step.apply(level, blockPos, blockState).y;
                double z = vec3.z + Mth.nextDouble(random, -0.5, 0.5);
                spawnParticle(context.options, x, y, z, random, wind);
            }
        };
        public static final Facing HORIZONTAL = new Facing() {
            @Override
            public boolean isAvailable(Level level, RandomSource random, BlockPos blockPos, BlockState blockState, Context context) {
                Direction opposite = windDirection.getOpposite();
                BlockPos relative = blockPos.relative(opposite);
                BlockState relativeState = level.getBlockState(relative);
                if (!relativeState.isAir() && relativeState.isSuffocating(level, relative)) return false;
                relative = blockPos.relative(windDirection);
                relativeState = level.getBlockState(relative);
                return relativeState.isAir() || !Block.isFaceFull(relativeState.getCollisionShape(level, relative), opposite);
            }

            @Override
            public void apply(Level level, RandomSource random, BlockPos blockPos, BlockState blockState, Context context, Vec3 wind) {
                Vec3 vec3 = Vec3.atCenterOf(blockPos);
                Vec3 step = context.step.apply(level, blockPos, blockState);
                // 用连续风向把粒子放到背风侧的边缘，45 度风也能正确偏移（旧实现取整到 4 向后会被吞掉）
                float dirX = Mth.cos(angle);
                float dirZ = Mth.sin(angle);
                double x = vec3.x + dirX * step.x;
                double y = vec3.y + Mth.nextDouble(random, -0.5, 0.5);
                double z = vec3.z + dirZ * step.z;
                spawnParticle(context.options, x, y, z, random, wind);
            }
        };

        public abstract boolean isAvailable(Level level, RandomSource random, BlockPos blockPos, BlockState blockState, Context context);

        public abstract void apply(Level level, RandomSource random, BlockPos blockPos, BlockState blockState, Context context, Vec3 wind);
    }

    private WeatherHandler() {}
}
