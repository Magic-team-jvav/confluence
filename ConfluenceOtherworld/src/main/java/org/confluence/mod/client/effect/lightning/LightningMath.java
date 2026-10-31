// Adapted from minecraft-lightning skill. See META-INF/arc-surge-lightning-NOTICES.md.
package org.confluence.mod.client.effect.lightning;

import org.confluence.mod.Confluence;

import java.util.SplittableRandom;

/** Small deterministic helpers shared by the two CPU effect generators. */
final class LightningMath {
    private LightningMath() {}
    static double clamp(double v, double lo, double hi) { return Math.max(lo, Math.min(hi, v)); }
    static double smooth(double x) { x = clamp(x, 0, 1); return x * x * (3 - 2 * x); }
    static double signed(long seed, long salt) {
        long z = seed + salt * 0x9E3779B97F4A7C15L; z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L; z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL; z ^= z >>> 31;
        return ((z >>> 11) * (1.0 / (1L << 53))) * 2 - 1;
    }
    static double unit(long seed, long salt) { return (signed(seed, salt) + 1) * .5; }
    static double noise(long seed, double x) {
        double cell = Math.floor(x), t = x - cell;
        double smoothT = t * t * (3 - 2 * t);
        long c = (long) cell;
        return signed(seed, c) * (1 - smoothT) + signed(seed, c + 1) * smoothT;
    }
    static double ringNoise(long seed, double theta, int harmonics) {
        double sum = 0, amp = 1, norm = 0;
        for (int h = 1; h <= Math.max(1, harmonics); h++) { double phase = (signed(seed, h) + 1) * Math.PI; sum += Math.sin(theta * h + phase) * amp; norm += amp; amp *= .55; }
        return sum / norm;
    }
    static double noise2(long seed, double x, double y) {
        long row = (long)Math.floor(y); double t = smooth(y - row);
        return noise(seed + row * 1013, x) * (1 - t) + noise(seed + (row + 1) * 1013, x) * t;
    }
    static double gaussian(SplittableRandom r) {
        double u = Math.max(1e-12, r.nextDouble());
        return Math.sqrt(-2 * Math.log(u)) * Math.cos(2 * Math.PI * r.nextDouble()) * .42;
    }
}
