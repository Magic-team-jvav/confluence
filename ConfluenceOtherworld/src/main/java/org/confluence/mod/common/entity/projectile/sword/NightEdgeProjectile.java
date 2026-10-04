package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.common.particle.CrossDustParticleOptions;
import org.confluence.lib.util.LibMathUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.ArrayDeque;
import java.util.Deque;

public class NightEdgeProjectile extends SwordProjectile {
    private static final float[] TIMES = {0.0F, 3.0F, 6.0F, 9.0F, 12.0F};
    private static final float[] X = {-1.2F, -1.2F, 1.2F, 1.2F, -1.2F};
    private static final float[] Y = {0.3F, -0.4F, -0.8F, -0.4F, 0.3F};
    private static final float[] Z = {-2.0F, 1.0F, 1.0F, -2.0F, -2.0F};
    private static final float[] X_SLOPES = {-0.5F, 0.5F, 0.5F, -0.5F, 0.0F};
    private static final float[] Z_SLOPES = {1.0F, 1.0F, -1.0F, -1.0F, 0.0F};
    private static final float[] POSITION_TENSIONS = {1.0F, 1.0F, 1.0F, 1.0F, 0.5F};
    private static final float[] YAW = {135.0F, 45.0F, -45.0F, -135.0F, -225.0F};
    private static final float[] ZERO_SLOPES = new float[TIMES.length];
    private static final float[] ROTATION_TENSIONS = {1.0F, 1.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] ROLL = {120.0F, 120.0F, 120.0F, 120.0F, 120.0F};
    private static final BakedCurve X_CURVE = new BakedCurve(X, X_SLOPES, POSITION_TENSIONS);
    private static final BakedCurve Z_CURVE = new BakedCurve(Z, Z_SLOPES, POSITION_TENSIONS);
    private static final BakedCurve YAW_CURVE = new BakedCurve(YAW, ZERO_SLOPES, ROTATION_TENSIONS);
    private static final double HIT_BOX_HALF_WIDTH = 0.875;
    private static final double HIT_BOX_HALF_LENGTH = 5.0;
    private static final double HIT_BOX_FORWARD_OFFSET = 3.75;
    private static final double BROAD_PHASE_INFLATION = 12.0;
    private final Deque<TrailSample> trailSamples = new ArrayDeque<>();

    public NightEdgeProjectile(EntityType<? extends SwordProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);

