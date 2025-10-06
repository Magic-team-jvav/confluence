package org.confluence.mod.common.item.gun;

import org.confluence.lib.common.component.ModRarity;

public class SpaceGunItem extends ManaGunItem{
    public SpaceGunItem(Properties properties) {
        super(properties, 6, 6.2f, 3.8f, 0.03f, 0.04f, 2, 0, ModRarity.GREEN, 6);
    }

    @Override
    public String getColorID() {
        return "space_gun";
    }
}
