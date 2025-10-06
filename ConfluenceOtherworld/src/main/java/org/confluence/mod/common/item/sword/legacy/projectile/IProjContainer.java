package org.confluence.mod.common.item.sword.legacy.projectile;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.api.entity.IGeneration;

/**
 * 使用{@link IGeneration}和{@link org.confluence.mod.common.component.SwordProjectileComponent}替换
 * @author coffee
 */
@Deprecated
public interface IProjContainer { // 剑气

    int getCooldown();

    float getBaseVelocity();

    SoundEvent getSound();

    Projectile getProjectile(LivingEntity player, ItemStack weapon);

    void genProjectile(LivingEntity owner, ItemStack weapon);

    default float getVelocity(LivingEntity living) {
        float velocity = getBaseVelocity();
        AttributeInstance attributeInstance = living.getAttribute(TCAttributes.getRangedVelocity());
        if (attributeInstance != null) return velocity * (float) attributeInstance.getValue();
        return velocity;
    }

    default int getAttackSpeed(LivingEntity living){
        int cooldown = getCooldown();
        AttributeInstance attributeInstance = living.getAttribute(Attributes.ATTACK_SPEED);
        if (attributeInstance != null) return Math.max(cooldown - (int) (attributeInstance.getValue() / 3.0), 0);
        return cooldown;
    }
}
