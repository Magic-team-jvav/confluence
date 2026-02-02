package org.confluence.terra_curio.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class AfterAccessoryAbilitiesFlushedEvent extends LivingEvent {
    public AfterAccessoryAbilitiesFlushedEvent(LivingEntity entity) {
        super(entity);
    }
}
