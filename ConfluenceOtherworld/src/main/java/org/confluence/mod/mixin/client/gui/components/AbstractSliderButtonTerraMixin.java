package org.confluence.mod.mixin.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.confluence.mod.client.gui.screen.TerraMenuStyle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// 泰拉界面模式：滑条（音量、视野、亮度……）画成蓝色面板，已调到的部分颜色更亮，滑块是一个小的亮面板。
@Mixin(AbstractSliderButton.class)
public abstract class AbstractSliderButtonTerraMixin extends AbstractWidget {
    protected AbstractSliderButtonTerraMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Shadow
    protected double value;

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void confluence$terraSlider(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!TerraMenuStyle.active()) return;
        boolean hovered = isHoveredOrFocused();
        TerraMenuStyle.panel(guiGraphics, getX(), getY(), getWidth(), getHeight(), false, active);
        int knob = 8;
        int knobX = getX() + (int) (value * (getWidth() - knob));
        if (active) guiGraphics.fill(getX() + 1, getY() + 2, knobX + knob / 2, getY() + getHeight() - 1, 0x403A9AE0); // 已调到的部分
        TerraMenuStyle.panel(guiGraphics, knobX, getY(), knob, getHeight(), hovered, active);
        renderScrollingString(guiGraphics, Minecraft.getInstance().font, 2, TerraMenuStyle.textColor(hovered, active) | Mth.ceil(alpha * 255.0F) << 24);
        ci.cancel();
    }
}
