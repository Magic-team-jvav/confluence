package org.confluence.mod.client.renderer.entity.projectile.sword;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.sword.StarFuryProjectile;

/**
 * 星怒落星的交叉面片渲染器；旋转角只由实体年龄决定，不保存共享可变帧计数。
 */
public final class StarFuryProjectileRenderer extends EntityRenderer<StarFuryProjectile> {
    private static final ResourceLocation TEXTURE =
            Confluence.asResource("textures/entity/star_fury_projectile.png");

    public StarFuryProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(
            StarFuryProjectile entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        poseStack.pushPose();
        float age = entity.tickCount + partialTick;
        float scale = Math.min(age * 0.2F, 1.0F) * 2.0F;
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(age * 18.0F));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        renderPlane(consumer, poseStack.last(), 0xF000F0);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        renderPlane(consumer, poseStack.last(), 0xF000F0);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private static void renderPlane(VertexConsumer consumer, PoseStack.Pose pose, int packedLight) {
        vertex(consumer, pose, packedLight, 0.0F, 0.0F, 0.0F, 1.0F);
        vertex(consumer, pose, packedLight, 1.0F, 0.0F, 1.0F, 1.0F);
        vertex(consumer, pose, packedLight, 1.0F, 1.0F, 1.0F, 0.0F);
        vertex(consumer, pose, packedLight, 0.0F, 1.0F, 0.0F, 0.0F);
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            int packedLight,
            float x,
            float y,
            float u,
            float v
    ) {
        consumer.vertex(pose.pose(), x - 0.5F, y - 0.5F, 0.0F)
                .color(255, 150, 150, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    protected int getBlockLightLevel(StarFuryProjectile entity, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(StarFuryProjectile entity) {
        return TEXTURE;
    }
}
