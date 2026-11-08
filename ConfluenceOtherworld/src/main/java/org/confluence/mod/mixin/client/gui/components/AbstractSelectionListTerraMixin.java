package org.confluence.mod.mixin.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import org.confluence.mod.client.gui.screen.TerraMenuStyle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// 泰拉界面模式：列表（世界、服务器、按键、资源包……）的底纹换成半透明深蓝面板，上下分隔线换成蓝色细线。
@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListTerraMixin {
    @Inject(method = "renderListBackground", at = @At("HEAD"), cancellable = true)
    private void confluence$terraListBackground(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (!TerraMenuStyle.active()) return;
        AbstractWidget self = (AbstractWidget) (Object) this;
        TerraMenuStyle.listPanel(guiGraphics, self.getX(), self.getY(), self.getRight(), self.getBottom());
        ci.cancel();
    }

    @Inject(method = "renderListSeparators", at = @At("HEAD"), cancellable = true)
    private void confluence$terraListSeparators(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (!TerraMenuStyle.active()) return;
        AbstractWidget self = (AbstractWidget) (Object) this;
        guiGraphics.fill(self.getX(), self.getY() - 1, self.getRight(), self.getY(), 0xFF5070B8);
        guiGraphics.fill(self.getX(), self.getBottom(), self.getRight(), self.getBottom() + 1, 0xFF5070B8);
        ci.cancel();
    }
}
