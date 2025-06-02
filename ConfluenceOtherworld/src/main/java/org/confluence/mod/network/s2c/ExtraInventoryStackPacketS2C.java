package org.confluence.mod.network.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.jetbrains.annotations.NotNull;

public record ExtraInventoryStackPacketS2C(long packedData, ItemStack itemStack) implements CustomPacketPayload {
    public static final Type<ExtraInventoryStackPacketS2C> TYPE = new Type<>(Confluence.asResource("extra_inventory_stack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ExtraInventoryStackPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, p -> p.packedData,
            ItemStack.OPTIONAL_STREAM_CODEC, p -> p.itemStack,
            ExtraInventoryStackPacketS2C::new
    );

    @Override
    public @NotNull Type<ExtraInventoryStackPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.isLocalPlayer() && player.level().getEntity(getEntityId()) instanceof Player entity) {
                ExtraInventory extraInventory = entity.getData(ModAttachmentTypes.EXTRA_INVENTORY);
                extraInventory.setAccessoryDyes(player, getSizeAccessoryDye());
                extraInventory.setItem(getSlot(), itemStack);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    private int getEntityId() {
        return BlockPos.getX(packedData);
    }

    private int getSizeAccessoryDye() {
        return BlockPos.getY(packedData);
    }

    private int getSlot() {
        return BlockPos.getZ(packedData);
    }

    public static void sendToPlayersTrackingEntityAndSelf(ServerPlayer serverPlayer, ServerPlayer player, int sizeAccessoryDye, int slot, ItemStack itemStack) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            long packedData = BlockPos.asLong(player.getId(), sizeAccessoryDye, slot);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new ExtraInventoryStackPacketS2C(packedData, itemStack));
        }
    }
}
