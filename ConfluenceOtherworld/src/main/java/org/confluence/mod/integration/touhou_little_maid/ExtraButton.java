package org.confluence.mod.integration.touhou_little_maid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;


/**
 * 联动车万女仆
 */
@OnlyIn(Dist.CLIENT)
public class ExtraButton {
    public static void addButton(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        String interfaceName = "com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.backpack.IBackpackContainerScreen";
        try {
            if (screen instanceof AbstractContainerScreen<?> screen1 && Class.forName(interfaceName).isAssignableFrom(screen1.getClass())) {
                int l = screen1.getGuiLeft();
                int t = screen1.getGuiTop();
                event.addListener(new ImageButton(l + 5, t + 30, 16, 16, ModClientSetups.EXTRA_INVENTORY_BUTTON, button -> {
                    LocalPlayer player = Minecraft.getInstance().player;
                    if (player != null) {
                        ItemStack stack = player.containerMenu.getCarried();
                        player.containerMenu.setCarried(ItemStack.EMPTY);

                        OpenMenuPacketC2S.sendToServer(OpenMenuPacketC2S.MAID_TRADE_MENU, stack);
                    }
                }));
            }
        } catch (ClassNotFoundException ignored) {}
    }
}
