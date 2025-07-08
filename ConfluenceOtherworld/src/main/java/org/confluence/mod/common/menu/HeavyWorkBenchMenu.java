package org.confluence.mod.common.menu;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.confluence.lib.common.menu.EitherAmountContainerMenu4x;
import org.confluence.lib.common.menu.ToggleAmountResultSlot;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.EnvironmentRecipeInput;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.confluence.mod.common.block.functional.crafting.HeavyWorkBenchBlock;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.HeavyWorkBenchRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HeavyWorkBenchMenu extends EitherAmountContainerMenu4x<EnvironmentRecipeInput, HeavyWorkBenchRecipe, HeavyWorkBenchMenu.ResultSlot, HeavyWorkBenchBlock.LevelAccess> {
    private List<RecipeHolder<CraftingRecipe>> craftingRecipes = new ArrayList<>();

    public HeavyWorkBenchMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new HeavyWorkBenchBlock.LevelAccess(null, null));
    }

    public HeavyWorkBenchMenu(int containerId, Inventory inventory, HeavyWorkBenchBlock.LevelAccess access) {
        super(ModMenuTypes.HEAVY_WORK_BENCH.get(), ModRecipes.HEAVY_WORK_BENCH_TYPE.get(), containerId, inventory, access,
                (menu, size) -> {
                    access.initializeIfNeeded(inventory.player);
                    return new EnvironmentRecipeInput(menu, size, access);
                },
                (input, container, slot, x, y, setup) -> new ResultSlot(input, container, slot, x, y) {
                    @Override
                    protected void updateMenu() {
                        setup.run();
                    }
                });
    }

    @Override
    public int getRecipesAmount() {
        return recipes.size() + craftingRecipes.size();
    }

    @Override
    public ItemStack getUpResult() {
        int index = getUpIndex();
        if (index == -1) return result.getItem(0);
        int recipesSize = recipes.size();
        if (index < recipesSize) {
            return recipes.get(index).value().getResultItem(player.registryAccess());
        }
        return craftingRecipes.get(index - recipesSize).value().getResultItem(player.registryAccess());
    }

    @Override
    public ItemStack getDownResult() {
        int index = getDownIndex();
        if (index == -1) return result.getItem(0);
        int recipesSize = recipes.size();
        if (index < recipesSize) {
            return recipes.get(index).value().getResultItem(player.registryAccess());
        }
        return craftingRecipes.get(index - recipesSize).value().getResultItem(player.registryAccess());
    }

    @Override
    public void slotsChanged(Container container) {
        this.craftingRecipes = player.level().getRecipeManager().getRecipesFor(RecipeType.CRAFTING, input.asCraftingInput(true), player.level()).stream().filter(holder -> {
            Class<?> clazz = holder.value().getClass();
            return clazz == ShapedRecipe.class || clazz == ShapelessRecipe.class;
        }).toList();
        this.recipes = player.level().getRecipeManager().getRecipesFor(recipeType, input, player.level());
        int totalSize = recipes.size() + craftingRecipes.size();
        if (selectedRecipeIndex.get() >= totalSize) selectedRecipeIndex.set(totalSize - 1);
        access.execute((level, pos) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack itemStack = ItemStack.EMPTY;
                if (!recipes.isEmpty() || !craftingRecipes.isEmpty()) {
                    if (selectedRecipeIndex.get() == -1) selectedRecipeIndex.set(0);
                    if (recipes.isEmpty()) {
                        CraftingRecipe recipe = craftingRecipes.get(selectedRecipeIndex.get()).value();
                        itemStack = recipe.getResultItem(player.registryAccess()).copy();
                        resultSlot.setAltRecipe(recipe);
                    } else {
                        HeavyWorkBenchRecipe recipe = recipes.get(selectedRecipeIndex.get()).value();
                        itemStack = recipe.getResultItem(player.registryAccess()).copy();
                        resultSlot.setCurrentRecipe(recipe);
                    }
                }
                result.setItem(0, itemStack);
                setRemoteSlot(0, itemStack);
                serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, itemStack));
            } else if (!recipes.isEmpty()) {
                if (selectedRecipeIndex.get() == -1) selectedRecipeIndex.set(0);
            }
        });
    }

    @Override
    public void setupResultSlot() {
        inner:
        {
            int index = selectedRecipeIndex.get();
            if (isValidRecipeIndex(index)) {
                int recipesSize = recipes.size();
                ItemStack itemStack;
                if (index < recipesSize) {
                    HeavyWorkBenchRecipe recipe = recipes.get(index).value();
                    itemStack = recipe.getResultItem(player.registryAccess());
                    if (!itemStack.isItemEnabled(player.level().enabledFeatures())) break inner;
                    resultSlot.setCurrentRecipe(recipe);
                } else {
                    CraftingRecipe recipe = craftingRecipes.get(index - recipesSize).value();
                    itemStack = recipe.getResultItem(player.registryAccess());
                    if (!itemStack.isItemEnabled(player.level().enabledFeatures())) break inner;
                    resultSlot.setAltRecipe(recipe);
                }
                result.setItem(0, itemStack.copy());
                broadcastChanges();
                return;
            }
        }
        result.setItem(0, ItemStack.EMPTY);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, FunctionalBlocks.HEAVY_WORK_BENCH.get());
    }

    public static class ResultSlot extends ToggleAmountResultSlot<HeavyWorkBenchRecipe> {
        private CraftingRecipe altRecipe;

        public ResultSlot(MenuRecipeInput input, Container result, int pSlot, int pX, int pY) {
            super(input, result, pSlot, pX, pY);
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
            if (altRecipe != null && (altRecipe.getClass() == ShapedRecipe.class || altRecipe.getClass() == ShapelessRecipe.class)) {
                for (ItemStack itemStack : input.getItems()) {
                    itemStack.shrink(1);
                }
                input.setChanged();
                updateMenu();
            }
        }

        @Override
        public void setCurrentRecipe(@Nullable HeavyWorkBenchRecipe recipe) {
            super.setCurrentRecipe(recipe);
            this.altRecipe = null;
        }

        public void setAltRecipe(CraftingRecipe recipe) {
            this.altRecipe = recipe;
            this.recipe = null;
        }
    }
}
