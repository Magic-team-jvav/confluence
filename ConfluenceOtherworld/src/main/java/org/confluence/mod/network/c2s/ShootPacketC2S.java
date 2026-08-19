package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.combat.gun.ShootingService;
import org.confluence.mod.network.s2c.ShotFeedbackPacketS2C;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public enum ShootPacketC2S implements IPortPacket.C2S {
    INSTANCE;

    public static final ResourceLocation ID = Confluence.asResource("shoot");
    public static final PortStreamCodec<ByteBuf, ShootPacketC2S> STREAM_CODEC = PortStreamCodec.unit(INSTANCE);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void handle(IPortPacket.Context context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> work(player));
        }
    }

    @Override
    public void work(ServerPlayer player) {
        if (ShootingService.tryShoot(player)) {
            ShotFeedbackPacketS2C.sendTo(player);
        }
    }

    public static void sendToServer() {
        Confluence.NETWORK_HANDLER.sendToServer(INSTANCE);
    }
}
