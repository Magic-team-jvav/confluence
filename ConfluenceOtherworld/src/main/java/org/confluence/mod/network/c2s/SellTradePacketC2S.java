package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade.SellTrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.mixed.IPlayer;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record SellTradePacketC2S(int tradeIndex) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("sell_trade_c2s");
    public static final PortStreamCodec<ByteBuf, SellTradePacketC2S> STREAM_CODEC = PortByteBufCodecs.VAR_INT.map(SellTradePacketC2S::new, SellTradePacketC2S::tradeIndex);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        if (IPlayer.of(player).terra_entity$getTradeHolder() instanceof ITradeHolder holder && tradeIndex >= 0) {
            SellTrade.INSTANCE.onSell(player, holder, tradeIndex);
        }
    }

    public static void sendToServer(int selected) {
        Confluence.NETWORK_HANDLER.sendToServer(new SellTradePacketC2S(selected));
    }
}
