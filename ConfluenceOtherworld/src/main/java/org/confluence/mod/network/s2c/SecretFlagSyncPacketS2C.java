package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;

public record SecretFlagSyncPacketS2C(long flag) implements CustomPacketPayload {
    public static final Type<SecretFlagSyncPacketS2C> TYPE = new Type<>(Confluence.asResource("secret_flag_sync"));
    public static final StreamCodec<ByteBuf, SecretFlagSyncPacketS2C> STREAM_CODEC = ByteBufCodecs.VAR_LONG.map(SecretFlagSyncPacketS2C::new, SecretFlagSyncPacketS2C::flag);

    @Override
    public Type<SecretFlagSyncPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                ClientPacketHandler.handleSecretFlag(this);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToAll(long flag) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(new SecretFlagSyncPacketS2C(flag));
        }
    }
}
