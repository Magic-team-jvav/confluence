package org.confluence.mod.common.summoner.util;

import net.minecraft.world.phys.Vec3;

/** Shared angular/distance scoring used by Zenith and the red glove. */
public final class ZenithTargeting {
    private ZenithTargeting() {}
    public static double score(Vec3 origin, Vec3 look, Vec3 target, double range, double halfAngle) {
        Vec3 delta = target.subtract(origin);
        double distance = delta.length();
        if (distance < 1.0E-8) return 0;
        double cosine = Math.max(-1, Math.min(1, delta.dot(look.normalize()) / distance));
        double angle = Math.toDegrees(Math.acos(cosine));
        return angle <= halfAngle ? 0.8 * angle / halfAngle + 0.2 * distance / range : Double.POSITIVE_INFINITY;
    }
}
