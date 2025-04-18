package org.confluence.mod.common.item.drill;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.item.pickaxe_axe.PickaxeAxeItem;

import java.util.function.Consumer;

public class DrillItem extends PickaxeAxeItem {
    public DrillItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        super(tier, rawDamage, rawSpeed, rarity);
    }

    public DrillItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        super(tier, rawDamage, rawSpeed, properties, rarity);
    }

    public DrillItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, Consumer<ItemAttributeModifiers.Builder> consumer, ModRarity rarity) {
        super(tier, rawDamage, rawSpeed, properties, consumer, rarity);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        return true; // 取消挥手
    }
}
