package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.ModSoundEvents;
import org.mesdag.portlib.wrapper.world.entity.projectile.PortProjectileDeflection;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 宝箱怪共用的开合、跳跃和困难模式特殊攻击状态机。
public class BaseMimic extends BaseMonster {
    private static final String IDLE_ANGLE_TAG = "MimicIdleAngle";
    private static final EntityDataAccessor<Byte> DATA_POSE = SynchedEntityData.defineId(BaseMimic.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> DATA_IDLE_ANGLE = SynchedEntityData.defineId(BaseMimic.class, EntityDataSerializers.INT);
    private static final RawAnimation CLOSED = RawAnimation.begin().thenLoop("Closed state");
    private static final RawAnimation OPEN = RawAnimation.begin().thenPlayAndHold("Open");
    private static final RawAnimation JUMP = RawAnimation.begin().thenPlayAndHold("Jump");
    private static final RawAnimation CLOSE = RawAnimation.begin().thenPlay("Closed");
    private static final MimicPose[] POSES = MimicPose.values();

    private int action;
    private int actionTicks;
    private int jumpAnimationTicks;
    private int targetMissingTicks;

    public BaseMimic(EntityType<? extends BaseMimic> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_POSE, (byte) MimicPose.CLOSED.ordinal());
        entityData.define(DATA_IDLE_ANGLE, -1);
    }

    @Override
    public void onAddedToWorld() {
        if (!level().isClientSide) {
            setFollowRange(isHardmodeVariant() ? 5.0 : 6.25);
            if (entityData.get(DATA_IDLE_ANGLE) < 0) setIdleAngle(random.nextInt(4) * 90);
        }
        super.onAddedToWorld();
    }

