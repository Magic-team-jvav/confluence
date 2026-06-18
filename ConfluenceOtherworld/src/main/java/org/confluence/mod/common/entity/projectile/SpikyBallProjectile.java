package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.common.entitiy.IAxisZRotate;
import org.confluence.lib.common.entitiy.IBouncy;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.mixed.Immunity;

import java.util.HashSet;
import java.util.Set;

public class SpikyBallProjectile extends Projectile implements Immunity, IAxisZRotate, IBouncy {
    public final Rotate rotate = new Rotate();
    private final Set<Entity> passThrough = new HashSet<>();

    public SpikyBallProjectile(EntityType<SpikyBallProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public SpikyBallProjectile(LivingEntity shooter) {
        super(ModEntities.SPIKY_BALL.get(), shooter.level());
        setPos(shooter.getX(), shooter.getEyeY() - 0.1F, shooter.getZ());
        setOwner(shooter);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        if (tickCount > 1596) {
            discard();
            return;
        }
        super.tick();
        updateRotation();

        bounce(this::move, this::getDeltaMovement, this::setDeltaMovement, getDefaultGravity(), 0.96);

        if (level().isClientSide) {
            rotateZ(rotate, this, 0.125F);
        } else {
            AABB boundingBox = getBoundingBox().inflate(1.0);
            if (ProjectileUtil.getEntityHitResult(level(), this, boundingBox.getMinPosition(), boundingBox.getMaxPosition(), boundingBox, this::canHitEntity, 0.5F) instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                if (entity.hurt(damageSources().mobProjectile(this, getOwner() instanceof LivingEntity living ? living : null), 3.2F)) {
                    LibEntityUtils.knockBackA2B(this, entity, 0.1, 0.02);
                }
                if (passThrough.add(entity) && passThrough.size() >= 7) {
                    discard();
                }
            }
        }
    }

    @Override
    protected void updateRotation() {
        if (rotate.different()) {
            super.updateRotation();
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return LibEntityUtils.canHitEntity(target, getOwner());
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
    public Type confluence$getImmunityType() {
        return Type.STATIC;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return 15;
    }
}
