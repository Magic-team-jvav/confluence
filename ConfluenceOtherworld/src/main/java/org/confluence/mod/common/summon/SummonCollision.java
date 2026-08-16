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
    private SummonCollision() {
    }

    /// 沿二次贝塞尔曲线采样定向碰撞箱，并按首次命中点到运动起点的距离返回命中目标。
    ///
    /// <p>采样间距不超过碰撞箱最短边的一半，让相邻碰撞箱保留约一半重叠，避免高速移动时目标刚好位于两个游戏刻端点之间而被漏判。
    /// 粗筛只查询扫掠包围盒中的生物，精确阶段再使用分离轴定理。</p>
    public static List<Hit> sweep(Level level, SummonPose previousPreviousPose, SummonPose previousPose,
                                  SummonPose currentPose, AABB localBox, Predicate<LivingEntity> targetFilter) {
        if (level == null || previousPreviousPose == null || previousPose == null || currentPose == null
                || localBox == null || targetFilter == null) {
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
        List<OrientedBox> samples = new ArrayList<>(steps + 1);
        AABB sweepBounds = null;
        Vec3 centerOffset = localBox.getCenter();
        for (int index = 0; index <= steps; index++) {
            float progress = (float) index / steps;
            double remaining = 1.0 - progress;
            Vec3 position = start.scale(remaining * remaining).add(control.scale(2.0 * remaining * progress))
                    .add(end.scale(progress * progress));
            float yaw = Mth.rotLerp(progress, previousPose.yaw(), currentPose.yaw());
            float pitch = Mth.rotLerp(progress, previousPose.pitch(), currentPose.pitch());
            float roll = Mth.rotLerp(progress, previousPose.roll(), currentPose.roll());
            if (centerOffset.lengthSqr() > 1.0E-5) {
                position = position.add(centerOffset.xRot((float) Math.toRadians(-pitch))
                        .yRot((float) Math.toRadians(-yaw)));
            }
            OrientedBox sample = new OrientedBox(position, size, yaw, pitch, roll);
            samples.add(sample);
            sweepBounds = sweepBounds == null ? sample.bounds() : sweepBounds.minmax(sample.bounds());
        }
        if (sweepBounds == null) {
            return List.of();
        }
        Map<LivingEntity, Vec3> hitPoints = new HashMap<>();
        for (Entity rawTarget : level.getEntities((Entity) null, sweepBounds, candidate -> {
            Entity impacted = ProjectileHitRules.impactedEntity(candidate);
            return impacted instanceof LivingEntity living && targetFilter.test(living);
        })) {
            Entity impacted = ProjectileHitRules.impactedEntity(rawTarget);
            if (!(impacted instanceof LivingEntity candidate)) {
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
                Vec3 hitPoint = new Vec3(Mth.clamp(center.x, targetBox.minX, targetBox.maxX),
                        Mth.clamp(center.y, targetBox.minY, targetBox.maxY),
                        Mth.clamp(center.z, targetBox.minZ, targetBox.maxZ));
                double distance = hitPoint.distanceToSqr(start);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestHit = hitPoint;
                }
            }
            if (closestHit != null) {
                hitPoints.put(candidate, closestHit);
            }
        }
        return hitPoints.entrySet().stream().sorted(Comparator.comparingDouble(entry ->
                entry.getValue().distanceToSqr(start))).map(entry -> new Hit(entry.getKey(), entry.getValue())).toList();
    }

    /// 一次连续碰撞命中的目标与近似命中位置。
    public record Hit(LivingEntity target, Vec3 position) {
    }

    /// 仅在连续碰撞实现内部使用的定向碰撞箱。
    private static final class OrientedBox {
        private static final float EPSILON = 1.0E-5F;
        private final Vector3f center;
        private final Vector3f extents;
        private final Vector3f[] axes;

        private OrientedBox(Vec3 center, Vec3 size, float yaw, float pitch, float roll) {
            this.center = new Vector3f((float) center.x, (float) center.y, (float) center.z);
            this.extents = new Vector3f((float) size.x * 0.5F, (float) size.y * 0.5F, (float) size.z * 0.5F);
            Quaternionf rotation = new Quaternionf().rotateY((float) Math.toRadians(-yaw))
                    .rotateX((float) Math.toRadians(pitch)).rotateZ((float) Math.toRadians(roll));
            this.axes = new Vector3f[]{new Vector3f(1.0F, 0.0F, 0.0F).rotate(rotation),
                    new Vector3f(0.0F, 1.0F, 0.0F).rotate(rotation),
                    new Vector3f(0.0F, 0.0F, 1.0F).rotate(rotation)};
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
            Vector3f boxCenter = new Vector3f((float) box.getCenter().x, (float) box.getCenter().y, (float) box.getCenter().z);
            Vector3f boxExtents = new Vector3f((float) box.getXsize() * 0.5F, (float) box.getYsize() * 0.5F,
                    (float) box.getZsize() * 0.5F);
            Vector3f offset = new Vector3f(center).sub(boxCenter);
            float[][] rotation = new float[3][3];
            float[][] absolute = new float[3][3];
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
                    rotation[row][column] = axisComponent(axes[column], row);
                    absolute[row][column] = Math.abs(rotation[row][column]) + EPSILON;
                }
            }
            float[] translation = {offset.x, offset.y, offset.z};
            float[] a = {boxExtents.x, boxExtents.y, boxExtents.z};
            float[] b = {extents.x, extents.y, extents.z};
            for (int row = 0; row < 3; row++) {
                if (Math.abs(translation[row]) > a[row] + b[0] * absolute[row][0]
                        + b[1] * absolute[row][1] + b[2] * absolute[row][2]) {
                    return false;
                }
            }
            for (int column = 0; column < 3; column++) {
                float projected = Math.abs(translation[0] * rotation[0][column]
                        + translation[1] * rotation[1][column] + translation[2] * rotation[2][column]);
                if (projected > a[0] * absolute[0][column] + a[1] * absolute[1][column]
                        + a[2] * absolute[2][column] + b[column]) {
                    return false;
                }
            }
            for (int aAxis = 0; aAxis < 3; aAxis++) {
                int nextA = (aAxis + 1) % 3;
                int lastA = (aAxis + 2) % 3;
                for (int bAxis = 0; bAxis < 3; bAxis++) {
                    int nextB = (bAxis + 1) % 3;
                    int lastB = (bAxis + 2) % 3;
                    float projected = Math.abs(translation[lastA] * rotation[nextA][bAxis]
                            - translation[nextA] * rotation[lastA][bAxis]);
                    float radiusA = a[nextA] * absolute[lastA][bAxis]
                            + a[lastA] * absolute[nextA][bAxis];
                    float radiusB = b[nextB] * absolute[aAxis][lastB]
                            + b[lastB] * absolute[aAxis][nextB];
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
    }
}
