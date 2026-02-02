package org.confluence.terra_curio.api.primitive;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.List;

public record ComponentsValue(List<Component> components) implements PrimitiveValue<List<Component>> {
    public static final Codec<ComponentsValue> CODEC = ComponentSerialization.CODEC.listOf().xmap(ComponentsValue::new, ComponentsValue::get);
    public static final CombineRule<List<Component>, ComponentsValue> COMBINE_RULE = CombineRule.register(PrimitiveValue.identity(), "components_ability");

    @Override
    public List<Component> get() {
        return components;
    }

    @Override
    public Codec<ComponentsValue> codec() {
        return CODEC;
    }
}
