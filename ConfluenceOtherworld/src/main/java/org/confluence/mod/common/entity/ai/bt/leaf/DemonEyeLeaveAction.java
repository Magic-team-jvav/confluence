package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;

/// 恶魔眼白天离场时使用的持续上升运动。
///
/// 离场时停止追击，不在固定时间后直接删除实体；实际离场仍交给
/// 原版距离卸载规则处理，使玩家能看见完整的飞离过程。
public final class DemonEyeLeaveAction extends BTNode {
    private final BaseMonster mob;
    private Vec3 acceleration = Vec3.ZERO;

    public DemonEyeLeaveAction(BaseMonster mob) {
        this.mob = mob;
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
        mob.setTarget(null);
        double x = mob.getRandom().nextDouble() - 0.5;
        double y = 0.1 + 0.5 * mob.getRandom().nextDouble();
        double z = mob.getRandom().nextDouble() - 0.5;
        acceleration = new Vec3(x, y, z).normalize().scale(0.25);
    }

    @Override
    public BTStatus execute() {
        if (!mob.level().isDay()) {
            return BTStatus.SUCCESS;
        }
        mob.setTarget(null);
        Vec3 movement = mob.getDeltaMovement().add(acceleration);
        mob.setDeltaMovement(movement.lengthSqr() > 0.25 ? movement.normalize().scale(0.5) : movement);
        mob.faceCombatMovement(10.0F, 30.0F);
        mob.hasImpulse = true;
        return BTStatus.RUNNING;
    }
}
