package org.confluence.mod.common.entity.npc.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.Comparator;
import java.util.EnumSet;

/// 城镇 NPC 只在附近存在敌人时自卫，并在敌人贴身时后撤；不会主动追击目标。
public final class NPCDefenseGoal extends Goal {
    private final BaseNPC npc;
    private LivingEntity target;
    private int attackCooldown;
    private int attackPreparation;
    private int retreatRepath;

    /// 创建同时占用移动和视线控制的自卫目标，防止闲逛目标覆盖后撤路径。
    public NPCDefenseGoal(BaseNPC npc) {
        this.npc = npc;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    /// 仅在非交易状态、允许自卫且附近存在合法敌人时启动。
    @Override
    public boolean canUse() {
        if (!npc.getCombatProfile().reactsToEnemies(npc) || !npc.canDefendSelf()
                || npc.getTradingPlayer() != null) return false;
        target = findTarget();
        return target != null;
    }

    /// 目标仍合法且 NPC 没有开始交易时持续执行，不因暂时丢失视线而重置冷却。
    @Override
    public boolean canContinueToUse() {
        if (npc.getTradingPlayer() != null || !npc.canDefendSelf() || !isValid(target))
            return false;
        NPCCombatProfile.Values values = npc.getCombatProfile().values(npc);
        double range = detectionRange(values);
        return npc.distanceToSqr(target) <= range * range;
    }

    /// 记录本轮敌人并进入注册项指定的攻击准备阶段。
    @Override
    public void start() {
        npc.setTarget(target);
        attackCooldown = 0;
        attackPreparation = npc.getCombatProfile().values(npc).prepareTime();
    }

    /// 清理本目标拥有的路径和战斗目标，不影响其他系统后来设置的新目标。
    @Override
    public void stop() {
        npc.getNavigation().stop();
        if (npc.getTarget() == target) npc.setTarget(null);
        target = null;
        attackCooldown = 0;
        attackPreparation = 0;
        retreatRepath = 0;
    }

    /// 后撤、朝向和攻击冷却均需要逐 tick 更新。
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    /// 面向敌人、在贴身时后撤，并仅在射程和视线均满足时执行配置动作。
    @Override
    public void tick() {
        if (target == null) return;
        NPCCombatProfile profile = npc.getCombatProfile();
        NPCCombatProfile.Values values = profile.values(npc);
        npc.getLookControl().setLookAt(target, 30, 30);

        double distanceSqr = npc.distanceToSqr(target);
        if (distanceSqr < values.retreatRange() * values.retreatRange()) {
            retreatFromTarget();
        } else {
            npc.getNavigation().stop();
        }

        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }
        if (distanceSqr > values.attackRange() * values.attackRange()
                || !npc.getSensing().hasLineOfSight(target)) {
            attackPreparation = values.prepareTime();
            return;
        }
        if (attackPreparation > 0) {
            attackPreparation--;
        } else {
            profile.attack().perform(npc, target, values);
            attackCooldown = values.attackInterval();
            attackPreparation = values.prepareTime();
        }
    }

    /// 在属性索敌上限和实际战斗距离的较小值内选择最近敌人，避免远处敌人冻结 NPC 闲逛。
    private LivingEntity findTarget() {
        NPCCombatProfile.Values values = npc.getCombatProfile().values(npc);
        double range = detectionRange(values);
        AABB area = npc.getBoundingBox().inflate(range, range * 0.5, range);
        return npc.level().getEntitiesOfClass(LivingEntity.class, area, this::isValid).stream()
                .min(Comparator.comparingDouble(npc::distanceToSqr)).orElse(null);
    }

    /// NPC 只把仍存活的敌对生物作为自卫目标，不攻击玩家或其他城镇 NPC。
    private boolean isValid(LivingEntity candidate) {
        return candidate != null && candidate.isAlive() && candidate instanceof Enemy && npc.canAttack(candidate);
    }

    /// 取属性索敌上限和实际战斗距离的较小值，作为目标的启动与持续范围。
    private double detectionRange(NPCCombatProfile.Values values) {
        return Math.min(npc.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE),
                Math.max(values.attackRange(), values.retreatRange()) + 2);
    }

    /// 每十 tick 重新寻找一次远离目标的可达位置，不主动计算接近敌人的路径。
    private void retreatFromTarget() {
        if (--retreatRepath > 0 || target == null) return;
        retreatRepath = 10;
        Vec3 destination = DefaultRandomPos.getPosAway(npc, 8, 4, target.position());
        if (destination != null)
            npc.getNavigation().moveTo(destination.x, destination.y, destination.z, 1.1);
    }

}
