package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.PaintItems;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.confluence.mod.common.item.common.BaseDyeItem;
import org.confluence.mod.common.item.paint.PaintItem;
import org.confluence.mod.common.menu.DyeMixMenu;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.common.extensions.IPortItemStackExtension;

/// 请求服务端混合当前染料槽中的三原色。
///
/// 客户端只上传用户输入的 RGB 值；输出物品种类、组件和数量全部由服务端根据当前
/// {@link DyeMixMenu} 的输入槽重新计算。菜单还必须仍然绑定附近的染缸，关闭界面、离开
/// 方块或方块被破坏后收到的延迟消息都不能继续消耗材料。
public record DyeMixPacketC2S(int rgb) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("dye_mix");
    public static final PortStreamCodec<ByteBuf, DyeMixPacketC2S> STREAM_CODEC = PortByteBufCodecs.INT.map(DyeMixPacketC2S::new, DyeMixPacketC2S::rgb);

    @Override
    public void work(ServerPlayer player) {
        AbstractContainerMenu menu = player.containerMenu;
        if (!(menu instanceof DyeMixMenu dyeMixMenu) || !dyeMixMenu.hasValidServerAccess(player)) {
            return;
        }
        // 颜色组件统一使用不透明 ARGB，拒绝界面能力之外的透明度。
        if ((rgb & 0xFF000000) != 0xFF000000) return;

        Slot red = dyeMixMenu.getSlot(0);
        Slot green = dyeMixMenu.getSlot(1);
        Slot blue = dyeMixMenu.getSlot(2);
        if (!red.hasItem() || !green.hasItem() || !blue.hasItem()) return;

        ItemStack output;
        if (red.getItem().is(VanityArmorItems.RED_DYE.get()) && green.getItem().is(VanityArmorItems.GREEN_DYE.get()) && blue.getItem().is(VanityArmorItems.BLUE_DYE.get())) {
            output = VanityArmorItems.DYE.toStack();
            BaseDyeItem.setRGB(output, rgb);
        } else if (red.getItem().is(PaintItems.RED_PAINT.get())
                && green.getItem().is(PaintItems.GREEN_PAINT.get())
                && blue.getItem().is(PaintItems.BLUE_PAINT.get())) {
            output = PaintItems.PAINT.toStack();
            PaintItem.setRGB(output, rgb);
        } else {
            return;
        }

        ItemStack carried = dyeMixMenu.getCarried();
        if (!carried.isEmpty() && (!IPortItemStackExtension.isSameItemSameComponents(carried, output) || carried.getCount() >= carried.getMaxStackSize())) {
            return;
        }

        red.remove(1);
        green.remove(1);
        blue.remove(1);
        if (carried.isEmpty()) {
            dyeMixMenu.setCarried(output);
        } else {
            carried.grow(1);
        }
        dyeMixMenu.broadcastChanges();
    }

    @Override
    public ResourceLocation identifier() {
        return Confluence.asResource("dye_mix");
    }

    public static void sendToServer(int rgb) {
        Confluence.NETWORK_HANDLER.sendToServer(new DyeMixPacketC2S(rgb));
    }
}
