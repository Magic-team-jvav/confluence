package org.confluence.mod.common.menu;

import com.google.common.collect.Lists;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.SkyMillRecipe;
import org.confluence.terra_curio.common.menu.AmountResultSlot;
import org.confluence.terra_curio.common.menu.RecipeInputContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SkyMillMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT_1 = 0;
    public static final int INPUT_SLOT_2 = 1;
    public static final int INPUT_SLOT_3 = 2;
    public static final int RESULT_SLOT = 3;
    private static final int INV_SLOT_START = 4;
    private static final int INV_SLOT_END = 31;
    private static final int USE_ROW_SLOT_START = 31;
    private static final int USE_ROW_SLOT_END = 40;
    private final ContainerLevelAccess access;
    private final Player player;
    private Runnable slotUpdateListener = () -> {};
    public final RecipeInputContainer input = new RecipeInputContainer(this, 3) {
        public void setChanged() {
            super.setChanged();
            SkyMillMenu.this.slotUpdateListener.run();
        }
    };
    private final ResultContainer result = new ResultContainer();
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private List<RecipeHolder<SkyMillRecipe>> recipes = Lists.newArrayList();

    public SkyMillMenu(int pContainerId, Inventory inventory) {
        this(pContainerId, inventory, ContainerLevelAccess.NULL);
    }

    public SkyMillMenu(int pContainerId, Inventory pPlayerInventory, final ContainerLevelAccess pAccess) {
        super(ModMenuTypes.SKY_MILL.get(), pContainerId);
        this.access = pAccess;
        this.player = pPlayerInventory.player;
        addSlot(new AmountResultSlot(input, result, 0, 35, 14) {
            @Override
            protected void updateMenu() {
                SkyMillMenu.this.setupResultSlot();
            }
        });
        addSlot(new Slot(input, 0, 35, 57));
        addSlot(new Slot(input, 1, 16, 38));
        addSlot(new Slot(input, 2, 54, 38));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            addSlot(new Slot(pPlayerInventory, k, 8 + k * 18, 142));
        }

        addDataSlot(selectedRecipeIndex);
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(access, pPlayer, FunctionalBlocks.SKY_MILL.get());
    }

    public int getSelectedRecipeIndex() {
        return selectedRecipeIndex.get();
    }

    public List<RecipeHolder<SkyMillRecipe>> getRecipes() {
        return recipes;
    }

    public int getNumRecipes() {
        return recipes.size();
    }

    public boolean hasInputItem() {
        return input.isEmpty() && !recipes.isEmpty();
    }

    public boolean clickMenuButton(@NotNull Player pPlayer, int pId) {
        if (isValidRecipeIndex(pId)) {
            selectedRecipeIndex.set(pId);
            setupResultSlot();
        }
        return true;
    }

    private boolean isValidRecipeIndex(int pRecipeIndex) {
        return pRecipeIndex >= 0 && pRecipeIndex < recipes.size();
    }

    public void slotsChanged(@NotNull Container pInventory) {
        this.recipes = player.level().getRecipeManager().getRecipesFor(ModRecipes.SKY_MILL_TYPE.get(), input, player.level());
        if (selectedRecipeIndex.get() >= recipes.size()) selectedRecipeIndex.set(recipes.size() - 1);
        access.execute((level, pos) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack itemStack = ItemStack.EMPTY;
                if (!recipes.isEmpty()) {
                    if (selectedRecipeIndex.get() == -1) selectedRecipeIndex.set(0);
                    SkyMillRecipe recipe = recipes.get(selectedRecipeIndex.get()).value();
                    itemStack = recipe.getResultItem(null).copy();
                    setCurrentRecipe(recipe);
                }
                result.setItem(0, itemStack);
                setRemoteSlot(0, itemStack);
                serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, itemStack));
            }
        });
    }

    private void setCurrentRecipe(SkyMillRecipe recipe) {
        if (getSlot(0) instanceof AmountResultSlot amountResultSlot) {
            amountResultSlot.setCurrentRecipe(recipe);
        }
    }

    private void setupResultSlot() {
        if (!recipes.isEmpty() && isValidRecipeIndex(selectedRecipeIndex.get())) {
            SkyMillRecipe recipe = recipes.get(selectedRecipeIndex.get()).value();
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

    public @NotNull MenuType<SkyMillMenu> getType() {
        return ModMenuTypes.SKY_MILL.get();
    }

    public void registerUpdateListener(Runnable pListener) {
        this.slotUpdateListener = pListener;
    }

    public boolean canTakeItemForPickAll(@NotNull ItemStack pStack, Slot pSlot) {
        return pSlot.container != result && super.canTakeItemForPickAll(pStack, pSlot);
    }

    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == 0) {
                item.onCraftedBy(itemstack1, pPlayer.level(), pPlayer);
                if (!moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (pIndex <= 3) {
                if (!moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (player.level().getRecipeManager().getRecipeFor(ModRecipes.SKY_MILL_TYPE.get(), new SingleRecipeInput(itemstack1), player.level()).isPresent()) {
                if (!moveItemStackTo(itemstack1, 1, 4, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex < INV_SLOT_END) {
                if (!moveItemStackTo(itemstack1, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex < USE_ROW_SLOT_END && !moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
            broadcastChanges();
        }

        return itemstack;
    }

    public void removed(@NotNull Player pPlayer) {
        super.removed(pPlayer);
        result.removeItemNoUpdate(1);
        access.execute((level, blockPos) -> clearContainer(pPlayer, input));
    }
}
