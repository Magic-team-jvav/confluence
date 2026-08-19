package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.common.particle.CrossDustParticleOptions;
import org.confluence.lib.util.LibMathUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

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
    private static final double HIT_RADIUS = 0.9;
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

    public boolean isControlledByLocalInstance() {
        return true;
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
        setDeltaMovement(Vec3.ZERO);
        setPos(worldPosition(livingOwner, Math.min(tickCount, maxTrailTime())));
        xRotO = getXRot();
        yRotO = getYRot();
        setXRot(livingOwner.getXRot() * 0.8F);
        setYRot(sampleYaw(tickCount) + livingOwner.getYRot());

        Level level = level();
        if (!level.isClientSide) {
            hitSweptTrail(livingOwner);
            return;
        }
        if (level.isClientSide() && tickCount <= 14 && level.random.nextBoolean()) {
            Vec3 pos = position().offsetRandom(level.random, 0.3f);
            Entity particleOwner = getOwner();
            if (particleOwner != null) {
                Vec3 facing = LibMathUtils.rotToDir(particleOwner.getYHeadRot(), particleOwner.getXRot()).scale(0.05);
                Vector4f curve = new Vector4f(0, 1f, 1f, 1);
                boolean dark = level.random.nextBoolean();
                CrossDustParticleOptions lightParticle = new CrossDustParticleOptions(false,
                        dark ? 0xff570EFD : 0xffE4E0FF, dark ? 0xff411EA3 : 0xffC55BFF, facing.toVector3f(), curve,
                        level.random.nextFloat() * 0.15f + 0.15f, 10, 60, curve,
                        true, true, true, false);
                level.addParticle(lightParticle, pos.x, pos.y, pos.z, 0, 0, 0);
            }

            Vector4f curve = new Vector4f(0, 0.7f, 0.9f, 1);
            CrossDustParticleOptions darkParticle = new CrossDustParticleOptions(level.random.nextBoolean(),
                    0x66DD99FF, 0x7f714E82, Vec3.ZERO.offsetRandom(level.random, level.random.nextFloat() * 0.005f + 0.01f).toVector3f(),
                    curve, level.random.nextFloat() * 0.6f + 0.4f, 30, level.random.nextInt(-40, 40),
                    curve, true, true, false, false);
            level.addParticle(darkParticle, pos.x, pos.y, pos.z, 0, 0, 0);
        }
    }

    private void hitSweptTrail(LivingEntity owner) {
        float age = Math.min(tickCount, TIMES[TIMES.length - 1]);
        float start = Math.max(0.0F, age - 1.5F);
        for (float time = start; time <= age + 0.001F; time += 0.5F) {
            Vec3 point = worldPosition(owner, time);
            AABB box = new AABB(point, point).inflate(HIT_RADIUS);
            for (Entity target : level().getEntities(this, box, this::canHitEntity)) {
                hurtTarget(target);
            }
        }
    }

    public static Vec3 sampleLocalPoint(float time) {
        return new Vec3(X_CURVE.sample(time), interpolate(Y, time), Z_CURVE.sample(time));
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
