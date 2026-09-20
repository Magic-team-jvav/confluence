package org.confluence.mod.mixin.integration.magiclib;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.common.init.ModBlockCounters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnvironmentLevelAccess.Matcher.class, remap = false)
public abstract class EnvironmentLevelAccess$MatcherMixin {
    @Inject(method = "isGraveyard", at = @At("HEAD"), cancellable = true)
    private static void inject(Player player, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(ModBlockCounters.isGraveyard(level, pos) || ModBlockCounters.isGraveyard(level, player.blockPosition()));
    }
}
