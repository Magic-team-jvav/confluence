package org.confluence.mod.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.confluence.mod.common.summon.SummonInstance;
import org.confluence.mod.common.summon.SummonType;

/// 玩家创建召唤实例时发布的扩展事件。
public class SummonEvent extends PlayerEvent {
    private final ItemStack itemStack;
    private final SummonInstance summon;

    public SummonEvent(Player player, ItemStack itemStack, SummonInstance summon) {
        super(player);
        this.itemStack = itemStack;
        this.summon = summon;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public SummonInstance getSummon() {
        return summon;
    }

    /// 在创建运行实例前发布，取消后不会占用栏位或播放召唤反馈。
    @Cancelable
    public static final class Pre extends PlayerEvent {
        private final ItemStack itemStack;
        private final SummonType summonType;

        public Pre(Player player, ItemStack itemStack, SummonType summonType) {
            super(player);
            this.itemStack = itemStack;
            this.summonType = summonType;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public SummonType getSummonType() {
            return summonType;
        }
    }
}
