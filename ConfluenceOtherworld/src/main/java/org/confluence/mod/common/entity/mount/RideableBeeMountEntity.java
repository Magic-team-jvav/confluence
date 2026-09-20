package org.confluence.mod.common.entity.mount;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 蜜蜂坐骑。
///
/// 飞行速度、升力、重力、能量和水体限制全部在实体内直接维护，
/// 不再通过通用 locomotion 配置解释。
public final class RideableBeeMountEntity extends AbstractMountEntity implements GeoEntity {
    public static final float RENDER_SCALE = 1.15F;

    private static final double MAX_HORIZONTAL_SPEED = 0.225;
    private static final double HORIZONTAL_ACCELERATION = 0.09;
    private static final double MAX_VERTICAL_SPEED = MAX_HORIZONTAL_SPEED * 41.0 / 31.0;
    private static final double POWERED_LIFT = 0.035;
    private static final double GRAVITY = 0.03;
    private static final int MAX_FLIGHT_ENERGY = 214;
    private static final int REST_TICKS = 60;
    private static final double MOVING_RIDER_OFFSET = 0.1;
    private static final double STOPPED_RIDER_OFFSET = 0.4;
    private static final int LOWER_RIDER_DURATION = 12;
    private static final int RAISE_RIDER_DURATION = 7;

