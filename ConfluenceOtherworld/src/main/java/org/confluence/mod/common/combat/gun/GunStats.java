package org.confluence.mod.common.combat.gun;

/// 枪械自身参与一次射击结算的不可变数值。
public record GunStats(
        float damage,
        float velocity,
        float knockback,
        float critical,
        int penetrate,
        float inaccuracy
) {}
