package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.yoyo.YoyoItem;
import org.confluence.mod.common.item.yoyo.YoyoSession;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/// 悠悠球左键控制包。
///
/// 客户端只提交按下、松开或一格距离调整；实体、目标、伤害和最大射程全部由服务端解析。
public record YoyoControlPacketC2S(Action action, int amount)
        implements IPortPacket.C2S {
    public enum Action {
        PRESS,
        RELEASE,
        ADJUST_RANGE
    }

    public static final ResourceLocation ID = Confluence.asResource("yoyo_control");
    public static final PortStreamCodec<ByteBuf, YoyoControlPacketC2S>
            STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public YoyoControlPacketC2S decode(ByteBuf buffer) {
            int ordinal = buffer.readUnsignedByte();
            if (ordinal >= Action.values().length) {
                throw new IllegalArgumentException("Unknown yoyo control action");
            }
            return new YoyoControlPacketC2S(Action.values()[ordinal], buffer.readByte());
        }

        @Override
        public void encode(ByteBuf buffer, YoyoControlPacketC2S packet) {
            buffer.writeByte(packet.action.ordinal());
            buffer.writeByte(packet.amount);
        }
    };

    public YoyoControlPacketC2S {
        java.util.Objects.requireNonNull(action, "Yoyo control action must not be null");
        if (action == Action.ADJUST_RANGE && amount != -1 && amount != 1 || action != Action.ADJUST_RANGE && amount != 0) {
            throw new IllegalArgumentException("Invalid yoyo control amount");
        }
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        switch (action) {
            case PRESS -> {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof YoyoItem item) {
                    item.press(player, stack);
                }
            }
            case RELEASE -> YoyoSession.of(player).release();
            case ADJUST_RANGE -> YoyoSession.of(player).adjustRange(player, amount);
        }
    }

    public static void sendPress() {
        Confluence.NETWORK_HANDLER.sendToServer(new YoyoControlPacketC2S(Action.PRESS, 0));
    }

    public static void sendRelease() {
        Confluence.NETWORK_HANDLER.sendToServer(new YoyoControlPacketC2S(Action.RELEASE, 0));
    }

    public static void sendRangeAdjustment(int amount) {
        Confluence.NETWORK_HANDLER.sendToServer(new YoyoControlPacketC2S(Action.ADJUST_RANGE, amount));
    }
}
