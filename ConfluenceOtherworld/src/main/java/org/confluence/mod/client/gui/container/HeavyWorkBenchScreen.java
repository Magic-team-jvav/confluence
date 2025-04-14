package org.confluence.mod.client.gui.container;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.lib.client.screen.ShapedAmountContainerScreen4x;
import org.confluence.mod.common.menu.HeavyWorkBenchMenu;

public class HeavyWorkBenchScreen extends ShapedAmountContainerScreen4x<HeavyWorkBenchMenu> {
    public HeavyWorkBenchScreen(HeavyWorkBenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
