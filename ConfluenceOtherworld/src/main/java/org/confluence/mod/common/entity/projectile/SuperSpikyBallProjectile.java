package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.util.TrapDamageHelper;
import org.confluence.mod.mixed.Immunity;

public class SuperSpikyBallProjectile extends Projectile implements Immunity, IAxisZRotate, IBouncy {
    public final Rotate rotate = new Rotate();

    public SuperSpikyBallProjectile(EntityType<SuperSpikyBallProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public SuperSpikyBallProjectile(Level level) {
        super(ModEntities.SUPER_SPIKY_BALL_PROJECTILE.get(), level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        if (tickCount > 1200) {
            discard();
            return;
        }
        super.tick();
        updateRotation();

        bounce(this::move, this::getDeltaMovement, this::setDeltaMovement, getDefaultGravity(), 0.99);

        if (level().isClientSide) {
            rotateZ(rotate, this, 0.125F);
        } else {
            AABB boundingBox = getBoundingBox().inflate(1.0);
            if (ProjectileUtil.getEntityHitResult(level(), this, boundingBox.getMinPosition(), boundingBox.getMaxPosition(), boundingBox, this::canHitEntity, 0.5F) instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                float damage = LibUtils.switchByDifficulty(level(), blockPosition(), 16, 32, 48);
                if (entity instanceof LivingEntity living) {
                    damage = TrapDamageHelper.applyDeadMansSweaterReduction(living, damage);
                }
                if (entity.hurt(ModDamageTypes.of(level(), DamageTypes.STING), damage)) {
                    VectorUtils.knockBackA2B(this, entity, 0.2, 0.04);
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
        return LibUtils.canHitEntity(target, getOwner());
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
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
