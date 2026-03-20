package org.confluence.mod.mixin.integration.journeymap;

import com.llamalad7.mixinextras.sugar.Local;
import journeymap.client.ui.UIManager;
import net.minecraft.client.Minecraft;
import org.confluence.mod.client.ClientConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "journeymap.client.ui.minimap.Effect", remap = false)
public abstract class EffectMixin {
    @Inject(method = "effectProcessor", at = @At("HEAD"), cancellable = true)
    private static void cancel(CallbackInfoReturnable<int[]> cir, @Local(argsOnly = true, ordinal = 1) int l) {
        if (ClientConfigs.leftEffectIcon) {
            int minimapWidth = UIManager.INSTANCE.getMiniMap().getDisplayVars().minimapWidth;
            double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
            cir.setReturnValue(new int[]{(int) (minimapWidth / guiScale) + 4, l});
        }
    }
}