    private static final EntityDataAccessor<Integer> FLIGHT_ENERGY = SynchedEntityData.defineId(RideableBeeMountEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ASCENDING = SynchedEntityData.defineId(RideableBeeMountEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> FATIGUE = SynchedEntityData.defineId(RideableBeeMountEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> MAX_ENERGY = SynchedEntityData.defineId(RideableBeeMountEntity.class, EntityDataSerializers.INT);
    private static final RawAnimation WING = RawAnimation.begin().thenLoop("wing");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private int movingTicks;
    private int stoppedTicks;
    private boolean moving;

    public RideableBeeMountEntity(EntityType<? extends RideableBeeMountEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineMountSynchedData() {
        entityData.define(FLIGHT_ENERGY, MAX_FLIGHT_ENERGY);
        entityData.define(ASCENDING, false);
        entityData.define(FATIGUE, 0.0F);
        entityData.define(MAX_ENERGY, MAX_FLIGHT_ENERGY);
    }

    @Override
    public boolean canSummon(Player player) {
        if (!player.isInFluidType()) return true;
        var fluid = level().getFluidState(player.blockPosition());
        return player.getEyeInFluidType() == ForgeMod.EMPTY_TYPE.get() && player.canStandOnFluid(fluid)
                && level().getFluidState(player.blockPosition().above()).isEmpty()
                && player.getY() >= player.blockPosition().getY() + fluid.getHeight(level(), player.blockPosition()) - 0.4;
    }

    @Override
    protected void onInitialized(Player player) {
        int duration = (int) Math.ceil(MAX_FLIGHT_ENERGY * jumpMultiplier(player));
        for (int index = 0; index < 5; index++) {
            var jump = accessoryJump(player, index);
            duration += jump.getB() + (int) Math.ceil(jump.getA() / 0.08F);
        }
        entityData.set(MAX_ENERGY, duration);
        entityData.set(FLIGHT_ENERGY, player.onGround() ? duration : duration / 2);
    }

    @Override
    protected void tickRidden(Player player) {
        boolean fluidGround = standOnFluid(player, false);
        if (isInFluidType() && !fluidGround) {
            if (!level().isClientSide) {
                player.stopRiding();
                discard();
            }
            return;
        }

        double strafe = Mth.clamp(player.xxa, -1.0F, 1.0F);
        double forward = Mth.clamp(player.zza, -0.1F, 1.0F);
        boolean grounded = onGround() || fluidGround;
        float fatigue = entityData.get(FATIGUE);
        int energy = flightEnergy();
        int maximum = maximumFlightEnergy();
        if (grounded) {
            energy = maximum;
            int remainingRest = Math.round(fatigue * REST_TICKS);
            fatigue = remainingRest > 0 ? (remainingRest - 1) / (float) REST_TICKS : 0;
        } else {
            if (energy > 0) energy--;
            if (energy < maximum / 2) {
                float exhausted = 1.0F - energy / (maximum / 2.0F);
                if (exhausted > fatigue) fatigue = exhausted;
            }
        }
        entityData.set(FLIGHT_ENERGY, energy);
        entityData.set(FATIGUE, fatigue);
        double speed = grounded ? MAX_HORIZONTAL_SPEED * 10.0 / 31.0 : MAX_HORIZONTAL_SPEED * (1.0 - fatigue * 0.5);
        Vec3 velocity = accelerateHorizontal(player, strafe, forward, speed, HORIZONTAL_ACCELERATION);

        double vertical = velocity.y;
        if (isDescendInputDown() && !grounded) {
            vertical = Math.max(-MAX_VERTICAL_SPEED * 2, vertical - GRAVITY * 2);
        } else if (energy == 0) {
            /// 耗尽后只能滑降；按住跳跃不能再施加向上升力。
            vertical = Math.max(-MAX_VERTICAL_SPEED, Math.min(0, vertical) - GRAVITY);
        } else if (isJumpInputDown()) {
            vertical = Math.min(MAX_VERTICAL_SPEED * (1 - fatigue) - fatigue * 0.06, vertical + POWERED_LIFT);
        } else {
            vertical = grounded ? -GRAVITY : -fatigue * MAX_VERTICAL_SPEED;
        }

        moveWithVelocity(new Vec3(velocity.x, vertical, velocity.z));
        if (!level().isClientSide) entityData.set(ASCENDING, !onGround() && energy > 0);
        updateMovementState();
        if (!level().isClientSide && isAscending() && (tickCount & 1) == 0) {
            playSound(SoundEvents.BEEHIVE_WORK, 0.5F, 2.0F);
        }
    }

    private void updateMovementState() {
        moving = getDeltaMovement().horizontalDistanceSqr() > 0.001;
        if (moving && !isJumpInputDown()) {
            movingTicks++;
            stoppedTicks = 0;
        } else {
            movingTicks = 0;
            stoppedTicks++;
        }
    }

    public int flightEnergy() {
        return entityData.get(FLIGHT_ENERGY);
    }

    public int maximumFlightEnergy() {
        return entityData.get(MAX_ENERGY);
    }

    public boolean isAscending() {
        return entityData.get(ASCENDING);
    }

    @Override
    public float modifyRiderDamage(DamageSource source, float amount) {
        return source.is(DamageTypes.FALL) ? 0 : amount;
    }

    @Override
    public double getPassengersRidingOffset() {
        double offset = moving && !isJumpInputDown()
                ? Mth.lerp(Math.min(movingTicks / (double) LOWER_RIDER_DURATION, 1.0), STOPPED_RIDER_OFFSET, MOVING_RIDER_OFFSET)
                : Mth.lerp(Math.min(stoppedTicks / (double) RAISE_RIDER_DURATION, 1.0), MOVING_RIDER_OFFSET, STOPPED_RIDER_OFFSET);
        return super.getPassengersRidingOffset() + offset;
    }

    @Override
    protected void playEnterSound() {
        playSound(SoundEvents.BEEHIVE_EXIT, 1.0F, 1.0F);
    }

    @Override
    protected void playExitSound() {
        playSound(SoundEvents.BEEHIVE_ENTER, 1.0F, 1.0F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "wings", 2,
                        state -> isAscending() ? state.setAndContinue(WING) : PlayState.STOP),
                new AnimationController<>(this, "movement", 10, state -> {
                    if (moving) {
                        return state.setAndContinue(onGround() ? WALK : FLY);
                    }
                    return state.setAndContinue(IDLE);
                }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}
