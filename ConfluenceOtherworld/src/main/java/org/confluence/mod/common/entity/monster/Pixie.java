package org.confluence.mod.common.entity.monster;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;

public class Pixie extends AbstractMonster implements FlyingAnimal {

    public Pixie(EntityType<? extends Pixie> type, Level level, AttributeBuilder builder) {
        super(type, level, builder);
        this.moveControl = new FlyingMoveControl(this, 180, false);
        this.getAttribute(PortAttributesExtension.gravity()).setBaseValue(0.0f);
        collisionProperties = new CollisionProperties(10, 20, 0.5f);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new Bird.BirdWanderGoal(this, 1.0f) {
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob.getTarget() == null;
            }
        });
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        return flyingpathnavigation;
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        float distance = (float) this.distanceToSqr(target);
        if (distance > 3f) {
            double angle = TEUtils.angleBetween(this.getDeltaMovement(), target.position().subtract(this.position()));
            if(angle > 0.6){
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95f));
            }
        }

        this.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), 0, 2.0f);

    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "Fly/Idle", 5, state ->
                state.setAndContinue(DefaultAnimations.FLY)
        ));
    }

    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.PIXIE_HURT.get();
    }

    @Override
    protected SoundEvent getAmbientSound(){
        return ModSoundEvents.PIXIE_FREE.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.PIXIE_DEATH.get();
    }
}
