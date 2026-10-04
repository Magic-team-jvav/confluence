package org.confluence.mod.client.summoner.info;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

/// 文本信息：用组件自身的文本与颜色做普通字体渲染。
public final class TextInfo extends Info {

    private final Component text;

    public TextInfo(Component text, Vec3 pos, Vec3 velocity) {
        super(TEXT_COLOR, pos, velocity);
        this.text = text;
    }

    public void render(MultiBufferSource bufferSource, PoseStack poseStack, Quaternionf baseRotation, Vec3 camPos, float partialTick) {
        float progress = beginPose(poseStack, baseRotation, camPos, partialTick);
        float textScale = SIZE * renderScale(progress) / FONT_HEIGHT;
        poseStack.scale(textScale, textScale, 1.0F);
        Font font = Minecraft.getInstance().font;
        font.drawInBatch(text.getVisualOrderText(), -font.width(text) / 2.0F, -FONT_HEIGHT * 0.5F, renderColor(progress), false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }
}
