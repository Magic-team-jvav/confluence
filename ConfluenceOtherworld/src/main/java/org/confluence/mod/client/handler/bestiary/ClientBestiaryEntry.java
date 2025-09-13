package org.confluence.mod.client.handler.bestiary;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.map.BestiaryEntry;

import java.util.List;

public class ClientBestiaryEntry extends BestiaryEntry {
    public static final ResourceLocation DEFAULT_BACKGROUND = Confluence.asResource("bestiary/unknown_background"); // gui sprite
    public static final Component DEFAULT_DESCRIPTION = Component.literal("???");
    public static final Codec<ClientBestiaryEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.lenientOptionalFieldOf("order", 1000000).forGetter(ClientBestiaryEntry::getOrder),
            ExtraCodecs.intRange(0, 5).lenientOptionalFieldOf("rarity", 0).forGetter(ClientBestiaryEntry::getRarity),
            ResourceLocation.CODEC.lenientOptionalFieldOf("background", DEFAULT_BACKGROUND).forGetter(ClientBestiaryEntry::getBackground),
            ComponentSerialization.CODEC.lenientOptionalFieldOf("description", DEFAULT_DESCRIPTION).forGetter(ClientBestiaryEntry::getDescription),
            EnvironmentEntry.CODEC.listOf().lenientOptionalFieldOf("environments", List.of()).forGetter(ClientBestiaryEntry::getEnvironments)
    ).apply(instance, ClientBestiaryEntry::new));

    private final int order;
    private final int rarity;
    private final ResourceLocation background;
    private final Component description;
    private final List<EnvironmentEntry> environments;

    private Component displayName;
    private Entity renderedEntity;

    public ClientBestiaryEntry() {
        this.order = 1000000;
        this.rarity = 0;
        this.background = DEFAULT_BACKGROUND;
        this.description = DEFAULT_DESCRIPTION;
        this.environments = List.of();
    }

    private ClientBestiaryEntry(int order, int rarity, ResourceLocation background, Component description, List<EnvironmentEntry> environments) {
        this.order = order;
        this.rarity = rarity;
        this.background = background;
        this.description = description;
        this.environments = environments;
    }

    public int getOrder() {
        return order;
    }

    public int getRarity() {
        return rarity;
    }

    public ResourceLocation getBackground() {
        return background;
    }

    public Component getDescription() {
        return description;
    }

    public List<EnvironmentEntry> getEnvironments() {
        return environments;
    }

    public Component getDisplayName() {
        if (displayName == null) {
            this.displayName = type.getDescription();
        }
        return displayName;
    }

    public Entity getRenderedEntity(Level level) {
        if (renderedEntity == null) {
            this.renderedEntity = type.create(level);
            if (renderedEntity == null) {
                throw new NullPointerException("Failed to create rendered entity from type " + type);
            }
        }
        return renderedEntity;
    }
}
