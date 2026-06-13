package org.confluence.mod.api.event.bestiary;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class ToBeBestiaryEntryEvent extends LivingEvent {
    public ToBeBestiaryEntryEvent(LivingEntity entity) {
        super(entity);
    }
}
