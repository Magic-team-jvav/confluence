package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import org.confluence.mod.common.entity.projectile.PrimeCannonballProjectile;
import org.confluence.mod.common.entity.projectile.PrimeLaserProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 机械骷髅王手臂。4 种模式：0=锯子, 1=钳子, 2=加农炮, 3=激光。
public class SkeletronPrimeArm extends BaseLivingBossPart<SkeletronPrime> implements GeoEntity {
    public static final int LASER = 0;
    public static final int SAW = 1;
    public static final int VICE = 2;
    public static final int CANNON = 3;

    /// 机械臂接触命中后再次造成伤害的间隔，单位 tick。
    private static final int CONTACT_COOLDOWN_TICKS = 20;
    /// 非旋转阶段锯臂与钳臂的攻击循环长度，单位 tick。
    private static final int HOVER_MELEE_CYCLE_TICKS = 120;
    /// 本体旋转时锯臂的攻击循环长度，单位 tick。
    private static final int SPIN_SAW_CYCLE_TICKS = 65;
    /// 本体旋转时钳臂的攻击循环长度，单位 tick。
    private static final int SPIN_VICE_CYCLE_TICKS = 85;
    /// 悬停攻击第一轮瞄准结束时刻，单位循环 tick。
    private static final int HOVER_FIRST_AIM_END = 30;
    /// 悬停攻击第一轮突进准备结束时刻，单位循环 tick。
    private static final int HOVER_FIRST_WINDUP_END = 35;
    /// 悬停攻击第一轮突进结束时刻，单位循环 tick。
    private static final int HOVER_FIRST_DASH_END = 45;
    /// 悬停攻击第二轮瞄准结束时刻，单位循环 tick。
    private static final int HOVER_SECOND_AIM_END = 75;
    /// 悬停攻击第二轮突进准备结束时刻，单位循环 tick。
    private static final int HOVER_SECOND_WINDUP_END = 80;
    /// 悬停攻击第二轮突进结束时刻，单位循环 tick。
    private static final int HOVER_SECOND_DASH_END = 90;
    /// 本体旋转时机械臂每次突进的周期，单位 tick。
    private static final int SPIN_ATTACK_PERIOD = 25;
    /// 旋转突进周期内的瞄准准备时长，单位 tick。
    private static final int SPIN_AIM_TICKS = 5;
    /// 旋转突进周期内的突进结束时刻，单位 tick。
    private static final int SPIN_DASH_END_TICKS = 15;

    /// 悬停阶段锯臂冲刺速度，单位格/tick。
    private static final float HOVER_SAW_DASH_SPEED = 1.0F;
    /// 悬停阶段钳臂冲刺速度，单位格/tick。
    private static final float HOVER_VICE_DASH_SPEED = 2.0F;
    /// 本体旋转时锯臂冲刺速度，单位格/tick。
    private static final float SPIN_SAW_DASH_SPEED = 1.5F;
    /// 本体旋转时钳臂冲刺速度，单位格/tick。
    private static final float SPIN_VICE_DASH_SPEED = 2.5F;

