package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.flail.BaseFlailItem;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/**
 * 链锤按下与松开控制包。
 *
 * <p>客户端只提交输入边沿；服务端重新读取主手并调用链锤物品的共享状态转换，
 * 不接受客户端提供的实体、阶段、伤害或冷却数据。</p>
 */
public record FlailControlPacketC2S(Action action)
        implements IPortPacket.C2S {
    public enum Action {
        HOLD,
        RELEASE
    }

    public static final ResourceLocation ID =
            Confluence.asResource("flail_control");
    public static final PortStreamCodec<ByteBuf, FlailControlPacketC2S>
            STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public FlailControlPacketC2S decode(ByteBuf buffer) {
            return new FlailControlPacketC2S(
                    buffer.readBoolean() ? Action.HOLD : Action.RELEASE);
        }

        @Override
        public void encode(
                ByteBuf buffer,
                FlailControlPacketC2S packet
        ) {
            buffer.writeBoolean(packet.action == Action.HOLD);
        }
    };

    public FlailControlPacketC2S {
        java.util.Objects.requireNonNull(
                action, "Flail control action must not be null");
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    /**
     * 链锤控制会生成实体并切换其状态，必须由本数据包显式切回服务端主线程。
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
        Confluence.NETWORK_HANDLER.sendToServer(
                new FlailControlPacketC2S(Action.HOLD));
    }

    public static void sendRelease() {
        Confluence.NETWORK_HANDLER.sendToServer(
                new FlailControlPacketC2S(Action.RELEASE));
    }
}
