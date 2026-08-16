package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// Otherworld 玩家战斗弹幕共享的可持久化运行状态。
///
/// <p>MagicLib 的 {@link ProjectileCombatSnapshot} 只负责不可变战斗数值；本类补充实体运行期的
/// 剩余寿命、命中预算和已成功命中的目标。它是组合式小委托，不要求剑气、长矛、子弹和魔法弹
/// 继承同一个庞大基类。</p>
///
/// <p>这里只接受当前格式。缺失、版本不符或字段损坏都会进入安全失效状态，实体必须停止伤害并
/// 在服务端销毁；不会读取旧字段，也不会回退到玩家当前主手。</p>
public final class ProjectileCombatState {
    public static final String ROOT_TAG = "ConfluenceCombat";
    public static final int CURRENT_FORMAT_VERSION = 1;

    private static final String VERSION_TAG = "Version";
    private static final String SNAPSHOT_TAG = "Snapshot";
    private static final String REMAINING_LIFETIME_TAG = "RemainingLifetime";
    private static final String REMAINING_HITS_TAG = "RemainingHits";
    private static final String HIT_TARGETS_TAG = "HitTargets";
    private static final int UNLIMITED = -1;
    private static final long LOG_INTERVAL_TICKS = 200L;
    private static final Map<String, Long> LAST_LOG_TICKS = new HashMap<>();

    private final Set<UUID> successfulHitTargets = new HashSet<>();
    private @Nullable ProjectileCombatSnapshot snapshot;
    private @Nullable String invalidReason;
    private boolean loadedFromTag;

    /// 安装发射事务解析出的不可变快照。只允许非空的已验证对象。
    public void installSnapshot(ProjectileCombatSnapshot snapshot) {
        this.snapshot = Objects.requireNonNull(snapshot, "Projectile combat snapshot must not be null");
    }

    /// 返回当前快照；未初始化或读取失败时返回 {@code null}。
    public @Nullable ProjectileCombatSnapshot snapshot() {
        return snapshot;
    }

    /// 判断目标是否仍可命中。
    ///
    /// @param allowRepeatedHits 特殊持续弹幕是否允许按自身冷却重复伤害同一目标
    public boolean canHit(UUID targetUuid, boolean allowRepeatedHits) {
        Objects.requireNonNull(targetUuid, "Target UUID must not be null");
        return !isInvalid() && (allowRepeatedHits || !successfulHitTargets.contains(targetUuid));
    }

    /// 只在 {@code target.hurt(...)} 成功后调用，返回该 UUID 是否为首次成功命中。
    public boolean recordSuccessfulHit(UUID targetUuid) {
        return successfulHitTargets.add(Objects.requireNonNull(targetUuid, "Target UUID must not be null"));
    }

    public int successfulHitCount() {
        return successfulHitTargets.size();
    }

    public boolean isInvalid() {
        return invalidReason != null;
    }

    public @Nullable String invalidReason() {
        return invalidReason;
    }

    public boolean wasLoadedFromTag() {
        return loadedFromTag;
    }

    /// 写出完整当前格式。没有快照的新建实体不会伪造数据；若这种实体被保存后再读取，读取端会按
    /// 缺失当前格式安全销毁，等待对应武器族迁移到统一发射事务。
    public void writeTo(CompoundTag entityTag, int remainingLifetime, int remainingHits) {
        Objects.requireNonNull(entityTag, "Entity tag must not be null");
        if (snapshot == null || isInvalid()) {
            return;
        }
        validateBudget(remainingLifetime, "Remaining lifetime");
        validateBudget(remainingHits, "Remaining hits");

        CompoundTag combatTag = new CompoundTag();
        combatTag.putInt(VERSION_TAG, CURRENT_FORMAT_VERSION);
        combatTag.put(SNAPSHOT_TAG, snapshot.toTag());
        combatTag.putInt(REMAINING_LIFETIME_TAG, remainingLifetime);
        combatTag.putInt(REMAINING_HITS_TAG, remainingHits);
        ListTag targetsTag = new ListTag();
        successfulHitTargets.forEach(uuid -> targetsTag.add(NbtUtils.createUUID(uuid)));
        combatTag.put(HIT_TARGETS_TAG, targetsTag);
        entityTag.put(ROOT_TAG, combatTag);
    }

