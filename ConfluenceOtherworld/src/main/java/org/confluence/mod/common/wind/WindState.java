package org.confluence.mod.common.wind;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.mod.common.CommonConfigs;

/// 全局风态：**服务端权威，大小全局一致**（方向才随位置变化，见 {@link WindField}）。
///
/// 泰拉瑞亚的风速是连续缓变的：`current` 以有限速率逼近 `target`，`target` 每隔一段时间重掷一次。
/// 本类把同样的模型用 m/s 表达，并额外叠一层确定性阵风（{@link WindMath#gust}）。
///
/// 风是**整个存档**级别的状态（不是维度数据），因此通过 {@link IGlobalData} 持久化到 `level.dat`，
/// 与 {@code KillBoard}、{@code GameEventSystem} 同一套机制（见 {@code ModEvents} 的注册与
/// {@code PrimaryLevelDataMixin} 的读写）。切换存档时由 {@link #clear()} 复位。
public enum WindState implements IGlobalData {
    INSTANCE;

    /// 风速收敛速率：0.025 m/s per tick，即 0.5 m/s per second。
    public static final float SPEED_RATE = 0.025F;
    /// 风向收敛速率：0.2 度 per tick，即 4 度 per second（转 90 度约需 22 秒）。
    public static final float ANGLE_RATE = 0.2F * Mth.DEG_TO_RAD;
    /// 重掷间隔下限 2 分钟。
    public static final int REROLL_MIN = 2400;
    /// 重掷间隔上限 10 分钟。
    public static final int REROLL_MAX = 12000;
    /// 每次重掷抽到「大风天档」的概率（默认值，实际取配置 `windWindyChance`）。
    ///
    /// 重掷平均每 6 分钟一次，一个游戏日（24000 tick）约 3.3 次，所以 4% 对应
    /// **大约每 7~8 个游戏日出现一次大风天**（还要再被 {@link #WINDY_COOLDOWN} 兜底）。
    /// 早期取 10% 配合 1~6 分钟的重掷间隔，等于**每两天就有一次大风天**，加上大风天事件
    /// 会把风速下限锁定到入夜，体感就是"一直在刮大风"。
    public static final float WINDY_CHANCE = 0.04F;
    /// 抽到「有风档」（10 ~ 20 mph）的概率上界：15% → 约每 2 个游戏日一次。
    private static final float BREEZY_CHANCE = 0.15F;
    /// 大风天档的冷却：抽到一次之后，至少隔这么久才可能再抽到（2 个游戏日）。
    ///
    /// 这是**代码常量而不是配置**，所以即使整合包的配置把 `windWindyChance` 调得很高，
    /// 也不会出现接连两天都是大风天。
    public static final int WINDY_COOLDOWN = 48000;
    /// 重掷时抽到的最大自然风速：**30 mph**（`= WindMath.REF_SPEED`）。
    ///
    /// 这里必须与 mph 锚点对齐，不能随手取个"15"：
    /// 30 mph 既是旧单位的满刻度、也是本系统里"自然风的上限"——高于它只可能来自事件（沙尘暴）
    /// 或命令。这样三条线自洽：**20 mph = 大风天阈值，30 mph = 自然上限，>30 mph = 只有事件/命令**。
    ///
    /// （曾经取 15 m/s = 33.6 mph，比 30 mph 满刻度还高，意味着一个普通日子就能刮出超过满刻度的风，
    /// 而且比 20 mph 的大风天线高出 68%，自相矛盾。）
    public static final float NATURAL_MAX_SPEED = WindMath.REF_SPEED;

    private static final String SPEED = "windSpeed";
    private static final String SPEED_TARGET = "windSpeedTarget";
    private static final String ANGLE = "windAngle";
    private static final String ANGLE_TARGET = "windAngleTarget";
    private static final String TIMER = "windTimer";
    private static final String GUST_PHASE = "windGustPhase";
    private static final String WINDY_COOLDOWN_TAG = "windWindyCooldown";

    // ===== 持久化状态 =====
    private boolean initialized = false;
    private float speedCurrent = 0.0F;
    private float speedTarget = 0.0F;
    private float angleCurrent = 0.0F;
    private float angleTarget = 0.0F;
    private int timer = 0;
    private float gustPhase = 0.0F;
    /// 大风天档的剩余冷却（tick）。见 {@link #WINDY_COOLDOWN}。
    private int windyCooldown = 0;

    // ===== 运行时覆盖（不持久化，重载后失效） =====
    private float forcedSpeed = -1.0F;
    private float forcedAngle = Float.NaN;
    private int forceTicks = 0;
    private float directionBlend = 1.0F;
    private float speedFloor = 0.0F;
    private int rerollHold = 0;

    // ===================== 查询 =====================

    /// 全局风速（m/s），不含阵风。
    public float speed() {
        return speedCurrent;
    }

