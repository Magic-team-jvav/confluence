package org.confluence.mod.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.menu.EitherAmountContainerMenu4x;
import org.confluence.lib.common.menu.ToggleAmountResultSlot;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.EnvironmentRecipeInput;
import org.confluence.mod.common.block.functional.crafting.HeavyWorkBenchBlock;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.HeavyWorkBenchRecipe;

public class HeavyWorkBenchMenu extends EitherAmountContainerMenu4x<EnvironmentRecipeInput, HeavyWorkBenchRecipe, ToggleAmountResultSlot<HeavyWorkBenchRecipe>, HeavyWorkBenchBlock.LevelAccess> {
    public HeavyWorkBenchMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new HeavyWorkBenchBlock.LevelAccess(null, null));
    }

    public HeavyWorkBenchMenu(int containerId, Inventory inventory, HeavyWorkBenchBlock.LevelAccess access) {
        super(ModMenuTypes.HEAVY_WORK_BENCH.get(), ModRecipes.HEAVY_WORK_BENCH_TYPE.get(), containerId, inventory, access,
                (menu, size) -> {
                    access.initializeIfNeeded(inventory.player);
                    return new EnvironmentRecipeInput(menu, size, access);
                },
                (input, container, slot, x, y, setup) -> new ToggleAmountResultSlot<>(input, container, slot, x, y) {
                    @Override
                    protected void updateMenu() {
                        setup.run();
                    }

                    @Override
                    public void onTake(Player player, ItemStack stack) {
                        if (recipe != null) {
                            AbstractAmountRecipe.consumeShaped(input, 4, 4, recipe.either.orThrow());
                            input.setChanged();
                            updateMenu();
                        }
                    }
                });
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, FunctionalBlocks.HEAVY_WORK_BENCH.get());
    }
}
