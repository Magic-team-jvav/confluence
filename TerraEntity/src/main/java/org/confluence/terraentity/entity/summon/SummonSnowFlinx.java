package org.confluence.terraentity.entity.summon;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.terraentity.api.entity.IMeleeAttackPartGoal;
import org.confluence.terraentity.entity.ai.goal.JumpOverBlockGoal;
import org.confluence.terraentity.entity.ai.goal.summon.SummonMeleeAttackGoal;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.DefaultAnimations;

public class SummonSnowFlinx extends AbstractSummonMob {

    int dashCd = 20;

    public SummonSnowFlinx(EntityType<? extends SummonSnowFlinx> entityType, Level level) {
        super(entityType, level);


    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new SummonJumpOverBlockGoal(this));
        this.goalSelector.addGoal(3, new SummonMeleeAttackGoal(this, 0.8f, true));

    }


    public static class SummonJumpOverBlockGoal extends JumpOverBlockGoal implements IMeleeAttackPartGoal {

        int cd = 20;
        public SummonJumpOverBlockGoal(Mob mob) {
            super(mob, 1.0f);
        }

        @Override
        public boolean canUse() {
            delayJumpTicks--;
            --cd;
            if(delayJumpTicks == 0) {
                mob.addDeltaMovement(mob.getForward().normalize().scale(mob.getSpeed() * speedModifier));
            }
            if(!mob.onGround() || cd > 0) {
                return false;
            }

            Entity target = getActualTarget(mob);
            if(target == null){
                return false;
            }

            Vec3 targetPos = getTargetPosition(mob, target);
            if(targetPos == null) return false;

            return mob.distanceToSqr(targetPos) < 25 && mob.getY() < targetPos.y + 2.0;
        }

        @Override
        public boolean canMeleeAttackTarget(Entity target) {
            if (target instanceof PartEntity<?> partEntity) {
                Entity parent = partEntity.getParent();
                return parent instanceof LivingEntity living && mob.canAttack(living);
            }
            return target instanceof LivingEntity living && mob.canAttack(living);
        }

        @Override
        public void start() {
            super.start();

            this.cd = 80 + this.mob.getRandom().nextInt(40);

        }
    }

    @Override
    public void tick() {
        super.tick();
        --this.dashCd;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean flag = super.doHurtTarget(entity);
        if(flag && this.dashCd <= 0) {
            Vec3 dir = entity.getEyePosition().subtract(this.position()).normalize();
            this.addDeltaMovement(dir);
            this.dashCd = 20;
        }
        return flag;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
    }
}
