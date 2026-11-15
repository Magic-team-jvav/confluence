package org.confluence.mod.api.event;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.function.BiConsumer;

public class RegisterCustomDeathEffectEvent extends Event implements IModBusEvent {
    private final Reference2IntMap<EntityType<?>> bloodColor;
    private final Reference2ObjectMap<EntityType<?>, BiConsumer<LivingEntity, ClientLevel>> effects;

    public RegisterCustomDeathEffectEvent(Reference2IntMap<EntityType<?>> bloodColor, Reference2ObjectMap<EntityType<?>, BiConsumer<LivingEntity, ClientLevel>> effects) {
        this.bloodColor = bloodColor;
        this.effects = effects;
    }

    /// RGB
    public void addBloodColor(EntityType<?> type, int color) {
        bloodColor.put(type, color);
    }

    public void addCustomEffect(EntityType<?> type, BiConsumer<LivingEntity, ClientLevel> onDeath) {
        effects.put(type, onDeath);
    }
}
