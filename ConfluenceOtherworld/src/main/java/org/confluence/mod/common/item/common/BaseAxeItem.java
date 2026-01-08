package org.confluence.mod.common.item.common;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.confluence.lib.common.component.ModRarity;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@Deprecated(since = "1.2.0")
@ApiStatus.ScheduledForRemoval(inVersion = "1.3.0")
public class BaseAxeItem extends org.confluence.mod.common.item.axe.BaseAxeItem {
    public BaseAxeItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        super(tier, rawDamage, rawSpeed, rarity);
    }

    public BaseAxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        super(tier, rawDamage, rawSpeed, properties, rarity);
    }

    public BaseAxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, Consumer<ItemAttributeModifiers.Builder> consumer, ModRarity rarity) {
        super(tier, rawDamage, rawSpeed, properties, consumer, rarity);
    }
}
