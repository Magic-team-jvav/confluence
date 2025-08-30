package org.confluence.mod.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.menu.EitherAmountContainerMenu4x;
import org.confluence.lib.common.menu.ToggleAmountResultSlot;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.LoomRecipe;

public class LoomMenu extends EitherAmountContainerMenu4x<MenuRecipeInput, LoomRecipe, ToggleAmountResultSlot<LoomRecipe>, ContainerLevelAccess> {
    public LoomMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public LoomMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.LOOM.get(), ModRecipes.LOOM_TYPE.get(), containerId, inventory, access, MenuRecipeInput::new,
                (input, container, slot, x, y, setup) -> new ToggleAmountResultSlot<>(input, container, slot, x, y) {
                    @Override
                    protected void updateMenu() {
                        setup.run();
                    }

                    @Override
                    public void onTake(Player player, ItemStack stack) {
                        if (recipe != null) {
                            recipe.either
                                    .ifLeft(pattern -> AbstractAmountRecipe.consumeShaped(input, 4, 4, pattern))
                                    .ifRight(ingredients -> AbstractAmountRecipe.consumeShapeless(input, ingredients));
                            input.setChanged();
                            updateMenu();
                        }
                    }
                });
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, FunctionalBlocks.LOOM.get());
    }
}
