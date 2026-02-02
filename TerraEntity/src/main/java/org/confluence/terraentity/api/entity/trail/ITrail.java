package org.confluence.terraentity.api.entity.trail;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.terraentity.client.util.ShaderUtil;
import org.confluence.terraentity.entity.util.trail.TrailProperties;
import org.joml.Matrix4f;

import java.util.Iterator;
import java.util.Queue;

/**
 * 拖尾效果接口
 * @param <T> 拖尾容器持有者
 */
public interface ITrail<T> {

    void generateTrail(T holder, int ticks);

    TrailProperties getTrailProperties();


    /**
     * 默认使用的位置队列，如果需要实现更好的拖尾效果，不应该使用这个方法
     * @param holder 持有者
     * @param trailsQueue 位置队列
     * @param entityPos 持有者位置
     */
    @OnlyIn(Dist.CLIENT)
    default void renderTrail(T holder, Queue<Vec3> trailsQueue, Vec3 entityPos, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        Iterator<Vec3> trails = trailsQueue.iterator();
        int size = trailsQueue.size();

        TrailProperties properties = getTrailProperties();
        if (!trails.hasNext()) return;

        poseStack.pushPose();
        Matrix4f matrix4f = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(ShaderUtil.TRAIL_RENDER_TYPE.get());

        Minecraft mc = Minecraft.getInstance();
        Vec3 camDir =  new Vec3(mc.gameRenderer.getMainCamera().getLookVector());

        int color = getTrailProperties().colorTo();
        int red = FastColor.ARGB32.red(color);
        int green = FastColor.ARGB32.green(color);
        int blue = FastColor.ARGB32.blue(color);
        int colorFrom = getTrailProperties().colorFrom();
        int redFrom = FastColor.ARGB32.red(colorFrom);
        int greenFrom = FastColor.ARGB32.green(colorFrom);
        int blueFrom = FastColor.ARGB32.blue(colorFrom);
        int lastColor = FastColor.ARGB32.color(0, red, green, blue);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_ONE, GlConst.GL_ONE);
        Vec3 o0 = null;
        Vec3 o1 = null;
        Vec3 o2 = null;
        Vec3 o3 = null;
        Vec3 lastPos = trails.next().subtract(entityPos);
        int i = 0;

        while (trails.hasNext()) {
            Vec3 pos0 = lastPos;
            Vec3 pos1 = trails.next().subtract(entityPos);

            Vec3 dir = pos1.subtract(pos0).normalize();

            float progress = i / (float) size;
            float width = properties.widthScale() * progress;
            int alpha = (int) (200 * progress);
            int lerpRed = (int) Mth.lerp(progress, red, redFrom);
            int lerpGreen = (int) Mth.lerp(progress, green, greenFrom);
            int lerpBlue = (int) Mth.lerp(progress, blue, blueFrom);
            int argb = FastColor.ARGB32.color(alpha, lerpRed, lerpGreen, lerpBlue);

//            Vec3 side = dir.cross(camDir).normalize();
            Vec3 side = dir.cross(camDir).normalize().scale(width);
            Vec3 left0 ;
            Vec3 left00;
            Vec3 right0 ;
            Vec3 right00;
            Vec3 left11 = pos1.add(side.scale(+width * properties.fadeWidthFactor()));
                Vec3 right11 = pos1.add(side.scale(-width * properties.fadeWidthFactor()));
            Vec3 left1 = pos1.add(side.scale(+width));
            Vec3 right1 = pos1.add(side.scale(-width));
            if(o0 != null) {
                left0 = o0;
                right0 = o1;
                left00 = o2;
                right00 = o3;
            }else {
                left0 = pos0.add(side.scale(+width));
                right0 = pos0.add(side.scale(-width));
                left00 = pos0.add(side.scale(+width));
                right00 = pos0.add(side.scale(-width));
            }

            addVertex(buffer, matrix4f, left0, lastColor);
            addVertex(buffer, matrix4f, right0, lastColor);
            addVertex(buffer, matrix4f, right1, argb);
            addVertex(buffer, matrix4f, left1, argb);

            addVertex(buffer, matrix4f, left00, lastColor & 0x00FFFFFF);
            addVertex(buffer, matrix4f, left0, lastColor);
            addVertex(buffer, matrix4f, left1, argb);
            addVertex(buffer, matrix4f, left11, argb& 0x00FFFFFF);

            addVertex(buffer, matrix4f, right0, lastColor);
            addVertex(buffer, matrix4f, right00, lastColor& 0x00FFFFFF);
            addVertex(buffer, matrix4f, right11, argb& 0x00FFFFFF);
            addVertex(buffer, matrix4f, right1, argb);


            o0 = left1;
            o1 = right1;
            o2 = left11;
            o3 = right11;
            lastPos = pos1;
            i++;
            lastColor = argb;
        }
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    static VertexConsumer addVertex(VertexConsumer buffer, Matrix4f matrix, Vec3 pos, int argb) {
        return buffer.addVertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .setColor(argb);
    }

}
