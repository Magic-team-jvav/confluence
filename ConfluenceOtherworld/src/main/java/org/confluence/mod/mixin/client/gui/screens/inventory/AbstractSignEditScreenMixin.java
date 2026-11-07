package org.confluence.mod.mixin.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.confluence.mod.client.gui.TombstoneEditScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin extends Screen {
    @Shadow
    @Final
    private SignBlockEntity sign;
    @Shadow
    private SignText text;
    @Shadow
    @Final
    private String[] messages;

    protected AbstractSignEditScreenMixin(Component title) {
        super(title);
    }

    /// 墓碑的死亡信息超出一行宽度时自动换行，见 {@link TombstoneEditScreen#renderWrappedText}
    @Inject(method = "renderSignText", at = @At("HEAD"), cancellable = true)
    private void wrapTombstoneText(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (!((Object) this instanceof TombstoneEditScreen)) return;
        int color = text.hasGlowingText() ? text.getColor().getTextColor() : SignRenderer.getDarkColor(text);
        if (TombstoneEditScreen.renderWrappedText(guiGraphics, font, sign, messages, color)) {
            ci.cancel();
        }
    }
}
