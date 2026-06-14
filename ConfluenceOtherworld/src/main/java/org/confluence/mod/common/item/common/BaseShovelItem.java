package org.confluence.mod.common.item.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.util.ModUtils;
import org.mesdag.portlib.wrapper.world.item.PortItem;

public class BaseShovelItem extends ShovelItem {
    public BaseShovelItem(Tier tier, float rawDamage, float rawSpeed, PortItem.PortProperties properties) {
        super(tier, ModItems.getAttackDamage(tier, rawDamage), ModItems.getAttackSpeed(rawSpeed), properties);
    }

    public BaseShovelItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        this(tier, rawDamage, rawSpeed, new PortItem.PortProperties(), rarity);
    }

    public BaseShovelItem(Tier tier, float rawDamage, float rawSpeed, PortItem.PortProperties properties, ModRarity rarity) {
        super(tier, ModItems.getAttackDamage(tier, rawDamage), ModItems.getAttackSpeed(rawSpeed), properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }
}
