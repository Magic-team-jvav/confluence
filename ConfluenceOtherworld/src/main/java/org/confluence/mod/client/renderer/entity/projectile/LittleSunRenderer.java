package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.mana.AbstractLittleCelestialEntity;
import org.confluence.mod.common.entity.projectile.mana.LittleSunEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/// 小日光：小小的发光立方体，飞行时会朝向移动方向。
public class LittleSunRenderer extends GeoEntityRenderer<LittleSunEntity> {
    private static final ResourceLocation MODEL = Confluence.asResource("geo/entity/proj/little_sun.geo.json");
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/proj/little_sun.png");

    public LittleSunRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
    }

    @Override
    public void render(LittleSunEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        faceMovement(entity, entityYaw, partialTick, poseStack);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    /// 让模型朝向移动方向；附着在敌怪头顶时保持朝向宿主
    static void faceMovement(AbstractLittleCelestialEntity entity, float entityYaw, float partialTick, PoseStack poseStack) {
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
    }

    private static final class Model extends GeoModel<LittleSunEntity> {
        @Override
        public ResourceLocation getModelResource(LittleSunEntity animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(LittleSunEntity animatable) {
            return TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(LittleSunEntity animatable) {
            return null;
        }
    }
}
