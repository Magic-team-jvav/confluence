package org.confluence.mod.client.summoner.info;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.summoner.LyraRenderTypes;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.Locale;

/// 数字信息：走字形贴图高速路径，颜色由 {@code InfoData.Type} 指定；
/// 用文字贴图管线（{@link LyraRenderTypes#textTexture}）渲染，不做方向光混合，亮度与视角无关。
public final class NumberInfo extends Info {

    private final String glyphs;

    public NumberInfo(int color, float amount, Vec3 pos, Vec3 velocity) {
        super(color, pos, velocity);
        float value = Math.abs(amount);
        this.glyphs = value < 1.0F ? String.format(Locale.ROOT, "%.2f", value) : value < 10.0F ? String.format(Locale.ROOT, "%.1f", value) : String.valueOf((int) value);
    }

    public static RenderType renderType() {
        return LyraRenderTypes.textTexture(TEXTURE);
    }

    public void render(VertexConsumer consumer, PoseStack poseStack, Quaternionf baseRotation, Vec3 camPos, float partialTick) {
        float progress = beginPose(poseStack, baseRotation, camPos, partialTick);
        float size = SIZE * renderScale(progress);
        float half = size * 0.5F;
        float halfWidth = (glyphs.length() - 1) * size * 0.5F + half;
        int color = renderColor(progress);
        Matrix4f matrix = poseStack.last().pose();
        float pixelWidth = (float) TEXTURE_WIDTH / GLYPH_COUNT;
        for (int i = 0; i < glyphs.length(); i++) {
            char c = glyphs.charAt(i);
            int glyph = c == '.' ? 10 : c - '0';
            float u0 = glyph * pixelWidth / TEXTURE_WIDTH;
            float u1 = (glyph + 1) * pixelWidth / TEXTURE_WIDTH;
            // 局部 +x 向右推进、+y 向下，所以 v0（贴图顶部）落在 -half
            float x0 = -halfWidth + i * size;
            float x1 = x0 + size;
            vertex(consumer, matrix, color, x0, -half, u0, 0.0F);
            vertex(consumer, matrix, color, x1, -half, u1, 0.0F);
            vertex(consumer, matrix, color, x1, half, u1, 1.0F);
            vertex(consumer, matrix, color, x0, half, u0, 1.0F);
        }
        poseStack.popPose();
    }

    /// 文字管线顶点格式为 POSITION_COLOR_TEX_LIGHTMAP：没有法线与 overlay 属性，不能多写。
    private static void vertex(VertexConsumer consumer, Matrix4f matrix, int color, float x, float y, float u, float v) {
        consumer.vertex(matrix, x, y, 0.0F).color(color).uv(u, v).uv2(LightTexture.FULL_BRIGHT).endVertex();
    }
}
