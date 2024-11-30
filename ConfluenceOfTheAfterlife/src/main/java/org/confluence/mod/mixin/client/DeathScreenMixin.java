package org.confluence.mod.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.confluence.mod.util.ModUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {

    @Shadow @Final private List<Button> exitButtons;

    protected DeathScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Shadow protected abstract void exitToTitleScreen();
    @Shadow private int delayTicker;

    @Unique private int confluence$respawnWaitTime;
    @Unique private Component confluence$respawnTimeComponent = Component.literal("");

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        delayTicker = 0;
        exitButtons.getFirst().visible = false;
        if (this.minecraft != null) {
            if (this.minecraft.player != null && !this.minecraft.player.isCreative()) {
                confluence$respawnWaitTime = ModUtils.getRespawnWaitTime(this.minecraft.player);
            } else {
                confluence$respawnWaitTime = -100;
            }
        } else {
            confluence$respawnWaitTime = -100;
        }
    }

    @Inject(method = "handleExitToTitleScreen", at = @At("HEAD"), cancellable = true)
    private void handleExitToTitleScreen(CallbackInfo ci) {
        exitToTitleScreen();
        ci.cancel();
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
        if (((confluence$respawnWaitTime * 20 - this.delayTicker) / 20) >= 0) {
            pGuiGraphics.drawCenteredString(this.font, this.confluence$respawnTimeComponent, this.width / 2, 120, 16777215);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (delayTicker >= confluence$respawnWaitTime * 20){
            exitButtons.getFirst().visible = true;
        }
        confluence$respawnTimeComponent = Component.translatable("info.confluence.respawn_time")
                .append(Component.literal(
                        String.valueOf((confluence$respawnWaitTime * 20 - this.delayTicker) / 20)))
                .append(Component.translatable("info.confluence.second"))
                .withStyle(ChatFormatting.GRAY);
    }
}