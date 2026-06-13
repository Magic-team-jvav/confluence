package org.confluence.mod.util;

import org.confluence.mod.common.component.BulletPropertyComponent;
import org.confluence.mod.common.component.GunPropertyComponent;

public class AmmoDataContext {
    private final GunPropertyComponent gunComponent;
    private final BulletPropertyComponent bulletComponent;
    private final float inaccuracy;

    public AmmoDataContext(GunPropertyComponent gunComponent, BulletPropertyComponent bulletComponent, float inaccuracy) {
        this.gunComponent = gunComponent;
        this.bulletComponent = bulletComponent;
        this.inaccuracy = inaccuracy;
    }

    public float getDamage() {
        return gunComponent.damage() + bulletComponent.damage();
    }

    public float getCritical() {
        return gunComponent.critical();
    }

    public float getVelocity() {
        return (gunComponent.velocity() + bulletComponent.velocity()) * bulletComponent.velocityMultiplier();
    }

    public float getKnockback() {
        return gunComponent.knockback() + bulletComponent.knockback();
    }

    public int getPenetrate() {
        if (bulletComponent.penetrate() == -1 || gunComponent.penetrate() == -1) return -1;
        return gunComponent.penetrate() + bulletComponent.penetrate();
    }

    public float getInaccuracy() {
        return inaccuracy;
    }
}
