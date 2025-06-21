package org.confluence.mod.mixin.integration.ars_nouveau;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.TriState;
import org.confluence.mod.integration.ars_nouveau.ArsNouveauHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster", remap = false)
public abstract class LivingCasterMixin {
    @Shadow
    public LivingEntity livingEntity;

    @Inject(method = "enoughMana", at = @At("HEAD"), cancellable = true)
    private void enoughMana(int totalCost, CallbackInfoReturnable<Boolean> cir) {
        TriState triState = ArsNouveauHelper.enoughMana(livingEntity, totalCost);
        if (!triState.isDefault()) {
            cir.setReturnValue(triState.isTrue());
        }
    }
}
