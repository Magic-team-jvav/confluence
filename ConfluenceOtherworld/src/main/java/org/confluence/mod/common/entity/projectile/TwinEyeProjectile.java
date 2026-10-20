package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.AbstractTwinEye;
import org.confluence.mod.common.entity.boss.Retinazer;
import org.confluence.mod.common.entity.boss.Spazmatism;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;
import org.joml.Vector3f;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/// 双子的直线激光、诅咒焰球和短程魔焰；传奇魔焰落地后保留伤害区域。
public final class TwinEyeProjectile extends StraightMonsterProjectile {
    private static final EntityDataAccessor<Boolean> STREAM = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GROUNDED = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LINGERING = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ARCING = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GIANT = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HOMING = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final DustParticleOptions CURSED_FLAME = new DustParticleOptions(new Vector3f(0.35F, 1.0F, 0.05F), 1.2F);
    private static final int GROUND_LIFETIME = 100;
    private static final int STREAM_LIFETIME = 30;
    private static final int ARC_LIFETIME = 60;
    private static final double ARC_SPEED = 0.75;
    private static final double ARC_LIFT = 0.25;
    private static final double ARC_MOVEMENT_INHERITANCE = 0.2;
    private static final double ARC_GRAVITY = 0.04;
    private static final byte FLAME_BURST_EVENT = 64;
    private static final int GIANT_LIFETIME = 120;
    private static final int HOMING_LIFETIME = 80;
    private static final int HOMING_GRACE_TICKS = 12;
    private static final int SPLIT_FLAME_COUNT = 5;
    private static final double SPLIT_RANGE = 30.0;
    private static final double GIANT_SPEED = 0.6;
    private static final double HOMING_LIFT = 0.3;
    private static final double HOMING_GRAVITY = 0.025;
    private static final double HOMING_SPEED = 0.85;
    private static final float HOMING_TURN_DEGREES = 4.0F;
    private final Variant variant;
    private Vec3 flameDestination = Vec3.ZERO;
    private Vec3 splitOrigin = Vec3.ZERO;
    private UUID homingTarget;
    private ParticleEmitter flameEmitter;
    private int groundTicks;

    public TwinEyeProjectile(EntityType<? extends TwinEyeProjectile> type, Level level, Variant variant) {
        super(type, level);
        this.variant = variant;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(STREAM, false);
        entityData.define(GROUNDED, false);
        entityData.define(LINGERING, false);
        entityData.define(ARCING, false);
        entityData.define(GIANT, false);
        entityData.define(HOMING, false);
    }

    public void configureFlame(AbstractTwinEye owner, Vec3 targetPosition, float damage, float velocity, boolean stream, boolean lingering) {
        Vec3 origin = owner.getMuzzlePosition(1.0F);
        configureAimed(owner, origin, targetPosition.subtract(origin),
                damage, velocity, stream ? 5.0F : 1.5F, stream ? STREAM_LIFETIME : 100);
        entityData.set(ARCING, false);
        entityData.set(STREAM, stream);
        entityData.set(LINGERING, lingering);
    }

    /// 发射方向与旋转后的眼球一致，继承少量冲刺速度并向上抛射，随后由重力落地。
    public void configureArcingFlame(AbstractTwinEye owner, float damage, boolean lingering) {
        Vec3 velocity = owner.getLookAngle().scale(ARC_SPEED)
                .add(owner.getDeltaMovement().scale(ARC_MOVEMENT_INHERITANCE)).add(0, ARC_LIFT, 0);
        configure(owner, owner.getMuzzlePosition(1.0F), velocity, damage, ARC_LIFETIME);
        entityData.set(STREAM, true);
        entityData.set(ARCING, true);
        entityData.set(LINGERING, lingering);
    }

    /// 巨焰先飞往预告位置，抵达后等待同组激光眼引爆，不能被其他组的激光触发。
    public void configureGiantFlame(Spazmatism owner, Vec3 destination, float damage, boolean lingering) {
        Vec3 origin = owner.getMuzzlePosition(1.0F);
        configure(owner, origin, destination.subtract(origin).normalize().scale(GIANT_SPEED), damage, GIANT_LIFETIME);
        flameDestination = destination;
        entityData.set(GIANT, true);
        entityData.set(LINGERING, lingering);
    }

    public boolean isGiantFlame() {return entityData.get(GIANT);}

