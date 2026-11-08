package org.confluence.mod.mixin.client.gui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
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
    @Final
    private Component causeOfDeath;
    @Shadow
    private Button exitToTitleButton;

    @Shadow
    protected abstract void exitToTitleScreen();

    @Unique
    private Button confluence$respawnButton;
    @Unique
    private int confluence$respawnWaitTime;
    @Unique
    private Component confluence$respawnTimeComponent = Component.empty();
    @Unique
    private boolean confluence$autoRespawned;
    /// 泰拉的自动复活最少等 2 秒（服务端没给等待时间时）
    @Unique
    private static final int CONFLUENCE$MIN_AUTO_RESPAWN = 40;

    /// 泰拉界面、非极限模式：泰拉式死亡画面（不变红、没有“重生”按钮，倒计时结束自动复活）
    @Unique
    private boolean confluence$terraria() {
        return org.confluence.mod.client.ClientConfigs.terrariaInterface && !hardcore;
    }

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

    @Inject(method = "init", at = @At("TAIL"))
    private void confluence$terrariaInit(CallbackInfo ci) {
        if (!confluence$terraria()) return;
        if (confluence$respawnButton != null) confluence$respawnButton.visible = false;
        if (exitToTitleButton != null) exitToTitleButton.setY(height - 32);
    }

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void confluence$terrariaBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!confluence$terraria()) return;
        guiGraphics.fill(0, 0, width, height, 0x70000000); // 世界还看得见，只是暗下来
        ci.cancel();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void confluence$terrariaRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!confluence$terraria()) return;
        super.render(guiGraphics, mouseX, mouseY, partialTick); // 背景（上面的暗色）+ 按钮
        int center = width / 2;
        int top = height / 2 - 50;
        Component slain = Component.translatable("gui.confluence.death.slain");
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(center, top, 0.0F);
        guiGraphics.pose().scale(2.5F, 2.5F, 1.0F);
        guiGraphics.drawString(font, slain, -font.width(slain) / 2, 0, 0xD8D8D8, true);
        guiGraphics.pose().popPose();
        int y = top + 34;
        if (causeOfDeath != null) {
            guiGraphics.drawCenteredString(font, causeOfDeath, center, y, 0xFFFFFF);
            y += 14;
        }
        if (deathScore != null) {
            guiGraphics.drawCenteredString(font, deathScore, center, y, 0xFFFFFF);
            y += 14;
        }
        int ticksLeft = Math.max(confluence$respawnWaitTime, CONFLUENCE$MIN_AUTO_RESPAWN) - delayTicker;
        if (ticksLeft > 0 && !confluence$autoRespawned) {
            String seconds = String.valueOf((ticksLeft + 19) / 20);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(center, y + 10, 0.0F);
            guiGraphics.pose().scale(2.0F, 2.0F, 1.0F);
            guiGraphics.drawString(font, seconds, -font.width(seconds) / 2, 0, 0xFFFFFF, true);
            guiGraphics.pose().popPose();
        }
        ci.cancel();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void confluence$terrariaAutoRespawn(CallbackInfo ci) {
        if (!confluence$terraria() || confluence$autoRespawned || confluence$respawnButton == null) return;
        if (delayTicker >= Math.max(confluence$respawnWaitTime, CONFLUENCE$MIN_AUTO_RESPAWN)) {
            confluence$autoRespawned = true;
            confluence$respawnButton.onPress(); // 和点“重生”一样
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(CallbackInfo ci, @Local(argsOnly = true) GuiGraphics guiGraphics) {
        if (confluence$respawnWaitTime >= delayTicker && !confluence$terraria()) {
            guiGraphics.drawCenteredString(font, confluence$respawnTimeComponent, width / 2, 120, 16777215);
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
