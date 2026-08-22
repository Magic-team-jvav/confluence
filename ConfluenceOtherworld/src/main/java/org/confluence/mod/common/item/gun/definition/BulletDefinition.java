package org.confluence.mod.common.item.gun.definition;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.BulletPropertyComponent;

public record BulletDefinition(float damage, float velocity, float velocityMultiplier,
                               float knockback, int penetrate, ModRarity rarity, boolean infinity,
                               BulletBehavior behavior, BulletImpactEffect impactEffect) {
    public BulletDefinition {
        if (!Float.isFinite(damage) || damage < 0) {
            throw new IllegalArgumentException("damage must be finite and non-negative");
        }
        if (!Float.isFinite(velocity) || velocity < 0) {
            throw new IllegalArgumentException("velocity must be finite and non-negative");
        }
        if (!Float.isFinite(velocityMultiplier) || velocityMultiplier < 0) {
            throw new IllegalArgumentException("velocityMultiplier must be finite and non-negative");
        }
        if (!Float.isFinite(knockback) || knockback < 0) {
            throw new IllegalArgumentException("knockback must be finite and non-negative");
        }
        if (penetrate < -1) throw new IllegalArgumentException("penetrate must be -1 or greater");
        if (rarity == null || behavior == null || impactEffect == null) {
            throw new IllegalArgumentException("rarity, behavior and impact effect are required");
        }
    }

    /// Compatibility constructor for the original normal projectile definition.
    public BulletDefinition(float damage, float velocity, float velocityMultiplier, float knockback, int penetrate, ModRarity rarity, boolean infinity) {
        this(damage, velocity, velocityMultiplier, knockback, penetrate, rarity, infinity, BulletBehavior.NORMAL, BulletImpactEffect.NONE);
    }

    public BulletDefinition(float damage, float velocity, float velocityMultiplier, float knockback, int penetrate, ModRarity rarity, boolean infinity, BulletBehavior behavior) {
        this(damage, velocity, velocityMultiplier, knockback, penetrate, rarity, infinity, behavior, BulletImpactEffect.NONE);
    }

    public BulletDefinition withBehavior(BulletBehavior behavior) {
        return new BulletDefinition(damage, velocity, velocityMultiplier, knockback, penetrate, rarity, infinity, behavior, impactEffect);
    }

    public BulletDefinition withImpactEffect(BulletImpactEffect impactEffect) {
        return new BulletDefinition(damage, velocity, velocityMultiplier, knockback, penetrate, rarity, infinity, behavior, impactEffect);
    }

    public BulletPropertyComponent component() {
        return new BulletPropertyComponent(damage, velocity, velocityMultiplier, knockback, penetrate, rarity, infinity);
    }
}
