package org.confluence.terraentity.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.event.NPCEvent;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.entity.npc.trade.TradeParams;
import org.confluence.terraentity.mixed.IPlayer;
import org.confluence.terraentity.utils.AdapterUtils;
import org.jetbrains.annotations.NotNull;


public record NPCShopPacket(int tradeIndex, TradeParams params) implements CustomPacketPayload {
    public static final Type<NPCShopPacket> TYPE = new Type<>(TerraEntity.space("npc_shop"));
    public static final StreamCodec<ByteBuf, NPCShopPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, NPCShopPacket::tradeIndex,
            TradeParams.STREAM_CODEC, NPCShopPacket::params,
            NPCShopPacket::new
    );

    @Override
    public @NotNull Type<NPCShopPacket> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {

            if (context.player() instanceof ServerPlayer sp) {
                ITrade trade;
                if (((IPlayer) sp).terra_entity$getTradeHolder() instanceof ITradeHolder holder) {
                    if (tradeIndex < 0) {
                        return;
                    }
//                    trade = holder.getTradeManager().availableTrades().get(tradeIndex);
                    trade = holder.getTradeManager().targetTrade(params, tradeIndex);

                    NPCEvent.NPCTradeEvent.Pre event = AdapterUtils.postGameEvent(new NPCEvent.NPCTradeEvent.Pre(holder, trade, sp));
                    if (event.isCanceled()) {
                        return;
                    }
                    if (event.isAlwaysPass() || trade.canTradeWithLock(sp, holder, tradeIndex)) {
                        if (event.getRedirection() != null) {
                            event.getRedirection().accept(sp, trade);
                        } else {
                            trade.onTrade(sp, holder, tradeIndex);
                        }
                        AdapterUtils.postGameEvent(new NPCEvent.NPCTradeEvent.Post(holder, trade, sp));
                    }
                }
            } else {
                // 正常情况不会触发
                context.player().sendSystemMessage(Component.translatable("message.terra_entity.trade.not_enough_items"));
            }

        }).exceptionally(e -> null);
    }
}
