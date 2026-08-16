package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.SkeletronPrime;
import software.bernie.geckolib.cache.object.GeoBone;

/// 只绘制机械骷髅王头部的本体渲染器。
///
/// <p>{@code bone3} 归四个独立机械臂实体所有，{@code bone7} 是当前未启用的旋转头分支；
/// 两者都不能随本体重复绘制。筛选逻辑单独保留为纯函数，便于契约测试验证模型骨骼映射。</p>
public class SkeletronPrimeBossRenderer extends BossGeoRenderer<SkeletronPrime> {
    public SkeletronPrimeBossRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/skeletron_prime"));
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SkeletronPrime boss, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        if (!rendersHeadBone(bone.getName())) {
            return;
        }
        super.renderRecursively(poseStack, boss, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    static boolean rendersHeadBone(String boneName) {
        // bone3 属于独立机械臂实体；bone7 是尚未使用的旋转头模型分支。
        return !boneName.equals("bone3") && !boneName.equals("bone7");
    }
}
