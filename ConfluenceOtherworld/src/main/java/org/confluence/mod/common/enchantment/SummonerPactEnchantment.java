package org.confluence.mod.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/// 提高佩戴者能够维持的仆从容量。
///
/// 该附魔只允许施加在头部护甲上，等级和附魔台消耗与 1.21 侧保持一致；
/// 实际属性修饰值由统一的物品属性事件根据附魔等级追加。
public final class SummonerPactEnchantment extends Enchantment {
    public SummonerPactEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    @Override
    public int getMinCost(int level) {
        return 10 + 8 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return 18 + 8 * (level - 1);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
