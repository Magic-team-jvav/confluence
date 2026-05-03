package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.confluence.lib.util.damage.MultiplyExplosionDamageCalculator;
import org.confluence.mod.util.TerraStyleExplosion;

public class LiquidBombEntity extends BaseBombEntity {
    private Fluid toFill;
    private int radius;

    public LiquidBombEntity(EntityType<? extends LiquidBombEntity> type, Level level) {
        super(type, level);
    }

    public LiquidBombEntity(EntityType<? extends LiquidBombEntity> type, LivingEntity shooter, Fluid fluid, int radius) {
        super(type, shooter);
        this.toFill = fluid;
        this.radius = radius;
    }

    @Override
    protected void explodeFunction(ServerLevel level) {
        if (!level.dimensionType().ultraWarm() || toFill.getFluidType() != NeoForgeMod.WATER_TYPE.value()) {
            BlockPos blockPos = blockPosition();
            BlockPos.MutableBlockPos mutable = blockPos.mutable();
            for (int i = -radius; i < radius; i++) {
                int x = blockPos.getX() + i;
                for (int j = 0; j < radius; j++) {
                    int y = blockPos.getY() + j;
                    for (int k = -radius; k < radius; k++) {
                        int z = blockPos.getZ() + k;
                        mutable.set(x, y, z);
                        BlockState state = level.getBlockState(mutable);
                        if (state.getBlock() instanceof SimpleWaterloggedBlock block &&
                                block.canPlaceLiquid(getOwner() instanceof Player player ? player : null, level, mutable, state, toFill)
                        ) {
                            block.placeLiquid(level, mutable, state, toFill.defaultFluidState());
                        } else if (state.canBeReplaced(toFill)) {
                            level.destroyBlock(mutable, true, getOwner());
                            level.setBlockAndUpdate(mutable, toFill.defaultFluidState().createLegacyBlock());
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
