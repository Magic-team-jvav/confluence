package org.confluence.mod.mixin.client.gui.screens.inventory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.util.TriState;
import org.confluence.mod.mixed.IAbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectRenderingInventoryScreen.class)
public abstract class EffectRenderingInventoryScreenMixin implements IAbstractContainerScreen {
    @Unique
    private boolean confluence$mouseClicked = false;

    @Override
    public TriState confluence$onMouseClicked(double mouseX, double mouseY, int button) {
        this.confluence$mouseClicked = true;
        return TriState.DEFAULT;
    }

    @Inject(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", shift = At.Shift.AFTER))
    private void switchEnabled(CallbackInfo ci, @Local(ordinal = 0) MobEffectInstance instance) {
        if (confluence$mouseClicked) {
            IAbstractContainerScreen.switchEnabled(instance);
        }
    }

    @Inject(method = "renderEffects", at = @At("TAIL"))
    private void reset(CallbackInfo ci) {
        this.confluence$mouseClicked = false;
    }

    @WrapOperation(method = "renderIcons", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(IIIIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
    private void makeTranslucent(GuiGraphics guiGraphics, int x, int y, int blitOffset, int width, int height, TextureAtlasSprite sprite, Operation<Void> original, @Local MobEffectInstance instance) {
        IAbstractContainerScreen.makeTranslucent(guiGraphics, instance, () -> original.call(guiGraphics, x, y, blitOffset, width, height, sprite));
    }
}
