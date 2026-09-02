package org.confluence.mod.network.s2c;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.screen.NPCDialogScreen;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record OpenNPCDialogPacketS2C(int entityId, boolean canTrade) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("open_npc_dialog");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, OpenNPCDialogPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VAR_INT, OpenNPCDialogPacketS2C::entityId,
            PortByteBufCodecs.BOOL, OpenNPCDialogPacketS2C::canTrade,
            OpenNPCDialogPacketS2C::new
    );

    public OpenNPCDialogPacketS2C(int entityId) {
        this(entityId, false);
    }

    @Override
    public void work(Player player) {
        NPCDialogScreen.open(entityId, canTrade);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }
}
