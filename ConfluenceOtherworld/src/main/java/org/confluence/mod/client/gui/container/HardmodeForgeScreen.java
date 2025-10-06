package org.confluence.mod.client.gui.container;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.common.menu.HardmodeForgeMenu;

public class HardmodeForgeScreen extends EnhanceForgeScreen<HardmodeForgeMenu> {
    public HardmodeForgeScreen(HardmodeForgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
