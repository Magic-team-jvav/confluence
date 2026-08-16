package org.confluence.mod.common.item.gun;

import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.armor.ModArmorBonus;

/// 太空枪：保留绿色弹道表现，并在陨石盔甲套装生效时免除本次事务魔力成本。
public class SpaceGunItem extends ManaGunItem {
    public SpaceGunItem(Properties properties) {
        super(properties, 6, 6.2f, 3.8f, 0.03f, 0.04f, 4, 0, ModRarity.GREEN, 6);
    }

    @Override
    public String getColorID() {
        return "space_gun";
    }

    /// 免魔力只由服务端当前盔甲套装状态决定。
    @Override
    protected boolean isManaFree(ProjectileFireContext context) {
        return ModArmorBonus.hasType(context.player(), ModArmorBonus.SPACE$GUN$FREE);
    }
}
