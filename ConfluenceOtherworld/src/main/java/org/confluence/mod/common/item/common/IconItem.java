package org.confluence.mod.common.item.common;

import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.terra_curio.common.component.ModRarity;

public class IconItem extends CustomRarityItem {
    public IconItem() {
        super(new Properties().stacksTo(1).fireResistant(), ModRarity.MASTER);
    }
}
