package org.confluence.mod.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.RecipeType;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;

public class HardmodeForgeMenu extends EnhancedForgeMenu {
    public HardmodeForgeMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.HARDMODE_FORGE.get(), containerId, inventory);
    }

    public HardmodeForgeMenu(int containerId, Inventory inventory, Container forgeContainer, ContainerData forgeData) {
        super(ModMenuTypes.HARDMODE_FORGE.get(), containerId, inventory, forgeContainer, forgeData);
    }

    @Override
    protected RecipeType<?> getRecipeType() {
        return ModRecipes.HARDMODE_FORGE_TYPE.get();
    }
}
