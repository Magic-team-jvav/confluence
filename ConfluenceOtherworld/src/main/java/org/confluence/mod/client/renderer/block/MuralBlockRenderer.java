package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.confluence.mod.common.block.common.MuralBlock;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class MuralBlockRenderer implements BlockEntityRenderer<MuralBlock.BEntity> {
    @Override
    public void render(MuralBlock.BEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ClientLevel level = Minecraft.getInstance().level;
        Direction direction = blockEntity.getBlockState().getValue(MuralBlock.FACING);
        int light = level == null ? packedLight : LevelRenderer.getLightColor(level, blockEntity.getBlockPos().relative(direction.getOpposite()));
        blockEntity.getBack().ifPresent(datas -> renderData(datas, poseStack, light, 180 - direction.toYRot()));
        blockEntity.getLeft().ifPresent(datas -> renderData(datas, poseStack, light, 270 - direction.toYRot()));
        blockEntity.getRight().ifPresent(datas -> renderData(datas, poseStack, light, 90 - direction.toYRot()));
        blockEntity.getFront().ifPresent(datas -> renderData(datas, poseStack, light, 360 - direction.toYRot()));
    }

    @Override
    public boolean shouldRenderOffScreen(MuralBlock.BEntity blockEntity) {
        return true;
    }

    private void renderData(List<MuralBlock.MuralData> datas, PoseStack poseStack, int packedLight, float angle) {
        Font font = Minecraft.getInstance().font;
        LightTexture lightTexture = Minecraft.getInstance().gameRenderer.lightTexture();
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.translate(0.5F, 1.0F, -0.5F);

        Matrix3f normalMatrix = poseStack.last().normal();

        Vector3f localNormal = new Vector3f(0.0F, 0.0F, -1.0F);

        Vector3f worldNormal = normalMatrix.transform(localNormal);

        float brightness = 0.25F * worldNormal.y() + 0.75F * Math.abs(worldNormal.y())
                + 0.8F * Math.abs(worldNormal.z())
                + 0.6F * Math.abs(worldNormal.x());

        for (MuralBlock.MuralData data : datas) {
            poseStack.pushPose();
            poseStack.translate(data.x(), data.y(), data.z());
            poseStack.mulPose(Axis.ZP.rotation(Mth.PI + data.roll()));
            poseStack.scale(data.scale(), data.scale(), data.scale());
            data.text().ifPresent(text -> {
                FormattedCharSequence visualOrderText = text.component().getVisualOrderText();
                Matrix4f matrix = poseStack.last().pose();
                int color = (text.color() & -67108864) == 0 ? text.color() | 0xFF000000 : text.color();
                MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                if (text.dropShadow()) {
                    font.renderText(
                            visualOrderText, text.x(), text.y(), color, true, matrix,
                            buffer, Font.DisplayMode.NORMAL, text.backgroundColor(), packedLight
                    );
                    matrix = matrix.translate(0, 0, -0.03F, new Matrix4f());
                }
                font.renderText(
                        visualOrderText, text.x(), text.y(), color, false, matrix,
                        buffer, Font.DisplayMode.NORMAL, text.backgroundColor(), packedLight
                );
            });
            data.icon().ifPresent(icon -> {
                lightTexture.turnOnLightLayer();
                RenderSystem.enableDepthTest();
                RenderSystem.setShader(GameRenderer::getParticleShader);
                RenderSystem.setShaderTexture(0, icon.atlasLocation());
                BufferBuilder builder = Tesselator.getInstance().getBuilder();
                builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                Matrix4f matrix4f = poseStack.last().pose();
                int x1 = icon.x();
                int x2 = x1 + icon.uWidth();
                int y1 = icon.y();
                int y2 = y1 + icon.vHeight();
                float minU = icon.uOffset() / (float) icon.textureWidth();
                float maxU = (icon.uOffset() + (float) icon.uWidth()) / (float) icon.textureWidth();
                float minV = icon.vOffset() / (float) icon.textureHeight();
                float maxV = (icon.vOffset() + (float) icon.vHeight()) / (float) icon.textureHeight();

                int br = (int) (255 * brightness);
                int iconColor = (0xFF << 24) | (br << 16) | (br << 8) | br;

                builder.vertex(matrix4f, x1, y1, 0).uv(minU, minV).color(iconColor).uv2(packedLight).endVertex();
                builder.vertex(matrix4f, x1, y2, 0).uv(minU, maxV).color(iconColor).uv2(packedLight).endVertex();
                builder.vertex(matrix4f, x2, y2, 0).uv(maxU, maxV).color(iconColor).uv2(packedLight).endVertex();
                builder.vertex(matrix4f, x2, y1, 0).uv(maxU, minV).color(iconColor).uv2(packedLight).endVertex();
                BufferUploader.drawWithShader(builder.end());
                RenderSystem.disableDepthTest();
                lightTexture.turnOffLightLayer();
            });
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
