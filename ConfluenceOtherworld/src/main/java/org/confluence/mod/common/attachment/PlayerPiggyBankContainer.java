package org.confluence.mod.common.attachment;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.PlayerContainer;
import org.confluence.mod.common.block.functional.PiggyBankBlock;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.network.s2c.PiggyBankTotalMoneyPacket;
import org.confluence.mod.util.Coins;
import org.jetbrains.annotations.ApiStatus;

import static org.confluence.mod.common.item.common.CoinItem.UPGRADES_COUNT;
import static org.confluence.mod.util.PlayerUtils.COIN_2_INDEX;
import static org.confluence.mod.util.PlayerUtils.decodeCoin;

public class PlayerPiggyBankContainer extends PlayerContainer<PiggyBankBlock.BEntity> {
    private long totalMoney;
    private Player owner;

    public PlayerPiggyBankContainer() {
        super(6);
        addListener(container -> {
            if (owner == null || owner.isLocalPlayer()) return;
            long res = 0;
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (!stack.isEmpty() && stack.is(ModTags.Items.COINS)) {
                    int index = COIN_2_INDEX.applyAsInt(stack.getItem());
                    if (index != -1) {
                        res += (long) (stack.getCount() * Math.pow(UPGRADES_COUNT, 3 - index));
                    }
                }
            }
            if (totalMoney != res) {
                this.totalMoney = res;
                if (owner instanceof ServerPlayer player) {
                    PiggyBankTotalMoneyPacket.sendToClient(player, this, false);
                }
            }
        });
    }

    @ApiStatus.Internal
    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }

    public long getTotalMoney() {
        return totalMoney;
    }

    /**
     * @return 从存钱罐中扣除后还差的钱
     */
    public long tryCostMoney(long cost) {
        if (cost <= 0) return 0;
        if (totalMoney <= 0) return cost;
        long res = totalMoney - cost;
        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty() && stack.is(ModTags.Items.COINS)) {
                setItemNoUpdate(i, ItemStack.EMPTY);
            }
        }
        if (res > 0) {
            Coins coins = decodeCoin(res);
            for (Object2IntMap.Entry<CoinItem> entry : coins.copper2PlatinumEntries()) {
                int coin = entry.getIntValue();
                if (coin <= 0) continue;
                CoinItem coinItem = entry.getKey();
                while (coin > UPGRADES_COUNT) {
                    addItem(new ItemStack(coinItem, UPGRADES_COUNT));
                    coin -= UPGRADES_COUNT;
                }
                addItem(new ItemStack(coinItem, coin));
            }
        }
        setChanged();
        return res < 0 ? -res : 0;
    }

    @ApiStatus.Internal
    public void setOwner(Player player) {
        this.owner = player;
    }

    public static PlayerPiggyBankContainer of(Player player) {
        PlayerPiggyBankContainer data = player.getData(ModAttachmentTypes.PIGGY_BANK);
        data.setOwner(player);
        return data;
    }
}
