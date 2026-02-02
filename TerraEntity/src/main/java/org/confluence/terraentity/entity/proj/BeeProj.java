package org.confluence.terraentity.entity.proj;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.ITrackType;
import org.confluence.terraentity.registries.track.variant.BasisTrack;
import org.confluence.terraentity.registries.track.variant.SimpleTrack;
import org.confluence.terraentity.utils.TEUtils;

import java.util.Optional;

public class BeeProj extends BaseProj<BeeProj> {

    ITrackType trackType;

    public BeeProj(EntityType<? extends BeeProj> entityType, Level pLevel) {
        super(entityType, pLevel);
        trackType = new BasisTrack(90, 0.3f);
        this.setTexture(TerraEntity.space("textures/entity/bee_projectile.png"));
    }

    public int getLifetime(){
        return 100;
    }

    @Override
    public void tick() {
        super.tick();
        if(getOwner() != null) {
            if (tickCount < 5) {
                this.addDeltaMovement(new Vec3(0, 0.03, 0));
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
                }else{
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.5f));
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
    }

    protected void doKnockBack(LivingEntity entity) {

    }
    @Override
    public DamageSource getDamageSource(LivingEntity hurter){
        if(getOwner() != null && getOwner() instanceof LivingEntity living){
            return damageSources().mobProjectile(this, living);
        }
        return this.damageSources().generic();
    }

}