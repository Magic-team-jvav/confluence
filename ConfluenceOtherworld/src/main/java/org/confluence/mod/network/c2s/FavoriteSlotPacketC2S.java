package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.TerraInventoryActions;

/// Alt+点击：切换当前容器某个槽位物品的“收藏”状态（只对玩家自己的背包生效）。
public record FavoriteSlotPacketC2S(int slot) implements IPacketC2S {
    public static final Type<FavoriteSlotPacketC2S> TYPE = Confluence.createType("favorite_slot");
    public static final StreamCodec<ByteBuf, FavoriteSlotPacketC2S> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(FavoriteSlotPacketC2S::new, FavoriteSlotPacketC2S::slot);

    @Override
    public Type<FavoriteSlotPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        TerraInventoryActions.toggleFavorite(player, slot);
    }

    public static void sendToServer(int slot) {
        PacketDistributor.sendToServer(new FavoriteSlotPacketC2S(slot));
    }
}
