package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.monster.Corruptor;
import org.confluence.mod.common.init.ModParticleTypes;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/// 使用粒子表现的敌对生物弹幕。
///
/// 每个注册实体类型在构造时固定一种 {@link Variant}，客户端只需根据实体类型即可
/// 还原外观和命中特效，不必为运行期间不会改变的数据额外同步字段。行为树负责请求创建
/// 弹幕，具体粒子和附加效果由弹幕变种自身保存。
public final class HostileParticleProjectile extends StraightMonsterProjectile {
    private static final float VELOCITY = 0.3F;
    private static final float INACCURACY = 0.8F;
    private static final int INFERNO_BLAST_LIFETIME = 180;
    private static final double INFERNO_BLAST_RADIUS = 2.5;
    private static final String INFERNO_FLIGHT_TICKS_KEY = "InfernoFlightTicks";
    private static final String INFERNO_BLAST_TICKS_KEY = "InfernoBlastTicks";
    private static final String INFERNO_BLAST_KEY = "InfernoBlast";
    private static final EntityDataAccessor<Boolean> INFERNO_BLAST = SynchedEntityData.defineId(HostileParticleProjectile.class, EntityDataSerializers.BOOLEAN);

    private final Variant variant;
    private int infernoFlightTicks = Integer.MAX_VALUE;
    private int infernoBlastTicks;

    public HostileParticleProjectile(EntityType<? extends HostileParticleProjectile> type, Level level, Variant variant) {
        super(type, level);
        this.variant = variant;
    }

