package org.confluence.mod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.util.DynamicBiomeUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnvironmentLevelAccess.Matcher.class, remap = false)
public abstract class EnvironmentLevelAccess$MatcherMixin {
    @Inject(method = "isEctoMist", at = @At("HEAD"), cancellable = true)
    private static void inject(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(DynamicBiomeUtils.getISection(level, pos).confluence$isEctoMist());
    }
}
