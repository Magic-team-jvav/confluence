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
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.common.init.item.FoodItems;
import org.joml.Vector4f;

public class Moonglow extends BaseHerbBlock {
    public static final IntegerProperty PROP_LIGHT = IntegerProperty.create("level", 3, 6);

    public Moonglow() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).randomTicks().lightLevel(value -> value.getValue(AGE) == MAX_AGE ? value.getValue(PROP_LIGHT) : 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PROP_LIGHT);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return FoodItems.MOONGLOW_SEED.get();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos blockPos, RandomSource random) {
        if (getAge(state) != MAX_AGE) return;
        int r = random.nextInt(200);
        int brightness;
        if (r < 34) {
            boolean large = r < 6;
            Vector4f curve = new Vector4f(0, random.nextFloat()*2-0.5f, random.nextFloat()*2-0.5f, random.nextFloat()*2-0.5f);
            Vec3 pos = blockPos.getCenter().add(state.getOffset(level, blockPos)).offsetRandom(random, 0.3f);
            CrossDustParticleOptions particle;
            if (large) {
                particle = new CrossDustParticleOptions(true,
                    0x7f8DBDFF, 0x7f4760E4, Vec3.ZERO.offsetRandom(level.random, level.random.nextFloat() * 0.03f + 0.03f).toVector3f(),
                    curve, level.random.nextFloat() * 0.4f + 0.6f, random.nextInt(60, 80), level.random.nextInt(-20, 20),
                    curve, true, true, false, false);
            }else{
                particle = new CrossDustParticleOptions(false,
                    0x7f6BB3FF, 0x7f4886E3, Vec3.ZERO.offsetRandom(level.random, level.random.nextFloat() * 0.03f + 0.03f).toVector3f(),
                    curve, level.random.nextFloat() * 0.4f + 0.3f, random.nextInt(60,80), level.random.nextInt(-20, 20),
                    curve, true, true, false, false);
            }
            level.addParticle(particle, pos.x, pos.y, pos.z, 0, 0, 0);
        }
        if (r < 10) {
            level.setBlockAndUpdate(blockPos, state.setValue(PROP_LIGHT, 6));
        } else if (r < 160 && (brightness = state.getValue(PROP_LIGHT)) > 3) {
            level.setBlockAndUpdate(blockPos, state.setValue(PROP_LIGHT, brightness - 1));
        }
    }

    @Override
    public boolean canBloom(ServerLevel level, BlockState state) {
        return LibDateUtils.isNight(level);
    }
}
