package org.confluence.mod.common.item.sword;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModTiers;

public class BatBatItem extends BaseSwordItem {
    public BatBatItem() {
        super(ModTiers.UNBREAKABLE, ModRarity.ORANGE, 21, 0.6F, SwordDefinition.builder()
                .tooltipImage()
                .attribute(Attributes.ENTITY_INTERACTION_RANGE, 2, AttributeModifier.Operation.ADD_VALUE)
                .specialSweep(0.8F));
    }

    @Override
    protected void onDamage(ItemStack weapon, LivingEntity attacker, LivingEntity victim, DamageSource source) {
        attacker.heal(1.0F);
    }
}
