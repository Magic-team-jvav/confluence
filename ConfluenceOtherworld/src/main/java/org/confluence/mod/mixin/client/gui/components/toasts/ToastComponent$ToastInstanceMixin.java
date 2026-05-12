package org.confluence.mod.mixin.client.gui.components.toasts;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.toasts.Toast;
import org.confluence.mod.client.gui.AchievementToast;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.gui.components.toasts.ToastComponent$ToastInstance")
public abstract class ToastComponent$ToastInstanceMixin<T extends Toast> {
    @Shadow
    @Final
    private T toast;

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private void modify(PoseStack instance, float x, float y, float z, Operation<Void> original) {
        if (toast instanceof AchievementToast) {
            original.call(instance, x, -y, z);
        } else {
            original.call(instance, x, y, z);
        }
    }
}
