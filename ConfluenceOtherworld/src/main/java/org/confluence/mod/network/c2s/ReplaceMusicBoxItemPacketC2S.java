package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.accessory.MusicBoxItem;
import org.confluence.terra_curio.TerraCurio;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public record ReplaceMusicBoxItemPacketC2S(int index, MusicBoxItem item) implements CustomPacketPayload {
    public static final Type<ReplaceMusicBoxItemPacketC2S> TYPE = new Type<>(Confluence.asResource("replace_music_box"));
    public static final StreamCodec<ByteBuf, ReplaceMusicBoxItemPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p -> p.index,
            ByteBufCodecs.INT, p -> Item.getId(p.item),
            (index, id) -> new ReplaceMusicBoxItemPacketC2S(index, (MusicBoxItem) Item.byId(id))
    );

    @Override
    public Type<ReplaceMusicBoxItemPacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                IDynamicStackHandler accessories = CuriosApi.getCuriosInventory(serverPlayer).orElseThrow().getCurios().get(TerraCurio.CURIO_SLOT).getStacks();
                ItemStack stack = accessories.getStackInSlot(index);
                accessories.setPreviousStackInSlot(index, stack);
                accessories.setStackInSlot(index, stack.transmuteCopy(item));
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToServer(int index, MusicBoxItem item) {
        PacketDistributor.sendToServer(new ReplaceMusicBoxItemPacketC2S(index, item));
    }
}
