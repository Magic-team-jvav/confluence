package org.confluence.terraentity.registries.npc_trade_modify;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITradeModifier;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.npc_trade_modify.variant.TradeItemModifier;
import org.confluence.terraentity.registries.npc_trade_modify.variant.TradeListModifier;

import java.util.function.Supplier;

/**
 * 注册交易任务编解码器的类型
 */
public class TradeModifierProviderTypes {
    public static final DeferredRegister<TradeModifierProvider> TYPES = DeferredRegister.create(TERegistries.TRADE_MODIFIER_PROVIDERS, TerraEntity.MODID);

    public static final Supplier<TradeModifierProvider> MODIFY_SINGLE = register("single", TradeItemModifier.CODEC);
    public static final Supplier<TradeModifierProvider> MODIFY_LIST = register("list", TradeListModifier.CODEC);


    public static Supplier<TradeModifierProvider> register(String name,
                                                       MapCodec<? extends ITradeModifier> codec) {
        return TYPES.register(name, ()->new TradeModifierProvider(codec));
    }
}
