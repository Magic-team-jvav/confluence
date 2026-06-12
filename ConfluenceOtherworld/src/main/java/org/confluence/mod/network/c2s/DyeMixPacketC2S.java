package org.confluence.mod.network.c2s;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record DyeMixPacketC2S(ItemStack stack) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("dye_mix");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, DyeMixPacketC2S> STREAM_CODEC = PortItemStackExtension.optionalStreamCodec().map(DyeMixPacketC2S::new, DyeMixPacketC2S::stack);

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
                menu.setCarried(stack);
            } else if (PortItemStackExtension.isSameItemSameComponents(carried, stack) && carried.getCount() < carried.getMaxStackSize()) {
                carried.grow(1);
            }
        }
    }

    @Override
    public ResourceLocation identifier() {
        return Confluence.asResource("dye_mix");
    }

    public static void sendToServer(ItemStack stack) {
        Confluence.NETWORK_HANDLER.sendToServer(new DyeMixPacketC2S(stack));
    }
}
