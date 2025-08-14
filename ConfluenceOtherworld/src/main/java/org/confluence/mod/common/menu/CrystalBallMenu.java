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
import net.minecraft.world.item.crafting.RecipeHolder;
import org.confluence.lib.common.menu.AmountResultSlot;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.lib.common.recipe.EnvironmentRecipeInput;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.CrystalBallRecipe;

public class CrystalBallMenu extends AbstractContainerMenu {
    private final EnvironmentLevelAccess access;
    private final Player player;
    private final EnvironmentRecipeInput input;
    private final ResultContainer result;
    private final AmountResultSlot<CrystalBallRecipe> resultSlot;

    public CrystalBallMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new EnvironmentLevelAccess(null, null));
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
                CrystalBallRecipe recipe = level.getRecipeManager().getRecipeFor(ModRecipes.CRYSTAL_BALL_TYPE.get(), input, level).map(RecipeHolder::value).orElse(null);
                if (recipe != null) {
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
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, FunctionalBlocks.CRYSTAL_BALL.get());
    }
}
