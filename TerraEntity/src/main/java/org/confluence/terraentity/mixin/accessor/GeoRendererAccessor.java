package org.confluence.terraentity.mixin.accessor;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import software.bernie.geckolib.renderer.GeoRenderer;

@Mixin(GeoRenderer.class)
public interface GeoRendererAccessor {

    @Invoker
    VertexConsumer callCheckAndRefreshBuffer(boolean isReRender, VertexConsumer buffer, MultiBufferSource bufferSource, RenderType renderType);


}
