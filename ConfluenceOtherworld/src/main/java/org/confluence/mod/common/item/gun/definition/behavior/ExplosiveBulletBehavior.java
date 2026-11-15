package org.confluence.mod.common.item.gun.definition.behavior;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;

public final class ExplosiveBulletBehavior extends AbstractBulletBehavior {
    public static final ExplosiveBulletBehavior INSTANCE = new ExplosiveBulletBehavior();

    private ExplosiveBulletBehavior() {
        super("tooltip.confluence.ability.explosive");
    }

    @Override
    public boolean onHitBlock(BaseBulletEntity entity, BlockHitResult result) {
        BulletBehaviorSupport.explode(entity);
        return false;
    }

    @Override
    public void onHitEntity(BaseBulletEntity entity, EntityHitResult result) {
        BulletBehaviorSupport.explode(entity);
    }
}
