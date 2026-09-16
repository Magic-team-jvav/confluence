package org.confluence.mod.common.entity.mount;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.summon.SummonStats;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RideableUnicornMountEntity extends AbstractMountEntity implements GeoEntity {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final SummonStats CHARGE_DAMAGE = new SummonStats(60.0F, 1.0F);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean jumpQueued;
    private boolean extraJump = true;

    public RideableUnicornMountEntity(EntityType<? extends RideableUnicornMountEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void onJumpInputChanged(Player player, boolean jumping) {
        if (jumping) jumpQueued = true;
    }

    @Override
    protected void tickRidden(Player player) {
        if (onGround()) extraJump = true;
        Vec3 velocity = accelerateHorizontal(player, player.xxa, player.zza, 1.1, 0.016);
        double vertical = onGround() ? -0.08 : velocity.y - 0.08;
        if (jumpQueued) {
            if (onGround()) vertical = 0.9;
            else if (extraJump) {
                vertical = 1.25;
                extraJump = false;
            }
            jumpQueued = false;
        }
        moveWithVelocity(new Vec3(velocity.x, vertical, velocity.z));
        if (player instanceof ServerPlayer owner && velocity.horizontalDistanceSqr() > 1) {
            float damage = CHARGE_DAMAGE.damage(owner);
            DamageSource source = LibDamageTypes.of(level(), LibDamageTypes.SUMMONER, owner);
            for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.2),
                    target -> ProjectileHitRules.canHit(player, target))) {
                if (target.hurt(source, damage)) {
                    target.knockback(0.6, -velocity.x, -velocity.z);
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 0, state -> state.setAndContinue(IDLE)));
    }

    @Override
    public float modifyRiderDamage(DamageSource source, float amount) {
        return source.is(DamageTypes.FALL) ? amount * 0.2F : amount;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
