package org.confluence.mod.common.combat.gun;

/// Runtime-independent ammunition values used by the ballistics pipeline.
public record AmmoStats(float damage, float velocity, float velocityMultiplier, float knockback,
                        int penetrate) {}
