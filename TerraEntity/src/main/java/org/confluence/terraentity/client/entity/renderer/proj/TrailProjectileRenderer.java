package org.confluence.terraentity.client.entity.renderer.proj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.proj.TrailProjectile;
import org.joml.Matrix4f;

import java.util.List;

public class TrailProjectileRenderer extends EntityRenderer<TrailProjectile> {
    public TrailProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(TrailProjectile entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(TrailProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        List<Vec3> trails = entity.getTrails();
        if (trails.isEmpty()) return;
        
        int trailColor = entity.getTrailColor();
        float r = (trailColor & 255) / 255.0F;
        float g = (trailColor >> 8 & 255) / 255.0F;
        float b = (trailColor >> 16 & 255) / 255.0F;
        float alpha = 0.8f;

        poseStack.pushPose();
        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lightning());
        Vec3 entityPos = entity.position();

        for (int i = 1; i < trails.size(); i++) {
            Vec3 prevPos = trails.get(i-1).subtract(entityPos);
            Vec3 currentPos = trails.get(i).subtract(entityPos);

            Vec3 dir = currentPos.subtract(prevPos);
            if (dir.lengthSqr() > 0.001) {
                dir = dir.normalize();

                // 计算立方体的方向向量
                Vec3 up = new Vec3(0, 1, 0);
                Vec3 right = dir.cross(up);
                if (right.lengthSqr() < 0.001) {
                    up = new Vec3(1, 0, 0);
                    right = dir.cross(up);
                }
                right = right.normalize();
                Vec3 forward = dir.cross(right).normalize();

                float width0 = 0.15f * (i-1) / trails.size();
                float width1 = 0.15f * i / trails.size();

                // 计算立方体截面的4个顶点
                Vec3[] prevPoints = new Vec3[4];
                Vec3[] currentPoints = new Vec3[4];

                // 前一截面的4个顶点
                prevPoints[0] = prevPos.add(right.scale(width0)).add(forward.scale(width0));  // 右上
                prevPoints[1] = prevPos.add(right.scale(-width0)).add(forward.scale(width0)); // 左上
                prevPoints[2] = prevPos.add(right.scale(-width0)).add(forward.scale(-width0)); // 左下
                prevPoints[3] = prevPos.add(right.scale(width0)).add(forward.scale(-width0));  // 右下

                // 当前截面的4个顶点
                currentPoints[0] = currentPos.add(right.scale(width1)).add(forward.scale(width1));  // 右上
                currentPoints[1] = currentPos.add(right.scale(-width1)).add(forward.scale(width1)); // 左上
                currentPoints[2] = currentPos.add(right.scale(-width1)).add(forward.scale(-width1)); // 左下
                currentPoints[3] = currentPos.add(right.scale(width1)).add(forward.scale(-width1));  // 右下

                // 绘制立方体的6个面
                // 前面
                buffer.addVertex(matrix, (float)prevPoints[0].x, (float)prevPoints[0].y, (float)prevPoints[0].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)prevPoints[1].x, (float)prevPoints[1].y, (float)prevPoints[1].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)currentPoints[1].x, (float)currentPoints[1].y, (float)currentPoints[1].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)currentPoints[0].x, (float)currentPoints[0].y, (float)currentPoints[0].z)
                        .setColor(r, g, b, alpha);

                // 后面
                buffer.addVertex(matrix, (float)prevPoints[2].x, (float)prevPoints[2].y, (float)prevPoints[2].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)prevPoints[3].x, (float)prevPoints[3].y, (float)prevPoints[3].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)currentPoints[3].x, (float)currentPoints[3].y, (float)currentPoints[3].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)currentPoints[2].x, (float)currentPoints[2].y, (float)currentPoints[2].z)
                        .setColor(r, g, b, alpha);

                // 左面
                buffer.addVertex(matrix, (float)prevPoints[1].x, (float)prevPoints[1].y, (float)prevPoints[1].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)prevPoints[2].x, (float)prevPoints[2].y, (float)prevPoints[2].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)currentPoints[2].x, (float)currentPoints[2].y, (float)currentPoints[2].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)currentPoints[1].x, (float)currentPoints[1].y, (float)currentPoints[1].z)
                        .setColor(r, g, b, alpha);

                // 右面
                buffer.addVertex(matrix, (float)prevPoints[3].x, (float)prevPoints[3].y, (float)prevPoints[3].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)prevPoints[0].x, (float)prevPoints[0].y, (float)prevPoints[0].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)currentPoints[0].x, (float)currentPoints[0].y, (float)currentPoints[0].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)currentPoints[3].x, (float)currentPoints[3].y, (float)currentPoints[3].z)
                        .setColor(r, g, b, alpha);

                // 上面
                buffer.addVertex(matrix, (float)prevPoints[0].x, (float)prevPoints[0].y, (float)prevPoints[0].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)prevPoints[3].x, (float)prevPoints[3].y, (float)prevPoints[3].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)currentPoints[3].x, (float)currentPoints[3].y, (float)currentPoints[3].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)currentPoints[0].x, (float)currentPoints[0].y, (float)currentPoints[0].z)
                        .setColor(r, g, b, alpha);

                // 下面
                buffer.addVertex(matrix, (float)prevPoints[1].x, (float)prevPoints[1].y, (float)prevPoints[1].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)prevPoints[2].x, (float)prevPoints[2].y, (float)prevPoints[2].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)currentPoints[2].x, (float)currentPoints[2].y, (float)currentPoints[2].z)
                        .setColor(r, g, b, alpha);
                buffer.addVertex(matrix, (float)currentPoints[1].x, (float)currentPoints[1].y, (float)currentPoints[1].z)
                        .setColor(r, g, b, alpha);
            }
        }
        poseStack.popPose();
    }
}
