package org.confluence.mod.common.menu;

import PortLib.extensions.java.util.List.PortListExtension;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.AlchemyTableRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AlchemyTableMenu extends AbstractContainerMenu {
    public static final int[][] CORD = {
            {39, 17},
            {121, 17},
            {39, 37},
            {121, 37},
            {39, 57},
            {121, 57}
    };
    private final ContainerLevelAccess access;
    private final Player player;
    public final AlchemyTableRecipe.Input input = new AlchemyTableRecipe.Input(this);
    public final ResultContainer result = new ResultContainer();

    public AlchemyTableMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public AlchemyTableMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.ALCHEMY_TABLE.get(), containerId);
        this.player = inventory.player;
        this.access = access;

        addSlot(new ResultSlot(input, result, 0, 80, 62));
        addSlot(new Slot(input, 0, 80, 17)); // 基底药水
        for (int i = 1; i < 7; i++) {
            int[] cord = CORD[i - 1];
            addSlot(new Slot(input, i, cord[0], cord[1]));
        }

        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(inventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for (int m = 0; m < 9; m++) {
            addSlot(new Slot(inventory, m, 8 + m * 18, 142));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        access.execute((level, pos) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                List<AlchemyTableRecipe> recipes = player.level().getRecipeManager().getRecipesFor(ModRecipes.ALCHEMY_TABLE_TYPE.get(), input, player.level());
                ItemStack itemStack = ItemStack.EMPTY;
                if (!recipes.isEmpty()) {
                    AlchemyTableRecipe recipe = recipes.stream().max(Comparator.comparingInt(r -> r.getIngredients().size()))
                            .orElseGet(() -> PortListExtension.getFirst(recipes));
                    itemStack = recipe.getResultItem(player.registryAccess()).copy();
                    setCurrentRecipe(recipe);
                }
                result.setItem(0, itemStack);
                setRemoteSlot(0, itemStack);
                serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, itemStack));
            }
        });
    }

    private void setCurrentRecipe(AlchemyTableRecipe recipe) {
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
                if (!moveItemStackTo(itemstack1, 8, 44, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index < 8) {
                if (!moveItemStackTo(itemstack1, 8, 44, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(itemstack1, 1, 8, false)) {
                if (index < 35) {
                    if (!moveItemStackTo(itemstack1, 35, 44, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 44 && !moveItemStackTo(itemstack1, 8, 35, false)) {
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
        return stillValid(access, player, FunctionalBlocks.ALCHEMY_TABLE.get());
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        result.removeItemNoUpdate(0);
        access.execute((level, blockPos) -> {
            clearContainer(pPlayer, input);
            clearContainer(pPlayer, input.getMaterials());
        });
    }

    private static class ResultSlot extends Slot {
        private static final float CHANCE = 1.0F / 3.0F;
        protected final AlchemyTableRecipe.Input input;
        protected @Nullable AlchemyTableRecipe recipe;

        public ResultSlot(AlchemyTableRecipe.Input input, Container result, int pSlot, int pX, int pY) {
            super(result, pSlot, pX, pY);
            this.input = input;
        }

        public void setCurrentRecipe(@Nullable AlchemyTableRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return false;
        }

        @Override
        public void onTake(Player pPlayer, ItemStack pStack) {
            if (recipe != null) {
                RandomSource random = pPlayer.getRandom();
                SimpleContainer materials = input.getMaterials();
                int size = materials.getContainerSize();
                ArrayList<Tuple<Integer, ItemStack>> itemStacks = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    if (random.nextFloat() < CHANCE) { // 33.33%概率返还
                        itemStacks.add(new Tuple<>(i + 1, materials.getItem(i).copy()));
                    }
                }
                AbstractAmountRecipe.consumeShapeless(size, materials::getItem, recipe.getIngredients());
                for (Tuple<Integer, ItemStack> back : itemStacks) {
                    input.setItem(back.getA(), back.getB());
                }
                if (random.nextFloat() < 1.0F - CHANCE) { // 66.67%概率消耗
                    input.removeItem(0, AmountIngredient.getAmount(recipe.getBase()));
                }
                input.setChanged();
            }
        }
    }
}
