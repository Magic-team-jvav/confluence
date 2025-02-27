package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.jetbrains.annotations.NotNull;

public record ManaPacketS2C(int maxMana, int currentMana) implements CustomPacketPayload {
    public static final Type<ManaPacketS2C> TYPE = new Type<>(Confluence.asResource("mana"));
    public static final StreamCodec<ByteBuf, ManaPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ManaPacketS2C::maxMana,
            ByteBufCodecs.INT,
            ManaPacketS2C::currentMana,
            ManaPacketS2C::new
    );

    @Override
    public @NotNull Type<ManaPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                ClientPacketHandler.handleMana(this, context.player());
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }
}
