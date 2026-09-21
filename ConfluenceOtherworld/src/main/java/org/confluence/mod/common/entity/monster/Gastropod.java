package org.confluence.mod.common.entity.monster;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import org.confluence.mod.common.data.map.CreatureDefinition;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 发射粉色能量弹幕的腹足怪。
public final class Gastropod extends RangedFlyingMonster {
    private static final double HOVER_HEIGHT = 5.5;
    private static final double FIRING_DISTANCE = 6.0;
    private static final int SHOT_COOLDOWN = 55;
    private static final int WINDUP_TICKS = 11;
    private static final RawAnimation NORMAL = RawAnimation.begin().thenLoop("normal");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("reach_out").thenLoop("attack_mode");
    private static final RawAnimation WITHDRAW = RawAnimation.begin().thenPlay("withdraw");
    private boolean firingWindup;
    private boolean shotCancelled;

    public Gastropod(EntityType<? extends Gastropod> type, Level level) {
        super(type, level, SHOT_COOLDOWN, 0.8);
    }

    @Override
    protected BTRoot createBT() {
        CreatureDefinition.BehaviorOverrides behavior = creatureDefinition().behavior();
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        new CombatAction(behavior.shotCooldownOr(SHOT_COOLDOWN)),
                        new HoverWanderAction(behavior.wanderSpeedOr(0.18)));
            }
        };
    }

    @Override
    protected Projectile createProjectile(LivingEntity target) {
        HostileParticleProjectile projectile = ModEntities.GASTROPOD_PROJECTILE.get().create(level());
        if (projectile == null) {
            return null;
        }
        projectile.configure(this, target, (float) (getAttributeValue(Attributes.ATTACK_DAMAGE) * shotMultiplier()));
        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(4.0));
        return projectile;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Shell", 0, state -> state.setAndContinue(NORMAL)).triggerableAnim("attack", ATTACK).triggerableAnim("withdraw", WITHDRAW));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);
        if (damaged && firingWindup) shotCancelled = true;
        return damaged;
    }

    private double getHoverY() {
        Vec3 start = position().add(0.0, 0.05, 0.0);
        HitResult floor = level().clip(new ClipContext(start, start.add(0.0, -32.0, 0.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, this));
        double desiredY = floor.getType() == HitResult.Type.MISS ? getY() - 1.0 : floor.getLocation().y + HOVER_HEIGHT;
        if (desiredY > getY()) {
            Vec3 top = position().add(0.0, getBbHeight(), 0.0);
            HitResult ceiling = level().clip(new ClipContext(top, new Vec3(top.x, desiredY + getBbHeight(), top.z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (ceiling.getType() != HitResult.Type.MISS)
                desiredY = ceiling.getLocation().y - getBbHeight() - 0.1;
        }
        return desiredY;
    }

    private void moveTowardHover(Vec3 horizontalOffset, double desiredY, double speed) {
        Vec3 horizontal = horizontalOffset.multiply(1.0, 0.0, 1.0);
        double distance = horizontal.length();
        if (distance > 1.0E-6)
            horizontal = horizontal.scale(Math.min(speed, distance * 0.08) / distance);
        double vertical = Mth.clamp((desiredY - getY()) * 0.12, -0.3, 0.3);
        setDeltaMovement(getDeltaMovement().lerp(new Vec3(horizontal.x, vertical, horizontal.z), 0.2));
        hasImpulse = true;
    }

    private final class HoverWanderAction extends BTNode {
        private final double speed;
        private Vec3 destination;
        private int ticks;

        private HoverWanderAction(double speed) {
            this.speed = speed;
        }

        @Override
        public void start() {
            double angle = random.nextDouble() * Math.PI * 2.0;
            destination = position().add(Math.cos(angle) * 6.0, 0.0, Math.sin(angle) * 6.0);
            ticks = 60;
        }

        @Override
        public BTStatus execute() {
            if (--ticks <= 0) return BTStatus.SUCCESS;
            moveTowardHover(destination.subtract(position()), getHoverY(), speed);
            faceCombatMovement(10.0F, 30.0F);
            return BTStatus.RUNNING;
        }
    }

    private final class CombatAction extends BTNode {
        private final int cooldownDuration;
        private int cooldown;
        private int windupTicks;

        private CombatAction(int cooldownDuration) {
            this.cooldownDuration = cooldownDuration;
        }

        @Override
        public boolean canStart() {
            LivingEntity target = getTarget();
            return target != null && target.isAlive() && canAttack(target);
        }

        @Override
        public BTStatus execute() {
            LivingEntity target = getTarget();
            if (target == null || !target.isAlive() || !canAttack(target)) return BTStatus.FAILURE;
            if (firingWindup) {
                setDeltaMovement(Vec3.ZERO);
                faceCombatPosition(target.getEyePosition(), 30.0F, 30.0F);
                if (shotCancelled || !hasLineOfSight(target) || distanceToSqr(target) > getAttributeValue(Attributes.FOLLOW_RANGE) * getAttributeValue(Attributes.FOLLOW_RANGE)) {
                    finishShot();
                } else if (++windupTicks >= WINDUP_TICKS) {
                    Projectile projectile = createProjectile(target);
                    if (projectile != null && !level().addFreshEntity(projectile))
                        projectile.discard();
                    finishShot();
                }
                return BTStatus.RUNNING;
            }
            double hoverY = getHoverY();
            Vec3 offset = target.position().subtract(position()).multiply(1.0, 0.0, 1.0);
            double distance = offset.length();
            if (distance > 1.0E-6) offset = offset.scale((distance - FIRING_DISTANCE) / distance);
            else offset = Vec3.directionFromRotation(0.0F, getYRot()).scale(-FIRING_DISTANCE);
            moveTowardHover(offset, hoverY, getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.8);
            faceCombatPosition(target.getEyePosition(), 30.0F, 30.0F);
            if (cooldown > 0) cooldown--;
            if (cooldown == 0 && Math.abs(hoverY - getY()) <= 0.5 && hasLineOfSight(target) && distanceToSqr(target) <= getAttributeValue(Attributes.FOLLOW_RANGE) * getAttributeValue(Attributes.FOLLOW_RANGE)) {
                firingWindup = true;
                shotCancelled = false;
                windupTicks = 0;
                getNavigation().stop();
                setDeltaMovement(Vec3.ZERO);
                stopTriggeredAnimation("Shell", null);
                triggerAnim("Shell", "attack");
            }
            return BTStatus.RUNNING;
        }

        private void finishShot() {
            firingWindup = false;
            cooldown = cooldownDuration;
            stopTriggeredAnimation("Shell", null);
            triggerAnim("Shell", "withdraw");
        }

        @Override
        public void stop() {
            if (firingWindup) finishShot();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.JELLYFISH_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.JELLYFISH_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.JELLYFISH_DEATH.get();
    }

}
