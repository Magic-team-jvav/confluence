package org.confluence.terraentity.registries.npc_trade_task;

import com.mojang.serialization.MapCodec;
import org.confluence.terraentity.api.npc.trade.ITradeTask;

/**
 * 用于提供NPC交易类型编解码器
 * @param codec
 */
public record TradeTaskProvider(MapCodec<? extends ITradeTask> codec){

}
