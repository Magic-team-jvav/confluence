package org.confluence.mod.common.entity.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/// 灵魂激荡的冲击波实体。
///
/// 半径、纵向范围与持续时间都由生成时传入（引魂激荡、强力激荡、灵魂激荡冲击共用同一实体），
/// 需要时还能在命中目标处生成连锁弹幕（灵魂引发）。客户端只按同步参数画粒子环。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SpiritSurgeEntity extends Projectile {
    private static final EntityDataAccessor<Long> START_TIME = SynchedEntityData.defineId(SpiritSurgeEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(SpiritSurgeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(SpiritSurgeEntity.class, EntityDataSerializers.INT);
    private static final String HIT_ENTITIES_TAG = "HitEntities";
    private final Set<UUID> hitEntities = new HashSet<>();
    private float damage;
    private double verticalRange = SoulSkillParameters.SPIRIT_SURGE_VERTICAL_RANGE;
    private int chainCount;
    private float chainDamage;

    public SpiritSurgeEntity(EntityType<? extends SpiritSurgeEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
        entityData.set(START_TIME, level.getGameTime());
        entityData.set(RADIUS, (float) SoulSkillParameters.SPIRIT_SURGE_RADIUS);
        entityData.set(DURATION, SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS);
    }

    /// 默认灵魂激荡：半径、时长取统一参数。
    public SpiritSurgeEntity(LivingEntity owner, float damage) {
        this(owner, damage, SoulSkillParameters.SPIRIT_SURGE_RADIUS, SoulSkillParameters.SPIRIT_SURGE_VERTICAL_RANGE,
                SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS, 0, 0.0F);
    }

    /// 指定范围与连锁参数的构造；`chainCount > 0` 时在每个被命中的目标处生成连锁弹幕。
    public SpiritSurgeEntity(LivingEntity owner, float damage, double radius, double verticalRange, int duration, int chainCount, float chainDamage) {
        this(ModEntities.SPIRIT_SURGE.get(), owner.level());
        setOwner(owner);
        setPos(owner.position());
        this.damage = damage;
        this.verticalRange = verticalRange;
        this.chainCount = chainCount;
        this.chainDamage = chainDamage;
        entityData.set(RADIUS, (float) radius);
        entityData.set(DURATION, duration);
        entityData.set(START_TIME, owner.level().getGameTime());
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(START_TIME, 0L);
        entityData.define(RADIUS, (float) SoulSkillParameters.SPIRIT_SURGE_RADIUS);
        entityData.define(DURATION, SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS);
    }

    /// 从同步的开始时间计算半径，后来追踪的客户端也能还原进度。
    public double getRadius() {
        int duration = Math.max(1, entityData.get(DURATION));
        double progress = Math.min(1.0, (double) (level().getGameTime() - entityData.get(START_TIME)) / duration);
        return entityData.get(RADIUS) * Math.max(0, progress);
    }

    /// 服务端逐步扩散且每个目标仅命中一次，客户端只画粒子环。
    @Override
    public void tick() {
        long age = level().getGameTime() - entityData.get(START_TIME);
        int duration = Math.max(1, entityData.get(DURATION));
        if (!level().isClientSide && age > duration) {
            discard();
            return;
        }
        super.tick();
        double radius = getRadius();
        if (level().isClientSide) {
            for (int i = 0; i < SoulSkillParameters.SPIRIT_SURGE_PARTICLE_COUNT; i++) {
                double angle = Math.PI * 2.0 * i / SoulSkillParameters.SPIRIT_SURGE_PARTICLE_COUNT;
                level().addParticle(ParticleTypes.SOUL, getX() + Math.cos(angle) * radius, getY(), getZ() + Math.sin(angle) * radius, 0, 0, 0);
            }
            return;
        }
        Entity owner = getOwner();
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(radius, verticalRange, radius),
                target -> target.isAlive() && !target.isSpectator() && target != owner && (owner == null || !owner.isAlliedTo(target)))) {
            double dx = target.getX() - getX();
            double dz = target.getZ() - getZ();
            if (dx * dx + dz * dz <= radius * radius && hitEntities.add(target.getUUID())) {
                target.hurt(LibDamageTypes.soul(level(), this, owner), damage);
                spawnChainProjectiles(target);
            }
        }
        if (age >= duration) {
            discard();
        }
    }

    /// 灵魂引发：在被命中的目标位置向随机方向射出连锁弹幕。
    private void spawnChainProjectiles(LivingEntity target) {
        if (chainCount <= 0 || chainDamage <= 0.0F || !(getOwner() instanceof LivingEntity owner)) {
            return;
        }
        RandomSource random = RandomSource.create();
        for (int i = 0; i < chainCount; i++) {
            double yaw = random.nextDouble() * Math.PI * 2.0;
            double pitch = (random.nextDouble() - 0.5) * 0.6;
            Vec3 direction = new Vec3(Math.cos(yaw), pitch, Math.sin(yaw));
            SoulLureProjectile projectile = new SoulLureProjectile(owner, target.position().add(0, target.getBbHeight() * 0.5, 0), direction,
                    chainDamage, SoulLureProjectile.PARTICLE, SoulSkillParameters.SOUL_LURE_SPEED, 0.0,
                    SoulSkillParameters.SOUL_LURE_MAX_DISTANCE, SoulSkillParameters.SOUL_LURE_MAX_AGE_TICKS, 0.0, 0.0F);
            level().addFreshEntity(projectile);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = tag.getFloat("Damage");
        verticalRange = tag.getDouble("VerticalRange");
        chainCount = tag.getInt("ChainCount");
        chainDamage = tag.getFloat("ChainDamage");
        entityData.set(START_TIME, tag.getLong("StartTime"));
        entityData.set(RADIUS, tag.getFloat("Radius"));
        entityData.set(DURATION, tag.getInt("Duration"));
        hitEntities.clear();
        for (Tag hit : tag.getList(HIT_ENTITIES_TAG, Tag.TAG_INT_ARRAY)) {
            hitEntities.add(NbtUtils.loadUUID(hit));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", damage);
        tag.putDouble("VerticalRange", verticalRange);
        tag.putInt("ChainCount", chainCount);
        tag.putFloat("ChainDamage", chainDamage);
        tag.putLong("StartTime", entityData.get(START_TIME));
        tag.putFloat("Radius", entityData.get(RADIUS));
        tag.putInt("Duration", entityData.get(DURATION));
        ListTag hits = new ListTag();
        for (UUID id : hitEntities) {
            hits.add(NbtUtils.createUUID(id));
        }
        tag.put(HIT_ENTITIES_TAG, hits);
    }
}
