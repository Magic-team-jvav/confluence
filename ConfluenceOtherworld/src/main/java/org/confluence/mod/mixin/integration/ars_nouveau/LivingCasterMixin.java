package org.confluence.mod.mixin.integration.ars_nouveau;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModAttachmentTypes;
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
        if (CommonConfigs.ARS_NOUVEAU_COMPATIBILITY.get() && livingEntity instanceof Player player) {
            cir.setReturnValue(totalCost <= player.getData(ModAttachmentTypes.MANA_STORAGE).getCurrentMana());
        }
    }
}
