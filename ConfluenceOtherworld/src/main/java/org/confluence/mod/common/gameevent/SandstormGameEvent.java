package org.confluence.mod.common.gameevent;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.wind.WindMath;
import org.confluence.mod.common.wind.WindState;
import org.confluence.mod.util.OverworldUtils;

/// 沙尘暴。
///
/// 与原作一致：**风速达到阈值才可能出现**，且事件期间**恒速恒向**（风速被强制维持在高位、
/// 通过 `directionBlend = 0` 关掉温度噪声造成的方向偏差，让全场同向）。
///
/// 它和 {@link BlizzardGameEvent} 是目前仅有的两个会**推实体**的天气事件（实现 {@link WindPushedEvent}）——
/// 风系统本身在任何风速下都不影响实体。
public enum SandstormGameEvent implements GameEvent, WindPushedEvent {
    INSTANCE;

    public static final ResourceKey<SandstormGameEvent> KEY = GameEvent.createKey(Confluence.asResource("sandstorm"));
    /// 非玩家实体的推力系数。
    public static final float COUPLING = 0.05F;

    private ServerLevel level;
    private boolean active;
    private boolean requested;
    private int remainingTicks;
    private float lockedAngle;

    @Override
    public void open(MinecraftServer server) {
        level = OverworldUtils.getLevel(server);
        requested = false;
    }

    @Override
    public void close(MinecraftServer server) {
        releaseWind();
        level = null;
        requested = false;
    }

    @Override
    public void tick() {
        if (active) remainingTicks = Math.max(0, remainingTicks - (strongWind() ? 1 : 15));
    }

    /// 事件激活期间一律视为强风；未激活时才看是否够格触发（与大风天共用 20 mph 那条线）。
    private boolean strongWind() {
        if (level == null) return false;
        return active || WindState.INSTANCE.instantSpeed(level.getGameTime()) >= WindMath.WINDY_DAY_MIN;
    }

    @Override
    public boolean canStart() {
        if (level == null) return false;
        return requested || (strongWind() && level.random.nextInt(KillBoard.INSTANCE.getGamePhase().isHardmode() ? 14400 : 21600) == 0);
    }

    @Override
    public boolean canEnd() {
        return remainingTicks == 0;
    }

    @Override
    public void onStart() {
        active = true;
        requested = false;
        remainingTicks = 9600 + level.random.nextInt(19201);

        // 恒速恒向：锁定风速与方向，并关掉温度噪声造成的方向偏差
        WindState state = WindState.INSTANCE;
        lockedAngle = state.angle();
        float forcedSpeed = (float) (double) CommonConfigs.WIND_SANDSTORM_FORCED_SPEED.get();
        state.override(forcedSpeed, lockedAngle, remainingTicks);
        state.setDirectionBlend(0.0F);
    }

    @Override
    public void onEnd() {
        active = false;
        releaseWind();
    }

    private void releaseWind() {
        if (level == null) return;
        WindState state = WindState.INSTANCE;
        state.setDirectionBlend(1.0F);
        state.clearOverride();
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
        remainingTicks = 0;
    }

    @Override
    public void decode(CompoundTag tag) {
        active = tag.getBoolean("Started");
        remainingTicks = Math.max(0, tag.getInt("RemainingTicks"));
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putBoolean("Started", active);
        tag.putInt("RemainingTicks", remainingTicks);
    }

    @Override
    public ResourceKey<SandstormGameEvent> key() {
        return KEY;
    }

    @Override
    public boolean isNonEnvEvent() {
        return false;
    }

    // ===================== WindPushedEvent =====================

    @Override
    public boolean inWindZone(Level level, BlockPos pos) {
        return OverworldUtils.isDesert(level.getBiome(pos))
                && pos.getY() >= OverworldUtils.getSurfaceY()
                && level.canSeeSky(pos);
    }

    @Override
    public float couplingFor(Entity entity) {
        return COUPLING;
    }

    @Override
    public boolean windPushEnabled() {
        return CommonConfigs.SANDSTORM_PUSHES_ENTITIES.get();
    }
}
