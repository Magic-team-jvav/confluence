package org.confluence.mod.integration.touhou_little_maid;

import com.github.tartaricacid.touhoulittlemaid.api.event.client.MaidContainerGuiEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;


/**
 * 联动车万女仆
 */
@OnlyIn(Dist.CLIENT)
public class ExtraButton {
    @SubscribeEvent
    public void addButton(MaidContainerGuiEvent.Init event) {
        int l = event.getLeftPos();
        int t = event.getTopPos();
        event.addButton("confluence:maid_trade_menu", new ImageButton(l + 5, t + 30, 16, 16,
                ModClientSetups.EXTRA_INVENTORY_BUTTON, button -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                ItemStack stack = player.containerMenu.getCarried();
                player.containerMenu.setCarried(ItemStack.EMPTY);
                OpenMenuPacketC2S.sendToServer(OpenMenuPacketC2S.MAID_TRADE_MENU, stack);
            }
        }));
    }
}
