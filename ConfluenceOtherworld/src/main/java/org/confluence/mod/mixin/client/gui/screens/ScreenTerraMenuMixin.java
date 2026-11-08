package org.confluence.mod.mixin.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.confluence.mod.client.gui.screen.TerraMenuBackground;
import org.confluence.mod.client.gui.screen.TerraMenuStyle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// 泰拉界面模式下的菜单背景：凡是要画原版全景图的地方（主菜单、世界/服务器列表、设置、角色选择，
/// 以及保存/载入世界时的“保存世界中”“载入地形”这类提示界面——它们在世界还没卸载时也画全景图）
/// 都换成成就界面同款的 BackgroundLayer；这些界面不做模糊，菜单底纹换成淡淡的深蓝。
@Mixin(Screen.class)
public abstract class ScreenTerraMenuMixin {
    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    protected Minecraft minecraft;

    /// 这个界面画的是泰拉背景（之后的模糊和底纹也换掉）
    @Unique
    private boolean confluence$terraBackdrop;

    @Unique
    private boolean confluence$terraMenu() {
        return TerraMenuStyle.active() && minecraft != null && (minecraft.level == null || confluence$terraBackdrop);
    }

    @Inject(method = "renderPanorama", at = @At("HEAD"), cancellable = true)
    private void confluence$terraPanorama(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if (!TerraMenuStyle.active()) return;
        confluence$terraBackdrop = true;
        TerraMenuBackground.render(guiGraphics, partialTick);
        ci.cancel();
    }

    @Inject(method = "renderBlurredBackground", at = @At("HEAD"), cancellable = true)
    private void confluence$terraNoBlur(float partialTick, CallbackInfo ci) {
        if (confluence$terraMenu()) ci.cancel();
    }

    @Inject(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"), cancellable = true)
    private void confluence$terraMenuBackground(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (!confluence$terraMenu()) return;
        guiGraphics.fill(0, 0, width, height, 0x28000010);
        ci.cancel();
    }

    @Inject(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;IIII)V", at = @At("HEAD"), cancellable = true)
    private void confluence$terraMenuBackgroundArea(GuiGraphics guiGraphics, int x, int y, int w, int h, CallbackInfo ci) {
        if (!confluence$terraMenu()) return;
        TerraMenuStyle.listPanel(guiGraphics, x, y, x + w, y + h);
        ci.cancel();
    }
}
