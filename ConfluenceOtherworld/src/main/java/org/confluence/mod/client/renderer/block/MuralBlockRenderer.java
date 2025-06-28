package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.confluence.mod.common.block.common.MuralBlock;
import org.joml.Matrix4f;

import java.util.List;

public class MuralBlockRenderer implements BlockEntityRenderer<MuralBlock.Entity> {
    @Override
    public void render(MuralBlock.Entity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ClientLevel level = Minecraft.getInstance().level;
        Direction direction = blockEntity.getBlockState().getValue(MuralBlock.FACING);
        int light = level == null ? packedLight : LevelRenderer.getLightColor(level, blockEntity.getBlockPos().relative(direction.getOpposite()));
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(360.0F - direction.toYRot()));
        poseStack.translate(0.5F, 1.0F, -0.5F);
        blockEntity.getBack().ifPresent(datas -> renderData(datas, poseStack, light));
        blockEntity.getLeft().ifPresent(datas -> renderData(datas, poseStack, light));
        blockEntity.getRight().ifPresent(datas -> renderData(datas, poseStack, light));
        blockEntity.getFront().ifPresent(datas -> renderData(datas, poseStack, light));
        poseStack.popPose();
    }

    private static void renderData(List<MuralBlock.MuralData> datas, PoseStack poseStack, int packedLight) {
        for (MuralBlock.MuralData data : datas) {
            poseStack.pushPose();
            poseStack.translate(data.x(), data.y(), data.z());
            poseStack.mulPose(Axis.ZP.rotation(Mth.PI + data.roll()));
            poseStack.scale(data.scale(), data.scale(), data.scale());
            data.text().ifPresent(text -> {
                FormattedCharSequence visualOrderText = text.getVisualOrderText();
                Font font = Minecraft.getInstance().font;
                font.drawInBatch(
                        visualOrderText,
                        0, 0, -1, false,
                        poseStack.last().pose(),
                        Minecraft.getInstance().renderBuffers().bufferSource(),
                        Font.DisplayMode.NORMAL,
                        0, packedLight
                );
            });
            data.icon().ifPresent(icon -> {
                Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
                RenderSystem.enableDepthTest();
                RenderSystem.setShader(GameRenderer::getParticleShader);
                RenderSystem.setShaderTexture(0, icon.atlasLocation());
                BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                Matrix4f matrix4f = poseStack.last().pose();
                int x1 = icon.x();
                int x2 = x1 + icon.uWidth();
                int y1 = icon.y();
                int y2 = y1 + icon.vHeight();
                float minU = icon.uOffset() / (float) icon.textureWidth();
                float maxU = (icon.uOffset() + (float) icon.uWidth()) / (float) icon.textureWidth();
                float minV = icon.vOffset() / (float) icon.textureHeight();
                float maxV = (icon.vOffset() + (float) icon.vHeight()) / (float) icon.textureHeight();
                builder.addVertex(matrix4f, x1, y1, 0).setUv(minU, minV).setColor(-1).setLight(packedLight);
                builder.addVertex(matrix4f, x1, y2, 0).setUv(minU, maxV).setColor(-1).setLight(packedLight);
                builder.addVertex(matrix4f, x2, y2, 0).setUv(maxU, maxV).setColor(-1).setLight(packedLight);
                builder.addVertex(matrix4f, x2, y1, 0).setUv(maxU, minV).setColor(-1).setLight(packedLight);
                BufferUploader.drawWithShader(builder.buildOrThrow());
                RenderSystem.disableDepthTest();
                Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
            });
            poseStack.popPose();
        }
    }
}
