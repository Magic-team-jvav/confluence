package org.confluence.terraentity.registries.npc_trade;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.npc_trade.variant.ItemTradeHealth;
import org.confluence.terraentity.registries.npc_trade.variant.ItemTradeItemList;
import org.confluence.terraentity.registries.npc_trade.variant.ItemTradeLootTable;
import org.confluence.terraentity.registries.npc_trade.variant.TradeTask;

import java.util.function.Supplier;

/**
 * 注册交易编解码器的类型
 */
public class TradeProviderTypes {
    public static final DeferredRegister<TradeProvider> TYPES = DeferredRegister.create(TERegistries.TRADE_PROVIDERS, TerraEntity.MODID);

    public static final Supplier<TradeProvider> ITEM_TRADE_HEALTH = register("ingredient_trade_health", ItemTradeHealth.CODEC);
    public static final Supplier<TradeProvider> TRADE_TASK = register("trade_task", TradeTask.CODEC);
    public static final Supplier<TradeProvider> ITEM_TRADE_LOOT_TABLE = register("ingredient_trade_loot_table", ItemTradeLootTable.CODEC);
    public static final Supplier<TradeProvider> INGREDIENT_TRADE_ITEM_LIST = register("ingredient_trade_item_list", ItemTradeItemList.CODEC);


    public static Supplier<TradeProvider> register(String name,
                                                    MapCodec<? extends ITrade> codec) {
        return TYPES.register(name, ()->new TradeProvider(codec));
    }
}
