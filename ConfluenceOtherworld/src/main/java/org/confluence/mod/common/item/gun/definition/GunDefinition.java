package org.confluence.mod.common.item.gun.definition;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.GunPropertyComponent;

public record GunDefinition(int cooldown, float damage, float velocity, float knockback,
                            float critical, int penetrate, float inaccuracy, ModRarity rarity,
                            FireMode fireMode, GunProjectilePattern projectilePattern) {
    public GunDefinition {
        if (cooldown < 0) throw new IllegalArgumentException("cooldown must be non-negative");
        if (damage < 0) {
            throw new IllegalArgumentException("damage must be non-negative");
        }
        if (velocity < 0) {
            throw new IllegalArgumentException("velocity must be non-negative");
        }
        if (knockback < 0) {
            throw new IllegalArgumentException("knockback must be non-negative");
        }
        if (critical < 0 || critical > 1) {
            throw new IllegalArgumentException("critical must be between 0 and 1");
        }
        if (inaccuracy < 0) {
            throw new IllegalArgumentException("inaccuracy must be non-negative");
        }
        if (penetrate < -1) throw new IllegalArgumentException("penetrate must be -1 or greater");
        if (rarity == null || fireMode == null || projectilePattern == null) {
            throw new IllegalArgumentException("rarity, fireMode and projectilePattern are required");
        }
    }

    /// Compatibility constructor for the original single-projectile definition.
    public GunDefinition(int cooldown, float damage, float velocity, float knockback, float critical, int penetrate, float inaccuracy, ModRarity rarity, FireMode fireMode) {
        this(cooldown, damage, velocity, knockback, critical, penetrate, inaccuracy, rarity, fireMode, GunProjectilePattern.single());
    }

    public static GunDefinition manual(int cooldown, float damage, float velocity, float knockback, float critical, int penetrate, float inaccuracy, ModRarity rarity) {
        return new GunDefinition(cooldown, damage, velocity, knockback, critical, penetrate, inaccuracy, rarity, FireMode.MANUAL);
    }

    public static GunDefinition automatic(int cooldown, float damage, float velocity, float knockback, float critical, int penetrate, float inaccuracy, ModRarity rarity) {
        return new GunDefinition(cooldown, damage, velocity, knockback, critical, penetrate, inaccuracy, rarity, FireMode.AUTOMATIC);
    }

    public GunDefinition withProjectilePattern(GunProjectilePattern projectilePattern) {
        return new GunDefinition(cooldown, damage, velocity, knockback, critical, penetrate, inaccuracy, rarity, fireMode, projectilePattern);
    }

    public GunDefinition withGravity(float gravity) {
        return withProjectilePattern(GunProjectilePattern.gravity(gravity));
    }

    public GunDefinition withShotgunPattern(int minProjectiles, int maxProjectiles) {
        return withProjectilePattern(GunProjectilePattern.shotgun(minProjectiles, maxProjectiles));
    }

    public GunPropertyComponent component() {
        return new GunPropertyComponent(cooldown, damage, velocity, knockback, critical, penetrate, rarity);
    }
}
