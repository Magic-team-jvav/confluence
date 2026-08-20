package org.confluence.mod.api.summon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.api.whip.WhipTagTracker;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// 按玩家共享的召唤物目标缓存。
///
/// <p>玩家战斗事件由全部召唤物读取，每个运行实例独立保存已处理的事件时间与自动目标，避免不同索敌范围
/// 互相覆盖。鞭子标记拥有最高优先级，其次是刚伤害玩家的目标和玩家刚攻击的目标，最后才是召唤物附近
/// 能够看见的敌对生物。缓存只存在于服务端运行期，不写入存档。</p>
public final class SummonTargetCache {
    private static final Map<ServerLevel, Map<SummonKey, CommandState>> COMMANDS = new WeakHashMap<>();
    private static final Map<ServerLevel, Map<SummonKey, AutomaticEntry>> AUTOMATIC_TARGETS = new WeakHashMap<>();

    private SummonTargetCache() {}

    public static @Nullable LivingEntity acquire(ServerLevel level, ServerPlayer owner, double automaticRange) {
        return acquire(level, owner, owner.getUUID(), owner.position(), automaticRange);
    }

    /// 按指定召唤物的位置选择目标。玩家战斗指令由同一玩家的召唤物共享，自动索敌则由每个运行实例独立维护。
    public static @Nullable LivingEntity acquire(ServerLevel level, ServerPlayer owner, UUID summonId, Vec3 origin, double automaticRange) {
        SummonKey key = new SummonKey(owner.getUUID(), summonId);
        boolean changedLevel = invalidateOtherLevels(level, key);
        Map<SummonKey, CommandState> levelCommands = COMMANDS.computeIfAbsent(level, ignored -> new HashMap<>());
        CommandState command;
        if (changedLevel) {
            command = new CommandState(owner.getLastHurtByMobTimestamp(), owner.getLastHurtMobTimestamp());
            levelCommands.put(key, command);
        } else {
            command = levelCommands.computeIfAbsent(key, ignored -> new CommandState(owner.getLastHurtByMobTimestamp(), owner.getLastHurtMobTimestamp()));
        }
        LivingEntity whipTarget = WhipTagTracker.lastTaggedTarget(owner);
        if (isValidTarget(owner, whipTarget, origin, automaticRange, true)) {
            command.target = null;
            return whipTarget;
        }

        int hurtByTimestamp = owner.getLastHurtByMobTimestamp();
        boolean hurtByChanged = hurtByTimestamp != command.lastHurtByTimestamp;
        command.lastHurtByTimestamp = hurtByTimestamp;
        int attackTimestamp = owner.getLastHurtMobTimestamp();
        boolean attackChanged = attackTimestamp != command.lastAttackTimestamp;
        command.lastAttackTimestamp = attackTimestamp;
        LivingEntity attacker = owner.getLastHurtByMob();
        if (hurtByChanged && isValidTarget(owner, attacker, origin, Double.MAX_VALUE, true)) {
            command.target = attacker;
            command.priority = 2;
        } else if (!(command.priority == 2 && isValidTarget(owner, command.target, origin, Double.MAX_VALUE, true))) {
            LivingEntity attacked = owner.getLastHurtMob();
            if (attackChanged && isValidTarget(owner, attacked, origin, Double.MAX_VALUE, true)) {
                command.target = attacked;
                command.priority = 3;
            }
        }
        if (isValidTarget(owner, command.target, origin, Double.MAX_VALUE, true))
            return command.target;
        command.target = null;
        command.priority = Integer.MAX_VALUE;

        Map<SummonKey, AutomaticEntry> levelTargets = AUTOMATIC_TARGETS.computeIfAbsent(level, ignored -> new HashMap<>());
        AutomaticEntry cached = levelTargets.get(key);
        if (cached != null && isValidTarget(owner, cached.target, origin, automaticRange, false))
            return cached.target;
        if (level.random.nextInt(10) != 0) return null;
        LivingEntity selected = selectAutomaticTarget(level, owner, origin, automaticRange);
        levelTargets.put(key, new AutomaticEntry(selected));
        return selected;
    }

    private static boolean invalidateOtherLevels(ServerLevel currentLevel, SummonKey key) {
        boolean invalidated = false;
        for (Map.Entry<ServerLevel, Map<SummonKey, CommandState>> entry : COMMANDS.entrySet()) {
            if (entry.getKey() != currentLevel) invalidated |= entry.getValue().remove(key) != null;
        }
        for (Map.Entry<ServerLevel, Map<SummonKey, AutomaticEntry>> entry : AUTOMATIC_TARGETS.entrySet()) {
            if (entry.getKey() != currentLevel) invalidated |= entry.getValue().remove(key) != null;
        }
        return invalidated;
    }

    public static void invalidate(ServerLevel level, UUID ownerId) {
        Map<SummonKey, CommandState> commands = COMMANDS.get(level);
        if (commands != null) commands.keySet().removeIf(key -> key.ownerId.equals(ownerId));
        Map<SummonKey, AutomaticEntry> targets = AUTOMATIC_TARGETS.get(level);
        if (targets != null) targets.keySet().removeIf(key -> key.ownerId.equals(ownerId));
    }