    /// 宝箱怪由实体状态机推进，行为树只保留调度槽位。
    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(20);
            }
        };
    }

    @Override
    protected boolean mustSeePlayerTarget() {
        return !isHardmodeVariant();
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    /// 宝箱怪的跳跃、追踪冲刺和悬浮攻击属于自身移动技能，落地不能反过来伤害施法者。
    /// 在公共基类处理可覆盖全部普通与困难模式变体，也避免特殊攻击结束时残留的
    /// {@code fallDistance} 因瞬间恢复重力而结算摔落伤害。
    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected double contactAttackInflation() {
        return 0.5;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive() || target.isSpectator() || target.level() != level()) {
            if (target != null) setTarget(null);
            tickWithoutTarget();
            return;
        }
        targetMissingTicks = 0;
        setFollowRange(16.0);
        MimicPose pose = getMimicPose();
        if (action != 7 && (pose == MimicPose.CLOSED || pose == MimicPose.CLOSING)) {
            setMimicPose(MimicPose.OPEN);
            resetAttackCycle();
        }
        tickAttack(target);
    }

    private void tickWithoutTarget() {
        navigation.stop();
        if (targetMissingTicks == 0) {
            resetAttackState();
            setMimicPose(MimicPose.OPEN);
        }
        if (++targetMissingTicks < 20) return;
        setFollowRange(isHardmodeVariant() ? 5.0 : 6.25);
        MimicPose pose = getMimicPose();
        if (targetMissingTicks == 20) {
            setGravity(0.08);
            setDeltaMovement(0.0, 0.3, 0.0);
            setIdleAngle(random.nextInt(4) * 90);
            setMimicPose(MimicPose.CLOSING);
            actionTicks = 6;
        } else if (pose == MimicPose.CLOSING && --actionTicks <= 0) {
            setMimicPose(MimicPose.CLOSED);
        } else if (pose == MimicPose.CLOSED) {
            snapToIdleAngle();
        }
    }

    private void tickAttack(LivingEntity target) {
        if (jumpAnimationTicks > 0 && --jumpAnimationTicks == 0 && getMimicPose() == MimicPose.JUMPING) {
            setMimicPose(MimicPose.OPEN);
        }
        lookAtTarget(target);
        if (actionTicks > 0 && action != 7 && action != 8 && action != 9) {
            actionTicks--;
            return;
        }
        if (action <= 2) {
            if (!onGround()) return;
            launchAt(target, action == 2 ? 1.5 : 1.0, action == 2 ? 0.5 : 0.0);
            actionTicks = 15;
            action++;
            return;
        }
        if (!isHardmodeVariant()) {
            action = 0;
            actionTicks = 15;
            return;
        }
        tickHardmodeAttack(target);
    }

    private void tickHardmodeAttack(LivingEntity target) {
        if (action == 3) {
            if (!onGround()) return;
            switch (random.nextInt(3)) {
                case 0 -> {
                    action = 4;
                    actionTicks = 8;
                }
                case 1 -> {
                    action = 7;
                    actionTicks = stateParameters(CombatState.DEFENDING).duration();
                    setDeltaMovement(Vec3.ZERO);
                    setMimicPose(MimicPose.CLOSED);
                }
                default -> {
                    action = 8;
                    actionTicks = stateParameters(CombatState.RISING).duration();
                    noPhysics = true;
                    setGravity(0.0);
                    setMimicPose(MimicPose.JUMPING);
                }
            }
            return;
        }
        if (action >= 4 && action <= 6) {
            if (!onGround()) return;
            launchAt(target, action == 6 ? 1.5 : 1.0, action == 6 ? 0.2 : 0.0);
            action = action == 6 ? 10 : action + 1;
            actionTicks = 8;
            return;
        }
        if (action == 7) {
            if (!onGround()) {
                resetAttackCycle();
                return;
            }
            setDeltaMovement(Vec3.ZERO);
            if (--actionTicks <= 0) resetAttackCycle();
            return;
        }
        if (action == 8) {
            Vec3 destination = target.position().add(0.0, 5.0, 0.0);
            Vec3 direction = destination.subtract(position());
            Vec3 velocity = getDeltaMovement().scale(0.75).add(direction.normalize().scale(0.2));
            if (velocity.lengthSqr() > 1.0) velocity = velocity.normalize();
            setDeltaMovement(velocity);
            if (--actionTicks > 0 && direction.lengthSqr() > 1.0) return;
            action = 9;
            actionTicks = stateParameters(CombatState.SLAMMING).duration();
            noPhysics = true;
            setGravity(0.16);
            setDeltaMovement(0.0, -stateParameters(CombatState.SLAMMING).behavior().chargeSpeed(), 0.0);
            return;
        }
        if (action == 9) {
            if (getY() <= target.getY() + target.getBbHeight() && level().noCollision(this))
                noPhysics = false;
            setDeltaMovement(getDeltaMovement().x * 0.5, Math.min(getDeltaMovement().y,
                    -stateParameters(CombatState.SLAMMING).behavior().chargeSpeed()), getDeltaMovement().z * 0.5);
            if (onGround() || --actionTicks <= 0) resetAttackCycle();
            return;
        }
        if (action == 10) resetAttackCycle();
    }

    private void launchAt(LivingEntity target, double horizontalPower, double verticalBonus) {
        horizontalPower = stateParameters(CombatState.JUMPING).behavior().chargeSpeedOr(horizontalPower);
        Vec3 horizontal = target.position().subtract(position()).multiply(1.0, 0.0, 1.0).normalize();
        jumpFromGround();
        addDeltaMovement(horizontal.scale(horizontalPower).add(0.0, verticalBonus, 0.0));
        hasImpulse = true;
        jumpAnimationTicks = 5;
        setMimicPose(MimicPose.JUMPING);
    }

    private void resetAttackCycle() {
        action = 0;
        actionTicks = 15;
        noPhysics = false;
        setGravity(0.08);
        setMimicPose(MimicPose.OPEN);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (action == 7 && onGround() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
            return false;
        boolean damaged = super.hurt(source, amount);
        if (damaged && !level().isClientSide && getTarget() == null) {
            if (source.getEntity() instanceof LivingEntity attacker && canAttack(attacker)) {
                setTarget(attacker);
            } else {
                LivingEntity nearest = level().getNearestPlayer(this, 16.0);
                if (nearest != null && canAttack(nearest)) setTarget(nearest);
            }
        }
        return damaged;
    }

    /// 困难模式拟态怪闭合时免疫伤害；专家及大师模式还会反射可反射的投射物。
    @Override
    public PortProjectileDeflection deflection(Projectile projectile) {
        return action == 7 && onGround() && isHardmodeVariant() && LibUtils.isAtLeastExpert(level(), blockPosition())
                ? PortProjectileDeflection.REVERSE
                : PortProjectileDeflection.NONE;
    }

    private void setGravity(double gravity) {
        AttributeInstance attribute = getAttribute(Attributes.GRAVITY.value());
        if (attribute != null && attribute.getBaseValue() != gravity)
            attribute.setBaseValue(gravity);
    }

    private void setFollowRange(double range) {
        AttributeInstance attribute = getAttribute(Attributes.FOLLOW_RANGE);
        if (attribute != null && attribute.getBaseValue() != range) attribute.setBaseValue(range);
    }

    protected boolean isHardmodeVariant() {
        return true;
    }

    private void lookAtTarget(LivingEntity target) {
        double dx = target.getX() - getX();
        double dz = target.getZ() - getZ();
        float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        setYRot(yaw);
        setYBodyRot(yaw);
        setYHeadRot(yaw);
    }

    private void snapToIdleAngle() {
        int angle = entityData.get(DATA_IDLE_ANGLE);
        if (angle < 0) return;
        setYRot(angle);
        setYBodyRot(angle);
        setYHeadRot(angle);
    }

    private void setIdleAngle(int angle) {
        entityData.set(DATA_IDLE_ANGLE, Math.floorMod(angle, 360) / 90 * 90);
        snapToIdleAngle();
    }

    private void setMimicPose(MimicPose pose) {
        if (getMimicPose() != pose) entityData.set(DATA_POSE, (byte) pose.ordinal());
    }

    public MimicPose getMimicPose() {
        int id = Byte.toUnsignedInt(entityData.get(DATA_POSE));
        return id < POSES.length ? POSES[id] : MimicPose.CLOSED;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(IDLE_ANGLE_TAG, entityData.get(DATA_IDLE_ANGLE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(IDLE_ANGLE_TAG, Tag.TAG_INT)) setIdleAngle(tag.getInt(IDLE_ANGLE_TAG));
        setMimicPose(MimicPose.CLOSED);
        resetAttackState();
    }

    private void resetAttackState() {
        action = 0;
        actionTicks = 0;
        jumpAnimationTicks = 0;
        targetMissingTicks = 0;
        noPhysics = false;
        setGravity(0.08);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 5, state -> state.setAndContinue(switch (getMimicPose()) {
            case CLOSED -> CLOSED;
            case OPEN -> OPEN;
            case JUMPING -> JUMP;
            case CLOSING -> CLOSE;
        })));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.SOUL_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.METAL_HURT.get();
    }

    public enum CombatState {JUMPING, DEFENDING, RISING, SLAMMING}

    public enum MimicPose {
        CLOSED,
        OPEN,
        JUMPING,
        CLOSING
    }
}
