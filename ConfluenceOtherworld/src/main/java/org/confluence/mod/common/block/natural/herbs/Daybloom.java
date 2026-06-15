package org.confluence.mod.common.block.natural.herbs;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.item.FoodItems;

public class Daybloom extends BaseHerbBlock {
    @Override
    protected ItemLike getBaseSeedId() {
        return FoodItems.DAYBLOOM_SEED.get();
    }

    @Override
    public boolean canBloom(ServerLevel level, BlockState state) {
        return level.isDay();
    }
}
