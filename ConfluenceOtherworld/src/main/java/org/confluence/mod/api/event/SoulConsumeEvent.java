package org.confluence.mod.api.event;

import net.minecraftforge.eventbus.api.Cancelable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

@Cancelable
public class SoulConsumeEvent extends PlayerEvent {
    private final float originalAmount;
    private float amount;
    private float healthPerSoul;

    public SoulConsumeEvent(ServerPlayer player, float amount, float healthPerSoul) {
        super(player);
        this.originalAmount = amount;
        this.amount = amount;
        this.healthPerSoul = healthPerSoul;
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

    public float getHealthPerSoul() {
        return healthPerSoul;
    }

    public void setHealthPerSoul(float healthPerSoul) {
        this.healthPerSoul = healthPerSoul;
    }
}

