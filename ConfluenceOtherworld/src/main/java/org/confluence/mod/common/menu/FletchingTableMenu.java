package org.confluence.mod.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.recipe.FletchingTableRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FletchingTableMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final Player player;
    public final FletchingTableRecipe.Input input = new FletchingTableRecipe.Input(this);
    private final ResultContainer result = new ResultContainer();

    public FletchingTableMenu(int pContainerId, Inventory inventory) {
        this(pContainerId, inventory, ContainerLevelAccess.NULL);
    }

    public FletchingTableMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(ModMenuTypes.FLETCHING_TABLE.get(), pContainerId);
        this.player = pPlayerInventory.player;
        this.access = pAccess;

        addSlot(new ResultSlot(input, result, 0, 124, 35));
        addSlot(new Slot(input, 0, 30, 53));
        addSlot(new Slot(input, 1, 48, 35));
        addSlot(new Slot(input, 2, 66, 17));

        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(pPlayerInventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for (int m = 0; m < 9; m++) {
            addSlot(new Slot(pPlayerInventory, m, 8 + m * 18, 142));
        }
    }

    @Override
    public void slotsChanged(Container pInventory) {
        access.execute((level, pos) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                List<RecipeHolder<FletchingTableRecipe>> recipes = player.level().getRecipeManager().getRecipesFor(ModRecipes.FLETCHING_TABLE_TYPE.get(), input, player.level());
                ItemStack itemStack = ItemStack.EMPTY;
                if (!recipes.isEmpty()) {
                    FletchingTableRecipe recipe = recipes.getFirst().value();
                    itemStack = recipe.getResultItem(null).copy();
                    setCurrentRecipe(recipe);
                }
                result.setItem(0, itemStack);
                setRemoteSlot(0, itemStack);
                serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, itemStack));
            }
        });
    }

    private void setCurrentRecipe(FletchingTableRecipe recipe) {
        if (getSlot(0) instanceof ResultSlot amountResultSlot) {
            amountResultSlot.setCurrentRecipe(recipe);
        }
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
                if (!moveItemStackTo(itemstack1, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index < 4) {
                if (!moveItemStackTo(itemstack1, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemstack1.is(ItemTags.ARROWS)) {
                if (!moveItemStackTo(itemstack1, 2, 3, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(itemstack1, 1, 4, false)) {
                if (index < 31) {
                    if (!moveItemStackTo(itemstack1, 31, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 40 && !moveItemStackTo(itemstack1, 4, 31, false)) {
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
    public boolean stillValid(Player player) {
        return stillValid(access, player, Blocks.FLETCHING_TABLE);
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        result.removeItemNoUpdate(0);
        access.execute((level, blockPos) -> clearContainer(pPlayer, input));
    }

    public static class Provider implements MenuProvider {
        private final Level level;
        private final BlockPos pos;

        public Provider(Level level, BlockPos pos) {
            this.level = level;
            this.pos = pos;
        }

        @Override
        public Component getDisplayName() {
            return Component.translatable("container.confluence.fletching_table");
        }

        @Override
        public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
            return new FletchingTableMenu(containerId, inventory, ContainerLevelAccess.create(level, pos));
        }
    }

    private static class ResultSlot extends Slot {
        protected final FletchingTableRecipe.Input input;
        protected @Nullable FletchingTableRecipe recipe;

        public ResultSlot(FletchingTableRecipe.Input input, Container result, int pSlot, int pX, int pY) {
            super(result, pSlot, pX, pY);
            this.input = input;
        }

        public void setCurrentRecipe(@Nullable FletchingTableRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return false;
        }

        @Override
        public void onTake(Player pPlayer, ItemStack pStack) {
            if (recipe != null) {
                AbstractAmountRecipe.consumeShapeless(input, recipe.getIngredients());
                input.setChanged();
            }
        }
    }
}
