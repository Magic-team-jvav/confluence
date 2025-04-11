package org.confluence.mod.mixin.integration.betteradvancements;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.gui.AchievementToast;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "betteradvancements.common.gui.BetterAdvancementWidget", remap = false)
public abstract class BetterAdvancementWidgetMixin {
    @Shadow
    @Final
    private AdvancementNode advancementNode;

    @WrapWithCondition(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderFakeItem(Lnet/minecraft/world/item/ItemStack;II)V"))
    private boolean renderIcon(GuiGraphics instance, ItemStack stack, int x, int y) {
        return AchievementToast.renderWidgetIcon(advancementNode.holder().id(), instance, x, y);
    }
}
