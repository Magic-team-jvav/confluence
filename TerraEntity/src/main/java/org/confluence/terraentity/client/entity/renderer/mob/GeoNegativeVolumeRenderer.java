package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.util.TriConsumer;
import org.confluence.terraentity.client.entity.model.GeoNormalModel;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Geo负体积/局部发光 渲染器
 * <p>若需要定制渲染类型和复杂的骨骼，需要继承此类</p>
 */
public class GeoNegativeVolumeRenderer<T extends Entity & GeoEntity> extends GeoNormalRenderer<T> {
    public static final Function<ResourceLocation, RenderType> TEXT_OUTLINE = Util.memoize(texture ->
            RenderType.create("terraentity_outline_text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
                    .setShaderState(RenderType.RENDERTYPE_TEXT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                    .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(RenderType.LIGHTMAP)
                    .createCompositeState(true)));

    private boolean init = false;
    protected List<GeoBone> toHide = new ArrayList<>();
    protected List<GeoBone> notToHide = new ArrayList<>();

    List<String> toHideNames = new ArrayList<>();

    List<String> toHideGroupNames;
    List<String> notToHideGroupNames;

    InitStrategy initRunnable = null; // 初始化策略

    enum InitStrategy implements BiConsumer<BakedGeoModel, GeoNegativeVolumeRenderer<?>> {
        SIMPLE {
            @Override
            public void accept(BakedGeoModel model, GeoNegativeVolumeRenderer<?> renderer) {
                model.topLevelBones().getFirst().getChildBones().forEach(b -> {
                    if (renderer.toHideNames.contains(b.getName())) {
                        renderer.toHide.add(b);
                    } else {
                        renderer.notToHide.add(b);
                    }
                });
                renderer.toHideNames = null;
            }
        },
        COMPLEX {
            final TriConsumer<BakedGeoModel, List<GeoBone>, List<String>> process = (model, addTo, groupNames) -> {
                groupNames.stream()
                        .map(s -> model.getBone(s).orElse(null))
                        .filter(Objects::nonNull)
                        .forEach(addTo::add);
            };

            @Override
            public void accept(BakedGeoModel model, GeoNegativeVolumeRenderer<?> renderer) {
                process.accept(model, renderer.toHide, renderer.toHideGroupNames);
                process.accept(model, renderer.notToHide, renderer.notToHideGroupNames);
                renderer.toHideGroupNames = null;
                renderer.notToHideGroupNames = null;
            }
        }
    }

    public GeoNegativeVolumeRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        this(renderManager, path, true);
    }

    public GeoNegativeVolumeRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path, boolean ifRotX) {
        this(renderManager, path, ifRotX, 1.0F, 0.0F);
    }

    public GeoNegativeVolumeRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path, boolean ifRotX, float scale, float offsetY) {
        this(renderManager, new GeoNormalModel<>(path), ifRotX, scale, offsetY);
    }

    public GeoNegativeVolumeRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model, boolean ifRotX, float scale, float offsetY) {
        super(renderManager, model, ifRotX, scale, offsetY);
        this.shadowRadius = 0;
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this) {

            @Override
            protected RenderType getRenderType(T animatable, @Nullable MultiBufferSource bufferSource) {
                return GeoNegativeVolumeRenderer.this.getGlowRenderType(animatable, this.getTextureResource(animatable));
            }
        });
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!init && !isReRender) {
            init = true;
            this.processInit(model);
        }

        this.processHide(isReRender);

        if (isReRender) {
            return;
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, colour);
    }

    /**
     * 只添加发光部分
     * <p>模型分组需遵从：顶层为一个骨骼</p>
     *
     * @param boneName 发光骨骼名称列表
     */
    public GeoNegativeVolumeRenderer<T> addBoneToGlow(List<String> boneName) {
        this.toHideNames.addAll(boneName);
        this.initRunnable = InitStrategy.SIMPLE;
        return this;
    }

    /**
     * 只添加发光部分
     * <p>模型分组需遵从：顶层为一个骨骼</p>
     *
     * @param boneName 发光骨骼名称
     */
    public GeoNegativeVolumeRenderer<T> addBoneToGlow(String boneName) {
        this.toHideNames.add(boneName);
        this.initRunnable = InitStrategy.SIMPLE;
        return this;
    }

    /**
     * 此方法和上面的方法任选其一即可。此方法可以应对复杂骨骼情况，但模型需要遵从：所有方块为叶节点，且添加的骨骼为叶结点的父节点
     */
    public GeoNegativeVolumeRenderer<T> setBoneToGlow(List<String> toHide, List<String> notToHide) {
        this.initRunnable = InitStrategy.COMPLEX;
        this.toHideGroupNames = toHide;
        this.notToHideGroupNames = notToHide;
        return this;
    }

    /**
     * 如果需要定制化隐藏骨骼，需要重写此方法
     */
    protected void processInit(BakedGeoModel model) {
        if (this.initRunnable != null) {
            this.initRunnable.accept(model, this);
            this.initRunnable = null;
        }
    }

    /**
     * 默认不需要重新，重写上面的方法就行
     */
    protected void processHide(boolean isReRender) {
        toHide.forEach(b -> {
            b.setHidden(!isReRender);
        });
        notToHide.forEach(b -> {
            b.setHidden(isReRender);
        });
    }

    protected RenderType getGlowRenderType(T animatable, ResourceLocation texture) {
        // 发光部分
        return RenderType.eyes(texture);
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        // 非发光部分不渲染阴影
        return TEXT_OUTLINE.apply(texture);
    }
}
