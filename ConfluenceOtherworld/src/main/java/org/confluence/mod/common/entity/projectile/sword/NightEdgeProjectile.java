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
import org.confluence.lib.common.particle.CrossDustParticleOptions;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.init.ModDamageTypes;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

public class NightEdgeProjectile extends SwordProjectile {
    private static final float[] TIMES = {0.0F, 3.0F, 6.0F, 9.0F, 12.0F};
    private static final float[] X = {-1.2F, -1.2F, 1.2F, 1.2F, -1.2F};
    private static final float[] Y = {0.3F, -0.4F, -0.8F, -0.4F, 0.3F};
    private static final float[] Z = {-2.0F, 1.0F, 1.0F, -2.0F, -2.0F};
    private static final float[] ROLL = {120.0F, 120.0F, 120.0F, 120.0F, 120.0F};
    private static final double HIT_RADIUS = 0.9;

    public NightEdgeProjectile(EntityType<? extends SwordProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);

        survivesBlockHit = true;
        remainingHits = 9999;
    }

    @Override
    public DamageSource damageSource() {
        return ModDamageTypes.of(level(), DamageTypes.MOB_ATTACK, this, getOwner());
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
    public void tick() {
        if (!level().isClientSide && (remainingHits == 0 || tickCount >= lifetime)) {
            discard();
            return;
        }

        baseTick();
        Entity owner = getOwner();
        if (!(owner instanceof LivingEntity livingOwner) || !owner.isAlive()) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }
        setPos(livingOwner.getEyePosition().add(0.0, -0.2, 0.0));
        direction = livingOwner.getLookAngle();
        entityData.set(DATA_DIRECTION, direction.toVector3f());
        setDeltaMovement(Vec3.ZERO);

        Level level = level();
        if (!level.isClientSide) {
            hitSweptTrail(livingOwner);
            if (tickCount >= lifetime) {
                discard();
            }
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
            Vec3 point = position().add(rotateLocalPoint(owner.getYRot(), sampleLocalPoint(time)));
            AABB box = new AABB(point, point).inflate(HIT_RADIUS);
            for (Entity target : level().getEntities(this, box, this::canHitEntity)) {
                hurtTarget(target);
            }
        }
    }

    public static Vec3 sampleLocalPoint(float time) {
        return new Vec3(interpolate(X, time), interpolate(Y, time), interpolate(Z, time));
    }

    public static Vec3 rotateLocalPoint(float ownerYaw, Vec3 point) {
        return point.yRot((-ownerYaw + 70.0F) * Mth.DEG_TO_RAD);
    }

    public static float sampleRoll(float time) {
        return interpolate(ROLL, time);
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
}
