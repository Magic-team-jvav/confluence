package org.confluence.mod.network.c2s;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.OldManNPC;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record SummonSkeletronPacketC2S(int entityId) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("summon_skeletron");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, SummonSkeletronPacketC2S> STREAM_CODEC = PortStreamCodec.composite(PortByteBufCodecs.VAR_INT, SummonSkeletronPacketC2S::entityId, SummonSkeletronPacketC2S::new);

    @Override
    public void work(ServerPlayer player) {
        if (player.level().getEntity(entityId) instanceof OldManNPC oldMan)
            oldMan.summonSkeletron(player);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    public static void sendToServer(int entityId) {
        PortPacketDistributor.sendToServer(new SummonSkeletronPacketC2S(entityId));
    }
}
