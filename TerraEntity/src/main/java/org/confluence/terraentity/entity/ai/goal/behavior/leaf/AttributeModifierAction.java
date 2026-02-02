package org.confluence.terraentity.entity.ai.goal.behavior.leaf;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;

public abstract class AttributeModifierAction extends BTNode {

    final LivingEntity living;
    final Holder<Attribute> attributeHolder;
    final ResourceLocation id;

    private AttributeModifierAction(LivingEntity living, Holder<Attribute> attributeHolder, ResourceLocation id) {
        this.living = living;
        this.attributeHolder = attributeHolder;
        this.id = id;
    }

    public static class Add extends AttributeModifierAction {
        final double value;
        final AttributeModifier.Operation operation;
        public Add(LivingEntity living, Holder<Attribute> attributeHolder, ResourceLocation id, double value, AttributeModifier.Operation operation) {
            super(living, attributeHolder, id);
            this.value = value;
            this.operation = operation;
        }
        @Override
        public BTStatus execute() {
            this.living.getAttribute(this.attributeHolder).addOrUpdateTransientModifier(new AttributeModifier(id, value, operation));
            return null;
        }
    }

    public static class Remove extends AttributeModifierAction {
        public Remove(LivingEntity living, Holder<Attribute> attributeHolder, ResourceLocation id) {
            super(living, attributeHolder, id);
        }
        @Override
        public BTStatus execute() {
            this.living.getAttribute(this.attributeHolder).removeModifier(id);
            return null;
        }
    }
}
