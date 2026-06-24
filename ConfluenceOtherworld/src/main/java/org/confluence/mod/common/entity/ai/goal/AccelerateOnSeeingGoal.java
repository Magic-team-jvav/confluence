package org.confluence.mod.common.entity.ai.goal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.UUID;

public class AccelerateOnSeeingGoal extends Goal {
    public static final ResourceLocation ID = Confluence.asResource("accelerate_on_seeing_goal");
    private static final UUID UUID = PortAttributeModifier.rl2uuid(ID);

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
            if (!mob.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(UUID)) {
                mob.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(new AttributeModifier(UUID, ID.getPath(), speedModifier, AttributeModifier.Operation.ADDITION));
                mob.setSprinting(true);
            }
        } else if (mob.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(UUID)) {
            mob.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(UUID);
            mob.setSprinting(false);
        }
        return false;
    }
}
