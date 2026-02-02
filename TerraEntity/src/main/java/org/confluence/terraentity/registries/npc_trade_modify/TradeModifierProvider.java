package org.confluence.terraentity.registries.npc_trade_modify;

import com.mojang.serialization.MapCodec;
import org.confluence.terraentity.api.npc.trade.ITradeModifier;

/**
 * 用于修改NPC交易列表编解码器
 * @param codec
 */
public record TradeModifierProvider(MapCodec<? extends ITradeModifier> codec){

}
