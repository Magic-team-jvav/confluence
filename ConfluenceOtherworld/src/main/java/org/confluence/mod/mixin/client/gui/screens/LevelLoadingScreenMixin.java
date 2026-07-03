package org.confluence.mod.mixin.client.gui.screens;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import org.confluence.mod.mixed.ILevelLoadingScreen;
import org.confluence.mod.mixed.IWorldOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

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

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V"))
    private void wrapDraw(GuiGraphics instance, Font font, String text, int x, int y, int color, Operation<Void> original) {
        if (confluence$secretFlag != 0L) {
            int progress = progressListener.getProgress();
            if ((confluence$secretFlag & IWorldOptions.DW_MASK) != 0) {
                if (progress >= 50) {
                    instance.drawCenteredString(font, PLACING_TRAPS, x, y, color);
                } else {
                    instance.drawCenteredString(font, SSK, x, y, color);
                }
            } else if ((confluence$secretFlag & IWorldOptions.NTB_MASK) != 0) {
                instance.drawCenteredString(font, GENERATING_BEES, x, y, color);
            } else if ((confluence$secretFlag & IWorldOptions.TC_MASK) != 0) {
                instance.drawCenteredString(font, GENERATING_WAVY_CAVES, x, y, color);
            } else if (progress >= 50) {
                if ((confluence$secretFlag & IWorldOptions.NT_MASK) != 0 || (confluence$secretFlag & IWorldOptions.GFB_MASK) != 0) {
                    instance.drawCenteredString(font, NOT_PLACING_TRAPS, x, y, color);
                } else if ((confluence$secretFlag & IWorldOptions.BW_MASK) != 0) {
                    instance.drawCenteredString(font, PLACING_BOULDERS, x, y, color);
                } else {
                    instance.drawCenteredString(font, PLACING_TRAPS, x, y, color);
                }
            } else if ((confluence$secretFlag & IWorldOptions.TOO_EASY_MASK) != 0) {
                instance.drawCenteredString(font, TOO_EASY, x, y, color);
            } else if ((confluence$secretFlag & IWorldOptions.C10_MASK) != 0) {
                original.call(instance, font, text, x, y, PINK);
            } else {
                original.call(instance, font, text, x, y, color);
            }
        } else {
            original.call(instance, font, text, x, y, color);
        }

//        if ((confluence$secretFlag & IWorldOptions.FTW_MASK) != 0 || (confluence$secretFlag & IWorldOptions.GFB_MASK) != 0) {
//            if (text == NOT_PLACING_TRAPS) {
//                original.call(instance, font, text, x, y, color);
//            } else {
//                PoseStack pose = instance.pose();
//                pose.pushPose();
//                pose.translate(x, y, 0);
//                pose.mulPose(LibRenderUtils.ANGLE_180);
//                original.call(instance, font, text, 0, 0, color);
//                pose.popPose();
//            }
//        } else if ((confluence$secretFlag & IWorldOptions.C10_MASK) != 0) {
//            original.call(instance, font, text, x, y, PINK);
//        } else {
//            original.call(instance, font, text, x, y, color);
//        }
    }
}
