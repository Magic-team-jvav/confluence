package org.confluence.terraentity.client.entity.renderer.proj;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.proj.LavaPillar;
import org.jetbrains.annotations.Nullable;

public class LavaPillarRenderer extends GeoNormalRenderer<LavaPillar> {

    public LavaPillarRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        super(renderManager, path);
    }

    @Override
    public boolean shouldRender(LavaPillar entity, Frustum camera, double camX, double camY, double camZ) {
        if (entity.isTriggered()) {
            return super.shouldRender(entity, camera, camX, camY, camZ);
        }
        return false;
    }

    @Override
    public RenderType getRenderType(LavaPillar animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(texture);
    }

}
