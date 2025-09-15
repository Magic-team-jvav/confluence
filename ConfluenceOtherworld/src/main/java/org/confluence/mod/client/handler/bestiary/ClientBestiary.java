package org.confluence.mod.client.handler.bestiary;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.map.BestiaryEntry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClientBestiary extends ContextAwareReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static ClientBestiary INSTANCE;

    private Comparator<Map.Entry<String, ClientBestiaryEntry>> comparator = SortType.ENTRY_ORDER.comparator;
    private Map<String, ClientBestiaryEntry> entries = Maps.newHashMap();
    private Map<String, ClientBestiaryEntry> sortedEntries = Maps.newLinkedHashMap();

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
                ConfluenceMagicLib.LOGGER.error("Couldn't read bestiary {} in resource pack {}", resourceLocation, resource.sourcePackId(), ioexception);
            }
        }
        return map;
    }

    protected void apply(Map<String, JsonElement> resourceList) {
        Map<String, ClientBestiaryEntry> map = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : resourceList.entrySet()) {
            ClientBestiaryEntry.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).ifSuccess(result -> {
                result.key = entry.getKey();
                map.put(result.key, result);
            });
        }
        this.entries = map;
        sortEntries();
    }

    public void setSortType(SortType type, boolean reverse) {
        this.comparator = reverse ? type.comparator.reversed() : type.comparator;
        sortEntries();
    }

    private void sortEntries() {
        Map<String, ClientBestiaryEntry> sorted = Maps.newLinkedHashMap();
        entries.entrySet().stream().sorted(comparator).forEachOrdered(entry -> sorted.put(entry.getKey(), entry.getValue()));
        this.sortedEntries = sorted;
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

    // 玩家进入存档统一同步
    // 随后只需更新部分实体
    public void handle(Either<Map<String, BestiaryEntry>, String> either) {
        either.ifLeft(map -> {
            for (Map.Entry<String, BestiaryEntry> entry : map.entrySet()) {
                BestiaryEntry be = entry.getValue();
                ClientBestiaryEntry cbe = entries.computeIfAbsent(entry.getKey(), key -> {
                    ClientBestiaryEntry unknown = new ClientBestiaryEntry();
                    unknown.type = be.type;
                    unknown.key = key;
                    return unknown;
                });
                cbe.killedByCount = be.killedByCount;
                cbe.maxHealth = be.maxHealth;
                cbe.knockbackResistance = be.knockbackResistance;
                cbe.attackDamage = be.attackDamage;
                cbe.armor = be.armor;
                cbe.drops = be.drops;
                cbe.updateUnlockedProgress();
            }
            sortEntries();
        }).ifRight(key -> {
            ClientBestiaryEntry entry = entries.get(key);
            if (entry != null) {
                entry.killedByCount++;
                entry.updateUnlockedProgress();
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

        public final Comparator<Map.Entry<String, ClientBestiaryEntry>> comparator;

        SortType(Comparator<Map.Entry<String, ClientBestiaryEntry>> comparator) {
            this.comparator = comparator;
        }
    }
}
