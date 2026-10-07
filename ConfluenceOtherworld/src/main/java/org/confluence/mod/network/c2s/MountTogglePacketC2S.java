package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.mount.MountManager;

/// 客户端快捷坐骑请求。
///
/// 数据包没有客户端可填写的参数。服务端收到后重新读取玩家的坐骑槽，
/// 决定召唤哪种坐骑或解除当前坐骑。
public record MountTogglePacketC2S() implements IPacketC2S {
    public static final MountTogglePacketC2S INSTANCE = new MountTogglePacketC2S();
    public static final Type<MountTogglePacketC2S> TYPE = Confluence.createType("mount_toggle");
    public static final StreamCodec<ByteBuf, MountTogglePacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<MountTogglePacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        MountManager.toggleFromSlot(player);
    }

    public static void sendToServer() {
        PacketDistributor.sendToServer(INSTANCE);
    }
}
