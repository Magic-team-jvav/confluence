package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.whip.WhipAttackEntity;

public record WhipPlaybackCompletePacketC2S(int entityId) implements IPacketC2S {
    public static final Type<WhipPlaybackCompletePacketC2S> TYPE = Confluence.createType("whip_playback_complete");
    public static final StreamCodec<ByteBuf, WhipPlaybackCompletePacketC2S> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(WhipPlaybackCompletePacketC2S::new, WhipPlaybackCompletePacketC2S::entityId);

    @Override
    public Type<WhipPlaybackCompletePacketC2S> type() {return TYPE;}

    @Override
    public void work(ServerPlayer player) {
        if (player.level().getEntity(entityId) instanceof WhipAttackEntity attack)
            attack.confirmPlaybackComplete(player);
    }
}
