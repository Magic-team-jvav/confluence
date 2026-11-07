package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.mana.AbstractLittleCelestialEntity;
import org.confluence.mod.common.entity.projectile.mana.LittleMoonEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/// 小月光：小小的月牙，飞行时会朝向移动方向。
public class LittleMoonRenderer extends GeoEntityRenderer<LittleMoonEntity> {
    private static final ResourceLocation MODEL = Confluence.asResource("geo/entity/proj/little_moon.geo.json");
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/proj/little_moon.png");

    public LittleMoonRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
    }

    @Override
    public void render(LittleMoonEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        LittleSunRenderer.faceMovement(entity, entityYaw, partialTick, poseStack);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    private static final class Model extends GeoModel<LittleMoonEntity> {
        @Override
        public ResourceLocation getModelResource(LittleMoonEntity animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(LittleMoonEntity animatable) {
            return TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(LittleMoonEntity animatable) {
            return null;
        }
    }
}
