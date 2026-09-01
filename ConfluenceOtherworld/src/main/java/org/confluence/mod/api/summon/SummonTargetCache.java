package org.confluence.mod.api.summon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.api.whip.WhipTagTracker;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.entity.boss.BaseWormBoss;
import org.confluence.mod.common.entity.monster.BaseWormMonster;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// 按玩家共享的召唤物目标缓存。
public final class SummonTargetCache {
    // 两张表都按服务端世界隔离并使用弱键，世界卸载后不会被全局缓存继续持有。
    // COMMANDS 以玩家为键共享手动指令；AUTOMATIC_TARGETS 以召唤实例为键缓存自动索敌。
    private static final Map<ServerLevel, Map<UUID, CommandState>> COMMANDS = new WeakHashMap<>();
    private static final Map<ServerLevel, Map<SummonKey, AutomaticEntry>> AUTOMATIC_TARGETS = new WeakHashMap<>();

    private SummonTargetCache() {}

    public static @Nullable LivingEntity acquire(ServerLevel level, ServerPlayer owner, double automaticRange) {
        return acquire(level, owner, owner.getUUID(), owner.position(), automaticRange);
    }

    /// 按指定召唤物的位置选择目标。玩家战斗指令由同一玩家的召唤物共享，自动索敌则由每个运行实例独立维护。
    public static @Nullable LivingEntity acquire(ServerLevel level, ServerPlayer owner, UUID summonId, Vec3 origin, double automaticRange) {
        SummonKey key = new SummonKey(owner.getUUID(), summonId);
        boolean changedLevel = invalidateOtherLevels(level, owner.getUUID());
        Map<UUID, CommandState> levelCommands = COMMANDS.computeIfAbsent(level, ignored -> new HashMap<>());
        CommandState command;
        if (changedLevel) {
            command = new CommandState(0, 0);
            levelCommands.put(owner.getUUID(), command);
        } else {
            command = levelCommands.computeIfAbsent(owner.getUUID(), ignored -> new CommandState(0, 0));
        }
        LivingEntity whipTarget = WhipTagTracker.lastTaggedTarget(owner);
        if (isValidTarget(owner, whipTarget, origin, automaticRange, true)) {
            command.target = null;
            return whipTarget;
        }

        if (command.priority == 2 && isValidTarget(owner, command.target, origin, automaticRange, true)) {
            return command.target;
        }
        int hurtByTimestamp = owner.getLastHurtByMobTimestamp();
        boolean hurtByChanged = hurtByTimestamp != command.lastHurtByTimestamp;
        command.lastHurtByTimestamp = hurtByTimestamp;
        LivingEntity attacker = owner.getLastHurtByMob();
        if (hurtByChanged && isValidTarget(owner, attacker, origin, automaticRange, true)) {
            command.target = attacker;
            command.priority = 2;
            return command.target;
        }
        if (command.priority == 3 && isValidTarget(owner, command.target, origin, automaticRange, true)) {
            return command.target;
        }
        int attackTimestamp = owner.getLastHurtMobTimestamp();
        boolean attackChanged = attackTimestamp != command.lastAttackTimestamp;
        command.lastAttackTimestamp = attackTimestamp;
        LivingEntity attacked = owner.getLastHurtMob();
        if (attackChanged && isValidTarget(owner, attacked, origin, automaticRange, true)) {
            command.target = attacked;
            command.priority = 3;
            return command.target;
        }
        command.target = null;
        command.priority = Integer.MAX_VALUE;

        Map<SummonKey, AutomaticEntry> levelTargets = AUTOMATIC_TARGETS.computeIfAbsent(level, ignored -> new HashMap<>());
        AutomaticEntry cached = levelTargets.get(key);
        if (cached != null && cached.retainsWithoutSight
                && isValidTarget(owner, cached.target, origin, automaticRange, false)
                && hasPartInRange(origin, automaticRange, cached.target)) return cached.target;
        if (cached != null && cached.unseenTicks <= 60
                && isValidTarget(owner, cached.target, origin, automaticRange, false)) {
            if (hasLineOfSight(level, owner, origin, cached.target)) {
                cached.unseenTicks = 0;
                return cached.target;
            }
            if (++cached.unseenTicks <= 60) return cached.target;
        }
        LivingEntity partTarget = selectPartTarget(level, owner, origin, automaticRange);
        if (partTarget != null) {
            levelTargets.put(key, new AutomaticEntry(partTarget, true));
            return partTarget;
        }
        if (level.random.nextInt(10) != 0) return null;
        LivingEntity selected = selectAutomaticTarget(level, owner, origin, automaticRange);
        levelTargets.put(key, new AutomaticEntry(selected, false));
        return selected;
    }

    private static boolean invalidateOtherLevels(ServerLevel currentLevel, UUID ownerId) {
        boolean invalidated = false;
        for (Map.Entry<ServerLevel, Map<UUID, CommandState>> entry : COMMANDS.entrySet()) {
            if (entry.getKey() != currentLevel)
                invalidated |= entry.getValue().remove(ownerId) != null;
        }
        for (Map.Entry<ServerLevel, Map<SummonKey, AutomaticEntry>> entry : AUTOMATIC_TARGETS.entrySet()) {
            if (entry.getKey() != currentLevel)
                invalidated |= entry.getValue().keySet().removeIf(key -> key.ownerId.equals(ownerId));
        }
        return invalidated;
    }