    /// 只有实际光束命中且发射者为同组搭档时才分裂；固定五枚弹体，分散后再有限转向追踪。
    public boolean detonateFromLaser(Retinazer laser) {
        if (!isGiantFlame() || isRemoved() || !(level() instanceof ServerLevel server)
                || !(getOwner() instanceof Spazmatism owner) || !owner.isAlive() || owner.getPartner() != laser)
            return false;
        List<LivingEntity> targets = new ArrayList<>();
        LivingEntity primary = owner.getTarget();
        if (primary != null && primary.isAlive() && primary.distanceToSqr(this) <= SPLIT_RANGE * SPLIT_RANGE)
            targets.add(primary);
        for (var player : server.players()) {
            if (player != primary && player.distanceToSqr(this) <= SPLIT_RANGE * SPLIT_RANGE && owner.canAttack(player))
                targets.add(player);
        }
        Vec3 center = position();
        server.broadcastEntityEvent(this, FLAME_BURST_EVENT);
        discard();
        playSound(SoundEvents.GENERIC_EXPLODE, 0.9F, 1.3F);
        double startAngle = random.nextDouble() * Math.PI * 2;
        for (int i = 0; i < SPLIT_FLAME_COUNT; i++) {
            TwinEyeProjectile child = ModEntities.SPAZMATISM_FLAME.get().create(server);
            if (child == null) continue;
            double angle = startAngle + i * Math.PI * 2 / SPLIT_FLAME_COUNT;
            Vec3 velocity = new Vec3(Math.cos(angle) * HOMING_SPEED, HOMING_LIFT, Math.sin(angle) * HOMING_SPEED);
            child.configure(owner, center, velocity, getDamage(), HOMING_LIFETIME);
            child.splitOrigin = center;
            child.homingTarget = targets.isEmpty() ? null : targets.get(i % targets.size()).getUUID();
            child.entityData.set(HOMING, true);
            child.entityData.set(STREAM, true);
            child.entityData.set(LINGERING, entityData.get(LINGERING));
            server.addFreshEntity(child);
        }
        return true;
    }

    @Override
    public void tick() {
        /// 活动边界以引爆位置为圆心，不随目标移动；超界、超时或发射者死亡都会清理。
        if (!level().isClientSide && (isGiantFlame() || entityData.get(HOMING))) {
            if (!(getOwner() instanceof Spazmatism owner) || !owner.isAlive()
                    || entityData.get(HOMING) && position().distanceToSqr(splitOrigin) > SPLIT_RANGE * SPLIT_RANGE) {
                discard();
                return;
            }
        }
        if (entityData.get(GROUNDED)) {
            /// 落地后不再调用直线弹幕的移动与碰撞；同一生物仍遵守正常受伤无敌帧。
            baseTick();
            if (!level().isClientSide) {
                if (++groundTicks >= GROUND_LIFETIME || !(getOwner() instanceof Mob owner) || !owner.isAlive()) {
                    discard();
                    return;
                }
                if (groundTicks % 10 == 0) {
                    for (Entity target : LibEntityUtils.areaTargets(level(), this, getBoundingBox().inflate(1.2, 0.4, 1.2),
                            candidate -> LibEntityUtils.canMobHit(owner, candidate))) {
                        if (target.hurt(damageSources().mobProjectile(this, owner), getDamage())) {
                            LivingEntity living = LibEntityUtils.effectTarget(target);
                            if (living != null) onSuccessfulHit(owner, living);
                        }
                    }
                }
            }
        } else {
            super.tick();
        }
        if (!level().isClientSide || isRemoved() || variant == Variant.LASER) return;
        if (flameEmitter == null || flameEmitter.isRemoved()) {
            String effect = isGiantFlame() ? "twins_giant_flame"
                    : entityData.get(HOMING) ? "twins_homing_flame" : "cursed_flames";
            flameEmitter = new ParticleEmitter(level(), position(), Confluence.asResource(effect));
            flameEmitter.attachEntity(this);
            flameEmitter.hideOutline = true;
            MolangParticleEngine.INSTANCE.addEmitter(flameEmitter);
        }
        /// 新联动特效全部由粒子风暴定义维护，不再叠加逐刻生成的原版粒子。
        if (isGiantFlame() || entityData.get(HOMING)) return;
        int count = entityData.get(STREAM) || entityData.get(GROUNDED) ? 8 : 4;
        for (int i = 0; i < count; i++) {
            level().addParticle(CURSED_FLAME, getRandomX(0.5), getRandomY(), getRandomZ(0.5), 0, 0.02, 0);
        }
    }

