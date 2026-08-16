package org.confluence.mod.common.entity.projectile.strip;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

/// 条纹弹幕头段/身体段共用的严格运行状态。
///
/// <p>起点决定头段何时达到最大距离，分段进度决定下一个身体段的生成间距，年龄与头部标志决定
/// 身体寿命和碰撞分支；四者都属于不可缺失的玩法状态。当前格式使用直接双精度坐标，避免编解码
/// 失败被静默忽略。</p>
///
/// <p>身体段年龄按具体变体的销毁边界校验，头段只要求年龄非负。字段损坏会抛出英文
/// 原因，由 {@link StripedProjectile} 清空战斗快照并安全销毁。</p>
final class StripedProjectileRuntime {
    static final String ROOT_TAG = "ConfluenceStripedRuntime";

    private static final int CURRENT_FORMAT_VERSION = 1;
    private static final String VERSION_TAG = "Version";
    private static final String START_X_TAG = "StartX";
    private static final String START_Y_TAG = "StartY";
    private static final String START_Z_TAG = "StartZ";
    private static final String SPACING_PROGRESS_TAG = "SpacingProgress";
    private static final String AGE_TAG = "Age";
    private static final String IS_HEAD_TAG = "IsHead";
    private static final double MINIMUM_SPACING_PROGRESS = -0.5;

    private StripedProjectileRuntime() {}

    /// 写出经过变体边界校验的完整当前状态。
    static void write(
            CompoundTag entityTag,
            Vec3 startPos,
            double spacingProgress,
            int age,
            boolean head,
            int maximumBodyAge,
            double maximumSpacingProgress
    ) {
        Objects.requireNonNull(entityTag, "Entity tag must not be null");
        validate(startPos, spacingProgress, age, head, maximumBodyAge, maximumSpacingProgress);

        CompoundTag runtimeTag = new CompoundTag();
        runtimeTag.putInt(VERSION_TAG, CURRENT_FORMAT_VERSION);
        runtimeTag.putDouble(START_X_TAG, startPos.x);
        runtimeTag.putDouble(START_Y_TAG, startPos.y);
        runtimeTag.putDouble(START_Z_TAG, startPos.z);
        runtimeTag.putDouble(SPACING_PROGRESS_TAG, spacingProgress);
        runtimeTag.putInt(AGE_TAG, age);
        runtimeTag.putBoolean(IS_HEAD_TAG, head);
        entityTag.put(ROOT_TAG, runtimeTag);
    }

    /// 先完整验证临时值，再返回一次性应用的不可变状态。
    static State read(CompoundTag entityTag, int maximumBodyAge, double maximumSpacingProgress) {
        Objects.requireNonNull(entityTag, "Entity tag must not be null");
        requireTag(entityTag, ROOT_TAG, Tag.TAG_COMPOUND);
        CompoundTag runtimeTag = entityTag.getCompound(ROOT_TAG);
        requireTag(runtimeTag, VERSION_TAG, Tag.TAG_INT);
        int version = runtimeTag.getInt(VERSION_TAG);
        if (version != CURRENT_FORMAT_VERSION) {
            throw new IllegalArgumentException("Unsupported striped projectile runtime state version: " + version);
        }
        requireTag(runtimeTag, START_X_TAG, Tag.TAG_DOUBLE);
        requireTag(runtimeTag, START_Y_TAG, Tag.TAG_DOUBLE);
        requireTag(runtimeTag, START_Z_TAG, Tag.TAG_DOUBLE);
        requireTag(runtimeTag, SPACING_PROGRESS_TAG, Tag.TAG_DOUBLE);
        requireTag(runtimeTag, AGE_TAG, Tag.TAG_INT);
        requireTag(runtimeTag, IS_HEAD_TAG, Tag.TAG_BYTE);

        Vec3 startPos = new Vec3(
                runtimeTag.getDouble(START_X_TAG),
                runtimeTag.getDouble(START_Y_TAG),
                runtimeTag.getDouble(START_Z_TAG)
        );
        double spacingProgress = runtimeTag.getDouble(SPACING_PROGRESS_TAG);
        int age = runtimeTag.getInt(AGE_TAG);
        boolean head = runtimeTag.getBoolean(IS_HEAD_TAG);
        validate(startPos, spacingProgress, age, head, maximumBodyAge, maximumSpacingProgress);
        return new State(startPos, spacingProgress, age, head);
    }

    /// 将异常消息收敛为 ASCII 英文开发者诊断。
    static String englishReason(RuntimeException exception) {
        Objects.requireNonNull(exception, "Runtime exception must not be null");
        String message = exception.getMessage();
        if (message == null || message.isBlank() || !message.chars().allMatch(character -> character < 128)) {
            return "Malformed striped projectile runtime state";
        }
        return message;
    }

    private static void requireTag(CompoundTag tag, String key, int expectedType) {
        if (!tag.contains(key, expectedType)) {
            throw new IllegalArgumentException(
                    "Missing or invalid striped projectile runtime state field: " + key);
        }
    }

    private static void validate(
            Vec3 startPos,
            double spacingProgress,
            int age,
            boolean head,
            int maximumBodyAge,
            double maximumSpacingProgress
    ) {
        Objects.requireNonNull(startPos, "Striped projectile start position must not be null");
        if (maximumBodyAge < 0) {
            throw new IllegalArgumentException("Maximum striped body age must not be negative");
        }
        if (!Double.isFinite(maximumSpacingProgress) || maximumSpacingProgress < 0.0) {
            throw new IllegalArgumentException("Maximum striped spacing progress must be finite and non-negative");
        }
        if (!Double.isFinite(startPos.x) || !Double.isFinite(startPos.y) || !Double.isFinite(startPos.z)) {
            throw new IllegalArgumentException("Striped projectile start position must be finite");
        }
        if (!Double.isFinite(spacingProgress)
                || spacingProgress < MINIMUM_SPACING_PROGRESS
                || spacingProgress > maximumSpacingProgress) {
            throw new IllegalArgumentException("Striped projectile spacing progress is outside the supported range");
        }
        if (age < 0 || !head && age > maximumBodyAge) {
            throw new IllegalArgumentException("Striped projectile age is outside the supported range");
        }
    }

    /// 完整校验后交给实体原子应用的值。
    record State(Vec3 startPos, double spacingProgress, int age, boolean head) {}
}
