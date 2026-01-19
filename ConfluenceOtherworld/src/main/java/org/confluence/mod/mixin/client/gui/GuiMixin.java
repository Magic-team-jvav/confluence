package org.confluence.mod.mixin.client.gui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.hud.RepeaterHud;
import org.confluence.mod.mixed.IMobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Unique
    private float confluence$oldRepeaterCrosshairAngle = 0;

    @ModifyVariable(method = "renderEffects", at = @At(value = "STORE", ordinal = 0/* first */), ordinal = 2/* ISTORE 12 */)
    private int modify0(int i) {
        return ClientConfigs.leftEffectIcon ? 25 : i;
    }

    @ModifyVariable(method = "renderEffects", at = @At(value = "STORE", ordinal = 1/* second */), ordinal = 2/* ISTORE 12 */)
    private int modify1(int i) {
        return ClientConfigs.leftEffectIcon ? -i : i;
    }

    @ModifyVariable(method = "renderEffects", at = @At(value = "STORE", ordinal = 2/* third */), ordinal = 2/* ISTORE 12 */)
    private int modify2(int i) {
        return ClientConfigs.leftEffectIcon ? -i : i;
    }

    @ModifyExpressionValue(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;showIcon()Z"))
    private boolean skip(boolean original, @Local MobEffectInstance instance) {
        if (original && !IMobEffectInstance.of(instance).confluence$isEnabled()) {
            return false;
        }
        return original;
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    private void confluence$repeaterRenderCrosshair(GuiGraphics instance, ResourceLocation sprite, int x, int y, int width, int height, Operation<Void> original, @Local(argsOnly = true) DeltaTracker deltaTracker) {
        RepeaterHud.repeaterRenderCrosshair(instance, sprite, x, y, width, height, original, deltaTracker);
    }
}
