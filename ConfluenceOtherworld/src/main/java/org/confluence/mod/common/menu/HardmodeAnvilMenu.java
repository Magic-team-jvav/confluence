package org.confluence.mod.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.menu.EitherAmountContainerMenu4x;
import org.confluence.lib.common.menu.ToggleAmountResultSlot;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.HardmodeAnvilRecipe;

// todo 支持工作台
public class HardmodeAnvilMenu extends EitherAmountContainerMenu4x<MenuRecipeInput, HardmodeAnvilRecipe, ToggleAmountResultSlot<HardmodeAnvilRecipe>, ContainerLevelAccess> {
    public HardmodeAnvilMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public HardmodeAnvilMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.HARDMODE_ANVIL.get(), ModRecipes.HARDMODE_ANVIL_TYPE.get(), containerId, inventory, access, MenuRecipeInput::new,
                (input, container, slot, x, y, setup) -> new ToggleAmountResultSlot<>(input, container, slot, x, y) {
                    @Override
                    protected void updateMenu() {
                        setup.run();
                    }

                    @Override
                    public void onTake(Player player, ItemStack stack) {
                        if (recipe != null) {
                            recipe.either
                                    .ifLeft(shaped -> AbstractAmountRecipe.consumeShaped(input, 4, 4, shaped))
                                    .ifRight(shapeless -> AbstractAmountRecipe.consumeShapeless(input, shapeless));
                            input.setChanged();
                            updateMenu();
                        }
                    }
                });
    }

    @Override
    public boolean stillValid(Player player) {
        return access.evaluate((level, pos) -> {
            BlockState blockState = level.getBlockState(pos);
            return (blockState.is(FunctionalBlocks.MYTHRIL_ANVIL) || blockState.is(FunctionalBlocks.ORICHALCUM_ANVIL)) &&
                    player.canInteractWithBlock(pos, 4.0);
        }, true);
    }
}
