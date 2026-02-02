package org.confluence.terraentity.client.boss.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.boss.model.GeoBossModel;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.boss.QueenBee;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class QueenBeeRenderer extends GeoNormalRenderer<QueenBee> {

    public QueenBeeRenderer(EntityRendererProvider.Context renderManager, GeoBossModel<QueenBee> model) {
        super(renderManager, model,false,1.0f,0.75f);
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this){
            @Override
            protected ResourceLocation getTextureResource(QueenBee animatable) {
                return TerraEntity.space("textures/entity/boss/queen_bee_eye.png");
            }
            @Override
            protected RenderType getRenderType(QueenBee animatable, @Nullable MultiBufferSource bufferSource) {
                return RenderType.entityTranslucentEmissive(getTextureResource(animatable));
            }
        });
    }

    @Override
    public void preRender(PoseStack poseStack, QueenBee animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public void reRender(BakedGeoModel model, PoseStack poseStack, MultiBufferSource bufferSource, QueenBee animatable,
                          RenderType renderType, VertexConsumer buffer, float partialTick,
                          int packedLight, int packedOverlay, int colour) {
        if(!animatable.isAngry()) return;
        poseStack.pushPose();
        actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
        postRender(poseStack, animatable, model, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
        poseStack.popPose();
    }
}
