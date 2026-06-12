package org.confluence.mod.api.event.bestiary;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.ICancellableEvent;

public class ToBeBestiaryEntryEvent extends LivingEvent implements ICancellableEvent {
    public ToBeBestiaryEntryEvent(LivingEntity entity) {
        super(entity);
    }
}
