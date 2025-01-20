package org.confluence.mod.common.block.natural.herbs;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.natural.BaseHerbBlock;
import org.confluence.mod.common.init.item.FoodItems;
import org.jetbrains.annotations.NotNull;

public class Waterleaf extends BaseHerbBlock {
	@Override
	protected @NotNull ItemLike getBaseSeedId(){
		return FoodItems.WATERLEAF_SEED.get();
	}

	@Override
	public boolean canBloom(ServerLevel world, BlockState state){
		return world.isRaining();
	}
}
