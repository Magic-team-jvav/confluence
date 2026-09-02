package org.confluence.mod.common.data.saved;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;

import java.util.*;

/// 服务端数据包驱动的渔夫任务池。
public enum AnglerQuestPool {
    INSTANCE;

    private static final com.mojang.serialization.Codec<List<AnglerQuestEntry>> CODEC =
            AnglerQuestEntry.CODEC.listOf().fieldOf("quests").codec();
    private volatile List<AnglerQuestEntry> entries = List.of();

    public List<AnglerQuestEntry> getEntries() {
        return entries;
    }

    public Optional<AnglerQuestEntry> find(ItemStack fish) {
        return entries.stream().filter(entry -> ItemStack.isSameItemSameTags(entry.fish(), fish)).findFirst();
    }

    public static final class Loader extends SimpleJsonResourceReloadListener {
        private static final Loader INSTANCE = new Loader();

        private Loader() {
            super(new com.google.gson.GsonBuilder().create(), "angler_quests");
        }

        public static Loader getInstance() {
            return INSTANCE;
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
            List<AnglerQuestEntry> loaded = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            for (var resource : resources.entrySet()) {
                DataResult<List<AnglerQuestEntry>> decoded = CODEC.parse(JsonOps.INSTANCE, resource.getValue());
                Optional<List<AnglerQuestEntry>> result = decoded.result();
                if (result.isEmpty()) {
                    errors.add("Cannot load angler quests " + resource.getKey() + ": " + decoded.error()
                            .map(DataResult.PartialResult::message).orElse("unknown decode error"));
                } else {
                    loaded.addAll(result.get());
                }
            }
            Set<net.minecraft.world.item.Item> uniqueFish = new HashSet<>();
            for (AnglerQuestEntry entry : loaded) {
                if (!uniqueFish.add(entry.fish().getItem())) {
                    errors.add("Duplicate angler quest fish " + BuiltInRegistries.ITEM.getKey(entry.fish().getItem()));
                }
            }
            if (!errors.isEmpty()) {
                errors.forEach(Confluence.LOGGER::error);
                Confluence.LOGGER.error("Angler quest reload was rejected; the previous valid pool remains active");
                return;
            }
            AnglerQuestPool.INSTANCE.entries = List.copyOf(loaded);
            Confluence.LOGGER.info("Loaded {} angler quests", loaded.size());
        }
    }
}
