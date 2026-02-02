package org.confluence.terraentity.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.confluence.terraentity.client.entity.model.GeoNormalModel;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class GeoNormalRenderer<T extends Entity & GeoEntity> extends GeoEntityRenderer<T> {
    protected boolean ifRotX;
    protected float scale;
    protected float offsetY;
    protected float motionAnimThreshold = 0.01F;
    protected boolean disableRenderModel;
    Color consumeColor;

    /**
     * @param path 实体文件位置 path.namespace/textures/entity/{name}.png
     */
    public GeoNormalRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        this(renderManager, path, false,1,0);
    }
    public GeoNormalRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path, boolean ifRotX) {
        this(renderManager, path, ifRotX,1,0);
    }
    public GeoNormalRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path, boolean ifRotX, float scale, float offsetY) {
        this(renderManager, new GeoNormalModel<>(path), ifRotX,scale,offsetY);
    }
    public GeoNormalRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model, boolean ifRotX, float scale, float offsetY) {
        super(renderManager, model);
        this.ifRotX = ifRotX;
        this.scale=scale;
        this.offsetY=offsetY;
        this.shadowRadius = 0.25F;
    }


    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        poseStack.scale(scale, scale, scale);
        poseStack.translate(0, offsetY, 0);
        if(ifRotX) {
            this.rotateX(poseStack, animatable, partialTick);
        }
        this.adjustPose(poseStack, animatable, model, partialTick);

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

    }

    protected void rotateX(PoseStack poseStack, T animatable, float partialTick){
        double rad = Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) * Math.PI / 180;
        poseStack.mulPose(Axis.of(new Vector3f((float) Math.cos(rad), 0, (float) Math.sin(rad))).rotationDegrees(
                Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
    }

    protected void adjustPose(PoseStack poseStack, T animatable, BakedGeoModel model,float partialTick){

    }

    @Override
    @ApiStatus.Internal
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        consumeColor = null;
    }

    @Override
    public float getMotionAnimThreshold(T animatable) {
        return motionAnimThreshold;
    }

    public GeoNormalRenderer<T> setMotionAnimThreshold(float threshold) {
        this.motionAnimThreshold = threshold;
        return this;
    }

    @Override
    public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight,
                                  int packedOverlay, int colour) {
        if (this.disableRenderModel){
            return;
        }
        super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);
    }

    @Override
    public Color getRenderColor(T animatable, float partialTick, int packedLight) {
        Color color = consumeColor;
        if(color!= null){
            return color;
        }
        return Color.WHITE;
    }

    public void setConsumeColor(Color consumeColor) {
        this.consumeColor = consumeColor;
    }

    public GeoNormalRenderer<T> setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
        return this;
    }

    public float getScale() {
        return scale;
    }

    public GeoNormalRenderer<T> setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public GeoNormalRenderer<T> setOffsetY(float offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    public boolean isIfRotX() {
        return ifRotX;
    }

    public GeoNormalRenderer<T> setIfRotX(boolean ifRotX) {
        this.ifRotX = ifRotX;
        return this;
    }
    public GeoNormalRenderer<T> setDisableRender() {
        this.disableRenderModel = true;
        return this;
    }




}
