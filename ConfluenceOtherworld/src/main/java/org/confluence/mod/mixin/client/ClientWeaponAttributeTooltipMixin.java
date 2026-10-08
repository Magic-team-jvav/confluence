package org.confluence.mod.mixin.client;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.common.util.AttributeUtil;
import org.confluence.mod.client.renderer.tooltip.WeaponTooltip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(AttributeUtil.class)
public abstract class ClientWeaponAttributeTooltipMixin {
    /// 仅替换主手提示的局部数值；保留隐藏属性和其他模组跳过属性的事件。
    @WrapOperation(method = "applyModifierTooltips", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/util/AttributeUtil;getSortedModifiers(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlotGroup;)Lcom/google/common/collect/Multimap;"))
    private static Multimap<Holder<Attribute>, AttributeModifier> weaponTooltipModifiers(ItemStack stack, EquipmentSlotGroup group,
                                                                                         Operation<Multimap<Holder<Attribute>, AttributeModifier>> original, @Local(argsOnly = true) AttributeTooltipContext context) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = original.call(stack, group);
        return group == EquipmentSlotGroup.MAINHAND ? WeaponTooltip.tooltipModifiers(stack, context.player(), modifiers) : modifiers;
    }

    /// 伤害、暴击和攻速共用原生属性格式，并按统一顺序输出一次。
    @WrapOperation(method = "applyModifierTooltips", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/util/AttributeUtil;applyTextFor(Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;Lcom/google/common/collect/Multimap;Lnet/neoforged/neoforge/common/util/AttributeTooltipContext;)V"))
    private static void weaponTooltipOrder(ItemStack stack, Consumer<Component> tooltip, Multimap<Holder<Attribute>, AttributeModifier> modifiers,
                                           AttributeTooltipContext context, Operation<Void> original, @Local EquipmentSlotGroup group) {
        if (group == EquipmentSlotGroup.MAINHAND) {
            WeaponTooltip.renderModifiers(stack, tooltip, modifiers, context, map -> original.call(stack, tooltip, map, context));
        } else original.call(stack, tooltip, modifiers, context);
    }
}