    /// 全局风速（m/s），含阵风。服务端的生成条件、事件受力都用这个。
    public float instantSpeed(long gameTime) {
        return speedCurrent * WindMath.gust(gameTime, gustPhase);
    }

    public float angle() {
        return angleCurrent;
    }

    /// 温度噪声造成的方向偏差的混合权重。0 = 关闭偏差（全体同向），1 = 完整偏差。
    public float directionBlend() {
        return directionBlend;
    }

    public float gustPhase() {
        return gustPhase;
    }

    /// 全局风向的单位向量（x, z）。
    public float dirX() {
        return Mth.cos(angleCurrent);
    }

    public float dirZ() {
        return Mth.sin(angleCurrent);
    }

    public WindTier tier() {
        return WindTier.bySpeed(speedCurrent);
    }

    /// 是否达到大风天阈值（20 mph），即本系统唯一的 gameplay 阈值。
    public boolean isWindyDay() {
        return speedCurrent >= WindMath.WINDY_DAY_MIN;
    }

    // ===================== 驱动 =====================

    /// 每 tick 调用一次（主世界，见 {@code TickEvents}）。
    public void tick(ServerLevel level) {
        if (!CommonConfigs.DO_WIND.get()) {
            speedCurrent = speedTarget = 0.0F;
            return;
        }
        if (!initialized) {
            initialized = true;
            gustPhase = level.random.nextFloat();
            reroll(level.random);
        }
        if (forceTicks > 0 && --forceTicks == 0) {
            clearOverride();
        }

        float targetSpeed = forcedSpeed >= 0.0F ? forcedSpeed : Math.max(speedTarget, speedFloor);
        float targetAngle = Float.isNaN(forcedAngle) ? angleTarget : forcedAngle;

        speedCurrent = WindMath.approach(speedCurrent, targetSpeed, SPEED_RATE);
        angleCurrent = WindMath.approachRadians(angleCurrent, targetAngle, ANGLE_RATE);

        if (rerollHold > 0) {
            --rerollHold;
        } else if (forcedSpeed < 0.0F && --timer <= 0) {
            reroll(level.random);
        }
        if (windyCooldown > 0) --windyCooldown;
    }

    /// 重掷目标风速与目标风向。分布偏向静风，有 `windWindyChance` 的概率抽到大风天档。
    ///
    /// 大风天档额外受 {@link #WINDY_COOLDOWN} 门控：冷却期内即使抽中也只按「有风档」处理，
    /// 这样"大风有多频繁"就有上界，不取决于配置里填了什么。
    private void reroll(RandomSource random) {
        float windyChance = (float) (double) CommonConfigs.WIND_WINDY_CHANCE.get();
        float roll = random.nextFloat();
        if (roll < windyChance && windyCooldown <= 0) {
            speedTarget = Mth.nextFloat(random, WindMath.WINDY_DAY_MIN, NATURAL_MAX_SPEED);
            windyCooldown = WINDY_COOLDOWN;
        } else if (roll < BREEZY_CHANCE) {
            speedTarget = Mth.nextFloat(random, WindTier.BREEZY.min(), WindMath.WINDY_DAY_MIN);
        } else {
            speedTarget = Mth.nextFloat(random, 0.0F, WindTier.BREEZY.min()) * random.nextFloat();
        }

        float delta = Mth.nextFloat(random, 30.0F, 110.0F) * Mth.DEG_TO_RAD;
        if (random.nextFloat() < 0.20F) {
            delta = Mth.PI - delta * 0.5F; // 20% 概率近似反向：原作的风会整体转向
        }
        angleTarget = angleCurrent + (random.nextBoolean() ? delta : -delta);

        timer = REROLL_MIN + random.nextInt(REROLL_MAX - REROLL_MIN);
    }

    // ===================== 覆盖（事件 / 命令） =====================

    /// 强制恒风（大小 + 方向）。`ticks` 之后自动失效。
    ///
    /// 只夹取**大小**上限，方向原样保留，所以斜风不会被夹偏。
    public void override(float speed, float angle, int ticks) {
        this.forcedSpeed = Mth.clamp(speed, 0.0F, MAX_FORCED_SPEED);
        this.forcedAngle = angle;
        this.forceTicks = ticks;
    }

    /// 强制风速上限（m/s）。命令的参数范围与之一致。
    public static final float MAX_FORCED_SPEED = 100.0F;

    /// 风速下限。事件期间用来防止刚开就掉档抖动。
    public void setSpeedFloor(float floor) {
        this.speedFloor = Math.max(0.0F, floor);
    }

    /// 0 = 关闭温度噪声偏差（全体同向，沙尘暴用）；1 = 完整偏差。
    public void setDirectionBlend(float blend) {
        this.directionBlend = Mth.clamp(blend, 0.0F, 1.0F);
    }

    /// 暂停重掷若干 tick（事件期间锁档）。
    public void setRerollHold(int ticks) {
        this.rerollHold = Math.max(this.rerollHold, ticks);
    }

