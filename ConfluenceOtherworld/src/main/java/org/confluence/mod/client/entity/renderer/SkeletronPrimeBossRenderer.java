package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.SkeletronPrime;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

/// 只绘制机械骷髅王头部的本体渲染器。
///
/// {@code bone3} 归四个独立机械臂实体所有；{@code bone7} 是旋转阶段专用的头部分支。
/// 普通头部和旋转头部互斥绘制，避免两套几何同时叠加。
public class SkeletronPrimeBossRenderer extends BossGeoRenderer<SkeletronPrime> {
    public SkeletronPrimeBossRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/skeletron_prime"));
    }

    @Override
    protected void adjustPose(PoseStack poseStack, SkeletronPrime boss,
                              BakedGeoModel model, float partialTick) {
        if (!boss.isSpinning()) return;
        poseStack.translate(0.0F, 1.15F, 0.0F);
        float yaw = Mth.rotLerp(partialTick, boss.yBodyRotO, boss.yBodyRot) * Mth.DEG_TO_RAD;
        poseStack.mulPose(Axis.of(new Vector3f((float) Math.cos(yaw), 0.0F, (float) Math.sin(yaw)))
                .rotationDegrees((boss.tickCount + partialTick) * 36.0F));
        poseStack.translate(0.0F, -1.15F, 0.0F);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SkeletronPrime boss, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        String boneName = bone.getName();
        if (boneName.equals("bone7")) {
            if (boss.isSpinning()) {
                poseStack.pushPose();
                poseStack.translate(6.0D, 0.0D, 0.0D);
                super.renderRecursively(poseStack, boss, bone, renderType, bufferSource, buffer,
                        isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
                poseStack.popPose();
            }
            return;
        }
        if (boss.isSpinning() || boneName.equals("bone3")) return;
        super.renderRecursively(poseStack, boss, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
