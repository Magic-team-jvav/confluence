package org.confluence.mod.mixin.integration.ftbchunks;

import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.integration.ftbchunks.FTBChunksHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "dev.ftb.mods.ftbchunks.LongRangePlayerTracker", remap = false)
public abstract class LongRangePlayerTrackerMixin {
    @Inject(method = "shouldTrack", at = @At("HEAD"), cancellable = true)
    private void visible(ServerPlayer p1, ServerPlayer p2, int maxDistSq, CallbackInfoReturnable<Boolean> cir) {
        if (FTBChunksHelper.shouldTrack(p1, p2)) {
            cir.setReturnValue(true);
        }
    }
}
