package org.confluence.mod.common.item.hamaxe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.hammer.HammerItem;

import java.util.function.Consumer;

public class HamaxeItem extends DiggerItem {
    public HamaxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        super(tier, ModTags.Blocks.MINEABLE_WITH_HAMAXE, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(tier, (rawDamage - tier.getAttackDamageBonus() - 1), rawSpeed - 4)));
    }

    public HamaxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, Consumer<ItemAttributeModifiers.Builder> consumer, ModRarity rarity) {
        super(tier, ModTags.Blocks.MINEABLE_WITH_HAMAXE, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS, ModItems.createAttributes(tier, (rawDamage - tier.getAttackDamageBonus() - 1), rawSpeed - 4, consumer)));
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        HammerItem.hammerMineBlock(stack, level, state, pos, miningEntity);
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return Items.NETHERITE_AXE.useOn(context);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(itemAbility);
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return true;
    }
}
