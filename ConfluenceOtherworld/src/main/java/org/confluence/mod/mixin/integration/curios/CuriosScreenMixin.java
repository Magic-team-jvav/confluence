package org.confluence.mod.mixin.integration.curios;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.client.gui.CuriosScreen;

import java.util.List;
import java.util.Optional;

/// todo [等待curios作者将issue解决后移除，目前作者已确认该bug](https://github.com/TheIllusiveC4/Curios/issues/536)
@Mixin(value = CuriosScreen.class, remap = false)
public abstract class CuriosScreenMixin {
    @WrapOperation(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"))
    private void fix(GuiGraphics instance, Font font, List<Component> tooltipLines, Optional<TooltipComponent> visualTooltipComponent, int mouseX, int mouseY, Operation<Void> original, @Local ItemStack stack) {
        instance.renderTooltip(font, tooltipLines, visualTooltipComponent, stack, mouseX, mouseY);
    }
}
