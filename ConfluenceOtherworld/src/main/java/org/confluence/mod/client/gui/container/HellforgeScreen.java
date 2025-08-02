package org.confluence.mod.client.gui.container;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.common.menu.HellforgeMenu;

public class HellforgeScreen extends EnhanceForgeScreen<HellforgeMenu> {
    public HellforgeScreen(HellforgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
