package org.confluence.terraentity.client.entity.renderer.proj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.terraentity.entity.proj.SkullProjectile;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SkullProjectileRenderer extends EntityRenderer<SkullProjectile> {
    private static final ResourceLocation SKELETON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/skeleton.png");
    private final ModelPart model;

    public SkullProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = context.bakeLayer(ModelLayers.SKELETON).getChild("head");
    }

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(SkullProjectile entity) {
        return SKELETON_LOCATION;
    }

    @Override
    public void render(SkullProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotation(Mth.PI));
//        System.out.println(entity.yHeadRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.yHeadRot));
        poseStack.translate(0, -0.25, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        poseStack.translate(0, 0.25, 0);
        model.render(poseStack, bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
