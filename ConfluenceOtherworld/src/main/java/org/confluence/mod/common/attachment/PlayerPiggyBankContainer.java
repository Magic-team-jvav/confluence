package org.confluence.mod.common.attachment;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.PlayerContainer;
import org.confluence.mod.common.block.functional.PiggyBankBlock;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.network.s2c.PiggyBankTotalMoneyPacket;
import org.jetbrains.annotations.ApiStatus;

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
                res += CoinItem.valueOf(stack.getItem()) * stack.getCount();
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
