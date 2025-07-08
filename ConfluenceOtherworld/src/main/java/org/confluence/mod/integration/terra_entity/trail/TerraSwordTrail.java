package org.confluence.mod.integration.terra_entity.trail;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.sword.NightEdgeProjectile;
import org.confluence.terraentity.entity.util.trail.ITrail;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Iterator;
import java.util.Queue;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class TerraSwordTrail implements ITrail<NightEdgeProjectile> {
    TrailProperties properties;

    public TerraSwordTrail(int size, float widthScale, int color) {
        this.properties = new TrailProperties(size, widthScale, 5, color, color);

    }

    @Override
    public void generateTrail(NightEdgeProjectile holder, int ticks) {
        if(holder.trailQueue.size() >= 8){
            holder.trailQueue.poll();
        }

        if(holder.TIME_EXISTENCE - ticks < 4){
            holder.trailQueue.poll();
        }

        if(ticks > 1 && holder.getOwner() != null) {
            holder.trailQueue.add(holder.position().subtract(holder.getOwner().position()));
        }
    }

    @Override
    public TrailProperties getTrailProperties() {
        return properties;
    }

    @OnlyIn(Dist.CLIENT)
    public void renderTrail(NightEdgeProjectile holder, Queue<Vec3> trailsQueue, Vec3 entityPos, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        RenderType main = RenderType.create("entity_cutout_no_cull_custom", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, false,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(Confluence.asResource("textures/mask/sword.png"), true, false))
                        .setTransparencyState(NO_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(true));

        this.actualRender(holder, trailsQueue, entityPos, poseStack, bufferSource, packedLight, main);
        RenderType glow = RenderType.create("entity_translucent_emissive", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(Confluence.asResource("textures/mask/sword.png"), true, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setWriteMaskState(COLOR_WRITE)
                        .setCullState(NO_CULL)
//                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .createCompositeState(false));
        poseStack.scale(1.1f, 1.1f, 1.1f);
        this.actualRender(holder, trailsQueue, entityPos, poseStack, bufferSource, packedLight, glow);
    }

    @OnlyIn(Dist.CLIENT)
    protected void actualRender(NightEdgeProjectile holder, Queue<Vec3> trailsQueue, Vec3 entityPos, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, RenderType renderType){
        Iterator<Vec3> trails = trailsQueue.iterator();
        int size = trailsQueue.size();

        TrailProperties properties = getTrailProperties();
        if (!trails.hasNext()) return;

        poseStack.pushPose();
        Matrix4f matrix4f = poseStack.last().pose();
//        VertexConsumer buffer = bufferSource.getBuffer(ShaderUtil.TRAIL_RENDER_TYPE);
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

        Vec3 lastPos = trails.next().subtract(entityPos);
        int i = 0;

        while (trails.hasNext()) {
            Vec3 pos0 = lastPos;
            Vec3 pos1 = trails.next().subtract(entityPos);

            float progress = i / (float) size * 0.6f + 0.4f;
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
            float rotx = holder.getXRot() * 0.017453292F;
            float roty = -holder.getYRot() * 0.017453292F;
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
