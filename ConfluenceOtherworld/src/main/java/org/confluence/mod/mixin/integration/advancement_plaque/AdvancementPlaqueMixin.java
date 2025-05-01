package org.confluence.mod.mixin.integration.advancement_plaque;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.network.chat.Component;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.AchievementToast;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "com.anthonyhilyard.advancementplaques.ui.render.AdvancementPlaque", remap = false)
public abstract class AdvancementPlaqueMixin {
    @Shadow
    @Final
    private AdvancementToast toast;

    @WrapOperation(method = "lambda$drawPlaque$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementType;getDisplayName()Lnet/minecraft/network/chat/Component;"))
    private Component replace(AdvancementType instance, Operation<Component> original) {
        if (!ClientConfigs.achievementToast) {
            AchievementToast achievementToast = AchievementToast.getToast(toast.advancement.id());
            if (achievementToast != null) {
                return AchievementToast.DISPLAY;
            }
        }
        return original.call(instance);
    }
}
