package org.confluence.mod.api.event.npc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.NPCTradeOffer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/// 服务端为一名玩家建立 NPC 商店会话前触发的报价收集事件。
///
/// <p>数据包贡献合并完成后才会发布本事件，因此附属模组可以追加命名空间隔离的报价，
/// 或显式替换、删除已有报价。事件结束后，NPC 自身的库存选择和报价条件才会执行，
/// 最终结果随后冻结为该玩家独享的会话快照。</p>
///
/// <p>监听器不得扣款、发货、修改玩家背包或向客户端发送自定义价格。所有成交仍由
/// 服务端交易会话统一校验和提交。</p>
public final class GatherNPCTradeOffersEvent extends Event {
    private final ServerPlayer player;
    private final BaseNPC npc;
    private final Map<ResourceLocation, NPCTradeOffer> offers =
            new LinkedHashMap<>();

    public GatherNPCTradeOffersEvent(
            ServerPlayer player,
            BaseNPC npc,
            List<NPCTradeOffer> initialOffers) {
        this.player = player;
        this.npc = npc;
        for (NPCTradeOffer offer : initialOffers) {
            add(offer);
        }
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public BaseNPC getNPC() {
        return npc;
    }

    /// 追加一条全新的报价。
    ///
    /// @throws IllegalArgumentException 报价 ID 已存在时抛出
    public void add(NPCTradeOffer offer) {
        if (offers.putIfAbsent(offer.id(), offer) != null) {
            throw new IllegalArgumentException(
                    "Duplicate NPC trade offer id: " + offer.id());
        }
    }

    /// 替换一条已经存在的报价，避免把拼写错误静默当成新增报价。
    ///
    /// @throws IllegalArgumentException 报价 ID 不存在时抛出
    public void replace(NPCTradeOffer offer) {
        if (!offers.containsKey(offer.id())) {
            throw new IllegalArgumentException(
                    "Cannot replace missing NPC trade offer id: "
                            + offer.id());
        }
        offers.put(offer.id(), offer);
    }

    /// 删除指定报价。
    ///
    /// @return 报价原本存在时为 {@code true}
    public boolean remove(ResourceLocation id) {
        return offers.remove(id) != null;
    }

    /// 返回保持作者声明与监听器追加顺序的不可修改快照。
    public List<NPCTradeOffer> getOffers() {
        return List.copyOf(offers.values());
    }
}
