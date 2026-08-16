package org.confluence.mod.common.item.gun.definition;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.BulletPropertyComponent;

/// 弹药数值、服务端行为与命中表现的完整注册定义。
public record BulletDefinition(
        float damage,
        float velocity,
        float velocityMultiplier,
        float knockback,
        int penetrate,
        ModRarity rarity,
        boolean infinity,
        BulletBehavior behavior,
        BulletImpactEffect impactEffect
) {
    public BulletDefinition {
        if (!Float.isFinite(damage) || damage < 0) {
            throw new IllegalArgumentException("damage must be finite and non-negative");
        }
        if (!Float.isFinite(velocity) || velocity < 0) {
            throw new IllegalArgumentException("velocity must be finite and non-negative");
        }
        if (!Float.isFinite(velocityMultiplier) || velocityMultiplier < 0) {
            throw new IllegalArgumentException("velocityMultiplier must be finite and non-negative");
        }
        if (!Float.isFinite(knockback) || knockback < 0) {
            throw new IllegalArgumentException("knockback must be finite and non-negative");
        }
        if (penetrate < -1) throw new IllegalArgumentException("penetrate must be -1 or greater");
        if (rarity == null || behavior == null || impactEffect == null) {
            throw new IllegalArgumentException("rarity, behavior and impact effect are required");
        }
    }

    /// 兼容旧的普通弹药定义写法。
    public BulletDefinition(float damage, float velocity, float velocityMultiplier, float knockback,
                            int penetrate, ModRarity rarity, boolean infinity) {
        this(damage, velocity, velocityMultiplier, knockback, penetrate, rarity, infinity,
                BulletBehavior.NORMAL, BulletImpactEffect.NONE);
    }

    /// 兼容 1.21 侧新增的“只指定行为，不指定命中特效”写法。
    public BulletDefinition(float damage, float velocity, float velocityMultiplier, float knockback,
                            int penetrate, ModRarity rarity, boolean infinity, BulletBehavior behavior) {
        this(damage, velocity, velocityMultiplier, knockback, penetrate, rarity, infinity,
                behavior, BulletImpactEffect.NONE);
    }

    public BulletDefinition withBehavior(BulletBehavior behavior) {
        return new BulletDefinition(damage, velocity, velocityMultiplier, knockback, penetrate, rarity,
                infinity, behavior, impactEffect);
    }

    public BulletDefinition withImpactEffect(BulletImpactEffect impactEffect) {
        return new BulletDefinition(damage, velocity, velocityMultiplier, knockback, penetrate, rarity,
                infinity, behavior, impactEffect);
    }

    public BulletPropertyComponent component() {
        return new BulletPropertyComponent(damage, velocity, velocityMultiplier, knockback, penetrate, rarity, infinity);
    }
}
