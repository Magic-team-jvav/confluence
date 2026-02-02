package org.confluence.terraentity.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Mixin(GeoEntityRenderer.class)
public interface GeoEntityRendererAccessor {
    @Mutable
    @Accessor("model")
    void setModel(GeoModel<?> model);
}
