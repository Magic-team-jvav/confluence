package org.confluence.mod.client.renderer.entity.bestiary;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.client.entity.renderer.mob.GeoWormRenderer;
import org.confluence.terraentity.entity.monster.BaseWorm;
import org.confluence.terraentity.entity.monster.BaseWormPart;

public class GeoWormBestiaryEntryRenderer<T extends BaseWorm<S>, S extends BaseWormPart> extends GeoWormRenderer<T, S> {
    public GeoWormBestiaryEntryRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        super(renderManager, path);
    }

    public GeoWormBestiaryEntryRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path, float scale, float offsetY) {
        super(renderManager, path, scale, offsetY);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.5F, 0);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    protected void renderPart(T entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {}
}
