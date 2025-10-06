package org.confluence.mod.mixin.integration.jei;

import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.confluence.mod.integration.jei.ModJeiPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Pseudo
@Mixin(targets = "mezz.jei.neoforge.platform.ScreenHelper", remap = false)
public abstract class ScreenHelperMixin {
    @ModifyVariable(method = "getToastsArea", at = @At("STORE"))
    private List<ToastComponent.ToastInstance<?>> filter(List<ToastComponent.ToastInstance<?>> visible) {
        return ModJeiPlugin.filterAchievements(visible);
    }
}
