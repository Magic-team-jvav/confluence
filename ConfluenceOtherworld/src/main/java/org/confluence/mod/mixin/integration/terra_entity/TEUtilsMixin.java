package org.confluence.mod.mixin.integration.terra_entity;

import net.minecraft.server.level.ServerLevel;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.terraentity.utils.TEUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TEUtils.class)
public class TEUtilsMixin {
    @Inject(method = "isFTWWorld",at = @At("HEAD"),cancellable = true)
    private static void isFTWWorld(ServerLevel level, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(ModSecretSeeds.FOR_THE_WORTHY.match(level));
    }
}
