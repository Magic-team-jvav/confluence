package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.BoomerangProjectile;

/// 按回旋镖当前水平飞行方向和返程状态渲染其物品模型。
public class BoomerangProjectileRenderer extends EntityRenderer<BoomerangProjectile> {
    public BoomerangProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BoomerangProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        Vec3 motion = entity.getDeltaMovement();
        double horizontal = motion.x * motion.x + motion.z * motion.z;
        if (horizontal > 1.0E-7) {
            float yaw = (float) Math.atan2(motion.z, motion.x);
            poseStack.mulPose(Axis.YN.rotation(yaw + (entity.isReturning() ? (float) Math.PI : 0.0F)));
        }

        /// 回旋镖应该像水平抛出的片状武器，而不是竖起来的盾牌。
        /// 先把物品片放平，再围绕片面法线自转；垂直速度只影响实体轨迹，不参与模型俯仰。
        float animation = entity.tickCount + partialTick + entity.getVisualRotationOffset();
        poseStack.mulPose(Axis.XN.rotationDegrees((float) (90.0F - 20.0F * Math.cos(animation / 10.0F))));
        poseStack.mulPose(Axis.ZN.rotation(entity.tickCount + partialTick));
        Minecraft.getInstance().getItemRenderer().renderStatic(entity.getWeapon(), ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), entity.getId());
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BoomerangProjectile entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
