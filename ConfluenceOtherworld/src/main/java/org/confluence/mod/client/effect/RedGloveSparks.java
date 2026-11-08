package org.confluence.mod.client.effect;

import net.minecraft.world.phys.Vec3;
import java.util.SplittableRandom;

/** Straight particle rays sampled uniformly in solid angle around the outgoing current tangent. */
public final class RedGloveSparks {
    public static final int PER_CAST = 12, PER_TICK = 24, CAPACITY = 128;
    private static final double COS_MIN = Math.cos(Math.toRadians(45));
    private static final double COS_MAX = Math.cos(Math.toRadians(10));

    private RedGloveSparks() {}

    public static Vec3 velocity(Vec3 tangent, SplittableRandom random) {
        Vec3 axis = tangent.lengthSqr() < 1.0E-12 ? new Vec3(0, 0, 1) : tangent.normalize();
        Vec3 side = axis.cross(Math.abs(axis.y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0)).normalize();
        Vec3 up = side.cross(axis);
        double cos = random.nextDouble(COS_MIN, COS_MAX);
        double sin = Math.sqrt(1 - cos * cos), azimuth = random.nextDouble(0, Math.PI * 2);
        return axis.scale(cos).add(side.scale(sin * Math.cos(azimuth)))
                .add(up.scale(sin * Math.sin(azimuth))).scale(random.nextDouble(0.3, 0.6));
    }

    public static int color(SplittableRandom random) {
        double brightness = random.nextDouble(0.8, 1);
        return ((int) Math.round(255 * brightness) << 16)
                | ((int) Math.round(135 * brightness) << 8) | (int) Math.round(145 * brightness);
    }
}
