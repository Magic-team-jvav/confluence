package org.confluence.mod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.mixed.ILevelChunkSection;
import org.confluence.mod.util.DynamicBiomeUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnvironmentLevelAccess.Matcher.class, remap = false)
public abstract class EnvironmentLevelAccess$MatcherMixin {
    @Inject(method = "isGraveyard", at = @At("HEAD"), cancellable = true)
    private static void inject(Player player, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ILevelChunkSection iSection = DynamicBiomeUtils.getISection(level, pos);
        boolean graveyard;
        if (iSection == null || !iSection.confluence$isGraveyard()) {
            iSection = DynamicBiomeUtils.getISection(level, player.blockPosition());
            graveyard = iSection != null && iSection.confluence$isGraveyard();
        } else {
            graveyard = true;
        }
        cir.setReturnValue(graveyard);
    }
}