    public boolean isOverridden() {
        return forcedSpeed >= 0.0F || !Float.isNaN(forcedAngle);
    }

    public void clearOverride() {
        this.forcedSpeed = -1.0F;
        this.forcedAngle = Float.NaN;
        this.forceTicks = 0;
        this.directionBlend = 1.0F;
        this.speedFloor = 0.0F;
        // ⚠️ 强制风期间 speedCurrent 会被拉到自然上限之上（比如 `/confluence wind set 40 0`），
        // 而 forcedSpeed 是**不持久化**的、speedCurrent 是**持久化**的 —— 两边不对称。
        // 不在解除时夹回来，就会留下一条长达一分钟的"衰减尾巴"：覆盖已经没了，
        // 但当前值还停在 40，然后以 0.5 m/s 每秒慢慢跌回自然目标（实测会被当成"不合理的大风"）。
        this.speedCurrent = Mth.clamp(this.speedCurrent, 0.0F, NATURAL_MAX_SPEED);
    }

    // ===================== 持久化（IGlobalData） =====================
    //
    // 注意：IGlobalData **没有也不需要 dirty 标记**。
    // level.dat 由 MinecraftServer.saveAllChunks 在每次自动保存（ticksUntilAutosave 归零）、
    // /save-all 与关服时**无条件**重写，写入路径是
    //   saveAllChunks → LevelStorageSource.saveDataTag → WorldData.createTag → PrimaryLevelData.setTagData
    // 而 IGlobalData.encode 就注入在 setTagData 的尾部，所以改完状态什么都不用做。
    // 千万不要用某个 SavedData 的 setDirty() 来"提醒"它——那只影响那份 SavedData 自己：
    // dirty 门控只存在于 DataStorage/SavedData 那一侧，与 level.dat 无关。

    @Override
    public void encode(CompoundTag nbt) {
        nbt.putFloat(SPEED, speedCurrent);
        nbt.putFloat(SPEED_TARGET, speedTarget);
        nbt.putFloat(ANGLE, angleCurrent);
        nbt.putFloat(ANGLE_TARGET, angleTarget);
        nbt.putInt(TIMER, timer);
        nbt.putFloat(GUST_PHASE, gustPhase);
        nbt.putInt(WINDY_COOLDOWN_TAG, windyCooldown);
    }

    @Override
    public void decode(CompoundTag nbt) {
        // ⚠️ 必须先整体复位再读。WindState 是**单例**（IGlobalData），而 decode 在**每次世界加载**时都会被调用；
        // 如果这里因为"本存档没有 wind 数据"而直接 return，上个存档留下的速度/初始化标记就会原封不动带进新世界
        // （表现为：新建世界一进去就刮着上个存档的风，然后才慢慢演化回去）。
        clear();
        if (!nbt.contains(SPEED)) return; // 新存档：留空等首次 tick 自行初始化
        this.initialized = true;
        // 会话内的强制风（forcedSpeed）不持久化，所以读档时不存在覆盖，当前值必须落在**自然范围**内。
        // 若上次退出前用过 `/confluence wind set 40 0`，存档里的 40 会被收成自然上限（30 mph），
        // 而不是原样保留一个"常态 40 m/s"。
        this.speedCurrent = saneSpeed(nbt.getFloat(SPEED), NATURAL_MAX_SPEED);
        this.speedTarget = nbt.contains(SPEED_TARGET) ? saneSpeed(nbt.getFloat(SPEED_TARGET), NATURAL_MAX_SPEED) : speedCurrent;
        this.angleCurrent = WindMath.wrapRadians(nbt.getFloat(ANGLE));
        this.angleTarget = nbt.contains(ANGLE_TARGET) ? WindMath.wrapRadians(nbt.getFloat(ANGLE_TARGET)) : angleCurrent;
        this.timer = nbt.getInt(TIMER);
        this.gustPhase = nbt.getFloat(GUST_PHASE);
        this.windyCooldown = Mth.clamp(nbt.getInt(WINDY_COOLDOWN_TAG), 0, WINDY_COOLDOWN);
        if (timer <= 0) timer = REROLL_MIN;
    }

    /// 把任意来源的风速夹到 `[0, max]`，并挡掉 NaN / 无穷。
    private static float saneSpeed(float speed, float max) {
        if (!Float.isFinite(speed)) return 0.0F;
        return Mth.clamp(speed, 0.0F, max);
    }

    @Override
    public String serializeKey() {
        return "confluence:wind";
    }

    /// 退出存档时复位，避免下一个存档继承上一个存档的风。
    @Override
    public void clear() {
        initialized = false;
        speedCurrent = 0.0F;
        speedTarget = 0.0F;
        angleCurrent = 0.0F;
        angleTarget = 0.0F;
        timer = 0;
        gustPhase = 0.0F;
        windyCooldown = 0;
        clearOverride();
        rerollHold = 0;
    }
}
