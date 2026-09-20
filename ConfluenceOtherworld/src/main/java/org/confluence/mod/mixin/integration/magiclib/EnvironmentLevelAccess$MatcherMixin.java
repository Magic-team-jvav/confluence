package org.confluence.mod.mixin.integration.magiclib;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.lib.common.worldgen.biome.DynamicBiomeUtils;
import org.confluence.lib.mixed.ILevelChunkSection;
import org.confluence.mod.util.ModBlockCounters;
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
        if (!ModBlockCounters.isGraveyard(iSection)) {
            iSection = DynamicBiomeUtils.getISection(level, player.blockPosition());
            graveyard = ModBlockCounters.isGraveyard(iSection);
        } else {
            graveyard = true;
        }
        cir.setReturnValue(graveyard);
    }
}
