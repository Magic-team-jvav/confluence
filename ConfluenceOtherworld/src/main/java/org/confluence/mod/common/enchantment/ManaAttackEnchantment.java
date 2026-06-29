package org.confluence.mod.common.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import org.confluence.mod.common.init.ModEnchantments;

public class ManaAttackEnchantment extends AbstractEnchantment {
    public ManaAttackEnchantment() {
        super(ModEnchantments.Categories.MANA, ModEnchantments.SlotGroups.MAINHAND, 2);
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof ManaAttackEnchantment);
    }
}
