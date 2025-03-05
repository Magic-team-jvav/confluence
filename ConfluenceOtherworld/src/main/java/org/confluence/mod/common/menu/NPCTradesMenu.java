package org.confluence.mod.common.menu;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.common.entity.npc.NPCTrades;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.mixed.IPlayer;
import org.confluence.mod.network.c2s.NPCShopPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NPCTradesMenu extends AbstractContainerMenu {
    private final SimpleContainer container;
    public NPCTrades NPCTrades;
    public int selectedMerchantIndex = -1;

    public NPCTradesMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null);
    }

    public NPCTradesMenu(int containerId, Inventory playerInventory, @Nullable NPCTrades NPCTrades) {
        super(ModMenuTypes.DAVE_TRADES_MENU.get(), containerId);
        this.NPCTrades = NPCTrades;
        if(NPCTrades == null) this.NPCTrades = ((IPlayer)playerInventory.player).rhyme$getDaveTrades();

        this.container = new SimpleContainer(1);
        this.addSlot(new Slot(this.container, 0, 238, 37){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
            @Override
            public void onTake(Player player, ItemStack stack){
                var d  = ((IPlayer)playerInventory.player).rhyme$getDaveTrades();
                if(selectedMerchantIndex >= 0 && selectedMerchantIndex < d.trades().size()){
                    PacketDistributor.sendToServer(new NPCShopPacket(d.trades().get(selectedMerchantIndex)));

                }
                super.onTake(player, stack);
            }
        });

        int k;
        for(k = 0; k < 3; ++k) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + k * 9 + 9, 108 + j * 18, 84 + k * 18));
            }
        }

        for(k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 108 + k * 18, 142));
        }
    }

    @Override
    public boolean clickMenuButton(@NotNull Player pPlayer, int pId) {
        selectedMerchantIndex = pId;
        return true;
    }

    public boolean stillValid(Player player) {
        return ((IPlayer)player).rhyme$getDaveTrades() == NPCTrades;
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
            } else  {
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



}
