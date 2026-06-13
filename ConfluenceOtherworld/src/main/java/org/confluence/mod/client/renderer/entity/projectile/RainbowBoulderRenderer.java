package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.client.renderer.type.ModRenderTypes;
import org.confluence.mod.common.entity.projectile.boulder.RainbowBoulderEntity;
import org.joml.Matrix4f;

import java.util.List;

public class RainbowBoulderRenderer extends BoulderRenderer<RainbowBoulderEntity> {
    public RainbowBoulderRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(RainbowBoulderEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        List<Vec3> trails = entity.getTrails();
        renderTrail(trails, entity.position(), poseStack, bufferSource);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    public static void renderTrail(List<Vec3> trails, Vec3 entityPos, PoseStack poseStack, MultiBufferSource bufferSource) {
        if (trails.size() < 2) return;

        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (Vec3 p : trails) {
            if (p.y < minY) minY = p.y;
            if (p.y > maxY) maxY = p.y;
        }
        if (minY == maxY) maxY = minY + 0.1;

        poseStack.pushPose();
        Matrix4f matrix4f = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.TRAIL_RENDER_TYPE);

        Minecraft mc = Minecraft.getInstance();
        Vec3 camDir = new Vec3(mc.gameRenderer.getMainCamera().getLookVector());

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);

        for (int i = 1; i < trails.size(); i++) {
            Vec3 p0 = trails.get(i - 1);
            Vec3 p1 = trails.get(i);
            Vec3 pos0 = p0.subtract(entityPos);
            Vec3 pos1 = p1.subtract(entityPos);

            Vec3 dir = pos1.subtract(pos0).normalize();

            float progress = i / (float) trails.size();
            int alpha = (int) (200 * progress);

            Vec3 side = dir.cross(camDir).normalize().scale(progress);
            Vec3 left0 = pos0.add(side.scale(+progress));
            Vec3 right0 = pos0.add(side.scale(-progress));
            Vec3 left1 = pos1.add(side.scale(+progress));
            Vec3 right1 = pos1.add(side.scale(-progress));

            int colorLeft0 = getRainbowColor(p0.y, minY, maxY, alpha);
            int colorRight0 = getRainbowColor(p0.y, minY, maxY, alpha);
            int colorRight1 = getRainbowColor(p1.y, minY, maxY, alpha);
            int colorLeft1 = getRainbowColor(p1.y, minY, maxY, alpha);

            vertex(buffer, matrix4f, left0, colorLeft0);
            vertex(buffer, matrix4f, right0, colorRight0);
            vertex(buffer, matrix4f, right1, colorRight1);
            vertex(buffer, matrix4f, left1, colorLeft1);
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private static int getRainbowColor(double y, double minY, double maxY, int alpha) {
        float t = (float) ((y - minY) / (maxY - minY));
        t = Math.max(0, Math.min(1, t));
        float hue = t * (300.0f / 360.0f);
        return LibMathUtils.hsvToArgb(hue, 1.0f, 1.0f, alpha);
    }

    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Vec3 pos, int argb) {
        buffer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z).color(argb).endVertex();
    }
}
