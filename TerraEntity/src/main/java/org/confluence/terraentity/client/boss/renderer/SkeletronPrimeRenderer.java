package org.confluence.terraentity.client.boss.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import org.confluence.terraentity.client.boss.model.GeoBossModel;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.boss.skeletronprime.SkeletronPrime;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.function.Supplier;

public class SkeletronPrimeRenderer extends GeoNormalRenderer<SkeletronPrime> {


    public SkeletronPrimeRenderer(EntityRendererProvider.Context renderManager, Supplier<EntityType<SkeletronPrime>> typeSupplier) {
        super(renderManager, new GeoBossModel<>(typeSupplier), true, 1, 0);
    }

    @Override
    public void render(SkeletronPrime entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.getGeoModel().getBone("bone3").ifPresent(b->b.setHidden(true));

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

    }

    @Override
    protected void adjustPose(PoseStack poseStack, SkeletronPrime animatable, BakedGeoModel model, float partialTick) {
        if(animatable.spinTicks > 0) {
            poseStack.translate(0, 1.15f * scale, 0);
            float yRot = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
            double rad = yRot*Math.PI/180;
            float xRot = Mth.lerp(partialTick, animatable.spinTicks * 36, (animatable.spinTicks + 1) * 36);
            poseStack.mulPose(Axis.of(new Vector3f((float) Math.cos(rad), 0, (float) Math.sin(rad))).rotationDegrees(xRot));
            poseStack.translate(0, -1.15f * scale, 0);
        }

    }

    protected void rotateX(PoseStack poseStack, SkeletronPrime animatable, float partialTick){

    }

    @Override
    public void renderRecursively(PoseStack poseStack, SkeletronPrime animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                                  int packedOverlay, int colour) {

        if(bone.getName().equals("bone7")) {
            if(animatable.isSpinning()) {
                poseStack.pushPose();
                poseStack.translate(6,0,0);
                super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
                poseStack.popPose();
            }
        }else{
            if(!animatable.isSpinning()) {
                super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
            }
        }
    }
}
