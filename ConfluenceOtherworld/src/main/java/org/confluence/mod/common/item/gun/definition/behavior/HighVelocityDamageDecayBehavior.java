package org.confluence.mod.common.item.gun.definition.behavior;

import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;

public final class HighVelocityDamageDecayBehavior extends AbstractBulletBehavior {
    public static final HighVelocityDamageDecayBehavior INSTANCE = new HighVelocityDamageDecayBehavior();

    private HighVelocityDamageDecayBehavior() {
        super("tooltip.confluence.ability.high_velocity_damage_decay");
    }

    @Override
    public void onHitEntity(BaseBulletEntity entity, EntityHitResult result) {
        float nextDamage = entity.getDamage() * 0.85F;
        entity.setDamage(nextDamage < 0.01F ? 0.0F : nextDamage);
    }
}
