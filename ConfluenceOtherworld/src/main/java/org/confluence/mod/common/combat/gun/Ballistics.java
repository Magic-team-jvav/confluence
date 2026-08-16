package org.confluence.mod.common.combat.gun;

/// 枪械和弹药合并后、进入事件与 MagicLib 快照前的弹道结果。
public record Ballistics(
        float damage,
        float critical,
        float velocity,
        float knockback,
        int penetrate,
        float inaccuracy
) {}
