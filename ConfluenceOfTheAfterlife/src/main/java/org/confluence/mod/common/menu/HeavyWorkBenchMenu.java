package org.confluence.mod.common.menu;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.confluence.mod.common.block.functional.crafting.HeavyWorkBenchBlock;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.EnvironmentRecipeInput;
import org.confluence.mod.common.recipe.HeavyWorkBenchRecipe;
import org.confluence.terra_curio.common.menu.AmountResultSlot;
import org.confluence.terra_curio.common.menu.RecipeInputContainer;
import org.confluence.terra_curio.common.recipe.AbstractAmountRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HeavyWorkBenchMenu extends AbstractContainerMenu {
    public static final int INPUT_START = 1;
    public static final int INPUT_END = 17;
    public static final int INV_SLOT_START = 17;
    public static final int INV_SLOT_END = 44;
    public static final int USE_ROW_SLOT_START = 44;
    public static final int USE_ROW_SLOT_END = 53;
    public final ToggleAmountResultSlot resultSlot;
    private final HeavyWorkBenchBlock.LevelAccess access;
    private final Player player;
    private final RecipeInputContainer input;
    private final ResultContainer result = new ResultContainer();
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private List<RecipeHolder<HeavyWorkBenchRecipe>> recipes = new ArrayList<>();

    public HeavyWorkBenchMenu(int pContainerId, Inventory inventory) {
        this(pContainerId, inventory, new HeavyWorkBenchBlock.LevelAccess(null, null));
    }

    public HeavyWorkBenchMenu(int pContainerId, Inventory pPlayerInventory, HeavyWorkBenchBlock.LevelAccess pAccess) {
        super(ModMenuTypes.HEAVY_WORK_BENCH.get(), pContainerId);
        this.player = pPlayerInventory.player;
        this.access = pAccess;
        this.input = new EnvironmentRecipeInput(this, 16, access);
        this.resultSlot = new ToggleAmountResultSlot(input, result, 0, 132, 36) {
            @Override
            protected void updateMenu() {
                HeavyWorkBenchMenu.this.setupResultSlot();
            }

            @Override
            public void onTake(@NotNull Player pPlayer, @NotNull ItemStack pStack) {
                if (recipe != null) {
                    AbstractAmountRecipe.extractInput(input, recipe.getIngredients(), true);
                    input.setChanged();
                    updateMenu();
                }
            }
        };
        addSlot(resultSlot);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                addSlot(new Slot(input, i * 4 + j, j * 18 + 21, i * 18 + 8));
            }
        }

        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(pPlayerInventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for (int m = 0; m < 9; m++) {
            addSlot(new Slot(pPlayerInventory, m, 8 + m * 18, 142));
        }

        addDataSlot(selectedRecipeIndex);
    }

    public int getCurrentIndex() {
        return selectedRecipeIndex.get();
    }

    public int getRecipesAmount() {
        return recipes.size();
    }

    public ItemStack getUpResult() {
        int index = getUpIndex();
        if (index == -1) return result.getItem(0);
        return recipes.get(index).value().getResultItem(null);
    }

    public int getUpIndex() {
        if (recipes.isEmpty()) return -1;
        if (recipes.size() == 1) {
            return 0;
        } else if (isValidRecipeIndex(selectedRecipeIndex.get())) {
            if (selectedRecipeIndex.get() == 0) {
                return recipes.size() - 1;
            } else {
                return selectedRecipeIndex.get() - 1;
            }
        }
        return -1;
    }

    public ItemStack getDownResult() {
        int index = getDownIndex();
        if (index == -1) return result.getItem(0);
        return recipes.get(index).value().getResultItem(null);
    }

    public int getDownIndex() {
        if (recipes.isEmpty()) return -1;
        if (recipes.size() == 1) {
            return 0;
        } else if (isValidRecipeIndex(selectedRecipeIndex.get())) {
            int next = selectedRecipeIndex.get() + 1;
            if (next == recipes.size()) {
                return 0;
            } else {
                return next;
            }
        }
        return -1;
    }

    private boolean isValidRecipeIndex(int pRecipeIndex) {
        return pRecipeIndex >= 0 && pRecipeIndex < recipes.size();
    }

    @Override
    public boolean clickMenuButton(@NotNull Player pPlayer, int pId) {
        if (isValidRecipeIndex(pId)) {
            selectedRecipeIndex.set(pId);
            setupResultSlot();
        }
        return true;
    }

    private void setupResultSlot() {
        if (!recipes.isEmpty() && isValidRecipeIndex(selectedRecipeIndex.get())) {
            HeavyWorkBenchRecipe recipe = recipes.get(selectedRecipeIndex.get()).value();
            ItemStack itemStack = recipe.getResultItem(null).copy();
            if (itemStack.isItemEnabled(player.level().enabledFeatures())) {
                result.setItem(0, itemStack);
                setCurrentRecipe(recipe);
            } else {
                result.setItem(0, ItemStack.EMPTY);
            }
        } else {
            result.setItem(0, ItemStack.EMPTY);
        }
        broadcastChanges();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(access, player, FunctionalBlocks.HEAVY_WORK_BENCH.get());
    }

    @Override
    public void removed(@NotNull Player pPlayer) {
        super.removed(pPlayer);
        access.execute((level, blockPos) -> clearContainer(pPlayer, input));
    }

    @Override
    public boolean canTakeItemForPickAll(@NotNull ItemStack pStack, Slot pSlot) {
        return pSlot.container != result && super.canTakeItemForPickAll(pStack, pSlot);
    }

    @Override
    public void slotsChanged(@NotNull Container pContainer) {
        this.recipes = player.level().getRecipeManager().getRecipesFor(ModRecipes.HEAVY_WORK_BENCH_TYPE.get(), input, player.level());
        if (selectedRecipeIndex.get() >= recipes.size()) selectedRecipeIndex.set(recipes.size() - 1);
        access.execute((level, pos) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack itemStack = ItemStack.EMPTY;
                if (!recipes.isEmpty()) {
                    if (selectedRecipeIndex.get() == -1) selectedRecipeIndex.set(0);
                    HeavyWorkBenchRecipe recipe = recipes.get(selectedRecipeIndex.get()).value();
                    itemStack = recipe.getResultItem(null).copy();
                    setCurrentRecipe(recipe);
                }
                result.setItem(0, itemStack);
                setRemoteSlot(0, itemStack);
                serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, itemStack));
            }
        });
    }

    private void setCurrentRecipe(HeavyWorkBenchRecipe recipe) {
        if (getSlot(0) instanceof AmountResultSlot amountResultSlot) {
            amountResultSlot.setCurrentRecipe(recipe);
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            itemStack = slotItem.copy();
            if (pIndex == 0) { // resultSlot
                access.execute((level, blockPos) -> slotItem.getItem().onCraftedBy(slotItem, level, pPlayer));
                if (!moveItemStackTo(slotItem, INV_SLOT_START, USE_ROW_SLOT_END, true)) { // playerInventory(ALL)
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(slotItem, itemStack);
            } else if (pIndex >= INV_SLOT_START && pIndex < USE_ROW_SLOT_END) { // playerInventory(ALL)
                if (!moveItemStackTo(slotItem, INPUT_START, INPUT_END, false)) { // craftSlots
                    if (pIndex < USE_ROW_SLOT_START) {
                        if (!moveItemStackTo(slotItem, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) { // playerInventory(HOT BAR)
                            return ItemStack.EMPTY;
                        }
                    } else if (!moveItemStackTo(slotItem, INV_SLOT_START, INV_SLOT_END, false)) { // playerInventory(MAIN)
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!moveItemStackTo(slotItem, INV_SLOT_START, USE_ROW_SLOT_END, false)) { // playerInventory(ALL)
                return ItemStack.EMPTY;
            }

            if (slotItem.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotItem.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, slotItem);
            if (pIndex == 0) { // resultSlot
                pPlayer.drop(slotItem, false);
            }
        }

        return itemStack;
    }
}
