package org.confluence.mod.network.s2c;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record ExtraInventoryStackPacketS2C(long packedData,
                                           ItemStack itemStack) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("extra_inventory_stack");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, ExtraInventoryStackPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VAR_LONG, ExtraInventoryStackPacketS2C::packedData,
            PortItemStackExtension.optionalStreamCodec(), ExtraInventoryStackPacketS2C::itemStack,
            ExtraInventoryStackPacketS2C::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
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
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            long packedData = BlockPos.asLong(player.getId(), sizeAccessoryDye, slot);
            Confluence.NETWORK_HANDLER.sendToPlayersTrackingEntityAndSelf(serverPlayer, new ExtraInventoryStackPacketS2C(packedData, itemStack));
        }
    }
}
