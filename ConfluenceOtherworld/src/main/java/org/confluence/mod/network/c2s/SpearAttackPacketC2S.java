package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public enum SpearAttackPacketC2S implements IPortPacket.C2S {
    INSTANCE;
    public static final ResourceLocation ID = Confluence.asResource("spear_attack");
    public static final PortStreamCodec<ByteBuf, SpearAttackPacketC2S> STREAM_CODEC = PortPortStreamCodec.unit(INSTANCE);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.is(ModTags.Items.SPEAR)) {
            itemStack.onEntitySwing(player, InteractionHand.MAIN_HAND);
        }
    }

    public static void sendToServer() {
        Confluence.NETWORK_HANDLER.sendToServer(INSTANCE);
    }
}
