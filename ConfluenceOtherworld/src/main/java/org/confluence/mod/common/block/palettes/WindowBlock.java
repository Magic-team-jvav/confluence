package org.confluence.mod.common.block.palettes;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class WindowBlock extends ConnectedGlassBlock {
    protected final boolean translucent;

    public WindowBlock(Properties properties, boolean translucent) {
        super(properties);
        this.translucent = translucent;
    }

    public boolean isTranslucent() {
        return translucent;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (state.getBlock() == adjacentBlockState.getBlock()) {
            return true;
        }
        if (state.getBlock() instanceof WindowBlock windowBlock && adjacentBlockState.getBlock() instanceof ConnectedGlassBlock) {
            return !windowBlock.isTranslucent() && side.getAxis().isHorizontal();
        }
        return super.skipRendering(state, adjacentBlockState, side);
    }

}
