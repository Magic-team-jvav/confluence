package org.confluence.mod.network.c2s;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.ExtraInventoryMenu;
import org.confluence.mod.common.menu.NPCReforgeMenu;
import org.confluence.mod.common.menu.NPCTradesForgeMenu;
import top.theillusivec4.curios.common.network.server.SPacketGrabbedItem;

public record OpenMenuPacketC2S(int menuId, ItemStack stack) implements CustomPacketPayload {
    public static final int EXTRA_INVENTORY = 0;
    public static final int MAID_TRADE_MENU = 1;
    public static final int NPC_REFORGE_MENU = 2;
    private static final Object2ObjectMap<Integer, Tuple<MenuConstructor, Component>> MENU_TYPES = Util.make(new Object2ObjectOpenHashMap<>(), map -> {
        map.put(EXTRA_INVENTORY, new Tuple<>((containerId, playerInventory, player) -> new ExtraInventoryMenu(containerId, playerInventory), Component.empty()));
        map.put(MAID_TRADE_MENU, new Tuple<>((containerId, playerInventory, player) -> new NPCTradesForgeMenu(containerId, playerInventory), Component.translatable("title.confluence.touhoulittlemaid")));
        map.put(NPC_REFORGE_MENU, new Tuple<>((containerId, playerInventory, player) -> new NPCReforgeMenu(containerId, playerInventory), Component.empty()));
    });
    public static final Type<OpenMenuPacketC2S> TYPE = new Type<>(Confluence.asResource("open_menu"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenMenuPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, p -> p.menuId,
            ItemStack.OPTIONAL_STREAM_CODEC, p -> p.stack,
            OpenMenuPacketC2S::new
    );

    @Override
    public Type<OpenMenuPacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                Tuple<MenuConstructor, Component> tuple = MENU_TYPES.get(menuId);
                if (tuple != null) {
                    ItemStack itemStack = serverPlayer.isCreative() ? stack : serverPlayer.containerMenu.getCarried();
                    serverPlayer.containerMenu.setCarried(ItemStack.EMPTY);
                    serverPlayer.openMenu(new SimpleMenuProvider(tuple.getA(), tuple.getB()));
                    if (!itemStack.isEmpty()) {
                        serverPlayer.containerMenu.setCarried(itemStack);
                        PacketDistributor.sendToPlayer(serverPlayer, new SPacketGrabbedItem(itemStack));
                    }
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToServer(int menuId, ItemStack stack) {
        PacketDistributor.sendToServer(new OpenMenuPacketC2S(menuId, stack));
    }
}
