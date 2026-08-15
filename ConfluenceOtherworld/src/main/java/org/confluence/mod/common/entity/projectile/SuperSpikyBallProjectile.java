package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
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
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.util.TrapDamageHelper;

/**
 * 地牢机关使用的超级尖刺球弹幕。
 *
 * <p>它保留原有的难度伤害、死人毛衣减伤、刺伤来源、重力与弹性，并且没有普通尖刺球的七目标
 * 销毁预算。两种尖刺球只共享带版本校验的年龄存档格式，战斗和运动逻辑保持独立。</p>
 */
public class SuperSpikyBallProjectile extends Projectile implements Immunity, IAxisZRotate, IBouncy {
    private static final int MAXIMUM_SAVED_AGE = 1201;
    private static final int MAXIMUM_TRACKED_HIT_TARGETS = 0;

    public final Rotate rotate = new Rotate();
    private final SpikyBallRuntime runtime = new SpikyBallRuntime();

    public SuperSpikyBallProjectile(EntityType<SuperSpikyBallProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public SuperSpikyBallProjectile(Level level) {
        super(ModEntities.SUPER_SPIKY_BALL.get(), level);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        if (runtime.discardIfInvalid(this)) {
            return;
        }
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
            EntityHitResult result = ProjectileUtil.getEntityHitResult(level(), this, boundingBox.getMinPosition(), boundingBox.getMaxPosition(), boundingBox, this::canHitEntity, 0.5F);
            if (result != null) {
                Entity entity = result.getEntity();
                float damage = LibUtils.switchByDifficulty(level(), blockPosition(), 16, 32, 48);
                if (entity instanceof LivingEntity living) {
                    damage = TrapDamageHelper.applyDeadMansSweaterReduction(living, damage);
                }
                if (entity.hurt(ModDamageTypes.of(level(), DamageTypes.STING), damage)) {
                    LibEntityUtils.knockBackA2B(this, entity, 0.2, 0.04);
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
    public double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.tickCount = runtime.readFrom(compound, MAXIMUM_SAVED_AGE, MAXIMUM_TRACKED_HIT_TARGETS);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        runtime.writeTo(compound, tickCount, MAXIMUM_SAVED_AGE, MAXIMUM_TRACKED_HIT_TARGETS);
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
