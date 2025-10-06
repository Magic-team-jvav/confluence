package org.confluence.mod.mixed;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.confluence.lib.mixed.SelfGetter;

public interface ILivingEntityRenderer extends SelfGetter<LivingEntityRenderer<?, ?>> {
    ModelPart confluence$getRootModelPart();

    static ILivingEntityRenderer of(LivingEntityRenderer<?, ?> renderer) {
        return (ILivingEntityRenderer) renderer;
    }
}
