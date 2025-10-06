package org.confluence.mod.mixin.client.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.mod.mixed.IModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRendererProvider.Context.class)
public abstract class EntityRendererProvider$ContextMixin {
    @Inject(method = "bakeLayer", at = @At("RETURN"))
    private void bakeLayer(ModelLayerLocation pLayer, CallbackInfoReturnable<ModelPart> cir) {
        ModelPart root = cir.getReturnValue();
        root.getAllParts().forEach(child -> IModelPart.of(child).confluence$root(root));
        IModelPart.of(root).confluence$root(root);
    }
}
