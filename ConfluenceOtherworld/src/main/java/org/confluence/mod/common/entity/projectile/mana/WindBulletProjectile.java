package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/// 风页（法书）释放的风弹。
///
/// 出手后先向上飞一段距离，随后自动锁定最近的敌人追踪过去；外观用粒子风暴拟合风球。
public class WindBulletProjectile extends AbstractManaProjectile {
    /// 向上飞行的时长（tick）。
    private static final int RISE_TICKS = 10;
    /// 上升时的垂直加速度；额外抬升约 3 格，落在需求的两~四格区间。
    private static final double RISE_ACCELERATION = 0.06;
    /// 追踪速度。
    private static final double HOMING_SPEED = 0.95;
    /// 追踪范围。
    private static final double HOMING_RANGE = 24.0;
    private static final int MAX_AGE = 160;

    private @Nullable UUID targetUuid;

    public WindBulletProjectile(EntityType<WindBulletProjectile> entityType, Level level) {
        super(entityType, level);
        withParticle(Confluence.asResource("wind_ball"));
    }

    public WindBulletProjectile(LivingEntity living) {
        this(ModEntities.WIND_BULLET.get(), living.level());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        Vec3 motion = getDeltaMovement();
        if (tickCount < RISE_TICKS) {
            setDeltaMovement(motion.x * 0.96, motion.y + RISE_ACCELERATION, motion.z * 0.96);
        } else {
            LivingEntity target = resolveTarget();
            if (target != null) {
                Vec3 direction = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0).subtract(position());
                if (direction.lengthSqr() > 1.0E-4) {
                    setDeltaMovement(direction.normalize().scale(HOMING_SPEED));
                }
            } else {
                setDeltaMovement(motion.scale(0.98));
            }
        }
        doSimpleMove();
        doAgeCheck(MAX_AGE);
    }

    private @Nullable LivingEntity resolveTarget() {
        if (targetUuid != null) {
            if (level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(targetUuid);
                if (entity instanceof LivingEntity living && living.isAlive() && LibEntityUtils.canHit(getOwner(), living)) {
                    return living;
                }
            }
            this.targetUuid = null;
        }
        LivingEntity nearest = null;
        double best = HOMING_RANGE * HOMING_RANGE;
        for (LivingEntity candidate : level().getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(HOMING_RANGE))) {
            if (candidate == getOwner() || !candidate.isAlive() || candidate.isSpectator()) continue;
            if (!LibEntityUtils.canHit(getOwner(), candidate)) continue;
            double distance = candidate.distanceToSqr(this);
            if (distance < best) {
                best = distance;
                nearest = candidate;
            }
        }
        if (nearest != null) this.targetUuid = nearest.getUUID();
        return nearest;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!level().isClientSide) discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (doPenetrateCheck(entity)) {
            doHurtAndKnockback(entity, 0.2, 0.1);
            doDiscardInMaxPenetrate(1);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (targetUuid != null) compound.putUUID("Target", targetUuid);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.targetUuid = compound.hasUUID("Target") ? compound.getUUID("Target") : null;
    }
}
