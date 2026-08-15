package org.confluence.mod.common.entity.npc.mood;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.Confluence;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 加载 NPC 心情数据。
 *
 * <p>每个文件对应同名 NPC 实体，例如
 * {@code data/confluence/npc/moods/merchant.json} 对应 {@code confluence:merchant}。
 * 附属模组可以在自己的命名空间下以相同规则追加 NPC。重载采用整批事务：任一文件损坏、
 * 实体未注册或目标重复时都保留上一份完整表，避免运行中的 NPC 读取到半份新数据。</p>
 */
public final class MoodData {
    static final Map<EntityType<?>, Mood> DEFAULT_MOODS = Map.of();

    private static volatile Map<EntityType<?>, Map<EntityType<?>, Mood>> moodTable = Map.of();

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
            List<String> errors = new ArrayList<>();
            for (var resource : map.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .toList()) {
                ResourceLocation npcId = resource.getKey();
                EntityType<?> npcType = BuiltInRegistries.ENTITY_TYPE.getOptional(npcId)
                        .orElse(null);
                if (npcType == null) {
                    errors.add("Cannot load NPC mood " + npcId + ": entity type is not registered");
                    continue;
                }

                DataResult<List<Entry>> decoded = listCodec.parse(JsonOps.INSTANCE, resource.getValue());
                var entries = decoded.result();
                if (entries.isEmpty()) {
                    String reason = decoded.error()
                            .map(DataResult.PartialResult::message)
                            .orElse("unknown decode error");
                    errors.add("Cannot load NPC mood " + npcId + ": " + reason);
                    continue;
                }

                Map<EntityType<?>, Mood> moods = new HashMap<>();
                for (Entry entry : entries.get()) {
                    if (moods.putIfAbsent(entry.target(), entry.mood()) != null) {
                        ResourceLocation targetId = BuiltInRegistries.ENTITY_TYPE.getKey(entry.target());
                        errors.add("Cannot load NPC mood " + npcId
                                + ": duplicate target " + targetId);
                    }
                }
                newTable.put(npcType, Map.copyOf(moods));
            }

            if (!errors.isEmpty()) {
                errors.forEach(Confluence.LOGGER::error);
                Confluence.LOGGER.error(
                        "NPC mood reload was rejected; the previous valid table remains active");
                return;
            }

            moodTable = newTable.isEmpty() ? EMPTY : Map.copyOf(newTable);
            Confluence.LOGGER.info("Loaded {} NPC mood tables", moodTable.size());
        }
    }
}
