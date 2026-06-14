package org.confluence.mod.api.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.confluence.mod.client.effect.biome.BiomeSkyEffect;

import java.util.Map;

public class BiomeSkyEffectRegisterEvent extends Event implements IModBusEvent {
    private final Map<ResourceLocation, BiomeSkyEffect> effects;

    public BiomeSkyEffectRegisterEvent(Map<ResourceLocation, BiomeSkyEffect> effects) {
        this.effects = effects;
    }

    public void register(ResourceLocation id, BiomeSkyEffect effect) {
        if (effects.containsKey(id)) {
            throw new IllegalArgumentException("Duplicated biome sky effect key: " + id);
        }
        effects.put(id, effect);
    }
}
