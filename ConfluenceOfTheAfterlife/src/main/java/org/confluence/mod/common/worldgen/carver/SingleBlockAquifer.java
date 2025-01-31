package org.confluence.mod.common.worldgen.carver;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;

public class SingleBlockAquifer implements Aquifer {
    private final BlockState blockState;
    private final boolean scheduleFluidUpdate;

    public SingleBlockAquifer(BlockState blockState, boolean scheduleFluidUpdate) {
        this.blockState = blockState;
        this.scheduleFluidUpdate = scheduleFluidUpdate;
    }

    @Override
    public BlockState computeSubstance(DensityFunction.FunctionContext context, double substance) {
        return blockState;
    }

    @Override
    public boolean shouldScheduleFluidUpdate() {
        return scheduleFluidUpdate;
    }
}
