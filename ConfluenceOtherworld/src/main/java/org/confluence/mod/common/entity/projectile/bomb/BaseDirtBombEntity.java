package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.util.MultiplyExplosionDamageCalculator;
import org.confluence.mod.common.init.ModEntities;

public class BaseDirtBombEntity extends BaseBombEntity {
    protected int radius = 4;
    protected BlockState toFill = Blocks.DIRT.defaultBlockState();

    public BaseDirtBombEntity(EntityType<? extends BaseDirtBombEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BaseDirtBombEntity(EntityType<? extends BaseDirtBombEntity> pEntityType, LivingEntity pShooter) {
        super(pEntityType, pShooter);
    }

    public BaseDirtBombEntity(LivingEntity pShooter) {
        super(ModEntities.DIRT_BOMB.get(), pShooter);
    }

    @Override
    protected void explodeFunction() {
        BlockPos blockPos = blockPosition();
        BlockPos.MutableBlockPos mutable = blockPos.mutable();
        int radiusSqr = Mth.square(radius);
        for (int i = -radius; i < radius; i++) {
            int x = blockPos.getX() + i;
            for (int j = -radius; j < radius; j++) {
                int y = blockPos.getY() + j;
                for (int k = -radius; k < radius; k++) {
                    int z = blockPos.getZ() + k;
                    mutable.set(x, y, z);
                    if (mutable.distSqr(blockPos) <= radiusSqr && level().getBlockState(mutable).isEmpty()) {
                        level().setBlockAndUpdate(mutable, toFill);
                    }
                }
            }
        }
        level().explode(
                this, Explosion.getDefaultDamageSource(level(), this),
                new MultiplyExplosionDamageCalculator(0.9F),
                getX(), getY(), getZ(), radius, false,
                Level.ExplosionInteraction.NONE, ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE
        );
    }
}
