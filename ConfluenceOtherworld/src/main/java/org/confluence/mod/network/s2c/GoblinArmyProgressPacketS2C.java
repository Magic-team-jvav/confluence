package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gameevent.GoblinArmyProgressRenderer;

public record GoblinArmyProgressPacketS2C(float progress) implements IPacketS2C {
    public static final Type<GoblinArmyProgressPacketS2C> TYPE = Confluence.createType("goblin_army_progress");
    public static final StreamCodec<ByteBuf, GoblinArmyProgressPacketS2C> STREAM_CODEC = ByteBufCodecs.FLOAT
            .map(GoblinArmyProgressPacketS2C::new, GoblinArmyProgressPacketS2C::progress);

    @Override
    public void work(Player player) {
        GoblinArmyProgressRenderer.handleProgress(progress);
    }

    @Override
    public Type<GoblinArmyProgressPacketS2C> type() {
        return TYPE;
    }

    public static void sendToAll(float progress) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(new GoblinArmyProgressPacketS2C(progress));
        }
    }
}
