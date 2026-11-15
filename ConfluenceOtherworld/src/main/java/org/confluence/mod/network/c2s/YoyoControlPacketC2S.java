package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.YoyoSession;
import org.confluence.mod.common.item.yoyo.YoyoItem;

/// 悠悠球左键控制包。
///
/// 客户端只提交按下、松开或一格距离调整；实体、目标、伤害和最大射程全部由服务端解析。
public record YoyoControlPacketC2S(Action action, int amount)
        implements IPacketC2S {
    public enum Action {
        PRESS,
        RELEASE,
        ADJUST_RANGE
    }

    public static final Type<YoyoControlPacketC2S> TYPE = Confluence.createType("yoyo_control");
    public static final StreamCodec<ByteBuf, YoyoControlPacketC2S>
            STREAM_CODEC = new StreamCodec<>() {
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
        if (action == Action.ADJUST_RANGE && amount != -1 && amount != 1 || action != Action.ADJUST_RANGE && amount != 0) {
            throw new IllegalArgumentException("Invalid yoyo control amount");
        }
    }

    @Override
    public Type<YoyoControlPacketC2S> type() {
        return TYPE;
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
            case RELEASE -> YoyoSession.of(player).release(player);
            case ADJUST_RANGE -> YoyoSession.of(player).adjustRange(player, amount);
        }
    }

    public static void sendPress() {
        PacketDistributor.sendToServer(new YoyoControlPacketC2S(Action.PRESS, 0));
    }

    public static void sendRelease() {
        PacketDistributor.sendToServer(new YoyoControlPacketC2S(Action.RELEASE, 0));
    }

    public static void sendRangeAdjustment(int amount) {
        PacketDistributor.sendToServer(new YoyoControlPacketC2S(Action.ADJUST_RANGE, amount));
    }
}
