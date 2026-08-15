package org.confluence.mod.common.data;

import org.confluence.mod.common.component.BulletPropertyComponent;
import org.confluence.mod.common.component.GunPropertyComponent;
import org.confluence.mod.common.combat.gun.AmmoStats;
import org.confluence.mod.common.combat.gun.Ballistics;
import org.confluence.mod.common.combat.gun.BallisticsResolver;
import org.confluence.mod.common.combat.gun.GunStats;

/**
 * @deprecated 使用 {@link BallisticsResolver}。保留本类只为兼容现有扩展源码，内部不再维护第二套计算公式。
 */
@Deprecated(forRemoval = true)
public class AmmoDataContext {
    private final Ballistics ballistics;

    public AmmoDataContext(GunPropertyComponent gunComponent, BulletPropertyComponent bulletComponent, float inaccuracy) {
        this.ballistics = BallisticsResolver.resolve(
                new GunStats(
                        gunComponent.damage(), gunComponent.velocity(), gunComponent.knockback(),
                        gunComponent.critical(), gunComponent.penetrate(), inaccuracy),
                new AmmoStats(
                        bulletComponent.damage(), bulletComponent.velocity(), bulletComponent.velocityMultiplier(),
                        bulletComponent.knockback(), bulletComponent.penetrate())
        );
    }

    public float getDamage() {
        return ballistics.damage();
    }

    public float getCritical() {
        return ballistics.critical();
    }

    public float getVelocity() {
        return ballistics.velocity();
    }

    public float getKnockback() {
        return ballistics.knockback();
    }

    public int getPenetrate() {
        return ballistics.penetrate();
    }

    public float getInaccuracy() {
        return ballistics.inaccuracy();
    }
}
