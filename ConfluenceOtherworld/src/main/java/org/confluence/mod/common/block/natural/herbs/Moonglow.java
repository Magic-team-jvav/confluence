package org.confluence.mod.common.block.natural.herbs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
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
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.common.init.item.FoodItems;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Moonglow extends BaseHerbBlock {
    public static final IntegerProperty PROP_LIGHT = IntegerProperty.create("level", BRIGHTNESS, 5);

    public Moonglow() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).randomTicks().lightLevel(value -> value.getValue(AGE) == MAX_AGE ? value.getValue(PROP_LIGHT) : 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PROP_LIGHT);
    }

    @Override
    protected @NotNull ItemLike getBaseSeedId() {
        return FoodItems.MOONGLOW_SEED.get();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (getAge(state) != MAX_AGE) return;
        int r = random.nextInt(200);
        int brightness;
        if (r < 10) {
            level.setBlockAndUpdate(pos, state.setValue(PROP_LIGHT, 5));
            Vec3 center = pos.getCenter().offsetRandom(random, 0.6f);
            level.addParticle(new DustParticleOptions(new Vector3f(0, 0.7f, 1), 1), center.x, center.y / 2, center.z, 10, 10, 10);
            center = pos.getCenter().offsetRandom(random, 0.6f);
            level.addParticle(new DustParticleOptions(new Vector3f(0, 0.7f, 1), 1), center.x, center.y / 2, center.z, 10, 10, 10);
        } else if (r < 160 && (brightness = state.getValue(PROP_LIGHT)) > BRIGHTNESS) {
            level.setBlockAndUpdate(pos, state.setValue(PROP_LIGHT, brightness - 1));
        }
    }

    @Override
    public boolean canBloom(ServerLevel level, BlockState state) {
        return LibDateUtils.isNight(level);
    }
}
