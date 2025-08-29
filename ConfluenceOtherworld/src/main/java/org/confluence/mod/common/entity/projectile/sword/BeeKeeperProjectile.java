package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.terraentity.api.entity.ITrackType;
import org.confluence.terraentity.registries.track.variant.BasisTrack;
import org.confluence.terraentity.registries.track.variant.SimpleTrack;
import org.confluence.terraentity.utils.TEUtils;

import java.util.Optional;

public class BeeKeeperProjectile extends SwordProjectile {

    ITrackType trackType;

    public BeeKeeperProjectile(EntityType<? extends SwordProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        this.lifetime = 100;
        trackType = new BasisTrack(90, 0.3f);

    }

    @Override
    public void tick() {
        super.tick();
        if(getOwner() != null) {
            if (tickCount < 5) {
                this.addDeltaMovement(direction);
            } else if (tickCount < 10) {
                setDeltaMovement(getDeltaMovement().scale(0.8f));
            } else {
                LivingEntity target = TEUtils.getAABBAngleTarget(position(), position().add(getDeltaMovement().normalize().scale(1)), level(), getOwner(), 10, 180, this::canHitEntity);
                if (target != null) {
                    Vec3 motion = getDeltaMovement();
                    Vec3 dir = target.position().add(0, target.getEyeHeight() * 0.5f, 0).subtract(position());
                    double angle = TEUtils.angleBetween(motion, dir);
                    if (angle < 90 && !(trackType instanceof SimpleTrack)) {
                        trackType = new SimpleTrack(90, 0.5, 0.5, Optional.of(0.5), 0.5);
                    }
                    Vec3 movement = trackType.calDeltaMovement(getDeltaMovement(), dir, angle);
                    setDeltaMovement(movement);
                }
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (tickCount <= 10) {
            return false;
        }
        return target instanceof Enemy && super.canHitEntity(target) && TEUtils.projectileCanHurtEntityTest.test(this,target);
    }
    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if(getOwner() != null) {
            this.direction = new Vec3(Math.sin(Math.random() * 2 * Math.PI) * 0.001f, 0.002f * Math.random() + 0.004f , Math.cos(Math.random() * 2 * Math.PI) * 0.001f);
            this.entityData.set(DATA_DIRECTION, direction.toVector3f());
        }
        this.knockBack = 0;
        this.baseKnockBack = 0;
    }

    @Override
    public DamageSource damageSource(){
        if(getOwner() instanceof LivingEntity living)
            return ModDamageTypes.of(level(), ModDamageTypes.SWORD_PROJECTILE, living, this); // 取消无敌帧
        else return damageSources().magic();
    }

}