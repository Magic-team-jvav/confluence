package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.DelayTaskHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.boomerang.BoomerangItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.mesdag.portlib.attachment.IPortAttachmentHolder;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record LeftClickItemActionPacketC2S(boolean pressed) implements IPortPacket.C2S {
    public static final PortStreamCodec<ByteBuf, LeftClickItemActionPacketC2S> STREAM_CODEC = PortByteBufCodecs.BOOL.map(LeftClickItemActionPacketC2S::new, LeftClickItemActionPacketC2S::pressed);
    public static final ResourceLocation ID = Confluence.asResource("left_click_item_action");

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (!pressed) {
            DelayTaskHolder holder = DelayTaskHolder.of((IPortAttachmentHolder) player);
            holder.removeTask(InteractionHand.MAIN_HAND, BaseTerraRepeaterItem.REPEATER_SHOOTING);
            holder.removeTask(InteractionHand.MAIN_HAND, BaseTerraRepeaterItem.REPEATER_CONTINUOUS_SHOOTING);
        }
        if (stack.getItem() instanceof BaseTerraRepeaterItem repeater) {
            if (pressed) repeater.onLeftClick(player, stack);
            else repeater.onLeftRelease(player, stack);
        } else if (pressed && stack.getItem() instanceof BoomerangItem boomerang) {
            boomerang.throwBoomerang(player, InteractionHand.MAIN_HAND);
        }
    }

    public static void sendPressed() {
        Confluence.NETWORK_HANDLER.sendToServer(new LeftClickItemActionPacketC2S(true));
    }

    public static void sendReleased() {
        Confluence.NETWORK_HANDLER.sendToServer(new LeftClickItemActionPacketC2S(false));
    }
}
