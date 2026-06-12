package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gameevent.GoblinArmyProgressRenderer;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record GoblinArmyProgressPacketS2C(float progress) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("goblin_army_progress");
    public static final PortStreamCodec<ByteBuf, GoblinArmyProgressPacketS2C> STREAM_CODEC = PortByteBufCodecs.FLOAT
            .map(GoblinArmyProgressPacketS2C::new, GoblinArmyProgressPacketS2C::progress);

    @Override
    public void work(Player player) {
        GoblinArmyProgressRenderer.handleProgress(progress);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    public static void sendToAll(float progress) {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            Confluence.NETWORK_HANDLER.sendToAllPlayers(new GoblinArmyProgressPacketS2C(progress));
        }
    }
}