    private static final String ARM_TYPE_TAG = "ArmType";
    private static final EntityDataAccessor<Integer> ARM_TYPE = SynchedEntityData.defineId(SkeletronPrimeArm.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int contactCooldown = CONTACT_COOLDOWN_TICKS;
    private int rangedBehaviorTick;
    private int meleeBehaviorTick;
    private boolean previousOwnerSpinning;
    private Vec3 dashDirection = Vec3.ZERO;

    public SkeletronPrimeArm(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setMaster(SkeletronPrime master, int type) {
        if (type < LASER || type > CANNON) {
            throw new IllegalArgumentException("Arm type must be between 0 and 3");
        }
        bindTo(master);
        entityData.set(ARM_TYPE, type);
        Vec3 initialPosition = getPinnedSlotPosition(master, 6.0F);
        setPos(initialPosition.x, initialPosition.y, initialPosition.z);
    }

    public int getArmType() {
        return entityData.get(ARM_TYPE);
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

    /// 按主体头部朝向旋转四个固定机械臂槽位，并以各自职责的速度跟随。
    /// 到达距离小于单刻速度时直接贴合，避免持续越过目标点造成抖动。
    private void followPinnedSlot(SkeletronPrime master, float distance, float speed) {
        Vec3 targetPosition = getPinnedSlotPosition(master, distance);
        Vec3 offset = targetPosition.subtract(position());
        if (offset.length() < speed) {
            setPos(targetPosition);
            setDeltaMovement(Vec3.ZERO);
            return;
        }
        setDeltaMovement(offset.normalize().scale(speed));
        moveToNextPosition();
    }

    private Vec3 getPinnedSlotPosition(SkeletronPrime master, float distance) {
        Vec3 unitOffset = switch (getArmType()) {
            case LASER -> new Vec3(-1.0, 1.0, 0.0);
            case SAW -> new Vec3(-1.0, -1.0, 0.0);
            case VICE -> new Vec3(1.0, -1.0, 0.0);
            case CANNON -> new Vec3(1.0, 1.0, 0.0);
            default ->
                    throw new IllegalStateException("Unsupported Prime arm type " + getArmType());
        };
        Vector3f rotatedOffset = unitOffset.scale(distance * master.getScale()).toVector3f();
        new Quaternionf().rotateY(-master.getYHeadRot() * Mth.DEG_TO_RAD).transform(rotatedOffset);
        return master.position().add(new Vec3(rotatedOffset));
    }

    /// 两条远程机械臂悬停准备三十刻，主体旋转时
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
        if (laser) {
            shootLaser(master, target);
        } else {
            shootCannon(master, target);
        }
        rangedBehaviorTick = 0;
    }

    /// 生成一枚机械激光弹幕，并在创建或加入世界失败时完整回收实体。
    private void shootLaser(SkeletronPrime master, LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        Vec3 origin = position().add(0.0, getBbHeight() * 0.5, 0.0);
        PrimeLaserProjectile laser = ModEntities.PRIME_LASER.get().create(level());
        if (laser == null) return;
        laser.configure(master, origin, target, (float) master.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (!serverLevel.addFreshEntity(laser)) laser.discard();
    }

    private void shootCannon(SkeletronPrime master, LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        PrimeCannonballProjectile cannonball = ModEntities.PRIME_CANNONBALL.get().create(level());
        if (cannonball == null) return;
        cannonball.configure(master, position().add(0.0, getBbHeight() * 0.5, 0.0), target);
        if (!serverLevel.addFreshEntity(cannonball)) cannonball.discard();
    }

    /// 两条近战机械臂使用固定攻击时间轴。
    ///
    /// 非旋转阶段是三十刻准备，然后重复两次“五刻瞄准、十刻锁向冲刺、
    /// 三十刻回位”。旋转阶段则分别使用锯臂两轮、钳臂三轮的短回位序列。
    /// 冲刺方向只在每个十刻冲刺开始时锁定，不能因距离接近或目标横移提前结束。
    private void tickMeleeArm(SkeletronPrime master, boolean vice) {
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
                ? (vice ? SPIN_VICE_CYCLE_TICKS : SPIN_SAW_CYCLE_TICKS)
                : HOVER_MELEE_CYCLE_TICKS;
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

    private void tickHoveringMeleeTimeline(SkeletronPrime master, LivingEntity target, boolean vice) {
        int tick = meleeBehaviorTick;
        if (tick < HOVER_FIRST_AIM_END) {
            face(target.getEyePosition());
            followPinnedSlot(master, 5.0F, 0.4F);
        } else if (tick < HOVER_FIRST_WINDUP_END) {
            face(target.getEyePosition());
            moveToNextPosition();
        } else if (tick < HOVER_FIRST_DASH_END) {
            dashTowardLockedTarget(master, target,
                    tick == HOVER_FIRST_WINDUP_END, vice ? HOVER_VICE_DASH_SPEED : HOVER_SAW_DASH_SPEED);
        } else if (tick < HOVER_SECOND_AIM_END) {
            followPinnedSlot(master, 2.0F, vice ? 1.0F : 0.3F);
        } else if (tick < HOVER_SECOND_WINDUP_END) {
            face(target.getEyePosition());
            moveToNextPosition();
        } else if (tick < HOVER_SECOND_DASH_END) {
            dashTowardLockedTarget(master, target,
                    tick == HOVER_SECOND_WINDUP_END, vice ? HOVER_VICE_DASH_SPEED : HOVER_SAW_DASH_SPEED);
        } else {
            followPinnedSlot(master, 2.0F, vice ? 1.0F : 0.3F);
        }
    }

    private void tickSpinningMeleeTimeline(SkeletronPrime master, LivingEntity target, boolean vice) {
        int tick = meleeBehaviorTick;
        int preparation = vice ? 10 : 15;
        int repeats = vice ? 3 : 2;
        int sequenceTick = tick - preparation;
        if (sequenceTick < 0) {
            face(target.getEyePosition());
            followPinnedSlot(master, 5.0F, 0.4F);
            return;
        }
        int repeatIndex = sequenceTick / SPIN_ATTACK_PERIOD;
        if (repeatIndex >= repeats) {
            return;
        }
        int localTick = sequenceTick % SPIN_ATTACK_PERIOD;
        if (localTick < SPIN_AIM_TICKS) {
            face(target.getEyePosition());
            moveToNextPosition();
        } else if (localTick < SPIN_DASH_END_TICKS) {
            dashTowardLockedTarget(master, target, localTick == SPIN_AIM_TICKS, vice ? SPIN_VICE_DASH_SPEED : SPIN_SAW_DASH_SPEED);
        } else {
            followPinnedSlot(master, 2.0F, vice ? 1.5F : 0.5F);
        }
    }

    private void dashTowardLockedTarget(SkeletronPrime master, LivingEntity target, boolean lockDirection, float speed) {
        if (lockDirection) {
            Vec3 direction = target.getEyePosition().subtract(position());
            dashDirection = direction.lengthSqr() <= 1.0E-9
                    ? getLookAngle()
                    : direction.normalize();
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
        LibEntityUtils.updateEntityRotation(this, direction);
    }

    /// 四条机械臂逐刻检查接触，成功造成伤害后冷却二十刻，检测范围不额外膨胀。
    private void damageContactTargets(SkeletronPrime master) {
        if (contactCooldown > 0 || master.getTarget() == null) {
            return;
        }
        for (Entity target : SweptContactAttack.findTargets(this, 0.0D,
                SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE,
                entity -> entity instanceof LivingEntity living && living.canBeSeenAsEnemy()
                        && !(living instanceof Enemy) && master.canAttack(living))) {
            if (target.hurt(damageSources().mobAttack(master), (float) getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                contactCooldown = CONTACT_COOLDOWN_TICKS;
                return;
            }
        }
    }

    private void moveToNextPosition() {
        Vec3 movement = getDeltaMovement();
        setPos(getX() + movement.x, getY() + movement.y, getZ() + movement.z);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        SkeletronPrime owner = getOwner();
        if (owner == null || !owner.isAlive() || isRemoved() || isInvulnerableTo(source)) {
            return false;
        }
        if (!super.hurt(source, amount)) return false;
        float remaining = getHealth();
        indicateHurt();
        onPartHealthChanged(owner, remaining);
        if (remaining <= 0.0F) {
            onPartDestroyed(owner);
        }
        return true;
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
    protected void definePartSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ARM_TYPE, LASER);
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
