package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record FlushArmorSetBonusPacketS2C(int playerId) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("flush_armor_set_bonus");
    public static final PortStreamCodec<ByteBuf, FlushArmorSetBonusPacketS2C> STREAM_CODEC = PortByteBufCodecs.VAR_INT.map(FlushArmorSetBonusPacketS2C::new, FlushArmorSetBonusPacketS2C::playerId);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleFlushArmorSetBonus(player, playerId);
    }

    public static void sendToPlayersTrackingTarget(ServerPlayer target) {
        Confluence.NETWORK_HANDLER.sendToPlayersTrackingEntityAndSelf(target, new FlushArmorSetBonusPacketS2C(target.getId()));
    }

    public static void sendToClient(ServerPlayer sendTo, ServerPlayer target) {
        Confluence.NETWORK_HANDLER.sendToPlayer(sendTo, new FlushArmorSetBonusPacketS2C(target.getId()));
    }
}
