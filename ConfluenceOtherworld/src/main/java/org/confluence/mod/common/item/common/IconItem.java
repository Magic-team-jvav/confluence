package org.confluence.mod.common.item.common;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;

public class IconItem extends CustomRarityItem {
    public IconItem() {
        super(new Properties().stacksTo(1).fireResistant(), ModRarity.MASTER);
    }
}
