package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.entity.DeadBodyPartEntity;
import org.confluence.mod.mixin.client.accessor.AgeableListModelAccessor;
import org.confluence.mod.mixin.client.accessor.LivingEntityRendererAccessor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.List;

public class BodyPartRenderer extends EntityRenderer<DeadBodyPartEntity> {
    public BodyPartRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(@NotNull DeadBodyPartEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(@NotNull DeadBodyPartEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight){
        Entity dying = entity.dyingEntity;
        if(dying == null) return;
        Object cube = entity.cube;
        EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(dying);
        // Geo生物
        if(cube instanceof GeoCube geoCube && renderer instanceof GeoEntityRenderer geoRenderer && dying instanceof GeoAnimatable animatable){
            // 把计算Y轴中心放到前面，如果cube有问题就提前返回
            GeoQuad[] twoQuads = new GeoQuad[2];
            for (GeoQuad quad : geoCube.quads()) {
                if (quad == null) continue;
                if (twoQuads[0] == null) {
                    twoQuads[0] = quad;
                } else {
                    twoQuads[1] = quad;
                    break;
                }
            }
            if (twoQuads[0] == null) {
                return;
            }
            if (twoQuads[1] == null) {
                twoQuads[1] = twoQuads[0];
            }
            float minY = Float.MAX_VALUE;
            float maxY = -Float.MAX_VALUE;
            for (GeoVertex vertex : twoQuads[0].vertices()) {
                minY = Math.min(minY, vertex.position().y);
                maxY = Math.max(maxY, vertex.position().y);
            }

            float centerY = (minY + maxY) / 2;

            poseStack.pushPose();

            BakedGeoModel bakedGeoModel = geoRenderer.getGeoModel().getBakedModel(geoRenderer.getGeoModel().getModelResource(animatable, geoRenderer));
            geoRenderer.preRender(poseStack, dying, bakedGeoModel, null, null, false, 1, 0, 0, 0);
            // GeoGeo的奇妙Y轴旋转
            poseStack.mulPose(Axis.YP.rotationDegrees(-dying.getYRot() + 180));
            poseStack.mulPose(Axis.XP.rotationDegrees(dying.getXRot()));
            // GeoGeo的奇妙Y轴偏移
            poseStack.translate(0, 0.01f, 0);
            // 还原bone的各级旋转
            List<Vector3f> rots = entity.boneRots;
            List<Vector3f> pivots = entity.bonePivots;
            for (int i = rots.size() - 1; i >= 0; i--) {
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
            applyRandomRotation(entity, poseStack, partialTick);
            poseStack.translate(0, -centerY, 0);

            ResourceLocation textureLocation = geoRenderer.getTextureLocation(dying);
            RenderType renderType = geoRenderer.getRenderType(dying, textureLocation, bufferSource, partialTick);
            geoRenderer.renderCube(poseStack, geoCube,
                bufferSource.getBuffer(renderType == null ? RenderType.entityCutoutNoCull(textureLocation) : renderType),
                packedLight,
                OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false)),
                geoRenderer.getRenderColor(dying, partialTick, packedLight).argbInt());
            poseStack.popPose();
        }else if(cube instanceof ModelPart.Cube partCube && renderer instanceof LivingEntityRenderer livingRenderer && dying instanceof LivingEntity living){ // 原版生物
//            if(true)return;
            LivingEntityRendererAccessor ra=(LivingEntityRendererAccessor) livingRenderer;
            boolean visible = ra.callIsBodyVisible(living);
            boolean translucent = !visible && !entity.isInvisibleTo(Minecraft.getInstance().player);
            boolean glowing = Minecraft.getInstance().shouldEntityAppearGlowing(entity);
            poseStack.pushPose();
            float halfMinSide = entity.minSide / 2;
            poseStack.translate(0, halfMinSide, 0);
            applyRandomRotation(entity, poseStack, partialTick);
//            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp((float) entity.tickCount / entity.lifetime, 0, 360)));
            poseStack.translate(-entity.xOffset, -entity.yOffset - halfMinSide, -entity.zOffset);

            if(livingRenderer.getModel() instanceof AgeableHierarchicalModel<?> model && model.young){
                poseStack.scale(model.youngScaleFactor, model.youngScaleFactor, model.youngScaleFactor);
            }else if(livingRenderer.getModel().young && livingRenderer.getModel() instanceof AgeableListModelAccessor model){
                for(ModelPart bodyPart : model.callBodyParts()){
                    if(entity.modelPart == bodyPart){  // FIXME: 父模型
                        float scale = 1.0F / model.getBabyBodyScale();
                        poseStack.scale(scale, scale, scale);
                        break;
                    }
                }
                for(ModelPart headPart : model.callHeadParts()){
                    if(entity.modelPart == headPart){
                        if (model.getScaleHead()) {
                            float scale = 1.5F / model.getBabyHeadScale();
                            poseStack.scale(scale, scale, scale);
                        }
                        break;
                    }
                }
            }
            float scale = living.getScale();
            poseStack.scale(scale, scale, scale);
            ra.callSetupRotations(living, poseStack, 0, living.yBodyRot, 1, scale);
            Vector3f modelPartRot = entity.modelPartRot;
            poseStack.mulPose(Axis.ZP.rotation(modelPartRot.z));
            poseStack.mulPose(Axis.YP.rotation(-modelPartRot.y));
            poseStack.mulPose(Axis.XP.rotation(-modelPartRot.x));
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            ra.callScale(living, poseStack, 1);

            partCube.compile(poseStack.last(), bufferSource.getBuffer(ra.callGetRenderType(living, visible, translucent, glowing)),
                packedLight, 655360, translucent ? 654311423 : -1);
            poseStack.popPose();
        }
    }

    private void applyRandomRotation(DeadBodyPartEntity entity, PoseStack poseStack, float partialTick){
        if(entity.still)return;
        if(entity.stop){
            poseStack.mulPose(Axis.ZP.rotationDegrees((entity.animTick + 1) * 10 * entity.rotZ));
            poseStack.mulPose(Axis.YP.rotationDegrees((entity.animTick + 1) * 10 * entity.rotY));
            poseStack.mulPose(Axis.XP.rotationDegrees((entity.animTick + 1) * 10 * entity.rotX));
        }else{
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.animTick * 10 * entity.rotZ, (entity.animTick + 1) * 10 * entity.rotZ)));
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.animTick * 10 * entity.rotY, (entity.animTick + 1) * 10 * entity.rotY)));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, entity.animTick * 10 * entity.rotX, (entity.animTick + 1) * 10 * entity.rotX)));
        }
    }
}
