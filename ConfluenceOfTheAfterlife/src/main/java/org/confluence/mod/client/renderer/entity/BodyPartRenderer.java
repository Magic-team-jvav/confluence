package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.entity.DeadBodyPartEntity;
import org.confluence.terraentity.client.boss.renderer.GeoBossRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.List;

public class BodyPartRenderer extends EntityRenderer<DeadBodyPartEntity> {
    public BodyPartRenderer(EntityRendererProvider.Context context){
        super(context);
    }

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(@NotNull DeadBodyPartEntity entity){
        return Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity).getTextureLocation(entity);
    }

    @Override
    public void render(@NotNull DeadBodyPartEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight){
        Object bone = entity.bone;
        Entity dying = entity.dyingEntity;
        // Geo生物
        if(dying != null && bone instanceof GeoCube geoCube && Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(dying) instanceof GeoEntityRenderer geoEntityRenderer){
            // 把计算Y轴中心放到前面，如果cube有问题就提前返回
            GeoQuad[] twoQuads = new GeoQuad[2];
            for(GeoQuad quad : geoCube.quads()){
                if(quad == null) continue;
                if(twoQuads[0] == null){
                    twoQuads[0] = quad;
                }else {
                    twoQuads[1] = quad;
                    break;
                }
            }
            if(twoQuads[0] == null){
                return;
            }
            if(twoQuads[1] == null){
                twoQuads[1] = twoQuads[0];
            }
            float minY=Float.MAX_VALUE;
            float maxY=-Float.MAX_VALUE;
            for(GeoVertex vertex : twoQuads[0].vertices()){
                minY = Math.min(minY, vertex.position().y);
                maxY = Math.max(maxY, vertex.position().y);
            }

            float centerY = (minY + maxY) / 2;

            poseStack.pushPose();

            if(geoEntityRenderer instanceof GeoBossRenderer<?, ?> bossRenderer){
                float scale = bossRenderer.getScale();
                poseStack.scale(scale, scale, scale);
                poseStack.translate(0, bossRenderer.getYOffset(), 0);
            }
            // GeoGeo的奇妙Y轴旋转
            poseStack.mulPose(Axis.YP.rotationDegrees(-dying.getYRot() + 180));
            poseStack.mulPose(Axis.XP.rotationDegrees(dying.getXRot()));
            // GeoGeo的奇妙Y轴偏移
            poseStack.translate(0, 0.01f, 0);
            // 还原bone的各级旋转
            List<Vector3f> rots = entity.boneRots;
            List<Vector3f> pivots = entity.bonePivots;
            for(int i = rots.size() - 1; i >= 0; i--){
                Vector3f boneRot = rots.get(i);
                Vector3f bonePivot = pivots.get(i);
                poseStack.translate(bonePivot.x, bonePivot.y, bonePivot.z);
                poseStack.mulPose(Axis.ZP.rotation(boneRot.z));
                poseStack.mulPose(Axis.YP.rotation(boneRot.y));
                poseStack.mulPose(Axis.XP.rotation(boneRot.x));
                poseStack.translate(-bonePivot.x, -bonePivot.y, -bonePivot.z);
            }

            // 模拟打飞的旋转
            poseStack.translate(0, centerY, 0);
            if(entity.stop){
                poseStack.mulPose(Axis.ZP.rotationDegrees((entity.animTick + 1) * 10 * entity.rotZ));
                poseStack.mulPose(Axis.YP.rotationDegrees((entity.animTick + 1) * 10 * entity.rotY));
                poseStack.mulPose(Axis.XP.rotationDegrees((entity.animTick + 1) * 10 * entity.rotX));
            }else{
                poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.animTick * 10 * entity.rotZ, (entity.animTick + 1) * 10 * entity.rotZ)));
                poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.animTick * 10 * entity.rotY, (entity.animTick + 1) * 10 * entity.rotY)));
                poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, entity.animTick * 10 * entity.rotX, (entity.animTick + 1) * 10 * entity.rotX)));
            }
            poseStack.translate(0, -centerY, 0);

            geoEntityRenderer.renderCube(poseStack, geoCube,
                bufferSource.getBuffer(geoEntityRenderer.getRenderType(dying, geoEntityRenderer.getTextureLocation(dying), bufferSource, partialTick)),
                packedLight,
                OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false)),
                geoEntityRenderer.getRenderColor(dying, partialTick, packedLight).argbInt());
            poseStack.popPose();
        }
    }

}
