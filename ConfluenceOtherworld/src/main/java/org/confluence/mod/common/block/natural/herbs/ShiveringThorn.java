package org.confluence.mod.common.block.natural.herbs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.particle.CrossDustParticleOptions;
import org.confluence.mod.common.init.item.FoodItems;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ShiveringThorn extends BaseHerbBlock {
    public static final IntegerProperty PROP_LIGHT = IntegerProperty.create("level", 0, 6);

    public ShiveringThorn() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).randomTicks());
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return FoodItems.SHIVERTHORN_SEED.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PROP_LIGHT);
    }


    @Override
    public boolean canBloom(ServerLevel level, BlockState state) {
        return getAge(state) == MAX_AGE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (getAge(state) == MAX_AGE - 1) {
            level.setBlockAndUpdate(pos, state.setValue(AGE, MAX_AGE));
        }
        super.randomTick(state, level, pos, random);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos blockPos, RandomSource random) {
        if (getAge(state) != MAX_AGE) return;
        int r = random.nextInt(200);
        int brightness;
        if (r < 34) {
            Vector4f curve = new Vector4f(0, 0.33f, 0.66f, 1);
            Vec3 pos = blockPos.getCenter().add(state.getOffset(level, blockPos)).offsetRandom(random, 0.5f);
            CrossDustParticleOptions particle = new CrossDustParticleOptions(false,
                0xffffffff, 0xb0A9FFFF, new Vector3f(), curve, 1,
                15, r < 17 ? 20 : -20, curve, true, true, false, true);
            level.addParticle(particle, pos.x, pos.y, pos.z, 0, 0, 0);
        }
        if (r < 10) {
            level.setBlockAndUpdate(blockPos, state.setValue(PROP_LIGHT, 6));
        } else if (r < 160 && (brightness = state.getValue(PROP_LIGHT)) > 0) {
            level.setBlockAndUpdate(blockPos, state.setValue(PROP_LIGHT, brightness - 1));
        }
    }

}
