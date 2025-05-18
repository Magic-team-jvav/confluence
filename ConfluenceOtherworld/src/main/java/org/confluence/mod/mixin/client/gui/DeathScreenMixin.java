package org.confluence.mod.mixin.client.gui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.confluence.mod.mixed.IDeathScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen implements IDeathScreen {
    protected DeathScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Shadow
    private int delayTicker;
    @Shadow
    @Final
    private boolean hardcore;
    @Shadow
    private Component deathScore;

    @Shadow
    protected abstract void exitToTitleScreen();

    @Unique
    private Button confluence$respawnButton;
    @Unique
    private int confluence$respawnWaitTime;
    @Unique
    private Component confluence$respawnTimeComponent = Component.empty();

    @Override
    public void confluence$setDelayTicker(int ticker) {
        this.delayTicker = ticker;
    }

    @Override
    public void confluence$setRespawnWaitTime(int time) {
        this.confluence$respawnWaitTime = time;
    }

    @Override
    public void confluence$setDropsMoney(MutableComponent prefix) {
        this.deathScore = prefix.append(", ").append(deathScore);
    }

    @ModifyExpressionValue(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/DeathScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 0))
    private GuiEventListener setInactive(GuiEventListener original) {
        if (!hardcore && original instanceof Button button) {
            this.confluence$respawnButton = button;
            button.active = false;
        }
        return original;
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
        if (confluence$respawnWaitTime >= delayTicker) {
            pGuiGraphics.drawCenteredString(font, confluence$respawnTimeComponent, width / 2, 120, 16777215);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (delayTicker >= confluence$respawnWaitTime) {
            if (confluence$respawnButton != null) {
                confluence$respawnButton.active = true;
            }
        }
        this.confluence$respawnTimeComponent = Component.translatable("info.confluence.respawn_time", (confluence$respawnWaitTime - delayTicker) / 20).withStyle(ChatFormatting.GRAY);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/DeathScreen;setButtonsActive(Z)V", shift = At.Shift.AFTER))
    private void setInactive(CallbackInfo ci) {
        if (confluence$respawnButton != null) {
            confluence$respawnButton.active = false;
        }
    }

    @Inject(method = "handleExitToTitleScreen", at = @At("HEAD"), cancellable = true)
    private void confirmExit(CallbackInfo ci) {
        if (delayTicker < confluence$respawnWaitTime) {
            exitToTitleScreen();
            ci.cancel();
        }
    }
}
