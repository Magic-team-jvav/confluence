package org.confluence.terraentity.entity.monster;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.ai.fsm.AbstractMobSkill;
import org.confluence.terraentity.entity.ai.fsm.CircleMobSkills;
import org.confluence.terraentity.entity.ai.fsm.EmptyMobSkill;
import org.confluence.terraentity.entity.ai.goal.FSMGoal;
import org.confluence.terraentity.entity.monster.prefab.AttributeBuilder;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;

/**
 * 花岗精
 */
public class GraniteElemental extends AbstractFSMMonster implements FlyingAnimal {

    public GraniteElemental(EntityType<? extends GraniteElemental> type, Level level, AttributeBuilder builder) {
        super(type, level, builder);
        this.moveControl = new FlyingMoveControl(this, 180, true);

    }

    @Override
    public boolean isFlying() {
        return this.getSkills().index == 0;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        return flyingpathnavigation;
    }

    private static class InternalFSMGoal extends FSMGoal<GraniteElemental> {

        static RawAnimation toDefense = RawAnimation.begin().thenPlay("to_defense");
        static RawAnimation fromDefense = RawAnimation.begin().thenPlay("from_defense");

        public InternalFSMGoal(GraniteElemental mob, EntityDataAccessor<Integer> skillIndexData) {
            super(mob, skillIndexData);
        }

        @Override
        public void init(CircleMobSkills skills) {
            AbstractMobSkill<GraniteElemental> walk = new AbstractMobSkill<>(DefaultAnimations.WALK, 9999999, 0) {
                @Override
                public void start(GraniteElemental mob) {

                }

                @Override
                public void tick(GraniteElemental mob, int time) {
                    LivingEntity target = mob.getTarget();
                    if(target== null || !target.isAlive()){
                        return;
                    }
                    float distance = (float) mob.distanceToSqr(target);
                    if(distance > 3f){
                        double angle = TEUtils.angleBetween(mob.getDeltaMovement(), target.position().subtract(mob.position()));
                        if(angle > 0.6){
                            mob.setDeltaMovement(mob.getDeltaMovement().scale(0.95f));
                        }

                    }
                    if(mob.navigation.isDone() || distance > 3f) {
                        mob.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), 0, 1.0f);
                    }
                }

                @Override
                public void stop(GraniteElemental mob) {
                    mob.getNavigation().stop();
                }
            };
            AbstractMobSkill<GraniteElemental> toDefenseSkill = new EmptyMobSkill<>(toDefense, 7);
            AbstractMobSkill<GraniteElemental> defenseSkill = new AbstractMobSkill<>(DefaultAnimations.IDLE, 100, 7) {
                @Override
                public void start(GraniteElemental mob) {

                }

                @Override
                public void tick(GraniteElemental mob, int time) {
                    mob.setNoGravity(false);
                    mob.setDeltaMovement(new Vec3(0, -0.2f,0f));
                }

                @Override
                public void stop(GraniteElemental mob) {

                }
            };
            AbstractMobSkill<GraniteElemental> from_denseSkill = new EmptyMobSkill<>(fromDefense, 7);

            this.addSkill(walk);
            this.addSkill(toDefenseSkill);
            this.addSkill(defenseSkill);
            this.addSkill(from_denseSkill);
        }
    }


    @Override
    protected FSMGoal<GraniteElemental> createFSMGoal(EntityDataAccessor<Integer> data) {
        return new InternalFSMGoal(this, data);
    }

    @Override
    public boolean onGround(){
        if(this.getSkills().index == 0){
            return false;
        }
        return super.onGround();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(!this.level().isClientSide) {
            int index = this.getSkills().index;
            if (index == 2) {
                return !pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
            }
            if (this.difficultSelector.isExpert() &&
                    this.random.nextFloat() < 0.2f &&
                    index == 0) {
                this.getSkills().forceEnd();
            }
        }
        return super.hurt(pSource, pAmount);
    }

}
