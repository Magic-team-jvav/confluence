package org.confluence.mod.api.event;

import net.minecraftforge.eventbus.api.Cancelable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

/// 由 LivingIncomingDamageEvent 转发的代偿事件；取消不回滚已提交的灵魂消耗、技能效果与冷却。
@Cancelable
public class SoulOverdraftEvent extends PlayerEvent {
    private float amount;

    public SoulOverdraftEvent(ServerPlayer player, float amount) {
        super(player);
        this.amount = amount;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}

