package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.util.ModUtils;

import java.util.HashSet;
import java.util.Set;

public class SpikyBallProjectile extends Projectile implements Immunity {
    public float rotateO = 0.0F;
    public float rotate = 0.0F;
    private final Set<Entity> passThrough = new HashSet<>();

    public SpikyBallProjectile(EntityType<SpikyBallProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public SpikyBallProjectile(LivingEntity shooter) {
        super(ModEntities.SPIKY_BALL_PROJECTILE.get(), shooter.level());
        setPos(shooter.getX(), shooter.getEyeY() - 0.1F, shooter.getZ());
        setOwner(shooter);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        if (tickCount > 1200) {
            discard();
            return;
        }
        super.baseTick();

        Vec3 vec3 = getDeltaMovement();
        move(MoverType.SELF, vec3.add(0.0, -getDefaultGravity(), 0.0));
        Vec3 motion = getDeltaMovement();
        if (!vec3.equals(motion)) {
            if (motion.x != vec3.x) motion = new Vec3(-vec3.x, vec3.y, vec3.z);
            if (motion.y != vec3.y) motion = new Vec3(vec3.x, -vec3.y, vec3.z);
            if (motion.z != vec3.z) motion = new Vec3(vec3.x, vec3.y, -vec3.z);
        }
        setDeltaMovement(motion.scale(0.96).add(0.0, -getDefaultGravity(), 0.0));

        if (level().isClientSide) {
            float s = (float) getDeltaMovement().length();
            if (s > Mth.EPSILON + Mth.EPSILON + getDefaultGravity()) {
                float r = s / 0.125F;
                if (rotate > Mth.TWO_PI) this.rotate -= Mth.TWO_PI;
                this.rotateO = rotate;
                this.rotate += r / Mth.PI;
            } else {
                this.rotateO = rotate;
            }
        } else {
            AABB boundingBox = getBoundingBox().inflate(1.0);
            if (ProjectileUtil.getEntityHitResult(level(), this, boundingBox.getMinPosition(), boundingBox.getMaxPosition(), boundingBox, this::canHitEntity, 0.5F) instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                if (entity.hurt(damageSources().mobProjectile(this, getOwner() instanceof LivingEntity living ? living : null), 3.2F)) {
                    VectorUtils.knockBackA2B(this, entity, 0.1, 0.02);
                }
                if (passThrough.add(entity) && passThrough.size() >= 7) {
                    discard();
                }
            }
        }
    }

    @Override
    protected void updateRotation() {
        if (rotate != rotateO) {
            super.updateRotation();
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return ModUtils.canHitEntity(target, getOwner());
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.tickCount = compound.getInt("Age");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Age", tickCount);
    }

    @Override
    public Types confluence$getImmunityType() {
        return Types.STATIC;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return 15;
    }
}
