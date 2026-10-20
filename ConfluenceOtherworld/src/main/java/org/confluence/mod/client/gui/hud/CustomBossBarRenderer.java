package org.confluence.mod.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.hud.BossBarStyles.BarSection;
import org.confluence.mod.client.gui.hud.BossBarStyles.BarStyle;
import org.confluence.mod.client.handler.ClientBossBarTracker;
import org.confluence.mod.client.handler.ClientBossBarTracker.BossBarData;
import org.confluence.mod.network.s2c.BossBarSyncPacketS2C.Health;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import java.util.Locale;

public final class CustomBossBarRenderer {
    private static ShaderInstance flowingFillShader;

    private CustomBossBarRenderer() {}

    public static void render(CustomizeGuiOverlayEvent.BossEventProgress event) {
        if (ClientConfigs.bossBarStyle == Style.VANILLA) return;
        BossBarData data = ClientBossBarTracker.get(event.getBossEvent().getId());
        if (data == null) return;
        BarStyle style = BossBarStyles.INSTANCE.get(data.styleId());
        if (style == null) return;

        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = event.getWindow().getGuiScaledWidth();
        int x = (screenWidth - style.frameWidth()) / 2;
        int y = event.getY();
        graphics.blit(style.frame(), x, y, 0, 0, style.frameWidth(), style.frameHeight(), style.textureWidth(), style.textureHeight());
        // 血量与布局按同一顺序配对，渲染器无需认识激光眼、魔焰眼或其他具体 Boss。
        for (int i = 0; i < style.sections().size() && i < data.health().size(); i++) {
            drawHealth(graphics, style, style.sections().get(i), data.health().get(i), x, y);
        }
        Minecraft minecraft = Minecraft.getInstance();
        graphics.drawString(minecraft.font, event.getBossEvent().getName(),
                (screenWidth - minecraft.font.width(event.getBossEvent().getName())) / 2, y - 9, 0xFFFFFF);
        event.setIncrement(style.verticalIncrement());
        event.setCanceled(true);
    }

    // 静态和动态样式只在填充绘制处选择，底框、裁切方向与数字排版保持一致。
    private static void drawHealth(GuiGraphics graphics, BarStyle style, BarSection section, Health health, int x, int y) {
        int width = filledWidth(section.width(), health.current(), health.maximum());
        int sourceX = section.left() + (section.reverse() ? section.width() - width : 0);
        if (width > 0) {
            if (ClientConfigs.bossBarStyle == Style.DYNAMIC && flowingFillShader != null) {
                graphics.flush();
                renderFlowingFill(graphics, style.fill(), x + sourceX, y + style.healthY(), width, sourceX, style.fillY(), style.fillHeight(), style.textureWidth(), style.textureHeight());
            } else {
                graphics.blit(style.fill(), x + sourceX, y + style.healthY(), sourceX, style.fillY(), width, style.fillHeight(), style.textureWidth(), style.textureHeight());
            }
        }
        if (!ClientConfigs.bossBarNumbersVisible || health.maximum() <= 0) return;
        var font = Minecraft.getInstance().font;
        String value = String.format(Locale.ROOT, "%.0f / %.0f", Math.max(0.0F, health.current()), health.maximum());
        // 按该段可用宽度缩放，普通血条保持原字号，双子长数字也不会盖到中间眼睛。
        float scale = Math.min(1.0F, (section.width() - 4.0F) / font.width(value));
        graphics.pose().pushPose();
        graphics.pose().translate(x + section.numberCenter(), y + section.numberY(), 0);
        graphics.pose().scale(scale, scale, 1);
        int left = -font.width(value) / 2;
        if (section.numberColor() == section.numberEndColor()) {
            graphics.drawString(font, value, left, 0, section.numberColor());
        } else {
            // 按整行位置插值，数字从左到右渐变，空格宽度也计入排版。
            int offset = 0;
            int span = Math.max(1, font.width(value) - font.width(value.substring(value.length() - 1)));
            for (int i = 0; i < value.length(); i++) {
                String glyph = value.substring(i, i + 1);
                float progress = offset / (float) span;
                int color = interpolateColor(section.numberColor(), section.numberEndColor(), progress);
                graphics.drawString(font, glyph, left + offset, 0, color);
                offset += font.width(glyph);
            }
        }
        graphics.pose().popPose();
    }

    // 逐通道插值，避免直接插值打包颜色导致进位串色。
    private static int interpolateColor(int start, int end, float progress) {
        int red = Math.round(Mth.lerp(progress, (start >> 16) & 255, (end >> 16) & 255));
        int green = Math.round(Mth.lerp(progress, (start >> 8) & 255, (end >> 8) & 255));
        int blue = Math.round(Mth.lerp(progress, start & 255, end & 255));
        return red << 16 | green << 8 | blue;
    }

    // 只计算各段自身的有效长度，归零后不再向眼睛装饰和连接区域继续收缩。
    private static int filledWidth(int width, float health, float maximum) {
        return maximum > 0 ? Mth.floor(width * Math.clamp(health / maximum, 0.0F, 1.0F)) : 0;
    }

    private static void renderFlowingFill(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width,
                                          int sourceX, int sourceY, int height, int textureWidth, int textureHeight) {
        ShaderInstance shader = flowingFillShader;
        if (shader == null) return;
        if (shader.getUniform("Time") != null)
            shader.getUniform("Time").set((Util.getMillis() % 100000L) / 1000.0F);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(() -> shader);
        Matrix4f matrix = graphics.pose().last().pose();
        float minU = sourceX / (float) textureWidth;
        float maxU = (sourceX + width) / (float) textureWidth;
        float minV = sourceY / (float) textureHeight;
        float maxV = (sourceY + height) / (float) textureHeight;
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.addVertex(matrix, x, y, 0.0F).setUv(minU, minV);
        builder.addVertex(matrix, x, y + height, 0.0F).setUv(minU, maxV);
        builder.addVertex(matrix, x + width, y + height, 0.0F).setUv(maxU, maxV);
        builder.addVertex(matrix, x + width, y, 0.0F).setUv(maxU, minV);
        BufferUploader.drawWithShader(builder.buildOrThrow());
    }

    @ApiStatus.Internal
    public static void setShader(ShaderInstance instance) {
        flowingFillShader = instance;
    }

    public enum Style implements TranslatableEnum {
        VANILLA,
        STATIC,
        DYNAMIC;

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.bossBarStyle." + name().toLowerCase(Locale.ROOT));
        }
    }

}
