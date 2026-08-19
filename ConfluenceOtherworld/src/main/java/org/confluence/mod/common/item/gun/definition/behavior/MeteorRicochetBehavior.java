package org.confluence.mod.common.item.gun.definition.behavior;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;

public final class MeteorRicochetBehavior extends AbstractBulletBehavior {
    public static final MeteorRicochetBehavior INSTANCE = new MeteorRicochetBehavior();

    private MeteorRicochetBehavior() {
        super("tooltip.confluence.ability.meteor_ricochet");
    }

    @Override
    public boolean onHitBlock(BaseBulletEntity entity, BlockHitResult result) {
        if (entity.getEffectState() >= 1) return false;
        Vec3 velocity = entity.getDeltaMovement();
        Direction direction = result.getDirection();
        Vec3 normal = new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
        double dot = velocity.dot(normal);
        if (dot >= -1.0E-6D) return false;
        Vec3 reflected = velocity.subtract(normal.scale(2.0D * dot));
        if (reflected.lengthSqr() < 1.0E-5D) return false;
        entity.setEffectState(1);
        entity.setDeltaMovement(reflected);
        BulletBehaviorSupport.moveOutsideBlock(entity, result);
        return true;
    }

    @Override
    public void onHitEntity(BaseBulletEntity entity, EntityHitResult result) {
        if (entity.getEffectState() == 1) {
            entity.setPenetrate(1);
        } else if (entity.getEffectState() == 0) {
            entity.setEffectState(2);
            int penetrate = entity.getPenetrate();
            entity.setPenetrate(penetrate < 0 ? 2 : Math.min(penetrate, 2));
        }
    }
}
