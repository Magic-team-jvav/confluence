package org.confluence.mod.common.item.gun.definition.behavior;

import org.confluence.lib.util.LibEntityUtils.WeaponTargeting;
import org.confluence.mod.common.item.gun.definition.BulletBehavior;

public abstract class AbstractBulletBehavior implements BulletBehavior {
    private final String tooltipKey;
    private final WeaponTargeting targeting;

    protected AbstractBulletBehavior(String tooltipKey) {
        this(tooltipKey, WeaponTargeting.DISABLED);
    }

    protected AbstractBulletBehavior(String tooltipKey, WeaponTargeting targeting) {
        this.tooltipKey = tooltipKey;
        this.targeting = targeting;
    }

    @Override
    public WeaponTargeting targeting() {
        return targeting;
    }

    @Override
    public String tooltipKey() {
        return tooltipKey;
    }
}
