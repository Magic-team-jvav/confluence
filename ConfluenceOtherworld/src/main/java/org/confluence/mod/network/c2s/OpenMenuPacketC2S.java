package org.confluence.mod.network.c2s;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.*;
import org.confluence.mod.network.s2c.AvailableHouseSelectPacketS2C;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import top.theillusivec4.curios.common.network.server.SPacketGrabbedItem;

public record OpenMenuPacketC2S(byte menuId, ItemStack stack) implements IPortPacket.C2S {
    public static final byte EXTRA_INVENTORY = 0;
    public static final byte MAID_TRADE_MENU = 1;
    public static final byte NPC_REFORGE_MENU = 2;
    public static final byte DYE_VAT_MENU = 3;
    public static final byte DYE_MIX_MENU = 4;
    private static final Object2ObjectMap<Byte, Tuple<MenuConstructor, Component>> MENU_TYPES = Util.make(new Object2ObjectOpenHashMap<>(), map -> {
        map.put(EXTRA_INVENTORY, new Tuple<>((containerId, playerInventory, player) -> new ExtraInventoryMenu(containerId, playerInventory), Component.empty()));
        map.put(MAID_TRADE_MENU, new Tuple<>((containerId, playerInventory, player) -> new NPCTradesForgeMenu(containerId, playerInventory), Component.translatable("title.confluence.touhoulittlemaid")));
        map.put(NPC_REFORGE_MENU, new Tuple<>((containerId, playerInventory, player) -> new NPCReforgeMenu(containerId, playerInventory), Component.empty()));
        map.put(DYE_VAT_MENU, new Tuple<>((containerId, playerInventory, player) -> new DyeVatMenu(containerId, playerInventory, getAccess(player)), Component.translatable("container.confluence.dye_vat")));
        map.put(DYE_MIX_MENU, new Tuple<>((containerId, playerInventory, player) -> new DyeMixMenu(containerId, playerInventory, getAccess(player)), Component.translatable("container.confluence.dye_mix")));
    });
    public static final ResourceLocation ID = Confluence.asResource("open_menu");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, OpenMenuPacketC2S> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.BYTE, OpenMenuPacketC2S::menuId,
            PortItemStackExtension.optionalStreamCodec(), OpenMenuPacketC2S::stack,
            OpenMenuPacketC2S::new
    );

    private static ContainerLevelAccess getAccess(Player player) {
        Vec3 start = player.getEyePosition(0.5F);
        Vec3 lookVector = player.getViewVector(0.5F);
        double range = Math.max(player.blockInteractionRange(), player.entityInteractionRange());
        Vec3 end = start.add(lookVector.x * range, lookVector.y * range, lookVector.z * range);
        ClipContext context = new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, CollisionContext.of(player));
        BlockHitResult blockResult = player.level().clip(context);
        if (blockResult.getType() == HitResult.Type.BLOCK) {
            return ContainerLevelAccess.create(player.level(), blockResult.getBlockPos());
        }
        return ContainerLevelAccess.NULL;
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        Tuple<MenuConstructor, Component> tuple = MENU_TYPES.get(menuId);
        if (tuple != null) {
            ItemStack itemStack = player.isCreative() ? stack : player.containerMenu.getCarried();
            player.containerMenu.setCarried(ItemStack.EMPTY);
            player.openMenu(new SimpleMenuProvider(tuple.getA(), tuple.getB()));
            CustomPacketPayload[] payloads = new CustomPacketPayload[0];
            if (!itemStack.isEmpty()) {
                player.containerMenu.setCarried(itemStack);
                payloads = new CustomPacketPayload[]{new SPacketGrabbedItem(itemStack)};
            }
            Confluence.NETWORK_HANDLER.sendToPlayer(AvailableHouseSelectPacketS2C.collectPacket(player), payloads);
        }
    }

    public static void sendToServer(byte menuId, ItemStack stack) {
        Confluence.NETWORK_HANDLER.sendToServer(new OpenMenuPacketC2S(menuId, stack));
    }
}
