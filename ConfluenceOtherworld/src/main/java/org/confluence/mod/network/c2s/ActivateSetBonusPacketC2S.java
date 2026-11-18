package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.armor.ModArmorBonus;

/// 客户端“激活套装奖励”按键触发的请求。
public enum ActivateSetBonusPacketC2S implements IPacketC2S {
    INSTANCE;
    public static final Type<ActivateSetBonusPacketC2S> TYPE = Confluence.createType("activate_set_bonus");
    public static final StreamCodec<ByteBuf, ActivateSetBonusPacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<ActivateSetBonusPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        ModArmorBonus.activateArmorSetBonus(player);
    }

    public static void send2Server() {
        PacketDistributor.sendToServer(INSTANCE);
    }
}
