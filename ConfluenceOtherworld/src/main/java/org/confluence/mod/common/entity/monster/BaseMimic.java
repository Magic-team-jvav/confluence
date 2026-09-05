package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.ModSoundEvents;
import org.mesdag.portlib.wrapper.common.extensions.IPortAttributesExtension;
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
    private int trackRepeats;
    private Vec3 trackingVelocity = Vec3.ZERO;

    public BaseMimic(EntityType<? extends BaseMimic> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 80.0).add(Attributes.ATTACK_DAMAGE, 15.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_POSE, (byte) MimicPose.CLOSED.ordinal());
        entityData.define(DATA_IDLE_ANGLE, -1);
    }

    @Override
    public void onAddedToWorld() {
        if (!level().isClientSide && entityData.get(DATA_IDLE_ANGLE) < 0)
            setIdleAngle(random.nextInt(4) * 90);
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
        return true;
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
        if (pose == MimicPose.CLOSED || pose == MimicPose.CLOSING) {
            setMimicPose(MimicPose.OPEN);
            resetAttackCycle();
        }
        tickAttack(target);
    }

    private void tickWithoutTarget() {
        navigation.stop();
        if (++targetMissingTicks < 20) return;
        setFollowRange(5.0);
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
        if (actionTicks > 0 && action != 8 && action != 9) {
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
            if (random.nextBoolean()) {
                action = 4;
                trackRepeats = getHealth() < getMaxHealth() * 0.5F ? 3 : 1;
                actionTicks = 8;
            } else {
                action = 9;
                actionTicks = 50;
                setGravity(0.0);
            }
            return;
        }
        if (action >= 4 && action <= 6) {
            if (!onGround()) return;
            launchAt(target, action == 6 ? 1.5 : 1.0, action == 6 ? 0.2 : 0.0);
            action++;
            actionTicks = 8;
            return;
        }
        if (action == 7) {
            trackingVelocity = Vec3.ZERO;
            setMimicPose(MimicPose.JUMPING);
            action = 8;
            actionTicks = 50;
            return;
        }
        if (action == 8) {
            trackTarget(target);
            if (--actionTicks > 0) return;
            setDeltaMovement(trackingVelocity.scale(0.5));
            setMimicPose(MimicPose.CLOSING);
            if (--trackRepeats > 0) {
                action = 7;
                actionTicks = 16;
            } else {
                action = 10;
                actionTicks = 8;
            }
            return;
        }
        if (action == 9) {
            Vec3 destination = target.position().add(0.0, 5.0, 0.0);
            Vec3 direction = destination.subtract(position());
            Vec3 acceleration = direction.scale(0.03);
            if (acceleration.lengthSqr() > 0.15 * 0.15) {
                acceleration = acceleration.normalize().scale(0.15);
            }
            Vec3 velocity = getDeltaMovement().add(acceleration);
            if (velocity.lengthSqr() > 1.0) {
                velocity = velocity.normalize();
            }
            if (direction.lengthSqr() < 2.0) {
                velocity = velocity.scale(0.8);
            }
            setDeltaMovement(velocity);
            if (--actionTicks <= 0) {
                setGravity(0.08);
                resetAttackCycle();
            }
            return;
        }
        if (action == 10) resetAttackCycle();
    }

    private void launchAt(LivingEntity target, double horizontalPower, double verticalBonus) {
        Vec3 horizontal = target.position().subtract(position()).multiply(1.0, 0.0, 1.0).normalize();
        jumpFromGround();
        addDeltaMovement(horizontal.scale(horizontalPower).add(0.0, verticalBonus, 0.0));
        hasImpulse = true;
        jumpAnimationTicks = 5;
        setMimicPose(MimicPose.JUMPING);
    }

    private void trackTarget(LivingEntity target) {
        Vec3 desired = target.position().subtract(position()).normalize().scale(5.0);
        Vec3 steering = desired.subtract(trackingVelocity);
        double steeringLength = steering.length();
        if (steeringLength > 0.1) steering = steering.scale(0.1 / steeringLength);
        trackingVelocity = trackingVelocity.add(steering);
        double speed = trackingVelocity.length();
        if (speed > 5.0) trackingVelocity = trackingVelocity.scale(5.0 / speed);
        trackingVelocity = trackingVelocity.scale(0.95);
        setDeltaMovement(trackingVelocity);
        if (trackingVelocity.lengthSqr() > 0.01) {
            float yaw = (float) Math.toDegrees(Math.atan2(-trackingVelocity.x, trackingVelocity.z));
            setYRot(yaw);
            setYHeadRot(yaw);
        }
    }

    private void resetAttackCycle() {
        action = 0;
        actionTicks = 15;
        trackingVelocity = Vec3.ZERO;
        setGravity(0.08);
        setMimicPose(MimicPose.OPEN);
    }

    private void setGravity(double gravity) {
        AttributeInstance attribute = getAttribute(IPortAttributesExtension.gravity().value());
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
        getLookControl().setLookAt(target, 90.0F, 85.0F);
        double dx = target.getX() - getX();
        double dz = target.getZ() - getZ();
        float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        setYRot(yaw);
        setYBodyRot(yaw);
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
        trackRepeats = 0;
        trackingVelocity = Vec3.ZERO;
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

    public enum MimicPose {
        CLOSED,
        OPEN,
        JUMPING,
        CLOSING
    }
}
