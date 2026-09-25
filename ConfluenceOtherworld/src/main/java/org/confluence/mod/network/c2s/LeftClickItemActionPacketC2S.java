package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.DelayTaskHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.item.ILeftClickStateItem;
import org.confluence.mod.common.attachment.LeftClickState;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.mesdag.portlib.attachment.IPortAttachmentHolder;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record LeftClickItemActionPacketC2S(Action action) implements IPortPacket.C2S {
    public static final PortStreamCodec<ByteBuf, LeftClickItemActionPacketC2S> STREAM_CODEC = PortByteBufCodecs.VAR_INT.map(
            id -> new LeftClickItemActionPacketC2S(Action.byId(id)), packet -> packet.action.ordinal());
    public static final ResourceLocation ID = Confluence.asResource("left_click_item_action");

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        LeftClickState state = LeftClickState.of(player);
        if (action == Action.RELEASE || player.isSpectator()) {
            release(player, state);
            return;
        }
        if (action == Action.WHEEL_UP || action == Action.WHEEL_DOWN) {
            if (state.isPressed(stack) && stack.getItem() instanceof ILeftClickStateItem item) {
                item.onWheelScroll(player, stack, action == Action.WHEEL_UP ? 1 : -1);
            }
            return;
        }
        if (!(stack.getItem() instanceof ILeftClickStateItem item)) {
            release(player, state);
            return;
        }
        if (state.isPressed(stack)) return;
        release(player, state);
        state.press(stack);
        item.onLeftClick(player, stack);
    }

    /// 松开时先清除状态，再通知原物品；切换主手后也不会漏掉连弩的延迟任务。
    private static void release(ServerPlayer player, LeftClickState state) {
        ItemStack active = state.activeStack();
        state.release();
        if (active.getItem() instanceof ILeftClickStateItem item) {
            item.onLeftRelease(player, active);
        }
        if (!active.isEmpty()) {
            DelayTaskHolder holder = DelayTaskHolder.of((IPortAttachmentHolder) player);
            holder.removeTask(InteractionHand.MAIN_HAND, BaseTerraRepeaterItem.REPEATER_SHOOTING);
            holder.removeTask(InteractionHand.MAIN_HAND, BaseTerraRepeaterItem.REPEATER_CONTINUOUS_SHOOTING);
        }
    }

    public static void sendPressed() {
        PortPacketDistributor.sendToServer(new LeftClickItemActionPacketC2S(Action.PRESS));
    }

    public static void sendReleased() {
        PortPacketDistributor.sendToServer(new LeftClickItemActionPacketC2S(Action.RELEASE));
    }

    public static void sendScroll(int amount) {
        PortPacketDistributor.sendToServer(new LeftClickItemActionPacketC2S(amount > 0 ? Action.WHEEL_UP : Action.WHEEL_DOWN));
    }

    public enum Action {
        PRESS, RELEASE, WHEEL_UP, WHEEL_DOWN;

        private static final Action[] VALUES = values();

        public static Action byId(int id) {
            return id >= 0 && id < VALUES.length ? VALUES[id] : RELEASE;
        }
    }
}
