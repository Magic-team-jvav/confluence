package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AetheriumBlock extends Block {
    public AetheriumBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 4; i++) {
            if (random.nextDouble() < 0.1) {
                double ox = random.nextDouble() * 5.0 - 2.5;
                double oy = random.nextDouble() * 5.0 - 2.5;
                double oz = random.nextDouble() * 5.0 - 2.5;

                double x = pos.getX() + 0.5 + ox;
                double y = pos.getY() + 0.5 + oy;
                double z = pos.getZ() + 0.5 + oz;

                double ol = ((ox * ox) + (oy * oy) + (oz * oz)) * -2;
                if (ol == 0) ol = 0.0001;
                level.addParticle(ParticleTypes.END_ROD, x, y, z, ox / ol, oy / ol, oz / ol);
            }
        }
    }
}
