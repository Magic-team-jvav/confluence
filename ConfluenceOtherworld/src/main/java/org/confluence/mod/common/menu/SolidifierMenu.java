package org.confluence.mod.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.confluence.lib.common.menu.EitherAmountContainerMenu4x;
import org.confluence.lib.common.menu.ToggleAmountResultSlot;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.SolidifierRecipe;

public class SolidifierMenu extends EitherAmountContainerMenu4x<MenuRecipeInput, SolidifierRecipe, ToggleAmountResultSlot<SolidifierRecipe>, ContainerLevelAccess> {
    public SolidifierMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public SolidifierMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.SOLIDIFIER.get(), ModRecipes.SOLIDIFIER_TYPE.get(), containerId, inventory, access, MenuRecipeInput::new, ToggleAmountResultSlot.For4x::new);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, FunctionalBlocks.SOLIDIFIER.get());
    }
}
