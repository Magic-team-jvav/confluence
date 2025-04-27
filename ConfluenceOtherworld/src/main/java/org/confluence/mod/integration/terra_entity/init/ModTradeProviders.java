package org.confluence.mod.integration.terra_entity.init;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade.MoneyTradeHealth;
import org.confluence.mod.integration.terra_entity.npc_trade.MoneyTradeHealthFull;
import org.confluence.mod.integration.terra_entity.npc_trade.MoneyTradeItem;
import org.confluence.mod.integration.terra_entity.npc_trade.SellTrade;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.npc_trade.ITrade;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;

import java.util.function.Supplier;

public class ModTradeProviders {

    public static final DeferredRegister<TradeProvider> TYPES = DeferredRegister.create(TERegistries.TradeProviders.REGISTRY, Confluence.MODID);

    public static final Supplier<TradeProvider> MONEY_TRADE_ITEM = register("money_trade_item", MoneyTradeItem.CODEC);
    public static final Supplier<TradeProvider> MONEY_TRADE_HEALTH = register("money_trade_health", MoneyTradeHealth.CODEC);
    public static final Supplier<TradeProvider> MONEY_TRADE_HEALTH_FULL = register("money_trade_health_full", MoneyTradeHealthFull.CODEC);
    public static final Supplier<TradeProvider> SELL_TRADE = register("sell_trade", SellTrade.CODEC);

    private static Supplier<TradeProvider> register(String name, MapCodec<? extends ITrade> codec) {
        return TYPES.register(name, ()->new TradeProvider(codec));
    }
}
