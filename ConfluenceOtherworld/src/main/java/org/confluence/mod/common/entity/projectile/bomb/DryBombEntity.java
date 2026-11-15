package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.util.damage.MultiplyExplosionDamageCalculator;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.util.TerraStyleExplosion;

public class DryBombEntity extends BaseBombEntity {
    protected int radius = 4;

    public DryBombEntity(EntityType<DryBombEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DryBombEntity(LivingEntity pShooter) {
        super(ModEntities.DRY_BOMB.get(), pShooter);
    }

    @Override
    protected void explodeFunction(ServerLevel level) {
        BlockPos blockPos = blockPosition();
        BlockPos.MutableBlockPos mutable = blockPos.mutable();
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int i = -radius; i < radius; i++) {
            int x = blockPos.getX() + i;
            for (int j = -radius; j < radius; j++) {
                int y = blockPos.getY() + j;
                for (int k = -radius; k < radius; k++) {
                    int z = blockPos.getZ() + k;
                    mutable.set(x, y, z);
                    if (!level.getFluidState(mutable).isEmpty()) {
                        level.setBlockAndUpdate(mutable, air);
                    }
                }
            }
        }
        TerraStyleExplosion.terraExplode(level, this, Explosion.getDefaultDamageSource(level, this), new MultiplyExplosionDamageCalculator(0.9F), getX(), getY(), getZ(), radius, Level.ExplosionInteraction.NONE);
    }
}
