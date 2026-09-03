package org.confluence.mod.common.data;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class AnglerQuestLoader extends SimpleJsonResourceReloadListener {
    private static AnglerQuestLoader INSTANCE;
    private static final Codec<List<Entry>> CODEC = Entry.CODEC.listOf().fieldOf("quests").codec();

    private Map<Item, Entry> entries = Map.of();

    public Map<Item, Entry> getEntries() {
        return entries;
    }

    public Optional<Entry> find(Item fish) {
        return Optional.ofNullable(entries.get(fish));
    }

    private AnglerQuestLoader() {
        super(new Gson(), "angler_quests");
    }

    public static AnglerQuestLoader getInstance() {
        if (INSTANCE == null) INSTANCE = new AnglerQuestLoader();
        return INSTANCE;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        Map<Item, Entry> loaded = new Reference2ObjectOpenHashMap<>();
        for (var resource : resources.entrySet()) {
            PortDataResultExtension.ifSuccess(CODEC.parse(JsonOps.INSTANCE, resource.getValue()), r -> {
                for (Entry entry : r) {
                    loaded.put(entry.fish, entry);
                }
            });
        }
        this.entries = loaded;
    }

    public record Entry(Item fish, CatchCondition condition) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("fish").forGetter(Entry::fish),
                CatchCondition.CODEC.fieldOf("condition").forGetter(Entry::condition)
        ).apply(instance, Entry::new));

        public boolean canBeCaught(FishingHook hook) {
            return condition.matches(hook);
        }
    }

    /// 渔夫任务鱼的捕获环境。条件以浮标位置为准，不复用以 NPC 位置为准的商店条件。
    public record CatchCondition(
            List<TagKey<Biome>> biomeTags,
            List<TagKey<Biome>> excludedBiomeTags,
            int minY,
            int maxY,
            Optional<TagKey<Fluid>> fluid
    ) {
        public static final Codec<CatchCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                TagKey.codec(Registries.BIOME).listOf().optionalFieldOf("biome_tags", List.of()).forGetter(CatchCondition::biomeTags),
                TagKey.codec(Registries.BIOME).listOf().optionalFieldOf("excluded_biome_tags", List.of()).forGetter(CatchCondition::excludedBiomeTags),
                Codec.INT.optionalFieldOf("min_y", Integer.MIN_VALUE).forGetter(CatchCondition::minY),
                Codec.INT.optionalFieldOf("max_y", Integer.MAX_VALUE).forGetter(CatchCondition::maxY),
                TagKey.codec(Registries.FLUID).optionalFieldOf("fluid").forGetter(CatchCondition::fluid)
        ).apply(instance, CatchCondition::new));

        public CatchCondition {
            if (minY > maxY) {
                throw new IllegalArgumentException("Angler quest minimum Y cannot exceed maximum Y");
            }
        }

        public boolean matches(FishingHook hook) {
            var biome = hook.level().getBiome(hook.blockPosition());
            if (!biomeTags.isEmpty() && biomeTags.stream().noneMatch(biome::is)) return false;
            if (excludedBiomeTags.stream().anyMatch(biome::is)) return false;
            int y = hook.blockPosition().getY();
            if (y < minY || y > maxY) return false;
            return fluid.isEmpty() || hook.getInBlockState().getFluidState().is(fluid.get());
        }
    }
}
