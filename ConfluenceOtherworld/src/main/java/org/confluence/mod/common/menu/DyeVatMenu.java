package org.confluence.mod.common.menu;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.menu.AmountResultSlot;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.DyeVatRecipe;

public class DyeVatMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final Player player;
    private final MenuRecipeInput input;
    private final ResultContainer result;
    private final AmountResultSlot<DyeVatRecipe> resultSlot;

    public DyeVatMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public DyeVatMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.DYE_VAT.get(), containerId);
        this.access = access;
        this.player = inventory.player;
        this.input = new MenuRecipeInput(this, 4);
        this.result = new ResultContainer();
        addSlot(this.resultSlot = new AmountResultSlot<>(input, result, 0, 125, 35));

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                addSlot(new Slot(input, j + i * 2, 35 + j * 18, 26 + i * 18));
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
                DyeVatRecipe recipe = level.getRecipeManager().getRecipeFor(ModRecipes.DYE_VAT_TYPE.get(), input, level).orElse(null);
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
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (index == 0) {
                item.onCraftedBy(itemstack1, player.level(), player);
                if (!moveItemStackTo(itemstack1, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index < 5) {
                if (!moveItemStackTo(itemstack1, 5, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(itemstack1, 1, 5, false)) {
                if (index < 32) {
                    if (!moveItemStackTo(itemstack1, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 41 && !moveItemStackTo(itemstack1, 5, 32, false)) {
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
        return stillValid(access, player, FunctionalBlocks.DYE_VAT.get());
    }

    /**
     * 严格验证当前服务端菜单仍然绑定在玩家附近的染缸上。
     *
     * <p>{@link ContainerLevelAccess#NULL} 在原版中会宽松地通过
     * {@code stillValid}，以便客户端只按菜单类型构造界面。服务端的页面切换不能
     * 接受这种空访问对象，否则伪造消息可以凭空打开染缸菜单。</p>
     */
    public boolean hasValidServerAccess(Player player) {
        return access.evaluate((level, blockPos) ->
                        level.hasChunkAt(blockPos)
                                && level.getBlockState(blockPos)
                                .is(FunctionalBlocks.DYE_VAT.get())
                                && player.distanceToSqr(
                                blockPos.getX() + 0.5,
                                blockPos.getY() + 0.5,
                                blockPos.getZ() + 0.5) <= 64.0,
                false);
    }

    /**
     * 返回由服务端方块交互建立的工作站访问对象。
     *
     * <p>该对象仅用于在染缸和混色页面之间切换；调用方仍须先通过
     * {@link #hasValidServerAccess(Player)} 验证其有效性。</p>
     */
    public ContainerLevelAccess workstationAccess() {
        return access;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        result.removeItemNoUpdate(0);
        access.execute((level, blockPos) -> clearContainer(player, input));
    }
}
