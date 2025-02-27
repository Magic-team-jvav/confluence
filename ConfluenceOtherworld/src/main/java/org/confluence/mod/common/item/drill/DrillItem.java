package org.confluence.mod.common.item.drill;

import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.confluence.mod.common.item.pickaxe_axe.PickaxeAxeItem;
import org.confluence.terra_curio.common.component.ModRarity;

import java.util.Map;

public class DrillItem extends PickaxeAxeItem {
    public DrillItem(Tier tier, float rawDamage, float rawSpeed) {
        super(tier, rawDamage, rawSpeed);
    }

    public DrillItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        super(tier, rawDamage, rawSpeed, rarity);
    }

    public DrillItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity, Map<Holder<Attribute>, AttributeModifier> modifiers) {
        super(tier, rawDamage, rawSpeed, rarity, modifiers);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        return true; // 取消挥手
    }
}
