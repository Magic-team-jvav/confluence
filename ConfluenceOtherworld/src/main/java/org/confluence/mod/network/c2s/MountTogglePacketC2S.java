package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.mount.MountManager;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/**
 * 客户端快捷坐骑请求。
 *
 * <p>数据包没有客户端可填写的参数。服务端收到后重新读取玩家的坐骑槽，
 * 决定召唤哪种坐骑或解除当前坐骑。</p>
 */
public record MountTogglePacketC2S() implements IPortPacket.C2S {
    public static final MountTogglePacketC2S INSTANCE =
            new MountTogglePacketC2S();
    public static final ResourceLocation ID = Confluence.asResource("mount_toggle");
    public static final PortStreamCodec<ByteBuf, MountTogglePacketC2S> STREAM_CODEC =
            PortStreamCodec.unit(INSTANCE);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    /**
     * 坐骑切换会创建或移除实体，必须回到服务端主线程执行。
     */
    @Override
    public void handle(IPortPacket.Context context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> work(player));
        }
    }

    @Override
    public void work(ServerPlayer player) {
        MountManager.toggleFromSlot(player);
    }

    public static void sendToServer() {
        Confluence.NETWORK_HANDLER.sendToServer(INSTANCE);
    }
}
