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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * 按玩家共享的召唤物目标缓存。
 *
 * <p>玩家战斗事件由全部召唤物读取，每个运行实例独立保存已处理的事件时间与自动目标，避免不同索敌范围
 * 互相覆盖。优先级与 1.21 一致：最后被鞭子标记的目标最高，其次是刚伤害玩家的目标，然后是玩家刚攻击的
 * 目标，最后才是召唤物自身附近能够看见的敌对生物。缓存只存在于服务端运行期，不写入存档。</p>
 */
public final class SummonTargetCache {
    private static final int REFRESH_INTERVAL = 5;
    private static final Map<ServerLevel, Map<SummonKey, CommandState>> COMMANDS = new WeakHashMap<>();
    private static final Map<ServerLevel, Map<SummonKey, AutomaticEntry>> AUTOMATIC_TARGETS = new WeakHashMap<>();

    private SummonTargetCache() {}

    public static @Nullable LivingEntity acquire(
            ServerLevel level,
            ServerPlayer owner,
            double automaticRange
    ) {
        return acquire(level, owner, owner.getUUID(), owner.position(), automaticRange);
    }

    /**
     * 按指定召唤物的位置选择目标。玩家战斗指令由同一玩家的召唤物共享，自动索敌则由每个运行实例独立维护。
     */
    public static @Nullable LivingEntity acquire(ServerLevel level, ServerPlayer owner, UUID summonId, Vec3 origin, double automaticRange) {
        SummonKey key = new SummonKey(owner.getUUID(), summonId);
        boolean changedLevel = invalidateOtherLevels(level, key);
        Map<SummonKey, CommandState> levelCommands = COMMANDS.computeIfAbsent(level, ignored -> new HashMap<>());
        CommandState command;
        if (changedLevel) {
            command = new CommandState(
                    owner.getLastHurtByMobTimestamp(),
                    owner.getLastHurtMobTimestamp());
            levelCommands.put(key, command);
        } else {
            command = levelCommands.computeIfAbsent(key, ignored -> new CommandState());
        }
        long gameTime = level.getGameTime();
        LivingEntity whipTarget = WhipTagTracker.lastTaggedTarget(owner);
        if (isValidTarget(owner, whipTarget, origin, automaticRange, true)) {
            command.target = null;
            return whipTarget;
        }

        int hurtByTimestamp = owner.getLastHurtByMobTimestamp();
        int hurtTimestamp = owner.getLastHurtMobTimestamp();
        boolean hurtByChanged = hurtByTimestamp != command.lastHurtByTimestamp;
        boolean hurtChanged = hurtTimestamp != command.lastHurtTimestamp;
        command.lastHurtByTimestamp = hurtByTimestamp;
        command.lastHurtTimestamp = hurtTimestamp;
        LivingEntity attacker = owner.getLastHurtByMob();
        LivingEntity attacked = owner.getLastHurtMob();
        if (hurtByChanged && isValidTarget(owner, attacker, origin, Double.MAX_VALUE, true)) {
            command.target = attacker;
        } else if (hurtChanged && isValidTarget(owner, attacked, origin, Double.MAX_VALUE, true)) {
            command.target = attacked;
        }
        if (isValidTarget(owner, command.target, origin, Double.MAX_VALUE, true))
            return command.target;
        command.target = null;

        Map<SummonKey, AutomaticEntry> levelTargets = AUTOMATIC_TARGETS.computeIfAbsent(level, ignored -> new HashMap<>());
        AutomaticEntry cached = levelTargets.get(key);
        if (cached != null && isValidTarget(owner, cached.target, origin, automaticRange, false))
            return cached.target;
        if (cached != null && gameTime - cached.gameTime < REFRESH_INTERVAL) return null;
        LivingEntity selected = selectAutomaticTarget(level, owner, origin, automaticRange);
        levelTargets.put(key, new AutomaticEntry(gameTime, selected));
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

    /**
     * Moves an owner's live summon command keys to the destination level while treating the
     * owner's existing combat timestamps as the baseline. Runtime summons removed immediately
     * after the transfer will invalidate their migrated keys individually.
     */
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
        Map<SummonKey, CommandState> currentCommands =
                COMMANDS.computeIfAbsent(currentLevel, ignored -> new HashMap<>());
        int hurtByTimestamp = owner.getLastHurtByMobTimestamp();
        int hurtTimestamp = owner.getLastHurtMobTimestamp();
        for (SummonKey key : keys) {
            currentCommands.put(key, new CommandState(hurtByTimestamp, hurtTimestamp));
        }
    }

    private static void collectAndRemoveOwnerKeys(
            @Nullable Map<SummonKey, ?> entries,
            UUID ownerId,
            Set<SummonKey> collected
    ) {
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

    /**
     * 清理一个已经移除的召唤物所持有的索敌状态，不影响同一玩家的其他召唤物。
     */
    public static void invalidate(ServerLevel level, UUID ownerId, UUID summonId) {
        SummonKey key = new SummonKey(ownerId, summonId);
        Map<SummonKey, CommandState> commands = COMMANDS.get(level);
        if (commands != null) commands.remove(key);
        Map<SummonKey, AutomaticEntry> targets = AUTOMATIC_TARGETS.get(level);
        if (targets != null) targets.remove(key);
    }

    private static @Nullable LivingEntity selectAutomaticTarget(ServerLevel level, ServerPlayer owner, Vec3 origin,
                                                                double automaticRange) {
        AABB searchBox = AABB.ofSize(origin, automaticRange * 2.0, automaticRange * 2.0, automaticRange * 2.0);
        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (LivingEntity candidate : level.getEntitiesOfClass(LivingEntity.class, searchBox)) {
            if (!isValidTarget(owner, candidate, origin, automaticRange, false)
                    || !(candidate instanceof Enemy)
                    || candidate instanceof NeutralMob
                    || !hasLineOfSight(level, owner, origin, candidate)) {
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
        return level.clip(new ClipContext(origin, target.getEyePosition(), ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, owner)).getType() == HitResult.Type.MISS;
    }

    /**
     * 判断目标是否符合召唤物的阵营、PVP、距离与存活规则。
     */
    public static boolean isValidTarget(
            ServerPlayer owner,
            @Nullable LivingEntity target,
            double range,
            boolean explicitTarget
    ) {
        return isValidTarget(owner, target, owner.position(), range, explicitTarget);
    }

    /**
     * 判断目标是否符合召唤物的阵营、PVP、距离与存活规则。
     */
    public static boolean isValidTarget(ServerPlayer owner, @Nullable LivingEntity target, Vec3 origin, double range,
                                        boolean explicitTarget) {
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
        if (target instanceof Player targetPlayer
                && (!owner.canHarmPlayer(targetPlayer)
                || !PlayerSpecialData.of(owner).isPvP()
                || !PlayerSpecialData.of(targetPlayer).isPvP())) {
            return false;
        }
        return explicitTarget || target instanceof Enemy;
    }

    private static final class CommandState {
        private int lastHurtByTimestamp;
        private int lastHurtTimestamp;
        private LivingEntity target;

        private CommandState() {
            this(-1, -1);
        }

        private CommandState(int lastHurtByTimestamp, int lastHurtTimestamp) {
            this.lastHurtByTimestamp = lastHurtByTimestamp;
            this.lastHurtTimestamp = lastHurtTimestamp;
        }
    }

    private record SummonKey(UUID ownerId, UUID summonId) {}

    private record AutomaticEntry(long gameTime, @Nullable LivingEntity target) {}
}
