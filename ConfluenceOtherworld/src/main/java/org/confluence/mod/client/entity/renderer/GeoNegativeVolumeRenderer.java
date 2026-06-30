package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.client.entity.model.GeoNormalModel;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Geo负体积/局部发光渲染器。主 pass 渲染轮廓（只显非发光骨骼），
 * 二次 pass 用 eyes shader 渲染发光骨骼。
 */
public class GeoNegativeVolumeRenderer<T extends Entity & GeoEntity> extends GeoNormalRenderer<T> {

    private boolean init;
    protected final List<GeoBone> toHide = new ArrayList<>();
    protected final List<GeoBone> notToHide = new ArrayList<>();
    private List<String> toHideNames = new ArrayList<>();
    private List<String> toHideGroupNames;
    private List<String> notToHideGroupNames;
    private InitStrategy initRunnable;

    enum InitStrategy {
        SIMPLE {
            @Override
            void apply(BakedGeoModel model, GeoNegativeVolumeRenderer<?> r) {
                model.topLevelBones().get(0).getChildBones().forEach(b -> {
                    if (r.toHideNames.contains(b.getName())) r.toHide.add(b);
                    else r.notToHide.add(b);
                });
                r.toHideNames = null;
            }
        },
        COMPLEX {
            @Override
            void apply(BakedGeoModel model, GeoNegativeVolumeRenderer<?> r) {
                for (String name : r.toHideGroupNames) model.getBone(name).ifPresent(r.toHide::add);
                for (String name : r.notToHideGroupNames) model.getBone(name).ifPresent(r.notToHide::add);
                r.toHideGroupNames = null;
                r.notToHideGroupNames = null;
            }
        };

        abstract void apply(BakedGeoModel model, GeoNegativeVolumeRenderer<?> r);
    }

    public GeoNegativeVolumeRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        this(context, new GeoNormalModel<>(path));
    }

    public GeoNegativeVolumeRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
        this.shadowRadius = 0;
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Override
            protected RenderType getRenderType(T animatable) {
                return GeoNegativeVolumeRenderer.this.getGlowRenderType(animatable, this.getTextureResource(animatable));
            }
        });
    }

    @Override
    public GeoNegativeVolumeRenderer<T> withScale(float scale) {
        super.withScale(scale);
        return this;
    }

    public GeoNegativeVolumeRenderer<T> addBoneToGlow(List<String> boneNames) {
        this.toHideNames.addAll(boneNames);
        this.initRunnable = InitStrategy.SIMPLE;
        return this;
    }

    public GeoNegativeVolumeRenderer<T> addBoneToGlow(String boneName) {
        this.toHideNames.add(boneName);
        this.initRunnable = InitStrategy.SIMPLE;
        return this;
    }

    public GeoNegativeVolumeRenderer<T> setBoneToGlow(List<String> toHide, List<String> notToHide) {
        this.initRunnable = InitStrategy.COMPLEX;
        this.toHideGroupNames = toHide;
        this.notToHideGroupNames = notToHide;
        return this;
    }

    protected void processInit(BakedGeoModel model) {
        if (this.initRunnable != null) {
            this.initRunnable.apply(model, this);
            this.initRunnable = null;
        }
    }

    protected void processHide(boolean isReRender) {
        toHide.forEach(b -> b.setHidden(!isReRender));
        notToHide.forEach(b -> b.setHidden(isReRender));
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (!init && !isReRender) {
            init = true;
            this.processInit(model);
        }
        this.processHide(isReRender);
        if (isReRender) return;
        super.preRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    protected RenderType getGlowRenderType(T animatable, ResourceLocation texture) {
        return RenderType.eyes(texture);
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderStateShardAccessor.createTextOutline(texture);
    }
}
