package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.ProjectileHitRules;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.AbstractTwinEye;
import org.confluence.mod.common.init.ModEffects;
import org.joml.Vector3f;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

/// 双子的直线激光、诅咒焰球和短程魔焰；传奇魔焰落地后保留伤害区域。
public final class TwinEyeProjectile extends StraightMonsterProjectile {
    private static final EntityDataAccessor<Boolean> STREAM = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GROUNDED = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LINGERING = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final DustParticleOptions CURSED_FLAME = new DustParticleOptions(new Vector3f(0.35F, 1.0F, 0.05F), 1.2F);
    private static final int GROUND_LIFETIME = 100;
    private final Variant variant;
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
    }

    public void configureFlame(AbstractTwinEye owner, Vec3 targetPosition, float damage, float velocity, boolean stream, boolean lingering) {
        Vec3 origin = owner.getMuzzlePosition(1.0F);
        configureAimed(owner, origin, targetPosition.subtract(origin),
                damage, velocity, stream ? 5.0F : 1.5F, stream ? 24 : 100);
        entityData.set(STREAM, stream);
        entityData.set(LINGERING, lingering);
    }

    @Override
    public void tick() {
        if (entityData.get(GROUNDED)) {
            // 落地后不再调用直线弹幕的移动与碰撞；同一生物仍遵守正常受伤无敌帧。
            baseTick();
            if (!level().isClientSide) {
                if (++groundTicks >= GROUND_LIFETIME || !(getOwner() instanceof Mob owner) || !owner.isAlive()) {
                    discard();
                    return;
                }
                if (groundTicks % 10 == 0) {
                    for (Entity target : ProjectileHitRules.areaTargets(level(), this, getBoundingBox().inflate(1.2, 0.4, 1.2),
                            candidate -> ProjectileHitRules.canMobHit(owner, candidate))) {
                        if (ProjectileHitRules.hurt(target, damageSources().mobProjectile(this, owner), getDamage())) {
                            LivingEntity living = ProjectileHitRules.effectTarget(target);
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
            flameEmitter = new ParticleEmitter(level(), position(), Confluence.asResource("cursed_flames"));
            flameEmitter.attachEntity(this);
            flameEmitter.hideOutline = true;
            MolangParticleEngine.INSTANCE.addEmitter(flameEmitter);
        }
        int count = entityData.get(STREAM) || entityData.get(GROUNDED) ? 8 : 4;
        for (int i = 0; i < count; i++) {
            level().addParticle(CURSED_FLAME, getRandomX(0.5), getRandomY(), getRandomZ(0.5), 0, 0.02, 0);
        }
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        if (!entityData.get(STREAM)) return velocity;
        return velocity.scale(0.97).add(0, entityData.get(LINGERING) ? -0.025 : 0, 0);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (variant == Variant.CURSED_FLAME && entityData.get(LINGERING) && result.getDirection() == Direction.UP) {
            setPos(result.getLocation().add(0, 0.05, 0));
            setDeltaMovement(Vec3.ZERO);
            entityData.set(GROUNDED, true);
            groundTicks = 0;
        } else super.onHitBlock(result);
    }

    @Override
    protected boolean ownsBlockImpactMovement(BlockHitResult result) {return entityData.get(GROUNDED);}

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        if (variant == Variant.CURSED_FLAME)
            target.addEffect(new MobEffectInstance(ModEffects.CURSED_INFERNO.get(), 100));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Stream", entityData.get(STREAM));
        tag.putBoolean("Grounded", entityData.get(GROUNDED));
        tag.putBoolean("Lingering", entityData.get(LINGERING));
        tag.putInt("GroundTicks", groundTicks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(STREAM, tag.getBoolean("Stream"));
        entityData.set(GROUNDED, tag.getBoolean("Grounded"));
        entityData.set(LINGERING, tag.getBoolean("Lingering"));
        groundTicks = Math.max(0, tag.getInt("GroundTicks"));
    }

    public enum Variant {LASER, CURSED_FLAME}
}
