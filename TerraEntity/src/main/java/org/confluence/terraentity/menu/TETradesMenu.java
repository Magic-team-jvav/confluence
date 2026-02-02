package org.confluence.terraentity.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.mixed.IPlayer;
import org.confluence.terraentity.network.c2s.NPCShopPacket;
import org.confluence.terraentity.utils.AdapterUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * npc交易菜单，提供统一处理ITrade的逻辑
 */
public abstract class TETradesMenu extends AbstractContainerMenu {
    protected final SimpleContainer container;
    public ITradeHolder NPCTrades;
    public int selectedMerchantIndex = -1;


    public TETradesMenu(MenuType<?> menuType, int containerId, Inventory playerInventory) {
        this(menuType, containerId, playerInventory, null);
    }

    public TETradesMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, @Nullable ITradeHolder NPCTrades) {
        super(menuType, containerId);
        this.NPCTrades = NPCTrades;
        if (NPCTrades == null) {
            this.NPCTrades = ((IPlayer) playerInventory.player).terra_entity$getTradeHolder();
        }

        this.container = new SimpleContainer(1);
        this.addResultSlot();

        int k;
        for (k = 0; k < 3; ++k) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + k * 9 + 9, 108 + j * 18, 84 + k * 18));
            }
        }

        for (k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 108 + k * 18, 142));
        }
    }

    protected void addResultSlot() {
        this.addSlot(new Slot(this.container, 0, 238, 37) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
//                var d  = ((IPlayer)playerInventory.player).terra_entity$getDaveTrades();
//                if(selectedMerchantIndex >= 0 && selectedMerchantIndex < d.trades().size()){
//                    PacketDistributor.sendToServer(new NPCShopPacket((ITrade) d.trades().get(selectedMerchantIndex)));
//                }
                super.onTake(player, stack);
            }
        });
    }

    @Override
    public boolean clickMenuButton(@NotNull Player pPlayer, int pId) {
        selectedMerchantIndex = pId;
        return true;
    }

    public boolean stillValid(Player player) {
        return ((IPlayer) player).terra_entity$getTradeHolder() == NPCTrades;
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
                playTradeSound();
            } else {
                if (index >= 1 && index < 28) {
                    if (!this.moveItemStackTo(itemstack1, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 28 && index < 37 && !this.moveItemStackTo(itemstack1, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
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

    public void playTradeSound() {

    }

    public void removed(Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer) {
            player.getInventory().placeItemBackInInventory(slots.get(0).getItem().copy());
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (player.isLocalPlayer()) {
            if (slotId == 0) {
                this.handleTrade(player, selectedMerchantIndex, button, clickType);
            }
        }
        super.clicked(slotId, button, clickType, player);
    }

    protected void handleTrade(Player player, int selectedMerchantIndex, int button, ClickType clickType) {
        var d = ((IPlayer) player).terra_entity$getTradeHolder();
        if (d != null && selectedMerchantIndex >= 0 && selectedMerchantIndex < d.trades().size()) {
            ITrade trade = d.trades().get(selectedMerchantIndex);
            AdapterUtils.sendToServer(new NPCShopPacket(selectedMerchantIndex, this.NPCTrades.getTradeParams()));
            var npc = ((IPlayer) player).terra_entity$getTradeHolder();
            trade.onLocalClickSlot(player, button, clickType, npc, selectedMerchantIndex);
        }
    }

}
