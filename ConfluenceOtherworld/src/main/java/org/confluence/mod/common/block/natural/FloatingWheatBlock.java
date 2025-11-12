package org.confluence.mod.common.block.natural;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.FoodItems;

import java.util.Set;

public class FloatingWheatBlock extends BaseCropBlock {
    public FloatingWheatBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return FoodItems.FLOATING_WHEAT_SEED.get();
    }

    @Override
    public Set<Block> getCanPlaceBlocks() {
        return Set.of(NatureBlocks.CLOUD_BLOCK.get(), NatureBlocks.RAIN_CLOUD_BLOCK.get());
    }
}
