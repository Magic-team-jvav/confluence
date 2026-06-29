package org.confluence.mod.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public abstract class AbstractEnchantment extends Enchantment {
    private final int maxLevel;

    public AbstractEnchantment(EnchantmentCategory category, EquipmentSlot[] applicableSlots, int maxLevel) {
        super(Rarity.UNCOMMON, category, applicableSlots);
        this.maxLevel = maxLevel;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int getMinCost(int level) {
        return 25 + 25 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return 75 + 25 * (level - 1);
    }

    @Override
    protected abstract boolean checkCompatibility(Enchantment other);
}
