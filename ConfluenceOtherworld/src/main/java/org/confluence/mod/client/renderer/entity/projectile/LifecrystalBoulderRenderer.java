package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.client.model.block.LifeCrystalBlockModel;
import org.confluence.mod.common.entity.projectile.boulder.LifecrystalBoulderEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import com.mojang.math.Axis;

public class LifecrystalBoulderRenderer extends GeoEntityRenderer<LifecrystalBoulderEntity> {
    public LifecrystalBoulderRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new LifecrystalBoulderModel());
    }

    @Override
    public void render(LifecrystalBoulderEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        // 先让巨石面向移动方向
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() - 90.0F));

        // 应用滚动旋转和缩放
        float radius = entity.radius;
        poseStack.translate(0, radius, 0);
        poseStack.mulPose(Axis.ZP.rotation(-(Mth.lerp(partialTick, entity.rotateO, entity.rotate))));
        poseStack.translate(0, -radius, 0);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    private static class LifecrystalBoulderModel extends GeoModel<LifecrystalBoulderEntity> {
        public static final ResourceLocation ENTITY_MODEL = LifeCrystalBlockModel.ENTITY_MODEL;
        public static final ResourceLocation ENTITY_TEXTURE = LifeCrystalBlockModel.ENTITY_TEXTURE;

        @Override
        public ResourceLocation getModelResource(LifecrystalBoulderEntity animatable) {
            return ENTITY_MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(LifecrystalBoulderEntity animatable) {
            return ENTITY_TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(LifecrystalBoulderEntity animatable) {
            return null;
        }
    }
}
