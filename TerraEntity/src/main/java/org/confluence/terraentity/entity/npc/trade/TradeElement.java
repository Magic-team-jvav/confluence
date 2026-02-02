package org.confluence.terraentity.entity.npc.trade;

import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;

public class TradeElement {
    public ITrade trade;
    public TradeProperties properties;

    public TradeElement(ITrade tradeList, TradeProperties properties) {
        this.trade = tradeList;
        this.properties = properties;
    }



}
