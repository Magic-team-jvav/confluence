package org.confluence.terraentity.client.boss.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.boss.Deerclops;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.util.Color;

public class DeerclopsRenderer extends GeoNormalRenderer<Deerclops> {


    public DeerclopsRenderer(EntityRendererProvider.Context renderManager, GeoModel<Deerclops> model) {
        super(renderManager, model, false, 1, 0);

        this.addRenderLayer(new AutoGlowingGeoLayer<>(this){
            @Override
            protected ResourceLocation getTextureResource(Deerclops animatable) {
                return TerraEntity.space("textures/entity/boss/deerclops_invulnerable_state.png");
            }
            @Override
            protected RenderType getRenderType(Deerclops animatable, @Nullable MultiBufferSource bufferSource) {
                return RenderType.entityTranslucentEmissive(getTextureResource(animatable));
            }

            @Override
            public void render(PoseStack poseStack, Deerclops animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

                renderType = RenderType.entityTranslucentEmissive(getTextureResource(animatable));

                float parti = animatable.isFarForInvulnerable()? animatable.transitionTicks + partialTick : animatable.transitionTicks - partialTick;
                float alpha = Mth.clamp(parti, 0, 30) / 30;


                Color color = getRenderer().getRenderColor(animatable, partialTick, packedLight);
                color = Color.ofARGB(alpha, color.getRedFloat(), color.getRedFloat(), color.getRedFloat());
                int c = color.argbInt();

                getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, renderType,
                        bufferSource.getBuffer(renderType), partialTick, LightTexture.FULL_SKY, packedOverlay,
                        c);

                float swing = (float) (1.1 + alpha * 0.05f * Math.sin((animatable.tickCount + partialTick) * 0.1f));
                poseStack.scale(swing,swing,swing);
                getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, renderType,
                        bufferSource.getBuffer(renderType), partialTick, LightTexture.FULL_SKY, packedOverlay,
                        c);

            }
        });
    }


    @Override
    public void reRender(BakedGeoModel model, PoseStack poseStack, MultiBufferSource bufferSource, Deerclops animatable,
                         RenderType renderType, VertexConsumer buffer, float partialTick,
                         int packedLight, int packedOverlay, int colour) {
        if(animatable.transitionTicks == 0) return;
        poseStack.pushPose();
        actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
        postRender(poseStack, animatable, model, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
        poseStack.popPose();
    }

}
