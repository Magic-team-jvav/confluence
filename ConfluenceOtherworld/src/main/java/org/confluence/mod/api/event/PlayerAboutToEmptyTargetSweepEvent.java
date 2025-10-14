package org.confluence.mod.api.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PlayerAboutToEmptyTargetSweepEvent extends PlayerEvent implements ICancellableEvent {
    private final float baseAttackDamage;
    private float attackDamage;
    public PlayerAboutToEmptyTargetSweepEvent(Player player, float baseAttackDamage) {
        super(player);
        this.baseAttackDamage = baseAttackDamage;
        this.attackDamage = baseAttackDamage;
    }

    public float getBaseAttackDamage() {
        return baseAttackDamage;
    }

    public void setAttackDamage(float attackDamage) {
        this.attackDamage = attackDamage;
    }

    public float getAttackDamage() {
        return attackDamage;
    }
}
