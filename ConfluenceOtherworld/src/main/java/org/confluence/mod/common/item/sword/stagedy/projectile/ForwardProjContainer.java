package org.confluence.mod.common.item.sword.stagedy.projectile;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.projectile.sword.ForwardSwordProjectile;
import org.confluence.mod.common.init.ModSoundEvents;


public class ForwardProjContainer extends AbstractProjContainer {

    private EntityType<ForwardSwordProjectile> entityType;
    public ForwardProjContainer(EntityType<ForwardSwordProjectile> entityType, int damage, float knockBack, int cooldown, float velocity) {
        super(damage,knockBack,cooldown, velocity, ModSoundEvents.FROZEN_ARROW.get());
        this.entityType = entityType;
    }

    public ForwardProjContainer(EntityType<ForwardSwordProjectile> entityType, int damage, float knockBack, int cooldown, float velocity, SoundEvent sound) {
        super(damage,knockBack,cooldown, velocity, sound);
        this.entityType = entityType;
    }

    //弹幕本身不带伤害，由容器定义弹幕伤害
    public Projectile getProjectile(LivingEntity living, ItemStack weapon){
        return (entityType.create(living.level())).addAttackDamage(damage).addKnockBack(knockBack);
    }

    //生成位置
    public void genProjectile(LivingEntity living, ItemStack weapon){
        living.level().playSound(null, living.getX(), living.getY(), living.getZ(), getSound(), SoundSource.AMBIENT, 1.0F, 1.0F);
        Projectile projectile = getProjectile(living,null);
        projectile.setOwner(living);
        projectile.setPos(living.getX(), living.getY() + living.getEyeHeight(), living.getZ());
        projectile.shootFromRotation(living, living.getXRot(), living.getYRot(), 0.0F, getVelocity(living), 0.0F);
        living.level().addFreshEntity(projectile);
    }

}
