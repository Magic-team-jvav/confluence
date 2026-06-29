package org.confluence.mod.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ManaIOEnchantment extends AbstractEnchantment {
    public ManaIOEnchantment(EnchantmentCategory category, EquipmentSlot[] applicableSlots, int maxLevel) {
        super(category, applicableSlots, maxLevel);
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof ManaIOEnchantment);
    }

    @Override
    public boolean isTradeable() {
        return false;
    }
}
