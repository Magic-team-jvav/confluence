package org.confluence.mod.common.item.hamaxe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.hammer.HammerItem;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;

import java.util.Map;

public class HamaxeItem extends DiggerItem {
    public HamaxeItem(Tier tier, float rawDamage, float rawSpeed, boolean unbreakable) {
        this(tier, rawDamage, rawSpeed, unbreakable, ModRarity.WHITE);
    }

    public HamaxeItem(Tier tier, float rawDamage, float rawSpeed, boolean unbreakable, ModRarity rarity) {
        super(tier, ModTags.Blocks.MINEABLE_WITH_HAMAXE, unbreakable(new Properties(), unbreakable).component(TCDataComponentTypes.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(tier, (rawDamage - tier.getAttackDamageBonus() - 1), rawSpeed - 4)));
    }

    public HamaxeItem(Tier tier, float rawDamage, float rawSpeed, boolean unbreakable, ModRarity rarity, Map<Holder<Attribute>, AttributeModifier> modifiers) {
        super(tier, ModTags.Blocks.MINEABLE_WITH_HAMAXE, unbreakable(new Properties(), unbreakable).component(TCDataComponentTypes.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS, getAttributeModifiers(tier, rawDamage, rawSpeed, modifiers)));
    }

    private static Properties unbreakable(Properties properties, boolean unbreakable) {
        if (unbreakable) properties.component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE);
        return properties;
    }

    private static ItemAttributeModifiers getAttributeModifiers(Tier tier, float rawDamage, float rawSpeed, Map<Holder<Attribute>, AttributeModifier> modifiers) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, (rawDamage - tier.getAttackDamageBonus() - 1) + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                ).add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, (double) rawSpeed - 4, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                );
        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : modifiers.entrySet()) {
            builder.add(entry.getKey(), entry.getValue(), EquipmentSlotGroup.MAINHAND);
        }
        return builder.build();
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
