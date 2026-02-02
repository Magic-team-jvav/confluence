package org.confluence.terra_curio.api.primitive;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.lib.util.LibCodecUtils;

import java.util.*;

public record AttributeModifiersValue(ImmutableListMultimap<Holder<Attribute>, AttributeModifier> value) implements PrimitiveValue<ImmutableListMultimap<Holder<Attribute>, AttributeModifier>> {
    public static final AttributeModifiersValue EMPTY = new AttributeModifiersValue(ImmutableListMultimap.of());
    public static final Codec<AttributeModifiersValue> CODEC = LibCodecUtils.multimap(Attribute.CODEC, AttributeModifier.CODEC)
            .xmap(AttributeModifiersValue::new, AttributeModifiersValue::get);
    public static final StreamCodec<RegistryFriendlyByteBuf, AttributeModifiersValue> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public AttributeModifiersValue decode(RegistryFriendlyByteBuf buffer) {
            int size = buffer.readInt();
            ImmutableListMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableListMultimap.builder();
            for (int i = 0; i < size; i++) {
                Holder<Attribute> holder = Attribute.STREAM_CODEC.decode(buffer);
                int amount = buffer.readInt();
                for (int j = 0; j < amount; j++) {
                    builder.put(holder, AttributeModifier.STREAM_CODEC.decode(buffer));
                }
            }
            return new AttributeModifiersValue(builder.build());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, AttributeModifiersValue value) {
            buffer.writeInt(value.value.keySet().size());
            for (Map.Entry<Holder<Attribute>, Collection<AttributeModifier>> entry : value.value.asMap().entrySet()) {
                Attribute.STREAM_CODEC.encode(buffer, entry.getKey());
                buffer.writeInt(entry.getValue().size());
                for (AttributeModifier modifier : entry.getValue()) {
                    AttributeModifier.STREAM_CODEC.encode(buffer, modifier);
                }
            }
        }
    };
    public static final CombineRule<ImmutableListMultimap<Holder<Attribute>, AttributeModifier>, AttributeModifiersValue> MERGE = CombineRule.register(
            (a, b) -> ImmutableListMultimap.<Holder<Attribute>, AttributeModifier>builder().putAll(a).putAll(b).build(),
            "attributes_modifiers_merge");

    @Override
    public ImmutableListMultimap<Holder<Attribute>, AttributeModifier> get() {
        return value;
    }

    @Override
    public Codec<AttributeModifiersValue> codec() {
        return CODEC;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        for (Map.Entry<Holder<Attribute>, Collection<AttributeModifier>> entry : value.asMap().entrySet()) {
            list.add(entry.getKey().unwrapKey().orElseThrow().location().toString());
            for (AttributeModifier modifier : entry.getValue()) {
                list.add("    " + modifier);
            }
        }
        return list;
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof AttributeModifiersValue(ImmutableListMultimap<Holder<Attribute>, AttributeModifier> value1) && value1.equals(value));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static AttributeModifiersValue simple(Holder<Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        return new AttributeModifiersValue(ImmutableListMultimap.of(attribute, new AttributeModifier(id, amount, operation)));
    }

    public static class Builder {
        private final ImmutableListMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableListMultimap.builder();

        Builder() {}

        public Builder add(Holder<Attribute> attribute, AttributeModifier... modifiers) {
            builder.putAll(attribute, modifiers);
            return this;
        }

        public Builder add(Holder<Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation) {
            builder.put(attribute, new AttributeModifier(id, amount, operation));
            return this;
        }

        public Builder addAll(Multimap<Holder<Attribute>, AttributeModifier> multimap) {
            builder.putAll(multimap);
            return this;
        }

        public AttributeModifiersValue build() {
            return new AttributeModifiersValue(builder.build());
        }
    }
}
