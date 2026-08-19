package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.mount.AbstractMountEntity;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/// 所有本体坐骑共用的客户端输入边沿。
///
/// <p>方向移动继续复用原版 {@code ServerboundPlayerInputPacket}；本包只补充原版
/// 马匹蓄力协议无法表达的“跳跃键按下/释放”状态。包中没有实体 ID、速度、能量
/// 或坐标，服务端始终从玩家当前载具重新解析并验证控制权。</p>
public record MountInputPacketC2S(boolean jumping) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("mount_input");
    public static final PortStreamCodec<ByteBuf, MountInputPacketC2S>
            STREAM_CODEC = org.mesdag.portlib.network.codec.PortByteBufCodecs.BOOL.map(MountInputPacketC2S::new, MountInputPacketC2S::jumping);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    /// 输入会修改坐骑同步状态，必须回到服务端主线程执行。
    @Override
    public void handle(IPortPacket.Context context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> work(player));
        }
    }

    @Override
    public void work(ServerPlayer player) {
        if (player.getVehicle() instanceof AbstractMountEntity mount) {
            mount.setControllerJumpInput(player, jumping);
        }
    }

    /// 发送一次跳跃键状态变化，不接受客户端提供坐骑实体或运动数值。
    public static void sendToServer(boolean jumping) {
        Confluence.NETWORK_HANDLER.sendToServer(new MountInputPacketC2S(jumping));
    }
}
