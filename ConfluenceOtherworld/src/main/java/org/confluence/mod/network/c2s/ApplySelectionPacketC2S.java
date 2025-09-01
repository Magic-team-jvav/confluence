package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.client.gui.SelectionsScreen;
import org.confluence.mod.network.IPacket;
import org.jetbrains.annotations.Nullable;

/**
 * @see SelectionsScreen
 * @see org.confluence.mod.network.s2c.OpenSelectionsScreenPacketS2C
 */
public record ApplySelectionPacketC2S(byte selected) implements IPacketC2S {
    public static final Type<ApplySelectionPacketC2S> TYPE = IPacket.createType("apply_selection_c2s");
    public static final StreamCodec<ByteBuf, ApplySelectionPacketC2S> STREAM_CODEC = ByteBufCodecs.BYTE.map(ApplySelectionPacketC2S::new, ApplySelectionPacketC2S::selected);

    @Override
    public Type<ApplySelectionPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() instanceof ISelectable<?> selectable) {
            selectable.applySelected(selected, itemStack, player);
        }
    }

    public static void sendToServer(byte selected) {
        PacketDistributor.sendToServer(new ApplySelectionPacketC2S(selected));
    }

    public interface ISelectable<T> {
        @Nullable T getSelected(byte index, ItemStack itemStack);

        void applySelected(byte index, ItemStack itemStack, ServerPlayer serverPlayer);
    }
}
