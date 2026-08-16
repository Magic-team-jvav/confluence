package org.confluence.mod.common.summon;

public record SummonStats(float baseDamage, float knockback, boolean critical) {
    public SummonStats {
        if (!Float.isFinite(baseDamage) || baseDamage < 0.0F) {
            throw new IllegalArgumentException("Summon damage must be finite and non-negative");
        }
        if (!Float.isFinite(knockback) || knockback < 0.0F) {
            throw new IllegalArgumentException("Summon knockback must be finite and non-negative");
        }
    }
}
