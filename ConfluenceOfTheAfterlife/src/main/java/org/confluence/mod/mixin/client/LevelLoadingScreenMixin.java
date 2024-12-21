package org.confluence.mod.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.util.Mth;
import org.confluence.mod.mixed.ILevelLoadingScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin implements ILevelLoadingScreen {
    @Shadow
    @Final
    private StoringChunkProgressListener progressListener;

    @Unique
    private long confluence$secretFlag = 0L;

    @Override
    public void confluence$setSecretFlag(long flag) {
        this.confluence$secretFlag = flag;
    }

    @Inject(method = "getFormattedProgress", at = @At("HEAD"), cancellable = true)
    private void ssk(CallbackInfoReturnable<Component> cir) {
        int progress = progressListener.getProgress();
        if ((confluence$secretFlag & DW_MASK) == DW_MASK) {
            if (progress >= 50) {
                cir.setReturnValue(PLACING_TRAPS);
            } else {
                cir.setReturnValue(SSK);
            }
        } else if ((confluence$secretFlag & NTB_MASK) == NTB_MASK) {
            cir.setReturnValue(GENERATING_BEES);
        } else if ((confluence$secretFlag & TC_MASK) == TC_MASK) {
            cir.setReturnValue(GENERATING_WAVY_CAVES);
        } else if (progress >= 50) {
            if ((confluence$secretFlag & NT_MASK) == NT_MASK || (confluence$secretFlag & GFB_MASK) == GFB_MASK) {
                cir.setReturnValue(NOT_PLACING_TRAPS);
            } else {
                cir.setReturnValue(PLACING_TRAPS);
            }
        }
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"))
    private void wrapDraw(GuiGraphics instance, Font font, Component text, int x, int y, int color, Operation<Void> original) {
        if ((confluence$secretFlag & FTW_MASK) == FTW_MASK || (confluence$secretFlag & GFB_MASK) == GFB_MASK) {
            if (text == NOT_PLACING_TRAPS) {
                original.call(instance, font, text, x, y, color);
            } else {
                PoseStack pose = instance.pose();
                pose.pushPose();
                pose.translate(x, y, 0);
                pose.mulPose(Axis.ZP.rotation(Mth.PI));
                original.call(instance, font, text, 0, 0, color);
                pose.popPose();
            }
        } else if ((confluence$secretFlag & C10_MASK) == C10_MASK) {
            original.call(instance, font, text, x, y, PINK);
        } else {
            original.call(instance, font, text, x, y, color);
        }
    }
}
