package org.confluence.mod.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import org.confluence.mod.common.init.ModEnchantments;

/**
 * 提高同一种回旋镖可同时存在的投掷数量。
 *
 * <p>每级增加一个在途实体，上限三级；附魔本身只声明适用物品和附魔台参数，
 * 实际数量检查仍留在回旋镖的统一投掷入口中。</p>
 */
public final class MultiBoomerangEnchantment extends Enchantment {
    public MultiBoomerangEnchantment() {
        super(
                Rarity.VERY_RARE,
                ModEnchantments.Categories.BOOMERANG,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND}
        );
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

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return other != this && super.checkCompatibility(other);
    }
}
