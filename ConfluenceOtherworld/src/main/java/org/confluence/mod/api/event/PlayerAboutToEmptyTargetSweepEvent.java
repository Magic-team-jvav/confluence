package org.confluence.mod.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class PlayerAboutToEmptyTargetSweepEvent extends PlayerEvent {
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
