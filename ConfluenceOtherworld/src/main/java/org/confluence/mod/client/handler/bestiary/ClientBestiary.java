package org.confluence.mod.client.handler.bestiary;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModLoader;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.bestiary.RegisterBestiaryFilterEvent;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.data.saved.BestiaryEntry;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public class ClientBestiary extends ContextAwareReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static ClientBestiary INSTANCE;

    private int unlockedCount;
    private boolean sortReversed = false;
    private SortType sortType = SortType.UNLOCKS;
    private Comparator<Map.Entry<String, ClientBestiaryEntry>> comparator = sortType.comparator;
    private Object2BooleanMap<FilterEntry> filterEntries = new Object2BooleanLinkedOpenHashMap<>();
    private Map<String, ClientBestiaryEntry> entries = Maps.newHashMap();
    private Map<String, ClientBestiaryEntry> backupEntries = Maps.newHashMap();
    private Map<String, ClientBestiaryEntry> sortedEntries = Maps.newLinkedHashMap();
    private CompletableFuture<SearchTree<Map.Entry<String, ClientBestiaryEntry>>> searchTree = CompletableFuture.completedFuture(SearchTree.empty());

    private Level currentLevel;

    private ClientBestiary() {}

    @Override
    public CompletableFuture<Void> reload(
            PreparableReloadListener.PreparationBarrier stage,
            ResourceManager resourceManager,
            ProfilerFiller preparationsProfiler,
            ProfilerFiller reloadProfiler,
            Executor backgroundExecutor,
            Executor gameExecutor
    ) {
        return CompletableFuture.supplyAsync(() -> prepare(resourceManager), backgroundExecutor).thenCompose(stage::wait).thenAcceptAsync(this::apply, gameExecutor);
    }

    protected Map<String, JsonElement> prepare(ResourceManager resourceManager) {
        Map<String, JsonElement> map = new HashMap<>();
        ResourceLocation resourceLocation = Confluence.asResource("bestiary.json");
        for (Resource resource : resourceManager.getResourceStack(resourceLocation)) {
            try (Reader reader = resource.openAsReader()) {
                JsonObject jsonobject = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                    map.put(entry.getKey(), entry.getValue());
                }
            } catch (RuntimeException | IOException ioexception) {
                Confluence.LOGGER.error("Couldn't read {} in resource pack {}", resourceLocation, resource.sourcePackId(), ioexception);
            }
        }
        return map;
    }

    protected void apply(Map<String, JsonElement> resourceList) {
        Map<String, ClientBestiaryEntry> map = Maps.newHashMap();
        Map<String, ClientBestiaryEntry> backup = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : resourceList.entrySet()) {
            ClientBestiaryEntry.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).ifSuccess(result -> {
                result.key = entry.getKey();
                if (entries != null) {
                    ClientBestiaryEntry entry1 = entries.get(result.key);
                    if (entry1 != null) {
                        result.unlockedProgress = entry1.unlockedProgress;
                        result.killedByCount = entry1.killedByCount;
                    }
                }
                map.put(result.key, result);
                backup.put(result.key, result.copy());
            });
        }
        this.entries = map;
        this.backupEntries = backup;
        sortEntries();
    }

    public void registerCustomFilter() {
        ModLoader.postEvent(new RegisterBestiaryFilterEvent(FilterEntry::register));
        Object2BooleanMap<FilterEntry> map = new Object2BooleanLinkedOpenHashMap<>();
        FilterEntry.PRESETS.values().stream().sorted(Comparator.comparingInt(FilterEntry::getOrder)).forEachOrdered(filter -> map.put(filter, true));
        map.put(FilterEntry.IF_UNLOCKED, false);
        this.filterEntries = map;
    }

    public Collection<FilterEntry> getFilterEntries() {
        return filterEntries.keySet();
    }

    public void toggleFilter(FilterEntry filter) {
        filterEntries.computeBooleanIfPresent(filter, (entry, enabled) -> !enabled);
        sortEntries();
    }

    public boolean isFilterEnabled(FilterEntry filter) {
        return filterEntries.getOrDefault(filter, false);
    }

    public void setSortType(SortType type, boolean reverse) {
        this.sortType = type;
        this.sortReversed = reverse;
        this.comparator = reverse ? type.comparator.reversed() : type.comparator;
        sortEntries();
    }

    public boolean isSortReversed() {
        return sortReversed;
    }

    public SortType getSortType() {
        return sortType;
    }

    public Collection<ClientBestiaryEntry> search(String query) {
        if (query.isEmpty()) {
            return sortedEntries.values();
        }
        return searchTree.join().search(query).stream().sorted(comparator).map(Map.Entry::getValue).filter(this::filter).toList();
    }

    private void sortEntries() {
        Map<String, ClientBestiaryEntry> sorted = Maps.newLinkedHashMap();
        entries.entrySet().stream()
                .sorted(comparator).filter(entry -> filter(entry.getValue()))
                .forEachOrdered(entry -> sorted.put(entry.getKey(), entry.getValue()));
        this.sortedEntries = sorted;
        this.searchTree = CompletableFuture.supplyAsync(() -> SearchTree.plainText(
                sortedEntries.entrySet().stream().filter(entry -> !entry.getValue().isLocked()).toList(),
                entry -> Stream.of(
                        entry.getKey(),
                        entry.getValue().description.getString(),
                        entry.getValue().type.getDescription().getString()
                ).map(s -> ChatFormatting.stripFormatting(s).trim())
        ), Util.backgroundExecutor());
    }

    public int getUnlockedCount() {
        return unlockedCount;
    }

    private boolean filter(ClientBestiaryEntry entry) {
        if (entry.isLocked()) {
            if (isFilterEnabled(FilterEntry.IF_UNLOCKED)) {
                return false;
            }
        }

        if (entry.filters.isEmpty()) return true;

        boolean enabled = false;
        for (FilterEntry filter : entry.filters) {
            enabled |= isFilterEnabled(filter);
        }
        return enabled;
    }

    public Collection<ClientBestiaryEntry> getSortedEntries() {
        return sortedEntries.values();
    }

    public static ClientBestiary getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientBestiary();
        }
        return INSTANCE;
    }

    public void reset() {
        if (currentLevel == null) return;
        this.currentLevel = null;
        Map<String, ClientBestiaryEntry> map = Maps.newHashMap();
        for (Map.Entry<String, ClientBestiaryEntry> entry : backupEntries.entrySet()) {
            entry.getValue().resetRenderedEntity(); // 为了防止备份条目也生成了实体
            map.put(entry.getKey(), entry.getValue().copy());
        }
        this.entries = map;
        sortEntries();
    }

    // 玩家进入存档统一同步
    // 随后只需更新部分实体
    public void handle(Level level, Either<Map<String, BestiaryEntry>, String> either) {
        if (level != currentLevel) {
            if (currentLevel != null) {
                for (Map.Entry<String, ClientBestiaryEntry> entry : entries.entrySet()) {
                    entry.getValue().resetRenderedEntity(); // 移除之前的level
                }
            }
            this.currentLevel = level;
        }
        either.ifLeft(map -> {
            boolean shouldCount = false;
            for (Map.Entry<String, BestiaryEntry> entry : map.entrySet()) {
                BestiaryEntry be = entry.getValue();
                if (!Bestiary.isAvailableType(be.type, level)) continue;
                ClientBestiaryEntry cbe = entries.computeIfAbsent(entry.getKey(), key -> {
                    ClientBestiaryEntry unknown = new ClientBestiaryEntry();
                    unknown.type = be.type;
                    unknown.key = key;
                    return unknown;
                });
                shouldCount |= cbe.unlock();
                cbe.killedByCount = be.killedByCount;
                cbe.maxHealth = be.maxHealth;
                cbe.knockbackResistance = be.knockbackResistance;
                cbe.attackDamage = be.attackDamage;
                cbe.armor = be.armor;
                cbe.drops = be.drops;
            }
            sortEntries();
            if (shouldCount) {
                int count = 0;
                for (ClientBestiaryEntry entry : entries.values()) {
                    if (entry.isLocked()) continue;
                    count++;
                }
                this.unlockedCount = count;
            }
        }).ifRight(key -> {
            ClientBestiaryEntry entry = getEntry(key);
            if (entry != null) {
                entry.killedByCount++;
            }
        });
    }

    public @Nullable ClientBestiaryEntry getEntry(String key) {
        return entries.get(key);
    }

    public enum SortType implements TranslatableEnum {
        UNLOCKS(Comparator.comparingInt(entry -> entry.getValue().isLocked() ? 1 : 0)),
        BESTIARY_ID(Comparator.comparingInt(entry -> entry.getValue().order)),
        NAME(Comparator.comparing(entry -> entry.getValue().description.getString())),
        ATTACK(Comparator.comparingDouble(entry -> entry.getValue().attackDamage)),
        DEFENSE(Comparator.comparingDouble(entry -> entry.getValue().armor)),
        COINS(Comparator.comparingInt(entry -> entry.getValue().drops)),
        HP(Comparator.comparingDouble(entry -> entry.getValue().maxHealth)),
        RARITY(Comparator.comparingInt(entry -> entry.getValue().rarity));

        private final Comparator<Map.Entry<String, ClientBestiaryEntry>> comparator;

        SortType(Comparator<Map.Entry<String, ClientBestiaryEntry>> comparator) {
            this.comparator = comparator;
        }

        @Override
        public Component getTranslatedName() {
            return Component.translatable("bestiary.sort_type." + name().toLowerCase(Locale.ROOT));
        }
    }
}
