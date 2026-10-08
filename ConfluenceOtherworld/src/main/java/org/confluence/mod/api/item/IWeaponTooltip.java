package org.confluence.mod.api.item;

import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.confluence.lib.common.LibAttributes;
import org.jetbrains.annotations.Nullable;

/// 武器只提供实际使用的数值口径；属性计算、原版提示样式和排版由客户端统一处理。
public interface IWeaponTooltip {
    /// 伤害所属属性，默认使用近战攻击伤害。
    default Attribute getTooltipDamageAttribute() {
        return LibAttributes.getAttackDamage().value();
    }

    /// 将属性值转换为本武器伤害；默认沿用原版近战和通用附魔伤害。
    default double getTooltipDamage(ItemStack stack, double attributeValue) {
        return attributeValue + EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
    }

    /// 合并武器自身额外暴击与属性中的暴击率，不假定不存在的基础暴击。
    default double getTooltipCriticalChance(ItemStack stack, double attributeValue) {
        return attributeValue;
    }

    /// 普通近战使用原版攻击速度；其他武器按实际冷却或挥动周期覆盖。
    default double getTooltipAttackSpeed(ItemStack stack, double attributeValue) {
        return attributeValue;
    }

    /// 少数挥动行为存在独立效果倍率时，可读取玩家上下文；不能修改玩家状态。
    default double getTooltipAttackSpeed(ItemStack stack, double attributeValue, @Nullable Player player) {
        return getTooltipAttackSpeed(stack, attributeValue);
    }

    /// 使用与受攻击速度影响的物品冷却相同的取整口径。
    static double speedFromCooldown(int ticks, double attackSpeed) {
        return speedFromCooldown(ticks, attackSpeed, null);
    }

    /// 与实际冷却计算一致，玩家基础攻速被其他模组调整时也不能固定按四计算。
    static double speedFromCooldown(int ticks, double attackSpeed, @Nullable Player player) {
        double baseSpeed = player == null ? 4.0 : player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
        if (baseSpeed <= 0.0 || attackSpeed <= 0.0) return 20.0 / Math.max(1, ticks);
        return 20.0 / Math.max(1, Math.ceil(ticks * baseSpeed / attackSpeed));
    }
}
