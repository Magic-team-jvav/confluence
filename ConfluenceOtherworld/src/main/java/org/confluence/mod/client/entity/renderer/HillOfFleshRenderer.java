package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.common.entity.boss.HillOfFlesh;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/// 血肉山生成前半段从地面下方旋转升起，进入战斗后固定在遭遇锚点。
/// 模型包含大量互相穿插的肉质表面，使用透明通道避免裁切通道留下硬边。
public final class HillOfFleshRenderer extends BossGeoRenderer<HillOfFlesh> {
    // 出场动画资源持续 150 tick。
    private static final float INITIALIZATION_TICKS = 150.0F;
    private static final int BOUNDARY_SEGMENTS = 128;

    public HillOfFleshRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/hill_of_flesh"));
    }

    @Override
    public void render(HillOfFlesh hill, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        super.render(hill, entityYaw, partialTick, poseStack, buffers, packedLight);
        if (!hill.isAlive() || hill.isInitializing()) return;
        // 场地使用同步的判定半径，不继承肉山模型的缩放、转向和出场位移。
        VertexConsumer vertices = buffers.getBuffer(RenderStateShardAccessor.HILL_OF_FLESH_BOUNDARY);
        float time = -((hill.level().getGameTime() % 100000L) + partialTick) * 0.01F;
        renderBoundary(poseStack.last().pose(), vertices, hill.getOuterRadius(), -10.0F, HillOfFlesh.ARENA_HEIGHT - 10.0F, time);
        renderBoundary(poseStack.last().pose(), vertices, hill.getInnerRadius(), 0.0F, 2.0F, time);
    }

    private static void renderBoundary(Matrix4f pose, VertexConsumer vertices, float radius, float bottom, float top, float time) {
        for (int layer = 0; layer < 2; layer++) {
            float offsetU = layer == 0 ? 0 : time * 0.24F;
            float offsetV = time * (layer == 0 ? 0.4F : 0.96F);
        for (int i = 0; i < BOUNDARY_SEGMENTS; i++) {
            float u0 = i / (float) BOUNDARY_SEGMENTS;
            float u1 = (i + 1) / (float) BOUNDARY_SEGMENTS;
            float x0 = Mth.cos(u0 * Mth.TWO_PI) * radius;
            float z0 = Mth.sin(u0 * Mth.TWO_PI) * radius;
            float x1 = Mth.cos(u1 * Mth.TWO_PI) * radius;
            float z1 = Mth.sin(u1 * Mth.TWO_PI) * radius;
            boundaryVertex(pose, vertices, x0, bottom, z0, u0 + offsetU, offsetV, 90);
            boundaryVertex(pose, vertices, x1, bottom, z1, u1 + offsetU, offsetV, 90);
            boundaryVertex(pose, vertices, x1, top, z1, u1 + offsetU, 1 + offsetV, 0);
            boundaryVertex(pose, vertices, x0, top, z0, u0 + offsetU, 1 + offsetV, 0);
        }
        }
    }

    private static void boundaryVertex(Matrix4f pose, VertexConsumer vertices, float x, float y, float z,
                                       float u, float v, int alpha) {
        vertices.vertex(pose, x, y, z).color(230, 50, 50, alpha).uv(u, v)
                .overlayCoords(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
                .uv2(15728880).normal(0, 1, 0).endVertex();
    }

    @Override
    protected void adjustPose(PoseStack poseStack, HillOfFlesh hill,
                              BakedGeoModel model, float partialTick) {
        if (!hill.isInitializing()) {
            return;
        }
        float progress = Mth.clamp((hill.tickCount + partialTick) / INITIALIZATION_TICKS, 0.0F, 1.0F);
        float riseProgress = Mth.clamp(progress * 2.0F, 0.0F, 1.0F);
        float eased = riseProgress < 0.5F
                ? 2.0F * riseProgress * riseProgress
                : 1.0F - (float) Math.pow(-2.0F * riseProgress + 2.0F, 2.0F) * 0.5F;
        poseStack.translate(0.0F, Mth.lerp(eased, -15.0F, 0.0F), 0.0F);
        poseStack.mulPose(Axis.YP.rotation(riseProgress * Mth.TWO_PI * 2.0F));
    }

    @Override
    public RenderType getRenderType(HillOfFlesh hill, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
