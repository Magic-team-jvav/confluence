package org.confluence.mod.client.renderer.entity.bullet;

import PortLib.extensions.com.mojang.blaze3d.vertex.VertexConsumer.PortVertexConsumerExtension;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.init.gun.GunRenderTypes;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.init.gun.GunTrailColors;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

public class BulletRenderer extends EntityRenderer<BaseBulletEntity> {
    public BulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(@NotNull BaseBulletEntity baseBulletEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(BaseBulletEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        List<Vec3> trails = entity.getTrails();

        String colorID = entity.getColorID();
        int color = GunTrailColors.getColor(colorID);
        renderTrail(trails, entity.position(), poseStack, bufferSource, color);
        renderBulletBody(poseStack, bufferSource, color);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void renderBulletBody(PoseStack poseStack, MultiBufferSource bufferSource, int color) {
        poseStack.pushPose();
        poseStack.scale(0.1f, 0.1f, 0.1f);

        int argb = FastColor.ARGB32.color(255, FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color));
        VertexConsumer buffer = bufferSource.getBuffer(GunRenderTypes.TRAIL_RENDER_TYPE);
        Matrix4f matrix4f = poseStack.last().pose();

        float s = 0.5f;
        addVertex(buffer, matrix4f, new Vec3(-s, -s, -s), argb);
        addVertex(buffer, matrix4f, new Vec3(s, -s, -s), argb);
        addVertex(buffer, matrix4f, new Vec3(s, s, -s), argb);
        addVertex(buffer, matrix4f, new Vec3(-s, s, -s), argb);

        addVertex(buffer, matrix4f, new Vec3(-s, s, s), argb);
        addVertex(buffer, matrix4f, new Vec3(s, s, s), argb);
        addVertex(buffer, matrix4f, new Vec3(s, -s, s), argb);
        addVertex(buffer, matrix4f, new Vec3(-s, -s, s), argb);

        poseStack.popPose();
    }

    public static void renderTrail(List<Vec3> trails, Vec3 entityPos, PoseStack poseStack, MultiBufferSource bufferSource, int color) {
        if (trails.size() < 2) return;

        poseStack.pushPose();
        Matrix4f matrix4f = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(GunRenderTypes.TRAIL_RENDER_TYPE);

        Minecraft mc = Minecraft.getInstance();
        Vec3 camDir = new Vec3(mc.gameRenderer.getMainCamera().getLookVector());

        int red = FastColor.ARGB32.red(color);
        int green = FastColor.ARGB32.green(color);
        int blue = FastColor.ARGB32.blue(color);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        for (int i = 1; i < trails.size(); i++) {
            Vec3 pos0 = trails.get(i - 1).subtract(entityPos);
            Vec3 pos1 = trails.get(i).subtract(entityPos);

            Vec3 dir = pos1.subtract(pos0).normalize();

            float progress = i / (float) trails.size();
            float width = 0.15f * progress;
            int alpha = (int) (200 * progress);
            int argb = FastColor.ARGB32.color(alpha, red, green, blue);

            Vec3 side = dir.cross(camDir).normalize().scale(width);
            Vec3 left0 = pos0.add(side.scale(+width));
            Vec3 right0 = pos0.add(side.scale(-width));
            Vec3 left1 = pos1.add(side.scale(+width));
            Vec3 right1 = pos1.add(side.scale(-width));

            addVertex(buffer, matrix4f, left0, argb);
            addVertex(buffer, matrix4f, right0, argb);
            addVertex(buffer, matrix4f, right1, argb);
            addVertex(buffer, matrix4f, left1, argb);
        }
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private static void addVertex(VertexConsumer buffer, Matrix4f matrix, Vec3 pos, int argb) {
        PortVertexConsumerExtension.vertex(buffer, matrix, (float) pos.x, (float) pos.y, (float) pos.z, vertex -> PortVertexConsumerExtension.setColor(vertex, argb));
    }
}