        survivesBlockHit = true;
        remainingHits = 9999;
    }

    @Override
    public DamageSource damageSource() {
        return LibDamageTypes.of(level(), DamageTypes.MOB_ATTACK, this, getOwner());
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(3.0);
    }

    @Override
    public double getDefaultGravity() {
        return 0;
    }

    @Override
    protected boolean usesDefaultCollisionDamage() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) return;
        Entity owner = getOwner();
        if (!(owner instanceof LivingEntity livingOwner) || !owner.isAlive()) {
            if (!level().isClientSide) discard();
            return;
        }
        Level level = level();
        if (level.isClientSide) recordTrail(livingOwner);
        else hitOrientedBlade();
        if (level.isClientSide && tickCount <= 14 && level.random.nextBoolean())
            emitParticles(level);

        setDeltaMovement(Vec3.ZERO);
        setPos(worldPosition(livingOwner, Math.min(tickCount, maxTrailTime())));
        xRotO = getXRot();
        yRotO = getYRot();
        setXRot(livingOwner.getXRot() * 0.8F);
        setYRot(sampleYaw(tickCount) + livingOwner.getYRot());
    }

    private void emitParticles(Level level) {
        Vec3 pos = position().offsetRandom(level.random, 0.3f);
        Entity particleOwner = getOwner();
        if (particleOwner != null) {
            Vec3 facing = LibMathUtils.rotToDir(particleOwner.getYHeadRot(), particleOwner.getXRot()).scale(0.05);
            Vector4f curve = new Vector4f(0, 1f, 1f, 1);
            boolean dark = level.random.nextBoolean();
            CrossDustParticleOptions lightParticle = new CrossDustParticleOptions(false,
                    dark ? 0xff570EFD : 0xffE4E0FF, dark ? 0xff411EA3 : 0xffC55BFF, facing.toVector3f(), curve,
                    level.random.nextFloat() * 0.15f + 0.15f, 10, 60, curve, true, true, true, false);
            level.addParticle(lightParticle, pos.x, pos.y, pos.z, 0, 0, 0);
        }

        Vector4f curve = new Vector4f(0, 0.7f, 0.9f, 1);
        CrossDustParticleOptions darkParticle = new CrossDustParticleOptions(level.random.nextBoolean(),
                0x66DD99FF, 0x7f714E82, Vec3.ZERO.offsetRandom(level.random, level.random.nextFloat() * 0.005f + 0.01f).toVector3f(),
                curve, level.random.nextFloat() * 0.6f + 0.4f, 30, level.random.nextInt(-40, 40), curve, true, true, false, false);
        level.addParticle(darkParticle, pos.x, pos.y, pos.z, 0, 0, 0);
    }

    private void hitOrientedBlade() {
        Vec3 axisZ = Vec3.directionFromRotation(getXRot(), getYRot()).normalize();
        Vec3 axisY = Vec3.directionFromRotation(getXRot() + 90.0F, getYRot()).reverse().normalize();
        Vec3 axisX = axisZ.cross(axisY).normalize();
        Vec3 center = position().add(axisZ.scale(HIT_BOX_FORWARD_OFFSET));
        AABB searchBox = getBoundingBox().inflate(BROAD_PHASE_INFLATION);
        for (Entity target : level().getEntities(this, searchBox, target -> !(target instanceof Player) && canHitEntity(target))) {
            if (intersects(center, axisX, axisY, axisZ, target.getBoundingBox(), getDeltaMovement(), target.getDeltaMovement())) {
                hurtTarget(target);
            }
        }
    }

    private static boolean intersects(Vec3 center, Vec3 axisX, Vec3 axisY, Vec3 axisZ, AABB box, Vec3 bladeMotion, Vec3 targetMotion) {
        return overlapsOnAxis(center, axisX, axisY, axisZ, box, bladeMotion, targetMotion,
                axisX.x, axisX.y, axisX.z)
                && overlapsOnAxis(center, axisX, axisY, axisZ, box, bladeMotion, targetMotion,
                axisY.x, axisY.y, axisY.z)
                && overlapsOnAxis(center, axisX, axisY, axisZ, box, bladeMotion, targetMotion,
                axisZ.x, axisZ.y, axisZ.z)
                && overlapsOnAxis(center, axisX, axisY, axisZ, box, bladeMotion, targetMotion, 1.0, 0.0, 0.0)
                && overlapsOnAxis(center, axisX, axisY, axisZ, box, bladeMotion, targetMotion, 0.0, 1.0, 0.0)
                && overlapsOnAxis(center, axisX, axisY, axisZ, box, bladeMotion, targetMotion, 0.0, 0.0, 1.0)
                && overlapsCrossAxes(center, axisX, axisY, axisZ, box, bladeMotion, targetMotion, axisX)
                && overlapsCrossAxes(center, axisX, axisY, axisZ, box, bladeMotion, targetMotion, axisY)
                && overlapsCrossAxes(center, axisX, axisY, axisZ, box, bladeMotion, targetMotion, axisZ);
    }

    private static boolean overlapsCrossAxes(Vec3 center, Vec3 axisX, Vec3 axisY, Vec3 axisZ, AABB box, Vec3 bladeMotion, Vec3 targetMotion, Vec3 localAxis) {
        return overlapsOnAxis(center, axisX, axisY, axisZ, box, bladeMotion, targetMotion,
                0.0, localAxis.z, -localAxis.y)
                && overlapsOnAxis(center, axisX, axisY, axisZ, box, bladeMotion, targetMotion,
                -localAxis.z, 0.0, localAxis.x)
                && overlapsOnAxis(center, axisX, axisY, axisZ, box, bladeMotion, targetMotion,
                localAxis.y, -localAxis.x, 0.0);
    }

    private static boolean overlapsOnAxis(Vec3 center, Vec3 axisX, Vec3 axisY, Vec3 axisZ, AABB box, Vec3 bladeMotion, Vec3 targetMotion, double x, double y, double z) {
        if (x * x + y * y + z * z <= 1.0E-12) return true;
        double bladeCenter = center.x * x + center.y * y + center.z * z;
        double bladeRadius = HIT_BOX_HALF_WIDTH * Math.abs(axisX.x * x + axisX.y * y + axisX.z * z)
                + HIT_BOX_HALF_WIDTH * Math.abs(axisY.x * x + axisY.y * y + axisY.z * z)
                + HIT_BOX_HALF_LENGTH * Math.abs(axisZ.x * x + axisZ.y * y + axisZ.z * z);
        double targetCenter = (box.minX + box.maxX) * 0.5 * x + (box.minY + box.maxY) * 0.5 * y + (box.minZ + box.maxZ) * 0.5 * z;
        double targetRadius = box.getXsize() * 0.5 * Math.abs(x) + box.getYsize() * 0.5 * Math.abs(y) + box.getZsize() * 0.5 * Math.abs(z);
        double bladeVelocity = bladeMotion.x * x + bladeMotion.y * y + bladeMotion.z * z;
        double targetVelocity = targetMotion.x * x + targetMotion.y * y + targetMotion.z * z;
        double bladeMin = bladeCenter - bladeRadius + Math.min(0.0, bladeVelocity);
        double bladeMax = bladeCenter + bladeRadius + Math.max(0.0, bladeVelocity);
        double targetMin = targetCenter - targetRadius + Math.min(0.0, targetVelocity);
        double targetMax = targetCenter + targetRadius + Math.max(0.0, targetVelocity);
        return bladeMin <= targetMax && targetMin <= bladeMax;
    }

    private static Vec3 sampleLocalPoint(float time) {
        return new Vec3(X_CURVE.sample(time), interpolate(Y, time), Z_CURVE.sample(time));
    }

    private void recordTrail(LivingEntity owner) {
        if (trailSamples.size() >= 100) trailSamples.pollFirst();
        if (lifetime - tickCount <= 0) {
            trailSamples.pollFirst();
        } else if (tickCount > 1) {
            trailSamples.addLast(new TrailSample(position().subtract(owner.position()), getXRot() * Mth.DEG_TO_RAD, getYRot() * Mth.DEG_TO_RAD));
        }
    }

    public Deque<TrailSample> getTrailSamples() {
        return trailSamples;
    }

    public static Vec3 worldPosition(LivingEntity owner, float time) {
        Vec3 local = sampleLocalPoint(time).xRot(owner.getXRot() * Mth.DEG_TO_RAD).yRot(-owner.getYRot() * Mth.DEG_TO_RAD);
        return handPosition(owner).add(local);
    }

    private static Vec3 handPosition(LivingEntity owner) {
        double yaw = owner.getYRot() * Mth.DEG_TO_RAD + Math.PI * 0.5;
        Vec3 offset = new Vec3(-0.3, owner.getEyeHeight(), owner.getBbWidth() * 0.1F);
        double x = Math.cos(yaw) * offset.z + Math.sin(yaw) * offset.x;
        double z = Math.sin(yaw) * offset.z - Math.cos(yaw) * offset.x;
        return owner.position().add(x, offset.y, z);
    }

    public static float sampleRoll(float time) {
        return interpolate(ROLL, time);
    }

    public static float sampleYaw(float time) {
        return (float) YAW_CURVE.sample(time);
    }

    public static float maxTrailTime() {
        return TIMES[TIMES.length - 1];
    }

    public record TrailSample(Vec3 position, float xRot, float yRot) {}

    private static float interpolate(float[] values, float time) {
        if (time <= TIMES[0]) {
            return values[0];
        }
        for (int index = 1; index < TIMES.length; index++) {
            if (time <= TIMES[index]) {
                float progress = (time - TIMES[index - 1]) / (TIMES[index] - TIMES[index - 1]);
                return Mth.lerp(progress, values[index - 1], values[index]);
            }
        }
        return values[values.length - 1];
    }

    private static double bezier(double start, double control1, double control2, double end, double progress) {
        double inverse = 1.0 - progress;
        return inverse * inverse * inverse * start + 3.0 * inverse * inverse * progress * control1
                + 3.0 * inverse * progress * progress * control2 + progress * progress * progress * end;
    }

    private static final class BakedCurve {
        private final NaturalSpline[] intervals = new NaturalSpline[TIMES.length - 1];

        private BakedCurve(float[] values, float[] slopes, float[] tensions) {
            for (int interval = 0; interval < intervals.length; interval++) {
                double x0 = TIMES[interval];
                double x3 = TIMES[interval + 1];
                double y0 = values[interval];
                double y3 = values[interval + 1];
                double slope0 = slopes[interval];
                double slope3 = slopes[interval + 1];
                double length0 = tensions[interval] / Math.sqrt(slope0 * slope0 + 1.0);
                double length3 = tensions[interval + 1] / Math.sqrt(slope3 * slope3 + 1.0);
                double x1 = x0 + length0;
                double y1 = y0 + length0 * slope0;
                double x2 = x3 - length3;
                double y2 = y3 - length3 * slope3;
                double[] x = new double[11];
                double[] y = new double[11];
                for (int sample = 0; sample <= 10; sample++) {
                    double progress = sample * 0.1;
                    x[sample] = bezier(x0, x1, x2, x3, progress);
                    y[sample] = bezier(y0, y1, y2, y3, progress);
                }
                intervals[interval] = new NaturalSpline(x, y);
            }
        }

        private double sample(double time) {
            if (time <= TIMES[0]) return intervals[0].firstValue();
            for (int index = 1; index < TIMES.length; index++) {
                if (time <= TIMES[index]) return intervals[index - 1].sample(time);
            }
            return intervals[intervals.length - 1].lastValue();
        }
    }

    private static final class NaturalSpline {
        private final double[] x;
        private final double[] a;
        private final double[] b;
        private final double[] c;
        private final double[] d;

        private NaturalSpline(double[] x, double[] values) {
            this.x = x;
            this.a = values;
            int size = x.length;
            b = new double[size - 1];
            c = new double[size];
            d = new double[size - 1];
            double[] h = new double[size - 1];
            double[] alpha = new double[size];
            for (int index = 0; index < size - 1; index++) h[index] = x[index + 1] - x[index];
            for (int index = 1; index < size - 1; index++) {
                alpha[index] = 3.0 * (values[index + 1] - values[index]) / h[index]
                        - 3.0 * (values[index] - values[index - 1]) / h[index - 1];
            }
            double[] l = new double[size];
            double[] mu = new double[size];
            double[] z = new double[size];
            l[0] = 1.0;
            for (int index = 1; index < size - 1; index++) {
                l[index] = 2.0 * (x[index + 1] - x[index - 1]) - h[index - 1] * mu[index - 1];
                mu[index] = h[index] / l[index];
                z[index] = (alpha[index] - h[index - 1] * z[index - 1]) / l[index];
            }
            l[size - 1] = 1.0;
            for (int index = size - 2; index >= 0; index--) {
                c[index] = z[index] - mu[index] * c[index + 1];
                b[index] = (values[index + 1] - values[index]) / h[index]
                        - h[index] * (c[index + 1] + 2.0 * c[index]) / 3.0;
                d[index] = (c[index + 1] - c[index]) / (3.0 * h[index]);
            }
        }

        private double sample(double time) {
            int interval = x.length - 2;
            for (int index = 0; index < x.length - 1; index++) {
                if (time <= x[index + 1]) {
                    interval = index;
                    break;
                }
            }
            double offset = time - x[interval];
            return a[interval] + b[interval] * offset + c[interval] * offset * offset
                    + d[interval] * offset * offset * offset;
        }

        private double firstValue() {
            return a[0];
        }

        private double lastValue() {
            return a[a.length - 1];
        }
    }
}