    /// 引爆是独立的一次性世界特效，不能附着即将删除的巨焰，否则库会在生成前将其清理。
    @Override
    public void handleEntityEvent(byte event) {
        if (event == FLAME_BURST_EVENT && level().isClientSide) {
            ParticleEmitter burst = new ParticleEmitter(level(), position(), Confluence.asResource("twins_flame_burst"));
            /// 一次性发射数量和寿命由粒子定义控制，Java 只负责触发。
            burst.hideOutline = true;
            MolangParticleEngine.INSTANCE.addEmitter(burst);
        } else super.handleEntityEvent(event);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        if (isGiantFlame()) {
            if (level().isClientSide) return velocity;
            Vec3 remaining = flameDestination.subtract(position());
            return remaining.lengthSqr() <= velocity.lengthSqr() ? remaining : velocity;
        }
        if (entityData.get(HOMING)) {
            if (!level().isClientSide && tickCount > HOMING_GRACE_TICKS && homingTarget != null
                    && level() instanceof ServerLevel server && server.getEntity(homingTarget) instanceof LivingEntity target
                    && target.isAlive() && target.position().distanceToSqr(splitOrigin) <= SPLIT_RANGE * SPLIT_RANGE
                    && getOwner() instanceof Mob owner && owner.canAttack(target)) {
                Vec3 aim = target.getBoundingBox().getCenter().subtract(position());
                float yaw = (float) (Mth.atan2(-velocity.x, velocity.z) * Mth.RAD_TO_DEG);
                float pitch = (float) (Mth.atan2(-velocity.y, velocity.horizontalDistance()) * Mth.RAD_TO_DEG);
                float targetYaw = (float) (Mth.atan2(-aim.x, aim.z) * Mth.RAD_TO_DEG);
                float targetPitch = (float) (Mth.atan2(-aim.y, aim.horizontalDistance()) * Mth.RAD_TO_DEG);
                velocity = Vec3.directionFromRotation(Mth.approachDegrees(pitch, targetPitch, HOMING_TURN_DEGREES),
                        Mth.approachDegrees(yaw, targetYaw, HOMING_TURN_DEGREES)).scale(HOMING_SPEED);
            }
            return velocity.add(0, -HOMING_GRAVITY, 0);
        }
        /// 抛物线在所有难度下都下落，传奇只决定落地后是否滞留。
        if (entityData.get(ARCING)) return velocity.add(0, -ARC_GRAVITY, 0);
        if (!entityData.get(STREAM)) return velocity;
        return velocity.scale(0.97).add(0, entityData.get(LINGERING) ? -0.025 : 0, 0);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (isGiantFlame()) {
            flameDestination = result.getLocation().add(Vec3.atLowerCornerOf(result.getDirection().getNormal()).scale(0.7));
            setPos(flameDestination);
            setDeltaMovement(Vec3.ZERO);
        } else if (variant == Variant.CURSED_FLAME && entityData.get(LINGERING) && result.getDirection() == Direction.UP) {
            setPos(result.getLocation().add(0, 0.05, 0));
            setDeltaMovement(Vec3.ZERO);
            entityData.set(GROUNDED, true);
            groundTicks = 0;
        } else super.onHitBlock(result);
    }

    @Override
    protected boolean ownsBlockImpactMovement(BlockHitResult result) {return isGiantFlame() || entityData.get(GROUNDED);}

    /// 巨焰是等待激光触发的联动载体，不在碰到玩家时提前消失。
    @Override
    public boolean canHitEntity(Entity target) {
        return !isGiantFlame() && super.canHitEntity(target);
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        if (variant == Variant.CURSED_FLAME)
            target.addEffect(new MobEffectInstance(ModEffects.CURSED_INFERNO.get(), 100));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Giant", isGiantFlame());
        tag.putBoolean("Homing", entityData.get(HOMING));
        writeVector(tag, "Destination", flameDestination);
        writeVector(tag, "SplitOrigin", splitOrigin);
        if (homingTarget != null) tag.putUUID("HomingTarget", homingTarget);
        tag.putBoolean("Arcing", entityData.get(ARCING));
        tag.putBoolean("Stream", entityData.get(STREAM));
        tag.putBoolean("Grounded", entityData.get(GROUNDED));
        tag.putBoolean("Lingering", entityData.get(LINGERING));
        tag.putInt("GroundTicks", groundTicks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(GIANT, tag.getBoolean("Giant"));
        entityData.set(HOMING, tag.getBoolean("Homing"));
        flameDestination = readVector(tag, "Destination");
        splitOrigin = readVector(tag, "SplitOrigin");
        homingTarget = tag.hasUUID("HomingTarget") ? tag.getUUID("HomingTarget") : null;
        entityData.set(ARCING, tag.getBoolean("Arcing"));
        entityData.set(STREAM, tag.getBoolean("Stream"));
        entityData.set(GROUNDED, tag.getBoolean("Grounded"));
        entityData.set(LINGERING, tag.getBoolean("Lingering"));
        groundTicks = Math.max(0, tag.getInt("GroundTicks"));
    }

    /// 运动目标与范围中心保留小数精度，重载后不会偏移到方块角落。
    private static void writeVector(CompoundTag tag, String name, Vec3 vector) {
        tag.putDouble(name + "X", vector.x);
        tag.putDouble(name + "Y", vector.y);
        tag.putDouble(name + "Z", vector.z);
    }

    private static Vec3 readVector(CompoundTag tag, String name) {
        return new Vec3(tag.getDouble(name + "X"), tag.getDouble(name + "Y"), tag.getDouble(name + "Z"));
    }

    public enum Variant {LASER, CURSED_FLAME}
}
