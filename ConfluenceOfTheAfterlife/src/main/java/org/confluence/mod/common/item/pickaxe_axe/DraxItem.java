package org.confluence.mod.common.item.pickaxe_axe;

import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.confluence.terra_curio.common.component.ModRarity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DraxItem extends PickaxeAxeItem {
    public DraxItem(Tier tier, float rawDamage, float rawSpeed) {
        super(tier, rawDamage, rawSpeed);
    }

    public DraxItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        super(tier, rawDamage, rawSpeed, rarity);
    }

    public DraxItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity, Map<Holder<Attribute>, AttributeModifier> modifiers) {
        super(tier, rawDamage, rawSpeed, rarity, modifiers);
    }

    @Override
    public boolean onEntitySwing(@NotNull ItemStack stack, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        return true; // 取消挥手
    }
}
