package org.confluence.terraentity.entity.summon;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.terraentity.api.entity.IMeleeAttackPartGoal;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;

import java.util.EnumSet;

import static software.bernie.geckolib.constant.DefaultAnimations.*;

public class SummonSlime extends AbstractSummonMob {


    private float distanceToFlyToOwner = 25.0f;
    private float distanceToStopToOwner = 4.0f;
    private float distanceToSlowDownToOwner = 6.0f;

    private float baseJump = 0.5f;
    private float enhanceJump = 1.0f;

    private boolean isFlying = false;

    public SummonSlime(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.7f);
        this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(0);
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(baseJump);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(10.0D);



        this.moveControl = new SlimeMoveControl(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SlimeFloatGoal(this));
        this.goalSelector.addGoal(2, new SlimeAttackGoal(this));
        this.goalSelector.addGoal(3, new SlimeKeepOnJumpingGoal(this));
        this.goalSelector.addGoal(5, new SlimeFlyToOwnerGoal(this));

//        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));

//        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));

    }

    protected int getJumpDelay() {
        return this.random.nextInt(10) + 5;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_SHARED_FLAGS_ID.equals(key) && level().isClientSide) {
            this.isFlying = getSharedFlag(6);
        }

    }




    static class SlimeMoveControl extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final SummonSlime slime;
        private boolean isAggressive;

        public SlimeMoveControl(SummonSlime slime) {
            super(slime);
            this.slime = slime;
            this.yRot = 180.0F * slime.getYRot() / 3.1415927F;
        }

        public void setDirection(float yRot, boolean aggressive) {
            this.yRot = yRot;
            this.isAggressive = aggressive;
        }

        public void setWantedMovement(double speed) {
            this.speedModifier = speed;
            this.operation = Operation.MOVE_TO;
        }


        @Override
        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = Operation.WAIT;
                if (this.mob.onGround()) {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        LivingEntity target  = slime.getTarget();
                        if(target !=null && target.distanceTo(slime) < 8 && target.getY()> slime.getY() + 2){
                            slime.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(slime.enhanceJump);
                        }else slime.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(slime.baseJump);
                        this.slime.getJumpControl().jump();

                    } else {
                        this.slime.xxa = 0.0F;
                        this.slime.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }
            }


        }
    }

    static class SlimeKeepOnJumpingGoal extends Goal {
        private final SummonSlime slime;

        public SlimeKeepOnJumpingGoal(SummonSlime slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        public boolean canUse() {
            if(slime.getOwner() == null) return false;
            float distance = this.slime.distanceTo(this.slime.getOwner());

            return this.slime.getTarget() == null && distance < 16;
        }

        public void tick() {
            MoveControl var2 = this.slime.getMoveControl();

            if (var2 instanceof SummonSlime.SlimeMoveControl control) {
                LivingEntity owner = this.slime.getOwner();
                if (owner!=null) {
                    Vec3 dir = owner.position().subtract(this.slime.position()).normalize();
                    float yaw = -(float) Math.atan2(dir.x, dir.z) * 57.295776F;
                    control.setDirection(yaw, true);
                    if (slime.distanceToOwner < slime.distanceToStopToOwner) {

                    } else if (slime.distanceToOwner < slime.distanceToSlowDownToOwner) {
                        control.setWantedMovement(0.8f);
                        this.slime.lookControl.setLookAt(owner);
                    } else
                        control.setWantedMovement(1.5f);
                }
            }
        }
    }

    static class SlimeAttackGoal extends Goal implements IMeleeAttackPartGoal {
        private final SummonSlime slime;
        private int growTiredTimer;

        public SlimeAttackGoal(SummonSlime slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            Entity target = this.getActualTarget(slime);
            if (target == null) {
                return false;
            } else {
                if(slime.getOwner() == null) return false;
                if(slime.distanceToOwner > slime.distanceToFlyToOwner) return false;

                return this.canMeleeAttackTarget(target) && this.slime.getMoveControl() instanceof SlimeMoveControl;
            }
        }

        public void start() {
            this.growTiredTimer = reducedTickDelay(300);
            super.start();
        }

        public boolean canContinueToUse() {
            Entity target = this.getActualTarget(slime);
            if (target == null) {
                return false;
            } else {
                if(slime.getOwner() == null) return false;
                if(slime.distanceToOwner > slime.distanceToFlyToOwner) return false;

                return this.canMeleeAttackTarget(target) && --this.growTiredTimer > 0;
            }
        }

        public boolean requiresUpdateEveryTick() {
            return false;
        }

        public void tick() {
            Entity target = this.getActualTarget(slime);
            if (target != null) {
                this.slime.lookAt(target, 20.0F, 20.0F);
            }

            MoveControl var3 = this.slime.getMoveControl();
            if (var3 instanceof SummonSlime.SlimeMoveControl control) {
                control.setDirection(this.slime.getYRot(), this.slime.isDealsDamage());
                control.setWantedMovement(1.5);
            }

        }

        @Override
        public boolean canMeleeAttackTarget(Entity target) {
            if(target instanceof PartEntity<?> part && part.getParent() instanceof LivingEntity living) {
                return this.slime.canAttack(living);
            }
            if(target instanceof LivingEntity living) {
                return this.slime.canAttack(living);
            }
            return false;
        }
    }

    static class SlimeFlyToOwnerGoal extends Goal {
        private final SummonSlime slime;

        public SlimeFlyToOwnerGoal(SummonSlime slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity owner = this.slime.getOwner();
            if (owner == null) {
                return false;
            }
            return !slime.shouldTryTeleportToOwner() && slime.distanceToOwner > slime.distanceToFlyToOwner && this.slime.getMoveControl() instanceof SlimeMoveControl;
        }

        public boolean canContinueToUse() {
            return slime.distanceToOwner > slime.distanceToStopToOwner;
        }

        public void start() {
            super.start();
            slime.setSharedFlag(6, true);
            this.slime.noPhysics = true;
        }

        public void stop() {
            slime.isFlying = false;
            slime.setSharedFlag(6, false);
            this.slime.noPhysics = false;

        }

        public void tick() {
            LivingEntity owner = this.slime.getOwner();
            if (owner != null) {
                this.slime.lookAt(owner, 20.0F, 20.0F);
                slime.setDeltaMovement(owner.position().add(0,3,0).subtract(slime.position()).normalize().scale(1f));
            }
        }
    }

    static class SlimeFloatGoal extends Goal {
        private final SummonSlime slime;

        public SlimeFloatGoal(SummonSlime slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
            slime.getNavigation().setCanFloat(true);
        }

        public boolean canUse() {
            return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof SummonSlime.SlimeMoveControl;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.slime.getRandom().nextFloat() < 0.8F) {
                this.slime.getJumpControl().jump();
            }

            MoveControl var2 = this.slime.getMoveControl();
            if (var2 instanceof SummonSlime.SlimeMoveControl control) {
                control.setWantedMovement(1.2);
            }

        }
    }

    public boolean isFlying(){
        return this.isFlying;
    }

    protected boolean isDealsDamage() {
        return this.isEffectiveAi();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly/Idle/Move", 0, state ->
                state.setAndContinue(this.isFlying() ? FLY :
                        ((this.onGround() ? IDLE  : WALK)
                ))));
    }

}
