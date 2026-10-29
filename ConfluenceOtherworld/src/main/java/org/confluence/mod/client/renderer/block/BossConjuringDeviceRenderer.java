package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.model.block.BossConjuringBlockModel;
import org.confluence.mod.common.block.functional.BossConjuringBlock;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.List;

/// 机械 Boss 召唤装置的渲染器。
///
/// 与妖精模型一致：灯塔灯头（顶部三个立方体）是负尺寸的同心外壳，必须开启背面剔除只画朝外的
/// 一面，否则外层会把内层的反面也一起画出来，看上去糊成一团。灯头另外单独走一遍发光层：主阶段
/// 只画灯身骨骼并隐藏灯头，第二阶段反过来只画灯头骨骼，用全亮光照提交，因此灯头不受环境光影响，
/// 同一几何也不会被重复提交。另外两个装置没有发光骨骼，直接走默认渲染。
public class BossConjuringDeviceRenderer extends GeoBlockRenderer<BossConjuringBlock.BEntity> {
    /// 灯头骨骼：灯塔顶部的三个立方体（核心与两层外壳）。
    private static final List<String> GLOW_BONES = List.of("bone2");
    /// 灯身骨骼：底座、四条支脚与顶板。
    private static final List<String> BODY_BONES = List.of("bone");

    /// 负尺寸检测的缓存模型；三个装置共用本渲染器，模型切换时重新检测。
    private BakedGeoModel cullingModel;
    private boolean faceCulling;

    public BossConjuringDeviceRenderer() {
        super(new BossConjuringBlockModel());
    }

    /// 负尺寸外壳开启背面剔除，只保留朝外的表面；普通模型维持不剔除的原版行为。
    @Override
    public RenderType getRenderType(BossConjuringBlock.BEntity animatable, ResourceLocation texture,
                                   @Nullable MultiBufferSource bufferSource, float partialTick) {
        return needsFaceCulling(animatable) ? RenderType.entityCutout(texture) : RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public void defaultRender(PoseStack poseStack, BossConjuringBlock.BEntity animatable, MultiBufferSource bufferSource,
                              @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        if (!isLighthouse(animatable)) {
            super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
            return;
        }
        BakedGeoModel model = getGeoModel().getBakedModel(getGeoModel().getModelResource(animatable));
        // 主阶段：隐藏灯头，只画灯身，保持世界光照。
        setHidden(model, GLOW_BONES, true);
        try {
            super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
        } finally {
            setHidden(model, GLOW_BONES, false);
        }
        // 发光阶段：隐藏灯身，只画灯头，全亮并由裁切通道保留明确的实体表面。
        setHidden(model, BODY_BONES, true);
        try {
            ResourceLocation texture = getTextureLocation(animatable);
            RenderType glow = getRenderType(animatable, texture, bufferSource, partialTick);
            poseStack.pushPose();
            try {
                // 重绘阶段不会再次应用方块中心的半格偏移，这里补上与主阶段一致的位置。
                poseStack.translate(0.5, 0.0, 0.5);
                reRender(model, poseStack, bufferSource, animatable, glow, bufferSource.getBuffer(glow),
                        partialTick, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            } finally {
                poseStack.popPose();
            }
        } finally {
            setHidden(model, BODY_BONES, false);
        }
    }

    private void setHidden(BakedGeoModel model, List<String> bones, boolean hidden) {
        for (String name : bones) model.getBone(name).ifPresent(bone -> bone.setHidden(hidden));
    }

    /// 按烘焙模型缓存负尺寸检测；模型变体切换或资源重载后重新检查。
    private boolean needsFaceCulling(BossConjuringBlock.BEntity animatable) {
        BakedGeoModel model = getGeoModel().getBakedModel(getGeoModel().getModelResource(animatable));
        if (model != cullingModel) {
            cullingModel = model;
            faceCulling = model.topLevelBones().stream().anyMatch(BossConjuringDeviceRenderer::needsFaceCulling);
        }
        return faceCulling;
    }

    private static boolean needsFaceCulling(GeoBone bone) {
        return bone.getCubes().stream().anyMatch(BossConjuringDeviceRenderer::needsFaceCulling)
                || bone.getChildBones().stream().anyMatch(BossConjuringDeviceRenderer::needsFaceCulling);
    }

    private static boolean needsFaceCulling(GeoCube cube) {
        var size = cube.size();
        return size.x < 0 || size.y < 0 || size.z < 0;
    }

    private static boolean isLighthouse(BossConjuringBlock.BEntity animatable) {
        return animatable.getBlockState().getBlock() instanceof BossConjuringBlock block
                && block.getVariant() == BossConjuringBlock.Variant.TWINS;
    }
}
