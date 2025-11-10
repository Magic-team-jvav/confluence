package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ParticleCloudBlock extends CloudBlock {
    protected final ParticleOptions particleTypes;

    public ParticleCloudBlock(Properties properties, ParticleOptions particleTypes) {
        super(properties);
        this.particleTypes = particleTypes;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        double x = (double) pos.getX() + 0.5D;
        double y = pos.getY();
        double z = (double) pos.getZ() + 0.5D;

        double xOffset = random.nextDouble() * 0.6D - 0.3D;
        double zOffset = random.nextDouble() * 0.6D - 0.3D;
        double yOffset = random.nextDouble() * 0.5D;

        level.addParticle(particleTypes, x + xOffset, y + yOffset, z + zOffset, 0.0D, 0.0D, 0.0D);
    }
}
