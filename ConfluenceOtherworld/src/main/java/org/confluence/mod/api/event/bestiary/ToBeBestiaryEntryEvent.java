package org.confluence.mod.api.event.bestiary;

import org.confluence.mod.common.data.saved.Bestiary;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class ToBeBestiaryEntryEvent extends LivingEvent implements ICancellableEvent {
    public ToBeBestiaryEntryEvent(LivingEntity entity) {
        super(entity);
    }
}
