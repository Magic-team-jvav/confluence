package org.confluence.mod.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.confluence.mod.api.item.IWeaponTooltip;
import org.confluence.mod.client.renderer.tooltip.WeaponTooltip;
import org.confluence.mod.common.item.summon.SummonerWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ClientItemStackTooltipMixin {
    /// 只统一武器自身追加的描述，原版属性提示和其他提示阶段保持原样。
    @WrapOperation(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"))
    private void grayWeaponDescriptions(Item item, ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag, Operation<Void> original) {
        int start = lines.size();
        original.call(item, stack, context, lines, flag);
        if (item instanceof IWeaponTooltip || item instanceof SummonerWeaponItem<?>) {
            for (int i = start; i < lines.size(); i++)
                lines.set(i, WeaponTooltip.grayDescription(lines.get(i)));
        }
    }
}
