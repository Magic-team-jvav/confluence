package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.flail.BaseFlailItem;

/**
 * <h1>连枷控制包C2S</h1>
 * 玩家按住/松开攻击键时发送，控制连枷 SPIN/THROWN/STAY/RETRACT 状态
 */
public final class FlailControlPacketC2S implements IPacketC2S {
    public enum Action {
        /** 按住**/
        HOLD,
        /** 松开**/
        RELEASE
    }

    private final Action action;
    private static final FlailControlPacketC2S HOLD_INSTANCE = new FlailControlPacketC2S(Action.HOLD);
    private static final FlailControlPacketC2S RELEASE_INSTANCE = new FlailControlPacketC2S(Action.RELEASE);

    public static final Type<FlailControlPacketC2S> TYPE = Confluence.createType("flail_control");
    public static final StreamCodec<ByteBuf, FlailControlPacketC2S> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public FlailControlPacketC2S decode(ByteBuf buf) {
            return buf.readBoolean() ? HOLD_INSTANCE : RELEASE_INSTANCE;
        }

        @Override
        public void encode(ByteBuf buf, FlailControlPacketC2S packet) {
            buf.writeBoolean(packet.action == Action.HOLD);
        }
    };

    private FlailControlPacketC2S(Action action) {
        this.action = action;
    }

    @Override
    public Type<FlailControlPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BaseFlailItem flailItem)) {
            return;
        }
        if (flailItem.isAutoSwing()) {
            if (action == Action.HOLD) {
                flailItem.tryAutoSwing(player, stack);
            }
            return;
        }
        if (action == Action.HOLD) {
            BaseFlailItem.press(player, stack);
        } else {
            BaseFlailItem.release(player, stack);
        }
    }

    public static void sendHold() {
        PacketDistributor.sendToServer(HOLD_INSTANCE);
    }

    public static void sendRelease() {
        PacketDistributor.sendToServer(RELEASE_INSTANCE);
    }
}
