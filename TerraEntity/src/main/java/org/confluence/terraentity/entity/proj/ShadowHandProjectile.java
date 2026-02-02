package org.confluence.terraentity.entity.proj;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.utils.TEUtils;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ShadowHandProjectile extends BaseProj<ShadowHandProjectile> implements GeoEntity {

    final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    LivingEntity target;
    Vec3 direction;
    private static final EntityDataAccessor<Vector3f> DATA_DIRECTION = SynchedEntityData.defineId(ShadowHandProjectile.class, EntityDataSerializers.VECTOR3);

    public ShadowHandProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DIRECTION, new Vector3f(0, 0, 0));

    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if(data == DATA_DIRECTION && this.level().isClientSide) {
            this.direction = new Vec3(this.entityData.get(DATA_DIRECTION));
//            this.lookAt(EntityAnchorArgument.Anchor.EYES, this.direction);
            float[] rots = TEUtils.dirToRot(this.direction);
            this.setYRot(rots[0]);
            this.setXRot(rots[1]);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if(this.tickCount ==1 ){
            if(this.getOwner() != null && this.getOwner() instanceof Mob mob) {
                if (mob.getTarget() != null) {
                    this.target = mob.getTarget();
                    this.direction = target.position().subtract(this.position()).normalize();
                    this.entityData.set(DATA_DIRECTION, direction.toVector3f());
                    float[] rots = TEUtils.dirToRot(this.direction);
                    this.setYRot(rots[0]);
                    this.setXRot(rots[1]);
                }
            }
        }

        if(this.direction != null && this.tickCount > 10 &&  this.tickCount < 25) {
            this.setDeltaMovement(this.direction);
        }else{
            this.setDeltaMovement(this.getDeltaMovement().scale(0.6f));
        }

    }

    @Override
    public int getLifetime() {
        return 40;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


}
