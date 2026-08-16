package org.confluence.mod.common.entity.flail;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.projectile.flail.FlaironBubbleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;

/// 猪鲨链球实体，按当前状态发射可追踪气泡。
public final class FlaironFlailEntity extends BaseFlailEntity {
    private static final double CONE_HALF_ANGLE =
            Math.toRadians(30.0);
    private int shootTimer;

    public FlaironFlailEntity(
            EntityType<? extends FlaironFlailEntity> type,
            Level level
    ) {
        super(type, level);
    }

    @Override
    public boolean usesSpriteHead() {
        return true;
    }

    @Override
    protected void tickSpecialBehavior(
            Player player,
            FlailComponent component,
            int phase
    ) {
        if (level().isClientSide()) {
            return;
        }
        boolean shouldShoot = phase == PHASE_STAY
                ? shootTimer % 2 == 0
                : shootTimer % 4 != 2;
        shootTimer++;
        if (!shouldShoot) {
            return;
        }

        Vec3 facing = movementDirection(player);
        if (phase == PHASE_RETRACT) {
            facing = facing.scale(-1.0);
        }
        Vec3 direction = randomInCone(facing);
        double speed = 0.1 + random.nextDouble() * 0.15;

        FlaironBubbleProjectile bubble =
                ModEntities.FLAIRON_BUBBLE.get().create(level());
        if (bubble == null) {
            return;
        }
        bubble.initialize(
                this,
                player,
                direction.scale(speed),
                component.damageFactor()
                        * (float) player.getAttributeValue(
                        LibAttributes.getAttackDamage())
                        * 0.5F,
                40);
        bubble.randomizeScale();
        bubble.setPos(position().add(0.0, getBbHeight() * 0.5, 0.0));
        level().addFreshEntity(bubble);
    }

    private Vec3 movementDirection(Player player) {
        Vec3 movement = new Vec3(
                getX() - xo,
                getY() - yo,
                getZ() - zo);
        return movement.lengthSqr() > 1.0E-6
                ? movement.normalize()
                : player.getViewVector(1.0F);
    }

    private Vec3 randomInCone(Vec3 axis) {
        double theta = random.nextDouble() * CONE_HALF_ANGLE;
        double phi = random.nextDouble() * Math.PI * 2.0;
        double sinTheta = Math.sin(theta);
        Vec3 perpendicular = Math.abs(axis.x) < 0.9
                ? new Vec3(1.0, 0.0, 0.0)
                : new Vec3(0.0, 1.0, 0.0);
        Vec3 right = axis.cross(perpendicular).normalize();
        Vec3 up = axis.cross(right);
        return axis.scale(Math.cos(theta))
                .add(right.scale(sinTheta * Math.cos(phi)))
                .add(up.scale(sinTheta * Math.sin(phi)))
                .normalize();
    }
}
