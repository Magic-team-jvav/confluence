package org.confluence.mod.common.item.gun.definition.behavior;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;

public final class CrystalSplitBehavior extends AbstractBulletBehavior {
    public static final CrystalSplitBehavior INSTANCE = new CrystalSplitBehavior();

    private CrystalSplitBehavior() {
        super("tooltip.confluence.ability.crystal_split");
    }

    @Override
    public boolean onHitBlock(BaseBulletEntity entity, BlockHitResult result) {
        Direction direction = result.getDirection();
        split(entity, new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ()).scale(0.12D));
        return false;
    }

    @Override
    public void onHitEntity(BaseBulletEntity entity, EntityHitResult result) {
        Vec3 velocity = entity.getDeltaMovement();
        split(entity, velocity.lengthSqr() > 1.0E-5D ? velocity.normalize().scale(-0.12D) : Vec3.ZERO);
    }

    private static void split(BaseBulletEntity entity, Vec3 spawnOffset) {
        if (entity.level().isClientSide || entity.getEffectState() > 0) return;
        Vec3 velocity = entity.getDeltaMovement();
        if (velocity.lengthSqr() < 1.0E-5D) velocity = entity.getLookAngle();
        if (velocity.lengthSqr() < 1.0E-5D) return;
        double shardSpeed = Math.min(6.0D, Math.max(0.35D, velocity.length() * 0.35D));
        Vec3 direction = velocity.normalize().scale(-1.0D);
        for (int index = -1; index <= 1; index += 2) {
            double angle = index * 0.32D;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            Vec3 shardDirection = new Vec3(direction.x * cos + direction.z * sin, direction.y, direction.z * cos - direction.x * sin).normalize();
            BaseBulletEntity shard = entity.createChild(shardDirection.scale(shardSpeed), 0.5F, 1, spawnOffset);
            shard.setIgnoresBlockCollision(true);
            entity.level().addFreshEntity(shard);
        }
    }
}
