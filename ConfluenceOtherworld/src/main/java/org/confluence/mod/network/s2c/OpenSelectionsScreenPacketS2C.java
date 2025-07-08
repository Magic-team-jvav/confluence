package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.SelectionsScreen;

/**
 * @see SelectionsScreen
 * @see org.confluence.mod.network.c2s.ApplySelectionPacketC2S
 */
public record OpenSelectionsScreenPacketS2C(Component[] selections, boolean[] enables) implements CustomPacketPayload {
    public static final Type<OpenSelectionsScreenPacketS2C> TYPE = new Type<>(Confluence.asResource("open_selections_screen_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSelectionsScreenPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public OpenSelectionsScreenPacketS2C decode(RegistryFriendlyByteBuf buffer) {
            int length = buffer.readInt();
            Component[] selections = new Component[length];
            boolean[] enables = new boolean[length];
            for (int i = 0; i < length; i++) {
                selections[i] = ComponentSerialization.STREAM_CODEC.decode(buffer);
                enables[i] = buffer.readBoolean();
            }
            return new OpenSelectionsScreenPacketS2C(selections, enables);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, OpenSelectionsScreenPacketS2C value) {
            int length = value.selections.length;
            buffer.writeInt(length);
            for (int i = 0; i < length; i++) {
                ComponentSerialization.STREAM_CODEC.encode(buffer, value.selections[i]);
                buffer.writeBoolean(value.enables[i]);
            }
        }
    };

    @Override
    public Type<OpenSelectionsScreenPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                SelectionsScreen.handlePacket(this);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToClient(ServerPlayer serverPlayer, Component[] selections, boolean[] enables) {
        PacketDistributor.sendToPlayer(serverPlayer, new OpenSelectionsScreenPacketS2C(selections, enables));
    }
}
