package org.confluence.terraentity.registries.npc_trade_lock;

import com.mojang.serialization.MapCodec;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;

/**
 * 用于提供NPC交易类型编解码器
 * @param codec
 */
public record TradeLockProvider(MapCodec<? extends ITradeLock> codec, TradeLockRecipeDrawer drawer){

}
