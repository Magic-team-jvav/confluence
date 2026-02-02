package org.confluence.terraentity.api.event;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class WhipRegisterModifyEvent extends Event implements IModBusEvent {
    public static float damageFactor = 0.5f;
    private float damage;
    private float markDamage;
    private float attackSpeed;
    private int cooldown;
    private float range;
    private String name;

    public WhipRegisterModifyEvent(float damage, float markDamage, float attackSpeed, int cooldown, float range, String name) {
        this.damage = damage;
        this.markDamage = markDamage;
        this.attackSpeed = attackSpeed;
        this.cooldown = cooldown;
        this.range = range;
        this.name = name;

    }

    public float getDamage() {
        return damage;
    }

    public float getMarkDamage() {
        return markDamage;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public int getCooldown() {
        return cooldown;
    }

    public float getRange() {
        return range;
    }

    public String getName() {
        return name;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setMarkDamage(float markDamage) {
        this.markDamage = markDamage;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public void setName(String name) {
        this.name = name;
    }
}
