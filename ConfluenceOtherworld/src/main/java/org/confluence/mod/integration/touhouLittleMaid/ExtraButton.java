package org.confluence.mod.integration.touhouLittleMaid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.mod.common.entity.npc.NPCTrades;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.mixed.IPlayer;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;


/**
 * 联动车万女仆
 */
@OnlyIn(Dist.CLIENT)
public class ExtraButton {

    public static void addButton(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (screen.getClass().getName().equals("com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.backpack.EmptyBackpackContainerScreen")) {
            int l = ((AbstractContainerScreen<?>) screen).getGuiLeft();
            int t = ((AbstractContainerScreen<?>) screen).getGuiTop();
            event.addListener(new ImageButton(l + 5, t + 30, 16, 16, ModClientSetups.EXTRA_INVENTORY_BUTTON, button -> {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    ItemStack stack = player.containerMenu.getCarried();
                    player.containerMenu.setCarried(ItemStack.EMPTY);
                    ((IPlayer) player).rhyme$setDaveTrades(NPCTrades.getTrade(BuiltInRegistries.ENTITY_TYPE.getKey(ModEntities.GUIDE.get())));
                    OpenMenuPacketC2S.sendToServer(OpenMenuPacketC2S.NPC_TRADE_MENU, stack);
                }
            }));
        }
    }
}
