package org.confluence.mod.client.gui.bestiary;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.data.SingleJsonFileReloadListener;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.map.BestiaryEntry;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClientBestiaryEntry extends BestiaryEntry {
    public static final Codec<ClientBestiaryEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("order").forGetter(entry -> entry.order),
            ExtraCodecs.intRange(0, 5).fieldOf("rarity").forGetter(entry -> entry.rarity),
            ResourceLocation.CODEC.fieldOf("background").forGetter(entry -> entry.background),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(entry -> entry.description),
            EnvironmentEntry.CODEC.listOf().lenientOptionalFieldOf("environments", List.of()).forGetter(entry -> entry.environments)
    ).apply(instance, ClientBestiaryEntry::new));

    private final int order;
    private final Component displayName;
    private final int rarity;
    private final ResourceLocation background;
    private final Component description;
    private final List<EnvironmentEntry> environments;

    private Entity renderedEntity;

    public ClientBestiaryEntry(int order, int rarity, ResourceLocation background, Component description, List<EnvironmentEntry> environments) {
        this.order = order;
        this.displayName = type.getDescription();
        this.rarity = rarity;
        this.background = background;
        this.description = description;
        this.environments = environments;
    }

    public void createEntity(Level level) {
        this.renderedEntity = type.create(level);
        if (renderedEntity == null) {
            throw new IllegalArgumentException();
        }
    }

    public static class Loader extends SingleJsonFileReloadListener {
        private static Loader INSTANCE;

        private LinkedHashMap<EntityType<?>, ClientBestiaryEntry> entries = Maps.newLinkedHashMap();

        private Loader() {}

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> resourceList) {
            List<ClientBestiaryEntry> list = Lists.newArrayList();
            for (Map.Entry<ResourceLocation, JsonElement> entry : resourceList.entrySet()) {
                CODEC.parse(JsonOps.INSTANCE, entry.getValue()).ifSuccess(result -> {
                    result.type = BuiltInRegistries.ENTITY_TYPE.get(entry.getKey());
                    list.add(result);
                });
            }
            LinkedHashMap<EntityType<?>, ClientBestiaryEntry> map = Maps.newLinkedHashMap();
            list.stream().sorted(Comparator.comparingInt(entry -> entry.order))
                    .forEachOrdered(entry -> map.put(entry.type, entry));
            this.entries = map;
        }

        @Override
        protected ResourceLocation resourcePath() {
            return Confluence.asResource("bestiary");
        }

        @Override
        protected String identifier() {
            return "bestiary";
        }

        public LinkedHashMap<EntityType<?>, ClientBestiaryEntry> getEntries() {
            return entries;
        }

        public static Loader getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new Loader();
            }
            return INSTANCE;
        }
    }
}
