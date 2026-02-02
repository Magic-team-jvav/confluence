package org.confluence.terra_furniture.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.menu.EitherAmountContainerMenu4x;
import org.confluence.lib.common.menu.ToggleAmountResultSlot;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.common.init.TFRegistries;
import org.confluence.terra_furniture.common.recipe.IceMachineRecipe;

public class IceMachineMenu extends EitherAmountContainerMenu4x<MenuRecipeInput, IceMachineRecipe, ToggleAmountResultSlot<IceMachineRecipe>, ContainerLevelAccess> {
    public IceMachineMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public IceMachineMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(TFRegistries.ICE_MACHINE_MENU.get(), TFRegistries.ICE_MACHINE_RECIPE_TYPE.get(), containerId, inventory, access, MenuRecipeInput::new,
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
        return stillValid(access, player, TFBlocks.ICE_MACHINE.get());
    }
}
