package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.IHeadRotatable;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractAttachmentEntityGeoRenderer<T extends AttachmentEntity> extends AbstractAttachmentEntityRenderer<T> implements GeoRenderer<T> {

    protected final GeoObjectRenderer<T> delegate;
    protected float currentAlpha = 1.0F;

    protected AbstractAttachmentEntityGeoRenderer(ResourceLocation location) {
        this(animatable -> location);
    }

    /**
     * 支持按召唤物状态切换模型：基础路径随实体变化（例如致命球的三形态）。
     */
    protected AbstractAttachmentEntityGeoRenderer(Function<T, ResourceLocation> basePathResolver) {
        this.delegate = new GeoObjectRenderer<>(new AttachmentEntityGeoModel<>(basePathResolver)) {
            @Override
            public Color getRenderColor(T animatable, float partialTick, int packedLight) {
                return AbstractAttachmentEntityGeoRenderer.this.getRenderColor(animatable, partialTick, packedLight);
            }

            @Override
            public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
                this.objectRenderTranslations = new Matrix4f(poseStack.last().pose());
            }
        };
    }

    @Override
    protected float getAlphaModify(RenderContext<T> config, PathNode visualNode, float partialTick) {
        currentAlpha = super.getAlphaModify(config, visualNode, partialTick);
        return currentAlpha;
    }

    @Override
    protected void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, PathNode visualNode, RenderContext<T> context, float partialTick, int packedLight, float alpha) {
        RenderType renderType = getRenderType(entity, getTextureLocation(entity), bufferSource, partialTick);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
        delegate.render(poseStack, entity, bufferSource, renderType, vertexConsumer, packedLight);
    }

    @Override
    protected void modelModify(T entity, PoseStack poseStack, MultiBufferSource bufferSource, PathNode visualNode, RenderContext<T> context, float partialTick, int packedLight, float alpha) {
        ModelConfig<T> model = context.model;
        Quaternionf rotation = new Quaternionf(Axis.YN.rotationDegrees(visualNode.yaw()))
                .mul(Axis.XP.rotationDegrees(visualNode.pitch()))
                .mul(Axis.ZP.rotationDegrees(visualNode.roll()))
                .mul(Axis.YN.rotationDegrees(model.yawOffset))
                .mul(Axis.XP.rotationDegrees(model.pitchOffset))
                .mul(Axis.ZP.rotationDegrees(model.rollOffset));
        poseStack.pushPose();
        poseStack.translate(model.translateX, model.translateY, model.translateZ);
        poseStack.mulPose(rotation);
        poseStack.scale(model.scale, model.scale, model.scale);
        render(entity, poseStack, bufferSource, visualNode, context, partialTick, packedLight, alpha);
        poseStack.popPose();
    }

    @Override
    public Color getRenderColor(T animatable, float partialTick, int packedLight) {
        return Color.ofRGBA(1.0F, 1.0F, 1.0F, currentAlpha);
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return currentAlpha < 1.0F ? RenderType.entityTranslucent(texture) : GeoRenderer.super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public GeoModel<T> getGeoModel() {
        return delegate.getGeoModel();
    }

    @Override
    public T getAnimatable() {
        return delegate.getAnimatable();
    }

    @Override
    public List<GeoRenderLayer<T>> getRenderLayers() {
        return delegate.getRenderLayers();
    }

    @Override
    public void fireCompileRenderLayersEvent() {
        delegate.fireCompileRenderLayersEvent();
    }

    @Override
    public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel bakedGeoModel, MultiBufferSource multiBufferSource, float partialTick, int packedLight) {
        return delegate.firePreRenderEvent(poseStack, bakedGeoModel, multiBufferSource, partialTick, packedLight);
    }

    @Override
    public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel bakedGeoModel, MultiBufferSource multiBufferSource, float partialTick, int packedLight) {
        delegate.firePostRenderEvent(poseStack, bakedGeoModel, multiBufferSource, partialTick, packedLight);
    }

    @Override
    public void updateAnimatedTextureFrame(T animatable) {
        delegate.updateAnimatedTextureFrame(animatable);
    }

    /**
     * Geo model using the virtual entity resource convention.
     * <p>
     * A base path such as {@code confluence:entity/summon/hornet_baby} resolves to
     * {@code geo/entity/summon/hornet_baby.geo.json},
     * {@code animations/entity/summon/hornet_baby.animation.json} and
     * {@code textures/entity/summon/hornet_baby.png}.
     * </p>
     */
    public static final class AttachmentEntityGeoModel<T extends AttachmentEntity> extends GeoModel<T> {

        private final Function<T, ResourceLocation> basePathResolver;
        private ResourceLocation cachedPath;
        private ResourceLocation model;
        private ResourceLocation texture;
        private ResourceLocation animation;

        public AttachmentEntityGeoModel(Function<T, ResourceLocation> basePathResolver) {
            this.basePathResolver = basePathResolver;
        }

        /**
         * 解析当前基础路径对应的模型、贴图与动画资源，路径没变时沿用缓存。
         */
        private void resolve(T animatable) {
            ResourceLocation basePath = basePathResolver.apply(animatable);
            if (!basePath.equals(cachedPath)) {
                cachedPath = basePath;
                String path = basePath.getPath();
                model = ResourceLocation.fromNamespaceAndPath(basePath.getNamespace(), "geo/" + path + ".geo.json");
                texture = ResourceLocation.fromNamespaceAndPath(basePath.getNamespace(), "textures/" + path + ".png");
                animation = ResourceLocation.fromNamespaceAndPath(basePath.getNamespace(), "animations/" + path + ".animation.json");
            }
        }

        @Override
        public ResourceLocation getModelResource(T animatable) {
            resolve(animatable);
            return model;
        }

        @Override
        public ResourceLocation getTextureResource(T animatable) {
            resolve(animatable);
            return texture;
        }

        @Override
        public ResourceLocation getAnimationResource(T animatable) {
            resolve(animatable);
            return animation;
        }

        /**
         * 让实现了头部转动接口的虚拟实体把视角作用在头部骨骼上，避免整个模型跟着俯仰。
         * <p>
         * 这些 Geo 模型朝向模型空间的 -Z，渲染时靠 180 度偏航转向世界方向，
         * 所以头部骨骼的偏航与俯仰都要取反才能对准目标。
         * </p>
         */
        @Override
        public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
            if (animatable instanceof IHeadRotatable rotatable) {
                getBone("head").ifPresent(head -> {
                    var initial = head.getInitialSnapshot();
                    float partialTick = animationState.getPartialTick();
                    head.setRotX(initial.getRotX() - (float) Math.toRadians(rotatable.getHeadPitch(partialTick)));
                    head.setRotY(initial.getRotY() - (float) Math.toRadians(rotatable.getHeadYaw(partialTick)));
                });
            }
        }
    }
}
