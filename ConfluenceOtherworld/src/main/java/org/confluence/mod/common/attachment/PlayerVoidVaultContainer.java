package org.confluence.mod.common.attachment;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.PlayerContainer;
import org.confluence.mod.common.block.functional.VoidVaultBlock;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.common.item.storage.VoidBagItem;
import org.confluence.mod.network.s2c.VoidVaultSyncPacketS2C;
import org.confluence.mod.util.Coins;
import org.confluence.mod.util.PlayerUtils;

// 库存属于玩家，方块和随身入口只负责访问，不保存物品副本。
public class PlayerVoidVaultContainer extends PlayerContainer<VoidVaultBlock.BEntity> {
    public static final int COLUMNS = 9;
    public static final int ROWS = 6;
    public static final int SIZE = COLUMNS * ROWS;
    private boolean dirty = true;

    public PlayerVoidVaultContainer() {
        super(ROWS);
    }

    @Override
    public int getMaxStackSize() {
        return 9999;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        dirty = true;
    }

    public static void tickContents(Player player) {
        if (!VoidBagItem.hasOpenBag(player)) return;
        PlayerVoidVaultContainer container = of(player);
        for (int slot = 0; slot < SIZE; slot++) {
            ItemStack stack = container.getItem(slot);
            if (!stack.isEmpty()) stack.inventoryTick(player.level(), player, 36 + slot, false);
        }
    }

    public void sync(ServerPlayer player, boolean force) {
        if (force || dirty) {
            VoidVaultSyncPacketS2C.send(player, this);
            dirty = false;
        }
    }

    public long getMoney() {
        long amount = 0;
        for (ItemStack stack : getItems()) {
            if (stack.getItem() instanceof CoinItem) {
                int index = PlayerUtils.COIN_2_INDEX.applyAsInt(stack.getItem());
                if (index >= 0)
                    amount += (long) Math.pow(CoinItem.UPGRADES_COUNT, 3 - index) * stack.getCount();
            }
        }
        return amount;
    }

    // 只扣除所需金额；找零优先回到空出的钱币格，溢出再交给玩家背包。
    public long tryCostMoney(Player player, long cost) {
        long money = getMoney();
        if (cost <= 0) return 0;
        if (money == 0) return cost;
        long paid = Math.min(money, cost);
        for (int slot = 0; slot < SIZE; slot++) {
            if (PlayerUtils.COIN_2_INDEX.applyAsInt(getItem(slot).getItem()) >= 0)
                setItem(slot, ItemStack.EMPTY);
        }
        Coins change = PlayerUtils.decodeCoin(money - paid);
        for (var entry : change.copper2PlatinumEntries()) {
            int remaining = entry.getIntValue();
            while (remaining > 0) {
                int count = Math.min(remaining, entry.getKey().getDefaultMaxStackSize());
                ItemStack overflow = addItem(new ItemStack(entry.getKey(), count));
                if (!overflow.isEmpty() && !player.getInventory().add(overflow))
                    player.drop(overflow, false);
                remaining -= count;
            }
        }
        setChanged();
        return cost - paid;
    }

    public static PlayerVoidVaultContainer of(Player player) {
        return player.getData(ModAttachmentTypes.VOID_VAULT);
    }
}
