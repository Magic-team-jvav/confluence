package org.confluence.mod.common.entity.mount;

import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
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

/// 蜜蜂坐骑。
///
/// <p>飞行速度、升力、重力、能量和水体限制全部在实体内直接维护，
/// 不再通过通用 locomotion 配置解释。</p>
public final class RideableBeeMountEntity extends AbstractMountEntity implements GeoEntity {
    public static final float RENDER_SCALE = 1.15F;

    private static final double MAX_HORIZONTAL_SPEED = 0.225;
    private static final double HORIZONTAL_ACCELERATION = 0.09;
    private static final double MAX_VERTICAL_SPEED = 0.2;
    private static final double POWERED_LIFT = 0.035;
    private static final double EXHAUSTED_LIFT = 0.02;
    private static final double GRAVITY = 0.03;
    private static final int MAX_FLIGHT_ENERGY = 200;
    private static final int GROUND_RECOVERY = 5;
    private static final String PLAYER_FLIGHT_ENERGY = "confluence.rideable_bee.flight_energy";
    private static final double MOVING_RIDER_OFFSET = 0.1;
    private static final double STOPPED_RIDER_OFFSET = 0.4;
    private static final int LOWER_RIDER_DURATION = 12;
    private static final int RAISE_RIDER_DURATION = 7;

    private static final EntityDataAccessor<Integer> FLIGHT_ENERGY = SynchedEntityData.defineId(RideableBeeMountEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ASCENDING = SynchedEntityData.defineId(RideableBeeMountEntity.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation WING = RawAnimation.begin().thenLoop("wing");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private boolean energyLoaded;
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
    }

    @Override
    protected void tickRidden(Player player) {
        if (!level().isClientSide && !energyLoaded) {
            int energy = player.getPersistentData().contains(PLAYER_FLIGHT_ENERGY, Tag.TAG_INT)
                    ? Mth.clamp(player.getPersistentData().getInt(PLAYER_FLIGHT_ENERGY), 0, MAX_FLIGHT_ENERGY)
                    : MAX_FLIGHT_ENERGY;
            setFlightEnergy(player, energy);
            energyLoaded = true;
        }
        if (isInWater()) {
            if (!level().isClientSide) {
                player.stopRiding();
                discard();
            }
            return;
        }

        double strafe = Mth.clamp(player.xxa, -1.0F, 1.0F);
        double forward = Mth.clamp(player.zza, -0.1F, 1.0F);
        if (onGround()) {
            strafe *= 0.04;
            forward *= 0.15;
        } else {
            strafe *= 0.25;
        }
        Vec3 velocity = accelerateHorizontal(player, strafe, forward, MAX_HORIZONTAL_SPEED, HORIZONTAL_ACCELERATION);

        int energy = flightEnergy();
        double vertical = velocity.y;
        if (isJumpInputDown()) {
            vertical = Math.min(MAX_VERTICAL_SPEED, vertical + (energy > 0 ? POWERED_LIFT : EXHAUSTED_LIFT));
            if (!level().isClientSide && energy > 0) {
                setFlightEnergy(player, energy - 1);
            }
        } else {
            vertical = Math.max(-MAX_VERTICAL_SPEED, vertical - GRAVITY);
        }
        if (!level().isClientSide && onGround()) {
            setFlightEnergy(player, Math.min(MAX_FLIGHT_ENERGY, flightEnergy() + GROUND_RECOVERY));
        }

        moveWithVelocity(new Vec3(velocity.x, vertical, velocity.z));
        updateMovementState();
        if (!level().isClientSide && isJumpInputDown() && (tickCount & 1) == 0) {
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

    private void setFlightEnergy(Player player, int energy) {
        int bounded = Mth.clamp(energy, 0, MAX_FLIGHT_ENERGY);
        entityData.set(FLIGHT_ENERGY, bounded);
        if (!level().isClientSide) {
            player.getPersistentData().putInt(PLAYER_FLIGHT_ENERGY, bounded);
        }
    }

    public int flightEnergy() {
        return entityData.get(FLIGHT_ENERGY);
    }

    public int maximumFlightEnergy() {
        return MAX_FLIGHT_ENERGY;
    }

    public boolean isAscending() {
        return entityData.get(ASCENDING);
    }

    @Override
    protected void onJumpInputChanged(Player player, boolean jumping) {
        if (!level().isClientSide) {
            entityData.set(ASCENDING, jumping && !isInWater());
        }
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
                        return state.setAndContinue(isJumpInputDown() ? FLY : WALK);
                    }
                    return state.setAndContinue(IDLE);
                }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}
