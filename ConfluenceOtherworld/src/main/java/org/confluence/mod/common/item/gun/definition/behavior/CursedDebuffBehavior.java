package org.confluence.mod.common.item.gun.definition.behavior;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;

public final class CursedDebuffBehavior extends AbstractBulletBehavior {
    public static final CursedDebuffBehavior INSTANCE = new CursedDebuffBehavior();

    private CursedDebuffBehavior() {
        super("tooltip.confluence.ability.cursed_debuff");
    }

    @Override
    public void onHitEntity(BaseBulletEntity entity, EntityHitResult result) {
        BulletBehaviorSupport.applyEffect(result.getEntity(), MobEffects.WITHER, 120, 0);
    }
}
