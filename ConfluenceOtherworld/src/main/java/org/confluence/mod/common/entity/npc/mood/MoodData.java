package org.confluence.mod.common.entity.npc.mood;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.Confluence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 从 {@code data/confluence/npc/moods.json} 加载心情数据。
 * 格式：{@code { "entity_id": [ {"mood": "like", "target": "confluence:guide"}, ... ] }}
 */
public final class MoodData {
    static final Map<EntityType<?>, Mood> DEFAULT_MOODS = Map.of();

    private static Map<EntityType<?>, Map<EntityType<?>, Mood>> moodTable = new HashMap<>();

    public static Map<EntityType<?>, Mood> getMoodsFor(EntityType<?> npcType) {
        return moodTable.getOrDefault(npcType, DEFAULT_MOODS);
    }

    public record Entry(Mood mood, EntityType<?> target) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(b -> b.group(
                Mood.CODEC.fieldOf("mood").forGetter(Entry::mood),
                BuiltInRegistries.ENTITY_TYPE.byNameCodec()
                        .fieldOf("target").forGetter(Entry::target)
        ).apply(b, Entry::new));
    }

    public static final class Loader extends SimpleJsonResourceReloadListener {
        private static final Map<EntityType<?>, Map<EntityType<?>, Mood>> EMPTY = Map.of();

        public Loader() {
            super(new com.google.gson.GsonBuilder().create(), "npc/moods");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager rm, ProfilerFiller pf) {
            Codec<List<Entry>> listCodec = Entry.CODEC.listOf();
            Map<EntityType<?>, Map<EntityType<?>, Mood>> newTable = new HashMap<>();
            for (var entry : map.entrySet()) {
                if (!Confluence.MODID.equals(entry.getKey().getNamespace())) continue;
                BuiltInRegistries.ENTITY_TYPE.getOptional(entry.getKey()).ifPresent(npcType -> {
                    listCodec.parse(com.mojang.serialization.JsonOps.INSTANCE, entry.getValue())
                            .result().ifPresent(entries -> {
                                Map<EntityType<?>, Mood> moods = new HashMap<>();
                                for (Entry e : entries) moods.put(e.target(), e.mood());
                                newTable.put(npcType, moods);
                            });
                });
            }
            moodTable = newTable.isEmpty() ? EMPTY : newTable;
        }
    }
}
