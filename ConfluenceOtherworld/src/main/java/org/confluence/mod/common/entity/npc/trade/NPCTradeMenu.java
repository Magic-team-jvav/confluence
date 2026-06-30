package org.confluence.mod.common.entity.npc.trade;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.util.Coins;
import org.confluence.mod.util.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

public class NPCTradeMenu extends AbstractContainerMenu {
    private static final int TRADE_COLS = 9;
    private static final int TRADE_ROWS = 4;
    private static final int TRADE_SIZE = TRADE_COLS * TRADE_ROWS;
    private static final int INVENTORY_SIZE = 27;
    private static final int HOTBAR_SIZE = 9;

    private final BaseNPC npc;
    private final ServerPlayer player;
    private final Container tradeContainer = new SimpleContainer(TRADE_SIZE);
    private final List<SlotState> slotStates = new ArrayList<>(TRADE_SIZE);
    private int scrollOffset;

    public static NPCTradeMenu fromNetwork(int containerId, Inventory playerInv, FriendlyByteBuf data) {
        int entityId = data.readInt();
        BaseNPC npc = (BaseNPC) playerInv.player.level().getEntity(entityId);
        return new NPCTradeMenu(containerId, playerInv, npc);
    }

    public NPCTradeMenu(int containerId, Inventory playerInv, BaseNPC npc) {
        super(ModMenuTypes.NPC_TRADE.get(), containerId);
        this.npc = npc;
        this.player = (ServerPlayer) playerInv.player;

        // 4×9 trade slots
        for (int row = 0; row < TRADE_ROWS; row++) {
            for (int col = 0; col < TRADE_COLS; col++) {
                int index = row * TRADE_COLS + col;
                addSlot(new Slot(tradeContainer, index, 8 + col * 18, 18 + row * 18));
                slotStates.add(SlotState.EMPTY);
            }
        }

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 86 + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 144));
        }

        // Fill NPC offers
        loadOffers();
    }

    private void loadOffers() {
        List<NPCTradeOffer> offers = NPCTradeList.getAvailableOffers(npc);
        for (int i = 0; i < offers.size() && i < TRADE_SIZE; i++) {
            NPCTradeOffer offer = offers.get(i);
            tradeContainer.setItem(i, offer.item().copy());
            slotStates.set(i, SlotState.NPC_ITEM);
        }
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType clickType, Player player) {
        if (slotIndex < 0 || slotIndex >= TRADE_SIZE) {
            super.clicked(slotIndex, button, clickType, player);
            return;
        }

        ItemStack cursor = getCarried();
        SlotState state = slotStates.get(slotIndex);
        Slot slot = slots.get(slotIndex);

        if (!cursor.isEmpty() && state == SlotState.EMPTY) {
            // === SELL ===
            int value = ValueComponent.getValue(cursor, 0);
            if (value == 0) return;
            int price = (int) (value * npc.getMood().getTradePriceMultiplier());
            slot.set(cursor.copy());
            slotStates.set(slotIndex, SlotState.PLAYER_SOLD);
            setCarried(ItemStack.EMPTY);
            giveCoins(player, price);
        } else if (cursor.isEmpty() && state == SlotState.PLAYER_SOLD) {
            // === REFUND ===
            ItemStack soldItem = slot.getItem().copy();
            int price = ValueComponent.getValue(soldItem, 0);
            if (PlayerUtils.tryCostMoney(player, price, true)) {
                setCarried(soldItem);
                slot.set(ItemStack.EMPTY);
                slotStates.set(slotIndex, SlotState.EMPTY);
            }
        } else if (cursor.isEmpty() && state == SlotState.NPC_ITEM) {
            // === BUY ===
            ItemStack npcItem = slot.getItem().copy();
            int value = ValueComponent.getValue(npcItem, 0);
            if (value == 0) return;
            int price = (int) (value * 5 * npc.getMood().getTradePriceMultiplier());
            if (PlayerUtils.tryCostMoney(player, price, true)) {
                setCarried(npcItem);
            }
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        // Discard all PLAYER_SOLD items
        for (int i = 0; i < TRADE_SIZE; i++) {
            if (slotStates.get(i) == SlotState.PLAYER_SOLD) {
                tradeContainer.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem().copy();
        if (index >= TRADE_SIZE) {
            // From inventory / hotbar → try to sell into first empty trade slot
            for (int i = 0; i < TRADE_SIZE; i++) {
                if (slotStates.get(i) == SlotState.EMPTY) {
                    int value = ValueComponent.getValue(stack, 0);
                    if (value == 0) return ItemStack.EMPTY;
                    int price = (int) (value * npc.getMood().getTradePriceMultiplier());
                    this.slots.get(i).set(stack);
                    slotStates.set(i, SlotState.PLAYER_SOLD);
                    slot.set(ItemStack.EMPTY);
                    giveCoins(player, price);
                    return ItemStack.EMPTY;
                }
            }
            return ItemStack.EMPTY;
        }
        // From trade slot → do nothing on shift-click (use normal click for buy/refund)
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return npc.isAlive() && player.distanceToSqr(npc) <= 64;
    }

    public BaseNPC getNPC() {
        return npc;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setScrollOffset(int offset) {
        this.scrollOffset = Math.max(0, offset);
    }

    public List<SlotState> getSlotStates() {
        return slotStates;
    }

    public enum SlotState {
        EMPTY, NPC_ITEM, PLAYER_SOLD
    }

    private static void giveCoins(Player player, long amount) {
        Coins coins = PlayerUtils.decodeCoin(amount);
        for (var entry : coins.copper2PlatinumEntries()) {
            int count = entry.getIntValue();
            if (count > 0) {
                player.getInventory().add(new ItemStack(entry.getKey(), count));
            }
        }
    }
}
