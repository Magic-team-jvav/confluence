package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.item.BaseBullet;

import java.util.Objects;

/// 子弹族外观与运动字段的当前格式编解码器。
///
/// <p>战斗伤害、击退、暴击、穿透预算和已命中 UUID 仍由 {@link ProjectileCombatState} 管理；本类只
/// 保存基础子弹的颜色、真实弹药、方块命中计数和加速度，以及重力变体额外的重力字段。
/// 将两类状态分根保存，可以让基础实体保持稳定，同时要求重力变体完整提供自身字段。</p>
///
/// <p>所有字段都必须来自带版本号的当前根节点。1.20 不读取旧扁平字段；类型错误、非有限浮点数、
/// 空物品或不合法的行为物品都会抛出英文诊断，由实体统一转入安全失效状态。</p>
final class BulletRuntimeState {
    static final String BASE_ROOT_TAG = "ConfluenceBulletRuntime";
    static final String CUSTOM_ROOT_TAG = "ConfluenceCustomBulletRuntime";

    private static final int CURRENT_FORMAT_VERSION = 2;
    private static final String VERSION_TAG = "Version";
    private static final String COLOR_ID_TAG = "ColorId";
    private static final String BULLET_TAG = "Bullet";
    private static final String HIT_BLOCK_TIMES_TAG = "HitBlockTimes";
    private static final String ACCELERATION_POWER_TAG = "AccelerationPower";
    private static final String EFFECT_STATE_TAG = "EffectState";
    private static final String IGNORE_BLOCK_COLLISION_TAG = "IgnoreBlockCollision";
    private static final String GRAVITY_TAG = "Gravity";

    private BulletRuntimeState() {}

    /// 写出基础子弹完整状态；调用方必须传入实体当前的行为物品。
    static void writeBase(
            CompoundTag entityTag,
            String colorId,
            ItemStack bullet,
            int hitBlockTimes,
            double accelerationPower,
            int effectState,
            boolean ignoreBlockCollision
    ) {
        Objects.requireNonNull(entityTag, "Entity tag must not be null");
        validateColorId(colorId);
        validateBehaviorBullet(bullet);
        validateHitBlockTimes(hitBlockTimes);
        validateFinite(accelerationPower, "Bullet acceleration power");
        validateEffectState(effectState);

        CompoundTag runtimeTag = new CompoundTag();
        runtimeTag.putInt(VERSION_TAG, CURRENT_FORMAT_VERSION);
        runtimeTag.putString(COLOR_ID_TAG, colorId);
        runtimeTag.put(BULLET_TAG, bullet.copyWithCount(1).save(new CompoundTag()));
        runtimeTag.putInt(HIT_BLOCK_TIMES_TAG, hitBlockTimes);
        runtimeTag.putDouble(ACCELERATION_POWER_TAG, accelerationPower);
        runtimeTag.putInt(EFFECT_STATE_TAG, effectState);
        runtimeTag.putBoolean(IGNORE_BLOCK_COLLISION_TAG, ignoreBlockCollision);
        entityTag.put(BASE_ROOT_TAG, runtimeTag);
    }

    /// 严格读取基础状态，不在失败时返回部分字段。
    static BaseState readBase(CompoundTag entityTag) {
        Objects.requireNonNull(entityTag, "Entity tag must not be null");
        CompoundTag runtimeTag = requireCurrentRoot(entityTag, BASE_ROOT_TAG);
        requireTag(runtimeTag, COLOR_ID_TAG, Tag.TAG_STRING);
        requireTag(runtimeTag, BULLET_TAG, Tag.TAG_COMPOUND);
        requireTag(runtimeTag, HIT_BLOCK_TIMES_TAG, Tag.TAG_INT);
        requireTag(runtimeTag, ACCELERATION_POWER_TAG, Tag.TAG_DOUBLE);
        requireTag(runtimeTag, EFFECT_STATE_TAG, Tag.TAG_INT);
        requireTag(runtimeTag, IGNORE_BLOCK_COLLISION_TAG, Tag.TAG_BYTE);

        String colorId = runtimeTag.getString(COLOR_ID_TAG);
        ItemStack bullet = ItemStack.of(runtimeTag.getCompound(BULLET_TAG));
        int hitBlockTimes = runtimeTag.getInt(HIT_BLOCK_TIMES_TAG);
        double accelerationPower = runtimeTag.getDouble(ACCELERATION_POWER_TAG);
        int effectState = runtimeTag.getInt(EFFECT_STATE_TAG);
        boolean ignoreBlockCollision = runtimeTag.getBoolean(IGNORE_BLOCK_COLLISION_TAG);
        validateColorId(colorId);
        validateBehaviorBullet(bullet);
        validateHitBlockTimes(hitBlockTimes);
        validateFinite(accelerationPower, "Bullet acceleration power");
        validateEffectState(effectState);
        return new BaseState(colorId, bullet.copyWithCount(1), hitBlockTimes, accelerationPower,
                effectState, ignoreBlockCollision);
    }

