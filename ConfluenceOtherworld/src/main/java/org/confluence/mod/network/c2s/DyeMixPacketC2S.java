package org.confluence.mod.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.network.IPacket;

public record DyeMixPacketC2S(ItemStack dye) implements IPacketC2S {
    public static final Type<DyeMixPacketC2S> TYPE = IPacket.createType("dye_mix");
    public static final StreamCodec<RegistryFriendlyByteBuf, DyeMixPacketC2S> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC.map(DyeMixPacketC2S::new, DyeMixPacketC2S::dye);

    @Override
    public Type<DyeMixPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        AbstractContainerMenu menu = player.containerMenu;
        Slot red = menu.getSlot(0);
        Slot green = menu.getSlot(1);
        Slot blue = menu.getSlot(2);
        if (red.hasItem() && green.hasItem() && blue.hasItem()) {
            red.remove(1);
            green.remove(1);
            blue.remove(1);
            ItemStack carried = menu.getCarried();
            if (carried.isEmpty()) {
                menu.setCarried(dye);
            } else if (ItemStack.isSameItemSameComponents(carried, dye) && carried.getCount() < carried.getMaxStackSize()) {
                carried.grow(1);
            }
        }
    }

    public static void sendToServer(ItemStack dye) {
        PacketDistributor.sendToServer(new DyeMixPacketC2S(dye));
    }
}
