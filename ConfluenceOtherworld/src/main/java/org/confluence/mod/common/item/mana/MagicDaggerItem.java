package org.confluence.mod.common.item.mana;

import net.minecraft.sounds.SoundEvent;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.MagicDaggerProjectile;
import org.confluence.mod.common.init.ModSoundEvents;

/**
 * 为统一法杖事务补充投掷物品外观和初始穿透阶段。
 */
public class MagicDaggerItem extends ManaStaffItem<MagicDaggerProjectile> {
    public MagicDaggerItem() {
        super(ModRarity.LIGHT_RED, MagicDaggerProjectile::new, 20, 6, 12, 2, 0.04);
    }

    @Override
    protected void configureProjectile(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            MagicDaggerProjectile projectile
    ) {
        projectile.setItem(context.weapon());
        projectile.setFlyTicks(10);
    }

    @Override
    protected SoundEvent getShootSound() {
        return ModSoundEvents.WAVING.get();
    }
}
