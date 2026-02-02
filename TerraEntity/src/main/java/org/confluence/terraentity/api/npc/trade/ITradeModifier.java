package org.confluence.terraentity.api.npc.trade;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.entity.npc.trade.NPCTradeManager;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.npc_trade_modify.TradeModifierProvider;

import java.util.function.BiConsumer;

/**
 * <h1>npc交易修饰器</h1>
 * <p>增删改单个表项或整个交易表</p>
 */
public interface ITradeModifier extends BiConsumer<NPCTradeManager, ResourceLocation> {

    /**
     * 优先级，越小越优先
     */
    int priority();

    ResourceLocation id();


    /**
     * 获取编解码器
     * @return 编解码器
     */
    TradeModifierProvider getCodec();


    Codec<ITradeModifier> TYPED_CODEC = TERegistries.TRADE_MODIFIER_PROVIDERS
            .byNameCodec()
            .dispatch(ITradeModifier::getCodec, TradeModifierProvider::codec);


    StreamCodec<ByteBuf, ITradeModifier> STREAM_CODEC = ByteBufCodecs.fromCodec(TYPED_CODEC);


    enum OperatorType {
        ADD,
        DEL,
        REPLACE;
        public static final Codec<OperatorType> CODEC = Codec.STRING.xmap(s-> OperatorType.valueOf(s.toUpperCase()), e-> e.name().toLowerCase());
    }
}
