package org.confluence.mod.common.block.natural.herbs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.item.FoodItems;
import org.jetbrains.annotations.NotNull;

public class Blinkroot extends BaseHerbBlock {

	@Override
	protected @NotNull ItemLike getBaseSeedId(){
		return FoodItems.BLINKROOT_SEED.get();
	}

	@Override
	public boolean canBloom(ServerLevel world, BlockState state){
		return getAge(state) == MAX_AGE;
	}

	@Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom){
		int age = getAge(pState);
		int r = pRandom.nextInt(10);
		if(age == MAX_AGE && r < 2){
			pLevel.setBlockAndUpdate(pPos, pState.setValue(AGE, MAX_AGE - 1));
		}else if(age == MAX_AGE - 1 && r <= 5){
			pLevel.setBlockAndUpdate(pPos, pState.setValue(AGE, MAX_AGE));
		}
		super.randomTick(pState, pLevel, pPos, pRandom);
	}

	@Override
	public boolean isRandomlyTicking(BlockState pState){
		return true;
	}
}
