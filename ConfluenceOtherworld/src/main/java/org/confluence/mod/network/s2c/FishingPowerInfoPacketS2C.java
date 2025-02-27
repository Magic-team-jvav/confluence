package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.util.PlayerUtils;
import org.jetbrains.annotations.NotNull;

public record FishingPowerInfoPacketS2C(float value) implements CustomPacketPayload {
    public static final Type<FishingPowerInfoPacketS2C> TYPE = new Type<>(Confluence.asResource("fishing_power_info"));
    public static final StreamCodec<ByteBuf, FishingPowerInfoPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, p -> p.value,
            FishingPowerInfoPacketS2C::new
    );

    @Override
    public @NotNull Type<FishingPowerInfoPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                ClientPacketHandler.handleFishingPower(this);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        float fishingPower = PlayerUtils.getFishingPower(serverPlayer);
        PacketDistributor.sendToPlayer(serverPlayer, new FishingPowerInfoPacketS2C(fishingPower));
    }
}
