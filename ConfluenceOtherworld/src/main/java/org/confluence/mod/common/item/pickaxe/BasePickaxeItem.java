package org.confluence.mod.common.item.pickaxe;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.util.ModUtils;

import java.util.function.Consumer;

public class BasePickaxeItem extends PickaxeItem {
    public BasePickaxeItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        this(tier, rawDamage, rawSpeed, new Properties(), rarity);
    }

    public BasePickaxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        super(tier, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity).attributes(createAttributes(tier, rawDamage - tier.getAttackDamageBonus() - 1.0F, rawSpeed - 4.0F)));
    }

    public BasePickaxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, Consumer<ItemAttributeModifiers.Builder> consumer, ModRarity rarity) {
        super(tier, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity).attributes(ModItems.createAttributes(tier, rawDamage - tier.getAttackDamageBonus() - 1.0F, rawSpeed - 4.0F, consumer)));
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }
}
