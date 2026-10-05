package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.GunEvent;

public enum ShotFeedbackPacketS2C implements IPacketS2C {
    INSTANCE;

    public static final Type<ShotFeedbackPacketS2C> TYPE = Confluence.createType("shot_feedback");
    public static final StreamCodec<RegistryFriendlyByteBuf, ShotFeedbackPacketS2C> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public void work(Player player) {
        NeoForge.EVENT_BUS.post(new GunEvent.ShotConfirmed(player));
    }

    @Override
    public Type<ShotFeedbackPacketS2C> type() {
        return TYPE;
    }

    public static void sendTo(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, INSTANCE);
    }
}
