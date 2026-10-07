package org.confluence.mod.common.entity.mount;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
import software.bernie.geckolib.util.GeckoLibUtil;

public class RideableUnicornMountEntity extends AbstractMountEntity implements GeoEntity {
    private static final float BODY_TURN_BLEND = 0.4F;
    private static final float MAX_BODY_TURN = 18.0F;
    private static final float MAX_HEAD_TURN = 45.0F;
    private static final float BANK_BLEND = 0.3F;
    private static final float BANK_PER_TURN = 0.3F;
    private static final double FULL_SPEED_SQUARED = 1.1 * 1.1;
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("mount.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("running");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean jumpQueued;
    private boolean extraJump = true;
    /// 仅客户端模型姿态使用，不参与移动、碰撞和骑手朝向计算。
    private boolean renderPoseInitialized;
    private float renderBodyYaw;
    private float previousRenderBodyYaw;
    private float renderBank;
    private float previousRenderBank;

    public RideableUnicornMountEntity(EntityType<? extends RideableUnicornMountEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void onJumpInputChanged(Player player, boolean jumping) {
        if (jumping) jumpQueued = true;
    }

    @Override
    protected void tickRidden(Player player) {
        standOnFluid(player, false);
        if (onGround()) extraJump = true;
        Vec3 velocity = accelerateHorizontal(player, player.xxa, player.zza, 1.1, 0.016);
        double vertical = onGround() ? -0.08 : velocity.y - 0.08;
        boolean nativeJump = false;
        if (jumpQueued) {
            if (onGround()) {
                vertical = 0.9 * jumpMultiplier(player);
                nativeJump = true;
            }
            else if (extraJump) {
                vertical = 1.25 * jumpMultiplier(player);
                extraJump = false;
                nativeJump = true;
            }
            jumpQueued = false;
        }
        vertical = accessoryJumpVelocity(player, vertical, nativeJump);
        var previousBox = getBoundingBox();
        moveWithVelocity(new Vec3(velocity.x, vertical, velocity.z), player, false);
        if (level().isClientSide) updateRenderPose(player);
        if (player instanceof ServerPlayer owner && velocity.horizontalDistanceSqr() > 1) {
            owner.serverLevel().sendParticles(ParticleTypes.END_ROD, getX(), getY() + 0.3, getZ(), 1, 0.25, 0.15, 0.25, 0.01);
            float damage = (float) (60.0 * PrefixUtils.attributeWithoutHeldItem(owner, LibAttributes.getSummonDamage(), owner.getMainHandItem()));
            DamageSource source = LibDamageTypes.of(level(), LibDamageTypes.SUMMONER, owner);
            for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, previousBox.minmax(getBoundingBox()).inflate(0.2),
                    target -> ProjectileHitRules.canHit(player, target))) {
                if (target.hurt(source, damage)) {
                    protectRider();
                    target.knockback(0.6, -velocity.x, -velocity.z);
                }
            }
        }
    }

    /// 身体沿最短角度跟随骑手，奔跑转向时轻微侧倾；实际操控仍即时响应。
    private void updateRenderPose(Player player) {
        if (!renderPoseInitialized) {
            renderPoseInitialized = true;
            renderBodyYaw = player.getYRot();
        }
        previousRenderBodyYaw = renderBodyYaw;
        previousRenderBank = renderBank;
        float turn = Mth.clamp(Mth.wrapDegrees(player.getYRot() - renderBodyYaw) * BODY_TURN_BLEND, -MAX_BODY_TURN, MAX_BODY_TURN);
        renderBodyYaw = Mth.wrapDegrees(renderBodyYaw + turn);
        float speed = (float) Mth.clamp(getDeltaMovement().horizontalDistanceSqr() / FULL_SPEED_SQUARED, 0.0, 1.0);
        float targetBank = -turn * BANK_PER_TURN * speed;
        renderBank = Mth.lerp(BANK_BLEND, renderBank, targetBank);
    }

    public float getRenderBodyYaw(float partialTick) {
        return renderPoseInitialized ? Mth.rotLerp(partialTick, previousRenderBodyYaw, renderBodyYaw) : getYRot();
    }

    public float getRenderBank(float partialTick) {
        return Mth.lerp(partialTick, previousRenderBank, renderBank);
    }

    /// 头先看向骑手视角，身体随后跟上；限制偏转以免头部扭到背后。
    public float getRenderHeadYaw(float partialTick) {
        return getFirstPassenger() instanceof Player player
                ? Mth.clamp(Mth.wrapDegrees(player.getViewYRot(partialTick) - getRenderBodyYaw(partialTick)), -MAX_HEAD_TURN, MAX_HEAD_TURN)
                : 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, state -> {
            double speed = getDeltaMovement().horizontalDistanceSqr();
            if (speed < 0.0001) return state.setAndContinue(IDLE);
            return state.setAndContinue(speed > 0.16 ? RUN : WALK);
        }));
    }

    @Override
    public float modifyRiderDamage(DamageSource source, float amount) {
        return super.modifyRiderDamage(source, source.is(DamageTypes.FALL) ? amount * 0.2F : amount);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
