package org.confluence.mod.client.particle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.sword.PhasebladeProjectile;
import org.mesdag.particlestorm.data.component.EmitterShape;
import org.mesdag.particlestorm.particle.ParticleEmitter;

/// 沿剑尖或剑柄末端的运动轨迹补点，粒子留在世界路径上，生命周期仍由绑定实体管理。
public final class BladeTrailEmitter extends ParticleEmitter {
    private static final double TRAIL_SPACING = 0.08D;
    private static final int MAX_SAMPLES_PER_TICK = 64;
    private static final double MAX_CONTINUOUS_DISTANCE_SQR = 16.0D * 16.0D;
    private final PhasebladeProjectile projectile;
    private final double end;
    private final EmitterShape shape;
    private Vec3 previousCenter;
    private float previousYaw;
    private float previousRoll;

    public BladeTrailEmitter(PhasebladeProjectile projectile, Vec3 position, ResourceLocation particle, double end) {
        super(projectile.level(), position, particle);
        this.projectile = projectile;
        this.end = end;
        this.shape = components.stream().filter(EmitterShape.class::isInstance).map(EmitterShape.class::cast).findFirst().orElseThrow();
        this.components = components.stream().filter(component -> !(component instanceof EmitterShape)).toList();
        attachEntity(projectile);
        hideOutline = true;
    }

    @Override
    protected void updatePos(double x, double y, double z) {
        Vec3 endpoint = endpoint(projectile.getBoundingBox().getCenter(), projectile.visualYaw(), projectile.visualRoll(0.0F));
        super.updatePos(endpoint.x, endpoint.y, endpoint.z);
    }

    @Override
    public void tick() {
        // 一刻只推进一次发射器生命周期，补点只调用形状组件，不能加速粒子计时。
        super.tick();
        if (isRemoved()) return;
        if (!active || projectile.state() == PhasebladeProjectile.State.STUCK) {
            previousCenter = null;
            return;
        }

        Vec3 center = projectile.getBoundingBox().getCenter();
        float yaw = projectile.visualYaw();
        float roll = projectile.visualRoll(0.0F);
        if (previousCenter == null || previousCenter.distanceToSqr(center) > MAX_CONTINUOUS_DISTANCE_SQR) {
            previousCenter = center;
            previousYaw = yaw;
            previousRoll = roll;
        }
        float yawDelta = Mth.wrapDegrees((yaw - previousYaw) * Mth.RAD_TO_DEG) * Mth.DEG_TO_RAD;
        float rollDelta = Mth.wrapDegrees((roll - previousRoll) * Mth.RAD_TO_DEG) * Mth.DEG_TO_RAD;
        double pathLength = previousCenter.distanceTo(center) + projectile.projectileGeometry().length() * 0.5D * (Math.abs(yawDelta) + Math.abs(rollDelta));
        int configuredRate = spawnRate;
        float configuredChance = spawnChance;
        int minimumSamples = configuredRate + (level.random.nextFloat() < configuredChance ? 1 : 0);
        int samples = Math.min(MAX_SAMPLES_PER_TICK, Math.max(minimumSamples, Mth.ceil(pathLength / TRAIL_SPACING)));
        spawnRate = 1;
        spawnChance = 0.0F;
        try {
            for (int sample = 1; sample <= samples; sample++) {
                double progress = (double) sample / samples;
                setPos(endpoint(previousCenter.lerp(center, progress), previousYaw + yawDelta * (float) progress, previousRoll + rollDelta * (float) progress));
                shape.update(this);
            }
        } finally {
            setPos(endpoint(center, yaw, roll));
            spawnRate = configuredRate;
            spawnChance = configuredChance;
        }
        previousCenter = center;
        previousYaw = yaw;
        previousRoll = roll;
    }

    private Vec3 endpoint(Vec3 center, float yaw, float roll) {
        Vec3 axis = new Vec3(-Mth.sin(roll), Mth.cos(roll), 0.0D).yRot(-yaw);
        return center.add(axis.scale(projectile.projectileGeometry().length() * 0.5D * end));
    }
}
