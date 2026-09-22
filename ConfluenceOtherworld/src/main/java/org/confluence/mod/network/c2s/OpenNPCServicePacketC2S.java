package org.confluence.mod.network.c2s;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/// 从已建立的 NPC 交互会话进入服务菜单，不接受客户端提供物品或授权信息。
public record OpenNPCServicePacketC2S(int entityId, byte service) implements IPortPacket.C2S {
    public static final byte TRADE = 0;
    public static final byte REFORGE = 1;
    public static final ResourceLocation ID = Confluence.asResource("open_npc_service");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, OpenNPCServicePacketC2S> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VAR_INT, OpenNPCServicePacketC2S::entityId,
            PortByteBufCodecs.BYTE, OpenNPCServicePacketC2S::service, OpenNPCServicePacketC2S::new);

    @Override
    public void work(ServerPlayer player) {
        if (!(player.level().getEntity(entityId) instanceof BaseNPC npc)) return;
        switch (service) {
            case TRADE -> npc.openTradeMenu(player);
            case REFORGE -> npc.openReforgeMenu(player);
        }
    }

    @Override
    public ResourceLocation identifier() {return ID;}

    public static void sendToServer(int entityId, byte service) {
        PortPacketDistributor.sendToServer(new OpenNPCServicePacketC2S(entityId, service));
    }
}
