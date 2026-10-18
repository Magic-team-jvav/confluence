package org.confluence.mod.common.item.gun.definition.behavior;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils.TargetPolicy;
import org.confluence.lib.util.LibEntityUtils.WeaponTargeting;
import org.confluence.mod.common.entity.*;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;

public final class NanoRicochetBehavior extends AbstractBulletBehavior {
    public static final NanoRicochetBehavior INSTANCE = new NanoRicochetBehavior();

    private NanoRicochetBehavior() {
        super("tooltip.confluence.ability.nano_ricochet", new WeaponTargeting(TargetPolicy.ENEMY_ONLY));
    }

    @Override
    public boolean onHitBlock(BaseBulletEntity entity, BlockHitResult result) {
        if (entity.getEffectState() >= 1) return false;
        Entity target = BulletBehaviorSupport.findNearestTargetInRange(entity, 32.0D);
        if (target == null && entity.level().isClientSide) target = entity.getHomingTarget();
        if (!BulletBehaviorSupport.canAcquireTarget(entity, target) || BulletBehaviorSupport.homingDistance(entity, target) > 32.0D)
            return false;
        Vec3 targetOffset = target.getBoundingBox().getCenter().subtract(entity.position());
        if (targetOffset.lengthSqr() <= 1.0E-7D) return false;
        double speed = Math.max(0.25D, entity.getDeltaMovement().length());
        entity.setEffectState(1);
        entity.setDamage(entity.getDamage() * 0.66F);
        entity.setHomingTarget(target);
        entity.setDeltaMovement(targetOffset.normalize().scale(speed));
        BulletBehaviorSupport.moveOutsideBlock(entity, result);
        return true;
    }
}
