package org.confluence.terraentity.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.entity.ai.goal.behavior.BTFactory;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;
import org.confluence.terraentity.entity.ai.goal.behavior.BTRoot;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.Condition;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.TargetExistCondition;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.AnimTriggerAction;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.JumpForwardAction;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.LookAtTargetAction;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.RandomLookAction;
import org.confluence.terraentity.entity.monster.prefab.AttributeBuilder;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * 跳跳怪
 */
public class JumpAttackMonster extends AbstractMonster {

    private static final RawAnimation jump = RawAnimation.begin().thenPlay("jump");
    private static final RawAnimation idle = RawAnimation.begin().thenPlay("idle");

    public JumpAttackMonster(EntityType<? extends AbstractMonster> entityType, Level level, AttributeBuilder builder) {
        super(entityType, level, builder);

    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(5, new JumpAttackBT(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class,false, LivingEntity::canBeSeenAsEnemy));

    }

    private static class JumpAttackBT extends BTRoot<JumpAttackMonster> {

        public JumpAttackBT(JumpAttackMonster mob) {
            super(mob);
        }

        @Override
        protected @NotNull BTNode createBehaviorTree() {
            return BTFactory.infinite(BTFactory.selector()
                    // 游走
                    .addWithCondition(Condition.not(new TargetExistCondition(mob)), BTFactory.infinite(this.createWonderBehavior()))
                    // 攻击
                    .addWithCondition(new TargetExistCondition(mob), this.createAttackBehavior())
                    .setDesc("AI")
            );
        }

        private BTNode createAttackBehavior() {
            return BTFactory.selector()
                    .addChild(BTFactory.sequence()
                            .addChild(BTFactory.withTimer(15, new LookAtTargetAction(this.mob)))
                            .addChild(new AnimTriggerAction(this.mob, "Controller", "jump"))
                            .addChild(BTFactory.wait(10))
                            .addChild(new JumpForwardAction(this.mob, 2f, 0f))
                    )
                    ;

        }

        private BTNode createWonderBehavior() {
            return BTFactory.sequence()
                    .addChild(BTFactory.withTimer(20, new RandomLookAction(this.mob)))
                    .addChild(new AnimTriggerAction(this.mob, "Controller", "jump"))
                    .addChild(BTFactory.wait(10))
                    .addChild(new JumpForwardAction(this.mob, 0.5f, 0f))
                    .addChild(BTFactory.wait(30))


                    ;
        }

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 5, state->{
            return state.setAndContinue(idle);
        }).triggerableAnim("jump", jump)
        );
    }
}
