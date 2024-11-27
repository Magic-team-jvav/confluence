package org.confluence.mod.common.item.sword.stagedy.projectile;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

/**
 * 这个文件夹定义弹幕的大体释放逻辑，参数可以在strategy调整
 * @author coffee
 */
public interface  AbstractProjContainer { // 剑气

    int getCooldown();

    float getBaseVelocity();

    SoundEvent getSound();

    Projectile getProjectile(Player player, ItemStack weapon);

    void genProjectile(Player owner, ItemStack weapon);

    float getVelocity(LivingEntity living);

    default int getAttackSpeed(LivingEntity living){
        int cooldown = getCooldown();
        AttributeInstance attributeInstance = living.getAttribute(Attributes.ATTACK_SPEED);
        if (attributeInstance != null) return Math.max(cooldown - (int) (attributeInstance.getValue() / 3.0), 0);
        return cooldown;
    }
}
