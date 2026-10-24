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

import java.util.ArrayList;
import java.util.List;

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
    /// 等待引爆的巨焰飞行速度，单位格/tick。
    private static final double GIANT_SPEED = 0.6;
    /// 巨焰撞墙后离开表面的距离，单位格。
    private static final double GIANT_WALL_PADDING = 0.7;
    /// 巨焰引爆后生成的魔焰雨实体总数。
    private static final int SPLIT_FLAME_COUNT = 30;
    /// 魔焰雨中覆盖玩家附近的弹体数量，其余铺开整个范围。
    private static final int TARGETED_RAIN_COUNT = 10;
    /// 联动魔焰活动和选取目标的范围半径，单位格。
    private static final double SPLIT_RANGE = 30.0;
    /// 魔焰雨落点相对引爆点的最大水平半径，单位格。
    private static final double RAIN_RADIUS = 27.0;
    /// 针对玩家的魔焰雨落点分散半径，单位格。
    private static final double TARGETED_RAIN_RADIUS = 6.0;
    /// 魔焰雨落至预定高度的最短飞行时间，单位 tick。
    private static final int RAIN_MIN_FLIGHT_TICKS = 32;
    /// 魔焰雨飞行时间的随机取值数量，单位 tick，最大附加值为此值减一。
    private static final int RAIN_FLIGHT_VARIATION = 17;
    /// 非针对玩家的魔焰雨落点低于引爆点的高度，单位格。
    private static final double RAIN_FALL_HEIGHT = 6;
    /// 魔焰雨落点使用的黄金角，单位弧度，使落点均匀铺开。
    private static final double RAIN_GOLDEN_ANGLE = Math.PI * (3.0 - Math.sqrt(5.0));
    /// 联动分裂魔焰的寿命，单位 tick。
    private static final int RAIN_LIFETIME = 80;
    /// 联动魔焰雨的重力加速度，单位格/tick²。
    private static final double RAIN_GRAVITY = 0.025;


    private static final DustParticleOptions CURSED_FLAME = new DustParticleOptions(new Vector3f(0.35F, 1.0F, 0.05F), 1.2F);
    private static final byte FLAME_BURST_EVENT = 64;
    private final Variant variant;
    private Vec3 flameDestination = Vec3.ZERO;
    private Vec3 splitOrigin = Vec3.ZERO;
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

    /// 巨焰先飞往预告位置，抵达后等待同组激光眼引爆，不能被其他组的激光触发。
    public void configureGiantFlame(Spazmatism owner, Vec3 destination, float damage, boolean lingering) {
        Vec3 origin = owner.getMuzzlePosition(1.0F);
        configure(owner, origin, destination.subtract(origin).normalize().scale(GIANT_SPEED), damage, GIANT_LIFETIME);
        flameDestination = destination;
        entityData.set(GIANT, true);
        entityData.set(LINGERING, lingering);
    }

    public boolean isGiantFlame() {return entityData.get(GIANT);}

    /// 同组激光实际命中后抛出三十枚魔焰雨；部分覆盖目标周围，其余铺开引爆点附近区域。
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
        playSound(SoundEvents.GENERIC_EXPLODE.value(), 0.9F, 1.3F);
        int targetedCount = targets.isEmpty() ? 0 : TARGETED_RAIN_COUNT;
        double startAngle = random.nextDouble() * Math.PI * 2;
        for (int i = 0; i < SPLIT_FLAME_COUNT; i++) {
            TwinEyeProjectile child = ModEntities.SPAZMATISM_FLAME.get().create(server);
            if (child == null) continue;
            double angle = startAngle + i * RAIN_GOLDEN_ANGLE;
            boolean targeted = i < targetedCount;
            LivingEntity target = targeted ? targets.get(i % targets.size()) : null;
            Vec3 destination = targeted ? target.position() : center.add(0, -RAIN_FALL_HEIGHT, 0);
            double radius = targeted ? TARGETED_RAIN_RADIUS * Math.sqrt((i + 0.5) / TARGETED_RAIN_COUNT) : RAIN_RADIUS * Math.sqrt((i - targetedCount + 0.5) / (SPLIT_FLAME_COUNT - targetedCount));
            destination = destination.add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
            /// 落点限制在原引爆范围内；固定抛物线让玩家可以观察并躲避，不把三十枚弹体全部追踪到同一点。
            Vec3 horizontal = new Vec3(destination.x - center.x, 0, destination.z - center.z);
            if (horizontal.lengthSqr() > RAIN_RADIUS * RAIN_RADIUS)
                horizontal = horizontal.normalize().scale(RAIN_RADIUS);
            int flightTicks = RAIN_MIN_FLIGHT_TICKS + random.nextInt(RAIN_FLIGHT_VARIATION);
            Vec3 velocity = horizontal.scale(1.0 / flightTicks).add(0, (destination.y - center.y) / flightTicks + RAIN_GRAVITY * (flightTicks + 1) / 2.0, 0);
            child.configure(owner, center, velocity, getDamage(), RAIN_LIFETIME);
            child.splitOrigin = center;
            child.entityData.set(RAIN, true);
            child.entityData.set(STREAM, true);
            child.entityData.set(LINGERING, entityData.get(LINGERING));
            server.addFreshEntity(child);
        }
        return true;
    }

    @Override
    public void tick() {
        /// 活动边界以引爆位置为圆心，不随目标移动；超界、超时或发射者死亡都会清理。
        if (!level().isClientSide && (isGiantFlame() || entityData.get(RAIN))) {
            if (!(getOwner() instanceof Spazmatism owner) || !owner.isAlive()
                    || entityData.get(RAIN) && position().distanceToSqr(splitOrigin) > SPLIT_RANGE * SPLIT_RANGE) {
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
            MolangParticleEngine.INSTANCE._addEmitter(flameEmitter);
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
            MolangParticleEngine.INSTANCE._addEmitter(burst);
        } else super.handleEntityEvent(event);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        if (isGiantFlame()) {
            if (level().isClientSide) return velocity;
            Vec3 remaining = flameDestination.subtract(position());
            return remaining.lengthSqr() <= velocity.lengthSqr() ? remaining : velocity;
        }
        if (entityData.get(RAIN)) {
            return velocity.add(0, -RAIN_GRAVITY, 0);
        }
        /// 抛物线在所有难度下都下落，传奇只决定落地后是否滞留。
        if (entityData.get(ARCING)) return velocity.add(0, -ARC_GRAVITY, 0);
        if (!entityData.get(STREAM)) return velocity;
        return velocity.scale(STREAM_DRAG).add(0, entityData.get(LINGERING) ? -LINGERING_GRAVITY : 0, 0);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (isGiantFlame()) {
            flameDestination = result.getLocation().add(Vec3.atLowerCornerOf(result.getDirection().getNormal()).scale(GIANT_WALL_PADDING));
            setPos(flameDestination);
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

    /// 巨焰是等待激光触发的联动载体，不在碰到玩家时提前消失。
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
        writeVector(tag, "Destination", flameDestination);
        writeVector(tag, "SplitOrigin", splitOrigin);
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
        flameDestination = readVector(tag, "Destination");
        splitOrigin = readVector(tag, "SplitOrigin");
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
