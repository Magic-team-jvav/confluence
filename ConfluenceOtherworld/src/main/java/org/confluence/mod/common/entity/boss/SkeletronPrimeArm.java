package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.PrimeCannonballProjectile;
import org.confluence.mod.common.entity.projectile.PrimeLaserProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 机械骷髅王手臂。4 种模式：0=锯子, 1=钳子, 2=加农炮, 3=激光。
public class SkeletronPrimeArm extends BaseBossPart<SkeletronPrime> implements GeoEntity {
    public static final int LASER = 0;
    public static final int SAW = 1;
    public static final int VICE = 2;
    public static final int CANNON = 3;

    private static final float MAX_PART_HEALTH = 2080.0F;
    private static final float PART_ARMOR = 26.0F;
    private static final String ARM_TYPE_TAG = "ArmType";
    private static final EntityDataAccessor<Integer> ARM_TYPE =
            SynchedEntityData.defineId(SkeletronPrimeArm.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int contactCooldown = 20;
    private int attackExecutions;
    private int rangedBehaviorTick;
    private int meleeBehaviorTick;
    private boolean previousOwnerSpinning;
    private Vec3 dashDirection = Vec3.ZERO;

    public SkeletronPrimeArm(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setMaster(SkeletronPrime master, int type) {
        if (type < LASER || type > CANNON) {
            throw new IllegalArgumentException("Arm type must be between 0 and 3");
        }
        bindTo(master);
        entityData.set(ARM_TYPE, type);
    }

    public int getArmType() {
        return entityData.get(ARM_TYPE);
    }

    public int getAttackExecutions() {
        return attackExecutions;
    }

    @Override
    protected void tickPart(SkeletronPrime master) {
        if (level().isClientSide) return;
        if (contactCooldown > 0) contactCooldown--;

        switch (getArmType()) {
            case LASER -> tickRangedArm(master, true);
            case SAW -> tickMeleeArm(master, false);
            case VICE -> tickMeleeArm(master, true);
            case CANNON -> tickRangedArm(master, false);
            default ->
                    throw new IllegalStateException("Unsupported Prime arm type " + getArmType());
        }
        damageContactTargets(master);
    }

    /// 按主体头部朝向旋转 1.21 的四个固定机械臂槽位，并以该职责原有速度跟随。
    /// 到达距离小于单刻速度时直接贴合，避免持续越过目标点造成抖动。
    private void followPinnedSlot(
            SkeletronPrime master, float distance, float speed) {
        Vec3 unitOffset = switch (getArmType()) {
            case LASER -> new Vec3(-1.0, 1.0, 0.0);
            case SAW -> new Vec3(-1.0, -1.0, 0.0);
            case VICE -> new Vec3(1.0, -1.0, 0.0);
            case CANNON -> new Vec3(1.0, 1.0, 0.0);
            default -> throw new IllegalStateException(
                    "Unsupported Prime arm type " + getArmType());
        };
        Vector3f rotatedOffset = unitOffset.scale(distance).toVector3f();
        new Quaternionf()
                .rotateY(-master.getYHeadRot() * Mth.DEG_TO_RAD)
                .transform(rotatedOffset);
        Vec3 targetPosition = master.position().add(
                new Vec3(rotatedOffset));
        Vec3 offset = targetPosition.subtract(position());
        if (offset.length() < speed) {
            setPos(targetPosition);
            setDeltaMovement(Vec3.ZERO);
            return;
        }
        setDeltaMovement(offset.normalize().scale(speed));
        moveToNextPosition();
    }

    /// 还原两条远程机械臂的行为树阶段：悬停时准备三十刻，主体旋转时
    /// 重新从十刻准备开始。阶段切换必须清空旧进度，不能继承上一分支的冷却。
    private void tickRangedArm(SkeletronPrime master, boolean laser) {
        LivingEntity target = master.getTarget();
        float distance = laser ? 6.0F : 7.0F;
        float followSpeed = laser ? 0.6F : 0.4F;
        if (target == null || !target.isAlive()) {
            followPinnedSlot(master, 6.0F, 1.0F);
            rangedBehaviorTick = 0;
            return;
        }
        boolean spinning = master.isSpinning();
        if (spinning != previousOwnerSpinning) {
            previousOwnerSpinning = spinning;
            rangedBehaviorTick = 0;
        }
        if (!spinning && distanceTo(master) > 15.0F) {
            followPinnedSlot(master, distance, laser ? 1.2F : 1.0F);
            rangedBehaviorTick = 0;
            return;
        }
        if (laser) {
            face(target.getEyePosition());
        } else {
            face(position().add(0.0, 10.0, 0.0));
        }
        followPinnedSlot(master, distance, followSpeed);
        int preparationTicks = spinning ? 10 : 30;
        if (++rangedBehaviorTick <= preparationTicks) {
            return;
        }
        boolean fired = laser
                ? shootLaser(master, target)
                : shootCannon(master, target);
        if (fired) {
            attackExecutions++;
        }
        rangedBehaviorTick = 0;
    }

    /// 生成一枚机械激光弹幕，并在创建或加入世界失败时完整回收实体。
    boolean shootLaser(
            SkeletronPrime master, LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        Vec3 origin =
                position().add(0.0, getBbHeight() * 0.5, 0.0);
        PrimeLaserProjectile laser =
                ModEntities.PRIME_LASER.get().create(level());
        if (laser == null) {
            return false;
        }
        laser.configure(master, origin, target, 8.0F);
        if (serverLevel.addFreshEntity(laser)) {
            return true;
        }
        laser.discard();
        return false;
    }

    private boolean shootCannon(
            SkeletronPrime master, LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        PrimeCannonballProjectile cannonball = ModEntities.PRIME_CANNONBALL.get().create(level());
        if (cannonball == null) {
            return false;
        }
        cannonball.configure(master, position().add(
                0.0, getBbHeight() * 0.5, 0.0), target);
        if (serverLevel.addFreshEntity(cannonball)) {
            return true;
        }
        cannonball.discard();
        return false;
    }

    /// 还原 1.21 两条近战机械臂的固定行为树时间轴。
    ///
    /// <p>非旋转阶段是三十刻准备，然后重复两次“五刻瞄准、十刻锁向冲刺、
    /// 三十刻回位”。旋转阶段则分别使用锯臂两轮、钳臂三轮的短回位序列。
    /// 冲刺方向只在每个十刻冲刺开始时锁定，不能因距离接近或目标横移提前结束。</p>
    private void tickMeleeArm(
            SkeletronPrime master, boolean vice) {
        LivingEntity target = master.getTarget();
        if (target == null || !target.isAlive()) {
            followPinnedSlot(master, 5.0F, 0.4F);
            meleeBehaviorTick = 0;
            dashDirection = Vec3.ZERO;
            return;
        }

        boolean spinning = master.isSpinning();
        if (spinning != previousOwnerSpinning) {
            previousOwnerSpinning = spinning;
            meleeBehaviorTick = 0;
            dashDirection = Vec3.ZERO;
        }
        if (!spinning && distanceTo(master) > 30.0F) {
            followPinnedSlot(master, 5.0F, 1.5F);
            meleeBehaviorTick = 0;
            dashDirection = Vec3.ZERO;
            return;
        }
        int cycleLength = spinning
                ? (vice ? 85 : 65)
                : 120;
        if (spinning) {
            tickSpinningMeleeTimeline(master, target, vice);
        } else {
            tickHoveringMeleeTimeline(master, target, vice);
        }
        meleeBehaviorTick++;
        if (meleeBehaviorTick >= cycleLength) {
            meleeBehaviorTick = 0;
        }
    }

    private void tickHoveringMeleeTimeline(
            SkeletronPrime master,
            LivingEntity target,
            boolean vice) {
        int tick = meleeBehaviorTick;
        if (tick < 30) {
            face(target.getEyePosition());
            followPinnedSlot(master, 5.0F, 0.4F);
        } else if (tick < 35) {
            face(target.getEyePosition());
            moveToNextPosition();
        } else if (tick < 45) {
            dashTowardLockedTarget(master, target,
                    tick == 35, vice ? 2.0F : 1.0F);
        } else if (tick < 75) {
            followPinnedSlot(master, 2.0F, vice ? 1.0F : 0.3F);
        } else if (tick < 80) {
            face(target.getEyePosition());
            moveToNextPosition();
        } else if (tick < 90) {
            dashTowardLockedTarget(master, target,
                    tick == 80, vice ? 2.0F : 1.0F);
        } else {
            followPinnedSlot(master, 2.0F, vice ? 1.0F : 0.3F);
        }
    }

    private void tickSpinningMeleeTimeline(
            SkeletronPrime master,
            LivingEntity target,
            boolean vice) {
        int tick = meleeBehaviorTick;
        int preparation = vice ? 10 : 15;
        int repeats = vice ? 3 : 2;
        int sequenceTick = tick - preparation;
        if (sequenceTick < 0) {
            face(target.getEyePosition());
            followPinnedSlot(master, 5.0F, 0.4F);
            return;
        }
        int repeatIndex = sequenceTick / 25;
        if (repeatIndex >= repeats) {
            return;
        }
        int localTick = sequenceTick % 25;
        if (localTick < 5) {
            face(target.getEyePosition());
            moveToNextPosition();
        } else if (localTick < 15) {
            dashTowardLockedTarget(
                    master,
                    target,
                    localTick == 5,
                    vice ? 2.5F : 1.5F);
        } else {
            followPinnedSlot(
                    master, 2.0F, vice ? 1.5F : 0.5F);
        }
    }

    private void dashTowardLockedTarget(
            SkeletronPrime master,
            LivingEntity target,
            boolean lockDirection,
            float speed) {
        if (lockDirection) {
            Vec3 direction = target.getEyePosition().subtract(position());
            dashDirection = direction.lengthSqr() <= 1.0E-9
                    ? getLookAngle()
                    : direction.normalize();
            attackExecutions++;
        }
        setDeltaMovement(dashDirection.scale(speed));
        face(position().add(dashDirection));
        moveToNextPosition();
    }

    private void face(Vec3 targetPosition) {
        Vec3 direction = targetPosition.subtract(position());
        if (direction.lengthSqr() <= 1.0E-9) {
            return;
        }
        double horizontal = direction.horizontalDistance();
        setYRot((float) (Mth.atan2(direction.z, direction.x)
                * Mth.RAD_TO_DEG) - 90.0F);
        setXRot((float) -(Mth.atan2(direction.y, horizontal)
                * Mth.RAD_TO_DEG));
    }

    /// 四条机械臂沿用 1.21 普通敌怪的接触攻击节奏。未碰到目标时十刻后复查，
    /// 命中或完成一次有效攻击尝试后等待二十刻；检测范围不额外膨胀。
    private void damageContactTargets(SkeletronPrime master) {
        if (contactCooldown > 0 || master.getTarget() == null) {
            return;
        }
        for (LivingEntity target : level().getEntitiesOfClass(
                LivingEntity.class,
                getBoundingBox(),
                living -> living.canBeSeenAsEnemy()
                        && !(living instanceof Enemy)
                        && master.canAttack(living))) {
            target.hurt(damageSources().mobAttack(master), 8.0F);
            contactCooldown = 20;
            return;
        }
        contactCooldown = 10;
    }

    private void moveToNextPosition() {
        Vec3 movement = getDeltaMovement();
        setPos(getX() + movement.x, getY() + movement.y, getZ() + movement.z);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        SkeletronPrime owner = getOwner();
        if (owner == null || !owner.isAlive() || isRemoved()
                || isInvulnerableTo(source)) {
            return false;
        }
        float appliedDamage = source.is(DamageTypeTags.BYPASSES_ARMOR)
                ? amount
                : CombatRules.getDamageAfterAbsorb(
                amount, PART_ARMOR, 0.0F);
        if (appliedDamage <= 0.0F) {
            return false;
        }
        float remaining = Math.max(
                0.0F, getPartHealth() - appliedDamage);
        setPartHealth(remaining);
        onPartHealthChanged(owner, remaining);
        if (remaining <= 0.0F) {
            onPartDestroyed(owner);
            discard();
        }
        return true;
    }

    @Override
    protected float getMaxPartHealth() {
        return MAX_PART_HEALTH;
    }

    @Override
    protected Class<SkeletronPrime> getOwnerType() {
        return SkeletronPrime.class;
    }

    @Override
    protected void onPartDestroyed(SkeletronPrime owner) {
        owner.onArmDestroyed(getArmType(), this);
    }

    @Override
    protected void onPartHealthChanged(SkeletronPrime owner, float remainingHealth) {
        owner.onArmHealthChanged(getArmType(), remainingHealth);
    }

    @Override
    protected void definePartSynchedData() {
        entityData.define(ARM_TYPE, LASER);
    }

    @Override
    protected void readPartSaveData(CompoundTag tag) {
        entityData.set(ARM_TYPE, Mth.clamp(tag.getInt(ARM_TYPE_TAG), LASER, CANNON));
        rangedBehaviorTick = 0;
        meleeBehaviorTick = 0;
        dashDirection = Vec3.ZERO;
    }

    @Override
    protected void addPartSaveData(CompoundTag tag) {
        tag.putInt(ARM_TYPE_TAG, getArmType());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
