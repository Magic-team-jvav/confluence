package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public final class SwordProjectilePacketC2S implements IPortPacket.C2S {
    private static final SwordProjectilePacketC2S INSTANCE = new SwordProjectilePacketC2S();
    public static final ResourceLocation ID = Confluence.asResource("sword_projectile");
    public static final PortStreamCodec<ByteBuf, SwordProjectilePacketC2S> STREAM_CODEC = PortStreamCodec.unit(INSTANCE);

    private SwordProjectilePacketC2S() {}

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
        if (player.getMainHandItem().getItem() instanceof BaseSwordItem sword) {
            sword.tryFireProjectile(player, InteractionHand.MAIN_HAND);
        }
    }

    public static void sendToServer() {
        Confluence.NETWORK_HANDLER.sendToServer(INSTANCE);
    }
}
