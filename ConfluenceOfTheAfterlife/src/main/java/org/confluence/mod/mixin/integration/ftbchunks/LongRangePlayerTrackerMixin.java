package org.confluence.mod.mixin.integration.ftbchunks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "dev.ftb.mods.ftbchunks.LongRangePlayerTracker", remap = false)
public abstract class LongRangePlayerTrackerMixin {
    @ModifyVariable(method = "tick", at = @At("STORE"), ordinal = 1)
    private int modify(int maxDistSq) {
        return Integer.MAX_VALUE;
    }
}
