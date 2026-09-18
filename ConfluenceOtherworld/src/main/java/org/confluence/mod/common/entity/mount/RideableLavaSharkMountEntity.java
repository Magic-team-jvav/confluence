package org.confluence.mod.common.entity.mount;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RideableLavaSharkMountEntity extends AbstractMountEntity implements GeoEntity {
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RideableLavaSharkMountEntity(EntityType<? extends RideableLavaSharkMountEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void tickRidden(Player player) {
        player.clearFire();
        Vec3 velocity;
        if (isInWater() || isInLava()) {
            double yaw = Math.toRadians(player.getYRot());
            Vec3 side = new Vec3(Math.cos(yaw), 0, Math.sin(yaw));
            Vec3 direction = player.getLookAngle().scale(player.zza).add(side.scale(player.xxa));
            if (isJumpInputDown()) direction = direction.add(0, 1, 0);
            Vec3 desired = direction.lengthSqr() > 1 ? direction.normalize().scale(1.3) : direction.scale(1.3);
            velocity = getDeltaMovement().lerp(desired, 0.2);
        } else {
            velocity = onGround() ? accelerateHorizontal(player, player.xxa, player.zza, 0.2, 0.05) : getDeltaMovement();
            velocity = new Vec3(velocity.x, onGround() && isJumpInputDown() ? 0.5 : velocity.y - 0.08, velocity.z);
        }
        moveWithVelocity(velocity);
        setXRot(Mth.rotLerp(0.2F, getXRot(), (float) -Math.toDegrees(Math.atan2(velocity.y, velocity.horizontalDistance()))));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 0, state -> state.setAndContinue(SWIM)));
    }

    @Override
    public float modifyRiderDamage(DamageSource source, float amount) {
        return source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypes.FALL) ? 0 : amount;
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() - 0.2;
    }

    @Override
    public boolean tiltsWithMovement() {
        return true;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
