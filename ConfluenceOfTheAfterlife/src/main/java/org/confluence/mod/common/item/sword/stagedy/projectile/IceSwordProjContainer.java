package org.confluence.mod.common.item.sword.stagedy.projectile;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.projectile.IceBladeSwordProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.terra_curio.common.init.TCAttributes;


public class IceSwordProjContainer implements AbstractProjContainer {

    public int getCooldown() {
        return 10;
    }

    public float getBaseVelocity() {
        return 1.5f;
    }

    public SoundEvent getSound(){//默认冰雪剑声音
        return ModSoundEvents.FROZEN_ARROW.get();
    }

    public Projectile getProjectile(Player player, ItemStack weapon){//先做逻辑，后面补模型
        return new IceBladeSwordProjectile(player);
    }

    public void genProjectile(Player owner, ItemStack weapon){
        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), getSound(), SoundSource.AMBIENT, 1.0F, 1.0F);
        Projectile projectile = getProjectile(owner,null);
        projectile.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, getVelocity(owner), 0.0F);
        owner.level().addFreshEntity(projectile);
    }

    public float getVelocity(LivingEntity living) {
        float velocity = getBaseVelocity();
        AttributeInstance attributeInstance = living.getAttribute(TCAttributes.getRangedVelocity());
        if (attributeInstance != null) return velocity * (float) attributeInstance.getValue();
        return velocity;
    }

}
