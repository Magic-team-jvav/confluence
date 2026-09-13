package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.whip.WhipAttackEntity;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record WhipPlaybackCompletePacketC2S(int entityId) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("whip_playback_complete");
    public static final PortStreamCodec<ByteBuf, WhipPlaybackCompletePacketC2S> STREAM_CODEC = PortByteBufCodecs.VAR_INT.map(WhipPlaybackCompletePacketC2S::new, WhipPlaybackCompletePacketC2S::entityId);

    @Override
    public ResourceLocation identifier() {return ID;}

    @Override
    public void work(ServerPlayer player) {
        if (player.level().getEntity(entityId) instanceof WhipAttackEntity attack)
            attack.confirmPlaybackComplete(player);
    }
}
