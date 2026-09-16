package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;

/// 停下并瞄准目标；蓄力期间受伤或失去视线会取消本次远程攻击。
public final class RangedWindupAction extends BTNode {
    private final BaseMonster mob;
    private final int duration;
    private int ticks;

    public RangedWindupAction(BaseMonster mob, int duration) {
        if (duration < 0)
            throw new IllegalArgumentException("Ranged windup duration must be non-negative");
        this.mob = mob;
        this.duration = duration;
    }

    @Override
    public void start() {
        ticks = 0;
        mob.getNavigation().stop();
        mob.getMoveControl().setWantedPosition(mob.getX(), mob.getY(), mob.getZ(), 0.0);
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive() || mob.hurtTime > 0 || !mob.getSensing().hasLineOfSight(target)) {
            return BTStatus.FAILURE;
        }
        Vec3 movement = mob.getDeltaMovement();
        mob.setDeltaMovement(0.0, movement.y, 0.0);
        mob.faceCombatPosition(target.getEyePosition(), 30.0F, 30.0F);
        return ++ticks >= duration ? BTStatus.SUCCESS : BTStatus.RUNNING;
    }
}
