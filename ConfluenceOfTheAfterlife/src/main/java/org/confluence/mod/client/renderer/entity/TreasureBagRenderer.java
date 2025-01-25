package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import org.confluence.mod.common.entity.TreasureBagItemEntity;
import org.joml.Matrix4f;

public class TreasureBagRenderer extends ItemEntityRenderer {
    private static final float length = 0.8F;
    private static final float width = 0.3F;
    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);

    public TreasureBagRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ItemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity instanceof TreasureBagItemEntity entity1 && entity1.isOwner(Minecraft.getInstance().player)) {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, 0xF000F0);

            float delta = ((float) entity.level().getGameTime() + partialTicks) / 200.0F;
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lightning());
            poseStack.pushPose();
            float y = Mth.sin(((float) entity.getAge() + partialTicks) / 10.0F + entity.bobOffs) * 0.1F;
            poseStack.translate(0.0F, 0.35F + y, 0.0F);

            RandomSource randomSource = new LegacyRandomSource(20250125L);
            for (int i = 0; i < 12; i++) {
                poseStack.mulPose(Axis.XP.rotationDegrees(i * randomSource.nextInt(60) + delta * randomSource.nextInt(30)));
                poseStack.mulPose(Axis.YN.rotationDegrees(i * randomSource.nextInt(60) + delta * randomSource.nextInt(60)));
                poseStack.mulPose(Axis.ZP.rotationDegrees(i * randomSource.nextInt(60) + delta * randomSource.nextInt(90)));
                Matrix4f matrix4f = poseStack.last().pose();
                vertex1(vertexConsumer, matrix4f, randomSource);
                vertex2(vertexConsumer, matrix4f, randomSource);
                vertex3(vertexConsumer, matrix4f, randomSource);
                vertex4(vertexConsumer, matrix4f, randomSource);
            }

            poseStack.popPose();
        }
    }

    private static void vertex1(VertexConsumer vertexConsumer, Matrix4f matrix4f, RandomSource randomSource) {
        vertexConsumer.addVertex(matrix4f, 0.0F, 0.0F, 0.0F).setColor(randomSource.nextInt(255), randomSource.nextInt(255), randomSource.nextInt(255), 255);
    }

    private static void vertex2(VertexConsumer vertexConsumer, Matrix4f matrix4f, RandomSource randomSource) {
        vertexConsumer.addVertex(matrix4f, -HALF_SQRT_3 * width, length, -0.5F * width).setColor(randomSource.nextInt(255), randomSource.nextInt(255), 0, 0);
    }

    private static void vertex3(VertexConsumer vertexConsumer, Matrix4f matrix4f, RandomSource randomSource) {
        vertexConsumer.addVertex(matrix4f, HALF_SQRT_3 * width, length, -0.5F * width).setColor(randomSource.nextInt(255), randomSource.nextInt(255), 0, 0);
    }

    private static void vertex4(VertexConsumer vertexConsumer, Matrix4f matrix4f, RandomSource randomSource) {
        vertexConsumer.addVertex(matrix4f, 0.0F, length, width).setColor(randomSource.nextInt(255), randomSource.nextInt(255), 0, 0);
    }
}
