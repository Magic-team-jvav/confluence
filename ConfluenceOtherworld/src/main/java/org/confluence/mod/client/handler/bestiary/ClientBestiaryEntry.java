package org.confluence.mod.client.handler.bestiary;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.bestiary.RegisterCustomBestiaryEntryRendererEvent;
import org.confluence.mod.common.data.saved.BestiaryEntry;
import org.confluence.mod.common.entity.BestiaryEntryDisplay;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.mixin.accessor.EntityAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "OptionalUsedAsFieldOrParameterType"})
public class ClientBestiaryEntry extends BestiaryEntry {
    public static final ResourceLocation SURFACE = background("surface");
    public static final ResourceLocation SURFACE_SUN = background("surface_sun");
    public static final ResourceLocation SURFACE_NIGHTTIME = background("surface_nighttime");
    public static final ResourceLocation SURFACE_MOON = background("surface_moon");
    public static final ResourceLocation SURFACE_RAIN = background("surface_rain");
    public static final ResourceLocation SURFACE_NIGHTTIME_RAIN = background("surface_nighttime_rain");
    public static final ResourceLocation GRAVEYARD = background("graveyard");
    public static final ResourceLocation BLOOD_MOON = background("blood_moon");
    public static final ResourceLocation ECLIPSE = background("eclipse");
    public static final ResourceLocation PUMPKIN_MOON = background("pumpkin_moon");
    public static final ResourceLocation FROST_MOON = background("frost_moon");
    public static final ResourceLocation SKY = background("sky");
    public static final ResourceLocation UNDERGROUND = background("underground");
    public static final ResourceLocation CAVE = background("cave");
    public static final ResourceLocation OCEAN = background("ocean");
    public static final ResourceLocation THE_JUNGLE = background("the_jungle");
    public static final ResourceLocation THE_JUNGLE_SUN = background("the_jungle_sun");
    public static final ResourceLocation UNDERGROUND_JUNGLE = background("underground_jungle");
    public static final ResourceLocation THE_JUNGLE_MOON = background("the_jungle_moon");
    public static final ResourceLocation SNOW = background("snow");
    public static final ResourceLocation SNOW_MOON = background("snow_moon");
    public static final ResourceLocation UNDERGROUND_SNOW = background("underground_snow");
    public static final ResourceLocation BLIZZARD = background("blizzard");
    public static final ResourceLocation GLOWING_MUSHROOM = background("glowing_mushroom");
    public static final ResourceLocation DESERT = background("desert");
    public static final ResourceLocation DESERT_SUN = background("desert_sun");
    public static final ResourceLocation UNDERGROUND_DESERT = background("underground_desert");
    public static final ResourceLocation SANDSTORM = background("underground_desert");
    public static final ResourceLocation THE_NETHER = background("the_nether");
    public static final ResourceLocation MARBLE = background("marble");
    public static final ResourceLocation GRANITE = background("granite");
    public static final ResourceLocation SPIDER_NEST = background("spider_nest");
    public static final ResourceLocation METEOR = background("meteor");
    public static final ResourceLocation THE_CORRUPTION = background("the_corruption");
    public static final ResourceLocation UNDERGROUND_CORRUPTION = background("underground_corruption");
    public static final ResourceLocation CORRUPT_DESERT = background("corrupt_desert");
    public static final ResourceLocation CORRUPT_CAVE_DESERT = background("corrupt_cave_desert");
    public static final ResourceLocation CORRUPT_ICE = background("corrupt_ice");
    public static final ResourceLocation THE_CRIMSON = background("the_crimson");
    public static final ResourceLocation UNDERGROUND_CRIMSON = background("underground_crimson");
    public static final ResourceLocation CRIMSON_DESERT = background("crimson_desert");
    public static final ResourceLocation CRIMSON_CAVE_DESERT = background("crimson_cave_desert");
    public static final ResourceLocation CRIMSON_ICE = background("crimson_ice");
    public static final ResourceLocation THE_HALLOW = background("the_hallow");
    public static final ResourceLocation THE_HALLOW_SUN = background("the_hallow_sun");
    public static final ResourceLocation THE_HALLOW_MOON = background("the_hallow_moon");
    public static final ResourceLocation THE_HALLOW_RAIN = background("the_hallow_rain");
    public static final ResourceLocation UNDERGROUND_HALLOW = background("underground_hallow");
    public static final ResourceLocation HALLOW_DESERT = background("hallow_desert");
    public static final ResourceLocation HALLOW_CAVE_DESERT = background("hallow_cave_desert");
    public static final ResourceLocation HALLOW_ICE = background("hallow_ice");
    public static final ResourceLocation THE_DUNGEON = background("the_dungeon");
    public static final ResourceLocation THE_TEMPLE = background("the_temple");
    public static final ResourceLocation SOLAR_PILLAR = background("solar_pillar");
    public static final ResourceLocation VORTEX_PILLAR = background("vortex_pillar");
    public static final ResourceLocation NEBULA_PILLAR = background("nebula_pillar");
    public static final ResourceLocation STARDUST_PILLAR = background("stardust_pillar");

