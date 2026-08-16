package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEffects;
import org.joml.Vector3f;

/// 独眼巨鹿远距离攻击抛出的冰块。
///
/// <p>冰块保留 1.21 侧的高抛运动：生成时获得向上的随机速度，
/// 随后持续受重力影响并轻微衰减。伤害与霜冻效果只在服务端命中成功后结算。</p>
public final class DeerclopsThrownIceProjectile extends StraightMonsterProjectile {
    private static final EntityDataAccessor<Vector3f> DATA_ROTATION_AXIS =
            SynchedEntityData.defineId(
                    DeerclopsThrownIceProjectile.class,
                    EntityDataSerializers.VECTOR3);
    private static final int LIFETIME = 200;
    private static final double GRAVITY = 0.108;
    private static final double DRAG = 0.98;

    private final float rotationSpeed;

    public DeerclopsThrownIceProjectile(
            EntityType<? extends DeerclopsThrownIceProjectile> type,
            Level level) {
        super(type, level);
        Vector3f rotationAxis = new Vector3f(
                random.nextFloat() - 0.5F,
                random.nextFloat() - 0.5F,
                random.nextFloat() - 0.5F);
        if (rotationAxis.lengthSquared() < 1.0E-4F) {
            rotationAxis.set(0.0F, 1.0F, 0.0F);
        }
        rotationAxis.normalize();
        entityData.set(DATA_ROTATION_AXIS, rotationAxis);
        this.rotationSpeed = 0.08F + random.nextFloat() * 0.22F;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_ROTATION_AXIS, new Vector3f(0.0F, 1.0F, 0.0F));
    }

    public void configure(Mob owner, Vec3 origin, float damage) {
        Vec3 randomDirection = new Vec3(
                random.nextDouble() - 0.5,
                random.nextDouble() * 0.3,
                random.nextDouble() - 0.5);
        if (randomDirection.lengthSqr() < 1.0E-4) {
            randomDirection = new Vec3(0.0, 0.1, 0.0);
        }
        double speed = 0.3 + random.nextDouble() * 0.3;
        super.configure(
                owner,
                origin,
                randomDirection.normalize().scale(speed).add(0.0, 1.0, 0.0),
                damage,
                LIFETIME);
    }

    public Vector3f getRotationAxis() {
        return entityData.get(DATA_ROTATION_AXIS);
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        return velocity.add(0.0, -GRAVITY, 0.0).scale(DRAG);
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        target.addEffect(new MobEffectInstance(ModEffects.FROST_BURN.get(), 100));
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SNOWFLAKE,
                    getX(),
                    getY(),
                    getZ(),
                    50,
                    0.3,
                    0.3,
                    0.3,
                    0.2);
        }
        super.onHitBlock(result);
    }
}
