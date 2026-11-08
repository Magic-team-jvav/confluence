package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;

/// 客户端的界面模式（泰拉 / 原版）。服务端只用它决定右键制作站是否打开泰拉背包。
public record TerraUiModePacketC2S(boolean terraria) implements IPacketC2S {
    public static final Type<TerraUiModePacketC2S> TYPE = Confluence.createType("terra_ui_mode");
    public static final StreamCodec<ByteBuf, TerraUiModePacketC2S> STREAM_CODEC = ByteBufCodecs.BOOL.map(TerraUiModePacketC2S::new, TerraUiModePacketC2S::terraria);
    private static final String KEY = "confluence_terraria_ui";

    /// 没收到过客户端设置时默认为泰拉界面
    public static boolean isTerrariaUi(Player player) {
        return !player.getPersistentData().contains(KEY) || player.getPersistentData().getBoolean(KEY);
    }

    @Override
    public Type<TerraUiModePacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        player.getPersistentData().putBoolean(KEY, terraria);
    }

    /// 发送到服务端，并同步写到客户端的玩家（右键事件两端都会触发）
    public static void sendToServer(Player localPlayer, boolean terraria) {
        if (localPlayer != null) localPlayer.getPersistentData().putBoolean(KEY, terraria);
        PacketDistributor.sendToServer(new TerraUiModePacketC2S(terraria));
    }
}
