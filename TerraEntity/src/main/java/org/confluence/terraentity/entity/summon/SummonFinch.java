package org.confluence.terraentity.entity.summon;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.terraentity.api.entity.IPartEntityTargetable;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.DefaultAnimations;

public class SummonFinch  extends AbstractSummonMob implements FlyingAnimal {

    int cooledDown;

    public SummonFinch(EntityType<? extends SummonFinch> entityType, Level level) {
        super(entityType, level);

        this.moveControl = new FlyingMoveControl(this, 20, true);

    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new FinchAttackGoal(this));

        this.goalSelector.addGoal(9, new FloatGoal(this));
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.addDeltaMovement(new Vec3(0, Math.sin(this.tickCount*0.5f) * 0.03f ,0));
        this.cooledDown--;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if(cooledDown <= -5){
            cooledDown = 10;
        }
        return super.doHurtTarget(entity);
    }

    protected static class FinchAttackGoal extends Goal{

        SummonFinch mob;
        int cooledDown;
        protected FinchAttackGoal(SummonFinch mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            return getActualTarget() != null;
        }

        @Override
        public void tick() {
            super.tick();
            Entity actualTarget = getActualTarget();
            if(actualTarget == null){
                return;
            }

            double distance = mob.distanceToSqr(actualTarget);
            if (--this.cooledDown <= 0) {
                mob.lookAt(actualTarget, 90, 85);
                Vec3 targetPos = actualTarget instanceof LivingEntity living ? living.getEyePosition() : actualTarget.position();
                Vec3 dir = targetPos.subtract(mob.position());
                if(TEUtils.angleBetween(mob.getLookAngle(), dir) < 0.5){
                    if(mob.getKnownMovement().length() < 1f) {
                        mob.addDeltaMovement(dir.normalize().scale(0.1f));
                    }
                }
                if(distance < 3f && mob.cooledDown < 0){
                    cooledDown = 20;
                }
            }
            else{
                mob.addDeltaMovement(new Vec3(0,Math.min( 0.02, 1 / distance),0));
                mob.addDeltaMovement(mob.getForward().normalize().scale(0.03f));
                mob.lookAt(actualTarget, 10, 85);
            }
        }

        private Entity getActualTarget() {
            if (mob instanceof IPartEntityTargetable targetable) {
                Entity actualTarget = targetable.getActualTargetEntity();
                if (actualTarget != null) return actualTarget;
            }
            return mob.getTarget();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericFlyController(this));
    }
}
