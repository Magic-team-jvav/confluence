package org.confluence.mod.common.block.natural.herbs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.particle.CrossDustParticleOptions;
import org.confluence.mod.common.init.item.FoodItems;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Waterleaf extends BaseHerbBlock {
    @Override
    protected ItemLike getBaseSeedId() {
        return FoodItems.WATERLEAF_SEED.get();
    }

    @Override
    public boolean canBloom(ServerLevel level, BlockState state) {
        return level.isRaining();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos blockPos, RandomSource random) {
        if (getAge(state) != MAX_AGE) return;
        int r = random.nextInt(60);
        if (r > 3) return;
        Vec3 pos = blockPos.getCenter().add(state.getOffset(level, blockPos)).offsetRandom(random, 0.3f);
        boolean large = level.random.nextBoolean();
        Vector4f curve = new Vector4f(0, 0.33f, 0.66f, 1);
        CrossDustParticleOptions sparkParticle = new CrossDustParticleOptions(large, 0xb0B0C3ED,
                0xa06C9AF7, new Vector3f((level.random.nextFloat() - 0.5f) / 40, 0.02f, (level.random.nextFloat() - 0.5f) / 40),
                curve, 0.7f, 15, 5, curve,
                true, true, false, true);
        level.addParticle(sparkParticle, pos.x, pos.y, pos.z, 0, 0.3f, 0);
    }

}
