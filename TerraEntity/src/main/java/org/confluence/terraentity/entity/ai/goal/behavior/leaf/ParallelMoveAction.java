package org.confluence.terraentity.entity.ai.goal.behavior.leaf;

import net.minecraft.world.entity.Mob;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;
import org.confluence.terraentity.entity.ai.motion.DashComponent;

/**
 * <p>平行于目标移动，如魔焰眼一阶段</p>
 * <p>或者在目标头顶，如激光眼二阶段</p>
 */
public class ParallelMoveAction extends BTNode {

    final Mob mob;
    float dist;
    final DashComponent component;
    final float speed;
    final float offsetY;

    public ParallelMoveAction(Mob mob, float dist, float speed, float offsetY) {
        this.mob = mob;
        this.dist = dist;
        this.component = new DashComponent(mob);
        this.speed = speed;
        this.offsetY = offsetY;
    }

    @Override
    public BTStatus execute() {
        if (mob.getTarget() == null) {
            return BTStatus.FAILURE;
        }
        if(dist <= 0) {
            component.hangAbove(mob.getTarget(), this.offsetY, this.speed);
        }else{
            component.hangOn(mob.getTarget(), dist, this.offsetY, this.speed);
        }
        return BTStatus.RUNNING;
    }
}
