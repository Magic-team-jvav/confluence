package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class Demon extends Harpy {

    public Demon(EntityType<? extends Monster> type, Level level, AttributeBuilder builder) {
        super(type, level, builder);
    }

    @Override
    protected void init(){
        this._shootCount = 1;
        this._shootTick = 50;
    }


    static RawAnimation HURT = RawAnimation.begin().thenPlay("hurt");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly/Hurt/Idle", 3, state -> {
            if(this.hurtTime > 0){
                return state.setAndContinue(HURT);
            }else if(this.swingTime > 0){
                return state.setAndContinue(DefaultAnimations.ATTACK_THROW);
            }
            return state.setAndContinue(DefaultAnimations.IDLE);
        }));


    }

    @Override
    public int getCurrentSwingDuration() {
        return 30;
    }

    @Override
    protected void shootTick(LivingEntity living, int tick){
        if(tick > 23 && tick % 8 == 0){
            this.shoot(living);
        }
        if(tick == 0){
            this.swing(InteractionHand.MAIN_HAND);
        }
    }
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypeTags.IS_FIRE)) {
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void shoot(LivingEntity living){
        DemonScytheProj proj = TEProjectileEntities.DEMON_SCYTHE_PROJ.get().create(level());
        this.playSound(ModSoundEvents.WAVING.get());
        if(proj != null) {
            proj.setPos(this.getEyePosition());
            proj.setOwner(this);
            proj.shootFromRotation(this, getXRot(), getYRot(), 0, 0.2F, 2); // fixme 射弹的初始旋转有问题
            //proj.shoot(living.getX() - this.getX(), living.getY() - this.getY(), living.getZ() - this.getZ(), 0.2f, 2f);
            proj.setDamage((float) this.getAttributeValue(LibAttributes.getAttackDamage()));
            level().addFreshEntity(proj);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.DEMON_HURT.get();
    }

    @Override
    protected SoundEvent getAmbientSound(){
        return ModSoundEvents.DEMON_FREE.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.DEMON_DEATH.get();
    }
}
