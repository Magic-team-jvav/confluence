package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.entity.boss.AncientDoom;

/// 用持续可见的发光核心标记邪教徒召唤物，粒子数量设为最低时仍可辨认并攻击。
public final class CultistSummonRenderer<T extends Entity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE = ResourceLocation.tryParse("minecraft:textures/entity/enderdragon/dragon_fireball.png");

    public CultistSummonRenderer(EntityRendererProvider.Context context) {super(context);}

    @Override
    public ResourceLocation getTextureLocation(T entity) {return TEXTURE;}

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light) {
        poseStack.pushPose();
        poseStack.translate(0.0, entity.getBbHeight() * 0.5, 0.0);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        float size = entity instanceof AncientDoom ? 0.6F + Math.min(1.0F, (entity.tickCount + partialTick) / 140.0F) * 0.6F : 1.2F;
        poseStack.scale(size, size, size);
        poseStack.mulPose(Axis.ZP.rotation((entity.tickCount + partialTick) * 0.04F));
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucent(TEXTURE));
        vertex(consumer, poseStack.last(), -1.0F, -1.0F, 0.0F, 1.0F);
        vertex(consumer, poseStack.last(), 1.0F, -1.0F, 1.0F, 1.0F);
        vertex(consumer, poseStack.last(), 1.0F, 1.0F, 1.0F, 0.0F);
        vertex(consumer, poseStack.last(), -1.0F, 1.0F, 0.0F, 0.0F);
        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffers, light);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float u, float v) {
        consumer.addVertex(pose.pose(), x, y, 0.0F).setColor(185, 120, 255, 230).setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(pose, 0.0F, 0.0F, 1.0F);
    }
}
