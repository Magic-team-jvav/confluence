package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.SelectionsScreen;
import org.confluence.mod.network.ExtraByteBufCodecs;
import org.jetbrains.annotations.NotNull;

/**
 * @see SelectionsScreen
 * @see org.confluence.mod.network.c2s.ApplySelectionPacketC2S
 */
public record OpenSelectionsScreenPacketS2C(Component[] selections, Boolean[] enables) implements CustomPacketPayload {
    public static final Type<OpenSelectionsScreenPacketS2C> TYPE = new Type<>(Confluence.asResource("open_selections_screen_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSelectionsScreenPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ExtraByteBufCodecs.arrayOf(ComponentSerialization.STREAM_CODEC), p -> p.selections,
            ExtraByteBufCodecs.arrayOf(ByteBufCodecs.BOOL), p -> p.enables,
            OpenSelectionsScreenPacketS2C::new
    );

    @Override
    public @NotNull Type<OpenSelectionsScreenPacketS2C> type() {
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

    public static void sendToClient(ServerPlayer serverPlayer, Component[] selections, Boolean[] enables) {
        PacketDistributor.sendToPlayer(serverPlayer, new OpenSelectionsScreenPacketS2C(selections, enables));
    }
}
