package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class AntlionSwarmer extends AbstractMonster {

    enum state{
        WONDER,
        ATTACKING
    }
    state currentState = state.WONDER;
    int _stateTime = 100;

    int stateTime = _stateTime;
    DashComponent dash;

    public AntlionSwarmer(EntityType<? extends Monster> type, Level level, AttributeBuilder builder) {
        super(type, level, builder);
        dash = new DashComponent(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(7, new LookForwardWanderFlyGoal(this, 0.3f, 0){
            @Override
            public boolean canUse() {
                return currentState == state.WONDER;
            }
        });
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericFlyController(this));
    }

    @Override
    public void tick() {
        super.tick();
        if(--stateTime <= 0){
            stateTime = _stateTime;
            switch(currentState){
                case WONDER:
                    if(getTarget() != null) {
                        currentState = state.ATTACKING;
                        dash.setDirection(getTarget().position().subtract(this.position()).normalize());
                        dash.setTargetPos(getTarget().position());
                    }
                    break;
                case ATTACKING:
                    currentState = state.WONDER;
                    break;
            }
        }
        if(getTarget() != null && currentState == state.ATTACKING){
            this.getLookControl().setLookAt(this.position().add(this.getDeltaMovement().scale(20).add(0,1,0)));
            this.setYRot(this.getYHeadRot());
            this.dash.uniformMove(0.2f);
            this.lookControl.setLookAt(dash.targetPos);
        }
    }

    public void move(@NotNull MoverType pType, @NotNull Vec3 motion) {
        if (dead) {
            super.move(pType, motion);
            return;
        }

        Vec3 collide = ((EntityAccessor) this).callCollide(motion);
        if (collide.x != motion.x) {
            motion = new Vec3(-motion.x, motion.y, motion.z);
            if(this.currentState == state.ATTACKING){
                this.currentState = state.WONDER;
            }
        }
        if (collide.y != motion.y) {
            motion = new Vec3(motion.x, -motion.y , motion.z);
            if(this.currentState == state.ATTACKING){
                this.currentState = state.WONDER;
            }
        }
        if (collide.z != motion.z) {
            motion = new Vec3(motion.x, motion.y, -motion.z);
            if(this.currentState == state.ATTACKING){
                this.currentState = state.WONDER;
            }
        }

        setDeltaMovement(motion);
        super.move(pType, motion);
    }
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return ModSoundEvents.ANTLION_HURT.get();
    }

    @Override
    protected SoundEvent getAmbientSound(){
        return ModSoundEvents.ANTLION_SWARMER_FREE.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ANTLION_SWARMER_DEATH.get();
    }
}
