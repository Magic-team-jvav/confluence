package org.confluence.terraentity.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.entity.monster.prefab.AttributeBuilder;
import org.confluence.terraentity.entity.proj.BaseProj;

import java.util.function.Supplier;

public class FireImpEntity extends RangeShooter {
    public FireImpEntity(EntityType<? extends Monster> type, Level level, Supplier<? extends EntityType<? extends BaseProj<?>>> projType, AttributeBuilder builder) {
        super(type, level, projType, builder);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) {
            if (random.nextInt(24) == 0 && !isSilent()) {
                level().playLocalSound(
                        getX() + 0.5,
                        getY() + 0.5,
                        getZ() + 0.5,
                        SoundEvents.BLAZE_BURN,
                        getSoundSource(),
                        1.0F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.3F,
                        false
                );
            }
            level().addParticle(ParticleTypes.FLAME, getRandomX(0.5), getRandomY(), getRandomZ(0.5), 0.0, 0.02, 0.0);
        }
    }
}
