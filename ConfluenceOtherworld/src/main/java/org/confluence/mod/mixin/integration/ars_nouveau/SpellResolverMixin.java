package org.confluence.mod.mixin.integration.ars_nouveau;

import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.integration.ars_nouveau.ArsNouveauHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.hollingsworth.arsnouveau.api.spell.SpellResolver", remap = false)
public abstract class SpellResolverMixin {
    /// 在创造和零消耗分支之前拒绝灵魂模式的新施法。
    @Inject(method = "enoughMana", at = @At("HEAD"), cancellable = true)
    private void confluence$checkManaMode(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (ArsNouveauHelper.isManaDisabled(entity)) {
            cir.setReturnValue(false);
        }
    }
}
