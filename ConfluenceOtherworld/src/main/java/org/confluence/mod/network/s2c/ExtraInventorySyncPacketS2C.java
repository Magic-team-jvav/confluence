package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModAttachmentTypes;

public record ExtraInventorySyncPacketS2C(int entityId, ExtraInventory extraInventory) implements CustomPacketPayload {
    public static final Type<ExtraInventorySyncPacketS2C> TYPE = new Type<>(Confluence.asResource("extra_inventory_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ExtraInventorySyncPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ExtraInventorySyncPacketS2C::entityId,
            ExtraInventory.STREAM_CODEC, ExtraInventorySyncPacketS2C::extraInventory,
            ExtraInventorySyncPacketS2C::new
    );

    @Override
    public Type<ExtraInventorySyncPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.isLocalPlayer() && player.level().getEntity(entityId) instanceof Player entity) {
                ExtraInventory.of(entity).copyFrom(extraInventory);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToClient(ServerPlayer from, ServerPlayer to, ExtraInventory extraInventory) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            extraInventory.initialize(from);
            PacketDistributor.sendToPlayer(from, new ExtraInventorySyncPacketS2C(to.getId(), extraInventory));
        }
    }

    public static void sendToPlayersTrackingEntityAndSelf(ServerPlayer from, ServerPlayer to, ExtraInventory extraInventory) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            extraInventory.initialize(from);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(from, new ExtraInventorySyncPacketS2C(to.getId(), extraInventory));
        }
    }
}
