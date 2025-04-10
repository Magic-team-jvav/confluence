package org.confluence.mod.common.data.gen.npc_trade;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.npc_trade.ITrade;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;

import java.util.function.Supplier;

public class ModTradeProviders {

    public static final DeferredRegister<TradeProvider> TYPES = DeferredRegister.create(TERegistries.TradeProviders.REGISTRY, Confluence.MODID);

    public static final Supplier<TradeProvider> MONEY_TRADE_ITEM = register("money_trade_item", MoneyTradeItem.CODEC);

    private static Supplier<TradeProvider> register(String name, MapCodec<? extends ITrade> codec) {
        return TYPES.register(name, ()->new TradeProvider(codec));
    }
}
