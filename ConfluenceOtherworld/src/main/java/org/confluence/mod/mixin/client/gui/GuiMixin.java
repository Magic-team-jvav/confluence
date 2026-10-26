package org.confluence.mod.mixin.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.hud.CrosshairHud;
import org.confluence.mod.mixed.IGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
public abstract class GuiMixin implements IGui {
    @Unique
    private float confluence$scale = 0;
    @Unique
    private float confluence$oldRepeaterCrosshairAngle = 0;

    @ModifyVariable(method = "renderEffects", at = @At(value = "STORE", ordinal = 0/* first */), /* ISTORE 12 */name = "i")
    private int modify0(int i) {
        return ClientConfigs.leftEffectIcon ? 25 : i;
    }

    @ModifyVariable(method = "renderEffects", at = @At(value = "STORE", ordinal = 1/* second */), /* ISTORE 12 */name = "i")
    private int modify1(int i) {
        return ClientConfigs.leftEffectIcon ? -i : i;
    }

    @ModifyVariable(method = "renderEffects", at = @At(value = "STORE", ordinal = 2/* third */), /* ISTORE 12 */name = "i")
    private int modify2(int i) {
        return ClientConfigs.leftEffectIcon ? -i : i;
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 0))
    private void confluence$renderCrosshair(GuiGraphics graphics, ResourceLocation texture, int x, int y,
                                           int u, int v, int width, int height, Operation<Void> original) {
        CrosshairHud.renderCrosshair(graphics, texture, x, y, u, v, width, height, original, Minecraft.getInstance().getFrameTime());
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
