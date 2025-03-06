package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class ParticleCloudBlock extends Block {
    private final ParticleOptions particleTypes;

    public ParticleCloudBlock(ParticleOptions particleTypes) {
        this(Properties.ofFullCopy(Blocks.GLASS).sound(SoundType.SNOW), particleTypes);
    }

    public ParticleCloudBlock(Properties properties, ParticleOptions particleTypes) {
        super(properties);
        this.particleTypes = particleTypes;
    }

    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity.isSuppressingBounce()) {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
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
