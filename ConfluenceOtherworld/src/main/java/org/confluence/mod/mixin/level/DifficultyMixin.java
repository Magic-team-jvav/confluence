package org.confluence.mod.mixin.level;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.worldgen.secret_seed.ForTheWorthy;
import org.confluence.terra_curio.mixed.SelfGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Difficulty.class)
public abstract class DifficultyMixin implements SelfGetter<Difficulty> {
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void ftw(CallbackInfoReturnable<Component> cir) {
        if (ModSecretSeeds.FOR_THE_WORTHY.match()) {
            cir.setReturnValue(ForTheWorthy.getDifficultyName(self()));
        }
    }
}
