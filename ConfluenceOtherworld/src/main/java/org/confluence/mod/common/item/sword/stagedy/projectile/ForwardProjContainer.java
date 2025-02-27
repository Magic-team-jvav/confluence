package org.confluence.mod.common.item.sword.stagedy.projectile;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
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
    public Projectile getProjectile(Player player, ItemStack weapon){
        return (entityType.create(player.level())).addAttackDamage(damage).addKnockBack(knockBack);
    }

    //生成位置
    public void genProjectile(Player owner, ItemStack weapon){
        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), getSound(), SoundSource.AMBIENT, 1.0F, 1.0F);
        Projectile projectile = getProjectile(owner,null);
        projectile.setOwner(owner);
        projectile.setPos(owner.getX(), owner.getY() + owner.getEyeHeight(), owner.getZ());
        projectile.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, getVelocity(owner), 0.0F);
        owner.level().addFreshEntity(projectile);
    }

}
