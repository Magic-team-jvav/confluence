package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.SelectionsScreen;
import org.jetbrains.annotations.Nullable;

/**
 * @see SelectionsScreen
 * @see org.confluence.mod.network.s2c.OpenSelectionsScreenPacketS2C
 */
public record ApplySelectionPacketC2S(byte selected) implements CustomPacketPayload {
    public static final Type<ApplySelectionPacketC2S> TYPE = new Type<>(Confluence.asResource("apply_selection_c2s"));
    public static final StreamCodec<ByteBuf, ApplySelectionPacketC2S> STREAM_CODEC = ByteBufCodecs.BYTE.map(ApplySelectionPacketC2S::new, ApplySelectionPacketC2S::selected);

    @Override
    public Type<ApplySelectionPacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                ItemStack itemStack = serverPlayer.getMainHandItem();
                if (itemStack.getItem() instanceof ISelectable<?> selectable) {
                    selectable.applySelected(selected, itemStack, serverPlayer);
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToServer(byte selected) {
        PacketDistributor.sendToServer(new ApplySelectionPacketC2S(selected));
    }

    public interface ISelectable<T> {
        @Nullable T getSelected(byte index, ItemStack itemStack);

        void applySelected(byte index, ItemStack itemStack, ServerPlayer serverPlayer);
    }
}
