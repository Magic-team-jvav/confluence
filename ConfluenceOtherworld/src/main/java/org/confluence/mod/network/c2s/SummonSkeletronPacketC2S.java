package org.confluence.mod.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.OldManNPC;

///
public record SummonSkeletronPacketC2S(int entityId) implements IPacketC2S {
    public static final Type<SummonSkeletronPacketC2S> TYPE = Confluence.createType("summon_skeletron");
    public static final StreamCodec<RegistryFriendlyByteBuf, SummonSkeletronPacketC2S> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, SummonSkeletronPacketC2S::entityId, SummonSkeletronPacketC2S::new);

    @Override
    public void work(ServerPlayer player) {
        if (player.level().getEntity(entityId) instanceof OldManNPC oldMan)
            oldMan.summonSkeletron(player);
    }

    @Override
    public Type<SummonSkeletronPacketC2S> type() {
        return TYPE;
    }

    public static void sendToServer(int entityId) {
        PacketDistributor.sendToServer(new SummonSkeletronPacketC2S(entityId));
    }
}
