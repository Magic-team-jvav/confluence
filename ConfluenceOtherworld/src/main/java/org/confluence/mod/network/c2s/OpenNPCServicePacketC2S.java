package org.confluence.mod.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.BaseNPC;

public record OpenNPCServicePacketC2S(int entityId, byte service) implements IPacketC2S {
    public static final byte TRADE = 0;
    public static final byte REFORGE = 1;
    public static final Type<OpenNPCServicePacketC2S> TYPE = Confluence.createType("open_npc_service");
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenNPCServicePacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, OpenNPCServicePacketC2S::entityId,
            ByteBufCodecs.BYTE, OpenNPCServicePacketC2S::service, OpenNPCServicePacketC2S::new);

    @Override
    public void work(ServerPlayer player) {
        if (!(player.level().getEntity(entityId) instanceof BaseNPC npc)) return;
        switch (service) {
            case TRADE -> npc.openTradeMenu(player);
            case REFORGE -> npc.openReforgeMenu(player);
        }
    }

    @Override
    public Type<OpenNPCServicePacketC2S> type() {
        return TYPE;
    }

    public static void sendToServer(int entityId, byte service) {
        PacketDistributor.sendToServer(new OpenNPCServicePacketC2S(entityId, service));
    }
}
