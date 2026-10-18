package org.confluence.mod.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.confluence.mod.common.soulcerer.skill.SoulSkillStack;

public class SoulSkillDamageEvent extends PlayerEvent {
    private final SoulSkillStack skillStack;
    private final float skillDamage;
    private final float weaponDamage;
    private float amount;

    public SoulSkillDamageEvent(Player player, SoulSkillStack skillStack, float skillDamage, float weaponDamage) {
        super(player);
        this.skillStack = skillStack;
        this.skillDamage = skillDamage;
        this.weaponDamage = weaponDamage;
        this.amount = skillDamage + weaponDamage;
    }

    public SoulSkillStack getSkillStack() {
        return skillStack;
    }

    public float getSkillDamage() {
        return skillDamage;
    }

    public float getWeaponDamage() {
        return weaponDamage;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}

