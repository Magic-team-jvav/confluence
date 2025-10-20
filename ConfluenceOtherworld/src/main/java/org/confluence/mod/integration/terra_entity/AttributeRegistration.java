package org.confluence.mod.integration.terra_entity;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

class AttributeRegistration {
    private final EntityAttributeModificationEvent event;
    private Holder<Attribute> current;

    AttributeRegistration(EntityAttributeModificationEvent event) {
        this.event = event;
    }

    AttributeRegistration set(Holder<Attribute> attribute) {
        this.current = attribute;
        return this;
    }

    AttributeRegistration register(EntityType<? extends LivingEntity> type, double value) {
        event.add(type, current, value);
        return this;
    }
}
