package org.confluence.mod.common.block.functional.announcement_box;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class AnnouncementBoxEntity extends SignBlockEntity {
    public AnnouncementBoxEntity(BlockPos pos, BlockState blockState) {
        super(FunctionalBlocks.ANNOUNCEMENT_BOX_ENTITY.get(), pos, blockState);
    }
}
