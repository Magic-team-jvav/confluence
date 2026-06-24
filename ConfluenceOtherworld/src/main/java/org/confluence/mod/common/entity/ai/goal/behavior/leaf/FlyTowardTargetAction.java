package org.confluence.mod.common.entity.ai.goal.behavior.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.goal.behavior.BTNode;
import org.confluence.mod.common.entity.ai.motion.DashComponent;

/**
 * 径直向目标移动
 */
public class FlyTowardTargetAction extends BTNode {

    final Mob mob;
    final DashComponent component;
    final float speed;
    final float offsetY;
    public FlyTowardTargetAction(Mob mob, float speed) {
        this(mob, speed, 0);
    }

    public FlyTowardTargetAction(Mob mob, float speed, float offsetY) {
        this.mob = mob;
        component = new DashComponent(mob);
        this.speed = speed;
        this.offsetY = offsetY;
    }
    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if(target == null) {
            return BTStatus.FAILURE;
        }
        Vec3 dir = target.position().subtract(mob.position()).add(0,offsetY,0);
        component.setDirection(dir);
        component.uniformMove(this.speed);
        return BTStatus.RUNNING;
    }
}
