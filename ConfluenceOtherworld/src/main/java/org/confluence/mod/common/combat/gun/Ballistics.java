package org.confluence.mod.common.combat.gun;

public record Ballistics(float damage, float critical, float velocity, float knockback,
                         int penetrate, float inaccuracy) {}
