package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.util.PlayerUtils;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record FishingPowerInfoPacketS2C(float value) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("fishing_power_info");
    public static final PortStreamCodec<ByteBuf, FishingPowerInfoPacketS2C> STREAM_CODEC = PortByteBufCodecs.FLOAT.map(FishingPowerInfoPacketS2C::new, FishingPowerInfoPacketS2C::value);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleFishingPower(this);
    }

    public static void sendToClient(ServerPlayer player) {
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new FishingPowerInfoPacketS2C(PlayerUtils.getFishingPower(player)));
    }
}
