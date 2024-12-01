package org.confluence.mod.common.menu;

import com.google.common.collect.Lists;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModMenus;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.RecipeInputContainer;
import org.confluence.mod.common.recipe.SkyMillRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SkyMillMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT_1 = 0;
    public static final int INPUT_SLOT_2 = 1;
    public static final int INPUT_SLOT_3 = 2;
    public static final int RESULT_SLOT = 3;
    private static final int INV_SLOT_START = 2;
    private static final int INV_SLOT_END = 29;
    private static final int USE_ROW_SLOT_START = 29;
    private static final int USE_ROW_SLOT_END = 38;
    private final ContainerLevelAccess access;
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private final Level level;
    private List<RecipeHolder<SkyMillRecipe>> recipes = Lists.newArrayList();
    private ItemStack input = ItemStack.EMPTY;
    long lastSoundTime;
    final Slot inputSlot;
    final Slot resultSlot;
    Runnable slotUpdateListener = () -> {};
    public final RecipeInputContainer container = new RecipeInputContainer(3) {
        public void setChanged() {
            super.setChanged();
            SkyMillMenu.this.slotsChanged(this);
            SkyMillMenu.this.slotUpdateListener.run();
        }
    };
    private final ResultContainer resultContainer = new ResultContainer();

    public SkyMillMenu(int pContainerId, Inventory inventory) {
        this(pContainerId, inventory, ContainerLevelAccess.NULL);
    }

    public SkyMillMenu(int pContainerId, Inventory pPlayerInventory, final ContainerLevelAccess pAccess) {
        super(ModMenus.SKY_MILL.get(), pContainerId);
        this.access = pAccess;
        this.level = pPlayerInventory.player.level();
        this.inputSlot = addSlot(new Slot(container, INPUT_SLOT_1, 20, 33));
        this.resultSlot = addSlot(new Slot(resultContainer, RESULT_SLOT, 143, 33) {
            public boolean mayPlace(@NotNull ItemStack pStack) {
                return false;
            }

            public void onTake(@NotNull Player pPlayer, @NotNull ItemStack pStack) {
                pStack.onCraftedBy(pPlayer.level(), pPlayer, pStack.getCount());
                SkyMillMenu.this.resultContainer.awardUsedRecipes(pPlayer, getRelevantItems());
                ItemStack itemstack = SkyMillMenu.this.inputSlot.remove(1);
                if (!itemstack.isEmpty()) {
                    SkyMillMenu.this.setupResultSlot();
                }

                pAccess.execute((level, blockPos) -> {
                    long l = level.getGameTime();
                    if (SkyMillMenu.this.lastSoundTime != l) {
                        level.playSound(null, blockPos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        SkyMillMenu.this.lastSoundTime = l;
                    }
                });
                super.onTake(pPlayer, pStack);
            }

            private List<ItemStack> getRelevantItems() {
                return List.of(SkyMillMenu.this.inputSlot.getItem());
            }
        });

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
        return inputSlot.hasItem() && !recipes.isEmpty();
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
        ItemStack itemstack = inputSlot.getItem();
        if (!itemstack.is(input.getItem())) {
            input = itemstack.copy();
            setupRecipeList(new RecipeInput() {
                @Override
                public @NotNull ItemStack getItem(int index) {
                    return pInventory.getItem(index);
                }

                @Override
                public int size() {
                    return 3;
                }
            }, itemstack);
        }
    }

    private void setupRecipeList(RecipeInput pContainer, ItemStack pStack) {
        recipes.clear();
        selectedRecipeIndex.set(-1);
        resultSlot.set(ItemStack.EMPTY);
        if (!pStack.isEmpty()) {
            this.recipes = level.getRecipeManager().getRecipesFor(ModRecipes.SKY_MILL_TYPE.get(), pContainer, level);
        }

    }

    private void setupResultSlot() {
        if (!recipes.isEmpty() && isValidRecipeIndex(selectedRecipeIndex.get())) {
            RecipeHolder<SkyMillRecipe> skyMillRecipe = recipes.get(selectedRecipeIndex.get());
            ItemStack itemstack = skyMillRecipe.value().assemble(container, level.registryAccess());
            if (itemstack.isItemEnabled(level.enabledFeatures())) {
                resultContainer.setRecipeUsed(skyMillRecipe);
                resultSlot.set(itemstack);
            } else {
                resultSlot.set(ItemStack.EMPTY);
            }
        } else {
            resultSlot.set(ItemStack.EMPTY);
        }

        broadcastChanges();
    }

    public @NotNull MenuType<SkyMillMenu> getType() {
        return ModMenus.SKY_MILL.get();
    }

    public void registerUpdateListener(Runnable pListener) {
        this.slotUpdateListener = pListener;
    }

    public boolean canTakeItemForPickAll(@NotNull ItemStack pStack, Slot pSlot) {
        return pSlot.container != resultContainer && super.canTakeItemForPickAll(pStack, pSlot);
    }

    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == 1) {
                item.onCraftedBy(itemstack1, pPlayer.level(), pPlayer);
                if (!moveItemStackTo(itemstack1, INV_SLOT_START, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (pIndex == 0) {
                if (!moveItemStackTo(itemstack1, INV_SLOT_START, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (level.getRecipeManager().getRecipeFor(ModRecipes.SKY_MILL_TYPE.get(), new SingleRecipeInput(itemstack1), level).isPresent()) {
                if (!moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= INV_SLOT_START && pIndex < INV_SLOT_END) {
                if (!moveItemStackTo(itemstack1, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= USE_ROW_SLOT_START && pIndex < USE_ROW_SLOT_END && !moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, false)) {
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
        resultContainer.removeItemNoUpdate(1);
        access.execute((level, blockPos) -> clearContainer(pPlayer, container));
    }
}
