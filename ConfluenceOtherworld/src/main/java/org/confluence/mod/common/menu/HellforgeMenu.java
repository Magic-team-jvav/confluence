package org.confluence.mod.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.RecipeType;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;

public class HellforgeMenu extends EnhancedForgeMenu {
    public HellforgeMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.HELLFORGE.get(), containerId, inventory);
    }

    public HellforgeMenu(int containerId, Inventory inventory, Container forgeContainer, ContainerData forgeData) {
        super(ModMenuTypes.HELLFORGE.get(), containerId, inventory, forgeContainer, forgeData);
    }

    @Override
    protected RecipeType<?> getRecipeType() {
        return ModRecipes.HELLFORGE_TYPE.get();
    }
}
