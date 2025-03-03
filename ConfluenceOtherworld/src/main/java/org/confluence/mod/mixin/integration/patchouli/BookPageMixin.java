package org.confluence.mod.mixin.integration.patchouli;

import org.confluence.mod.integration.patchouli.PatchouliHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.patchouli.client.book.BookEntry;

@Pseudo
@Mixin(targets = "vazkii.patchouli.client.book.BookPage", remap = false)
public abstract class BookPageMixin {
    @Shadow
    protected transient BookEntry entry;

    @Inject(method = "isPageUnlocked", at = @At("RETURN"), cancellable = true)
    private void extra(CallbackInfoReturnable<Boolean> cir) {
        if (entry != null && cir.getReturnValue()) cir.setReturnValue(PatchouliHelper.isEntityPageUnlocked(entry.getId()));
    }
}
