package org.confluence.mod.common.component.prefix;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import com.google.common.collect.ImmutableListMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

public record PrefixComponent(
        PrefixType type,
        String name,
        AttributeModifiersValue modifiers,
        float manaCost
) {
    public static final Codec<PrefixComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PortCodecExtension.lenientOptionalFieldOf(PrefixType.CODEC, "type", PrefixType.UNKNOWN).forGetter(PrefixComponent::type),
            PortCodecExtension.lenientOptionalFieldOf(Codec.STRING, "name", "unknown").forGetter(PrefixComponent::name),
            PortCodecExtension.lenientOptionalFieldOf(AttributeModifiersValue.CODEC, "modifiers", AttributeModifiersValue.EMPTY).forGetter(PrefixComponent::modifiers),
            PortCodecExtension.lenientOptionalFieldOf(Codec.FLOAT, "mana_cost", 0.0F).forGetter(PrefixComponent::manaCost),
            PortCodecExtension.lenientOptionalFieldOf(Codec.INT, "additional_mana", 0).forGetter(prefix -> 0)
    ).apply(instance, (type, name, modifiers, manaCost, legacyMana) -> {
        if (legacyMana != 0 && !modifiers.get().containsKey(ConfluenceMagicLib.MAX_MANA.get())) {
            modifiers = new AttributeModifiersValue(ImmutableListMultimap.<Attribute, AttributeModifier>builder()
                    .putAll(modifiers.get())
                    .put(ConfluenceMagicLib.MAX_MANA.get(), ModPrefix.Accessory.ARCANE.createModifier(legacyMana, PortAttributeModifier.Operation.ADD_VALUE))
                    .build());
        }
        return new PrefixComponent(type, name, modifiers, manaCost);
    }));
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, PrefixComponent> STREAM_CODEC = PortStreamCodec.composite(
            PrefixType.STREAM_CODEC, PrefixComponent::type,
            PortByteBufCodecs.STRING_UTF8, PrefixComponent::name,
            AttributeModifiersValue.STREAM_CODEC, PrefixComponent::modifiers,
            PortByteBufCodecs.FLOAT, PrefixComponent::manaCost,
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
        return result;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof PrefixComponent c &&
                c.manaCost == manaCost &&
                c.type == type &&
                c.name.equals(name) &&
                c.modifiers.equals(modifiers));
    }
}
