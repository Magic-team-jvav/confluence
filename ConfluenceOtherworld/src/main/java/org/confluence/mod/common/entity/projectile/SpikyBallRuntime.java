package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/// 尖刺球族共用的严格运行状态。
///
/// <p>这里只提取两种弹幕真正相同的持久化职责：剩余寿命所依赖的实体年龄，以及普通尖刺球已经
/// 计入穿透预算的目标 UUID。移动阻尼、重力、伤害来源和命中伤害仍由各实体自行实现，避免为了
/// 复用存档格式而合并不同的战斗行为。</p>
///
/// <p>1.20 侧只接受带版本号的当前格式。缺少根节点、字段类型错误、年龄越界、目标过多或 UUID
/// 损坏时，状态会记录英文原因，并要求实体在下一次服务端 tick 安全销毁；不会读取旧的扁平
/// {@code Age}。</p>
final class SpikyBallRuntime {
    static final String ROOT_TAG = "ConfluenceSpikyBallRuntime";

    private static final int CURRENT_FORMAT_VERSION = 1;
    private static final String VERSION_TAG = "Version";
    private static final String AGE_TAG = "Age";
    private static final String HIT_TARGETS_TAG = "HitTargets";

    private final Set<UUID> hitTargets = new HashSet<>();
    private @Nullable String invalidReason;

    /// 记录一个唯一目标；返回 {@code true} 表示该 UUID 首次进入预算。
    boolean recordHitTarget(UUID targetUuid) {
        return hitTargets.add(Objects.requireNonNull(targetUuid, "Hit target UUID must not be null"));
    }

    /// 返回当前已经消耗的唯一目标数量。
    int hitTargetCount() {
        return hitTargets.size();
    }

    /// 写出当前格式。最大年龄和目标数量由具体变体传入，以免共享格式改变两种弹幕各自的边界。
    void writeTo(CompoundTag entityTag, int age, int maximumAge, int maximumHitTargets) {
        Objects.requireNonNull(entityTag, "Entity tag must not be null");
        validateLimits(maximumAge, maximumHitTargets);
        if (invalidReason != null) {
            return;
        }
        validateAge(age, maximumAge);
        validateHitTargetCount(hitTargets.size(), maximumHitTargets, "save");

        CompoundTag runtimeTag = new CompoundTag();
        runtimeTag.putInt(VERSION_TAG, CURRENT_FORMAT_VERSION);
        runtimeTag.putInt(AGE_TAG, age);
        ListTag targetsTag = new ListTag();
        hitTargets.stream().sorted().forEach(uuid -> targetsTag.add(NbtUtils.createUUID(uuid)));
        runtimeTag.put(HIT_TARGETS_TAG, targetsTag);
        entityTag.put(ROOT_TAG, runtimeTag);
    }

    /// 原子读取当前格式并返回年龄。失败时清空目标集合并返回零，实体随后会由
    /// {@link #discardIfInvalid(Entity)} 销毁，零值不会进入正常战斗逻辑。
    int readFrom(CompoundTag entityTag, int maximumAge, int maximumHitTargets) {
        Objects.requireNonNull(entityTag, "Entity tag must not be null");
        validateLimits(maximumAge, maximumHitTargets);
        try {
            requireTag(entityTag, ROOT_TAG, Tag.TAG_COMPOUND);
            CompoundTag runtimeTag = entityTag.getCompound(ROOT_TAG);
            requireTag(runtimeTag, VERSION_TAG, Tag.TAG_INT);
            int version = runtimeTag.getInt(VERSION_TAG);
            if (version != CURRENT_FORMAT_VERSION) {
                throw new IllegalArgumentException("Unsupported spiky ball runtime state version: " + version);
            }
            requireTag(runtimeTag, AGE_TAG, Tag.TAG_INT);
            requireTag(runtimeTag, HIT_TARGETS_TAG, Tag.TAG_LIST);

            int loadedAge = runtimeTag.getInt(AGE_TAG);
            validateAge(loadedAge, maximumAge);
            Set<UUID> loadedTargets = readHitTargets(runtimeTag, maximumHitTargets);

            hitTargets.clear();
            hitTargets.addAll(loadedTargets);
            invalidReason = null;
            return loadedAge;
        } catch (RuntimeException exception) {
            hitTargets.clear();
            invalidReason = englishReason(exception);
            return 0;
        }
    }

    /// 损坏状态只在服务端记录英文原因并销毁，防止继续移动或造成伤害。
    boolean discardIfInvalid(Entity projectile) {
        Objects.requireNonNull(projectile, "Projectile must not be null");
        if (invalidReason == null || projectile.level().isClientSide) {
            return false;
        }
        Confluence.LOGGER.error("Discarding invalid spiky ball projectile {}: {}",
                projectile.getStringUUID(), invalidReason);
        projectile.discard();
        return true;
    }

    private static Set<UUID> readHitTargets(CompoundTag runtimeTag, int maximumHitTargets) {
        Tag rawTargets = runtimeTag.get(HIT_TARGETS_TAG);
        if (!(rawTargets instanceof ListTag targetsTag)
                || !targetsTag.isEmpty() && targetsTag.getElementType() != Tag.TAG_INT_ARRAY) {
            throw new IllegalArgumentException("Spiky ball hit targets must be a UUID list");
        }
        validateHitTargetCount(targetsTag.size(), maximumHitTargets, "load");

        Set<UUID> loadedTargets = new HashSet<>();
        for (Tag targetTag : targetsTag) {
            UUID targetUuid = NbtUtils.loadUUID(targetTag);
            if (!loadedTargets.add(targetUuid)) {
                throw new IllegalArgumentException("Duplicate spiky ball hit target UUID");
            }
        }
        return loadedTargets;
    }

    private static void requireTag(CompoundTag tag, String key, int expectedType) {
        if (!tag.contains(key, expectedType)) {
            throw new IllegalArgumentException("Missing or invalid spiky ball runtime state field: " + key);
        }
    }

    private static void validateLimits(int maximumAge, int maximumHitTargets) {
        if (maximumAge < 0) {
            throw new IllegalArgumentException("Maximum spiky ball age must not be negative");
        }
        if (maximumHitTargets < 0) {
            throw new IllegalArgumentException("Maximum spiky ball hit target count must not be negative");
        }
    }

    private static void validateAge(int age, int maximumAge) {
        if (age < 0 || age > maximumAge) {
            throw new IllegalArgumentException("Spiky ball age must be within [0, " + maximumAge + "]");
        }
    }

    private static void validateHitTargetCount(int count, int maximumHitTargets, String operation) {
        if (count > maximumHitTargets) {
            throw new IllegalArgumentException("Spiky ball hit target count exceeds the " + operation + " limit");
        }
    }

    private static String englishReason(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank() || !message.chars().allMatch(character -> character < 128)) {
            return "Malformed spiky ball runtime state";
        }
        return message;
    }
}
