package org.confluence.mod.api.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/// 由 LivingIncomingDamageEvent 转发的代偿事件；取消不回滚已提交的灵魂消耗、技能效果与冷却。
public class SoulOverdraftEvent extends PlayerEvent implements ICancellableEvent {
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
