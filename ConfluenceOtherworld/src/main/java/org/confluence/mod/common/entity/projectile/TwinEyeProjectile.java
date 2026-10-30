package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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


/// 双子的直线激光、诅咒焰球和短程魔焰；传奇魔焰落地后保留伤害区域。
public final class TwinEyeProjectile extends StraightMonsterProjectile {
    private static final EntityDataAccessor<Boolean> STREAM = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GROUNDED = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LINGERING = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ARCING = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GIANT = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RAIN = SynchedEntityData.defineId(TwinEyeProjectile.class, EntityDataSerializers.BOOLEAN);

    /// 一阶段诅咒焰球的寿命，单位 tick。
    private static final int FLAME_BALL_LIFETIME = 100;
    /// 诅咒焰球的随机散布强度。
    private static final float FLAME_BALL_INACCURACY = 1.5F;
    /// 单枚持续喷火弹体的寿命，单位 tick；与初速、阻力共同决定射程。
    private static final int STREAM_LIFETIME = 45;
    /// 持续喷火的随机散布强度。
    private static final float STREAM_INACCURACY = 5.0F;
    /// 持续火焰每 tick 保留的速度比例，越大射程越远。
    private static final double STREAM_DRAG = 0.97;
    /// 传奇直喷魔焰的下落加速度，单位格/tick²。
    private static final double LINGERING_GRAVITY = 0.025;
    /// 命中后施加诅咒狱火的持续时间，单位 tick。
    private static final int CURSED_INFERNO_TICKS = 100;

    /// 冲刺抛物线魔焰的寿命，单位 tick。
    private static final int ARC_LIFETIME = 60;
    /// 冲刺抛物线魔焰沿眼球朝向的初速，单位格/tick。
    private static final double ARC_SPEED = 0.75;
    /// 冲刺抛物线魔焰的额外向上初速，单位格/tick。
    private static final double ARC_LIFT = 0.25;
    /// 冲刺魔焰继承发射者移动速度的比例。
    private static final double ARC_MOVEMENT_INHERITANCE = 0.2;
    /// 冲刺抛物线魔焰的重力加速度，单位格/tick²。
    private static final double ARC_GRAVITY = 0.04;

    /// 传奇魔焰落地后的滞留时间，单位 tick。
    private static final int GROUND_LIFETIME = 100;
    /// 滞留魔焰尝试造成伤害的间隔，单位 tick。
    private static final int GROUND_DAMAGE_INTERVAL_TICKS = 10;
    /// 滞留魔焰伤害检测的水平扩张距离，单位格。
    private static final double GROUND_HORIZONTAL_RADIUS = 1.2;
    /// 滞留魔焰伤害检测的竖直扩张距离，单位格。
    private static final double GROUND_VERTICAL_RADIUS = 0.4;
    /// 滞留魔焰高于地面的偏移，单位格。
    private static final double GROUND_SURFACE_OFFSET = 0.05;

    /// 等待引爆的巨焰寿命，单位 tick。
    private static final int GIANT_LIFETIME = 120;
    /// 巨焰追踪飞行速度，单位格/tick。
    private static final double GIANT_SPEED = 1.2;
    /// 巨焰每 tick 向目标方向修正的比例，范围 0–1。
    private static final double GIANT_TRACKING = 0.12;
    /// 巨焰接触伤害相对普通魔焰的倍率。
    private static final float GIANT_DAMAGE_MULTIPLIER = 3.0F;
    /// 巨焰撞墙后离开表面的距离，单位格。
    private static final double GIANT_WALL_PADDING = 0.7;
    /// 巨焰引爆后生成的追踪魔焰数量。
    private static final int SPLIT_FLAME_COUNT = 15;
    /// 分裂魔焰的寿命，单位 tick。
    private static final int RAIN_LIFETIME = 200;
    /// 分裂魔焰持续追踪的时间，单位 tick；结束后保持最后的飞行方向。
    private static final int RAIN_TRACKING_TICKS = 160;
    /// 分裂魔焰飞行速度，单位格/tick。
    private static final double RAIN_SPEED = 1.1;
    /// 分裂魔焰每 tick 向目标方向修正的比例，范围 0–1。
    private static final double RAIN_TRACKING = 0.24;

    private static final DustParticleOptions CURSED_FLAME = new DustParticleOptions(new Vector3f(0.35F, 1.0F, 0.05F), 1.2F);
    private static final byte FLAME_BURST_EVENT = 64;
    private final Variant variant;
    private ParticleEmitter flameEmitter;
    private int groundTicks;

