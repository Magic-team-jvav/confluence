package org.confluence.mod.common.item.gun.definition.behavior;

import org.confluence.mod.common.item.gun.definition.BulletBehavior;

public abstract class AbstractBulletBehavior implements BulletBehavior {
    private final String tooltipKey;

    protected AbstractBulletBehavior(String tooltipKey) {
        this.tooltipKey = tooltipKey;
    }

    @Override
    public String tooltipKey() {
        return tooltipKey;
    }
}
