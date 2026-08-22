package org.confluence.mod.common.entity.npc;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record NPCNames(Map<String, Float> namesWeights) {
    public static final Codec<NPCNames> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("names_weights").forGetter(NPCNames::namesWeights)
    ).apply(instance, NPCNames::new));
    public static final Codec<Map<EntityType<?>, NPCNames>> TABLE_CODEC =
            Codec.unboundedMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), CODEC);

    public static NPCNames of(Map<String, Float> names) {
        return new NPCNames(names);
    }

    public static final class Loader extends SimpleJsonResourceReloadListener {
        private static final ResourceLocation PATH = Confluence.asResource("names");
        private static Loader instance;
        private Map<EntityType<?>, NPCNames> names = ImmutableMap.of();

        private Loader() {
            super(new GsonBuilder().create(), "npc");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
            JsonElement json = map.get(PATH);
            if (json == null) {
                names = ImmutableMap.of();
                Confluence.LOGGER.info("Loaded 0 NPC name tables");
                return;
            }
            DataResult<Map<EntityType<?>, NPCNames>> decoded = TABLE_CODEC.parse(JsonOps.INSTANCE, json);
            var parsed = decoded.result();
            if (parsed.isEmpty()) {
                String reason = decoded.error().map(DataResult.PartialResult::message).orElse("unknown decode error");
                Confluence.LOGGER.error("Cannot load npc/names.json: {}", reason);
                Confluence.LOGGER.error("NPC name reload was rejected; the previous valid table remains active");
                return;
            }
            names = ImmutableMap.copyOf(parsed.get());
            Confluence.LOGGER.info("Loaded {} NPC name tables", names.size());
        }

        @Nullable
        public String getRandomName(EntityType<?> type, RandomSource random) {
            NPCNames table = names.get(type);
            if (table == null || table.namesWeights.isEmpty()) return null;
            float totalWeight = 0.0F;
            for (float weight : table.namesWeights.values()) {
                if (weight > 0.0F) totalWeight += weight;
            }
            if (totalWeight <= 0.0F) return null;
            float choice = random.nextFloat() * totalWeight;
            for (Map.Entry<String, Float> entry : table.namesWeights.entrySet()) {
                if (entry.getValue() <= 0.0F) continue;
                choice -= entry.getValue();
                if (choice < 0.0F) return entry.getKey();
            }
            return table.namesWeights.keySet().iterator().next();
        }

        public static Loader getInstance() {
            if (instance == null) instance = new Loader();
            return instance;
        }
    }
}
