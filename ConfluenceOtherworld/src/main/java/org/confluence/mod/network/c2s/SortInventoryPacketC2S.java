package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.TerraInventoryActions;

/// 泰拉背包的“整理”按钮：合并并排序主背包（不含快捷栏与收藏的物品）。
public record SortInventoryPacketC2S() implements IPacketC2S {
    public static final SortInventoryPacketC2S INSTANCE = new SortInventoryPacketC2S();
    public static final Type<SortInventoryPacketC2S> TYPE = Confluence.createType("sort_inventory");
    public static final StreamCodec<ByteBuf, SortInventoryPacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<SortInventoryPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        TerraInventoryActions.sortInventory(player);
    }

    public static void sendToServer() {
        PacketDistributor.sendToServer(INSTANCE);
    }
}
