package org.confluence.mod.common.effect.harmful;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/// 保存并推进飞行生物的持续驱离状态。
public final class DriveAwayController {
    private static final String DATA_KEY = "confluence:drive_away";
    private static final String CENTER_X = "CenterX";
    private static final String CENTER_Y = "CenterY";
    private static final String CENTER_Z = "CenterZ";
    private static final String SPEED = "Speed";
    private static final String DURATION = "Duration";
    private static final String ELAPSED = "Elapsed";

    private DriveAwayController() {}

    public static void start(Mob mob, Vec3 center, double speed, int duration) {
        if (speed <= 0.0 || duration <= 0) return;
        CompoundTag state = new CompoundTag();
        state.putDouble(CENTER_X, center.x);
        state.putDouble(CENTER_Y, center.y);
        state.putDouble(CENTER_Z, center.z);
        state.putDouble(SPEED, speed);
        state.putInt(DURATION, duration);
        state.putInt(ELAPSED, 0);
        mob.getPersistentData().put(DATA_KEY, state);
        mob.setTarget(null);
        mob.getNavigation().stop();
    }

    public static void tick(Mob mob) {
        CompoundTag entityData = mob.getPersistentData();
        if (!entityData.contains(DATA_KEY, Tag.TAG_COMPOUND)) return;
        CompoundTag state = entityData.getCompound(DATA_KEY);
        int elapsed = state.getInt(ELAPSED);
        int duration = state.getInt(DURATION);
        double speed = state.getDouble(SPEED);
        if (elapsed >= duration || speed <= 0.0 || !mob.isAlive()) {
            entityData.remove(DATA_KEY);
            return;
        }

        Vec3 position = mob.position();
        Vec3 center = new Vec3(state.getDouble(CENTER_X), state.getDouble(CENTER_Y), state.getDouble(CENTER_Z));
        Vec3 direction = position.subtract(center);
        if (direction.lengthSqr() < 1.0E-6) direction = mob.getLookAngle();
        direction = direction.normalize();
        double remainingDistance = speed * (duration - elapsed);
        Vec3 target = avoidWall(mob, position, position.add(direction.scale(Math.min(remainingDistance, speed * 5.0))), speed);
        if (mob instanceof FlyingMob || mob instanceof FlyingAnimal) {
            mob.getNavigation().moveTo(target.x, target.y, target.z, speed);
        } else {
            Vec3 movement = target.subtract(position).normalize().scale(speed);
            mob.setDeltaMovement(movement);
            double yaw = Math.toDegrees(Math.atan2(movement.z, movement.x)) - 90.0;
            mob.setYRot((float) yaw);
            mob.setYHeadRot((float) yaw);
        }
        state.putInt(ELAPSED, elapsed + 1);
    }

    private static Vec3 avoidWall(Mob mob, Vec3 position, Vec3 target, double speed) {
        BlockHitResult hit = mob.level().clip(new ClipContext(position, target, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, mob));
        if (hit.getType() != BlockHitResult.Type.BLOCK) return target;
        Vec3 direction = target.subtract(position).normalize();
        Vec3 normal = Vec3.atLowerCornerOf(hit.getDirection().getNormal());
        Vec3 slide = direction.subtract(normal.scale(direction.dot(normal)));
        if (slide.lengthSqr() < 1.0E-6) slide = new Vec3(-direction.z, 0.25, direction.x);
        return position.add(slide.normalize().scale(Math.max(1.0, speed * 5.0)));
    }
}
