package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.GunEvent;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/// Server acknowledgement used to drive client-only fire feedback.
public enum ShotFeedbackPacketS2C implements IPortPacket.S2C {
    INSTANCE;

    public static final ResourceLocation ID = Confluence.asResource("shot_feedback");
    public static final PortStreamCodec<ByteBuf, ShotFeedbackPacketS2C> STREAM_CODEC = PortStreamCodec.unit(INSTANCE);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        PortEventHandler.postEvent(new GunEvent.ShotConfirmedEvent(player));
    }

    public static void sendTo(ServerPlayer player) {
        Confluence.NETWORK_HANDLER.sendToPlayer(player, INSTANCE);
    }
}
