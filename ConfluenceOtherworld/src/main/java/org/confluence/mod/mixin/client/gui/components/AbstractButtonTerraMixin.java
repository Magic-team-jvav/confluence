package org.confluence.mod.mixin.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.confluence.mod.client.gui.screen.TerraMenuStyle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// 泰拉界面模式：普通按钮（以及用它画背景的循环按钮等）画成泰拉的蓝色面板，悬停变亮、文字变黄。
/// 自己画外观的按钮（图标按钮、纯文字按钮、复选框）不受影响。
@Mixin(AbstractButton.class)
public abstract class AbstractButtonTerraMixin extends AbstractWidget {
    protected AbstractButtonTerraMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Shadow
    public abstract void renderString(GuiGraphics guiGraphics, Font font, int color);

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void confluence$terraButton(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!TerraMenuStyle.active()) return;
        boolean hovered = isHoveredOrFocused();
        TerraMenuStyle.panel(guiGraphics, getX(), getY(), getWidth(), getHeight(), hovered, active);
        renderString(guiGraphics, Minecraft.getInstance().font, TerraMenuStyle.textColor(hovered, active) | Mth.ceil(alpha * 255.0F) << 24);
        ci.cancel();
    }
}
