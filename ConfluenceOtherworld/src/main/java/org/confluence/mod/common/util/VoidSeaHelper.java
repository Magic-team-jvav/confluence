package org.confluence.mod.common.util;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.init.ModEffects;
import org.jetbrains.annotations.Nullable;

/**
 * 虚空海
 */
public class VoidSeaHelper {
    public static final String ID = "void_sea";
    /**
     * 潮位上限（单位：格）。
     */
    public static final float MAX_HEIGHT = 53.0f;
    /**
     * 潮位下限（单位：格）。
     */
    public static final float MIN_HEIGHT = -64.0f;
    /**
     * 潮汐周期（单位：刻）。
     */
    public static final int TIDE_PERIOD = 10000;
    /**
     * 非游泳移动加速度。
     */
    public static final float MOVEMENT_SPEED = 0.9f;
    /**
     * 游泳移动倍率。
     */
    public static final float SWIMMING_SPEED = 2.0F;
    /**
     * 垂直游泳速度（单位：格/刻）。
     */
    public static final float VERTICAL_MOVEMENT_SPEED = 0.04F;
    /**
     * 水平速度保留比例。
     */
    public static final float HORIZONTAL_MOVEMENT_RESISTANCE = 0.86F;
    /**
     * 潮汐影响海面范围（单位：格）。
     */
    public static final float TIDE_SURFACE_RANGE = 3.0F;
    /**
     * 跃出加速触发范围（单位：格）。
     */
    public static final float SURFACE_EXIT_RANGE = 5.0F;
    /**
     * 跃出海面的总速度增量（单位：格/刻）。
     */
    public static final float SURFACE_EXIT_BOOST = 3.18F;
    /**
     * 跃出海面的最小上仰角（单位：度）。
     */
    public static final float SURFACE_EXIT_MIN_ANGLE = 15.0F;
    /**
     * 跃出海面的最大上仰角（单位：度）。
     */
    public static final float SURFACE_EXIT_MAX_ANGLE = 85.0F;
    /**
     * 跃出海面的加速分段数（用于换算单刻加速度）。
     */
    public static final int SURFACE_EXIT_ACCELERATION_TICKS = 10;
    /**
     * 跃出海面的单刻加速度（单位：格/刻²）。
     */
    public static final float SURFACE_EXIT_ACCELERATION = SURFACE_EXIT_BOOST / SURFACE_EXIT_ACCELERATION_TICKS;
    // 当前潮位缓存
    private static float height = 0;
    private static float heightO = 0;

    public static void tick(Level level) {
        heightO = height;
        height = calculateHeight(level);
    }

    public static float calculateHeight(Level level) {
        return (MIN_HEIGHT + MAX_HEIGHT) / 2.0f
                + (MAX_HEIGHT - MIN_HEIGHT) / 2.0f
                * (float) Math.sin(2f * Math.PI * level.getGameTime() / TIDE_PERIOD);
    }

    public static float getHeight() {
        return height;
    }

    public static float getHeightO() {
        return heightO;
    }

    public static float getHeight(float delta) {
        return Mth.lerp(delta, getHeightO(), getHeight());
    }

    public static boolean isVoidErosionDeltaDamage(LivingEntity entity) {
        return entity.getY() < getVoidErosionDeltaDamageHeight(entity);
    }

    public static float getVoidErosionDeltaDamageHeight(LivingEntity entity) {
        return entity.level().getMinBuildHeight() - 64 + getAttribute(entity);
    }

    public static float getAttribute(LivingEntity entity) {
        if (!entity.getAttributes().hasAttribute(ConfluenceMagicLib.VOID_EROSION_DELTA)) {
            return (float) ConfluenceMagicLib.VOID_EROSION_DELTA.get().getDefaultValue();
        }
        return (float) entity.getAttributeValue(ConfluenceMagicLib.VOID_EROSION_DELTA);
    }

    public static int getDimensionalOverlapLevel(LivingEntity entity) {
        MobEffectInstance effect = getDimensionalOverlapEffect(entity);
        if (effect == null) {
            return -1;
        }
        return effect.getAmplifier();
    }

    @Nullable
    public static MobEffectInstance getDimensionalOverlapEffect(LivingEntity entity) {
        return entity.getEffect(ModEffects.DIMENSIONAL_OVERLAP);
    }

    public static boolean isDimensionalOverlapEffect(LivingEntity entity) {
        return entity.hasEffect(ModEffects.DIMENSIONAL_OVERLAP);
    }
}
