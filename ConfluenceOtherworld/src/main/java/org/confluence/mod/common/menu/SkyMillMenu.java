package org.confluence.mod.common.menu;

import com.google.common.collect.Lists;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.confluence.lib.common.menu.AmountResultSlot;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.lib.common.recipe.EnvironmentRecipeInput;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.SkyMillRecipe;

import java.util.List;

public class SkyMillMenu extends AbstractContainerMenu {
    private static final int INV_SLOT_START = 4;
    private static final int INV_SLOT_END = 31;
    private static final int USE_ROW_SLOT_START = 31;
    private static final int USE_ROW_SLOT_END = 40;
    private final EnvironmentLevelAccess access;
    private final Player player;
    private Runnable slotUpdateListener = () -> {};
    public final EnvironmentRecipeInput input;
    private final AmountResultSlot<SkyMillRecipe> resultSlot;
    private final ResultContainer result = new ResultContainer();
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private List<RecipeHolder<SkyMillRecipe>> recipes = Lists.newArrayList();

    public SkyMillMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, EnvironmentLevelAccess.empty());
    }

    public SkyMillMenu(int containerId, Inventory inventory, EnvironmentLevelAccess access) {
        super(ModMenuTypes.SKY_MILL.get(), containerId);
        this.access = access;
        this.player = inventory.player;
        this.access.initializeIfNeeded(player);
        this.input = new EnvironmentRecipeInput(this, 3, SkyMillMenu.this.access) {
            public void setChanged() {
                super.setChanged();
                SkyMillMenu.this.slotUpdateListener.run();
            }
        };
        this.resultSlot = new AmountResultSlot<>(input, result, 0, 35, 14) {
            @Override
            public void onTake(Player pPlayer, ItemStack pStack) {
                super.onTake(pPlayer, pStack);
                SkyMillMenu.this.access.execute((level, pos) -> {
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 5, 0, 0, 0, 0.01);
                    }
                });
            }

            @Override
            protected void updateMenu() {
                SkyMillMenu.this.setupResultSlot();
            }
        };
        addSlot(resultSlot);
        addSlot(new Slot(input, 0, 35, 57));
        addSlot(new Slot(input, 1, 16, 38));
        addSlot(new Slot(input, 2, 54, 38));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }

        addDataSlot(selectedRecipeIndex);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, FunctionalBlocks.SKY_MILL.get());
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
        return !input.isEmpty() && !recipes.isEmpty();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (isValidRecipeIndex(id)) {
            selectedRecipeIndex.set(id);
            setupResultSlot();
        }
        return true;
    }

    private boolean isValidRecipeIndex(int recipeIndex) {
        return recipeIndex >= 0 && recipeIndex < recipes.size();
    }

    @Override
    public void slotsChanged(Container container) {
        this.recipes = player.level().getRecipeManager().getRecipesFor(ModRecipes.SKY_MILL_TYPE.get(), input, player.level());
        if (selectedRecipeIndex.get() >= recipes.size()) selectedRecipeIndex.set(recipes.size() - 1);
        access.execute((level, pos) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack itemStack = ItemStack.EMPTY;
                if (!recipes.isEmpty()) {
                    if (selectedRecipeIndex.get() == -1) selectedRecipeIndex.set(0);
                    SkyMillRecipe recipe = recipes.get(selectedRecipeIndex.get()).value();
                    itemStack = recipe.getResultItem(null).copy();
                    resultSlot.setCurrentRecipe(recipe);
                }
                result.setItem(0, itemStack);
                setRemoteSlot(0, itemStack);
                serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, itemStack));
            }
        });
    }

    private void setupResultSlot() {
        if (!recipes.isEmpty() && isValidRecipeIndex(selectedRecipeIndex.get())) {
            SkyMillRecipe recipe = recipes.get(selectedRecipeIndex.get()).value();
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

    public void registerUpdateListener(Runnable listener) {
        this.slotUpdateListener = listener;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != result && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (index == 0) {
                item.onCraftedBy(itemstack1, player.level(), player);
                if (!moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index < 4) {
                if (!moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!moveItemStackTo(itemstack1, 1, 4, false)) {
                    return ItemStack.EMPTY;
                } else if (index < INV_SLOT_END) {
                    if (!moveItemStackTo(itemstack1, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < USE_ROW_SLOT_END && !moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
            broadcastChanges();
        }

        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        result.removeItemNoUpdate(0);
        access.execute((level, blockPos) -> clearContainer(player, input));
    }
}
