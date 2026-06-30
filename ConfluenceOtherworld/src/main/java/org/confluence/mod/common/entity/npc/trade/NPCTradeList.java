package org.confluence.mod.common.entity.npc.trade;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局交易列表——从 JSON 加载每个 NPC 的出售商品列表。
 */
public final class NPCTradeList {
    private static Map<EntityType<?>, List<NPCTradeOffer>> offerTable = Map.of();

    /**
     * 获取指定 NPC 当前可用的商品列表。
     */
    public static List<NPCTradeOffer> getAvailableOffers(BaseNPC npc) {
        List<NPCTradeOffer> all = offerTable.getOrDefault(npc.getType(), List.of());
        if (!(npc.level() instanceof ServerLevel level)) return List.of();
        return all.stream()
                .filter(o -> o.isAvailable(level, npc))
                .toList();
    }

    public static final class Loader extends SimpleJsonResourceReloadListener {
        public Loader() {
            super(new com.google.gson.GsonBuilder().create(), "npc/trades");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager rm, ProfilerFiller pf) {
            var codec = NPCTradeOffer.CODEC.listOf();
            Map<EntityType<?>, List<NPCTradeOffer>> newTable = new HashMap<>();
            for (var entry : map.entrySet()) {
                if (!Confluence.MODID.equals(entry.getKey().getNamespace())) continue;
                BuiltInRegistries.ENTITY_TYPE.getOptional(entry.getKey()).ifPresent(type ->
                        codec.parse(JsonOps.INSTANCE, entry.getValue())
                                .result().ifPresent(offers -> newTable.put(type, offers)));
            }
            offerTable = newTable;
        }
    }
}
