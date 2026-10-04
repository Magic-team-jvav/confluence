package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractAttachmentEntityGeoRenderer<T extends AttachmentEntity> extends AbstractAttachmentEntityRenderer<T> implements GeoRenderer<T> {

    protected final GeoObjectRenderer<T> delegate;
    protected float currentAlpha = 1.0F;

    protected AbstractAttachmentEntityGeoRenderer(AttachmentEntityGeoModel<T> model) {
        this.delegate = new GeoObjectRenderer<>(model) {
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

    /**
     * 第一人称下按距离淡出：基类用 {@code context.visualNode / partialTick / model.alphaDistanceFactor} 计算，
     * 这里把结果缓存进 {@link #currentAlpha}，供 {@link #getRenderColor} 与 {@link #getRenderType} 使用。
     */
    @Override
    protected float getAlphaModify() {
        currentAlpha = super.getAlphaModify();
        return currentAlpha;
    }

    /**
     * Geo 模型本体提交。
     * <p>
     * 基类 {@code modelModify} 已经在调用本方法之前施加了
     * visualNode 朝向 + {@code model} 三轴缩放/平移，因此这里必须<b>直接在当前姿态下</b>提交
     * delegate，绝不能再重复施加同一套变换（否则会双重变换）。
     * </p>
     */
    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        T entity = context.entity;
        RenderType renderType = getRenderType(entity, getTextureLocation(entity), bufferSource, context.partialTick);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
        delegate.render(poseStack, entity, bufferSource, renderType, vertexConsumer, context.packedLight);
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
    public static class AttachmentEntityGeoModel<T extends AttachmentEntity> extends GeoModel<T> {

        private final Function<T, ResourceLocation> basePathResolver;
        private ResourceLocation cachedPath;
        private ResourceLocation model;
        private ResourceLocation texture;
        private ResourceLocation animation;

        public AttachmentEntityGeoModel(Function<T, ResourceLocation> basePathResolver) {
            this.basePathResolver = basePathResolver;
        }

        @Override
        public RenderType getRenderType(T animatable, ResourceLocation texture) {
            return RenderType.entityTranslucentCull(texture);
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

    }
}
