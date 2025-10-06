package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.common.attachment.PlayerPiggyBankContainer;
import org.confluence.mod.network.IPacket;

public record PiggyBankTotalMoneyPacket(long totalMoney) implements IPacketS2C {
    public static final Type<PiggyBankTotalMoneyPacket> TYPE = IPacket.createType("piggy_bank_total_money");
    public static final StreamCodec<ByteBuf, PiggyBankTotalMoneyPacket> STREAM_CODEC = ByteBufCodecs.VAR_LONG.map(PiggyBankTotalMoneyPacket::new, PiggyBankTotalMoneyPacket::totalMoney);

    @Override
    public Type<PiggyBankTotalMoneyPacket> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        PlayerPiggyBankContainer.of(player).setTotalMoney(totalMoney);
    }

    public static void sendToClient(ServerPlayer player, PlayerPiggyBankContainer container, boolean update) {
        if (update) container.setChanged();
        PacketDistributor.sendToPlayer(player, new PiggyBankTotalMoneyPacket(container.getTotalMoney()));
    }
}
