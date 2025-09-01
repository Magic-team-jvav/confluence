package org.confluence.mod.network.c2s;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.common.menu.ExtraInventoryMenu;
import org.confluence.mod.common.menu.NPCReforgeMenu;
import org.confluence.mod.common.menu.NPCTradesForgeMenu;
import org.confluence.mod.network.IPacket;
import top.theillusivec4.curios.common.network.server.SPacketGrabbedItem;

public record OpenMenuPacketC2S(byte menuId, ItemStack stack) implements IPacketC2S {
    public static final byte EXTRA_INVENTORY = 0;
    public static final byte MAID_TRADE_MENU = 1;
    public static final byte NPC_REFORGE_MENU = 2;
    private static final Object2ObjectMap<Byte, Tuple<MenuConstructor, Component>> MENU_TYPES = Util.make(new Object2ObjectOpenHashMap<>(), map -> {
        map.put(EXTRA_INVENTORY, new Tuple<>((containerId, playerInventory, player) -> new ExtraInventoryMenu(containerId, playerInventory), Component.empty()));
        map.put(MAID_TRADE_MENU, new Tuple<>((containerId, playerInventory, player) -> new NPCTradesForgeMenu(containerId, playerInventory), Component.translatable("title.confluence.touhoulittlemaid")));
        map.put(NPC_REFORGE_MENU, new Tuple<>((containerId, playerInventory, player) -> new NPCReforgeMenu(containerId, playerInventory), Component.empty()));
    });
    public static final Type<OpenMenuPacketC2S> TYPE = IPacket.createType("open_menu");
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenMenuPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, OpenMenuPacketC2S::menuId,
            ItemStack.OPTIONAL_STREAM_CODEC, OpenMenuPacketC2S::stack,
            OpenMenuPacketC2S::new
    );

    @Override
    public Type<OpenMenuPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        Tuple<MenuConstructor, Component> tuple = MENU_TYPES.get(menuId);
        if (tuple != null) {
            ItemStack itemStack = player.isCreative() ? stack : player.containerMenu.getCarried();
            player.containerMenu.setCarried(ItemStack.EMPTY);
            player.openMenu(new SimpleMenuProvider(tuple.getA(), tuple.getB()));
            if (!itemStack.isEmpty()) {
                player.containerMenu.setCarried(itemStack);
                PacketDistributor.sendToPlayer(player, new SPacketGrabbedItem(itemStack));
            }
        }
    }

    public static void sendToServer(byte menuId, ItemStack stack) {
        PacketDistributor.sendToServer(new OpenMenuPacketC2S(menuId, stack));
    }
}
