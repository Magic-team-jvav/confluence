package org.confluence.mod.common.item.pickaxe_axe;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PickaxeAxeItem extends DiggerItem {
    public PickaxeAxeItem(Tier tier, float rawDamage, float rawSpeed) {
        this(tier, rawDamage, rawSpeed, ModRarity.WHITE);
    }

    public PickaxeAxeItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        super(tier, ModTags.Blocks.MINEABLE_WITH_PICKAXE_AXE, new Properties().component(TCDataComponentTypes.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(tier, (rawDamage - tier.getAttackDamageBonus() - 1), rawSpeed - 4)));
    }

    public PickaxeAxeItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity, Map<Holder<Attribute>, AttributeModifier> modifiers) {
        super(tier, ModTags.Blocks.MINEABLE_WITH_PICKAXE_AXE, new Properties().component(TCDataComponentTypes.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS, getAttributeModifiers(tier, rawDamage, rawSpeed, modifiers)));
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
    public InteractionResult useOn(UseOnContext context) {
        return Items.NETHERITE_AXE.useOn(context);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(itemAbility) || ItemAbilities.DEFAULT_AXE_ACTIONS.contains(itemAbility);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return Component.translatable(getDescriptionId(pStack)).withColor(pStack.getOrDefault(TCDataComponentTypes.MOD_RARITY, ModRarity.WHITE).getColor());
    }
}
