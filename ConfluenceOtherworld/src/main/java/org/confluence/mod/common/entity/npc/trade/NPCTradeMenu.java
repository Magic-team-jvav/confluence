package org.confluence.mod.common.entity.npc.trade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.util.MoneyText;
import org.confluence.mod.util.PlayerMoneyTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// NPC 商店菜单。
///
/// 商品槽保持三种状态：NPC 商品、空槽以及玩家刚刚售出的物品。购买结果和售回结果进入光标，
/// 玩家售出的物品只在本次菜单中保留，关闭菜单后清空。
public class NPCTradeMenu extends AbstractContainerMenu {
    private static final int TRADE_COLS = 9;
    private static final int TRADE_ROWS = 4;
    private static final int TRADE_SIZE = TRADE_COLS * TRADE_ROWS;
    private static final int DATA_PAGE = 0;
    private static final int DATA_PAGE_COUNT = 1;
    private static final int DATA_OFFER_COUNT = 2;

    private final BaseNPC npc;
    private final Container tradeContainer = new SimpleContainer(TRADE_SIZE);
    private final List<NPCTradeOffer> offers;
    private final Map<Integer, SoldItem> soldItems = new HashMap<>();
    private final List<SlotState> slotStates = new ArrayList<>(TRADE_SIZE);
    private final SimpleContainerData pageData = new SimpleContainerData(3);

    public static NPCTradeMenu fromNetwork(int containerId, Inventory inventory, FriendlyByteBuf data) {
        int entityId = data.readInt();
        var entity = inventory.player.level().getEntity(entityId);
        if (!(entity instanceof BaseNPC npc)) {
            throw new IllegalArgumentException("NPC trade menu requires a valid NPC entity, got entity id " + entityId);
        }
        return new NPCTradeMenu(containerId, inventory, npc);
    }

    public NPCTradeMenu(int containerId, Inventory inventory, BaseNPC npc) {
        this(containerId, inventory, npc, List.of());
    }

    public NPCTradeMenu(int containerId, Inventory inventory, BaseNPC npc, List<NPCTradeOffer> offers) {
        super(ModMenuTypes.NPC_TRADE.get(), containerId);
        this.npc = npc;
        this.offers = List.copyOf(offers);

        for (int row = 0; row < TRADE_ROWS; row++) {
            for (int col = 0; col < TRADE_COLS; col++) {
                int index = row * TRADE_COLS + col;
                addSlot(new TradeSlot(tradeContainer, index, 8 + col * 18, 18 + row * 18));
                slotStates.add(SlotState.EMPTY);
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 86 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 144));
        }

