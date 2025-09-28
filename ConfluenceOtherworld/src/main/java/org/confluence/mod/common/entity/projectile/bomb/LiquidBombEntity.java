package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.confluence.lib.util.MultiplyExplosionDamageCalculator;

public class LiquidBombEntity extends BaseBombEntity {
    private BlockState toFill;
    private int radius;

    public LiquidBombEntity(EntityType<? extends LiquidBombEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public LiquidBombEntity(EntityType<? extends LiquidBombEntity> pEntityType, LivingEntity pShooter, Fluid fluid, int radius) {
        super(pEntityType, pShooter);
        this.toFill = fluid.defaultFluidState().createLegacyBlock();
        this.radius = radius;
    }

    @Override
    protected void explodeFunction(ServerLevel level) {
        if (!level.dimensionType().ultraWarm() || toFill.getFluidState().getType().getFluidType() != NeoForgeMod.WATER_TYPE.value()) {
            BlockPos blockPos = blockPosition();
            BlockPos.MutableBlockPos mutable = blockPos.mutable();
            for (int i = -radius; i < radius; i++) {
                int x = blockPos.getX() + i;
                for (int j = 0; j < radius; j++) {
                    int y = blockPos.getY() + j;
                    for (int k = -radius; k < radius; k++) {
                        int z = blockPos.getZ() + k;
                        mutable.set(x, y, z);
                        if (level.getBlockState(mutable).isEmpty()) {
                            level.setBlockAndUpdate(mutable, toFill);
                        }
                    }
                }
            }
        }
        TerraStyleExplosion.terraExplode(level, this, Explosion.getDefaultDamageSource(level, this), new MultiplyExplosionDamageCalculator(0.9F), getX(), getY(), getZ(), radius, Level.ExplosionInteraction.NONE);
    }

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel level && isInFluidType()) {
            explodeFunction(level);
            discard();
        }
    }
}
