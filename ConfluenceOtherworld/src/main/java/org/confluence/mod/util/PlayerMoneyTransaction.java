package org.confluence.mod.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.PlayerPiggyBankContainer;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.common.CoinItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_COINS;

/// 为玩家钱币提供先规划、后提交的原子扣款。
///
/// <p>旧实现会先清空真钱币，再尝试把找零塞回背包；空间不足时 {@code Inventory#add}
/// 的失败结果被忽略，因而可能在返回成功的同时吞掉找零。本类只操作物品快照，只有
/// 资金充足且全部找零都能放入时才统一覆盖真实容器。</p>
public final class PlayerMoneyTransaction {
    private PlayerMoneyTransaction() {}

    /// 从主背包、钱币栏以及可选存钱罐中扣款。
    ///
    /// @return 扣款已经完整提交时为 {@code true}；资金或找零空间不足时为 {@code false}
    public static boolean debit(Player player, long cost, boolean includePiggyBank) {
        return execute(player, cost, includePiggyBank, ItemStack.EMPTY);
    }

    /// 在同一事务中扣款并把商品放入玩家主背包。
    ///
    /// <p>商品无法完整合并或放入空槽时，钱币快照也不会提交。</p>
    public static boolean purchase(Player player, long cost, boolean includePiggyBank, ItemStack result) {
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Purchase result cannot be empty");
        }
        return execute(player, cost, includePiggyBank, result);
    }

    /// 把售回所得的钱币完整写入钱包；空间不足时不写入任何钱币。
    public static boolean credit(Player player, long amount, boolean includePiggyBank) {
        if (amount < 0) {
            throw new IllegalArgumentException("Money credit cannot be negative");
        }
        if (amount == 0) {
            return true;
        }

        try {
            return creditChecked(player, amount, includePiggyBank);
        } catch (ArithmeticException ignored) {
            // 数据或附属内容给出的金额无法用 long 精确表示时，拒绝整笔事务。
            return false;
        }
    }

    /// 从玩家主背包的指定槽位移除物品并结算售出所得。
    public static boolean creditFromInventory(
            Player player,
            int sourceSlot,
            ItemStack expectedStack,
            long amount,
            boolean includePiggyBank) {
        if (sourceSlot < 0 || sourceSlot >= player.getInventory().items.size() || expectedStack.isEmpty() || amount <= 0) {
            return false;
        }
        try {
            return creditFromInventoryChecked(player, sourceSlot, expectedStack, amount, includePiggyBank);
        } catch (ArithmeticException ignored) {
            return false;
        }
    }

    private static boolean creditChecked(
            Player player,
            long amount,
            boolean includePiggyBank) {
        Inventory inventory = player.getInventory();
        ExtraInventory extraInventory = ExtraInventory.of(player);
        PlayerPiggyBankContainer piggyBank =
                includePiggyBank ? PlayerPiggyBankContainer.of(player) : null;
        List<ItemStack> inventoryCopy = copyStacks(inventory.items);
        List<ItemStack> extraCopy = copyStacks(extraInventory.getAllCoins());
        List<ItemStack> piggyCopy = piggyBank == null
                ? List.of()
                : copyContainer(piggyBank);

        long current = Math.addExact(
                sumAndClearCoins(inventoryCopy),
                Math.addExact(sumAndClearCoins(extraCopy), sumAndClearCoins(piggyCopy)));
        Optional<List<ItemStack>> encoded = encodeCoins(
                Math.addExact(current, amount),
                inventoryCopy.size() + extraCopy.size() + piggyCopy.size());
        if (encoded.isEmpty()) {
            return false;
        }
        for (ItemStack stack : encoded.get()) {
            if (!placeIntoEmptySlot(stack, extraCopy)
                    && !placeIntoEmptySlot(stack, piggyCopy)
                    && !placeIntoEmptySlot(stack, inventoryCopy)) {
                return false;
            }
        }

        commitInventory(inventory, inventoryCopy);
        commitExtraInventory(extraInventory, extraCopy);
        if (piggyBank != null) {
            commitContainer(piggyBank, piggyCopy);
        }
        return true;
    }

    private static boolean creditFromInventoryChecked(
            Player player,
            int sourceSlot,
            ItemStack expectedStack,
            long amount,
            boolean includePiggyBank) {
        Inventory inventory = player.getInventory();
        ExtraInventory extraInventory = ExtraInventory.of(player);
        PlayerPiggyBankContainer piggyBank = includePiggyBank ? PlayerPiggyBankContainer.of(player) : null;
        ItemStack source = inventory.items.get(sourceSlot);
        if (!ItemStack.matches(source, expectedStack)) return false;

        List<ItemStack> inventoryCopy = copyStacks(inventory.items);
        List<ItemStack> extraCopy = copyStacks(extraInventory.getAllCoins());
        List<ItemStack> piggyCopy = piggyBank == null ? List.of() : copyContainer(piggyBank);
        inventoryCopy.set(sourceSlot, ItemStack.EMPTY);

        long current = Math.addExact(
                sumAndClearCoins(inventoryCopy),
                Math.addExact(sumAndClearCoins(extraCopy), sumAndClearCoins(piggyCopy)));
        Optional<List<ItemStack>> encoded = encodeCoins(
                Math.addExact(current, amount),
                inventoryCopy.size() + extraCopy.size() + piggyCopy.size());
        if (encoded.isEmpty()) return false;
        for (ItemStack stack : encoded.get()) {
            if (!placeIntoEmptySlot(stack, extraCopy)
                    && !placeIntoEmptySlot(stack, piggyCopy)
                    && !placeIntoEmptySlot(stack, inventoryCopy)) {
                return false;
            }
        }

        commitInventory(inventory, inventoryCopy);
        commitExtraInventory(extraInventory, extraCopy);
        if (piggyBank != null) commitContainer(piggyBank, piggyCopy);
        return true;
    }

    private static boolean execute(Player player, long cost, boolean includePiggyBank, ItemStack result) {
        if (cost < 0) {
            throw new IllegalArgumentException("Money cost cannot be negative");
        }

        try {
            return executeChecked(player, cost, includePiggyBank, result);
        } catch (ArithmeticException ignored) {
            // 金额溢出意味着无法证明事务守恒，必须在提交任何快照前失败。
            return false;
        }
    }

    private static boolean executeChecked(
            Player player,
            long cost,
            boolean includePiggyBank,
            ItemStack result) {
        Inventory inventory = player.getInventory();
        ExtraInventory extraInventory = ExtraInventory.of(player);
        PlayerPiggyBankContainer piggyBank =
                includePiggyBank ? PlayerPiggyBankContainer.of(player) : null;

        List<ItemStack> inventoryCopy = copyStacks(inventory.items);
        List<ItemStack> extraCopy = copyStacks(extraInventory.getAllCoins());
        List<ItemStack> piggyCopy = piggyBank == null
                ? List.of()
                : copyContainer(piggyBank);

        long total = Math.addExact(
                sumAndClearCoins(inventoryCopy),
                Math.addExact(sumAndClearCoins(extraCopy), sumAndClearCoins(piggyCopy)));
        if (total < cost) {
            return false;
        }

        if (!result.isEmpty() && !insertIntoInventory(result, inventoryCopy)) {
            return false;
        }

        Optional<List<ItemStack>> change = encodeCoins(
                total - cost,
                inventoryCopy.size() + extraCopy.size() + piggyCopy.size());
        if (change.isEmpty()) {
            return false;
        }
        for (ItemStack stack : change.get()) {
            if (!placeIntoEmptySlot(stack, extraCopy)
                    && !placeIntoEmptySlot(stack, piggyCopy)
                    && !placeIntoEmptySlot(stack, inventoryCopy)) {
                return false;
            }
        }

        commitInventory(inventory, inventoryCopy);
        commitExtraInventory(extraInventory, extraCopy);
        if (piggyBank != null) {
            commitContainer(piggyBank, piggyCopy);
        }
        return true;
    }

    private static List<ItemStack> copyStacks(List<ItemStack> source) {
        List<ItemStack> copy = new ArrayList<>(source.size());
        for (ItemStack stack : source) {
            copy.add(stack.copy());
        }
        return copy;
    }

    private static List<ItemStack> copyContainer(Container container) {
        List<ItemStack> copy = new ArrayList<>(container.getContainerSize());
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            copy.add(container.getItem(slot).copy());
        }
        return copy;
    }

    private static long sumAndClearCoins(List<ItemStack> stacks) {
        long total = 0;
        for (int slot = 0; slot < stacks.size(); slot++) {
            ItemStack stack = stacks.get(slot);
            long value = CoinItem.valueOf(stack.getItem());
            if (stack.isEmpty() || !stack.is(ModTags.Items.COINS) || value == 0) {
                continue;
            }
            total = Math.addExact(total, Math.multiplyExact(value, stack.getCount()));
            stacks.set(slot, ItemStack.EMPTY);
        }
        return total;
    }

    /// 在明确的槽位预算内拆分钱币。
    ///
    /// <p>先计算每种币值需要的物品组数，再创建物品栈。这样即使附属数据提供接近
    /// {@link Long#MAX_VALUE} 的金额，也只会快速返回失败，不会构造数十亿个临时栈。</p>
    private static Optional<List<ItemStack>> encodeCoins(long amount, int maxStacks) {
        if (amount < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
        if (maxStacks < 0) {
            throw new IllegalArgumentException("Money stack budget cannot be negative");
        }
        List<ItemStack> result = new ArrayList<>(4);
        amount = appendCoins(result, ModItems.PLATINUM_COIN.get(), amount, CoinItem.PLATINUM_VALUE, maxStacks);
        if (amount < 0) return Optional.empty();
        amount = appendCoins(result, ModItems.GOLD_COIN.get(), amount, CoinItem.GOLD_VALUE, maxStacks);
        if (amount < 0) return Optional.empty();
        amount = appendCoins(result, ModItems.SILVER_COIN.get(), amount, CoinItem.SILVER_VALUE, maxStacks);
        if (amount < 0) return Optional.empty();
        amount = appendCoins(result, ModItems.COPPER_COIN.get(), amount, CoinItem.COPPER_VALUE, maxStacks);
        return amount < 0 ? Optional.empty() : Optional.of(result);
    }

    private static long appendCoins(
            List<ItemStack> output,
            Item coin,
            long amount,
            long value,
            int maxStacks
    ) {
        long count = amount / value;
        long remaining = amount % value;
        int maxStackSize = coin.getMaxStackSize();
        long requiredStacks = count == 0
                ? 0
                : ((count - 1L) / maxStackSize) + 1L;
        if (requiredStacks > maxStacks - output.size()) {
            return -1L;
        }
        while (count > 0) {
            int stackSize = (int) Math.min(count, maxStackSize);
            output.add(new ItemStack(coin, stackSize));
            count -= stackSize;
        }
        return remaining;
    }

    private static boolean placeIntoEmptySlot(ItemStack stack, List<ItemStack> slots) {
        if (slots.isEmpty()) {
            return false;
        }
        for (int slot = 0; slot < slots.size(); slot++) {
            if (slots.get(slot).isEmpty()) {
                slots.set(slot, stack.copy());
                return true;
            }
        }
        return false;
    }

    private static boolean insertIntoInventory(ItemStack source, List<ItemStack> inventory) {
        ItemStack remaining = source.copy();
        for (int slot = 0; slot < inventory.size() && !remaining.isEmpty(); slot++) {
            ItemStack existing = inventory.get(slot);
            if (existing.isEmpty() || !ItemStack.isSameItemSameTags(existing, remaining)) {
                continue;
            }
            int transferable = Math.min(
                    remaining.getCount(),
                    existing.getMaxStackSize() - existing.getCount());
            if (transferable <= 0) {
                continue;
            }
            existing.grow(transferable);
            remaining.shrink(transferable);
        }
        for (int slot = 0; slot < inventory.size() && !remaining.isEmpty(); slot++) {
            if (!inventory.get(slot).isEmpty()) {
                continue;
            }
            int transferable = Math.min(remaining.getCount(), remaining.getMaxStackSize());
            ItemStack inserted = remaining.copy();
            inserted.setCount(transferable);
            inventory.set(slot, inserted);
            remaining.shrink(transferable);
        }
        return remaining.isEmpty();
    }

    private static void commitInventory(Inventory inventory, List<ItemStack> stacks) {
        for (int slot = 0; slot < stacks.size(); slot++) {
            inventory.items.set(slot, stacks.get(slot));
        }
        inventory.setChanged();
    }

    private static void commitExtraInventory(ExtraInventory inventory, List<ItemStack> stacks) {
        if (stacks.size() != SIZE_COINS) {
            throw new IllegalStateException("Coin inventory snapshot has an invalid size");
        }
        for (int slot = 0; slot < SIZE_COINS; slot++) {
            inventory.setCoins(slot, stacks.get(slot));
        }
    }

    private static void commitContainer(Container container, List<ItemStack> stacks) {
        if (container.getContainerSize() != stacks.size()) {
            throw new IllegalStateException("Money container changed size during transaction");
        }
        for (int slot = 0; slot < stacks.size(); slot++) {
            container.setItem(slot, stacks.get(slot));
        }
        container.setChanged();
    }
}
