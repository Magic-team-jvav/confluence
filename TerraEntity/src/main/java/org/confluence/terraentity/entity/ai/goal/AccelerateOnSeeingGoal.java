package org.confluence.terraentity.entity.ai.goal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import org.confluence.terraentity.TerraEntity;

public class AccelerateOnSeeingGoal extends Goal {
    protected final Mob mob;
    protected float speedModifier;
    private static final ResourceLocation name = TerraEntity.space("accelerate_on_seeing_goal");
    public AccelerateOnSeeingGoal(Mob mob,float speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;

    }

    @Override
    public boolean canUse() {
        boolean canUse = mob.getTarget()!= null && mob.getTarget().isAlive();
        if (canUse) {
            if (!mob.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(name)) {
                mob.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(new AttributeModifier(name, speedModifier, AttributeModifier.Operation.ADD_VALUE));
                mob.setSprinting(true);
            }
        }else if (mob.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(name)) {
            mob.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(name);
            mob.setSprinting(false);
        }
        return false;
    }
}
