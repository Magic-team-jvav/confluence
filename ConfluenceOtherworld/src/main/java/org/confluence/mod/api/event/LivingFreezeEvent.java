package org.confluence.mod.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LivingFreezeEvent extends LivingEvent implements ICancellableEvent {
    public LivingFreezeEvent(LivingEntity entity) {
        super(entity);
    }
}
