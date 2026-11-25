package org.confluence.mod.common.item.mana;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.MagicMissileProjectile;

public class MagicMissileItem extends BaseDraggingStaffItem<MagicMissileProjectile> {
    public static final int COOLDOWN = 2;

    public MagicMissileItem() {
        super(ModRarity.GREEN, MagicMissileProjectile::new, 22, 14, 12, COOLDOWN, 0.04);
    }
}
