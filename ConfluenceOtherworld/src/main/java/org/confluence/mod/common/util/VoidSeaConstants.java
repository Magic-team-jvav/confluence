package org.confluence.mod.common.util;

public class VoidSeaConstants {
    /// 虚空海功能标识。
    public static final String ID = "void_sea";

    // 潮汐
    /// 潮位上限（单位：格）。
    public static final float MAX_HEIGHT = 53.0F;
    /// 潮位下限（单位：格）。
    public static final float MIN_HEIGHT = -64.0F;
    /// 完整潮汐周期（单位：刻）。
    public static final int TIDE_PERIOD = 10000;
    /// 潮汐正弦波角速度倍率。
    public static final float TIDE_ANGULAR_MULTIPLIER = 2.0F;
    /// 潮位缓存的初始高度（单位：格）。
    public static final float INITIAL_HEIGHT = 0.0F;

    // 移动
    /// 非游泳状态下的移动速度倍率。
    public static final float MOVEMENT_SPEED = 0.9F;
    /// 游泳状态下的移动速度倍率。
    public static final float SWIMMING_SPEED = 2.0F;
    /// 玩家主动垂直游泳速度（单位：格/刻）。
    public static final float VERTICAL_MOVEMENT_SPEED = 0.04F;
    /// 虚空海内每刻保留的水平速度比例。
    public static final float HORIZONTAL_MOVEMENT_RESISTANCE = 0.86F;
    /// 潮位变化带动实体的海面范围（单位：格）。
    public static final float TIDE_SURFACE_RANGE = 3.0F;
    /// 跃出加速可触发的海面下方范围（单位：格）。
    public static final float SURFACE_EXIT_RANGE = 5.0F;
    /// 跃出海面期间累计增加的速度（单位：格/刻）。
    public static final float SURFACE_EXIT_BOOST = 3.18F;
    /// 跃出加速允许的最小上仰角（单位：度）。
    public static final float SURFACE_EXIT_MIN_ANGLE = 15.0F;
    /// 跃出加速允许的最大上仰角（单位：度）。
    public static final float SURFACE_EXIT_MAX_ANGLE = 85.0F;
    /// 跃出总速度增量的分段刻数。
    public static final int SURFACE_EXIT_ACCELERATION_TICKS = 10;
    /// 每刻施加的跃出加速度（单位：格/刻²）。
    public static final float SURFACE_EXIT_ACCELERATION = SURFACE_EXIT_BOOST / SURFACE_EXIT_ACCELERATION_TICKS;

    // 虚空侵蚀
    /// 侵蚀伤害高度相对维度最低建筑高度的偏移（单位：格）。
    public static final float VOID_EROSION_DAMAGE_HEIGHT_OFFSET = -64.0F;
}
