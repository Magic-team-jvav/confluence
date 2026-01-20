package org.confluence.mod.mixin.client.gui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.component.RepeaterContents;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.mixed.IGui;
import org.confluence.mod.mixed.IMobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
public abstract class GuiMixin implements IGui {
    @Shadow
    @Final
    private Minecraft minecraft;
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

    @ModifyExpressionValue(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;showIcon()Z"))
    private boolean skip(boolean original, @Local MobEffectInstance instance) {
        if (original && !IMobEffectInstance.of(instance).confluence$isEnabled()) {
            return false;
        }
        return original;
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    private void confluence$repeaterRenderCrosshair(GuiGraphics instance, ResourceLocation sprite, int x, int y, int width, int height, Operation<Void> original, @Local(argsOnly = true) DeltaTracker deltaTracker) {
        LocalPlayer player = this.minecraft.player;
        if (player == null) {
            original.call(instance, sprite, x, y, width, height);
            return;
        }
        PoseStack pose = instance.pose();
        pose.pushPose();

        float v = width / 2f;
        float v1 = height / 2f;
        pose.translate(x + v, y + v1, 0);
        ItemStack itemStack = player.getMainHandItem();

        float end = 0;
        if (itemStack.getItem() instanceof BaseTerraRepeaterItem repeaterItem) {
            if (player.isUsingItem() && player.getUseItem().equals(itemStack)) {
                end = Math.clamp(confluence$oldRepeaterCrosshairAngle + (float) 360 / repeaterItem.getReloadSpeed(player, itemStack), 0, 720);
            } else if (!itemStack.getOrDefault(ModDataComponentTypes.REPEATER_CONTENTS.get(), RepeaterContents.EMPTY).isEmpty()) {
                end = 45;
            }
        }

        float timeDeltaPartialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
        float v2 = Mth.lerp(timeDeltaPartialTick / 2, confluence$oldRepeaterCrosshairAngle, end);
        pose.mulPose(Axis.ZN.rotationDegrees(v2 % 360));

        float scale = 1f + (0.5f * (confluence$scale / 2));
        pose.scale(scale, scale, 1);

        confluence$oldRepeaterCrosshairAngle = v2;
        confluence$scale = Math.max(1, confluence$scale - timeDeltaPartialTick);

        pose.translate(-v, -v1, 0);
        original.call(instance, sprite, 0, 0, width, height);

        pose.popPose();
    }

    @Override
    @Unique
    public void confluence$setShooting() {
        confluence$scale = 3;
    }
}