    /// 读取当前格式并返回实体预算。任何异常都会被收敛为安全失效状态，不把损坏数据传播到 tick。
    public RestoredBudgets readFrom(CompoundTag entityTag) {
        Objects.requireNonNull(entityTag, "Entity tag must not be null");
        loadedFromTag = true;
        snapshot = null;
        successfulHitTargets.clear();
        invalidReason = null;
        try {
            requireTag(entityTag, ROOT_TAG, Tag.TAG_COMPOUND);
            CompoundTag combatTag = entityTag.getCompound(ROOT_TAG);
            requireTag(combatTag, VERSION_TAG, Tag.TAG_INT);
            int version = combatTag.getInt(VERSION_TAG);
            if (version != CURRENT_FORMAT_VERSION) {
                throw new IllegalArgumentException("Unsupported projectile combat state version: " + version);
            }
            requireTag(combatTag, SNAPSHOT_TAG, Tag.TAG_COMPOUND);
            requireTag(combatTag, REMAINING_LIFETIME_TAG, Tag.TAG_INT);
            requireTag(combatTag, REMAINING_HITS_TAG, Tag.TAG_INT);
            requireTag(combatTag, HIT_TARGETS_TAG, Tag.TAG_LIST);

            int remainingLifetime = combatTag.getInt(REMAINING_LIFETIME_TAG);
            int remainingHits = combatTag.getInt(REMAINING_HITS_TAG);
            validateBudget(remainingLifetime, "Remaining lifetime");
            validateBudget(remainingHits, "Remaining hits");
            Tag rawTargetsTag = combatTag.get(HIT_TARGETS_TAG);
            if (!(rawTargetsTag instanceof ListTag targetsTag)
                    || !targetsTag.isEmpty() && targetsTag.getElementType() != Tag.TAG_INT_ARRAY) {
                throw new IllegalArgumentException("Projectile hit targets must be a UUID list");
            }
            for (Tag targetTag : targetsTag) {
                UUID targetUuid = NbtUtils.loadUUID(targetTag);
                if (!successfulHitTargets.add(targetUuid)) {
                    throw new IllegalArgumentException("Duplicate projectile hit target UUID");
                }
            }
            snapshot = ProjectileCombatSnapshot.fromTag(combatTag.getCompound(SNAPSHOT_TAG));
            return new RestoredBudgets(remainingLifetime, remainingHits);
        } catch (RuntimeException exception) {
            invalidate(asEnglishReason(exception));
            return RestoredBudgets.INVALID;
        }
    }

    /// 供实体自己的运动字段校验失败时复用同一安全失效通道。
    public void invalidate(String reason) {
        String checkedReason = Objects.requireNonNull(reason, "Invalid reason must not be null");
        if (checkedReason.isBlank()) {
            throw new IllegalArgumentException("Invalid reason must not be blank");
        }
        snapshot = null;
        successfulHitTargets.clear();
        invalidReason = checkedReason;
    }

    /// 在服务端限频记录英文诊断并销毁损坏实体。返回 {@code true} 便于 tick 入口直接结束。
    public boolean discardIfInvalid(Entity projectile) {
        Objects.requireNonNull(projectile, "Projectile must not be null");
        if (!isInvalid() || projectile.level().isClientSide) {
            return false;
        }
        long gameTime = projectile.level().getGameTime();
        String key = projectile.getType() + "|" + invalidReason;
        boolean shouldLog;
        synchronized (LAST_LOG_TICKS) {
            Long lastTick = LAST_LOG_TICKS.get(key);
            shouldLog = lastTick == null || gameTime < lastTick || gameTime - lastTick >= LOG_INTERVAL_TICKS;
            if (shouldLog) {
                LAST_LOG_TICKS.put(key, gameTime);
            }
        }
        if (shouldLog) {
            Confluence.LOGGER.error("Discarding invalid player projectile {}: {}",
                    projectile.getStringUUID(), invalidReason);
        }
        projectile.discard();
        return true;
    }

    private static void requireTag(CompoundTag tag, String key, int expectedType) {
        if (!tag.contains(key, expectedType)) {
            throw new IllegalArgumentException("Missing or invalid projectile combat state field: " + key);
        }
    }

    private static void validateBudget(int value, String fieldName) {
        if (value < UNLIMITED) {
            throw new IllegalArgumentException(fieldName + " must be -1 or non-negative");
        }
    }

    private static String asEnglishReason(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank() || !message.chars().allMatch(character -> character < 128)) {
            return "Malformed projectile combat state";
        }
        return message;
    }

    /// 从 NBT 恢复出的剩余实体预算；{@code -1} 表示该维度不限。
    public record RestoredBudgets(int remainingLifetime, int remainingHits) {
        private static final RestoredBudgets INVALID = new RestoredBudgets(0, 0);
    }
}
