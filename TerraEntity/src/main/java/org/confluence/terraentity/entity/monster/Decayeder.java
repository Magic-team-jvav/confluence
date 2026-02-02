package org.confluence.terraentity.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.entity.monster.prefab.AttributeBuilder;
import org.confluence.terraentity.entity.monster.skeleton.RangeSkeleton;
import org.confluence.terraentity.init.TESounds;
import org.jetbrains.annotations.Nullable;

public class Decayeder extends RangeSkeleton {
    public Decayeder(EntityType<? extends AbstractSkeleton> entityType, Level level, AttributeBuilder builder) {
        super(entityType, level, builder);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return TESounds.DECAYEDER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TESounds.DECAYEDER_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return TESounds.DECAYEDER_HURT.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        return TESounds.DECAYEDER_STEP.get();
    }
}
