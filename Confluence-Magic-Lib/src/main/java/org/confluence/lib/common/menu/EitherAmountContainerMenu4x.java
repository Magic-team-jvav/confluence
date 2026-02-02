package org.confluence.lib.common.menu;

import com.mojang.datafixers.util.Function6;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.confluence.lib.common.recipe.EitherAmountRecipe4x;
import org.confluence.lib.common.recipe.MenuRecipeInput;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public abstract class EitherAmountContainerMenu4x<I extends MenuRecipeInput, R extends EitherAmountRecipe4x<I>, S extends ToggleAmountResultSlot<R>, A extends ContainerLevelAccess> extends AbstractContainerMenu {
    public static final int INPUT_START = 1;
    public static final int INPUT_END = 17;
    public static final int INV_SLOT_START = 17;
    public static final int INV_SLOT_END = 44;
    public static final int USE_ROW_SLOT_START = 44;
    public static final int USE_ROW_SLOT_END = 53;
    public final S resultSlot;
    protected final A access;
    protected final RecipeType<R> recipeType;
    protected final Player player;
    protected final I input;
    protected final ResultContainer result;
    protected final DataSlot selectedRecipeIndex = DataSlot.standalone();
    protected List<RecipeHolder<R>> recipes = new ArrayList<>();

    public <M extends EitherAmountContainerMenu4x<I, R, S, A>> EitherAmountContainerMenu4x(MenuType<M> menuType, RecipeType<R> recipeType, int containerId, Inventory inventory, A access, BiFunction<M, Integer, I> inputFactory, Function6<I, ResultContainer, Integer, Integer, Integer, Runnable, S> resultSlotFactory) {
        super(menuType, containerId);
        this.recipeType = recipeType;
        this.player = inventory.player;
        this.access = access;
        this.input = inputFactory.apply((M) this, 16);
        this.result = new ResultContainer();
        addSlot(this.resultSlot = resultSlotFactory.apply(input, result, 0, 132, 36, this::setupResultSlot));

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                addSlot(new Slot(input, i * 4 + j, j * 18 + 21, i * 18 + 8));
            }
        }

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

    public A getAccess() {
        return access;
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
        int totalSize = getRecipesAmount();
        if (totalSize == 0) return -1;
        if (totalSize == 1) {
            return 0;
        } else if (isValidRecipeIndex(selectedRecipeIndex.get())) {
            if (selectedRecipeIndex.get() == 0) {
                return totalSize - 1;
            } else {
                return selectedRecipeIndex.get() - 1;
            }
        }
        return -1;
    }

    public ItemStack getDownResult() {
        int index = getDownIndex();
        if (index == -1) return result.getItem(0);
        return recipes.get(index).value().getResultItem(player.registryAccess());
    }

    public int getDownIndex() {
        int totalSize = getRecipesAmount();
        if (totalSize == 0) return -1;
        if (totalSize == 1) {
            return 0;
        } else if (isValidRecipeIndex(selectedRecipeIndex.get())) {
            int next = selectedRecipeIndex.get() + 1;
            if (next == totalSize) {
                return 0;
            } else {
                return next;
            }
        }
        return -1;
    }

    protected boolean isValidRecipeIndex(int recipeIndex) {
        return recipeIndex >= 0 && recipeIndex < getRecipesAmount();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (isValidRecipeIndex(id)) {
            selectedRecipeIndex.set(id);
            setupResultSlot();
        }
        return true;
    }

    public void setupResultSlot() {
        if (isValidRecipeIndex(selectedRecipeIndex.get())) {
            R recipe = recipes.get(selectedRecipeIndex.get()).value();
            ItemStack itemStack = recipe.getResultItem(player.registryAccess()).copy();
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
    public void removed(Player player) {
        super.removed(player);
        access.execute((level, blockPos) -> clearContainer(player, input));
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != result && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public void slotsChanged(Container container) {
        input.asCraftingInput(true);
        this.recipes = player.level().getRecipeManager().getRecipesFor(recipeType, input, player.level());
        if (selectedRecipeIndex.get() >= recipes.size()) selectedRecipeIndex.set(recipes.size() - 1);
        access.execute((level, pos) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack itemStack = ItemStack.EMPTY;
                if (!recipes.isEmpty()) {
                    if (selectedRecipeIndex.get() == -1) selectedRecipeIndex.set(0);
                    R recipe = recipes.get(selectedRecipeIndex.get()).value();
                    itemStack = recipe.getResultItem(player.registryAccess()).copy();
                    resultSlot.setCurrentRecipe(recipe);
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
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
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

    public Container getContainer() {
        return input;
    }

    public void clearContainerNoUpdate(Player player) {
        if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer) player).hasDisconnected()) {
            for (int j = 0; j < input.getContainerSize(); ++j) {
                player.drop(input.removeItemNoUpdate(j), false);
            }
        } else {
            for (int i = 0; i < input.getContainerSize(); ++i) {
                Inventory inventory = player.getInventory();
                if (inventory.player instanceof ServerPlayer) {
                    inventory.placeItemBackInInventory(input.removeItemNoUpdate(i), false);
                }
            }
        }
    }
}
