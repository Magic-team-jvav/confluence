package org.confluence.mod.common.combat.gun;

/**
 * 弹药自身参与一次射击结算的不可变数值。
 */
public record AmmoStats(
        float damage,
        float velocity,
        float velocityMultiplier,
        float knockback,
        int penetrate
) {}
