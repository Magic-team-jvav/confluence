package org.confluence.terra_curio.api.primitive;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terra_curio.TerraCurio;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class CombineRule<T, V extends PrimitiveValue<T>> {
    public static final Map<ResourceLocation, CombineRule<?, ?>> RULES = new Hashtable<>();

    public static final Codec<CombineRule<?, ?>> CODEC = ResourceLocation.CODEC.xmap(RULES::get, CombineRule::id); // 暂未使用

    public abstract T combine(T componentA, T componentB);

    public T combineValue(V valueA, V valueB) {
        return combine(valueA.get(), valueB.get());
    }

    public T combineFromValue(V valueA, T componentB) {
        return combine(valueA.get(), componentB);
    }

    public T combineWithValue(T componentA, V valueB) {
        return combine(componentA, valueB.get());
    }

    public abstract ResourceLocation id();

    @Deprecated(forRemoval = true)
    public String name() {
        return id().toString();
    }

    @Override
    public String toString() {
        return id().toString();
    }

    public static <T, V extends PrimitiveValue<T>> CombineRule<T, V> register(CombineRule<T, V> combineRule) {
        RULES.put(combineRule.id(), combineRule);
        return combineRule;
    }

    public static <T, V extends PrimitiveValue<T>> CombineRule<T, V> register(BiFunction<T, T, T> combineFunc, String name) {
        return register(combineFunc, TerraCurio.asResource(name));
    }

    public static <T, V extends PrimitiveValue<T>> CombineRule<T, V> register(BiFunction<T, T, T> combineFunc, ResourceLocation id) {
        CombineRule<T, V> combineRule = new CombineRule<>() {
            @Override
            public T combine(T componentA, T componentB) {
                return combineFunc.apply(componentA, componentB);
            }

            @Override
            public ResourceLocation id() {
                return id;
            }
        };
        if (RULES.put(id, combineRule) != null) {
            throw new IllegalArgumentException("Duplicated CombineRule with id '" + id + "'");
        }
        return combineRule;
    }
}
