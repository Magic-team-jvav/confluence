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
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.util.Coins;
import org.confluence.mod.util.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

/// NPC 商店菜单。
///
/// 商品可购买；把物品放入任意空白商店格或 Shift 点击背包即可出售，并在本次交易中回购。
public class NPCTradeMenu extends AbstractContainerMenu {
    public static final String BUY_PRICE_TAG = "ConfluenceShopBuyPrice";
    private static final int TRADE_COLS = 9;
    private static final int TRADE_ROWS = 4;
    private static final int TRADE_SIZE = TRADE_COLS * TRADE_ROWS;
    private static final int OFFER_SLOTS = TRADE_SIZE;
    private static final int MONEY_SLOT_START = TRADE_SIZE;
    private static final int MONEY_SLOT_COUNT = 4;
    private static final int MONEY_SLOT_END = MONEY_SLOT_START + MONEY_SLOT_COUNT;
    private static final int PLAYER_SLOT_START = MONEY_SLOT_END;
    private static final int DATA_PAGE = 0;
    private static final int DATA_PAGE_COUNT = 1;
    private static final int DATA_OFFER_COUNT = 2;
    private static final int DATA_BUYBACK_COUNT = 3;

    private final BaseNPC npc;
    private final Player player;
    private final Container tradeContainer = new SimpleContainer(TRADE_SIZE);
    private final Container moneyContainer = new SimpleContainer(MONEY_SLOT_COUNT) {
        @Override
        public int getMaxStackSize() {
            return Integer.MAX_VALUE;
        }
    };
    private final List<NPCTradeOffer> offers;
    private final int shopRevision;
    private final List<SlotState> slotStates = new ArrayList<>(TRADE_SIZE);
    private final SimpleContainerData pageData = new SimpleContainerData(4);
    private final List<Buyback> buybacks = new ArrayList<>();
    private final List<Buyback> refundablePurchases = new ArrayList<>();

    public static NPCTradeMenu fromNetwork(int containerId, Inventory inventory, FriendlyByteBuf data) {
        int entityId = data.readInt();
        var entity = inventory.player.level().getEntity(entityId);
        if (!(entity instanceof BaseNPC npc)) {
            throw new IllegalArgumentException("NPC trade menu requires a valid NPC entity, got entity id " + entityId);
        }
        return new NPCTradeMenu(containerId, inventory, npc);
    }

    public NPCTradeMenu(int containerId, Inventory inventory, BaseNPC npc) {
        this(containerId, inventory, npc, List.of(), -1);
    }

    public NPCTradeMenu(int containerId, Inventory inventory, BaseNPC npc, List<NPCTradeOffer> offers) {
        this(containerId, inventory, npc, offers, NPCTradeList.getRevision());
    }

