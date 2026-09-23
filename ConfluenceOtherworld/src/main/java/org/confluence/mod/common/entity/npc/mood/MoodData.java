package org.confluence.mod.common.entity.npc.mood;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import org.confluence.mod.Confluence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// 每个 NPC 的邻居与群系偏好由同一份 data/confluence/npc/moods/<npc>.json 提供。
public final class MoodData {
    private static volatile Map<EntityType<?>, Preferences> preferences = Map.of();

    private MoodData() {}

    public static Map<EntityType<?>, Mood> getMoodsFor(EntityType<?> npcType) {
        Preferences entry = preferences.get(npcType);
        return entry == null ? Map.of() : entry.neighbors();
    }

    public static List<BiomeEntry> getBiomeMoodsFor(EntityType<?> npcType) {
        Preferences entry = preferences.get(npcType);
        return entry == null ? List.of() : entry.biomes();
    }

    public record Entry(Mood mood, EntityType<?> target) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(b -> b.group(
                Mood.CODEC.fieldOf("mood").forGetter(Entry::mood),
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("target").forGetter(Entry::target)
        ).apply(b, Entry::new));
    }

    public record BiomeEntry(Mood mood, TagKey<Biome> biome) {
        public static final Codec<BiomeEntry> CODEC = RecordCodecBuilder.create(b -> b.group(
                Mood.CODEC.fieldOf("mood").forGetter(BiomeEntry::mood),
                TagKey.hashedCodec(Registries.BIOME).fieldOf("biome").forGetter(BiomeEntry::biome)
        ).apply(b, BiomeEntry::new));
    }

    public record Profile(List<Entry> neighbors, List<BiomeEntry> biomes) {
        public static final Codec<Profile> CODEC = RecordCodecBuilder.create(b -> b.group(
                Entry.CODEC.listOf().optionalFieldOf("neighbors", List.of()).forGetter(Profile::neighbors),
                BiomeEntry.CODEC.listOf().optionalFieldOf("biomes", List.of()).forGetter(Profile::biomes)
        ).apply(b, Profile::new));
    }

    private record Preferences(Map<EntityType<?>, Mood> neighbors, List<BiomeEntry> biomes) {}

    public static final class Loader extends SimpleJsonResourceReloadListener {
        public Loader() {
            super(new com.google.gson.GsonBuilder().create(), "npc/moods");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
            Map<EntityType<?>, Preferences> loaded = new HashMap<>();
            List<String> errors = new ArrayList<>();
            for (var resource : resources.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
                ResourceLocation npcId = resource.getKey();
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(npcId).orElse(null);
                if (type == null) {
                    errors.add("Cannot load NPC mood " + npcId + ": entity type is not registered");
                    continue;
                }
                DataResult<Profile> decoded = Profile.CODEC.parse(JsonOps.INSTANCE, resource.getValue());
                if (decoded.result().isEmpty()) {
                    errors.add("Cannot load NPC mood " + npcId + ": "
                            + decoded.error().map(DataResult.PartialResult::message).orElse("unknown decode error"));
                    continue;
                }
                Profile profile = decoded.result().orElseThrow();
                Map<EntityType<?>, Mood> neighbors = new HashMap<>();
                for (Entry entry : profile.neighbors()) {
                    if (neighbors.putIfAbsent(entry.target(), entry.mood()) != null) {
                        errors.add("Cannot load NPC mood " + npcId + ": duplicate target " + BuiltInRegistries.ENTITY_TYPE.getKey(entry.target()));
                    }
                }
                loaded.put(type, new Preferences(Map.copyOf(neighbors), List.copyOf(profile.biomes())));
            }
            if (!errors.isEmpty()) {
                errors.forEach(Confluence.LOGGER::error);
                Confluence.LOGGER.error("NPC mood reload was rejected; the previous valid table remains active");
                return;
            }
            preferences = Map.copyOf(loaded);
        }
    }
}
