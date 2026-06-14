package org.confluence.mod.network.c2s;


import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.SelectionsScreen;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/**
 * @see SelectionsScreen
 * @see org.confluence.mod.network.s2c.OpenSelectionsScreenPacketS2C
 */
public record ApplySelectionPacketC2S(byte selected) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("apply_selection_c2s");
    public static final PortStreamCodec<ByteBuf, ApplySelectionPacketC2S> STREAM_CODEC = PortByteBufCodecs.BYTE.map(ApplySelectionPacketC2S::new, ApplySelectionPacketC2S::selected);

    @Override
    public void work(ServerPlayer player) {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() instanceof ISelectable<?> selectable) {
            selectable.applySelected(selected, itemStack, player);
        }
    }

    public static void sendToServer(byte selected) {
        Confluence.NETWORK_HANDLER.sendToServer(new ApplySelectionPacketC2S(selected));
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    public interface ISelectable<T> {
        @Nullable T getSelected(byte index, ItemStack itemStack);

        void applySelected(byte index, ItemStack itemStack, ServerPlayer serverPlayer);
    }
}
