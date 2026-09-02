package org.confluence.mod.network.c2s;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/// 从 NPC 对话界面请求打开商店。
public record OpenNPCTradePacketC2S(int entityId) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("open_npc_trade");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, OpenNPCTradePacketC2S> STREAM_CODEC = PortStreamCodec.composite(PortByteBufCodecs.VAR_INT, OpenNPCTradePacketC2S::entityId, OpenNPCTradePacketC2S::new);

    @Override
    public void work(ServerPlayer player) {
        if (player.level().getEntity(entityId) instanceof BaseNPC npc && npc.canTradeWith(player))
            npc.openTradeMenu(player);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    public static void sendToServer(int entityId) {
        Confluence.NETWORK_HANDLER.sendToServer(new OpenNPCTradePacketC2S(entityId));
    }
}
