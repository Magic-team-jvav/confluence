package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.TerraLoadouts;

/// 泰拉背包里的装备栏位按钮：切换到第 index 套装备
public record SwitchLoadoutPacketC2S(int index) implements IPacketC2S {
    public static final Type<SwitchLoadoutPacketC2S> TYPE = Confluence.createType("switch_loadout");
    public static final StreamCodec<ByteBuf, SwitchLoadoutPacketC2S> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(SwitchLoadoutPacketC2S::new, SwitchLoadoutPacketC2S::index);

    @Override
    public Type<SwitchLoadoutPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        if (!player.containerMenu.getCarried().isEmpty()) return; // 手里拿着东西时不换（防止误操作）
        TerraLoadouts.switchTo(player, index);
    }

    public static void sendToServer(int index) {
        PacketDistributor.sendToServer(new SwitchLoadoutPacketC2S(index));
    }
}
