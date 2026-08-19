package org.confluence.mod.common.item.gun.definition.behavior;

import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.init.ModEffects;

public final class IchorDebuffBehavior extends AbstractBulletBehavior {
    public static final IchorDebuffBehavior INSTANCE = new IchorDebuffBehavior();

    private IchorDebuffBehavior() {
        super("tooltip.confluence.ability.ichor_debuff");
    }

    @Override
    public void onHitEntity(BaseBulletEntity entity, EntityHitResult result) {
        BulletBehaviorSupport.applyEffect(result.getEntity(), ModEffects.ICHOR.get(), 240, 0);
    }
}
