package org.confluence.terraentity.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.ai.goal.behavior.BTFactory;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;
import org.confluence.terraentity.entity.ai.goal.behavior.BTRoot;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.AbstractConditionLeaf;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.Condition;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.DistanceLowerThanCondition;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.TargetExistCondition;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.*;
import org.confluence.terraentity.entity.boss.KingSlime;
import org.confluence.terraentity.entity.proj.SlimeSpikeProjectile;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Supplier;

/**
 * 史莱姆王生成的尖刺史莱姆，只能发射近战弹幕
 */
public class SpikedSlime extends BaseSlime implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation idle = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation jump = RawAnimation.begin().thenPlay("jump");
    private static final RawAnimation attack = RawAnimation.begin().thenPlay("attack");


    public SpikedSlime(EntityType<? extends Slime> slime, Level level, int size) {
        super(slime, level, 0x73bcf4, size);

        this.moveControl = new MoveControl(this); // 原版史莱姆的move control似乎不自动旋转方向
    }


    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(5, new SpikedSlimeBT(this));

        this.targetSelector.addGoal(1, new KingSlime.HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, null));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }


    private static class SpikedSlimeBT extends BTRoot<SpikedSlime> {

        public SpikedSlimeBT(SpikedSlime mob) {
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
                    .addWithCondition(new DistanceLowerThanCondition(this.mob, 7.0), BTFactory.sequence()
                            .addChild(BTFactory.withTimer(20, new LookAtTargetAction(this.mob)))
                            .addChild(new AnimTriggerAction(this.mob, "Controller", "attack"))
                            .addChild(BTFactory.wait(5))
                            .addChild(BTFactory.repeater(3, BTFactory.sequence()
                                    .addChild(new ShootSpikeAction(this.mob))
                                    .addChild(BTFactory.wait(3))
                            ))
                            .addChild(BTFactory.wait(20))
                    )
                    .addChild(BTFactory.sequence()
                            .addChild(this.mob.createFarAwayFromTargetWaitingBehavior())
                            .addChild(new AnimTriggerAction(this.mob, "Controller", "jump"))
                            .addChild(BTFactory.wait(10))
                            .addChild(BTFactory.selector()
                                    .addWithCondition(new HeightLowerThanCondition(this.mob, 4), new JumpAttackAction(this.mob, 1.0f, 0f))
                                    .addChild(new JumpAttackAction(this.mob, 1.0f, 0.5f)))
                            .addChild(BTFactory.wait(10))
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

        private static class ShootSpikeAction extends ShootAction<SpikedSlime> {

            public ShootSpikeAction(SpikedSlime mob) {
                super(mob);
            }

            @Override
            protected void shoot(LivingEntity target) {
                Vec3 dir = TEUtils.sphere(1, mob.getRandom().nextFloat() * 6.28f, mob.getRandom().nextFloat() * 0.3f + 0.05f);

                for(int i=0;i<8;i++) {
                    SlimeSpikeProjectile entity = this.mob.getSpikeType().get().create(mob.level());
                    if(entity != null) {
                        float[] dirs = TEUtils.dirToRot(dir);
                        entity.shootFromRotation(mob, dirs[1], dirs[0], 0, 0.3f, 1.0f);
                        entity.setPos(mob.getBoundingBox().getCenter().offsetRandom(mob.getRandom(), 0.2f));
                        entity.setOwner(mob);
                        entity.setDamage(mob.getAttackDamage());
                        mob.level().addFreshEntity(entity);
                    }
                    dir = dir.yRot((float) (Math.PI * 0.25));
                }
            }
        }


        private static class HeightLowerThanCondition extends AbstractConditionLeaf {
            final Mob mob;
            final double height;
            public HeightLowerThanCondition(Mob mob, double height) {
                this.mob = mob;
                this.height = height;
            }
            @Override
            public boolean check() {
                if(mob.getTarget() == null) {
                    return false;
                }
                return mob.getY() - mob.getTarget().getY() < height;
            }
        }
    }

    protected BTNode createFarAwayFromTargetWaitingBehavior() {
        return BTFactory.withTimer(20, new LookAtTargetAction(this));
    }

    protected Supplier<EntityType<SlimeSpikeProjectile>> getSpikeType(){
        return TEProjectileEntities.SLIME_SPIKE;
    }

    @Override
    public void jumpFromGround() {
        super.jumpFromGround();
//        this.triggerAnim("Controller", "jump");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 5, state-> state.setAndContinue(idle))
                .triggerableAnim("jump", jump)
                .triggerableAnim("attack", attack)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
