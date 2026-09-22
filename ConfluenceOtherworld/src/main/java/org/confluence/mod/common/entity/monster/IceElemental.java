package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.LookForwardWanderFlyAction;
import org.confluence.mod.common.entity.projectile.FrostMonsterProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public final class IceElemental extends BaseFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("flying");

    public IceElemental(EntityType<? extends IceElemental> type, Level level) {
        super(type, level);
        setDiscardFriction(true);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(new HoverAttack(), new LookForwardWanderFlyAction(IceElemental.this, 0.12, 0.0F));
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Flying", 4, state -> state.setAndContinue(FLY)));
    }

    private final class HoverAttack extends BTNode {
        private int shotCooldown = 70;

        @Override
        public boolean canStart() {
            LivingEntity target = getTarget();
            return target != null && target.isAlive() && canAttack(target);
        }

        @Override
        public BTStatus execute() {
            if (!canStart()) return BTStatus.FAILURE;
            LivingEntity target = getTarget();
            Vec3 origin = position();
            HitResult floor = level().clip(new ClipContext(origin.add(0, 0.1, 0), origin.add(0, -9, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, IceElemental.this));
            double desiredY = floor.getType() == HitResult.Type.MISS ? getY() - 1.0 : Math.min(target.getY() + 2.0, floor.getLocation().y + 8.0);
            Vec3 offset = target.position().subtract(origin).multiply(1, 0, 1);
            double distance = offset.length();
            Vec3 horizontal = distance > 3.0 ? offset.scale(0.14 / distance) : Vec3.ZERO;
            Vec3 velocity = new Vec3(horizontal.x, Mth.clamp((desiredY - getY()) * 0.1, -0.18, 0.18), horizontal.z);
            if (horizontalCollision) velocity = velocity.add(0, 0.12, 0);
            setDeltaMovement(getDeltaMovement().lerp(velocity, 0.12));
            faceCombatPosition(target.getEyePosition(), 12.0F, 15.0F);
            if (shotCooldown > 0) shotCooldown--;
            if (shotCooldown == 0 && hasLineOfSight(target)) {
                FrostMonsterProjectile projectile = ModEntities.FROST_BLAST.get().create(level());
                if (projectile != null) {
                    projectile.configure(IceElemental.this, target, (float) getAttributeValue(Attributes.ATTACK_DAMAGE), 0.6F, 0.0F, 120);
                    if (level().addFreshEntity(projectile))
                        playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 1.0F);
                }
                shotCooldown = 70;
            }
            return BTStatus.RUNNING;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.PIXIE_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.PIXIE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.PIXIE_DEATH.get();
    }

}
