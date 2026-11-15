package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.mount.AbstractMountEntity;

/// 所有本体坐骑共用的客户端输入边沿。
///
/// 方向移动继续复用原版 {@code ServerboundPlayerInputPacket}；本包只补充原版
/// 马匹蓄力协议无法表达的跳跃、下降按键状态。包中没有实体 ID、速度、能量
/// 或坐标，服务端始终从玩家当前载具重新解析并验证控制权。
public record MountInputPacketC2S(boolean jumping, boolean descending) implements IPacketC2S {
    public static final Type<MountInputPacketC2S> TYPE = Confluence.createType("mount_input");
    public static final StreamCodec<ByteBuf, MountInputPacketC2S>
            STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, MountInputPacketC2S::jumping,
            ByteBufCodecs.BOOL, MountInputPacketC2S::descending,
            MountInputPacketC2S::new);

    @Override
    public Type<MountInputPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        if (player.getVehicle() instanceof AbstractMountEntity mount) {
            mount.setControllerJumpInput(player, jumping);
            mount.setDescendInput(player, descending);
        }
    }

    /// 发送跳跃或下降键的状态变化，不接受客户端提供坐骑实体或运动数值。
    public static void sendToServer(boolean jumping, boolean descending) {
        PacketDistributor.sendToServer(new MountInputPacketC2S(jumping, descending));
    }
}
