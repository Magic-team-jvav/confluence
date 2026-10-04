package org.confluence.mod.common.entity.boss;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/// 渲染与接触判定共用的肩、肘、掌骨架，坐标均为世界坐标。
public record SkeletronArmPose(Vec3 shoulder, Vec3 elbow, Vec3 palm, Vec3 reach, Vec3 pole) {
    public static SkeletronArmPose solve(Vec3 shoulder, Vec3 palm, Vec3 ownerPosition, float yaw, float scale) {
        Vec3 front = new Vec3(-Mth.sin(yaw * Mth.DEG_TO_RAD), 0.0, Mth.cos(yaw * Mth.DEG_TO_RAD));
        Vec3 side = shoulder.subtract(ownerPosition).multiply(1.0, 0.0, 1.0).normalize();
        if (side.lengthSqr() < 1.0E-7) side = new Vec3(front.z, 0.0, -front.x);
        Vec3 displacement = shoulder.subtract(palm);
        double distance = displacement.length();
        Vec3 reach = distance < 1.0E-7 ? front : displacement.scale(1.0 / distance);
        Vec3 preferred = front.scale(-Math.cos(Math.PI / 6.0)).add(side.scale(0.5)).add(0.0, 0.08, 0.0);
        Vec3 pole = preferred.subtract(reach.scale(preferred.dot(reach)));
        if (pole.lengthSqr() < 1.0E-5) pole = side.subtract(reach.scale(side.dot(reach)));
        if (pole.lengthSqr() < 1.0E-7) {
            Vec3 fallback = Math.abs(reach.y) < 0.9 ? new Vec3(0.0, 1.0, 0.0) : new Vec3(0.0, 0.0, 1.0);
            pole = fallback.subtract(reach.scale(fallback.dot(reach)));
        }
        pole = pole.normalize();
        // skeletron 模型的掌、肘、肩枢轴 X 分别为 0、-85、-147 像素。
        double distal = 85.0 / 16.0 * scale;
        double upper = 62.0 / 16.0 * scale;
        double maximum = distal + upper;
        double minimum = distal - upper;
        if (distance >= maximum) {
            distal *= distance / maximum;
            upper *= distance / maximum;
        } else if (distance < minimum) {
            double common = distal * 0.58;
            double blend = distance * distance / (minimum * minimum);
            distal = Mth.lerp(blend, common, distal);
            upper = Mth.lerp(blend, common, upper);
        }
        double along = distance < 1.0E-7 ? 0.0 : (distal * distal + distance * distance - upper * upper) / (2.0 * distance);
        double height = Math.sqrt(Math.max(0.0, distal * distal - along * along));
        Vec3 elbow = palm.add(reach.scale(along)).add(pole.scale(height));
        return new SkeletronArmPose(shoulder, elbow, palm, reach, pole);
    }

    public AABB bounds(double radius) {
        return new AABB(shoulder, palm).minmax(new AABB(elbow, elbow)).inflate(radius);
    }

    public boolean touches(AABB target, double radius) {
        AABB expanded = target.inflate(radius);
        return expanded.contains(palm) || expanded.contains(elbow) || expanded.contains(shoulder)
                || expanded.clip(palm, elbow).isPresent() || expanded.clip(elbow, shoulder).isPresent();
    }

    public boolean sweeps(SkeletronArmPose previous, AABB target, double radius) {
        double distance = Math.max(palm.distanceTo(previous.palm), Math.max(elbow.distanceTo(previous.elbow), shoulder.distanceTo(previous.shoulder)));
        if (distance > 16.0) return touches(target, radius);
        int steps = Math.max(1, Mth.ceil(distance / 0.25));
        for (int step = 0; step <= steps; step++) {
            double progress = step / (double) steps;
            SkeletronArmPose pose = new SkeletronArmPose(previous.shoulder.lerp(shoulder, progress), previous.elbow.lerp(elbow, progress), previous.palm.lerp(palm, progress), reach, pole);
            if (pose.touches(target, radius + distance / steps * 0.5)) return true;
        }
        return false;
    }
}