    /// 将玩家现有召唤物的目标缓存迁移到新维度，并以当前受击时间作为新的事件基线。
    /// 随后被移除的运行实例仍会逐个清理自己的缓存键。
    public static void transitionLevel(ServerLevel previousLevel, ServerLevel currentLevel, ServerPlayer owner) {
        UUID ownerId = owner.getUUID();
        Set<SummonKey> keys = new HashSet<>();
        collectAndRemoveOwnerKeys(COMMANDS.get(previousLevel), ownerId, keys);
        collectAndRemoveOwnerKeys(COMMANDS.get(currentLevel), ownerId, keys);
        collectAndRemoveOwnerKeys(AUTOMATIC_TARGETS.get(previousLevel), ownerId, keys);
        collectAndRemoveOwnerKeys(AUTOMATIC_TARGETS.get(currentLevel), ownerId, keys);
        if (keys.isEmpty()) {
            return;
        }
        Map<SummonKey, CommandState> currentCommands = COMMANDS.computeIfAbsent(currentLevel, ignored -> new HashMap<>());
        int hurtByTimestamp = owner.getLastHurtByMobTimestamp();
        for (SummonKey key : keys) {
            currentCommands.put(key, new CommandState(hurtByTimestamp, owner.getLastHurtMobTimestamp()));
        }
    }

    private static void collectAndRemoveOwnerKeys(@Nullable Map<SummonKey, ?> entries, UUID ownerId, Set<SummonKey> collected) {
        if (entries == null) {
            return;
        }
        entries.keySet().removeIf(key -> {
            if (!key.ownerId.equals(ownerId)) {
                return false;
            }
            collected.add(key);
            return true;
        });
    }

    /// 清理一个已经移除的召唤物所持有的索敌状态，不影响同一玩家的其他召唤物。
    public static void invalidate(ServerLevel level, UUID ownerId, UUID summonId) {
        SummonKey key = new SummonKey(ownerId, summonId);
        Map<SummonKey, CommandState> commands = COMMANDS.get(level);
        if (commands != null) commands.remove(key);
        Map<SummonKey, AutomaticEntry> targets = AUTOMATIC_TARGETS.get(level);
        if (targets != null) targets.remove(key);
    }

    private static @Nullable LivingEntity selectAutomaticTarget(ServerLevel level, ServerPlayer owner, Vec3 origin, double automaticRange) {
        AABB searchBox = AABB.ofSize(origin, automaticRange * 2.0, automaticRange * 2.0, automaticRange * 2.0);
        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (LivingEntity candidate : level.getEntitiesOfClass(LivingEntity.class, searchBox)) {
            if (!isValidTarget(owner, candidate, origin, automaticRange, false) || !(candidate instanceof Enemy) || candidate instanceof NeutralMob || !hasLineOfSight(level, owner, origin, candidate)) {
                continue;
            }
            double distance = candidate.position().distanceToSqr(origin);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = candidate;
            }
        }
        return nearest;
    }

    private static boolean hasLineOfSight(ServerLevel level, ServerPlayer owner, Vec3 origin, LivingEntity target) {
        return level.clip(new ClipContext(origin, target.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner)).getType() == HitResult.Type.MISS;
    }

    /// 判断目标是否符合召唤物的阵营、PVP、距离与存活规则。
    public static boolean isValidTarget(ServerPlayer owner, @Nullable LivingEntity target, double range, boolean explicitTarget) {
        return isValidTarget(owner, target, owner.position(), range, explicitTarget);
    }

    /// 判断目标是否符合召唤物的阵营、PVP、距离与存活规则。
    public static boolean isValidTarget(ServerPlayer owner, @Nullable LivingEntity target, Vec3 origin, double range, boolean explicitTarget) {
        if (target == null
                || !target.isAlive()
                || target.isRemoved()
                || target.level() != owner.level()
                || target == owner
                || owner.isAlliedTo(target)
                || !target.canBeSeenAsEnemy()
                || target.position().distanceToSqr(origin) > range * range) {
            return false;
        }
        if (target instanceof Player targetPlayer && (!owner.canHarmPlayer(targetPlayer) || !PlayerSpecialData.of(owner).isPvP() || !PlayerSpecialData.of(targetPlayer).isPvP())) {
            return false;
        }
        return explicitTarget || target instanceof Enemy;
    }

    private static final class CommandState {
        private int lastHurtByTimestamp;
        private int lastAttackTimestamp;
        private int priority = Integer.MAX_VALUE;
        private LivingEntity target;

        private CommandState(int lastHurtByTimestamp, int lastAttackTimestamp) {
            this.lastHurtByTimestamp = lastHurtByTimestamp;
            this.lastAttackTimestamp = lastAttackTimestamp;
        }
    }

    private record SummonKey(UUID ownerId, UUID summonId) {}

    private record AutomaticEntry(@Nullable LivingEntity target) {}
}
