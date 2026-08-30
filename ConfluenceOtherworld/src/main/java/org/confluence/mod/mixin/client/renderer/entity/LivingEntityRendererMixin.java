package org.confluence.mod.mixin.client.renderer.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.confluence.lib.client.DummyMultiBufferSource;
import org.confluence.mod.mixed.ILivingEntityRenderer;
import org.confluence.mod.util.DeathAnimUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin implements ILivingEntityRenderer {
    @Unique
    private ModelPart confluence$rootModelPart;

    @Override
    public ModelPart confluence$getRootModelPart() {
        if (confluence$rootModelPart == null) {
            confluence$rootModelPart = DeathAnimUtils.findRootModelPart(confluence$self());
        }
        return confluence$rootModelPart;
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getInstance()Lnet/minecraft/client/Minecraft;"), cancellable = true)
    private void cancelDummyRender(CallbackInfo ci, @Local(argsOnly = true) MultiBufferSource buffer) {
        if (DummyMultiBufferSource.isDummy(buffer)) ci.cancel();
    }
}
