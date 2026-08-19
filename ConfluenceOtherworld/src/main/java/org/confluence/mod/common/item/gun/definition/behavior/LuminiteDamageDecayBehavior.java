package org.confluence.mod.common.item.gun.definition.behavior;

import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;

public final class LuminiteDamageDecayBehavior extends AbstractBulletBehavior {
    public static final LuminiteDamageDecayBehavior INSTANCE = new LuminiteDamageDecayBehavior();

    private LuminiteDamageDecayBehavior() {
        super("tooltip.confluence.ability.luminite_damage_decay");
    }

    @Override
    public void onHitEntity(BaseBulletEntity entity, EntityHitResult result) {
        float nextDamage = entity.getDamage() * 0.96F;
        entity.setDamage(nextDamage < 0.01F ? 0.0F : nextDamage);
    }
}
