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
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import org.confluence.mod.client.ModClientConfigs;
import org.confluence.mod.client.gui.hud.BossBarStyles.BarSection;
import org.confluence.mod.client.gui.hud.BossBarStyles.BarStyle;
import org.confluence.mod.client.handler.ClientBossBarTracker;
import org.confluence.mod.client.handler.ClientBossBarTracker.BossBarData;
import org.confluence.mod.network.s2c.BossBarSyncPacketS2C.Health;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.mesdag.portlib.wrapper.common.PortTranslatableEnum;

import java.util.Locale;

public final class CustomBossBarRenderer {
    private static ShaderInstance flowingFillShader;

    private CustomBossBarRenderer() {}

    public static void render(CustomizeGuiOverlayEvent.BossEventProgress event) {
        if (ModClientConfigs.bossBarStyle == Style.VANILLA) return;
        BossBarData data = ClientBossBarTracker.get(event.getBossEvent().getId());
        if (data == null) return;
        BarStyle style = BossBarStyles.INSTANCE.get(data.styleId());
        if (style == null) return;

        GuiGraphics graphics = event.getGuiGraphics();
        event.setIncrement(drawBar(graphics, data, style, event.getBossEvent().getName(), event.getY(),
                ModClientConfigs.bossBarNameX.get(), ModClientConfigs.bossBarNameY.get(),
                ModClientConfigs.bossBarValueX.get(), ModClientConfigs.bossBarValueY.get()));
        event.setCanceled(true);
    }

    private static int drawBar(GuiGraphics graphics, BossBarData data, BarStyle style, Component name, int y,
                               int nameOffsetX, int nameOffsetY, int valueOffsetX, int valueOffsetY) {
        int screenWidth = graphics.guiWidth();
        int x = (screenWidth - style.frameWidth()) / 2 + ModClientConfigs.bossLayoutX.get();
        y += ModClientConfigs.bossLayoutY.get();
        // 底框和填充取同一帧，过渡动画播完后停在末帧。
        int frame = (int) Math.min(style.frames() - 1, (Util.getMillis() - data.styleSince()) / style.frameMillis());
        int frameY = frame * style.frameHeight();
        graphics.blit(style.frame(), x, y, 0, frameY, style.frameWidth(), style.frameHeight(), style.textureWidth(), style.textureHeight());
        // 血量与布局按同一顺序配对，渲染器无需认识激光眼、魔焰眼或其他具体 Boss。
        for (int i = 0; i < style.sections().size() && i < data.health().size(); i++) {
            drawHealth(graphics, style, style.sections().get(i), data.health().get(i), x, y, frameY, valueOffsetX, valueOffsetY);
        }
        Minecraft minecraft = Minecraft.getInstance();
        int nameWidth = minecraft.font.width(name);
        int lineHeight = minecraft.font.lineHeight;
        NamePosition position = ModClientConfigs.bossBarNamePosition;
        if ((position == NamePosition.LEFT || position == NamePosition.RIGHT) && x + style.sections().get(0).left() - 12 < nameWidth + 4) {
            position = NamePosition.TOP;
        }
        int nameX = x + (style.frameWidth() - nameWidth) / 2;
        int nameY = y + style.nameTopInset() - lineHeight;
        int increment = style.verticalIncrement();
        if (ModClientConfigs.bossBarNumbersVisible) {
            increment = Math.max(increment, style.frameHeight() + lineHeight + 10);
        }
        switch (position) {
            case BOTTOM -> {
                nameY = y + style.frameHeight();
                if (ModClientConfigs.bossBarNumbersVisible) nameY += lineHeight;
                increment = Math.max(increment, nameY - y + lineHeight * 2 + 2);
            }
            case LEFT, RIGHT -> {
                nameX = position == NamePosition.LEFT ? x + style.sections().get(0).left() - 14 - nameWidth : x + style.frameWidth() - style.sections().get(0).left() + 14;
                nameY = y + (style.frameHeight() - lineHeight) / 2;
                // 同侧显示时，名称放在整组血量数字上方，为双子魔眼的两行读数留出空间。
                if (ModClientConfigs.bossBarNumbersVisible && (position == NamePosition.LEFT
                        ? ModClientConfigs.bossBarNumberPosition == NumberPosition.LEFT
                        : ModClientConfigs.bossBarNumberPosition == NumberPosition.RIGHT)) {
                    nameY = y + (style.frameHeight() - style.sections().size() * lineHeight) / 2 - lineHeight - 2;
                }
            }
        }
        nameX = Mth.clamp(nameX + nameOffsetX, 0, Math.max(0, screenWidth - nameWidth));
        nameY = Mth.clamp(nameY + nameOffsetY, 0, graphics.guiHeight() - lineHeight);
        graphics.drawString(minecraft.font, name, nameX, nameY, 0xFFFFFF);
        return increment + Math.max(0, Math.max(nameOffsetY, valueOffsetY));
    }

