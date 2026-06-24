package org.confluence.mod.common.entity.ai.goal.behavior.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.confluence.mod.common.entity.ai.goal.behavior.BTNode;

public class SetAttributeAction extends BTNode {
    private final LivingEntity living;
    private final Attribute attribute;
    private final double value;

    public SetAttributeAction(LivingEntity living, Attribute attribute, double value) {
        this.living = living;
        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public BTStatus execute() {
        living.getAttribute(attribute).setBaseValue(value);
        return BTStatus.SUCCESS;
    }
}
