package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MenuConstructor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.ExtraInventoryMenu;
import org.jetbrains.annotations.NotNull;

public record OpenMenuPacketC2S(int menuId) implements CustomPacketPayload {
    public static final int EXTRA_INVENTORY = 0;
    private static final Object2ObjectMap<Integer, Tuple<MenuConstructor, Component>> MENU_TYPES = Util.make(new Object2ObjectOpenHashMap<>(), map -> {
        map.put(EXTRA_INVENTORY, new Tuple<>((containerId, playerInventory, player) -> new ExtraInventoryMenu(containerId, playerInventory), Component.translatable("container.confluence.coin")));
    });
    public static final Type<OpenMenuPacketC2S> TYPE = new Type<>(Confluence.asResource("open_menu"));
    public static final StreamCodec<ByteBuf, OpenMenuPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, p -> p.menuId,
            OpenMenuPacketC2S::new
    );

    @Override
    public @NotNull Type<OpenMenuPacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                Tuple<MenuConstructor, Component> tuple = MENU_TYPES.get(menuId);
                if (tuple != null) {
                    serverPlayer.openMenu(new SimpleMenuProvider(tuple.getA(), tuple.getB()));
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToServer(int menuId) {
        PacketDistributor.sendToServer(new OpenMenuPacketC2S(menuId));
    }
}