        addDataSlots(pageData);
        pageData.set(DATA_OFFER_COUNT, this.offers.size());
        pageData.set(DATA_PAGE_COUNT, Math.max(1, (this.offers.size() + TRADE_SIZE - 1) / TRADE_SIZE));
        populatePage(0);
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType clickType, Player player) {
        if (slotIndex < 0 || slotIndex >= TRADE_SIZE) {
            super.clicked(slotIndex, button, clickType, player);
            return;
        }
        if (!(player instanceof ServerPlayer serverPlayer) || !canUseThisMenu(serverPlayer)) return;

        int absoluteSlot = getCurrentPage() * TRADE_SIZE + slotIndex;
        SlotState state = getState(absoluteSlot);
        ItemStack cursor = getCarried();
        if (!cursor.isEmpty() && state == SlotState.EMPTY) {
            long price = getSellPrice(cursor);
            if (price > 0 && PlayerMoneyTransaction.credit(serverPlayer, price, false)) {
                soldItems.put(absoluteSlot, new SoldItem(cursor.copy(), price));
                setCarried(ItemStack.EMPTY);
                populatePage(getCurrentPage());
            }
        } else if (cursor.isEmpty() && state == SlotState.PLAYER_SOLD) {
            SoldItem sold = soldItems.get(absoluteSlot);
            if (sold != null && PlayerMoneyTransaction.debit(serverPlayer, sold.price(), true)) {
                soldItems.remove(absoluteSlot);
                setCarried(sold.stack().copy());
                populatePage(getCurrentPage());
            }
        } else if (cursor.isEmpty() && state == SlotState.NPC_ITEM) {
            ItemStack result = offers.get(absoluteSlot).stack();
            long price = getBuyPrice(result);
            if (price > 0 && PlayerMoneyTransaction.debit(serverPlayer, price, true)) {
                setCarried(result.copy());
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < TRADE_SIZE || index >= slots.size()) return ItemStack.EMPTY;
        if (!(player instanceof ServerPlayer serverPlayer) || !canUseThisMenu(serverPlayer))
            return ItemStack.EMPTY;

        Slot source = slots.get(index);
        if (!source.hasItem()) return ItemStack.EMPTY;
        int emptySlot = findEmptySlotOnCurrentPage();
        if (emptySlot < 0) return ItemStack.EMPTY;

        ItemStack soldStack = source.getItem().copy();
        long price = getSellPrice(soldStack);
        if (price <= 0 || !PlayerMoneyTransaction.creditFromInventory(
                serverPlayer, source.getContainerSlot(), soldStack, price, false)) {
            return ItemStack.EMPTY;
        }

        int absoluteSlot = getCurrentPage() * TRADE_SIZE + emptySlot;
        soldItems.put(absoluteSlot, new SoldItem(soldStack, price));
        populatePage(getCurrentPage());
        return soldStack;
    }

    @Override
    public boolean clickMenuButton(Player player, int page) {
        if (!(player instanceof ServerPlayer serverPlayer) || !canUseThisMenu(serverPlayer))
            return false;
        if (page < 0 || page >= getPageCount() || page == getCurrentPage()) return false;
        populatePage(page);
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        soldItems.clear();
        tradeContainer.clearContent();
    }

    @Override
    public boolean stillValid(Player player) {
        return npc.isAlive() && player.isAlive() && player.level() == npc.level() && player.distanceToSqr(npc) <= 64.0D;
    }

    private boolean canUseThisMenu(ServerPlayer player) {
        return player.containerMenu == this && stillValid(player);
    }

    public BaseNPC getNPC() {
        return npc;
    }

    public int getCurrentPage() {
        return pageData.get(DATA_PAGE);
    }

    public int getPageCount() {
        return pageData.get(DATA_PAGE_COUNT);
    }

    public List<SlotState> getSlotStates() {
        int firstSlot = getCurrentPage() * TRADE_SIZE;
        for (int slot = 0; slot < TRADE_SIZE; slot++) {
            int absoluteSlot = firstSlot + slot;
            slotStates.set(slot, absoluteSlot < pageData.get(DATA_OFFER_COUNT)
                    ? SlotState.NPC_ITEM
                    : tradeContainer.getItem(slot).isEmpty() ? SlotState.EMPTY : SlotState.PLAYER_SOLD);
        }
        return slotStates;
    }

    private void populatePage(int page) {
        int firstOffer = page * TRADE_SIZE;
        for (int slot = 0; slot < TRADE_SIZE; slot++) {
            int absoluteSlot = firstOffer + slot;
            SlotState state = getState(absoluteSlot);
            slotStates.set(slot, state);
            if (absoluteSlot < offers.size()) {
                ItemStack stack = offers.get(absoluteSlot).stack();
                tradeContainer.setItem(slot, withPrice(stack, getBuyPrice(stack)));
            } else {
                SoldItem sold = soldItems.get(absoluteSlot);
                tradeContainer.setItem(slot, sold == null ? ItemStack.EMPTY : withPrice(sold.stack(), sold.price()));
            }
        }
        pageData.set(DATA_PAGE, page);
        broadcastChanges();
    }

    private SlotState getState(int absoluteSlot) {
        if (absoluteSlot < pageData.get(DATA_OFFER_COUNT)) return SlotState.NPC_ITEM;
        return soldItems.containsKey(absoluteSlot) ? SlotState.PLAYER_SOLD : SlotState.EMPTY;
    }

    private int findEmptySlotOnCurrentPage() {
        int firstSlot = getCurrentPage() * TRADE_SIZE;
        for (int slot = 0; slot < TRADE_SIZE; slot++) {
            if (getState(firstSlot + slot) == SlotState.EMPTY) return slot;
        }
        return -1;
    }

    private long getBuyPrice(ItemStack stack) {
        try {
            long value = ValueComponent.getValueLong(stack, 0);
            if (value <= 0) return 0;
            return Math.max(1L, Math.round(Math.multiplyExact(value, 5L) * (double) npc.getMood().getBuyPriceMultiplier()));
        } catch (ArithmeticException ignored) {
            return 0;
        }
    }

    private long getSellPrice(ItemStack stack) {
        try {
            long value = ValueComponent.getValueLong(stack, 0);
            if (value <= 0) return 0;
            return Math.max(1L, Math.round(value * (double) npc.getMood().getSellPriceMultiplier()));
        } catch (ArithmeticException ignored) {
            return 0;
        }
    }

    private static ItemStack withPrice(ItemStack source, long price) {
        if (price <= 0) return source.copy();
        ItemStack display = source.copy();
        CompoundTag displayTag = display.getOrCreateTagElement("display");
        ListTag lore = displayTag.getList("Lore", Tag.TAG_STRING);
        Component line = Component.translatable("tooltip.price.buy").withStyle(ChatFormatting.GRAY).append(MoneyText.format(price));
        lore.add(StringTag.valueOf(Component.Serializer.toJson(line)));
        displayTag.put("Lore", lore);
        return display;
    }

    public enum SlotState {
        EMPTY, NPC_ITEM, PLAYER_SOLD
    }

    private record SoldItem(ItemStack stack, long price) {}

    private static final class TradeSlot extends Slot {
        private TradeSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }
    }
}
