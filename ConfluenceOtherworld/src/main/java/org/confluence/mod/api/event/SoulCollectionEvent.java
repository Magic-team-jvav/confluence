package org.confluence.mod.api.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class SoulCollectionEvent extends PlayerEvent implements ICancellableEvent {
    private final LivingEntity victim;
    private final float originalAmount;
    private float amount;

    public SoulCollectionEvent(ServerPlayer player, LivingEntity victim, float amount) {
        super(player);
        this.victim = victim;
        this.originalAmount = amount;
        this.amount = amount;
    }

    public LivingEntity getVictim() {
        return victim;
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
