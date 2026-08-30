package org.confluence.mod.mixin.client.multiplayer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.multiplayer.ClientAdvancements;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.AchievementToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientAdvancements.class)
public abstract class ClientAdvancementsMixin {
    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/toasts/ToastComponent;addToast(Lnet/minecraft/client/gui/components/toasts/Toast;)V"))
    private void showAchievementToast(ToastComponent instance, Toast toast, Operation<Void> original, @Local AdvancementNode advancementNode) {
        if (ClientConfigs.achievementToast) {
            AchievementToast achievementToast = AchievementToast.getToast(advancementNode.holder().id());
            if (achievementToast != null) {
                achievementToast.playedSound = false;
                original.call(instance, achievementToast);
                return;
            }
        }
        original.call(instance, toast);
    }
}
