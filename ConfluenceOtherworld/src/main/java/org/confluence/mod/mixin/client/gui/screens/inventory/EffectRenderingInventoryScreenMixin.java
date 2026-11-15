package org.confluence.mod.mixin.client.gui.screens.inventory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.common.util.TriState;
import org.confluence.lib.mixed.ILibAbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectRenderingInventoryScreen.class)
public abstract class EffectRenderingInventoryScreenMixin<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements ILibAbstractContainerScreen {
    @Unique
    private boolean confluence$mouseClicked = false;
    @Unique
    private double confluence$clickX;
    @Unique
    private double confluence$clickY;

    public EffectRenderingInventoryScreenMixin(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public TriState confluence$onMouseClicked(double mouseX, double mouseY, int button) {
        this.confluence$mouseClicked = button == 0;
        this.confluence$clickX = mouseX;
        this.confluence$clickY = mouseY;
        return TriState.DEFAULT;
    }

    /// 按实际点击坐标命中效果面板；宽列表和紧凑图标共用边界，避免点击同高度的物品误关 Buff。
    @Inject(method = "renderBackgrounds", at = @At("HEAD"))
    private void switchEnabled(GuiGraphics guiGraphics, int renderX, int yOffset, Iterable<MobEffectInstance> effects, boolean large, CallbackInfo ci) {
        if (!confluence$mouseClicked) return;
        this.confluence$mouseClicked = false;
        int panelWidth = large ? 120 : 32;
        if (confluence$clickX < renderX || confluence$clickX >= renderX + panelWidth) return;
        int y = this.topPos;
        MobEffectInstance clicked = null;
        for (MobEffectInstance effect : effects) {
            if (confluence$clickY >= y && confluence$clickY < y + 32) clicked = effect;
            y += yOffset;
        }
        if (clicked != null) ILibAbstractContainerScreen.switchEnabled(clicked);
    }

    @Inject(method = "renderEffects", at = @At("TAIL"))
    private void reset(CallbackInfo ci) {
        this.confluence$mouseClicked = false;
    }

    @WrapOperation(method = "renderIcons", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(IIIIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
    private void makeTranslucent(GuiGraphics guiGraphics, int x, int y, int blitOffset, int width, int height, TextureAtlasSprite sprite, Operation<Void> original, @Local MobEffectInstance instance) {
        ILibAbstractContainerScreen.makeTranslucent(guiGraphics, instance, () -> original.call(guiGraphics, x, y, blitOffset, width, height, sprite));
    }
}
