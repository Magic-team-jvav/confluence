package org.confluence.mod.network.s2c;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.SelectionsScreen;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.chat.PortComponentSerialization;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/**
 * @see SelectionsScreen
 * @see org.confluence.mod.network.c2s.ApplySelectionPacketC2S
 */
public record OpenSelectionsScreenPacketS2C(Component[] selections,
                                            boolean[] enables) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("open_selections_screen_s2c");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, OpenSelectionsScreenPacketS2C> STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public OpenSelectionsScreenPacketS2C decode(PortRegistryFriendlyByteBuf buffer) {
            int length = buffer.readInt();
            Component[] selections = new Component[length];
            boolean[] enables = new boolean[length];
            for (int i = 0; i < length; i++) {
                selections[i] = PortComponentSerialization.STREAM_CODEC.decode(buffer);
                enables[i] = buffer.readBoolean();
            }
            return new OpenSelectionsScreenPacketS2C(selections, enables);
        }

        @Override
        public void encode(PortRegistryFriendlyByteBuf buffer, OpenSelectionsScreenPacketS2C value) {
            int length = value.selections.length;
            buffer.writeInt(length);
            for (int i = 0; i < length; i++) {
                PortComponentSerialization.STREAM_CODEC.encode(buffer, value.selections[i]);
                buffer.writeBoolean(value.enables[i]);
            }
        }
    };

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        SelectionsScreen.handlePacket(this);
    }

    public static void sendToClient(ServerPlayer serverPlayer, Component[] selections, boolean[] enables) {
        Confluence.NETWORK_HANDLER.sendToPlayer(serverPlayer, new OpenSelectionsScreenPacketS2C(selections, enables));
    }
}
