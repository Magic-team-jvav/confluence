package org.confluence.mod.common.gameevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.wind.WindMath;
import org.confluence.mod.common.wind.WindState;
import org.confluence.mod.util.OverworldUtils;

/// 大风天（对应泰拉瑞亚的 Windy Day）。
///
/// 这是**派生事件**：它没有独立的抽签，条件就是「白天 + 风速达到 20 mph」。
/// 风速本身由 {@link WindState} 连续演化，事件只负责在跨过阈值时提供**粘滞**：
/// 启动时把风速下限抬到阈值以上并暂停重掷，避免事件刚开就因为阵风掉档而抖动。
///
/// 事件期间**不影响任何实体**（风推实体只属于沙尘暴与暴风雪），只驱动生成条件与视觉。
public enum WindyDayGameEvent implements GameEvent {
    INSTANCE;

    public static final ResourceKey<WindyDayGameEvent> KEY = GameEvent.createKey(Confluence.asResource("windy_day"));
    /// 风速跌回阈值以下后需要持续这么久才结束，避免在阈值附近反复开关。
    private static final int CALM_TOLERANCE = 600;
    /// 启动后锁定风速下限与重掷的时长（3 分钟）。
    private static final int HOLD_TICKS = 3600;
    /// 锁定期间维持的风速下限。
    private static final float SPEED_FLOOR = WindMath.WINDY_DAY_MIN + 0.5F;

    private ServerLevel level;
    private boolean active;
    private boolean requested;
    private int calmTicks;

    @Override
    public void open(MinecraftServer server) {
        level = OverworldUtils.getLevel(server);
        requested = false;
    }

    @Override
    public void close(MinecraftServer server) {
        clear();
        level = null;
        requested = false;
    }

    @Override
    public void tick() {
        if (!active || level == null) return;
        calmTicks = windSpeed() < WindMath.WINDY_DAY_MIN ? calmTicks + 1 : 0;
    }

    private float windSpeed() {
        return WindState.INSTANCE.instantSpeed(level.getGameTime());
    }

    @Override
    public boolean canStart() {
        if (level == null) return false;
        return requested || (level.isDay() && windSpeed() >= WindMath.WINDY_DAY_MIN);
    }

    @Override
    public boolean canEnd() {
        if (level == null) return true;
        if (requested) return false;
        return !level.isDay() || calmTicks > CALM_TOLERANCE;
    }

    @Override
    public void onStart() {
        active = true;
        requested = false;
        calmTicks = 0;
        if (level == null) return;
        WindState state = WindState.INSTANCE;
        state.setSpeedFloor(SPEED_FLOOR);
        state.setRerollHold(HOLD_TICKS);
    }

    @Override
    public void onEnd() {
        clear();
    }

    private void clear() {
        active = false;
        if (level != null) {
            WindState.INSTANCE.setSpeedFloor(0.0F);
        }
    }

    @Override
    public boolean started() {
        return active;
    }

    @Override
    public boolean forceStart() {
        if (active || level == null) return false;
        requested = true;
        return true;
    }

    @Override
    public void forceEnd() {
        requested = false;
        calmTicks = CALM_TOLERANCE + 1;
    }

    @Override
    public void decode(CompoundTag tag) {
        active = tag.getBoolean("Started");
        calmTicks = tag.getInt("CalmTicks");
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putBoolean("Started", active);
        tag.putInt("CalmTicks", calmTicks);
    }

    @Override
    public ResourceKey<WindyDayGameEvent> key() {
        return KEY;
    }

    @Override
    public boolean isNonEnvEvent() {
        return false; // 环境事件：不阻断自然刷怪
    }
}
