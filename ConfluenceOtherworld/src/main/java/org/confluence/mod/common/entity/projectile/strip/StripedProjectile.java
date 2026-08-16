package org.confluence.mod.common.entity.projectile.strip;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.projectile.DamageSettableProjectile;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.ModDamageTypes;

/// 由一个移动头段和若干短寿命身体段组成的长条形弹幕。
///
/// <p>头段沿发射方向移动，并按固定距离留下身体段；头段达到最大距离后消失，身体段停留在生成位置，
/// 在短时间内持续提供接触伤害。免疫类型应由数据生成器登记为 {@code static}，避免同一段弹幕在极短时间内
/// 重复触发目标的受伤无敌。</p>
///
/// <p>本类只保存 1.20 重写后的运行时格式。恢复时如果战斗快照或运行时字段损坏，弹幕会进入安全失效状态，
/// 服务端销毁实体，不回退到玩家当前手持物。</p>
///
/// @see org.confluence.mod.common.data.gen.data_map.ImmunitySubProvider
public abstract class StripedProjectile extends DamageSettableProjectile {
    protected static final EntityDataAccessor<Boolean> DATA_IS_HEAD = SynchedEntityData.defineId(
            StripedProjectile.class,
            EntityDataSerializers.BOOLEAN);
    protected double distForHeadRemove = 10.0;
    protected double distForCreateBody = 0.95;
    protected int ticksForBodyRemove = 28;
    protected Vec3 startPos = Vec3.ZERO;
    protected double distO = -0.5;
    protected float[] rot;

    public StripedProjectile(EntityType<? extends StripedProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public StripedProjectile(EntityType<? extends StripedProjectile> entityType, LivingEntity living) {
        this(entityType, living, new Vec3(living.getX(), living.getEyeY() - 0.1, living.getZ()));
    }

    public StripedProjectile(EntityType<? extends StripedProjectile> entityType, LivingEntity living, Vec3 pos) {
        this(entityType, living.level());
        setOwner(living);
        setNoGravity(true);
        setPos(pos);
        this.startPos = pos;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_HEAD, true);
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldAbortSubclassTick()) {
            return;
        }
        if (isHead()) {
            Vec3 vec3 = getDeltaMovement();

            if (!level().isClientSide && getOwner() instanceof LivingEntity living) {
                HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
                if (hitresult.getType() == HitResult.Type.BLOCK) {
                    onHitBlock((BlockHitResult) hitresult);
                } else if (hitresult.getType() == HitResult.Type.ENTITY) {
                    onHitEntity((EntityHitResult) hitresult);
                }
                double dist = position().distanceTo(startPos);
                double delta = dist - distO;
                if (delta >= distForCreateBody) {
                    if (dist > distForHeadRemove) {
                        onRemove();
                    } else {
                        ProjectileCombatSnapshot snapshot = getProjectileCombatSnapshot();
                        if (snapshot == null) {
                            combatState().invalidate("Striped projectile head is missing its combat snapshot");
                            combatState().discardIfInvalid(this);
                            return;
                        }
                        StripedProjectile body = createBody(living);
                        body.setDeltaMovement(vec3);
                        body.setHead(false);
                        // 身体段是头段派生出的同一条弹幕，必须沿用同一份冻结属性与暴击结果。
                        body.setProjectileCombatSnapshot(snapshot);
                        body.setDefaultVelocity(getDefaultVelocity());
                        level().addFreshEntity(body);
                        this.distO = dist;
                    }
                }
            }

            checkInsideBlocks();
            double offX = getX() + vec3.x;
            double offY = getY() + vec3.y;
            double offZ = getZ() + vec3.z;
            setPos(offX, offY, offZ);
        } else if (!level().isClientSide) {
            if (tickCount > ticksForBodyRemove) {
                onRemove();
            } else {
                AABB boundingBox = getBoundingBox().inflate(1.0);
                EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
                        level(),
                        this,
                        boundingBox.getMinPosition(),
                        boundingBox.getMaxPosition(),
                        boundingBox,
                        this::canHitEntity,
                        0.5F);
                checkInsideBlocks();
                if (hitResult != null) {
                    onTouchEntity(hitResult);
                }
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity.hurt(getDamageSource(), getCalculatedDamage())) {
            combatState().recordSuccessfulHit(ProjectileHitRules.impactedEntity(entity).getUUID());
            LibEntityUtils.knockBackA2B(this, entity, 0.5, 0.2);
        }
    }

    public DamageSource getDamageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
    }

    protected void onRemove() {
        discard();
    }

    protected abstract void onTouchEntity(EntityHitResult result);

    protected abstract StripedProjectile createBody(LivingEntity shooter);

    public float[] getRot() {
        if (rot == null) {
            updateRotation();
            this.rot = new float[]{getYRot() * Mth.DEG_TO_RAD, getXRot() * Mth.DEG_TO_RAD};
        }
        return rot;
    }

    public boolean isHead() {
        return entityData.get(DATA_IS_HEAD);
    }

    public void setHead(boolean is) {
        entityData.set(DATA_IS_HEAD, is);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        StripedProjectileRuntime.write(
                compound,
                startPos,
                distO,
                tickCount,
                isHead(),
                ticksForBodyRemove,
                distForHeadRemove
        );
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (combatState().isInvalid()) {
            return;
        }
        try {
            StripedProjectileRuntime.State runtimeState = StripedProjectileRuntime.read(
                    compound, ticksForBodyRemove, distForHeadRemove);
            this.startPos = runtimeState.startPos();
            this.distO = runtimeState.spacingProgress();
            this.tickCount = runtimeState.age();
            this.setHead(runtimeState.head());
            this.rot = null;
        } catch (RuntimeException exception) {
            this.startPos = Vec3.ZERO;
            this.distO = -0.5;
            this.tickCount = 0;
            this.setHead(false);
            this.rot = null;
            combatState().invalidate(StripedProjectileRuntime.englishReason(exception));
        }
    }
}
