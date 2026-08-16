package org.confluence.mod.common.entity.npc.trade;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// 从每个 NPC 对应的 JSON 文件加载固定商品列表。
public final class NPCTradeList {
    private static final Codec<List<NPCTradeOffer>> SHOP_CODEC = NPCTradeOffer.CODEC.listOf().fieldOf("offers").codec();
    private static volatile State state = new State(Map.of(), 0);

    private NPCTradeList() {}

    public static ShopSnapshot getAvailableOffers(ServerPlayer player, BaseNPC npc) {
        State current = state;
        List<NPCTradeOffer> selected = npc.selectTradeOffers(current.table().getOrDefault(npc.getType(), List.of()));
        return new ShopSnapshot(selected.stream().filter(offer -> offer.isAvailable(player, npc)).toList(), current.revision());
    }

    public static int getRevision() {
        return state.revision();
    }

    private static ParseResult parse(Map<ResourceLocation, JsonElement> resources) {
        Map<EntityType<?>, List<NPCTradeOffer>> table = new HashMap<>();
        List<String> errors = new ArrayList<>();
        for (var entry : resources.entrySet()) {
            ResourceLocation npcId = entry.getKey();
            var entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(npcId);
            if (entityType.isEmpty()) {
                errors.add("Unknown NPC entity type " + npcId);
                continue;
            }
            DataResult<List<NPCTradeOffer>> decoded = SHOP_CODEC.parse(JsonOps.INSTANCE, entry.getValue());
            var offers = decoded.result();
            if (offers.isEmpty()) {
                String reason = decoded.error().map(DataResult.PartialResult::message).orElse("unknown decode error");
                errors.add("Cannot load NPC shop " + npcId + ": " + reason);
                continue;
            }
            table.put(entityType.get(), List.copyOf(offers.get()));
        }
        return new ParseResult(Map.copyOf(table), List.copyOf(errors));
    }

    private record ParseResult(Map<EntityType<?>, List<NPCTradeOffer>> table,
                               List<String> errors) {}

    private record State(Map<EntityType<?>, List<NPCTradeOffer>> table, int revision) {}

    public record ShopSnapshot(List<NPCTradeOffer> offers, int revision) {
        public ShopSnapshot {
            offers = List.copyOf(offers);
        }
    }

    public static final class Loader extends SimpleJsonResourceReloadListener {
        private static final Loader INSTANCE = new Loader();

        private Loader() {
            super(new com.google.gson.GsonBuilder().create(), "npc/trades");
        }

        public static Loader getInstance() {
            return INSTANCE;
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
            ParseResult result = parse(map);
            if (!result.errors().isEmpty()) {
                result.errors().forEach(Confluence.LOGGER::error);
                Confluence.LOGGER.error("NPC shop reload was rejected; the previous valid table remains active");
                return;
            }
            State previous = state;
            state = new State(result.table(), previous.revision() + 1);
            Confluence.LOGGER.info("Loaded {} NPC shop tables", result.table().size());
        }
    }
}
