package org.confluence.mod.network.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;

public record ExtraInventoryStackPacketS2C(long packedData, ItemStack itemStack) implements IPacketS2C {
    public static final Type<ExtraInventoryStackPacketS2C> TYPE = Confluence.createType("extra_inventory_stack");
    public static final StreamCodec<RegistryFriendlyByteBuf, ExtraInventoryStackPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, ExtraInventoryStackPacketS2C::packedData,
            ItemStack.OPTIONAL_STREAM_CODEC, ExtraInventoryStackPacketS2C::itemStack,
            ExtraInventoryStackPacketS2C::new
    );

    @Override
    public Type<ExtraInventoryStackPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        if (player.level().getEntity(getEntityId()) instanceof Player entity) {
            ExtraInventory extraInventory = ExtraInventory.of(entity);
            if (extraInventory.getSizeAccessoryDye() != getSizeAccessoryDye()) {
                extraInventory.setAccessoryDyes(player, getSizeAccessoryDye());
            }
            extraInventory.setItem(getSlot(), itemStack);
        }
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
