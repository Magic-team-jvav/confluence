package org.confluence.mod.common.item.vanity_armor;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import software.bernie.geckolib.util.Color;

public class BaseDyeItem extends CustomRarityItem {
    public final Color colour;
    public final int color;

    public BaseDyeItem(Properties properties, ModRarity rarity, int argb) {
        super(properties, rarity);
        this.colour = new Color(argb);
        this.color = argb;
    }

    public BaseDyeItem(ModRarity rarity, int color) {
        this(new Properties(), rarity, color);
    }
}
