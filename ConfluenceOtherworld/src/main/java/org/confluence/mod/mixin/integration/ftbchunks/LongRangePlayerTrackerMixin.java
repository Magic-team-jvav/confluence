package org.confluence.mod.mixin.integration.ftbchunks;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbchunks.FTBChunksWorldConfig;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.network.c2s.WormholeToPlayerPacketC2S;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "dev.ftb.mods.ftbchunks.LongRangePlayerTracker", remap = false)
public abstract class LongRangePlayerTrackerMixin {
    @Inject(method = "shouldTrack", at = @At("HEAD"), cancellable = true)
    private void visible(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true, ordinal = 0) ServerPlayer p1, @Local(argsOnly = true, ordinal = 1) ServerPlayer p2) {
        if (CommonConfigs.FTB_CHUNKS_WORMHOLE_POTION.get() && (FTBChunksWorldConfig.LOCATION_MODE_OVERRIDE.get() || WormholeToPlayerPacketC2S.isTrackable(p1, p2))) {
            cir.setReturnValue(true);
        }
    }
}
