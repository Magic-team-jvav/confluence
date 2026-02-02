package org.confluence.terraentity.client.entity.renderer.proj;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.proj.BoomerangProjectile;
import org.joml.Matrix4f;

import java.util.Iterator;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class BoomerangProjRenderer extends EntityRenderer<BoomerangProjectile> {

    public BoomerangProjRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(BoomerangProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
//        int pack = OverlayTexture.pack((int) (partialTick*Math.sin(partialTick/10)), (int) (partialTick*Math.cos(partialTick/10)));


        // 位置插值
        Vec3 v = entity.getDeltaMovement();

        if(entity.trail != null) {
            double x =  Mth.lerp(partialTick, entity.xo, entity.getX());
            double y =  Mth.lerp(partialTick, entity.yo, entity.getY());
            double z =  Mth.lerp(partialTick, entity.zo, entity.getZ());

            Vec3 pos = new Vec3(x, y, z);
//        renderTrail(entity.trailQueue.iterator(), pos , poseStack, bufferSource, color, entity.trailQueue.size());
//        renderTrail(entity.trailQueue2.iterator(), pos , poseStack, bufferSource, color, entity.trailQueue2.size());

            entity.trail.renderTrail(entity, entity.trailQueue, pos, poseStack, bufferSource, packedLight);
            entity.trail.renderTrail(entity, entity.trailQueue2, pos, poseStack, bufferSource, packedLight);
        }

        float yaw = (float) Math.atan2(v.z, v.x);
        // 旋转到正前方yaw
        poseStack.mulPose(Axis.YN.rotation( (yaw + (float) Math.PI * (entity.isBacking?1:0))));
        float pitch = (float) Math.atan2(v.y, Math.sqrt(v.x*v.x + v.z*v.z));
        // 旋转到正前方pitch
        poseStack.mulPose(Axis.ZN.rotation( pitch * (entity.isBacking? 1:-1) ));

        // 旋转到水平位置
        poseStack.mulPose(Axis.XN.rotationDegrees((float) (90 -  20 *Math.cos((entity.tickCount + entity.randomRotation) / 10.0)  )));

        // 随时间旋转
        poseStack.mulPose(Axis.ZN.rotation((entity.tickCount + partialTick)));

        Minecraft.getInstance().getItemRenderer().renderStatic(entity.weapon, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY,poseStack, bufferSource,  entity.level(),0);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);




    }

    public static void renderTrail(Iterator<Vec3> trails, Vec3 entityPos, PoseStack poseStack, MultiBufferSource bufferSource, int color, int size) {

        if (!trails.hasNext()) return;

        poseStack.pushPose();
        Matrix4f matrix4f = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(TRAIL_RENDER_TYPE);

        Minecraft mc = Minecraft.getInstance();
        Vec3 camDir =  new Vec3(mc.gameRenderer.getMainCamera().getLookVector());

        int red = FastColor.ARGB32.red(color);
        int green = FastColor.ARGB32.green(color);
        int blue = FastColor.ARGB32.blue(color);

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
            float width = 0.15f * progress;
            int alpha = (int) (200 * progress);
            int argb = FastColor.ARGB32.color(alpha, red, green, blue);

//            Vec3 side = dir.cross(camDir).normalize();
            Vec3 side = dir.cross(camDir).normalize().scale(width);
            Vec3 left0 ;
            Vec3 left00;
            Vec3 right0 ;
            Vec3 right00;
            Vec3 left11 = pos1.add(side.scale(+width * 10));
            Vec3 right11 = pos1.add(side.scale(-width * 10));
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

            addVertex(buffer, matrix4f, left0, argb);
            addVertex(buffer, matrix4f, right0, argb);
            addVertex(buffer, matrix4f, right1, argb);
            addVertex(buffer, matrix4f, left1, argb);

            addVertex(buffer, matrix4f, left00, argb & 0x00FFFFFF);
            addVertex(buffer, matrix4f, left0, argb);
            addVertex(buffer, matrix4f, left1, argb);
            addVertex(buffer, matrix4f, left11, argb& 0x00FFFFFF);

            addVertex(buffer, matrix4f, right0, argb);
            addVertex(buffer, matrix4f, right00, argb& 0x00FFFFFF);
            addVertex(buffer, matrix4f, right11, argb& 0x00FFFFFF);
            addVertex(buffer, matrix4f, right1, argb);


            o0 = left1;
            o1 = right1;
            o2 = left11;
            o3 = right11;
            lastPos = pos1;
            i++;
        }
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private static void addVertex(VertexConsumer buffer, Matrix4f matrix, Vec3 pos, int argb) {
        buffer.addVertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .setColor(argb);
    }

    public static RenderType TRAIL_RENDER_TYPE = RenderType.create(
            "trail_render_type",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
//                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setLightmapState(NO_LIGHTMAP)
                    .setOutputState(WEATHER_TARGET)
                    .createCompositeState(false)
    );
    @Override
    public ResourceLocation getTextureLocation(BoomerangProjectile baseArrowEntity) {
        return null;
    }

}
