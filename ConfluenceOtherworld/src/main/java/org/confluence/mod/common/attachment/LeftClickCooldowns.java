package org.confluence.mod.common.attachment;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.network.s2c.LeftClickCooldownPacketS2C;

/// 独立于右键使用冷却，按物品保存并同步左键冷却。
public class LeftClickCooldowns extends ItemCooldowns {
    private final Player player;

    public LeftClickCooldowns(Player player) {
        this.player = player;
    }

    public static LeftClickCooldowns of(Player player) {
        return player.getData(ModAttachmentTypes.LEFT_CLICK_COOLDOWNS);
    }

    /// 同一 tick 为这些物品设置相同冷却，分组成员由调用方决定。
    public void addSharedCooldown(Iterable<? extends Item> items, int ticks) {
        for (Item item : items) {
            if (ticks > 0) {
                addCooldown(item, ticks);
            } else {
                removeCooldown(item);
            }
        }
    }

    @Override
    protected void onCooldownStarted(Item item, int ticks) {
        super.onCooldownStarted(item, ticks);
        if (player instanceof ServerPlayer serverPlayer) {
            LeftClickCooldownPacketS2C.send(serverPlayer, item, ticks);
        }
    }

    @Override
    protected void onCooldownEnded(Item item) {
        super.onCooldownEnded(item);
        if (player instanceof ServerPlayer serverPlayer) {
            LeftClickCooldownPacketS2C.send(serverPlayer, item, 0);
        }
    }
}
