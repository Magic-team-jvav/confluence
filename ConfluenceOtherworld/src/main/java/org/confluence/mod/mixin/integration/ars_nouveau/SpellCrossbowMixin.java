package org.confluence.mod.mixin.integration.ars_nouveau;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.integration.ars_nouveau.ArsNouveauHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.hollingsworth.arsnouveau.common.items.SpellCrossbow", remap = false)
public abstract class SpellCrossbowMixin {
    @Inject(method = "tryLoadProjectiles", at = @At("HEAD"), cancellable = true)
    private void confluence$tryLoadProjectiles(LivingEntity pShooter, ItemStack pCrossbowStack, CallbackInfoReturnable<Boolean> cir) {
        if (ArsNouveauHelper.isSpellCrossbowLoadingDisabled(pShooter, pCrossbowStack)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "performShooting", at = @At("HEAD"), cancellable = true)
    private void confluence$performShooting(Level pLevel, LivingEntity pShooter, InteractionHand pUsedHand, ItemStack pCrossbowStack, float pVelocity, float pInaccuracy, @Nullable LivingEntity pTarget, CallbackInfo ci) {
        if (ArsNouveauHelper.isSpellCrossbowShootingDisabled(pShooter, pCrossbowStack)) {
            ci.cancel();
        }
    }
}
