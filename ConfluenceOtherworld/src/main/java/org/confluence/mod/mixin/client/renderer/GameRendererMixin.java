package org.confluence.mod.mixin.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.worldgen.secret_seed.TheConstant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    Minecraft minecraft;

    @Shadow
    @Nullable
    PostChain postEffect;

    @Inject(method = "checkEntityPostEffect", at = @At("HEAD"), cancellable = true)
    private void cancelReset(Entity entity, CallbackInfo ci) {
        if ((entity == null || entity == minecraft.player) && postEffect != null && TheConstant.POST_EFFECT.toString().equals(postEffect.getName())) {
            ci.cancel();
        }
    }

    @Inject(method = "togglePostEffect", at = @At("HEAD"), cancellable = true)
    private void cancelToggle(CallbackInfo ci) {
        if (ModSecretSeeds.THE_CONSTANT.match() && postEffect != null && TheConstant.POST_EFFECT.toString().equals(postEffect.getName())) {
            ci.cancel();
        }
    }
}
