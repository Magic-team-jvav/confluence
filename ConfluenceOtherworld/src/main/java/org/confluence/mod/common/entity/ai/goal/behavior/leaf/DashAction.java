package org.confluence.mod.common.entity.ai.goal.behavior.leaf;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.goal.behavior.BTNode;
import org.confluence.mod.common.entity.ai.motion.DashComponent;

/**
 * 向前冲刺
 */
public class DashAction extends BTNode {
    final Mob mob;
    final DashComponent component;
    float speed;
    Vec3 targetPos;

    public DashAction(Mob mob, float speed) {
        this.mob = mob;
        this.component = new DashComponent(mob);
        this.speed = speed;

    }

    @Override
    public void start() {
        super.start();
        this.component.setDirection(mob.getForward());
        targetPos = mob.position().add(mob.getForward().normalize().scale(200));
    }

    @Override
    public BTStatus execute() {
        this.component.uniformMove(speed);
        this.mob.getLookControl().setLookAt(targetPos);
        this.mob.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
        return BTStatus.RUNNING;
    }
}
