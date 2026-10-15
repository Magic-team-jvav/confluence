package org.confluence.mod.api.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/// 服务端普通敌怪的收集收益修正；Boss 本体补满不触发此事件。
public class SoulCollectionEvent extends PlayerEvent implements ICancellableEvent {
    private final LivingEntity victim;
    private final DamageSource source;
    private final float originalAmount;
    private float amount;

    public SoulCollectionEvent(ServerPlayer player, LivingEntity victim, DamageSource source, float amount) {
        super(player);
        this.victim = victim;
        this.source = source;
        this.originalAmount = amount;
        this.amount = amount;
    }

    public LivingEntity getVictim() {
        return victim;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getOriginalAmount() {
        return originalAmount;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
