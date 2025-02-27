package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModParticleTypes;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class LightBaneProjectile extends SwordProjectile {

    public Vec3 direction;
    public static final EntityDataAccessor<Vector3f> DATA_DIRECTION = SynchedEntityData.defineId(LightBaneProjectile.class, EntityDataSerializers.VECTOR3);

    public LightBaneProjectile(EntityType<LightBaneProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        hitCount = 99999;
        if(!level().isClientSide()){
            direction = new Vec3(this.getRandom().nextFloat() - 0.5f, this.getRandom().nextFloat() - 0.5f, this.getRandom().nextFloat() - 0.5f);
//            direction = new Vec3(1,0,0);
            this.entityData.set(DATA_DIRECTION, direction.toVector3f());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(direction!=null) {
            float f = 10;
            float control = Math.min(Math.abs(tickCount - f), f) * (tickCount < f? -0.02f : 0.02f);
            this.setDeltaMovement(direction.normalize().scale(control));
            lookAt(EntityAnchorArgument.Anchor.EYES , getEyePosition().subtract(direction));
        }
    }

    @Override
    protected boolean doHurt(Entity living) {
        if(super.doHurt(living)){
            ((ServerLevel) level()).sendParticles(ModParticleTypes.LIGHT_BANE.get(),getX(),getY(),getZ(),1,0,0,0,0);
            return true;
        }
        return false;
    }

    public DamageSource damageSource(){
        return damageSources().magic();
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DIRECTION, new Vector3f());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (this.level().isClientSide() && DATA_DIRECTION.equals(key)) {
            direction =new Vec3(this.entityData.get(DATA_DIRECTION));
            float yaw = (float) Math.atan2(direction.x, direction.z) * (180F / (float) Math.PI);
            this.setYRot(yaw);
            yRotO = yaw;
        }

    }

    @Nullable
    protected ParticleOptions getTrailParticle() {
        return random.nextBoolean() ? ModParticleTypes.LIGHT_BANE_FADE.get() : ModParticleTypes.LIGHT_BANE_DUST.get();
    }
}
