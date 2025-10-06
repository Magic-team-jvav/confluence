package org.confluence.mod.mixin.integration.betteradvancements;

import net.minecraft.advancements.AdvancementHolder;
import org.confluence.mod.common.data.AchievementOffset;
import org.confluence.mod.common.data.AchievementOffsetLoader;
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
        AchievementOffset offset = AchievementOffsetLoader.getDisplayOffset().get(advancementHolder.id());
        if (offset != null) {
            this.hideLines = offset.hideLink();
        }
    }
}
