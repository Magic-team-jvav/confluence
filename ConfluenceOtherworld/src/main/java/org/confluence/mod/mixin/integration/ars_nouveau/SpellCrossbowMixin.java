package org.confluence.mod.mixin.integration.ars_nouveau;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.integration.ars_nouveau.ArsNouveauHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.hollingsworth.arsnouveau.common.items.SpellCrossbow", remap = false)
public abstract class SpellCrossbowMixin {
    /// 禁止在灵魂模式装填法术，普通无附法术箭保留原行为。
    @Inject(method = "tryLoadProjectiles", at = @At("HEAD"), cancellable = true)
    private void confluence$checkManaForLoading(LivingEntity shooter, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ArsNouveauHelper.isManaDisabled(shooter) && ArsNouveauHelper.hasStoredSpell(stack)) {
            cir.setReturnValue(false);
        }
    }

    /// 发射前拦截已装填法术，避免消费尚未释放的弹药。
    @Inject(method = "performShooting", at = @At("HEAD"), cancellable = true)
    private void confluence$checkManaForShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack stack, float velocity, float inaccuracy, LivingEntity target, CallbackInfo ci) {
        if (ArsNouveauHelper.isManaDisabled(shooter) && ArsNouveauHelper.hasChargedSpell(stack)) {
            ci.cancel();
        }
    }
}
