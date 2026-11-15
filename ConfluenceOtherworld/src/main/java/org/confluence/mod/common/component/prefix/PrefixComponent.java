package org.confluence.mod.common.component.prefix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.jetbrains.annotations.Nullable;

public record PrefixComponent(
        PrefixType type,
        String name,
        AttributeModifiersValue modifiers,
        float manaCost,
        int additionalMana
) implements DataComponentType<PrefixComponent> {
    public static final Codec<PrefixComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PrefixType.CODEC.lenientOptionalFieldOf("type", PrefixType.UNKNOWN).forGetter(PrefixComponent::type),
            Codec.STRING.lenientOptionalFieldOf("name", "unknown").forGetter(PrefixComponent::name),
            AttributeModifiersValue.CODEC.lenientOptionalFieldOf("modifiers", AttributeModifiersValue.EMPTY).forGetter(PrefixComponent::modifiers),
            Codec.FLOAT.lenientOptionalFieldOf("mana_cost", 0.0F).forGetter(PrefixComponent::manaCost),
            Codec.INT.lenientOptionalFieldOf("additional_mana", 0).forGetter(PrefixComponent::additionalMana)
    ).apply(instance, PrefixComponent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, PrefixComponent> STREAM_CODEC = StreamCodec.composite(
            PrefixType.STREAM_CODEC, PrefixComponent::type,
            ByteBufCodecs.STRING_UTF8, PrefixComponent::name,
            AttributeModifiersValue.STREAM_CODEC, PrefixComponent::modifiers,
            ByteBufCodecs.FLOAT, PrefixComponent::manaCost,
            ByteBufCodecs.VAR_INT, PrefixComponent::additionalMana,
            PrefixComponent::new
    );

    public MutableComponent getName() {
        return Component.translatable("prefix.confluence." + name);
    }

    @Override
    public @Nullable Codec<PrefixComponent> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, PrefixComponent> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + modifiers.hashCode();
        result = 31 * result + Float.hashCode(manaCost);
        result = 31 * result + additionalMana;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        return o instanceof PrefixComponent(
                PrefixType type1, String name1, AttributeModifiersValue modifiers1, float cost,
                int mana
        ) &&
                mana == additionalMana && cost == manaCost && type1 == type && name1.equals(name) && modifiers1.equals(modifiers);
    }
}
