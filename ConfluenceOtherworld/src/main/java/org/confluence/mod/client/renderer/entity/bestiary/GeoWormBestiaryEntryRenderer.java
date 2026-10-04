package org.confluence.mod.client.renderer.entity.bestiary;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.entity.renderer.GeoNormalRenderer;
import org.confluence.mod.common.entity.monster.BaseWormMonster;

public class GeoWormBestiaryEntryRenderer extends GeoNormalRenderer<BaseWormMonster> {
    public GeoWormBestiaryEntryRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        super(renderManager, path);
    }

    @Override
    public void render(BaseWormMonster entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.5F, 0);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
