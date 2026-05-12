package org.confluence.mod.mixin.client.model.geom;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import org.confluence.mod.mixed.IModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityModelSet.class)
public abstract class EntityModelSetMixin {
    @Inject(method = "bakeLayer", at = @At("RETURN"))
    private void bakeLayer(CallbackInfoReturnable<ModelPart> cir) {
        ModelPart root = cir.getReturnValue();
        root.getAllParts().forEach(child -> IModelPart.of(child).confluence$root(root));
        IModelPart.of(root).confluence$root(root);
    }
}
