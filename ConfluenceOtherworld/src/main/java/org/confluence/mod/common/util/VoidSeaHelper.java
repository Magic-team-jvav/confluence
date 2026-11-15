package org.confluence.mod.common.util;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.init.ModEffects;
import org.jetbrains.annotations.Nullable;

import static org.confluence.mod.common.util.VoidSeaConstants.*;

/// 虚空海
public class VoidSeaHelper {
    // 潮位缓存
    private static float height = INITIAL_HEIGHT;
    private static float heightO = INITIAL_HEIGHT;

    public static void tick(Level level) {
        heightO = height;
        height = calculateHeight(level);
    }

    public static float calculateHeight(Level level) {
        return (MIN_HEIGHT + MAX_HEIGHT) / TIDE_ANGULAR_MULTIPLIER
                + (MAX_HEIGHT - MIN_HEIGHT) / TIDE_ANGULAR_MULTIPLIER
                * (float) Math.sin(TIDE_ANGULAR_MULTIPLIER * Math.PI * level.getGameTime() / TIDE_PERIOD);
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

    public static boolean isTrigger(LivingEntity entity) {
        return isEnd(entity.level()) && isSeaBelow(entity) && isDimensionalOverlapEffect(entity);
    }

    public static boolean isTrigger(LivingEntity entity, float delta) {
        return isEnd(entity.level()) && isSeaBelow(entity, delta);
    }

    public static boolean isSeaBelow(LivingEntity entity) {
        return entity.getY() < getHeight();
    }

    public static boolean isSeaBelow(LivingEntity entity, float delta) {
        return entity.getY() < getHeight(delta);
    }

    public static boolean isVoidErosionDeltaDamage(LivingEntity entity) {
        return entity.getY() < getVoidErosionDeltaDamageHeight(entity);
    }

    public static float getVoidErosionDeltaDamageHeight(LivingEntity entity) {
        return entity.level().getMinBuildHeight() + VOID_EROSION_DAMAGE_HEIGHT_OFFSET + getAttribute(entity);
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

    public static boolean isEnd(Level level) {
        return level.dimension() == Level.END;
    }
}
