package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;

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
            public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
                this.objectRenderTranslations = new Matrix4f(poseStack.last().pose());
            }
        };
    }

    protected AbstractAttachmentEntityGeoRenderer(ResourceLocation location) {
        this(animatable -> location);
    }

    /// 支持按召唤物状态切换模型：基础路径随实体变化（例如致命球的三形态）。
    protected AbstractAttachmentEntityGeoRenderer(Function<T, ResourceLocation> basePathResolver) {
        this.delegate = new GeoObjectRenderer<>(new AttachmentEntityGeoModel<>(basePathResolver)) {
            @Override
            public Color getRenderColor(T animatable, float partialTick, int packedLight) {
                return AbstractAttachmentEntityGeoRenderer.this.getRenderColor(animatable, partialTick, packedLight);
            }

            @Override
            public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
                this.objectRenderTranslations = new Matrix4f(poseStack.last().pose());
            }
        };
    }

    /// 将第一人称距离淡出同步给 Geo 模型颜色与透明材质。
    @Override
    protected float getAlphaModify() {
        currentAlpha = super.getAlphaModify();
        return currentAlpha;
    }

    /// 直接在公共框架已完成变换的姿态下提交 Geo 模型，避免重复变换。
    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        T entity = context.entity;
        RenderType renderType = getRenderType(entity, getTextureLocation(entity), bufferSource, context.partialTick);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
        delegate.render(poseStack, entity, bufferSource, renderType, vertexConsumer, context.packedLight, context.partialTick);
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

    /// 按基础路径解析并缓存模型、贴图和动画资源。
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

        /// 解析当前基础路径对应的模型、贴图与动画资源，路径没变时沿用缓存。
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
