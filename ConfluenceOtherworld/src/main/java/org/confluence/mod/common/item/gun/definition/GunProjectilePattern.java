package org.confluence.mod.common.item.gun.definition;

import net.minecraft.util.RandomSource;

/// Describes how a gun turns one resolved shot into projectile entities.
///
/// <p>This is deliberately part of the gun definition instead of a gun item
/// subclass. A gun item keeps its data; the combat pipeline interprets this
/// value when the server fires.</p>
public record GunProjectilePattern(Type type, float gravity, int minProjectiles,
                                   int maxProjectiles) {
    public GunProjectilePattern {
        if (type == null) {
            throw new IllegalArgumentException("projectile pattern type is required");
        }
        if (!Float.isFinite(gravity) || gravity < 0.0F) {
            throw new IllegalArgumentException("gravity must be finite and non-negative");
        }
        if (minProjectiles < 1 || maxProjectiles < minProjectiles) {
            throw new IllegalArgumentException("projectile count range is invalid");
        }
        if (type == Type.SINGLE && (gravity != 0.0F || minProjectiles != 1 || maxProjectiles != 1)) {
            throw new IllegalArgumentException("single projectile pattern cannot carry extra parameters");
        }
        if (type == Type.GRAVITY && (gravity <= 0.0F || minProjectiles != 1 || maxProjectiles != 1)) {
            throw new IllegalArgumentException("gravity projectile pattern requires one positive gravity value");
        }
        if (type == Type.SHOTGUN && (gravity != 0.0F || minProjectiles < 2)) {
            throw new IllegalArgumentException("shotgun projectile pattern requires at least two projectiles");
        }
    }

    public static GunProjectilePattern single() {
        return new GunProjectilePattern(Type.SINGLE, 0.0F, 1, 1);
    }

    public static GunProjectilePattern gravity(float gravity) {
        return new GunProjectilePattern(Type.GRAVITY, gravity, 1, 1);
    }

    public static GunProjectilePattern shotgun(int minProjectiles, int maxProjectiles) {
        return new GunProjectilePattern(Type.SHOTGUN, 0.0F, minProjectiles, maxProjectiles);
    }

    public int sampleProjectileCount(RandomSource random) {
        return minProjectiles + random.nextInt(maxProjectiles - minProjectiles + 1);
    }

    public enum Type {
        SINGLE,
        GRAVITY,
        SHOTGUN
    }
}
