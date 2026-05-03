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
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.entity.TreasureBagItemEntity;
import org.confluence.mod.util.DateUtils;
import org.joml.Matrix4f;

import java.util.Calendar;


public class TreasureBagRenderer extends ItemEntityRenderer {
    private static final float length = 1.0F;
    private static final float width = 0.4F;
    private final long time;

    public TreasureBagRenderer(EntityRendererProvider.Context context) {
        super(context);
        Calendar instance = DateUtils.getCalendar();
        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH) + 1;
        int day = instance.get(Calendar.DAY_OF_MONTH);
        this.time = Long.parseLong(String.valueOf(year) + (month < 10 ? "0" + month : month) + (day < 10 ? "0" + day : day));
    }

    @Override
    public void render(ItemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity instanceof TreasureBagItemEntity entity1 && entity1.isOwner(Minecraft.getInstance().player)) {
            this.shadowStrength = 1.0F;
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, 0xF000F0);

            float delta = ((float) entity.level().getGameTime() + partialTicks) / 200.0F;
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lightning());
            poseStack.pushPose();
            float y = Mth.sin(((float) entity.getAge() + partialTicks) / 10.0F + entity.bobOffs) * 0.1F;
            poseStack.translate(0.0F, 0.35F + y, 0.0F);

            RandomSource randomSource = RandomSource.create(time);
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
        } else {
            this.shadowStrength = 0.0F;
        }
    }

    private static void vertex1(VertexConsumer vertexConsumer, Matrix4f matrix4f, RandomSource randomSource) {
        vertexConsumer.addVertex(matrix4f, 0.0F, 0.0F, 0.0F).setColor(randomSource.nextInt(255), randomSource.nextInt(255), randomSource.nextInt(255), 255);
    }

    private static void vertex2(VertexConsumer vertexConsumer, Matrix4f matrix4f, RandomSource randomSource) {
        vertexConsumer.addVertex(matrix4f, -LibMathUtils.HALF_SQRT_3 * width, length, -0.5F * width).setColor(randomSource.nextInt(255), randomSource.nextInt(255), randomSource.nextInt(255), 0);
    }

    private static void vertex3(VertexConsumer vertexConsumer, Matrix4f matrix4f, RandomSource randomSource) {
        vertexConsumer.addVertex(matrix4f, LibMathUtils.HALF_SQRT_3 * width, length, -0.5F * width).setColor(randomSource.nextInt(255), randomSource.nextInt(255), randomSource.nextInt(255), 0);
    }

    private static void vertex4(VertexConsumer vertexConsumer, Matrix4f matrix4f, RandomSource randomSource) {
        vertexConsumer.addVertex(matrix4f, 0.0F, length, width).setColor(randomSource.nextInt(255), randomSource.nextInt(255), randomSource.nextInt(255), 0);
    }
}
