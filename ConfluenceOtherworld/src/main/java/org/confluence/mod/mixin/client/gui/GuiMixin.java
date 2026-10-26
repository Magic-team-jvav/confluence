package org.confluence.mod.mixin.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.hud.CrosshairHud;
import org.confluence.mod.mixed.IGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin implements IGui {
    @Unique
    private float confluence$scale = 0;
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

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    private void confluence$renderCrosshair(
            GuiGraphics instance,
            ResourceLocation sprite,
            int x,
            int y,
            int width,
            int height,
            Operation<Void> original,
            @Local(argsOnly = true) DeltaTracker deltaTracker
    ) {
        CrosshairHud.renderCrosshair(instance, sprite, x, y, width, height, original, deltaTracker);
    }

    @Override
    public void confluence$setShooting() {
        this.confluence$scale = 3;
    }

    @Override
    public float confluence$getScale() {
        return confluence$scale;
    }

    @Override
    public void confluence$setScale(float scale) {
        this.confluence$scale = scale;
    }

    @Override
    public float confluence$getOldRepeaterCrosshairAngle() {
        return confluence$oldRepeaterCrosshairAngle;
    }

    @Override
    public void confluence$setOldRepeaterCrosshairAngle(float oldRepeaterCrosshairAngle) {
        this.confluence$oldRepeaterCrosshairAngle = oldRepeaterCrosshairAngle;
    }
}