    public TwinEyeProjectile(EntityType<? extends TwinEyeProjectile> type, Level level, Variant variant) {
        super(type, level);
        this.variant = variant;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STREAM, false);
        builder.define(GROUNDED, false);
        builder.define(LINGERING, false);
        builder.define(ARCING, false);
        builder.define(GIANT, false);
        builder.define(RAIN, false);
    }

    public void configureFlame(AbstractTwinEye owner, Vec3 targetPosition, float damage, float velocity, boolean stream, boolean lingering) {
        Vec3 origin = owner.getMuzzlePosition(1.0F);
        configureAimed(owner, origin, targetPosition.subtract(origin),
                damage, velocity, stream ? STREAM_INACCURACY : FLAME_BALL_INACCURACY, stream ? STREAM_LIFETIME : FLAME_BALL_LIFETIME);
        entityData.set(ARCING, false);
        entityData.set(STREAM, stream);
        entityData.set(LINGERING, lingering);
    }

    /// 发射方向与旋转后的眼球一致，继承少量冲刺速度并向上抛射，随后由重力落地。
    public void configureArcingFlame(AbstractTwinEye owner, float damage, boolean lingering) {
        Vec3 velocity = owner.getLookAngle().scale(ARC_SPEED).add(owner.getDeltaMovement().scale(ARC_MOVEMENT_INHERITANCE)).add(0, ARC_LIFT, 0);
        configure(owner, owner.getMuzzlePosition(1.0F), velocity, damage, ARC_LIFETIME);
        entityData.set(STREAM, true);
        entityData.set(ARCING, true);
        entityData.set(LINGERING, lingering);
    }

    /// 巨焰从预告方向出发并持续追踪目标，只能被同组激光眼引爆。
    public void configureGiantFlame(Spazmatism owner, Vec3 destination, float damage, boolean lingering) {
        Vec3 origin = owner.getMuzzlePosition(1.0F);
        configure(owner, origin, destination.subtract(origin).normalize().scale(GIANT_SPEED), damage, GIANT_LIFETIME);
        entityData.set(GIANT, true);
        entityData.set(LINGERING, lingering);
    }

    public boolean isGiantFlame() {return entityData.get(GIANT);}

    /// 同组激光实际命中后生成十五枚分散出射的追踪魔焰。
    public boolean detonateFromLaser(Retinazer laser) {
        if (!isGiantFlame() || isRemoved() || !(level() instanceof ServerLevel server)
                || !(getOwner() instanceof Spazmatism owner) || !owner.isAlive() || owner.getPartner() != laser)
            return false;
        Vec3 center = position();
        server.broadcastEntityEvent(this, FLAME_BURST_EVENT);
        discard();
        playSound(SoundEvents.GENERIC_EXPLODE.value(), 0.9F, 1.3F);
        double startAngle = random.nextDouble() * Math.PI * 2;
        for (int i = 0; i < SPLIT_FLAME_COUNT; i++) {
            TwinEyeProjectile child = ModEntities.SPAZMATISM_FLAME.get().create(server);
            if (child == null) continue;
            double angle = startAngle + i * Math.PI * 2 / SPLIT_FLAME_COUNT;
            Vec3 velocity = new Vec3(Math.cos(angle), 0.25, Math.sin(angle)).normalize().scale(RAIN_SPEED);
            child.configure(owner, center, velocity, getDamage(), RAIN_LIFETIME);
            child.entityData.set(RAIN, true);
            child.entityData.set(STREAM, true);
            child.entityData.set(LINGERING, entityData.get(LINGERING));
            server.addFreshEntity(child);
        }
        return true;
    }

    @Override
    public void tick() {
        /// 联动弹体在发射者死亡或寿命耗尽时清理，追踪范围不会固定在旧引爆位置。
        if (!level().isClientSide && (isGiantFlame() || entityData.get(RAIN))) {
            if (!(getOwner() instanceof Spazmatism owner) || !owner.isAlive()) {
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
                if (groundTicks % GROUND_DAMAGE_INTERVAL_TICKS == 0) {
                    for (Entity target : LibEntityUtils.areaTargets(level(), this, getBoundingBox().inflate(GROUND_HORIZONTAL_RADIUS, GROUND_VERTICAL_RADIUS, GROUND_HORIZONTAL_RADIUS),
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
                    : entityData.get(RAIN) ? "twins_flame_rain" : "cursed_flames";
            flameEmitter = new ParticleEmitter(level(), position(), Confluence.asResource(effect));
            flameEmitter.attachEntity(this);
            flameEmitter.hideOutline = true;
            MolangParticleEngine.INSTANCE.addEmitter(flameEmitter);
        }
        /// 新联动特效全部由粒子风暴定义维护，不再叠加逐刻生成的原版粒子。
        if (isGiantFlame() || entityData.get(RAIN)) return;
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
        if (isGiantFlame() || entityData.get(RAIN)) {
            if (level().isClientSide) return velocity;
            double speed = isGiantFlame() ? GIANT_SPEED : RAIN_SPEED;
            double tracking = isGiantFlame() ? GIANT_TRACKING : RAIN_TRACKING;
            if (getOwner() instanceof Spazmatism owner) {
                LivingEntity target = owner.getTarget();
                if (target != null && target.isAlive() && (isGiantFlame() || tickCount <= RAIN_TRACKING_TICKS)) {
                    Vec3 aim = target.getEyePosition().subtract(position());
                    Vec3 forward = velocity.normalize();
                    Vec3 desired = aim.normalize();
                    if (forward.dot(desired) < -0.95) {
                        Vec3 axis = Math.abs(forward.y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
                        forward = forward.add(forward.cross(axis).normalize().scale(tracking)).normalize();
                    }
                    Vec3 direction = forward.lerp(desired, tracking);
                    if (direction.lengthSqr() > 1.0E-8)
                        velocity = direction.normalize().scale(speed);
                }
                if (isGiantFlame()) {
                    for (Entity victim : LibEntityUtils.areaTargets(level(), this,
                            getBoundingBox().expandTowards(velocity), candidate -> LibEntityUtils.canMobHit(owner, candidate))) {
                        if (victim.hurt(damageSources().mobProjectile(this, owner), getDamage() * GIANT_DAMAGE_MULTIPLIER)) {
                            LivingEntity living = LibEntityUtils.effectTarget(victim);
                            if (living != null) onSuccessfulHit(owner, living);
                        }
                    }
                }
            }
            return velocity;
        }
        /// 抛物线在所有难度下都下落，传奇只决定落地后是否滞留。
        if (entityData.get(ARCING)) return velocity.add(0, -ARC_GRAVITY, 0);
        if (!entityData.get(STREAM)) return velocity;
        return velocity.scale(STREAM_DRAG).add(0, entityData.get(LINGERING) ? -LINGERING_GRAVITY : 0, 0);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (isGiantFlame()) {
            setPos(result.getLocation().add(Vec3.atLowerCornerOf(result.getDirection().getNormal()).scale(GIANT_WALL_PADDING)));
            setDeltaMovement(Vec3.ZERO);
        } else if (variant == Variant.CURSED_FLAME && entityData.get(LINGERING) && result.getDirection() == Direction.UP) {
            setPos(result.getLocation().add(0, GROUND_SURFACE_OFFSET, 0));
            setDeltaMovement(Vec3.ZERO);
            entityData.set(GROUNDED, true);
            groundTicks = 0;
        } else super.onHitBlock(result);
    }

    @Override
    protected boolean ownsBlockImpactMovement(BlockHitResult result) {return isGiantFlame() || entityData.get(GROUNDED);}

    /// 巨焰的接触伤害单独检测，不因撞到玩家而在激光引爆前消失。
    @Override
    public boolean canHitEntity(Entity target) {
        return !isGiantFlame() && super.canHitEntity(target);
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        if (variant == Variant.CURSED_FLAME)
            target.addEffect(new MobEffectInstance(ModEffects.CURSED_INFERNO, CURSED_INFERNO_TICKS));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Giant", isGiantFlame());
        tag.putBoolean("Rain", entityData.get(RAIN));
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
        entityData.set(RAIN, tag.getBoolean("Rain"));
        entityData.set(ARCING, tag.getBoolean("Arcing"));
        entityData.set(STREAM, tag.getBoolean("Stream"));
        entityData.set(GROUNDED, tag.getBoolean("Grounded"));
        entityData.set(LINGERING, tag.getBoolean("Lingering"));
        groundTicks = Math.max(0, tag.getInt("GroundTicks"));
    }

    public enum Variant {LASER, CURSED_FLAME}
}
