package org.confluence.mod.common.menu;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.menu.AmountResultSlot;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.lib.common.recipe.EnvironmentRecipeInput;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.CrystalBallRecipe;

public class CrystalBallMenu extends AbstractContainerMenu {
    private static final int RESULT_SLOT = 0;
    private static final int INPUT_SLOT_START = 1;
    private static final int INPUT_SLOT_END = 5;
    private static final int INVENTORY_SLOT_START = 5;
    private static final int INVENTORY_SLOT_END = 32;
    private static final int HOTBAR_SLOT_START = 32;
    private static final int HOTBAR_SLOT_END = 41;

    private final EnvironmentLevelAccess access;
    private final Player player;
    private final EnvironmentRecipeInput input;
    private final ResultContainer result;
    private final AmountResultSlot<CrystalBallRecipe> resultSlot;

    public CrystalBallMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, EnvironmentLevelAccess.empty());
    }

    public CrystalBallMenu(int containerId, Inventory inventory, EnvironmentLevelAccess access) {
        super(ModMenuTypes.CRYSTAL_BALL.get(), containerId);
        this.access = access;
        this.player = inventory.player;
        access.initializeIfNeeded(player);
        this.input = new EnvironmentRecipeInput(this, 4, access);
        this.result = new ResultContainer();
        addSlot(this.resultSlot = new AmountResultSlot<>(input, result, 0, 103, 35));

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                addSlot(new Slot(input, j + i * 2, 26 + j * 18, 26 + i * 18));
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
    }

    @Override
    public void slotsChanged(Container container) {
        access.execute((level, pos) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack itemStack = ItemStack.EMPTY;
                CrystalBallRecipe recipe = level.getRecipeManager().getRecipeFor(ModRecipes.CRYSTAL_BALL_TYPE.get(), input, level).orElse(null);
                if (recipe != null) {
                    itemStack = recipe.getResultItem(player.registryAccess()).copy();
                    resultSlot.setCurrentRecipe(recipe);
                }
                result.setItem(0, itemStack);
                setRemoteSlot(0, itemStack);
                serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, itemStack));
            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack originalStack = sourceStack.copy();
        if (index == RESULT_SLOT) {
            sourceStack.getItem().onCraftedBy(sourceStack, player.level(), player);
            if (!moveItemStackTo(sourceStack, INVENTORY_SLOT_START, HOTBAR_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
            sourceSlot.onQuickCraft(sourceStack, originalStack);
        } else if (index >= INPUT_SLOT_START && index < INPUT_SLOT_END) {
            if (!moveItemStackTo(sourceStack, INVENTORY_SLOT_START, HOTBAR_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(sourceStack, INPUT_SLOT_START, INPUT_SLOT_END, false)) {
            if (index >= INVENTORY_SLOT_START && index < INVENTORY_SLOT_END) {
                if (!moveItemStackTo(sourceStack, HOTBAR_SLOT_START, HOTBAR_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= HOTBAR_SLOT_START && index < HOTBAR_SLOT_END && !moveItemStackTo(sourceStack, INVENTORY_SLOT_START, INVENTORY_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.setByPlayer(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        if (sourceStack.getCount() == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }

        sourceSlot.onTake(player, sourceStack);
        broadcastChanges();
        return originalStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, FunctionalBlocks.CRYSTAL_BALL.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        result.removeItemNoUpdate(0);
        access.execute((level, blockPos) -> clearContainer(player, input));
    }
}
