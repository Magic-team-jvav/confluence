package org.confluence.terra_furniture.api.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.terra_furniture.common.block.func.be.ISwayingBE;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;

public final class CommonRenderHooks {
    public static final IRenderFunctionHook<? extends ISwayingBE> SWAYING = new SwayingHook<>();
    /**
     * 摇晃方块实体的渲染钩子
     */
    @SuppressWarnings("unchecked")
    public static <O extends BlockEntity & GeoBlockEntity & ISwayingBE> IRenderFunctionHook<O> swaying() {
        return (IRenderFunctionHook<O>) SWAYING;
    }

    public static class SwayingHook<T extends BlockEntity & ISwayingBE & GeoBlockEntity> implements IRenderFunctionHook<T> {
        @Override
        public void processBefore(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
            animatable.trigController();
            poseStack.pushPose();
            // 平移到锚点位置
            Vec3 anchor = animatable.getAnchorPoint();
            poseStack.translate(anchor.x, anchor.y, anchor.z);
            // 应用旋转（绕X和Z轴）
            poseStack.mulPose(Axis.XP.rotation(animatable.getSwingX()));
            poseStack.mulPose(Axis.ZP.rotation(animatable.getSwingZ()));
            // 平移回原点
            poseStack.translate(-anchor.x, -anchor.y, -anchor.z);
        }

        @Override
        public void processAfter(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
            poseStack.popPose();
        }
    }
}
