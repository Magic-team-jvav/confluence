package org.confluence.terraentity.registries.npc_trade_list;

import com.mojang.serialization.MapCodec;
import org.confluence.terraentity.api.npc.trade.ITradeGenerator;

/**
 * 用于提供NPC交易类型编解码器
 * @param codec
 */
public record TradeGeneratorProvider(MapCodec<? extends ITradeGenerator> codec){

}
