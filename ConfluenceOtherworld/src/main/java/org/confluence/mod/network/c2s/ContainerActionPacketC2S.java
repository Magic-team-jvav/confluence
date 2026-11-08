package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.TerraInventoryActions;

/// 泰拉箱子界面的按钮：全部拿取、全部存放、快速堆叠、整理、补货（见 {@link TerraInventoryActions}）。
public record ContainerActionPacketC2S(byte action) implements IPacketC2S {
    public static final Type<ContainerActionPacketC2S> TYPE = Confluence.createType("container_action");
    public static final StreamCodec<ByteBuf, ContainerActionPacketC2S> STREAM_CODEC = ByteBufCodecs.BYTE.map(ContainerActionPacketC2S::new, ContainerActionPacketC2S::action);

    @Override
    public Type<ContainerActionPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        TerraInventoryActions.containerAction(player, action);
    }

    public static void sendToServer(byte action) {
        PacketDistributor.sendToServer(new ContainerActionPacketC2S(action));
    }
}
