package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.hud.RepeaterHud;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public enum RepeaterShootingPayloadS2C implements IPortPacket.S2C {
    INSTANCE;
    public static final ResourceLocation ID = Confluence.asResource("repeater_shooting_payload");
    public static final PortStreamCodec<ByteBuf, RepeaterShootingPayloadS2C> STREAM_CODEC = PortStreamCodec.unit(INSTANCE);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        RepeaterHud.handle();
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        Confluence.NETWORK_HANDLER.sendToPlayer(serverPlayer, INSTANCE);
    }
}
