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
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.RegisterBestiaryFilterEvent;
import org.confluence.mod.common.data.map.BestiaryEntry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClientBestiary extends ContextAwareReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static ClientBestiary INSTANCE;

    private Comparator<Map.Entry<String, ClientBestiaryEntry>> comparator = SortType.ENTRY_ORDER.comparator;
    private Map<String, ClientBestiaryEntry> entries = Maps.newHashMap();
    private Map<String, ClientBestiaryEntry> backupEntries = Maps.newHashMap();
    private Map<String, ClientBestiaryEntry> sortedEntries = Maps.newLinkedHashMap();
    private CompletableFuture<SearchTree<Map.Entry<String, ClientBestiaryEntry>>> searchTree = CompletableFuture.completedFuture(SearchTree.empty());

    private Object2BooleanMap<FilterEntry> filterEntries = new Object2BooleanLinkedOpenHashMap<>();

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
                map.put(result.key, result);
                backup.put(result.key, result.copy());
            });
        }
        this.entries = map;
        this.backupEntries = backup;
    }

    public void registerCustomFilter() {
        ModLoader.postEvent(new RegisterBestiaryFilterEvent(FilterEntry::preset));
        Object2BooleanMap<FilterEntry> map = new Object2BooleanLinkedOpenHashMap<>();
        FilterEntry.PRESETS.values().stream().sorted(Comparator.comparingInt(FilterEntry::getOrder)).forEachOrdered(filter -> map.put(filter, true));
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
        this.comparator = reverse ? type.comparator.reversed() : type.comparator;
        sortEntries();
    }

    public Collection<ClientBestiaryEntry> search(String query) {
        if (query.isEmpty()) {
            return sortedEntries.values();
        }
        return searchTree.join().search(query).stream().sorted(comparator).map(Map.Entry::getValue).filter(this::filter).toList();
    }

    private void sortEntries() {
        Map<String, ClientBestiaryEntry> sorted = Maps.newLinkedHashMap();
        entries.entrySet().stream().sorted(comparator).forEachOrdered(entry -> {
            if (filter(entry.getValue())) {
                sorted.put(entry.getKey(), entry.getValue());
            }
        });
        this.sortedEntries = sorted;
    }

    private boolean filter(ClientBestiaryEntry entry) {
        List<FilterEntry> filters = entry.getFilters();
        if (filters.isEmpty()) return true;
        boolean enabled = false;
        for (FilterEntry filter : filters) {
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

    public void resetEntries() {
        if (currentLevel == null) return;
        this.currentLevel = null;
        Map<String, ClientBestiaryEntry> map = Maps.newHashMap();
        for (Map.Entry<String, ClientBestiaryEntry> entry : backupEntries.entrySet()) {
            map.put(entry.getKey(), entry.getValue().copy());
        }
        this.entries = map;
        sortEntries();
    }

    // 玩家进入存档统一同步
    // 随后只需更新部分实体
    public void handle(Level level, Either<Map<String, BestiaryEntry>, String> either) {
        if (currentLevel != level) { // 这一步是为了释放内存
            resetEntries();
            this.currentLevel = level;
        }
        either.ifLeft(map -> {
            int lastSize = entries.size();
            for (Map.Entry<String, BestiaryEntry> entry : map.entrySet()) {
                BestiaryEntry be = entry.getValue();
                ClientBestiaryEntry cbe = entries.computeIfAbsent(entry.getKey(), key -> {
                    ClientBestiaryEntry unknown = new ClientBestiaryEntry();
                    unknown.type = be.type;
                    unknown.key = key;
                    return unknown;
                });
                cbe.unlock();
                cbe.killedByCount = be.killedByCount;
                cbe.maxHealth = be.maxHealth;
                cbe.knockbackResistance = be.knockbackResistance;
                cbe.attackDamage = be.attackDamage;
                cbe.armor = be.armor;
                cbe.drops = be.drops;
            }
            if (lastSize != entries.size()) {
                sortEntries();
                this.searchTree = CompletableFuture.supplyAsync(() -> SearchTree.plainText(
                        sortedEntries.entrySet().stream().filter(entry -> !entry.getValue().isLocked()).toList(),
                        entry -> Stream.of(
                                entry.getValue().type.getDescription(),
                                entry.getValue().getDescription()
                        ).map(c -> ChatFormatting.stripFormatting(c.getString()).trim())
                ), Util.backgroundExecutor());
            }
        }).ifRight(key -> {
            ClientBestiaryEntry entry = entries.get(key);
            if (entry != null) {
                entry.killedByCount++;
            }
        });
    }

    public enum SortType {
        ENTRY_ORDER(Comparator.comparingInt(entry -> entry.getValue().getOrder())),
        ENTITY_ID(Map.Entry.comparingByKey()),
        ATTACK_DAMAGE(Comparator.comparingDouble(entry -> entry.getValue().attackDamage)),
        ARMOR(Comparator.comparingDouble(entry -> entry.getValue().armor)),
        DROPS(Comparator.comparingInt(entry -> entry.getValue().drops)),
        MAX_HEALTH(Comparator.comparingDouble(entry -> entry.getValue().maxHealth)),
        RARITY(Comparator.comparingInt(entry -> entry.getValue().getRarity()));

        private final Comparator<Map.Entry<String, ClientBestiaryEntry>> comparator;

        SortType(Comparator<Map.Entry<String, ClientBestiaryEntry>> comparator) {
            this.comparator = comparator;
        }
    }
}