    public static final ResourceLocation THE_END = background("the_end");

    public static final Component UNKNOWN = Component.literal("???");
    public static final Codec<ClientBestiaryEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(entry -> entry.type),
            Codec.INT.lenientOptionalFieldOf("order", 1000000).forGetter(entry -> entry.order),
            ExtraCodecs.intRange(0, 5).lenientOptionalFieldOf("rarity", 1).forGetter(entry -> entry.rarity),
            ResourceLocation.CODEC.lenientOptionalFieldOf("background", SURFACE).forGetter(entry -> entry.background),
            ComponentSerialization.CODEC.lenientOptionalFieldOf("description", UNKNOWN).forGetter(entry -> entry.description),
            LibCodecUtils.homogenousList(FilterEntry.CODEC, false).lenientOptionalFieldOf("filters", List.of()).forGetter(entry -> entry.filters),
            TagParser.LENIENT_CODEC.lenientOptionalFieldOf("entity_nbt").forGetter(entry -> entry.entityNbt)
    ).apply(instance, ClientBestiaryEntry::new));

    public final int order;
    public final int rarity;
    public final ResourceLocation background;
    public final Component description;
    public final List<FilterEntry> filters;
    public final Optional<CompoundTag> entityNbt;

    private transient Component displayName;
    private transient LivingEntity renderedEntity;

    public ClientBestiaryEntry() {
        this.order = 1000000;
        this.rarity = 1;
        this.background = SURFACE;
        this.description = UNKNOWN;
        this.filters = List.of();
        this.entityNbt = Optional.empty();
    }

    private ClientBestiaryEntry(EntityType<?> type, int order, int rarity, ResourceLocation background, Component description, List<FilterEntry> filters, Optional<CompoundTag> entityNbt) {
        this.type = type;
        this.order = order;
        this.rarity = rarity;
        this.background = background;
        this.description = description;
        this.filters = filters;
        this.entityNbt = entityNbt;
    }

    public Component getDisplayName() {
        if (displayName == null) {
            this.displayName = Component.translatable(key);
        }
        return displayName;
    }

    public void resetRenderedEntity() {
        this.renderedEntity = null;
    }

    public @Nullable LivingEntity getRenderedEntity(@Nullable Level level) {
        if (renderedEntity == null && level != null) {
            if (type.create(level) instanceof LivingEntity living) {
                if (RegisterCustomBestiaryEntryRendererEvent.hasRenderer(key)) {
                    BestiaryEntryDisplay entity = new BestiaryEntryDisplay(ModEntities.BESTIARY_ENTRY_DISPLAY.get(), level);
                    entity.setDelegate(key, living);
                    this.renderedEntity = entity;
                } else {
                    this.renderedEntity = living;
                }
                entityNbt.ifPresent(nbt -> ((EntityAccessor) renderedEntity).callReadAdditionalSaveData(nbt));
            } else {
                throw new NullPointerException("Failed to create rendered entity from type " + key);
            }
        }
        return renderedEntity;
    }

    public void updateUnlockedProgress(@Nullable Level level) {
        if (isLocked() || isCompleted()) return;
        LivingEntity living = getRenderedEntity(level);
        if (living instanceof BestiaryEntryDisplay display) {
            living = display.getDelegate();
        }
        updateUnlockedProgress(living);
    }

    public static ResourceLocation background(String path) {
        return Confluence.asResource("bestiary/background/" + path);
    }

    public static Builder builderc(EntityType<?> type, String key) {
        return new Builder(type, key);
    }

    @Override
    public ClientBestiaryEntry copy() {
        ClientBestiaryEntry entry = new ClientBestiaryEntry(
                type,
                order,
                rarity,
                background,
                description,
                filters,
                entityNbt
        );
        entry.key = key;
        return entry;
    }

    public static class Builder {
        private final EntityType<?> type;
        private final String key;
        private int order = 1000000;
        private int rarity = 1;
        private ResourceLocation background = SURFACE;
        private Component description = UNKNOWN;
        private List<FilterEntry> filters = List.of();
        private CompoundTag entityNbt;

        private Builder(EntityType<?> type, String key) {
            this.type = type;
            this.key = key;
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

        public Builder entityNbt(Consumer<CompoundTag> consumer) {
            CompoundTag nbt = new CompoundTag();
            consumer.accept(nbt);
            this.entityNbt = nbt;
            return this;
        }

        public Builder entityNbt(CompoundTag nbt) {
            this.entityNbt = nbt;
            return this;
        }

        public ClientBestiaryEntry build() {
            ClientBestiaryEntry entry = new ClientBestiaryEntry(type, order, rarity, background, description, filters, Optional.ofNullable(entityNbt));
            entry.key = key;
            return entry;
        }
    }
}
