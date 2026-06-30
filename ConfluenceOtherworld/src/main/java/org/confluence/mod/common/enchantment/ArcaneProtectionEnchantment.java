package org.confluence.mod.common.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.confluence.lib.util.LibEnchantmentUtils;

public class ArcaneProtectionEnchantment extends AbstractEnchantment {
    public ArcaneProtectionEnchantment() {
        super(EnchantmentCategory.ARMOR, LibEnchantmentUtils.SlotGroups.ARMOR, 4);
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof ArcaneProtectionEnchantment) && !(other instanceof ProtectionEnchantment);
    }
}
