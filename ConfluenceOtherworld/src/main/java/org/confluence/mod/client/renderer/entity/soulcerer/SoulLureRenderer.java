package org.confluence.mod.client.renderer.entity.soulcerer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;

public class SoulLureRenderer extends EntityRenderer<SoulLureProjectile> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/gui/sprites/hud/soulcerer_skill/soul_lure.png");
    private static final float HALF_SIZE = 0.5F;

    public SoulLureRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    /// 使用已有引魂图标绘制面向镜头的弹幕。
    @Override
    public void render(SoulLureProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(SoulSkillParameters.SOUL_ENTITY_RENDER_SCALE, SoulSkillParameters.SOUL_ENTITY_RENDER_SCALE, SoulSkillParameters.SOUL_ENTITY_RENDER_SCALE);
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        vertex(consumer, pose, -HALF_SIZE, -HALF_SIZE, 0.0F, 1.0F);
        vertex(consumer, pose, HALF_SIZE, -HALF_SIZE, 1.0F, 1.0F);
        vertex(consumer, pose, HALF_SIZE, HALF_SIZE, 1.0F, 0.0F);
        vertex(consumer, pose, -HALF_SIZE, HALF_SIZE, 0.0F, 0.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    /// 写入自发光图标四边形的单个顶点。
    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float u, float v) {
        consumer.vertex(pose.pose(), x, y, 0.0F).color(-1).uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0.0F, 0.0F, 1.0F).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(SoulLureProjectile entity) {
        return TEXTURE;
    }
}

