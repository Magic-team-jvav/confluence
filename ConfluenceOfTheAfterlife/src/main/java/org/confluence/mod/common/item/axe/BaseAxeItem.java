package org.confluence.mod.common.item.axe;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Tier;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;

public class BaseAxeItem extends AxeItem {
    public BaseAxeItem(Tier tier, float rawDamage, float rawSpeed) {
        this(tier, rawDamage, rawSpeed, ModRarity.WHITE);
    }

    public BaseAxeItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        super(tier, new Properties().component(TCDataComponentTypes.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(tier, (rawDamage - tier.getAttackDamageBonus() - 1), rawSpeed - 4)));
    }
}

