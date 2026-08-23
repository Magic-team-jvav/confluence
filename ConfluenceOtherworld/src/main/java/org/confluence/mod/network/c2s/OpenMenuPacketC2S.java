package org.confluence.mod.network.c2s;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.GoblinTinkererNPC;
import org.confluence.mod.common.entity.npc.trade.NPCTradeMenu;
import org.confluence.mod.common.menu.DyeMixMenu;
import org.confluence.mod.common.menu.DyeVatMenu;
import org.confluence.mod.common.menu.ExtraInventoryMenu;
import org.confluence.mod.common.menu.NPCReforgeMenu;
import org.confluence.mod.network.s2c.AvailableHouseSelectPacketS2C;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import top.theillusivec4.curios.common.network.NetworkHandler;
import top.theillusivec4.curios.common.network.server.SPacketGrabbedItem;

/// 请求服务端切换 Confluence 自有菜单。
///
/// 光标物品始终以服务端当前容器为准，不随 C2S 消息上传。切换菜单时先暂存服务端
/// 光标物品，打开目标菜单后再恢复，既避免切换菜单吞掉物品，也阻止客户端借菜单切换
/// 注入任意物品或超大 NBT。
public record OpenMenuPacketC2S(byte menuId, ItemStack stack) implements IPortPacket.C2S {
    public static final byte EXTRA_INVENTORY = 0;
    public static final byte MAID_TRADE_MENU = 1;
    public static final byte NPC_REFORGE_MENU = 2;
    public static final byte DYE_VAT_MENU = 3;
    public static final byte DYE_MIX_MENU = 4;
    public static final ResourceLocation ID = Confluence.asResource("open_menu");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, OpenMenuPacketC2S> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.BYTE, OpenMenuPacketC2S::menuId,
            PortItemStackExtension.optionalStreamCodec(), OpenMenuPacketC2S::stack,
            OpenMenuPacketC2S::new
    );

    /// 根据玩家当前的服务端菜单解析一次合法的菜单切换。
    ///
    /// 染缸与混色界面的按钮只是同一工作站内的页面切换，因此必须继承当前有效
    /// 菜单已经绑定的 {@link ContainerLevelAccess}。重新依据玩家视线寻找方块既会
    /// 在打开界面后丢失准确位置，也允许伪造消息从任意位置创建工作站菜单。
    /// 重铸界面只能从哥布林工匠的有效交易会话进入，不能作为通用菜单直接打开。
    private static MenuRequest resolveMenu(ServerPlayer player, byte menuId) {
        if (menuId == EXTRA_INVENTORY) {
            return new MenuRequest((containerId, inventory, owner) -> new ExtraInventoryMenu(containerId, inventory), Component.empty());
        }
        if (menuId == NPC_REFORGE_MENU) {
            if (!(player.containerMenu instanceof NPCTradeMenu tradeMenu) || !(tradeMenu.getNPC() instanceof GoblinTinkererNPC) || !tradeMenu.stillValid(player)) {
                return null;
            }
            return new MenuRequest((containerId, inventory, owner) -> new NPCReforgeMenu(containerId, inventory, tradeMenu.getNPC()), Component.translatable("container.confluence.reforge"));
        }
        if (menuId != DYE_VAT_MENU && menuId != DYE_MIX_MENU) {
            return null;
        }

        ContainerLevelAccess access = currentDyeVatAccess(player);
        if (access == null) {
            return null;
        }
        if (menuId == DYE_VAT_MENU) {
            return new MenuRequest((containerId, inventory, owner) -> new DyeVatMenu(containerId, inventory, access), Component.translatable("container.confluence.dye_vat"));
        }
        return new MenuRequest((containerId, inventory, owner) -> new DyeMixMenu(containerId, inventory, access), Component.translatable("container.confluence.dye_mix"));
    }

    private static ContainerLevelAccess currentDyeVatAccess(ServerPlayer player) {
        if (player.containerMenu instanceof DyeVatMenu menu && menu.hasValidServerAccess(player)) {
            return menu.workstationAccess();
        }
        if (player.containerMenu instanceof DyeMixMenu menu && menu.hasValidServerAccess(player)) {
            return menu.workstationAccess();
        }
        return null;
    }

    /// 供服务端回归测试检查菜单消息的授权边界。
    static boolean canOpenMenu(ServerPlayer player, byte menuId) {
        return resolveMenu(player, menuId) != null;
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    /// 菜单和光标物品都属于服务端玩家状态，只能在主线程切换。
    @Override
    public void handle(IPortPacket.Context context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> work(player));
        }
    }

    @Override
    public void work(ServerPlayer player) {
        MenuRequest request = resolveMenu(player, menuId);
        if (request != null) {
            // 创造模式的光标栈只存在于客户端；其余模式仍以服务端菜单状态为准。
            ItemStack itemStack = player.isCreative()
                    ? stack.copy()
                    : player.containerMenu.getCarried().copy();
            player.containerMenu.setCarried(ItemStack.EMPTY);
            player.openMenu(new SimpleMenuProvider(request.constructor(), request.title()));
            Confluence.NETWORK_HANDLER.sendToPlayer(player, AvailableHouseSelectPacketS2C.collectPacket(player));
            if (!itemStack.isEmpty()) {
                player.containerMenu.setCarried(itemStack);
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SPacketGrabbedItem(itemStack));
            }
        }
    }

    public static void sendToServer(byte menuId) {
        sendToServer(menuId, ItemStack.EMPTY);
    }

    public static void sendToServer(byte menuId, ItemStack stack) {
        Confluence.NETWORK_HANDLER.sendToServer(new OpenMenuPacketC2S(menuId, stack.copy()));
    }

    private record MenuRequest(MenuConstructor constructor, Component title) {}
}
