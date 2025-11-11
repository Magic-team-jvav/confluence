package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade.SellTrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.mixed.IPlayer;

public record SellTradePacketC2S(int tradeIndex) implements IPacketC2S {
    public static final Type<SellTradePacketC2S> TYPE = Confluence.createType("sell_trade_c2s");
    public static final StreamCodec<ByteBuf, SellTradePacketC2S> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(SellTradePacketC2S::new, SellTradePacketC2S::tradeIndex);

    @Override
    public Type<SellTradePacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        if (IPlayer.of(player).terra_entity$getTradeHolder() instanceof ITradeHolder holder && tradeIndex >= 0) {
            SellTrade.INSTANCE.onSell(player, holder, tradeIndex);
        }
    }

    public static void sendToServer(int selected) {
        PacketDistributor.sendToServer(new SellTradePacketC2S(selected));
    }
}
