package org.confluence.mod.integration.terra_entity.trail;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.mod.common.entity.projectile.sword.NightEdgeProjectile;
import org.confluence.terraentity.api.entity.trail.ITrail;
import org.confluence.terraentity.entity.util.trail.PositionPoseProperties;
import org.confluence.terraentity.entity.util.trail.TrailProperties;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Iterator;
import java.util.Queue;

public class TerraSwordTrail implements ITrail<NightEdgeProjectile> {
    TrailProperties properties;
    public Queue<PositionPoseProperties> trailsQueue;

    public TerraSwordTrail(float size, float widthScale, int color) {
        this.properties = new TrailProperties(size, widthScale, 5, color, color);
        this.trailsQueue = new java.util.LinkedList<>();
    }

    @Override
    public void generateTrail(NightEdgeProjectile holder, int ticks) {
        if(trailsQueue.size() >= 100){
            trailsQueue.poll();
        }

        if(holder.lifetime - ticks <= 0){
            trailsQueue.poll();
        }else {

            if (ticks > 1 && holder.getOwner() != null) {
                trailsQueue.add(new PositionPoseProperties(holder.position().subtract(holder.getOwner().position()),
                        holder.getXRot() * 0.017453292F, holder.getYRot() * 0.017453292F));
            }
        }
    }


    public void generateTrail(NightEdgeProjectile holder, int ticks, Vec3 entityPos, float partialTicks) {
        if(trailsQueue.size() >= 100){
            trailsQueue.poll();
        }

        if(holder.lifetime - ticks <= 4){
            trailsQueue.poll();
        }else {

            float lerpRotx = Mth.lerp(partialTicks, holder.xRotO, holder.getXRot());
            float lerpRoty = Mth.lerp(partialTicks, holder.yRotO, holder.getYRot());

            if (ticks > 1 && holder.getOwner() != null) {
                trailsQueue.add(new PositionPoseProperties(entityPos,
                        lerpRotx * 0.017453292F, lerpRoty * 0.017453292F));
            }
        }
    }

    @Override
    public TrailProperties getTrailProperties() {
        return properties;
    }

    @OnlyIn(Dist.CLIENT)
    public void renderTrail(NightEdgeProjectile holder, Vec3 entityPos, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.actualRender(holder, trailsQueue, entityPos, poseStack, bufferSource, packedLight, ModClientSetups.TERRA_SWORD_RENDER_TYPE);
//        poseStack.scale(1.2f, 1.2f, 1.2f);
//        this.actualRender(holder, trailsQueue, entityPos, poseStack, bufferSource, packedLight, glow);
    }

