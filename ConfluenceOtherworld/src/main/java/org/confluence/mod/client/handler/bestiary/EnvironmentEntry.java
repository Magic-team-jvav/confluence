package org.confluence.mod.client.handler.bestiary;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;

public record EnvironmentEntry(ResourceLocation texture, Component name) {
    public static final Codec<EnvironmentEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(EnvironmentEntry::texture),
            ComponentSerialization.CODEC.fieldOf("name").forGetter(EnvironmentEntry::name)
    ).apply(instance, EnvironmentEntry::new));
}
