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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SpiritSurgeEntity extends Projectile {
    private static final EntityDataAccessor<Long> START_TIME = SynchedEntityData.defineId(SpiritSurgeEntity.class, EntityDataSerializers.LONG);
    private static final String HIT_ENTITIES_TAG = "HitEntities";
    private final Set<UUID> hitEntities = new HashSet<>();
    private float damage;

    public SpiritSurgeEntity(EntityType<? extends SpiritSurgeEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
        entityData.set(START_TIME, level.getGameTime());
    }

    public SpiritSurgeEntity(LivingEntity owner, float damage) {
        this(ModEntities.SPIRIT_SURGE.get(), owner.level());
        setOwner(owner);
        setPos(owner.position());
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(START_TIME, 0L);
    }

    /// 从同步的开始时间计算半径，后来追踪的客户端也能还原进度。
    public double getRadius() {
        double progress = Math.min(1.0, (double) (level().getGameTime() - entityData.get(START_TIME)) / SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS);
        return SoulSkillParameters.SPIRIT_SURGE_RADIUS * Math.max(0, progress);
    }

    /// 服务端逐步扩散且每个目标仅命中一次，客户端只画粒子环。
    @Override
    public void tick() {
        long age = level().getGameTime() - entityData.get(START_TIME);
        if (!level().isClientSide && age > SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS) {
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
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(radius, SoulSkillParameters.SPIRIT_SURGE_VERTICAL_RANGE, radius), target -> target.isAlive() && !target.isSpectator() && target != owner && (owner == null || !owner.isAlliedTo(target)))) {
            double dx = target.getX() - getX();
            double dz = target.getZ() - getZ();
            if (dx * dx + dz * dz <= radius * radius && hitEntities.add(target.getUUID())) {
                target.hurt(LibDamageTypes.soul(level(), this, owner), damage);
            }
        }
        if (age >= SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS) {
            discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = tag.getFloat("Damage");
        entityData.set(START_TIME, tag.getLong("StartTime"));
        hitEntities.clear();
        for (Tag hit : tag.getList(HIT_ENTITIES_TAG, Tag.TAG_INT_ARRAY)) {
            hitEntities.add(NbtUtils.loadUUID(hit));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", damage);
        tag.putLong("StartTime", entityData.get(START_TIME));
        ListTag hits = new ListTag();
        for (UUID id : hitEntities) {
            hits.add(NbtUtils.createUUID(id));
        }
        tag.put(HIT_ENTITIES_TAG, hits);
    }
}
