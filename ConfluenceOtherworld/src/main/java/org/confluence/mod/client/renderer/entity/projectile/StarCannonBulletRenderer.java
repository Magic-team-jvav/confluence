package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.mod.common.entity.projectile.StarCannonBulletEntity;

public class StarCannonBulletRenderer extends ThrownItemRenderer<StarCannonBulletEntity> {
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucentEmissive(ModClientSetups.BLOOM_TEXTURE);

    public StarCannonBulletRenderer(EntityRendererProvider.Context context) {
        super(context, 1, true);
    }

    @Override
    public void render(StarCannonBulletEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.tickCount > 2) {
            poseStack.pushPose();
            poseStack.translate(0, -0.5F, 0);
            poseStack.scale(2, 2, 2);
            poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
            PoseStack.Pose posestack$pose = poseStack.last();
            VertexConsumer vertexconsumer = buffer.getBuffer(RENDER_TYPE);
            vertex(vertexconsumer, posestack$pose, packedLight, 0F, 0F, 0F, 1F);
            vertex(vertexconsumer, posestack$pose, packedLight, 1F, 0F, 1F, 1F);
            vertex(vertexconsumer, posestack$pose, packedLight, 1F, 1F, 1F, 0F);
            vertex(vertexconsumer, posestack$pose, packedLight, 0F, 1F, 0F, 0F);
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, float y, float u, float v) {
        consumer.addVertex(pose, x - 0.5F, y - 0.25F, 0F)
                .setColor(0.2F, 0.2F, 1F, 1F)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0F, 1F, 0F);
    }
}
