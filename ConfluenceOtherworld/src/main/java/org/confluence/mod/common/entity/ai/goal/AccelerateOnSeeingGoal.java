package org.confluence.mod.common.entity.ai.goal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import org.confluence.mod.Confluence;

public class AccelerateOnSeeingGoal extends Goal {
    private static final ResourceLocation ID = Confluence.asResource("accelerate_on_seeing_goal");

    protected final Mob mob;
    protected float speedModifier;

    public AccelerateOnSeeingGoal(Mob mob, float speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;

    }

    @Override
    public boolean canUse() {
        boolean canUse = mob.getTarget() != null && mob.getTarget().isAlive();
        if (canUse) {
            if (!mob.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(ID)) {
                mob.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(new AttributeModifier(ID, speedModifier, AttributeModifier.Operation.ADD_VALUE));
                mob.setSprinting(true);
            }
        } else if (mob.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(ID)) {
            mob.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(ID);
            mob.setSprinting(false);
        }
        return false;
    }
}
