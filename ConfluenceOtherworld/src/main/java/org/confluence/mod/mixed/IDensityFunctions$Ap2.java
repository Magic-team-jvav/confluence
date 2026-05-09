package org.confluence.mod.mixed;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public interface IDensityFunctions$Ap2 {
    void confluence$setArgs(DensityFunction arg1, DensityFunction arg2);

    DensityFunction confluence$getArg(boolean arg1);

    DensityFunctions.TwoArgumentSimpleFunction.Type confluence$getType();
}
