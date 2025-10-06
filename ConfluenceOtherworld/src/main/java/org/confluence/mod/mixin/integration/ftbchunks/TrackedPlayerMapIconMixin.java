package org.confluence.mod.mixin.integration.ftbchunks;

import dev.ftb.mods.ftblibrary.icon.FaceIcon;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import org.confluence.mod.integration.ftbchunks.FTBChunksHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "dev.ftb.mods.ftbchunks.client.mapicon.TrackedPlayerMapIcon", remap = false)
public abstract class TrackedPlayerMapIconMixin {
    @Shadow
    @Final
    private FaceIcon faceIcon;

    @Inject(method = "onMousePressed", at = @At("HEAD"))
    private void click(BaseScreen screen, MouseButton button, CallbackInfoReturnable<Boolean> cir) {
        FTBChunksHelper.onMouseClicked(screen, button, faceIcon.profile.getId());
    }
}
