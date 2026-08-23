package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.flail.BaseFlailItem;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/// 玩家按住或松开攻击键时发送，控制连枷状态。
public record FlailControlPacketC2S(Action action) implements IPortPacket.C2S {
    public enum Action {
        HOLD,
        RELEASE
    }

    public static final ResourceLocation ID = Confluence.asResource("flail_control");
    public static final PortStreamCodec<ByteBuf, FlailControlPacketC2S> STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public FlailControlPacketC2S decode(ByteBuf buffer) {
            return new FlailControlPacketC2S(buffer.readBoolean() ? Action.HOLD : Action.RELEASE);
        }

        @Override
        public void encode(ByteBuf buffer, FlailControlPacketC2S packet) {
            buffer.writeBoolean(packet.action == Action.HOLD);
        }
    };

    public FlailControlPacketC2S {
        java.util.Objects.requireNonNull(action, "Flail control action must not be null");
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BaseFlailItem)) {
            return;
        }
        if (action == Action.HOLD) {
            BaseFlailItem.press(player, stack);
        } else {
            BaseFlailItem.release(player, stack);
        }
    }

    public static void sendHold() {
        Confluence.NETWORK_HANDLER.sendToServer(new FlailControlPacketC2S(Action.HOLD));
    }

    public static void sendRelease() {
        Confluence.NETWORK_HANDLER.sendToServer(new FlailControlPacketC2S(Action.RELEASE));
    }
}
