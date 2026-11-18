package org.confluence.mod.network.c2s;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/// 客户端“激活套装奖励”按键触发的请求。
public enum ActivateSetBonusPacketC2S implements IPortPacket.C2S {
    INSTANCE;
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, ActivateSetBonusPacketC2S> STREAM_CODEC = PortStreamCodec.unit(INSTANCE);
    public static final ResourceLocation ID = Confluence.asResource("activate_set_bonus");

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        ModArmorBonus.activateArmorSetBonus(player);
    }

    public static void send2Server() {
        PortPacketDistributor.sendToServer(INSTANCE);
    }
}
