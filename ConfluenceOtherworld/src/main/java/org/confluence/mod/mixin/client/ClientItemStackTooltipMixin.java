package org.confluence.mod.mixin.client;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.mod.api.item.IWeaponTooltip;
import org.confluence.mod.client.renderer.tooltip.WeaponTooltip;
import org.confluence.mod.common.item.summon.SummonerWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ClientItemStackTooltipMixin {
    /// 原版负责属性段输出和 HideFlags；接口只调整此处的显示数值，不接管真实属性入口。
    @WrapOperation(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getAttributeModifiers(Lnet/minecraft/world/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
    private Multimap<Attribute, AttributeModifier> weaponTooltipModifiers(ItemStack stack, EquipmentSlot slot, Operation<Multimap<Attribute, AttributeModifier>> original, @Local(argsOnly = true) Player player) {
        Multimap<Attribute, AttributeModifier> modifiers = original.call(stack, slot);
        return slot == EquipmentSlot.MAINHAND ? WeaponTooltip.tooltipModifiers(stack, player, modifiers) : modifiers;
    }

    /// 只统一武器自身追加的描述，原版属性提示和其他提示阶段保持原样。
    @WrapOperation(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"))
    private void grayWeaponDescriptions(Item item, ItemStack stack, Level level, List<Component> lines, TooltipFlag flag, Operation<Void> original) {
        int start = lines.size();
        original.call(item, stack, level, lines, flag);
        if (item instanceof IWeaponTooltip || item instanceof SummonerWeaponItem<?>) {
            for (int i = start; i < lines.size(); i++)
                lines.set(i, WeaponTooltip.grayDescription(lines.get(i)));
        }
    }
}
