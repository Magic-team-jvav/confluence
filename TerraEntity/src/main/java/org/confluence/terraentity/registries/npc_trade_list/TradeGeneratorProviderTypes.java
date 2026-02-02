package org.confluence.terraentity.registries.npc_trade_list;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITradeGenerator;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.npc_trade_list.variant.SimpleGenerator;
import org.confluence.terraentity.registries.npc_trade_list.variant.WeightMapGenerator;

import java.util.function.Supplier;

/**
 * 注册交易表生成器编解码器的类型
 */
public class TradeGeneratorProviderTypes {
    public static final DeferredRegister<TradeGeneratorProvider> TYPES = DeferredRegister.create(TERegistries.TRADE_GENERATOR_PROVIDERS, TerraEntity.MODID);

    public static final Supplier<TradeGeneratorProvider> SIMPLE_LIST = register("simple_list", SimpleGenerator.CODEC);
    public static final Supplier<TradeGeneratorProvider> WEIGHT_MAP = register("weight_map", WeightMapGenerator.CODEC);



    public static Supplier<TradeGeneratorProvider> register(String name,
                                                            MapCodec<? extends ITradeGenerator> codec) {
        return TYPES.register(name, ()->new TradeGeneratorProvider(codec));
    }
}
