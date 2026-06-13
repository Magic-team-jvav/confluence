package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.lib.client.DummyVertexConsumer;
import org.confluence.mod.common.entity.DeadBodyPartEntity;
import org.confluence.mod.mixin.client.model.AgeableListModelAccessor;
import org.confluence.mod.mixin.client.renderer.entity.LivingEntityRendererAccessor;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BodyPartRenderer extends EntityRenderer<DeadBodyPartEntity> {
    public BodyPartRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(DeadBodyPartEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(DeadBodyPartEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        Entity dying = entity.dyingEntity;
        if (dying == null) return;
        Object cube = entity.cube;
        Minecraft minecraft = Minecraft.getInstance();
        EntityRenderer<?> renderer = minecraft.getEntityRenderDispatcher().getRenderer(dying);
        // Geo生物
        if (renderer instanceof GeoEntityRenderer geoRenderer && dying instanceof GeoAnimatable animatable) {
            if (cube instanceof GeoCube geoCube) {
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
                geoRenderer.preRender(poseStack, dying, bakedGeoModel, null, null, false, 1, 0, 0, 0, 0, 0, 0);
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
                RenderType renderType = geoRenderer.getRenderType(animatable, textureLocation, bufferSource, partialTick);
                Color color = geoRenderer.getRenderColor(animatable, partialTick, packedLight);
                geoRenderer.renderCube(poseStack, geoCube,
                        bufferSource.getBuffer(renderType == null ? RenderType.entityCutoutNoCull(textureLocation) : renderType),
                        packedLight,
                        OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false)),
                        color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
                poseStack.popPose();
            } else if (cube instanceof GeoBone geoBone) {
                BakedGeoModel bakedGeoModel = geoRenderer.getGeoModel().getBakedModel(geoRenderer.getGeoModel().getModelResource(animatable, geoRenderer));
                geoRenderer.preRender(poseStack, dying, bakedGeoModel, null, null, false, 1, 0, 0, 0, 0, 0, 0);
                // GeoGeo的奇妙Y轴旋转
//                poseStack.mulPose(Axis.YP.rotationDegrees(-dying.getYRot() + 180));
//                poseStack.mulPose(Axis.XP.rotationDegrees(dying.getXRot()));
                // GeoGeo的奇妙Y轴偏移
//                poseStack.translate(0, 0.01f, 0);
                applyRandomRotation(entity, poseStack, partialTick);
                ResourceLocation textureLocation = geoRenderer.getTextureLocation(dying);
                RenderType renderType = geoRenderer.getRenderType(animatable, textureLocation, bufferSource, partialTick);
                Color color = geoRenderer.getRenderColor(animatable, partialTick, packedLight);
                geoRenderer.renderRecursively(poseStack, dying, geoBone, renderType, bufferSource,
                        bufferSource.getBuffer(renderType == null ? RenderType.entityCutoutNoCull(textureLocation) : renderType),
                        false, partialTick, packedLight,
                        OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false)),
                        color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
            }
        } else if (renderer instanceof LivingEntityRenderer livingRenderer && dying instanceof LivingEntity living) { // 原版生物
            if (cube instanceof ModelPart.Cube partCube && minecraft.player != null) {
                LivingEntityRendererAccessor ra = (LivingEntityRendererAccessor) livingRenderer;
                boolean visible = ra.callIsBodyVisible(living);
                boolean translucent = !visible && !entity.isInvisibleTo(minecraft.player);
                boolean glowing = minecraft.shouldEntityAppearGlowing(entity);
                RenderType renderType = entity.texture == null ? ra.callGetRenderType(living, visible, translucent, glowing) : RenderType.armorCutoutNoCull(entity.texture);
                if (renderType == null) return;
                poseStack.pushPose();
                float halfMinSide = entity.minSide / 2;
                poseStack.translate(0, halfMinSide, 0);
                applyRandomRotation(entity, poseStack, partialTick);
                poseStack.translate(-entity.xOffset, -entity.yOffset - halfMinSide, -entity.zOffset);

                if (livingRenderer.getModel() instanceof AgeableHierarchicalModel<?> model && model.young) {
                    poseStack.scale(model.youngScaleFactor, model.youngScaleFactor, model.youngScaleFactor);
                } else if (livingRenderer.getModel().young && livingRenderer.getModel() instanceof AgeableListModelAccessor model) {
                    for (ModelPart bodyPart : model.callBodyParts()) {
                        if (entity.modelPart == bodyPart) {  // FIXME: 父模型
                            float scale = 1.0F / model.getBabyBodyScale();
                            poseStack.scale(scale, scale, scale);
                            break;
                        }
                    }
                    for (ModelPart headPart : model.callHeadParts()) {
                        if (entity.modelPart == headPart) {
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

                int color = translucent ? 654311423 : -1;
                float red = FastColor.ARGB32.red(color) / 255F;
                float green = FastColor.ARGB32.green(color) / 255F;
                float blue = FastColor.ARGB32.blue(color) / 255F;
                float alpha = FastColor.ARGB32.alpha(color) / 255F;
                partCube.compile(poseStack.last(), bufferSource.getBuffer(renderType), packedLight, 655360, red, green, blue, alpha);
                poseStack.popPose();
            } else if (cube instanceof ItemStack itemStack && itemStack.getItem() instanceof Equipable equipable && livingRenderer.getModel() instanceof HumanoidModel baseModel) {
                HumanoidModel<?> humanoidModel = IClientItemExtensions.of(itemStack.getItem()).getHumanoidArmorModel(living, itemStack, null, baseModel);
                if (!(humanoidModel instanceof GeoArmorRenderer<?> geoArmorRenderer)) {
                    return;
                }
                baseModel.setupAnim(living, 0, 0, 0, living.getYHeadRot(), living.getXRot());
                poseStack.pushPose();
                EquipmentSlot equipmentSlot = equipable.getEquipmentSlot();
                applyRandomRotation(entity, poseStack, partialTick);
                boolean baby = living.isBaby();
                if (equipmentSlot == EquipmentSlot.CHEST) {
                    poseStack.translate(0, baby ? 1.1 : 0.5, baby ? -0.1 : -0.2);
                } else if (equipmentSlot == EquipmentSlot.LEGS) {
                    poseStack.translate(0, baby ? 1.4 : 1.25, 0);
                } else if (equipmentSlot == EquipmentSlot.FEET) {
                    poseStack.translate(0, 1.5, 0);
                } else if (equipmentSlot == EquipmentSlot.HEAD) {
                    poseStack.translate(0, baby ? 0.8 : -0.25, 0);
                }
                poseStack.scale(-1.0F, -1.0F, 1.0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(-dying.getYRot() + 180));
                poseStack.mulPose(Axis.XP.rotationDegrees(dying.getXRot()));
                geoArmorRenderer.prepForRender(dying, itemStack, equipable.getEquipmentSlot(), baseModel);
                geoArmorRenderer.renderToBuffer(poseStack, DummyVertexConsumer.INSTANCE, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
                poseStack.popPose();
                geoArmorRenderer.doPostRenderCleanup();
            }
        }
    }

    private void applyRandomRotation(DeadBodyPartEntity entity, PoseStack poseStack, float partialTick) {
        if (entity.still) return;
        if (entity.stop) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((entity.animTick + 1) * 10 * entity.rotZ));
            poseStack.mulPose(Axis.YP.rotationDegrees((entity.animTick + 1) * 10 * entity.rotY));
            poseStack.mulPose(Axis.XP.rotationDegrees((entity.animTick + 1) * 10 * entity.rotX));
        } else {
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.animTick * 10 * entity.rotZ, (entity.animTick + 1) * 10 * entity.rotZ)));
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.animTick * 10 * entity.rotY, (entity.animTick + 1) * 10 * entity.rotY)));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, entity.animTick * 10 * entity.rotX, (entity.animTick + 1) * 10 * entity.rotX)));
        }
    }
}
