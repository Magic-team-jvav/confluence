package org.confluence.mod.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

/// 炼药瓶的界面：与炼药桌共用全部炼药配方，只是没有概率保留材料的效果。
public class BrewingVialMenu extends AlchemyTableMenu {
    public BrewingVialMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public BrewingVialMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.BREWING_VIAL.get(), containerId, inventory, access, FunctionalBlocks.BREWING_VIAL.get(), false);
    }
}
