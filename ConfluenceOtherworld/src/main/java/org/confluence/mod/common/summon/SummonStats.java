package org.confluence.mod.common.summon;

public record SummonStats(float baseDamage) {
    public SummonStats {
        if (!Float.isFinite(baseDamage) || baseDamage < 0.0F) {
            throw new IllegalArgumentException("Summon damage must be finite and non-negative");
        }
    }
}
