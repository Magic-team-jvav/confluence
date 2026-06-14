package org.confluence.mod.integration.terra_entity.init;

import com.mojang.serialization.MapCodec;
import net.minecraftforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade.*;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;

import java.util.function.Supplier;

public class ModTradeProviders {

    public static final DeferredRegister<TradeProvider> TYPES = DeferredRegister.create(TERegistries.TRADE_PROVIDERS, Confluence.MODID);

    public static final Supplier<TradeProvider> MONEY_TRADE_ITEM = register("money_trade_item", MoneyTradeItem.CODEC);
    public static final Supplier<TradeProvider> DEFERRED_MONEY_TRADE_ITEM = register("deferred_money_trade_item", DeferredMoneyTradeItem.CODEC);
    public static final Supplier<TradeProvider> MONEY_TRADE_HEALTH = register("money_trade_health", MoneyTradeHealth.CODEC);
    public static final Supplier<TradeProvider> MONEY_TRADE_HEALTH_FULL = register("money_trade_health_full", MoneyTradeHealthFull.CODEC);
    public static final Supplier<TradeProvider> SELL_TRADE = register("sell_trade", SellTrade.CODEC);

    private static Supplier<TradeProvider> register(String name, MapCodec<? extends ITrade> codec) {
        return TYPES.register(name, () -> new TradeProvider(codec));
    }
}