    // 静态和动态样式只在填充绘制处选择，底框、裁切方向与数字排版保持一致。
    private static void drawHealth(GuiGraphics graphics, BarStyle style, BarSection section, Health health, int x, int y, int frameY, int offsetX, int offsetY) {
        int width = filledWidth(section.width(), health.current(), health.maximum());
        int sourceX = section.left() + (section.reverse() ? section.width() - width : 0);
        if (width > 0) {
            if (ModClientConfigs.bossBarStyle == Style.DYNAMIC && flowingFillShader != null) {
                graphics.flush();
                renderFlowingFill(graphics, style.fill(), x + sourceX, y + style.healthY(), width,
                        sourceX, style.fillY() + frameY, style.fillHeight(), style.textureWidth(), style.textureHeight());
            } else {
                graphics.blit(style.fill(), x + sourceX, y + style.healthY(), sourceX, style.fillY() + frameY,
                        width, style.fillHeight(), style.textureWidth(), style.textureHeight());
            }
        }
        if (!ModClientConfigs.bossBarNumbersVisible || health.maximum() <= 0) return;
        var font = Minecraft.getInstance().font;
        String value = String.format(Locale.ROOT, "%.0f / %.0f", Math.max(0.0F, health.current()), health.maximum());
        // 按该段可用宽度缩放，普通血条保持原字号，双子长数字也不会盖到中间眼睛。
        float scale = Math.min(1.0F, (section.width() - 4.0F) / font.width(value));
        graphics.pose().pushPose();
        float center = x + (style.sections().size() == 1 ? style.frameWidth() * 0.5F : section.left() + section.width() * 0.5F);
        int top = y + style.frameHeight() - 6;
        NumberPosition position = ModClientConfigs.bossBarNumberPosition;
        float halfWidth = font.width(value) * scale * 0.5F;
        // 窄屏侧边容不下完整数值时，回到血条下方。
        if (position != NumberPosition.BOTTOM && x + style.sections().get(0).left() - 14 >= 2 * halfWidth) {
            center = position == NumberPosition.LEFT ? x + style.sections().get(0).left() - 14 - halfWidth : x + style.frameWidth() - style.sections().get(0).left() + 14 + halfWidth;
            top = y + (style.frameHeight() - style.sections().size() * font.lineHeight) / 2
                    + style.sections().indexOf(section) * font.lineHeight;
        }
        center = Mth.clamp(center + offsetX, halfWidth, graphics.guiWidth() - halfWidth);
        top = Mth.clamp(top + offsetY, 0, graphics.guiHeight() - font.lineHeight);
        graphics.pose().translate(center, top, 0);
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
        return maximum > 0 ? Mth.floor(width * Mth.clamp(health / maximum, 0.0F, 1.0F)) : 0;
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
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(matrix, x, y, 0.0F).uv(minU, minV).endVertex();
        builder.vertex(matrix, x, y + height, 0.0F).uv(minU, maxV).endVertex();
        builder.vertex(matrix, x + width, y + height, 0.0F).uv(maxU, maxV).endVertex();
        builder.vertex(matrix, x + width, y, 0.0F).uv(maxU, minV).endVertex();
        BufferUploader.drawWithShader(builder.end());
    }

    @ApiStatus.Internal
    public static void setShader(ShaderInstance instance) {
        flowingFillShader = instance;
    }

    public enum NamePosition implements PortTranslatableEnum {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT;

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.bossBarNamePosition." + name().toLowerCase(Locale.ROOT));
        }
    }

    public enum NumberPosition implements PortTranslatableEnum {
        BOTTOM,
        LEFT,
        RIGHT;

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.bossBarNumberPosition." + name().toLowerCase(Locale.ROOT));
        }
    }

    public enum Style implements PortTranslatableEnum {
        VANILLA,
        STATIC,
        DYNAMIC;

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.bossBarStyle." + name().toLowerCase(Locale.ROOT));
        }
    }

}
