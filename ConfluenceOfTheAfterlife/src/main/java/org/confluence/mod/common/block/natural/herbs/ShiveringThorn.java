package org.confluence.mod.common.block.natural.herbs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.natural.BaseHerbBlock;
import org.confluence.mod.common.init.item.FoodItems;

public class ShiveringThorn extends BaseHerbBlock {

    @Override
    protected ItemLike getBaseSeedId(){
        return FoodItems.SHIVERTHORN_SEED.get();
    }

    @Override
    public boolean canBloom(ServerLevel world, BlockState state){
        return getAge(state) == MAX_AGE;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom){
        if(getAge(pState) == MAX_AGE - 1){
            pLevel.setBlockAndUpdate(pPos, pState.setValue(AGE, MAX_AGE));
        }
        super.randomTick(pState, pLevel, pPos, pRandom);
    }
}
