package org.confluence.mod.common.entity.mount;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 史莱姆坐骑。
///
/// <p>速度、跳跃、水中浮力、踩踏和座位高度都直接属于本实体。新增或调整
/// 史莱姆行为只改这里，避免为了一个坐骑来回维护多套外部参数和行为注册入口。</p>
public final class RideableSlimeMountEntity extends AbstractMountEntity implements GeoEntity {
    public static final float RENDER_SCALE = 2.4F;

    private static final double MAX_SPEED = 0.5;
    private static final double ACCELERATION = 0.12;
    private static final double JUMP_VELOCITY = 2.0;
    private static final double GRAVITY = 0.12;
    private static final float STOMP_DAMAGE = 5.0F;
    private static final double STOMP_RADIUS = 2.5;
    private static final int STOMP_COOLDOWN = 10;
    private static final double GROUNDED_RIDER_OFFSET = 0.55;
    private static final double MINIMUM_GROUNDED_RIDER_OFFSET = 0.35;
    private static final double AIRBORNE_RIDER_OFFSET = 0.62;

    private static final EntityDataAccessor<Boolean> JUMPING = SynchedEntityData.defineId(RideableSlimeMountEntity.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation JUMP = RawAnimation.begin().thenPlay("jump");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private boolean jumpQueued;
    private boolean jumpActive;
    private boolean groundStateInitialized;
    private boolean wasOnGround;
    private int movingTicks;
    private int airborneTicks;
    private long nextStompTick;

    public RideableSlimeMountEntity(
            EntityType<? extends RideableSlimeMountEntity> type,
            Level level
    ) {
        super(type, level);
    }

    @Override
    protected void defineMountSynchedData() {
        entityData.define(JUMPING, false);
    }

    @Override
    protected void tickRidden(Player player) {
        boolean groundedBeforeMove = onGround();
        double strafe = Mth.clamp(player.xxa, -1.0F, 1.0F);
        double forward = Mth.clamp(player.zza, -1.0F, 1.0F);
        if (groundedBeforeMove) {
            strafe *= 0.1;
            forward *= 0.2;
        } else {
            strafe *= 0.5;
        }

        Vec3 velocity = accelerateHorizontal(
                player, strafe, forward, MAX_SPEED, ACCELERATION);
        double vertical = groundedBeforeMove
                ? jumpQueued ? JUMP_VELOCITY : -GRAVITY
                : velocity.y - GRAVITY;
        if (jumpQueued && groundedBeforeMove) {
            jumpActive = true;
            entityData.set(JUMPING, true);
            jumpQueued = false;
        }
        if (isInWater()) {
            vertical = Math.min(
                    JUMP_VELOCITY,
                    vertical + (getRandom().nextFloat() < 0.8F
                            ? 0.2 : 0.1));
        }
        moveWithVelocity(new Vec3(velocity.x, vertical, velocity.z));
        updateGroundState(player);
        updateAnimationCounters();
    }

    private void updateGroundState(Player player) {
        boolean grounded = onGround();
        if (!groundStateInitialized) {
            groundStateInitialized = true;
            wasOnGround = grounded;
            return;
        }
        if (!level().isClientSide && jumpActive && !grounded && level().getGameTime() >= nextStompTick) {
            LivingEntity target = level().getEntitiesOfClass(
                            LivingEntity.class,
                            getBoundingBox().inflate(STOMP_RADIUS),
                            candidate -> candidate != player
                                    && candidate.isAlive()
                                    && candidate.isAttackable()
                                    && candidate.getY() < getY())
                    .stream()
                    .min(java.util.Comparator.comparingDouble(
                            candidate -> candidate.distanceToSqr(this)))
                    .orElse(null);
            if (target != null && target.hurt(
                    damageSources().playerAttack(player), STOMP_DAMAGE)) {
                Vec3 velocity = getDeltaMovement();
                setDeltaMovement(
                        velocity.x, JUMP_VELOCITY, velocity.z);
                nextStompTick =
                        level().getGameTime() + STOMP_COOLDOWN;
                playSound(
                        SoundEvents.SLIME_BLOCK_PLACE, 0.5F, 2.0F);
            }
        }
        if (!wasOnGround && grounded) {
            jumpActive = false;
            entityData.set(JUMPING, false);
            playSound(
                    SoundEvents.SLIME_SQUISH_SMALL, 0.7F, 1.0F);
        } else if (wasOnGround && !grounded) {
            playSound(
                    SoundEvents.SLIME_BLOCK_HIT, 0.5F, 1.0F);
        }
        wasOnGround = grounded;
    }

    private void updateAnimationCounters() {
        if (getDeltaMovement().horizontalDistanceSqr() > 0.001
                && !isJumpInputDown()) {
            movingTicks++;
        } else {
            movingTicks = 0;
        }
        if (entityData.get(JUMPING) && !onGround()) {
            airborneTicks++;
        } else {
            airborneTicks = 0;
        }
    }

    @Override
    protected void onJumpInputChanged(Player player, boolean jumping) {
        jumpQueued = jumping;
    }

    @Override
    public double getPassengersRidingOffset() {
        double base = super.getPassengersRidingOffset();
        if (!entityData.get(JUMPING)) {
            double phase =
                    (Math.cos(movingTicks * 0.6) - 1.0) * 0.3;
            return Math.max(
                    base + MINIMUM_GROUNDED_RIDER_OFFSET,
                    base + GROUNDED_RIDER_OFFSET
                            + Math.sin(phase) * 0.35);
        }
        double phase = Math.min(airborneTicks * 0.5, Math.PI);
        return base + AIRBORNE_RIDER_OFFSET
                + Math.sin(phase) * 0.3;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (jumpActive
                && !onGround()
                && attacker != null
                && source.is(DamageTypes.MOB_ATTACK)
                && attacker.getY() < getY() - 0.5) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected void playEnterSound() {
        playSound(SoundEvents.SLIME_BLOCK_FALL, 0.5F, 1.0F);
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers
    ) {
        controllers.add(new AnimationController<>(
                this, "movement", 0, state -> {
            if (entityData.get(JUMPING) && airborneTicks < 20) {
                return state.setAndContinue(JUMP);
            }
            if (getDeltaMovement().horizontalDistanceSqr() > 0.001) {
                return state.setAndContinue(WALK);
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}
