package org.confluence.mod.common.component.prefix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PrefixComponent(PrefixType type, String name, AttributeModifiersValue modifiers, float manaCost, int additionalMana, int tier, float value) implements DataComponentType<PrefixComponent> {
    public static final Codec<PrefixComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PrefixType.CODEC.lenientOptionalFieldOf("type", PrefixType.UNKNOWN).forGetter(PrefixComponent::type),
            Codec.STRING.lenientOptionalFieldOf("name", "unknown").forGetter(PrefixComponent::name),
            AttributeModifiersValue.CODEC.lenientOptionalFieldOf("modifiers", AttributeModifiersValue.EMPTY).forGetter(PrefixComponent::modifiers),
            Codec.FLOAT.lenientOptionalFieldOf("mana_cost", 0.0F).forGetter(PrefixComponent::manaCost),
            Codec.INT.lenientOptionalFieldOf("additional_mana", 0).forGetter(PrefixComponent::additionalMana),
            Codec.INT.lenientOptionalFieldOf("tier", 0).forGetter(PrefixComponent::tier),
            Codec.FLOAT.lenientOptionalFieldOf("value", 0.0F).forGetter(PrefixComponent::value)
    ).apply(instance, PrefixComponent::new));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, PrefixComponent> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull PrefixComponent decode(@NotNull RegistryFriendlyByteBuf buffer) {
            PrefixType t1 = PrefixType.STREAM_CODEC.decode(buffer);
            String t2 = ByteBufCodecs.STRING_UTF8.decode(buffer);
            AttributeModifiersValue t3 = AttributeModifiersValue.STREAM_CODEC.decode(buffer);
            float t4 = buffer.readFloat();
            int t5 = buffer.readInt();
            int t6 = buffer.readInt();
            float t7 = buffer.readFloat();
            return new PrefixComponent(t1, t2, t3, t4, t5, t6, t7);
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buffer, PrefixComponent component) {
            PrefixType.STREAM_CODEC.encode(buffer, component.type);
            ByteBufCodecs.STRING_UTF8.encode(buffer, component.name);
            AttributeModifiersValue.STREAM_CODEC.encode(buffer, component.modifiers);
            buffer.writeFloat(component.manaCost);
            buffer.writeInt(component.additionalMana);
            buffer.writeInt(component.tier);
            buffer.writeFloat(component.value);
        }
    };

    @Override
    public @Nullable Codec<PrefixComponent> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, PrefixComponent> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        return o instanceof PrefixComponent(PrefixType type1, String name1, AttributeModifiersValue modifiers1, float cost, int mana, int tier1, float value1) &&
                mana == additionalMana && tier1 == tier && value1 == value && cost == manaCost && type1 == type && name1.equals(name) && modifiers1.equals(modifiers);
    }
}
