package org.confluence.mod.common.item.yoyo;

import org.confluence.terra_curio.common.entity.BeeProjectile;

public final class YoyoHitEffects {
    public static final YoyoHitEffect IGNITE = (yoyo, owner, target) -> target.setRemainingFireTicks(100);
    public static final YoyoHitEffect SPAWN_BEE = (yoyo, owner, target) -> {
        if (owner.getRandom().nextFloat() >= 0.33F) return;
        BeeProjectile bee = new BeeProjectile(owner.level(), owner, false);
        bee.setBaseDamage(yoyo.getDamage() * 0.5F);
        bee.setPos(target.position().add(target.getRandom().nextFloat() * 0.2F, target.getEyeHeight() * 0.5F, target.getRandom().nextFloat() * 0.2F));
        owner.level().addFreshEntity(bee);
    };

    private YoyoHitEffects() {}
}
