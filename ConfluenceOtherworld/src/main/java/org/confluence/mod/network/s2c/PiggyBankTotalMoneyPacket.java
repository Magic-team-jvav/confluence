package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.PlayerPiggyBankContainer;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record PiggyBankTotalMoneyPacket(long totalMoney) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("piggy_bank_total_money");
    public static final PortStreamCodec<ByteBuf, PiggyBankTotalMoneyPacket> STREAM_CODEC = PortByteBufCodecs.VAR_LONG.map(PiggyBankTotalMoneyPacket::new, PiggyBankTotalMoneyPacket::totalMoney);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        PlayerPiggyBankContainer.of(player).setTotalMoney(totalMoney);
    }

    public static void sendToClient(ServerPlayer player, PlayerPiggyBankContainer container, boolean update) {
        if (update) container.setChanged();
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new PiggyBankTotalMoneyPacket(container.getTotalMoney()));
    }
}
