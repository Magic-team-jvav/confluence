package org.confluence.terraentity.mixin.client;

import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.confluence.terraentity.client.post.BossSpawnCameraManager;
import org.confluence.terraentity.mixed.HotSwap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {


    @Unique
    TextureTarget terra_entity$handTarget;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/Camera;FLorg/joml/Matrix4f;)V"))
    public void renderLevel(DeltaTracker deltaTracker, CallbackInfo ci) {
        if(terra_entity$handTarget == null)
            terra_entity$handTarget = new TextureTarget(Minecraft.getInstance().getMainRenderTarget().width, Minecraft.getInstance().getMainRenderTarget().height,false,false);

        HotSwap.doSomething(deltaTracker.getGameTimeDeltaPartialTick(true),terra_entity$handTarget);

    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V", shift = At.Shift.AFTER))
    public void setupCamera(DeltaTracker deltaTracker, CallbackInfo ci) {

        BossSpawnCameraManager.INSTANCE.update(deltaTracker.getGameTimeDeltaPartialTick(true));

    }
}
