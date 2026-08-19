package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class Pixie extends BaseFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");

    public Pixie(EntityType<? extends Pixie> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 180, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 30.0).add(Attributes.ATTACK_DAMAGE, 8.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(SequenceNode.of(new HasTargetCondition(Pixie.this), new PixiePursuitAction()), new FlyWanderAction(Pixie.this, 0.15, 10));
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly", 0, state -> state.setAndContinue(FLY)));
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

    @Override
    protected boolean hasPushableBody() {
        return true;
    }

    /// 妖精与 1.21 一致，可以利用水面漂浮导航，但不会穿门。
    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
    }

    @Override
    protected double contactAttackInflation() {
        return 0.5;
    }

    private final class PixiePursuitAction extends BTNode {
        @Override
        public BTStatus execute() {
            var target = getTarget();
            if (target == null || !target.isAlive()) return BTStatus.FAILURE;
            Vec3 toTarget = target.position().subtract(position());
            Vec3 movement = getDeltaMovement();
            if (distanceToSqr(target) > 3.0 && angleBetween(movement, toTarget) > 0.6) {
                setDeltaMovement(movement.scale(0.95));
            }
            getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), 2.0);
            return BTStatus.RUNNING;
        }

        private double angleBetween(Vec3 first, Vec3 second) {
            double product = first.length() * second.length();
            if (product < 1.0E-6) return 0.0;
            return Math.acos(net.minecraft.util.Mth.clamp(first.dot(second) / product, -1.0, 1.0));
        }
    }
}