    @OnlyIn(Dist.CLIENT)
    protected void actualRender(NightEdgeProjectile holder, Queue<PositionPoseProperties> trailsQueue, Vec3 entityPos, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, RenderType renderType){
        Iterator<PositionPoseProperties> trails = trailsQueue.iterator();

        int size = trailsQueue.size();

        TrailProperties properties = getTrailProperties();
        if (!trails.hasNext()) return;

        poseStack.pushPose();
        Matrix4f matrix4f = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        int color = 0XFFFFFF;

//        int color = getTrailProperties().colorTo();
        int red = FastColor.ARGB32.red(color);
        int green = FastColor.ARGB32.green(color);
        int blue = FastColor.ARGB32.blue(color);

        int colorFrom = 0XFFFFFF;
//        int colorFrom = getTrailProperties().colorFrom();
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
        float _progress = 0;
        PositionPoseProperties p = trails.next();
        Vec3 lastPos = p.position.subtract(entityPos);
        int i = 0;

        while (trails.hasNext()) {
            Vec3 pos0 = lastPos;
            p = trails.next();
            Vec3 pos1 = p.position.subtract(entityPos);

            float progress = i / (float) size;
            float width = properties.widthScale() * progress;
            int alpha = (int) (200 * progress);
            if(!trails.hasNext()){
                alpha = 20;
            }
            int lerpRed = (int) Mth.lerp(progress, red, redFrom);
            int lerpGreen = (int) Mth.lerp(progress, green, greenFrom);
            int lerpBlue = (int) Mth.lerp(progress, blue, blueFrom);
            int argb = FastColor.ARGB32.color(alpha, lerpRed, lerpGreen, lerpBlue);

//            Vec3 side = dir.cross(camDir).normalize();
            float rotx = p.xrot;
            float roty = -p.yrot;

            Vector3f d = new Vector3f(0,0,1);
            new Quaternionf().rotateY(roty).rotateX(rotx).transform(d);

            Vec3 side = new Vec3(d.normalize());

            Vec3 left0 ;
            Vec3 left00;
            Vec3 right0 ;
            Vec3 right00;
            Vec3 left11 = pos1.add(side.scale(+width * properties.fadeWidthFactor()));
            Vec3 right11 = pos1.add(side.scale(-width * properties.fadeWidthFactor()));
            Vec3 left1 = pos1.add(side.scale(+width*5));
            Vec3 right1 = pos1.add(side.scale(-width*5));
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


            ITrail.addVertex(buffer, matrix4f, left0, lastColor)
                    .setUv(0, _progress)
                    .setNormal(poseStack.last(), 0, 1, 0).setUv2(packedLight & 65535, packedLight >> 16 & 65535).setOverlay(OverlayTexture.NO_OVERLAY);
            ITrail.addVertex(buffer, matrix4f, right0, lastColor)
                    .setUv(1, _progress)
                    .setNormal(poseStack.last(), 0, 1, 0).setUv2(packedLight & 65535, packedLight >> 16 & 65535).setOverlay(OverlayTexture.NO_OVERLAY);
            ITrail.addVertex(buffer, matrix4f, right1, argb)
                    .setUv(1, progress)
                    .setNormal(poseStack.last(), 0, 1, 0).setUv2(packedLight & 65535, packedLight >> 16 & 65535).setOverlay(OverlayTexture.NO_OVERLAY);
            ITrail.addVertex(buffer, matrix4f, left1, argb)
                    .setUv(0, progress)
                    .setNormal(poseStack.last(), 0, 1, 0).setUv2(packedLight & 65535, packedLight >> 16 & 65535).setOverlay(OverlayTexture.NO_OVERLAY);

//            ITrail.addVertex(buffer, matrix4f, left00, lastColor & 0x00FFFFFF)
//                    .setUv(_progress, _progress)
//                    .setNormal(poseStack.last(), 0, 1, 0).setUv2(packedLight & 65535, packedLight >> 16 & 65535).setOverlay(OverlayTexture.NO_OVERLAY);
//            ITrail.addVertex(buffer, matrix4f, left0, lastColor)
//                    .setUv(progress, _progress)
//                    .setNormal(poseStack.last(), 0, 1, 0).setUv2(packedLight & 65535, packedLight >> 16 & 65535).setOverlay(OverlayTexture.NO_OVERLAY);
//            ITrail.addVertex(buffer, matrix4f, left1, argb)
//                    .setUv(progress, progress)
//                    .setNormal(poseStack.last(), 0, 1, 0).setUv2(packedLight & 65535, packedLight >> 16 & 65535).setOverlay(OverlayTexture.NO_OVERLAY);
//            ITrail.addVertex(buffer, matrix4f, left11, argb& 0x00FFFFFF)
//                    .setUv(_progress, progress)
//                    .setNormal(poseStack.last(), 0, 1, 0).setUv2(packedLight & 65535, packedLight >> 16 & 65535).setOverlay(OverlayTexture.NO_OVERLAY);
//
//            ITrail.addVertex(buffer, matrix4f, right0, lastColor)
//                    .setUv(_progress,_progress)
//                    .setNormal(poseStack.last(), 0, 1, 0).setUv2(packedLight & 65535, packedLight >> 16 & 65535).setOverlay(OverlayTexture.NO_OVERLAY);
//            ITrail.addVertex(buffer, matrix4f, right00, lastColor& 0x00FFFFFF)
//                    .setUv(progress, _progress)
//                    .setNormal(poseStack.last(), 0, 1, 0).setUv2(packedLight & 65535, packedLight >> 16 & 65535).setOverlay(OverlayTexture.NO_OVERLAY);
//            ITrail.addVertex(buffer, matrix4f, right11, argb& 0x00FFFFFF)
//                    .setUv(progress, progress)
//                    .setNormal(poseStack.last(), 0, 1, 0).setUv2(packedLight & 65535, packedLight >> 16 & 65535).setOverlay(OverlayTexture.NO_OVERLAY);
//            ITrail.addVertex(buffer, matrix4f, right1, argb)
//                    .setUv(_progress, progress)
//                    .setNormal(poseStack.last(), 0, 1, 0).setUv2(packedLight & 65535, packedLight >> 16 & 65535).setOverlay(OverlayTexture.NO_OVERLAY);


            o0 = left1;
            o1 = right1;
            o2 = left11;
            o3 = right11;
            lastPos = pos1;
            i++;
            lastColor = argb;
            _progress = progress;
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }
}
