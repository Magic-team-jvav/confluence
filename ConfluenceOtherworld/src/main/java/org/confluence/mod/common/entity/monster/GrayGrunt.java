package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/** A relentless contact fighter with complete knockback resistance. */
public final class GrayGrunt extends MartianHumanoidMonster {
    private static final RawAnimation STAND = RawAnimation.begin().thenLoop("Stand");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("Walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("Run");

    public GrayGrunt(EntityType<? extends GrayGrunt> type, Level level) {
        super(type, level, 0.08);
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    private boolean isUsableTarget(LivingEntity target) {
        return target != null && target.isAlive() && canAttack(target);
    }

    @Override
    protected BTNode createLandBehavior() {
        BTNode combatBehavior = new BTNode() {
            @Override
            public boolean canStart() {
                return isUsableTarget(getTarget());
            }

            @Override
            public BTStatus execute() {
                LivingEntity target = getTarget();
                if (!isUsableTarget(target)) {
                    return BTStatus.FAILURE;
                }

                getLookControl().setLookAt(target, 30, 30);
                getNavigation().moveTo(target, 1.0);
                return BTStatus.RUNNING;
            }

            @Override
            public void stop() {
                getNavigation().stop();
            }

        };
        return SelectorNode.of(combatBehavior,
                new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(this, 0.8)),
                new VanillaGoalAction(new RandomLookAroundGoal(this)));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<GrayGrunt> controller = new AnimationController<>(this, "grunt", 3, state -> {
            RawAnimation movement = isSprinting() ? RUN : WALK;
            return state.setAndContinue(state.isMoving() ? movement : STAND);
        });
        controllers.add(controller);
    }
}
