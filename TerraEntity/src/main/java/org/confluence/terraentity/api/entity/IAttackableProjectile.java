package org.confluence.terraentity.api.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.projectile.Projectile;

/**
 * 很多弹幕命中后会消失
 * @param <T> 弹幕类型
 */
public interface IAttackableProjectile{

    default boolean canBeAttacked(){
        return false;
    }

    static boolean canHit(Entity projectile, DamageSource damageSource){
        if(projectile instanceof IAttackableProjectile projectile1){
            if(!projectile1.canBeAttacked()){
                return false;
            }
            Projectile projectile2 = (Projectile) projectile;
            if(damageSource.getDirectEntity() == projectile2.getOwner() ||
                    projectile2.getOwner() instanceof OwnableEntity ownableEntity && ownableEntity.getOwner() == damageSource.getDirectEntity()){
                return false;
            }
            return true;
        }
        return false;
    }

    static boolean tryHit(Entity projectile, DamageSource damageSource){
        if(canHit(projectile, damageSource)){
            projectile.hurt(projectile.damageSources().genericKill(),10);
            return true;
        }
        return false;
    }

}