    /// 使用普通施法怪物的低速、有轻微散布参数瞄准目标。
    public void configure(Mob owner, LivingEntity target, float damage) {
        Vec3 origin = owner.getEyePosition();
        Vec3 aim = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0).subtract(origin);
        configureAimed(owner, origin, aim, damage, VELOCITY, INACCURACY, variant.maximumLifetime());
        if (variant == Variant.INFERNO_BOLT) {
            infernoFlightTicks = Math.max(1, Mth.ceil(aim.length() / VELOCITY));
        }
    }

    public Variant getVariant() {
        return variant;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(INFERNO_BLAST, false);
    }

    @Override
    public void tick() {
        if (!level().isClientSide && variant == Variant.INFERNO_BOLT
                && !isInfernoBlast() && tickCount >= infernoFlightTicks) {
            activateInfernoBlast();
        }
        super.tick();
        if (isRemoved()) return;
        if (!level().isClientSide && isInfernoBlast()) {
            tickInfernoBlast();
        } else if (level().isClientSide) {
            spawnParticles();
        }
    }

    private void spawnParticles() {
        ParticleOptions particle = variant.particle();
        if (particle == null) return;
        if (isInfernoBlast()) {
            for (int i = 0; i < 18; i++) {
                Vec3 offset = new Vec3(random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
                if (offset.lengthSqr() < 1.0E-7) continue;
                offset = offset.normalize().scale(random.nextDouble() * INFERNO_BLAST_RADIUS);
                level().addParticle(particle, getX() + offset.x, getY() + offset.y, getZ() + offset.z, 0.0, 0.0, 0.0);
            }
            return;
        }
        Vec3 movement = getDeltaMovement();
        for (int i = 0; i < 3; i++) {
            level().addParticle(particle, getRandomX(0.5), getRandomY(), getRandomZ(0.5), movement.x, movement.y, movement.z);
        }
    }

    private void tickInfernoBlast() {
        setDeltaMovement(Vec3.ZERO);
        if (++infernoBlastTicks > INFERNO_BLAST_LIFETIME) {
            discard();
            return;
        }
        if (!(getOwner() instanceof Mob owner)) return;
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(INFERNO_BLAST_RADIUS), owner::canAttack)) {
            if (target.hurt(damageSources().mobProjectile(this, owner), getDamage())) {
                onSuccessfulHit(owner, target);
            }
        }
    }

    private void activateInfernoBlast() {
        entityData.set(INFERNO_BLAST, true);
        infernoBlastTicks = 0;
        setDeltaMovement(Vec3.ZERO);
        hasImpulse = true;
    }

    private boolean isInfernoBlast() {
        return variant == Variant.INFERNO_BOLT && entityData.get(INFERNO_BLAST);
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        switch (variant) {
            case VILE_SPIT -> {
                if (owner instanceof Corruptor && random.nextInt(20) == 0) {
                    int duration = LibUtils.isMaster(level(), blockPosition()) ? 15_000
                            : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 12_000 : 6_000;
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration), owner);
                }
            }
            case FIRE_IMP -> {
                if (random.nextInt(3) == 0) {
                    int ticks = LibUtils.isMaster(level(), blockPosition()) ? 350
                            : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 280 : 140;
                    target.igniteForTicks(ticks);
                }
            }
            case SHADOW_BEAM -> {
                int duration = Mth.randomBetweenInclusive(random, 100, 300);
                if (LibUtils.isMaster(level(), blockPosition())) duration = duration * 5 / 2;
                else if (LibUtils.isAtLeastExpert(level(), blockPosition())) duration *= 2;
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration), owner);
            }
            case LOST_SOUL -> {
                int duration = Mth.randomBetweenInclusive(random, 40, 120);
                if (LibUtils.isMaster(level(), blockPosition())) duration = duration * 5 / 2;
                else if (LibUtils.isAtLeastExpert(level(), blockPosition())) duration *= 2;
                target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, duration), owner);
            }
            case INFERNO_BOLT -> {
                int duration = Mth.randomBetweenInclusive(random, 160, 300);
                if (LibUtils.isMaster(level(), blockPosition())) duration = duration * 5 / 2;
                else if (LibUtils.isAtLeastExpert(level(), blockPosition())) duration *= 2;
                target.igniteForTicks(duration);
            }
            case WATER_SPHERE, CHAOS_BALL, RUNE_BLAST, GASTROPOD, WALL_OF_FLESH_LASER -> {
                // 这些能量弹幕只结算直接伤害，不附加状态。
            }
        }
    }

    /// 亡魂射弹会在有限范围内寻找最近玩家，平滑修正方向，并在逼近时降低速度。
    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        if (isInfernoBlast()) return Vec3.ZERO;
        if (variant != Variant.LOST_SOUL || !(getOwner() instanceof Mob owner)) return velocity;
        Player nearest = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (Player player : level().players()) {
            if (!player.isAlive() || !owner.canAttack(player)) continue;
            double distance = Math.abs(player.getX() - getX()) + Math.abs(player.getY() - getY()) + Math.abs(player.getZ() - getZ());
            if (distance <= 12.5 && distance < nearestDistance) {
                nearest = player;
                nearestDistance = distance;
            }
        }
        if (nearest == null) return velocity;
        Vec3 desired = nearest.getEyePosition().subtract(position());
        if (desired.lengthSqr() < 1.0E-7) return velocity;
        Vec3 currentDirection = velocity.lengthSqr() < 1.0E-7 ? desired.normalize() : velocity.normalize();
        Vec3 direction = currentDirection.scale(0.85).add(desired.normalize().scale(0.15)).normalize();
        double speed = Mth.lerp(Mth.clamp(nearestDistance / 12.5, 0.0, 1.0), 0.08, VELOCITY);
        return direction.scale(speed);
    }

    /// 法师弹幕除暗影束外均可穿墙；其他敌怪的粒子弹幕保留实体碰撞。
    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (variant == Variant.SHADOW_BEAM) {
            Vec3 velocity = getDeltaMovement();
            Vec3 reflected = switch (result.getDirection().getAxis()) {
                case X -> new Vec3(-velocity.x, velocity.y, velocity.z);
                case Y -> new Vec3(velocity.x, -velocity.y, velocity.z);
                case Z -> new Vec3(velocity.x, velocity.y, -velocity.z);
            };
            Vec3 offset = reflected.lengthSqr() < 1.0E-8 ? Vec3.ZERO : reflected.normalize().scale(0.01);
            setPos(result.getLocation().add(offset));
            setDeltaMovement(reflected);
            hasImpulse = true;
            return;
        }
        if (!variant.passesThroughBlocks()) super.onHitBlock(result);
    }

    @Override
    protected boolean ownsBlockImpactMovement(BlockHitResult result) {
        return variant == Variant.SHADOW_BEAM;
    }

    /// 狱火弹命中实体后在命中点展开爆燃，其余弹幕沿用首次命中即消失的规则。
    @Override
    protected void finishEntityHit(EntityHitResult result) {
        if (variant == Variant.INFERNO_BOLT && !isInfernoBlast()) {
            setPos(result.getLocation());
            activateInfernoBlast();
            return;
        }
        super.finishEntityHit(result);
    }

    @Override
    protected boolean ownsEntityImpactMovement(EntityHitResult result) {
        return variant == Variant.INFERNO_BOLT && isInfernoBlast();
    }

    @Override
    public boolean canHitEntity(net.minecraft.world.entity.Entity target) {
        return !isInfernoBlast() && super.canHitEntity(target);
    }

    @Override
    public boolean isPickable() {
        return variant.isDestructible() && !isRemoved();
    }

    /// 水球、混沌球与燃烧球受到一次有效攻击即被摧毁。
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!variant.isDestructible() || source.getEntity() == null || amount <= 0.0F || isInvulnerableTo(source) || isRemoved())
            return false;
        if (!level().isClientSide) discard();
        return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(INFERNO_FLIGHT_TICKS_KEY, infernoFlightTicks);
        tag.putInt(INFERNO_BLAST_TICKS_KEY, infernoBlastTicks);
        tag.putBoolean(INFERNO_BLAST_KEY, isInfernoBlast());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        infernoFlightTicks = tag.getInt(INFERNO_FLIGHT_TICKS_KEY);
        infernoBlastTicks = tag.getInt(INFERNO_BLAST_TICKS_KEY);
        entityData.set(INFERNO_BLAST, tag.getBoolean(INFERNO_BLAST_KEY));
    }

    /// 外观和命中特性由注册类型固定，禁止在运行期间临时切换。
    public enum Variant {
        HILL_FIRE_BOUND(ParticleTypes.FLAME, false, false, 100),
        WATER_SPHERE(ParticleTypes.SOUL, true, true, 100),
        CHAOS_BALL(ParticleTypes.SOUL, true, true, 100),
        RUNE_BLAST(new DustParticleOptions(new Vector3f(0.2F, 1.0F, 0.1F), 1.2F), true, false, 160),
        SHADOW_BEAM(ParticleTypes.SOUL_FIRE_FLAME, false, false, 100),
        INFERNO_BOLT(ParticleTypes.FLAME, true, false, 400),
        LOST_SOUL(ParticleTypes.SOUL, true, false, 50),
        VILE_SPIT(ModParticleTypes.SPIT_GLOW.get(), false, true, 100),
        FIRE_IMP(ParticleTypes.FLAME, true, true, 100),
        GASTROPOD(null, false, false, 100),
        WALL_OF_FLESH_LASER(null, false, false, 100);

        @Nullable
        private final ParticleOptions particle;
        private final boolean passesThroughBlocks;
        private final boolean destructible;
        private final int maximumLifetime;

        Variant(@Nullable ParticleOptions particle, boolean passesThroughBlocks, boolean destructible, int maximumLifetime) {
            this.particle = particle;
            this.passesThroughBlocks = passesThroughBlocks;
            this.destructible = destructible;
            this.maximumLifetime = maximumLifetime;
        }

        @Nullable
        ParticleOptions particle() {
            return particle;
        }

        boolean passesThroughBlocks() {
            return passesThroughBlocks;
        }

        boolean isDestructible() {
            return destructible;
        }

        int maximumLifetime() {
            return maximumLifetime;
        }
    }
}
