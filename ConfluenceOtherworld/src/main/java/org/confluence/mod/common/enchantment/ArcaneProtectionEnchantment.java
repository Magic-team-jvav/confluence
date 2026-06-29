package org.confluence.mod.common.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.confluence.mod.common.init.ModEnchantments;

public class ArcaneProtectionEnchantment extends AbstractEnchantment {
    public ArcaneProtectionEnchantment() {
        super(EnchantmentCategory.ARMOR, ModEnchantments.SlotGroups.ARMOR, 4);
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof ArcaneProtectionEnchantment) && !(other instanceof ProtectionEnchantment);
    }
}
