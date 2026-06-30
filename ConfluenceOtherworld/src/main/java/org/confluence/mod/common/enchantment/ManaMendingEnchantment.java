package org.confluence.mod.common.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.MendingEnchantment;
import org.confluence.lib.util.LibEnchantmentUtils;

public class ManaMendingEnchantment extends AbstractEnchantment {
    public ManaMendingEnchantment() {
        super(EnchantmentCategory.BREAKABLE, LibEnchantmentUtils.SlotGroups.ANY, 3);
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof ManaMendingEnchantment) && !(other instanceof MendingEnchantment);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }
}
