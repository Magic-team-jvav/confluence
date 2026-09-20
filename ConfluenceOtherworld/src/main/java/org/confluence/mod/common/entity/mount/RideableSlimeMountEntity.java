package org.confluence.mod.common.entity.mount;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.util.PrefixUtils;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 史莱姆坐骑。
///
/// 速度、跳跃、水中浮力、踩踏和座位高度都直接属于本实体。新增或调整
/// 史莱姆行为只改这里，避免为了一个坐骑来回维护多套外部参数和行为注册入口。
public final class RideableSlimeMountEntity extends AbstractMountEntity implements GeoEntity {
    public static final float RENDER_SCALE = 2.4F;

    private static final double MAX_SPEED = 0.5;
    private static final double ACCELERATION = 0.12;
    private static final double JUMP_VELOCITY = 2.0;
    private static final double GRAVITY = 0.12;
    private static final float STOMP_DAMAGE = 40.0F;
    private static final double GROUNDED_RIDER_OFFSET = 0.2;
    private static final double AIRBORNE_RIDER_OFFSET = 0.2;

    private static final EntityDataAccessor<Boolean> JUMPING = SynchedEntityData.defineId(RideableSlimeMountEntity.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation JUMP = RawAnimation.begin().thenPlay("jump");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private boolean groundStateInitialized;
    private boolean wasOnGround;
    private int movingTicks;
    private int airborneTicks;

    public RideableSlimeMountEntity(EntityType<? extends RideableSlimeMountEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineMountSynchedData() {
        entityData.define(JUMPING, false);
    }

    @Override
    protected void tickRidden(Player player) {
        standOnFluid(player, true);
        boolean groundedBeforeMove = onGround();
        double strafe = Mth.clamp(player.xxa, -1.0F, 1.0F);
        double forward = Mth.clamp(player.zza, -1.0F, 1.0F);
        if (groundedBeforeMove) {
            strafe *= 0.1;
            forward *= 0.2;
        } else {
            strafe *= 0.5;
        }

        Vec3 velocity = accelerateHorizontal(player, strafe, forward, MAX_SPEED, ACCELERATION);
        double vertical = groundedBeforeMove ? isJumpInputDown() ? JUMP_VELOCITY * jumpMultiplier(player) : -GRAVITY : velocity.y - GRAVITY;
        if (isJumpInputDown() && groundedBeforeMove) {
            entityData.set(JUMPING, true);
        }
        if (isInFluidType() && !groundedBeforeMove) {
            vertical = Math.min(0.3, vertical + 0.2);
        }
        vertical = accessoryJumpVelocity(player, vertical, groundedBeforeMove && isJumpInputDown());
        AABB previousBox = getBoundingBox();
        moveWithVelocity(new Vec3(velocity.x, vertical, velocity.z));
        if (!level().isClientSide && vertical < 0 && !groundedBeforeMove)
            stomp(player, previousBox);
        updateGroundState();
        updateAnimationCounters();
    }

    private void updateGroundState() {
        boolean grounded = onGround();
        if (!groundStateInitialized) {
            groundStateInitialized = true;
            wasOnGround = grounded;
            return;
        }
        if (!wasOnGround && grounded) {
            entityData.set(JUMPING, false);
            if (!level().isClientSide) {
                playSound(SoundEvents.SLIME_SQUISH_SMALL, 0.7F, 1.0F);
            }
        } else if (wasOnGround && !grounded) {
            if (!level().isClientSide) {
                playSound(SoundEvents.SLIME_BLOCK_HIT, 0.5F, 1.0F);
            }
        }
        wasOnGround = grounded;
    }

    private void updateAnimationCounters() {
        if (getDeltaMovement().horizontalDistanceSqr() > 0.001 && !isJumpInputDown()) {
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

    private void stomp(Player player, AABB previousBox) {
        float damage = STOMP_DAMAGE * (float) PrefixUtils.attributeWithoutHeldItem(player, LibAttributes.getSummonDamage(), player.getMainHandItem());
        DamageSource source = LibDamageTypes.of(level(), LibDamageTypes.SUMMONER, player);
        boolean hit = false;
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, previousBox.minmax(getBoundingBox()).inflate(0.1),
                target -> ProjectileHitRules.canHit(player, target))) {
            double top = target.getBoundingBox().maxY;
            if (previousBox.minY >= top - 0.1 && getY() <= top + 0.1 && target.hurt(source, damage)) {
                hit = true;
                target.knockback(0.5, getX() - target.getX(), getZ() - target.getZ());
            }
        }
        if (hit) {
            Vec3 velocity = getDeltaMovement();
            setDeltaMovement(velocity.x, JUMP_VELOCITY, velocity.z);
            entityData.set(JUMPING, true);
            protectRider();
            player.fallDistance = 0;
            playSound(SoundEvents.SLIME_BLOCK_PLACE, 0.5F, 2.0F);
        }
    }

    @Override
    public float modifyRiderDamage(DamageSource source, float amount) {
        return super.modifyRiderDamage(source, source.is(DamageTypes.FALL) ? amount * 0.5F : amount);
    }

    @Override
    public double getPassengersRidingOffset() {
        double base = super.getPassengersRidingOffset();
        if (!entityData.get(JUMPING)) {
            double phase = (Math.cos(movingTicks * 0.6) - 1.0) * 0.3;
            return base + GROUNDED_RIDER_OFFSET + Math.sin(phase) * 0.6;
        }
        double phase = Math.min(airborneTicks * 0.5, Math.PI);
        return base + AIRBORNE_RIDER_OFFSET + Math.sin(phase) * 0.5;
    }

    @Override
    protected void playEnterSound() {
        playSound(SoundEvents.SLIME_BLOCK_FALL, 0.5F, 1.0F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 0, state -> {
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
