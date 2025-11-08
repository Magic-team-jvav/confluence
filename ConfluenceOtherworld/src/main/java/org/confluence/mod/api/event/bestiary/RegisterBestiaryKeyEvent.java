package org.confluence.mod.api.event.bestiary;

import com.google.common.collect.Maps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.VariantHolder;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.confluence.terraentity.api.entity.IVariant;

import java.util.Map;
import java.util.function.Function;

/**
 * 仅需要注册特殊键，如带有变种的生物
 */
public class RegisterBestiaryKeyEvent extends Event implements IModBusEvent {
    private static final Map<EntityType<?>, Factory<?>> factories = Maps.newIdentityHashMap();

    public <T extends LivingEntity> void register(EntityType<T> type, Factory<T> factory) {
        if (!factories.containsKey(type)) {
            factories.put(type, factory);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String getKey(LivingEntity living) {
        EntityType type = living.getType();
        Factory factory = factories.get(type);
        if (factory == null) return type.getDescriptionId();
        return factory.get(type, living);
    }

    public static <V, T extends LivingEntity & VariantHolder<V>> Factory<T> vanillaVariant(Function<V, String> toString) {
        return (type, living) -> type.getDescriptionId() + '.' + toString.apply(living.getVariant());
    }

    public static <V, T extends LivingEntity & IVariant<V>> Factory<T> terraVariant(Function<V, String> toString) {
        return (type, living) -> type.getDescriptionId() + '.' + toString.apply(living.getTEVariant());
    }

    @FunctionalInterface
    public interface Factory<T extends Entity> {
        String get(EntityType<T> type, T living);
    }
}
