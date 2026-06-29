package org.confluence.mod.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class MagicAttackEnchantment extends AbstractEnchantment {
    public MagicAttackEnchantment(EnchantmentCategory category, EquipmentSlot[] applicableSlots, int maxLevel) {
        super(category, applicableSlots, maxLevel);
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof MagicAttackEnchantment);
    }

    @Override
    public boolean isTradeable() {
        return false;
    }
}
