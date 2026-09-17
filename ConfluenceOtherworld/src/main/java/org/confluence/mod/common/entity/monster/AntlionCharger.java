package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.goal.AcceleratingMeleeAttackGoal;
import org.confluence.mod.common.init.ModSoundEvents;

public final class AntlionCharger extends BaseWarriorMonster {
    public AntlionCharger(EntityType<? extends AntlionCharger> type, Level level) {
        super(type, level, 0.0, LandAnimationProfile.WALK_IDLE, LandSoundProfile.ROUTINE, 1.0, true);
    }

    @Override
    protected MeleeAttackGoal createMeleeGoal(double speed) {
        return new AcceleratingMeleeAttackGoal(this, speed, speed * 2.2, 40);
    }

    @Override
    protected float movementAnimationSpeed(float limbSwingAmount) {
        return Mth.clamp(limbSwingAmount * 4.0F, 0.5F, 2.2F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ANTLION_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ANTLION_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ANTLION_SWARMER_DEATH.get();
    }
}
