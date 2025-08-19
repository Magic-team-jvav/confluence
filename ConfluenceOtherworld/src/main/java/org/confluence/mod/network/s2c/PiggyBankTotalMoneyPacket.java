package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.PlayerPiggyBankContainer;

public record PiggyBankTotalMoneyPacket(long totalMoney) implements CustomPacketPayload {
    public static final Type<PiggyBankTotalMoneyPacket> TYPE = new Type<>(Confluence.asResource("piggy_bank_total_money"));
    public static final StreamCodec<ByteBuf, PiggyBankTotalMoneyPacket> STREAM_CODEC = ByteBufCodecs.VAR_LONG.map(PiggyBankTotalMoneyPacket::new, PiggyBankTotalMoneyPacket::totalMoney);

    @Override
    public Type<PiggyBankTotalMoneyPacket> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> PlayerPiggyBankContainer.of(context.player()).setTotalMoney(totalMoney)).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToClient(ServerPlayer player, PlayerPiggyBankContainer container, boolean update) {
        if (update) container.setChanged();
        PacketDistributor.sendToPlayer(player, new PiggyBankTotalMoneyPacket(container.getTotalMoney()));
    }
}
