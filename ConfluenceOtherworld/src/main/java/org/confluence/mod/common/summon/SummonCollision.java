package org.confluence.mod.common.summon;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Predicate;

/// 为高速移动的召唤物计算两个游戏刻之间的连续定向碰撞。
public final class SummonCollision {
    private SummonCollision() {}

    /// 沿二次贝塞尔曲线采样定向碰撞箱，并按首次命中点到运动起点的距离返回命中目标。
    public static List<Hit> sweep(Level level, SummonPose previousPreviousPose, SummonPose previousPose, SummonPose currentPose, AABB localBox, Predicate<LivingEntity> targetFilter) {
        if (level == null || previousPreviousPose == null || previousPose == null || currentPose == null || localBox == null || targetFilter == null) {
            throw new IllegalArgumentException("Summon collision arguments must not be null");
        }
        Vec3 size = new Vec3(localBox.getXsize(), localBox.getYsize(), localBox.getZsize());
        double shortestSide = Math.min(size.x, Math.min(size.y, size.z));
        if (shortestSide <= 0.0) {
            throw new IllegalArgumentException("Summon collision box must have positive dimensions");
        }
        Vec3 start = previousPose.position();
        Vec3 end = currentPose.position();
        Vec3 control = start.add(start.subtract(previousPreviousPose.position()).scale(0.5));
        int steps = Math.max(2, (int) Math.ceil(start.distanceTo(end) / (shortestSide * 0.5)));
        OrientedBox[] samples = new OrientedBox[steps + 1];
        AABB sweepBounds = null;
        Vec3 centerOffset = localBox.getCenter();
        for (int index = 0; index <= steps; index++) {
            float progress = (float) index / steps;
            double remaining = 1.0 - progress;
            Vec3 position = start.scale(remaining * remaining).add(control.scale(2.0 * remaining * progress)).add(end.scale(progress * progress));
            float yaw = Mth.rotLerp(progress, previousPose.yaw(), currentPose.yaw());
            float pitch = Mth.rotLerp(progress, previousPose.pitch(), currentPose.pitch());
            float roll = Mth.rotLerp(progress, previousPose.roll(), currentPose.roll());
            if (centerOffset.lengthSqr() > 1.0E-5) {
                position = position.add(centerOffset.xRot((float) Math.toRadians(-pitch)).yRot((float) Math.toRadians(-yaw)));
            }
            OrientedBox sample = new OrientedBox(position, size, yaw, pitch, roll);
            samples[index] = sample;
            sweepBounds = sweepBounds == null ? sample.bounds() : sweepBounds.minmax(sample.bounds());
        }
        if (sweepBounds == null) {
            return List.of();
        }
        Map<Entity, Hit> hitPoints = new HashMap<>();
        for (Entity rawTarget : level.getEntities((Entity) null, sweepBounds, candidate -> {
            LivingEntity logicalTarget = ProjectileHitRules.logicalLivingTarget(candidate);
            return logicalTarget != null && targetFilter.test(logicalTarget);
        })) {
            Entity impacted = ProjectileHitRules.impactedEntity(rawTarget);
            LivingEntity logicalTarget = ProjectileHitRules.logicalLivingTarget(rawTarget);
            if (logicalTarget == null) {
                continue;
            }
            Vec3 closestHit = null;
            double closestDistance = Double.MAX_VALUE;
            for (OrientedBox sample : samples) {
                if (!sample.intersects(rawTarget.getBoundingBox())) {
                    continue;
                }
                Vec3 center = sample.center();
                AABB targetBox = rawTarget.getBoundingBox();
                Vec3 hitPoint = new Vec3(Mth.clamp(center.x, targetBox.minX, targetBox.maxX), Mth.clamp(center.y, targetBox.minY, targetBox.maxY), Mth.clamp(center.z, targetBox.minZ, targetBox.maxZ));
                double distance = hitPoint.distanceToSqr(start);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestHit = hitPoint;
                }
            }
            if (closestHit != null) {
                Hit previousHit = hitPoints.get(impacted);
                if (previousHit == null || closestHit.distanceToSqr(start) < previousHit.position.distanceToSqr(start)) {
                    hitPoints.put(impacted, new Hit(impacted, logicalTarget, closestHit));
                }
            }
        }
        List<Hit> hits = new ArrayList<>(hitPoints.size());
        hits.addAll(hitPoints.values());
        hits.sort(Comparator.comparingDouble(hit -> hit.position.distanceToSqr(start)));
        return hits;
    }

    /// 一次连续碰撞命中的目标与近似命中位置。
    public record Hit(Entity target, LivingEntity logicalTarget, Vec3 position) {}

    /// 仅在连续碰撞实现内部使用的定向碰撞箱。
    private static final class OrientedBox {
        private static final float EPSILON = 1.0E-5F;
        private final Vector3f center;
        private final Vector3f extents;
        private final Vector3f[] axes;
        private final float[][] rotation = new float[3][3];
        private final float[][] absolute = new float[3][3];

        private OrientedBox(Vec3 center, Vec3 size, float yaw, float pitch, float roll) {
            this.center = new Vector3f((float) center.x, (float) center.y, (float) center.z);
            this.extents = new Vector3f((float) size.x * 0.5F, (float) size.y * 0.5F, (float) size.z * 0.5F);
            Quaternionf rotation = new Quaternionf().rotateY((float) Math.toRadians(-yaw))
                    .rotateX((float) Math.toRadians(pitch)).rotateZ((float) Math.toRadians(roll));
            this.axes = new Vector3f[]{new Vector3f(1.0F, 0.0F, 0.0F).rotate(rotation),
                    new Vector3f(0.0F, 1.0F, 0.0F).rotate(rotation),
                    new Vector3f(0.0F, 0.0F, 1.0F).rotate(rotation)};
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
                    this.rotation[row][column] = axisComponent(axes[column], row);
                    this.absolute[row][column] = Math.abs(this.rotation[row][column]) + EPSILON;
                }
            }
        }

        private Vec3 center() {
            return new Vec3(center.x, center.y, center.z);
        }

        private AABB bounds() {
            float x = Math.abs(axes[0].x * extents.x) + Math.abs(axes[1].x * extents.y) + Math.abs(axes[2].x * extents.z);
            float y = Math.abs(axes[0].y * extents.x) + Math.abs(axes[1].y * extents.y) + Math.abs(axes[2].y * extents.z);
            float z = Math.abs(axes[0].z * extents.x) + Math.abs(axes[1].z * extents.y) + Math.abs(axes[2].z * extents.z);
            return new AABB(center.x - x, center.y - y, center.z - z, center.x + x, center.y + y, center.z + z);
        }

        private boolean intersects(AABB box) {
            float translationX = center.x - (float) ((box.minX + box.maxX) * 0.5);
            float translationY = center.y - (float) ((box.minY + box.maxY) * 0.5);
            float translationZ = center.z - (float) ((box.minZ + box.maxZ) * 0.5);
            float boxExtentX = (float) box.getXsize() * 0.5F;
            float boxExtentY = (float) box.getYsize() * 0.5F;
            float boxExtentZ = (float) box.getZsize() * 0.5F;
            for (int row = 0; row < 3; row++) {
                float translation = component(translationX, translationY, translationZ, row);
                float boxExtent = component(boxExtentX, boxExtentY, boxExtentZ, row);
                if (Math.abs(translation) > boxExtent + extents.x * absolute[row][0]
                        + extents.y * absolute[row][1] + extents.z * absolute[row][2]) {
                    return false;
                }
            }
            for (int column = 0; column < 3; column++) {
                float projected = Math.abs(translationX * rotation[0][column] + translationY * rotation[1][column]
                        + translationZ * rotation[2][column]);
                float extent = component(extents.x, extents.y, extents.z, column);
                if (projected > boxExtentX * absolute[0][column] + boxExtentY * absolute[1][column]
                        + boxExtentZ * absolute[2][column] + extent) {
                    return false;
                }
            }
            for (int aAxis = 0; aAxis < 3; aAxis++) {
                int nextA = (aAxis + 1) % 3;
                int lastA = (aAxis + 2) % 3;
                for (int bAxis = 0; bAxis < 3; bAxis++) {
                    int nextB = (bAxis + 1) % 3;
                    int lastB = (bAxis + 2) % 3;
                    float projected = Math.abs(component(translationX, translationY, translationZ, lastA) * rotation[nextA][bAxis]
                            - component(translationX, translationY, translationZ, nextA) * rotation[lastA][bAxis]);
                    float radiusA = component(boxExtentX, boxExtentY, boxExtentZ, nextA) * absolute[lastA][bAxis]
                            + component(boxExtentX, boxExtentY, boxExtentZ, lastA) * absolute[nextA][bAxis];
                    float radiusB = component(extents.x, extents.y, extents.z, nextB) * absolute[aAxis][lastB]
                            + component(extents.x, extents.y, extents.z, lastB) * absolute[aAxis][nextB];
                    if (projected > radiusA + radiusB) {
                        return false;
                    }
                }
            }
            return true;
        }

        private static float axisComponent(Vector3f axis, int component) {
            return component == 0 ? axis.x : component == 1 ? axis.y : axis.z;
        }

        private static float component(float x, float y, float z, int component) {
            return component == 0 ? x : component == 1 ? y : z;
        }
    }
}
