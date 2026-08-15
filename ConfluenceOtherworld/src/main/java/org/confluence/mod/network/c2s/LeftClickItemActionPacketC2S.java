package org.confluence.mod.network.c2s;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.boomerang.BoomerangItem;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public enum LeftClickItemActionPacketC2S implements IPortPacket.C2S {
    INSTANCE;

    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, LeftClickItemActionPacketC2S> STREAM_CODEC =
            PortStreamCodec.unit(INSTANCE);
    public static final ResourceLocation ID = Confluence.asResource("left_click_item_action");

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    /**
     * 左键动作可能生成实体，必须回到服务端主线程执行。
     */
    @Override
    public void handle(IPortPacket.Context context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> work(player));
        }
    }

    @Override
    public void work(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof BoomerangItem boomerang) {
            boomerang.throwBoomerang(player, InteractionHand.MAIN_HAND);
        }
    }

    public static void send2Server() {
        Confluence.NETWORK_HANDLER.sendToServer(INSTANCE);
    }
}
