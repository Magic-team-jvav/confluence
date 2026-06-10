package org.confluence.mod.common.component.prefix;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record PrefixComponent(
        PrefixType type,
        String name,
        AttributeModifiersValue modifiers,
        float manaCost,
        int additionalMana,
        int tier,
        float value
) {
    public static final Codec<PrefixComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PortCodecExtension.lenientOptionalFieldOf(PrefixType.CODEC, "type", PrefixType.UNKNOWN).forGetter(PrefixComponent::type),
            PortCodecExtension.lenientOptionalFieldOf(Codec.STRING, "name", "unknown").forGetter(PrefixComponent::name),
            PortCodecExtension.lenientOptionalFieldOf(AttributeModifiersValue.CODEC, "modifiers", AttributeModifiersValue.EMPTY).forGetter(PrefixComponent::modifiers),
            PortCodecExtension.lenientOptionalFieldOf(Codec.FLOAT, "mana_cost", 0.0F).forGetter(PrefixComponent::manaCost),
            PortCodecExtension.lenientOptionalFieldOf(Codec.INT, "additional_mana", 0).forGetter(PrefixComponent::additionalMana),
            PortCodecExtension.lenientOptionalFieldOf(Codec.INT, "tier", 0).forGetter(PrefixComponent::tier),
            PortCodecExtension.lenientOptionalFieldOf(Codec.FLOAT, "value", 0.0F).forGetter(PrefixComponent::value)
    ).apply(instance, PrefixComponent::new));
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, PrefixComponent> STREAM_CODEC = LibStreamCodecUtils.composite(
            PrefixType.STREAM_CODEC, PrefixComponent::type,
            PortByteBufCodecs.STRING_UTF8, PrefixComponent::name,
            AttributeModifiersValue.STREAM_CODEC, PrefixComponent::modifiers,
            PortByteBufCodecs.FLOAT, PrefixComponent::manaCost,
            PortByteBufCodecs.VAR_INT, PrefixComponent::additionalMana,
            PortByteBufCodecs.VAR_INT, PrefixComponent::tier,
            PortByteBufCodecs.FLOAT, PrefixComponent::value,
            PrefixComponent::new
    );

    public MutableComponent getName() {
        return Component.translatable("prefix.confluence." + name);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + modifiers.hashCode();
        result = 31 * result + Float.hashCode(manaCost);
        result = 31 * result + additionalMana;
        result = 31 * result + tier;
        result = 31 * result + Float.hashCode(value);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof PrefixComponent c &&
                c.additionalMana == additionalMana &&
                c.tier == tier &&
                c.value == value &&
                c.manaCost == manaCost &&
                c.type == type &&
                c.name.equals(name) &&
                c.modifiers.equals(modifiers));
    }
}
