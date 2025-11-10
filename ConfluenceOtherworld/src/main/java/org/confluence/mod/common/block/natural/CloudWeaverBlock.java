package org.confluence.mod.common.block.natural;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.FoodItems;

import java.util.Set;

public class CloudWeaverBlock extends BaseCropBlock {
    public CloudWeaverBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return FoodItems.CLOUDWEAVER_SEED.get();
    }

    @Override
    public Set<Block> getCanPlaceBlocks() {
        return Set.of(NatureBlocks.CLOUD_BLOCK.get(), NatureBlocks.RAIN_CLOUD_BLOCK.get());
    }
}
