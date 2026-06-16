package org.confluence.mod.integration.terra_entity;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.mesdag.portlib.event.entity.PortEntityAttributeModificationEvent;

public class AttributeRegistration {
    private final PortEntityAttributeModificationEvent event;
    private Holder<Attribute> current;

    public AttributeRegistration(PortEntityAttributeModificationEvent event) {
        this.event = event;
    }

    public AttributeRegistration set(Holder<Attribute> attribute) {
        this.current = attribute;
        return this;
    }

    public AttributeRegistration register(EntityType<? extends LivingEntity> type, double value) {
        event.add(type, current, value);
        return this;
    }
}
