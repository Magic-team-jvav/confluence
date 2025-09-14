package org.confluence.mod.client.handler.bestiary;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.map.BestiaryEntry;

import java.util.Arrays;
import java.util.List;

public class ClientBestiaryEntry extends BestiaryEntry {
    // gui sprite
    public static final ResourceLocation DEFAULT_BACKGROUND = background("unknown");
    public static final ResourceLocation SURFACE_BACKGROUND = background("surface");
    public static final ResourceLocation THE_HALLOW_BACKGROUND = background("the_hallow");

    public static final Component DEFAULT_DESCRIPTION = Component.literal("???");
    public static final Codec<ClientBestiaryEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.lenientOptionalFieldOf("order", 1000000).forGetter(ClientBestiaryEntry::getOrder),
            ExtraCodecs.intRange(0, 5).lenientOptionalFieldOf("rarity", 0).forGetter(ClientBestiaryEntry::getRarity),
            ResourceLocation.CODEC.lenientOptionalFieldOf("background", DEFAULT_BACKGROUND).forGetter(ClientBestiaryEntry::getBackground),
            ComponentSerialization.CODEC.lenientOptionalFieldOf("description", DEFAULT_DESCRIPTION).forGetter(ClientBestiaryEntry::getDescription),
            FilterEntry.CODEC.listOf().lenientOptionalFieldOf("filters", List.of()).forGetter(ClientBestiaryEntry::getFilters)
    ).apply(instance, ClientBestiaryEntry::new));

    private final int order;
    private final int rarity;
    private final ResourceLocation background;
    private final Component description;
    private final List<FilterEntry> filters;

    private Component displayName;
    private Entity renderedEntity;

    public ClientBestiaryEntry() {
        this.order = 1000000;
        this.rarity = 0;
        this.background = DEFAULT_BACKGROUND;
        this.description = DEFAULT_DESCRIPTION;
        this.filters = List.of();
    }

    private ClientBestiaryEntry(int order, int rarity, ResourceLocation background, Component description, List<FilterEntry> filters) {
        this.order = order;
        this.rarity = rarity;
        this.background = background;
        this.description = description;
        this.filters = filters;
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

    public List<FilterEntry> getFilters() {
        return filters;
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

    public static ResourceLocation background(String path) {
        return Confluence.asResource("bestiary/background/" + path);
    }

    public static Builder builder(EntityType<?> entityType) {
        return new Builder(entityType);
    }

    public static class Builder {
        private final EntityType<?> entityType;
        private int order = 1000000;
        private int rarity = 0;
        private ResourceLocation background = DEFAULT_BACKGROUND;
        private Component description = DEFAULT_DESCRIPTION;
        private List<FilterEntry> filters = List.of();

        private Builder(EntityType<?> entityType) {
            this.entityType = entityType;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        /**
         * @param rarity [0, 5]
         */
        public Builder rarity(int rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder background(ResourceLocation background) {
            this.background = background;
            return this;
        }

        public Builder description(Component description) {
            this.description = description;
            return this;
        }

        public Builder filters(FilterEntry... entries) {
            this.filters = Arrays.stream(entries).toList();
            return this;
        }

        public ClientBestiaryEntry build() {
            ClientBestiaryEntry entry = new ClientBestiaryEntry(order, rarity, background, description, filters);
            entry.type = entityType;
            return entry;
        }
    }
}
