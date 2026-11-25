package org.confluence.mod.common.item.mana;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.RainbowProjectile;

public class RainbowRodItem extends BaseDraggingStaffItem<RainbowProjectile> {
    public static final int COOLDOWN = 2;

    public RainbowRodItem() {
        super(ModRarity.PINK, RainbowProjectile::new, 30, 21, 18, COOLDOWN, 0.04);
    }
}
