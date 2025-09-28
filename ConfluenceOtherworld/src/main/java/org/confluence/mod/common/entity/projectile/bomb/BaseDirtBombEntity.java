package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
    protected void explodeFunction(ServerLevel level) {
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
                    if (mutable.distSqr(blockPos) <= radiusSqr && level.getBlockState(mutable).isEmpty()) {
                        level.setBlockAndUpdate(mutable, toFill);
                    }
                }
            }
        }
        TerraStyleExplosion.terraExplode(level, this, Explosion.getDefaultDamageSource(level, this), new MultiplyExplosionDamageCalculator(0.9F), getX(), getY(), getZ(), radius, Level.ExplosionInteraction.NONE);
    }
}
