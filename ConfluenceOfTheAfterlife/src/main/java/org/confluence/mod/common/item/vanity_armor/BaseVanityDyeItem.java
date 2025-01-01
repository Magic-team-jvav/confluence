package org.confluence.mod.common.item.vanity_armor;

import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.terra_curio.common.component.ModRarity;

public class BaseVanityDyeItem extends CustomRarityItem {
    public final int color;

    public BaseVanityDyeItem(int color, ModRarity rarity) {
        this(color, new Properties(), rarity);
    }

    public BaseVanityDyeItem(int color, Properties properties, ModRarity rarity) {
        super(properties, rarity);
        this.color = color;
    }
}
