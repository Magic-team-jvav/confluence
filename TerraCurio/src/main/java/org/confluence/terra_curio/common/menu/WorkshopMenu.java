package org.confluence.terra_curio.common.menu;

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
import org.confluence.lib.common.menu.AmountResultSlot;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.lib.common.recipe.EnvironmentRecipeInput;
import org.confluence.terra_curio.common.init.TCBlocks;
import org.confluence.terra_curio.common.init.TCMenus;
import org.confluence.terra_curio.common.init.TCRecipes;
import org.confluence.terra_curio.common.recipe.WorkshopRecipe;

import java.util.ArrayList;
import java.util.List;

@javax.annotation.ParametersAreNonnullByDefault
@net.minecraft.MethodsReturnNonnullByDefault
public class WorkshopMenu extends AbstractContainerMenu {
    private final EnvironmentLevelAccess access;
    private final Player player;
    private final EnvironmentRecipeInput input;
    private final ResultContainer result;
    private final AmountResultSlot<WorkshopRecipe> resultSlot;
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private List<RecipeHolder<WorkshopRecipe>> recipes = new ArrayList<>();

    public WorkshopMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, EnvironmentLevelAccess.empty());
    }

    /*
     * 00 01 02 03
     * 11       04
     * 10       05
     * 09 08 07 06
     */
    public WorkshopMenu(int containerId, Inventory inventory, EnvironmentLevelAccess access) {
        super(TCMenus.WORKSHOP.get(), containerId);
        this.player = inventory.player;
        access.initializeIfNeeded(player);
        this.access = access;
        this.input = new EnvironmentRecipeInput(this, 12, access);
        this.result = new ResultContainer();
        addSlot(this.resultSlot = new AmountResultSlot<>(input, result, 0, 62, 35) {
            @Override
            protected void updateMenu() {
                WorkshopMenu.this.setupResultSlot();
            }
        });

        addSlot(new Slot(input, 0, 35, 8));
        addSlot(new Slot(input, 1, 53, 8));
        addSlot(new Slot(input, 2, 71, 8));
        addSlot(new Slot(input, 3, 89, 8));
        addSlot(new Slot(input, 4, 89, 26));
        addSlot(new Slot(input, 5, 89, 44));
        addSlot(new Slot(input, 6, 89, 62));
        addSlot(new Slot(input, 7, 71, 62));
        addSlot(new Slot(input, 8, 53, 62));
        addSlot(new Slot(input, 9, 35, 62));
        addSlot(new Slot(input, 10, 35, 44));
        addSlot(new Slot(input, 11, 35, 26));

        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(inventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for (int m = 0; m < 9; m++) {
            addSlot(new Slot(inventory, m, 8 + m * 18, 142));
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
    public boolean clickMenuButton(Player pPlayer, int pId) {
        if (isValidRecipeIndex(pId)) {
            selectedRecipeIndex.set(pId);
            setupResultSlot();
        }
        return true;
    }

    private void setupResultSlot() {
        if (!recipes.isEmpty() && isValidRecipeIndex(selectedRecipeIndex.get())) {
            WorkshopRecipe recipe = recipes.get(selectedRecipeIndex.get()).value();
            ItemStack itemStack = recipe.getResultItem(null).copy();
            if (itemStack.isItemEnabled(player.level().enabledFeatures())) {
                result.setItem(0, itemStack);
                resultSlot.setCurrentRecipe(recipe);
            } else {
                result.setItem(0, ItemStack.EMPTY);
            }
        } else {
            result.setItem(0, ItemStack.EMPTY);
        }
        broadcastChanges();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, TCBlocks.WORKSHOP.get());
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        access.execute((level, blockPos) -> clearContainer(pPlayer, input));
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return pSlot.container != result && super.canTakeItemForPickAll(pStack, pSlot);
    }

    @Override
    public void slotsChanged(Container pContainer) {
        this.recipes = player.level().getRecipeManager().getRecipesFor(TCRecipes.WORKSHOP_TYPE.get(), input, player.level());
        if (selectedRecipeIndex.get() >= recipes.size()) selectedRecipeIndex.set(recipes.size() - 1);
        access.execute((level, pos) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack itemStack = ItemStack.EMPTY;
                if (!recipes.isEmpty()) {
                    if (selectedRecipeIndex.get() == -1) selectedRecipeIndex.set(0);
                    WorkshopRecipe recipe = recipes.get(selectedRecipeIndex.get()).value();
                    itemStack = recipe.getResultItem(null).copy();
                    resultSlot.setCurrentRecipe(recipe);
                }
                result.setItem(0, itemStack);
                setRemoteSlot(0, itemStack);
                serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, itemStack));
            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            itemStack = slotItem.copy();
            if (pIndex == 0) { // resultSlot
                access.execute((level, blockPos) -> slotItem.getItem().onCraftedBy(slotItem, level, pPlayer));
                if (!moveItemStackTo(slotItem, 13, 49, true)) { // playerInventory(ALL)
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(slotItem, itemStack);
            } else if (pIndex >= 13 && pIndex < 49) { // playerInventory(ALL)
                if (!moveItemStackTo(slotItem, 1, 13, false)) { // craftSlots
                    if (pIndex < 40) {
                        if (!moveItemStackTo(slotItem, 40, 49, false)) { // playerInventory(HOT BAR)
                            return ItemStack.EMPTY;
                        }
                    } else if (!moveItemStackTo(slotItem, 13, 40, false)) { // playerInventory(MAIN)
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!moveItemStackTo(slotItem, 13, 49, false)) { // playerInventory(ALL)
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
