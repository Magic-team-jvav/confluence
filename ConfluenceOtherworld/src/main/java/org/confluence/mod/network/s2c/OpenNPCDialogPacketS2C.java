package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.screen.NPCDialogScreen;

///
public record OpenNPCDialogPacketS2C(int entityId, boolean canTrade) implements IPacketS2C {
    public static final Type<OpenNPCDialogPacketS2C> TYPE = Confluence.createType("open_npc_dialog");
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenNPCDialogPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, OpenNPCDialogPacketS2C::entityId,
            ByteBufCodecs.BOOL, OpenNPCDialogPacketS2C::canTrade,
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
    public Type<OpenNPCDialogPacketS2C> type() {
        return TYPE;
    }
}
