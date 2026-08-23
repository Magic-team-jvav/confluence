package org.confluence.mod.common.item.yoyo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;

import java.util.List;

/// 悠悠球品种的不可变数值与命中行为。
public record YoyoDefinition(float attackDamage, float maximumRange, int stringColor,
                             int lifetimeTicks, List<YoyoHitEffect> hitEffects) {
    public YoyoDefinition {
        if (!Float.isFinite(attackDamage) || attackDamage < 0.0F)
            throw new IllegalArgumentException("Yoyo attack damage must be finite and non-negative");
        if (!Float.isFinite(maximumRange) || maximumRange < 1.0F)
            throw new IllegalArgumentException("Yoyo range must be finite and at least 1.0");
        if (lifetimeTicks <= 0)
            throw new IllegalArgumentException("Yoyo lifetime must be positive");
        stringColor = 0xFF000000 | stringColor & 0x00FFFFFF;
        hitEffects = List.copyOf(hitEffects);
    }

    public static YoyoDefinition of(float attackDamage, float maximumRange, int stringColor, float lifetimeSeconds, YoyoHitEffect... hitEffects) {
        return new YoyoDefinition(attackDamage, maximumRange, stringColor, Math.max(1, Math.round(lifetimeSeconds * 20.0F)), List.of(hitEffects));
    }

    public void applyHitEffects(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        for (YoyoHitEffect effect : hitEffects) effect.apply(yoyo, owner, target);
    }
}
