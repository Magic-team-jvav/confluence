package org.confluence.mod.common.item.common;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.util.ModUtils;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.function.Consumer;

public class BasePickaxeAxeItem extends DiggerItem {
    public BasePickaxeAxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        super(ModItems.getAttackDamage(tier, rawDamage), ModItems.getAttackSpeed(rawSpeed), tier, ModTags.Blocks.MINEABLE_WITH_PICKAXE_AXE, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
    }

    public BasePickaxeAxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, Consumer<PortItemAttributeModifiers.PortBuilder> consumer, ModRarity rarity) {
        super(ModItems.getAttackDamage(tier, rawDamage), ModItems.getAttackSpeed(rawSpeed), tier, ModTags.Blocks.MINEABLE_WITH_PICKAXE_AXE, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.defaultModifiers = ModItems.mergeModifiers(defaultModifiers, consumer);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return Items.NETHERITE_AXE.useOn(context);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction itemAbility) {
        return ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(itemAbility) || ToolActions.DEFAULT_AXE_ACTIONS.contains(itemAbility);
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }
}
