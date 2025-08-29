package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModParticleTypes;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GrassSwordProjectile extends SwordProjectile {

    float yawSpeed;
    float pitchSpeed;
    protected static final EntityDataAccessor<Float> DATA_YAW = SynchedEntityData.defineId(GrassSwordProjectile.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_PITCH = SynchedEntityData.defineId(GrassSwordProjectile.class, EntityDataSerializers.FLOAT);

    public GrassSwordProjectile(EntityType<GrassSwordProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        this.canPenalize = true;
        hitCount = 99999;
        this.collisionProperties = new CollisionProperties(10,10,1);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data){
        super.onSyncedDataUpdated(data);
        if(level().isClientSide) {
            if (data == DATA_YAW) {
                this.yawSpeed = this.entityData.get(DATA_YAW);
            }else if (data == DATA_PITCH) {
                this.pitchSpeed = this.entityData.get(DATA_PITCH);
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_YAW, 0.0f);
        builder.define(DATA_PITCH, 0.0f);
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount > 5) {
            Vec3 motion = getDeltaMovement();
            float yaw = (float) (Math.PI / 2-Math.atan2(motion.z, motion.x));
            float pitch = (float) (-Math.atan2(motion.y,
                    Math.sqrt(motion.x * motion.x + motion.z * motion.z)));
            Quaternionf q = new Quaternionf()
                    .rotateY(yaw)
                    .rotateX(pitch)
                    .rotateX(-0.1f)
                    .rotateY(yawSpeed);

            Vec3 transformed = new Vec3(q.transform(new Vector3f(0, 0, 1)));
            setDeltaMovement(transformed);

        }
        if(tickCount % 2 == 0 && level().isClientSide){
            level().addParticle(ModParticleTypes.LEAVES.get(), getX(), getY(), getZ(), 0, 0, 0);
        }
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        super.shootFromRotation(shooter, x, y, z, velocity, inaccuracy);
        this.yawSpeed = (float) (Math.random() * 0.5f + 0.5f);
        this.pitchSpeed = (float) (-Math.random() * 0.05f - 0.05f);
        this.entityData.set(DATA_YAW, yawSpeed);
        this.entityData.set(DATA_PITCH, pitchSpeed);

    }

}
