package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.LookForwardWanderFlyAction;
import org.confluence.mod.common.entity.ai.bt.leaf.PhasedFlyingPursuitAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class CursedSkull extends BaseFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private final PhasedFlyingPursuitAction pursuit;

    public CursedSkull(EntityType<? extends CursedSkull> type, Level level) {
        super(type, level);
        noPhysics = true;
        pursuit = new PhasedFlyingPursuitAction(this, 200, 150, 80, 0.02, 0.05, 0.5, 5.0, 0.3);
    }

    @Override
    protected boolean mustSeePlayerTarget() {
        return false;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(CursedSkull.this), pursuit),
                        new LookForwardWanderFlyAction(CursedSkull.this, 0.18, 0.0F));
            }
        };
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        pursuit.resetCycle();
        return super.doHurtTarget(target);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly", 0, state -> state.setAndContinue(FLY)));
    }

    @Override
    protected boolean hasPushableBody() {
        return true;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.TR_SKELETON_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.TR_ZOMBIE_DEATH.get();
    }

}
