package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import org.confluence.lib.util.LibMathUtils;
import org.joml.Matrix4f;

public class FallingStarRenderer extends ItemEntityRenderer {
    private static final float length = 0.5F;
    private static final float width = 0.25F;

    public FallingStarRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ItemEntity itemEntity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        super.render(itemEntity, entityYaw, partialTick, poseStack, multiBufferSource, 0xF000F0);

        float delta = ((float) itemEntity.level().getGameTime() + partialTick) / 200.0F;
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.lightning());
        poseStack.pushPose();
        float y = Mth.sin((itemEntity.getAge() + partialTick) / 10.0F + itemEntity.bobOffs) * 0.1F;
        poseStack.translate(0.0F, 0.35F + y, 0.0F);

        for (int i = 0; i < 6; i++) {
            poseStack.mulPose(Axis.XP.rotationDegrees(i * 60.0F + delta * 30.0F));
            poseStack.mulPose(Axis.YN.rotationDegrees(i * 60.0F + delta * 60.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(i * 60.0F + delta * 90.0F));
            Matrix4f matrix4f = poseStack.last().pose();
            vertex1(vertexConsumer, matrix4f);
            vertex2(vertexConsumer, matrix4f);
            vertex3(vertexConsumer, matrix4f);
            vertex4(vertexConsumer, matrix4f);
        }

        poseStack.popPose();
    }

    private static void vertex1(VertexConsumer vertexConsumer, Matrix4f matrix4f) {
        vertexConsumer.addVertex(matrix4f, 0.0F, 0.0F, 0.0F).setColor(255, 255, 255, 128);
    }

    private static void vertex2(VertexConsumer vertexConsumer, Matrix4f matrix4f) {
        vertexConsumer.addVertex(matrix4f, -LibMathUtils.HALF_SQRT_3 * width, length, -0.5F * width).setColor(255, 255, 0, 0);
    }

    private static void vertex3(VertexConsumer vertexConsumer, Matrix4f matrix4f) {
        vertexConsumer.addVertex(matrix4f, LibMathUtils.HALF_SQRT_3 * width, length, -0.5F * width).setColor(255, 255, 0, 0);
    }

    private static void vertex4(VertexConsumer vertexConsumer, Matrix4f matrix4f) {
        vertexConsumer.addVertex(matrix4f, 0.0F, length, width).setColor(255, 255, 0, 0);
    }
}
