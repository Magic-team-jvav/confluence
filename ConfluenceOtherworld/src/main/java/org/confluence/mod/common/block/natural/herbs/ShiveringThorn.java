package org.confluence.mod.common.block.natural.herbs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.item.FoodItems;
import org.jetbrains.annotations.NotNull;

public class ShiveringThorn extends BaseHerbBlock {
    @Override
    protected @NotNull ItemLike getBaseSeedId() {
        return FoodItems.SHIVERTHORN_SEED.get();
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
}
