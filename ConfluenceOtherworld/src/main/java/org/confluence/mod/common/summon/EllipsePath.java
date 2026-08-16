package org.confluence.mod.common.summon;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/// 根据长轴端点、平面法线和短轴比例计算椭圆轨迹。
public final class EllipsePath {
    private final Vec3 center;
    private final Vec3 major;
    private final Vec3 minor;
    private final Vec3 planeNormal;

    public EllipsePath(Vec3 pointA, Vec3 pointB, Vec3 planeNormal, float curvature) {
        if (!Float.isFinite(curvature) || curvature <= 0.0F) {
            throw new IllegalArgumentException("Ellipse curvature must be finite and positive");
        }
        this.major = pointB.subtract(pointA).scale(0.5);
        if (major.lengthSqr() < 1.0E-6) {
            throw new IllegalArgumentException("Ellipse endpoints must not overlap");
        }
        this.center = pointA.add(major);
        this.planeNormal = planeNormal.normalize();
        Vec3 minorDirection = this.planeNormal.cross(major.normalize()).normalize();
        if (minorDirection.lengthSqr() < 1.0E-6) {
            minorDirection = major.normalize().cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        }
        if (minorDirection.lengthSqr() < 1.0E-6) {
            minorDirection = major.normalize().cross(new Vec3(1.0, 0.0, 0.0)).normalize();
        }
        this.minor = minorDirection.scale(major.length() * curvature);
    }

    public static Vec3 randomPlaneNormal(RandomSource random, Vec3 pointA, Vec3 pointB) {
        Vec3 majorDirection = pointB.subtract(pointA).normalize();
        if (majorDirection.lengthSqr() < 1.0E-6) {
            return new Vec3(0.0, 1.0, 0.0);
        }
        Vec3 initial = majorDirection.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        if (initial.lengthSqr() < 1.0E-6) {
            initial = majorDirection.cross(new Vec3(1.0, 0.0, 0.0)).normalize();
        }
        Quaternionf rotation = new Quaternionf().rotateAxis(random.nextFloat() * Mth.TWO_PI,
                (float) majorDirection.x, (float) majorDirection.y, (float) majorDirection.z);
        Vector3f rotated = new Vector3f((float) initial.x, (float) initial.y, (float) initial.z).rotate(rotation);
        return new Vec3(rotated.x(), rotated.y(), rotated.z()).normalize();
    }

    public Vec3 point(float progress) {
        float adjusted = progress - 0.08F * Mth.sin(progress * Mth.TWO_PI);
        float angle = adjusted * Mth.TWO_PI;
        return center.add(major.scale(Math.cos(angle))).add(minor.scale(Math.sin(angle)));
    }

    public Vec3 center() {
        return center;
    }

    public Vec3 planeNormal() {
        return planeNormal;
    }
}
