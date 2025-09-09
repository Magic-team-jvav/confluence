package org.confluence.mod.mixin.integration.betteradvancements;

import net.minecraft.advancements.AdvancementHolder;
import org.confluence.mod.client.gui.AchievementToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "betteradvancements.common.advancements.BetterDisplayInfo", remap = false)
public abstract class BetterDisplayInfoMixin {
    @Shadow
    private boolean hideLines;

    @Inject(method = "<init>(Lnet/minecraft/advancements/AdvancementHolder;)V", at = @At("TAIL"))
    private void hideLines(AdvancementHolder advancementHolder, CallbackInfo ci) {
        this.hideLines = AchievementToast.hideLink(advancementHolder.id(), hideLines);
    }
}
