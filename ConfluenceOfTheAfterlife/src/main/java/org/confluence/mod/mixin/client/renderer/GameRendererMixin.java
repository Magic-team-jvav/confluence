package org.confluence.mod.mixin.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    Minecraft minecraft;

    @Inject(method = "checkEntityPostEffect", at = @At("HEAD"), cancellable = true)
    private void cancelReset(Entity entity, CallbackInfo ci) {
        if ((entity == null || entity == minecraft.player) && ModSecretSeeds.THE_CONSTANT.match()) {
            ci.cancel();
        }
    }
}