    public NPCTradeMenu(int containerId, Inventory inventory, BaseNPC npc, List<NPCTradeOffer> offers, int shopRevision) {
        super(ModMenuTypes.NPC_TRADE.get(), containerId);
        this.npc = npc;
        this.player = inventory.player;
        this.offers = offers;
        this.shopRevision = shopRevision;

        for (int row = 0; row < TRADE_ROWS; row++) {
            for (int col = 0; col < TRADE_COLS; col++) {
                int index = row * TRADE_COLS + col;
                addSlot(new TradeSlot(tradeContainer, index, 8 + col * 18, 18 + row * 18));
                slotStates.add(SlotState.EMPTY);
            }
        }
        for (int slot = 0; slot < MONEY_SLOT_COUNT; slot++)
            addSlot(new MoneyDisplaySlot(moneyContainer, slot, -25, 18 + slot * 18));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 103 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 161));
        }

        addDataSlots(pageData);
        pageData.set(DATA_OFFER_COUNT, this.offers.size());
        pageData.set(DATA_PAGE_COUNT, this.offers.size() / OFFER_SLOTS + 1);
        populatePage(0);
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType clickType, Player player) {
        if (slotIndex < 0 || slotIndex >= TRADE_SIZE) {
            super.clicked(slotIndex, button, clickType, player);
            return;
        }
        if (!(player instanceof ServerPlayer serverPlayer) || !canUseThisMenu(serverPlayer)) return;
        int offerIndex = getCurrentPage() * OFFER_SLOTS + slotIndex;
        if (offerIndex >= offers.size()) {
            int buybackIndex = offerIndex - offers.size();
            if (buybackIndex < buybacks.size()) {
                if (clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE)
                    buyBack(serverPlayer, buybackIndex, clickType == ClickType.QUICK_MOVE);
            } else if (clickType == ClickType.PICKUP) {
                sellCarried(serverPlayer);
            }
            return;
        }
        NPCTradeOffer offer = offers.get(offerIndex);
        if (clickType == ClickType.QUICK_MOVE) buyBatch(serverPlayer, offer);
        else if (clickType == ClickType.PICKUP) buyToCursor(serverPlayer, offer);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < PLAYER_SLOT_START || index >= slots.size()) return ItemStack.EMPTY;
        if (!(player instanceof ServerPlayer serverPlayer) || !canUseThisMenu(serverPlayer))
            return ItemStack.EMPTY;

        Slot source = slots.get(index);
        if (!source.hasItem()) return ItemStack.EMPTY;
        ItemStack soldStack = source.getItem().copy();
        if (soldStack.is(ModTags.Items.COINS)) return ItemStack.EMPTY;
        long price = getSellPrice(soldStack);
        if (price > 0 && PlayerUtils.creditFromInventory(serverPlayer, source.getContainerSlot(), soldStack, price, true)) {
            rememberSale(soldStack, price);
            return soldStack;
        }
        return ItemStack.EMPTY;
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
        if (npc.getTradingPlayer() == player) npc.setTradingPlayer(null);
        tradeContainer.clearContent();
        refundablePurchases.clear();
    }

    @Override
    public boolean stillValid(Player player) {
        return npc.isAlive() && player.isAlive() && player.level() == npc.level() && player.distanceToSqr(npc) <= 64.0D
                && (!(player instanceof ServerPlayer) || shopRevision == NPCTradeList.getRevision());
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
        int firstSlot = getCurrentPage() * OFFER_SLOTS;
        for (int slot = 0; slot < TRADE_SIZE; slot++) {
            int absolute = firstSlot + slot;
            slotStates.set(slot, absolute < pageData.get(DATA_OFFER_COUNT) ? SlotState.NPC_ITEM
                    : absolute < pageData.get(DATA_OFFER_COUNT) + pageData.get(DATA_BUYBACK_COUNT) ? SlotState.BUYBACK : SlotState.EMPTY);
        }
        return slotStates;
    }

    private void populatePage(int page) {
        int firstOffer = page * OFFER_SLOTS;
        for (int slot = 0; slot < TRADE_SIZE; slot++) {
            int absoluteSlot = firstOffer + slot;
            SlotState state = absoluteSlot < offers.size() ? SlotState.NPC_ITEM
                    : absoluteSlot < offers.size() + buybacks.size() ? SlotState.BUYBACK : SlotState.EMPTY;
            slotStates.set(slot, state);
            if (state == SlotState.NPC_ITEM) {
                NPCTradeOffer offer = offers.get(absoluteSlot);
                ItemStack stack = offer.stack();
                tradeContainer.setItem(slot, withTradeDetails(stack, offer.costs(), getBuyPrice(stack)));
            } else if (state == SlotState.BUYBACK) {
                Buyback sale = buybacks.get(absoluteSlot - offers.size());
                tradeContainer.setItem(slot, withTradeDetails(sale.stack(), List.of(), sale.price()));
            } else {
                tradeContainer.setItem(slot, ItemStack.EMPTY);
            }
        }
        pageData.set(DATA_PAGE, page);
        broadcastChanges();
    }

    public boolean isOfferSlot(int slot) {
        return slot >= 0 && slot < OFFER_SLOTS && getCurrentPage() * OFFER_SLOTS + slot < pageData.get(DATA_OFFER_COUNT);
    }

    public int getMoneySlotStart() {
        return MONEY_SLOT_START;
    }

    public int getMoneySlotCount() {
        return MONEY_SLOT_COUNT;
    }

    @Override
    public void broadcastChanges() {
        if (!npc.level().isClientSide) updateMoneyDisplay();
        super.broadcastChanges();
    }

    private void updateMoneyDisplay() {
        Coins coins = PlayerUtils.decodeCoin(PlayerUtils.getMoney(player, true));
        setMoneyDisplay(0, ModItems.PLATINUM_COIN.toStack(coins.platinum()));
        setMoneyDisplay(1, ModItems.GOLD_COIN.toStack(coins.gold()));
        setMoneyDisplay(2, ModItems.SILVER_COIN.toStack(coins.silver()));
        setMoneyDisplay(3, ModItems.COPPER_COIN.toStack(coins.copper()));
    }

    private void setMoneyDisplay(int slot, ItemStack stack) {
        if (stack.getCount() <= 0) stack = ItemStack.EMPTY;
        if (!ItemStack.matches(moneyContainer.getItem(slot), stack))
            moneyContainer.setItem(slot, stack);
    }

    private long getBuyPrice(ItemStack stack) {
        try {
            long value = ValueComponent.getValueLong(stack, 0);
            if (value <= 0) return 0;
            return (long) (Math.multiplyExact(value, 5L) * (double) npc.getMood().getBuyPriceMultiplier());
        } catch (ArithmeticException ignored) {
            return 0;
        }
    }

    private long getSellPrice(ItemStack stack) {
        try {
            int remaining = stack.getCount();
            long refund = 0;
            for (Buyback purchase : refundablePurchases) {
                if (!ItemStack.isSameItemSameTags(stack, purchase.stack())) continue;
                int count = Math.min(remaining, purchase.stack().getCount());
                refund = Math.addExact(refund, Math.multiplyExact(purchase.price(), count) / purchase.stack().getCount());
                remaining -= count;
                if (remaining == 0) return refund;
            }
            ItemStack ordinary = stack.copyWithCount(remaining);
            long value = ValueComponent.getValueLong(ordinary, 0);
            return Math.addExact(refund, value <= 0 ? 0 : (long) (value * (double) npc.getMood().getSellPriceMultiplier()));
        } catch (ArithmeticException ignored) {
            return 0;
        }
    }

    private void sellCarried(ServerPlayer player) {
        ItemStack cursor = getCarried();
        if (cursor.isEmpty() || cursor.is(ModTags.Items.COINS)) return;
        long price = getSellPrice(cursor);
        if (price > 0 && PlayerUtils.credit(player, price)) {
            ItemStack sold = cursor.copy();
            setCarried(ItemStack.EMPTY);
            rememberSale(sold, price);
        }
    }

    private void rememberSale(ItemStack stack, long price) {
        int remaining = stack.getCount();
        for (int index = 0; index < refundablePurchases.size() && remaining > 0; ) {
            Buyback purchase = refundablePurchases.get(index);
            if (!ItemStack.isSameItemSameTags(stack, purchase.stack())) {
                index++;
                continue;
            }
            int count = Math.min(remaining, purchase.stack().getCount());
            long refunded = purchase.price() * count / purchase.stack().getCount();
            remaining -= count;
            if (count == purchase.stack().getCount()) refundablePurchases.remove(index);
            else {
                refundablePurchases.set(index++, new Buyback(purchase.stack().copyWithCount(purchase.stack().getCount() - count), purchase.price() - refunded));
            }
        }
        buybacks.add(new Buyback(stack.copy(), price));
        refreshStock();
    }

    private void refreshStock() {
        pageData.set(DATA_BUYBACK_COUNT, buybacks.size());
        // 商品恰好占满一页时仍提供空白页，随时可将光标物品放入商店出售。
        pageData.set(DATA_PAGE_COUNT, (offers.size() + buybacks.size()) / OFFER_SLOTS + 1);
        populatePage(Math.min(getCurrentPage(), getPageCount() - 1));
    }

    private void buyBack(ServerPlayer player, int index, boolean toInventory) {
        Buyback sale = buybacks.get(index);
        ItemStack result = sale.stack().copy();
        ItemStack receipt = result.copy();
        if (toInventory) {
            if (!PlayerUtils.purchase(player, sale.price(), true, result)) return;
        } else {
            ItemStack cursor = getCarried();
            if (!cursor.isEmpty() && (!ItemStack.isSameItemSameTags(cursor, result)
                    || cursor.getCount() + result.getCount() > cursor.getMaxStackSize())) return;
            if (!PlayerUtils.debit(player, sale.price(), true)) return;
            if (cursor.isEmpty()) setCarried(result);
            else cursor.grow(result.getCount());
        }
        buybacks.remove(index);
        refundablePurchases.add(new Buyback(receipt, sale.price()));
        refreshStock();
    }

    private record Buyback(ItemStack stack, long price) {}

    private void buyToCursor(ServerPlayer player, NPCTradeOffer offer) {
        ItemStack result = offer.stack();
        ItemStack cursor = getCarried();
        if (!cursor.isEmpty() && (!ItemStack.isSameItemSameTags(cursor, result) || cursor.getCount() + result.getCount() > cursor.getMaxStackSize()))
            return;
        List<ItemStack> costs = offer.costs();
        long price = costs.isEmpty() ? getBuyPrice(result) : 0;
        boolean completed = costs.isEmpty() ? price > 0 && PlayerUtils.debit(player, price, true) : consumeCosts(player, costs, 1, ItemStack.EMPTY);
        if (!completed) return;
        if (costs.isEmpty()) refundablePurchases.add(new Buyback(result.copy(), price));
        if (cursor.isEmpty()) setCarried(result);
        else cursor.grow(result.getCount());
    }

    private void buyBatch(ServerPlayer player, NPCTradeOffer offer) {
        ItemStack result = offer.stack();
        int capacity = inventoryCapacity(player, result);
        if (capacity < result.getCount()) return;
        int trades = Math.min(10, capacity / result.getCount());
        List<ItemStack> costs = offer.costs();
        if (costs.isEmpty()) {
            long price = getBuyPrice(result);
            if (price <= 0) return;
            trades = (int) Math.min(trades, PlayerUtils.getMoney(player, true) / price);
            if (trades <= 0) return;
            ItemStack totalResult = result.copy();
            totalResult.setCount(Math.multiplyExact(result.getCount(), trades));
            long totalPrice = Math.multiplyExact(price, trades);
            ItemStack receipt = totalResult.copy();
            if (PlayerUtils.purchase(player, totalPrice, true, totalResult))
                refundablePurchases.add(new Buyback(receipt, totalPrice));
            return;
        }
        trades = Math.min(trades, availableCostTrades(player, costs));
        if (trades <= 0) return;
        ItemStack totalResult = result.copy();
        totalResult.setCount(Math.multiplyExact(result.getCount(), trades));
        consumeCosts(player, costs, trades, totalResult);
    }

    private static int inventoryCapacity(ServerPlayer player, ItemStack result) {
        long capacity = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty()) capacity += result.getMaxStackSize();
            else if (ItemStack.isSameItemSameTags(stack, result))
                capacity += Math.max(0, stack.getMaxStackSize() - stack.getCount());
        }
        return (int) Math.min(Integer.MAX_VALUE, capacity);
    }

    private static int availableCostTrades(ServerPlayer player, List<ItemStack> costs) {
        int trades = Integer.MAX_VALUE;
        for (int index = 0; index < costs.size(); index++) {
            ItemStack cost = costs.get(index);
            boolean counted = false;
            long required = 0;
            for (int previous = 0; previous < costs.size(); previous++) {
                ItemStack other = costs.get(previous);
                if (!ItemStack.isSameItemSameTags(other, cost)) continue;
                if (previous < index) counted = true;
                required += other.getCount();
            }
            if (counted) continue;
            long available = 0;
            for (ItemStack stack : player.getInventory().items) {
                if (ItemStack.isSameItemSameTags(stack, cost)) available += stack.getCount();
            }
            trades = Math.min(trades, (int) Math.min(Integer.MAX_VALUE, available / required));
        }
        return trades;
    }

    private static boolean consumeCosts(ServerPlayer player, List<ItemStack> costs, int trades, ItemStack result) {
        List<ItemStack> inventory = new ArrayList<>(player.getInventory().items.size());
        player.getInventory().items.forEach(stack -> inventory.add(stack.copy()));
        for (ItemStack cost : costs) {
            int remaining = Math.multiplyExact(cost.getCount(), trades);
            for (ItemStack stack : inventory) {
                if (remaining == 0) break;
                if (!ItemStack.isSameItemSameTags(stack, cost)) continue;
                int consumed = Math.min(remaining, stack.getCount());
                stack.shrink(consumed);
                remaining -= consumed;
            }
            if (remaining > 0) return false;
        }
        if (!result.isEmpty() && !insertIntoInventory(result, inventory)) return false;
        for (int slot = 0; slot < inventory.size(); slot++) {
            player.getInventory().items.set(slot, inventory.get(slot));
        }
        player.getInventory().setChanged();
        return true;
    }

    private static boolean insertIntoInventory(ItemStack source, List<ItemStack> inventory) {
        ItemStack remaining = source.copy();
        for (ItemStack stack : inventory) {
            if (remaining.isEmpty()) break;
            if (!stack.isEmpty() && ItemStack.isSameItemSameTags(stack, remaining)) {
                int moved = Math.min(remaining.getCount(), stack.getMaxStackSize() - stack.getCount());
                if (moved > 0) {
                    stack.grow(moved);
                    remaining.shrink(moved);
                }
            }
        }
        for (int slot = 0; slot < inventory.size() && !remaining.isEmpty(); slot++) {
            if (!inventory.get(slot).isEmpty()) continue;
            int moved = Math.min(remaining.getCount(), remaining.getMaxStackSize());
            ItemStack inserted = remaining.copy();
            inserted.setCount(moved);
            inventory.set(slot, inserted);
            remaining.shrink(moved);
        }
        return remaining.isEmpty();
    }

    private static ItemStack withTradeDetails(ItemStack source, List<ItemStack> costs, long price) {
        ItemStack display = source.copy();
        CompoundTag displayTag = display.getOrCreateTagElement("display");
        ListTag lore = displayTag.getList("Lore", Tag.TAG_STRING);
        if (costs.isEmpty()) {
            if (price <= 0) return display;
            display.getOrCreateTag().putLong(BUY_PRICE_TAG, price);
        } else {
            for (ItemStack cost : costs) {
                Component line = Component.translatable("tooltip.trade.cost", cost.getCount(), cost.getHoverName()).withStyle(ChatFormatting.GRAY).withStyle(style -> style.withItalic(false));
                lore.add(StringTag.valueOf(Component.Serializer.toJson(line)));
            }
        }
        displayTag.put("Lore", lore);
        return display;
    }

    public enum SlotState {
        EMPTY, NPC_ITEM, BUYBACK
    }

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

    private static final class MoneyDisplaySlot extends Slot {
        private MoneyDisplaySlot(Container container, int slot, int x, int y) {
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
