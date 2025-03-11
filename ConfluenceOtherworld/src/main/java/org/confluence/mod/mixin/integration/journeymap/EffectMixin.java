package org.confluence.mod.mixin.integration.journeymap;

import com.mojang.blaze3d.platform.Window;
import journeymap.client.ui.UIManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffect;
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
    private static void cancel(int k, int l, int j, int i, MobEffect mobEffect, CallbackInfoReturnable<int[]> cir) {
        if (ClientConfigs.leftEffectIcon) {
            int minimapWidth = UIManager.INSTANCE.getMiniMap().getDisplayVars().minimapWidth;
            Window window = Minecraft.getInstance().getWindow();
            cir.setReturnValue(new int[]{(int) (minimapWidth / window.getGuiScale()) + 4, l});
        }
    }
}
