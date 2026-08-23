package org.confluence.mod.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.PaintItems;
import org.confluence.mod.common.init.item.VanityArmorItems;

public class DyeMixMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    public final SimpleContainer container;
    private Runnable listener = () -> {};

    public DyeMixMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public DyeMixMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.DYE_MIX.get(), containerId);
        this.access = access;
        this.container = new SimpleContainer(3) {
            @Override
            public void setChanged() {
                super.setChanged();
                DyeMixMenu.this.slotsChanged(this);
            }
        };

        addSlot(new Slot(container, 0, 17, 14) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(VanityArmorItems.RED_DYE.get())
                        || stack.is(PaintItems.RED_PAINT.get());
            }
        });
        addSlot(new Slot(container, 1, 17, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(VanityArmorItems.GREEN_DYE.get())
                        || stack.is(PaintItems.GREEN_PAINT.get());
            }
        });
        addSlot(new Slot(container, 2, 17, 56) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(VanityArmorItems.BLUE_DYE.get())
                        || stack.is(PaintItems.BLUE_PAINT.get());
            }
        });

        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(inventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for (int m = 0; m < 9; m++) {
            addSlot(new Slot(inventory, m, 8 + m * 18, 142));
        }
    }

    public void registerUpdateListener(Runnable listener) {
        this.listener = listener;
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        listener.run();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index > 2) {
                if (!moveItemStackTo(itemstack1, 0, 3, false)) {
                    if (index < 30) {
                        if (!moveItemStackTo(itemstack1, 30, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 39 && !moveItemStackTo(itemstack1, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, FunctionalBlocks.DYE_VAT.get());
    }

    /// 严格验证服务端菜单是否仍绑定在玩家附近的染缸上。
    ///
    /// 原版 {@link ContainerLevelAccess#NULL} 为了兼容客户端菜单，会让通用
    /// {@code stillValid} 使用宽松默认值。网络事务不能采用这个默认值，否则一个
    /// 没有真实方块位置的菜单也可能通过校验。本方法只在访问对象能提供真实世界和
    /// 坐标、目标方块仍是染缸且玩家距离不超过八格时返回 {@code true}。
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

    /// 返回由服务端方块交互建立的工作站访问对象。
    ///
    /// 页面切换必须复用这个位置，不能在玩家已经打开界面后重新依赖视线追踪。
    /// 调用方仍须先通过 {@link #hasValidServerAccess(Player)} 验证其有效性。
    public ContainerLevelAccess workstationAccess() {
        return access;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        access.execute((level, blockPos) -> clearContainer(player, container));
    }
}
