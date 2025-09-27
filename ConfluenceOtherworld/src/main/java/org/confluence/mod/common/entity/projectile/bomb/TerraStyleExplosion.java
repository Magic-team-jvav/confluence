package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TerraStyleExplosion extends Explosion {
    public TerraStyleExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, int radius, BlockInteraction blockInteraction) {
        super(level, source, damageSource, damageCalculator, x, y, z, radius, false, blockInteraction, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
    }

    @Override
    public void explode() {
        level.gameEvent(source, GameEvent.EXPLODE, new Vec3(x, y, z));

        int radius = (int) this.radius;
        int diameter = radius + radius + 1;
        BlockPos origin = BlockPos.containing(x, y, z);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < diameter; i++) {
            int dx = i - radius;
            for (int j = 0; j < diameter; j++) {
                int dy = j - radius;
                for (int k = 0; k < diameter; k++) {
                    int dz = k - radius;
                    if (Mth.lengthSquared(dx,dy,dz) > radius)continue;

                    pos.setWithOffset(origin, dx, dy, dz);
                    BlockState blockState = this.level.getBlockState(pos);
                    FluidState fluidState = this.level.getFluidState(pos);
                }
            }
        }
    }
}
