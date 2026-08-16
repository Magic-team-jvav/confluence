package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Objects;

/// 可回收投掷物独有运行状态的严格编解码器。
///
/// <p>战斗快照、成功命中 UUID 与所有者由共享弹幕状态保存；这里只保存投掷物运动和穿透过程
/// 中仍会变化的字段。1.20 重写不读取旧的扁平 {@code Item}，缺字段、类型错误或数值越界都会
/// 让实体通过共享失效通道安全销毁。</p>
final class ThrowableProjectileRuntime {
    static final String ROOT_TAG = "ConfluenceThrowableRuntime";

    private static final String VERSION_TAG = "Version";
    private static final String DROP_SELF_TAG = "DropSelf";
    private static final String FLY_TICKS_TAG = "FlyTicks";
    private static final String PENETRATION_PHASE_TAG = "PenetrationPhase";
    private static final String CURRENT_DAMAGE_TAG = "CurrentDamage";
    private static final int CURRENT_VERSION = 1;

    private ThrowableProjectileRuntime() {}

    /// 将全部字段先校验、再作为单个当前格式根节点写出。
    static void write(
            CompoundTag entityTag,
            boolean dropSelf,
            int flyTicks,
            int penetrationPhase,
            float currentDamage,
            float initialDamage,
            int maximumPenetrationPhase
    ) {
        Objects.requireNonNull(entityTag, "Entity tag must not be null");
        validate(flyTicks, penetrationPhase, currentDamage, initialDamage, maximumPenetrationPhase);

        CompoundTag runtimeTag = new CompoundTag();
        runtimeTag.putInt(VERSION_TAG, CURRENT_VERSION);
        runtimeTag.putBoolean(DROP_SELF_TAG, dropSelf);
        runtimeTag.putInt(FLY_TICKS_TAG, flyTicks);
        runtimeTag.putInt(PENETRATION_PHASE_TAG, penetrationPhase);
        runtimeTag.putFloat(CURRENT_DAMAGE_TAG, currentDamage);
        entityTag.put(ROOT_TAG, runtimeTag);
    }

    /// 原子读取当前格式；调用者只在本方法完整成功后安装返回状态。
    static State read(CompoundTag entityTag, float initialDamage, int maximumPenetrationPhase) {
        Objects.requireNonNull(entityTag, "Entity tag must not be null");
        requireTag(entityTag, ROOT_TAG, Tag.TAG_COMPOUND);
        CompoundTag runtimeTag = entityTag.getCompound(ROOT_TAG);
        requireTag(runtimeTag, VERSION_TAG, Tag.TAG_INT);
        if (runtimeTag.getInt(VERSION_TAG) != CURRENT_VERSION) {
            throw new IllegalArgumentException("Unsupported throwable runtime state version");
        }
        requireTag(runtimeTag, DROP_SELF_TAG, Tag.TAG_BYTE);
        requireTag(runtimeTag, FLY_TICKS_TAG, Tag.TAG_INT);
        requireTag(runtimeTag, PENETRATION_PHASE_TAG, Tag.TAG_INT);
        requireTag(runtimeTag, CURRENT_DAMAGE_TAG, Tag.TAG_FLOAT);

        boolean dropSelf = runtimeTag.getBoolean(DROP_SELF_TAG);
        int flyTicks = runtimeTag.getInt(FLY_TICKS_TAG);
        int penetrationPhase = runtimeTag.getInt(PENETRATION_PHASE_TAG);
        float currentDamage = runtimeTag.getFloat(CURRENT_DAMAGE_TAG);
        validate(flyTicks, penetrationPhase, currentDamage, initialDamage, maximumPenetrationPhase);
        return new State(dropSelf, flyTicks, penetrationPhase, currentDamage);
    }

    private static void validate(
            int flyTicks,
            int penetrationPhase,
            float currentDamage,
            float initialDamage,
            int maximumPenetrationPhase
    ) {
        if (flyTicks < 0) {
            throw new IllegalArgumentException("Throwable fly delay must be non-negative");
        }
        if (maximumPenetrationPhase < 0) {
            throw new IllegalArgumentException("Maximum throwable penetration phase must be non-negative");
        }
        if (penetrationPhase < 0 || penetrationPhase > maximumPenetrationPhase) {
            throw new IllegalArgumentException(
                    "Throwable penetration phase is outside the supported subtype range");
        }
        if (!Float.isFinite(initialDamage) || initialDamage < 0.0F) {
            throw new IllegalArgumentException("Throwable initial damage must be finite and non-negative");
        }
        if (!Float.isFinite(currentDamage) || currentDamage < 0.0F || currentDamage > initialDamage) {
            throw new IllegalArgumentException(
                    "Throwable current damage must be finite and within [0, initial damage]");
        }
    }

    private static void requireTag(CompoundTag tag, String key, int expectedType) {
        if (!tag.contains(key, expectedType)) {
            throw new IllegalArgumentException("Missing or invalid throwable runtime state field: " + key);
        }
    }

    /// 一次完整验证后的不可变恢复结果。
    record State(boolean dropSelf, int flyTicks, int penetrationPhase, float currentDamage) {}
}
