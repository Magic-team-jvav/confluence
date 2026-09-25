package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.confluence.mod.client.entity.model.MartianProbeModel;
import org.confluence.mod.common.entity.monster.MartianProbe;

/** Four conical scan bands with one spherical cap closing the bottom. */
public final class MartianProbeRenderer extends GeoNegativeVolumeRenderer<MartianProbe> {
    private static final int AZIMUTH_SEGMENTS = 48;
    private static final int CAP_SEGMENTS = 12;
    private static final int LAYERS = 4;
    private static final float FULL_REVOLUTION = Mth.TWO_PI;
    private static final float CONE_HEIGHT = (float) MartianProbe.SCAN_CONE_HEIGHT;
    private static final float CONE_BASE_RADIUS = (float) MartianProbe.SCAN_CONE_BASE_RADIUS;
    private static final float CAP_SPHERE_RADIUS = (float) MartianProbe.SCAN_CAP_SPHERE_RADIUS;
    private static final float CAP_CENTER_DEPTH = (float) MartianProbe.SCAN_CAP_CENTER_DEPTH;
    private static final float MAX_DEPTH = (float) MartianProbe.SCAN_MAX_DEPTH;

    public MartianProbeRenderer(EntityRendererProvider.Context context) {
        super(context, new MartianProbeModel());
        shadowRadius = 0.0F;
    }

    @Override
    public boolean shouldRender(MartianProbe entity, Frustum frustum,
                                double cameraX, double cameraY, double cameraZ) {
        return super.shouldRender(entity, frustum, cameraX, cameraY, cameraZ)
                || entity.shouldRender(cameraX, cameraY, cameraZ)
                && frustum.isVisible(entity.getBoundingBox()
                .inflate(CONE_BASE_RADIUS, 0.0D, CONE_BASE_RADIUS)
                .expandTowards(0.0D, -MAX_DEPTH, 0.0D));
    }

    @Override
    public void render(MartianProbe entity, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight) {
        super.render(entity, yaw, partialTick, poseStack, buffers, packedLight);
        MartianProbe.State state = entity.getProbeState();
        boolean fleeing = state == MartianProbe.State.ALERT || state == MartianProbe.State.FLEEING;
        int red = fleeing ? 255 : 35;
        int green = fleeing ? 22 : 255;
        int blue = fleeing ? 28 : 75;

        poseStack.pushPose();
        VertexConsumer vertices = buffers.getBuffer(ScanRenderType.TYPE);
        float originY = entity.getEyeHeight();
        for (int layer = 0; layer < LAYERS; layer++) {
            float topDepth = CONE_HEIGHT * layer / LAYERS;
            float bottomDepth = CONE_HEIGHT * (layer + 1.0F) / LAYERS;
            float topRadius = CONE_BASE_RADIUS * topDepth / CONE_HEIGHT;
            float bottomRadius = CONE_BASE_RADIUS * bottomDepth / CONE_HEIGHT;
            // Each lower band is fainter overall, but the bottom of each band is more opaque.
            float topOpacity = 0.28F - 0.07F * layer;
            float bottomOpacity = topOpacity * 1.25F;
            for (int segment = 0; segment < AZIMUTH_SEGMENTS; segment++) {
                float a0 = FULL_REVOLUTION * segment / AZIMUTH_SEGMENTS;
                float a1 = FULL_REVOLUTION * (segment + 1.0F) / AZIMUTH_SEGMENTS;
                emit(vertices, poseStack, originY, topDepth, topRadius, a0, red, green, blue, topOpacity);
                emit(vertices, poseStack, originY, bottomDepth, bottomRadius, a0, red, green, blue, bottomOpacity);
                emit(vertices, poseStack, originY, bottomDepth, bottomRadius, a1, red, green, blue, bottomOpacity);
                emit(vertices, poseStack, originY, topDepth, topRadius, a1, red, green, blue, topOpacity);
            }
        }
        renderBottomCap(vertices, poseStack, originY, red, green, blue);
        poseStack.popPose();
    }

    private static void renderBottomCap(VertexConsumer vertices, PoseStack poseStack, float originY,
                                        int red, int green, int blue) {
        for (int slice = 0; slice < CAP_SEGMENTS; slice++) {
            float topDepth = CONE_HEIGHT + (MAX_DEPTH - CONE_HEIGHT) * slice / CAP_SEGMENTS;
            float bottomDepth = CONE_HEIGHT + (MAX_DEPTH - CONE_HEIGHT) * (slice + 1.0F) / CAP_SEGMENTS;
            float topRadius = capRadius(topDepth);
            float bottomRadius = capRadius(bottomDepth);
            float topOpacity = 0.07F - 0.04F * slice / CAP_SEGMENTS;
            float bottomOpacity = 0.07F - 0.04F * (slice + 1.0F) / CAP_SEGMENTS;
            for (int segment = 0; segment < AZIMUTH_SEGMENTS; segment++) {
                float a0 = FULL_REVOLUTION * segment / AZIMUTH_SEGMENTS;
                float a1 = FULL_REVOLUTION * (segment + 1.0F) / AZIMUTH_SEGMENTS;
                emit(vertices, poseStack, originY, topDepth, topRadius, a0, red, green, blue, topOpacity);
                emit(vertices, poseStack, originY, bottomDepth, bottomRadius, a0, red, green, blue, bottomOpacity);
                emit(vertices, poseStack, originY, bottomDepth, bottomRadius, a1, red, green, blue, bottomOpacity);
                emit(vertices, poseStack, originY, topDepth, topRadius, a1, red, green, blue, topOpacity);
            }
        }
    }

    private static float capRadius(float depth) {
        float fromCenter = depth - CAP_CENTER_DEPTH;
        return Mth.sqrt(Math.max(0.0F, CAP_SPHERE_RADIUS * CAP_SPHERE_RADIUS - fromCenter * fromCenter));
    }

    private static void emit(VertexConsumer vertices, PoseStack poseStack, float originY,
                             float depth, float radius, float azimuth,
                             int red, int green, int blue, float alpha) {
        vertices.vertex(poseStack.last().pose(), radius * Mth.sin(azimuth), originY - depth,
                        radius * Mth.cos(azimuth))
                .color(red, green, blue, Mth.clamp((int) (alpha * 255.0F), 0, 255))
                .endVertex();
    }

    private static final class ScanRenderType extends RenderStateShard {
        private static final RenderType TYPE = RenderType.create("confluence_martian_probe_scan_volume",
                DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 4096, false, true,
                RenderType.CompositeState.builder()
                        .setShaderState(POSITION_COLOR_SHADER)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(false));

        private ScanRenderType() {
            super(null, null, null);
        }
    }
}
