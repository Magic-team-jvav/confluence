package org.confluence.mod.client.gui.container;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.lib.client.screen.ShapedAmountContainerScreen4x;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.HeavyWorkBenchMenu;

public class HeavyWorkBenchScreen extends ShapedAmountContainerScreen4x<HeavyWorkBenchMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/heavy_work_bench.png");

    public HeavyWorkBenchScreen(HeavyWorkBenchMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected ResourceLocation background() {
        return BACKGROUND;
    }
}
