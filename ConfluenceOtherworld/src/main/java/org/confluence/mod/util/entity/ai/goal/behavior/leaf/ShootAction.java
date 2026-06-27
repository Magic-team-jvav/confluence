package org.confluence.mod.util.entity.ai.goal.behavior.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;

/**
 * 射击行为
 */
public abstract class ShootAction<T extends Mob> extends BTNode {

    protected final T mob;

    public ShootAction(T mob) {
        this.mob = mob;
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if(target == null) {
            return BTStatus.FAILURE;
        }
        this.shoot(target);
        return BTStatus.SUCCESS;
    }

    protected abstract void shoot(LivingEntity target);

}
