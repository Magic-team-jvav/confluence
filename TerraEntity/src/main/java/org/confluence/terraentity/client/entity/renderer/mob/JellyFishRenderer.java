package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.monster.JellyFish;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import java.util.Optional;

public class JellyFishRenderer extends GeoNormalRenderer<JellyFish> {

    public JellyFishRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        super(renderManager, path);
    }

    public JellyFishRenderer(EntityRendererProvider.Context renderManager, GeoModel<JellyFish> model) {
        super(renderManager, model, false, 1,0);

        this.addRenderLayer(new AutoGlowingGeoLayer<>(this){

            @Override
            public void render(PoseStack poseStack, JellyFish animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType,
                               MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick,
                               int packedLight, int packedOverlay) {
                if(animatable.getSkills().index == 0){
                    return;
                }
                super.render(poseStack,animatable,bakedModel,renderType,bufferSource,buffer,partialTick,packedLight,packedOverlay);
            }

            @Override
            protected RenderType getRenderType(JellyFish animatable, @Nullable MultiBufferSource bufferSource) {
                return RenderType.entityTranslucentEmissive(getTextureLocation(animatable));
            }
        });
    }

    @Override
    protected void adjustPose(PoseStack poseStack, JellyFish animatable, BakedGeoModel model, float partialTick) {

        if(animatable.getSkills().index == 0){

            Vec3 from = new Vec3(0,1,0);
            Vec3 dir = animatable.lastMovement.lerp(animatable.currentMovement, partialTick);
            Optional<Integer> curContinue = animatable.getSkills().getCurContinue();
            if(curContinue.isEmpty()){
                return;
            }
            float progress;

            if(animatable.getSkills().tick < curContinue.get() + 2){
                progress = (animatable.getSkills().tick + partialTick) / curContinue.get();
                progress = progress * ( 1 - progress) * 7;
            }else{
                progress = 1;
            }

            Vec3 lerp = from.lerp(dir, Mth.clamp(progress, 0, 1));

            Quaternionf rotate = TEUtils.rotateFromV1ToV2(new Vector3f(0,1,0), lerp.toVector3f());
            poseStack.mulPose(rotate);

        }

        poseStack.mulPose(Axis.YN.rotationDegrees((animatable.tickCount + partialTick) * 3 ));
    }

    @Override
    public void reRender(BakedGeoModel model, PoseStack poseStack, MultiBufferSource bufferSource, JellyFish animatable,
                          RenderType renderType, VertexConsumer buffer, float partialTick,
                          int packedLight, int packedOverlay, int colour) {
        poseStack.pushPose();
        actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
        postRender(poseStack, animatable, model, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
        poseStack.popPose();
    }
}
