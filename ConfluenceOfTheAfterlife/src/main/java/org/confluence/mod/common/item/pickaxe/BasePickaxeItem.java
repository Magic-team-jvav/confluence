package org.confluence.mod.common.item.pickaxe;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;

public class BasePickaxeItem extends PickaxeItem {
    public BasePickaxeItem(Tier tier, float rawDamage, float rawSpeed) {
        this(tier, rawDamage, rawSpeed, new Properties(), ModRarity.WHITE);
    }

    public BasePickaxeItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        this(tier, rawDamage, rawSpeed, new Properties(), rarity);
    }

    public BasePickaxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        super(tier, properties.component(TCDataComponentTypes.MOD_RARITY, rarity)
                .attributes(createAttributes(tier, rawDamage - tier.getAttackDamageBonus() - 1.0F, rawSpeed - 4.0F)));
    }

    @Override
    public MutableComponent getName(ItemStack pStack) {
        return Component.translatable(getDescriptionId(pStack)).withColor(pStack.get(TCDataComponentTypes.MOD_RARITY).getColor());
    }
}