    public static void invalidate(ServerLevel level, UUID ownerId) {
        Map<UUID, CommandState> commands = COMMANDS.get(level);
        if (commands != null) commands.remove(ownerId);
        Map<SummonKey, AutomaticEntry> targets = AUTOMATIC_TARGETS.get(level);
        if (targets != null) targets.keySet().removeIf(key -> key.ownerId.equals(ownerId));
    }

    /// 清理一个已经移除的召唤物所持有的索敌状态，不影响同一玩家的其他召唤物。
    public static void invalidate(ServerLevel level, UUID ownerId, UUID summonId) {
        SummonKey key = new SummonKey(ownerId, summonId);
        Map<SummonKey, AutomaticEntry> targets = AUTOMATIC_TARGETS.get(level);
        if (targets != null) targets.remove(key);
    }

    private static @Nullable LivingEntity selectAutomaticTarget(ServerLevel level, ServerPlayer owner, Vec3 origin, double automaticRange) {
        AABB searchBox = AABB.ofSize(origin, automaticRange * 2.0, automaticRange * 2.0, automaticRange * 2.0);
        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        int nearestPriority = Integer.MAX_VALUE;
        for (LivingEntity candidate : level.getEntitiesOfClass(LivingEntity.class, searchBox)) {
            if (!isValidTarget(owner, candidate, origin, automaticRange, false) || !(candidate instanceof Enemy) || candidate instanceof NeutralMob || !hasLineOfSight(level, owner, origin, candidate)) {
                continue;
            }
            int priority = hasVisiblePart(level, owner, origin, automaticRange, candidate) ? 3
                    : candidate instanceof Monster ? 4 : candidate instanceof Slime ? 5 : Integer.MAX_VALUE;
            if (priority == Integer.MAX_VALUE || priority > nearestPriority) continue;
            double distance = candidate.position().distanceToSqr(origin);
            if (priority < nearestPriority || distance < nearestDistance) {
                nearestPriority = priority;
                nearestDistance = distance;
                nearest = candidate;
            }
        }
        return nearest;
    }

    private static @Nullable LivingEntity selectPartTarget(ServerLevel level, ServerPlayer owner, Vec3 origin, double automaticRange) {
        AABB searchBox = AABB.ofSize(origin, automaticRange * 2.0, automaticRange * 2.0, automaticRange * 2.0);
        for (Entity part : level.getEntities(owner, searchBox,
                entity -> entity.isAlive() && ProjectileHitRules.canHit(owner, entity))) {
            LivingEntity candidate = ProjectileHitRules.logicalLivingTarget(part);
            if (candidate == null || candidate == part) continue;
            if (isValidTarget(owner, candidate, Double.MAX_VALUE, false)
                    && hasLineOfSight(level, owner, origin, part.getEyePosition()))
                return candidate;
        }
        return null;
    }

    private static boolean hasVisiblePart(ServerLevel level, ServerPlayer owner, Vec3 origin, double range, LivingEntity candidate) {
        return findPart(level, owner, origin, range, candidate, true);
    }

    private static boolean hasPartInRange(Vec3 origin, double range, LivingEntity candidate) {
        return findPart(null, null, origin, range, candidate, false);
    }

    private static boolean findPart(@Nullable ServerLevel level, @Nullable ServerPlayer owner, Vec3 origin, double range, LivingEntity candidate, boolean mustSee) {
        List<? extends Entity> parts;
        if (candidate instanceof BaseWormBoss wormBoss) parts = wormBoss.getSegments();
        else if (candidate instanceof BaseWormMonster wormMonster)
            parts = wormMonster.getSegments();
        else if (candidate instanceof BaseBoss boss) parts = boss.getSubEntities();
        else return false;
        double rangeSqr = range * range;
        for (Entity part : parts) {
            boolean canHit = level == null || owner == null
                    ? ProjectileHitRules.acceptsDirectHit(part)
                    : ProjectileHitRules.canHit(owner, part);
            if (part.isAlive() && canHit && part.position().distanceToSqr(origin) <= rangeSqr
                    && (!mustSee || hasLineOfSight(level, owner, origin, part.getEyePosition())))
                return true;
        }
        return false;
    }

    private static boolean hasLineOfSight(ServerLevel level, ServerPlayer owner, Vec3 origin, LivingEntity target) {
        return hasLineOfSight(level, owner, origin, target.getEyePosition());
    }

    private static boolean hasLineOfSight(ServerLevel level, ServerPlayer owner, Vec3 origin, Vec3 target) {
        return level.clip(new ClipContext(origin, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner)).getType() == HitResult.Type.MISS;
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

    private static final class AutomaticEntry {
        private final @Nullable LivingEntity target;
        private final boolean retainsWithoutSight;
        private int unseenTicks;

        private AutomaticEntry(@Nullable LivingEntity target, boolean retainsWithoutSight) {
            this.target = target;
            this.retainsWithoutSight = retainsWithoutSight;
        }
    }
}
