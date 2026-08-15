package org.confluence.mod.common.entity.npc.trade;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.npc.GatherNPCTradeOffersEvent;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 保存数据包定义的 NPC 商品表。
 *
 * <p>重载时先在临时映射中完整解析全部文件，只有所有文件都合法时才一次性替换当前表。
 * 这样某个附属包的错误条目不会把已经可用的商店清空，也不会留下只更新了一半的报价表。
 * 当前版本只接受新格式，不承担旧交易数据迁移。</p>
 */
public final class NPCTradeList {
    private static volatile Map<EntityType<?>, List<NPCTradeOffer>> offerTable = Map.of();

    private NPCTradeList() {}

    /**
     * 获取指定 NPC 当前可用的商品列表。
     *
     * <p>顺序固定为：数据包整表快照、附属事件修饰、NPC 特有库存选择、玩家条件过滤。
     * 该顺序使旅商能够从附属追加的商品中抽取库存，同时保证条件只负责可用性判断。</p>
     */
    public static List<NPCTradeOffer> getAvailableOffers(ServerPlayer player, BaseNPC npc) {
        List<NPCTradeOffer> modified = applyOfferModifiers(
                player,
                npc,
                offerTable.getOrDefault(npc.getType(), List.of()));
        List<NPCTradeOffer> selected = npc.selectTradeOffers(modified);
        return selected.stream()
                .filter(o -> o.isAvailable(player, npc))
                .toList();
    }

    /**
     * 返回当前已提交商品表的只读快照，供服务端诊断与契约测试使用。
     */
    static Map<EntityType<?>, List<NPCTradeOffer>> snapshotOfferTable() {
        return offerTable;
    }

    /**
     * 发布代码扩展事件并取得稳定快照，包级入口同时供契约测试使用。
     */
    static List<NPCTradeOffer> applyOfferModifiers(
            ServerPlayer player,
            BaseNPC npc,
            List<NPCTradeOffer> initialOffers) {
        GatherNPCTradeOffersEvent event =
                new GatherNPCTradeOffersEvent(player, npc, initialOffers);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getOffers();
    }

    /**
     * 纯解析全部商店贡献，供重载提交和回归测试共用。
     *
     * <p>解析阶段不修改当前商店表，也不写日志。调用方只有在错误列表为空时才能提交结果，
     * 从而保证损坏数据不会留下半张新表。</p>
     */
    static ParseResult parseContributions(Map<ResourceLocation, JsonElement> resources) {
        Map<EntityType<?>, List<NPCTradeOffer>> contributions = new HashMap<>();
        Map<EntityType<?>, HashSet<ResourceLocation>> offerIds = new HashMap<>();
        List<String> errors = new ArrayList<>();

        for (var entry : resources.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList()) {
            ResourceLocation contributionId = entry.getKey();
            var decoded = NPCShopDefinition.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
            var definition = decoded.result();
            if (definition.isEmpty()) {
                String reason = decoded.error()
                        .map(DataResult.PartialResult::message)
                        .orElse("unknown decode error");
                errors.add("Cannot load NPC shop contribution " + contributionId + ": " + reason);
                continue;
            }

            NPCShopDefinition shop = definition.get();
            HashSet<ResourceLocation> ids =
                    offerIds.computeIfAbsent(shop.npc(), ignored -> new HashSet<>());
            for (NPCTradeOffer offer : shop.offers()) {
                if (!ids.add(offer.id())) {
                    errors.add("Cannot load NPC shop contribution " + contributionId
                            + ": duplicate offer id " + offer.id()
                            + " for NPC " + shop.npc());
                }
            }
            contributions.computeIfAbsent(shop.npc(), ignored -> new ArrayList<>())
                    .addAll(shop.offers());
        }

        Map<EntityType<?>, List<NPCTradeOffer>> table = new HashMap<>();
        contributions.forEach((npc, offers) ->
                table.put(npc, List.copyOf(offers)));
        return new ParseResult(Map.copyOf(table), List.copyOf(errors));
    }

    record ParseResult(Map<EntityType<?>, List<NPCTradeOffer>> table, List<String> errors) {
        boolean isValid() {
            return errors.isEmpty();
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
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager rm, ProfilerFiller pf) {
            ParseResult parsed = parseContributions(map);
            if (!parsed.isValid()) {
                parsed.errors().forEach(Confluence.LOGGER::error);
                Confluence.LOGGER.error("NPC shop reload was rejected; the previous valid table remains active");
                return;
            }

            offerTable = parsed.table();
            Confluence.LOGGER.info("Loaded {} NPC shop tables", offerTable.size());
        }
    }
}
