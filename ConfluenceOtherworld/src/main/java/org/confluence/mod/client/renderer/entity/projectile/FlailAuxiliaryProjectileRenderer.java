package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.flail.DripplerCripplerProjectile;
import org.confluence.mod.common.entity.projectile.flail.FlailAuxiliaryProjectile;
import org.confluence.mod.common.entity.projectile.flail.FlaironBubbleProjectile;

/// 将链锤附属弹幕按面向摄像机的精灵绘制。
///
/// <p>泰拉瑞亚原始弹幕是平面像素图，不需要为每一种弹幕额外建立空的 GeckoLib 模型。</p>
public final class FlailAuxiliaryProjectileRenderer<T extends FlailAuxiliaryProjectile> extends EntityRenderer<T> {
    private static final ResourceLocation FLOWER = Confluence.asResource("textures/entity/projectile/flail/flower_power_petal.png");
    private static final ResourceLocation DRIPPLER = Confluence.asResource("textures/entity/projectile/flail/drippler_crippler.png");
    private static final ResourceLocation FLAIRON = Confluence.asResource("textures/entity/projectile/flail/flairon_bubble.png");

    public FlailAuxiliaryProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity instanceof FlaironBubbleProjectile) {
            return FLAIRON;
        }
        if (entity instanceof DripplerCripplerProjectile) {
            return DRIPPLER;
        }
        return FLOWER;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float scale = entity instanceof FlaironBubbleProjectile bubble
                ? bubble.getRenderScale()
                : 0.3F;
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        PoseStack.Pose pose = poseStack.last();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        vertex(consumer, pose, packedLight, -1.0F, -1.0F, 0.0F, 1.0F);
        vertex(consumer, pose, packedLight, 1.0F, -1.0F, 1.0F, 1.0F);
        vertex(consumer, pose, packedLight, 1.0F, 1.0F, 1.0F, 0.0F);
        vertex(consumer, pose, packedLight, -1.0F, 1.0F, 0.0F, 0.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, float y, float u, float v) {
        consumer.vertex(pose.pose(), x, y, 0.0F)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
