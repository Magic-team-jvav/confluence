package org.confluence.mod.mixin.level;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import org.confluence.mod.mixed.IDensityFunctions$Ap2;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.level.levelgen.DensityFunctions$Ap2")
public abstract class DensityFunctions$Ap2Mixin implements IDensityFunctions$Ap2 {
    @Mutable
    @Shadow
    @Final
    private DensityFunction argument1;

    @Mutable
    @Shadow
    @Final
    private DensityFunction argument2;

    @Shadow
    @Final
    private DensityFunctions.TwoArgumentSimpleFunction.Type type;

    @Override
    public void confluence$setArgs(DensityFunction arg1, DensityFunction arg2) {
        this.argument1 = arg1;
        this.argument2 = arg2;
    }

    @Override
    public DensityFunction confluence$getArg(boolean arg1) {
        return arg1 ? argument1 : argument2;
    }

    @Override
    public DensityFunctions.TwoArgumentSimpleFunction.Type confluence$getType() {
        return type;
    }
}
