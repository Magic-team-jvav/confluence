package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade.SellTrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.mixed.IPlayer;
import org.confluence.terraentity.api.npc.trade.ITrade;

public record SellTradePacketC2S (int tradeIndex) implements CustomPacketPayload {
    public static final Type<SellTradePacketC2S> TYPE = new Type<>(Confluence.asResource("sell_trade_c2s"));
    public static final StreamCodec<ByteBuf, SellTradePacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p -> p.tradeIndex,
            SellTradePacketC2S::new
    );

    @Override
    public Type<SellTradePacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer sp){
                ITrade trade;
                if(((IPlayer)sp).terra_entity$getTradeHolder() instanceof ITradeHolder holder){
                    if(tradeIndex < 0 ){
                        return;
                    }
                    trade = holder.getTradeManager().availableTrades().get(tradeIndex);

                    if(trade instanceof SellTrade sell){
                        sell.onSell(sp, holder, tradeIndex);
                    }
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToServer(int selected) {
        PacketDistributor.sendToServer(new SellTradePacketC2S(selected));
    }
}
