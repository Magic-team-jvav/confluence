package org.confluence.mod.common.entity.npc.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.EnumSet;

/// NPC 受到敌怪伤害后短暂撤离攻击者，结束后再交回公共自卫目标处理反击。
public final class NPCHurtRetreatGoal extends Goal {
    private static final int MAX_RETREAT_TICKS = 60;
    private final BaseNPC npc;
    private LivingEntity attacker;
    private int handledHurtTimestamp = Integer.MIN_VALUE;
    private int retreatTicks;
    private int repathDelay;

    /// 创建独占移动和朝向控制的受击撤离目标。
    public NPCHurtRetreatGoal(BaseNPC npc) {
        this.npc = npc;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    /// 每次有效敌怪伤害只触发一轮撤离，交易期间不启动。
    @Override
    public boolean canUse() {
        int timestamp = npc.getLastHurtByMobTimestamp();
        LivingEntity source = npc.getLastHurtByMob();
        if (timestamp == handledHurtTimestamp || npc.getTradingPlayer() != null || !isValid(source))
            return false;
        handledHurtTimestamp = timestamp;
        attacker = source;
        return true;
    }

    /// 攻击者仍在十格内、撤离未超时且 NPC 没有开始交易时继续。
    @Override
    public boolean canContinueToUse() {
        return retreatTicks < MAX_RETREAT_TICKS && npc.getTradingPlayer() == null && isValid(attacker)
                && npc.distanceToSqr(attacker) < 100;
    }

    /// 清空计时并立即计算第一条远离攻击者的路径。
    @Override
    public void start() {
        retreatTicks = 0;
        repathDelay = 0;
        updateRetreatPath();
    }

    /// 面向攻击者并定期刷新撤离路径，避免一次寻路失败后原地停留。
    @Override
    public void tick() {
        retreatTicks++;
        if (attacker == null) return;
        npc.getLookControl().setLookAt(attacker, 30, 30);
        if (--repathDelay <= 0 || npc.getNavigation().isDone()) updateRetreatPath();
    }

    /// 撤离完成后只清理本目标的路径，不清除 LivingEntity 记录的受击者。
    @Override
    public void stop() {
        npc.getNavigation().stop();
        attacker = null;
    }

    /// 撤离路径和超时需要逐 tick 更新。
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    /// 只把 NPC 可以反击的存活实体视为有效攻击者，排除玩家和其他城镇 NPC。
    private boolean isValid(LivingEntity entity) {
        return entity != null && entity.isAlive() && npc.canAttack(entity);
    }

    /// 在八格水平、四格垂直范围内寻找远离攻击者的可达位置。
    private void updateRetreatPath() {
        repathDelay = 10;
        if (attacker == null) return;
        Vec3 destination = DefaultRandomPos.getPosAway(npc, 8, 4, attacker.position());
        if (destination != null)
            npc.getNavigation().moveTo(destination.x, destination.y, destination.z, 1.3);
    }
}
