package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.CompatibilityHandler;
import org.confluence.mod.common.CommonConfigs;

public record CompatibilitySyncPacketS2c(int data) implements CustomPacketPayload {
    private static ModConfigSpec.BooleanValue[] configs;
    public static final Type<CompatibilitySyncPacketS2c> TYPE = new Type<>(Confluence.asResource("compatibility_sync"));
    public static final StreamCodec<ByteBuf, CompatibilitySyncPacketS2c> STREAM_CODEC = ByteBufCodecs.INT.map(CompatibilitySyncPacketS2c::new, CompatibilitySyncPacketS2c::data);

    @Override
    public Type<CompatibilitySyncPacketS2c> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                CompatibilityHandler.handle(data);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToAll() {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            int data = 0;
            ModConfigSpec.BooleanValue[] arr = getConfigs();
            for (int i = 0; i < arr.length; i++) {
                if (arr[i].get()) data |= (1 << i);
            }
            PacketDistributor.sendToAllPlayers(new CompatibilitySyncPacketS2c(data));
        }
    }

    public static ModConfigSpec.BooleanValue[] getConfigs() {
        if (configs == null) {
            configs = new ModConfigSpec.BooleanValue[]{
                    CommonConfigs.CONVERT_ARS_NOUVEAU_MANA,
                    CommonConfigs.CONVERT_IRONS_SPELL_MANA,
                    CommonConfigs.FTB_CHUNKS_WORMHOLE_POTION,
                    CommonConfigs.XAEROS_MAP_WORMHOLE_POTION
            };
        }
        return configs;
    }
}