    /// 写出重力变体额外状态；真实弹药由基础状态统一保存。
    static void writeCustom(CompoundTag entityTag, float gravity, ItemStack bullet) {
        Objects.requireNonNull(entityTag, "Entity tag must not be null");
        validateFinite(gravity, "Custom bullet gravity");
        validateVisualBullet(bullet);

        CompoundTag runtimeTag = new CompoundTag();
        runtimeTag.putInt(VERSION_TAG, CURRENT_FORMAT_VERSION);
        runtimeTag.putFloat(GRAVITY_TAG, gravity);
        entityTag.put(CUSTOM_ROOT_TAG, runtimeTag);
    }

    /// 严格读取重力字段；弹药本身从基础状态恢复。
    static CustomState readCustom(CompoundTag entityTag) {
        Objects.requireNonNull(entityTag, "Entity tag must not be null");
        CompoundTag runtimeTag = requireCurrentRoot(entityTag, CUSTOM_ROOT_TAG);
        requireTag(runtimeTag, GRAVITY_TAG, Tag.TAG_FLOAT);

        float gravity = runtimeTag.getFloat(GRAVITY_TAG);
        validateFinite(gravity, "Custom bullet gravity");
        return new CustomState(gravity);
    }

    /// 将任意运行时异常收敛为 ASCII 英文原因，避免本地化文本进入开发者日志。
    static String englishReason(RuntimeException exception, String fallback) {
        Objects.requireNonNull(exception, "Runtime exception must not be null");
        Objects.requireNonNull(fallback, "Fallback reason must not be null");
        String message = exception.getMessage();
        if (message == null || message.isBlank() || !message.chars().allMatch(character -> character < 128)) {
            return fallback;
        }
        return message;
    }

    private static CompoundTag requireCurrentRoot(CompoundTag entityTag, String rootTag) {
        requireTag(entityTag, rootTag, Tag.TAG_COMPOUND);
        CompoundTag runtimeTag = entityTag.getCompound(rootTag);
        requireTag(runtimeTag, VERSION_TAG, Tag.TAG_INT);
        int version = runtimeTag.getInt(VERSION_TAG);
        if (version != CURRENT_FORMAT_VERSION) {
            throw new IllegalArgumentException("Unsupported bullet runtime state version: " + version);
        }
        return runtimeTag;
    }

    private static void requireTag(CompoundTag tag, String key, int expectedType) {
        if (!tag.contains(key, expectedType)) {
            throw new IllegalArgumentException("Missing or invalid bullet runtime state field: " + key);
        }
    }

    private static void validateColorId(String colorId) {
        Objects.requireNonNull(colorId, "Bullet color ID must not be null");
    }

    private static void validateBehaviorBullet(ItemStack bullet) {
        validateVisualBullet(bullet);
        if (!(bullet.getItem() instanceof BaseBullet)) {
            throw new IllegalArgumentException("Bullet behavior item must be a BaseBullet");
        }
    }

    private static void validateVisualBullet(ItemStack visualBullet) {
        Objects.requireNonNull(visualBullet, "Bullet item must not be null");
        if (visualBullet.isEmpty()) {
            throw new IllegalArgumentException("Bullet item must not be empty");
        }
    }

    private static void validateHitBlockTimes(int hitBlockTimes) {
        if (hitBlockTimes < 0) {
            throw new IllegalArgumentException("Bullet block-hit count must be non-negative");
        }
    }

    private static void validateEffectState(int effectState) {
        if (effectState < 0) {
            throw new IllegalArgumentException("Bullet effect state must be non-negative");
        }
    }

    private static void validateFinite(double value, String fieldName) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(fieldName + " must be finite");
        }
    }

    /// 基础子弹一次性验证完成后的不可变值。
    record BaseState(
            String colorId,
            ItemStack bullet,
            int hitBlockTimes,
            double accelerationPower,
            int effectState,
            boolean ignoreBlockCollision
    ) {}

    /// 重力变体一次性验证完成后的不可变值。
    record CustomState(float gravity) {}
}
